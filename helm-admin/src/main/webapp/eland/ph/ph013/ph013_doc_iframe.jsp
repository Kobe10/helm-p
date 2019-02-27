<%--待签约处理--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="docFileUrl" value="${docFileUrl}"/>
<iframe id="ph013DocIFrame"
        style="border: 0;margin: 0; width: 100%;"
        layoutH="${subLayoutH}"
        src="${pageContext.request.contextPath}/eland/ph/ph013/ph013-viewDoc.gv?docFileUrl=${docFileUrl}&docObjId=${docObjId}">
</iframe>
