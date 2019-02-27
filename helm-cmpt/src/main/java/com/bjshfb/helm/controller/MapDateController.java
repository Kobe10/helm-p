package com.bjshfb.helm.controller;

import com.bjshfb.helm.cmpt.common.EntityInfo;
import com.bjshfb.helm.util.EntityInfoUtils;
import com.shfb.oframe.core.util.common.ServerFile;
import com.shfb.oframe.core.util.common.StringUtil;
import com.shfb.oframe.core.util.common.XmlBean;
import com.shfb.oframe.core.util.excel.ExcelReader;
import com.shfb.oframe.core.util.service.bo.SvcRequest;
import com.shfb.oframe.core.util.service.bo.SvcResponse;
import com.shfb.oframe.core.web.controller.GroovyController;
import com.shfb.oframe.core.web.util.RequestUtil;
import com.shfb.oframe.core.web.util.ResponseUtil;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * @program: helm
 * @description: 数据维护
 * @author: fuzq
 * @create: 2018-09-13 15:29
 **/
@Controller
@RequestMapping({"/shfb/map/date"})
public class MapDateController extends GroovyController {

    /**
     * @Description: TODO 导入文件
     * @Param: [request, response]
     * @return: void
     * @Author: fuzq
     * @Date: 2018/9/13 15:30
     **/
    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public void importXls(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jsonObject = new JSONObject();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        MultipartFile localFile = super.getFile(request, "importMbFile");
        String originalFileName = localFile.getOriginalFilename();
        String fileType = originalFileName.substring(originalFileName.lastIndexOf("."), originalFileName.length());
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
                        outputStream.close();
                    } catch (Exception e) {
                    }
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Exception e) {
                    }
                }
            }
            //获取sheet页名字
            ExcelReader excelReader = new ExcelReader(saveFile);
            String sheetName = excelReader.getSheetName(0);
            XmlBean paramData = new XmlBean();
            String nodePath = "OpData.";
            paramData.setStrValue(nodePath + "filePath", saveFile.getAbsolutePath());
            //获取sheet页名字   GENERAL_DATA 通用数据导入
            paramData.setStrValue(nodePath + "configRowNum", "0");
            if (StringUtil.isEqual("GENERAL_DATA", sheetName)) {
                paramData.setStrValue(nodePath + "entityName", "MapDataInfo");
                paramData.setStrValue(nodePath + "type", "1");
            } else if (StringUtil.isEqual("IPC_DATA", sheetName)) {
                EntityInfo entityInfo = EntityInfoUtils.getEntityInfo("2");
                paramData.setStrValue(nodePath + "entityName", entityInfo.getEntityName());
                paramData.setStrValue(nodePath + "type", entityInfo.getLayerType());
            }
            paramData.setStrValue(nodePath + "dealRowSize", "1");
            svcRequest.addOp("MAP_DATA_IMPORT_CMPT", paramData);
            // 调用服务，执行数据查询
            SvcResponse svcResponse = query(svcRequest);
            if (svcResponse.isSuccess()) {
                jsonObject.put("success", true);
                jsonObject.put("errMsg", "");
                jsonObject.put("data", "");
            } else {
                errMsg = svcResponse.getErrMsg();
                jsonObject.put("success", false);
                jsonObject.put("errMsg", errMsg);
                jsonObject.put("data", "");
            }
            saveFile.delete();
        } else {
            errMsg = "文件格式不正确";
            jsonObject.put("success", false);
            jsonObject.put("errMsg", errMsg);
            jsonObject.put("data", "");
        }
        ResponseUtil.printAjaxJson(response, jsonObject);
    }

    /**
     * @Description: TODO 下载导入模板
     * @Param: [request, response]
     * @return: void
     * @Author: fuzq
     * @Date: 2018/9/20 11:43
     **/
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public void downTemplate(HttpServletRequest request, HttpServletResponse response) {
        String agent = request.getHeader("USER-AGENT");

        RequestUtil requestUtil = new RequestUtil(request);

        //模板类型
        String templateType = requestUtil.getStrParam("templateType");

        if (StringUtil.isEqual("1", templateType)) {
            //通用数据模板
            File templateFile = new File(StringUtil.formatFilePath("webroot:/template/通用图层数据导入模版.xls"));
            ResponseUtil.downloadFile(response, "", "通用图层数据导入模版.xls", templateFile, agent);
        } else if (StringUtil.isEqual("2", templateType)) {
            //摄像头数据模板
            File templateFile = new File(StringUtil.formatFilePath("webroot:/template/摄像头图层数据导入模版.xls"));
            ResponseUtil.downloadFile(response, "", "摄像头图层数据导入模版.xls", templateFile, agent);
        }
    }

    /**
     * @Description: TODO 图层数据保存
     * @Param: [request, response]
     * @return: void
     * @Author: fuzq
     * @Date: 2018/9/13 17:44
     **/
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public void save(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jsonObject = new JSONObject();
        RequestUtil requestUtil = new RequestUtil(request);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);

        String layperType = requestUtil.getStrParam("layperType");
        String mapLayerDefId = requestUtil.getStrParam("mapLayerDefId");
        String data = requestUtil.getStrParam("data");

        XmlBean reqData = new XmlBean();
        String nodePath = "OpData.";
        reqData.setStrValue(nodePath + "layperType", layperType);
        reqData.setStrValue(nodePath + "mapLayerDefId", mapLayerDefId);
        reqData.setStrValue(nodePath + "data", data);
        svcRequest.addOp("MAP_DATE_SAVE_CMPT", reqData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            if (StringUtil.isNotEmptyOrNull(svcResponse.getFirstOpRsp("MAP_DATE_SAVE_CMPT").getStrValue("Operation.mapDataId"))) {
                String mapDataId = svcResponse.getFirstOpRsp("MAP_DATE_SAVE_CMPT").getStrValue("Operation.mapDataId");
                String expanId = svcResponse.getFirstOpRsp("MAP_DATE_SAVE_CMPT").getStrValue("Operation.expanId");
                JSONObject resultJson = new JSONObject();
                resultJson.put("mapDataId", mapDataId);
                resultJson.put("expanId", expanId);

                jsonObject.put("success", true);
                jsonObject.put("errMsg", "");
                jsonObject.put("data", resultJson);
            } else {
                jsonObject.put("success", false);
                jsonObject.put("errMsg", "保存图层数据失败");
                jsonObject.put("data", "");
            }
        } else {
            jsonObject.put("success", false);
            jsonObject.put("errMsg", "保存图层数据失败");
            jsonObject.put("data", "");
        }
        ResponseUtil.printAjaxJson(response, jsonObject);
    }

    /**
     * @Description: TODO 删除图层数据 关联数据是否不能删除
     * @Param: [request, response]
     * @return: void
     * @Author: fuzq
     * @Date: 2018/9/13 17:44
     **/
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public void delete(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jsonObject = new JSONObject();
        RequestUtil requestUtil = new RequestUtil(request);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);

        String mapDataIds = requestUtil.getStrParam("mapDataIds");
        String expanIds = requestUtil.getStrParam("expanIds");
        String layperType = requestUtil.getStrParam("layperType");

        XmlBean reqData = new XmlBean();
        String nodePath = "OpData.";
        reqData.setStrValue(nodePath + "mapDataIds", mapDataIds);
        reqData.setStrValue(nodePath + "expanIds", expanIds);
        reqData.setStrValue(nodePath + "layperType", layperType);
        svcRequest.addOp("MAP_DATE_DEL_CMPT", reqData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            jsonObject.put("success", true);
            jsonObject.put("errMsg", "");
            jsonObject.put("data", "");
        } else {
            jsonObject.put("success", false);
            jsonObject.put("errMsg", "删除图层数据失败");
            jsonObject.put("data", "");
        }
        ResponseUtil.printAjaxJson(response, jsonObject);
    }

    /**
     * @Description: TODO 数据通用查询
     * @Param: [request, response]
     * @return: void
     * @Author: fuzq
     * @Date: 2018/9/13 17:44
     **/
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public void query(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jsonObject = new JSONObject();
        RequestUtil requestUtil = new RequestUtil(request);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        //查询标志 false 查询失败
        String errMsg = "";

        String queryType = requestUtil.getStrParam("queryType");
        String data = requestUtil.getStrParam("data");
        if (!StringUtil.isNotEmptyOrNull(queryType)) {
            errMsg = "查询类型为空";
            jsonObject.put("success", false);
            jsonObject.put("errMsg", errMsg);
            jsonObject.put("data", "");
            ResponseUtil.printAjaxJson(response, jsonObject);
        }

        if (StringUtil.isNotEmptyOrNull(data)) {
            XmlBean reqData = new XmlBean();
            String nodePath = "OpData.";
            reqData.setStrValue(nodePath + "queryType", queryType);
            reqData.setStrValue(nodePath + "data", data);
            svcRequest.addOp("MAP_DATE_QUERY_CMPT", reqData);
            SvcResponse svcResponse = query(svcRequest);
            if (svcResponse.isSuccess()) {
                String json = svcResponse.getFirstOpRsp("MAP_DATE_QUERY_CMPT").getStrValue("Operation.Data");
                JSONObject jsonObject1 = JSONObject.fromObject(json);
                jsonObject.put("success", false);
                jsonObject.put("errMsg", errMsg);
                jsonObject.put("data", jsonObject1);
                ResponseUtil.printAjaxJson(response, jsonObject);

            }
        } else {
            errMsg = "查询数据为空";
            jsonObject.put("success", false);
            jsonObject.put("errMsg", errMsg);
            jsonObject.put("data", "");
            ResponseUtil.printAjaxJson(response, jsonObject);
        }
    }
}
