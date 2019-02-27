<%--
  Created by IntelliJ IDEA.
  User: shfb_wang
  Date: 2015/5/17 0017 9:52
  Copyright(c) 北京四海富博计算机服务有限公司
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div style="position: relative; width: 100%;">
    <div class="pageNav">
        <a href="javascript:void(0)">统计报表</a>--->
        <a class="link" href="javascript:void(0)">报表数据展示</a>
    </div>
    <div class="panelBar mar5">
        <ul class="toolBar">
            <li onclick="rpt002.showOrHideCond();">
                <a class="find" href="javascript:void(0)"><span>条件定义</span></a>
            </li>
            <li onclick="rpt002.generateReport('pdf');">
                <a class="reflesh" href="javascript:void(0)"><span>刷新</span></a>
            </li>
            <li>
                <a class="export" href="javascript:void(0)">
                    <span onclick="rpt002.doExport();">导出</span>
                    <label><input type="radio" value="excel" checked name="exportType">EXCEL
                        <input type="radio" value="pdf" name="exportType">PDF
                    </label>
                </a>
            </li>
        </ul>
    </div>
    <iframe name="rpt002IFrame" allowTransparency="false" id="rpt002IFrame"
            style="position: absolute;top: 65px;border:0;width:100%;background-color:#f5f9fc;margin: 5px;"
            src="${pageContext.request.contextPath}/oframe/report/rpt002/rpt002-condition.gv?templateName=houseInfoReport"
            height="300px;">
    </iframe>
    <%--内容--%>
    <div id="rpt002_ReportData"
         style="border: 1px solid #B3E4EB;"
         class="mar5" layoutH="83">
    </div>
</div>
<oframe:script src="${pageContext.request.contextPath}/oframe/report/rpt002/js/rpt002.js" type="text/javascript"/>
<oframe:script src="${pageContext.request.contextPath}/oframe/plugin/pdfobject/pdfobject.js" type="text/javascript"/>