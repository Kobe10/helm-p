import com.shfb.oframe.core.util.common.DateUtil
import com.shfb.oframe.core.util.common.NumberUtil
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

class ch003 extends GroovyController {

    //选房系统登录验证
    public void loginChooseHs(HttpServletRequest request, HttpServletResponse response) {
        String chooseHsSid = request.getParameter("chooseHsSid");
        String hsId = "";

        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        opData.setStrValue(prePath + "entityName", "HouseInfo");
        opData.setStrValue(prePath + "queryType", "2");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "HouseInfo.HsCtInfo.chooseHsSid");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", chooseHsSid);
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "HouseInfo.hsId");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "HouseInfo.HsCtInfo.ctType");
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);

        SvcResponse svcResponse = query(svcRequest);
        boolean result = svcResponse.isSuccess();
        String errMsg = "";
        String ctType = "";
        if (result) {
            List resultList = svcResponse.getAllOpRsp("QUERY_ENTITY_PAGE_DATA");
            XmlBean resultBean1 = resultList.get(0).getBeanByPath("Operation.OpResult.PageData");
            if (resultBean1 != null) {
                hsId = resultBean1.getStrValue("PageData.Row[0].HouseInfo.hsId");
                ctType = resultBean1.getStrValue("PageData.Row[0].HouseInfo.HsCtInfo.ctType");
            } else {
                errMsg = "选房序号对应的居民信息不存在！";
            }
        } else {
            errMsg = svcResponse.getErrMsg();
        }
        ResponseUtil.printAjax(response, """{success:${result}, errMsg:"${errMsg}", hsId:"${hsId}",ctType:"${
            ctType
        }"} """);
    }

    /**
     * 初始化页面
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String prjCd = request.getParameter("prjCd");
        modelMap.put("prjCd", prjCd);
        return new ModelAndView("/eland/ch/ch003/login", modelMap);
    }

    /**
     * 用户选房信息展示，分发回迁和外迁
     * @param request
     * @param response
     * @return
     */
    public ModelAndView info(HttpServletRequest request, HttpServletResponse response) {
        String ctType = request.getParameter("ctType");
        if (StringUtil.isEqual(ctType, "2")) {
            infoWq(request, response)
        } else {
            infoHq(request, response);
        }
    }

    /**
     * 回迁选房信息展示
     * @param request
     * @param response
     * @return
     */
    public ModelAndView infoHq(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String prjCd = request.getParameter("prjCd");
        modelMap.put("prjCd", prjCd);

        String hsId = request.getParameter("hsId");
        modelMap.put("hsId", hsId);
        // 调用服务，获取居民基本信息和签约状态信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 获取基本信息
        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        opData.setStrValue(prePath + "entityName", "HouseInfo");
        opData.setStrValue(prePath + "queryType", "2");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "HouseInfo.hsId");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", hsId);
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "HouseInfo.hsFullAddr");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "HouseInfo.hsOwnerPersons");
        opData.setStrValue(prePath + "ResultFields.fieldName[2]", "HouseInfo.HsCtInfo.ctType");
        opData.setStrValue(prePath + "ResultFields.fieldName[3]", "HouseInfo.HsCtInfo.hsCtId");
        opData.setStrValue(prePath + "ResultFields.fieldName[4]", "HouseInfo.HsCtInfo.chHsStatus");
        opData.setStrValue(prePath + "ResultFields.fieldName[5]", "HouseInfo.HsCtInfo.printChHsStatus");
        opData.setStrValue(prePath + "ResultFields.fieldName[6]", "HouseInfo.HsCtInfo.chooseHsSid");
        opData.setStrValue(prePath + "ResultFields.fieldName[7]", "HouseInfo.HsCtInfo.ctDate");
        opData.setStrValue(prePath + "ResultFields.fieldName[8]", "HouseInfo.hsCd");
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
        // 调用服务
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            // 房屋信息
            XmlBean pageData = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult.PageData");
            if (pageData != null && pageData.getListNum("PageData.Row") > 0) {
                XmlBean houseInfo = pageData.getBeanByPath("PageData.Row[0].HouseInfo");
                String ctDate = houseInfo.getStrValue("HouseInfo.HsCtInfo.ctDate");
                if (StringUtil.isNotEmptyOrNull(ctDate)) {
                    Date ctDateD = DateUtil.parse(ctDate, "yyyyMMddHHmmssSSS");
                    ctDate = DateUtil.format(ctDateD, "yyyy-MM-dd HH:mm:ss.SSS");
                }
                modelMap.put("ctDate", ctDate);
                modelMap.put("hsInfo", houseInfo.getRootNode());
            }
        }
        /*回迁展示方式*/
        // 未完成选房
        svcRequest = RequestUtil.getSvcRequest(request);
        // 查询选房控制信息
        opData = new XmlBean();
        opData.setStrValue(prePath + "queryType", "2");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "HouseInfo.hsId");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", hsId);
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "HouseInfo.hsFullAddr");
        svcRequest.addOp("QUERY_HS_CHOOSE_REGION", opData);
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
        } else {
            modelMap.put("areaSize", "0-0");
            modelMap.put("houseType", "");
            modelMap.put("houseNum", "0");
            modelMap.put("errorMsg", "选房控制获取失败，请联系管理员!");
        }

        // 获取控制信息
        opData = new XmlBean();
        opData.setStrValue("OpData.SysControlInfo.scType", "2");
        svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.addOp("QUERY_CONTROL_INFO", opData);
        svcResponse = query(svcRequest);

        // 控制信息
        XmlBean spaceTimeXml = svcResponse.getFirstOpRsp("QUERY_CONTROL_INFO");
        if (spaceTimeXml != null) {
            spaceTimeXml = spaceTimeXml.getBeanByPath("Operation.OpResult.SysControlInfo");
        }
        if (spaceTimeXml == null) {
            spaceTimeXml = new XmlBean("<SysControlInfo/>");
        }
        String spaceTime = spaceTimeXml.getStrValue("SysControlInfo.spaceTime");
        modelMap.put("spaceTime", spaceTime);
        return new ModelAndView("/eland/ch/ch003/ch003", modelMap);
    }

    /** 初始化右边界面 */
    public ModelAndView infoWq(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        // 项目编号
        String prjCd = request.getParameter("prjCd");
        modelMap.put("prjCd", prjCd);
        // 获取居室列表
        modelMap.put("HS_ROOM_TYPE", getCfgCollection(request, "HS_ROOM_TYPE", true));
        // 获取房屋编号
        String hsId = request.getParameter("hsId");
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
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "HouseInfo.hsFullAddr");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "HouseInfo.hsOwnerPersons");
        opData.setStrValue(prePath + "ResultFields.fieldName[2]", "HouseInfo.HsCtInfo.ctType");
        opData.setStrValue(prePath + "ResultFields.fieldName[3]", "HouseInfo.HsCtInfo.hsCtId");
        opData.setStrValue(prePath + "ResultFields.fieldName[4]", "HouseInfo.HsCtInfo.chHsStatus");
        opData.setStrValue(prePath + "ResultFields.fieldName[5]", "HouseInfo.HsCtInfo.chooseHsSid");
        opData.setStrValue(prePath + "ResultFields.fieldName[6]", "HouseInfo.HsCtInfo.HsCtChooseInfo.chooseHsAddr");
        opData.setStrValue(prePath + "ResultFields.fieldName[7]", "HouseInfo.HsCtInfo.HsCtChooseInfo.chooseStatus");
        opData.setStrValue(prePath + "ResultFields.fieldName[8]", "HouseInfo.HsCtInfo.HsCtChooseInfo.hsCtChooseId");
        opData.setStrValue(prePath + "ResultFields.fieldName[9]", "HouseInfo.HsCtInfo.HsCtChooseInfo.buyPersonName");
        opData.setStrValue(prePath + "ResultFields.fieldName[10]", "HouseInfo.HsCtInfo.HsCtChooseInfo.buyPersonCerty");
        opData.setStrValue(prePath + "ResultFields.fieldName[11]", "HouseInfo.HsCtInfo.HsCtChooseInfo.buyPersonTel");
        opData.setStrValue(prePath + "ResultFields.fieldName[12]", "HouseInfo.HsCtInfo.HsCtChooseInfo.buyPersonOwnerRel");
        opData.setStrValue(prePath + "ResultFields.fieldName[13]", "HouseInfo.HsCtInfo.HsCtChooseInfo.choosePrintStatus");
        opData.setStrValue(prePath + "ResultFields.fieldName[14]", "HouseInfo.HsCtInfo.HsCtChooseInfo.personNoticeAddr");
        opData.setStrValue(prePath + "ResultFields.fieldName[15]", "HouseInfo.hsCd");
        //配比信息
        opData.setStrValue(prePath + "ResultFields.fieldName[16]", "HouseInfo.HsCtInfo.chooseYjNum");
        opData.setStrValue(prePath + "ResultFields.fieldName[17]", "HouseInfo.HsCtInfo.chooseEjNum");
        opData.setStrValue(prePath + "ResultFields.fieldName[18]", "HouseInfo.HsCtInfo.chooseSjNum");
        opData.setStrValue(prePath + "ResultFields.fieldName[19]", "HouseInfo.HsCtInfo.chooseLjNum");
        opData.setStrValue(prePath + "ResultFields.fieldName[20]", "HouseInfo.HsCtInfo.ctDate");
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
        // 获取控制信息
        opData = new XmlBean();
        opData.setStrValue("OpData.SysControlInfo.scType", "2");
        svcRequest.addOp("QUERY_CONTROL_INFO", opData);

        //查询选房信息
        XmlBean contractReqData = new XmlBean();
        contractReqData.setStrValue("OpData.HsCtInfo.hsId", hsId);
        svcRequest.addOp("QUERY_HS_CT_INFO", contractReqData);
        //整体调用服务查询
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            // 房屋信息
            XmlBean pageData = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult.PageData");
            int num = pageData.getListNum("PageData.Row")
            if (pageData != null && num > 0) {
                XmlBean houseInfo = pageData.getBeanByPath("PageData.Row[0].HouseInfo");
                String ctDate = houseInfo.getStrValue("HouseInfo.HsCtInfo.ctDate");
                if (StringUtil.isNotEmptyOrNull(ctDate)) {
                    Date ctDateD = DateUtil.parse(ctDate, "yyyyMMddHHmmssSSS");
                    ctDate = DateUtil.format(ctDateD, "yyyy-MM-dd HH:mm:ss.SSS");
                }
                modelMap.put("ctDate", ctDate);
                modelMap.put("hsInfo", houseInfo.getRootNode());
                //配比信息
                String[] tempArr = [0, 0, 0, 0];
                String chooseLjNum = pageData.getStrValue("PageData.Row[0].HouseInfo.HsCtInfo.chooseLjNum");
                String chooseYjNum = pageData.getStrValue("PageData.Row[0].HouseInfo.HsCtInfo.chooseYjNum");
                String chooseEjNum = pageData.getStrValue("PageData.Row[0].HouseInfo.HsCtInfo.chooseEjNum");
                String chooseSjNum = pageData.getStrValue("PageData.Row[0].HouseInfo.HsCtInfo.chooseSjNum");
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
                modelMap.put("tempArr", tempArr);
                Map<String, String> hsTps = getCfgCollection(request, "NEW_HS_JUSH", true, NumberUtil.getIntFromObj(request.getParameter("prjCd")));
                modelMap.put("hsTps", hsTps);
            }
            XmlBean conRspData = svcResponse.getFirstOpRsp("QUERY_HS_CT_INFO");
            List<XmlNode> chRegList = new LinkedList<>();
            if (conRspData != null) {
                int chooseRegNum = conRspData.getListNum("Operation.OpResult.HsCtInfo.chooseHs");
                //[选房区域id，选房bean]
                for (int i = 0; i < chooseRegNum; i++) {
                    XmlBean chooseRegBean = conRspData.getBeanByPath("Operation.OpResult.HsCtInfo.chooseHs[${i}]");
                    chRegList.add(chooseRegBean.getRootNode());
                }
            }
            modelMap.put("chRegList", chRegList);
            // 控制信息
            XmlBean spaceTimeXml = svcResponse.getFirstOpRsp("QUERY_CONTROL_INFO");
            if (spaceTimeXml != null) {
                spaceTimeXml = spaceTimeXml.getBeanByPath("Operation.OpResult.SysControlInfo");
            }
            if (spaceTimeXml == null) {
                spaceTimeXml = new XmlBean("<SysControlInfo/>");
            }
            String spaceTime = spaceTimeXml.getIntValue("SysControlInfo.spaceTime");
            String startCd = spaceTimeXml.getStrValue("SysControlInfo.startCd");
            modelMap.put("startCd", startCd);
            if ("1".equals(startCd)) {
                modelMap.put("spaceTime", spaceTime);
            }
        }
        return new ModelAndView("/eland/ch/ch003/ch0031", modelMap);
    }

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public void data(HttpServletRequest request, HttpServletResponse response) {
        // 请求信息
        RequestUtil requestUtil = new RequestUtil(request);
        String itemCd = requestUtil.getStrParam("itemCd");
        String prjCd = requestUtil.getStrParam("prjCd");
        // 处理结果
        Map<String, String> mapResult = getCfgCollection(request, itemCd, true, NumberUtil.getIntFromObj(prjCd));
        List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        for (Map.Entry<String, String> entry : mapResult.entrySet()) {
            Map<String, String> temp = new HashMap<String, String>();
            temp.put("key", entry.key);
            temp.put("value", entry.value);
            result.add(temp);
        }
        ResponseUtil.print(response, JSONArray.fromObject(result).toString());
    }

    public ModelAndView queryBuyPerson(HttpServletRequest request, HttpServletResponse response) {
        RequestUtil requestUtil = new RequestUtil(request);
        String prjCd = requestUtil.getStrParam("prjCd");
        String hsId = requestUtil.getStrParam("hsId");
        ModelMap modelMap = new ModelMap();
        modelMap.put("prjCd", prjCd);
        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        opData.setStrValue(prePath + "entityName", "HouseInfo");
        opData.setStrValue(prePath + "queryType", "2");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "HouseInfo.hsId");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", hsId);
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "HouseInfo.HsCtInfo.HsCtChooseInfo.buyPersonName");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "HouseInfo.HsCtInfo.HsCtChooseInfo.buyPersonCerty");
        opData.setStrValue(prePath + "ResultFields.fieldName[2]", "HouseInfo.HsCtInfo.HsCtChooseInfo.huJush");
        opData.setStrValue(prePath + "ResultFields.fieldName[3]", "HouseInfo.HsCtInfo.HsCtChooseInfo.chooseStatus");
        opData.setStrValue(prePath + "ResultFields.fieldName[4]", "HouseInfo.HsCtInfo.HsCtChooseInfo.buyPersonId");
        opData.setStrValue(prePath + "ResultFields.fieldName[5]", "HouseInfo.HsCtInfo.HsCtChooseInfo.chooseHsRegId");
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean pageData = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult.PageData");
            List<XmlNode> list = new ArrayList<>();
            for (int i = 0; i < pageData.getListNum("PageData.Row"); i++) {
                Map buyPerson = new LinkedHashMap();
                String buyPersonName = pageData.getStrValue("PageData.Row[${i}].HouseInfo.HsCtInfo.HsCtChooseInfo.buyPersonName");
                String buyPersonCerty = pageData.getStrValue("PageData.Row[${i}].HouseInfo.HsCtInfo.HsCtChooseInfo.buyPersonCerty");
                String huJush = pageData.getStrValue("PageData.Row[${i}].HouseInfo.HsCtInfo.HsCtChooseInfo.huJush");
                String chooseStatus_Name = pageData.getStrValue("PageData.Row[${i}].HouseInfo.HsCtInfo.HsCtChooseInfo.chooseStatus_Name");
                String buyPersonId = pageData.getStrValue("PageData.Row[${i}].HouseInfo.HsCtInfo.HsCtChooseInfo.buyPersonId");
                String chooseHsRegId = pageData.getStrValue("PageData.Row[${i}].HouseInfo.HsCtInfo.HsCtChooseInfo.chooseHsRegId");
                buyPerson.put("buyPersonName", buyPersonName);
                buyPerson.put("buyPersonCerty", buyPersonCerty);
                buyPerson.put("huJush", huJush);
                buyPerson.put("chooseStatus_Name", chooseStatus_Name);
                buyPerson.put("buyPersonId", buyPersonId);
                buyPerson.put("chooseHsRegId", chooseHsRegId);
                list.add(buyPerson);
            }
            modelMap.put("personList", list);
            modelMap.put("hsId", hsId);
            return new ModelAndView("/eland/ch/ch003/ch0032", modelMap);
        }
    }
}
