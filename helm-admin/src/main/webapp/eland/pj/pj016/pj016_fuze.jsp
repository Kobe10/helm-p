<%--
  Created by IntelliJ IDEA.
  User: shfb_wang
  Date: 2015/5/8
  Time: 15:20
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<div class="js_query_condition" style="position: relative;">
    <%--<div class="panelBar">--%>
        <%--<ul class="toolBar">--%>
            <%--<li onclick="Page.queryCondition(this, {width: '100%', formObj: $(this).closest('div.js_query_condition').next('form')});">--%>
                <%--<a class="find" href="javascript:void(0)"><span>检索</span></a>--%>
            <%--</li>--%>
            <%--<li onclick="Page.query($(this).closest('div.js_query_condition').next('form'), 'pj016.changeWidth();');">--%>
                <%--<a class="reflesh" href="javascript:void(0)"><span>刷新</span>--%>
                <%--</a>--%>
            <%--</li>--%>

            <%--<li>--%>
                <%--<a class="export" onclick="Page.exportExcel('pj016fuze_list', false)">--%>
                    <%--<span>导出EXCEL</span>--%>
                <%--</a>--%>
            <%--</li>--%>
        <%--</ul>--%>
    <%--</div>--%>
</div>
<form id="pj016fuzeForm">
    <input type="hidden" name="entityName" value="BuildInfo"/>
    <input type="hidden" name="conditionName" value="BuildInfo.ttCompanyId">
    <input type="hidden" name="condition" value="=">
    <input type="hidden" name="conditionValue" value="${extCmpId}">
    <input type="hidden" class="js_conditionValue" value="${extCmpId},">
    <input type="hidden" name="divId" value="pj016fuze_list">
    <input type="hidden" name="resultField" value="BuildInfo.buildFullAddr,BuildInfo.totalZhFee,BuildInfo.payStatus,BuildInfo.hsCount">
    <input type="hidden" name="forward" id="forward" value="/eland/pj/pj016/pj016_fuze_list"/>
</form>
<div class="js_page" layoutH="185">

</div>
<script type="text/javascript">
    $(document).ready(function () {
        Page.query($("#pj016fuzeForm", navTab.getCurrentPanel()), '');
    });
</script>


