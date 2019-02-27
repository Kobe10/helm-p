import com.shfb.oframe.core.util.common.*
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 签约处理控制层
 */
class ch001 extends GroovyController {
    /** 初始化主界面  */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("hsId", request.getParameter("hsId"));
        String chsType = request.getParameter("chsType");
        if (StringUtil.isEmptyOrNull(chsType)) {
            chsType = "1";
        }
        modelMap.put("chsType", chsType);
        // 获取显示模式
        modelMap.put("showXfList", "false");
        String ctType = request.getParameter("ctType");
        if (StringUtil.isEmptyOrNull(ctType)) {
            ctType = getCookieByName(request, "ctType");
        }
        if (StringUtil.isEmptyOrNull(ctType)) {
            ctType = "1";
        }
        modelMap.put("ctType", ctType);
        String hsClass = request.getParameter("hsClass");
        if (StringUtil.isEmptyOrNull(hsClass)) {
            if (ctType == "1") {
                // 回迁
                hsClass = "2";
            } else if (ctType == "2") {
                // 外迁
                hsClass = "3";
            } else {
                // 其他方式
                hsClass = "0";
            }
        }
        modelMap.put("hsClass", hsClass);
        // 选房模式三增加购房协议表单
        String xyFormCd = request.getParameter("xyFormCd");
        modelMap.put("xyFormCd", xyFormCd);

        String chooseStatus = request.getParameter("chooseStatus");
        if (StringUtil.isEmptyOrNull(chooseStatus)) {
            chooseStatus = getCookieByName(request, "chooseStatus");
        }

        String isGiveUpHs = request.getParameter("isGiveUpHs");
        if (StringUtil.isEmptyOrNull(isGiveUpHs)) {
            isGiveUpHs = getCookieByName(request, "isGiveUpHs");
        }
        if (StringUtil.isEmptyOrNull(chooseStatus)) {
            chooseStatus = "1";
        }
        modelMap.put("chooseStatus", chooseStatus);
        modelMap.put("isGiveUpHs", isGiveUpHs);
        modelMap.put("countSwitch", getCookieByName(request, "countSwitch"));
        modelMap.put("kxMinSize", getCookieByName(request, "kxMinSize"));//从cookie中读取最小可选面积
        modelMap.put("kxMaxSize", getCookieByName(request, "kxMaxSize"));//从cookie中读取最大可选面积
        return new ModelAndView("/eland/ch/ch001/ch001", modelMap);
    }
    /** 初始化右边界面 */
    public ModelAndView info(HttpServletRequest request, HttpServletResponse response) {
        // 判断选房模式，根据选房模式调用不同的展示界面(1: 选中1套的模式，可以自己添加购房人， 2：选择多套的模式， 不允许添加购房人，购房信息要求提前登记)
        // 选房模式二，允许多人独立选房
        String chsType = request.getParameter("chsType");
        if (StringUtil.isEqual("1", chsType)) {
            return chsModel01(request, response);
        } else if (StringUtil.isEqual("2", chsType)) {
            return chsModel02(request, response);
        } else if (StringUtil.isEqual("3", chsType)) {
            return chsModel03(request, response);
        }
    }

    /**
     * 选房模式01：2: 根据户型居室控制选房信息
     * @param request
     * @param modelMap
     * @return
     */
    private ModelAndView chsModel01(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        // 获取居室列表
        modelMap.put("HS_ROOM_TYPE", getCfgCollection(request, "HS_ROOM_TYPE", true));
        // 获取房屋编号
        String hsId = request.getParameter("hsId");
        String showFlag = request.getParameter("showFlag");
        modelMap.put("hsId", hsId);
        // 调用服务，获取居民基本信息和签约状态信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String prePath = "OpData.";
        XmlBean opData = null;
        // 获取基本信息
        opData = new XmlBean();
        opData.setStrValue(prePath + "entityName", "HouseInfo");
        opData.setStrValue(prePath + "queryType", "2");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "HouseInfo.hsId");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", hsId);
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");

        int addCount = 0;
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.hsCd");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.hsFullAddr");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.hsOwnerPersons");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.HsCtInfo.ctType");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.HsCtInfo.hsCtId");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.HsCtInfo.chHsStatus");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.HsCtInfo.chooseHsSid");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.HsCtInfo.HsCtChooseInfo.chooseHsAddr");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.HsCtInfo.HsCtChooseInfo.chooseHsSize");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.HsCtInfo.HsCtChooseInfo.chooseStatus");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.HsCtInfo.HsCtChooseInfo.hsCtChooseId");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.HsCtInfo.HsCtChooseInfo.buyPersonName");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.HsCtInfo.HsCtChooseInfo.buyPersonCerty");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.HsCtInfo.HsCtChooseInfo.buyPersonTel");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.HsCtInfo.HsCtChooseInfo.buyPersonOwnerRel");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.HsCtInfo.HsCtChooseInfo.choosePrintStatus");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.HsCtInfo.HsCtChooseInfo.personNoticeAddr");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.HsCtInfo.HsCtChooseInfo.chooseDrawStatus");
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
        // 获取控制信息
        opData = new XmlBean();
        opData.setStrValue("OpData.SysControlInfo.scType", "2");
        svcRequest.addOp("QUERY_CONTROL_INFO", opData);
        // 房屋产权人信息
        opData = new XmlBean();
        opData.setStrValue("OpData.entityName", "HouseInfo");
        opData.setStrValue("OpData.Conditions.Condition[0].fieldName", "hsId");
        opData.setStrValue("OpData.Conditions.Condition[0].operation", "=");
        opData.setStrValue("OpData.Conditions.Condition[0].fieldValue", hsId);
        opData.setStrValue("OpData.ResultFields.fieldName[0]", "ownerPsId");
        svcRequest.addOp("QUERY_ENTITY_CMPT", opData);
        // 调用服务
        String chHsStatus = "";
        String ctType = "";
        boolean allChPrint = true;
        SvcResponse svcResponse = query(svcRequest);
        // 查询产区人信息
        Set<String> ownerPsId = new LinkedHashSet<String>();
        if (svcResponse.isSuccess()) {
            // 房屋信息
            XmlBean pageData = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult.PageData");
            int num = pageData.getListNum("PageData.Row")
            if (pageData != null && num > 0) {
                XmlBean houseInfo = pageData.getBeanByPath("PageData.Row[0].HouseInfo");
                ctType = pageData.getStrValue("PageData.Row[0].HouseInfo.HsCtInfo.ctType");
                chHsStatus = pageData.getStrValue("PageData.Row[0].HouseInfo.HsCtInfo.chHsStatus");
                modelMap.put("hsInfo", houseInfo.getRootNode());
                // 已经选房的数据
                int count = pageData.getListNum("PageData.Row");
                List hsInfos = new ArrayList();
                for (int i = 0; i < count; i++) {
                    XmlBean hsInfo = pageData.getBeanByPath("PageData.Row[${i}].HouseInfo");
                    // 选房信息的打印状态
                    String tempPrint = hsInfo.getStrValue("HouseInfo.HsCtInfo.HsCtChooseInfo.choosePrintStatus");
                    // 未打印标记
                    if (!StringUtil.isEqual("1", tempPrint)) {
                        allChPrint = false;
                    }
                    hsInfos.add(hsInfo.getRootNode());
                }
                modelMap.put("hsInfos", hsInfos);
                // 获取房屋产信息
                XmlBean ownerInfo = svcResponse.getFirstOpRsp("QUERY_ENTITY_CMPT").getBeanByPath("Operation.OpResult");
                int ownerCount = ownerInfo.getListNum("OpResult.PageData.Row");
                for (int i = 0; i < ownerCount; i++) {
                    ownerPsId.add(ownerInfo.getStrValue("OpResult.PageData.Row[${i}].ownerPsId"));
                }
                // 控制信息
                XmlBean spaceTimeXml = svcResponse.getFirstOpRsp("QUERY_CONTROL_INFO");
                if (spaceTimeXml != null) {
                    spaceTimeXml = spaceTimeXml.getBeanByPath("Operation.OpResult.SysControlInfo");
                }
                if (spaceTimeXml == null) {
                    spaceTimeXml = new XmlBean("<SysControlInfo/>");
                }
                String spaceTime = spaceTimeXml.getStrValue("SysControlInfo.spaceTime");
                String startCd = spaceTimeXml.getStrValue("SysControlInfo.startCd");
                modelMap.put("startCd", startCd);
                if ("1".equals(startCd)) {
                    modelMap.put("spaceTime", spaceTime);
                }
            }
        }
        modelMap.put("ctType", ctType);

        if (StringUtil.isEqual("1", chHsStatus) && ownerPsId.size() > 0) {
            // 为签约获取人员信息
            // 查询人员信息
            svcRequest = RequestUtil.getSvcRequest(request);
            // 查询户籍人员详细信息
            if (ownerPsId.size() > 0) {
                // 查询选房控制信息
                opData = new XmlBean();
                opData.setStrValue(prePath + "queryType", "2");
                opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "HouseInfo.hsId");
                opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", hsId);
                opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
                opData.setStrValue(prePath + "ResultFields.fieldName[0]", "HouseInfo.hsFullAddr");
                svcRequest.addOp("QUERY_HS_CHOOSE_REGION", opData);
                // 产权人信息
                opData = new XmlBean();
                opData.setStrValue("OpData.entityName", "PersonInfo");
                opData.setStrValue("OpData.Conditions.Condition[0].fieldName", "personId");
                opData.setStrValue("OpData.Conditions.Condition[0].operation", "in");
                opData.setStrValue("OpData.Conditions.Condition[0].fieldValue", "(" + ownerPsId.join(",") + ")");
                opData.setStrValue("OpData.ResultFields.fieldName[0]", "personId");
                opData.setStrValue("OpData.ResultFields.fieldName[1]", "personName");
                opData.setStrValue("OpData.ResultFields.fieldName[2]", "personCertyNum");
                opData.setStrValue("OpData.ResultFields.fieldName[3]", "personTelphone");
                opData.setStrValue("OpData.ResultFields.fieldName[4]", "personNoticeAddr");
                opData.setStrValue("OpData.SortFields.SortField[0].fieldName", "personId");
                opData.setStrValue("OpData.SortFields.SortField[0].sortOrder", "asc");
                svcRequest.addOp("QUERY_ENTITY_CMPT", opData);
                SvcResponse svcRsp = query(svcRequest);

                if (svcRsp.isSuccess()) {
                    // 获取可选房源控制信息
                    XmlBean canChooseRegionBean = svcRsp.getFirstOpRsp("QUERY_HS_CHOOSE_REGION")
                            .getBeanByPath("Operation.OpResult.Ext");
                    String areaSize = canChooseRegionBean.getStrValue("Ext.areaSize");
                    String houseType = canChooseRegionBean.getStrValue("Ext.houseType");
                    String houseNum = canChooseRegionBean.getStrValue("Ext.houseNum");
                    modelMap.put("areaSize", areaSize);
                    modelMap.put("houseType", houseType);
                    modelMap.put("houseNum", houseNum);
                    // 产权人信息
                    if (ownerPsId.size() == 1) {
                        // 单人持有产权
                        XmlBean ownerInfo = svcRsp.getFirstOpRsp("QUERY_ENTITY_CMPT")
                                .getBeanByPath("Operation.OpResult.PageData.Row[0]");
                        ownerInfo.setStrValue("Row.buyPersonOwnerRel", "本人")
                        modelMap.put("ownerInfo", ownerInfo.getRootNode());
                    } else {
                        // 多人持有产权
                        Map<String, XmlBean> tempMap = new HashMap<String, XmlBean>(ownerPsId.size());
                        // 为了保障产权人的顺序，暂时保存产权人信息
                        XmlBean tempBean = svcRsp.getFirstOpRsp("QUERY_ENTITY_CMPT").getBeanByPath("Operation.OpResult.PageData");
                        int resultCount = tempBean.getListNum("PageData.Row");
                        for (int k = 0; k < resultCount; k++) {
                            XmlBean tempRow = tempBean.getBeanByPath("PageData.Row[${k}]");
                            tempMap.put(tempRow.getStrValue("Row.personId"), tempRow);
                        }
                        String tempPersonIds = "";
                        String tempPersonNames = "";
                        String tempPersonCertyNums = "";
                        String tempPersonTelphones = "";
                        String tempPersonNoticeAddrs = "";
                        String tempBuyPersonOwnerRels = "";
                        for (String tempId : ownerPsId) {
                            XmlBean tempRow = tempMap.get(tempId);
                            if (tempRow != null) {
                                tempPersonIds = tempPersonIds + "," + tempRow.getStrValue("Row.personId");
                                tempPersonNames = tempPersonNames + "，" + tempRow.getStrValue("Row.personName");
                                tempBuyPersonOwnerRels = tempBuyPersonOwnerRels + "，" + "本人";
                                tempPersonCertyNums = tempPersonCertyNums + "，" + tempRow.getStrValue("Row.personCertyNum");
                                tempPersonTelphones = tempPersonTelphones + "，" + tempRow.getStrValue("Row.personTelphone");
                                tempPersonNoticeAddrs = tempPersonNoticeAddrs + "，" + tempRow.getStrValue("Row.personNoticeAddr");
                            }
                        }
                        XmlBean ownerInfo = new XmlBean();
                        ownerInfo.setStrValue("Row.personId", tempPersonIds.substring(1));
                        ownerInfo.setStrValue("Row.personName", tempPersonNames.substring(1));
                        ownerInfo.setStrValue("Row.buyPersonOwnerRel", tempBuyPersonOwnerRels.substring(1));
                        ownerInfo.setStrValue("Row.personCertyNum", tempPersonCertyNums.substring(1));
                        ownerInfo.setStrValue("Row.personTelphone", tempPersonTelphones.substring(1));
                        ownerInfo.setStrValue("Row.personNoticeAddr", tempPersonNoticeAddrs.substring(1));
                        modelMap.put("ownerInfo", ownerInfo.getRootNode());
                    }
                } else {
                    modelMap.put("areaSize", "0-0");
                    modelMap.put("houseType", "选房控制获取失败，请联系管理员!");
                    modelMap.put("houseNum", "0");
                }
            }
        } else if (StringUtil.isEqual("2", chHsStatus)) {
            boolean temp = !allChPrint || (allChPrint && "print".equals(showFlag));
            if (allChPrint && !"print".equals(showFlag)) {
                // 存在未打印的选房序号单
                svcRequest = RequestUtil.getSvcRequest(request);
                // 获取可用房源区域列表
                opData = new XmlBean();
                prePath = "OpData.";
                opData.setStrValue(prePath + "entityName", "SvcDocInfo");
                opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "relId");
                opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", hsId);
                opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
                //只查询状态为 1 的，
                opData.setStrValue(prePath + "Conditions.Condition[1].fieldName", "statusCd");
                opData.setStrValue(prePath + "Conditions.Condition[1].fieldValue", "1");
                opData.setStrValue(prePath + "Conditions.Condition[1].operation", "=");
                // 需要查询的字段
                opData.setStrValue(prePath + "ResultFields.fieldName[0]", "docId");
                opData.setStrValue(prePath + "ResultFields.fieldName[1]", "docTypeName");
                opData.setStrValue(prePath + "ResultFields.fieldName[2]", "docName");
                // 增加查询
                svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
                SvcResponse svcRsp = query(svcRequest);
                // 返回处理结果
                if (svcRsp.isSuccess()) {
                    // 生成选房确认单
                    XmlBean queryResult = svcRsp.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult");
                    // 处理查询结果，返回json格式数据
                    if (queryResult != null) {
                        int rowCount = queryResult.getListNum("OpResult.PageData.Row");
                        List<XmlNode> hsDocs = new ArrayList<XmlNode>(rowCount);
                        String signDocIds = "";
                        for (int i = 0; i < rowCount; i++) {
                            hsDocs.add(queryResult.getBeanByPath("OpResult.PageData.Row[${i}]").getRootNode());
                            String docType = queryResult.getStrValue("OpResult.PageData.Row[${i}].docTypeName");
                            if (StringUtil.isEqual(docType, "选房确认单")) {
                                signDocIds += queryResult.getStrValue("OpResult.PageData.Row[${i}].docId") + ",";
                            }
                        }
                        modelMap.put("hsDocs", hsDocs);
                        if (signDocIds.length() > 0) {
                            modelMap.put("signDocIds", signDocIds.substring(0, signDocIds.length() - 1));
                        }
                    }
                }
                return new ModelAndView("/eland/ch/ch001/ch00103", modelMap);
            } else {
                //
                return new ModelAndView("/eland/ch/ch001/ch00102", modelMap);
            }
        }
        return new ModelAndView("/eland/ch/ch001/ch00101", modelMap);
    }

    /**
     * 选房模式01：2: 根据户型居室控制选房信息
     * @param request
     * @param modelMap
     * @return
     */
    private ModelAndView chsModel02(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        // 获取居室列表
        modelMap.put("HS_ROOM_TYPE", getCfgCollection(request, "HS_ROOM_TYPE", true));
        // 获取房屋编号
        String hsId = request.getParameter("hsId");
        String showFlag = request.getParameter("showFlag");
        modelMap.put("hsId", hsId);
        // 调用服务，获取居民基本信息和签约状态信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String prePath = "OpData.";
        XmlBean opData = null;
        // 获取基本信息
        opData = new XmlBean();
        opData.setStrValue(prePath + "entityName", "HouseInfo");
        opData.setStrValue(prePath + "queryType", "2");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "HouseInfo.hsId");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", hsId);
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
        int addCount = 0;
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.hsCd");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.hsFullAddr");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.hsOwnerPersons");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.HsCtInfo.ctType");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.HsCtInfo.hsCtId");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.HsCtInfo.chHsStatus");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.HsCtInfo.chooseHsSid");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.HsCtInfo.HsCtChooseInfo.chooseHsAddr");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.HsCtInfo.HsCtChooseInfo.chooseStatus");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.HsCtInfo.HsCtChooseInfo.hsCtChooseId");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.HsCtInfo.HsCtChooseInfo.buyPersonName");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.HsCtInfo.HsCtChooseInfo.buyPersonCerty");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.HsCtInfo.HsCtChooseInfo.buyPersonTel");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.HsCtInfo.HsCtChooseInfo.buyPersonOwnerRel");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.HsCtInfo.HsCtChooseInfo.choosePrintStatus");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.HsCtInfo.HsCtChooseInfo.personNoticeAddr");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.hsCd");
        //配比信息
        Map<String, String> hsTps = getCfgCollection(request, "NEW_HS_JUSH", true, NumberUtil.getIntFromObj(request.getParameter("prjCd")));
        for (Map.Entry<String, String> mapItem : hsTps) {
            opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.HsCtInfo.hsJs" + mapItem.getKey());
        }
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.HsCtInfo.HsCtChooseInfo.chooseDrawStatus");
        // 添加组件执行分页数据查询
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);

        // 获取控制信息
        opData = new XmlBean();
        opData.setStrValue("OpData.SysControlInfo.scType", "2");
        svcRequest.addOp("QUERY_CONTROL_INFO", opData);
        // 房屋产权人信息
        opData = new XmlBean();
        opData.setStrValue("OpData.entityName", "HouseInfo");
        opData.setStrValue("OpData.Conditions.Condition[0].fieldName", "hsId");
        opData.setStrValue("OpData.Conditions.Condition[0].operation", "=");
        opData.setStrValue("OpData.Conditions.Condition[0].fieldValue", hsId);
        opData.setStrValue("OpData.ResultFields.fieldName[0]", "ownerPsId");
        svcRequest.addOp("QUERY_ENTITY_CMPT", opData);
        // 调用服务
        String chHsStatus = "";
        String ctType = "";
        boolean allChPrint = true;
        SvcResponse svcResponse = query(svcRequest);
        // 查询产区人信息
        Set<String> ownerPsId = new LinkedHashSet<String>();
        if (svcResponse.isSuccess()) {
            // 房屋信息
            XmlBean pageData = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult.PageData");
            int num = pageData.getListNum("PageData.Row")
            if (pageData != null && num > 0) {
                //配比信息
                String[] tempArr = new String[hsTps.size()];
                int tempCnt = 0;
                for (Map.Entry<String, String> tempEntry : hsTps) {
                    // TODO: 字段名称规则强制
                    String value = pageData.getStrValue("PageData.Row[0].HouseInfo.HsCtInfo.hsJs" + tempEntry.getKey());
                    if (StringUtil.isEmptyOrNull(value)) {
                        value = "0";
                    }
                    tempArr[tempCnt++] = value;
                }
                modelMap.put("tempArr", tempArr);
                modelMap.put("hsTps", hsTps);

                XmlBean houseInfo = pageData.getBeanByPath("PageData.Row[0].HouseInfo");
                chHsStatus = pageData.getStrValue("PageData.Row[0].HouseInfo.HsCtInfo.chHsStatus");
                modelMap.put("hsInfo", houseInfo.getRootNode());
                // 已经选房的数据
                int count = pageData.getListNum("PageData.Row");
                List hsInfos = new ArrayList();
                for (int i = 0; i < count; i++) {
                    XmlBean hsInfo = pageData.getBeanByPath("PageData.Row[${i}].HouseInfo");
                    // 选房信息的打印状态
                    String tempPrint = hsInfo.getStrValue("HouseInfo.HsCtInfo.HsCtChooseInfo.choosePrintStatus");
                    // 未打印标记
                    if (!StringUtil.isEqual("1", tempPrint)) {
                        allChPrint = false;
                    }
                    hsInfos.add(hsInfo.getRootNode());
                }
                modelMap.put("hsInfos", hsInfos);
                // 获取房屋产信息
                XmlBean ownerInfo = svcResponse.getFirstOpRsp("QUERY_ENTITY_CMPT").getBeanByPath("Operation.OpResult");
                int ownerCount = ownerInfo.getListNum("OpResult.PageData.Row");
                for (int i = 0; i < ownerCount; i++) {
                    ownerPsId.add(ownerInfo.getStrValue("OpResult.PageData.Row[${i}].ownerPsId"));
                }
                // 控制信息
                XmlBean spaceTimeXml = svcResponse.getFirstOpRsp("QUERY_CONTROL_INFO");
                if (spaceTimeXml != null) {
                    spaceTimeXml = spaceTimeXml.getBeanByPath("Operation.OpResult.SysControlInfo");
                }
                if (spaceTimeXml == null) {
                    spaceTimeXml = new XmlBean("<SysControlInfo/>");
                }
                String spaceTime = spaceTimeXml.getStrValue("SysControlInfo.spaceTime");
                String startCd = spaceTimeXml.getStrValue("SysControlInfo.startCd");
                modelMap.put("startCd", startCd);
                if ("1".equals(startCd)) {
                    modelMap.put("spaceTime", spaceTime);
                }
            }
        }
        // 选房模式
        if (StringUtil.isEqual("1", chHsStatus) || StringUtil.isEqual("choose", showFlag)) {
            //查询选房信息
            XmlBean contractReqData = new XmlBean();
            contractReqData.setStrValue("OpData.HsCtInfo.hsId", hsId);
            svcRequest.addOp("QUERY_HS_CT_INFO", contractReqData);
            //整体调用服务查询
            svcResponse = query(svcRequest);
            //获取签约信息查询结果
            XmlBean conRspData = svcResponse.getFirstOpRsp("QUERY_HS_CT_INFO");
            if (conRspData != null) {
                int chooseRegNum = conRspData.getListNum("Operation.OpResult.HsCtInfo.chooseHs");
                //[选房区域id，选房bean]
                List<XmlNode> chRegList = new LinkedList<>();
                for (int i = 0; i < chooseRegNum; i++) {
                    XmlBean chooseRegBean = conRspData.getBeanByPath("Operation.OpResult.HsCtInfo.chooseHs[${i}]");
                    chRegList.add(chooseRegBean.getRootNode());
                }
                modelMap.put("chRegList", chRegList);
            }
            modelMap.put("showFlag", showFlag);
            return new ModelAndView("/eland/ch/ch001/ch00104", modelMap);
        } else if (StringUtil.isEqual("2", chHsStatus)) {
            if (allChPrint && !"print".equals(showFlag)) {
                // 存在未打印的选房序号单
                svcRequest = RequestUtil.getSvcRequest(request);
                // 获取可用房源区域列表
                opData = new XmlBean();
                prePath = "OpData.";
                opData.setStrValue(prePath + "entityName", "SvcDocInfo");
                opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "relId");
                opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", hsId);
                opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
                //只查询状态为 1 的，
                opData.setStrValue(prePath + "Conditions.Condition[1].fieldName", "statusCd");
                opData.setStrValue(prePath + "Conditions.Condition[1].fieldValue", "1");
                opData.setStrValue(prePath + "Conditions.Condition[1].operation", "=");
                // 需要查询的字段
                opData.setStrValue(prePath + "ResultFields.fieldName[0]", "docId");
                opData.setStrValue(prePath + "ResultFields.fieldName[1]", "docTypeName");
                opData.setStrValue(prePath + "ResultFields.fieldName[2]", "docName");
                // 增加查询
                svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
                SvcResponse svcRsp = query(svcRequest);
                // 返回处理结果
                if (svcRsp.isSuccess()) {
                    // 生成选房确认单
                    XmlBean queryResult = svcRsp.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult");
                    // 处理查询结果，返回json格式数据
                    if (queryResult != null) {
                        int rowCount = queryResult.getListNum("OpResult.PageData.Row");
                        List<XmlNode> hsDocs = new ArrayList<XmlNode>(rowCount);
                        String signDocIds = "";
                        for (int i = 0; i < rowCount; i++) {
                            hsDocs.add(queryResult.getBeanByPath("OpResult.PageData.Row[${i}]").getRootNode());
                            String docType = queryResult.getStrValue("OpResult.PageData.Row[${i}].docTypeName");
                            if (StringUtil.isEqual(docType, "选房确认单")) {
                                signDocIds += queryResult.getStrValue("OpResult.PageData.Row[${i}].docId") + ",";
                            }
                        }
                        modelMap.put("hsDocs", hsDocs);
                        if (signDocIds.length() > 0) {
                            modelMap.put("signDocIds", signDocIds.substring(0, signDocIds.length() - 1));
                        }
                    }
                }
                return new ModelAndView("/eland/ch/ch001/ch00103", modelMap);
            } else {
                return new ModelAndView("/eland/ch/ch001/ch001021", modelMap);
            }
        }
    }

    /**
     * 选房模式03：根据选房套餐控制选房信息
     * @param request
     * @param modelMap
     * @return
     */
    private ModelAndView chsModel03(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/eland/ch/ch001/ch00130", modelMap);
    }

    /**
     * 选房模式03：根据选房套餐控制选房信息
     * @param request
     * @param modelMap
     * @return
     */
    public ModelAndView chsModel03Choose(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        // 获取居室列表
        modelMap.put("HS_ROOM_TYPE", getCfgCollection(request, "HS_ROOM_TYPE", true));
        // 获取房屋编号
        String hsId = request.getParameter("hsId");
        String showFlag = request.getParameter("showFlag");
        modelMap.put("hsId", hsId);
        // 调用服务，获取居民基本信息和签约状态信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String prePath = "OpData.";
        XmlBean opData = null;
        // 获取基本信息
        opData = new XmlBean();
        opData.setStrValue(prePath + "entityName", "HouseInfo");
        opData.setStrValue(prePath + "queryType", "2");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "HouseInfo.hsId");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", hsId);
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
        int addCount = 0;
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.hsCd");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.hsFullAddr");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.hsOwnerPersons");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.hsOwnerCertyNum");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.hsOwnerTelphone");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.HsCtInfo.ctType");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.HsCtInfo.hsCtId");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.HsCtInfo.chHsStatus");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.HsCtInfo.chooseHsSid");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.HsCtInfo.HsCtChooseInfo.chooseHsAddr");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.HsCtInfo.HsCtChooseInfo.chooseStatus");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.HsCtInfo.HsCtChooseInfo.chooseHsSize");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.HsCtInfo.HsCtChooseInfo.hsCtChooseId");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.HsCtInfo.HsCtChooseInfo.buyPersonName");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.HsCtInfo.HsCtChooseInfo.buyPersonCerty");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.HsCtInfo.HsCtChooseInfo.buyPersonTel");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.HsCtInfo.HsCtChooseInfo.buyPersonOwnerRel");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.HsCtInfo.HsCtChooseInfo.choosePrintStatus");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.HsCtInfo.HsCtChooseInfo.personNoticeAddr");
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.hsCd");
        //配比信息
        Map<String, String> hsTps = getCfgCollection(request, "NEW_HS_PROD", true, NumberUtil.getIntFromObj(request.getParameter("prjCd")));
        for (Map.Entry<String, String> mapItem : hsTps) {
            opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.HsCtInfo.chp" + mapItem.getKey());
        }
        opData.setStrValue(prePath + "ResultFields.fieldName[${addCount++}]", "HouseInfo.HsCtInfo.HsCtChooseInfo.chooseDrawStatus");
        // 添加组件执行分页数据查询
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);

        // 获取控制信息
        opData = new XmlBean();
        opData.setStrValue("OpData.SysControlInfo.scType", "2");
        svcRequest.addOp("QUERY_CONTROL_INFO", opData);
        // 房屋产权人信息
        opData = new XmlBean();
        opData.setStrValue("OpData.entityName", "HouseInfo");
        opData.setStrValue("OpData.Conditions.Condition[0].fieldName", "hsId");
        opData.setStrValue("OpData.Conditions.Condition[0].operation", "=");
        opData.setStrValue("OpData.Conditions.Condition[0].fieldValue", hsId);
        opData.setStrValue("OpData.ResultFields.fieldName[0]", "ownerPsId");
        svcRequest.addOp("QUERY_ENTITY_CMPT", opData);
        // 调用服务
        String chHsStatus = "";
        String ctType = "";
        boolean allChPrint = true;
        SvcResponse svcResponse = query(svcRequest);
        // 查询产区人信息
        Set<String> ownerPsId = new LinkedHashSet<String>();
        if (svcResponse.isSuccess()) {
            // 房屋信息
            XmlBean pageData = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult.PageData");
            int num = pageData.getListNum("PageData.Row")
            if (pageData != null && num > 0) {
                // 配比信息
                Map<String, String> chpMap = new LinkedHashMap<String, String>();
                Map<String, String> chpSizeMap = new LinkedHashMap<String, String>();
                int tempCnt = 0;
                for (Map.Entry<String, String> tempEntry : hsTps) {
                    String value = pageData.getStrValue("PageData.Row[0].HouseInfo.HsCtInfo.chp" + tempEntry.getKey());
                    if (StringUtil.isEmptyOrNull(value)) {
                        value = "0";
                    }
                    chpMap.put(tempEntry.getValue(), value);
                    chpSizeMap.put(tempEntry.getKey(), value);
                }
                modelMap.put("chpMap", chpMap);
                modelMap.put("chpSizeMap", chpSizeMap);

                XmlBean houseInfo = pageData.getBeanByPath("PageData.Row[0].HouseInfo");
                chHsStatus = pageData.getStrValue("PageData.Row[0].HouseInfo.HsCtInfo.chHsStatus");
                modelMap.put("hsInfo", houseInfo.getRootNode());
                // 已经选房的数据
                int count = pageData.getListNum("PageData.Row");
                List hsInfos = new ArrayList();
                for (int i = 0; i < count; i++) {
                    XmlBean hsInfo = pageData.getBeanByPath("PageData.Row[${i}].HouseInfo");
                    // 选房信息的打印状态
                    String tempPrint = hsInfo.getStrValue("HouseInfo.HsCtInfo.HsCtChooseInfo.choosePrintStatus");
                    // 未打印标记
                    if (!StringUtil.isEqual("1", tempPrint)) {
                        allChPrint = false;
                    }
                    hsInfos.add(hsInfo.getRootNode());
                }
                modelMap.put("hsInfos", hsInfos);
                // 获取房屋产信息
                XmlBean ownerInfo = svcResponse.getFirstOpRsp("QUERY_ENTITY_CMPT").getBeanByPath("Operation.OpResult");
                int ownerCount = ownerInfo.getListNum("OpResult.PageData.Row");
                for (int i = 0; i < ownerCount; i++) {
                    ownerPsId.add(ownerInfo.getStrValue("OpResult.PageData.Row[${i}].ownerPsId"));
                }
                // 控制信息
                XmlBean spaceTimeXml = svcResponse.getFirstOpRsp("QUERY_CONTROL_INFO");
                if (spaceTimeXml != null) {
                    spaceTimeXml = spaceTimeXml.getBeanByPath("Operation.OpResult.SysControlInfo");
                }
                if (spaceTimeXml == null) {
                    spaceTimeXml = new XmlBean("<SysControlInfo/>");
                }
                String spaceTime = spaceTimeXml.getStrValue("SysControlInfo.spaceTime");
                String startCd = spaceTimeXml.getStrValue("SysControlInfo.startCd");
                modelMap.put("startCd", startCd);
                if ("1".equals(startCd)) {
                    modelMap.put("spaceTime", spaceTime);
                }
            }
        }
        // 控制是否已经选房
        if (StringUtil.isEqual("1", chHsStatus) || StringUtil.isEqual("choose", showFlag)) {
            // 查询选房信息
            XmlBean contractReqData = new XmlBean();
            contractReqData.setStrValue("OpData.HsCtInfo.hsId", hsId);
            svcRequest.addOp("QUERY_HS_CT_INFO", contractReqData);
            //整体调用服务查询
            svcResponse = query(svcRequest);

            //获取签约信息查询结果
            XmlBean conRspData = svcResponse.getFirstOpRsp("QUERY_HS_CT_INFO");
            if (conRspData != null) {
                int chooseRegNum = conRspData.getListNum("Operation.OpResult.HsCtInfo.chooseHs");
                //[选房区域id，选房bean]
                List<XmlNode> chRegList = new LinkedList<>();
                for (int i = 0; i < chooseRegNum; i++) {
                    XmlBean chooseRegBean = conRspData.getBeanByPath("Operation.OpResult.HsCtInfo.chooseHs[${i}]");
                    chRegList.add(chooseRegBean.getRootNode());
                }
                modelMap.put("chRegList", chRegList);
            }
            modelMap.put("showFlag", showFlag);
            return new ModelAndView("/eland/ch/ch001/ch00131", modelMap);

        } else if (StringUtil.isEqual("2", chHsStatus)) {
            // 已经选房
            if (allChPrint && !"print".equals(showFlag)) {
                // 存在未打印的选房序号单
                svcRequest = RequestUtil.getSvcRequest(request);
                // 获取可用房源区域列表
                opData = new XmlBean();
                prePath = "OpData.";
                opData.setStrValue(prePath + "entityName", "SvcDocInfo");
                opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "relId");
                opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", hsId);
                opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
                //只查询状态为 1 的，
                opData.setStrValue(prePath + "Conditions.Condition[1].fieldName", "statusCd");
                opData.setStrValue(prePath + "Conditions.Condition[1].fieldValue", "1");
                opData.setStrValue(prePath + "Conditions.Condition[1].operation", "=");
                // 需要查询的字段
                opData.setStrValue(prePath + "ResultFields.fieldName[0]", "docId");
                opData.setStrValue(prePath + "ResultFields.fieldName[1]", "docTypeName");
                opData.setStrValue(prePath + "ResultFields.fieldName[2]", "docName");
                // 增加查询
                svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
                SvcResponse svcRsp = query(svcRequest);
                // 返回处理结果
                if (svcRsp.isSuccess()) {
                    // 生成选房确认单
                    XmlBean queryResult = svcRsp.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult");
                    // 处理查询结果，返回json格式数据
                    if (queryResult != null) {
                        int rowCount = queryResult.getListNum("OpResult.PageData.Row");
                        List<XmlNode> hsDocs = new ArrayList<XmlNode>(rowCount);
                        String signDocIds = "";
                        for (int i = 0; i < rowCount; i++) {
                            hsDocs.add(queryResult.getBeanByPath("OpResult.PageData.Row[${i}]").getRootNode());
                            String docType = queryResult.getStrValue("OpResult.PageData.Row[${i}].docTypeName");
                            if (StringUtil.isEqual(docType, "选房确认单")) {
                                signDocIds += queryResult.getStrValue("OpResult.PageData.Row[${i}].docId") + ",";
                            }
                        }
                        modelMap.put("hsDocs", hsDocs);
                        if (signDocIds.length() > 0) {
                            modelMap.put("signDocIds", signDocIds.substring(0, signDocIds.length() - 1));
                        }
                    }
                }
                return new ModelAndView("/eland/ch/ch001/ch00103", modelMap);
            } else {
                return new ModelAndView("/eland/ch/ch001/ch00132", modelMap);
            }
        }
    }

    /**
     * 生成确认单
     * @param request 请求信息
     * @param response 响应结果
     * @return
     */
    public ModelAndView createConfirm(HttpServletRequest request, HttpServletResponse response) {
        RequestUtil requestUtil = new RequestUtil(request);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 输入参数
        String hsId = request.getParameter("hsId");
        String newHsId = request.getParameter("newHsId");

        String agent = request.getHeader("USER-AGENT");
        if (StringUtil.isNotEmptyOrNull(hsId)) {
            XmlBean paramData = new XmlBean();
            // 系统配置表
            Map<String, String> sysFormCfg = getCfgCollection(request, "PRJ_FORM", true, svcRequest.getPrjCd());
            // 生成报表模板
            paramData.setStrValue("OpData.formCd", sysFormCfg.get("ctAndChFrm"));
            // 生成报表参数
            paramData.setStrValue("OpData.Report.Parameter[0].entityName", "HouseInfo");
            paramData.setStrValue("OpData.Report.Parameter[0].Property[0].attrName", "hsId");
            paramData.setStrValue("OpData.Report.Parameter[0].Property[0].value", request.getParameter("hsId"));
            //标志位 用于判断取那个模板
            paramData.setStrValue("OpData.Report.Parameter[1].Property[0].attrName", "sourceType");
            paramData.setStrValue("OpData.Report.Parameter[1].Property[0].value", "chooseCfmWq");
            paramData.setStrValue("OpData.Report.Parameter[1].Property[1].attrName", "newHsId");
            paramData.setStrValue("OpData.Report.Parameter[1].Property[1].value", newHsId);

            // 增加到请求参数,调用服务
            svcRequest.addOp("GENERATE_REPORT", paramData);
            // 调用服务，执行数据查询
            SvcResponse svcResponse = transaction(svcRequest);

            // 处理服务返回结果
            String errMsg = "生成文档失败，请联系管理员!"
            boolean isSuccess = svcResponse.isSuccess();
            if (isSuccess) {
                XmlBean opResult = svcResponse.getFirstOpRsp("GENERATE_REPORT");

                if (opResult != null) {
                    // 返回结果类型
                    String resultType = opResult.getValue("Operation.OpResult.resultType");
                    if ("1".equals(resultType)) {
                        String docPath = opResult.getValue("Operation.OpResult.resultParam");
                        String docPrePath = "/reports/report"
                        String fromOp = request.getParameter("fromOp");
                        if (StringUtil.isEqual(fromOp, "view")) {
                            //本地查看
                            ModelMap modelMap = new ModelMap();
                            modelMap.put("docFileUrl", docPrePath + docPath);
                            return new ModelAndView("/eland/fm/fm003/fm003_doc_iframe", modelMap)
                        } else if (StringUtil.isEqual(fromOp, "download")) {
                            // 获取下载文件的后缀
                            String docExt = docPath.substring(docPath.lastIndexOf("."), docPath.length());
                            // 生成下载文件的展示名称
                            String docName = request.getParameter("docName");
                            if (StringUtil.isEmptyOrNull(docName)) {
                                docName = "确认单(" + hsId + ")";
                            } else {
                                docName = java.net.URLDecoder.decode(docName, "utf-8")
                            }
                            docName = docName + "." + docExt;
                            // 获取下载的本地文件
                            String contextType = requestUtil.getStrParam("contextType");
                            File localFile = new File(StringUtil.formatFilePath("webroot:" + docPrePath + docPath));
                            // 文件下载输出到响应流
                            ResponseUtil.downloadFile(response, contextType, docName, localFile, agent, false, true);
                            return;
                        }
                    } else {
                        errMsg = errMsg + "只有文档类型的模板允许在该模块中生成,请调整表单配置!"
                    }
                }
            } else {
                errMsg = "生成文档失败，错误原因:\"" + svcResponse.getErrMsg() + "\"";
            }
            ModelMap modelMap = new ModelMap();
            modelMap.put("errMsg", errMsg);
            return new ModelAndView("/eland/ct/ct001/ct001_doc_error", modelMap)
        }
    }
    /**
     * 领取选房单
     * @param request 请求信息
     * @param response 响应结果
     * @return
     */
    public void drawConfirm(HttpServletRequest request, HttpServletResponse response) {
        RequestUtil requestUtil = new RequestUtil(request);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 输入参数
        String hsCtChooseId = request.getParameter("hsCtChooseId");
        String agent = request.getHeader("USER-AGENT");
        String jsonStr = "";
        if (StringUtil.isNotEmptyOrNull(hsCtChooseId)) {
            //更新领取状态和时间
            XmlBean opData = new XmlBean();
            opData.setStrValue("OpData.entityName", "HsCtChooseInfo");
            opData.setStrValue("OpData.EntityData.hsCtChooseId", hsCtChooseId);
            opData.setStrValue("OpData.EntityData.chooseDrawStatus", 1);
            opData.setStrValue("OpData.EntityData.chooseDrawDate", svcRequest.getReqTime());
            svcRequest.addOp("SAVE_ENTITY", opData);
            SvcResponse svcResponse = transaction(svcRequest);
            jsonStr = """{success:${svcResponse.isSuccess()}, errMsg:"${svcResponse.getErrMsg()}" }""";
        }
        ResponseUtil.printAjax(response, jsonStr);
    }

    /**
     * 外迁选房 确认选房
     * @param request
     * @param response
     */
    public void cfmHsChoose(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsCtId = request.getParameter("hsCtId");
        String[] newHsId = request.getParameterValues("newHsId");
        String[] oldNewHsId = request.getParameterValues("oldNewHsId");
        String[] hsCtChooseId = request.getParameterValues("hsCtChooseId");
        String[] personNoticeAddr = request.getParameterValues("personNoticeAddr");
        String[] buyPersonTel = request.getParameterValues("buyPersonTel");

        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        opData.setStrValue(prePath + "hsCtId", hsCtId);
        int temp = 0;
        for (int i = 0; i < newHsId.length; i++) {
            opData.setStrValue(prePath + "HsChooses.HsChoose[" + temp + "].hsCtChooseId", hsCtChooseId[i]);
            opData.setStrValue(prePath + "HsChooses.HsChoose[" + temp + "].chooseHsId", newHsId[i]);
            opData.setStrValue(prePath + "HsChooses.HsChoose[" + temp + "].oldNewHsId", oldNewHsId[i]);
            opData.setStrValue(prePath + "HsChooses.HsChoose[" + temp + "].personNoticeAddr", personNoticeAddr[i]);
            opData.setStrValue(prePath + "HsChooses.HsChoose[" + temp++ + "].buyPersonTel", buyPersonTel[i]);
        }
        svcRequest.addOp("CONFIRM_CHOOSE_HS_EX", opData);

        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /**
     * 单人取消选房操作
     * @param request
     * @param response
     */
    public void cancelHsChoose(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.hsCtId", request.getParameter("hsCtId"));
        opData.setStrValue("OpData.HsChoose.hsCtChooseId", request.getParameter("hsCtChooseId"));
        svcRequest.addOp("CANCEL_CHOOSE_HS_EXT_ITEM", opData);
        opData = new XmlBean();
        opData.setStrValue("OpData.entityName", "HsCtChooseInfo");
        opData.setStrValue("OpData.EntityData.hsCtChooseId", request.getParameter("hsCtChooseId"));
        opData.setStrValue("OpData.EntityData.chooseDrawStatus", "0");
        svcRequest.addOp("SAVE_ENTITY", opData);
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /**
     * 单人删除选房操作
     * @param request
     * @param response
     */
    public void deleteHsChoose(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.hsCtId", request.getParameter("hsCtId"));
        opData.setStrValue("OpData.HsChoose.hsCtChooseId", request.getParameter("hsCtChooseId"));
        svcRequest.addOp("DELETE_CHOOSE_HS_EXT_ITEM", opData);
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /** 查询人信息 */
    public ModelAndView queryPer(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsId = request.getParameter("hsId");
        String json = "";

        XmlBean opData = new XmlBean();
        svcRequest = RequestUtil.getSvcRequest(request);
        // 房屋产权人信息
        opData.setStrValue("OpData.entityName", "HouseInfo");
        opData.setStrValue("OpData.Conditions.Condition[0].fieldName", "hsId");
        opData.setStrValue("OpData.Conditions.Condition[0].operation", "=");
        opData.setStrValue("OpData.Conditions.Condition[0].fieldValue", hsId);
        opData.setStrValue("OpData.ResultFields.fieldName[0]", "ownerPsId");
        svcRequest.addOp("QUERY_ENTITY_CMPT", opData);
        SvcResponse svcResponse = query(svcRequest);
        // 查询产区人信息
        Set<String> ownerPsId = new LinkedHashSet<String>();
        if (svcResponse.isSuccess()) {
            // 获取房屋产信息
            XmlBean ownerInfo = svcResponse.getFirstOpRsp("QUERY_ENTITY_CMPT").getBeanByPath("Operation.OpResult");
            int ownerCount = ownerInfo.getListNum("OpResult.PageData.Row");
            for (int i = 0; i < ownerCount; i++) {
                ownerPsId.add(ownerInfo.getStrValue("OpResult.PageData.Row[${i}].ownerPsId"));
            }
        }
        // 查询人员信息
        svcRequest = RequestUtil.getSvcRequest(request);
        // 查询户籍人员详细信息
        if (ownerPsId.size() > 0) {
            opData = new XmlBean();
            opData.setStrValue("OpData.entityName", "PersonInfo");
            opData.setStrValue("OpData.Conditions.Condition[0].fieldName", "personId");
            opData.setStrValue("OpData.Conditions.Condition[0].operation", "in");
            opData.setStrValue("OpData.Conditions.Condition[0].fieldValue", "(" + ownerPsId.join(",") + ")");
            opData.setStrValue("OpData.ResultFields.fieldName[0]", "personId");
            opData.setStrValue("OpData.ResultFields.fieldName[1]", "personName");
            opData.setStrValue("OpData.ResultFields.fieldName[2]", "personCertyNum");
            opData.setStrValue("OpData.ResultFields.fieldName[3]", "personTelphone");
            opData.setStrValue("OpData.ResultFields.fieldName[4]", "personNoticeAddr");
            opData.setStrValue("OpData.SortFields.SortField[0].fieldName", "personId");
            opData.setStrValue("OpData.SortFields.SortField[0].sortOrder", "asc");
            svcRequest.addOp("QUERY_ENTITY_CMPT", opData);
        }
        // 房屋户籍人员信息
        opData = new XmlBean();
        opData.setStrValue("OpData.PersonInfo.hsId", hsId);
        svcRequest.addOp("QUERY_HS_PERSON_INFO", opData);
        svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            // 获取房屋产信息
            if (ownerPsId.size() > 0) {
                ownerPsId.clear();
                XmlBean ownerInfo = svcResponse.getFirstOpRsp("QUERY_ENTITY_CMPT").getBeanByPath("Operation.OpResult");
                int ownerCount = ownerInfo.getListNum("OpResult.PageData.Row");
                for (int i = 0; i < ownerCount; i++) {
                    XmlBean perInfoXml = ownerInfo.getBeanByPath("OpResult.PageData.Row[${i}]");
                    String personId = perInfoXml.getStrValue("Row.personId");
                    String personName = perInfoXml.getStrValue("Row.personName");
                    String familyPersonRel = "本人";
                    String personCertyNum = perInfoXml.getStrValue("Row.personCertyNum");
                    String personTelphone = perInfoXml.getStrValue("Row.personTelphone");
                    String personNoticeAddr = perInfoXml.getStrValue("Row.personNoticeAddr");
                    json += """{"personId": "${personId}", "personName": "${personName}", "familyPersonRel": "${
                        familyPersonRel
                    }", "personCertyNum": "${personCertyNum}", "personTelphone": "${personTelphone}", "personNoticeAddr": "${personNoticeAddr}"},""";
                    // 记录当前人
                    ownerPsId.add(personName);
                }
            }

            // 处理人房屋人员信息
            XmlBean perInfo = svcResponse.getFirstOpRsp("QUERY_HS_PERSON_INFO")
                    .getBeanByPath("Operation.OpResult.Persons");
            if (perInfo != null) {
                int perCount = perInfo.getListNum("Persons.Person");
                List<XmlNode> perInfoXmls = new ArrayList<XmlNode>();
                for (int i = 0; i < perCount; i++) {
                    XmlBean perInfoXml = perInfo.getBeanByPath("Persons.Person[${i}]");
                    String personId = perInfoXml.getStrValue("Person.personId");
                    String personName = perInfoXml.getStrValue("Person.personName");
                    if (ownerPsId.contains(personName)) {
                        continue;
                    }
                    String familyPersonRel = perInfoXml.getStrValue("Person.familyPersonRel");
                    String personCertyNum = perInfoXml.getStrValue("Person.personCertyNum");
                    String personTelphone = perInfoXml.getStrValue("Person.personTelphone");
                    String personNoticeAddr = perInfoXml.getStrValue("Person.personNoticeAddr");
                    json += """{"personId": "${personId}", "personName": "${personName}", "familyPersonRel": "${
                        familyPersonRel
                    }", "personCertyNum": "${personCertyNum}", "personTelphone": "${personTelphone}", "personNoticeAddr": "${personNoticeAddr}"},""";
                }
            }
        }
        if (json.length() > 0) {
            json = json.substring(0, json.length() - 1);
        }
        json = "[" + json + "]";
        ResponseUtil.print(response, json);
    }

    /** 取消选房 */
    public ModelAndView quXiao(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String[] hsCtChooseId = request.getParameter("hsCtChooseId").split(",");
        String hsCtId = request.getParameter("hsCtId");
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.hsCtId", hsCtId);
        int addCount = 0;
        for (int i = 0; i < hsCtChooseId.length; i++) {
            String tempStr = hsCtChooseId[i];
            if (StringUtil.isNotEmptyOrNull(tempStr)) {
                opData.setStrValue("OpData.HsChoose.hsCtChooseId[${addCount++}]", tempStr);
            }
        }
        svcRequest.addOp("CANCEL_CHOOSE_HS", opData);
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /** 取消选房 */
    public ModelAndView quXiaoChoose(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String[] hsCtChooseId = request.getParameter("hsCtChooseId").split(",");
        String hsCtId = request.getParameter("hsCtId");
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.hsCtId", hsCtId);
        for (int i = 0; i < hsCtChooseId.length; i++) {
            opData.setStrValue("OpData.HsChoose.hsCtChooseId[${i}]", hsCtChooseId[i]);
        }
        svcRequest.addOp("CANCEL_CHOOSE_HS_EXT_ITEM", opData);
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /** 回迁选房 确认选房  */
    public void cfmChoose(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsCtId = request.getParameter("hsCtId");
        // 购房人信息
        String[] buyPersonIdArr = request.getParameterValues("buyPersonId");
        String[] buyPersonNameArr = request.getParameterValues("buyPersonName");
        String[] buyPersonCertyNumArr = request.getParameterValues("buyPersonCertyNum");
        String[] buyPersonTelphoneArr = request.getParameterValues("buyPersonTelphone");
        String[] buyPersonOwnerRelArr = request.getParameterValues("buyPersonOwnerRel");

        // 购房信息
        String[] newHsIdArr = request.getParameterValues("newHsId");
        String[] chooseHsAddrArr = request.getParameterValues("chooseHsAddr");
        String[] hsCtChooseIdArr = request.getParameterValues("hsCtChooseId");
        String[] chooseHsHxName = request.getParameterValues("hsHxName");
        String[] chooseHsTp = request.getParameterValues("hsTp");
        String[] chooseHsSize = request.getParameterValues("preBldSize");

        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.hsCtId", hsCtId);
        //
        int addCount = 0;
        for (int i = 0; i < newHsIdArr.length; i++) {
            if (StringUtil.isEmptyOrNull(newHsIdArr[i])) {
                continue;
            }
            String hsCtChooseId = hsCtChooseIdArr[i];
            if (StringUtil.isEmptyOrNull(hsCtChooseId)) {
                hsCtChooseId = "\${hsCtChooseId_${i}}"
            }
            String buyPersonId = buyPersonIdArr[i];
            if (StringUtil.isEmptyOrNull(buyPersonId)) {
                buyPersonId = "\${buyPersonId_${i}}"
            }
            opData.setStrValue("OpData.HsChooses.HsChoose[${addCount}].hsCtChooseId", hsCtChooseId);
            // 房源信息
            opData.setStrValue("OpData.HsChooses.HsChoose[${addCount}].chooseHsId", newHsIdArr[i]);
            opData.setStrValue("OpData.HsChooses.HsChoose[${addCount}].chooseHsAddr", chooseHsAddrArr[i]);
            opData.setStrValue("OpData.HsChooses.HsChoose[${addCount}].chooseHsHxName", chooseHsHxName[i]);
            opData.setStrValue("OpData.HsChooses.HsChoose[${addCount}].chooseHsTp", chooseHsTp[i]);
            opData.setStrValue("OpData.HsChooses.HsChoose[${addCount}].chooseHsSize", chooseHsSize[i]);
            opData.setStrValue("OpData.HsChooses.HsChoose[${addCount}].cfmChooseDate", DateUtil.toStringYmdHms(DateUtil.getSysDate()));
            // 购房人信息
            opData.setStrValue("OpData.HsChooses.HsChoose[${addCount}].buyPersonId", buyPersonId);
            opData.setStrValue("OpData.HsChooses.HsChoose[${addCount}].buyPersonName", buyPersonNameArr[i]);
            opData.setStrValue("OpData.HsChooses.HsChoose[${addCount}].buyPersonCerty", buyPersonCertyNumArr[i]);
            opData.setStrValue("OpData.HsChooses.HsChoose[${addCount}].buyPersonTel", buyPersonTelphoneArr[i]);
            opData.setStrValue("OpData.HsChooses.HsChoose[${addCount++}].buyPersonOwnerRel", buyPersonOwnerRelArr[i]);
        }
        svcRequest.addOp("CONFIRM_CHOOSE_HS", opData);
        SvcResponse svcResponse = transaction(svcRequest);
        XmlBean hsCtChooseId;
        String json = "";
        if (svcResponse.isSuccess()) {
            hsCtChooseId = svcResponse.getFirstOpRsp("CONFIRM_CHOOSE_HS").getBeanByPath("Operation.OpResult.HsChooses");
            if (hsCtChooseId != null) {
                int count = hsCtChooseId.getListNum("HsChooses.HsChoose");
                for (int i = 0; i < count; i++) {
                    String hsCtChoose = hsCtChooseId.getStrValue("HsChooses.HsChoose[${i}].hsCtChooseId");
                    String chooseHsId = hsCtChooseId.getStrValue("HsChooses.HsChoose[${i}].chooseHsId");
                    json += "{'choose_id':'${hsCtChoose}','new_id':'${chooseHsId}'},";
                }
            }
        }
        if (json.length() > 0) {
            json = json.substring(0, json.length() - 1);
        }
        json = "'returnjson':[" + json + "]";
        ResponseUtil.printSvcResponse(response, svcResponse, json);
    }

    /** 保存打印状态  */
    public void doPrint(HttpServletRequest request, HttpServletResponse response) {
        // 获取房屋编号
        String hsId = request.getParameter("hsId");
        // 调用服务，获取居民基本信息和签约状态信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String prePath = "OpData.";
        XmlBean opData = new XmlBean();
        opData.setStrValue(prePath + "queryType", "2");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "HouseInfo.hsId");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", hsId);
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
        //
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "HouseInfo.hsFullAddr");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "HouseInfo.hsOwnerPersons");
        opData.setStrValue(prePath + "ResultFields.fieldName[2]", "HouseInfo.HsCtInfo.ctType");
        opData.setStrValue(prePath + "ResultFields.fieldName[3]", "HouseInfo.HsCtInfo.hsCtId");
        opData.setStrValue(prePath + "ResultFields.fieldName[4]", "HouseInfo.ownerPsId");
        opData.setStrValue(prePath + "ResultFields.fieldName[5]", "HouseInfo.HsCtInfo.HsCtChooseInfo.chooseHsAddr");
        opData.setStrValue(prePath + "ResultFields.fieldName[6]", "HouseInfo.HsCtInfo.HsCtChooseInfo.chooseStatus");
        opData.setStrValue(prePath + "ResultFields.fieldName[7]", "HouseInfo.HsCtInfo.HsCtChooseInfo.hsCtChooseId");
        opData.setStrValue(prePath + "ResultFields.fieldName[8]", "HouseInfo.HsCtInfo.HsCtChooseInfo.buyPersonId");
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
        // 调用服务
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean pageData = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA")
                    .getBeanByPath("Operation.OpResult.PageData");
            if (pageData != null && pageData.getListNum("PageData.Row") > 0) {
                String hsCtId = pageData.getStrValue("PageData.Row[0].HouseInfo.HsCtInfo.hsCtId");
                //保存打印状态
                String hsCtChooseIds = request.getParameter("hsCtChooseIds");
                String[] hsCtChooseIdArr = hsCtChooseIds.split(",");
                for (String temp : hsCtChooseIdArr) {
                    opData = new XmlBean();
                    opData.setStrValue("OpData.entityName", "HsCtChooseInfo");
                    opData.setStrValue("OpData.EntityData.hsCtChooseId", temp);
                    opData.setStrValue("OpData.EntityData.choosePrintStatus", 1);
                    opData.setStrValue("OpData.EntityData.choosePrintDate", svcRequest.getReqTime());
                    svcRequest.addOp("SAVE_ENTITY", opData);
                }

                svcResponse = transaction(svcRequest);
            }
        }
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /** 生成选房确认单 */
    public ModelAndView generateCh(HttpServletRequest request, HttpServletResponse response) {
        // 输入参数
        String hsId = request.getParameter("hsId");
        String ctType = request.getParameter("ctType");
        String sourceType = "chooseCfm";
        if (StringUtil.isEqual("2", ctType)) {
            sourceType = "chooseCfmWq";
        }
        // 调用服务，获取居民基本信息和签约状态信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        RequestUtil requestUtil = new RequestUtil(request);
        // 系统配置表
        Map<String, String> sysFormCfg = getCfgCollection(request, "PRJ_FORM", true, svcRequest.getPrjCd());
        XmlBean paramData = new XmlBean();
        // 生成报表模板
        paramData.setStrValue("OpData.formCd", sysFormCfg.get("ctAndChFrm"));
        // 生成报表参数
        paramData.setStrValue("OpData.Report.Parameter[0].entityName", "HouseInfo");
        paramData.setStrValue("OpData.Report.Parameter[0].Property[0].attrName", "hsId");
        paramData.setStrValue("OpData.Report.Parameter[0].Property[0].value", request.getParameter("hsId"));
        // 协议编号
        paramData.setStrValue("OpData.Report.Parameter[1].Property[0].attrName", "sourceType");
        paramData.setStrValue("OpData.Report.Parameter[1].Property[0].value", sourceType);
        // 增加到请求参数,调用服务
        svcRequest.addOp("GENERATE_REPORT", paramData);
        // 调用服务，执行数据查询
        SvcResponse svcResponse = transaction(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean opResult = svcResponse.getFirstOpRsp("GENERATE_REPORT");
            // 返回结果类型
            String resultType = opResult.getValue("Operation.OpResult.resultType");
            if ("1".equals(resultType)) {
                String docPath = opResult.getValue("Operation.OpResult.resultParam");
                String docPrePath = "/reports/report"
                String fromOp = request.getParameter("fromOp");
                if (StringUtil.isEqual(fromOp, "download")) {
                    // 获取下载文件的后缀
                    String docExt = docPath.substring(docPath.lastIndexOf("."), docPath.length());
                    // 生成下载文件的展示名称
                    String docName = request.getParameter("docName");
                    if (StringUtil.isEmptyOrNull(docName)) {
                        docName = "选房确认单(" + hsId + ")";
                    } else {
                        docName = java.net.URLDecoder.decode(docName, "utf-8")
                    }
                    docName = docName + "." + docExt;
                    // 获取下载的本地文件
                    String contextType = requestUtil.getStrParam("contextType");
                    File localFile = new File(StringUtil.formatFilePath("webroot:" + docPrePath + docPath));
                    // 文件下载输出到响应流
                    ResponseUtil.downloadFile(response, contextType, docName, localFile,
                            request.getHeader("USER-AGENT"), false);
                } else {
                    //本地查看
                    ModelMap modelMap = new ModelMap();
                    modelMap.put("docFileUrl", docPrePath + docPath);
                    return new ModelAndView("/eland/ch/ch001/ch001_doc_iframe", modelMap);
                }
            } else {
                errMsg = errMsg + "只有文档类型的模板允许在该模块中生成,请调整表单配置!"
            }
        }
    }

    /** 查询上一户、下一户hsId */
    public void singleQuery(HttpServletRequest request, HttpServletResponse response) {
        // 获取房屋编号
        RequestUtil requestUtil = new RequestUtil(request);
        String chooseHsSid = requestUtil.getStrParam("chooseHsSid");
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
        // 增加查询条件
        opData.setStrValue(prePath + "Conditions.Condition[${addCount}].fieldName", "HouseInfo.HsCtInfo.chooseHsSid");
        opData.setStrValue(prePath + "Conditions.Condition[${addCount}].operation", operation);
        opData.setStrValue(prePath + "Conditions.Condition[${addCount++}].fieldValue", chooseHsSid);
        // 排序条件
        opData.setStrValue(prePath + "SortFields.SortField.fieldName", "HouseInfo.HsCtInfo.chooseHsSid");
        opData.setStrValue(prePath + "SortFields.SortField.sortOrder", sortOrder);
        // 分页条件
        opData.setStrValue(prePath + "PageInfo.pageSize", "1");
        opData.setStrValue(prePath + "PageInfo.currentPage", "1");
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "HouseInfo.hsId");

        svcRequest.addOp("QUERY_CANDIDATE", opData);

        // 调用服务
        SvcResponse svcResponse = query(svcRequest);
        String hsId = "";
        if (svcResponse.isSuccess()) {
            XmlBean pageData = svcResponse.getFirstOpRsp("QUERY_CANDIDATE")
                    .getBeanByPath("Operation.OpResult.PageData");
            if (pageData != null && pageData.getListNum("PageData.Row") > 0) {
                hsId = pageData.getStrValue("PageData.Row[0].HouseInfo.hsId");
            }
        }
        ResponseUtil.printSvcResponse(response, svcResponse, """hsId:'${hsId}'""");
    }

    /**
     * 按照户型展示图形
     * @param request 请求信息（区域编号， 项目编号，区域编号）
     * @param response 图形输出
     * @return
     */
    public ModelAndView initB(HttpServletRequest request, HttpServletResponse response) {
        // 返回数据
        ModelMap modelMap = new ModelMap();
        // 输入参数
        RequestUtil requestUtil = new RequestUtil(request);
        String regId = request.getParameter("regId");
        String prjCd = request.getParameter("prjCd");
        String regUseType = request.getParameter("regUseType")
        String chsType = request.getParameter("chsType")
        // 查询区域汇总信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 请求参数
        String prePath = "OpData.";
        XmlBean opData = new XmlBean();
        opData.setValue("OpData.PrjReg.prjCd", prjCd);
        opData.setValue("OpData.PrjReg.regUseType", regUseType);
        opData.setValue("OpData.PrjReg.rightControlFlag", false);
        svcRequest.addOp("QUERY_REG_TREE", opData);
        // 查询房源数据
        opData = new XmlBean();
        // 查询实体
        opData.setStrValue(prePath + "entityName", "NewHsInfo");
        //过滤的组织 字段名
        String regOrgIdValue = request.getParameter("rhtOrgId");
        opData.setStrValue(prePath + "OrgFilter.attrName", "NewHsInfo.ttOrgId");
        opData.setStrValue(prePath + "OrgFilter.attrValue", regOrgIdValue);
        opData.setStrValue(prePath + "OrgFilter.rhtType", "2");

        //过滤的区域 字段名
        if (StringUtil.isNotEmptyOrNull(regId)) {
            opData.setStrValue(prePath + "RegFilter.attrName", "NewHsInfo.ttRegId");
            opData.setStrValue(prePath + "RegFilter.attrValue", regId);
            opData.setStrValue(prePath + "RegFilter.regUseType", request.getParameter("regUseType"));
        }
        // 其他查询条件
        String conditionNames = requestUtil.getStrParam("conditionName");
        String conditions = requestUtil.getStrParam("condition");
        String conditionValues = requestUtil.getStrParam("conditionValue");
        String[] conditionFieldArr = conditionNames.split(",");
        String[] conditionArr = conditions.split(",", conditionFieldArr.length);
        String[] conditionValueArr = conditionValues.split(",", conditionFieldArr.length);
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
        // 分页信息
        opData.setStrValue(prePath + "PageInfo.pageSize", "10000");
        opData.setStrValue(prePath + "PageInfo.currentPage", "1");
        //排序
        opData.setStrValue(prePath + "SortFields.SortField[0].fieldName", "NewHsInfo.hsTp");
        opData.setStrValue(prePath + "SortFields.SortField[0].sortOrder", "asc");
        // 需要查询的字段
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "NewHsInfo.hsTp");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "NewHsInfo.hsAddr");
        opData.setStrValue(prePath + "ResultFields.fieldName[2]", "NewHsInfo.statusCd");
        // 街道地址
        opData.setStrValue(prePath + "ResultFields.fieldName[3]", "NewHsInfo.hsSt");
        // 楼宇编号
        opData.setStrValue(prePath + "ResultFields.fieldName[4]", "NewHsInfo.hsNo");
        // 地块
        opData.setStrValue(prePath + "ResultFields.fieldName[5]", "NewHsInfo.hsFl");
        // 单元
        opData.setStrValue(prePath + "ResultFields.fieldName[6]", "NewHsInfo.hsUn");
        // 门牌
        opData.setStrValue(prePath + "ResultFields.fieldName[7]", "NewHsInfo.hsNm");
        // 建筑标准
        opData.setStrValue(prePath + "ResultFields.fieldName[8]", "NewHsInfo.ttRegId");
        //户型名称
        opData.setStrValue(prePath + "ResultFields.fieldName[9]", "NewHsInfo.hsHxName");
        //建筑面积
        opData.setStrValue(prePath + "ResultFields.fieldName[10]", "NewHsInfo.preBldSize");
        // 购房人
        opData.setStrValue(prePath + "ResultFields.fieldName[11]", "NewHsInfo.buyPersonName");
        // 房源编号
        opData.setStrValue(prePath + "ResultFields.fieldName[12]", "NewHsInfo.newHsId");
        // 单独门牌
        opData.setStrValue(prePath + "ResultFields.fieldName[13]", "NewHsInfo.hsOnlyNm");
        // 调用服务
        svcRequest.addOp("QUERY_NEW_HOUSE_REGION", opData);
        SvcResponse svcResponse = query(svcRequest);

        // 处理服务返回结果
        if (svcResponse.isSuccess()) {
            // 解析区域目录树
            XmlBean treeBean = svcResponse.getFirstOpRsp("QUERY_REG_TREE").getBeanByPath("Operation.OpResult.PrjReg");
            // 区域名称映射关系, key: 区域ID、value: 区域Bean
            Map<String, XmlBean> hsAreaMap = new LinkedHashMap<String, XmlBean>();
            Map<String, XmlBean> foundHsAreaMap = new LinkedHashMap<String, XmlBean>();
            if (treeBean != null) {
                int count = treeBean.getListNum("PrjReg.Node");
                // 获取直接挂到根节点下的节点
                for (int i = 0; i < count; i++) {
                    XmlBean tempBean = treeBean.getBeanByPath("PrjReg.Node[${i}]");
                    String id = tempBean.getStrValue("Node.regId");
                    // 保存区域节点
                    hsAreaMap.put(id, tempBean);
                }
            }
            // 获取建筑的楼层数量
            XmlBean opResult = svcResponse.getFirstOpRsp("QUERY_NEW_HOUSE_REGION");
            opResult = opResult.getBeanByPath("Operation.OpResult.PageData");
            // 建筑列表
            Map<String, XmlBean> buildMap = new LinkedHashMap<String, XmlBean>();
            if (opResult != null) {
                int rowCount = opResult.getListNum("PageData.Row");
                // 按照区域-户型编号对查询结果进行排序
                List<XmlBean> sortedHsInfo = new LinkedList<XmlBean>()
                for (int i = 0; i < rowCount; i++) {
                    XmlBean row = opResult.getBeanByPath("PageData.Row[${i}]");
                    sortedHsInfo.add(row);
                }
                Collections.sort(sortedHsInfo, new Comparator<XmlBean>() {
                    @Override
                    int compare(XmlBean o1, XmlBean o2) {
                        String ttRegId01 = o1.getStrValue("Row.NewHsInfo.ttRegId");
                        String ttRegId02 = o2.getStrValue("Row.NewHsInfo.ttRegId");
                        int result = ttRegId01.compareTo(ttRegId02);
                        if (result == 0) {
                            String hsHxName01 = o1.getStrValue("Row.NewHsInfo.hsHxName");
                            String hsHxName02 = o2.getStrValue("Row.NewHsInfo.hsHxName");
                            // 进一步按照户型配需
                            char[] o1Arr = hsHxName01.toCharArray();
                            char[] o2Arr = hsHxName02.toCharArray();
                            int minLength = o1Arr.length < o2Arr.length ? o1Arr.length : o2Arr.length;
                            int sameIdx = -1;
                            for (int k = 0; k < minLength; k++) {
                                if (o1Arr[k] == o2Arr[k]) {
                                    sameIdx = k;
                                    break;
                                }
                            }
                            if (sameIdx == -1) {
                                return hsHxName01.compareTo(hsHxName02);
                            } else if (hsHxName01.length() > hsHxName02.length()) {
                                return 1;
                            } else if (hsHxName01.length() < hsHxName02.length()) {
                                return -1;
                            } else {
                                return hsHxName01.compareTo(hsHxName02);
                            }
                        } else {
                            return result;
                        }
                    }
                });
                // 所有的单元信息， key：区域编号即楼房, value: 楼房的单元信息
                Map<String, Set<String>> units = new LinkedHashMap<String, Set<String>>();
                // 按照“区域编号_单元编号_楼层编号_房间号”的形式存储每个房间
                Map<String, XmlNode> houses = new HashMap<String, XmlNode>(rowCount);
                // 按照“区域编号_单元编号_门牌号”的形式存储门牌的第一个房间
                Map<String, XmlNode> roomNoHsInfo = new HashMap<String, XmlNode>(rowCount);
                int minNum = 0;
                for (int i = 0; i < rowCount; i++) {
                    XmlBean row = sortedHsInfo.get(i);
                    String ttRegId = row.getStrValue("Row.NewHsInfo.ttRegId");
                    String hsSt = row.getStrValue("Row.NewHsInfo.hsSt");
                    String hsArea = row.getStrValue("Row.NewHsInfo.hsFl");
                    String hsNo = row.getStrValue("Row.NewHsInfo.hsNo");
                    String unitName = row.getStrValue("Row.NewHsInfo.hsUn");
                    String hsNm = row.getStrValue("Row.NewHsInfo.hsNm");
                    String hsOnlyNm = row.getStrValue("Row.NewHsInfo.hsOnlyNm");
                    // 对应的房源区域
                    XmlBean hsAreaBean = getHsAreaBean(ttRegId, hsAreaMap, foundHsAreaMap);
                    if (hsAreaBean == null) {
                        continue;
                    }
                    String hsAreaId = hsAreaBean.getStrValue("Node.regId");
                    String hsAreaName = hsAreaBean.getStrValue("Node.regName");
                    row.setStrValue("Row.NewHsInfo.hsAreaId", hsAreaId);
                    row.setStrValue("Row.NewHsInfo.hsAreaName", hsAreaName);

                    // 建筑信息
                    XmlBean buildBean = buildMap.get(ttRegId);
                    if (buildBean == null) {
                        buildBean = new XmlBean();
                        // 房屋地址
                        buildBean.setStrValue("BuildInfo.ttRegId", ttRegId);
                        buildBean.setStrValue("BuildInfo.buildAddr", "${hsSt}-${hsArea}-${hsNo}");
                        buildMap.put(ttRegId, buildBean);
                    }
                    // 处理楼房的楼层
                    int floorNum = buildBean.getIntValue("BuildInfo.floorNum");
                    String roomNo = "";
                    String floorNo = "";

                    int strOnlyLen = hsOnlyNm.length();
                    int strLen = hsNm.length();
                    if (strLen > 2) {
                        floorNo = hsNm.substring(0, strLen - 2);
                        if (strOnlyLen > 0) {
                            println("strOnlyLen----------->" + strOnlyLen);
                            roomNo = hsOnlyNm;
                        } else {
                            roomNo = hsNm.substring(strLen - 2, strLen);
                        }
                    }
                    if (StringUtil.isPositiveNum(floorNo) && floorNum < NumberUtil.getIntFromObj(floorNo)) {
                        floorNum = NumberUtil.getIntFromObj(floorNo);
                    }
                    if (minNum > NumberUtil.getIntFromObj(floorNo)) {
                        minNum = NumberUtil.getIntFromObj(floorNo);
                    }

                    buildBean.setStrValue("BuildInfo.floorNum", floorNum);
                    buildBean.setStrValue("BuildInfo.minNum", minNum);

                    // 获取楼房的单元信息，不存创建存储
                    Map<String, Set<String>> bUnits = units.get(ttRegId);
                    if (bUnits == null) {
                        bUnits = new TreeMap<String, Set<String>>(new Comparator<String>() {
                            public int compare(String obj1, String obj2) {
                                // 升序排序
                                return obj2.compareTo(obj1);
                            }
                        });
                        units.put(ttRegId, bUnits);
                    }

                    // 根据单元名称获取单元的房号信息
                    Set<String> unit = bUnits.get(unitName);
                    if (unit == null) {
                        unit = new TreeSet<String>(new Comparator<String>() {
                            public int compare(String obj1, String obj2) {
                                // 升序排序
                                return obj2.compareTo(obj1);
                            }
                        });
                        bUnits.put(unitName, unit);
                    }
                    if (!unit.contains(roomNo)) {
                        unit.add(roomNo);
                        // 记录当前房间作为整个单元房号的信息
                        roomNoHsInfo.put("${ttRegId}_${unitName}_${roomNo}".toString(), row.getRootNode())
                    }
                    // 保存房源数据
                    houses.put("${ttRegId}_${unitName}_${floorNo}_${roomNo}".toString(), row.getRootNode());
                }
                // 返回单元和房屋列表
                List<XmlNode> builds = new ArrayList<XmlNode>(buildMap.size());
                for (XmlBean temp : buildMap.values()) {
                    builds.add(temp.getRootNode());
                }
                modelMap.put("builds", builds);
                modelMap.put("units", units);
                modelMap.put("roomNoHsInfo", roomNoHsInfo);
                modelMap.put("houses", houses);
            }
        }
        //获取状态颜色
        //调用服务查询 院落 房产 状态对应的颜色信息
        svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean OpData = new XmlBean();
        OpData.setStrValue("OpData.SysCfgs.SysCfg[0].itemCd", "10003");
        OpData.setStrValue("OpData.SysCfgs.SysCfg[0].isCached", "true");
        OpData.setStrValue("OpData.SysCfgs.SysCfg[0].prjCd", request.getParameter("prjCd"));
        svcRequest.addOp("QUERY_SYS_CFG_DATA", OpData);
        svcResponse = query(svcRequest);
        // 处理服务返回结果
        Map newHsColorMap = new HashMap();
        if (svcResponse.isSuccess()) {
            XmlBean colorResult = svcResponse.getFirstOpRsp("QUERY_SYS_CFG_DATA").getBeanByPath("Operation.OpResult.SysCfgs[0].SysCfg.Values");
            if (colorResult != null) {
                int count = colorResult.getListNum("Values.Value");
                List<XmlNode> newHsColorList = new ArrayList<XmlNode>();
                for (int i = 0; i < count; i++) {
                    XmlBean color = colorResult.getBeanByPath("Values.Value[${i}]");
                    String valueCd = color.getStrValue("Value.valueCd");
                    if ("2".equals(valueCd) || "3".equals(valueCd)) {
                        newHsColorMap.put(valueCd, color.getStrValue("Value.valueName"));
                        newHsColorList.add(color.getRootNode());
                    }
                }
                modelMap.put("newHsColorMap", newHsColorMap);
                modelMap.put("newHsColorList", newHsColorList);
            }
        }
        String displayBuildPage = request.getParameter("displayBuildPage");
        if (StringUtil.isEmptyOrNull(displayBuildPage)) {
            displayBuildPage = "/eland/ch/ch001/ch00101_build";
        }
        return new ModelAndView(displayBuildPage, modelMap);
    }

    /**
     * 打开房源导入
     * @param request 请求信息
     * @param response 响应信息
     * @return 打开导入界面
     */
    public ModelAndView openDelay(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/eland/ch/ch001/ch001_delay", modelMap);
    }

    /** 根据名字获取cookie  */
    private String getCookieByName(HttpServletRequest request, String name) {
        // 从cookie获取Session主键
        Cookie[] cookieArr = request.getCookies();
        if (cookieArr != null) {
            for (int i = 0; i < cookieArr.length; i++) {
                Cookie cookie = cookieArr[i];
                if (name.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * 获取区域节点对应房源区域ID(房源区域：第二级节点)
     * @param regId
     * @param hsAreaMap
     * @return
     */
    private XmlBean getHsAreaBean(String regId, Map<String, XmlBean> hsAreaMap, Map<String, XmlBean> fountHsAreaMap) {
        XmlBean result = fountHsAreaMap.get(regId);
        if (result != null) {
            return result;
        }
        // 当前Bean对象
        XmlBean currentBean = hsAreaMap.get(regId);
        if (currentBean != null) {
            // 获取当前节点的上级节点编号
            String pId = currentBean.getStrValue("Node.upRegId");
            // 获取当前节点的上级节点,上级节点不为空
            XmlBean currentPBean = hsAreaMap.get(pId);
            if (currentPBean != null) {
                pId = currentPBean.getStrValue("Node.upRegId");
                // 当前节点的上级节点的PId等于0的时候返回当前节点
                if ("0".equals(pId)) {
                    fountHsAreaMap.put(regId, currentBean);
                    return currentBean;
                } else {
                    return getHsAreaBean(pId, hsAreaMap, fountHsAreaMap);
                }
            } else if (StringUtil.isEqual(pId, "0")) {
                // 当前节点已经是根节点则直接返回
                fountHsAreaMap.put(regId, currentBean);
                return currentBean;
            }
        }
        return null;
    }
}