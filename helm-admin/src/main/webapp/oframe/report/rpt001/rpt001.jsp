<%--
  Created by IntelliJ IDEA.
  User: shfb_wang
  Date: 2015/5/17 0017 9:52
  Copyright(c) 北京四海富博计算机服务有限公司
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>

<div class="mar5" style="position: relative;">
    <%--左侧区域树--%>
    <div style="width: 240px;float: left;position: relative;" id="rpt001DivTree" layoutH="15" class="panel left_menu">
        <h1>
            <span class="panel_title">报表选择</span>
        </h1>

        <div class="accordion" extendAll="true" id="rpt001Left" layoutH="55">
            <c:forEach items="${reportType}" var="typeItem">
                <c:set var="reportItems" value="${reportListByType[typeItem.key]}"/>
                <c:if test="${fn:length(reportItems) > 0}">
                    <div class="accordionHeader">
                        <h2 class="collapsable">${typeItem.value.Node.rhtName}</h2>
                    </div>
                    <div class="accordionContent">
                        <ul class="accordion_menu">
                            <c:forEach items="${reportItems}" var="reportItem">
                                <li onclick="rpt001.showReport('${reportItem.Node.navUrl}')">${reportItem.Node.rhtName}</li>
                            </c:forEach>
                        </ul>
                    </div>
                </c:if>
            </c:forEach>
        </div>
    </div>
    <div class="split_line" layoutH="15">
        <span class="split_but" title="展开/隐藏导航"></span>
    </div>

    <div id="rpt001RightDiv" style="margin-left: 250px;position: relative;">
        <div class="panelBar">
            <ul class="toolBar">
                <li onclick="rpt001.showOrHideCond();" class="excel_hidden">
                    <a class="find" href="javascript:void(0)"><span>条件定义</span></a>
                </li>
                <li onclick="rpt001.generateReport('pdf');" class="excel_hidden">
                    <a class="reflesh" href="javascript:void(0)"><span>刷新报表</span></a>
                </li>
                <li>
                    <a class="export" href="javascript:void(0)">
                        <span onclick="rpt001.doExport('xlsx');">导出EXCEL</span>
                    </a>
                </li>
                <li class="excel_hidden">
                    <a class="export" href="javascript:void(0)">
                        <span onclick="rpt001.doExport('docx');">导出WORD</span>
                    </a>
                </li>
                <li class="excel_hidden">
                    <a class="export" href="javascript:void(0)">
                        <span onclick="rpt001.doExport('pdf');">导出PDF</span>
                    </a>
                </li>
            </ul>
        </div>
        <%--检索条件处理控制--%>
        <iframe name="rpt001IFrame" allowTransparency="false" id="rpt001IFrame" class="hidden"
                style="position: absolute;top: 35px;border:0;width:100%;background-color:#f5f9fc;"
                src=""
                layoutH="55">
        </iframe>
        <%--内容--%>
        <iframe id="rpt001_ReportData" style="border: 1px solid #B3E4EB; width: 100%;"
                layoutH="55">
        </iframe>
    </div>
</div>
<oframe:script src="${pageContext.request.contextPath}/oframe/report/rpt001/js/rpt001.js" type="text/javascript"/>
