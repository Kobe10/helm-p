<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>居民住房改善信息系统</title>
    <link href="${pageContext.request.contextPath}/oframe/themes/common/core.css" rel="stylesheet" type="text/css"
          media="screen"/>
    <link href="${pageContext.request.contextPath}/oframe/themes/blue/style.css" rel="stylesheet" type="text/css"
          media="screen"/>
    <script src="${pageContext.request.contextPath}/oframe/plugin/jquery/eland.jquery.min.js"
            type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/oframe/plugin/pdfobject/pdfobject.js"
            type="text/javascript"></script>
    <!--自定义引入js-->
    <script id="systemScript" contextPath="${pageContext.request.contextPath}/"
            src="${pageContext.request.contextPath}/oframe/plugin/system/system.min.js"
            type="text/javascript"></script>
    <script type="text/javascript">
        $(document).ready(function () {
            var url = "${pageContext.request.contextPath}/oframe/common/file/file-downPdf.gv?docId=" + "${param.docId}"
                    + "&prjCd=" + "${param.prjCd}"
            var myPDF = new PDFObject({url: url}).embed();
        });
    </script>
</head>
<body>
<p class="mart20">
    浏览器没有安装PDF插件，请先安装插件.
    <a class="link"
       href="${pageContext.request.contextPath}/oframe/common/file/file-downPdf.gv?docId=${param.docId}&prjCd=${param.prjCd}">点击下载PDF文件</a>
</p>
</body>
</html>