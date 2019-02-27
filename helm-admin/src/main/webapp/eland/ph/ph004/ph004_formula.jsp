<%-- 补偿计算 腾退项目 展示 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<style type="text/css">
    #ph004TreeBody th, #ph004TreeBody td {
        border: 1px solid #8DB9DB;
        padding: 5px;
        border-collapse: collapse;
        font-size: 16px;
    }
</style>
<div class="mar5" id="ph004_div">
    <div class="panel" style="margin-top: 0px;" id="ph004Condition">
        <h1>计算参数<a class="collapsable" onclick='ph004.showHideCondition(this);'></a></h1>
        <div class="panelContent">
            <form id="ph004Frm" class="js_dynamic_form" method="post">
                ${article}
            </form>
        </div>
    </div>
    <div class="subBar center mart5 marb5">
        <span style="margin-right: 30px; color: #ff0000; font-size: 18px; font-weight: bold">${errMsg}</span>
        <button type="button" class="btn btn-opt marr10" onclick="ph004.calculate()">计算</button>
        <span class="btn btn-pri" onclick="ph004.printInfo('ph004_div')">打印</span>
    </div>
    <div class="panel">
        <h1>计算结果</h1>
        <div id="calResult" layoutH="340"></div>
    </div>
</div>
<script>
    ${operDefCode}
</script>
<oframe:script src="${pageContext.request.contextPath}/eland/ph/ph004/js/ph004.js" type="text/javascript"/>
