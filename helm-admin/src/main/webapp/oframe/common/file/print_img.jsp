<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<oframe:script src="${pageContext.request.contextPath}/oframe/common/file/js/file.js" type="text/javascript"/>
<html>
<head>
    <title></title>
    <style type="text/css">
        body {
            text-align: center;
        }

        .printA4Contain {
            width: 794px;
            height: 1123px;
            border: 1px solid #000000;
            margin-left: auto;
            margin-right: auto;
        }

        @media print {
            body {
                margin: 0;
                padding: 0;
            }

            .printA4Contain {
                border: 0px;
            }
        }

        .printA4Contain img {
            width: 100%;
        }
    </style>
    <script type="text/javascript">
        /**
         * 检测浏览器
         * @returns {boolean}
         */
        function isIE() {
            if (!!window.ActiveXObject || "ActiveXObject" in window) {
                return true;
            } else {
                return false;
            }
        }
        function printImg() {
            window.print();
            if (isIE()) {
                window.close();
            }
        }

    </script>
</head>
<body onload="printImg();">
<div class="printA4Contain">
    <img src="${pageContext.request.contextPath}/oframe/common/file/file-downview.gv?docId=${docId}">
</div>
</body>
</html>