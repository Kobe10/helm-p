import com.shfb.oframe.core.util.common.NumberUtil
import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.common.XmlNode
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created with Intellij IDEA
 * User: shfb_wang 
 * Date: 2015/11/10 0010 14:34
 * Copyright(c) 北京四海富博计算机服务有限公司
 */
class ch002 extends GroovyController {
    /** 初始化居民选房 页面 */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        String hsId = request.getParameter("hsId");
        String prjCd = request.getParameter("prjCd");
        String regId = "";

        //基本信息 BaseInfo
        XmlBean reqData = new XmlBean();
        reqData.setStrValue("OpData.HouseInfo.hsId", hsId);
        svcRequest.addOp("QUERY_HS_BASE_INFO", reqData);
        reqData = new XmlBean();
        reqData.setStrValue("OpData.HouseInfo.hsId", hsId);
        svcRequest.addOp("QUERY_HS_OWNER_INFO", reqData);
        //调用最新的服务
        SvcResponse ownRsp = query(svcRequest);
        if (ownRsp.isSuccess()) {
            XmlBean rspData = ownRsp.getFirstOpRsp("QUERY_HS_BASE_INFO");
            if(rspData != null){
                modelMap.put("oldHsBean", rspData.getBeanByPath("Operation.HouseInfo").getRootNode());
            }
            XmlBean rspBean = ownRsp.getFirstOpRsp("QUERY_HS_OWNER_INFO");
            if (rspBean != null) {
                modelMap.put("hsOwnerBean", rspBean.getBeanByPath("Operation.HouseInfo").getRootNode());
                int hsOwnersNum = rspBean.getListNum("Operation.HouseInfo.HsOwners.HsOwner");
                //拼接多个人的  产权证号  一块展示
                String ownerCerty = "";
                for (int i = 0; i < hsOwnersNum; i++) {
                    String temp = rspBean.getStrValue("Operation.HouseInfo.HsOwners.HsOwner[${i}].ownerCerty");
                    if (StringUtil.isNotEmptyOrNull(temp)) {
                        ownerCerty = temp + "," + ownerCerty;
                    }
                }
                if (ownerCerty.length() > 0) {
                    ownerCerty = ownerCerty.substring(0, ownerCerty.length() - 1);
                }
                modelMap.put("ownerCerty", ownerCerty);
            }
        }

        /* 获取可用房源区域列表 */
        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        opData.setStrValue(prePath + "entityName", "RegInfo");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "regLevel");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", "1");
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
        opData.setStrValue(prePath + "Conditions.Condition[1].fieldName", "regUseType");
        opData.setStrValue(prePath + "Conditions.Condition[1].fieldValue", "3");
        opData.setStrValue(prePath + "Conditions.Condition[1].operation", "=");
        opData.setStrValue(prePath + "Conditions.Condition[2].fieldName", "prjCd");
        opData.setStrValue(prePath + "Conditions.Condition[2].fieldValue", "${prjCd}");
        opData.setStrValue(prePath + "Conditions.Condition[2].operation", "=");
        // 需要查询的字段
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "regId");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "regName");
        // 增加查询
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
        SvcResponse svcResponse = query(svcRequest);
        // 处理可用选房区域数据
        if (svcResponse.isSuccess()) {
            XmlBean queryResult = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult");
            // 处理查询结果，返回json格式数据
            if (queryResult != null) {
                int rowCount = queryResult.getListNum("OpResult.PageData.Row");
                List<Map<String, String>> newHsAreaList = new ArrayList<Map<String, String>>();
                for (int i = 0; i < rowCount; i++) {
                    String tempField01 = queryResult.getStrValue("OpResult.PageData.Row[${i}].regId");
                    String tempField02 = queryResult.getStrValue("OpResult.PageData.Row[${i}].regName");
                    Map<String, String> temp = ["regId": tempField01, "regName": tempField02];
                    newHsAreaList.add(temp);
                }
                modelMap.put("newHsArea", newHsAreaList);
            }
        }

        //签约信息  选房信息
        XmlBean contractReqData = new XmlBean();
        contractReqData.setStrValue("OpData.HsCtInfo.hsId", hsId);
        svcRequest.addOp("QUERY_HS_CT_INFO", contractReqData);
        //整体调用服务查询
        svcResponse = query(svcRequest);
        //获取签约信息查询结果
        XmlBean conRspData = svcResponse.getFirstOpRsp("QUERY_HS_CT_INFO");
        // 获取所有除户籍外的人员ID列表
        Set<String> personIds = new HashSet<String>();
        if (conRspData != null) {
            XmlBean newXmlBean = null;
            //获取产权人ids 串
            String ctPsIdStr = conRspData.getStrValue("Operation.OpResult.HsCtInfo.ctPsIds");
            String[] ctPsIdStrs;
            if (StringUtil.isNotEmptyOrNull(ctPsIdStr)) {
                ctPsIdStrs = ctPsIdStr.split(",");
            } else {
                ctPsIdStrs = new String[0];
            }
            personIds.addAll(ctPsIdStrs);
            //获取受托人ids串
            String ctWtPsIdStr = conRspData.getStrValue("Operation.OpResult.HsCtInfo.ctWtPsId");
            String[] ctWtPsIdStrs = new String[ctPsIdStrs.length];
            if (StringUtil.isNotEmptyOrNull(ctWtPsIdStr)) {
                String[] temp = ctWtPsIdStr.split(",");
                for (int i = 0; i < temp.length; i++) {
                    ctWtPsIdStrs[i] = temp[i];
                }
            }
            personIds.addAll(ctWtPsIdStrs);
            // 拼接产权人对应委托人map  key=产权人id value=受托人id
            Map ctPsWtMap = new HashMap();
            if (ctPsIdStrs.length > 0) {
                for (int i = 0; i < ctPsIdStrs.length; i++) {
                    ctPsWtMap.put(ctPsIdStrs[i], ctWtPsIdStrs[i]);
                }
            }
            modelMap.put("ctPsWtMap", ctPsWtMap);

            // 获取签约腾退信息
            XmlBean ctInfo = conRspData.getBeanByPath("Operation.OpResult.HsCtInfo");
            String hsCtId = "";
            if (ctInfo != null) {
                hsCtId = ctInfo.getStrValue("HsCtInfo.hsCtId");
                modelMap.put("ctInfo", ctInfo.getRootNode());
            }
            //根据签约id查询面积赠送信息 推送到页面的数据有待优化
            Map<String, String> giveMap = new LinkedHashMap();
            if (StringUtil.isNotEmptyOrNull(hsCtId)) {
                XmlBean hsGvData = new XmlBean();
                hsGvData.setStrValue("OpData.HsCtGive.hsCtId", hsCtId);
                hsGvData.setStrValue("OpData.HsCtGive.giveStatusCd", "1");
//                        hsGvData.setStrValue("OpData.HsCtGive.getFlag", "true");
                svcRequest = RequestUtil.getSvcRequest(request);
                svcRequest.addOp("QUERY_GV_INFO_LIST", hsGvData);
                SvcResponse giveQueryResp = query(svcRequest);
                if (giveQueryResp.isSuccess()) {
                    XmlBean gvRsp = giveQueryResp.getFirstOpRsp("QUERY_GV_INFO_LIST");
                    if (gvRsp != null) {
                        int giveNum = gvRsp.getListNum("Operation.OpResult.HsCtGives.HsCtGive");
                        List aGiveSize = new ArrayList();
                        List bGiveSize = new ArrayList();
                        for (int i = 0; i < giveNum; i++) {
                            XmlBean giveBean = gvRsp.getBeanByPath("Operation.OpResult.HsCtGives.HsCtGive[${i}]");
                            //判断是赠出还是 获赠
                            String hsCtAId = giveBean.getStrValue("HsCtGive.hsCtAId");
                            if (StringUtil.isEqual(hsCtId, hsCtAId)) {
                                //赠出面积
                                aGiveSize.add(giveBean.getRootNode());
                            } else {
                                //获赠
                                bGiveSize.add(giveBean.getRootNode());
                            }
                        }
                        modelMap.put("aGiveSize", aGiveSize);
                        modelMap.put("bGiveSize", bGiveSize);
                    }
                }
            }

            //获取签约信息里[面积分配]信息
            int chooseRegNum = conRspData.getListNum("Operation.OpResult.HsCtInfo.HsRegInfo");
            //[选房区域id，选房bean]
            Map<String, XmlNode> chRegMap = new HashMap();
            for (int i = 0; i < chooseRegNum; i++) {
                XmlBean chooseRegBean = conRspData.getBeanByPath("Operation.OpResult.HsCtInfo.HsRegInfo[${i}]");
                String chRegId = chooseRegBean.getStrValue("HsRegInfo.ttRegId");
                chRegMap.put(chRegId, chooseRegBean.getRootNode());
            }
            modelMap.put("chooseRegMap", chRegMap);
            // 获取签约信息里的[选房]信息
            int chooseNum = conRspData.getListNum("Operation.OpResult.HsCtInfo.chooseHs");
            // 循环选择的所有房产数据，并按选择的房产数据初始化
            // 转换成[区域ID、户型编号、选房列表]
            Map<String, Map<String, List>> allHsMap = new LinkedHashMap<String, Map<String, List>>();
            for (int i = 0; i < chooseNum; i++) {
                XmlBean chooseBean = new XmlBean();
                chooseBean = conRspData.getBeanByPath("Operation.OpResult.HsCtInfo.chooseHs[${i}]");
                // 获取选择房源所在的区域
                regId = chooseBean.getStrValue("chooseHs.chooseHsRegId");
                Map<String, List> areaHsMap = allHsMap.get(regId);
                if (areaHsMap == null) {
                    areaHsMap = new LinkedHashMap<String, List>();
                    allHsMap.put(regId, areaHsMap);
                }
                // 获取选择房源的户型
                String chooseHsTp = chooseBean.getStrValue("chooseHs.chooseHsTp");
                List<XmlNode> chooseList = areaHsMap.get(chooseHsTp);
                if (chooseList == null) {
                    chooseList = new ArrayList<XmlNode>();
                    areaHsMap.put(chooseHsTp, chooseList);
                }
                // 增加选房区域
                chooseList.add(chooseBean.getRootNode());
                // 获取购房相关的人员信息
                personIds.add(chooseBean.getStrValue("chooseHs.buyPersonId"));
                personIds.add(chooseBean.getStrValue("chooseHs.buyPsWtIds"));
                String livePsIds = chooseBean.getStrValue("chooseHs.gtJzPsIds");
                if (StringUtil.isNotEmptyOrNull(livePsIds)) {
                    personIds.addAll(livePsIds.split(","))
                }
            }
            modelMap.put("allHsMap", allHsMap);
        }

        // 调用服务获取所有的其他人员信息
        personIds.remove(null);
        personIds.remove("");
        if (personIds.size() > 0) {
            String personIdsStr = personIds.join(",");
            // 调用服务获取人员信息
            svcRequest = RequestUtil.getSvcRequest(request);
            opData = new XmlBean();
            prePath = "OpData.";
            opData.setStrValue(prePath + "entityName", "PersonInfo");
            opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "personId");
            opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", "(${personIdsStr})");
            opData.setStrValue(prePath + "Conditions.Condition[0].operation", "in");
            opData.setStrValue(prePath + "Conditions.Condition[1].fieldName", "prjCd");
            opData.setStrValue(prePath + "Conditions.Condition[1].fieldValue", "${prjCd}");
            opData.setStrValue(prePath + "Conditions.Condition[1].operation", "=");

            opData.setStrValue(prePath + "PageInfo.pageSize", "1000");
            opData.setStrValue(prePath + "PageInfo.currentPage", "1");

            // 需要查询的字段
            opData.setStrValue(prePath + "ResultFields.fieldName[0]", "personId");
            opData.setStrValue(prePath + "ResultFields.fieldName[1]", "personName");
            opData.setStrValue(prePath + "ResultFields.fieldName[2]", "personCertyNum");
            opData.setStrValue(prePath + "ResultFields.fieldName[3]", "personCertyDocIds");
            opData.setStrValue(prePath + "ResultFields.fieldName[4]", "personTelphone");
            opData.setStrValue(prePath + "ResultFields.fieldName[5]", "familyAddr");
            opData.setStrValue(prePath + "ResultFields.fieldName[6]", "personJobAddr");
            opData.setStrValue(prePath + "ResultFields.fieldName[7]", "personLiveAddr");
            // 增加查询
            svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
            SvcResponse personQueryResp = query(svcRequest);
            if (personQueryResp.isSuccess()) {
                XmlBean queryResult = personQueryResp.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult");
                Map<String, XmlNode> personsData = new HashMap<String, XmlNode>();
                // 处理查询结果，返回json格式数据
                if (queryResult != null) {
                    int rowCount = queryResult.getListNum("OpResult.PageData.Row");
                    for (int i = 0; i < rowCount; i++) {
                        XmlBean row = queryResult.getBeanByPath("OpResult.PageData.Row[${i}]");
                        String personId = row.getStrValue("Row.personId");
                        personsData.put(personId, row.getRootNode());
                    }
                    modelMap.put("personsData", personsData);
                }
            }
        }

        modelMap.put("hsId", hsId);
        modelMap.put("prjCd", prjCd);
        return new ModelAndView("/eland/ch/ch002/ch002", modelMap);
    }

    /** 查询选房数据 */
    public ModelAndView initNewHsArea(HttpServletRequest request, HttpServletResponse response) {
        RequestUtil requestUtil = new RequestUtil(request);
        ModelMap modelMap = new ModelMap();
        // 获取区域编号
        String regId = requestUtil.getStrParam("regId");
        String prjCd = requestUtil.getStrParam("prjCd");
        String regUseType = "3";
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        //查询该区域应安置系数，应安置面积，计算可安置面积
        XmlBean reqData = new XmlBean();
        String rootNodePath = "SvcCont.PrjReg.";
        reqData.setStrValue(rootNodePath + "regId", regId);     // 区域编码
        reqData.setStrValue(rootNodePath + "prjCd", prjCd);     //  项目编码，必须
        reqData.setStrValue(rootNodePath + "regUseType", regUseType);       // 区域类型
        svcRequest.setReqData(reqData);     // 调用服务
        SvcResponse svcResponse = callService("prjRegService", "queryPrjReg", svcRequest);
        // 获取输出结果
        if (svcResponse.isSuccess()) {
            XmlBean prjRegNode = svcResponse.getRspData().getBeanByPath("SvcCont.PrjReg");
            modelMap.put("regInfo", prjRegNode.getRootNode());
        }

        Map<String, String> hsTps = getCfgCollection(request, "HS_ROOM_TYPE", true, NumberUtil.getIntFromObj(prjCd));
        modelMap.put("hsTps", hsTps);
        modelMap.put("prjCd", prjCd);
        modelMap.put("regId", regId);
        // 返回暂时界面
        return new ModelAndView("/eland/ch/ch002/ch00201", modelMap);
    }

    /** 保存选房信息 */
    public void saveChooseHsInfo(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsCtId = request.getParameter("hsCtId");
        String hsId = request.getParameter("hsId");
        String prjCd = request.getParameter("prjCd");

        String nodePath = "OpData.";
        // 调用人员信息保存组件  保存人员信息
        XmlBean personOpData = null;
        //单个人员信息
        String[] personIds = request.getParameterValues("personId");
        String[] personNames = request.getParameterValues("personName");
        String[] personCertyNums = request.getParameterValues("personCerty");
        String[] personTelphones = request.getParameterValues("personTelphone");
        String[] personCertyDocIds = request.getParameterValues("personCertyDocIds");
        String[] personJobAddr = request.getParameterValues("personJobAddr");
        String[] familyAddr = request.getParameterValues("familyAddr");
        String[] personLiveAddr = request.getParameterValues("personLiveAddr");

        nodePath = "OpData."
        if (personNames != null && personNames.length > 0) {
            for (int i = 0; i < personNames.length; i++) {
                personOpData = new XmlBean();
                personOpData.setStrValue(nodePath + "entityName", "PersonInfo");
                personOpData.setStrValue(nodePath + "EntityData.personId", personIds[i]);
                personOpData.setStrValue(nodePath + "EntityData.personName", personNames[i]);
                personOpData.setStrValue(nodePath + "EntityData.personCertyNum", personCertyNums[i]);
                personOpData.setStrValue(nodePath + "EntityData.personTelphone", personTelphones[i]);
                personOpData.setStrValue(nodePath + "EntityData.personCertyDocIds", personCertyDocIds[i]);
                personOpData.setStrValue(nodePath + "EntityData.personJobAddr", personJobAddr[i]);
                personOpData.setStrValue(nodePath + "EntityData.familyAddr", familyAddr[i]);
                personOpData.setStrValue(nodePath + "EntityData.personLiveAddr", personLiveAddr[i]);
                personOpData.setStrValue(nodePath + "EntityData.prjCd", prjCd);
                svcRequest.addOp("SAVE_ENTITY", personOpData);
            }
        }

        // 保存选房组件  循环获取多个所购居室
        nodePath = "OpData."
        XmlBean chooseData = new XmlBean();
        chooseData.setStrValue(nodePath + "hsCtId", hsCtId);
        //保存分配面积，应安置总面积。入参
        nodePath = "OpData.HsRegInfos.";
        String[] hsCtRegIds = request.getParameterValues("gvHsCtRegId");
        String[] ttRegIds = request.getParameterValues("gvTtRegId");
        String[] fpmjs = request.getParameterValues("fpmj");
        String[] yazmjs = request.getParameterValues("yazmj");
        String[] sjmjs = request.getParameterValues("lastSize");
        int chRegIdx = 0;
        int hsCtRegIdx = 0;
        if (ttRegIds != null && ttRegIds.length > 0) {
            for (int i = 0; i < ttRegIds.length; i++) {
                if (StringUtil.isEmptyOrNull(ttRegIds[i])) {
                    continue;
                }
                String hsCtRegIdStr = "";
                //选房id如果为空传变量
                if (StringUtil.isEmptyOrNull(hsCtRegIds[i])) {
                    hsCtRegIdStr = "\${${hsCtRegIdx}${i}}";
                } else {
                    hsCtRegIdStr = hsCtRegIds[i];
                }
                chooseData.setStrValue(nodePath + "HsRegInfo[${chRegIdx}].hsCtRegId", hsCtRegIdStr);
                chooseData.setStrValue(nodePath + "HsRegInfo[${chRegIdx}].ttRegId", ttRegIds[i]);
                chooseData.setStrValue(nodePath + "HsRegInfo[${chRegIdx}].hsCtId", hsCtId);
                chooseData.setStrValue(nodePath + "HsRegInfo[${chRegIdx}].fpmj", fpmjs[i]);
                chooseData.setStrValue(nodePath + "HsRegInfo[${chRegIdx}].yazmj", yazmjs[i]);
                chooseData.setStrValue(nodePath + "HsRegInfo[${chRegIdx}].sjmj", sjmjs[i]);
                chRegIdx++;
                hsCtRegIdx++;
            }
        }

        nodePath = "OpData.HsChooses.";
        String[] hsCtChooseIds = request.getParameterValues("hsCtChooseId");
        String[] chooseHsRegIds = request.getParameterValues("chooseHsRegId");
        String[] chooseHsTps = request.getParameterValues("chooseHsTp");
        String[] buyPersonIds = request.getParameterValues("buyPersonId");
        String[] gtJzPsIdss = request.getParameterValues("gtJzPsIds");
        String[] gtJzPsOwnerRels = request.getParameterValues("gtJzPsOwnerRels");
        String[] buyPsWtIdss = request.getParameterValues("buyPsWtIds");
        String[] buyPsWtOwnerRel = request.getParameterValues("buyPsWtOwnerRel");
        String[] buyPsWtDocIdss = request.getParameterValues("buyPsWtDocIds");
        String[] buyPersonOwnerRels = request.getParameterValues("buyPersonOwnerRel");
        String[] buyPsOwnerRelDocIds = request.getParameterValues("buyPsOwnerRelDocIds");
        String[] chooseNote = request.getParameterValues("chooseNote");

        int idx = 0;
        int chooseIdIdx = 0;
        String chooseIdStr = "";
        if (chooseHsTps != null && chooseHsTps.length > 0) {
            for (int i = 0; i < chooseHsTps.length; i++) {
                if (StringUtil.isEmptyOrNull(chooseHsTps[i])) {
                    continue;
                }
                //选房id如果为空传变量
                if (StringUtil.isEmptyOrNull(hsCtChooseIds[i])) {
                    chooseIdStr = "\${${chooseIdIdx}}";
                } else {
                    chooseIdStr = hsCtChooseIds[i];
                }
                chooseData.setStrValue(nodePath + "HsChoose[${idx}].hsCtChooseId", chooseIdStr);
                chooseData.setStrValue(nodePath + "HsChoose[${idx}].chooseHsRegId", chooseHsRegIds[i]);
                chooseData.setStrValue(nodePath + "HsChoose[${idx}].chooseHsTp", chooseHsTps[i]);
                chooseData.setStrValue(nodePath + "HsChoose[${idx}].buyPersonId", buyPersonIds[i]);
                chooseData.setStrValue(nodePath + "HsChoose[${idx}].buyPersonOwnerRel", buyPersonOwnerRels[i]);
                chooseData.setStrValue(nodePath + "HsChoose[${idx}].buyPsOwnerRelDocIds", buyPsOwnerRelDocIds[i]);
                chooseData.setStrValue(nodePath + "HsChoose[${idx}].gtJzPsIds", gtJzPsIdss[i]);
                chooseData.setStrValue(nodePath + "HsChoose[${idx}].gtJzPsOwnerRels", gtJzPsOwnerRels[i]);
                chooseData.setStrValue(nodePath + "HsChoose[${idx}].buyPsWtIds", buyPsWtIdss[i]);
                chooseData.setStrValue(nodePath + "HsChoose[${idx}].buyPsWtOwnerRel", buyPsWtOwnerRel[i]);
                chooseData.setStrValue(nodePath + "HsChoose[${idx}].buyPsWtDocIds", buyPsWtDocIdss[i]);
                chooseData.setStrValue(nodePath + "HsChoose[${idx}].chooseNote", chooseNote[i]);
                idx++;
                chooseIdIdx++;
            }
        } else if (ttRegIds != null && ttRegIds.length > 0) {
            for (int i = 0; i < ttRegIds.length; i++) {
                if (StringUtil.isEmptyOrNull(ttRegIds[i])) {
                    continue;
                }
                // 选房id如果为空传变量
                if (StringUtil.isEmptyOrNull(hsCtChooseIds[i])) {
                    chooseIdStr = "\${${chooseIdIdx}}";
                } else {
                    chooseIdStr = hsCtChooseIds[i];
                }
                chooseData.setStrValue(nodePath + "HsChoose[${idx}].hsCtChooseId", chooseIdStr);
                chooseData.setStrValue(nodePath + "HsChoose[${idx}].chooseHsRegId", chooseHsRegIds[i]);
                chooseData.setStrValue(nodePath + "HsChoose[${idx}].chooseHsTp", chooseHsTps[i]);
                chooseData.setStrValue(nodePath + "HsChoose[${idx}].buyPersonId", buyPersonIds[i]);
                chooseData.setStrValue(nodePath + "HsChoose[${idx}].buyPersonOwnerRel", buyPersonOwnerRels[i]);
                chooseData.setStrValue(nodePath + "HsChoose[${idx}].buyPsOwnerRelDocIds", buyPsOwnerRelDocIds[i]);
                chooseData.setStrValue(nodePath + "HsChoose[${idx}].gtJzPsIds", gtJzPsIdss[i]);
                chooseData.setStrValue(nodePath + "HsChoose[${idx}].gtJzPsOwnerRels", gtJzPsOwnerRels[i]);
                chooseData.setStrValue(nodePath + "HsChoose[${idx}].buyPsWtIds", buyPsWtIdss[i]);
                chooseData.setStrValue(nodePath + "HsChoose[${idx}].buyPsWtOwnerRel", buyPsWtOwnerRel[i]);
                chooseData.setStrValue(nodePath + "HsChoose[${idx}].buyPsWtDocIds", buyPsWtDocIdss[i]);
                chooseData.setStrValue(nodePath + "HsChoose[${idx}].chooseNote", chooseNote[i]);
                idx++;
                chooseIdIdx++;
            }
        }
        // 是否保存入库标志
        chooseData.setStrValue("OpData.toDb", "0");
        chooseData.setStrValue("OpData.toContext", "0");
        svcRequest.addOp("SAVE_CHOOSE_HS_INFO", chooseData);
        SvcResponse svcResponse = transaction(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean rspBean = svcResponse.getFirstOpRsp("SAVE_CHOOSE_HS_INFO");
        }
        ResponseUtil.printAjax(response, """{success:${svcResponse.isSuccess()}, errMsg:"${
            svcResponse.getErrMsg()
        }" }""");
    }
}