import com.shfb.oframe.core.util.common.StringUtil
import com.shfb.oframe.core.util.common.XmlBean
import com.shfb.oframe.core.util.common.XmlNode
import com.shfb.oframe.core.util.report.ReportData
import com.shfb.oframe.core.util.report.ReportFactory
import com.shfb.oframe.core.util.service.bo.SvcRequest
import com.shfb.oframe.core.util.service.bo.SvcResponse
import com.shfb.oframe.core.util.spring.SpringContextUtil
import com.shfb.oframe.core.web.controller.GroovyController
import com.shfb.oframe.core.web.util.RequestUtil
import com.shfb.oframe.core.web.util.ResponseUtil
import com.shfb.portal.service.POIReadExcel
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
class rpt001 extends GroovyController {

    /**
     * 根据报表模板ID获取报表的属性，初始化报表查询界面
     * @param request 请求信息
     * @param response 响应信息
     * @return 报表生成初始化化界面
     */
    public ModelAndView init(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        // 获取输入参数
        SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
        svcRequest.setValue("Request.SvcCont.treeType", "1");
        svcRequest.setValue("Request.SvcCont.level", "-1");
        svcRequest.setValue("Request.SvcCont.rhtCd", "SYS_REPORT");
        SvcResponse svcResponse = callService("staffService", "queryMenuTree", svcRequest);
        // 服务调用成功
        if (svcResponse.isSuccess()) {
            // 以权限Id为Key的报表分类展示
            Map<String, XmlNode> reportType = new LinkedHashMap<String, XmlNode>();
            // 以权限ID为Key，报表为数据的集合
            Map<String, List<XmlNode>> reportListByType = new LinkedHashMap<String, List<XmlNode>>();
            // 获取节点数量
            XmlBean menuBean = svcResponse.getBeanByPath("Response.SvcCont.Staff.Menu");
            if (menuBean != null) {
                int count = menuBean.getListNum("Menu.Node");
                for (int i = 0; i < count; i++) {
                    XmlBean tempBean = menuBean.getBeanByPath("Menu.Node[${i}]");
                    String id = tempBean.getStrValue("Node.rhtId");
                    String url = tempBean.getStrValue("Node.navUrl");
                    String pId = tempBean.getStrValue("Node.uRhtId");
                    if ("#".equals(url)) {
                        reportType.put(id, tempBean.getRootNode());
                    } else {
                        List<XmlNode> tempList = reportListByType.get(pId);
                        if (tempList == null) {
                            tempList = new ArrayList<XmlNode>();
                            reportListByType.put(pId, tempList);
                        }
                        tempList.add(tempBean.getRootNode());
                    }
                }
            }
            modelMap.put("reportType", reportType);
            modelMap.put("reportListByType", reportListByType);
        }
        return new ModelAndView("/oframe/report/rpt001/rpt001", modelMap);
    }

    /**
     * 生成报表文件，返回报表的访问地址
     * @param request 请求信息
     * @param response 响应信息
     * @return 报表的URL访问地址
     */
    public ModelAndView generateReport(HttpServletRequest request, HttpServletResponse response) {
        String templateName = request.getParameter("templateName");
        String exportType = request.getParameter("exportType");

        String reportUrl = "";
        String errMsg = "";
        boolean success = true;
        if (StringUtil.contains(templateName, 'formCd=')) {
            String formCd = templateName.substring(templateName.indexOf("=") + 1);
            /**
             * 调用组件获取 ireport模板 文件 路径
             */
            //调用 获取表单 信息 返回页面
            SvcRequest svcRequest = RequestUtil.getSvcRequest(request);
            XmlBean paramData = new XmlBean();
            // 生成报表模板
            paramData.setStrValue("OpData.formCd", formCd);
            // 生成报表参数
            if (StringUtil.isNotEmptyOrNull(exportType)) {
                paramData.setStrValue("OpData.Report.Parameter.Property.attrName", "type");
                paramData.setStrValue("OpData.Report.Parameter.Property.value", exportType);
            } else {
                paramData.setStrValue("OpData.Report", "");
            }
            //放置参数
            String isAddParam = request.getParameter("isAddParam");
            if (StringUtil.isEqual(isAddParam, "true")) {
                Enumeration enu = request.getParameterNames();
                while (enu.hasMoreElements()) {
                    String paraName = (String) enu.nextElement();
                    String paramValue = request.getParameter(paraName);
                    XmlBean paramXml = new XmlBean("<Property/>");
                    paramXml.setStrValue("Property.attrName", paraName);
                    paramXml.setStrValue("Property.value", paramValue);
                    paramData.setBeanByPath("OpData.Report.Parameter", paramXml);
                }
            }
//          paramData.setStrValue("OpData.Report.Parameter[0].Property[0].attrName", "width");
//          paramData.setStrValue("OpData.Report.Parameter[0].Property[0].value", "");
//          paramData.setStrValue("OpData.Report.Parameter[0].Property[1].attrName", "height");
//          paramData.setStrValue("OpData.Report.Parameter[0].Property[1].value", "");

            // 增加到请求参数,调用服务
            svcRequest.addOp("GENERATE_REPORT", paramData);
            // 调用服务，执行数据查询
            SvcResponse pageResponse = query(svcRequest);
            success = pageResponse.isSuccess();
            String docPrePath = "/reports/report"
            if (success) {
                XmlBean opResult = pageResponse.getFirstOpRsp("GENERATE_REPORT");
                String filePath = opResult.getStrValue("Operation.OpResult.resultParam");
                reportUrl = docPrePath + filePath;
            } else {
                errMsg = pageResponse.getErrMsg();
            }
        } else {
            RequestUtil requestUtil = new RequestUtil();
            Map<String, Object> queryMap = requestUtil.getRequestMap(request);
            ReportData reportData = new ReportData();
            exportType = "pdf";
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

            DataSource dataSource = null;
            Connection conn = null;
            try {
                dataSource = SpringContextUtil.getBean("dataSource");
                conn = dataSource.getConnection();
                reportData.addParameters("contextPath", request.getContextPath());
                reportData.setConnection(conn);
                reportData.setIsUseSql(true);
                if ("pdf".equals(exportType)) {
                    reportUrl = ReportFactory.viewReport(reportData, "${templateName}", ReportFactory.ReportFormat.PDF);
                } else if ("html".equals(exportType)) {
                    reportUrl = ReportFactory.viewReport(reportData, "${templateName}", ReportFactory.ReportFormat.HTML);
                } else if ("docx".equals(exportType)) {
                    reportUrl = ReportFactory.viewReport(reportData, "${templateName}", ReportFactory.ReportFormat.WRODX);
                } else {
                    reportUrl = ReportFactory.viewReport(reportData, "${templateName}", ReportFactory.ReportFormat.EXCELX);
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
        }
        String matchStr = "";
        if (success && StringUtil.isNotEmptyOrNull(reportUrl)) {
            matchStr = reportUrl.substring(reportUrl.lastIndexOf("."), reportUrl.length());
            if (StringUtil.isEqual(matchStr, ".xlsx") && StringUtil.isEqual("html", exportType)) {
                // E:\shfb\WS\eland-web\eland-web\src\main\webapp\reports\report
//                String cfgPath = PropertiesUtil.readPath("oframe", "com.shfb.oframe.common.report.reportfactory.report.path");
//                if (cfgPath != null && cfgPath.length() > 0) {
//                    cfgPath = cfgPath.substring(0, cfgPath.lastIndexOf("\\reports"));
//                }
//                String excelPath = cfgPath + reportUrl;
                /** 根据相对路径获取绝对路径 */
                String excelPath = StringUtil.formatFilePath("webroot:" + reportUrl);
                String excelToHtmlStr = POIReadExcel.mains(excelPath);
                // 同路径下，生成同名html文件
                String htmlPath = creatHtmlFile(excelToHtmlStr, excelPath);
                reportUrl = reportUrl.replace(".xlsx", ".html");
            }
        }
        // 输出报表生成结果，返回报表访问url路径
        String jsonStr = "{\"success\": " + success + ", \"errMsg\": \"" + errMsg + "\"," + "\"reportUrl\": \"${reportUrl}\"," + "\"matchStr\": \"${matchStr}\"" + "}";
        ResponseUtil.printAjax(response, jsonStr)
    }

    /**
     * 生成报表文件，返回报表的访问地址
     * @param request 请求信息
     * @param response 响应信息
     * @return 报表的URL访问地址
     */
    public ModelAndView openReport(HttpServletRequest request, HttpServletResponse response) {
        ModelMap modelMap = new ModelMap();
        RequestUtil requestUtil = new RequestUtil(request);
        Map<String, String> requestMap = requestUtil.getRequestMap(request);
        String templateName = request.getParameter("templateName");
        modelMap.put("templateName", templateName);
        modelMap.put("prjCd", requestMap.get("prjCd"));
        //
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> tempEntry : requestMap) {
            stringBuilder.append(tempEntry.getKey() + "=" + tempEntry.getValue() + "&");
        }
        modelMap.put("queryStr", stringBuilder.toString());
        return new ModelAndView("/oframe/report/rpt001/rpt00102", modelMap);
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
//        XmlBean report = ReportFactory.getReportParameter(templateName);
        XmlBean report = new XmlBean();
        List<Map<String, String>> returnList = new ArrayList<Map<String, String>>();
        Map<String, Map<String, String>> allOptions = new HashMap<String, Map<String, String>>();
        int count = report.getListNum("Report.Parameter");
        // 动态执行groovy脚本使用
        Binding binding = new Binding();
        GroovyShell shell = new GroovyShell(binding);
        // 获取字符串
        for (int i = 0; i < count; i++) {
            XmlBean parameter = report.getBeanByPath("Report.Parameter[${i}]");
            // 固有熟悉字段
            String isForPrompting = parameter.getStrValue("Parameter.isForPrompting");
            if ("true".equals(isForPrompting)) {
                Map<String, String> property = new LinkedHashMap<String, String>();
                property.put("_name", parameter.getStrValue("Parameter.name"));
                property.put("_desc", parameter.getStrValue("Parameter.desc"));
                String requestDefaultValue = request.getParameter(parameter.getStrValue("Parameter.name"));
                if (StringUtil.isEmptyOrNull(requestDefaultValue)) {
                    property.put("_defaultValue", shell.evaluate(parameter.getStrValue("Parameter.defaultValue")));
                } else {
                    property.put("_defaultValue", requestDefaultValue);
                }
                // 获取属属性数量
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
        }

        // 返回处理结果
        modelMap.put("returnList", returnList);
        modelMap.put("allOptions", allOptions);
        modelMap.put("templateName", templateName);
        return new ModelAndView("/oframe/report/rpt001/rpt00101", modelMap)
    }

    public String creatHtmlFile(String htmlStr, String excelPath) {
        String htmlPath = excelPath.replace(".xlsx", ".html")
        InputStream is = new ByteArrayInputStream(htmlStr.getBytes());
        File dest = new File(htmlPath);
        FileOutputStream fos = new FileOutputStream(dest);//字节输出流
        BufferedInputStream bis = new BufferedInputStream(is);//为字节输入流加缓冲
        BufferedOutputStream bos = new BufferedOutputStream(fos);//为字节输出流加缓冲
        int length;
        byte[] bytes = new byte[1024 * 20];
        while ((length = bis.read(bytes, 0, bytes.length)) != -1) {
            fos.write(bytes, 0, length);
        }
        bos.close();
        fos.close();
        bis.close();
        is.close();
        return htmlPath;
    }
}