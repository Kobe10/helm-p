import com.shfb.oframe.core.util.common.NumberUtil
import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.util.spring.SvcUtil
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import net.sf.json.JSONObject
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created with IntelliJ IDEA
 * User: shfb_wang 
 * Date: 2015/6/11 0011 14:51
 * Copyright(c) 北京四海富博计算机服务有限公司
 */
class mb008 extends GroovyController {
    String[] colorArr = ["#58a7f7", "#ff0000", "#84ff00", "#ffe400", "#0090ff", "#ef0eef", "#1dd326", "#0042ff", "#ffea00",
                         "#ff0000", "#0096ff", "#30ff00", "#a40ff9", "#2cc6d8", "#9e960e", "#ff7575", "#2cd8ba", "#00e4ff",
                         "#ffaa4e", "#3c9615", "#fc69fa", "#9473ff", "#2cd8c2", "#d8ff00", "#ff009c", "#d88d2c", "#0e8a8a",
                         "#fcb1b1", "#67b414", "#00d8ff", "#81f3bf", "#f4bdf4", "#1dd326", "#db804a", "#a384c3", "#b73400",
                         "#2328e7", "#5ce8f8", "#fffaa3", "#9d6ef3", "#38fe3c", "#31b9c9", "#9f4ae7", "#989b48", "#da2bd8",
                         "#2d9dc6", "#90f1a0", "#ff9e6f", "#ffa4dc", "#9fe0f8", "#0a8307", "#ff9000", "#e941a8"];

    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        RequestUtil requestUtil = new RequestUtil(request);
        int prjCd = NumberUtil.getIntFromObj(request.getParameter("prjCd"));
        //取地图颜色作为指标颜色
        Map<String, String> colorMap = getCfgCollection(request, "MAP_COLORS", true, prjCd);
        String colorStr = "";
        if (colorMap.size() > 0) {
            for (Map.Entry<String, String> entry : colorMap.entrySet()) {
                colorStr = entry.getValue();
                if (colorStr != null) {
                    break;
                }
            }
            colorStr = "\"" + colorStr.toString().replace(" ", "").replace(",", "\",\"") + "\"";
        } else {//没有地图颜色，则手动设置颜色
            colorStr = "\"" + colorArr.toString().replace(" ", "").replace("[", "").replace("]", "").replace(",", "\",\"") + "\"";
        }
        modelMap.put("colorStr", colorStr);
        modelMap.put("prjJobCd", requestUtil.getStrParam("prjJobCd"));
        modelMap.put("prjJobGroup", requestUtil.getStrParam("prjJobGroup"));
        modelMap.put("initParams", JSONObject.fromObject(requestUtil.getRequestMap(request)).toString());
        if (StringUtil.isNotEmptyOrNull(request.getParameter("title"))) {
            modelMap.put("title", java.net.URLDecoder.decode(request.getParameter("title"), "utf-8"));
        }
        modelMap.put("isfull", request.getParameter("isfull"));
        return new ModelAndView("/eland/mb/mb008/mb008_org", modelMap);
    }

    /**
     * 按照区域统计维度展示数据
     * @param request 请求信息
     * @param response 响应结果
     * @return
     */
    public ModelAndView initReg(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        RequestUtil requestUtil = new RequestUtil(request);
        int prjCd = NumberUtil.getIntFromObj(request.getParameter("prjCd"));
        //取地图颜色作为指标颜色
        Map<String, String> colorMap = getCfgCollection(request, "MAP_COLORS", true, prjCd);
        String colorStr = "";
        if (colorMap.size() > 0) {
            for (Map.Entry<String, String> entry : colorMap.entrySet()) {
                colorStr = entry.getValue();
                if (colorStr != null) {
                    break;
                }
            }
            colorStr = "\"" + colorStr.toString().replace(" ", "").replace(",", "\",\"") + "\"";
        } else {//没有地图颜色，则手动设置颜色
            colorStr = "\"" + colorArr.toString().replace(" ", "").replace("[", "").replace("]", "").replace(",", "\",\"") + "\"";
        }
        modelMap.put("colorStr", colorStr);
        modelMap.put("prjJobCd", requestUtil.getStrParam("prjJobCd"));
        modelMap.put("prjJobGroup", requestUtil.getStrParam("prjJobGroup"));
        modelMap.put("initParams", JSONObject.fromObject(requestUtil.getRequestMap(request)).toString());
        if (StringUtil.isNotEmptyOrNull(request.getParameter("title"))) {
            modelMap.put("title", java.net.URLDecoder.decode(request.getParameter("title"), "utf-8"));
        }
        modelMap.put("isfull", request.getParameter("isfull"));
        return new ModelAndView("/eland/mb/mb008/mb008_reg", modelMap);
    }

    /**
     * 按照管理组织统计数据
     * @param request 请求报文
     * @param response 响应报文
     * @return ModelAndView 图标数据结果
     */
    public void orgSummary(HttpServletRequest request, HttpServletResponse response) {
        RequestUtil requestUtil = new RequestUtil(request);
        //接收入参 xmlBean
        XmlBean reqData = new XmlBean();
        Map<String, Object> paramMap = requestUtil.getRequestMap(request);
        // 获取请求的orgId路径
        String orgIdPath = paramMap.get("orgIdPath");
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
        // 判断编号是否为空
        String queryOrgId = null;
        if (StringUtil.isNotEmptyOrNull(orgIdPath)) {
            String[] tempArr = orgIdPath.split("/");
            queryOrgId = tempArr.last().replaceAll("/", "");
            reqData.setStrValue("SvcCont.QuerySvc.prjOrgId", queryOrgId);
        }
        String orgName = "";
        if (!StringUtil.isEmptyOrNull(queryOrgId)) {
            // 获取管理组织名称
            SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
            // 调用服务获取组织下的员工信息
            svcRequest.setValue("Request.SvcCont.Org.Node.orgId", queryOrgId);
            svcRequest.setValue("Request.SvcCont.Org.Node.prjCd", request.getParameter("prjCd"));
            SvcResponse svcResponse = callService("orgService", "queryNodeInfo", svcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean tempBean = svcResponse.getRspData().getBeanByPath("SvcCont.Org.Node");
                if (tempBean != null) {
                    orgName = tempBean.getStrValue("Node.orgName");
                }
            }
        }
        //调用 旧服务获取 查询结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = SvcUtil.callSvc("reportService", "queryOrgChartData", svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean rspData = svcResponse.getRspData().getBeanByPath("SvcCont.ReportDatas");
            if (rspData != null && rspData.getValue("ReportDatas.xAxis") instanceof List) {
                //获取多个维度的 数据
                int orgCount = rspData.getListNum("ReportDatas.ReportData");
                //获取横坐标
                List xAxis = rspData.getValue("ReportDatas.xAxis");
                // 图例
                List legendList = new ArrayList();
                //大标题
                String titleText = "";
                // 各个维度的数据
                List<List> reportDataList = new ArrayList();
                List<Integer> orgIdList = new ArrayList();
                XmlBean seriesBean = new XmlBean();
                int num = 0;
                // 循环所有的管理组织
                for (int i = 0; i < orgCount; i++) {
                    // 以组织维度来绘制数据
                    String prjOrgName = rspData.getStrValue("ReportDatas.ReportData[${i}].prjOrgName");
                    if (StringUtil.isNotEmptyOrNull(orgName) && prjOrgName.startsWith(orgName)) {
                        String tempStr = prjOrgName.substring(orgName.length(), prjOrgName.length());
                        if (tempStr.endsWith("组")) {
                            tempStr = tempStr.substring(0, tempStr.length() - 1);
                        }
                        legendList.add(tempStr);
                    } else {
                        legendList.add(prjOrgName);
                    }
                    // 管理组织编号
                    int orgId = rspData.getIntValue("ReportDatas.ReportData[${i}].prjOrgId");
                    orgIdList.add(orgId);
                    //
                    titleText = rspData.getStrValue("ReportDatas.ReportData[${i}].series.name");
                    reportDataList.add((List) rspData.getValue("ReportDatas.ReportData[${i}].series.data"));
                }
                // 按照数据维度，组织各个组织节点的数据
                List dataRowList = new ArrayList();
                if (reportDataList.size() > 0) {
                    // 循环数据维度数量
                    int statusCount = reportDataList.get(0).size();
                    for (int j = 0; j < statusCount; j++) {
                        List data = new ArrayList();
                        for (int l = 0; l < reportDataList.size(); l++) {
                            String dataStr = reportDataList.get(l).get(j);
                            data.add(dataStr);
                        }
                        dataRowList.add(data);
                    }
                }
                // 循环补充各节点的数据
                for (int i = 0; i < dataRowList.size(); i++) {
                    seriesBean.setStrValue("series.series[${num}].name", xAxis.get(i));
                    seriesBean.setStrValue("series.series[${num}].stackName",
                            rspData.getStrValue("ReportDatas.ReportData[0].series.stackName"));
                    seriesBean.setStrValue("series.series[${num}].chartType",
                            rspData.getStrValue("ReportDatas.ReportData[0].series.chartType"));
                    seriesBean.setValue("series.series[${num++}].data", dataRowList.get(i));
                }

                String jsonData = rspData.toJson();
                if (seriesBean != null) {
                    jsonData = seriesBean.toJson();
                }
                JSONObject xAxisJson = new JSONObject();
                xAxisJson.put("xAxis", xAxis);
                xAxisJson.put("orgIds", orgIdList);
                xAxisJson.put("orgName", orgName);
                JSONObject legendJson = new JSONObject();
                legendJson.put("legend", legendList);
                ResponseUtil.printSvcResponse(response, svcResponse, """ "data":${
                    jsonData.toString()
                },"orgIdPath":"${orgIdPath}","legend":${legendJson.toString()},"xAxis":${
                    xAxisJson.toString()
                },"titleText": "${titleText}" """);
            } else {
                ResponseUtil.printSvcResponse(response, svcResponse, """""");
            }
        } else {
            ResponseUtil.printSvcResponse(response, svcResponse, """""");
        }
    }

    /**
     * 按照区域统计数据
     * @param request 请求报文
     * @param response 响应报文
     * @return ModelAndView 图标数据结果
     */
    public void regSummary(HttpServletRequest request, HttpServletResponse response) {
        RequestUtil requestUtil = new RequestUtil(request);
        //接收入参 xmlBean
        XmlBean reqData = new XmlBean();
        Map<String, Object> paramMap = requestUtil.getRequestMap(request);
        // 获取请求的orgId路径
        String regIdPath = paramMap.get("regIdPath");
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
        // 目标区域编号R
        String prjRegId = null;
        if (StringUtil.isNotEmptyOrNull(regIdPath)) {
            String[] tempArr = regIdPath.split("/");
            prjRegId = tempArr.last().replaceAll("/", "");
            reqData.setStrValue("SvcCont.QuerySvc.prjRegId", prjRegId);
        }

        String regName = "";
        if (!StringUtil.isEmptyOrNull(prjRegId)) {
            // 获取区域名称
            SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
            // 调用服务获取组织下的员工信息
            svcRequest.setValue("Request.SvcCont.PrjReg.regId", prjRegId);
            svcRequest.setValue("Request.SvcCont.PrjReg.prjCd", request.getParameter("prjCd"));
            String regUseType = request.getParameter("regUseType");
            if (StringUtil.isEmptyOrNull(regUseType)) {
                regUseType = "1";
            }
            svcRequest.setValue("Request.SvcCont.PrjReg.regUseType", regUseType);
            SvcResponse svcResponse = callService("prjRegService", "queryPrjReg", svcRequest);
            if (svcResponse.isSuccess()) {
                XmlBean tempBean = svcResponse.getRspData().getBeanByPath("SvcCont.PrjReg");
                if (tempBean != null) {
                    regName = tempBean.getStrValue("PrjReg.regName");
                }
            }
        }

        //调用 旧服务获取 查询结果
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = SvcUtil.callSvc("reportService", "queryRegChartData", svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean rspData = svcResponse.getRspData().getBeanByPath("SvcCont.ReportDatas");
            if (rspData != null && rspData.getValue("ReportDatas.xAxis") instanceof List) {
                //获取多个维度的 数据
                int m = rspData.getListNum("ReportDatas.ReportData");
                //获取横坐标
                List xAxis = rspData.getValue("ReportDatas.xAxis");
                // 图例
                List legendList = new ArrayList();
                //大标题
                String titleText = "";
                // 各个维度的数据
                List<List> reportDataList = new ArrayList();
                List<Integer> regIdList = new ArrayList();
                XmlBean seriesBean = new XmlBean();
                int num = 0;
                for (int i = 0; i < m; i++) {
                    // 以组织维度来绘制数据
                    String prjRegName = rspData.getStrValue("ReportDatas.ReportData[${i}].prjRegName");
                    if (StringUtil.isNotEmptyOrNull(prjRegName) && prjRegName.startsWith(regName)) {
                        String tempStr = prjRegName.substring(regName.length(), prjRegName.length());
                        legendList.add(tempStr);
                    } else {
                        legendList.add(prjRegName);
                    }
                    // 管理区域变量
                    int tempRegId = rspData.getIntValue("ReportDatas.ReportData[${i}].prjRegId");
                    regIdList.add(tempRegId);
                    //
                    titleText = rspData.getStrValue("ReportDatas.ReportData[${i}].series.name");
                    reportDataList.add((List) rspData.getValue("ReportDatas.ReportData[${i}].series.data"));
                }
                //
                List dataRowList = new ArrayList();
                if (reportDataList.size() > 0) {
                    for (int j = 0; j < reportDataList.get(0).size(); j++) {
                        List data = new ArrayList();
                        for (int l = 0; l < reportDataList.size(); l++) {
                            String dataStr = reportDataList.get(l).get(j);
                            data.add(dataStr);
                        }
                        dataRowList.add(data);
                    }
                }

                for (int i = 0; i < dataRowList.size(); i++) {
                    seriesBean.setStrValue("series.series[${num}].name", xAxis.get(i));
                    seriesBean.setStrValue("series.series[${num}].stackName",
                            rspData.getStrValue("ReportDatas.ReportData[0].series.stackName"));
                    seriesBean.setStrValue("series.series[${num}].chartType",
                            rspData.getStrValue("ReportDatas.ReportData[0].series.chartType"));
                    seriesBean.setValue("series.series[${num++}].data", dataRowList.get(i));
                }

                String jsonData = rspData.toJson();
                if (seriesBean != null) {
                    jsonData = seriesBean.toJson();
                }
                JSONObject xAxisJson = new JSONObject();
                xAxisJson.put("xAxis", xAxis);
                xAxisJson.put("regIds", regIdList);
                xAxisJson.put("regName", regName);
                JSONObject legendJson = new JSONObject();
                legendJson.put("legend", legendList);
                ResponseUtil.printSvcResponse(response, svcResponse, """ "data":${
                    jsonData.toString()
                },"regIdPath":"${regIdPath}","legend":${legendJson.toString()},"xAxis":${
                    xAxisJson.toString()
                },"titleText": "${titleText}" """);
            } else {
                ResponseUtil.printSvcResponse(response, svcResponse, """""");
            }
        } else {
            ResponseUtil.printSvcResponse(response, svcResponse, """""");
        }
    }
}