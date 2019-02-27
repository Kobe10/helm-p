import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.common.XmlNode
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import net.sf.json.JSONArray
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 外迁选房
 */
class oh002 extends GroovyController {

    /**
     * 外迁选房操作。
     * @param request
     * @param response
     * @return
     */
    public ModelAndView outChooseHs(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String hsId = request.getParameter("hsId");
        String prjCd = request.getParameter("prjCd");
        String fromOp = request.getParameter("fromOp");
        String method = request.getParameter("method");
        modelMap.put("fromOp", fromOp);
        modelMap.put("method", method);
        modelMap.put("hsId", hsId);

        //选择推送页面
        String toOpPage = "/eland/oh/oh002/oh002";
        List hsOwnersList = new ArrayList();
        if (StringUtil.isNotEmptyOrNull(hsId)) {
            //基本信息  产权信息 单独查询
            SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
            XmlBean operationData = new XmlBean();
            //基本信息 BaseInfo
            operationData.setStrValue("OpData.HouseInfo.hsId", hsId);
            svcRequest.addOp("QUERY_HS_BASE_INFO", operationData);
            svcRequest.addOp("QUERY_HS_OWNER_INFO", operationData);
            //签约信息  选房信息
            XmlBean contractReqData = new XmlBean();
            contractReqData.setStrValue("OpData.HsCtInfo.hsId", hsId);
            svcRequest.addOp("QUERY_HS_CT_INFO", contractReqData);

            /* 获取可用房源区域列表 */
            XmlBean opData = new XmlBean();
            String prePath = "OpData.";
            //调用最新的服务
            SvcResponse svcResponse = query(svcRequest);
            XmlBean houseInfo = new XmlBean();
            if (svcResponse.isSuccess()) {
                // 获取所有除户籍外的人员ID列表
                Set<String> personIds = new HashSet<String>();
                //所选房源Id列表
                Set<String> chooseHsIds = new HashSet<String>();
                XmlBean rspBean = svcResponse.getFirstOpRsp("QUERY_HS_BASE_INFO");
                XmlBean hsOwnerInfoBean = svcResponse.getFirstOpRsp("QUERY_HS_OWNER_INFO");
                if (rspBean != null) {
                    //获取房产基本信息，产权信息查询结果
                    modelMap.put("baseBean", rspBean.getBeanByPath("Operation.HouseInfo").getRootNode());
                    modelMap.put("ownerInfo", hsOwnerInfoBean.getBeanByPath("Operation.HouseInfo").getRootNode());
                    XmlBean hsOwnerBean = hsOwnerInfoBean.getBeanByPath("Operation.HouseInfo.HsOwners");
                    if (hsOwnerBean != null) {
                        int hsOwnerCount = hsOwnerBean.getListNum("HsOwners.HsOwner");
                        for (int i = 0; i < hsOwnerCount; i++) {
                            XmlBean ownerBean = hsOwnerBean.getBeanByPath("HsOwners.HsOwner[${i}]");
                            hsOwnersList.add(ownerBean.getRootNode());
                            // 增加到待查询人员列表
                            String ownerPsId = ownerBean.getStrValue("HsOwner.ownerPsId");
                            personIds.add(ownerPsId);
                        }
                        modelMap.put("hsOwnersList", hsOwnersList);
                    }
                }

                //获取签约信息, 选房信息查询结果
                XmlBean conRspData = svcResponse.getFirstOpRsp("QUERY_HS_CT_INFO");
                if (conRspData != null) {
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
                    if (ctInfo != null) {
                        modelMap.put("ctInfo", ctInfo.getRootNode());
                    }
                    // 获取签约信息里的选房信息
                    int chooseNum = conRspData.getListNum("Operation.OpResult.HsCtInfo.chooseHs");
                    // 循环选择的所有房产数据，并按选择的房产数据初始化
                    // 转换成[区域ID、户型编号、选房列表]
                    Map<String, Map<String, List>> allHsMap = new LinkedHashMap<String, Map<String, List>>();

                    for (int i = 0; i < chooseNum; i++) {
                        XmlBean chooseBean = new XmlBean();
                        chooseBean = conRspData.getBeanByPath("Operation.OpResult.HsCtInfo.chooseHs[${i}]");
                        // 获取选择房源所在的区域
                        String regId = chooseBean.getStrValue("chooseHs.chooseHsRegId");
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
                        //集合所选房源id，
                        chooseHsIds.add(chooseBean.getStrValue("chooseHs.chooseHsId"));
                    }
                    modelMap.put("allHsMap", allHsMap);
                }
                modelMap.put("houseInfo", houseInfo.getRootNode());
                // 调用服务获取所有的其他人员信息
                personIds.remove(null);
                personIds.remove("");
                if (personIds.size() > 0) {
                    String personIdsStr = personIds.join(",");
                    // 调用服务获取人员信息
                    svcRequest = RequestUtil.getSvcRequest(request);
                    /* 获取可用房源区域列表 */
                    // 增加等级
                    opData = new XmlBean();
                    prePath = "OpData.";
                    opData.setStrValue(prePath + "entityName", "PersonInfo");
                    opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "personId");
                    opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", "(${personIdsStr})");
                    opData.setStrValue(prePath + "Conditions.Condition[0].operation", "in");
                    opData.setStrValue(prePath + "Conditions.Condition[1].fieldName", "prjCd");
                    opData.setStrValue(prePath + "Conditions.Condition[1].fieldValue", "${prjCd}");
                    opData.setStrValue(prePath + "Conditions.Condition[1].operation", "=");
                    opData.setStrValue(prePath + "PageInfo.pageSize", "100");
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
                            List<Map<String, String>> newHsAreaList = new ArrayList<Map<String, String>>();
                            for (int i = 0; i < rowCount; i++) {
                                XmlBean row = queryResult.getBeanByPath("OpResult.PageData.Row[${i}]");
                                String personId = row.getStrValue("Row.personId");
                                personsData.put(personId, row.getRootNode());
                            }
                            modelMap.put("personsData", personsData);
                        }
                    }
                }

                //查询所选房源详细信息
                chooseHsIds.remove(null);
                chooseHsIds.remove("");
                if (chooseHsIds.size() > 0) {
                    String chooseHsIdStr = chooseHsIds.join(",");
                    svcRequest = RequestUtil.getSvcRequest(request);
                    /* 获取可用房源区域列表 */
                    // 增加等级
                    opData = new XmlBean();
                    prePath = "OpData.";
                    opData.setStrValue(prePath + "entityName", "NewHsInfo");
                    opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "newHsId");
                    opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", "(${chooseHsIdStr})");
                    opData.setStrValue(prePath + "Conditions.Condition[0].operation", "in");
                    // 需要查询的字段
                    opData.setStrValue(prePath + "ResultFields.fieldName[0]", "newHsId");
                    opData.setStrValue(prePath + "ResultFields.fieldName[1]", "hsAddr");
                    opData.setStrValue(prePath + "ResultFields.fieldName[2]", "hsHxName");
                    opData.setStrValue(prePath + "ResultFields.fieldName[3]", "dealUnPrice");  //成交单价
                    opData.setStrValue(prePath + "ResultFields.fieldName[4]", "ttRegId");
                    opData.setStrValue(prePath + "ResultFields.fieldName[5]", "dealSalePrice");  //成交总价
                    opData.setStrValue(prePath + "ResultFields.fieldName[6]", "hsBldSize");
                    opData.setStrValue(prePath + "ResultFields.fieldName[7]", "preBldSize");
                    opData.setStrValue(prePath + "ResultFields.fieldName[8]", "hsTp");
                    opData.setStrValue(prePath + "ResultFields.fieldName[9]", "buyHsCtDate");
                    opData.setStrValue(prePath + "ResultFields.fieldName[10]", "cmpWyCost");
                    opData.setStrValue(prePath + "ResultFields.fieldName[11]", "cmpQnCost");
                    opData.setStrValue(prePath + "ResultFields.fieldName[12]", "grQnCost");
                    opData.setStrValue(prePath + "ResultFields.fieldName[13]", "allQnCost");
                    opData.setStrValue(prePath + "ResultFields.fieldName[14]", "rzTime");
                    opData.setStrValue(prePath + "ResultFields.fieldName[15]", "jsStatus");
                    opData.setStrValue(prePath + "ResultFields.fieldName[16]", "jsTime");
                    // 增加查询
                    svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
                    SvcResponse chHsQueryResp = query(svcRequest);
                    if (chHsQueryResp.isSuccess()) {
                        XmlBean queryResult = chHsQueryResp.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult");
                        Map<String, XmlNode> chooseHsData = new HashMap<String, XmlNode>();
                        // 处理查询结果，返回json格式数据
                        if (queryResult != null) {
                            int rowCount = queryResult.getListNum("OpResult.PageData.Row");
                            for (int i = 0; i < rowCount; i++) {
                                XmlBean row = queryResult.getBeanByPath("OpResult.PageData.Row[${i}]");
                                String newHsId = row.getStrValue("Row.newHsId");
                                chooseHsData.put(newHsId, row.getRootNode());
                            }
                            modelMap.put("chooseHsData", chooseHsData);
                        }
                    }
                }
            }

            Map<String, String> hsTps = getCfgCollection(request, "HS_ROOM_TYPE", true);
            modelMap.put("hsTps", hsTps);
            //判断是修改操作还是查看操作
            if (StringUtil.isEqual(method, "edit")) {
                //修改
                if (StringUtil.isEqual(fromOp, "gfhtcl")) {
                    //领取购房合同材料
                    toOpPage = "/eland/ph/ph008/ph008_oh_gfhtcl";
                } else if (StringUtil.isEqual(fromOp, "qdgfht")) {
                    //签订购房合同
                    toOpPage = "/eland/ph/ph008/ph008_oh_qdgfht";
                } else {
                    //外迁选房、购房资格审核
                    toOpPage = "/eland/oh/oh002/oh002";
                }
            } else if (StringUtil.isEqual(method, "view")) {
                //查看
                if (StringUtil.isEqual(fromOp, "gfhtcl")) {
                    //领取购房合同材料
                    toOpPage = "/eland/ph/ph008/ph008_oh_gfhtcl";
                } else if (StringUtil.isEqual(fromOp, "qdgfht")) {
                    //签订购房合同
                    toOpPage = "/eland/ph/ph008/ph008_oh_qdgfht";
                } else if (StringUtil.isEqual(fromOp, "gylwqxf")) {
                    //回迁选房功能中的外迁选房 详情页面
                    toOpPage = "/eland/ch/ch001/ch001_detail";
                } else {
                    //外迁选房、购房资格审核
                    toOpPage = "/eland/oh/oh002/oh002";
                }
            }
        }
        return new ModelAndView(toOpPage, modelMap);
    }

    /**
     * 根据地址查询新房源，返回界面自助查询
     * @param request 请求信息
     * @param response 返回Json格式数据
     */
    public void queryNewHsAddr(HttpServletRequest request, HttpServletResponse response) {
        // 请求信息
        RequestUtil requestUtil = new RequestUtil(request);
        ModelMap modelMap = new ModelMap();

        String hsAddr = requestUtil.getStrParam("hsAddr");
        String prjCd = requestUtil.getStrParam("prjCd");
        String hsHxTp = requestUtil.getStrParam("hsHxTp");
        //根据不同选房区，选择对应区域的房源
        String regId = requestUtil.getStrParam("regId");
        // 处理结果
        XmlBean opData = new XmlBean();
        String prePath = "OpData.";

        //调用权限过滤组件。
        opData.setStrValue(prePath + "opName", "QUERY_ENTITY_PAGE_DATA");  //调用分页组件查询
        opData.setStrValue(prePath + "RegFilter.attrName", "ttRegId");
        opData.setStrValue(prePath + "RegFilter.attrValue", regId);
        opData.setStrValue(prePath + "RegFilter.regUseType", "3");
        opData.setStrValue(prePath + "RegFilter.setPath", "OpData.Conditions");

        opData.setStrValue(prePath + "OrgFilter.attrName", "ttOrgId");
        opData.setStrValue(prePath + "OrgFilter.attrValue", "");
        opData.setStrValue(prePath + "OrgFilter.rhtType", "3");
        opData.setStrValue(prePath + "OrgFilter.setPath", "OpData.Conditions");

        opData.setStrValue(prePath + "entityName", "NewHsInfo");
        // 查询条件
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "hsAddr");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", "%${hsAddr}%");
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "like");
        opData.setStrValue(prePath + "Conditions.Condition[1].fieldName", "statusCd");
        opData.setStrValue(prePath + "Conditions.Condition[1].fieldValue", "2");
        opData.setStrValue(prePath + "Conditions.Condition[1].operation", "=");
        opData.setStrValue(prePath + "Conditions.Condition[2].fieldName", "hsTp");
        opData.setStrValue(prePath + "Conditions.Condition[2].fieldValue", "${hsHxTp}");
        opData.setStrValue(prePath + "Conditions.Condition[2].operation", "=");
        // ^^多选房区要区分选房区域

        /*排序条件*/
        opData.setStrValue(prePath + "SortFields.SortField[0].fieldName", "hsAddr");
        opData.setStrValue(prePath + "SortFields.SortField[0].sortOrder", "asc");
        // 设置分页
        opData.setStrValue(prePath + "PageInfo.pageSize", "2000");
        /*需要查询的字段*/
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "newHsId");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "hsAddr");
        opData.setStrValue(prePath + "ResultFields.fieldName[2]", "hsHxName");
        opData.setStrValue(prePath + "ResultFields.fieldName[3]", "hsUnPrice");
        opData.setStrValue(prePath + "ResultFields.fieldName[4]", "ttRegId");
        opData.setStrValue(prePath + "ResultFields.fieldName[5]", "hsSalePrice");
        opData.setStrValue(prePath + "ResultFields.fieldName[6]", "hsBldSize");   //实测建筑面积
        opData.setStrValue(prePath + "ResultFields.fieldName[7]", "preBldSize");  //预测建筑面积
        opData.setStrValue(prePath + "ResultFields.fieldName[8]", "hsTp");  //居室

        // 调用服务
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.addOp("PRIVI_FILTER", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean queryResult = svcResponse.getFirstOpRsp("PRIVI_FILTER").getBeanByPath("Operation.OpResult");
            // 处理查询结果，返回json格式数据
            Set regIds = new HashSet();
            if (queryResult != null) {
                int rowCount = queryResult.getListNum("OpResult.PageData.Row");
                List<Map<String, String>> result = new ArrayList<Map<String, String>>();
                Map<String, String> temp = new HashMap<>();
                for (int i = 0; i < rowCount; i++) {
                    String tempField01 = queryResult.getStrValue("OpResult.PageData.Row[${i}].newHsId");
                    regIds.add(tempField01);
                    String tempField02 = queryResult.getStrValue("OpResult.PageData.Row[${i}].hsAddr");    //房屋地址
                    String tempField03 = queryResult.getStrValue("OpResult.PageData.Row[${i}].hsHxName");  //户型居室名称
                    String tempField04 = queryResult.getStrValue("OpResult.PageData.Row[${i}].hsUnPrice"); //单价
                    String tempField05 = queryResult.getStrValue("OpResult.PageData.Row[${i}].ttRegId");   //选房区域id
                    String tempField06 = queryResult.getStrValue("OpResult.PageData.Row[${i}].hsSalePrice");  //总购房款
                    String tempField07 = queryResult.getStrValue("OpResult.PageData.Row[${i}].hsBldSize");  //实测建筑面积
                    String tempField08 = queryResult.getStrValue("OpResult.PageData.Row[${i}].preBldSize"); //预测建筑面积
                    String tempField09 = queryResult.getStrValue("OpResult.PageData.Row[${i}].hsTp"); //居室
                    temp = ["newHsId": tempField01, "hsAddr": tempField02, "hsHxName": tempField03, "hsUnPrice": tempField04, "ttRegId": tempField05, "hsSalePrice": tempField06, "hsBldSize": tempField07, "preBldSize": tempField08, "hsTp": tempField09];
                    result.add(temp);
                }
                ResponseUtil.print(response, JSONArray.fromObject(result).toString());
            } else {
                // 没有上述任意查询条件，执行查询
                ResponseUtil.printAjax(response, "{}")
                return;
            }
        }
    }

    /**
     * 确认选房
     * @param request
     * @param response
     */
    public void cfmHsChoose(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);

        String hsCtId = request.getParameter("hsCtId");
        String newHsId = request.getParameter("newHsId");
        String oldNewHsId = request.getParameter("oldNewHsId");
        String hsCtChooseId = request.getParameter("hsCtChooseId");
        String hsUnPrice = request.getParameter("hsUnPrice");   //购房单价
        String realSize = request.getParameter("realSize");     //实际面积
        String hsHxName = request.getParameter("hsHxName");
        String allPayMoney = request.getParameter("allPayMoney");      //总购房金额
        String chooseDate = request.getParameter("chooseDate");        //确认选房时间
        String chooseHsAddr = request.getParameter("chooseHsAddr");

        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        opData.setStrValue(prePath + "hsCtId", hsCtId);
        opData.setStrValue(prePath + "HsChooses.HsChoose[0].hsCtChooseId", hsCtChooseId);
        opData.setStrValue(prePath + "HsChooses.HsChoose[0].chooseHsId", newHsId);
        opData.setStrValue(prePath + "HsChooses.HsChoose[0].oldNewHsId", oldNewHsId);
        opData.setStrValue(prePath + "HsChooses.HsChoose[0].chooseHsPrice", hsUnPrice);
        opData.setStrValue(prePath + "HsChooses.HsChoose[0].chooseHsSalePrice", allPayMoney);
        opData.setStrValue(prePath + "HsChooses.HsChoose[0].chooseHsSize", realSize);
        opData.setStrValue(prePath + "HsChooses.HsChoose[0].chooseHsHxName", hsHxName);
        opData.setStrValue(prePath + "HsChooses.HsChoose[0].chooseDate", chooseDate);
        opData.setStrValue(prePath + "HsChooses.HsChoose[0].chooseHsAddr", chooseHsAddr);
        svcRequest.addOp("CONFIRM_CHOOSE_HS", opData);

//        房源表数据由上面确认选房组件 统一处理。
//        opData = new XmlBean();
//        prePath = "OpData.";
//        opData.setStrValue(prePath + "entityName", "NewHsInfo");
//        opData.setStrValue(prePath + "EntityData.newHsId", newHsId);
//        opData.setStrValue(prePath + "EntityData.hsSalePrice", allPayMoney);
//        opData.setStrValue(prePath + "EntityData.hsBldSize", realSize);
//        opData.setStrValue(prePath + "EntityData.hsUnPrice", hsUnPrice);
//        svcRequest.addOp("SAVE_ENTITY", opData);

        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /**
     * 取消选房操作
     * @param request
     * @param response
     */
    public void cancelHsChoose(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.hsCtId", request.getParameter("hsCtId"));
        opData.setStrValue("OpData.HsChoose.hsCtChooseId", request.getParameter("hsCtChooseId"));
        svcRequest.addOp("CANCEL_CHOOSE_HS", opData);
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }
}
