<%--交房处理界面--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<oframe:script src="${pageContext.request.contextPath}/oframe/common/form/js/form.js" type="text/javascript"/>
<div style="position: relative;" class="js_page_data" fromEdit="${param.fromEdit}">
    <input type="hidden" name="clickId" value="${clickId}">
    <input type="hidden" name="clickType" value="${clickType}">
    ${article}
</div>
<script type="text/javascript">
    <%--调用 js 区--%>
    ${operDefCode}
</script>