import com.shfb.oframe.core.util.common.*
import com.shfb.oframe.core.util.excel.ExcelType
import com.shfb.oframe.core.util.excel.ExcelWriter
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.util.spring.SvcUtil
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import net.sf.json.JSONArray
import net.sf.json.JSONObject
import org.springframework.ui.ModelMap
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by Dennis on 15/9/14.
 * 预分方案 模块
 */
class ct002 extends GroovyController {

    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        String prjCd = request.getParameter("prjCd");
        String schemeId = request.getParameter("schemeId");
        if (StringUtil.isNotEmptyOrNull(schemeId)) {
            modelMap.put("schemeId", schemeId);
        }

        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        opData.setStrValue(prePath + "entityName", "PreassignedScheme");

        opData.setStrValue(prePath + "SortFields.SortField[0].fieldName", "schemeName");
        opData.setStrValue(prePath + "SortFields.SortField[0].sortOrder", "asc");

        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "schemeId");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "schemeName");
        opData.setStrValue(prePath + "ResultFields.fieldName[2]", "schemeCreateTime");
        opData.setStrValue(prePath + "ResultFields.fieldName[3]", "schemeStatus");
        opData.setStrValue(prePath + "ResultFields.fieldName[4]", "schemeType");//预分分类
        svcRequest.addOp("QUERY_ENTITY_CMPT", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean resultBean = svcResponse.getFirstOpRsp("QUERY_ENTITY_CMPT");
            if (resultBean != null) {
                List schList = new ArrayList();
                int schNum = resultBean.getListNum("Operation.OpResult.PageData.Row");
                for (int i = 0; i < schNum; i++) {
                    schList.add(resultBean.getBeanByPath("Operation.OpResult.PageData.Row[${i}]").getRootNode());
                }
                modelMap.put("schList", schList);
            }
            //查参数SCHEME_TYPE
            Map cfgMap = getCfgCollection(request, "SCHEME_TYPE", true, NumberUtil.getIntFromObj(prjCd));
            modelMap.put("cfgMap", cfgMap);
        }
        modelMap.put("prjCd", prjCd);
        return new ModelAndView("/eland/ct/ct002/ct002", modelMap);
    }

    /**
     * 查询 获取条目 结果。
     * @param request
     * @param response
     */
    public void itemMap(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String schemeType = request.getParameter("schemeType");
        XmlBean opData = new XmlBean();
        opData.setStrValue("OpData.ctGroup", "");
        opData.setStrValue("OpData.schemeType", schemeType);
        svcRequest.addOp("QUERY_CTITEM_CMPT", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean resultCtItem = svcResponse.getFirstOpRsp("QUERY_CTITEM_CMPT").getBeanByPath("Operation.OpResult");
            List subNodes = resultCtItem.getNodeNames("OpResult.Item");
            List<Map<String, String>> result = new ArrayList<Map<String, String>>();
            for (int i = 0; i < subNodes.size(); i++) {
                String code = subNodes.get(i);
                String name = resultCtItem.getStrValue("OpResult.Item." + code);
                Map<String, String> temp = new HashMap<String, String>();
                temp.put("key", code);
                temp.put("value", name);
                result.add(temp);
            }
            ResponseUtil.print(response, JSONArray.fromObject(result).toString());
        } else {
            ResponseUtil.print(response, "{}");
        }
    }

    /** 方案类型定义 */
    public ModelAndView typeInfo(HttpServletRequest request, HttpServletResponse response) {
        // 获取输入参数
        String schemeType = request.getParameter("schemeType");
        // 当前节点的配置信息
        String currentTypeName = "";
        JSONObject currentTypeDef = new JSONObject();
        Map<String, String> moneyDef = new LinkedHashMap<String, String>();
        String totalMoney = "";

        // 数据
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean reqData = new XmlBean();
        String nodePath = "SvcCont.SysCfgs.SysCfg[0].";
        reqData.setStrValue(nodePath + "itemCd", "SCHEME_TYPE");
        reqData.setValue(nodePath + "prjCd", request.getParameter("prjCd"));
        // 请求信息
        svcRequest.setReqData(reqData);
        // 调用服务
        SvcResponse svcResponse = callService("sysCfgService", "querySysCfgData", svcRequest);
        //
        if (svcResponse.isSuccess()) {
            XmlBean staffBean = svcResponse.getRspData().getBeanByPath("SvcCont.SysCfgs.SysCfg[0]");
            int roleCount = staffBean.getListNum("SysCfg.Values.Value");
            String currentTypeJson = null;
            for (int i = 0; i < roleCount; i++) {
                nodePath = "SysCfg.Values.Value[${i}].";
                if (StringUtil.isEqual(schemeType, staffBean.getStrValue(nodePath + "valueCd"))) {
                    currentTypeName = staffBean.getStrValue(nodePath + "valueName");
                    currentTypeJson = staffBean.getStrValue(nodePath + "notes");
                    break;
                }
            }
            // 如果配置参数不为空
            if (StringUtil.isNotEmptyOrNull(currentTypeJson)) {
                currentTypeDef = JSONObject.fromObject(currentTypeJson);

                String moneyDy = currentTypeDef.get("money_dy");
                totalMoney = currentTypeDef.get("total_money");
                if (StringUtil.isNotEmptyOrNull(moneyDy)) {
                    moneyDef = getCfgCollection(request, moneyDy, true, svcRequest.getPrjCd());
                }
            }
        }
        ModelMap modelMap = new ModelMap();
        modelMap.put("prjCd", request.getParameter("prjCd"));
        modelMap.put("schemeType", schemeType);
        modelMap.put("schemeTypeName", currentTypeName);
        modelMap.put("schemeTypeDef", currentTypeDef);
        modelMap.put("moneyDef", moneyDef);
        modelMap.put("totalMoney", totalMoney);
        return new ModelAndView("/eland/ct/ct002/ct002_type_def", modelMap);
    }

    /**
     * 修改预分方案类型对应的签约信息
     * @param request 请求信息流
     * @param response 响应输出流
     * @return
     */
    public ModelAndView saveSchemeTypeDef(HttpServletRequest request, HttpServletResponse response) {
        // 获取输入参数
        String prjCd = request.getParameter("prjCd");
        String schemeType = request.getParameter("schemeType");
        String schemeTypeName = request.getParameter("schemeTypeName");
        /* 可以新增和修改分类的定义的配置信息 */
        String moneyDy = request.getParameter("moneyDy");
        if (StringUtil.isEmptyOrNull(moneyDy)) {
            moneyDy = "MONEY_DY_" + schemeType;
        }
        // 按照界面的签约信息等一拼接配置对一个的JSON字符串;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("formCd", request.getParameter("formCd"));
        jsonObject.put("money_dy", moneyDy);
        //
        JSONArray jsonArray = new JSONArray();
        JSONObject temp = new JSONObject();
        temp.put("entity_name", request.getParameter("entityName"));
        jsonArray.push(temp);
        temp = new JSONObject();
        temp.put("attr", request.getParameter("attr"));
        jsonArray.push(temp);
        temp = new JSONObject();
        temp.put("attr_value", request.getParameter("attrValue"));
        jsonArray.push(temp);
        jsonObject.put("entity_key", jsonArray);
        //
        jsonObject.put("attr_status", request.getParameter("attrStatus"));
        jsonObject.put("attr_date", request.getParameter("attrDate"));
        jsonObject.put("attr_status_staff", request.getParameter("attrStatusStaff"));
        jsonObject.put("sign_doc_name", request.getParameter("signDocName"));
        jsonObject.put("photo_doc_name", request.getParameter("photoDocName"));
        jsonObject.put("attr_status_value", request.getParameter("attrStatusValue"));
        jsonObject.put("total_money", request.getParameter("totalMoney"));
        jsonObject.put("conditionNames", request.getParameter("conditionNames"));
        jsonObject.put("conditions", request.getParameter("conditions"));
        jsonObject.put("conditionValues", request.getParameter("conditionValues"));
        jsonObject.put("skip", request.getParameter("skip"));

        // 查询SchemeType参数,遍历参数列表值,获取old对应的列表值
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean reqData = new XmlBean();
        String nodePath = "SvcCont.SysCfgs.SysCfg[0].";
        // 预分定义信息
        reqData.setStrValue(nodePath + "itemCd", "SCHEME_TYPE");
        reqData.setValue(nodePath + "prjCd", request.getParameter("prjCd"));
        // 条目定义信息
        nodePath = "SvcCont.SysCfgs.SysCfg[1].";
        reqData.setStrValue(nodePath + "itemCd", moneyDy);
        reqData.setValue(nodePath + "prjCd", request.getParameter("prjCd"));
        // 请求信息
        svcRequest.setReqData(reqData);
        // 调用服务, 插叙现在的配置信息
        SvcResponse svcResponse = callService("sysCfgService", "querySysCfgData", svcRequest);
        if (svcResponse.isSuccess()) {
            // 获取SchemeType的配置信息
            XmlBean sysCfgs = svcResponse.getRspData();
            XmlBean matchedDef = null;
            int sysCfgCnt = sysCfgs.getListNum("SvcCont.SysCfgs.SysCfg");
            XmlBean schemeTypeCfg = svcResponse.getRspData().getBeanByPath("SvcCont.SysCfgs.SysCfg[0]");
            int roleCount = schemeTypeCfg.getListNum("SysCfg.Values.Value");
            for (int i = 0; i < roleCount; i++) {
                nodePath = "SysCfg.Values.Value[${i}].";
                if (StringUtil.isEqual(schemeType, schemeTypeCfg.getStrValue(nodePath + "valueCd"))) {
                    matchedDef = schemeTypeCfg.getBeanByPath(nodePath)
                    break;
                }
            }
            //
            XmlBean moneyDef = null;
            if (sysCfgCnt == 2) {
                moneyDef = sysCfgs.getBeanByPath("SvcCont.SysCfgs.SysCfg[1]");
            } else {
                sysCfgs.setStrValue("SvcCont.SysCfgs.SysCfg[1].itemCd", moneyDy);
                sysCfgs.setStrValue("SvcCont.SysCfgs.SysCfg[1].prjCd", prjCd);
                sysCfgs.setStrValue("SvcCont.SysCfgs.SysCfg[1].itemName", schemeTypeName + "条目定义");
                sysCfgs.setStrValue("SvcCont.SysCfgs.SysCfg[1].valueType", "2");
                sysCfgs.setStrValue("SvcCont.SysCfgs.SysCfg[1].statusCd", "1");
                sysCfgs.setStrValue("SvcCont.SysCfgs.SysCfg[1].itemUseType", "-");
                sysCfgs.setStrValue("SvcCont.SysCfgs.SysCfg[1].withEmpty", "0");
                moneyDef = sysCfgs.getBeanByPath("SvcCont.SysCfgs.SysCfg[1]");
            }

            // 当前值信息
            if (matchedDef == null) {
                printAjax(response, "{\"success\": " + false + ", \"errMsg\": \"配置不存在\"" + "}");
            } else {
                // 将界面修改过的内容体现到对一个的值项中
                matchedDef.setStrValue("Value.valueName", schemeTypeName);
                // JSON字符串
                matchedDef.setStrValue("Value.notes", jsonObject.toString());
            }
            // 处理拼接的值对象
            moneyDef.removeNode("SysCfg.Values");
            String[] valueCds = request.getParameterValues("valueCd");
            String[] valueNames = request.getParameterValues("valueName");
            int j = 0;
            if (valueCds != null) {
                for (int i = 0; i < valueCds.length; i++) {
                    if (StringUtil.isEmptyOrNull(valueCds[i])) {
                        continue;
                    }
                    nodePath = "SysCfg.Values.Value[${j++}].";
                    moneyDef.setStrValue(nodePath + "valueCd", valueCds[i]);
                    moneyDef.setStrValue(nodePath + "valueName", valueNames[i]);
                }
            }
            // 项目编号覆盖
            sysCfgs.setStrValue("SvcCont.SysCfgs.SysCfg[0].prjCd", prjCd);
            sysCfgs.setStrValue("SvcCont.SysCfgs.SysCfg[1].prjCd", prjCd);
            // 请求调用服务
            svcRequest = RequestUtil.getSvcRequest(request);
            svcRequest.setReqData(sysCfgs);
            String svcMetho = "saveSysCfgData";
            // 调用服务,保存调整后的SchemeType及条目定义参数到数据库中
            svcResponse = SvcUtil.callSvc("sysCfgService", svcMetho, svcRequest);
        }
        // 输出保存结果
        ResponseUtil.printSvcResponse(response, svcResponse, "");

    }

    /** 查询 具体方案信息 */
    public ModelAndView info(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        String prjCd = request.getParameter("prjCd");
        String schemeId = request.getParameter("schemeId");

        if (StringUtil.isNotEmptyOrNull(schemeId)) {
            XmlBean opData = new XmlBean();
            String prePath = "OpData.";
            opData.setStrValue(prePath + "entityName", "PreassignedScheme");
            opData.setStrValue(prePath + "entityKey", schemeId);
            svcRequest.addOp("QUERY_ALL_ENTITY", opData);
            SvcResponse svcResponse = query(svcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean resultBean = svcResponse.getFirstOpRsp("QUERY_ALL_ENTITY");
                if (resultBean != null) {
                    XmlBean preassignedScheme = resultBean.getBeanByPath("Operation.OpResult.PreassignedScheme");
                    modelMap.put("schemeInfo", preassignedScheme.getRootNode());
                    // 处理适用条件
                    String schemeEnableRule = preassignedScheme.getStrValue("PreassignedScheme.schemeEnableRule");
                    // 兼容历史配置
                    if (StringUtil.isNotEmptyOrNull(schemeEnableRule) && !StringUtil.isPositiveNum(schemeEnableRule)) {
                        modelMap.put("schemeEnableRule", new XmlBean(schemeEnableRule).getRootNode());
                    }
                    //处理科目
                    List subList = new ArrayList();
                    String subStr = resultBean.getStrValue("Operation.OpResult.PreassignedScheme.schemeSubject");
                    if (StringUtil.isEmptyOrNull(subStr)) {
                        subStr = "<Subjects/>";
                    }
                    XmlBean schemeSubjectBean = new XmlBean(subStr);
                    int subNum = schemeSubjectBean.getListNum("Subjects.Subject");
                    for (int i = 0; i < subNum; i++) {
                        subList.add(schemeSubjectBean.getBeanByPath("Subjects.Subject[${i}]").getRootNode());
                    }
                    modelMap.put("subjectList", subList);
                }
            }
            modelMap.put("method", "edit");
        } else {
            XmlBean schemeInfo = new XmlBean();
            schemeInfo.setStrValue("PreassignedScheme.schemeStatus", "0");
            modelMap.put("schemeInfo", schemeInfo.getRootNode());
            modelMap.put("method", "new");
        }
        /**
         * 查询 获取条目 结果。
         */
        XmlBean opData = new XmlBean();
        String schemeType = request.getParameter("schemeType");
        opData.setStrValue("OpData.ctGroup", "");
        opData.setStrValue("OpData.schemeType", schemeType);
        svcRequest.addOp("QUERY_CTITEM_CMPT", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean resultCtItem = svcResponse.getFirstOpRsp("QUERY_CTITEM_CMPT").getBeanByPath("Operation.OpResult");
            List subNodes = resultCtItem.getNodeNames("OpResult.Item");
            Map itemMap = new HashMap();
            for (int i = 0; i < subNodes.size(); i++) {
                String code = subNodes.get(i);
                String name = resultCtItem.getStrValue("OpResult.Item." + code);
                itemMap.put(code, name);
            }
            modelMap.put("itemMap", itemMap);
        }

        modelMap.put("prjCd", prjCd);
        return new ModelAndView("/eland/ct/ct002/ct002_def", modelMap);
    }

    /** 查询 具体方案信息 */
    public ModelAndView calcResult(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        ModelMap modelMap = new ModelMap();
        String prjCd = request.getParameter("prjCd");
        String schemeId = request.getParameter("schemeId");
        if (StringUtil.isNotEmptyOrNull(schemeId)) {
            modelMap.put("schemeId", schemeId);
        }

        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        opData.setStrValue(prePath + "entityName", "PreassignedResult");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "schemeId");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", schemeId);
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");

        opData.setStrValue(prePath + "SortFields.SortField[0].fieldName", "preStartTime");
        opData.setStrValue(prePath + "SortFields.SortField[0].sortOrder", "desc");

        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "resultId");
        opData.setStrValue(prePath + "ResultFields.fieldName[1]", "schemeId");
        opData.setStrValue(prePath + "ResultFields.fieldName[2]", "preBatch");
        opData.setStrValue(prePath + "ResultFields.fieldName[3]", "preStartTime");
        opData.setStrValue(prePath + "ResultFields.fieldName[4]", "preEndTime");
        opData.setStrValue(prePath + "ResultFields.fieldName[5]", "preStatus");
        opData.setStrValue(prePath + "ResultFields.fieldName[6]", "totalThread");
        opData.setStrValue(prePath + "ResultFields.fieldName[7]", "finishThread");
        svcRequest.addOp("QUERY_ENTITY_PAGE_DATA", opData);
        SvcResponse svcResponse = query(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean resultBean = svcResponse.getFirstOpRsp("QUERY_ENTITY_PAGE_DATA");
            if (resultBean != null) {
                List schResultList = new ArrayList();
                int schNum = resultBean.getListNum("Operation.OpResult.PageData.Row");
                for (int i = 0; i < schNum; i++) {
                    schResultList.add(resultBean.getBeanByPath("Operation.OpResult.PageData.Row[${i}]").getRootNode());
                }
                modelMap.put("schResultList", schResultList);
            }
        }
        modelMap.put("prjCd", prjCd);
        modelMap.put("method", "edit");
        return new ModelAndView("/eland/ct/ct002/ct002_result", modelMap);
    }

    /** 保存方案 */
    public void saveScheme(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        //获取请求参数
        String schemeId = request.getParameter("schemeId");
        String schemeName = request.getParameter("schemeName");
        String schemeDesc = request.getParameter("schemeDesc");
        String schemeStatus = request.getParameter("schemeStatus");
        String ctType = request.getParameter("ctType");
        String schemeType = request.getParameter("schemeType");
        String flag = request.getParameter("flag");
        //拼接报文
        String prePath = "OpData.PreassignedScheme.";
        XmlBean opData = new XmlBean();
        if (StringUtil.isEmptyOrNull(schemeId)) {
            // 新建规则默认为未启用
            schemeId = "";
            schemeStatus = "0";
            if (StringUtil.isEqual("copy", flag)) {
                schemeName += "(复制)";
            }
        }
        opData.setStrValue(prePath + "schemeId", schemeId);
        opData.setStrValue(prePath + "schemeName", schemeName);
        opData.setStrValue(prePath + "schemeDesc", schemeDesc);
        opData.setStrValue(prePath + "schemeStatus", schemeStatus);
        opData.setStrValue(prePath + "schemeType", schemeType);

        // 谁知
        XmlBean schemeEnableRule = new XmlBean("<SchemeEnableRule/>");
        schemeEnableRule.setStrValue("SchemeEnableRule.conditionRule", request.getParameter("conditionRule"));
        schemeEnableRule.setStrValue("SchemeEnableRule.conditionRuleParams.ctType", ctType);
        opData.setStrValue(prePath + "schemeEnableRule", schemeEnableRule.toXML());

        // 拼接科目
        String[] subIds = request.getParameterValues("subId");
        String[] upIds = request.getParameterValues("upId");
        String[] names = request.getParameterValues("name");
        String[] codes = request.getParameterValues("code");
        String[] nodes = request.getParameterValues("node");
        String[] conditionRules = request.getParameterValues("conditionRule");
        String[] executeRules = request.getParameterValues("executeRule");
        String[] calcTypes = request.getParameterValues("calcType");
        String[] calcParams = request.getParameterValues("calcParam");

        prePath = "OpData.PreassignedScheme.schemeSubject.Subjects.";
        int subNum = 0;
        if (subIds != null && subIds.length > 0) {
            for (int i = 0; i < subIds.length; i++) {
                opData.setStrValue(prePath + "Subject[${subNum}].id", subIds[i]);
                opData.setStrValue(prePath + "Subject[${subNum}].upId", upIds[i]);
                opData.setStrValue(prePath + "Subject[${subNum}].name", names[i]);
                opData.setStrValue(prePath + "Subject[${subNum}].code", codes[i]);
                opData.setStrValue(prePath + "Subject[${subNum}].node", nodes[i]);
                opData.setStrValue(prePath + "Subject[${subNum}].conditionRule", conditionRules[i]);
                opData.setStrValue(prePath + "Subject[${subNum}].executeRule", "SCHEME_RULE");
                opData.setStrValue(prePath + "Subject[${subNum}].executeRuleParams.calcType", calcTypes[i]);
                opData.setStrValue(prePath + "Subject[${subNum++}].executeRuleParams.calcParam", calcParams[i]);
            }
        }

        //调用服务
        svcRequest.addOp("SAVE_SCHEME_CMPT", opData);
        SvcResponse svcResponse = transaction(svcRequest);
        if (svcResponse.isSuccess()) {
            if (svcResponse.getFirstOpRsp("SAVE_SCHEME_CMPT") != null) {
                XmlBean resultBean = svcResponse.getFirstOpRsp("SAVE_SCHEME_CMPT");
                schemeId = resultBean.getStrValue("Operation.schemeId");
            }
        }
        ResponseUtil.printAjax(response, """{isSuccess:${svcResponse.isSuccess()}, errMsg:"${
            svcResponse.getErrMsg()
        }", schemeId:"${schemeId}" }""");
    }

    /** 删除方案 方案页面 */
    public void delScheme(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String schemeId = request.getParameter("schemeId");

        String prePath = "OpData.";
        XmlBean opData = new XmlBean();
        opData.setStrValue(prePath + "schemeId", schemeId);
        //调用服务
        svcRequest.addOp("DELETE_SCHEME_CMPT", opData);
        SvcResponse svcResponse = transaction(svcRequest);

        ResponseUtil.printAjax(response, """{isSuccess:${svcResponse.isSuccess()}, errMsg:"${
            svcResponse.getErrMsg()
        }" }""");
    }

    /** 启用,停用，废弃 方案页面 */
    public void enableScheme(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String schemeId = request.getParameter("schemeId");
        String ableFlag = request.getParameter("ableFlag");
        String schemeType = request.getParameter("schemeType");
        String prePath = "OpData.";
        XmlBean opData = new XmlBean();
        opData.setStrValue(prePath + "schemeId", schemeId);
        opData.setStrValue(prePath + "schemeStatus", ableFlag);
        opData.setStrValue(prePath + "schemeType", schemeType);
        //调用服务
        svcRequest.addOp("UPDATE_SCHEME_STATUS_CMPT", opData);
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printAjax(response, """{isSuccess:${svcResponse.isSuccess()}, errMsg:"${
            svcResponse.getErrMsg()
        }" }""");
    }

    /** 导出方案 方案页面 */
    public void exportScheme(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String schemeId = request.getParameter("schemeId");

        String prePath = "OpData.";
        XmlBean opData = new XmlBean();
        opData.setStrValue(prePath + "schemeId", schemeId);
        //调用服务
        svcRequest.addOp("EXPORT_SCHEME_CMPT", opData);
        SvcResponse svcResponse = transaction(svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean result = svcResponse.getFirstOpRsp("EXPORT_SCHEME_CMPT");
            if (result != null) {
                XmlBean preSchemes = result.getBeanByPath("Operation.OpResult.PreassignedSchemes");

                String schemeName = preSchemes.getStrValue("PreassignedSchemes.PreassignedScheme[0].schemeName");
                String fileName = UUID.randomUUID().toString() + ".xml";
                File xmlFile = ServerFile.createFile("temp", fileName);
                XmlBean exportBean = new XmlBean();
                exportBean.setBeanByPath("OpData", preSchemes);
                xmlFile.write(exportBean.toXML(), "UTF-8");

                // 文件下载输出到响应流
                ResponseUtil.downloadFile(response, null, "${schemeName}.xml", xmlFile, request.getHeader("USER-AGENT"));
            }
        }
    }

    /** 导入方案 方案页面 */
    public void importScheme(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        //获取传入文件
        MultipartFile localFile = super.getFile(request, "importSchemeFile");
        String originalFileName = localFile.getOriginalFilename();
        String fileType = originalFileName.substring(originalFileName.lastIndexOf("."), originalFileName.length());
        boolean result = false;
        String errMsg = "";
        if (fileType.contains(".xml")) {
            try {
                String fileText = new InputStreamReader(localFile.getInputStream(), "UTF-8").getText();
                byte[] bytes = fileText.getBytes("utf-8");
                if (bytes.length > 3 && bytes[0] == (byte) 0xEF && bytes[1] == (byte) 0xBB && bytes[2] == (byte) 0xBF) {
                    fileText = new String(bytes, 3, bytes.length - 3, "utf-8");
                }
                XmlBean importBean = new XmlBean(fileText);
                svcRequest.addOp("IMPORT_SCHEME_CMPT", importBean);
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
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", result);
        jsonObject.put("errMsg", errMsg);
        ResponseUtil.printAjax(response, jsonObject.toString());
    }

    /**
     * 预计算设置
     * @param request 请求信息
     * @param response 响应信息
     * @return 打开导入界面
     */
    public ModelAndView runSetting(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("schemeId", request.getParameter("schemeId"));
        modelMap.put("preBatch", DateUtil.toStringYmd(DateUtil.getSysDate()));
        return new ModelAndView("/eland/ct/ct002/ct002_run_setting", modelMap);
    }

    /**
     * 打开预分计算面板
     * @param request 请求信息
     * @param response 响应信息
     * @return 打开导入界面
     */
    public ModelAndView openRunScheme(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String schemeId = request.getParameter("schemeId");
        modelMap.put("schemeId", schemeId);
        return new ModelAndView("/eland/ct/ct002/ct002_run_scheme", modelMap);
    }

    /** 预计算方案 方案页面 */
    public void runScheme(HttpServletRequest request, HttpServletResponse response) {
        // 请求信息
        RequestUtil requestUtil = new RequestUtil(request);
        String conditionEntityName = requestUtil.getStrParam("entityName");
        String conditionNames = requestUtil.getStrParam("conditionName");
        String conditions = requestUtil.getStrParam("condition");
        String conditionValues = requestUtil.getStrParam("conditionValue");
        String[] conditionFieldArr = conditionNames.split(",");
        String[] conditionArr = conditions.split(",");
        String[] conditionValueArr = conditionValues.split(",", conditionFieldArr.length);
        if (StringUtil.isEmptyOrNull(conditionEntityName)) {
            conditionEntityName = "HouseInfo";
        }
        //根据结果字段判断是否是跨实体查询
        boolean whetherCrossEntity = false;
        if (conditionNames.indexOf(".") != -1) {
            //是跨实体查询
            whetherCrossEntity = true;
        }

        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String schemeId = request.getParameter("schemeId");

        String prePath = "OpData.";
        XmlBean opData = new XmlBean();
        //计算之前 查询是否有计算中的
        opData.setStrValue(prePath + "entityName", "PreassignedResult");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldName", "schemeId");
        opData.setStrValue(prePath + "Conditions.Condition[0].fieldValue", schemeId);
        opData.setStrValue(prePath + "Conditions.Condition[0].operation", "=");
        opData.setStrValue(prePath + "Conditions.Condition[1].fieldName", "preStatus");
        opData.setStrValue(prePath + "Conditions.Condition[1].fieldValue", "1");
        opData.setStrValue(prePath + "Conditions.Condition[1].operation", "=");
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "preStatus");
        svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.addOp("QUERY_ENTITY_CMPT", opData);
        SvcResponse svcResponse = query(svcRequest);
        boolean svcResult = false;
        String errMsg = "";
        if (svcResponse.isSuccess()) {
            XmlBean result = svcResponse.getFirstOpRsp("QUERY_ENTITY_CMPT").getBeanByPath("Operation.OpResult.PageData");
            if (result != null) {
                int rowNum = result.getListNum("PageData.Row");
                for (int i = 0; i < rowNum; i++) {
                    if (StringUtil.isEqual(result.getStrValue("PageData.Row[${i}].preStatus"), "0")) {
                        ResponseUtil.printAjax(response, """{isSuccess:false , errMsg:"此方案正在计算中..." }""");
                        return;
                    }
                }
            }
            // 调用计算服务开始计算。。。
            opData = new XmlBean();
            String batchName = request.getParameter("batchName");
            if (StringUtil.isEmptyOrNull(batchName)) {
                batchName = "未命名";
            }
            // 拼接查询条件
            opData.setStrValue(prePath + "entityName", conditionEntityName);
            opData.setStrValue(prePath + "schemeId", schemeId);
            opData.setStrValue(prePath + "batchName", batchName);
            opData.setStrValue(prePath + "threadDealNum", "400");
            // 是否进行适配条件检测
            String useCondition = request.getParameter("useCondition");
            if (StringUtil.isEqual("1", useCondition)) {
                opData.setStrValue(prePath + "checkedHs", "0");
            } else {
                opData.setStrValue(prePath + "checkedHs", "1");
            }
            //过滤的区域 字段名
            String rhtRegIdValue = requestUtil.getStrParam("rhtRegId");
            String namePre = "";
            if (whetherCrossEntity) {
                namePre = conditionEntityName + ".";
                opData.setStrValue(prePath + "queryType", "2");
            }
            //过滤的组织 字段名
            opData.setStrValue(prePath + "OrgFilter.attrName", namePre + "\$ttOrgId");
            opData.setStrValue(prePath + "OrgFilter.attrValue", requestUtil.getStrParam("rhtOrgId"));
            opData.setStrValue(prePath + "OrgFilter.rhtType", requestUtil.getStrParam("rhtType"));
            if (StringUtil.isNotEmptyOrNull(rhtRegIdValue)) {
                opData.setStrValue(prePath + "RegFilter.attrName", namePre + "ttRegId");
                opData.setStrValue(prePath + "RegFilter.attrValue", rhtRegIdValue);
                opData.setStrValue(prePath + "RegFilter.regUseType", requestUtil.getStrParam("regUseType"));
            }
            //通用查询，不传此参数==>通用分页查询， 传入此参数==>通用跨实体查询
            opData.setStrValue(prePath + "opName", "EXECUTE_SCHEME_CMPT");
            int addCount = 0;
            for (int i = 0; i < conditionFieldArr.length; i++) {
                if (StringUtil.isEmptyOrNull(conditionFieldArr[i])
                        || StringUtil.isEmptyOrNull(conditionArr[i])
                        || StringUtil.isEmptyOrNull(conditionValueArr[i])) {
                    continue;
                }
                opData.setStrValue(prePath + "Conditions.Condition[${addCount}].fieldName", conditionFieldArr[i]);
                opData.setStrValue(prePath + "Conditions.Condition[${addCount}].operation", conditionArr[i]);
                if ("like".equalsIgnoreCase(conditionArr[i])) {
                    opData.setStrValue(prePath + "Conditions.Condition[${addCount++}].fieldValue", "%" + conditionValueArr[i] + "%");
                } else if ("in".equalsIgnoreCase(conditionArr[i])) {
                    String usedValue = conditionValueArr[i].replaceAll("\\|", ",");
                    opData.setStrValue(prePath + "Conditions.Condition[${addCount++}].fieldValue", "(" + usedValue + ")");
                } else {
                    opData.setStrValue(prePath + "Conditions.Condition[${addCount++}].fieldValue", conditionValueArr[i]);
                }
            }
            svcRequest = RequestUtil.getSvcRequest(request);
            svcRequest.addOp("PRIVI_FILTER", opData);
            svcResponse = transaction(svcRequest);
            if (svcResponse.isSuccess()) {
                svcResult = true;
            } else {
                errMsg = svcResponse.getErrMsg();
            }
        } else {
            errMsg = svcResponse.getErrMsg();
        }
        ResponseUtil.printAjax(response, """{isSuccess:${svcResult}, errMsg:"${errMsg}" }""");
    }

    /** 导出计算结果 方案页面 */
    public void exportResult(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String resultId = request.getParameter("resultId");
//        查询实体配置 导出结果字段， 要导出的条目
        //查询预分结果报文
        XmlBean opData = new XmlBean();
        opData.fromXML("<OpData>" +
                "<entityName>PreassignedResult</entityName>\n" +
                "<Conditions>\n" +
                "    <Condition>\n" +
                "        <fieldName>resultId</fieldName>\n" +
                "        <fieldValue>" + resultId + "</fieldValue>\n" +
                "        <operation>=</operation>\n" +
                "    </Condition>\n" +
                "</Conditions>\n" +
                "<ResultFields>\n" +
                "    <fieldName>param1_hsCd</fieldName>\n" +
                "    <fieldName>param1_hsFullAddr</fieldName>\n" +
                "    <fieldName>param1_hsOwnerPersons</fieldName>\n" +
                "    <fieldName>param1_hsBuildSize</fieldName>\n" +
                "    <fieldName>param1_hsUseSize</fieldName>\n" +
                "    <fieldName>PreBatchs</fieldName>\n" +
                "    <fieldName>preResult</fieldName>\n" +
                "    <fieldName>calcTime</fieldName>\n" +
                "</ResultFields>\n" +
                "</OpData>")
        svcRequest.addOp("QUERY_SCHEME_RESULT_DATA_CMPT", opData)
        SvcResponse svcResponse = query(svcRequest);
        String errMsg = "";

        if (svcResponse.isSuccess()) {
            // 存储科目配置
            XmlBean resultData = svcResponse.getRspData().getBeanByPath("SvcCont.Operation.OpResult");
            XmlBean titleBean = resultData.getBeanByPath("OpResult.title");
            String title = titleBean.getStrValue("title");
            String[] titles = title.split(",")
            int rowCount = resultData.getListNum("OpResult.Row");

            //数据写入
            ExcelWriter excelWriter;
            //导出文件名
            String excelName = UUID.randomUUID().toString() + ".xlsx";
            //导出路径
            String excelPath = "temp/" + excelName;
            //创建excel
            excelWriter = new ExcelWriter(ServerFile.createFile(excelPath));
            //修改sheet页
            excelWriter.createSheet("导出数据", 0);
            excelWriter.changeSheet("导出数据");
            //写如标题
            int titleIndex = 0;
            for (int i = 0; i < titles.size(); i++) {
                excelWriter.writeData(0, titleIndex++, titles[i]);
            }
            //写如内容
            for (int i = 0; i < rowCount; i++) {
                XmlBean row = resultData.getBeanByPath("OpResult.Row[" + i + "]");
                List<String> columnNames = row.getNodeNames("Row");

                for (int j = 0; j < columnNames.size(); j++) {
                    String key = columnNames.get(j);
                    String columnValue = row.getStrValue("Row." + key);
                    //结果值插入到对应行列
                    if (StringUtil.isNumeric(columnValue) && columnValue.length() < 17) {
                        //如果该列是数字则使用Number类型
                        excelWriter.writeData(i + 1, j, columnValue, ExcelType.NUMBER);
                    } else {
                        excelWriter.writeData(i + 1, j, columnValue);
                    }
                }
            }
            // 关闭文件
            if (excelWriter != null) {
                excelWriter.closeBook();
            }
            String data = """ {"remoteFile": "${excelPath}", "clientFile": "${excelName}"} """;
            ResponseUtil.printSvcResponse(response, svcResponse, """ "data":${data} """);
        } else {
            ResponseUtil.printAjax(response, "{\"success\": " + false + ", errMsg:\"没有数据\"}");
            errMsg = svcResponse.getErrMsg();
        }

        ResponseUtil.printSvcResponse(response, svcResponse, """ "errMsg":"${errMsg}" """);
    }

    /**
     * 删除房产信息
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public void deleteResult(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String resultIds = request.getParameter("resultIds");
        // 删除房产组件
        String nodePath = "OpData.";
        String[] resultIdArr = resultIds.split(",");
        for (String resultId : resultIdArr) {
            if (StringUtil.isNotEmptyOrNull(resultId)) {
                XmlBean operationData = new XmlBean();
                operationData.setStrValue(nodePath + "entityName", "PreassignedResult");
                operationData.setStrValue(nodePath + "entityKey", resultId);
                svcRequest.addOp("DELETE_ENTITY_INFO", operationData);
            }
        }
        // 输出处理结果
        SvcResponse svcResponse = transaction(svcRequest);
        ResponseUtil.printSvcResponse(response, svcResponse, "");
    }

}