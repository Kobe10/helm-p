<%--院落信息基本信息面板--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="panel">
    <h1>入户调查表<span class="panel_menu js_reload">刷新</span></h1>
    <div class="panelContent">
        ${article}
    </div>
</div>
<script type="text/javascript">
    <%--调用 js 区--%>
    ${operDefCode}
</script>