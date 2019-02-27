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
</div>
<form id="pb003fuzeForm">
    <input type="hidden" name="entityName" value="BuildInfo"/>
    <input type="hidden" name="conditionName" value="BuildInfo.ttCompanyId">
    <input type="hidden" name="condition" value="=">
    <input type="hidden" name="conditionValue" value="${extCmpId}">
    <input type="hidden" class="js_conditionValue" value="${extCmpId},">
    <input type="hidden" name="divId" value="pb003fuze_list">
    <input type="hidden" name="resultField" value="BuildInfo.buildFullAddr,BuildInfo.totalZhFee,BuildInfo.payStatus,BuildInfo.hsCount">
    <input type="hidden" name="forward" id="forward" value="/eland/pb/pb003/pb003_fuze_list"/>
</form>
<div class="js_page" layoutH="182">

</div>
<script type="text/javascript">
    $(document).ready(function () {
        Page.query($("#pb003fuzeForm", navTab.getCurrentPanel()), '');
    });
</script>


