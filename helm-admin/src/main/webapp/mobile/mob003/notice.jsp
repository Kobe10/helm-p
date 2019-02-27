<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html>
<head>
    <script type="text/javascript" charset="utf-8"
            src="${pageContext.request.contextPath}/oframe/plugin/ueditor/ueditor.parse.js"></script>
    <style>
        .noticeContainer img {
            height: auto !important;
            max-width: 100% !important;
        }
    </style>
</head>
<body class="noticeContainer">
${prjNotice.PrjNotice.noticeContext}
</body>
<script type="text/javascript">
  uParse("body", {rootPath: '${pageContext.request.contextPath}/oframe/plugin/ueditor/'})
</script>
</html>