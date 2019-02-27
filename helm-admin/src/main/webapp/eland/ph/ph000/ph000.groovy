import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 人地房  房产信息展示页面
 */
class ph000 extends GroovyController {

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        // 获取权限编码ID,读取配置初始化查询条件
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        String privilegeId = request.getParameter("privilegeId");
        svcRequest.setValue("Request.SvcCont.treeType", "1");
        svcRequest.setValue("Request.SvcCont.rhtId", privilegeId);
        SvcResponse svcResponse = callService("rhtTreeService", "queryNodeInfo", svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean nodeInfoBean = svcResponse.getRspData().getBeanByPath("SvcCont.RhtTree.Node");
            if (nodeInfoBean != null) {
                // 获取备注描述信息
                String conditions = nodeInfoBean.getStrValue("Node.rhtAttr02");
                XmlBean todoBean = new XmlBean();
                todoBean.setStrValue("Condition.conditionNames", "");
                todoBean.setStrValue("Condition.conditions", "");
                todoBean.setStrValue("Condition.conditionValues", "");
                if (StringUtil.isNotEmptyOrNull(conditions)) {
                    // 配置的Json字符串转换为XMLBean
                    todoBean = XmlBean.jsonStr2Xml(conditions, "Condition");
                    //获取json串里的参数；
                }
                modelMap.put("conditions", todoBean.getRootNode());
            }
        }
        modelMap.put("formCd", request.getParameter("formCd"));
        return new ModelAndView("/eland/ph/ph000/ph000", modelMap)
    }

    /**
     * 信息表单展示界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView info(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String formCd = request.getParameter("formCd");
        String hsId = request.getParameter("hsId");

        //查询指定房产的 交房信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean paramData = new XmlBean();
        // 生成报表模板
        paramData.setStrValue("OpData.formCd", formCd);
        // 生成报表参数
        paramData.setStrValue("OpData.Report.Parameter[0].entityName", "HouseInfo");
        paramData.setStrValue("OpData.Report.Parameter[0].Property[0].attrName", "hsId");
        paramData.setStrValue("OpData.Report.Parameter[0].Property[0].value", hsId);
        // 增加到请求参数,调用服务
        svcRequest.addOp("GENERATE_REPORT", paramData);
        // 调用服务，执行数据查询
        SvcResponse pageResponse = query(svcRequest);
        if (pageResponse.isSuccess()) {
            XmlBean opResult = pageResponse.getFirstOpRsp("GENERATE_REPORT");
            List operaList = new ArrayList();
            int operNum = opResult.getListNum("Operation.OpResult.OperationDefs.OperationDef");
            for (int i = 0; i < operNum; i++) {
                operaList.add(opResult.getBeanByPath("Operation.OpResult.OperationDefs.OperationDef[${i}]").getRootNode());
            }
            String pageStr = opResult.getStrValue("Operation.OpResult.resultParam");
            String operDefCode = opResult.getStrValue("Operation.OpResult.OperationDefs.operDefCode");
            //返回展示页面字符串
            modelMap.put("article", pageStr);
            modelMap.put("operaList", operaList);
            modelMap.put("operDefCode", operDefCode);
        }
        modelMap.put("hsId", hsId);
        return new ModelAndView("/eland/ph/ph000/ph000_info", modelMap);
    }

    /**
     * 按照hsId排序,查找上一个居民和下一个居民
     * @param request 请求信息(通用分页查询条件)
     * @param response
     */
    public void singleQuery(HttpServletRequest request, HttpServletResponse response) {
        // 获取房屋编号
        RequestUtil requestUtil = new RequestUtil(request);
        String hsId = requestUtil.getStrParam("hsId");
        // 查询条件
        String conditionNames = requestUtil.getStrParam("conditionName");
        String conditions = requestUtil.getStrParam("condition");
        String conditionValues = requestUtil.getStrParam("conditionValue");
        // 排序条件
        String flag = request.getParameter("flag");
        String operation = "<";
        String sortOrder = "desc";
        if (StringUtil.isEqual("last", flag)) {
            operation = ">";
            sortOrder = "asc";
        }
        // 调用服务，获取居民基本信息和签约状态信息
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        XmlBean opData = new XmlBean();
        String prePath = "OpData.";
        // 获取基本信息
        opData.setStrValue(prePath + "entityName", "HouseInfo");
        String[] conditionFieldArr = conditionNames.split(",");
        String[] conditionArr = conditions.split(",", conditionFieldArr.length);
        String[] conditionValueArr = conditionValues.split(",", conditionFieldArr.length);
        // 查询条件
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
        // 增加查询条件
        if (StringUtil.isNotEmptyOrNull(hsId)) {
            opData.setStrValue(prePath + "Conditions.Condition[${addCount}].fieldName", "HouseInfo.hsId");
            opData.setStrValue(prePath + "Conditions.Condition[${addCount}].operation", operation);
            opData.setStrValue(prePath + "Conditions.Condition[${addCount++}].fieldValue", hsId);
        }
        // 排序条件
        opData.setStrValue(prePath + "SortFields.SortField.fieldName", "HouseInfo.hsId");
        opData.setStrValue(prePath + "SortFields.SortField.sortOrder", sortOrder);
        // 分页条件
        opData.setStrValue(prePath + "PageInfo.pageSize", "1");
        opData.setStrValue(prePath + "PageInfo.currentPage", "1");
        opData.setStrValue(prePath + "ResultFields.fieldName[0]", "HouseInfo.hsId");

        //通用查询，不传此参数==>通用分页查询， 传入此参数==>通用跨实体查询
        opData.setStrValue(prePath + "queryType", "2");
        //过滤的组织 字段名
        String regOrgIdValue = request.getParameter("rhtOrgId");
        opData.setStrValue(prePath + "OrgFilter.attrName", "HouseInfo.ttOrgId");
        opData.setStrValue(prePath + "OrgFilter.attrValue", regOrgIdValue);
        opData.setStrValue(prePath + "OrgFilter.rhtType", request.getParameter("rhtType"));

        //过滤的区域 字段名
        String rhtRegIdValue = request.getParameter("rhtRegId");
        if (StringUtil.isNotEmptyOrNull(rhtRegIdValue)) {
            opData.setStrValue(prePath + "RegFilter.attrName", "HouseInfo.ttRegId");
            opData.setStrValue(prePath + "RegFilter.attrValue", rhtRegIdValue);
            opData.setStrValue(prePath + "RegFilter.regUseType", request.getParameter("regUseType"));
        }

        opData.setStrValue(prePath + "opName", "QUERY_ENTITY_PAGE_DATA");
        svcRequest.addOp("PRIVI_FILTER", opData);
        // 调用服务
        SvcResponse svcResponse = query(svcRequest);
        hsId = "";
        if (svcResponse.isSuccess()) {
            XmlBean pageData = svcResponse.getFirstOpRsp("PRIVI_FILTER")
                    .getBeanByPath("Operation.OpResult.PageData");
            if (pageData != null && pageData.getListNum("PageData.Row") > 0) {
                hsId = pageData.getStrValue("PageData.Row[0].HouseInfo.hsId");
            }
        }
        ResponseUtil.printSvcResponse(response, svcResponse, """hsId:'${hsId}'""");
    }

}
