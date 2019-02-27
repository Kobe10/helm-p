package com.bjshfb.vf.server.controller;

import com.bjshfb.vf.server.nettyser.server.ChanleMap;
import com.shfb.oframe.core.util.common.Encrypt;
import com.shfb.oframe.core.util.common.StringUtil;
import com.shfb.oframe.core.util.common.XmlBean;
import com.shfb.oframe.core.util.service.bo.SvcRequest;
import com.shfb.oframe.core.util.service.bo.SvcResponse;
import com.shfb.oframe.core.web.controller.AbstractController;
import com.shfb.oframe.core.web.util.RequestUtil;
import com.shfb.oframe.core.web.util.ResponseUtil;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.SOAPException;
import java.io.*;
import java.util.Date;

/**
 * 提供数据初始化服务
 * Created by linql on 2018/9/4.
 */
@Controller
@RequestMapping({"/vf/service"})
public class IpcController extends AbstractController {
    public static final Logger logger = Logger.getLogger(IpcController.class);

    /**
     * @Description: TODO 获取IPC基础信息
     * @Param: [request, response, prjCd, clientId]
     * @return: void
     * @Author: fuzq
     * @Date: 2018/9/6 18:47
     **/
    @RequestMapping(method = RequestMethod.POST, value = "/ipc/info/{prjCd}/{clientId}")
    public void queryIpcInfo(HttpServletRequest request, HttpServletResponse response,
                             @PathVariable("prjCd") String prjCd,
                             @PathVariable("clientId") String clientId) {
        JSONObject result = new JSONObject();

        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        // 查询条件
        opData.setStrValue(prePath + "entityName", "CamDataInfo");

        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "camId");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "camCd");
        opData.setStrValue(prePath + "ResultFields.fieldName[2]", "camName");
        opData.setStrValue(prePath + "ResultFields.fieldName[4]", "camType");
        opData.setStrValue(prePath + "ResultFields.fieldName[5]", "camIpAddr");
        opData.setStrValue(prePath + "ResultFields.fieldName[6]", "camUser");
        opData.setStrValue(prePath + "ResultFields.fieldName[7]", "camPwd");
        opData.setStrValue(prePath + "ResultFields.fieldName[8]", "camRestPos");
        //截图时间
        opData.setStrValue(prePath + "ResultFields.fieldName[9]", "cptPicTimeExp");
        //初始化位置
        opData.setStrValue(prePath + "ResultFields.fieldName[10]", "camRestPos");

        //权限信息
//        opData.setStrValue(prePath + "ResultFields.fieldName[9]", "CamDataRhts");
//        opData.setStrValue(prePath + "ResultFields.fieldName[10]", "rhtType");
//        opData.setStrValue(prePath + "ResultFields.fieldName[11]", "rhtId");

        //调用服务
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
        SvcResponse svcResponse = query(svcRequest);
        boolean isSuccess = svcResponse.isSuccess();
        XmlBean xmlBean = new XmlBean();
        String preDate = "Rows.";
        if (isSuccess) {
            XmlBean resultBean = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA")
                    .getBeanByPath("Operation.OpResult.");
            if (resultBean != null) {
                int num = resultBean.getListNum("OpResult.PageData.Row");
                XmlBean infoXml = resultBean.getBeanByPath("OpResult.PageData");
                for (int i = 0; i < num; i++) {
                    xmlBean.setStrValue(preDate + "Row[" + i + "].id", infoXml.getStrValue("PageData.Row[" + i + "].camCd"));
                    xmlBean.setStrValue(preDate + "Row[" + i + "].name", infoXml.getStrValue("PageData.Row[" + i + "].camName"));
                    xmlBean.setStrValue(preDate + "Row[" + i + "].ip", infoXml.getStrValue("PageData.Row[" + i + "].camIpAddr"));
                    xmlBean.setStrValue(preDate + "Row[" + i + "].user", infoXml.getStrValue("PageData.Row[" + i + "].camUser"));
                    if (StringUtil.isNotEmptyOrNull(infoXml.getStrValue("PageData.Row[" + i + "].camPwd"))) {
                        xmlBean.setStrValue(preDate + "Row[" + i + "].psw", Encrypt.decryptByDES(infoXml.getStrValue("PageData.Row[" + i + "].camPwd")));
                    } else {
                        xmlBean.setStrValue(preDate + "Row[" + i + "].psw", infoXml.getStrValue("PageData.Row[" + i + "].camPwd"));
                    }
                    xmlBean.setStrValue(preDate + "Row[" + i + "].camId", infoXml.getStrValue("PageData.Row[" + i + "].camId"));
                    //定时截图时间
                    xmlBean.setStrValue(preDate + "Row[" + i + "].cptPicTime", infoXml.getStrValue("PageData.Row[" + i + "].cptPicTimeExp"));
                    //初始位置信息
                    xmlBean.setStrValue(preDate + "Row[" + i + "].camRestPos", infoXml.getStrValue("PageData.Row[" + i + "].camRestPos"));
                    //权限信息需要拼接 后续拼接
                    xmlBean.setStrValue(preDate + "Row[" + i + "].viewUsers", "");
                    xmlBean.setStrValue(preDate + "Row[" + i + "].controlUsers", "");

//                    xmlBean.setStrValue(preDate + "Row[" + i + "].viewUsers", infoXml.getStrValue("PageData.Row[" + i + "].camCd"));
//                    xmlBean.setStrValue(preDate + "Row[" + i + "].controlUsers", infoXml.getStrValue("PageData.Row[" + i + "].camCd"));
                }
            }

            result.put("success", true);
            result.put("errMsg", "");
            result.put("data", xmlBean.toXML());
        } else {
            result.put("success", false);
            result.put("errMsg", svcResponse.getErrMsg());
            result.put("data", "");
        }

        ResponseUtil.printAjaxJson(response, result);
    }

    /**
     * @Description: TODO 保存图片->附件表
     * @Param: [request, response, prjCd, clientId]
     * @return: void
     * @Author: fuzq
     * @Date: 2018/9/6 14:45
     **/
    @RequestMapping(method = RequestMethod.POST, value = "/ipc/file/{prjCd}/{clientId}")
    public void uploadImage(HttpServletRequest request, HttpServletResponse response,
                            @PathVariable("prjCd") String prjCd,
                            @PathVariable("clientId") String clientId) {
        JSONObject result = new JSONObject();
        RequestUtil requestUtil = new RequestUtil(request);
        String docName = requestUtil.getStrParam("docName");
        String docPath = requestUtil.getStrParam("docPath");
        String docTypeName = requestUtil.getStrParam("docTypeName");
        String relType = requestUtil.getStrParam("relType");
        String relId = requestUtil.getStrParam("relId");

        XmlBean reqData = new XmlBean();

        String nodePath = "SvcCont.Files.File[" + 0 + "].";
        reqData.setStrValue(nodePath + "docName", docName);
        reqData.setStrValue(nodePath + "docPath", docPath);
        reqData.setStrValue(nodePath + "docTypeName", docTypeName);
        reqData.setStrValue(nodePath + "prjCd", prjCd);
        reqData.setStrValue(nodePath + "relType", relType);
        reqData.setStrValue(nodePath + "relId", relId);

        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = callService("fileService", "saveAddFile", svcRequest);

        JSONObject jsonObject = new JSONObject();
        if (svcResponse.isSuccess()) {
            XmlBean rspData = svcResponse.getRspData();
            int count = rspData.getListNum("SvcCont.Files.File");
            String docId = rspData.getStrValue("SvcCont.Files.File[0].docId");
            docName = rspData.getStrValue("SvcCont.Files.File[0].docName");
            docPath = "upload/" + rspData.getStrValue("SvcCont.Files.File[0].docPath");
            jsonObject.put("docName", docName);
            jsonObject.put("docId", docId);
            jsonObject.put("docPath", docPath);
            result.put("success", true);
            result.put("errMsg", "");
            result.put("data", jsonObject);
        } else {
            result.put("success", false);
            result.put("errMsg", "保存图片失败");
            result.put("data", "");
        }
        ResponseUtil.printAjaxJson(response, result);
    }

    /**
     * @Description: TODO 更新监控信息
     * @Param: [request, response, prjCd, clientId]
     * @return: void
     * @Author: fuzq
     * @Date: 2018/9/6 14:45
     **/
    @RequestMapping(method = RequestMethod.POST, value = "/ipc/update/{prjCd}/{clientId}")
    public void updateIpcInfo(HttpServletRequest request, HttpServletResponse response,
                              @PathVariable("prjCd") String prjCd,
                              @PathVariable("clientId") String clientId) {
        JSONObject result = new JSONObject();
        XmlBean opData = new XmlBean();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        RequestUtil requestUtil = new RequestUtil(request);
        opData.setStrValue("OpData.entityName", "CamDataInfo");

        //操作类型：1 上报状态 2 截图操作 3 更新操作
        String type = requestUtil.getStrParam("type");
        //上报状态
        if (StringUtil.isEqual("1", type)) {
            boolean flag = true;
            String errId = "";
            //获取状态数据
            String data = requestUtil.getStrParam("data");
            JSONObject jsonObject = new JSONObject(data);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                SvcRequest svcRequest1 = RequestUtil.getSvcRequest(request);

                JSONObject myjObject = jsonArray.getJSONObject(i);
                String ipcId = myjObject.getString("ipcId");
                //监控状态
                String camSts = myjObject.getString("command");
                //主键Id
                String camId = myjObject.getString("primaryId");

                //更新当前id的状态
                opData.setStrValue("OpData.EntityData.camId", camId);
                if (StringUtil.isNotEmptyOrNull(camSts)) {
                    opData.setStrValue("OpData.EntityData.camSts", camSts);
                    //变更时间 camStsChgTime
                    opData.setStrValue("OpData.EntityData.camStsChgTime", new Date());
                    //报告时间 camStsChgTime
                    opData.setStrValue("OpData.EntityData.camStsUptTime", new Date());
                }
                svcRequest1.addOp("SAVE_ENTITY", opData);
                SvcResponse svcResponse = transaction(svcRequest1);
                if (!svcResponse.isSuccess()) {
                    flag = false;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder = stringBuilder.append(errId + "," + ipcId);
                    errId = stringBuilder.toString();
                }
            }
            //判断是否都执行成功
            if (flag) {
                result.put("success", true);
                result.put("date", "");
                result.put("errMsg", "");
            } else {
                result.put("success", false);
                result.put("date", "");
                result.put("errMsg", errId + "  号IPC 更新状态失败");
            }
        }
        //截图操作
        if (StringUtil.isEqual("2", type)) {
            String camCoverPic = requestUtil.getStrParam("camCoverPic");
            String ipcId = requestUtil.getStrParam("ipcId");
            String primaryId = requestUtil.getStrParam("primaryId");
            opData.setStrValue("OpData.EntityData.camId", primaryId);
            if (StringUtil.isNotEmptyOrNull(camCoverPic)) {
                opData.setStrValue("OpData.EntityData.camCoverPic", camCoverPic);
            }
            svcRequest.addOp("SAVE_ENTITY", opData);
            SvcResponse svcResponse = transaction(svcRequest);
            if (svcResponse.isSuccess()) {
                result.put("success", true);
                result.put("date", "");
                result.put("errMsg", "");
            } else {
                result.put("success", false);
                result.put("date", "");
                result.put("errMsg", svcResponse.getErrMsg());
            }
        }
        ResponseUtil.printAjaxJson(response, result);
    }

    /**
     * @Description: TODO 发现摄像头
     * @Param: [request, response]
     * @return: void
     * @Author: fuzq
     * @Date: 2018/9/17 17:10
     **/
    @RequestMapping(method = RequestMethod.POST, value = "/ipc/discovery")
    public void discovery(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Headers", "X-Requested-With,content-type");
        response.setHeader("Access-Control-Allow-Methods", "POST,GET");
        response.setHeader("X-Powered-By", "3.2.1");
        String Ip = RequestUtil.getIpAddr(request);
        String result = "";
        net.sf.json.JSONObject jsonObject = new net.sf.json.JSONObject();
        result = ChanleMap.sendMessageToUser("111", "discovery");
        if (StringUtil.isNotEmptyOrNull(result)) {
            jsonObject.put("success", true);
            jsonObject.put("errMsg", "  ");
            jsonObject.put("data", result);
            ResponseUtil.printAjax(response, jsonObject);
        } else {
            jsonObject.put("success", false);
            jsonObject.put("errMsg", "客户端未连接");
            jsonObject.put("data", "");
            ResponseUtil.printAjax(response, jsonObject);
        }
    }

    /**
     * @Description: TODO 获取快照地址
     * @Param: [request, response]
     * @return: void
     * @Author: fuzq
     * @Date: 2018/9/6 16:22
     **/
    @RequestMapping(value = "/ipc/getSnapshot", method = RequestMethod.POST)
    public void getSnapshotURI(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Headers", "X-Requested-With,content-type");
        response.setHeader("Access-Control-Allow-Methods", "POST,GET");
        response.setHeader("X-Powered-By", "3.2.1");
        net.sf.json.JSONObject jsonObject = new net.sf.json.JSONObject();
        String ipcId = request.getParameter("ipcId");
        String result = "";
        String Ip = RequestUtil.getIpAddr(request);
        if (StringUtil.isEmptyOrNull(ipcId)) {
            jsonObject.put("success", false);
            jsonObject.put("errMsg", "摄像头Id不能为空");
            jsonObject.put("data", " ");
            ResponseUtil.printAjax(response, jsonObject);
            return;
        }
        result = ChanleMap.sendMessageToUser("111", "getSnapshot", ipcId);
        if (StringUtil.isNotEmptyOrNull(result)) {
            jsonObject.put("success", true);
            jsonObject.put("errMsg", " ");
            jsonObject.put("data", result);
            ResponseUtil.printAjax(response, jsonObject);
        } else {
            jsonObject.put("success", false);
            jsonObject.put("errMsg", "客户端未连接");
            jsonObject.put("data", "");
            ResponseUtil.printAjax(response, jsonObject);
        }
    }

    /**
     * @Description: TODO 控制摄像头
     * @Param: [request, response]
     * @return: void
     * @Author: fuzq
     * @Date: 2018/9/6 16:22
     **/
    @RequestMapping(value = "/ipc/controlIpc", method = RequestMethod.POST)
    public void controlIpc(HttpServletRequest request, HttpServletResponse response) throws SOAPException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Headers", "X-Requested-With,content-type");
        response.setHeader("Access-Control-Allow-Methods", "POST,GET");
        response.setHeader("X-Powered-By", "3.2.1");
        net.sf.json.JSONObject jsonObject = new net.sf.json.JSONObject();
        String result = "";
        String ipcId = request.getParameter("ipcId");
        //command
        String command = request.getParameter("type");
        //事件类型 click 单次点击事件  press 长按事件  release 释放长按事件
        String eventType = request.getParameter("eventType");
        String Ip = RequestUtil.getIpAddr(request);
        //处理并发操作
        if (StringUtil.isEmptyOrNull(ipcId)) {
            jsonObject.put("success", false);
            jsonObject.put("errMsg", "摄像头Id不能为空");
            jsonObject.put("data", "  ");
            ResponseUtil.printAjax(response, jsonObject);
            return;
        }
        if (StringUtil.isEmptyOrNull(command)) {
            jsonObject.put("success", false);
            jsonObject.put("errMsg", "指令不能为空");
            jsonObject.put("data", "");
            ResponseUtil.printAjax(response, jsonObject);
            return;
        }
        result = ChanleMap.sendMessageToUser("111", "controlIpc", ipcId, command, eventType);
        if (StringUtil.isNotEmptyOrNull(result)) {
            jsonObject.put("success", true);
            jsonObject.put("errMsg", "");
            jsonObject.put("data", result);
            ResponseUtil.printAjax(response, jsonObject);
        } else {
            jsonObject.put("success", false);
            jsonObject.put("errMsg", "客户端未连接");
            jsonObject.put("data", "");
            ResponseUtil.printAjax(response, jsonObject);
        }
    }

    /**
     * 读字符串类型的文件
     *
     * @param file 文件对象
     * @return 对应文件字符串
     */
    private String stringFile(String file) {
        InputStreamReader reader = null;
        InputStream inputStream = null;
        BufferedReader br = null;
        StringBuilder configStr = new StringBuilder();
        try {
            inputStream = new FileInputStream(StringUtil.formatFilePath(file));
            reader = new InputStreamReader(inputStream, "UTF-8");
            br = new BufferedReader(reader);
            String temp;
            while ((temp = br.readLine()) != null) {
                configStr.append(temp).append("\r\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return configStr.toString();
    }
}