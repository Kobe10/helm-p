import com.shfb.oframe.core.util.common.ServerFile
import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
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

class sys018 extends GroovyController {

    /**
     * 初始化
     * @param request
     * @param response
     * @return
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/oframe/sysmg/sys018/sys018", modelMap)
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
        String jobId = request.getParameter("jobId");
        String para = request.getParameter("para");//参数为0表示新增或修改，为1表示复制
        String jobCd = request.getParameter("jobCd");
        String prjCd = request.getParameter("prjCd");
        if (StringUtil.isNotEmptyOrNull(jobId)) {
            XmlBean opData = new XmlBean();
            opData.setStrValue("OpData.PrjJobDef.jobCd", jobCd);
            opData.setStrValue("OpData.PrjJobDef.prjCd", prjCd);
            // 调用服务查询数据
            svcRequest.addOp("QUERY_PRJ_JOB_DEF_CMPT", opData);
            SvcResponse svcResponse = query(svcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean prjJobInfo = svcResponse.getFirstOpRsp("QUERY_PRJ_JOB_DEF_CMPT")
                if (prjJobInfo != null) {
                    XmlBean tempNode = prjJobInfo.getBeanByPath("Operation.OpResult.PrjJobDef");
                    modelMap.put("prjJobDef", tempNode.getRootNode());
                }
            }
        }

        // 可以项目
        Map map = new LinkedHashMap();
        map.put("0", "系统基础平台");
        // 调用该服务获取所有项目
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
                for (int i = 0; i < count; i++) {
                    map.put(prjInfo.getStrValue("PageData.Row[" + i + "].prjCd"),
                            prjInfo.getStrValue("PageData.Row[" + i + "].prjName"));
                }

            }
        }
        // 推送页面端展示
        modelMap.put("prjCdMap", map);
        modelMap.put("para", para);
        return new ModelAndView("/oframe/sysmg/sys018/sys018_add", modelMap);
    }

    /**
     * 添加指标
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView add(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);

        String jobId = request.getParameter("jobId");
        String jobName = request.getParameter("jobName");
        String jobCd = request.getParameter("jobCd");
        String prjCd = request.getParameter("prjCd");
        String jobGroupName = request.getParameter("jobGroupName");
        String qryService = request.getParameter("qryService");
        String jobValueSuffix = request.getParameter("jobValueSuffix");
        String qryParam01 = request.getParameter("qryParam01");
        String qryParam02 = request.getParameter("qryParam02");
        String qryParam03 = request.getParameter("qryParam03");
        String entityName = request.getParameter("entityName");
        String exeParam09 = request.getParameter("exeParam09");
        String exeParam10 = request.getParameter("exeParam10");
        //x描述
        String exeParam07 = request.getParameter("exeParam07");
        //y描述
        String exeParam08 = request.getParameter("exeParam08");
        String jobDesc = request.getParameter("jobDesc");
        String oldJobCd = request.getParameter("oldJobCd");
        String oldPrjCd = request.getParameter("oldPrjCd");
        String para = request.getParameter("para");

        XmlBean dOpData = new XmlBean();
        String nodePath = "OpData.";
        if ("1".equals(para)) {
            //表示复制
            dOpData.setStrValue(nodePath + "PrjJobDef.jobId", "\${jobId}");
            dOpData.setStrValue(nodePath + "PrjJobDef.oldPrjCd", oldPrjCd);
        } else {
            if (StringUtil.isNotEmptyOrNull(jobId)) {
                //表示进行的操作是修改
                dOpData.setStrValue(nodePath + "PrjJobDef.jobId", jobId);

                dOpData.setStrValue(nodePath + "PrjJobDef.oldJobCd", oldJobCd);
                dOpData.setStrValue(nodePath + "PrjJobDef.oldPrjCd", oldPrjCd);
            } else {
                //新增
                dOpData.setStrValue(nodePath + "PrjJobDef.jobId", "\${jobId}");

            }
        }

        dOpData.setStrValue(nodePath + "entityName", "PrjJobDef");
        dOpData.setStrValue(nodePath + "PrjJobDef.prjCd", prjCd);
        dOpData.setStrValue(nodePath + "PrjJobDef.jobName", jobName);
        dOpData.setStrValue(nodePath + "PrjJobDef.jobCd", jobCd);
        dOpData.setStrValue(nodePath + "PrjJobDef.qryParam01", qryParam01);
        dOpData.setStrValue(nodePath + "PrjJobDef.qryParam02", qryParam02);
        dOpData.setStrValue(nodePath + "PrjJobDef.qryParam03", qryParam03);
        dOpData.setStrValue(nodePath + "PrjJobDef.entityName", entityName);
        dOpData.setStrValue(nodePath + "PrjJobDef.jobValueSuffix", jobValueSuffix);
        dOpData.setStrValue(nodePath + "PrjJobDef.jobGroupName", jobGroupName);
        dOpData.setStrValue(nodePath + "PrjJobDef.qryService", qryService);
        dOpData.setStrValue(nodePath + "PrjJobDef.exeParam09", exeParam09);
        dOpData.setStrValue(nodePath + "PrjJobDef.exeParam07", exeParam07);
        dOpData.setStrValue(nodePath + "PrjJobDef.exeParam08", exeParam08);
        dOpData.setStrValue(nodePath + "PrjJobDef.exeParam10", exeParam10);
        dOpData.setStrValue(nodePath + "PrjJobDef.jobDesc", jobDesc);
        dOpData.setStrValue(nodePath + "PrjJobDef.createDate", svcRequest.getReqTimeWithDate());
        svcRequest.addOp("SAVE_PRJ_JOB_DEF_CMPT", dOpData);
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
        String jobId = requestUtil.getStrParam("jobId");
        String prjCds = requestUtil.getStrParam("prjCds");
        String jobCds = requestUtil.getStrParam("jobCds");
        SvcResponse svcResponse = null;
        if (jobId.indexOf(",") > 0) {//表明是批量删除
            String[] jobIds = jobId.split(",");
            String[] prjCd = prjCds.split(",");
            String[] jobCd = jobCds.split(",");
            for (int i = 0; i < jobIds.length; i++) {
                XmlBean opData = new XmlBean();
                opData.setStrValue("OpData.PrjJobDef.jobCd", jobCd[i]);
                opData.setStrValue("OpData.PrjJobDef.prjCd", prjCd[i]);

                svcRequest.addOp("DELETE_PRJ_JOB_DEF_CMPT", opData);
                svcResponse = transaction(svcRequest);
            }
        } else {
            XmlBean opData = new XmlBean();
            opData.setStrValue("OpData.PrjJobDef.jobCd", jobCds);
            opData.setStrValue("OpData.PrjJobDef.prjCd", prjCds);

            svcRequest.addOp("DELETE_PRJ_JOB_DEF_CMPT", opData);
            svcResponse = transaction(svcRequest);
        }
        String result = svcResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg();

        String jsonStr = """{success:${result}, errMsg:"${errMsg}"}""";
        ResponseUtil.printAjax(response, jsonStr);
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
        String jobCd = request.getParameter("jobCd");
        String prjCd = request.getParameter("prjCd");
        // 返回处理结果
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.jobCd", jobCd);
        opData.setStrValue("OpData.prjCd", prjCd);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.addOp("QUERY_ALL_PRJ_JOB_DEF_CMPT", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean entity = svcResponse.getFirstOpRsp("QUERY_ALL_PRJ_JOB_DEF_CMPT").getBeanByPath("Operation.OpResult.PrjJobDefs");
            String fileName = UUID.randomUUID().toString() + ".xml";
            String filePath = "export/" + fileName;
            File xmlFile = ServerFile.createFile("export" + File.separator, fileName);
            xmlFile.write(entity.toXML(), "UTF-8");
            // 文件下载输出到响应流
            ResponseUtil.downloadFile(response, null, "${entityName}.xml", xmlFile, request.getHeader("USER-AGENT"));
        }
    }

    /**
     * 批量导出实体
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public void batchExport(HttpServletRequest request, HttpServletResponse response) {
        // 输入参数
        String prjCds = request.getParameter("prjCds");
        String jobCds = request.getParameter("jobCds");
        // 返回处理结果
        XmlBean opData = new XmlBean();
        String[] prjCd = prjCds.split(",");
        String[] id = jobCds.split(",");
        opData.setStrValue("OpData.jobCd", jobCds);
        opData.setStrValue("OpData.prjCd", prjCds);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.addOp("QUERY_ALL_PRJ_JOB_DEF_CMPT", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean entity = svcResponse.getFirstOpRsp("QUERY_ALL_PRJ_JOB_DEF_CMPT").getBeanByPath("Operation.OpResult.PrjJobDefs");
            String fileName = UUID.randomUUID().toString() + ".xml";
            String filePath = "export/" + fileName;
            File xmlFile = ServerFile.createFile("export" + File.separator, fileName);
            xmlFile.write(entity.toXML(), "UTF-8");
            // 文件下载输出到响应流
            jobCds = jobCds.replaceAll(",", " ")
            ResponseUtil.downloadFile(response, null, "${jobCds}.xml", xmlFile, request.getHeader("USER-AGENT"));
        }
    }

    //导入xml配置参数
    public ModelAndView saveImportData(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean resultXmlBean = new XmlBean();
        //获取传入文件
        MultipartFile localFile = super.getFile(request, "importJobFile");
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
                XmlBean opdate = new XmlBean();
                XmlBean reqData = new XmlBean(fileText);
                opdate.setBeanByPath("OpData", reqData);
                svcRequest = RequestUtil.getSvcRequest(request);
                svcRequest.addOp("IMPORT_PRJ_JOB_DEF_CMPT", opdate);
                SvcResponse svcResponse = transaction(svcRequest);
                if (svcResponse.isSuccess()) {
                    resultXmlBean = svcResponse.getFirstOpRsp("IMPORT_PRJ_JOB_DEF_CMPT").getBeanByPath("Operation.OpResult.PrjJobDefs");
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
        String res = resultXmlBean.toString();
        String jsonStr = """{isSuccess: ${result}, errMsg: "${errMsg}"}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

    /**
     * 初始化规则管理界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView ruleCode(HttpServletRequest request, HttpServletResponse response) {
        // 页面返回视图
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/oframe/sysmg/sys018/sys018_rule_code", modelMap)
    }


}
