import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.common.XmlNode
import com.shfb.oframe.core.util.report.ReportData
import com.shfb.oframe.core.util.report.ReportFactory
import com.shfb.oframe.core.util.spring.SpringContextUtil
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import net.sf.json.JSONArray
import net.sf.json.JSONObject
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.sql.DataSource
import java.sql.Connection

/**
 * Created with IntelliJ IDEA
 * User: shfb_wang 
 * Date: 2015/5/17 0017 9:50
 * Copyright(c) 北京四海富博计算机服务有限公司
 */
class rpt002 extends GroovyController {

    /**
     * 根据报表模板ID获取报表的属性，初始化报表查询界面
     * @param request 请求信息
     * @param response 响应信息
     * @return 报表生成初始化化界面
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String templateName = request.getParameter("templateName");
        modelMap.put("templateName", templateName);
        return new ModelAndView("/oframe/report/rpt002/rpt002", modelMap);
    }

    /**
     * 生成报表文件，返回报表的访问地址
     * @param request 请求信息
     * @param response 响应信息
     * @return 报表的URL访问地址
     */
    public ModelAndView generateReport(HttpServletRequest request, HttpServletResponse response) {
        String templateName = request.getParameter("templateName");
        RequestUtil requestUtil = new RequestUtil();
        Map<String, Object> queryMap = requestUtil.getRequestMap(request);
        ReportData reportData = new ReportData();
        String exportType = "pdf";
        if (queryMap != null) {
            Iterator<String> it = queryMap.keySet().iterator();
            while (it.hasNext()) {
                String keyName = it.next();
                if ("exportType".equals(keyName)) {
                    exportType = queryMap.get(keyName);
                } else {
                    reportData.addParameters(keyName, queryMap.get(keyName));
                }
            }
        }
        String reportUrl = "";
        DataSource dataSource = null;
        Connection conn = null;
        String errMsg = "";
        boolean success = true;
        try {
            dataSource = SpringContextUtil.getBean("dataSource");
            conn = dataSource.getConnection();
            reportData.setConnection(conn);
            reportData.setIsUseSql(true);
            if ("pdf".equals(exportType)) {
                reportUrl = ReportFactory.viewReport(reportData, "${templateName}", ReportFactory.ReportFormat.PDF);
            } else {
                reportUrl = ReportFactory.viewReport(reportData, "${templateName}", ReportFactory.ReportFormat.EXCEL);
            }
        } catch (Exception e) {
            success = false;
            errMsg = e.getMessage();
            log.error("生成报表错误", e);
        } finally {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        }
        // 输出报表生成结果，返回报表访问url路径
        String jsonStr = "{\"success\": " + success + ", \"errMsg\": \"" + errMsg + "\"," + "\"reportUrl\": \"${reportUrl}\"" + "}";
        ResponseUtil.printAjax(response, jsonStr)
    }

    /**
     * 生成报表文件，返回报表的访问地址
     * @param request 请求信息
     * @param response 响应信息
     * @return 报表的URL访问地址
     */
    public ModelAndView condition(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        String templateName = request.getParameter("templateName");

        // 获取模板需要的参数
        XmlBean report = ReportFactory.getReportParameter(templateName);
        List<Map<String, String>> returnList = new ArrayList<Map<String, String>>();
        Map<String, Map<String, String>> allOptions = new HashMap<String, Map<String, String>>();
        int count = report.getListNum("Report.Parameter");
        // 动态执行groovy脚本使用
        Binding binding = new Binding();
        GroovyShell shell = new GroovyShell(binding);
        // 获取字符串
        for (int i = 0; i < count; i++) {
            XmlBean parameter = report.getBeanByPath("Report.Parameter[${i}]");
            Map<String, String> property = new LinkedHashMap<String, String>();
            // 固有熟悉字段
            property.put("_name", parameter.getStrValue("Parameter.name"));
            property.put("_desc", parameter.getStrValue("Parameter.desc"));
            property.put("_defaultValue", shell.evaluate(parameter.getStrValue("Parameter.defaultValue")));
            int parCount = parameter.getListNum("Parameter.Property");
            for (int j = 0; j < parCount; j++) {
                String name = parameter.getStrValue("Parameter.Property[${j}].name");
                String value = parameter.getStrValue("Parameter.Property[${j}].value");
                property.put(name, value);
            }
            // 处理下拉类型
            if ("select".equals(property.get("type"))) {
                String options = property.get("options");
                if (StringUtil.isNotEmptyOrNull(options)) {
                    JSONArray jsonArray = JSONArray.fromObject(options);
                    Map<String, String> optionsMap = new LinkedHashMap<String, String>();
                    for (JSONObject jsonObject : jsonArray) {
                        optionsMap.put(jsonObject.get("name"), jsonObject.get("value"));
                    }
                    allOptions.put(property.get("_name"), optionsMap);
                }
            }
            returnList.add(property);
        }
        // 返回处理结果
        modelMap.put("returnList", returnList);
        modelMap.put("allOptions", allOptions);
        modelMap.put("templateName", templateName);
        return new ModelAndView("/oframe/report/rpt002/rpt00201", modelMap)
    }

}