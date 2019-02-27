import com.shfb.oframe.core.util.common.ImageUtil
import com.shfb.oframe.core.util.common.NumberUtil
import com.shfb.oframe.core.util.common.ServerFile
import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.properties.PropertiesUtil
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.util.spring.SvcUtil
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.session.SessionUtil
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import com.zhuozhengsoft.pageoffice.FileSaver
import net.sf.json.JSONArray
import net.sf.json.JSONObject
import org.springframework.ui.ModelMap
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 文件上传与下载组件
 * <p>
 * @author linql 创建 2012-3-18
 */

public class file extends GroovyController {

    /**
     * 上传文件处理
     * @param request 请求信息
     * @param response
     */
    public void upload(HttpServletRequest request, HttpServletResponse response) {
        Iterator<String> fileList = super.getFileNames(request);
        // 服务器文件存放目录
        RequestUtil requestUtil = new RequestUtil();
        // 服务器端文件名称
        List<String> fileNameList = new ArrayList<String>();
        List<String> filePathList = new ArrayList<String>();
        List<String> fieldNameList = new ArrayList<String>();
        // 请求参数
        String relType = request.getParameter("relType");
        String relId = request.getParameter("relId");
        String prjCd = request.getParameter("prjCd");
        // 文档类型
        String docTypeName = request.getParameter("docTypeName");

        // 处理传递参数
        if (StringUtil.isEmptyOrNull(prjCd)) {
            prjCd = 0;
        }

        if (StringUtil.isEmptyOrNull(docTypeName)) {
            docTypeName = "fileUpLoad"
        } else {
            docTypeName = java.net.URLDecoder.decode(docTypeName, "utf-8");
        }

        // 文件存放子目录
        String subDir = request.getParameter("subDir");
        if (StringUtil.isNotEmptyOrNull(subDir)) {
            subDir = subDir + File.separator;
        } else if (StringUtil.isNotEmptyOrNull(relId) && StringUtil.isNotEmptyOrNull(relType)) {
            subDir = relType + File.separator + relId + File.separator + docTypeName + File.separator;
        } else {
            subDir = "";
        }
        subDir = prjCd + File.separator + subDir

        if (fileList != null) {
            while (fileList.hasNext()) {
                String fieldName = fileList.next();
                List<MultipartFile> multipartFileList = super.getFiles(request, fieldName);
                // 保存上传文件信息
                // 一个上传控件多个文件进行处理
                for (int j = 0; j < multipartFileList.size(); j++) {
                    MultipartFile localFile = multipartFileList.get(j);
                    String originalFileName = localFile.getOriginalFilename();
                    String fileType = originalFileName.substring(
                            originalFileName.lastIndexOf("."), originalFileName.length());
                    String remoteFileName = subDir + File.separator + UUID.randomUUID().toString() + fileType;
                    File saveFile = ServerFile.createFile(remoteFileName);
                    FileOutputStream outputStream = null;
                    InputStream inputStream = null;
                    // 文件上传存在
                    if (!StringUtil.isEmptyOrNull(originalFileName)) {
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
                            fileNameList.add(originalFileName);
                            filePathList.add(remoteFileName);
                            fieldNameList.add(fieldName);
                        } catch (IOException e) {
                            continue;
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
                }
                // 调用服务上传文件
            }
        }
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        //
        XmlBean reqData = new XmlBean();
        for (int i = 0; i < fileNameList.size(); i++) {
            String nodePath = "SvcCont.Files.File[" + i + "].";
            reqData.setStrValue(nodePath + "docName", fileNameList.get(i));
            reqData.setStrValue(nodePath + "docPath", filePathList.get(i));
            reqData.setStrValue(nodePath + "docTypeName", docTypeName);
            reqData.setStrValue(nodePath + "prjCd", prjCd);
            reqData.setStrValue(nodePath + "relType", relType);
            reqData.setStrValue(nodePath + "relId", relId);
        }
        //
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = callService("fileService", "saveAddFile", svcRequest);
        String jsonStr = "data: [";
        if (svcResponse.isSuccess()) {
            XmlBean rspData = svcResponse.getRspData();
            int count = rspData.getListNum("SvcCont.Files.File");
            for (int i = 0; i < count; i++) {
                String docId = rspData.getStrValue("SvcCont.Files.File[$i].docId");
                String docName = rspData.getStrValue("SvcCont.Files.File[$i].docName");
                String docPath = "upload/" + rspData.getStrValue("SvcCont.Files.File[$i].docPath");
                jsonStr = jsonStr + """{\"docName\": "${docName}", \"docId\": "${docId}",\"docPath\": \"${
                    docPath
                }\"},""";
            }
            if (count > 0) {
                jsonStr = jsonStr.substring(0, jsonStr.length() - 1) + "]";
            }
        }
        ResponseUtil.printSvcResponse(response, svcResponse, jsonStr);
    }

    /**
     * 上传文件处理
     * @param request 请求信息
     * @param response
     */
    public void upbase64(HttpServletRequest request, HttpServletResponse response) {

        // 由于数据大，需要单独处理，请求参数按照字符串传递
        JSONArray requstJsonArray = request.getAttribute("requstJsonArray");
        if (requstJsonArray == null) {
            requstJsonArray = getJsonArrayFromRequest(request);
        }

        // 文件列表
        String[] fileList = getStringValues(requstJsonArray, "fileName");
        String[] upFile = getStringValues(requstJsonArray, "upFile");
        String docTypeName = getStringValue(requstJsonArray, "docTypeName");
        String relType = getStringValue(requstJsonArray, "relType");
        String relId = getStringValue(requstJsonArray, "relId");
        String prjCd = getStringValue(requstJsonArray, "prjCd");
        // 文件存放子目录
        if (StringUtil.isEmptyOrNull(prjCd)) {
            prjCd = 0;
        }
        if (StringUtil.isEmptyOrNull(docTypeName)) {
            docTypeName = "fileUpLoad"
        } else {
            docTypeName = java.net.URLDecoder.decode(docTypeName, "utf-8");
        }
        // 文件存放子目录
        String subDir = request.getParameter("subDir");
        if (StringUtil.isNotEmptyOrNull(subDir)) {
            subDir = subDir + File.separator;
        } else if (StringUtil.isNotEmptyOrNull(relId) && StringUtil.isNotEmptyOrNull(relType)) {
            subDir = relType + File.separator + relId + File.separator + docTypeName + File.separator;
        } else {
            subDir = "";
        }
        subDir = prjCd + File.separator + subDir

        // 服务器端文件名称
        List<String> fileNameList = new ArrayList<String>();
        List<String> filePathList = new ArrayList<String>();
        if (fileList != null) {
            // 保存上传文件信息
            // 一个上传控件多个文件进行处理
            for (int j = 0; j < fileList.length; j++) {
                String fileName = fileList[j];
                String upFileContext = upFile[j];
                if (StringUtil.isEmptyOrNull(fileName)) {
                    continue;
                }
                // 文档类型
                String fileType = "";
                if (fileName.lastIndexOf(".") > 0) {
                    fileType = fileName.substring(fileName.lastIndexOf("."), fileName.length());
                } else {
                    // 默认为图片类型
                    fileType = ".jpg";
                }
                // 拼接文档类型
                String remoteFileName = subDir + UUID.randomUUID().toString() + fileType;
                File saveFile = ServerFile.createFile(remoteFileName);
                FileOutputStream outputStream = null;
                // 文件上传存在
                if (!StringUtil.isEmptyOrNull(fileName)) {
                    try {
                        outputStream = new FileOutputStream(saveFile);
                        outputStream.write(StringUtil.decodeBase64(upFileContext));
                        outputStream.flush();
                        fileNameList.add(fileName);
                        filePathList.add(remoteFileName);
                    } catch (IOException e) {
                        continue;
                    } finally {
                        if (outputStream != null) {
                            try {
                                outputStream.close()
                            } catch (Exception e) {
                            }
                        }
                    }
                }
            }
        }
        // 调用服务上传文件
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean reqData = new XmlBean();
        for (int i = 0; i < fileNameList.size(); i++) {
            String nodePath = "SvcCont.Files.File[" + i + "].";
            reqData.setStrValue(nodePath + "docName", fileNameList.get(i));
            reqData.setStrValue(nodePath + "docPath", filePathList.get(i));
            reqData.setStrValue(nodePath + "docTypeName", docTypeName);
            reqData.setStrValue(nodePath + "prjCd", prjCd);
            reqData.setStrValue(nodePath + "relType", relType);
            reqData.setStrValue(nodePath + "relId", relId);
        }

        // 调用服务保存文件
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = callService("fileService", "saveAddFile", svcRequest);
        String jsonStr = "\"data\": [";
        if (svcResponse.isSuccess()) {
            XmlBean rspData = svcResponse.getRspData();
            int count = rspData.getListNum("SvcCont.Files.File");
            for (int i = 0; i < count; i++) {
                String docId = rspData.getStrValue("SvcCont.Files.File[$i].docId");
                String docName = rspData.getStrValue("SvcCont.Files.File[$i].docName");
                String docPath = "upload/" + rspData.getStrValue("SvcCont.Files.File[$i].docPath");
                JSONObject json = new JSONObject();
                json.put('docName', docName)
                json.put('docId', docId)
                json.put('docPath', docPath)
                jsonStr = jsonStr + json.toString() + ',';
            }
            if (count > 0) {
                jsonStr = jsonStr.substring(0, jsonStr.length() - 1) + "]";
            }
        }
        // 返回处理结果
        ResponseUtil.printSvcResponse(response, svcResponse, jsonStr);
    }

    /**
     * 更新文档与业务对象的关系
     * @param request
     * @param response
     */
    public String updateFileRel(HttpServletRequest request, HttpServletResponse response) {
        // 由于数据大，需要单独处理，请求参数按照字符串传递
        JSONArray requstJsonArray = getJsonArrayFromRequest(request);
        // 获取输入参数
        String[] docId = getStringValue(requstJsonArray, "docId").split(",");
        String relType = getStringValue(requstJsonArray, "relType");
        String relId = getStringValue(requstJsonArray, "relId");
        String prjCd = getStringValue(requstJsonArray, "prjCd");
        if (StringUtil.isEmptyOrNull(prjCd)) {
            prjCd = 0;
        }
        // 调用服务上传文件
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean reqData = new XmlBean();
        for (int i = 0; i < docId.length; i++) {
            String nodePath = "SvcCont.Files.File[" + i + "].";
            reqData.setStrValue(nodePath + "docId", docId[i]);
            reqData.setStrValue(nodePath + "prjCd", prjCd);
            reqData.setStrValue(nodePath + "relType", relType);
            reqData.setStrValue(nodePath + "relId", relId);
        }
        // 调用服务保存文件
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = callService("fileService", "saveUpdateFileRel", svcRequest);
        if (svcResponse.isSuccess()) {
            // 存在扫描提交的文件
            String[] fileList = getStringValues(requstJsonArray, "fileName");
            if (fileList != null && fileList.length > 0) {
                request.setAttribute("requstJsonArray", requstJsonArray);
                return "forward:/oframe/common/file/file-upbase64.gv";
            }
        }
        // 返回处理结果
        ResponseUtil.printSvcResponse(response, svcResponse, "data:[]");
    }

    /**
     * 根据文件编号下载文件
     * @param request
     * @param response
     */
    public void download(HttpServletRequest request, HttpServletResponse response) {
        String agent = request.getHeader("USER-AGENT");
        RequestUtil requestUtil = new RequestUtil(request);
        // 服务器文件存放目录
        String docId = requestUtil.getStrParam("docId");
        // 文件编号为空或者不是数字直接返回，不下载
        if (StringUtil.isEmptyOrNull(docId) || !StringUtil.isNumeric(docId)) {
            return;
        }
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        //
        XmlBean reqData = new XmlBean();
        reqData.setStrValue("SvcCont.Files.File.docId", "${docId}");
        //
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = SvcUtil.callSvc("fileService", "query", svcRequest);
        if (svcResponse.isSuccess()) {
            // 调用服务获取信息
            XmlBean rspData = svcResponse.getRspData();
            String docName = rspData.getStrValue("SvcCont.Files.File[0].docName");
            String docPath = rspData.getStrValue("SvcCont.Files.File[0].docPath");
            // 上传文件类型
            String contextType = requestUtil.getStrParam("contextType");
            // 文件上传
            // 从Ftp服务器下载文件到本地
            File localFile = ServerFile.getFile(docPath);
            // 文件下载输出到响应流
            ResponseUtil.downloadFile(response, contextType, docName, localFile, agent, false, true);
        }
    }

    /**
     * 下载服务的文档
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public void downByPath(HttpServletRequest request, HttpServletResponse response) {
        String docFileUrl = request.getParameter("docFileUrl");
        String urlType = request.getParameter("urlType");
        String agent = request.getHeader("USER-AGENT");
        docFileUrl = StringUtil.dcdNetBase64Str(docFileUrl);
        // 获取下载文件的后缀
        String docExt = docFileUrl.substring(docFileUrl.lastIndexOf("."), docFileUrl.length());
        // 生成下载文件的展示名称
        String docName = request.getParameter("docName");
        if (StringUtil.isEmptyOrNull(docName)) {
            docName = "信息报表";
        } else {
            docName = java.net.URLDecoder.decode(docName, "utf-8")
        }
        docName = docName + docExt;
        File localFile = null;
        if ("w" == urlType) {
            localFile = new File(StringUtil.formatFilePath("webroot:" + docFileUrl))
        } else if ("r" == urlType) {
            // 拿到文件名,封装成完整路径
            String cfgPath = PropertiesUtil.readPath("oframe",
                    "com.shfb.oframe.common.report.reportfactory.report.path");
            localFile = new File(StringUtil.formatFilePath(cfgPath + docFileUrl));
        } else if ("s" == urlType) {
            // 获取Upload中的文件
            localFile = ServerFile.getFile(docFileUrl);
        }
        // 文件下载输出到响应流
        ResponseUtil.downloadFile(response, "", docName, localFile, agent, false, true);
        return;
    }

    /**
     * 根据文件编号下载文件
     * @param request
     * @param response
     */
    public String downview(HttpServletRequest request, HttpServletResponse response) {
        String agent = request.getHeader("USER-AGENT");
        RequestUtil requestUtil = new RequestUtil(request);

        // 服务器文件存放目录
        String docId = requestUtil.getStrParam("docId");
        // 文件编号为空或者不是数字直接返回，不下载
        if (StringUtil.isEmptyOrNull(docId) || !StringUtil.isNumeric(docId)) {
            return;
        }
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        //
        XmlBean reqData = new XmlBean();
        reqData.setStrValue("SvcCont.Files.File.docId", "${docId}");
        //
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = SvcUtil.callSvc("fileService", "query", svcRequest);
        if (svcResponse.isSuccess()) {
            // 调用服务获取信息
            XmlBean rspData = svcResponse.getRspData();
            String docName = rspData.getStrValue("SvcCont.Files.File[0].docName");
            String docPath = rspData.getStrValue("SvcCont.Files.File[0].docPath");
            String statusCd = rspData.getStrValue("SvcCont.Files.File[0].statusCd");
            String docType = "";
            int lastDot = docName.lastIndexOf(".");
            if (lastDot > 0) {
                docType = docName.substring(lastDot + 1).toLowerCase();
            }
            if ("2".equals(statusCd)) {
                // 文件已删除
                return "forward:/oframe/themes/images/file-deleted.png";
            } else if (docType in ["jpg", "gif", "jpeg", "bmp", "png", "tif"]) {
                // 上传文件类型
                String contextType = requestUtil.getStrParam("contextType");
                // 从Ftp服务器下载文件到本地
                File localFile = ServerFile.getFile(docPath);
                // 文件下载输出到响应流
                ResponseUtil.downloadFile(response, contextType, docName, localFile, agent, true, false);
            } else {
                return "forward:/oframe/themes/images/word.png";
            }
        }
    }

    /**
     * 查看Pdf文件
     * @param request
     * @param response
     */
    public String viewPdf(HttpServletRequest request, HttpServletResponse response) {
        return "/oframe/common/file/viewPdf";
    }

    /**
     * 根据文件编号下载文件
     * @param request
     * @param response
     */
    public String downPdf(HttpServletRequest request, HttpServletResponse response) {
        String agent = request.getHeader("USER-AGENT");
        RequestUtil requestUtil = new RequestUtil(request);

        // 服务器文件存放目录
        String docId = requestUtil.getStrParam("docId");
        // 文件编号为空或者不是数字直接返回，不下载
        if (StringUtil.isEmptyOrNull(docId) || !StringUtil.isNumeric(docId)) {
            return;
        }
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        //
        XmlBean reqData = new XmlBean();
        reqData.setStrValue("SvcCont.Files.File.docId", "${docId}");
        //
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = SvcUtil.callSvc("fileService", "query", svcRequest);
        if (svcResponse.isSuccess()) {
            // 调用服务获取信息
            XmlBean rspData = svcResponse.getRspData();
            String docName = rspData.getStrValue("SvcCont.Files.File[0].docName");
            String docPath = rspData.getStrValue("SvcCont.Files.File[0].docPath");
            String docType = "";
            int lastDot = docName.lastIndexOf(".");
            if (lastDot > 0) {
                docType = docName.substring(lastDot + 1).toLowerCase();
            }
            if (docType in ["pdf"]) {
                // 上传文件类型
                String contextType = requestUtil.getStrParam("contextType");
                // 从Ftp服务器下载文件到本地
                File localFile = ServerFile.getFile(docPath);
                // 文件下载输出到响应流
                ResponseUtil.downloadFile(response, "application/pdf", docName, localFile, agent, true, true);
            }
        }
    }

    /**
     * 直接读取服务器文件信息
     * @param request 请求
     * @param response 相应
     */
    public void down(HttpServletRequest request, HttpServletResponse response) {
        String agent = request.getHeader("USER-AGENT");
        String docPath = java.net.URLDecoder.decode(request.getParameter("remoteFile"), "utf-8");
        String clientFile = "";
        if (StringUtil.isNotEmptyOrNull(request.getParameter("clientFile"))) {
            clientFile = java.net.URLDecoder.decode(request.getParameter("clientFile"), "utf-8");
        }
        //拿到文件名,封装成完整路径
        File localFile = ServerFile.getFile(docPath);
        if (!localFile.exists()) {
            localFile = new File(StringUtil.formatFilePath("webroot:" + docPath));
        }
        // 文件存在执行文件下载
        if (localFile.exists()) {
            if (StringUtil.isEmptyOrNull(clientFile)) {
                clientFile = localFile.getName();
            }
            // 文件下载输出到响应流
            ResponseUtil.downloadFile(response, null, clientFile, localFile, agent, false);
        }
    }

    /**
     * 直接读取服务器文件信息(供批量下载使用)
     * @param request 请求
     * @param response 相应
     */
    public void batchDown(HttpServletRequest request, HttpServletResponse response) {
        String agent = request.getHeader("USER-AGENT");
        String docPath = java.net.URLDecoder.decode(request.getParameter("remoteFile"), "utf-8");
        String clientFile = java.net.URLDecoder.decode(request.getParameter("clientFile"), "utf-8");
        //拿到文件名,封装成完整路径
        File localFile = ServerFile.getFile(docPath);
        // 文件下载输出到响应流
        ResponseUtil.downloadFile(response, "contextType", clientFile, localFile, agent);
        localFile.delete();
    }

    /**
     * 删除FTP文件
     * @param request 请求组件
     * @param response 响应组件
     */
    public void delete(HttpServletRequest request, HttpServletResponse response) {
        RequestUtil requestUtil = new RequestUtil(request);
        // 服务器文件存放目录
        String docId = requestUtil.getStrParam("docId");
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        //
        XmlBean reqData = new XmlBean();
        reqData.setStrValue("SvcCont.Files.File.docId", "${docId}");

        //
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = SvcUtil.callSvc("fileService", "query", svcRequest);
        if (svcResponse.isSuccess()) {
            // 调用服务获取信息
            XmlBean rspData = svcResponse.getRspData();
            String docPath = rspData.getStrValue("SvcCont.Files.File[0].docPath");
            reqData.setStrValue("SvcCont.Files.File.docPath", "deleted" + File.separator + docPath);
            svcResponse = SvcUtil.callSvc("fileService", "delete", svcRequest);
            if (svcResponse.isSuccess()) {
                // 删除本地文件
                File currentFile = ServerFile.getFile(docPath);
                File newFile = ServerFile.createFile("deleted" + File.separator + docPath);
                currentFile.renameTo(newFile);
            }
        }
        // 删除服务器上的文件
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

    /**
     * 初始化文件上传界面
     * @param request
     * @param response
     * @return
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String type = request.getParameter("type");
        String docTypeName = request.getParameter("docTypeName");
        String docTypeCd = request.getParameter("docTypeCd");
        String docRelType = request.getParameter("docRelType")
        String prjCd = request.getParameter("prjCd");
        if (StringUtil.isEmptyOrNull(prjCd)) {
            prjCd = "0";
        }
        if (!StringUtil.isEmptyOrNull(docTypeName)) {
            docTypeName = java.net.URLDecoder.decode(docTypeName, "utf-8");
        }
        // 参数类型获取编码
        if (StringUtil.isNotEmptyOrNull(docTypeCd)) {
            Map<String, String> docRelDef = getCfgCollection(request, docRelType, true, NumberUtil.getIntFromObj(prjCd));
            if (docRelDef.containsKey(docTypeCd)) {
                docTypeName = docRelDef.get(docTypeCd);
            }
        }

        // 信息读写标志
        String editAble = request.getParameter("editAble");
        if (StringUtil.isEmptyOrNull(editAble)) {
            modelMap.put("editAble", true);
        } else {
            modelMap.put("editAble", Boolean.valueOf(editAble));
        }

        // 上传文件类型控制
        if (StringUtil.isEmptyOrNull(type)) {
            type = "*";
        }
        modelMap.put("type", type);
        modelMap.put("docTypeName", StringUtil.isEmptyOrNull(docTypeName) ? "fileUpLoad" : docTypeName);

        // 默认值处理
        String subDir = request.getParameter("subDir");
        String relType = StringUtil.obj2Str(request.getParameter("relType"));
        String relId = StringUtil.obj2Str(request.getParameter("relId"));

        // 增加上传的关联类型
        modelMap.put("relType", relType);
        modelMap.put("relId", relId);
        modelMap.put("subDir", subDir);

        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 指定了关联对象ID和关联对象类型
        boolean hasRels = false;
        boolean needQuery = false;
        // 确定了关联类型和关联对象，查询已有图片
        if (StringUtil.isNotEmptyOrNull(relId) && StringUtil.isNotEmptyOrNull(relType)) {
            // 存在关联
            hasRels = true;
            // 获取可用房源区域列表
            XmlBean opData = new XmlBean();
            String prePath = "OpData.";
            opData.setStrValue(prePath + "entityName", "SvcDocInfo");
            opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "relType");
            opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", relType);
            opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");

            opData.setStrValue(prePath + "Conditions.Condition[1].fieldName", "relId");
            opData.setStrValue(prePath + "Conditions.Condition[1].fieldValue", relId);
            opData.setStrValue(prePath + "Conditions.Condition[1].operation", "=");

            opData.setStrValue(prePath + "Conditions.Condition[2].fieldName", "statusCd");
            opData.setStrValue(prePath + "Conditions.Condition[2].fieldValue", "1");
            opData.setStrValue(prePath + "Conditions.Condition[2].operation", "=");

            opData.setStrValue(prePath + "Conditions.Condition[3].fieldName", "prjCd");
            opData.setStrValue(prePath + "Conditions.Condition[3].fieldValue", prjCd);
            opData.setStrValue(prePath + "Conditions.Condition[3].operation", "=");

            // 需要查询的字段
            opData.setStrValue(prePath + "ResultFields.fieldName[0]", "docId");
            opData.setStrValue(prePath + "ResultFields.fieldName[1]", "docName");
            opData.setStrValue(prePath + "ResultFields.fieldName[2]", "docTypeName");
            // 增加查询
            needQuery = true;
            svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
            // 查询本次选择的文件信息
        }
        // 获取上传的文件列表
        String docIds = request.getParameter("docIds");
        if (StringUtil.isEqual("undefined", docIds)) {
            docIds = "";
        }
        // 指定了关联的文档ID
        boolean hasDocs = false;
        if (StringUtil.isNotEmptyOrNull(docIds)) {
            // 调用服务获取文档信息
            hasDocs = true;
            // 处理结果
            XmlBean opData = new XmlBean();
            String prePath = "OpData.";
            opData.setStrValue(prePath + "entityName", "SvcDocInfo");
            // 增加等级
            opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "docId");
            opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", "(${docIds})");
            opData.setStrValue(prePath + "Conditions.Condition[0].operation", "in");
            opData.setStrValue(prePath + "Conditions.Condition[1].fieldName", "statusCd");
            opData.setStrValue(prePath + "Conditions.Condition[1].fieldValue", "1");
            opData.setStrValue(prePath + "Conditions.Condition[1].operation", "=");

            /*需要查询的字段*/
            opData.setStrValue(prePath + "ResultFields.fieldName[0]", "docId");
            opData.setStrValue(prePath + "ResultFields.fieldName[1]", "docName");
            opData.setStrValue(prePath + "ResultFields.fieldName[2]", "docPath");
            // 增加查询数据
            needQuery = true;
            svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
        }
        // 查询关联对象的文件及关联ID的文档
        if (!needQuery) {
            // 返回附件上传界面
            Map<String, String> typeMap = new LinkedHashMap<String, String>();
            modelMap.put("typeMap", typeMap);
            return new ModelAndView("/oframe/common/file/file", modelMap);
        }
        SvcResponse svcResponse = query(svcRequest);
        Map<String, String> typeMap = new LinkedHashMap<String, String>();
        if (svcResponse.isSuccess()) {
            List<XmlBean> allRsp = svcResponse.getAllOpRsp("QUERY_ENTITY_PAGE_DATA");
            // 根据关系类型查
            if (hasRels) {
                XmlBean xmlBean = null;
                if (allRsp.size() > 1) {
                    xmlBean = allRsp.get(0);
                } else {
                    xmlBean = allRsp.get(0);
                }
                XmlBean queryResult = xmlBean.getBeanByPath("Operation.OpResult");
                if (queryResult != null) {
                    int rowCount = queryResult.getListNum("OpResult.PageData.Row");
                    List<Map<String, String>> allDocResult = new ArrayList<Map<String, String>>();
                    for (int i = 0; i < rowCount; i++) {
                        String tempField01 = queryResult.getStrValue("OpResult.PageData.Row[${i}].docId");
                        String tempField02 = queryResult.getStrValue("OpResult.PageData.Row[${i}].docTypeName");
                        String tempField03 = queryResult.getStrValue("OpResult.PageData.Row[${i}].docName");
                        if ("fileUpLoad".equals(tempField02)) {
                            tempField02 = "其他";
                        }
                        typeMap.put(tempField02, tempField02);
                        //
                        allDocResult.add(["docId": tempField01, "docTypeName": tempField02, "docName": tempField03])
                    }
                    modelMap.put("hasRels", hasRels);
                    modelMap.put("allDocResult", allDocResult);
                }
            }
            // 根据文档ID查询
            if (hasDocs) {
                XmlBean xmlBean = null;
                if (allRsp.size() > 1) {
                    xmlBean = allRsp.get(1);
                } else {
                    xmlBean = allRsp.get(0);
                }
                XmlBean queryResult = xmlBean.getBeanByPath("Operation.OpResult");
                // 处理查询结果，返回json格式数据
                if (queryResult != null) {
                    int rowCount = queryResult.getListNum("OpResult.PageData.Row");
                    List<Map<String, String>> result = new ArrayList<Map<String, String>>();
                    for (int i = 0; i < rowCount; i++) {
                        String tempField01 = queryResult.getStrValue("OpResult.PageData.Row[${i}].docId");
                        String tempField02 = queryResult.getStrValue("OpResult.PageData.Row[${i}].docName");
                        String tempField03 = queryResult.getStrValue("OpResult.PageData.Row[${i}].docPath");
                        Map<String, String> temp = ["docId": tempField01, "docName": tempField02, "docPath": tempField03];
                        result.add(temp);
                    }
                    modelMap.put("upLoadDocs", result);
                }
            } else {
                //指定了上传分类类型，为指定关联的ID按照分类上传文件的方式处理
                XmlBean xmlBean = null;
                if (allRsp.size() > 1) {
                    xmlBean = allRsp.get(1);
                } else {
                    xmlBean = allRsp.get(0);
                }
                XmlBean queryResult = xmlBean.getBeanByPath("Operation.OpResult");
                // 处理查询结果，返回json格式数据
                if (queryResult != null) {
                    int rowCount = queryResult.getListNum("OpResult.PageData.Row");
                    List<Map<String, String>> result = new ArrayList<Map<String, String>>();
                    for (int i = 0; i < rowCount; i++) {
                        String tempField01 = queryResult.getStrValue("OpResult.PageData.Row[${i}].docId");
                        String tempField02 = queryResult.getStrValue("OpResult.PageData.Row[${i}].docName");
                        String tempField03 = queryResult.getStrValue("OpResult.PageData.Row[${i}].docPath");
                        String tempField04 = queryResult.getStrValue("OpResult.PageData.Row[${i}].docTypeName");
                        if (StringUtil.isEqual(tempField04, docTypeName)) {
                            Map<String, String> temp = ["docId": tempField01, "docName": tempField02, "docPath": tempField03];
                            result.add(temp);
                        }
                    }
                    modelMap.put("upLoadDocs", result);
                }
            }
        }
        // 返回附件上传界面
        modelMap.put("typeMap", typeMap);
        return new ModelAndView("/oframe/common/file/file", modelMap);
    }

    /**
     * 初始化扫描页面
     * @param request
     * @param response
     * @return
     */
    public ModelAndView scan(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String type = request.getParameter("type");
        String docTypeName = request.getParameter("docTypeName");
        if (StringUtil.isEmptyOrNull(type)) {
            type = "*";
        }
        if (StringUtil.isEmptyOrNull(docTypeName)) {
            docTypeName = "fileUpLoad";
        }
        docTypeName = java.net.URLDecoder.decode(docTypeName, "utf-8");

        modelMap.put("type", type);
        modelMap.put("docTypeName", docTypeName);

        // 增加上传的关联类型
        modelMap.put("relType", request.getParameter("relType"));
        modelMap.put("relId", request.getParameter("relId"));
        // 上传子目录
        modelMap.put("subDir", request.getParameter("subDir"));
        return new ModelAndView("/oframe/common/file/scan", modelMap);
    }

    /**
     * 文件浏览
     * @param request
     * @param response
     * @return
     */
    public ModelAndView view(HttpServletRequest request, HttpServletResponse response) {
        // 输出业务
        ModelMap modelMap = new ModelMap();
        String docIds = request.getParameter("docIds");
        //删除附件权限
        String delFjRht = request.getParameter("delFjRht");
        if(StringUtil.isEmptyOrNull(delFjRht)){
            delFjRht="edit_tt_hs_file_rht";
        }
        modelMap.put("delFjRht", delFjRht);
        String canDelStr = request.getParameter("canDel");
        boolean canDel = true;
        if ("false".equals(canDelStr)) {
            modelMap.put("canDel", false);
        } else {
            modelMap.put("canDel", true);
        }
        // 查看图片的路径
        String imgUrl = "oframe/common/file/file-downview.gv?docId=";
        String paramImgUrl = request.getParameter("imgBaseUrl");
        if (StringUtil.isNotEmptyOrNull(paramImgUrl)) {
            imgUrl = paramImgUrl;
        }
        modelMap.put("imgBaseUrl", imgUrl);
        // 背景颜色
        String background = request.getParameter("background");
        if (StringUtil.isEmptyOrNull(background)) {
            background = "#1C1C1C"
        }
        modelMap.put("background", background);


        if (StringUtil.isEmptyOrNull(docIds)) {
            return;
        } else {
            /* 根据docdIds获取到都docIds对应的文件名称 */
            XmlBean opData = new XmlBean();
            String prePath = "OpData.";
            opData.setStrValue(prePath + "entityName", "SvcDocInfo");
            // 增加等级
            opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "docId");
            opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", "(${docIds})");
            opData.setStrValue(prePath + "Conditions.Condition[0].operation", "in");
            opData.setStrValue(prePath + "Conditions.Condition[1].fieldName", "statusCd");
            opData.setStrValue(prePath + "Conditions.Condition[1].fieldValue", "1");
            opData.setStrValue(prePath + "Conditions.Condition[1].operation", "=");

            /*需要查询的字段*/
            opData.setStrValue(prePath + "ResultFields.fieldName[0]", "docId");
            opData.setStrValue(prePath + "ResultFields.fieldName[1]", "docName");
            opData.setStrValue(prePath + "ResultFields.fieldName[2]", "docPath");
            // 增加查询数据
            SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
            svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
            // 调用服务获取文件名称
            SvcResponse svcResponse = query(svcRequest);

            List<String> canUseDocIds = new ArrayList<String>();
            List<String> canUseDocNames = new ArrayList<String>();
            //
            if (svcResponse.isSuccess()) {
                XmlBean queryResult = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult");
                if (queryResult != null) {
                    int rowCount = queryResult.getListNum("OpResult.PageData.Row");
                    for (int i = 0; i < rowCount; i++) {
                        canUseDocIds.add(queryResult.getStrValue("OpResult.PageData.Row[${i}].docId"));
                        canUseDocNames.add(queryResult.getStrValue("OpResult.PageData.Row[${i}].docName"));
                    }
                }
            }
            // 没有状态可用的文件
            if (canUseDocIds.size() == 0) {
                return;
            }
            //
            String currentDocId = request.getParameter("currentDocId");
            if (StringUtil.isEmptyOrNull(currentDocId)) {
                currentDocId = canUseDocIds.get(0);
            }
            int currentIdx = canUseDocIds.indexOf(currentDocId);
            modelMap.put("docIds", canUseDocIds.join(","));
            modelMap.put("docNames", canUseDocNames.join(","));
            modelMap.put("currentIdx", currentIdx);
            modelMap.put("totalCount", canUseDocIds.size());
            modelMap.put("currentDocId", currentDocId);
            modelMap.put("currentDocName", canUseDocNames.get(currentIdx));
            String dialogFlag = request.getParameter("dialogFlag");
            if ("true".equals(dialogFlag)) {
                return new ModelAndView("/oframe/common/file/viewNew", modelMap);
            } else {
                return new ModelAndView("/oframe/common/file/view", modelMap);
            }
        }
    }

    /**
     * 单独文件上传
     * @param request
     * @param response
     * @return
     */
    public ModelAndView uploadFile(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String docIds = request.getParameter("docIds");
        String type = request.getParameter("type");
        String editAble = request.getParameter("editAble");
        String typeItemCd = request.getParameter("typeItemCd");
        if (StringUtil.isEmptyOrNull(typeItemCd)) {
            typeItemCd = "HS_DOC_REL_TYPE";
        }
        modelMap.put("typeItemCd", typeItemCd);
        // 文档类型
        String docTypeName = request.getParameter("docTypeName");
        if (StringUtil.isEmptyOrNull(docTypeName)) {
            docTypeName = "fileUpLoad"
        } else {
            docTypeName = java.net.URLDecoder.decode(docTypeName, "utf-8");
        }
        if (StringUtil.isEmptyOrNull(type)) {
            type = "*";
        }
        if (StringUtil.isEmptyOrNull(editAble)) {
            modelMap.put("editAble", true);
        } else {
            modelMap.put("editAble", Boolean.valueOf(editAble));
        }
        // 增加上传的关联类型
        modelMap.put("relType", request.getParameter("relType"));
        modelMap.put("relId", request.getParameter("relId"));
        modelMap.put("docIds", docIds);
        modelMap.put("docType", type);
        modelMap.put("docTypeName", docTypeName);
        return new ModelAndView("/oframe/common/file/uploadFile", modelMap);
    }

    /**
     * 打开文档编辑
     * @param request 请求信息流
     * @param response 处理结果流
     * @return 业务数据
     */
    public ModelAndView openDocEditIframe(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("docId", request.getParameter("docId"));
        modelMap.put("prjCd", request.getParameter("prjCd"));
        modelMap.put("isEditable", request.getParameter("isEditable"));
        return new ModelAndView("/oframe/common/file/doc_iframe", modelMap);
    }

    /**
     * 打开文档编辑
     * @param request 请求信息流
     * @param response 处理结果流
     * @return 业务数据
     */
    public ModelAndView openDocEdit(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("prjCd", request.getParameter("prjCd"));
        RequestUtil requestUtil = new RequestUtil(request);
        // 服务器文件存放目录
        String docId = requestUtil.getStrParam("docId");
        // 文件编号为空或者不是数字直接返回，不下载
        if (StringUtil.isEmptyOrNull(docId) || !StringUtil.isNumeric(docId)) {
            return;
        }
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 查询文档信息
        XmlBean reqData = new XmlBean();
        reqData.setStrValue("SvcCont.Files.File.docId", "${docId}");
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = SvcUtil.callSvc("fileService", "query", svcRequest);
        if (svcResponse.isSuccess()) {
            // 文档编号
            modelMap.put("docId", request.getParameter("docId"));
            // 调用服务获取信息
            XmlBean rspData = svcResponse.getRspData();
            String docName = rspData.getStrValue("SvcCont.Files.File[0].docName");
            // 文档名称
            modelMap.put("docName", docName);
            // 操作人名称
            SessionUtil sessionUtil = new SessionUtil(request);
            String loginInfo = sessionUtil.getString(SessionUtil.LOGIN_IN_SESSION_KEY);
            XmlBean loginBean = new XmlBean(loginInfo);
            modelMap.put("staffName", loginBean.getStrValue("LoginInfo.staffName"));
            // 查询员工是否有文档修改权限
            svcRequest = RequestUtil.getSvcRequest(request);
        }
        // 是否有文档修改权限
        modelMap.put("isEditable", request.getParameter("isEditable"));
        return new ModelAndView("/oframe/common/file/doc_edit", modelMap);
    }

    /**
     * 打开文档编辑
     * @param request 请求信息流
     * @param response 处理结果流
     * @return 业务数据
     */
    public void saveEditFile(HttpServletRequest request, HttpServletResponse response) {
        RequestUtil requestUtil = new RequestUtil(request);
        // 服务器文件存放目录
        String docId = requestUtil.getStrParam("docId");
        // 文件编号为空或者不是数字直接返回，不下载
        if (StringUtil.isEmptyOrNull(docId) || !StringUtil.isNumeric(docId)) {
            return;
        }
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        //
        XmlBean reqData = new XmlBean();
        reqData.setStrValue("SvcCont.Files.File.docId", "${docId}");
        //
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = callService("fileService", "query", svcRequest);
        if (svcResponse.isSuccess()) {
            // 调用服务获取信息
            XmlBean rspData = svcResponse.getRspData();
            String docName = rspData.getStrValue("SvcCont.Files.File[0].docName");
            String docPath = rspData.getStrValue("SvcCont.Files.File[0].docPath");
            // 上传文件类型
            String contextType = requestUtil.getStrParam("contextType");
            // 文件上传
            // 从Ftp服务器下载文件到本地
            File localFile = ServerFile.getFile(docPath);
            // 保存上传的文件
            FileSaver fs = new FileSaver(request, response);
            fs.saveToFile(localFile.getAbsolutePath());
            fs.close();
        }
    }

    /**
     * 通过XXX文档控件提交修改的内容
     * @param request 请求信息流
     * @param response 处理结果流
     * @return 业务数据
     */
    public void saveUploadedFile(HttpServletRequest request, HttpServletResponse response) {
        RequestUtil requestUtil = new RequestUtil(request);
        // 服务器文件存放目录
        String docId = requestUtil.getStrParam("docId");
        System.out.print(docId);
        // 文件编号为空或者不是数字直接返回，不下载
        if (StringUtil.isEmptyOrNull(docId) || !StringUtil.isNumeric(docId)) {
            return;
        }
        // 根据docId查询文件在服务器的存放地址
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean reqData = new XmlBean();
        reqData.setStrValue("SvcCont.Files.File.docId", "${docId}");
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = callService("fileService", "query", svcRequest);
        if (svcResponse.isSuccess()) {
            // 调用服务获取信息
            XmlBean rspData = svcResponse.getRspData();
            String docPath = rspData.getStrValue("SvcCont.Files.File[0].docPath");

            // 将上传的文件保存到docId对应的服务文件中
            Iterator<String> fileList = super.getFileNames(request);
            if (fileList != null && fileList.hasNext()) {
                String fieldName = fileList.next();
                List<MultipartFile> multipartFileList = super.getFiles(request, fieldName);
                MultipartFile uploadTempFile = multipartFileList.get(0); ;
                OutputStream outputStream = null;
                InputStream inputStream = null;
                try {
                    File tempFile = ServerFile.getFile(docPath);
                    outputStream = new FileOutputStream(tempFile);
                    inputStream = uploadTempFile.getInputStream();
                    // 保存待上传文件信息
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = inputStream.read(b)) != -1) {
                        outputStream.write(b, 0, len);
                    }
                    outputStream.flush();
                    response.getWriter().write("OK\0");//代码执行完成了发送一个标志，SaveToURL就能获取
                } catch (IOException e) {
                    e.printStackTrace();
                    // TODO: 异常处理
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
        }
    }

    /**
     * 根据类型加载附件信息
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public void docsInfo(HttpServletRequest request, HttpServletResponse response) {
        String docIds = request.getParameter("docIds");
        String docTypeName = request.getParameter("docTypeName");
        String relId = request.getParameter("relId");
        String relType = request.getParameter("relType");
        //查询指定房产的 交房信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        //查询 交房确认单 附件
        svcRequest = RequestUtil.getSvcRequest(request);
        // 获取可用房源区域列表
        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        opData.setStrValue(prePath + "entityName", "SvcDocInfo");
        int addCount = -1;
        if (StringUtil.isNotEmptyOrNull(relType)) {
            opData.setStrValue(prePath + "Conditions.Condition[${++addCount}].fieldName", "relType");
            opData.setStrValue(prePath + "Conditions.Condition[${addCount}].fieldValue", relType);
            opData.setStrValue(prePath + "Conditions.Condition[${addCount}].operation", "=");
        }
        if (StringUtil.isNotEmptyOrNull(relId)) {
            opData.setStrValue(prePath + "Conditions.Condition[${addCount++}].fieldName", "relId");
            opData.setStrValue(prePath + "Conditions.Condition[${addCount}].fieldValue", relId);
            opData.setStrValue(prePath + "Conditions.Condition[${addCount}].operation", "=");
        }

        if (StringUtil.isNotEmptyOrNull(docTypeName)) {
            opData.setStrValue(prePath + "Conditions.Condition[${++addCount}].fieldName", "docTypeName");
            opData.setStrValue(prePath + "Conditions.Condition[${addCount}].fieldValue", docTypeName);
            opData.setStrValue(prePath + "Conditions.Condition[${addCount}].operation", "=");
        }
        if (StringUtil.isNotEmptyOrNull(docIds)) {
            opData.setStrValue(prePath + "Conditions.Condition[${++addCount}].fieldName", "docId");
            opData.setStrValue(prePath + "Conditions.Condition[${addCount}].fieldValue", "(" + docIds + ")");
            opData.setStrValue(prePath + "Conditions.Condition[${addCount}].operation", "in");
        }
        // 只展示有效的附件
        opData.setStrValue(prePath + "Conditions.Condition[${++addCount}].fieldName", "statusCd");
        opData.setStrValue(prePath + "Conditions.Condition[${addCount}].fieldValue", "1");
        opData.setStrValue(prePath + "Conditions.Condition[${addCount}].operation", "=");

        // 需要查询的字段
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "docId");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "docTypeName");
        opData.setStrValue(prePath + "ResultFields.fieldName[2]", "docName");

        XmlBean pageData = null;
        if (addCount > 0) {
            // 增加查询
            svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
            SvcResponse svcRsp = query(svcRequest);
            //返回处理结果
            if (svcRsp.isSuccess()) {
                pageData = svcRsp.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA")
                        .getBeanByPath("Operation.OpResult.PageData");
            }
        }
        if (pageData == null) {
            pageData = new XmlBean()
        }
        ResponseUtil.print(response, pageData.toJson());
    }

    /**
     * 打印附件
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public ModelAndView print(HttpServletRequest request, HttpServletResponse response) {
        String docId = request.getParameter("docId");
        // 服务器文件存放目录
        // 文件编号为空或者不是数字直接返回，不下载
        if (StringUtil.isEmptyOrNull(docId) || !StringUtil.isNumeric(docId)) {
            return;
        }
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 查询文档信息
        XmlBean reqData = new XmlBean();
        reqData.setStrValue("SvcCont.Files.File.docId", "${docId}");
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = SvcUtil.callSvc("fileService", "query", svcRequest);
        if (svcResponse.isSuccess()) {
            // 调用服务获取信息
            XmlBean rspData = svcResponse.getRspData();
            String docName = rspData.getStrValue("SvcCont.Files.File[0].docName");
            String statusCd = rspData.getStrValue("SvcCont.Files.File[0].statusCd");
            // 文档名称
            docName = docName.toLowerCase();
            String docType = "";
            int lastDot = docName.lastIndexOf(".");
            if (lastDot > 0) {
                docType = docName.substring(lastDot + 1).toLowerCase();
            }
            if ("2".equals(statusCd)) {
                return null;
            } else if (docType in ["jpg", "gif", "jpeg", "bmp", "png", "tif"]) {
                // 返回图片打印界面
                ModelMap modelMap = new ModelMap();
                modelMap.put("docId", docId);
                return new ModelAndView("/oframe/common/file/print_img", modelMap);
            } else if (docType in ["doc", "docx", "xls", "xlsx", "ppt", "pptx"]) {
                // 返回图片打印界面
                ModelMap modelMap = new ModelMap();
                modelMap.put("docId", docId);
                modelMap.put("isEditable", false);
                return new ModelAndView("forward:/oframe/common/file/file-openDocEdit.gv", modelMap);
            } else {
                // 返回图片打印界面
                ModelMap modelMap = new ModelMap();
                modelMap.put("docId", docId);
                return new ModelAndView("/oframe/common/file/print_not_support", modelMap);
            }
        }
    }

    /**
     * 根据文件编号下载文件
     * @param request
     * @param response
     */
    public void rotate(HttpServletRequest request, HttpServletResponse response) {
        RequestUtil requestUtil = new RequestUtil(request);
        // 服务器文件存放目录
        String docId = requestUtil.getStrParam("docId");
        String rotate = requestUtil.getStrParam("rotate");
        if (!StringUtil.isNumeric(rotate)) {
            return;
        }
        // 文件编号为空或者不是数字直接返回，不下载
        if (StringUtil.isEmptyOrNull(docId) || !StringUtil.isNumeric(docId)) {
            return;
        }
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        //
        XmlBean reqData = new XmlBean();
        reqData.setStrValue("SvcCont.Files.File.docId", "${docId}");
        //
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = SvcUtil.callSvc("fileService", "query", svcRequest);
        if (svcResponse.isSuccess()) {
            // 调用服务获取信息
            XmlBean rspData = svcResponse.getRspData();
            String docName = rspData.getStrValue("SvcCont.Files.File[0].docName");
            String docPath = rspData.getStrValue("SvcCont.Files.File[0].docPath");
            String statusCd = rspData.getStrValue("SvcCont.Files.File[0].statusCd");
            String docType = "";
            int lastDot = docName.lastIndexOf(".");
            if (lastDot > 0) {
                docType = docName.substring(lastDot + 1).toLowerCase();
            }
            if ("2" != statusCd && docType in ["jpg", "gif", "jpeg", "bmp", "png", "tif"]) {
                // 从Ftp服务器下载文件到本地
                File localFile = ServerFile.getFile(docPath);
                try {
                    ImageUtil.rotate(localFile.getPath(), NumberUtil.getDoubleFromObj(rotate), localFile.getPath());
                    ResponseUtil.print(response, """{"success": true, "errMsg": ""}""");
                } catch (Exception e) {
                    log.error("图片旋转失败", e);
                    ResponseUtil.print(response, """{"success": false, "errMsg": "图片旋转失败"}""");
                }
            }
        }
    }

    /**
     * 获取卓正控件
     * @param request
     * @param response
     */
    public String zzTag(HttpServletRequest request, HttpServletResponse response) {
        return "/oframe/common/file/zzFileTag"
    }

    /**
     * 将参数转换为json数组对象
     * @param request
     * @return
     */
    private JSONArray getJsonArrayFromRequest(HttpServletRequest request) {
        InputStreamReader isr = new InputStreamReader(request.getInputStream(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        StringBuilder stringBuilder = new StringBuilder(1024);
        String tstr = br.readLine();
        while (tstr != null) {
            stringBuilder.append(tstr);
            stringBuilder.append("\n");
            tstr = br.readLine();
        }
        String tempString = stringBuilder.toString();
        if (tempString.startsWith("[")) {
            JSONArray jsonArray = JSONArray.fromObject(tempString);
            return jsonArray;
        } else {
            JSONArray jsonArray = new JSONArray();
            jsonArray.add(JSONObject.fromObject(tempString));
            return jsonArray;
        }
    }

    /**
     * 从请求的json报文中获取第一个参数
     * @param jsonArray
     * @param paramName
     * @return
     */
    private String getStringValue(JSONArray jsonArray, String paramName) {
        if (jsonArray == null) {
            return "";
        } else {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.get(i);
                if (StringUtil.isEqual(paramName, jsonObject.get("name"))) {
                    return jsonObject.optString("value", "");
                }
            }
        }
        return "";
    }

    /**
     * 从请求的json报文中获取参数数据
     * @param jsonArray
     * @param paramName
     * @return
     */
    private String[] getStringValues(JSONArray jsonArray, String paramName) {
        List<String> tempList = new ArrayList<String>();
        if (jsonArray == null) {
            return "";
        } else {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.get(i);
                if (StringUtil.isEqual(paramName, jsonObject.get("name"))) {
                    tempList.add(jsonObject.optString("value", ""));
                }
            }
        }
        // 返回处理结果
        String[] result = new String[tempList.size()];
        tempList.toArray(result);
        return result;
    }

}