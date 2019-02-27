import com.shfb.oframe.core.util.common.*
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import net.sf.json.JSONObject
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 签约处理控制层
 */
class ct005 extends GroovyController {

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        RequestUtil requestUtil = new RequestUtil(request);
        ModelMap modelMap = new ModelMap();
        modelMap.put("hsId", requestUtil.getStrParam("hsId"));
        String schemeType = requestUtil.getStrParam("schemeType");
        if (StringUtil.isEmptyOrNull(schemeType)) {
            schemeType = "3";
        }
        modelMap.put("schemeType", schemeType);

        // 获取预分分类定义
        JSONObject jsonObject = getSchemeTypeCfg(request, schemeType);
        // 推送输出结果到界面
        modelMap.put("ctStatusField", jsonObject.get("attr_status"));
        modelMap.put("conditionNames", jsonObject.get("conditionNames", ""));
        modelMap.put("conditions", jsonObject.get("conditions", ""));
        modelMap.put("conditionValues", jsonObject.get("conditionValues", ""));
        // 返回展示界面
        return new ModelAndView("/eland/ct/ct005/ct005", modelMap);
    }

    /** 初始化 右侧 主界面 */
    public ModelAndView info(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        // 获取房屋编号
        String hsId = request.getParameter("hsId");
        String hsCtId = request.getParameter("hsCtId");
        String schemeType = request.getParameter("schemeType");
        String opFlag = request.getParameter("opFlag");
        // 推送界面展示信息
        modelMap.put("hsId", hsId);
        modelMap.put("hsCtId", hsCtId);
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
        opData.setStrValue(prePath + "ResultFields.fieldName[3]", "HouseInfo.HsCtInfo.ctType");             // 安置方式
        opData.setStrValue(prePath + "ResultFields.fieldName[4]", "HouseInfo.HsCtInfo." + ctStatusField);  // 签约状态
        opData.setStrValue(prePath + "ResultFields.fieldName[5]", "HouseInfo.HsCtInfo." + ctDateField);  // 签约时间
        opData.setStrValue(prePath + "ResultFields.fieldName[6]", "HouseInfo.hsCd");
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
        // 调用服务
        SvcResponse svcResponse = query(svcRequest);
        //获取各种状态  跳转不同页面    0，未生成  1， 已生成
        String ctStatus = "";
        if (svcResponse.isSuccess()) {
            XmlBean pageData = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA")
                    .getBeanByPath("Operation.OpResult.PageData");
            if (pageData != null && pageData.getListNum("PageData.Row") > 0) {
                XmlBean houseInfo = pageData.getBeanByPath("PageData.Row[0].HouseInfo");
                ctStatus = pageData.getStrValue("PageData.Row[0].HouseInfo.HsCtInfo." + ctStatusField);
                String ctDate = pageData.getStrValue("PageData.Row[0].HouseInfo.HsCtInfo." + ctDateField);
                /*if (StringUtil.isNotEmptyOrNull(ctDate)) {
                    ctDate = DateUtil.format(DateUtil.parse(ctDate, "yyyyMMddHHmmss"), "yyyy-MM-dd HH:mm:ss")
                }*/
                modelMap.put("hsInfo", houseInfo.getRootNode());
                modelMap.put("ctDate", ctDate);
                modelMap.put("ctStatus", ctStatus);
            }
        }
//        根据结算状态过滤跳转页面   00501--> 已结算页面  00502-->
        String forwardPage = "/eland/ct/ct005/ct00501";
        if ("viewDoc".equals(opFlag)) {
            // 协议未打印: 继续留在签约页面
            forwardPage = "/eland/ct/ct005/ct00501";
        } else if ("viewFj".equals(opFlag)) {
            // 已经签约： 去上传附件页面
            forwardPage = "forward:/eland/ct/ct005/ct005-initFj.gv"
        } else if (StringUtil.isEqual("2", ctStatus)) {
            // 已经签约： 去上传附件页面
            forwardPage = "forward:/eland/ct/ct005/ct005-initFj.gv"
        } else {
            // 协议未打印: 继续留在签约页面
            forwardPage = "/eland/ct/ct005/ct00501";
        }
        return new ModelAndView(forwardPage, modelMap);
    }

    /** 初始化附件  */
    public ModelAndView initFj(HttpServletRequest request, HttpServletResponse response) {
        //基本信息  产权信息 单独查询
        String hsId = request.getParameter("hsId");
        String hsCtId = request.getParameter("hsCtId");
        String schemeType = request.getParameter("schemeType");

        ModelMap modelMap = new ModelMap();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 获取预分分类定义
        JSONObject jsonObject = getSchemeTypeCfg(request, schemeType);
        // 获取附件名称
        String signDocName = jsonObject.get("sign_doc_name");
        String photoDocName = jsonObject.get("photo_doc_name");
        // 推动到界面视图
        modelMap.put("signDocName", signDocName);
        modelMap.put("photoDocName", photoDocName);
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
        modelMap.put("hsId", hsId);
        return new ModelAndView("/eland/ct/ct005/ct00502", modelMap);
    }

    /**
     * 生成及下载安置协议
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public ModelAndView generateCt(HttpServletRequest request, HttpServletResponse response) {
        RequestUtil requestUtil = new RequestUtil(request);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 输入参数
        String hsId = request.getParameter("hsId");
        String hsCtId = request.getParameter("hsCtId");
        String agent = request.getHeader("USER-AGENT");
        String fromOp = request.getParameter("fromOp");
        String schemeType = request.getParameter("schemeType");

        // 生成签约协议文档
        XmlBean paramData = new XmlBean();
        paramData.setStrValue("OpData.HsCtInfo.hsId", hsId);
        paramData.setStrValue("OpData.HsCtInfo.hsCtId", hsCtId);
        paramData.setStrValue("OpData.HsCtInfo.schemeType", schemeType);
        svcRequest.addOp("CREATE_AGREEMENT", paramData);
        // 调用服务，执行数据查询
        SvcResponse svcResponse = transaction(svcRequest);
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
                    return new ModelAndView("/eland/ct/ct005/ct005_doc_print_iframe", modelMap)
                } else {
                    //本地查看
                    ModelMap modelMap = new ModelMap();
                    modelMap.put("docFileUrl", docPrePath + docPath);
                    return new ModelAndView("/eland/ct/ct005/ct005_doc_iframe", modelMap)
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
                modelMap.put("errMsg", "生成文档失败，错误原因[${svcResponse.getErrMsg()}]!");
                return new ModelAndView("/eland/ct/ct005/ct005_doc_error", modelMap)
            }
        }
    }

    /**
     * 查看协议文档
     * @param request
     * @param response
     * @return
     */
    public ModelAndView viewDoc(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("docFileUrl", request.getParameter("docFileUrl"));
        return new ModelAndView("/eland/ct/ct005/ct005_doc_view", modelMap)
    }

    /**
     * 确认签约处理
     * @param request
     * @param response
     * @return
     */
    public ModelAndView cfmCt(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String hsCtId = request.getParameter("hsCtId");
        String schemeType = request.getParameter("schemeType");

        // 确认签约处理
        String nodePath = "OpData.";
        XmlBean opData = new XmlBean();
        opData.setStrValue(nodePath + "HsCtInfo.hsCtId", hsCtId);
        opData.setStrValue(nodePath + "HsCtInfo.schemeType", schemeType);

        // 获取预分分类定义
        JSONObject jsonObject = getSchemeTypeCfg(request, schemeType);
        // 获取操作人
        String attrStatusStaff = jsonObject.get("attr_status_staff");
        opData.setStrValue(nodePath + "HsCtInfo." + attrStatusStaff, svcRequest.getReqStaff());
//        opData.setStrValue(nodePath + "HsCtInfo.hsDlDate", svcRequest.getReqTime());
        svcRequest.addOp("CHANGE_TO_FORMAL_SIGN_TT", opData);
        // 输出处理结果
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /**
     * 协议打印字段
     * @param request
     * @param response
     * @return
     */
    public ModelAndView cancelCt(HttpServletRequest request, HttpServletResponse response) {
        String hsCtId = request.getParameter("hsCtId");
        String schemeType = request.getParameter("schemeType");
        // 请求参数
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 确认签约处理
        String nodePath = "OpData.";
        XmlBean opData = new XmlBean();
        opData.setStrValue(nodePath + "HsCtInfo.hsCtId", hsCtId);
        opData.setStrValue(nodePath + "HsCtInfo.schemeType", schemeType);
        svcRequest.addOp("CHANGE_TO_UN_SIGNED", opData);
        // 输出处理结果
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /**
     * 协议打印状态
     * @param request
     * @param response
     * @return
     */
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
        opData.setStrValue(nodePath + "EntityData.printJsStatus", "1");
        //打印协议，修改安置意向。
        String ctType = request.getParameter("ctType");
        if (StringUtil.isNotEmptyOrNull(ctType)) {
            opData.setStrValue(nodePath + "EntityData.ctType", ctType);
        }
        svcRequest.addOp("SAVE_ENTITY", opData);
        // 输出处理结果
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /**
     * 初始化打印协议界面
     * @param request
     * @param response
     * @return
     */
    public ModelAndView printDoc(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("docFileUrl", request.getParameter("docFileUrl"));
        return new ModelAndView("/eland/ct/ct001/ct001_doc_print", modelMap)
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

