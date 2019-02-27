import com.shfb.oframe.core.util.common.ServerFile
import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.exception.BusinessException
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.util.spring.SvcUtil
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import org.springframework.ui.ModelMap
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 签约处理控制层
 */
class ph010 extends GroovyController {
    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/eland/ph/ph010/ph010", modelMap);
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
        String camId = request.getParameter("camId");
        if (!StringUtil.isEmptyOrNull(camId)) {
            XmlBean opData = new XmlBean();
            opData.setStrValue("OpData.entityName", "CameraMngInfo");
            opData.setStrValue("OpData.entityKey", camId);
            svcRequest.addOp("QUERY_ALL_ENTITY", opData);
            SvcResponse svcResponse = query(svcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean cameraMngInfo = svcResponse.getFirstOpRsp("QUERY_ALL_ENTITY")
                        .getBeanByPath("Operation.OpResult.CameraMngInfo");
                if (cameraMngInfo != null) {
                    modelMap.put("cameralist", cameraMngInfo.getRootNode());
                }
            }
        }
        return new ModelAndView("/eland/ph/ph010/ph010_edit", modelMap);
    }

    /**
     * 初始化修改界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView viewMain(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("prjCd", request.getParameter("prjCd"));
        modelMap.put("camId", request.getParameter("camId"));
        return new ModelAndView("/eland/ph/ph010/ph010_view_iframe", modelMap);
    }

    /**
     * 初始化修改界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView view(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String camId = request.getParameter("camId");
        if (!StringUtil.isEmptyOrNull(camId)) {
            XmlBean opData = new XmlBean();
            opData.setStrValue("OpData.entityName", "CameraMngInfo");
            opData.setStrValue("OpData.entityKey", camId);
            svcRequest.addOp("QUERY_ALL_ENTITY", opData);
            SvcResponse svcResponse = query(svcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean cameraMngInfo = svcResponse.getFirstOpRsp("QUERY_ALL_ENTITY")
                        .getBeanByPath("Operation.OpResult.CameraMngInfo");
                if (cameraMngInfo != null) {
                    modelMap.put("cameralist", cameraMngInfo.getRootNode());
                }
            }
        }
        return new ModelAndView("/eland/ph/ph010/ph010_view", modelMap);
    }

    /**
     * 初始化修改界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView delectView(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String camId = request.getParameter("camId");
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.entityKey", camId);
        opData.setStrValue("OpData.entityName", "CameraMngInfo");
        svcRequest.addOp("DELETE_ENTITY_INFO", opData);
        SvcResponse svcResponse = transaction(svcRequest);
        boolean result = svcResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg();

        String jsonStr = """{success:${result}, errMsg:"${errMsg}"}""";
        ResponseUtil.printAjax(response, jsonStr);
    }
    /**
     * 初始化修改界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView openImport(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/eland/ph/ph010/ph010_import", modelMap);
    }

    /**
     * 接收js传递的导入Excel
     */
    public void imput(HttpServletRequest request, HttpServletResponse response) throws BusinessException {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        SvcResponse svcResponse;
        RequestUtil requestUtil = new RequestUtil((request));
        String docPath;
        MultipartFile localFile = super.getFile(request, "uploadFile");
        String originalFileName = localFile.getOriginalFilename();
        String fileType = originalFileName.substring(
                originalFileName.lastIndexOf("."), originalFileName.length());
        String errMsg = "";
        boolean result = false;

        if (fileType.contains(".xls") || fileType.contains(".xlsx")) {
            String remoteFileName = UUID.randomUUID().toString() + fileType;
            File saveFile = ServerFile.createFile(remoteFileName);
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
            docPath = saveFile.getAbsolutePath();
            XmlBean opData = new XmlBean();
            opData.setStrValue("OpData.filePath", "${docPath}");
            opData.setStrValue("OpData.configRowNum", "1");
            opData.setStrValue("OpData.serviceName", "SAVE_CAM_MNG_BATCH");
            svcRequest.addOp("IMPORT_EXCEL_INFO", opData);
            svcResponse = transaction(svcRequest);
            result = svcResponse.isSuccess();
            errMsg = svcResponse.getErrMsg();
            saveFile.delete();
        } else {
            errMsg = "文件格式不正确";
        }
        String jsonStr = """{isSuccess: ${result}, errMsg: "${errMsg}"}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

    /**
     * 保存
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView saveMP(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String camId = request.getParameter("camId");
        String camCd = request.getParameter("camCd");
        String channelCd = request.getParameter("channelCd");
        String camAddr = request.getParameter("camAddr");
        String accessAddr = request.getParameter("accessAddr");
        String markPos = request.getParameter("markPos");
        ModelMap modelMap = new ModelMap();

        XmlBean opData = new XmlBean();
        if (StringUtil.isEmptyOrNull(camId)) {
            opData.setStrValue("OpData.EntityData.camId", "\${camId}");
        } else {
            opData.setStrValue("OpData.EntityData.camId", camId);
        }
        opData.setStrValue("OpData.entityName", "CameraMngInfo");
        opData.setStrValue("OpData.EntityData.camCd", camCd);
        opData.setStrValue("OpData.EntityData.channelCd", channelCd);
        opData.setStrValue("OpData.EntityData.camAddr", camAddr);
        opData.setStrValue("OpData.EntityData.accessAddr", accessAddr);
        opData.setStrValue("OpData.EntityData.markPos", markPos);
        svcRequest.addOp("SAVE_ENTITY", opData);
        SvcResponse svcResponse = transaction(svcRequest);
        boolean result = svcResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg();

        String jsonStr = """{success:${result}, errMsg:"${errMsg}", hsId:"${camId}"}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

    /**
     * 下载导入模板
     * @param request 请求信息
     * @param response 响应信息
     * @return 打开导入界面
     */
    public void downTemplate(HttpServletRequest request, HttpServletResponse response) {
        String agent = request.getHeader("USER-AGENT");
        String templateFile = new File(StringUtil.formatFilePath("webroot:/eland/ph/ph010/CameraMngInfo.xls"));
        // 文件下载输出到响应流
        ResponseUtil.downloadFile(response, "", "摄像头信息导入模板.xls", new File(templateFile), agent);
    }

    /**
     * 打开视频监控地图标注位置
     * @param request
     * @param response
     * @return
     */
    public ModelAndView setCamPos(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String prjCd = request.getParameter("prjCd");

        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String camId = request.getParameter("camId");
        modelMap.put("camId", camId);
        if (!StringUtil.isEmptyOrNull(camId)) {
            XmlBean opData = new XmlBean();
            opData.setStrValue("OpData.entityName", "CameraMngInfo");
            opData.setStrValue("OpData.entityKey", camId);
            svcRequest.addOp("QUERY_ALL_ENTITY", opData);
            SvcResponse svcResponse = query(svcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean cameraMngInfo = svcResponse.getFirstOpRsp("QUERY_ALL_ENTITY").getBeanByPath("Operation.OpResult.CameraMngInfo");
                if (cameraMngInfo != null) {
                    modelMap.put("cameraInfo", cameraMngInfo.getRootNode());
                }
            }
        }

        SvcRequest svcReqCfg = RequestUtil.getSvcRequest(request);
        XmlBean reqDataCfg = new XmlBean();
        String nodePath = "SvcCont.SysCfgs.";
        reqDataCfg.setValue(nodePath + "SysCfg[0].itemCd", "geoserver_map");
        reqDataCfg.setValue(nodePath + "SysCfg[0].isCached", "true");
        reqDataCfg.setValue(nodePath + "SysCfg[0].prjCd", prjCd);
        svcReqCfg.setReqData(reqDataCfg);
        // 调用服务
        SvcResponse svcRspCfg = SvcUtil.callSvc("sysCfgService", "querySysCfgData", svcReqCfg);
        if (svcRspCfg.isSuccess()) {
            XmlBean cfgBean = svcRspCfg.getRspData().getBeanByPath("SvcCont.SysCfgs");
            int cfgValueNum = svcRspCfg.getRspData().getListNum("SvcCont.SysCfgs.SysCfg[0].Values.Value");
            String baselayerUrl = "";
            String layerName1 = "";
            String layerName2 = "";
            String lonLatLevel = "";
            String mapBounds = "";
            for (int i = 0; i < cfgValueNum; i++) {
                //获取该配置参数的 key ， value
                String val = cfgBean.getStrValue("SysCfgs.SysCfg[0].Values.Value[${i}].valueCd");
                String relValue = cfgBean.getStrValue("SysCfgs.SysCfg[0].Values.Value[${i}].valueName");
                if (StringUtil.isEqual("MAP_LAYER_URL", val)) {
                    baselayerUrl = relValue;
                } else if (StringUtil.isEqual("MAP_LAYER_NAME1", val)) {
                    layerName1 = relValue;
                } else if (StringUtil.isEqual("MAP_LAYER_NAME2", val)) {
                    layerName2 = relValue;
                } else if (StringUtil.isEqual("MAP_LON_LAT_LEVEL", val)) {
                    lonLatLevel = relValue;
                } else if (StringUtil.isEqual("MAP_BOUNDS", val)) {
                    mapBounds = relValue;
                }
            }
            //查询原有地图坐标，返回页面绘制地图
            modelMap.put("baselayerUrl", baselayerUrl);
            modelMap.put("layerName1", layerName1);
            modelMap.put("layerName2", layerName2);
            modelMap.put("lonLatLevel", lonLatLevel);
            modelMap.put("mapBounds", mapBounds);
        }
        return new ModelAndView("/eland/ph/ph010/ph010_cammap", modelMap);
    }

    /**
     * 保存摄像头坐标位置
     * @param request
     * @param response
     */
    public void saveCamPos(HttpServletRequest request, HttpServletResponse response) {
        String camId = request.getParameter("camId");
        String posStr = request.getParameter("posStr");
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.entityName", "CameraMngInfo");
        opData.setStrValue("OpData.EntityData.camId", camId);
        opData.setStrValue("OpData.EntityData.markPos", posStr);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.addOp("SAVE_ENTITY", opData);
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printAjax(response, """{success:"${svcResponse.isSuccess()}", errMsg:"${
            svcResponse.getErrMsg()
        }"}""");
    }
}