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
class mb007 extends GroovyController {

    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        RequestUtil requestUtil = new RequestUtil(request);
        modelMap.put("prjJobCd", requestUtil.getStrParam("prjJobCd"));
        modelMap.put("prjJobGroup", requestUtil.getStrParam("prjJobGroup"));
        modelMap.put("initParams", JSONObject.fromObject(requestUtil.getRequestMap(request)).toString());
        if (StringUtil.isNotEmptyOrNull(request.getParameter("title"))) {
            modelMap.put("title", java.net.URLDecoder.decode(request.getParameter("title"), "utf-8"));
        }
        return new ModelAndView("/eland/mb/mb007/mb007");
    }

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView regSummary(HttpServletRequest request, HttpServletResponse response) {
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        RequestUtil requestUtil = new RequestUtil(request);
        // 调用服务数据
        XmlBean reqData = new XmlBean();
        // SQL信息
        Map<String, Object> paramMap = requestUtil.getRequestMap(request);
        int k = 0;
        for (Map.Entry tempItem : paramMap) {
            if (tempItem.getKey().equals("prjJobCd")) {
                reqData.setStrValue("SvcCont.QuerySvc.prjJobCd", tempItem.getValue());
            } else if (tempItem.getKey().equals("jobGroup")) {
                reqData.setStrValue("SvcCont.QuerySvc.prjJobGroup", tempItem.getValue());
            } else {
                reqData.setStrValue("SvcCont.ParamInfo.Param[${k}].name", tempItem.getKey());
                reqData.setStrValue("SvcCont.ParamInfo.Param[${k}].value", tempItem.getValue());
                k++;
            }
        }
        // 调用服务
        svcRequest.setReqData(reqData);
        SvcResponse svcResponse = SvcUtil.callSvc("reportService", "queryOrgChartData", svcRequest);
        if (svcResponse.isSuccess()) {
            XmlBean rspData = svcResponse.getRspData();
            if (rspData != null) {
                rspData = rspData.getBeanByPath("SvcCont.ReportDatas");
                //获取出参，拼接需要的数据格式
                int m = rspData.getListNum("ReportDatas.ReportData");
                //指标
                List xAxis = rspData.getValue("ReportDatas.xAxis");
                //图例
                List legendList = new ArrayList();
                //统计标题
                String titleText = "";
                //展示数据，从报文获取
                List<List> reportDataList = new ArrayList();
                XmlBean seriesBean = new XmlBean();
                int num = 0;
                for (int i = 0; i < m; i++) {
                    //获取到指标名称
                    String prjOrgName = rspData.getStrValue("ReportDatas.ReportData[${i}].prjOrgName");
                    legendList.add(prjOrgName);

                    titleText = rspData.getStrValue("ReportDatas.ReportData[${i}].series.name");
                    reportDataList.add((List)rspData.getValue("ReportDatas.ReportData[${i}].series.data"));
                }
                  //数据转换可以使用
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
                    seriesBean.setValue("series.series[${num++}].data", dataRowList.get(i));
                }
                String jsonData = rspData.toJson();
                if(seriesBean != null){
                    jsonData = seriesBean.toJson();
                }
                JSONObject xAxisJson = new JSONObject();
                xAxisJson.put("xAxis", xAxis);
                JSONObject legendJson = new JSONObject();
                legendJson.put("legend", legendList);

                ResponseUtil.printSvcResponse(response, svcResponse, """"data":${
                    jsonData.toString()
                },"legend":${legendJson.toString()},"xAxis":${
                    xAxisJson.toString()
                },"titleText": "${titleText}" """);
            }
        } else {
            ResponseUtil.printSvcResponse(response, svcResponse, """{}""");
        }
    }
}