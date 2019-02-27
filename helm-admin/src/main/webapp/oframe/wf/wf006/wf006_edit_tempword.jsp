<%--待签约处理--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="docPath" value="${docPath}"/>
<c:set var="formId" value="${formId}"/>
<c:set var="oldTemplateCd" value="${oldTemplateCd}"/>
<iframe id="wf006TempIFrame" style="border: 0;margin: 0; width: 100%;" layoutH="0"
        src="${pageContext.request.contextPath}/oframe/wf/wf006/wf006-viewWordDoc.gv?docPath=${docPath}&formId=${formId}&oldTemplateCd=${oldTemplateCd}">
</iframe>
