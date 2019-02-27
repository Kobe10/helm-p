<%--
  Created by IntelliJ IDEA.
  User: shfb_linql
  Date: 2016/3/17 0017 16:09
  Copyright(c) 北京四海富博计算机服务有限公司
--%>
<%--信息审批结果提交处理界面--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div>
    <form id="_formApplyfrm" method="get">
        <input type="hidden" name="tableLayOut" value="80"/>
        <input type="hidden" name="entityName" value="CheckRecord"/>
        <input type="hidden" name="sortColumn" value="checkTime">
        <input type="hidden" name="sortOrder" value="desc">
        <input type="hidden" name="conditionName" value="checkRelType,checkRelId,checkSubRelId">
        <input type="hidden" name="condition" value="=,=,=">
        <input type="hidden" name="conditionValue" value="${checkRelType},${checkRelId},${subRelId}">
        <input type="hidden" class="js_conditionValue" value="">
        <input type="hidden" name="divId" value="form_check_apply_list">
        <input type="hidden" name="resultField"
               value="checkStaff,checkTime,checkResult,checkOpName,checkNote,modifyTransactionId">
        <input type="hidden" name="forward" id="forward" value="/oframe/common/form/form_check_list"/>
        <input type="hidden" name="cmptName" id="cmptName" value="QUERY_ENTITY_PAGE_DATA"/>
    </form>
    <div id="form_check_apply_list" class="js_page" layoutH="0"></div>
</div>
<script type="text/javascript">
    $(document).ready(function () {
        $("textarea[name=checkNote]", $.pdialog.getCurrent()).focus();
        var form = $("#_formApplyfrm", $.pdialog.getCurrent());
        Page.query(form, "");
    });
</script>
