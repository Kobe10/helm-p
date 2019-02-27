import com.shfb.oframe.core.util.common.*
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

/**
 * Created by Administrator on 2015/11/10.
 */
class sys019 extends GroovyController {

    /**
     * 初始化
     *
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/oframe/sysmg/sys019/sys019", modelMap);
    }

    /**
     * 初始化修改页面
     */
    public ModelAndView editView(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String messageDefId = request.getParameter("messageDefId");
        if (StringUtil.isNotEmptyOrNull(messageDefId)) {
            XmlBean opData = new XmlBean();
            opData.setStrValue("OpData.MessageDef.messageDefId", messageDefId);
            //调用服务查询数据
            svcRequest.addOp("QUERY_MESSAGE_DEF_CMPT", opData);
            SvcResponse svcResponse = query(svcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean msgInfo = svcResponse.getFirstOpRsp("QUERY_MESSAGE_DEF_CMPT");
                if (msgInfo != null) {
                    List<XmlNode> resultList = new ArrayList();
                    int rowCount = msgInfo.getListNum("Operation.OpResult.MessageDefs.MessageDef[0].messageProducers.messageProducer");
                    for (int i = 0; i < rowCount; i++) {
                        XmlBean list = msgInfo.getBeanByPath("Operation.OpResult.MessageDefs.MessageDef[0].messageProducers.messageProducer[" + i + "]");
                        resultList.add(list.getRootNode());
                    }
                    modelMap.put("messageProducer", resultList);
                    resultList = new ArrayList();
                    rowCount = msgInfo.getListNum("Operation.OpResult.MessageDefs.MessageDef[0].messageConsumers.messageConsumer");
                    for (int i = 0; i < rowCount; i++) {
                        XmlBean list = msgInfo.getBeanByPath("Operation.OpResult.MessageDefs.MessageDef[0].messageConsumers.messageConsumer[" + i + "]");
                        resultList.add(list.getRootNode());
                    }
                    modelMap.put("messageConsumer", resultList);
                    modelMap.put("MessageDef", msgInfo.getBeanByPath("Operation.OpResult.MessageDefs.MessageDef[0]").getRootNode());
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
                map.put("0", "通用");
                map.put("-1", "腾退项目");
                map.put("-2", "征收项目");
                for (int i = 0; i < count; i++) {
                    map.put(prjInfo.getStrValue("PageData.Row[" + i + "].prjCd"), prjInfo.getStrValue("PageData.Row[" + i + "].prjName"));
                }
                modelMap.put("prjCdMap", map);
            }
        }
        //查询伪属性
        svcRequest = RequestUtil.getSvcRequest(request);
        String flag = request.getParameter("flag");
        opData = new XmlBean();
        opData.setStrValue("OpData.MessageDef.messageDefId", messageDefId);
        //调用服务查询数据
        svcRequest.addOp("QUERY_MESSAGE_DEF_CMPT", opData);
        svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean msgInfo = svcResponse.getFirstOpRsp("QUERY_MESSAGE_DEF_CMPT");
            List<XmlNode> resultList = new ArrayList();
            if (msgInfo != null && flag == "pro") {
                int rowCount = msgInfo.getListNum("Operation.OpResult.MessageDefs.MessageDef[0].messageProducers.messageProducer");
                for (int i = 0; i < rowCount; i++) {
                    XmlBean list = msgInfo.getBeanByPath("Operation.OpResult.MessageDefs.MessageDef[0].messageProducers.messageProducer[" + i + "]");
                    resultList.add(list.getRootNode());
                }
                modelMap.put("messageProducer", resultList);
            } else {
                int rowCount = msgInfo.getListNum("Operation.OpResult.MessageDefs.MessageDef[0].messageConsumers.messageConsumer");
                for (int i = 0; i < rowCount; i++) {
                    XmlBean list = msgInfo.getBeanByPath("Operation.OpResult.MessageDefs.MessageDef[0].messageConsumers.messageConsumer[" + i + "]");
                    resultList.add(list.getRootNode());
                }
                modelMap.put("messageConsumer", resultList);
            }
        }
        return new ModelAndView("/oframe/sysmg/sys019/sys019_add", modelMap);
    }

    /**
     * 初始化发送消息界面
     */
    public ModelAndView sendView(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String messageDefId = request.getParameter("messageDefId");
        modelMap.put("messageDefId", messageDefId);
        return new ModelAndView("/oframe/sysmg/sys019/sys019_sendView", modelMap);
    }

    /**
     * 触发消息
     */
    public void sendViewMessage(HttpServletRequest request, HttpServletResponse response){
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String messageDefId = request.getParameter("messageDefId");
        String message = request.getParameter("message");
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.messageDefId", messageDefId);
        opData.setStrValue("OpData.message", message);
        svcRequest.addOp("AUTO_SEND_MESSAGE_CMPT",opData);
        SvcResponse svcResponse = transaction(svcRequest);
        String result = svcResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg();
        String jsonStr = """{success:${result},errMsg:"${errMsg}"}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

    /**
     * 新增、修改
     * */

    public ModelAndView add(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);

        String messageDefId = request.getParameter("messageDefId");
        String messageDefType = request.getParameter("messageDefType");
        String messageCreateTime = request.getParameter("messageCreateTime");
        messageCreateTime = DateUtil.toStringYmdHms(DateUtil.toDateYmdWthH(messageCreateTime));
        String dealTemplate = request.getParameter("dealTemplate");
        String messageDefTarget = request.getParameter("messageDefTarget");
        String messageDefContent = request.getParameter("messageDefContent");
        String messageDefDesc = request.getParameter("messageDefDesc");
        //生产者
        String[] producerType = request.getParameterValues("producerType");
        String[] producerEntity = request.getParameterValues("producerEntity");
        String[] producerName = request.getParameterValues("producerName");
        String[] producerCondition = request.getParameterValues("producerCondition");
        String[] producerState = request.getParameterValues("producerState");
        String[] producerCreateTime = request.getParameterValues("producerCreateTime");
        String[] producerRuleCd = request.getParameterValues("producerRuleCd");
        //消费者
        String[] consumerName = request.getParameterValues("consumerName");
        String[] consumerCreateTime = request.getParameterValues("consumerCreateTime");
        String[] consumerState = request.getParameterValues("consumerState");
        String[] consumerDesc = request.getParameterValues("consumerDesc");

        XmlBean opData = new XmlBean();
        String nodePath = "OpData.MessageDefs.MessageDef.";
        if (StringUtil.isNotEmptyOrNull(producerName) && producerName.length >= 1) {
            int prodNum = 0;
            for (int i = 0; i < producerName.length; i++) {
                if (StringUtil.isEmptyOrNull(producerName[i])) {
                    continue;
                }
                //生产者
                opData.setStrValue(nodePath + "messageProducers.messageProducer[" + prodNum + "].producerType", producerType[i]);
                opData.setStrValue(nodePath + "messageProducers.messageProducer[" + prodNum + "].producerEntity", producerEntity[i]);
                opData.setStrValue(nodePath + "messageProducers.messageProducer[" + prodNum + "].producerName", producerName[i]);
                opData.setStrValue(nodePath + "messageProducers.messageProducer[" + prodNum + "].producerCondition", producerCondition[i]);
                opData.setStrValue(nodePath + "messageProducers.messageProducer[" + prodNum + "].producerRuleCd", producerRuleCd[i]);
                opData.setStrValue(nodePath + "messageProducers.messageProducer[" + prodNum + "].producerState", producerState[i]);
                opData.setStrValue(nodePath + "messageProducers.messageProducer[" + prodNum++ + "].producerCreateTime", DateUtil.toStringYmdHms(DateUtil.toDateYmdWthH(producerCreateTime[i])));
            }
        }
        if (StringUtil.isNotEmptyOrNull(consumerName) && consumerName.length >= 1) {
            int prodNum = 0;
            for (int i = 0; i < consumerName.length; i++) {
                if (StringUtil.isEmptyOrNull(consumerName[i])) {
                    continue;
                }
                //消费者
                opData.setStrValue(nodePath + "messageConsumers.messageConsumer[" + prodNum + "].consumerName", consumerName[i]);
                opData.setStrValue(nodePath + "messageConsumers.messageConsumer[" + prodNum + "].consumerCreateTime", DateUtil.toStringYmdHms(DateUtil.toDateYmdWthH(consumerCreateTime[i])));
                opData.setStrValue(nodePath + "messageConsumers.messageConsumer[" + prodNum + "].consumerState", consumerState[i]);
                opData.setStrValue(nodePath + "messageConsumers.messageConsumer[" + prodNum++ + "].consumerDesc", consumerDesc[i]);
            }
        }

        if (StringUtil.isNotEmptyOrNull(messageDefId)) {
            //修改
            opData.setStrValue(nodePath + "messageDefId", messageDefId);
        } else {
            //新增
            opData.setStrValue(nodePath + "messageDefId", "\${messageDefId}");
        }
        //消息
        opData.setStrValue(nodePath + "entityName", "MessageDef");
        opData.setStrValue(nodePath + "messageDefType", messageDefType);
        opData.setStrValue(nodePath + "messageCreateTime", messageCreateTime);
        opData.setStrValue(nodePath + "dealTemplate", dealTemplate);
        opData.setStrValue(nodePath + "messageDefTarget", messageDefTarget);
        opData.setStrValue(nodePath + "messageDefContent", messageDefContent);
        opData.setStrValue(nodePath + "messageDefDesc", messageDefDesc);
        //调用组件
        svcRequest.addOp("SAVE_MESSAGE_DEF_CMPT", opData);
        SvcResponse svcResponse = transaction(svcRequest);
        String result = svcResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg();
        String jsonStr = """{success:${result},errMsg:"${errMsg}"}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

    /**
     * 删除
     */
    public void deleteView(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String messageDefIds = request.getParameter("messageDefId");
        SvcResponse svcResponse = null;
        if (messageDefIds.indexOf(",") > 0) {
            String[] messageDefId = messageDefIds.split(",");
            for (int i = 0; i < messageDefId.length; i++) {
                XmlBean opData = new XmlBean();
                opData.setStrValue("OpData.MessageDef.messageDefId", messageDefId[i]);

                svcRequest.addOp("DELETE_MESSAGE_DEF_CMPT", opData);
                svcResponse = transaction(svcRequest);
            }
        } else {
            XmlBean opData = new XmlBean();
            opData.setStrValue("OpData.MessageDef.messageDefId", messageDefIds);
            svcRequest.addOp("DELETE_MESSAGE_DEF_CMPT", opData);
            svcResponse = transaction(svcRequest);
        }
        String result = svcResponse.isSuccess();
        String errMsg = svcResponse.getErrMsg();
        String jsonStr = """{success:${result},errMsg:"${errMsg}"}""";
        ResponseUtil.printAjax(response, jsonStr);
    }

    /**
     * 批量导出
     */
    public void batchExport(HttpServletRequest request, HttpServletResponse response) {
        String messageDefIds = request.getParameter("messageDefIds");

        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.MessageDef.messageDefId", messageDefIds);
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.addOp("QUERY_MESSAGE_DEF_CMPT", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean entity = svcResponse.getFirstOpRsp("QUERY_MESSAGE_DEF_CMPT").getBeanByPath("Operation.OpResult.MessageDefs");
            String fileName = UUID.randomUUID().toString() + ".xml";
            File xmlFile = ServerFile.createFile("export" + File.separator, fileName);
            xmlFile.write(entity.toXML(), "UTF-8");
            // 文件下载输出到响应流
            messageDefIds = messageDefIds.replaceAll(",", " ");
            ResponseUtil.downloadFile(response, null, "${messageDefIds}.xml", xmlFile, request.getHeader("USER-AGENT"));
        }
    }

    /**
     * 导入xml
     */
    public ModelAndView importData(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        //XmlBean resultXmlBean = new XmlBean();
        //获取传入文件
        MultipartFile localFile = super.getFile(request, "importMsgFile");
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
                svcRequest.addOp("IMPORT_MESSAGE_DEF_CMPT", opData);
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

    /**
     * 快速查询
     */
    public ModelAndView queryKs(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String prjCd = request.getParameter("prjCd");
        modelMap.put("prjCd", prjCd);
        String flag = request.getParameter("flag");
        modelMap.put("flag", flag);
        String messageDefType = request.getParameter("messageDefType");
        String consumerName = request.getParameter("consumerName");
        String producerName = request.getParameter("producerName");
        //拼接请求参数
        XmlBean opData = new XmlBean();
        if (StringUtil.isNotEmptyOrNull(messageDefType)) {
            opData.setStrValue("OpData.MessageDef.messageDefType", messageDefType);
        } else if (StringUtil.isNotEmptyOrNull(consumerName)) {
            opData.setStrValue("OpData.MessageDef.messageConsumer.consumerName", consumerName);
        } else if (StringUtil.isNotEmptyOrNull(producerName)) {
            opData.setStrValue("OpData.MessageDef.messageProducer.producerName", producerName);
        }

        // 调用服务
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.addOp("QUERY_MESSAGE_DEF_CMPT", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean queryResult = svcResponse.getFirstOpRsp("QUERY_MESSAGE_DEF_CMPT").getBeanByPath("Operation.OpResult");
            // 处理查询结果，返回json格式数据
            List<XmlNode> resultList = new ArrayList<>();
            if (queryResult != null) {
                int rowCount = queryResult.getListNum("OpResult.MessageDefs.MessageDef");
                for (int i = 0; i < rowCount; i++) {
                    XmlBean list = queryResult.getBeanByPath("OpResult.MessageDefs.MessageDef[${i}]");
                    resultList.add(list.getRootNode());
                }
                modelMap.put("resultList", resultList);
            }
        }
        String toPage = "/oframe/sysmg/sys019/sys019_list";
        return new ModelAndView(toPage, modelMap);
    }

    /**
     * iframe
     */
    public ModelAndView msgCode(HttpServletRequest request, HttpServletResponse response) {
        // 页面返回视图
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/oframe/sysmg/sys019/sys019_msg_code", modelMap);
    }


}














