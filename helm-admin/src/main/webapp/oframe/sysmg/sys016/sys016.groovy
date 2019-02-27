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

class sys016 extends GroovyController {

    /**
     * 初始化
     * @param request
     * @param response
     * @return
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/oframe/sysmg/sys016/sys016", modelMap)
    }

    /**
     * 导出实体
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public void export(HttpServletRequest request, HttpServletResponse response) {
        // 输入参数
        String entityName = request.getParameter("entityName");
        String prjCd = request.getParameter("prjCd");
        // 返回处理结果
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.componentName", entityName);
        opData.setStrValue("OpData.prjCd", prjCd);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.addOp("QUERY_ALL_COMPONENT_CMPT", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean entity = svcResponse.getFirstOpRsp("QUERY_ALL_COMPONENT_CMPT").getBeanByPath("Operation.OpResult.PrjComponents");
            String fileName = UUID.randomUUID().toString() + ".xml";
            String filePath = "export/" + fileName;
            File xmlFile = ServerFile.createFile("export" + File.separator, fileName);
            xmlFile.write(entity.toXML(), "UTF-8");
            // 文件下载输出到响应流
            ResponseUtil.downloadFile(response, null, "${entityName}.xml", xmlFile, request.getHeader("USER-AGENT"));
        }
    }
    /**
     * 导出实体
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public void batchExport(HttpServletRequest request, HttpServletResponse response) {
        // 输入参数
        String prjCds = request.getParameter("prjCds");
        String Ids = request.getParameter("Ids");
        // 返回处理结果
        XmlBean opData = new XmlBean();
        String[] prjCd = prjCds.split(",");
        String[] id = Ids.split(",");
        opData.setStrValue("OpData.componentNameEn", Ids);
        opData.setStrValue("OpData.prjCd", prjCds);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.addOp("QUERY_ALL_COMPONENT_CMPT", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean entity = svcResponse.getFirstOpRsp("QUERY_ALL_COMPONENT_CMPT").getBeanByPath("Operation.OpResult.PrjComponents");
            String fileName = UUID.randomUUID().toString() + ".xml";
            String filePath = "export/" + fileName;
            File xmlFile = ServerFile.createFile("export" + File.separator, fileName);
            xmlFile.write(entity.toXML(), "UTF-8");
            // 文件下载输出到响应流
            Ids = Ids.replaceAll(",", " ")
            ResponseUtil.downloadFile(response, null, "${Ids}.xml", xmlFile, request.getHeader("USER-AGENT"));
        }
    }

    //导入xml配置参数
    public ModelAndView saveImportData(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean resultXmlBean = new XmlBean();
        //获取传入文件
        MultipartFile localFile = super.getFile(request, "importComponentFile");
        String originalFileName = localFile.getOriginalFilename();
        String fileType = originalFileName.substring(originalFileName.lastIndexOf("."), originalFileName.length());
        String errMsg = "";
        boolean result = false;

        if (fileType.contains(".xml")) {
            String remoteFileName = UUID.randomUUID().toString() + fileType;
            File xmlFile = ServerFile.createFile(remoteFileName);
            /* file2String */
            StringWriter outputStream = null;
            InputStreamReader inputStream = null;
            try {
                outputStream = new StringWriter();
                inputStream = new InputStreamReader(localFile.getInputStream(), "UTF-8");
                // 保存待上传文件信息
                char[] b = new char[1024 * 5];
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
            XmlBean opdate = new XmlBean();
            XmlBean reqData = new XmlBean(outputStream.toString());
            opdate.setBeanByPath("OpData", reqData);
            svcRequest = RequestUtil.getSvcRequest(request);
            svcRequest.addOp("IMPORT_COMPONENT_CMPT", opdate);
            SvcResponse svcResponse = transaction(svcRequest);
            if (svcResponse.isSuccess()) {
                resultXmlBean = svcResponse.getFirstOpRsp("IMPORT_COMPONENT_CMPT").getBeanByPath("Operation.OpResult.UpdateInfos");
                result = true;
            } else {
                errMsg = svcResponse.getErrMsg();
            }
            xmlFile.delete();
        } else {
            errMsg = "文件格式不正确";
        }
        String res = resultXmlBean.toString();
        String jsonStr = """{isSuccess: ${result}, errMsg: "${errMsg}",resultXml:"${res}"}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

    public ModelAndView initImportResult(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        String result = request.getParameter("res");
        XmlBean res = new XmlBean(result);
        int updateAttrCount = res.getListNum("updateinfos.info");
        List<XmlNode> returnList = new ArrayList<XmlNode>();

        for (int i = 0; i < updateAttrCount; i++) {
            returnList.add(res.getBeanByPath("updateinfos.info[${i}]").getRootNode());
        }
        modelMap.put("returnList", returnList);
        return new ModelAndView("/oframe/sysmg/sys016/sys016_importResult", modelMap);
    }

    /**
     * 初始化修改界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView editView(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String componetId = request.getParameter("componetId");
        String para = request.getParameter("para");//参数为0表示新增或修改，为1表示复制
        String componentNameEn = request.getParameter("componentNameEn");
        String ComPrjCd = request.getParameter("ComPrjCd");
        if (StringUtil.isNotEmptyOrNull(componetId)) {
            XmlBean opData = new XmlBean();
            opData.setStrValue("OpData.componentNameEn", componentNameEn);
            opData.setStrValue("OpData.prjCd", ComPrjCd);
            // 调用服务查询数据
            svcRequest.addOp("QUERY_ALL_COMPONENT_CMPT", opData);
            SvcResponse svcResponse = query(svcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean prjCmtInfo = svcResponse.getFirstOpRsp("QUERY_ALL_COMPONENT_CMPT").getBeanByPath("Operation.OpResult.PrjComponents");
                if (prjCmtInfo != null) {
                    modelMap.put("prjComponent", prjCmtInfo.getRootNode());
                }
            }
        }
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.entityName", "CmpPrj");
        opData.setStrValue("OpData.ResultFields.fieldName[0]", "prjCd");
        opData.setStrValue("OpData.ResultFields.fieldName[1]", "prjName");
        // 调用服务查询数据
        svcRequest.addOp("QUERY_ENTITY_CMPT", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean prjInfo = svcResponse.getFirstOpRsp("QUERY_ENTITY_CMPT").getBeanByPath("Operation.OpResult.PageData");
            if (prjInfo != null) {
                int count = prjInfo.getListNum("PageData.Row")
                Map map = new LinkedHashMap();
                map.put("0", "系统基础平台");
                for (int i = 0; i < count; i++) {
                    map.put(prjInfo.getStrValue("PageData.Row[" + i + "].prjCd"), prjInfo.getStrValue("PageData.Row[" + i + "].prjName"));
                }
                modelMap.put("prjCdMap", map);
            }
        }
        modelMap.put("para", para);
        return new ModelAndView("/oframe/sysmg/sys016/sys016_add", modelMap);
    }

    /**
     * 添加组件
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView add(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String componetId = request.getParameter("componetId");
        String componentName = request.getParameter("componentName");
        String componentNameEn = request.getParameter("componentNameEn");
        String prjCd = request.getParameter("prjCd");
        String executeBeforeRule = request.getParameter("executeBeforeRule");
        String executeAfterRule = request.getParameter("executeAfterRule");
        String componentNote = request.getParameter("componentNote");
        String componentNameCh = request.getParameter("componentNameCh");
        String entityName = request.getParameter("entityName");
        String oldComponentNameEn = request.getParameter("oldComponentNameEn");
        String oldPrjCd = request.getParameter("oldPrjCd");
        String para = request.getParameter("para");
        XmlBean dOpData = new XmlBean();
        String nodePath = "OpData.";
        if ("1".equals(para)) {
            //表示复制
            dOpData.setStrValue(nodePath + "EntityData.componetId", "\${componentId}");
            dOpData.setStrValue(nodePath + "EntityData.oldPrjCd", oldPrjCd);
        } else {
            if (StringUtil.isNotEmptyOrNull(componetId)) {
                //表示进行的操作是修改
                dOpData.setStrValue(nodePath + "EntityData.componetId", componetId);
                dOpData.setStrValue(nodePath + "EntityData.oldComponentNameEn", oldComponentNameEn);
                dOpData.setStrValue(nodePath + "EntityData.oldPrjCd", oldPrjCd);
            } else {
                //新增
                dOpData.setStrValue(nodePath + "EntityData.componetId", "\${componentId}");
            }
        }
        dOpData.setStrValue(nodePath + "entityName", "PrjComponent");
        dOpData.setStrValue(nodePath + "EntityData.prjCd", prjCd);
        dOpData.setStrValue(nodePath + "EntityData.componentName", componentName);
        dOpData.setStrValue(nodePath + "EntityData.componentNameEn", componentNameEn);
        dOpData.setStrValue(nodePath + "EntityData.componentNameCh", componentNameCh);
        dOpData.setStrValue(nodePath + "EntityData.entityName", entityName);
        dOpData.setStrValue(nodePath + "EntityData.componentNote", componentNote);
        dOpData.setStrValue(nodePath + "EntityData.executeBeforeRule", executeBeforeRule);
        dOpData.setStrValue(nodePath + "EntityData.executeAfterRule", executeAfterRule);//OpData.EntityData.CmptParams
        dOpData.setStrValue(nodePath + "EntityData.CmptParams", "");
        svcRequest.addOp("SAVE_COMPONENT_CMPT", dOpData);
        SvcResponse svcResponse = transaction(svcRequest);
        String result = svcResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg();
        String jsonStr = """{success:${result}, errMsg:"${errMsg}"}""";
        ResponseUtil.printAjax(response, jsonStr);
    }
    /**
     * 批量删除
     * @param request 请求参数，含entityName参数
     * @param response 响应页面
     * @return
     */
    public void deleteView(HttpServletRequest request, HttpServletResponse response) {
        RequestUtil requestUtil = new RequestUtil(request);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String componetId = requestUtil.getStrParam("componetId");
        String prjCds = requestUtil.getStrParam("prjCds");
        String enames = requestUtil.getStrParam("enames");
        SvcResponse svcResponse = null;
        if (componetId.indexOf(",") > 0) {//表明是批量删除
            String[] componetIds = componetId.split(",");
            String[] prjCd = prjCds.split(",");
            String[] ename = enames.split(",");
            for (int i = 0; i < componetIds.length; i++) {
                XmlBean opData = new XmlBean();
                opData.setStrValue("OpData.componentNameEn", ename[i]);
                opData.setStrValue("OpData.prjCd", prjCd[i]);
                opData.setStrValue("OpData.entityKey", componetIds[i]);
                svcRequest.addOp("DELETE_COMPONENT_CMPT", opData);
                svcResponse = transaction(svcRequest);
            }
        } else {
            XmlBean opData = new XmlBean();
            opData.setStrValue("OpData.componentNameEn", enames);
            opData.setStrValue("OpData.prjCd", prjCds);
            opData.setStrValue("OpData.entityKey", componetId);
            svcRequest.addOp("DELETE_COMPONENT_CMPT", opData);
            svcResponse = transaction(svcRequest);
        }
        String result = svcResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg();

        String jsonStr = """{success:${result}, errMsg:"${errMsg}"}""";
        ResponseUtil.printAjax(response, jsonStr);
    }
}


