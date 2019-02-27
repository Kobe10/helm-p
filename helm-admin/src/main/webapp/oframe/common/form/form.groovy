import com.shfb.oframe.core.util.common.DateUtil
import com.shfb.oframe.core.util.common.NumberUtil
import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.common.XmlNode
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import groovy.json.JsonSlurper
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.text.SimpleDateFormat

/**
 * User: shfb_wang 
 * Date: 2016/4/5 0005 11:01
 * Copyright(c) 北京四海富博计算机服务有限公司
 */

class form extends GroovyController {

    /**
     * 根据类型加载附件信息
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public ModelAndView initDocs(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String relId = request.getParameter("relId");
        String relType = request.getParameter("relType");
        String docTypeName = request.getParameter("docTypeName");
        if (StringUtil.isEmptyOrNull(docTypeName)) {
            return initDocsFolder(request, response);
        }
        //查询指定房产的 交房信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        if (StringUtil.isNotEmptyOrNull(relId)) {
            //查询 交房确认单 附件
            svcRequest = RequestUtil.getSvcRequest(request);
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
            opData.setStrValue(prePath + "Conditions.Condition[2].fieldName", "docTypeName");
            opData.setStrValue(prePath + "Conditions.Condition[2].fieldValue", docTypeName);
            opData.setStrValue(prePath + "Conditions.Condition[2].operation", "=");
            opData.setStrValue(prePath + "Conditions.Condition[3].fieldName", "statusCd");
            opData.setStrValue(prePath + "Conditions.Condition[3].fieldValue", "1");
            opData.setStrValue(prePath + "Conditions.Condition[3].operation", "=");
            // 需要查询的字段
            opData.setStrValue(prePath + "ResultFields.fieldName[0]", "docId");
            opData.setStrValue(prePath + "ResultFields.fieldName[1]", "docTypeName");
            opData.setStrValue(prePath + "ResultFields.fieldName[2]", "docName");

            // 增加查询
            svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
            SvcResponse svcRsp = query(svcRequest);
            //返回处理结果
            if (svcRsp.isSuccess()) {
                XmlBean queryResult = svcRsp.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult");
                // 处理查询结果，返回json格式数据
                if (queryResult != null) {
                    int rowCount = queryResult.getListNum("OpResult.PageData.Row");
                    List<XmlNode> docs = new ArrayList<XmlNode>(rowCount);
                    for (int i = 0; i < rowCount; i++) {
                        docs.add(queryResult.getBeanByPath("OpResult.PageData.Row[${i}]").getRootNode());
                    }
                    modelMap.put("docs", docs);
                }
            }
        }
        return new ModelAndView("/oframe/common/form/form_docs", modelMap);
    }

    /**
     * 根据类型加载附件信息
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public ModelAndView initDocsFolder(HttpServletRequest request, HttpServletResponse response) {

        //基本信息  产权信息 单独查询
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String relId = request.getParameter("relId");
        String prjCd = request.getParameter("prjCd");
        // 获取可用房源区域列表
        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        opData.setStrValue(prePath + "entityName", "SvcDocInfo");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "relType");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", "100");
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
        opData.setStrValue(prePath + "Conditions.Condition[1].fieldName", "relId");
        opData.setStrValue(prePath + "Conditions.Condition[1].fieldValue", relId);
        opData.setStrValue(prePath + "Conditions.Condition[1].operation", "=");
        //只查询状态为 1 的，
        opData.setStrValue(prePath + "Conditions.Condition[2].fieldName", "statusCd");
        opData.setStrValue(prePath + "Conditions.Condition[2].fieldValue", "1");
        opData.setStrValue(prePath + "Conditions.Condition[2].operation", "=");

        opData.setStrValue(prePath + "Conditions.Condition[3].fieldName", "prjCd");
        opData.setStrValue(prePath + "Conditions.Condition[3].fieldValue", prjCd);
        opData.setStrValue(prePath + "Conditions.Condition[3].operation", "=");
        // 需要查询的字段
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "docId");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "docTypeName");
        // 增加查询
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
        SvcResponse svcResponse = query(svcRequest);
        // 返回处理结果
        ModelMap modelMap = new ModelMap();

        if (svcResponse.isSuccess()) {
            XmlBean queryResult = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA").getBeanByPath("Operation.OpResult");
            // 处理查询结果，返回json格式数据
            if (queryResult != null) {
                int rowCount = queryResult.getListNum("OpResult.PageData.Row");
                Map<String, List<String>> dataMap = new LinkedHashMap<String, List<String>>();
                for (int i = 0; i < rowCount; i++) {
                    String tempField01 = queryResult.getStrValue("OpResult.PageData.Row[${i}].docId");
                    String tempField02 = queryResult.getStrValue("OpResult.PageData.Row[${i}].docTypeName");
                    if ("fileUpLoad".equals(tempField02)) {
                        tempField02 = "其他";
                    }
                    List<String> tempItem = dataMap.get(tempField02);
                    if (tempItem == null) {
                        tempItem = new ArrayList<String>();
                        dataMap.put(tempField02, tempItem);
                    }
                    tempItem.add(tempField01);
                }
                List<Map<String, String>> attachTypeSummary = new ArrayList<Map<String, String>>();
                for (Map.Entry<String, List<String>> entry : dataMap) {
                    List<String> temp = entry.getValue();
                    attachTypeSummary.add(["name": entry.getKey(), "count": entry.getValue().size(), "ids": temp.join(",")])
                }

                modelMap.put("attachTypeSummary", attachTypeSummary);
            }
        }
        modelMap.put("relId", relId);
        return new ModelAndView("/oframe/common/form/form_docs_folder", modelMap);
    }

    /**
     * TODO: 保存通用 配置模板方法
     * @param request 请求信息
     * @param response 响应结果
     */
    public void saveCommonForm(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        int prjCd = NumberUtil.intValueOf(request.getParameter("prjCd"));
        /**
         * 拼接 表单基本信息 报文
         */
        Map<String, String[]> reqParamMap = request.getParameterMap();

        // 创建以某一个实体为 key 的map
        Map<String, XmlBean> entityMap = new LinkedHashMap<String, XmlBean>();
        //控制页面是否可以保存
        boolean isCanSave = true;
        //entityMap = [HouseInfo_20 : updateEntity]
        if (reqParamMap != null) {
            Iterator<String> it = reqParamMap.keySet().iterator();
            while (it.hasNext()) {
                String keyName = it.next();             // HouseInfo_hsId_20
                if (!StringUtil.contains(keyName, '_')) {
                    continue;
                }
                String[] keyParams = keyName.split("_");
                if (keyParams.length != 3) {
                    continue;
                }
                String entityPriKey = keyParams[1];   //hsId

                //获取 参数里 的  value[]
                String[] listValue = reqParamMap.get(keyName);
                String value = null;
                if (listValue.length == 1) {
                    value = listValue[0];
                } else {
                    //结果值 如果不止 一个 需要 处理
                    value = listValue.join(",");
                }

                String priKey = keyParams[0] + "_" + keyParams[2];     //HouseInfo_20
                XmlBean updateEntity = entityMap.get(priKey);
                if (updateEntity == null) {
                    updateEntity = new XmlBean();
                    updateEntity.setStrValue("OpData.entityName", keyParams[0]);
                    updateEntity.setStrValue("OpData.EntityData.${entityPriKey}", value);
                    entityMap.put(priKey, updateEntity);
                } else {
                    updateEntity.setStrValue("OpData.EntityData.${entityPriKey}", value);
                }
            }
            for (Object temp : entityMap.keySet()) {
                XmlBean tempBean = entityMap.get(temp);
                Map<String, String> controlMap = getCfgCollection(request, "SAVE_CONTROL_BY_DOC", true, prjCd);
                //获取处理状态(交房状态、拆除状态等)
                String status = "";
                //是否需要控制保存按钮
                boolean isControl = false;
                String docTypeName = "";
                for (Object obj : controlMap.keySet()) {
                    status = tempBean.getStrValue("OpData.EntityData.${obj}");
                    if (StringUtil.isNotEmptyOrNull(status)) {
                        isControl = true;
                        docTypeName = controlMap.get(obj);
                        break;
                    }
                }
                //获取交房时间
                String date = tempBean.getStrValue("OpData.EntityData.hsHandleDate");
                if (isControl) {
                    String hsId = tempBean.getStrValue("OpData.EntityData.hsId");
                    //查询有没有上传附件，若没有，则不允许保存
                    XmlBean queryDoc = new XmlBean();
                    String prePath = "OpData.";
                    queryDoc.setStrValue(prePath + "entityName", "SvcDocInfo");
                    queryDoc.setStrValue(prePath + "Conditions.Condition[0].fieldName", "docTypeName");
                    queryDoc.setStrValue(prePath + "Conditions.Condition[0].fieldValue", docTypeName);
                    queryDoc.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
                    queryDoc.setStrValue(prePath + "Conditions.Condition[1].fieldName", "relId");
                    queryDoc.setStrValue(prePath + "Conditions.Condition[1].fieldValue", hsId);
                    queryDoc.setStrValue(prePath + "Conditions.Condition[1].operation", "=");
                    queryDoc.setStrValue(prePath + "Conditions.Condition[2].fieldName", "statusCd");
                    queryDoc.setStrValue(prePath + "Conditions.Condition[2].fieldValue", "1");
                    queryDoc.setStrValue(prePath + "Conditions.Condition[2].operation", "=");
                    // 需要查询的字段
                    queryDoc.setStrValue(prePath + "ResultFields.fieldName", "docId");
                    // 增加查询
                    svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", queryDoc);
                    SvcResponse svcResponse = query(svcRequest);
                    if (svcResponse.isSuccess()) {
                        XmlBean queryResult = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA")
                                .getBeanByPath("Operation.OpResult.PageData");
                        if (queryResult == null) {
                            isCanSave = false;
                        } else {
                            //已交房，没有时间的为第一次登记，获取当前时间保存
                            if (StringUtil.isEqual("2", status) && StringUtil.isEmptyOrNull(date)) {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                String dayTime = sdf.format(DateUtil.getSysDate()).toString();
                                tempBean.setStrValue("OpData.EntityData.hsHandleDate", dayTime);
                            } else if (StringUtil.isEqual("1", status)) {
                                //没交房，清空时间
                                tempBean.setStrValue("OpData.EntityData.hsHandleDate", "");
                            }
                        }
                    }
                }
                if (isCanSave) {
                    svcRequest.addOp("SAVE_ENTITY", tempBean);
                }
            }
        }
        if (isCanSave) {
            // 保存验证信息
            String checkDataJsonStr = request.getParameter("checkDataJson");
            if (StringUtil.isNotEmptyOrNull(checkDataJsonStr)) {
                def jsonSlurper = new JsonSlurper()
                // 以下代码出取值,其他逻辑与form.groovy中大表单提交一致
                // 将提交的对象转换为JSON对象
                def checkDataJson = jsonSlurper.parseText(checkDataJsonStr);
                // 获取最新代码
                String checkId = checkDataJson.checkId;
                if (StringUtil.isEmptyOrNull(checkId)) {
                    checkId = "\${checkId}";
                }
                String checkRelId = checkDataJson.checkRelId;
                String subRelId = checkDataJson.subRelId;
                String entityKey = checkRelId;
                XmlBean rowData = new XmlBean();
                rowData.setStrValue("OpData.entityName", "CheckRecord");
                rowData.setStrValue("OpData.EntityData.checkId", checkId);
                rowData.setStrValue("OpData.EntityData.checkTime", svcRequest.getReqTime());
                rowData.setStrValue("OpData.EntityData.checkStaff", svcRequest.getReqStaff());
                rowData.setStrValue("OpData.EntityData.checkRelId", checkRelId);
                rowData.setStrValue("OpData.EntityData.checkSubRelId", subRelId);
                rowData.setStrValue("OpData.EntityData.checkRelType", checkDataJson.checkRelType);
                rowData.setStrValue("OpData.EntityData.checkResult", checkDataJson.checkResult);
                rowData.setStrValue("OpData.EntityData.checkOpName", checkDataJson.checkOpName);
                rowData.setStrValue("OpData.EntityData.checkNote", checkDataJson.checkNote);
                rowData.setStrValue("OpData.EntityData.modifyTransactionId", svcRequest.getReqId());
                svcRequest.addOp("SAVE_CHECK_INFO", rowData);
                // 更新关联实体的状态
                String entityProperty = checkDataJson.entityProperty;
                String successStatus = checkDataJson.successStatus;
                String backStatus = checkDataJson.backStatus;
                // 审批不通过
                String newStatus = successStatus;
                if (StringUtil.isEqual("2", checkDataJson.checkResult)) {
                    newStatus = backStatus;
                }
                if (StringUtil.isNotEmptyOrNull(entityProperty) && StringUtil.isNotEmptyOrNull(newStatus)) {
                    String[] entityInfo = entityProperty.split("_");
                    if (entityInfo.length == 3) {
                        rowData = new XmlBean();
                        rowData.setStrValue("OpData.entityName", entityInfo[0]);
                        rowData.setStrValue("OpData.EntityData." + entityInfo[1], entityKey);
                        rowData.setStrValue("OpData.EntityData." + entityInfo[2], newStatus);
                        svcRequest.addOp("SAVE_ENTITY", rowData);
                    }
                }
            }
            // 保存交易流水信息
            SvcResponse svcResponse = transaction(svcRequest);
            ResponseUtil.printSvcResponse(response, svcResponse, null);
        } else {
            String errMsg = "请上传附件后，再进行保存";
            String jsonStr = """{success:false, errMsg:"${errMsg}"}""";
            ResponseUtil.printAjax(response, jsonStr);
        }
    }

    /**
     * 表单通用保存指定属性
     * @param request 请求信息
     * @param response 响应结果
     */
    public void saveProperty(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        /**
         * 拼接 表单基本信息 报文
         */
        Map<String, String[]> reqParamMap = request.getParameterMap();

        String entityName = request.getParameter("entityName");
        String propertyName = request.getParameter("propertyName");
        String hsId = "";
        String hsCtId = "";
        String propertyValue = "";
        //entityMap = [HouseInfo_20 : updateEntity]
        if (reqParamMap != null) {
            Iterator<String> it = reqParamMap.keySet().iterator();
            while (it.hasNext()) {
                String keyName = it.next();             // HouseInfo_hsId_20
                if (!StringUtil.contains(keyName, '_')) {
                    continue;
                }
                String[] keyParams = keyName.split("_");
                if (keyParams.length != 3) {
                    continue;
                }
                String entityKey = keyParams[0];   //houseInfo
                String entityPriKey = keyParams[1];   //hsId
                if (StringUtil.isEqual(entityName, entityKey) && StringUtil.isEqual(propertyName, entityPriKey)) {
                    //获取 参数里 的  value[]
                    propertyValue = reqParamMap.get(keyName)[0];
                }
                if (entityKey == "HouseInfo" && entityPriKey == "hsId") {
                    hsId = reqParamMap.get(keyName)[0];
                }
                if (entityKey == "HsCtInfo" && entityPriKey == "hsCtId") {
                    hsCtId = reqParamMap.get(keyName)[0];
                }
            }
            XmlBean opData = new XmlBean();
            opData.setStrValue("OpData.entityName", entityName);
            if (StringUtil.isEqual("HouseInfo", entityName)) {
                opData.setStrValue("OpData.EntityData.hsId", hsId);
            } else if (StringUtil.isEqual("HsCtInfo", entityName)) {
                opData.setStrValue("OpData.EntityData.hsCtId", hsCtId);
            }
            opData.setStrValue("OpData.EntityData.${propertyName}", propertyValue);
            svcRequest.addOp("SAVE_ENTITY", opData);
            SvcResponse svcResponse = transaction(svcRequest);
            ResponseUtil.printSvcResponse(response, svcResponse, null);
        }
    }

    /**
     * 打开提交申请对话框
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public ModelAndView checkApply(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String checkRelId = request.getParameter("checkRelId");
        String[] checkRelIds = checkRelId.split("\\|");
        String subRelId = checkRelIds[0];
        checkRelId = checkRelIds[0];
        if (checkRelIds.length > 1) {
            subRelId = checkRelIds[1];
        } else {
            subRelId = "";
        }
        String checkRelType = request.getParameter("checkRelType");
        String entityProperty = request.getParameter("entityProperty");
        String successStatus = request.getParameter("successStatus");
        String checkOpName = request.getParameter("checkOpName");
        if(StringUtil.isNotEmptyOrNull(checkOpName)) {
            checkOpName = java.net.URLDecoder.decode(checkOpName, "UTF-8");
        }
        modelMap.put("checkRelId", checkRelId);
        modelMap.put("subRelId", subRelId);
        modelMap.put("checkRelType", checkRelType);
        modelMap.put("entityProperty", entityProperty);
        modelMap.put("successStatus", successStatus);
        modelMap.put("checkOpName", checkOpName);
        modelMap.put("checkResult", request.getParameter("checkResult"));
        modelMap.put("submitFunc", request.getParameter("submitFunc"));
        return new ModelAndView("/oframe/common/form/form_check_apply", modelMap);
    }

    /**
     * 打开验证申请处理对话框
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public ModelAndView checkDone(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String checkRelId = request.getParameter("checkRelId");
        String[] checkRelIds = checkRelId.split("\\|");
        String subRelId = checkRelIds[0];
        checkRelId = checkRelIds[0];
        if (checkRelIds.length > 1) {
            subRelId = checkRelIds[1];
        } else {
            subRelId = "";
        }
        String checkRelType = request.getParameter("checkRelType");
        String entityProperty = request.getParameter("entityProperty");
        String successStatus = request.getParameter("successStatus");
        String backStatus = request.getParameter("backStatus");
        String checkOpName = request.getParameter("checkOpName");
        if(StringUtil.isNotEmptyOrNull(checkOpName)) {
            checkOpName = java.net.URLDecoder.decode(checkOpName, "UTF-8");
        }
        modelMap.put("checkRelId", checkRelId);
        modelMap.put("subRelId", subRelId);
        modelMap.put("checkRelType", checkRelType);
        modelMap.put("entityProperty", entityProperty);
        modelMap.put("successStatus", successStatus);
        modelMap.put("backStatus", backStatus);
        modelMap.put("checkOpName", checkOpName);
        modelMap.put("submitFunc", request.getParameter("submitFunc"));
        return new ModelAndView("/oframe/common/form/form_check_done", modelMap);
    }

    /**
     * 打开验证申请处理对话框
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public ModelAndView checkLog(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String checkRelId = request.getParameter("checkRelId");
        String[] checkRelIds = checkRelId.split("\\|");
        String subRelId = checkRelIds[0];
        checkRelId = checkRelIds[0];
        if (checkRelIds.length > 1) {
            subRelId = checkRelIds[1];
        } else {
            subRelId = "";
        }

        String checkRelType = request.getParameter("checkRelType");
        modelMap.put("checkRelId", checkRelId);
        modelMap.put("subRelId", subRelId);
        modelMap.put("checkRelType", checkRelType);
        return new ModelAndView("/oframe/common/form/form_check_log", modelMap);
    }

    /**
     * 保存验证记录
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public void saveCheck(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 保存验证信息
        String checkDataJsonStr = request.getParameter("checkDataJson");
        // 以下代码出取值,其他逻辑与form.groovy中大表单提交一致
        def jsonSlurper = new JsonSlurper()
        def checkDataJson = jsonSlurper.parseText(checkDataJsonStr);
        // 获取最新代码
        String checkId = checkDataJson.checkId;
        if (StringUtil.isEmptyOrNull(checkId)) {
            checkId = "\${checkId}";
        }
        String checkRelId = checkDataJson.checkRelId;
        String subRelId = checkDataJson.subRelId;
        String entityKey = checkRelId;
        XmlBean rowData = new XmlBean();
        rowData.setStrValue("OpData.entityName", "CheckRecord");
        rowData.setStrValue("OpData.EntityData.checkId", checkId);
        rowData.setStrValue("OpData.EntityData.checkTime", svcRequest.getReqTime());
        rowData.setStrValue("OpData.EntityData.checkStaff", svcRequest.getReqStaff());
        rowData.setStrValue("OpData.EntityData.checkRelId", checkRelId);
        rowData.setStrValue("OpData.EntityData.checkSubRelId", subRelId);
        rowData.setStrValue("OpData.EntityData.checkRelType", checkDataJson.checkRelType);
        rowData.setStrValue("OpData.EntityData.checkResult", checkDataJson.checkResult);
        rowData.setStrValue("OpData.EntityData.checkOpName", checkDataJson.checkOpName);
        rowData.setStrValue("OpData.EntityData.checkNote", checkDataJson.checkNote);
        rowData.setStrValue("OpData.EntityData.modifyTransactionId", svcRequest.getReqId());
        svcRequest.addOp("SAVE_CHECK_INFO", rowData);
        /* 更新关联实体的状态 */
        String entityProperty = checkDataJson.entityProperty;
        String successStatus = checkDataJson.successStatus;
        String backStatus = checkDataJson.backStatus;
        // 审批不通过
        String newStatus = successStatus;
        if (StringUtil.isEqual("2", checkDataJson.checkResult)) {
            newStatus = backStatus;
        }
        if (StringUtil.isNotEmptyOrNull(entityProperty) && StringUtil.isNotEmptyOrNull(newStatus)) {
            String[] entityInfo = entityProperty.split("_");
            if (entityInfo.length == 3) {
                rowData = new XmlBean();
                rowData.setStrValue("OpData.entityName", entityInfo[0]);
                rowData.setStrValue("OpData.EntityData." + entityInfo[1], entityKey);
                rowData.setStrValue("OpData.EntityData." + entityInfo[2], newStatus);
                svcRequest.addOp("SAVE_ENTITY", rowData);
            }
        }
        SvcResponse svcRsp = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcRsp, "");
    }

}