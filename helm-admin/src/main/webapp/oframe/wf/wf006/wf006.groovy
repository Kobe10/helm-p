import com.shfb.oframe.core.common.util.OframeCoreUtils
import com.shfb.oframe.core.util.common.DateUtil
import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.properties.PropertiesUtil
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import com.zhuozhengsoft.pageoffice.FileSaver
import org.springframework.ui.ModelMap
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.util.HtmlUtils

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * User: shfb_wang 
 * Date: 2016/3/12 0012 10:30
 * Copyright(c) 北京四海富博计算机服务有限公司
 */

class wf006 extends GroovyController {

    /** 初始化页面 */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        String designFlag = request.getParameter("designFlag");
        String formId = request.getParameter("formId");
        // 表单信息
        XmlBean formInfo = null;
        if (!StringUtil.isEqual("new", designFlag) && StringUtil.isNotEmptyOrNull(formId)) {
            XmlBean reqData = new XmlBean();
            reqData.setStrValue("OpData.entityName", "SysFormInfoDef");
            reqData.setStrValue("OpData.Conditions.Condition[0].fieldName", "formId");
            reqData.setStrValue("OpData.Conditions.Condition[0].operation", "=");
            reqData.setStrValue("OpData.Conditions.Condition[0].fieldValue", formId);

            reqData.setStrValue("OpData.ResultFields.fieldName[0]", "formId");
            reqData.setStrValue("OpData.ResultFields.fieldName[1]", "formCd");
            reqData.setStrValue("OpData.ResultFields.fieldName[2]", "formName");
            reqData.setStrValue("OpData.ResultFields.fieldName[3]", "formType");
            reqData.setStrValue("OpData.ResultFields.fieldName[4]", "formTypeId");
            reqData.setStrValue("OpData.ResultFields.fieldName[5]", "formStatus");
            reqData.setStrValue("OpData.ResultFields.fieldName[6]", "formStatuDate");
            reqData.setStrValue("OpData.ResultFields.fieldName[7]", "formDesc");
            reqData.setStrValue("OpData.ResultFields.fieldName[8]", "exeFormRule");
            reqData.setStrValue("OpData.ResultFields.fieldName[9]", "exeFormDataType");
            reqData.setStrValue("OpData.ResultFields.fieldName[10]", "exeDataParam");
            reqData.setStrValue("OpData.ResultFields.fieldName[11]", "nextExeDef");

            svcRequest.addOp("QUERY_ENTITY_CMPT", reqData);

            SvcResponse svcResponse = query(svcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean rspData = svcResponse.getFirstOpRsp("QUERY_ENTITY_CMPT").getBeanByPath("Operation.OpResult.PageData");
                if (rspData != null) {
                    formInfo = rspData.getBeanByPath("PageData.Row[0]");
                    //处理操作定义
                    String operaStr = formInfo.getStrValue("Row.nextExeDef");
                    if (StringUtil.isNotEmptyOrNull(operaStr)) {
                        XmlBean operDefBean = new XmlBean(operaStr);
                        if (operDefBean != null) {
                            int opDefNum = operDefBean.getListNum("OperationDefs.OperationDef");
                            List opDefList = new ArrayList();
                            for (int i = 0; i < opDefNum; i++) {
                                opDefList.add(operDefBean.getBeanByPath("OperationDefs.OperationDef[${i}]").getRootNode());
                            }
                            modelMap.put("opDefList", opDefList);
                            modelMap.put("operDefCode", operDefBean.getStrValue("OperationDefs.operDefCode"));
                        }
                    }

                    //根据 查询出来的 formid  查询 temp 模板
                    svcRequest = RequestUtil.getSvcRequest(request);
                    reqData = new XmlBean();
                    reqData.setStrValue("OpData.entityName", "SysFormInfoTemplate");
                    reqData.setStrValue("OpData.Conditions.Condition[0].fieldName", "sysFormId");
                    reqData.setStrValue("OpData.Conditions.Condition[0].operation", "=");
                    reqData.setStrValue("OpData.Conditions.Condition[0].fieldValue", formId)
                    reqData.setStrValue("OpData.ResultFields.fieldName[0]", "formTemplateId");
                    reqData.setStrValue("OpData.ResultFields.fieldName[1]", "formTemplateCd");
                    reqData.setStrValue("OpData.ResultFields.fieldName[2]", "formTemplateDesc");
                    reqData.setStrValue("OpData.ResultFields.fieldName[3]", "sysFormTemplateType");
                    reqData.setStrValue("OpData.ResultFields.fieldName[4]", "sysTemplateDate");
                    reqData.setStrValue("OpData.ResultFields.fieldName[5]", "sysTemplateDef");
                    reqData.setStrValue("OpData.ResultFields.fieldName[6]", "sysFormId");
                    reqData.setStrValue("OpData.ResultFields.fieldName[7]", "formTemplateName");
                    svcRequest.addOp("QUERY_ENTITY_CMPT", reqData);
                    svcResponse = query(svcRequest);
                    if (svcResponse.isSuccess()) {
                        rspData = svcResponse.getFirstOpRsp("QUERY_ENTITY_CMPT").getBeanByPath("Operation.OpResult.PageData");
                        if (rspData != null) {
                            List tempList = new ArrayList();
                            int tempNum = rspData.getListNum("PageData.Row");
                            for (int i = 0; i < tempNum; i++) {
                                tempList.add(rspData.getBeanByPath("PageData.Row[${i}]").getRootNode());
                            }
                            modelMap.put("formTempList", tempList);
                        }
                    }
                }
            }
        }
        // 处理新建等场合的默认值
        if (formInfo == null) {
            formInfo = new XmlBean();
        }
        // 数据准备 类型  RULE_DATA、SQL_DATA、CMPT_DATA
        if (StringUtil.isEmptyOrNull(formInfo.getStrValue("Row.exeFormDataType"))) {
            formInfo.setStrValue("Row.exeFormDataType", "RULE_DATA");
        }
        modelMap.put("formInfo", formInfo.getRootNode());
        //查看详情不可以修改
        boolean editFlag = true;
        if (StringUtil.isEqual(designFlag, "detail")) {
            editFlag = false;
        }
        modelMap.put("editFlag", editFlag);
        // 返回界面展示结果
        return new ModelAndView("/oframe/wf/wf006/wf006", modelMap);
    }

    /**
     * 在线编辑模板
     * @return 返回模板编辑页面
     */
    public ModelAndView editTemplate(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        //调用组件查询表单模板
        String formId = request.getParameter("formId");
        String oldTemplateCd = request.getParameter("oldTemplateCd");
        String sysFormTemplateType = request.getParameter("sysFormTemplateType");

        XmlBean paramData = new XmlBean();
        paramData.setStrValue("OpData.formId", formId);
        paramData.setStrValue("OpData.formTemplateCd", oldTemplateCd);
        paramData.setStrValue("OpData.flag", "edit");//判断是下载还是编辑模板，download下载、edit编辑，默认为编辑
        svcRequest.addOp("FILE_DOWNLOAD_CMPT", paramData);
        SvcResponse svcResponse = transaction(svcRequest);
        String docPath = "";
        if (svcResponse.isSuccess()) {
            XmlBean opResult = svcResponse.getFirstOpRsp("FILE_DOWNLOAD_CMPT").getBeanByPath("Operation.OpResult");
            if (opResult != null) {
                docPath = opResult.getValue("OpResult.path");

                String cfgPath = PropertiesUtil.readPath("oframe", "com.shfb.oframe.common.report.reportfactory.report.path");
                File localFile = new File(StringUtil.formatFilePath(cfgPath + docPath));
                modelMap.put("tempText", HtmlUtils.htmlEscape(OframeCoreUtils.stringFile(localFile)));
            }
        }
        modelMap.put("formId", formId);
        modelMap.put("oldTemplateCd", oldTemplateCd);
        modelMap.put("sysFormTemplateType", sysFormTemplateType);
        if (StringUtil.isEqual("HTML_TYPE_TEMPLATE", sysFormTemplateType)) {
            //PC页面
            return new ModelAndView("/oframe/wf/wf006/wf006_edit_temp", modelMap);
        } else if (StringUtil.isEqual("REPORT_TYPE_TEMPLATE", sysFormTemplateType)) {
            //Ireport模版
            return new ModelAndView("/oframe/wf/wf006/wf006_edit_tempxml", modelMap);
        } else {
            //Word模版、Excel模版
            modelMap.put("docPath", docPath);
            return new ModelAndView("/oframe/wf/wf006/wf006_edit_tempword", modelMap);
        }
    }

    /**
     * 根据地址下载文件
     * @param request
     * @param response
     */
    public void openFile(HttpServletRequest request, HttpServletResponse response) {
        String agent = request.getHeader("USER-AGENT");
        RequestUtil requestUtil = new RequestUtil(request);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 获取参数
        String formId = requestUtil.getStrParam("formId");
        String oldTemplateCd = requestUtil.getStrParam("oldTemplateCd");

        XmlBean paramData = new XmlBean();
        paramData.setStrValue("OpData.formId", formId);
        paramData.setStrValue("OpData.formTemplateCd", oldTemplateCd);
        svcRequest.addOp("FILE_DOWNLOAD_CMPT", paramData);
        SvcResponse svcResponse = transaction(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean opResult = svcResponse.getFirstOpRsp("FILE_DOWNLOAD_CMPT").getBeanByPath("Operation.OpResult");
            if (opResult != null) {
                String docPath = opResult.getValue("OpResult.path");

                String cfgPath = PropertiesUtil.readPath("oframe", "com.shfb.oframe.common.report.reportfactory.report.path");
                File localFile = new File(StringUtil.formatFilePath(cfgPath + docPath));
                // 上传文件类型
                String contextType = requestUtil.getStrParam("contextType");
                String[] tempArr = docPath.split("\\.");
                String docName = "模板文件." + tempArr[1];
                ResponseUtil.downloadFile(response, contextType, docName, localFile, agent, false, true);
            }
        }
    }

    /**
     * 编辑Ireport模版页面
     */
    public ModelAndView xmlCode(HttpServletRequest request, HttpServletResponse response) {
        // 页面返回视图
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/oframe/wf/wf006/wf006_edit_xml", modelMap);
    }

    /** 查看word/excel文档 */
    public ModelAndView viewWordDoc(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("docPath", request.getParameter("docPath"));
        modelMap.put("formId", request.getParameter("formId"));
        modelMap.put("oldTemplateCd", request.getParameter("oldTemplateCd"));
        return new ModelAndView("/oframe/wf/wf006/wf006_edit_word", modelMap);
    }

    /** 保存表单模板信息 saveTemplateText **/
    public void saveTemplateText(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String formId = request.getParameter("formId");
        String oldTemplateCd = request.getParameter("oldTemplateCd");
        String templateText = request.getParameter("templateText");
        String sysFormTemplateType = request.getParameter("sysFormTemplateType");
        String fileType = "html";
        if (StringUtil.isEqual("REPORT_TYPE_TEMPLATE", sysFormTemplateType)) {
            //Ireport模版
            fileType = "jrxml";
        }
        String cfgPath = PropertiesUtil.readPath("oframe", "com.shfb.oframe.common.report.reportfactory.report.path");
        String tempFilePath = OframeCoreUtils.createFile(cfgPath, "${formId}_${oldTemplateCd}." + fileType, templateText.toCharArray());

        XmlBean reqData = new XmlBean();
        String reqPath = "OpData.";
        reqData.setStrValue(reqPath + "formId", formId);
        reqData.setStrValue(reqPath + "formTemplateCd", oldTemplateCd);
        reqData.setStrValue(reqPath + "formTemplateId", formId + oldTemplateCd);
        reqData.setStrValue(reqPath + "sysTemplateDef", tempFilePath);
        svcRequest.addOp("UPDATE_TEMPLATE_DEF_CMPT", reqData);
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, """ """);
    }

    /**
     * 保存word、excel模板
     */
    public void saveEditFile(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 获取参数
        String formId = request.getParameter("formId");
        String oldTemplateCd = request.getParameter("oldTemplateCd");
        //生成文件
        XmlBean paramData = new XmlBean();
        paramData.setStrValue("OpData.formId", formId);
        paramData.setStrValue("OpData.formTemplateCd", oldTemplateCd);
        svcRequest.addOp("FILE_DOWNLOAD_CMPT", paramData);
        SvcResponse svcResponse = transaction(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean opResult = svcResponse.getFirstOpRsp("FILE_DOWNLOAD_CMPT").getBeanByPath("Operation.OpResult");
            if (opResult != null) {
                String docPath = opResult.getValue("OpResult.path");

                String cfgPath = PropertiesUtil.readPath("oframe", "com.shfb.oframe.common.report.reportfactory.report.path");
                File localFile = new File(StringUtil.formatFilePath(cfgPath + docPath));
                FileSaver fs = new FileSaver(request, response);
                fs.saveToFile(localFile.getAbsolutePath());
                fs.close();
                //保存表单模板信息
                svcRequest = RequestUtil.getSvcRequest(request);
                XmlBean reqData = new XmlBean();
                String reqPath = "OpData.";
                reqData.setStrValue(reqPath + "formId", formId);
                reqData.setStrValue(reqPath + "formTemplateCd", oldTemplateCd);
                reqData.setStrValue(reqPath + "formTemplateId", formId + oldTemplateCd);
                reqData.setStrValue(reqPath + "sysTemplateDef", cfgPath + docPath);
                svcRequest.addOp("UPDATE_TEMPLATE_DEF_CMPT", reqData);
                transaction(svcRequest);
            }
        }
    }

    /** 保存表单信息 saveForm **/
    public void saveForm(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String prjCd = request.getParameter("prjCd");
        String formId = request.getParameter("formId");
        String formCd = request.getParameter("formCd");
        String formName = request.getParameter("formName");
        String formType = request.getParameter("formType");
        String formTypeId = request.getParameter("formTypeId");
        String formStatus = request.getParameter("formStatus");
        String formStatuDate = request.getParameter("formStatuDate");
        String formDesc = request.getParameter("formDesc");
        String formDef = request.getParameter("formDef");
        //前置条件
        String exeFormRule = request.getParameter("exeFormRule");
        String exeFormDataType = request.getParameter("exeFormDataType");
        //数据类型
        String exeDataParam = request.getParameter("exeDataParam");

        if (StringUtil.isEmptyOrNull(formId)) {
            formId = "";
        }

        /**
         * 拼接 表单基本信息 报文
         */
        XmlBean reqData = new XmlBean();
        String reqPath = "OpData.SysFormInfoDef.";
        reqData.setStrValue(reqPath + "formId", formId);
        reqData.setStrValue(reqPath + "formCd", formCd);
        reqData.setStrValue(reqPath + "formName", formName);
        reqData.setStrValue(reqPath + "formType", formType);
        reqData.setStrValue(reqPath + "formTypeId", formTypeId);
        reqData.setStrValue(reqPath + "formStatus", formStatus);
        reqData.setStrValue(reqPath + "formStatuDate", formStatuDate);
        reqData.setStrValue(reqPath + "formDesc", formDesc);
        reqData.setStrValue(reqPath + "formDef", formDef);
        //前置条件
        reqData.setStrValue(reqPath + "exeFormRule", exeFormRule);
        reqData.setStrValue(reqPath + "exeFormDataType", exeFormDataType);
        //数据类型
        reqData.setStrValue(reqPath + "exeDataParam", exeDataParam);

        // 操作定义
        XmlBean operDefBean = new XmlBean();
        reqPath = "OperationDefs.";
        String[] operNames = request.getParameterValues("operName");
        String[] operOnClicks = request.getParameterValues("operOnClick");
        String[] operRhtCds = request.getParameterValues("operRhtCd");
        String[] operClasss = request.getParameterValues("operClass");
        String[] operTemplates = request.getParameterValues("operTemplate");
        String[] operRhts = request.getParameterValues("operRht");
        String operDefCode = request.getParameter("operDefCode");
        int x = 0;
        if (operNames.length > 0) {
            for (int i = 0; i < operNames.length; i++) {
                if (StringUtil.isEmptyOrNull(operNames[i])) {
                    continue;
                }
                operDefBean.setStrValue(reqPath + "OperationDef[${x}].operName", operNames[i]);
                operDefBean.setStrValue(reqPath + "OperationDef[${x}].operOnClick", operOnClicks[i]);
                operDefBean.setStrValue(reqPath + "OperationDef[${x}].operRhtCd", operRhtCds[i]);
                operDefBean.setStrValue(reqPath + "OperationDef[${x}].operClass", operClasss[i]);
                operDefBean.setStrValue(reqPath + "OperationDef[${x}].operTemplate", operTemplates[i]);
                operDefBean.setStrValue(reqPath + "OperationDef[${x++}].operRht", operRhts[i]);
            }
            operDefBean.setStrValue(reqPath + "operDefCode", operDefCode);
        }
        reqData.setStrValue("OpData.SysFormInfoDef.nextExeDef", operDefBean.toXML());

        // 表单模板 报文
        reqPath = "OpData.SysFormInfoDef.SysFormInfoTemplates.";
        String[] formTemplateIds = request.getParameterValues("formTemplateId");
        String[] tempFilePaths = request.getParameterValues("tempFilePath");
        String[] formTemplateCds = request.getParameterValues("formTemplateCd");
        String[] formTemplateNames = request.getParameterValues("formTemplateName");
        String[] oldTemplateCds = request.getParameterValues("oldTemplateCd");
        String[] sysFormTemplateTypes = request.getParameterValues("sysFormTemplateType");
        x = 0;
        if (formTemplateCds.length > 0) {
            for (int i = 0; i < formTemplateCds.length; i++) {
                if (StringUtil.isEmptyOrNull(formTemplateCds[i])) {
                    continue;
                }
                reqData.setStrValue(reqPath + "SysFormInfoTemplate[${x}].formTemplateId", formTemplateIds[i]);
                if (StringUtil.isNotEmptyOrNull(tempFilePaths[i])) {
                    reqData.setStrValue(reqPath + "SysFormInfoTemplate[${x}].sysTemplateDef", tempFilePaths[i]);
                }
                reqData.setStrValue(reqPath + "SysFormInfoTemplate[${x}].formTemplateCd", formTemplateCds[i]);
                reqData.setStrValue(reqPath + "SysFormInfoTemplate[${x}].formTemplateName", formTemplateNames[i]);
                reqData.setStrValue(reqPath + "SysFormInfoTemplate[${x}].oldTemplateCd", oldTemplateCds[i]);
                reqData.setStrValue(reqPath + "SysFormInfoTemplate[${x++}].sysFormTemplateType", sysFormTemplateTypes[i]);
            }
        }

        svcRequest.addOp("SAVE_FORM_INFO_CMPT", reqData);
        SvcResponse svcResponse = transaction(svcRequest);
        String resultFormId = "";
        if (svcResponse.isSuccess()) {
            XmlBean rspData = svcResponse.getFirstOpRsp("SAVE_FORM_INFO_CMPT").getBeanByPath("Operation.OpResult");
            if (rspData != null) {
                resultFormId = rspData.getStrValue("OpResult.formId");
            }
        }
        ResponseUtil.printAjax(response, """{success:${svcResponse.isSuccess()}, errMsg:"${
            svcResponse.getErrMsg()
        }", resultFormId:"${
            resultFormId
        }" }""");
    }

    /** 保存 上传模板文件 saveUploadFile **/
    public void saveUploadFile(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String sysFormTemplateType = request.getParameter("sysFormTemplateType");
        //获取传入文件
        MultipartFile localFile = super.getFile(request, "uploadTempFile");
        String originalFileName = localFile.getOriginalFilename();
        String fileType = originalFileName.substring(originalFileName.lastIndexOf("."), originalFileName.length());
        String errMsg = "";
        boolean result = false;

        // 临时文件存放位置
        String remoteFileName = UUID.randomUUID().toString() + fileType;
        String prePath = "webroot:reports/";
        Date nowDate = DateUtil.getSysDate();
        String sysDateStr = DateUtil.toStringYmd(nowDate);
        String addPath = "temp/${sysDateStr}/${remoteFileName}";
        //模板类型 1、pc  2、word 3、excel  4、Ireport
        if (fileType.contains(".xml") || fileType.contains(".xlsx") || fileType.contains(".xls") ||
                fileType.contains(".doc") || fileType.contains(".docx") || fileType.contains(".jrxml") || fileType.contains(".html") || fileType.contains(".htm")) {
            // 文件夹不存在先创建文件夹
            File folder = new File(StringUtil.formatFilePath(prePath + "temp/${sysDateStr}"));
            if (!folder.exists()) {
                folder.mkdirs();
            }
            String outPutFile = StringUtil.formatFilePath(prePath + addPath);
            //临时 文件 路径
            File saveFile = new File(outPutFile);
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
                log.error("上传文件读取失败,请确认文件内容是否正确", e);
                errMsg = "上传文件读取失败，请确认文件内容是否正确";
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
            //返回结果，以及临时文件路径
            result = true;
            addPath = saveFile.getAbsolutePath();
            addPath = addPath.replace("\\", "/");
        } else {
            errMsg = "文件格式不正确";
        }
        String upLoadTime = DateUtil.toStringYmdWthH(nowDate)
        String jsonStr = """{isSuccess: ${result}, errMsg: "${errMsg}", upLoadTime: "${upLoadTime}",  tempFilePath:"${
            addPath
        }"}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

    /** 初始化 code frame */
    public ModelAndView textCode(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        String textarea = request.getParameter("textarea");
        String content = request.getParameter("content");
        String model = request.getParameter("model");
        if (StringUtil.isEmptyOrNull(model)) {
            model = "groovy";
        }
        modelMap.put("textarea", textarea);
        modelMap.put("model", model);
        modelMap.put("content", content);
        return new ModelAndView("/oframe/wf/wf006/wf006_text_code", modelMap);
    }

    /** 初始化 code frame */
    public ModelAndView webDesign(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        String textarea = request.getParameter("textarea");
        String content = request.getParameter("content");
        modelMap.put("textarea", textarea);
        modelMap.put("content", content);
        return new ModelAndView("/oframe/wf/wf006/wf006_edit_web", modelMap);
    }

    /** 下载 模板文件 */
    public void downTemp(HttpServletRequest request, HttpServletResponse response) {
        RequestUtil requestUtil = new RequestUtil(request);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String agent = request.getHeader("USER-AGENT");

        String formId = request.getParameter("formId");
        String oldTemplateCd = request.getParameter("tid");

        XmlBean paramData = new XmlBean();
        paramData.setStrValue("OpData.formId", formId);
        paramData.setStrValue("OpData.formTemplateCd", oldTemplateCd);
        paramData.setStrValue("OpData.flag", "download");//判断是下载还是编辑模板，download下载、edit编辑，默认为编辑
        svcRequest.addOp("FILE_DOWNLOAD_CMPT", paramData);
        SvcResponse svcResponse = transaction(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean opResult = svcResponse.getFirstOpRsp("FILE_DOWNLOAD_CMPT");
            if (opResult != null) {

                String docPath = opResult.getValue("Operation.OpResult.path");
                String docName = docPath.substring(docPath.lastIndexOf("/"));

                String cfgPath = PropertiesUtil.readPath("oframe", "com.shfb.oframe.common.report.reportfactory.report.path");
                File localFile = new File(StringUtil.formatFilePath(cfgPath + docPath));

                //20秒之内重新进入该页面的话不会进入该servlet的
                Date modifiedTime = DateUtil.toDateYmdHms("20150101000000");
                String contextType = requestUtil.getStrParam("contextType");
                response.setDateHeader("Last-Modified", modifiedTime.getTime());
                response.setDateHeader("Expires", modifiedTime.getTime() + 6553600);
                response.setHeader("Cache-Control", "public");
                response.setHeader("Pragma", "Pragma");
                // 文件下载输出到响应流
                ResponseUtil.downloadFile(response, contextType, docName, localFile, agent, false);
            }
        }
    }
}