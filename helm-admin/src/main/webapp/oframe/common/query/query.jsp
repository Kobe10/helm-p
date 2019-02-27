<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div class="mar5">
    <div class="js_query_condition" style="position: relative;">
        <div class="panelBar">
            <ul class="toolBar">
                <li class=""
                    onclick="Page.queryCondition(this,
                {width: '100%', formObj: $(this).closest('div.js_query_condition').next('form')});">
                    <a class="find" href="javascript:void(0)"><span>检索</span></a>
                </li>
                <li onclick="Page.query($(this).closest('div.js_query_condition').next('form'));">
                    <a class="reflesh"
                       href="javascript:void(0)">
                        <span>刷新</span>
                    </a>
                </li>
                <li onclick="Page.exportExcel('query_list', false);">
                    <a class="export"
                       href="javascript:void(0)">
                        <span>导出</span>
                    </a>
                </li>
            </ul>
        </div>
    </div>
    <form id="queryForm">
        <input type="hidden" name="entityName" value="${conditions.Condition.entityName}"/>
        <input type="hidden" name="conditionName" value="${conditions.Condition.conditionName}">
        <input type="hidden" name="condition" value="${conditions.Condition.condition}">
        <input type="hidden" name="conditionValue" value="${conditions.Condition.conditionValue}">
        <input type="hidden" name="sortColumn" value="${conditions.Condition.sortColumn}">
        <input type="hidden" name="sortOrder" value="${conditions.Condition.sortOrder}">
        <input type="hidden" class="js_conditionValue" value="">
        <input type="hidden" class="js_canDefResult" value="${conditions.Condition.canDefResult}">
        <input type="hidden" name="forceResultField" value="${conditions.Condition.forceResultField}">
        <input type="hidden" name="divId" value="query_list">
        <input type="hidden" name="resultField" value="${conditions.Condition.resultField}">
        <%--必须包含的字段--%>
        <input type="hidden" name="forward" id="forward" value="/oframe/common/query/list"/>
        <input type="hidden" name="cmptName" value="${conditions.Condition.cmptName}"/>
    </form>
</div>
<script type="text/javascript">
    $(document).ready(function () {
        Page.query($("#queryForm", navTab.getCurrentPanel()));
    });
</script>

