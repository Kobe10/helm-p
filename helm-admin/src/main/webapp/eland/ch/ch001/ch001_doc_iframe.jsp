<%--待签约处理--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="docFileUrl" value="${docFileUrl}"/>
<iframe id="ch001DocIFrame" style="border: 0;margin: 0; width: 100%;"
        src="${pageContext.request.contextPath}/eland/ct/ct001/ct001-viewDoc.gv?docFileUrl=${docFileUrl}">
</iframe>
