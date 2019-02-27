import com.shfb.oframe.core.util.common.*
import com.shfb.oframe.core.util.excel.ExcelReader
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import net.sf.json.JSONArray
import org.springframework.ui.ModelMap
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 签约处理控制层
 */
class oh006 extends GroovyController {
    /** 初始化主界面  */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/eland/oh/oh006/oh006", modelMap);
    }

    /** 初始化右边界面 */
    public ModelAndView info(HttpServletRequest request, HttpServletResponse response) {

        // TODO: 目前居室与后端配置存折紧密关系, 码表值顺序1:一居,2:二居,3:三居,4:零居室, 顺序不允许调整
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
            if (rspData != null) {
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

            //获取签约信息里[面积分配]信息
            int huJushNum = conRspData.getListNum("Operation.OpResult.HsCtInfo.chooseHs");
            //[选房区域id，选房bean]
            Map<String, XmlNode> chRegMap = new HashMap();
            for (int i = 0; i < huJushNum; i++) {
                XmlBean chooseRegBean = conRspData.getBeanByPath("Operation.OpResult.HsCtInfo.chooseHs[${i}]");
                String huJush = chooseRegBean.getStrValue("chooseHs.huJush");
                chRegMap.put(huJush, chooseRegBean.getRootNode());
            }
            modelMap.put("chooseHuJushMap", chRegMap);

            // 获取签约信息里的[选房]信息
            int chooseNum = conRspData.getListNum("Operation.OpResult.HsCtInfo.chooseHs");
            // 循环选择的所有房产数据，并按选择的房产数据初始化
            // 转换成[户型编号、选房列表]
            Map<String, List> allHsMap = new LinkedHashMap<String, List>();
            for (int i = 0; i < chooseNum; i++) {
                XmlBean chooseBean = conRspData.getBeanByPath("Operation.OpResult.HsCtInfo.chooseHs[${i}]");
                // 获取选择房源的户型
                String huJush = chooseBean.getStrValue("chooseHs.huJush");
                List<XmlNode> chooseList = allHsMap.get(huJush);
                if (chooseList == null) {
                    chooseList = new ArrayList<XmlNode>();
                    allHsMap.put(huJush, chooseList);
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

        //查询配比信息
        opData = new XmlBean();
        svcRequest = RequestUtil.getSvcRequest(request);
        prePath = "OpData.";
        opData.setStrValue(prePath + "entityName", "HouseInfo");
        opData.setStrValue(prePath + "queryType", "2");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "HouseInfo.hsId");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", hsId);
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");

        // 需要查询的字段
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "HouseInfo.familyStructure");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "HouseInfo.allFamNum");
        opData.setStrValue(prePath + "ResultFields.fieldName[2]", "HouseInfo.famPsNum");
        opData.setStrValue(prePath + "ResultFields.fieldName[3]", "HouseInfo.HsCtInfo.chooseYjNum");
        opData.setStrValue(prePath + "ResultFields.fieldName[4]", "HouseInfo.HsCtInfo.chooseEjNum");
        opData.setStrValue(prePath + "ResultFields.fieldName[5]", "HouseInfo.HsCtInfo.chooseSjNum");
        opData.setStrValue(prePath + "ResultFields.fieldName[6]", "HouseInfo.HsCtInfo.chooseLjNum");
        opData.setStrValue(prePath + "ResultFields.fieldName[7]", "HouseInfo.HsCtInfo.choosePbNote");
        opData.setStrValue(prePath + "ResultFields.fieldName[8]", "HouseInfo.HsCtInfo.choosePbStatus");
        // 增加查询
        svcRequest.addOp("QUERY_MORE_ENTITY_CMPT", opData);
        svcResponse = query(svcRequest);
        String[] tempArr = [0, 0, 0, 0];
        if (svcResponse.isSuccess()) {
            XmlBean resultBean = svcResponse.getFirstOpRsp("QUERY_MORE_ENTITY_CMPT")
                    .getBeanByPath("Operation.OpResult.PageData.Row.HouseInfo");
            String chooseLjNum = resultBean.getStrValue("HouseInfo.HsCtInfo.chooseLjNum");
            String chooseYjNum = resultBean.getStrValue("HouseInfo.HsCtInfo.chooseYjNum");
            String chooseEjNum = resultBean.getStrValue("HouseInfo.HsCtInfo.chooseEjNum");
            String chooseSjNum = resultBean.getStrValue("HouseInfo.HsCtInfo.chooseSjNum");
            if (StringUtil.isEmptyOrNull(chooseLjNum)) {
                chooseLjNum = "0";
            }
            if (StringUtil.isEmptyOrNull(chooseYjNum)) {
                chooseYjNum = "0";
            }
            if (StringUtil.isEmptyOrNull(chooseEjNum)) {
                chooseEjNum = "0";
            }
            if (StringUtil.isEmptyOrNull(chooseSjNum)) {
                chooseSjNum = "0";
            }
            tempArr[0] = chooseYjNum;
            tempArr[1] = chooseEjNum;
            tempArr[2] = chooseSjNum;
            tempArr[3] = chooseLjNum;
            modelMap.put("matchInfo", resultBean.getRootNode());
            modelMap.put("tempArr", tempArr);
        }

        Map<String, String> hsTps = getCfgCollection(request, "NEW_HS_JUSH", true, NumberUtil.getIntFromObj(prjCd));
        modelMap.put("hsTps", hsTps);
        modelMap.put("hsId", hsId);
        modelMap.put("prjCd", prjCd);
        return new ModelAndView("/eland/oh/oh006/oh00601", modelMap);
    }

    /**
     * 区域树详细细数据
     * @param request
     * @param response
     */
    public void treeInfo(HttpServletRequest request, HttpServletResponse response) {
        StringBuilder sb = new StringBuilder();
        Map<String, String> hsTps = getCfgCollection(request, "NEW_HS_JUSH", true, NumberUtil.getIntFromObj(request.getParameter("prjCd")));
        String[] hsTpsArr = hsTps.keySet().toArray();
        for (int j = 0; j < 4; j++) {
            sb.append("""{ id: "0", pId: "",name: "${hsTps.get(hsTpsArr[j])}",hsType:"${
                hsTpsArr[j]
            }",regName:"未知区域"},""");
        }
        String jsonStr = """{success:true, errMsg:"", resultMap:{treeJson:[${sb.toString()}]}}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

    /**
     * 根据地址查询新房源，返回界面自助查询
     * @param request 请求信息
     * @param response 返回Json格式数据
     */
    public void queryNewHsReg(HttpServletRequest request, HttpServletResponse response) {
        //查询可用房源信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        String prjCd = request.getParameter("prjCd");
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
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "upRegId");
        opData.setStrValue(prePath + "ResultFields.fieldName[2]", "regName");
        // 增加查询
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean queryResult = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult");
            if (queryResult) {
                int rowCount = queryResult.getListNum("OpResult.PageData.Row");
                List<Map<String, String>> result = new ArrayList<Map<String, String>>();
                Map<String, String> temp = new HashMap<>();
                XmlBean reqData = new XmlBean();
                for (int i = 0; i < rowCount; i++) {
                    String tempField01 = queryResult.getStrValue("OpResult.PageData.Row[${i}].regId");
                    String tempField02 = queryResult.getStrValue("OpResult.PageData.Row[${i}].regName");

                    svcRequest = RequestUtil.getSvcRequest(request);
                    reqData.setStrValue("SvcCont.PrjReg.regId", tempField01);
                    reqData.setStrValue("SvcCont.PrjReg.prjCd", prjCd);
                    reqData.setStrValue("SvcCont.PrjReg.regUseType", "3");//外迁区域
                    svcRequest.setReqData(reqData);
                    svcResponse = callService("prjRegService", "queryPrjReg", svcRequest);
                    if (svcResponse.isSuccess()) {
                        XmlBean prjRegNode = svcResponse.getRspData().getBeanByPath("SvcCont.PrjReg");
                        String regAttr7 = prjRegNode.getStrValue("PrjReg.regAttr7");//是否是可选区域 1为是
                        String regAttr10 = prjRegNode.getStrValue("PrjReg.regAttr10");//零居
                        String regAttr11 = prjRegNode.getStrValue("PrjReg.regAttr11");//一居
                        String regAttr12 = prjRegNode.getStrValue("PrjReg.regAttr12");//二居
                        String regAttr13 = prjRegNode.getStrValue("PrjReg.regAttr13");//三居
                        //获取区域内的居室(与区域进行对应)
                        String[] regAttrArr = [regAttr11, regAttr12, regAttr13, regAttr10];
                        if (StringUtil.isEqual("1", regAttr7)) {
                            String tempFlag = "false";
                            for (int j = 0; j < regAttrArr.length; j++) {
                                if (StringUtil.isNotEmptyOrNull(regAttrArr[j]) && !StringUtil.isEqual("0", regAttrArr[j])) {
                                    tempFlag = "true";
                                    break;
                                }
                            }
                            if (StringUtil.isEqual("true", tempFlag)) {
                                temp = ["regId": tempField01, "regName": tempField02];
                                result.add(temp);
                            }
                        }
                    }
                }
                ResponseUtil.print(response, JSONArray.fromObject(result).toString());
            }
        }
    }

    /**
     * 调整审批状态
     * @param request 请求信息
     * @param response 返回Json格式数据
     */
    public void approve(HttpServletRequest request, HttpServletResponse response) {
        //查询可用房源信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsCtId = request.getParameter("hsCtId");
        String choosePbStatus = request.getParameter("choosePbStatus");
        XmlBean rowData = new XmlBean();
        rowData.setStrValue("OpData.entityName", "HsCtInfo");
        rowData.setStrValue("OpData.EntityData.hsCtId", hsCtId);
        rowData.setStrValue("OpData.EntityData.choosePbStatus", choosePbStatus);
        // 调用服务
        svcRequest.addOp("SAVE_ENTITY", rowData);
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /** 保存选房信息 */
    public void saveChooseHsInfo(HttpServletRequest request, HttpServletResponse response) {
        //保存配比信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsCtId = request.getParameter("hsCtId");
        String ljNum = request.getParameter("ljNum");
        String yjNum = request.getParameter("yjNum");
        String ejNum = request.getParameter("ejNum");
        String sjNum = request.getParameter("sjNum");
        String choosePbNote = request.getParameter("choosePbNote");
        XmlBean chooseData = new XmlBean();
        chooseData.setStrValue("OpData.HsCtInfo.hsCtId", hsCtId);
        chooseData.setStrValue("OpData.HsCtInfo.chooseLjNum", ljNum);
        chooseData.setStrValue("OpData.HsCtInfo.chooseYjNum", yjNum);
        chooseData.setStrValue("OpData.HsCtInfo.chooseEjNum", ejNum);
        chooseData.setStrValue("OpData.HsCtInfo.chooseSjNum", sjNum);
        chooseData.setStrValue("OpData.HsCtInfo.choosePbNote", choosePbNote);
        String prjCd = request.getParameter("prjCd");

        String nodePath = "OpData.";
        // 调用人员信息保存组件  保存人员信息
        //单个人员信息
        String[] personIds = request.getParameterValues("personId");
        String[] personNames = request.getParameterValues("personName");
        String[] personCertyNums = request.getParameterValues("personCerty");
        String[] personTelphones = request.getParameterValues("personTelphone");
        String[] personCertyDocIds = request.getParameterValues("personCertyDocIds");
        String[] personJobAddr = request.getParameterValues("personJobAddr");
        String[] familyAddr = request.getParameterValues("familyAddr");
        String[] personLiveAddr = request.getParameterValues("personLiveAddr");
        int temp = 0;
        if (personNames != null && personNames.length > 0) {
            for (int i = 0; i < personNames.length; i++) {
                chooseData.setStrValue(nodePath + "PersonInfos.PersonInfo[" + temp + "].personId", personIds[i]);
                chooseData.setStrValue(nodePath + "PersonInfos.PersonInfo[" + temp + "].personName", personNames[i]);
                chooseData.setStrValue(nodePath + "PersonInfos.PersonInfo[" + temp + "].personCertyNum", personCertyNums[i]);
                chooseData.setStrValue(nodePath + "PersonInfos.PersonInfo[" + temp + "].personTelphone", personTelphones[i]);
                chooseData.setStrValue(nodePath + "PersonInfos.PersonInfo[" + temp + "].personCertyDocIds", personCertyDocIds[i]);
                chooseData.setStrValue(nodePath + "PersonInfos.PersonInfo[" + temp + "].personJobAddr", personJobAddr[i]);
                chooseData.setStrValue(nodePath + "PersonInfos.PersonInfo[" + temp + "].familyAddr", familyAddr[i]);
                chooseData.setStrValue(nodePath + "PersonInfos.PersonInfo[" + temp + "].personLiveAddr", personLiveAddr[i]);
                chooseData.setStrValue(nodePath + "PersonInfos.PersonInfo[" + temp++ + "].prjCd", prjCd);
            }
        }

        // 保存选房组件  循环获取多个所购居室
        nodePath = "OpData."
        chooseData.setStrValue(nodePath + "hsCtId", hsCtId);
        nodePath = "OpData.HsChooses.";
        String[] hsCtChooseIds = request.getParameterValues("hsCtChooseId");
        String[] chooseHsRegIds = request.getParameterValues("chooseHsRegId");
        String[] chooseHsRegName = request.getParameterValues("chooseHsRegName");
        String[] huJush = request.getParameterValues("huJush");
        String[] buyPersonIds = request.getParameterValues("buyPersonId");
        String[] buyPersonName = request.getParameterValues("buyPersonName");
        String[] personNoticeAddr = request.getParameterValues("personNoticeAddr");
        String[] hsHxName = request.getParameterValues("hsHxName");
        String[] preBldSize = request.getParameterValues("preBldSize");
        String[] gtJzPsIdss = request.getParameterValues("gtJzPsIds");
        String[] gtJzPsOwnerRels = request.getParameterValues("gtJzPsOwnerRels");
        String[] buyPsWtIdss = request.getParameterValues("buyPsWtIds");
        String[] buyPsWtOwnerRel = request.getParameterValues("buyPsWtOwnerRel");
        String[] buyPsWtDocIdss = request.getParameterValues("buyPsWtDocIds");
        String[] buyPersonOwnerRels = request.getParameterValues("buyPersonOwnerRel");
        String[] buyPersonCerty = request.getParameterValues("buyPersonCerty");
        String[] buyPersonTel = request.getParameterValues("buyPersonTel");
        String[] buyPsOwnerRelDocIds = request.getParameterValues("buyPsOwnerRelDocIds");
        String[] chooseNote = request.getParameterValues("chooseNote");
        String[] chooseHsAddr = request.getParameterValues("hsAddr");
        String[] newHsId = request.getParameterValues("newHsId");
        String[] oldNewHsId = request.getParameterValues("oldNewHsId");

        int idx = 0;
        int chooseIdIdx = 0;
        String chooseIdStr = "";
        if (huJush != null && huJush.length > 0) {
            for (int i = 0; i < huJush.length; i++) {
                if (StringUtil.isEmptyOrNull(huJush[i])) {
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
                chooseData.setStrValue(nodePath + "HsChoose[${idx}].chooseHsRegName", chooseHsRegName[i]);
                chooseData.setStrValue(nodePath + "HsChoose[${idx}].huJush", huJush[i]);
                chooseData.setStrValue(nodePath + "HsChoose[${idx}].buyPersonId", buyPersonIds[i]);
                chooseData.setStrValue(nodePath + "HsChoose[${idx}].buyPersonName", buyPersonName[i]);
                chooseData.setStrValue(nodePath + "HsChoose[${idx}].personNoticeAddr", personNoticeAddr[i]);
                chooseData.setStrValue(nodePath + "HsChoose[${idx}].chooseHsHxName", hsHxName[i]);
                chooseData.setStrValue(nodePath + "HsChoose[${idx}].chooseHsSize", preBldSize[i]);
                chooseData.setStrValue(nodePath + "HsChoose[${idx}].buyPersonOwnerRel", buyPersonOwnerRels[i]);
                chooseData.setStrValue(nodePath + "HsChoose[${idx}].buyPersonCerty", buyPersonCerty[i]);
                chooseData.setStrValue(nodePath + "HsChoose[${idx}].buyPersonTel", buyPersonTel[i]);
                chooseData.setStrValue(nodePath + "HsChoose[${idx}].buyPsOwnerRelDocIds", buyPsOwnerRelDocIds[i]);
                chooseData.setStrValue(nodePath + "HsChoose[${idx}].gtJzPsIds", gtJzPsIdss[i]);
                chooseData.setStrValue(nodePath + "HsChoose[${idx}].gtJzPsOwnerRels", gtJzPsOwnerRels[i]);
                chooseData.setStrValue(nodePath + "HsChoose[${idx}].buyPsWtIds", buyPsWtIdss[i]);
                chooseData.setStrValue(nodePath + "HsChoose[${idx}].buyPsWtOwnerRel", buyPsWtOwnerRel[i]);
                chooseData.setStrValue(nodePath + "HsChoose[${idx}].buyPsWtDocIds", buyPsWtDocIdss[i]);
                chooseData.setStrValue(nodePath + "HsChoose[${idx}].chooseNote", chooseNote[i]);
                chooseData.setStrValue(nodePath + "HsChoose[${idx}].chooseHsAddr", chooseHsAddr[i]);
                chooseData.setStrValue(nodePath + "HsChoose[${idx}].newHsId", newHsId[i]);
                chooseData.setStrValue(nodePath + "HsChoose[${idx}].oldNewHsId", oldNewHsId[i]);
                idx++;
                chooseIdIdx++;
            }
        }
        // 是否保存入库标志
        chooseData.setStrValue("OpData.toDb", "0");
        chooseData.setStrValue("OpData.toContext", "0");
        svcRequest.addOp("SAVE_WQ_HSCHOOSE_INFO", chooseData);
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printAjax(response, """{success:${svcResponse.isSuccess()}, errMsg:"${
            svcResponse.getErrMsg()
        }" }""");
    }

    /** 锁定配比信息后，选房之前修改通讯地址和电话 */
    public void saveNoticeInfo(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        //保存通讯地址和电话
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String newHsId = request.getParameterValues("newHsId");
        String hsCtChooseId = request.getParameter("hsCtChooseId");
        String personNoticeAddr = request.getParameter("personNoticeAddr");
        String buyPersonTel = request.getParameter("buyPersonTel");
        //选房实体（hsCtChoose）中查询chooseStatus“选房状态” 1未选房  2已选房  3预选房
        XmlBean noticData = new XmlBean();
        String nodePath = "OpData.";
        noticData.setStrValue(nodePath + "entityName","HsCtChooseInfo");
        //按照hsCtId查询
        noticData.setStrValue(nodePath + "Conditions.Condition.fieldName","hsCtChooseId");
        noticData.setStrValue(nodePath + "Conditions.Condition.fieldValue",hsCtChooseId);
        noticData.setStrValue(nodePath + "Conditions.Condition.operation","=");
        //查询“信息锁定”状态
        noticData.setStrValue(nodePath + "ResultFields.fieldName","chooseStatus");
        //调用服务
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", noticData);
        //启动服务
        SvcResponse svcResponse = query(svcRequest);
        String chooseStatus = "";
        if (svcResponse.isSuccess()) {
            XmlBean resultBean = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult.PageData");
            chooseStatus = resultBean.getStrValue("PageData.Row.chooseStatus");
        }
        String jsonStr = "";
        if(StringUtil.isEqual("1", chooseStatus) || StringUtil.isEqual("3", chooseStatus)){
            //保存到HsCtChooseInfo实体
            noticData = new XmlBean();
            noticData.setStrValue("OpData.entityName", "HsCtChooseInfo");
            noticData.setStrValue("OpData.EntityData.hsCtChooseId", hsCtChooseId);
            noticData.setStrValue("OpData.EntityData.personNoticeAddr", personNoticeAddr);
            noticData.setStrValue("OpData.EntityData.buyPersonTel", buyPersonTel);
            //调用服务
            svcRequest.addOp("SAVE_ENTITY", noticData);
            //启动服务
            svcResponse = transaction(svcRequest);
            jsonStr = """{success:${svcResponse.isSuccess()}, errMsg:"${svcResponse.getErrMsg()}" }""";
        } else {
            jsonStr = """{success:false, errMsg:"已选房" }""";
        }
        ResponseUtil.printAjax(response, jsonStr);
    }

    /** 查询上一户、下一户hsId */
    public void singleQuery(HttpServletRequest request, HttpServletResponse response) {
        // 获取房屋编号
        RequestUtil requestUtil = new RequestUtil(request);
        String hsId = requestUtil.getStrParam("hsId");
        // 查询条件
        String conditionNames = requestUtil.getStrParam("conditionName");
        String conditions = requestUtil.getStrParam("condition");
        String conditionValues = requestUtil.getStrParam("conditionValue");
        // 排序条件
        String flag = request.getParameter("flag");
        String operation = ">";
        String sortOrder = "asc";
        if (StringUtil.isEqual("last", flag)) {
            operation = "<";
            sortOrder = "desc";
        }
        // 调用服务，获取居民基本信息和签约状态信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        // 获取基本信息
        opData.setStrValue(prePath + "entityName", "HouseInfo");
        String[] conditionFieldArr = conditionNames.split(",");
        String[] conditionArr = conditions.split(",", conditionFieldArr.length);
        String[] conditionValueArr = conditionValues.split(",", conditionFieldArr.length);
        // 查询条件
        int addCount = 0;
        for (int i = 0; i < conditionFieldArr.length; i++) {
            if (StringUtil.isEmptyOrNull(conditionFieldArr[i])
                    || StringUtil.isEmptyOrNull(conditionArr[i])
                    || StringUtil.isEmptyOrNull(conditionValueArr[i])) {
                continue;
            }
            opData.setStrValue(prePath + "Conditions.Condition[${addCount}].fieldName", conditionFieldArr[i]);
            opData.setStrValue(prePath + "Conditions.Condition[${addCount}].operation", conditionArr[i]);
            if ("like".equalsIgnoreCase(conditionArr[i])) {
                opData.setStrValue(prePath + "Conditions.Condition[${addCount++}].fieldValue", "%" + conditionValueArr[i] + "%");
            } else if ("in".equalsIgnoreCase(conditionArr[i])) {
                String usedValue = conditionValueArr[i].replaceAll("\\|", ",");
                opData.setStrValue(prePath + "Conditions.Condition[${addCount++}].fieldValue", "(" + usedValue + ")");
            } else {
                opData.setStrValue(prePath + "Conditions.Condition[${addCount++}].fieldValue", conditionValueArr[i]);
            }
        }
        opData.setStrValue(prePath + "queryType", "2");
        // 增加查询条件
        opData.setStrValue(prePath + "Conditions.Condition[${addCount}].fieldName", "HouseInfo.hsId");
        opData.setStrValue(prePath + "Conditions.Condition[${addCount}].operation", operation);
        opData.setStrValue(prePath + "Conditions.Condition[${addCount++}].fieldValue", hsId);
        // 排序条件
        opData.setStrValue(prePath + "SortFields.SortField.fieldName", "HouseInfo.hsId");
        opData.setStrValue(prePath + "SortFields.SortField.sortOrder", sortOrder);
        // 分页条件
        opData.setStrValue(prePath + "PageInfo.pageSize", "1");
        opData.setStrValue(prePath + "PageInfo.currentPage", "1");
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "HouseInfo.hsId");


        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);

        // 调用服务
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean pageData = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA")
                    .getBeanByPath("Operation.OpResult.PageData");
            if (pageData != null && pageData.getListNum("PageData.Row") > 0) {
                hsId = pageData.getStrValue("PageData.Row[0].HouseInfo.hsId");
            } else {
                hsId = "null";
            }
        }
        ResponseUtil.printSvcResponse(response, svcResponse, """hsId:'${hsId}'""");
    }

    /**
     * 导入面板
     */
    public ModelAndView openImport(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/eland/oh/oh006/oh006Import_mb", modelMap);
    }

    /**
     * 下载导入模板
     */
    public void downTemplate(HttpServletRequest request, HttpServletResponse response) {
        String agent = request.getHeader("USER-AGENT");
        String templateFile = new File(StringUtil.formatFilePath("webroot:/eland/oh/oh006/chooseExcel.xlsx"));
        // 文件下载输出到响应流
        ResponseUtil.downloadFile(response, "", "chooseExcel.xlsx", new File(templateFile), agent);
    }

    /**
     * 选房信息导入
     * @param request 请求信息
     * @param response 响应信息
     */
    public ModelAndView chooseImport(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        MultipartFile localFile = super.getFile(request, "importMbFile");
        String originalFileName = localFile.getOriginalFilename();
        String fileType = originalFileName.substring(originalFileName.lastIndexOf("."), originalFileName.length());
        String errMsg = "";
        boolean result = false;

        if (fileType.contains(".xls") || fileType.contains(".xlsx")) {
            String remoteFileName = UUID.randomUUID().toString() + fileType;
            File saveFile = ServerFile.createFile(remoteFileName);
            FileOutputStream outputStream = null;
            InputStream inputStream = null;
            try {
                outputStream = new FileOutputStream(saveFile);
                inputStream = localFile.getInputStream();
                // 保存待上传文件信息
                byte[] b = new byte[1024 * 5];
                int len;
                while ((len = inputStream.read(b)) != -1) {
                    outputStream.write(b, 0, len);
                }
                outputStream.flush();
            } catch (IOException e) {
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close()
                    } catch (Exception e) {
                    }
                }
                if (inputStream != null) {
                    try {
                        inputStream.close()
                    } catch (Exception e) {
                    }
                }
            }
            //获取sheet页名字
            ExcelReader excelReader = new ExcelReader(saveFile);
            String sheetName = excelReader.getSheetName(0);
            XmlBean paramData = new XmlBean();
            String nodePath = "OpData.";
            paramData.setStrValue(nodePath + "path", saveFile.getAbsolutePath());
            if (StringUtil.isEqual("DATA_IMPORT", sheetName)) {
                paramData.setStrValue(nodePath + "cofRow", "2");
            } else {
                paramData.setStrValue(nodePath + "cofRow", "4");
            }
            svcRequest.addOp("IMPORT_WQ_HS_CHOOSE_INFO_NO_COF_EX", paramData);
            // 调用服务，执行数据查询
            SvcResponse svcResponse = transaction(svcRequest);
            if (svcResponse.isSuccess()) {
                result = true;
            } else {
                errMsg = svcResponse.getErrMsg();
            }
            saveFile.delete();
        } else {
            errMsg = "文件格式不正确";
        }
        String jsonStr = """{isSuccess: ${result}, errMsg: "${errMsg}"}""";
        ResponseUtil.printAjax(response, jsonStr);
    }
}
