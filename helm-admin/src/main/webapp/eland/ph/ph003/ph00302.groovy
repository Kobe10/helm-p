import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import groovy.json.JsonSlurper
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 人地房  房产信息展示页面
 */
class ph00302 extends GroovyController {

    /**
     * 查询人地房信息的Json字符串
     * @param request
     * @param response
     * @return
     */
    public ModelAndView queryHsJson(HttpServletRequest request, HttpServletResponse response) {
        // 输入参数
        RequestUtil requestUtil = new RequestUtil(request);
        String hsId = requestUtil.getStrParam("hsId");
        String rhtType = requestUtil.getStrParam("rhtType");
        /**
         * 查询房产下所有录入信息
         */
        SvcResponse svcResponse = null;
        if (StringUtil.isNotEmptyOrNull(hsId)) {
            SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
            XmlBean opData = new XmlBean();
            opData.setStrValue("OpData.hsId", hsId);
            opData.setStrValue("OpData.rhtType", rhtType);
            svcRequest.addOp("QUERY_PBH_CMPT", opData);

            // 签约信息
            opData = new XmlBean();
            opData.setStrValue("OpData.entityName", "HouseInfo");
            opData.setStrValue("OpData.queryType", "2");
            opData.setStrValue("OpData.Conditions.Condition[0].fieldName", "HouseInfo.hsId");
            opData.setStrValue("OpData.Conditions.Condition[0].fieldValue", hsId);
            opData.setStrValue("OpData.Conditions.Condition[0].operation", "=");
            opData.setStrValue("OpData.ResultFields.fieldName[0]", "HouseInfo.HsCtInfo.hsCtId");
            opData.setStrValue("OpData.ResultFields.fieldName[1]", "HouseInfo.HsCtInfo.ctType");
            svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);

            svcResponse = query(svcRequest);
            boolean result = svcResponse.isSuccess();
            String errMsg = svcResponse.getErrMsg();
            if (result) {
                XmlBean houseInfo = svcResponse.getFirstOpRsp("QUERY_PBH_CMPT").getBeanByPath("Operation.OpResult.HouseInfo");

                // 房屋签约信息
                XmlBean tempBean = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA");
                houseInfo.setBeanByPath("HouseInfo", tempBean.getBeanByPath("Operation.OpResult.PageData.Row[0].HouseInfo.HsCtInfo"));
                houseInfo.setStrValue("success", result);
                houseInfo.setStrValue("errMsg", errMsg);
                // 处理居民信息标记
                int flagCount = houseInfo.getListNum("HouseInfo.HsFlags.HsFlag");
                if (flagCount > 0) {
                    String[] tempArr = new String[flagCount];
                    for (int i = 0; i < flagCount; i++) {
                        tempArr[i] = houseInfo.getStrValue("HouseInfo.HsFlags.HsFlag[${i}].hsFlag");
                    }
                    houseInfo.setStrValue("HouseInfo.hsFlags", tempArr.join(","));
                }

                ResponseUtil.printAjax(response, houseInfo.toJson());
            } else {
                ResponseUtil.printAjax(response, """{success:${result}, errMsg:"${errMsg}"} """);
            }
        }
    }

    /**
     * 查询建筑信息
     * @param request
     * @param response
     * @return
     */
    public ModelAndView queryBuildJson(HttpServletRequest request, HttpServletResponse response) {
        // 输入参数
        RequestUtil requestUtil = new RequestUtil(request);
        String buildId = requestUtil.getStrParam("buildId");
        String buildAddr = requestUtil.getStrParam("buildAddr");
        if (StringUtil.isEmptyOrNull(buildId) && StringUtil.isNotEmptyOrNull(buildAddr)) {
            XmlBean tempBean = new XmlBean();
            String prePath = "OpData.";
            tempBean.setStrValue(prePath + "entityName", "BuildInfo");
            tempBean.setStrValue(prePath + "Conditions.Condition[0].fieldName", "buildFullAddr");
            tempBean.setStrValue(prePath + "Conditions.Condition[0].fieldValue", buildAddr);
            tempBean.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
            tempBean.setStrValue(prePath + "ResultFields.fieldName[0]", "buildId");
            // 调用服务
            SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
            svcRequest.addOp("QUERY_ENTITY_CMPT", tempBean);
            SvcResponse svcResponse = query(svcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean queryResult = svcResponse.getFirstOpRsp("QUERY_ENTITY_CMPT").getBeanByPath("Operation.OpResult");
                // 处理查询结果，返回json格式数据
                if (queryResult != null) {
                    int rowCount = queryResult.getListNum("OpResult.PageData.Row");
                    if (rowCount == 1) {
                        buildId = queryResult.getStrValue("OpResult.PageData.Row[0].buildId");
                    }
                }
            }
        }

        /**
         * 查询房产下所有录入信息
         */
        SvcResponse svcResponse = null;
        if (StringUtil.isNotEmptyOrNull(buildId)) {
            SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
            // 人地房基本信息
            XmlBean opData = new XmlBean();
            opData.setStrValue("OpData.entityName", "BuildInfo");
            opData.setStrValue("OpData.entityKey", buildId);
            opData.setStrValue("OpData.level", "0");
            svcRequest.addOp("QUERY_ALL_ENTITY", opData)
            //整体调用服务查询
            svcResponse = query(svcRequest);
            XmlBean buildInfo = new XmlBean();
            if (svcResponse.isSuccess()) {
                // 基本信息
                buildInfo = svcResponse.getFirstOpRsp("QUERY_ALL_ENTITY")
                        .getBeanByPath("Operation.OpResult.BuildInfo");
            }
            ResponseUtil.print(response, buildInfo.toJson());
        } else {
            ResponseUtil.print(response, "{}");
        }
    }

    /**
     * 保存建筑及房屋信息
     * @param request
     * @param response
     * @return
     */
    public void saveHsBdJson(HttpServletRequest request, HttpServletResponse response) {
        // 输入参数
        RequestUtil requestUtil = new RequestUtil(request);
        String buildInfoStr = requestUtil.getStrParam("buildInfoJson");
        String houseInfoStr = requestUtil.getStrParam("houseInfoJson");

        // 将提交的对象转换为JSON对象
        def jsonSlurper = new JsonSlurper()

        def houseInfoJson = jsonSlurper.parseText(houseInfoStr);
        String nodePath = "OpData.";
        // 处理房屋编号
        String hsId = houseInfoJson.hsId;
        boolean addFlag = false;
        if (StringUtil.isEmptyOrNull(hsId)) {
            hsId = "\${hsId}";
            addFlag = true;
            houseInfoJson.hsId = hsId;
        }
        // 调用服务准备
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean pbhRequestData = new XmlBean("<PhbData></PhbData>");
        XmlBean opData = new XmlBean();
        // 原来保存建筑信息
        if (StringUtil.isNotEmptyOrNull(buildInfoStr)) {
            def buildInfoJson = jsonSlurper.parseText(buildInfoStr);
            merge(opData, nodePath + "BuildInfo", buildInfoJson);

            //获取地址串 拼接报文
            List<String> bldRegs = buildInfoJson.regPath
            if (bldRegs != null) {
                int bn = 0;
                for (int i = 0; i < bldRegs.size(); i++) {
                    String tempAddr = bldRegs.get(i);
                    if (StringUtil.isEmptyOrNull(tempAddr)) {
                        continue;
                    }
                    opData.setStrValue(nodePath + "BuildInfo.AddrPath.regName[${bn++}]", tempAddr);
                }
            }
            pbhRequestData.append(opData);
        }

        // 拼接报文
        opData = new XmlBean();
        merge(opData, nodePath + "HouseInfo", houseInfoJson);
        /* 房屋标志信息 */
        String hsFlags = houseInfoJson.hsFlags;
        int tempFlagCount = 0;
        if (StringUtil.isNotEmptyOrNull(hsFlags)) {
            for (String temp : hsFlags.split(",")) {
                if (StringUtil.isNotEmptyOrNull(temp)) {
                    opData.setStrValue(nodePath + "HouseInfo.HsFlags.HsFlag[${tempFlagCount++}].hsFlag", temp);
                }
            }
        }
        if (tempFlagCount == 0) {
            opData.setStrValue(nodePath + "HouseInfo.HsFlags", "");
        }
        pbhRequestData.append(opData);
        /* 保存产权信息 */
        // 保存产权信息
        opData = new XmlBean();
        List tempJsonArr = houseInfoJson.HsOwner;
        if (tempJsonArr != null) {
            for (int i = 0; i < tempJsonArr.size(); i++) {
                def ownInfo = houseInfoJson.HsOwner[i];
                merge(opData, nodePath + "OwnerInfo.HsOwners.HsOwner[${i}]", ownInfo);
            }
            pbhRequestData.append(opData);
        }
        // 保存协议信息
        if (houseInfoJson.HsCtInfo != null) {
            XmlBean contractOpData = new XmlBean();
            contractOpData.setStrValue(nodePath + "HsCtInfo.hsId", hsId);
            String hsCtId = houseInfoJson.HsCtInfo.hsCtId;
            if (StringUtil.isEmptyOrNull(hsCtId)) {
                hsCtId = "\${hsCtId}";
            }
            houseInfoJson.HsCtInfo.hsCtId = hsCtId;
            merge(contractOpData, "OpData.HsCtInfo", houseInfoJson.HsCtInfo);
            pbhRequestData.append(contractOpData);
        }
        /* 保存人员信息 */
        // 调用人员信息保存组件  保存人员信息
        opData = new XmlBean();
        // 根路径下公用信息
        if (houseInfoJson.RegistHj != null || houseInfoJson.RegistOthHj != null || houseInfoJson.LiveHj != null) {
            opData.setStrValue(nodePath + "HousePersons.prjOrgId", request.getParameter("ttOrgId"));
        }
        int addCount = 0;
        if (houseInfoJson.RegistHj != null) {
            for (int i = 0; i < houseInfoJson.RegistHj.size; i++) {
                def personInfo = houseInfoJson.RegistHj[i];
                // 生成人员信息
                merge(opData, "OpData.HousePersons.Persons.Person[${addCount++}]", personInfo);
            }
        }
        // 非本址户籍信息
        if (houseInfoJson.RegistOthHj != null) {
            for (int i = 0; i < houseInfoJson.RegistOthHj.size; i++) {
                def personInfo = houseInfoJson.RegistOthHj[i];
                // 生成人员信息
                merge(opData, "OpData.HousePersons.Persons.Person[${addCount++}]", personInfo);
            }
        }
        // 现场居住人
        if (houseInfoJson.LiveHj != null) {
            for (int i = 0; i < houseInfoJson.LiveHj.size; i++) {
                def personInfo = houseInfoJson.LiveHj[i];
                // 生成人员信息
                merge(opData, "OpData.HousePersons.Persons.Person[${addCount++}]", personInfo);
            }
        }
        pbhRequestData.append(opData);
        // 转换请求报文信息
        XmlBean reqData = new XmlBean("<OpData></OpData>");
        reqData.setStrValue("OpData.rhtType", request.getParameter("rhtType"))
        int opDataNum = pbhRequestData.getListNum("PhbData.OpData");
        for (int i = 0; i < opDataNum; i++) {
            String[] opNextNode = ["BuildInfo", "HouseInfo", "HouseReg", "PersonInfos", "HsCtInfo", "OwnerInfo", "HousePersons"];
            for (int j = 0; j < opNextNode.length; j++) {
                XmlBean opDataBean = pbhRequestData.getBeanByPath("PhbData.OpData[" + i + "]." + opNextNode[j]);
                if (opDataBean != null) {
                    reqData.append(opDataBean);
                }
            }
        }

        // 保存关联文档信息
        String relDocIds = houseInfoJson.relDocIds;
        if (StringUtil.isNotEmptyOrNull(relDocIds)) {
            XmlBean svcBean = new XmlBean("<SvcDocs></SvcDocs>");
            String[] relDocIdArr = relDocIds.split(",");
            for (int i = 0; i < relDocIdArr.length; i++) {
                opData = new XmlBean();
                if (StringUtil.isEmptyOrNull(relDocIdArr[i])) {
                    continue;
                }
                opData.setStrValue("SvcDoc.entityName", "SvcDocInfo");
                opData.setStrValue("SvcDoc.EntityData.docId", relDocIdArr[i]);
                opData.setStrValue("SvcDoc.EntityData.relType", "100");
                opData.setStrValue("SvcDoc.EntityData.relId", hsId);
                svcBean.append(opData);
            }
            //保存上传文档
            reqData.append(svcBean);
        }

        //保存人地房信息
        svcRequest.addOp("SAVE_PBH_CMPT", reqData);
        // 保存验证信息
        String checkDataJsonStr = request.getParameter("checkDataJson");
        if (StringUtil.isNotEmptyOrNull(checkDataJsonStr)) {
            // 以下代码出取值,其他逻辑与form.groovy中大表单提交一致
            def checkDataJson = jsonSlurper.parseText(checkDataJsonStr);
            // 获取最新代码
            String checkId = checkDataJson.checkId;
            if (StringUtil.isEmptyOrNull(checkId)) {
                checkId = "\${checkId}";
            }
            String checkRelId = checkDataJson.checkRelId;
            String subRelId = checkDataJson.subRelId;
            String entityKey = checkRelId;
//            if (StringUtil.isNotEmptyOrNull(subRelId)) {
//                entityKey = subRelId;
//            }
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
        // 提交审批处理结果
        SvcResponse svcResponse = transaction(svcRequest);
        // 处理服务调用返回结果
        if (addFlag && svcResponse.isSuccess()) {
            //新增操作
            if (addFlag) {
                hsId = svcResponse.getFirstOpRsp("SAVE_PBH_CMPT").getStrValue("Operation.OpResult.hsId")
            }
        }

        // 返回处理结果
        ResponseUtil.print(response,
                """{success: ${svcResponse.isSuccess()}, errMsg: "${svcResponse.getErrMsg()}", addFlag: ${
                    addFlag
                }, hsId:"${hsId}"}""");
    }

    /**
     * 将Map数据对象合并到XmlBean对象中, 值处理简单数据类型
     * @param toXmlBean
     * @param toPath
     * @param jsonObject
     */
    private void merge(XmlBean toXmlBean, String toPath, Map<String, Object> jsonObject) {
        for (Map.Entry<String, Object> entity : jsonObject) {
            String entityKey = entity.key;
            Object entityValue = entity.value;
            if (entityValue != null && !(entityValue instanceof Map) && !(entityValue instanceof List)) {
                toXmlBean.setStrValue(toPath + "." + entityKey, entityValue);
            }
        }
    }

}
