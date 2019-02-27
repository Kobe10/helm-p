import com.shfb.oframe.core.util.common.DateUtil
import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.common.XmlNode
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.util.spring.SvcUtil
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import net.sf.json.JSONArray
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class rp000 extends GroovyController {
    /**
     * 初始化报表指标
     * @param request 请求信息
     * @param response 返回信息
     * @return
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        Date nowTime = DateUtil.getSysDate();
        modelMap.put("startTime", DateUtil.toStringYmd(DateUtil.change(nowTime, DateUtil.DAY, -7)));
        modelMap.put("endTime", DateUtil.toStringYmd(nowTime))
        return new ModelAndView("/eland/rp/rp000/rp000", modelMap);
    }

    /**
     *  获取统计指标数据
     */
    public void dataSummary(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        RequestUtil requestUtil = new RequestUtil(request);
        String orgIdPath = requestUtil.getStrParam("orgIdPath");
        String regIdPath = requestUtil.getStrParam("regIdPath");
        // 调用服务数据
        XmlBean reqData = new XmlBean();
        // SQL信息
        Map<String, Object> paramMap = requestUtil.getRequestMap(request);
        int k = 0;
        for (Map.Entry tempItem : paramMap) {
            if (tempItem.getKey().equals("prjJobCd")) {
                reqData.setStrValue("SvcCont.QuerySvc.prjJobCd", tempItem.getValue());
            } else if (tempItem.getKey().equals("prjJobGroup")) {
                reqData.setStrValue("SvcCont.QuerySvc.prjJobGroup", tempItem.getValue());
            } else if (tempItem.getKey().equals("regUseType")) {
                reqData.setStrValue("SvcCont.QuerySvc.regType", tempItem.getValue());
            } else {
                reqData.setStrValue("SvcCont.ParamInfo.Param[${k}].name", tempItem.getKey());
                reqData.setStrValue("SvcCont.ParamInfo.Param[${k}].value", tempItem.getValue());
                k++;
            }
        }
        String queryRegId = null;
        if (StringUtil.isNotEmptyOrNull(regIdPath)) {
            String[] tempArr = regIdPath.split("/");
            queryRegId = tempArr.last().replaceAll("/", "");
            reqData.setStrValue("SvcCont.QuerySvc.prjRegId", queryRegId);
        }
        String regName = "";
        if (!StringUtil.isEmptyOrNull(queryRegId)) {
            // 获取区域名称
            SvcRequest regSvcRequest = RequestUtil.getSvcRequest(request);
            // 调用服务获取组织下的员工信息
            regSvcRequest.setValue("Request.SvcCont.PrjReg.regId", queryRegId);
            regSvcRequest.setValue("Request.SvcCont.PrjReg.prjCd", request.getParameter("prjCd"));
            String regUseType = request.getParameter("regUseType");
            if (StringUtil.isEmptyOrNull(regUseType)) {
                regUseType = "1";
            }
            regSvcRequest.setValue("Request.SvcCont.PrjReg.regUseType", regUseType);
            SvcResponse svcResponse = callService("prjRegService", "queryPrjReg", regSvcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean tempBean = svcResponse.getRspData().getBeanByPath("SvcCont.PrjReg");
                if (tempBean != null) {
                    regName = tempBean.getStrValue("PrjReg.regName");
                }
            }
        }

        String queryOrgId = null;
        if (StringUtil.isNotEmptyOrNull(orgIdPath)) {
            String[] tempArr = orgIdPath.split("/");
            queryOrgId = tempArr.last().replaceAll("/", "");
            reqData.setStrValue("SvcCont.QuerySvc.prjOrgId", queryOrgId);
        }
        String orgName = "";
        if (!StringUtil.isEmptyOrNull(queryOrgId)) {
            // 获取管理组织名称
            SvcRequest orgQueryRequest = RequestUtil.getSvcRequest(request);
            // 调用服务获取组织下的员工信息
            orgQueryRequest.setValue("Request.SvcCont.Org.Node.orgId", queryOrgId);
            orgQueryRequest.setValue("Request.SvcCont.Org.Node.prjCd", request.getParameter("prjCd"));
            SvcResponse orgQueryResponse = callService("orgService", "queryNodeInfo", orgQueryRequest);
            if (orgQueryResponse.isSuccess()) {
                XmlBean tempBean = orgQueryResponse.getRspData().getBeanByPath("SvcCont.Org.Node");
                if (tempBean != null) {
                    orgName = tempBean.getStrValue("Node.orgName");
                }
            }
        }

        // 调用服务
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = SvcUtil.callSvc("reportService", "queryChartData", svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean rspData = svcResponse.getRspData();
            // 获取服务的输出参数进行转换，将List转换为XMLBean数组元素
            XmlBean tempResultBean = new XmlBean();
            int seriesNum = 0;
            if (null != rspData) {
                List<String> xAxisData = rspData.getValue("SvcCont.ReportData.xAxis");
                if (xAxisData != null) {
                    for (int i = 0; i < xAxisData.size(); i++) {
                        tempResultBean.setStrValue("Report.xAxis[${i}]", xAxisData.get(i));
                    }
                }
                // 处理序列内容
                seriesNum = rspData.getListNum("SvcCont.ReportData.series");
            }
            for (int i = 0; i < seriesNum; i++) {
                XmlBean series = rspData.getBeanByPath("SvcCont.ReportData.series[${i}]");
                tempResultBean.setStrValue("Report.series[${i}].name", series.getStrValue("series.name"));
                tempResultBean.setStrValue("Report.series[${i}].jobCd", series.getStrValue("series.jobCd"));
                tempResultBean.setStrValue("Report.series[${i}].valueSuffix", series.getStrValue("series.valueSuffix"));
                tempResultBean.setStrValue("Report.series[${i}].stackName", series.getStrValue("series.stackName"));
                tempResultBean.setStrValue("Report.series[${i}].chartType", series.getStrValue("series.chartType"));
                // 数据值
                List<String> tempList = series.getValue("series.data");
                if (tempList != null) {
                    for (int j = 0; j < tempList.size(); j++) {
                        tempResultBean.setStrValue("Report.series[${i}].data[${j}]", tempList.get(j));
                    }
                }
            }
            // 处理标题
            if (rspData != null) {
                tempResultBean.setStrValue("Report.title", rspData.getStrValue("SvcCont.ReportData.title"));
            }
            String displayName = regName;
            if (StringUtil.isNotEmptyOrNull(displayName) || StringUtil.isNotEmptyOrNull(orgName)) {
                displayName = displayName + "  ";
            }
            displayName = displayName + orgName;
            tempResultBean.setStrValue("Report.showName", displayName);
            // 最大值与最小值
            tempResultBean.setStrValue("Report.minValue", request.getParameter("minValue"));
            tempResultBean.setStrValue("Report.maxValue", request.getParameter("maxValue"));
            // 输出
            ResponseUtil.printSvcResponse(response, svcResponse, """ "data": ${tempResultBean.toJson()}""");
        } else {
            ResponseUtil.printSvcResponse(response, svcResponse, "");
        }
    }
    /**
     *  查询统计指标
     */
    public ModelAndView queryJob(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        // 将查询参数传递给服务组件
        RequestUtil requestUtil = new RequestUtil(request);
        Map<String, Object> paramMap = requestUtil.getRequestMap(request);

        // 调用服务数据
        XmlBean reqData = new XmlBean();
        reqData.setValue("SvcCont.PageInfo.currentPage", "1");
        reqData.setValue("SvcCont.PageInfo.pageSize", "20");
        // SQL信息
        reqData.setStrValue("SvcCont.QuerySvc.subSvcName", "pj00901");
        int i = 0;
        for (Map.Entry tempItem : paramMap) {
            reqData.setStrValue("SvcCont.ParamInfo.Param[${i}].name", tempItem.getKey());
            reqData.setStrValue("SvcCont.ParamInfo.Param[${i}].value", tempItem.getValue());
            i++;
        }
        svcRequest.setReqData(reqData);
        // 调用服务
        SvcResponse svcResponse = callService("pageService", "queryPageData", svcRequest);
        // 添加分页查询数据参数
        ModelMap modelMap = new ModelMap();
        if (svcResponse.isSuccess()) {
            XmlBean rspData = svcResponse.getRspData();
            // 返回结果集
            modelMap.addAttribute("returnList", rspData.getValue("SvcCont.PageData"));
        } else {
            modelMap.put("errMsg", svcResponse.getErrMsg());
        }
        // 跳转页面
        return new ModelAndView("/eland/rp/rp000/rp000_list", modelMap);
    }

    /**
     *  实体指标统计
     */
    public ModelAndView entitySummary(HttpServletRequest request, HttpServletResponse response) {
        // 输出业务
        ModelMap modelMap = new ModelMap();
        // 获取输入参数
        RequestUtil requestUtil = new RequestUtil(request);
        String rhtRegId = requestUtil.getStrParam("rhtRegId");

        String[] xAttrs = requestUtil.getStrParam("xAttrs").split(",");
        String[] legendAttrs = new String[0];
        if (StringUtil.isNotEmptyOrNull(requestUtil.getStrParam("legendAttrs"))) {
            legendAttrs = requestUtil.getStrParam("legendAttrs").split(",");
        }
        String[] resultAttrsName = requestUtil.getStrParam("resultAttrsName").split(",");
        String[] resultAttrsType = requestUtil.getStrParam("resultAttrsType").split(",", resultAttrsName.length);

        // 请求参数
        String prePath = "OpData.";
        XmlBean opData = new XmlBean();

        String entityName = "HouseInfo";
        if (StringUtil.isNotEmptyOrNull(request.getParameter("entityName"))) {
            entityName = request.getParameter("entityName");
        }
        //调用权限过滤组件。
        opData.setStrValue(prePath + "opName", "QUERY_ATTR_COUNT_CMPT");
        opData.setStrValue(prePath + "RegFilter.attrName", "${entityName}.ttRegId");
        opData.setStrValue(prePath + "RegFilter.attrValue", rhtRegId);
        String regUseType = request.getParameter("regUseType");
        if (StringUtil.isEmptyOrNull(regUseType)) {
            regUseType = "1"
        }
        opData.setStrValue(prePath + "RegFilter.regUseType", regUseType);
        opData.setStrValue(prePath + "RegFilter.setPath", "OpData.Conditions");

        //过滤的组织 字段名
        String regOrgIdValue = request.getParameter("rhtOrgId");
        String rhtType = request.getParameter("rhtType");
        opData.setStrValue(prePath + "OrgFilter.attrName", "\$entityName.\$ttOrgId");
        opData.setStrValue(prePath + "OrgFilter.attrValue", regOrgIdValue);
        opData.setStrValue(prePath + "OrgFilter.rhtType", rhtType);

        opData.setStrValue(prePath + "OrgFilter.setPath", "OpData.Conditions");

        // 查询维度
        for (int i = 0; i < xAttrs.length; i++) {
            opData.setStrValue(prePath + "XAttrs.attrName[${i}]", xAttrs[i]);
        }
        // 统计维度函数
        String xAttrFuncs = request.getParameter("xAttrFuncs");
        if (StringUtil.isNotEmptyOrNull(xAttrFuncs)) {
            String[] xAttrFuncArr = xAttrFuncs.split(",");
            int tempCount = 0;
            for (int i = 0; i < xAttrFuncArr.length; i++) {
                String funcItem = xAttrFuncArr[i];
                if (StringUtil.isEmptyOrNull(funcItem)) {
                    continue;
                } else {
                    String[] funcDefArgs = funcItem.split("\\|");
                    if (funcDefArgs.length < 3) {
                        continue;
                    } else {
                        opData.setStrValue(prePath + "XAttrFuncs.XAttrFunc[${tempCount}].attrName", funcDefArgs[0]);
                        opData.setStrValue(prePath + "XAttrFuncs.XAttrFunc[${tempCount}].funcName", funcDefArgs[1]);
                        opData.setStrValue(prePath + "XAttrFuncs.XAttrFunc[${tempCount}].param", funcDefArgs[2].replaceAll(":", ","));
                        tempCount++;
                    }
                }
            }
        }
        // 统计指标
        for (int i = 0; i < resultAttrsType.length; i++) {
            if (StringUtil.isNotEmptyOrNull(resultAttrsName[i])) {
                opData.setStrValue(prePath + "ResultAttrs.AttrInfo[${i}].type", resultAttrsType[i]);
                opData.setStrValue(prePath + "ResultAttrs.AttrInfo[${i}].attrName", resultAttrsName[i]);
            }
        }
        // 统计维度
        for (int i = 0; i < legendAttrs.length; i++) {
            opData.setStrValue(prePath + "LegendAttrs.attrName[${i}]", legendAttrs[i]);
        }

        // 查询条件
        String conditionNames = requestUtil.getStrParam("conditionName");
        String conditions = requestUtil.getStrParam("condition");
        String conditionValues = requestUtil.getStrParam("conditionValue");
        String[] conditionFieldArr = conditionNames.split(",");
        String[] conditionArr = conditions.split(",");
        String[] conditionValueArr = conditionValues.split(",", conditionFieldArr.length);
        int addCount = 0;
        boolean whetherCrossEntity = false;
        if (conditionNames.contains(".")) {
            whetherCrossEntity = true;
        }
        for (int i = 0; i < conditionFieldArr.length; i++) {
            if (StringUtil.isEmptyOrNull(conditionFieldArr[i])
                    || StringUtil.isEmptyOrNull(conditionArr[i])
                    || StringUtil.isEmptyOrNull(conditionValueArr[i])) {
                continue;
            }
            if (whetherCrossEntity) {
                opData.setStrValue(prePath + "Conditions.Condition[${addCount}].fieldName", conditionFieldArr[i]);
            } else {
                opData.setStrValue(prePath + "Conditions.Condition[${addCount}].fieldName", entityName + "." + conditionFieldArr[i]);
            }
            opData.setStrValue(prePath + "Conditions.Condition[${addCount}].operation", conditionArr[i]);
            if ("like".equalsIgnoreCase(conditionArr[i])) {
                opData.setStrValue(prePath + "Conditions.Condition[${addCount++}].fieldValue", "%" + conditionValueArr[i] + "%");
            } else if ("in".equalsIgnoreCase(conditionArr[i])) {
                System.out.println(conditionValueArr[i]);
                String usedValue = conditionValueArr[i].replaceAll("\\|", ",");
                opData.setStrValue(prePath + "Conditions.Condition[${addCount++}].fieldValue", "(" + usedValue + ")");
            } else {
                opData.setStrValue(prePath + "Conditions.Condition[${addCount++}].fieldValue", conditionValueArr[i]);
            }
        }
        // 排序字段
        String orderAttrsName = requestUtil.getStrParam("orderAttrsName");
        String orderAttrsOrder = requestUtil.getStrParam("orderAttrsOrder");
        String[] orderAttrsNameArr = orderAttrsName.split(",");
        String[] orderAttrsOrderArr = orderAttrsOrder.split(",", orderAttrsNameArr.length);
        addCount = 0;
        for (int i = 0; i < orderAttrsNameArr.length; i++) {
            if (StringUtil.isEmptyOrNull(orderAttrsNameArr[i])
                    || StringUtil.isEmptyOrNull(orderAttrsOrderArr[i])) {
                continue;
            }
            opData.setStrValue(prePath + "SortFields.SortField[${addCount}].fieldName", orderAttrsNameArr[i]);
            opData.setStrValue(prePath + "SortFields.SortField[${addCount}].sortOrder", orderAttrsOrderArr[i]);
        }

        // 执行组件查询
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.addOp("PRIVI_FILTER", opData);
        SvcResponse svcResponse = query(svcRequest);
        XmlBean titleBean = null;
        if (svcResponse.isSuccess()) {
            titleBean = svcResponse.getFirstOpRsp("PRIVI_FILTER");
            if (titleBean != null) {
                titleBean = titleBean.getBeanByPath("Operation.OpResult");
            }
        }
        if (titleBean != null) {
            /* 饼图类型 */

            /* 柱状图类型 */
            // X轴数据标题
            List<XmlBean> xAxisTitle = new ArrayList<XmlBean>();
            // y轴维度标题
            List<XmlBean> yAxisTitle = new ArrayList<XmlBean>();
            // 处理数据
            List<XmlBean> seriesTitle = new ArrayList<XmlBean>();
            int fieldCount = titleBean.getListNum("OpResult.TableDef.Field");
            for (int i = 0; i < fieldCount; i++) {
                XmlBean tempBean = titleBean.getBeanByPath("OpResult.TableDef.Field[${i}]");
                String trans = tempBean.getStrValue("Field.trans");
                // 统计维度
                String stype = tempBean.getStrValue("Field.stype");
                if (StringUtil.isEmptyOrNull(trans) || !"true".equals(trans)) {
                    if ("xAttrs".equals(stype)) {
                        // X轴数据标题
                        xAxisTitle.add(tempBean);
                    } else if ("legend".equals(stype)) {
                        // Y轴数据标题
                        yAxisTitle.add(tempBean);
                    } else {
                        if ("1".equals(stype)) {
                            tempBean.setStrValue("Field.name", tempBean.getStrValue("Field.name") + "(求和)")
                        } else if ("2".equals(stype)) {
                            tempBean.setStrValue("Field.name", tempBean.getStrValue("Field.name") + "(计数)")
                        } else if ("3".equals(stype)) {
                            tempBean.setStrValue("Field.name", tempBean.getStrValue("Field.name") + "(平均)")
                        }
                        seriesTitle.add(tempBean);
                    }
                }
            }
            // X轴一级分类
            List<XmlBean> titles = new ArrayList<XmlNode>();
            for (XmlBean temp : xAxisTitle) {
                titles.add(temp.getRootNode());
            }
            // X轴二级分类
            for (XmlBean temp : yAxisTitle) {
                titles.add(temp.getRootNode());
            }
            // 汇总指标
            for (XmlBean temp : seriesTitle) {
                titles.add(temp.getRootNode());
            }
            modelMap.put("titles", titles);
            // 处理数据
            int rowCount = titleBean.getListNum("OpResult.TableData.Row");
            // X轴数据
            Set<String> xAxis = new LinkedHashSet<String>();
            // y轴维度
            Set<String> yAxis = new LinkedHashSet<String>();
            // 循环所有的数据，处理数据格式为：
            Map<String, List<String>> series = new LinkedHashMap<String, List<String>>();
            for (int i = 0; i < rowCount; i++) {
                // 获取记录行
                XmlBean tempRow = titleBean.getBeanByPath("OpResult.TableData.Row[${i}]");
                // 全部信息为空的数据，不进行处理
                boolean allEmpty = true;
                for (String temp : tempRow.getNodeNames("Row")) {
                    String value = tempRow.getStrValue("Row." + temp);
                    if (!StringUtil.isEmptyOrNull(value) && !"0".equals(value)) {
                        allEmpty = false;
                        break;
                    }
                }
                if (allEmpty) {
                    continue;
                }

                // 累计横坐标所有值(一级维度的所有值)
                List<String> row = new ArrayList<String>();
                for (XmlBean temp : xAxisTitle) {
                    String rname = temp.getStrValue("Field.rname");
                    String value = tempRow.getStrValue("Row." + rname);
                    row.add(value);
                }
                String xAxisKey = row.join("-");
                xAxis.add(xAxisKey);

                // 累计横坐标二级维度的所有值
                List<String> yRow = new ArrayList<String>();
                for (XmlBean temp : yAxisTitle) {
                    String rname = temp.getStrValue("Field.rname");
                    String value = tempRow.getStrValue("Row." + rname);
                    yRow.add(value);
                }
//                row.addAll(yRow);

                // 处理数据值
                for (XmlBean dataTemp : seriesTitle) {
                    String dataRname = dataTemp.getStrValue("Field.rname");
                    String dataValue = tempRow.getStrValue("Row." + dataRname);
                    // 数据区域名称
                    String yAxisKey = yRow.join("-");
                    yAxis.add(yAxisKey);
                    //
                    row.add(dataValue);
                    series.put(xAxisKey + "-" + yAxisKey, row);
                }
            }
            modelMap.put("series", series);
            // 设置X轴图标数据
            modelMap.put("xAxis", JSONArray.fromObject(xAxis).toString());
            // 各个Y轴的数据
            Map<String, String> seriesCharData = new LinkedHashMap<String, String>();
            for (String yTemp : yAxis) {
                int i = 0;
                for (XmlBean temp : seriesTitle) {
                    String name = temp.getStrValue("Field.name");
                    if (StringUtil.isNotEmptyOrNull(yTemp)) {
                        name = yTemp + "-" + name;
                    }
                    List<String> yValues = new ArrayList<String>();
                    for (String xTemp : xAxis) {
                        List<String> yValue = series.get(xTemp + "-" + yTemp);
                        if (yValue == null) {
                            yValues.add("");
                        } else {
                            yValues.add(yValue[xAxisTitle.size() + yAxisTitle.size() + i])
                        }
                    }
                    seriesCharData.put(name, JSONArray.fromObject(yValues).toString());
                    i++;
                }
            }
            modelMap.put("seriesCharData", seriesCharData);
            modelMap.put("legendData", JSONArray.fromObject(seriesCharData.keySet()).toString());
        }
        modelMap.put("toContainer", requestUtil.getStrParam("toContainer"));
        String chartType = request.getParameter("chartType");
        String forwardPage = "/eland/rp/rp000/rp000_bar";
        if ("pie".equals(chartType)) {
            forwardPage = "/eland/rp/rp000/rp000_pie";
        }
        return new ModelAndView(forwardPage, modelMap);
    }

}
