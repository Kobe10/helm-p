import com.shfb.oframe.core.util.common.BigDecimalUtil
import com.shfb.oframe.core.util.common.DateUtil
import com.shfb.oframe.core.util.common.NumberUtil
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

class rp001 extends GroovyController {

    private static List colorList = new ArrayList();
    //查询组织集合
    private static List<Integer> prjOrgIdList = new ArrayList<Integer>();


    private static Integer[][] sumData = new Integer[2][17];

    static {
        // 颜色列表
        colorList.add("#ffa309");
        colorList.add("#ffcc02");
        colorList.add("#ffcc02");
        colorList.add("#ffcc02");
        colorList.add("#ffcc02");
        colorList.add("#ffcc02");
        colorList.add("#ffa309");
        colorList.add("#9acc02");
        colorList.add("#9acc02");
        colorList.add("#9acc02");
        colorList.add("#9acc02");
        colorList.add("#9acc02");
        colorList.add("#51cc1a");
        colorList.add("#ffcc02");
        colorList.add("#ffcc02");
        colorList.add("#ffcc02");
        colorList.add("#ffa309");
        //
        //督导部
        prjOrgIdList.add(30);
        //一分指 一组~五组 + 一分指总
        prjOrgIdList.add(64);
        prjOrgIdList.add(68);
        prjOrgIdList.add(72);
        prjOrgIdList.add(76);
        prjOrgIdList.add(80);
        prjOrgIdList.add(34);
        //二分指 一组~五组 + 二分指总
        prjOrgIdList.add(84);
        prjOrgIdList.add(88);
        prjOrgIdList.add(92);
        prjOrgIdList.add(96);
        prjOrgIdList.add(100);
        prjOrgIdList.add(38);

        //三四五分指
        prjOrgIdList.add(42);
        prjOrgIdList.add(46);
        prjOrgIdList.add(50);
        // 总数量
        sumData[0][0] = 4603;
        sumData[0][1] = 278;
        sumData[0][2] = 181;
        sumData[0][3] = 248;
        sumData[0][4] = 169;
        sumData[0][5] = 236;
        sumData[0][6] = 1112;
        sumData[0][7] = 287;
        sumData[0][8] = 171;
        sumData[0][9] = 285;
        sumData[0][10] = 278;
        sumData[0][11] = 129;
        sumData[0][12] = 1150;
        sumData[0][13] = null;
        sumData[0][14] = null;
        sumData[0][15] = null;
        sumData[0][16] = 2349;
        //
        sumData[1][0] = 45833;
        sumData[1][1] = 2379;
        sumData[1][2] = 1655;
        sumData[1][3] = 2118;
        sumData[1][4] = 1392;
        sumData[1][5] = 1918;
        sumData[1][6] = 9462;
        sumData[1][7] = 2610;
        sumData[1][8] = 1635;
        sumData[1][9] = 3026;
        sumData[1][10] = 2070;
        sumData[1][11] = 1145;
        sumData[1][12] = 10486;
        sumData[1][13] = null;
        sumData[1][14] = null;
        sumData[1][15] = null;
        sumData[1][16] = 25885;
    }

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        // 返回处理结果
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("/eland/rp/rp001/rp001", modelMap)
    }

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView info(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();

        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        SvcResponse svcResponse = null;
        List resultList = new ArrayList();
        // 调用服务入参
        XmlBean reqData = new XmlBean();
        // 参数信息
        String jobCdType = request.getParameter("jobCdType");
        //指标集合
        List<String[]> jobCdList = new ArrayList<String[]>();
        String[] jobCds = new String[4];
        jobCds[0] = jobCdType + "_yard_all";
        jobCds[1] = jobCdType + "_yard_day";
        jobCds[2] = jobCdType + "_hs_all";
        jobCds[3] = jobCdType + "_hs_day";
        jobCdList.add(jobCds);

        String reportDate;
        if (StringUtil.isEmptyOrNull(request.getParameter("reportDate"))) {
            reportDate = DateUtil.format(DateUtil.getSysDate(), "yyyyMMdd");
        } else {
            reportDate = request.getParameter("reportDate");
        }
        //清空入参
        svcRequest = RequestUtil.getSvcRequest(request);
        reqData.setStrValue("SvcCont.ParamInfo.Param[0].name", "prjCd");
        reqData.setStrValue("SvcCont.ParamInfo.Param[0].value", "205");
        reqData.setStrValue("SvcCont.ParamInfo.Param[1].name", "needSub");
        reqData.setStrValue("SvcCont.ParamInfo.Param[1].value", "false");
        reqData.setStrValue("SvcCont.ParamInfo.Param[2].name", "prjOrgId");
        reqData.setStrValue("SvcCont.ParamInfo.Param[3].name", "jobCd");
        reqData.setStrValue("SvcCont.ParamInfo.Param[4].name", "startTime");
        reqData.setStrValue("SvcCont.ParamInfo.Param[4].value", reportDate);
        reqData.setStrValue("SvcCont.ParamInfo.Param[5].name", "endTime");
        reqData.setStrValue("SvcCont.ParamInfo.Param[5].value", reportDate);
        // 调用服务
        svcRequest.setReqData(reqData);

        for (int i = 0; i < prjOrgIdList.size(); i++) {
            String[] jobArr = new String[jobCds.length];
            resultList.add(jobArr);

            for (int j = 0; j < jobCds.length; j++) {
                reqData.setStrValue("SvcCont.ParamInfo.Param[2].value", prjOrgIdList.get(i));
                reqData.setStrValue("SvcCont.ParamInfo.Param[3].value", jobCdList.get(0)[j]);
                // 调用服务
                svcResponse = callService("chartService", "queryReportData", svcRequest);
                int result = 0;
                //处理成功。
                if (svcResponse.isSuccess()) {
                    XmlBean rspData = svcResponse.getRspData();
                    if (rspData != null) {
                        int count = rspData.getListNum("SvcCont.ReportData.series");
                        for (int x = 0; x < count; x++) {
                            XmlBean reportBean = rspData.getBeanByPath("SvcCont.ReportData.series[" + x + "]");
                            List list = reportBean.getValue("series.data");
                            if (list.size() > 0) {
                                result = NumberUtil.getDoubleFromObj(list.get(0)).intValue();
                            } else {
                                result = "0";
                            }
                        }
                    }
                }
                //处理失败
                jobArr[j] = result;
            }
        }
        // 合并最后三个行为一行数据
        String[] jobArr = new String[jobCds.length];
        for (int i = resultList.size() - 3; i < resultList.size(); i++) {
            String[] tempArr = resultList.get(i);
            for (int j = 0; j < jobArr.length; j++) {
                jobArr[j] = NumberUtil.getIntFromObj(jobArr[j]) + NumberUtil.getIntFromObj(tempArr[j]);
            }
        }
        // 删除最后两行数据
        resultList.add(jobArr);

        // 返回处理结果
        modelMap.put("jobCdType", request.getParameter("jobCdType"));
        modelMap.put("jobCdName", request.getParameter("jobCdName"));
        modelMap.put("resultList", resultList);
        modelMap.put("colorList", colorList);
        return new ModelAndView("/eland/rp/rp001/rp00101", modelMap);
    }

    /**
     * 初始化主界面
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView ratio(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();

        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        SvcResponse svcResponse = null;
        List resultList = new ArrayList();
        // 调用服务入参
        XmlBean reqData = new XmlBean();
        // 参数信息
        String jobCdType = request.getParameter("jobCdType");
        //指标集合
        List<String[]> jobCdList = new ArrayList<String[]>();
        String[] jobCds = new String[1];
        jobCds[0] = "apply_yard_all";
        jobCdList.add(jobCds);

        String reportDate;
        if (StringUtil.isEmptyOrNull(request.getParameter("reportDate"))) {
            reportDate = DateUtil.format(DateUtil.getSysDate(), "yyyyMMdd");
        } else {
            reportDate = request.getParameter("reportDate");
        }
        //清空入参
        svcRequest = RequestUtil.getSvcRequest(request);
        reqData.setStrValue("SvcCont.ParamInfo.Param[0].name", "prjCd");
        reqData.setStrValue("SvcCont.ParamInfo.Param[0].value", "205");
        reqData.setStrValue("SvcCont.ParamInfo.Param[1].name", "needSub");
        reqData.setStrValue("SvcCont.ParamInfo.Param[1].value", "false");
        reqData.setStrValue("SvcCont.ParamInfo.Param[2].name", "prjOrgId");
        reqData.setStrValue("SvcCont.ParamInfo.Param[3].name", "jobCd");
        reqData.setStrValue("SvcCont.ParamInfo.Param[4].name", "startTime");
        reqData.setStrValue("SvcCont.ParamInfo.Param[4].value", reportDate);
        reqData.setStrValue("SvcCont.ParamInfo.Param[5].name", "endTime");
        reqData.setStrValue("SvcCont.ParamInfo.Param[5].value", reportDate);
        // 调用服务
        svcRequest.setReqData(reqData);

        // 合并最后三个行为一行数据
        String[] lastThree = new String[jobCds.length];

        for (int i = 0; i < prjOrgIdList.size(); i++) {
            String[] jobArr = new String[jobCds.length];
            resultList.add(jobArr);
            for (int j = 0; j < jobCds.length; j++) {
                reqData.setStrValue("SvcCont.ParamInfo.Param[2].value", prjOrgIdList.get(i));
                reqData.setStrValue("SvcCont.ParamInfo.Param[3].value", jobCdList.get(0)[j]);
                // 调用服务
                svcResponse = callService("chartService", "queryReportData", svcRequest);
                int result = 0;
                //处理成功。
                if (svcResponse.isSuccess()) {
                    XmlBean rspData = svcResponse.getRspData();
                    if (rspData != null) {
                        int count = rspData.getListNum("SvcCont.ReportData.series");
                        for (int x = 0; x < count; x++) {
                            XmlBean reportBean = rspData.getBeanByPath("SvcCont.ReportData.series[" + x + "]");
                            List list = reportBean.getValue("series.data");
                            if (list.size() > 0) {
                                result = NumberUtil.getDoubleFromObj(list.get(0)).intValue();
                            } else {
                                result = "0";
                            }
                        }
                    }
                }
                // 最后三行的数据
                if (i >= prjOrgIdList.size() - 3) {
                    lastThree[j] = result + NumberUtil.getIntFromObj(lastThree[j]);
                }
                // 计算比率
                String ratio = "";
                if (sumData[j][i] == null || NumberUtil.getDoubleFromObj(sumData[j][i]) == 0) {
                    ratio = "-";
                } else {
                    ratio = BigDecimalUtil.round((NumberUtil.getDoubleFromObj(result) * 100) / NumberUtil.getDoubleFromObj(sumData[j][i]), 2) + "%";
                }
                //处理失败
                jobArr[j] = ratio;
            }
        }
        // 增加自后一样数据
        for (int i = 0; i < jobCds.length; i++) {
            double sumValue = NumberUtil.getDoubleFromObj(sumData[i][16]);
            if (sumValue == null || sumValue == 0) {
                lastThree[i] = "-";
            } else {
                lastThree[i] = BigDecimalUtil.round((NumberUtil.getDoubleFromObj(lastThree[i]) * 100) / sumValue, 2) + "%";
            }
        }
        resultList.add(lastThree);
        // 返回处理结果
        modelMap.put("jobCdType", request.getParameter("jobCdType"));
        modelMap.put("jobCdName", request.getParameter("jobCdName"));
        modelMap.put("resultList", resultList);
        modelMap.put("colorList", colorList);
        return new ModelAndView("/eland/rp/rp001/rp00102", modelMap);
    }

    /**
     * 房产类型
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView hsTp(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();

        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        SvcResponse svcResponse = null;
        List resultList = new ArrayList();
        // 调用服务入参
        XmlBean reqData = new XmlBean();
        // 参数信息
        String jobCdType = request.getParameter("jobCdType");
        //指标集合
        List<String[]> jobCdList = new ArrayList<String[]>();
        String[] jobCds = new String[2];
        jobCds[0] = "hs_tp_prv";
        jobCds[1] = "hs_tp_pub";
        jobCdList.add(jobCds);

        String reportDate;
        if (StringUtil.isEmptyOrNull(request.getParameter("reportDate"))) {
            reportDate = DateUtil.format(DateUtil.getSysDate(), "yyyyMMdd");
        } else {
            reportDate = request.getParameter("reportDate");
        }
        //清空入参
        svcRequest = RequestUtil.getSvcRequest(request);
        reqData.setStrValue("SvcCont.ParamInfo.Param[0].name", "prjCd");
        reqData.setStrValue("SvcCont.ParamInfo.Param[0].value", "205");
        reqData.setStrValue("SvcCont.ParamInfo.Param[1].name", "needSub");
        reqData.setStrValue("SvcCont.ParamInfo.Param[1].value", "false");
        reqData.setStrValue("SvcCont.ParamInfo.Param[2].name", "prjOrgId");
        reqData.setStrValue("SvcCont.ParamInfo.Param[3].name", "jobCd");
        reqData.setStrValue("SvcCont.ParamInfo.Param[4].name", "startTime");
        reqData.setStrValue("SvcCont.ParamInfo.Param[4].value", reportDate);
        reqData.setStrValue("SvcCont.ParamInfo.Param[5].name", "endTime");
        reqData.setStrValue("SvcCont.ParamInfo.Param[5].value", reportDate);
        // 调用服务
        svcRequest.setReqData(reqData);
        // 合并最后三个行为一行数据
        String[] lastThree = new String[jobCds.length];
        for (int i = 0; i < prjOrgIdList.size(); i++) {
            String[] jobArr = new String[jobCds.length];
            resultList.add(jobArr);
            for (int j = 0; j < jobCds.length; j++) {
                reqData.setStrValue("SvcCont.ParamInfo.Param[2].value", prjOrgIdList.get(i));
                reqData.setStrValue("SvcCont.ParamInfo.Param[3].value", jobCdList.get(0)[j]);
                // 调用服务
                svcResponse = callService("chartService", "queryReportData", svcRequest);
                int result = 0;
                //处理成功。
                if (svcResponse.isSuccess()) {
                    XmlBean rspData = svcResponse.getRspData();
                    if (rspData != null) {
                        int count = rspData.getListNum("SvcCont.ReportData.series");
                        for (int x = 0; x < count; x++) {
                            XmlBean reportBean = rspData.getBeanByPath("SvcCont.ReportData.series[" + x + "]");
                            List list = reportBean.getValue("series.data");
                            if (list.size() > 0) {
                                result = NumberUtil.getDoubleFromObj(list.get(0)).intValue();
                            } else {
                                result = "0";
                            }
                        }
                    }
                }
                // 最后三行的数据
                if (i >= prjOrgIdList.size() - 3) {
                    lastThree[j] = result + NumberUtil.getIntFromObj(lastThree[j]);
                }
                //处理失败
                jobArr[j] = result;
            }
        }
        // 增加自后一样数据
        resultList.add(lastThree);

        // 返回处理结果
        modelMap.put("jobCdType", request.getParameter("jobCdType"));
        modelMap.put("jobCdName", request.getParameter("jobCdName"));
        modelMap.put("resultList", resultList);
        modelMap.put("colorList", colorList);
        return new ModelAndView("/eland/rp/rp001/rp00103", modelMap);
    }

    /**
     * 房产类型
     * @param request 请求信息
     * @param response 响应信息
     * @return ModelAndView 展示视图
     */
    public ModelAndView desc(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        // 指标描述
        String jobCdTypeDesc = request.getParameter("jobCdTypeDesc");
        if (StringUtil.isNotEmptyOrNull(jobCdTypeDesc)) {
            modelMap.put("jobCdTypeDesc", java.net.URLDecoder.decode(request.getParameter("jobCdTypeDesc"), "utf-8"))
        }
        return new ModelAndView("/eland/rp/rp001/rp00104", modelMap);
    }
}