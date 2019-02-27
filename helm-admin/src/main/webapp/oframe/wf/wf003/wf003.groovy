import com.shfb.oframe.core.util.common.ServerFile
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
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by Administrator on 2015/12/2.
 */
class wf003 extends GroovyController {
    /**
     * 初始化界面
     * @param request 请求信息
     * @param response 响应结果
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/oframe/wf/wf003/wf003", modelMap);
    }

    /**
     * 导出流程
     */
    public void exportLc(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String id = request.getParameter("ids");
        String flag = request.getParameter("flag");
        String cmptName = "BATCH_EXPORT_MODEL_ZIP";
        String nodeId = "modelIds";
        if (StringUtil.isEqual(flag, "publish")) {
            cmptName = "BATCH_EXPORT_PROC_DEF_ZIP";
            nodeId = "procDefIds";
        }
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData." + nodeId, id);
        svcRequest.addOp(cmptName, opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean queryResult = svcResponse.getFirstOpRsp(cmptName).getBeanByPath("Operation.OpResult");
            String docPath = queryResult.getStrValue("OpResult.filePath");
            String fileName = queryResult.getStrValue("OpResult.fileName");
            //拿到文件名,封装成完整路径
            File localFile = ServerFile.getFile(docPath);
            // 文件下载输出到响应流
            ResponseUtil.downloadFile(response, null, fileName, localFile, request.getHeader("USER-AGENT"), false);
        }
    }

    /**
     *  发布流程
     * @param request 请求信息
     * @param response
     */
    public void doDeploy(HttpServletRequest request, HttpServletResponse response) {
        // 获取请求参数信息
        RequestUtil requestUtil = new RequestUtil(request);
        // 请求信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 获取文件
        MultipartFile localFile = super.getFile(request, "wfFile");
        // 获取文件后缀
        String originalFileName = localFile.getOriginalFilename();
        String errMsg = "";
        boolean result = false;
        File saveFile = null;
        String saveFilePath = null;
        if (StringUtil.isNotEmptyOrNull(originalFileName)) {
            String fileType = originalFileName.substring(
                    originalFileName.lastIndexOf("."), originalFileName.length());
            String remoteFileName = "temp/" + UUID.randomUUID().toString() + fileType;
            saveFile = ServerFile.createFile(remoteFileName);
            saveFilePath = saveFile.getAbsolutePath();
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
                ResponseUtil.print(response, """{"success": false, "errMsg": "文件上传失败!" }""");
                return;
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
        }
        // 签约信息保存
        XmlBean opData = new XmlBean();
        String prePath = "OpData.Process.";
        opData.setStrValue(prePath + "resourcePath", saveFilePath);
        opData.setStrValue(prePath + "name", request.getParameter("deployName"));
        opData.setStrValue(prePath + "category", request.getParameter("procDepCategory"));
        opData.setStrValue(prePath + "description", request.getParameter("description"));
        opData.setStrValue(prePath + "key", request.getParameter("key"));
        opData.setStrValue(prePath + "actTypeCd", request.getParameter("actTypeCd"));

        // 调用服务
        svcRequest.addOp("DEPLOY_DEP_PROC", opData);
        SvcResponse svcResponse = transaction(svcRequest);
        // 删除上传的文件
        if (saveFile != null) {
            saveFile.delete();
        }
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /**
     * 发布说明
     */
    public ModelAndView publishDialog(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("modelId", request.getParameter("modelId"));
        return new ModelAndView("/oframe/wf/wf003/wf003_publish_dialog", modelMap);
    }

    /**
     * 部署流程
     */
    public ModelAndView deploy(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String modelId = request.getParameter("modelId");
        String flowDesc = request.getParameter("flowDesc");
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.modelId", modelId);
        opData.setStrValue("OpData.flowDesc", flowDesc);
        svcRequest.addOp("DEPLOY_BY_MODEL", opData);
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /**
     * 批量删除流程
     */
    public void batchDelete(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String id = request.getParameter("ids");
        String flag = request.getParameter("flag");
        String nodeName = "procDefIds";
        String cmptName = "BATCH_DELETE_PROC_DEF";
        if (StringUtil.isEqual(flag, 'design')) {
            nodeName = "modelIds";
            cmptName = "BATCH_DELETE_MODEL";
        }
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData." + nodeName, id);
        svcRequest.addOp(cmptName, opData);
        SvcResponse svcResponse = transaction(svcRequest);
        String result = svcResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg();
        String jsonStr = """{success:${result},errMsg:'${errMsg}'}""";
        ResponseUtil.printAjax(response, jsonStr);
    }
    //管理工作流分类----------------------------------------------------------------------------------------------------------------------------------
    /**
     * 在线编辑查询
     * @param request 请求信息
     * @param response 响应结果
     */
    public ModelAndView editTemplate(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        //请求信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String id = request.getParameter("id");
        String wfReg = request.getParameter("wfReg");
        modelMap.put("id", id);
        modelMap.put("wfReg", wfReg);
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.id", id);
        opData.setStrValue("OpData.wfReg", wfReg);
        //调用服务
        svcRequest.addOp("QUERY_PROC_DEF_CMPT", opData);
        //启动查询
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean queryResult = svcResponse.getFirstOpRsp("QUERY_PROC_DEF_CMPT").getBeanByPath("Operation.OpResult");
            if (queryResult != null) {
                modelMap.put("procDefInfo", queryResult.getStrValue("OpResult.procDefInfo")); //内容以字符串格式取出
            }
        }
        return new ModelAndView("/oframe/wf/wf003/wf003_edit_temp", modelMap);
    }

    /** 初始化 code frame */
    public ModelAndView textCode(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        String textarea = request.getParameter("textarea");
        String content = request.getParameter("content");
        String model = request.getParameter("model");
        if (StringUtil.isEmptyOrNull(model)) {
            model = "xml";
        }
        modelMap.put("textarea", textarea);
        modelMap.put("model", model);
        modelMap.put("content", content);
        return new ModelAndView("/oframe/wf/wf003/wf003_text_code", modelMap);
    }

    /**
     * 在线编辑保存
     * @param request 请求信息
     * @param response 响应结果
     */
    public ModelAndView saveTemplateText(HttpServletRequest request, HttpServletResponse response) {
        //请求信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String id = request.getParameter("id");
        String wfReg = request.getParameter("wfReg");
        String procDefInfo = request.getParameter("procDefInfo");
        //拼接下半部分报文
        XmlBean opData = new XmlBean();
        //入参
        opData.setStrValue("OpData.id", id);
        opData.setStrValue("OpData.wfReg", wfReg);
        opData.setStrValue("OpData.procDefInfo", procDefInfo);
        //调用服务
        svcRequest.addOp("UPDATE_PROC_DEF_CMPT", opData);
        //启动查询
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");//保存
    }

    /**
     * 编辑页面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView editActType(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        String actTypeId = request.getParameter("actTypeId");
        //判断是否是新增
        if (StringUtil.isNotEmptyOrNull(actTypeId)) {
            //查询分类信息
            opData.setStrValue("OpData.entityName", "SysActType");
            opData.setStrValue("OpData.entityKey", actTypeId);
            svcRequest.addOp("QUERY_ALL_ENTITY", opData);
            SvcResponse svcResponse = query(svcRequest);
            // 获取输出结果
            if (svcResponse.isSuccess()) {
                XmlBean opResult = svcResponse.getFirstOpRsp("QUERY_ALL_ENTITY").getBeanByPath("Operation.OpResult.SysActType");
                modelMap.put("nodeInfo", opResult.getRootNode());
                int rhtCnt = opResult.getListNum("SysActType.ActTypeRhts.ActTypeRht");
                if (rhtCnt > 0) {
                    /* 处理授权组织信息, 后去组织MAP对象 */
                    Map<String, String> roleMap = new LinkedHashMap<String, String>();
                    List<Map<String, String>> mapList = new ArrayList<>();
                    svcRequest = RequestUtil.getSvcRequest(request);
                    XmlBean reqData = new XmlBean();
                    reqData.setStrValue("SvcCont.Org.prjCd", request.getParameter("prjCd"));
                    reqData.setStrValue("SvcCont.Org.orgId", "");
                    svcRequest.setReqData(reqData);
                    svcResponse = callService("orgService", "queryTree", svcRequest);
                    boolean result = svcResponse.isSuccess();
                    if (result) {
                        XmlBean treeBean = svcResponse.getBeanByPath("Response.SvcCont");
                        int count = treeBean.getListNum("SvcCont.TreeNode") - 1;
                        for (int i = 0; i < count; i++) {
                            String id = treeBean.getStrValue("""SvcCont.TreeNode[${i}].orgId""");
                            String name = treeBean.getStrValue("""SvcCont.TreeNode[${i}].orgName""");
                            roleMap.put(id, name);
                        }
                    }
                    List<XmlNode> rhtInfos = new ArrayList<XmlNode>(rhtCnt);
                    for (int i = 0; i < rhtCnt; i++) {
                        XmlBean tempBean = opResult.getBeanByPath("SysActType.ActTypeRhts.ActTypeRht[${i}]");
                        String rhtId = tempBean.getStrValue("ActTypeRht.rhtId");
                        tempBean.setStrValue("ActTypeRht.rhtName", roleMap.get(rhtId));
                        rhtInfos.add(tempBean.getRootNode());
                    }
                    modelMap.put("rhtList", rhtInfos);
                }
            }
        } else {
            XmlBean newXmlBean = new XmlBean();
            newXmlBean.setStrValue("SysActType.actTypeId", "");
            newXmlBean.setStrValue("SysActType.upActTypeId", request.getParameter("upActTypeId"));
            modelMap.put("nodeInfo", newXmlBean.getRootNode());
        }
        return new ModelAndView("/oframe/wf/wf003/wf003TreeType", modelMap);
    }

    /**
     * 新增 流程类型树，权限（如果为null，参照上级节点权限）
     * @param request 请求信息
     * @param response 响应结果
     */
    public ModelAndView saveActType(HttpServletRequest request, HttpServletResponse response) {
        StringBuilder stringBuilder = new StringBuilder();
        //请求信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String actTypeId = request.getParameter("actTypeId");//如果是null就新增、  如果有值就修改
        String upActTypeId = request.getParameter("upActTypeId");
        String actTypeName = request.getParameter("actTypeName");
        String actTypeCd = request.getParameter("actTypeCd");//分类编码
        //检查是否有特殊符号
        Matcher matcher = actTypeCd =~ /[#%^&*()+=}{'\[\]:;,\/.<>\/?~！￥…（）—+|【】‘；：”“’。，、？]/;
        boolean tempBn = matcher.find();
        if (!tempBn) {
            boolean addFlag = false;
            if (StringUtil.isEmptyOrNull(actTypeId)) {
                addFlag = true;
            }
            //拼接下半部分报文
            XmlBean opData = new XmlBean();
            //入参
            opData.setStrValue("OpData.SysActType.actTypeId", actTypeId);//如果是null就新增、  如果有值就修改
            opData.setStrValue("OpData.SysActType.upActTypeId", upActTypeId);
            opData.setStrValue("OpData.SysActType.actTypeName", actTypeName);
            opData.setStrValue("OpData.SysActType.actTypeCd", actTypeCd);
            // 组织授权信息
            String[] rhtIdArr = request.getParameterValues("orgCd");
            int addCount = 0;
            for (int i = 0; i < rhtIdArr.length; i++) {
                String rhtId = rhtIdArr[i];
                if (StringUtil.isNotEmptyOrNull(rhtId)) {
                    opData.setStrValue("OpData.SysActType.ActTypeRhts.ActTypeRht[${addCount}].rhtType", "1");//授权类型1：组织
                    opData.setStrValue("OpData.SysActType.ActTypeRhts.ActTypeRht[${addCount++}].rhtId", rhtId);
                } else {
                    opData.setStrValue("OpData.SysActType.ActTypeRhts", "");
                }
            }
            //调用服务
            svcRequest.addOp("SAVE_PROC_TREE_INFO_CMPT", opData);
            //启动查询
            SvcResponse svcResponse = transaction(svcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean folderBean = svcResponse.getFirstOpRsp("SAVE_PROC_TREE_INFO_CMPT").getBeanByPath("Operation.OpResult");
                String newActTypeId = folderBean.getStrValue("OpResult.actTypeId");
                stringBuilder.append("""{ id: "${newActTypeId}", typeId: "${newActTypeId}", actTypeCd: "${
                    actTypeCd
                }", pId: "${
                    upActTypeId
                }", iconSkin: "folder",  name: "${actTypeName}"}""");
            }
            String jsonStr = """data: {addFlag: ${addFlag}, node:[${stringBuilder.toString()}]}""";
            ResponseUtil.printSvcResponse(response, svcResponse, jsonStr);
        } else {
            //分类编码包含特殊符号（"_"不算特殊符号），不保存，并返回错误信息
            String jsonStr = """{success:${false}, errMsg:"分类编码不合法，不能包含“*,/-”等特殊符号"}""";
            ResponseUtil.printAjax(response, jsonStr);
        }
    }

    /**
     * 查询流程类型树，权限控制（输出完整的树结构）
     * @param request 请求信息
     * @param response 响应结果
     */
    public ModelAndView actTypeTree(HttpServletRequest request, HttpServletResponse response) {
        //请求信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData", "");
        //调用服务
        svcRequest.addOp("QUERY_PROC_TREE_INFO_CMPT", opData);
        //启动查询
        SvcResponse svcResponse = query(svcRequest);
        String errMsg = svcResponse.getErrMsg();
        boolean result = svcResponse.isSuccess();
        StringBuilder stringBuilder = new StringBuilder();
        if (result) {
            //查询结果
            XmlBean queryResult = svcResponse.getFirstOpRsp("QUERY_PROC_TREE_INFO_CMPT").getBeanByPath("Operation.OpResult");
            if (queryResult != null) {
                int nodeCount = queryResult.getListNum("OpResult.Node");
                for (int i = 0; i < nodeCount; i++) {
                    String actTypeId = queryResult.getStrValue("""OpResult.Node[${i}].actTypeId""");//actTypeId
                    String upActTypeId = queryResult.getStrValue("""OpResult.Node[${i}].upActTypeId""");//upActTypeId
                    String actTypeName = queryResult.getStrValue("""OpResult.Node[${i}].actTypeName""");//actTypeName
                    String actTypeCd = queryResult.getStrValue("""OpResult.Node[${i}].actTypeCd""");//actTypeCd
                    boolean open = false;
                    if (i == 0) {
                        open = true;
                    }
                    stringBuilder.append("""{ id: "${actTypeId}",typeId: "${actTypeId}", actTypeCd: "${
                        actTypeCd
                    }", pId: "${
                        upActTypeId
                    }", iconSkin: "folder",name: "${
                        actTypeName
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
     * 删除流程类型树  检查类型下是否有其它类型（流程定义）  有的话 不允许删除
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView  展示视图
     */
    public ModelAndView deleteActType(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        //取值
        String actTypeCd = request.getParameter("actTypeCd");//类型type
        XmlBean opData = new XmlBean();
        //入参
        opData.setStrValue("OpData.actTypeCd", actTypeCd);
        //调用服务
        svcRequest.addOp("DELETE_PROC_TREE_INFO_CMPT", opData);
        //启动服务
        SvcResponse svcResponse = transaction(svcRequest);
        //返回值
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    public ModelAndView orgTree(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        String orgPrjCd = request.getParameter("prjCd");
        modelMap.put("orgPrjCd", orgPrjCd);
        modelMap.put("objName", "wf003");
        modelMap.put("fromOp", "wf003");
        return new ModelAndView("/oframe/sysmg/sys004/orgTree", modelMap)
    }
}