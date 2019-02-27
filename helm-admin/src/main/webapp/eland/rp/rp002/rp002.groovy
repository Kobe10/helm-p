import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created with IntelliJ IDEA
 * User: shfb_wang 
 * Date: 2015/5/13 9:09
 * Copyright(c) 北京四海富博计算机服务有限公司
 */
/**
 * 建筑成本分析 模块
 */
class rp002 extends GroovyController {

    /**
     * 初始化页面
     * @param request
     * @param response
     * @return
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/eland/rp/rp002/rp002", modelMap);
    }

    /**
     * 计算合计金额
     */
    public ModelAndView initReport(HttpServletRequest request, HttpServletResponse response) {
        // 输出业务
        ModelMap modelMap = new ModelMap();
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 获取输入参数
        RequestUtil requestUtil = new RequestUtil(request);
        String[] resultAttrsName = requestUtil.getStrParam("resultAttrsName").split(",");
        String[] resultAttrsType = requestUtil.getStrParam("resultAttrsType").split(",", resultAttrsName.length);
        resultAttrsName = ["BuildInfo.buildId",
                           "BuildInfo.totalCjNum",
                           "BuildInfo.totalHjNum",
                           "BuildInfo.totalPubHsNum",
                           "BuildInfo.totalPriHsNum",
                           "BuildInfo.totalBuldSize",
                           "BuildInfo.totalLandSize",
                           "BuildInfo.totalJmdj",
                           "BuildInfo.totalZddj",
                           "BuildInfo.totalCb",
                           "BuildInfo.totalJlbz",
                           "BuildInfo.totalTaxFee",
                           "BuildInfo.totalHkWq",
                           "BuildInfo.totalYaz",
                           "BuildInfo.totalGfk",
                           "BuildInfo.totalMjceBz"
        ];
        resultAttrsType = ["0", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1"];
        // 请求参数
        String prePath = "OpData.";
        XmlBean opData = new XmlBean();
        //调用权限过滤组件。
        opData.setStrValue(prePath + "opName", "QUERY_ATTR_COUNT_CMPT");
        opData.setStrValue(prePath + "RegFilter.attrName", "BuildInfo.ttRegId");
        opData.setStrValue(prePath + "RegFilter.attrValue", request.getParameter("rhtRegId"));
        opData.setStrValue(prePath + "RegFilter.regUseType", request.getParameter("regUseType"));
        opData.setStrValue(prePath + "RegFilter.setPath", "OpData.Conditions");

        opData.setStrValue(prePath + "OrgFilter.attrName", "BuildInfo.ttOrgId");
        opData.setStrValue(prePath + "OrgFilter.attrValue", "1");
        opData.setStrValue(prePath + "OrgFilter.rhtType", "1");
        opData.setStrValue(prePath + "OrgFilter.setPath", "OpData.Conditions");

        // 统计指标
        for (int i = 0; i < resultAttrsType.length; i++) {
            if (StringUtil.isNotEmptyOrNull(resultAttrsName[i])) {
                opData.setStrValue(prePath + "ResultAttrs.AttrInfo[${i}].type", resultAttrsType[i]);
                opData.setStrValue(prePath + "ResultAttrs.AttrInfo[${i}].attrName", resultAttrsName[i]);
            }
        }

        // 查询条件
        String conditionNames = requestUtil.getStrParam("conditionName");
        String conditions = requestUtil.getStrParam("condition");
        String conditionValues = requestUtil.getStrParam("conditionValue");
        String[] conditionFieldArr = conditionNames.split(",");
        String[] conditionArr = conditions.split(",");
        String[] conditionValueArr = conditionValues.split(",", conditionFieldArr.length);

        String entityName = requestUtil.getStrParam("entityName");
        int addCount = 0;
        for (int i = 0; i < conditionFieldArr.length; i++) {
            if (StringUtil.isEmptyOrNull(conditionFieldArr[i]) || StringUtil.isEmptyOrNull(conditionArr[i]) || StringUtil.isEmptyOrNull(conditionValueArr[i])) {
                continue;
            }
            opData.setStrValue(prePath + "Conditions.Condition[${addCount}].fieldName", entityName + "." + conditionFieldArr[i]);
            opData.setStrValue(prePath + "Conditions.Condition[${addCount}].operation", conditionArr[i]);
            if ("like".equalsIgnoreCase(conditionArr[i])) {
                opData.setStrValue(prePath + "Conditions.Condition[${addCount++}].fieldValue", "%" + conditionValueArr[i] + "%");
            } else {
                opData.setStrValue(prePath + "Conditions.Condition[${addCount++}].fieldValue", conditionValueArr[i]);
            }
        }

        //单独处理 去勾选的院落
        String buildIdsStr = request.getParameter("buildIds");
        if (StringUtil.isNotEmptyOrNull(buildIdsStr)) {
            String[] temp = buildIdsStr.split(",");
            Set set = new HashSet();
            set.addAll(temp);
            set.remove("on");
            set.remove("");
            buildIdsStr = set.join(",");
            if (StringUtil.isNotEmptyOrNull(buildIdsStr)) {
                opData.setStrValue(prePath + "Conditions.Condition[${addCount}].fieldName", "BuildInfo.buildId");
                opData.setStrValue(prePath + "Conditions.Condition[${addCount}].operation", "NOT IN");
                opData.setStrValue(prePath + "Conditions.Condition[${addCount++}].fieldValue", "(${buildIdsStr})");
            }
        }
        // 排序字段
        String orderAttrsName = requestUtil.getStrParam("orderAttrsName");
        String orderAttrsOrder = requestUtil.getStrParam("orderAttrsOrder");
        String[] orderAttrsNameArr = orderAttrsName.split(",");
        String[] orderAttrsOrderArr = orderAttrsOrder.split(",", orderAttrsNameArr.length);
        addCount = 0;
        if (StringUtil.isNotEmptyOrNull(orderAttrsName) && StringUtil.isNotEmptyOrNull(orderAttrsOrder)) {
            for (int i = 0; i < orderAttrsNameArr.length; i++) {
                if (StringUtil.isEmptyOrNull(orderAttrsNameArr[i]) || StringUtil.isEmptyOrNull(orderAttrsOrderArr[i])) {
                    continue;
                }
                opData.setStrValue(prePath + "SortFields.SortField[${addCount}].fieldName", "BuildInfo." + orderAttrsNameArr[i]);
                opData.setStrValue(prePath + "SortFields.SortField[${addCount++}].sortOrder", orderAttrsOrderArr[i]);
            }
        }
        // 执行组件查询
        svcRequest.addOp("PRIVI_FILTER", opData);
        SvcResponse svcResponse = query(svcRequest);
        XmlBean titleBean = null;
        if (svcResponse.isSuccess()) {
            titleBean = svcResponse.getFirstOpRsp("PRIVI_FILTER").getBeanByPath("Operation.OpResult");
            if (titleBean != null) {
                int fieldNum = titleBean.getListNum("OpResult.TableDef.Field");
                Map buildReport = new HashMap();

                //由于页面计算方法修改。要拿出来重新计算该值
                String totalJmdj = "0.00";
                String totalBuldSize = "0.0";
                String totalZddj = "0.00";
                String totalLandSize = "0.0";
                String totalCb = "";
                for (int i = 0; i < fieldNum; i++) {
                    String attrName = titleBean.getStrValue("OpResult.TableDef.Field[${i}].attrName");
                    String rname = titleBean.getStrValue("OpResult.TableDef.Field[${i}].rname");
                    String value = titleBean.getStrValue("OpResult.TableData.Row.${rname}");
                    if (StringUtil.isEmptyOrNull(value)) {
                        value = "0";
                    }
                    //取出总成本合计
                    if (StringUtil.isEqual("BuildInfo.totalCb", attrName)) {
                        totalCb = value;
                    }
                    //取出总建面
                    if (StringUtil.isEqual("BuildInfo.totalBuldSize", attrName)) {
                        totalBuldSize = value;
                    }
                    //取出建面单价
                    if (StringUtil.isEqual("BuildInfo.totalJmdj", attrName)) {
                        totalJmdj = value;
                    }
                    //取出总占地面积
                    if (StringUtil.isEqual("BuildInfo.totalLandSize", attrName)) {
                        totalLandSize = value;
                    }
                    //取出占面单价
                    if (StringUtil.isEqual("BuildInfo.totalZddj", attrName)) {
                        totalZddj = value;
                    }
                    buildReport.put(attrName.replace("BuildInfo.", ""), value);
                }

                //重新计算 建面单价、占面单价

                if (StringUtil.isNotEmptyOrNull(totalBuldSize) && new BigDecimal(totalBuldSize).compareTo(new BigDecimal("0.0")) > 0) {
                    totalJmdj = new BigDecimal(totalCb).divide(new BigDecimal(totalBuldSize), 2, BigDecimal.ROUND_HALF_UP).toString();
                } else {
                    totalJmdj = "0.00";
                }
                if (StringUtil.isNotEmptyOrNull(totalLandSize) && new BigDecimal(totalLandSize).compareTo(new BigDecimal("0.0")) > 0) {
                    totalZddj = new BigDecimal(totalCb).divide(new BigDecimal(totalLandSize), 2, BigDecimal.ROUND_HALF_UP).toString();
                } else {
                    totalZddj = "0.00";
                }
                modelMap.put("totalJmdj", totalJmdj);
                modelMap.put("totalZddj", totalZddj);
                modelMap.put("buildReport", buildReport);
            }

            /**
             * 再次调用次组件 统计院落数
             */
            svcRequest = RequestUtil.getSvcRequest(request);
            // 获取输入参数
            requestUtil = new RequestUtil(request);
            resultAttrsName = ["BuildInfo.buildType"];
            resultAttrsType = ["0"];
            // 请求参数
            opData = new XmlBean();
            //权限过滤参数。
            opData.setStrValue(prePath + "opName", "QUERY_ATTR_COUNT_CMPT");
            opData.setStrValue(prePath + "RegFilter.attrName", "BuildInfo.ttRegId");
            opData.setStrValue(prePath + "RegFilter.attrValue", request.getParameter("rhtRegId"));
            opData.setStrValue(prePath + "RegFilter.regUseType", request.getParameter("regUseType"));
            opData.setStrValue(prePath + "RegFilter.setPath", "OpData.Conditions");
            opData.setStrValue(prePath + "OrgFilter.attrName", "BuildInfo.ttOrgId");
            opData.setStrValue(prePath + "OrgFilter.attrValue", "1");
            opData.setStrValue(prePath + "OrgFilter.rhtType", "1");
            opData.setStrValue(prePath + "OrgFilter.setPath", "OpData.Conditions");

            // 统计指标
            for (int i = 0; i < resultAttrsType.length; i++) {
                if (StringUtil.isNotEmptyOrNull(resultAttrsName[i])) {
                    opData.setStrValue(prePath + "ResultAttrs.AttrInfo[${i}].type", resultAttrsType[i]);
                    opData.setStrValue(prePath + "ResultAttrs.AttrInfo[${i}].attrName", resultAttrsName[i]);
                }
            }

            // 查询条件
            conditionNames = requestUtil.getStrParam("conditionName");
            conditions = requestUtil.getStrParam("condition");
            conditionValues = requestUtil.getStrParam("conditionValue");
            conditionFieldArr = conditionNames.split(",");
            conditionArr = conditions.split(",");
            conditionValueArr = conditionValues.split(",", conditionFieldArr.length);

            entityName = requestUtil.getStrParam("entityName");
            int addCount_ = 0;
            for (int i = 0; i < conditionFieldArr.length; i++) {
                if (StringUtil.isEmptyOrNull(conditionFieldArr[i]) || StringUtil.isEmptyOrNull(conditionArr[i]) || StringUtil.isEmptyOrNull(conditionValueArr[i])) {
                    continue;
                }
                opData.setStrValue(prePath + "Conditions.Condition[${addCount_}].fieldName", entityName + "." + conditionFieldArr[i]);
                opData.setStrValue(prePath + "Conditions.Condition[${addCount_}].operation", conditionArr[i]);
                if ("like".equalsIgnoreCase(conditionArr[i])) {
                    opData.setStrValue(prePath + "Conditions.Condition[${addCount_++}].fieldValue", "%" + conditionValueArr[i] + "%");
                } else {
                    opData.setStrValue(prePath + "Conditions.Condition[${addCount_++}].fieldValue", conditionValueArr[i]);
                }
            }
            //单独处理 去勾选的院落
            buildIdsStr = request.getParameter("buildIds");
            if (StringUtil.isNotEmptyOrNull(buildIdsStr)) {
                String[] temp = buildIdsStr.split(",");
                Set set = new HashSet();
                set.addAll(temp);
                set.remove("on");
                set.remove("");
                buildIdsStr = set.join(",");
                if (StringUtil.isNotEmptyOrNull(buildIdsStr)) {
                    opData.setStrValue(prePath + "Conditions.Condition[${addCount_}].fieldName", "BuildInfo.buildId");
                    opData.setStrValue(prePath + "Conditions.Condition[${addCount_}].operation", "NOT IN");
                    opData.setStrValue(prePath + "Conditions.Condition[${addCount_++}].fieldValue", "(${buildIdsStr})");
                }
            }
            // 执行组件查询
            svcRequest.addOp("PRIVI_FILTER", opData);
            int buildNum = 0;
            int PubBuildNum = 0;
            int PriBuildNum = 0;
            int PubPriBuildNum = 0;
            SvcResponse svcResCount = query(svcRequest);
            if (svcResCount.isSuccess()) {
                titleBean = svcResCount.getFirstOpRsp("PRIVI_FILTER");
                if (titleBean != null) {
                    titleBean = titleBean.getBeanByPath("Operation.OpResult");
                    if (titleBean != null) {
                        int fieldNum = titleBean.getListNum("OpResult.TableDef.Field");
                        Map buildCount = new HashMap();
                        for (int i = 0; i < fieldNum; i++) {
                            String attrName = titleBean.getStrValue("OpResult.TableDef.Field[${i}].attrName");
                            String rname = titleBean.getStrValue("OpResult.TableDef.Field[${i}].rname");
                            String value = titleBean.getStrValue("OpResult.TableData.Row.${rname}");
                            int rowNum = titleBean.getListNum("OpResult.TableData.Row");
                            for (int j = 0; j < rowNum; j++) {
                                String buildTypeTemp = titleBean.getStrValue("OpResult.TableData.Row[${j}].${rname}");
                                if (StringUtil.isEqual(buildTypeTemp, "1")) {
                                    PubBuildNum++;
                                    buildNum++;
                                } else if (StringUtil.isEqual(buildTypeTemp, "2")) {
                                    PriBuildNum++;
                                    buildNum++;
                                } else if (StringUtil.isEqual(buildTypeTemp, "3")) {
                                    PubPriBuildNum++;
                                    buildNum++;
                                }
                            }
                            if (StringUtil.isEmptyOrNull(value)) {
                                value = "0";
                            }
                            buildCount.put(attrName.replace("BuildInfo.", ""), value);
                        }
                        modelMap.put("buildCount", buildCount);
                    }
                    modelMap.put("buildNum", buildNum);
                    modelMap.put("PubBuildNum", PubBuildNum);
                    modelMap.put("PriBuildNum", PriBuildNum);
                    modelMap.put("PubPriBuildNum", PubPriBuildNum);
                }
            }
        }
        return new ModelAndView("/eland/rp/rp002/rp002_report", modelMap);
    }
}