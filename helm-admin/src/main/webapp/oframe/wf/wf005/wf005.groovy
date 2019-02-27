import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.common.XmlNode
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import org.springframework.ui.ModelMap
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class wf005 extends GroovyController {

    /**
     * 初始化界面
     * @param request 请求信息
     * @param response 响应结果
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/oframe/wf/wf005/wf005", modelMap)
    }

    /**
     * 表单树数据
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView formTypeTree(HttpServletRequest request, HttpServletResponse response) {
        // 获取输入参数
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "formTypeId");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "upFormTypeId");
        opData.setStrValue(prePath + "ResultFields.fieldName[2]", "formTypeName");
        opData.setStrValue(prePath + "ResultFields.fieldName[3]", "formTypeCd");
        svcRequest.addOp("QUERY_FORM_TYPE_RHT_CMPT", opData);
        SvcResponse svcResponse = query(svcRequest);
        boolean result = svcResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg();
        StringBuilder stringBuilder = new StringBuilder();
        List<String> formTypeIdList = new ArrayList<String>();
        if (result) {
            XmlBean treeBean = svcResponse.getFirstOpRsp("QUERY_FORM_TYPE_RHT_CMPT").getBeanByPath("Operation.OpResult");
            if (treeBean != null) {
                int count = treeBean.getListNum("OpResult.Node");
                for (int i = 0; i < count; i++) {
                    String id = treeBean.getStrValue("""OpResult.Node[${i}].formTypeId""");
                    formTypeIdList.add(id);
                    String pId = treeBean.getStrValue("""OpResult.Node[${i}].upFormTypeId""");
                    String name = treeBean.getStrValue("""OpResult.Node[${i}].formTypeName""");
                    String formTypeCd = treeBean.getStrValue("""OpResult.Node[${i}].formTypeCd""");
                    boolean open = true;
//                    if (i == 0) {
//                        open = true;
//                    }
                    stringBuilder.append("""{ id: "${id}",typeId: "${id}", formTypeCd: "${formTypeCd}", pId: "${
                        pId
                    }", iconSkin: "folder",name: "${
                        name
                    }",open: "${open}"},""");
                }
            }
        }
        String jsonStr = """{success:${result}, errMsg:"${errMsg}", resultMap:{treeJson:[${
            stringBuilder.toString()
        }]}}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView editFromType(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        String formTypeId = request.getParameter("formTypeId");
        //判断是否是新增
        if (StringUtil.isNotEmptyOrNull(formTypeId)) {
            //查询分类信息
            opData.setStrValue("OpData.entityName", "SysFormType");
            opData.setStrValue("OpData.entityKey", formTypeId);
            svcRequest.addOp("QUERY_ALL_ENTITY", opData);
            SvcResponse svcResponse = query(svcRequest);
            // 获取输出结果
            if (svcResponse.isSuccess()) {
                XmlBean opResult = svcResponse.getFirstOpRsp("QUERY_ALL_ENTITY").getBeanByPath("Operation.OpResult.SysFormType");
                modelMap.put("nodeInfo", opResult.getRootNode());
                int rhtCnt = opResult.getListNum("SysFormType.FormTypeRhts.FormTypeRht");
                if (rhtCnt > 0) {
                    /* 处理授权角色信息, 后去角色MAP对象 */
                    Map<String, String> roleMap = new LinkedHashMap<String, String>();
                    svcRequest = RequestUtil.getSvcRequest(request);
                    XmlBean reqData = new XmlBean();
                    reqData.setStrValue("SvcCont.Role.roleCd", "");
                    svcRequest.setReqData(reqData);
                    svcResponse = callService("roleService", "queryTree", svcRequest);
                    boolean result = svcResponse.isSuccess();
                    if (result) {
                        XmlBean treeBean = svcResponse.getBeanByPath("Response.SvcCont.Role");
                        int count = treeBean.getListNum("Role.Node");
                        for (int i = 0; i < count; i++) {
                            String id = treeBean.getStrValue("""Role.Node[${i}].roleCd""");
                            String name = treeBean.getStrValue("""Role.Node[${i}].roleName""");
                            roleMap.put(id, name);
                        }
                    }
                    List<XmlNode> rhtInfos = new ArrayList<XmlNode>(rhtCnt);
                    for (int i = 0; i < rhtCnt; i++) {
                        XmlBean tempBean = opResult.getBeanByPath("SysFormType.FormTypeRhts.FormTypeRht[${i}]");
                        String rhtId = tempBean.getStrValue("FormTypeRht.rhtId");
                        tempBean.setStrValue("FormTypeRht.rhtName", roleMap.get(rhtId));
                        rhtInfos.add(tempBean.getRootNode());
                    }
                    modelMap.put("rhtList", rhtInfos);
                }
            }
        } else {
            XmlBean newXmlBean = new XmlBean();
            newXmlBean.setStrValue("SysFormType.formTypeId", "");
            newXmlBean.setStrValue("SysFormType.upFormTypeId", request.getParameter("upFormTypeId"));
            modelMap.put("nodeInfo", newXmlBean.getRootNode());
        }
        return new ModelAndView("/oframe/wf/wf005/wf005TreeType", modelMap);
    }

    /**
     * 保存表单分类
     */
    public void saveFormType(HttpServletRequest request, HttpServletResponse response) {
        // 输出结果
        StringBuilder stringBuilder = new StringBuilder();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        String formTypeId = request.getParameter("formTypeId");
        String formTypeCd = request.getParameter("formTypeCd");
        String formTypeName = request.getParameter("formTypeName");
        String upFormTypeId = request.getParameter("upFormTypeId");
        String prePath = "OpData.SysFormType.";
        boolean addFlag = false;
        if (StringUtil.isEmptyOrNull(formTypeId)) {
            formTypeId = ""
            addFlag = true;
        }
        opData.setStrValue(prePath + "formTypeId", formTypeId);
        opData.setStrValue(prePath + "formTypeName", formTypeName);
        opData.setStrValue(prePath + "upFormTypeId", upFormTypeId);
        opData.setStrValue(prePath + "formTypeCd", formTypeCd);
        // 角色授权信息
        String[] rhtIdArr = request.getParameterValues("roleCd");
        int addCount = 0;
        for (int i = 0; i < rhtIdArr.length; i++) {
            String temp = rhtIdArr[i];
            if (StringUtil.isNotEmptyOrNull(temp)) {
                opData.setStrValue("OpData.SysFormType.FormTypeRhts.FormTypeRht[${addCount}].rhtType", "0");//授权类型0：角色
                opData.setStrValue("OpData.SysFormType.FormTypeRhts.FormTypeRht[${addCount++}].rhtId", temp);
            }
        }
        svcRequest.addOp("SAVE_FORM_INFO_TYPE_CMPT", opData);
        SvcResponse svcResponse = transaction(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean folderBean = svcResponse.getFirstOpRsp("SAVE_FORM_INFO_TYPE_CMPT").getBeanByPath("Operation.OpData");
            String newFormTypeId = folderBean.getStrValue("OpData.SysFormType.formTypeId");
            stringBuilder.append("""{ id: "${newFormTypeId}", typeId: "${newFormTypeId}", formTypeCd: "${
                formTypeCd
            }", pId: "${
                upFormTypeId
            }", iconSkin: "folder",  name: "${formTypeName}"}""");
        }
        String jsonStr = """data: {addFlag: ${addFlag}, node:[${stringBuilder.toString()}]}""";
        ResponseUtil.printSvcResponse(response, svcResponse, jsonStr);
    }

    /**
     * 删除表单分类
     */
    public void deleteFormType(HttpServletRequest request, HttpServletResponse response) {
        String formTypeId = request.getParameter("formTypeId");
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.entityKey", formTypeId);
        svcRequest.addOp("DELETE_FORM_TYPE_CMPT", opData);
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /**
     * 批量导出表单
     */
    public void exportForm(HttpServletRequest request, HttpServletResponse response) {
        String formIds = request.getParameter("formIds");
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.SysFormInfoDef.formId", formIds);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.addOp("REPORT_FORM_INFO_CMPT", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean exportFormBean = svcResponse.getFirstOpRsp("REPORT_FORM_INFO_CMPT").getBeanByPath("Operation.OpResult");
            if (exportFormBean != null) {
                String formPath = exportFormBean.getStrValue("OpResult.formPath");
                File exportFile = new File(formPath);

                // 文件下载输出到响应流
                ResponseUtil.downloadFile(response, null, "批量表单导出.zip", exportFile, request.getHeader("USER-AGENT"));
            }
        }
    }
    /**
     * 批量导入表单
     */
    public void saveImportForm(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        //获取传入文件
        MultipartFile localFile = super.getFile(request, "wf005ImportForms");
        String originalFileName = localFile.getOriginalFilename();
        String fileType = originalFileName.substring(originalFileName.lastIndexOf("."), originalFileName.length());
        boolean result = false;
        String errMsg = "导入文件失败！";
        String formPath = "";
        if (fileType.contains(".zip")) {
            String remoteFileName = UUID.randomUUID().toString() + fileType;
            String tempPath = StringUtil.formatFilePath("server:temp/");
            // 文件夹不存在先创建文件夹
            File folder = new File(tempPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            //临时 文件 路径
            File tempFile = new File(tempPath + remoteFileName);
            FileOutputStream outputStream = null;
            InputStream inputStream = null;
            try {
                outputStream = new FileOutputStream(tempFile);
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

            formPath = tempFile.getAbsolutePath();
            if (StringUtil.isNotEmptyOrNull(formPath)) {
                XmlBean opData = new XmlBean();
                opData.setStrValue("OpData.formPath", formPath);
                svcRequest.addOp("IMPORT_FORM_INFO_CMPT", opData);
                SvcResponse svcResponse = transaction(svcRequest);
                result = svcResponse.isSuccess();
                errMsg = svcResponse.getErrMsg();
            } else {
                errMsg = "上传文件失败！";
            }
            tempFile.delete();
        } else {
            errMsg = "文件格式不正确";
        }
        String jsonStr = """{isSuccess: ${result}, errMsg: "${errMsg}"}""";
        ResponseUtil.printAjax(response, jsonStr);
    }
    /**
     * 删除表单
     */
    public void deleteForm(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String[] formIds = request.getParameter("formIds").split(",");
        XmlBean opData = new XmlBean();
        for (int i = 0; i < formIds.length; i++) {
            opData = new XmlBean();
            opData.setStrValue("OpData.formId", formIds[i]);
            svcRequest.addOp("DELETE_FORM_INFO_CMPT", opData);
            SvcResponse svcResponse = transaction(svcRequest);
            String result = svcResponse.isSuccess();
            String errMsg = svcResponse.getErrMsg();
            String jsonStr = """{success:${result},errMsg:"${errMsg}"}""";
            ResponseUtil.printAjax(response, jsonStr);
        }
    }

    /**
     * 发布说明
     */
    public ModelAndView publishDialog(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("formId", request.getParameter("formId"));
        String formDesc = request.getParameter("formDesc");
        formDesc = java.net.URLDecoder.decode(formDesc, "utf-8");
        modelMap.put("formDesc", formDesc);
        return new ModelAndView("/oframe/wf/wf006/wf006_publish_dialog", modelMap);
    }

    /**
     * 发布表单
     */
    public void publishForm(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        String formId = request.getParameter("formId");
        String formDesc = request.getParameter("formDesc");
        opData.setStrValue("OpData.formId", formId);
        opData.setStrValue("OpData.formDesc", formDesc);
        svcRequest.addOp("DEPLOY_FORM_INFM_CMPT", opData);
        SvcResponse svcResponse = transaction(svcRequest);
        String result = svcResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg();
        String jsonStr = """{success:${result},errMsg:"${errMsg}"}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

    /** 撤销修改已发布表单 */
    public void cancelPublish(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        String formId = request.getParameter("formId");
        opData.setStrValue("OpData.formId", formId);
        svcRequest.addOp("CHANGE_FORM_STATUS_CMPT", opData);
        SvcResponse svcResponse = transaction(svcRequest);
        String result = svcResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg();
        String jsonStr = """{success:${result},errMsg:"${errMsg}"}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

    /** 导入表单 */
    public ModelAndView importData(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        //XmlBean resultXmlBean = new XmlBean();
        //获取传入文件
        MultipartFile localFile = super.getFile(request, "importFormFile");
        String originalFileName = localFile.getOriginalFilename();
        String fileType = originalFileName.substring(originalFileName.lastIndexOf("."), originalFileName.length());
        String errMsg = "";
        boolean result = false;
        if (fileType.contains(".xml")) {
            try {
                String fileText = new InputStreamReader(localFile.getInputStream(), "UTF-8").getText();
                byte[] bytes = fileText.getBytes("utf-8");
                if (bytes.length > 3 && bytes[0] == (byte) 0xEF && bytes[1] == (byte) 0xBB && bytes[2] == (byte) 0xBF) {
                    fileText = new String(bytes, 3, bytes.length - 3, "utf-8");
                }
                XmlBean opData = new XmlBean();
                XmlBean reqData = new XmlBean(fileText);
                opData.setBeanByPath("OpData", reqData);
                svcRequest = RequestUtil.getSvcRequest(request);
                svcRequest.addOp("IMPORT_FORM_INFO_CMPT", opData);
                SvcResponse svcResponse = transaction(svcRequest);
                if (svcResponse.isSuccess()) {
                    result = true;
                } else {
                    errMsg = svcResponse.getErrMsg();
                }
            } catch (Exception e) {
                log.error("导入文件读取失败,请确认文件内容是否正确", e);
                errMsg = "导入文件读取失败，请确认文件内容是否正确";
            }
        } else {
            errMsg = "文件格式不正确";
        }
        String jsonStr = """{isSuccess: ${result},errMsg:"${errMsg}"}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

    /** 初始化表单分类 tree **/
    public ModelAndView initTypeTree(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("fromOp", request.getParameter("fromOp"));
        return new ModelAndView("/oframe/wf/wf005/tree", modelMap);
    }

}
