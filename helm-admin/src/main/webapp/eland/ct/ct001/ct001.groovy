import com.shfb.oframe.core.util.common.*
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.session.SessionUtil
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import net.sf.json.JSONObject
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.text.SimpleDateFormat

/**
 * 签约处理控制层
 *
 */
class ct001 extends GroovyController {

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        // 协议方案类型
        String schemeType = request.getParameter("schemeType");
        if (StringUtil.isEmptyOrNull(schemeType)) {
            schemeType = "1";
        }
        // 基础信息
        ModelMap modelMap = new ModelMap();
        modelMap.put("hsId", request.getParameter("hsId"));
        // 获取签约控制,并推送签约控制到界面进行展示
        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        opData.setStrValue(prePath + "entityName", "SysControlInfo");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "scType");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", "1");
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
        // 1:启动， 2：暂停 3： 停用
        opData.setStrValue(prePath + "ResultFields.fieldName", "startCd");
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.addOp("QUERY_ENTITY_CMPT", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            //获取签约控制启动状态
            XmlBean startCdBean = svcResponse.getFirstOpRsp("QUERY_ENTITY_CMPT")
                    .getBeanByPath("Operation.OpResult.PageData");
            if (startCdBean != null) {
                String startCd = startCdBean.getStrValue("PageData.Row[0].startCd");
                modelMap.put("startCd", startCd);
            } else {
                modelMap.put("startCd", "3");
            }
        }
        // 推动预分配置信息
        modelMap.put("schemeType", schemeType);
        // 获取预分分类定义
        JSONObject jsonObject = getSchemeTypeCfg(request, schemeType);
        // 推送输出结果到界面
        modelMap.put("ctStatusField", jsonObject.get("attr_status"));
        modelMap.put("conditionNames", jsonObject.get("conditionNames", ""));
        modelMap.put("conditions", jsonObject.get("conditions", ""));
        modelMap.put("conditionValues", jsonObject.get("conditionValues", ""));

        return new ModelAndView("/eland/ct/ct001/ct001", modelMap);
    }

    /**  签约查询页面 */
    public ModelAndView initSearch(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/eland/ct/ct001/ct00103", modelMap);
    }

    /** 初始化 右侧 主界面 */
    public ModelAndView info(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        // 获取房屋编号
        String hsId = request.getParameter("hsId");
        modelMap.put("hsId", hsId);
        // 获取界面来源
        String schemeType = request.getParameter("schemeType");
        String opFlag = request.getParameter("opFlag");

        // 推送界面展示信息
        modelMap.put("schemeType", schemeType);
        // 获取预分分类定义
        JSONObject jsonObject = getSchemeTypeCfg(request, schemeType);
        // 推送输出结果到界面
        String ctStatusField = jsonObject.get("attr_status");
        String ctDateField = jsonObject.get("attr_date");
        modelMap.put("ctStatusField", ctStatusField);
        modelMap.put("ctDateField", ctDateField);

        // 调用服务，获取居民基本信息和签约状态信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String prePath = "OpData.";
        XmlBean opData = null;
        // 获取基本信息
        opData = new XmlBean();
        opData.setStrValue(prePath + "queryType", "2");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "HouseInfo.hsId");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", hsId);
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
        //
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "HouseInfo.hsFullAddr");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "HouseInfo.hsOwnerPersons");
        opData.setStrValue(prePath + "ResultFields.fieldName[2]", "HouseInfo.HsCtInfo.hsCtId");
        opData.setStrValue(prePath + "ResultFields.fieldName[3]", "HouseInfo.HsCtInfo.ctType"); //安置方式
        opData.setStrValue(prePath + "ResultFields.fieldName[4]", "HouseInfo.HsCtInfo." + ctStatusField); //签约状态
        opData.setStrValue(prePath + "ResultFields.fieldName[5]", "HouseInfo.HsCtInfo." + ctDateField);
        opData.setStrValue(prePath + "ResultFields.fieldName[6]", "HouseInfo.hsCd");
        opData.setStrValue(prePath + "ResultFields.fieldName[7]", "HouseInfo.HsCtInfo.printCtStatus");   // 协议打印状态
        opData.setStrValue(prePath + "ResultFields.fieldName[8]", "HouseInfo.HsCtInfo.printReCtStatus");   //回执单打印状态
        opData.setStrValue(prePath + "ResultFields.fieldName[9]", "HouseInfo.HsCtInfo.printChHsStatus");   //序号单打印状态
        opData.setStrValue(prePath + "ResultFields.fieldName[10]", "HouseInfo.HsCtInfo.ctOrder");   //序号单打印状态
        opData.setStrValue(prePath + "ResultFields.fieldName[11]", "HouseInfo.hsType");   //序号单打印状态
        opData.setStrValue(prePath + "ResultFields.fieldName[12]", "HouseInfo.hsOwnerType");   //序号单打印状态
        opData.setStrValue(prePath + "ResultFields.fieldName[13]", "HouseInfo.HsCtInfo.ctStatusStaff");   //选房顺序号
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
        // 调用服务
        SvcResponse svcResponse = query(svcRequest);
        //获取各种状态  跳转不同页面    0，未生成  1， 已生成
        String ctStatus = "";
        String printCtStatus = "";
        String printReCtStatus = "";
        String printChHsStatus = "";
        if (svcResponse.isSuccess()) {
            XmlBean pageData = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult.PageData");
            if (pageData != null && pageData.getListNum("PageData.Row") > 0) {
                XmlBean houseInfo = pageData.getBeanByPath("PageData.Row[0].HouseInfo");
                ctStatus = pageData.getStrValue("PageData.Row[0].HouseInfo.HsCtInfo." + ctStatusField);
                printCtStatus = pageData.getStrValue("PageData.Row[0].HouseInfo.HsCtInfo.printCtStatus");
                printReCtStatus = pageData.getStrValue("PageData.Row[0].HouseInfo.HsCtInfo.printReCtStatus");
                printChHsStatus = pageData.getStrValue("PageData.Row[0].HouseInfo.HsCtInfo.printChHsStatus");
                String ctDate = pageData.getStrValue("PageData.Row[0].HouseInfo.HsCtInfo." + ctDateField);
                if (StringUtil.isNotEmptyOrNull(ctDate)) {
                    Date ctDateD = DateUtil.parse(ctDate, "yyyyMMddHHmmssSSS");
                    ctDate = DateUtil.format(ctDateD, "yyyy-MM-dd HH:mm:ss.SSS");
                }
                modelMap.put("ctDate", ctDate);
                modelMap.put("hsInfo", houseInfo.getRootNode());
                // 查询同一批次签约的协议信息
                String ctOrder = pageData.getStrValue("PageData.Row[0].HouseInfo.HsCtInfo.ctOrder");
                List<XmlNode> resultList = new ArrayList();
                if (StringUtil.isNotEmptyOrNull(ctOrder) && !StringUtil.isEqual("0", ctOrder)) {
                    opData = new XmlBean();
                    svcRequest = RequestUtil.getSvcRequest(request);
                    opData.setStrValue(prePath + "queryType", "2");
                    opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "HouseInfo.HsCtInfo.ctOrder");
                    opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", ctOrder);
                    opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
                    opData.setStrValue(prePath + "Conditions.Condition[1].fieldName", "HouseInfo.HsCtInfo.ctStatus");
                    opData.setStrValue(prePath + "Conditions.Condition[1].fieldValue", "2");
                    opData.setStrValue(prePath + "Conditions.Condition[1].operation", "=");

                    opData.setStrValue(prePath + "ResultFields.fieldName[0]", "HouseInfo.hsFullAddr");
                    opData.setStrValue(prePath + "ResultFields.fieldName[1]", "HouseInfo.hsOwnerPersons");
                    opData.setStrValue(prePath + "ResultFields.fieldName[3]", "HouseInfo.HsCtInfo.ctType");
                    opData.setStrValue(prePath + "ResultFields.fieldName[4]", "HouseInfo.HsCtInfo." + ctDateField);
                    opData.setStrValue(prePath + "ResultFields.fieldName[5]", "HouseInfo.hsId");
                    opData.setStrValue(prePath + "ResultFields.fieldName[6]", "HouseInfo.hsCd");
                    svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
                    svcResponse = query(svcRequest);
                    if (svcResponse.isSuccess()) {
                        pageData = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult.PageData");
                        if (pageData != null && pageData.getListNum("PageData.Row") > 0) {
                            for (int i = 0; i < pageData.getListNum("PageData.Row"); i++) {
                                XmlBean list = pageData.getBeanByPath("PageData.Row[" + i + "]");
                                ctDate = pageData.getStrValue("PageData.Row[" + i + "].HouseInfo.HsCtInfo." + ctDateField);
                                if (StringUtil.isNotEmptyOrNull(ctDate)) {
                                    Date ctDateD = DateUtil.parse(ctDate, "yyyyMMddHHmmssSSS");
                                    ctDate = DateUtil.format(ctDateD, "yyyy-MM-dd HH:mm:ss.SSS");
                                    pageData.setStrValue("PageData.Row[" + i + "].HouseInfo.HsCtInfo.ctDate", ctDate);
                                }
                                resultList.add(list.getRootNode());
                            }
                        }
                    }
                }
                modelMap.put("resultList", resultList);
            }
        }
        // 根据签约状态过滤跳转页面   00101--> 待签约页面  00102-->
        String forwardPage = "/eland/ct/ct001/ct00107";
        if ("viewDoc".equals(opFlag)) {
            // 查看协议
            forwardPage = "/eland/ct/ct001/ct00107";
        } else if ("viewFj".equals(opFlag)) {
            // 查看附件
            forwardPage = "forward:/eland/ct/ct001/ct001-initFj.gv";
        } else if ("viewCfm".equals(opFlag)) {
            // 回执单
            forwardPage = "/eland/ct/ct001/ct00108";
        } else if ("viewOneCfm".equals(opFlag)) {
            // 回执单
            forwardPage = "/eland/ct/ct001/ct00102";
        } else if (StringUtil.isEqual("2", ctStatus)) {
            //已签约： 接下来 判断协议打印状态
            if (StringUtil.isEqual("1", printCtStatus)) {
                // 协议已打印： 接下来 判断打印签约回执单状态。
                if (StringUtil.isEqual("1", printReCtStatus)) {
                    // 回执单已打印：去上传附件页面
                    forwardPage = "forward:/eland/ct/ct001/ct001-initFj.gv";
                } else {
                    // 回执单未打印 留在 回执单 页面
                    forwardPage = "/eland/ct/ct001/ct00108";
                }
            } else {
                // 协议未打印: 继续留在签约页面
                forwardPage = "/eland/ct/ct001/ct00107";
            }
        }
        return new ModelAndView(forwardPage, modelMap);
    }

    /**
     * 初始化显示签约附件
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public ModelAndView initFj(HttpServletRequest request, HttpServletResponse response) {
        //基本信息  产权信息 单独查询
        String hsId = request.getParameter("hsId");
        String schemeType = request.getParameter("schemeType");

        ModelMap modelMap = new ModelMap();
        // 获取预分分类定义
        JSONObject jsonObject = getSchemeTypeCfg(request, schemeType);
        // 获取附件名称
        String signDocName = jsonObject.get("sign_doc_name");
        String photoDocName = jsonObject.get("photo_doc_name");
        // 推动到界面视图
        modelMap.put("signDocName", signDocName);
        modelMap.put("photoDocName", photoDocName);

        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 获取可用房源区域列表
        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        opData.setStrValue(prePath + "entityName", "SvcDocInfo");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "relId");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", hsId);
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
        // 附件类型
        opData.setStrValue(prePath + "Conditions.Condition[1].fieldName", "docTypeName");
        opData.setStrValue(prePath + "Conditions.Condition[1].fieldValue", "('${signDocName}',${photoDocName})");
        opData.setStrValue(prePath + "Conditions.Condition[1].operation", "in");
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
            XmlBean queryResult = svcRsp.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult");
            // 处理查询结果，返回json格式数据
            if (queryResult != null) {
                int rowCount = queryResult.getListNum("OpResult.PageData.Row");
                List<XmlNode> hsDocs = new ArrayList<XmlNode>(rowCount);
                String photoDocIds = "";
                String signDocIds = "";
                for (int i = 0; i < rowCount; i++) {
                    hsDocs.add(queryResult.getBeanByPath("OpResult.PageData.Row[${i}]").getRootNode());
                    String docType = queryResult.getStrValue("OpResult.PageData.Row[${i}].docTypeName");
                    if (StringUtil.isEqual(docType, signDocName)) {
                        signDocIds += queryResult.getStrValue("OpResult.PageData.Row[${i}].docId") + ",";
                    }
                    if (StringUtil.isEqual(docType, photoDocName)) {
                        photoDocIds += queryResult.getStrValue("OpResult.PageData.Row[${i}].docId") + ",";
                    }
                }
                modelMap.put("hsDocs", hsDocs);
                if (photoDocIds.length() > 0) {
                    modelMap.put("photoDocIds", photoDocIds.substring(0, photoDocIds.length() - 1));
                }
                if (signDocIds.length() > 0) {
                    modelMap.put("signDocIds", signDocIds.substring(0, signDocIds.length() - 1));
                }
            }
        }
        return new ModelAndView("/eland/ct/ct001/ct00104", modelMap);
    }

    /**
     * 新增签约人
     * @param request 请求信息
     * @param response 响应信息
     * @return 返回结果
     */
    public ModelAndView addCtPerson(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String hsCtId = request.getParameter("hsCtId");
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.HsCtRelationInfo.hsCtId", hsCtId);
        //传入想要的数据，如果想要翻译的名称，在后面同时传入加上 “_Name”的数据
        opData.setStrValue("OpData.HsCtRelationInfo.fields.HsCtInfo", "hsCtId,ctPsNames,ctType,printCtStatus");
        opData.setStrValue("OpData.HsCtRelationInfo.fields.HouseInfo", "hsFullAddr,hsId,hsType,hsType_Name,hsOwnerType,hsOwnerType_Name,hsCd");
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.addOp("QUERY_HSCTRE_INFO_BY_HSCTID_CMPT", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean resultBean = svcResponse.getFirstOpRsp("QUERY_HSCTRE_INFO_BY_HSCTID_CMPT");
            if (resultBean != "") {
                List<XmlNode> resultList = new ArrayList();
                int rowCount = resultBean.getListNum("Operation.OpResult.PageData.Row");
                for (int i = 0; i < rowCount; i++) {
                    XmlBean list = resultBean.getBeanByPath("Operation.OpResult.PageData.Row[" + i + "]");
                    resultList.add(list.getRootNode());
                }
                modelMap.put("resultList", resultList);
            }
        }
        return new ModelAndView("/eland/ct/ct001/ct00105", modelMap);
    }

    /** 确认签约按钮控制 */
    public void signControl(HttpServletRequest request, HttpServletResponse response) {
        XmlBean opData = new XmlBean();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.addOp("QUERY_TIME_INTERVAL", opData);
        SvcResponse svcResponse = query(svcRequest);
        String mark = "";
        String info = "";
        // 使用交易流水号作为签约Token，确认签约时候进行验证，一个token只允许提交一次
        String tokenId = svcRequest.getReqId();
        boolean flag = false;
        if (svcResponse.isSuccess()) {
            XmlBean resultBean = svcResponse.getFirstOpRsp("QUERY_TIME_INTERVAL").getBeanByPath("Operation.OpResult");
            mark = resultBean.getStrValue("OpResult.mark");
            info = resultBean.getStrValue("OpResult.info");
            long sysDate = DateUtil.getSysDate().getTime();  //获取服务器时间
            if (StringUtil.isEqual(mark, "1")) {
                //本轮开始
                long ctTimeSt = NumberUtil.getLongFromObj(resultBean.getStrValue("OpResult.CtTimeSlice.ctTimeSt"));
                //本轮结束
                long ctTimeEnd = NumberUtil.getLongFromObj(resultBean.getStrValue("OpResult.CtTimeSlice.ctTimeEnd"));
                //阅读时长
                long readTime = NumberUtil.getLongFromObj(resultBean.getStrValue("OpResult.CtTimeSlice.readTime"));
                //本轮间隔
                long spaceTime = NumberUtil.getLongFromObj(resultBean.getStrValue("OpResult.CtTimeSlice.spaceTime"));
                //暂停时刻 距 正常结束时长
                long ctTimeSpace = NumberUtil.getLongFromObj(resultBean.getStrValue("OpResult.CtTimeSlice.ctTimeSpace"));
                /**
                 * 判断本轮签约 有无暂停
                 */
                if (ctTimeSpace == 0) {
                    //无暂停， 倒计时差值计算
                    long temp = ctTimeSt + readTime;
                    flag = temp < sysDate
                } else {
                    //暂停时长
                    long stopLong = NumberUtil.getLongFromObj(resultBean.getStrValue("OpResult.CtTimeSlice.stopLong"));
                    /**
                     * 判断当前时刻处于 阅读阶段 还是 可签约阶段
                     * 本轮间隔 - 暂停时刻距本轮正常签约结束时长  与 阅读时长 作比较
                     * 大于0 即为： 可签约阶段
                     * 小于0 即为： 阅读阶段
                     */
                    long x = spaceTime - ctTimeSpace - readTime;
                    if (x < 0) {
                        long realRead = ctTimeSt + readTime + stopLong;
                        flag = realRead <= sysDate
                    } else {
                        flag = true;
                    }
                }
            } else if (StringUtil.isEqual(mark, "3")) {
                flag = true;
            }
        }
        // 允许签约保存签约令牌
        if (flag) {
            SessionUtil sessionUtil = new SessionUtil(request);
            sessionUtil.setAttr("cfmCtToken", tokenId);
        }
        ResponseUtil.printSvcResponse(response, svcResponse,
                """mark:'${mark}',info:'${info}',flag:${flag},tokenId:'${tokenId}'""");
    }

    /** 重新 打开 协议内容 提供重新打印入口 */
    public ModelAndView rePrintDoc(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        // 获取房屋编号
        String hsId = request.getParameter("hsId");
        String prjCd = request.getParameter("prjCd");
        String reDirPage = request.getParameter("reDirPage");  // 重新跳转 页面
        modelMap.put("hsId", hsId);
        // 调用服务，获取居民基本信息和签约状态信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String prePath = "OpData.";
        XmlBean opData = null;
        // 获取基本信息
        opData = new XmlBean();
        opData.setStrValue(prePath + "queryType", "2");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "HouseInfo.hsId");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", hsId);
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
        //
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "HouseInfo.hsFullAddr");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "HouseInfo.hsOwnerPersons");
        opData.setStrValue(prePath + "ResultFields.fieldName[2]", "HouseInfo.HsCtInfo.hsCtId");
        opData.setStrValue(prePath + "ResultFields.fieldName[3]", "HouseInfo.HsCtInfo.ctType");//安置方式
        opData.setStrValue(prePath + "ResultFields.fieldName[4]", "HouseInfo.HsCtInfo.ctStatus");  //签约状态
        opData.setStrValue(prePath + "ResultFields.fieldName[5]", "HouseInfo.HsCtInfo.ctDate");
        opData.setStrValue(prePath + "ResultFields.fieldName[6]", "HouseInfo.hsCd");
        opData.setStrValue(prePath + "ResultFields.fieldName[7]", "HouseInfo.HsCtInfo.printCtStatus");   // 协议打印状态
        opData.setStrValue(prePath + "ResultFields.fieldName[8]", "HouseInfo.HsCtInfo.printReCtStatus");   //回执单打印状态
        opData.setStrValue(prePath + "ResultFields.fieldName[9]", "HouseInfo.HsCtInfo.printChHsStatus");   //序号单打印状态
        opData.setStrValue(prePath + "ResultFields.fieldName[10]", "HouseInfo.HsCtInfo.ctOrder");   //序号单打印状态
        opData.setStrValue(prePath + "ResultFields.fieldName[11]", "HouseInfo.hsType"); // 房屋类型
        opData.setStrValue(prePath + "ResultFields.fieldName[12]", "HouseInfo.hsOwnerType");//产权性质
        opData.setStrValue(prePath + "ResultFields.fieldName[13]", "HouseInfo.HsCtInfo.ctStatusStaff");   //选房顺序号

        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);

        opData = new XmlBean();
        opData.setStrValue(prePath + "entityName", "SysControlInfo");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "scType");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", "1");
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
        opData.setStrValue(prePath + "ResultFields.fieldName", "startCd");   // 1:启动， 2：暂停 3： 停用
        svcRequest.addOp("QUERY_ENTITY_CMPT", opData);
        // 调用服务
        SvcResponse svcResponse = query(svcRequest);
        //获取各种状态  跳转不同页面    0，未生成  1， 已生成
        if (svcResponse.isSuccess()) {
            XmlBean pageData = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult.PageData");
            if (pageData != null && pageData.getListNum("PageData.Row") > 0) {
                XmlBean houseInfo = pageData.getBeanByPath("PageData.Row[0].HouseInfo");
                String ctDate = pageData.getStrValue("PageData.Row[0].HouseInfo.HsCtInfo.ctDate");
                if (StringUtil.isNotEmptyOrNull(ctDate)) {
                    Date ctDateD = DateUtil.parse(ctDate, "yyyyMMddHHmmssSSS");
                    ctDate = DateUtil.format(ctDateD, "yyyy-MM-dd HH:mm:ss.SSS");
                }
                modelMap.put("ctDate", ctDate);
                modelMap.put("hsInfo", houseInfo.getRootNode());
            }

            //获取签约控制启动状态
            if (svcResponse.getFirstOpRsp("QUERY_ENTITY_CMPT") != null) {
                XmlBean startCdBean = svcResponse.getFirstOpRsp("QUERY_ENTITY_CMPT").getBeanByPath("Operation.OpResult.PageData");
                String startCd = startCdBean.getStrValue("PageData.Row[0].startCd");
                modelMap.put("startCd", startCd);
            }
            //                查询同一批次签约的协议信息
            String ctOrder = pageData.getStrValue("PageData.Row[0].HouseInfo.HsCtInfo.ctOrder");
            opData = new XmlBean();
            svcRequest = RequestUtil.getSvcRequest(request);
            opData.setStrValue(prePath + "queryType", "2");
            opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "HouseInfo.HsCtInfo.ctOrder");
            opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", ctOrder);
            opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
            opData.setStrValue(prePath + "Conditions.Condition[1].fieldName", "HouseInfo.HsCtInfo.ctStatus");
            opData.setStrValue(prePath + "Conditions.Condition[1].fieldValue", "2");
            opData.setStrValue(prePath + "Conditions.Condition[1].operation", "=");

            opData.setStrValue(prePath + "ResultFields.fieldName[0]", "HouseInfo.hsFullAddr");
            opData.setStrValue(prePath + "ResultFields.fieldName[1]", "HouseInfo.hsOwnerPersons");
            opData.setStrValue(prePath + "ResultFields.fieldName[3]", "HouseInfo.HsCtInfo.ctType");
            opData.setStrValue(prePath + "ResultFields.fieldName[4]", "HouseInfo.HsCtInfo.ctDate");
            opData.setStrValue(prePath + "ResultFields.fieldName[5]", "HouseInfo.hsId");
            opData.setStrValue(prePath + "ResultFields.fieldName[6]", "HouseInfo.hsCd");
            svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
            svcResponse = query(svcRequest);
            if (svcResponse.isSuccess()) {
                pageData = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult.PageData");
                if (pageData != null && pageData.getListNum("PageData.Row") > 0) {
                    List<XmlNode> resultList = new ArrayList();
                    for (int i = 0; i < pageData.getListNum("PageData.Row"); i++) {
                        XmlBean list = pageData.getBeanByPath("PageData.Row[" + i + "]");
                        String ctDate = pageData.getStrValue("PageData.Row[" + i + "].HouseInfo.HsCtInfo.ctDate");
                        if (StringUtil.isNotEmptyOrNull(ctDate)) {
                            Date ctDateD = DateUtil.parse(ctDate, "yyyyMMddHHmmssSSS");
                            ctDate = DateUtil.format(ctDateD, "yyyy-MM-dd HH:mm:ss.SSS");
                            pageData.setStrValue("PageData.Row[" + i + "].HouseInfo.HsCtInfo.ctDate", ctDate);
                        }
                        resultList.add(list.getRootNode());
                    }
                    modelMap.put("resultList", resultList);
                }
            }
        }
//        根据签约状态过滤跳转页面   00101--> 待签约页面  00102-->
        String forwardPage = "/eland/ct/ct001/ct00107";
        if (StringUtil.isEqual("ct00102", reDirPage)) {
            forwardPage = "/eland/ct/ct001/ct00108";
        }
        return new ModelAndView(forwardPage, modelMap);
    }

    /**
     * 生成安置协议
     * @param request 请求信息
     * @param response 响应结果
     * @return
     */
    public ModelAndView genMainCtDoc(HttpServletRequest request, HttpServletResponse response) {
        RequestUtil requestUtil = new RequestUtil(request);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 输入参数
        String hsId = request.getParameter("hsId");
        String hsCtId = request.getParameter("hsCtId");
        String ctType = request.getParameter("ctType");
        String agent = request.getHeader("USER-AGENT");
        String fromOp = request.getParameter("fromOp");
        String schemeType = request.getParameter("schemeType");
        if (StringUtil.isEmptyOrNull(schemeType)) {
            schemeType = "1";
        }
        // 生成签约协议文档
        XmlBean paramData = new XmlBean();
        paramData.setStrValue("OpData.HsCtInfo.hsId", hsId);
        paramData.setStrValue("OpData.HsCtInfo.hsCtId", hsCtId);
        paramData.setStrValue("OpData.HsCtInfo.ctType", ctType);
        paramData.setStrValue("OpData.HsCtInfo.schemeType", schemeType);
        svcRequest.addOp("CREATE_AGREEMENT", paramData);
        // 调用服务，执行数据查询
        SvcResponse svcResponse = transaction(svcRequest);
        // 调整安置方式不生成
        if ("change".equals(fromOp)) {
            ResponseUtil.printSvcResponse(response, svcResponse, "");
            return;
        }
        // 生成协议文档
        if (svcResponse.isSuccess()) {
            XmlBean opResult = svcResponse.getFirstOpRsp("CREATE_AGREEMENT");
            if (opResult != null) {
                String docName = "${hsId}_hsContract.docx";
                String docPrePath = "/reports/report";
                String docPath = opResult.getValue("Operation.OpResult.resultParam");
                String contextType = requestUtil.getStrParam("contextType");
                File localFile = new File(StringUtil.formatFilePath("webroot:" + docPrePath + docPath));
                if (StringUtil.isEqual(fromOp, "download")) {
                    // 下载内容进行缓存
                    //20秒之内重新进入该页面的话不会进入该servlet的
                    Date modifiedTime = DateUtil.toDateYmdHms("20150101000000");
                    response.setDateHeader("Last-Modified", modifiedTime.getTime());
                    response.setDateHeader("Expires", modifiedTime.getTime() + 6553600);
                    response.setHeader("Cache-Control", "public");
                    response.setHeader("Pragma", "Pragma");
                    // 文件下载输出到响应流
                    ResponseUtil.downloadFile(response, contextType, docName, localFile, agent, false);
                } else if (StringUtil.isEqual(fromOp, "print")) {
                    //本地查看
                    ModelMap modelMap = new ModelMap();
                    modelMap.put("docFileUrl", docPrePath + docPath);
                    return new ModelAndView("/eland/ct/ct001/ct001_doc_print_iframe", modelMap)
                } else {
                    //本地查看
                    ModelMap modelMap = new ModelMap();
                    modelMap.put("docFileUrl", docPrePath + docPath);
                    return new ModelAndView("/eland/ct/ct001/ct001_doc_iframe", modelMap)
                }
            }
        } else {
            if (StringUtil.isEqual(fromOp, "download")) {
                // 下载错误不响应
                String downloadFileName = "生成文档失败.txt";
                File tempFile = ServerFile.createFile("temp/", UUID.randomUUID().toString() + downloadFileName);
                // 文件下载输出到响应流
                ResponseUtil.downloadFile(response, "", downloadFileName, tempFile, request.getHeader("USER-AGENT"));
            } else {
                //本地查看
                ModelMap modelMap = new ModelMap();
                modelMap.put("errMsg", "生成文档失败，请联系管理员!");
                return new ModelAndView("/eland/ct/ct001/ct001_doc_error", modelMap)
            }
        }
    }

    /** 查看协议文档 */
    public ModelAndView viewDoc(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("docFileUrl", request.getParameter("docFileUrl"));
        return new ModelAndView("/eland/ct/ct001/ct001_doc_view", modelMap)
    }

    /**
     * 查看阅读须知
     * @param request
     * @param response
     * @return
     */
    public ModelAndView viewCtNotice(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("noticeId", request.getParameter("noticeId"));
        modelMap.put("prjCd", request.getParameter("prjCd"));
        return new ModelAndView("/eland/ct/ct001/ct001_ct_notice", modelMap);
    }

    /**
     * 显示阅读公告
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public ModelAndView viewCfmNotice(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        modelMap.put("prjCd", request.getParameter("prjCd"));
        XmlBean reqData = new XmlBean();
        String nodePath = "SvcCont.PrjNotice.";
        reqData.setValue(nodePath + "noticeId", request.getParameter("noticeId"));
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = callService("prjNoticeService", "queryPrjNotice", svcRequest);
        //查询公告成功
        if (svcResponse.isSuccess()) {
            XmlBean rspData = svcResponse.getRspData();
            // 获取查询结果
            XmlBean prjNoticeBean = rspData.getBeanByPath("SvcCont.PrjNotice");
            modelMap.put("prjNotice", prjNoticeBean.getRootNode());
        }
        return new ModelAndView("/eland/ct/ct001/ct001_ct_noitce_view", modelMap);
    }

    /**
     * 取消签约操作
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public ModelAndView cancelContract(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsCtId = request.getParameter("hsCtId");
        String schemeType = request.getParameter("schemeType");
        if (StringUtil.isEmptyOrNull(schemeType)) {
            schemeType = "1";
        }
        XmlBean opData = new XmlBean();
        // 腾退签约动作组件
        String nodePath = "OpData.";
        opData.setStrValue(nodePath + "HsCtInfo.hsCtId", hsCtId);
        opData.setStrValue(nodePath + "HsCtInfo.schemeType", schemeType);
        svcRequest.addOp("CHANGE_TO_UN_SIGNED", opData);
        // 输出处理结果
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /**
     * 打开安置方式修改界面
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public ModelAndView openChangeCtType(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("hsId", request.getParameter("hsId"));
        modelMap.put("hsCtId", request.getParameter("hsCtId"));

        // 调用服务，获取居民基本信息和签约状态信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String prePath = "OpData.";
        // 获取基本信息
        XmlBean opData = new XmlBean();
        opData.setStrValue(prePath + "queryType", "2");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "HouseInfo.hsId");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", request.getParameter("hsId"));
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
        // 查询签约信息
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "HouseInfo.HsCtInfo.hsCtId");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "HouseInfo.HsCtInfo.ctType");
        opData.setStrValue(prePath + "ResultFields.fieldName[2]", "HouseInfo.HsCtInfo.ctStatus");
        opData.setStrValue(prePath + "ResultFields.fieldName[3]", "HouseInfo.HsCtInfo.ctDate");
        // 查询签约信息
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
        // 调用服务
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean pageData = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult.PageData");
            if (pageData != null && pageData.getListNum("PageData.Row") > 0) {
                String ctDate = pageData.getStrValue("PageData.Row[0].HouseInfo.HsCtInfo.ctDate");
                String ctType = pageData.getStrValue("PageData.Row[0].HouseInfo.HsCtInfo.ctType");
                modelMap.put("ctDate", ctDate);
                modelMap.put("ctType", ctType);
            }
        }
        return new ModelAndView("/eland/ct/ct001/ct001_change_ct_type", modelMap);
    }

    /**
     * 更换安置方式 操作
     * @param request
     * @param response
     * @return
     */
    public ModelAndView changeCtType(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsCtId = request.getParameter("hsCtId");
        String ctType = request.getParameter("ctType");
        String changeCtOrder = request.getParameter("changeCtOrder");
        String hsId = request.getParameter("hsId");
        XmlBean opData = new XmlBean();
        // 腾退签约动作组件
        String nodePath = "OpData.";
        opData.setStrValue(nodePath + "hsId", hsId);
        opData.setStrValue(nodePath + "hsCtId", hsCtId);
        opData.setStrValue(nodePath + "ctType", ctType);
        opData.setStrValue(nodePath + "changeCtOrder", changeCtOrder);
        opData.setStrValue(nodePath + "schemeType", "1");
        svcRequest.addOp("SWITCH_CONTRACT_TYPE", opData);
        // 输出处理结果
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /** 打印签约文本 修改是否打印协议状态 */
    public ModelAndView printCtText(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String printObj = request.getParameter("printObj");
        String hsCtId = request.getParameter("hsCtId");
        XmlBean opData = new XmlBean();
        // 腾退签约动作组件
        String nodePath = "OpData.";
        opData.setStrValue(nodePath + "entityName", "HsCtInfo");
        opData.setStrValue(nodePath + "EntityData.hsCtId", hsCtId);
        String printObjVal = "";
        if (StringUtil.isEqual("printCt", printObj)) {
            printObjVal = "printCtStatus";
        } else if (StringUtil.isEqual("printReCt", printObj)) {
            printObjVal = "printReCtStatus";
        } else if (StringUtil.isEqual("printChHs", printObj)) {
            printObjVal = "printChHsStatus";
        }
        opData.setStrValue(nodePath + "EntityData.${printObjVal}", "1");
        svcRequest.addOp("SAVE_ENTITY", opData);
        // 输出处理结果
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /** 查看选房确认单页面  */
    public ModelAndView infoChHsNum(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        // 获取房屋编号
        String hsId = request.getParameter("hsId");
        modelMap.put("hsId", hsId);
        // 调用服务，获取居民基本信息和签约状态信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String prePath = "OpData.";
        XmlBean opData = null;
        // 获取基本信息
        opData = new XmlBean();
        opData.setStrValue(prePath + "queryType", "2");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "HouseInfo.hsId");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", hsId);
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
        //
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "HouseInfo.hsFullAddr");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "HouseInfo.hsOwnerPersons");
        opData.setStrValue(prePath + "ResultFields.fieldName[2]", "HouseInfo.HsCtInfo.hsCtId");
        opData.setStrValue(prePath + "ResultFields.fieldName[3]", "HouseInfo.HsCtInfo.ctType");
        opData.setStrValue(prePath + "ResultFields.fieldName[4]", "HouseInfo.HsCtInfo.ctStatus");
        opData.setStrValue(prePath + "ResultFields.fieldName[5]", "HouseInfo.HsCtInfo.ctDate");
        opData.setStrValue(prePath + "ResultFields.fieldName[6]", "HouseInfo.hsCd");
        opData.setStrValue(prePath + "ResultFields.fieldName[7]", "HouseInfo.HsCtInfo.printCtStatus");   // 协议打印状态
        opData.setStrValue(prePath + "ResultFields.fieldName[8]", "HouseInfo.HsCtInfo.printReCtStatus");   //回执单打印状态
        opData.setStrValue(prePath + "ResultFields.fieldName[9]", "HouseInfo.HsCtInfo.printChHsStatus");   //序号单打印状态
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
        // 调用服务
        SvcResponse svcResponse = query(svcRequest);
        String ctStatus = "";
        if (svcResponse.isSuccess()) {
            XmlBean pageData = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult.PageData");
            if (pageData != null && pageData.getListNum("PageData.Row") > 0) {
                XmlBean houseInfo = pageData.getBeanByPath("PageData.Row[0].HouseInfo");
                ctStatus = pageData.getStrValue("PageData.Row[0].HouseInfo.HsCtInfo.ctStatus");
                String ctDate = pageData.getStrValue("PageData.Row[0].HouseInfo.HsCtInfo.ctDate");
                if (StringUtil.isNotEmptyOrNull(ctDate)) {
                    Date ctDateD = DateUtil.parse(ctDate, "yyyyMMddHHmmssSSS");
                    ctDate = DateUtil.format(ctDateD, "yyyy-MM-dd HH:mm:ss.SSS");
                }
                modelMap.put("ctDate", ctDate);
                modelMap.put("hsInfo", houseInfo.getRootNode());
            }
        }
        return new ModelAndView("/eland/ct/ct001/ct00106", modelMap);
    }

    /**
     * 生成签约回执单,根据配置调用表单组件生成选房序号单
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public ModelAndView generateCtOrder(HttpServletRequest request, HttpServletResponse response) {
        RequestUtil requestUtil = new RequestUtil(request);
        // 输入参数
        String hsId = request.getParameter("hsId");
        // 调用服务，获取居民基本信息和签约状态信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
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
        paramData.setStrValue("OpData.Report.Parameter[1].Property[0].value", "ctCfm");

        // 增加到请求参数,调用服务
        svcRequest.addOp("GENERATE_REPORT", paramData);
        // 调用服务，执行数据查询
        SvcResponse svcResponse = transaction(svcRequest);
        String errMsg = "";
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
                        docName = "签约回执单(" + hsId + ")";
                    } else {
                        docName = java.net.URLDecoder.decode(docName, "utf-8")
                    }
                    docName = docName + "." + docExt;
                    // 获取下载的本地文件
                    String contextType = requestUtil.getStrParam("contextType");
                    File localFile = new File(StringUtil.formatFilePath("webroot:" + docPrePath + docPath));
                    // 文件下载输出到响应流
                    ResponseUtil.downloadFile(response, contextType, docName, localFile,
                            request.getHeader("USER-AGENT"), false, true);
                    return;
                } else {
                    //本地查看
                    ModelMap modelMap = new ModelMap();
                    modelMap.put("docFileUrl", docPrePath + docPath);
                    return new ModelAndView("/eland/ct/ct001/ct001_doc_iframe", modelMap);
                }
            } else {
                errMsg = errMsg + "只有文档类型的模板允许在该模块中生成,请调整表单配置!"
            }
        } else {
            errMsg = "生成文档失败,请联系系统管理员";
        }
        String fromOp = request.getParameter("fromOp");
        if (StringUtil.isEqual(fromOp, "download")) {
            // 下载错误不响应
            String downloadFileName = "生成文档失败.txt";
            File tempFile = ServerFile.createFile("temp/", UUID.randomUUID().toString() + downloadFileName);
            // 文件下载输出到响应流
            ResponseUtil.downloadFile(response, "", downloadFileName, tempFile, request.getHeader("USER-AGENT"));
        } else {
            //本地查看
            ModelMap modelMap = new ModelMap();
            modelMap.put("errMsg", errMsg);
            return new ModelAndView("/eland/ct/ct001/ct001_doc_error", modelMap)
        }
    }

    /**
     * 生成选房序号单
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public ModelAndView generateCfmChHsNum(HttpServletRequest request, HttpServletResponse response) {
        // 输入参数
        String hsId = request.getParameter("hsId");
        // 调用服务，获取居民基本信息和签约状态信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
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
        paramData.setStrValue("OpData.Report.Parameter[1].Property[0].value", "chooseOrder");
        // 增加到请求参数,调用服务
        svcRequest.addOp("GENERATE_REPORT", paramData);
        // 调用服务，执行数据查询
        SvcResponse svcResponse = transaction(svcRequest);
        String errMsg = "";
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
                        docName = "选房序号单(" + hsId + ")";
                    } else {
                        docName = java.net.URLDecoder.decode(docName, "utf-8")
                    }
                    docName = docName + "." + docExt;
                    // 获取下载的本地文件
                    String contextType = request.getParameter("contextType");
                    File localFile = new File(StringUtil.formatFilePath("webroot:" + docPrePath + docPath));
                    // 文件下载输出到响应流
                    ResponseUtil.downloadFile(response, contextType, docName, localFile,
                            request.getHeader("USER-AGENT"), false, true);
                    return;
                } else {
                    //本地查看
                    ModelMap modelMap = new ModelMap();
                    modelMap.put("docFileUrl", docPrePath + docPath);
                    return new ModelAndView("/eland/ct/ct001/ct001_doc_iframe", modelMap);
                }
            } else {
                errMsg = errMsg + "只有文档类型的模板允许在该模块中生成,请调整表单配置!"
            }
        } else {
            errMsg = "生成文档失败,请联系系统管理员";
        }
        String fromOp = request.getParameter("fromOp");
        if (StringUtil.isEqual(fromOp, "download")) {
            // 下载错误不响应
            String downloadFileName = "生成文档失败.txt";
            File tempFile = ServerFile.createFile("temp/", UUID.randomUUID().toString() + downloadFileName);
            // 文件下载输出到响应流
            ResponseUtil.downloadFile(response, "", downloadFileName, tempFile, request.getHeader("USER-AGENT"));
        } else {
            //本地查看
            ModelMap modelMap = new ModelMap();
            modelMap.put("errMsg", errMsg);
            return new ModelAndView("/eland/ct/ct001/ct001_doc_error", modelMap)
        }
    }

    /** 签约开始倒计时 */
    public ModelAndView stCountDown(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();

        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        opData.setStrValue(prePath + "entityName", "SysControlInfo");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "scType");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", "1");
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
        opData.setStrValue(prePath + "ResultFields.fieldName", "startCd");   // 1:启动， 2：暂停 3： 停用
        svcRequest.addOp("QUERY_ENTITY_CMPT", opData);
        opData = new XmlBean();
        svcRequest.addOp("QUERY_TIME_INTERVAL", opData);
        SvcResponse svcResponse = query(svcRequest);
        String startCd = "1";
        String errMsg = "";
        String mark = "";
        long sysDate = DateUtil.getSysDate().getTime();  //获取服务器时间
        if (svcResponse.isSuccess()) {
            if (svcResponse.getFirstOpRsp("QUERY_ENTITY_CMPT") != null) {
                XmlBean startCdBean = svcResponse.getFirstOpRsp("QUERY_ENTITY_CMPT").getBeanByPath("Operation.OpResult.PageData");
                startCd = startCdBean.getStrValue("PageData.Row[0].startCd");
            }
            XmlBean opResult = svcResponse.getFirstOpRsp("QUERY_TIME_INTERVAL");
            XmlBean resultBean = opResult.getBeanByPath("Operation.OpResult");
            mark = resultBean.getStrValue("OpResult.mark");
            //获取服务器时间
            sysDate = DateUtil.getSysDate().getTime();
            if (StringUtil.isEqual(startCd, "1") && StringUtil.isEqual(mark, "1")) {
                //本轮开始
                long ctTimeSt = NumberUtil.getLongFromObj(resultBean.getStrValue("OpResult.CtTimeSlice.ctTimeSt"));
                //本轮结束
                long ctTimeEnd = NumberUtil.getLongFromObj(resultBean.getStrValue("OpResult.CtTimeSlice.ctTimeEnd"));
                //阅读时长
                long readTime = NumberUtil.getLongFromObj(resultBean.getStrValue("OpResult.CtTimeSlice.readTime"));
                //本轮间隔
                long spaceTime = NumberUtil.getLongFromObj(resultBean.getStrValue("OpResult.CtTimeSlice.spaceTime"));
                //暂停时刻 距 正常结束时长
                long ctTimeSpace = NumberUtil.getLongFromObj(resultBean.getStrValue("OpResult.CtTimeSlice.ctTimeSpace"));

                //推送 本轮开始时间，本轮结束时间。
                Date timeSt = new Date(ctTimeSt);
                Date timeEnd = new Date(ctTimeEnd);
                SimpleDateFormat sd = new SimpleDateFormat("HH:mm:ss");
                modelMap.put("stTime", sd.format(timeSt));
                modelMap.put("endTime", sd.format(timeEnd));

                /**
                 * 判断本轮签约 有无暂停
                 */
                if (ctTimeSpace == 0) {
                    //无暂停， 倒计时差值计算
                    long temp = ctTimeSt + readTime;
                    if (temp < sysDate) {
                        //当前时间 大于 阅读：可签约阶段
                        long cfm_end = ctTimeEnd - sysDate;
                        modelMap.put("spaceTime", cfm_end / 1000);
                        modelMap.put("stEndCt", 'end');
                    } else {
                        //阅读阶段
                        long st_cfm = temp - sysDate;
                        modelMap.put("spaceTime", st_cfm / 1000);
                        modelMap.put("stEndCt", 'start');
                    }
                } else {
                    //暂停时长
                    long stopLong = NumberUtil.getLongFromObj(resultBean.getStrValue("OpResult.CtTimeSlice.stopLong"));
                    /**
                     * 判断当前时刻处于 阅读阶段 还是 可签约阶段
                     * 本轮间隔 - 暂停时刻距本轮正常签约结束时长  与 阅读时长 作比较
                     * 大于0 即为： 可签约阶段
                     * 小于0 即为： 阅读阶段
                     */
                    long x = spaceTime - ctTimeSpace - readTime;
                    long realRead = ctTimeSt + readTime + stopLong;
                    if (x < 0 && realRead > sysDate) {
                        //阅读阶段
                        long cfm_st = realRead - sysDate;
                        modelMap.put("spaceTime", cfm_st / 1000);
                        modelMap.put("stEndCt", 'start');
                    } else {
                        //可签约
                        long cfm_end = ctTimeEnd - sysDate;
                        modelMap.put("spaceTime", cfm_end / 1000);
                        modelMap.put("stEndCt", 'end');
                    }
                }
                modelMap.put("sysDate", sysDate);
                modelMap.put("startCd", startCd);
                modelMap.put("mark", mark);
                return new ModelAndView("/eland/ct/ct001/ct001_stCtTime", modelMap);
            } else {
                modelMap.put("sysDate", sysDate);
                String info = resultBean.getStrValue("OpResult.info");
                if (StringUtil.isEqual(mark, "11") || StringUtil.isEqual(mark, "12")) {
                    String start = resultBean.getStrValue("OpResult.start");
                    String end = resultBean.getStrValue("OpResult.end");
                    info = "签约时间段:<br>" + start + "至<br>" + end;
                } else if (StringUtil.isEqual(mark, "13")) {
                    long cdTimeStart = NumberUtil.getLongFromObj(resultBean.getStrValue("OpResult.start"));
                    long cdTime = cdTimeStart - sysDate;
                    info = "开始签约倒计时:<br>";
                    modelMap.put("spaceTime", cdTime / 1000);
                } else if (StringUtil.isEqual(mark, "14")) {
                    info = "今日签约结束！";
                } else if (StringUtil.isEqual(mark, "15")) {
                    long nextTimeStart = NumberUtil.getLongFromObj(resultBean.getStrValue("OpResult.end"));
                    long nextTime = nextTimeStart - sysDate;
                    info = "本轮已完成签约<br>下轮开始倒计时<br>";
                    modelMap.put("spaceTime", nextTime / 1000);
                } else if (StringUtil.isEqual(mark, "2")) {
                    long ztime = NumberUtil.getLongFromObj(resultBean.getStrValue("OpResult.ztime"));
                    info = "签约暂停中<br>距暂停结束还剩<br>";
                    modelMap.put("spaceTime", ztime / 1000);
                } else if (StringUtil.isEqual(mark, "3")) {
                    info = "已关闭签约控制<br>可随时签约<br>";
                }
                modelMap.put("info", info);
            }
        } else {
            errMsg = svcResponse.getErrMsg();
        }
        modelMap.put("sysDate", sysDate);
        modelMap.put("startCd", startCd);
        modelMap.put("mark", mark);
        modelMap.put("errMsg", errMsg);
        return new ModelAndView("/eland/ct/ct001/ct001_ctTime_err", modelMap);
    }

    /** 生成签约安置协议 */
    public ModelAndView printDoc(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("docFileUrl", request.getParameter("docFileUrl"));
        return new ModelAndView("/eland/ct/ct001/ct001_doc_print", modelMap)
    }

    /** 获取签约状态 */
    public void getStatus(HttpServletRequest request, HttpServletResponse response) {
        XmlBean opData = new XmlBean();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.addOp("QUERY_TIME_INTERVAL", opData);
        SvcResponse svcResponse = query(svcRequest);
        String mark = "";
        if (svcResponse.isSuccess()) {
            //获取签约控制启动状态
            XmlBean opResult = svcResponse.getFirstOpRsp("QUERY_TIME_INTERVAL");
            XmlBean resultBean = opResult.getBeanByPath("Operation.OpResult");
            mark = resultBean.getStrValue("OpResult.mark");
        }
        ResponseUtil.printSvcResponse(response, svcResponse, """mark:'${mark}'""");
    }

    /** 为 远程调用查询 签约信息 提供jsonP接口 */
    public void queryCtCfmInfo(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String ctStartTime = request.getParameter("ctStartTime");
        String ctEndTime = request.getParameter("ctEndTime");
        String ctDelay = request.getParameter("ctDelay");
//        对日期做非空判断
        if (StringUtil.isEmptyOrNull(ctDelay)) {
            ctDelay = "0";//延迟时间
        }
        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        opData.setStrValue(prePath + "queryType", "2");
        //根据延迟时间 计算实际需要查询的时间区间
        int x = 0;
        opData.setStrValue(prePath + "Conditions.Condition[${x}].fieldName", "HouseInfo.HsCtInfo.ctStatus");
        opData.setStrValue(prePath + "Conditions.Condition[${x}].fieldValue", "2");
        opData.setStrValue(prePath + "Conditions.Condition[${x++}].operation", "=");
        if (StringUtil.isNotEmptyOrNull(ctStartTime)) {
            //计算 延迟时间
            Date realStartTime = new Date(DateUtil.toDateYmdHmsWthH(ctStartTime).getTime());
            opData.setStrValue(prePath + "Conditions.Condition[${x}].fieldName", "HouseInfo.HsCtInfo.ctDate");
            opData.setStrValue(prePath + "Conditions.Condition[${x}].fieldValue", DateUtil.toStringYmdHmsWthHS(realStartTime));
            opData.setStrValue(prePath + "Conditions.Condition[${x++}].operation", ">=");
        }
        if (StringUtil.isNotEmptyOrNull(ctEndTime)) {
            //计算 延迟时间
            Date realEndTime = new Date(DateUtil.toDateYmdHmsWthH(ctEndTime).getTime());
            opData.setStrValue(prePath + "Conditions.Condition[${x}].fieldName", "HouseInfo.HsCtInfo.ctDate");
            opData.setStrValue(prePath + "Conditions.Condition[${x}].fieldValue", DateUtil.toStringYmdHmsWthHS(realEndTime));
            opData.setStrValue(prePath + "Conditions.Condition[${x++}].operation", "<=");
        }

        // 本地查询的数据
        String lastEndTime = request.getParameter("lastEndTime");
        if (StringUtil.isNotEmptyOrNull(lastEndTime)) {
            //计算 延迟时间
            opData.setStrValue(prePath + "Conditions.Condition[${x}].fieldName", "HouseInfo.HsCtInfo.ctDate");
            opData.setStrValue(prePath + "Conditions.Condition[${x}].fieldValue", lastEndTime);
            opData.setStrValue(prePath + "Conditions.Condition[${x++}].operation", ">=");
        }

        String currentEndTime = DateUtil.toStringYmdHmsWthHS(new Date(DateUtil.getSysDate().getTime() - NumberUtil.getLongFromObj(ctDelay)));
        //计算 延迟时间
        opData.setStrValue(prePath + "Conditions.Condition[${x}].fieldName", "HouseInfo.HsCtInfo.ctDate");
        opData.setStrValue(prePath + "Conditions.Condition[${x}].fieldValue", currentEndTime);
        opData.setStrValue(prePath + "Conditions.Condition[${x++}].operation", "<");
        // 排序字段
        opData.setStrValue(prePath + "SortFields.SortField[0].fieldName", "HouseInfo.HsCtInfo.ctDate");
        opData.setStrValue(prePath + "SortFields.SortField[0].sortOrder", "asc");

        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "HouseInfo.hsId");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "HouseInfo.hsFullAddr");
        opData.setStrValue(prePath + "ResultFields.fieldName[2]", "HouseInfo.hsOwnerPersons");
        opData.setStrValue(prePath + "ResultFields.fieldName[3]", "HouseInfo.HsCtInfo.ctDate");

        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
        SvcResponse svcResponse = query(svcRequest);
        //返回json 字符串
        StringBuilder sb = new StringBuilder();
        if (svcResponse.isSuccess()) {
            XmlBean result = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult.PageData")
            if (result != null) {
                int resultCount = result.getListNum("PageData.Row");
                for (int i = 0; i < resultCount; i++) {
                    String hsId = result.getStrValue("PageData.Row[${i}].HouseInfo.hsId");
                    String hsFullAddr = result.getStrValue("PageData.Row[${i}].HouseInfo.hsFullAddr");
                    String hsOwnerPersons = result.getStrValue("PageData.Row[${i}].HouseInfo.hsOwnerPersons");
                    String ctDate = result.getStrValue("PageData.Row[${i}].HouseInfo.HsCtInfo.ctDate");
                    if (StringUtil.isNotEmptyOrNull(ctDate)) {
                        ctDate = DateUtil.format(DateUtil.parse(ctDate, "yyyyMMddHHmmssSSS"), "yyyy-MM-dd HH:mm:ss.SSS")
                    }
                    sb.append("""{ "hsId":"${hsId}","hsOwnerPersons":"${hsOwnerPersons}", "hsFullAddr":"${
                        hsFullAddr
                    }", "ctDate":"${ctDate}" },""");
                }
            }
        }
        // 查询参数 CT_END_DAYS
        XmlBean reqData = new XmlBean();
        String nodePath = "SvcCont.SysCfgs.SysCfg[0].";
        String ctEndDays = "";
        reqData.setStrValue(nodePath + "itemCd", "CT_END_DAYS");
        reqData.setValue(nodePath + "prjCd", "300");
        // 请求信息
        SvcRequest svcReqCfg = RequestUtil.getSvcRequest(request);
        svcReqCfg.setReqData(reqData);
        // 调用服务
        svcResponse = callService("sysCfgService", "querySysCfgData", svcReqCfg);
        //
        if (svcResponse.isSuccess()) {
            XmlBean staffBean = svcResponse.getRspData().getBeanByPath("SvcCont.SysCfgs.SysCfg[0]");
            //获取系统当前日期
            String sysDate = DateUtil.toStringYmd(DateUtil.getSysDate());
            ctEndDays = DateUtil.datePoorYMD(staffBean.getStrValue("SysCfg.dftValue"), sysDate);
        }

        String jsonStr = """{"ctEndDays":"${ctEndDays}","ctEndTime":"${currentEndTime}","ctInfo":[${
            sb.toString()
        }] }""";
        ResponseUtil.printAjax(response, jsonStr);
    }

    /**
     * 获取预分配置数据
     * @param request
     * @return
     */
    private JSONObject getSchemeTypeCfg(HttpServletRequest request, schemeType) {
        // 获取预分分类定义
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setValue("Request.SvcCont.SysCfgs.SysCfg[0].itemCd", "SCHEME_TYPE");
        svcRequest.setValue("Request.SvcCont.SysCfgs.SysCfg[0].isCached", true);
        svcRequest.setValue("Request.SvcCont.SysCfgs.SysCfg[0].prjCd", svcRequest.getPrjCd());
        SvcResponse svcResponse = callService("sysCfgService", "querySysCfgData", svcRequest);
        if (svcResponse.isSuccess()) {
            int count = svcResponse.getRspData().getListNum("SvcCont.SysCfgs.SysCfg[0].Values.Value");
            String schemeJsonStr = null;
            for (int i = 0; i < count; ++i) {
                String key = svcResponse.getRspData().getStrValue("SvcCont.SysCfgs.SysCfg[0].Values.Value[" + i + "].valueCd");
                if (StringUtil.isEqual(key, schemeType)) {
                    schemeJsonStr = svcResponse.getRspData().getStrValue("SvcCont.SysCfgs.SysCfg[0].Values.Value[" + i + "].notes");
                    break;
                }
            }
            // 获取定义类型
            return JSONObject.fromObject(schemeJsonStr);
        } else {
            return new JSONObject();
        }
    }

}

