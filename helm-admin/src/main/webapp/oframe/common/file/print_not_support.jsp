<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<oframe:script src="${pageContext.request.contextPath}/oframe/common/file/js/file.js" type="text/javascript"/>
<html>
<head>
    <title>不支持打印</title>
</head>
<body style="text-align: center">
<h1>不支持打印 </h1>
<a href="${pageContext.request.contextPath}/oframe/common/file/file-download.gv?docId=${docId}">点击下载</a>
</body>
</html>