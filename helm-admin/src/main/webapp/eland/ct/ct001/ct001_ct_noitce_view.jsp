<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html>
    <head>
        <script type="text/javascript" charset="utf-8" src="${pageContext.request.contextPath}/oframe/plugin/ueditor/ueditor.parse.js"></script>
    </head>
    <style>
        #ct001CtNotice {text-align: center;}
        #ct001CtNotice span {display: inline-block;padding: 4px 10px;margin: 0 5px;font-size: 14px;font-weight: 500;line-height: 1.42857143;text-align: center;white-space: nowrap;vertical-align: middle;cursor: pointer;color: #ffffff;border: 1px solid transparent;border-radius: 4px;}
        #ct001CtNotice span.cfmct {background-color: #3c90c8;}
        #ct001CtNotice span.cancelct {background-color: #7dd457;}
    </style>
    <body>
        ${prjNotice.PrjNotice.noticeContext}
        <div id="ct001CtNotice">
            <span class="cfmct" onclick="window.parent.ct001.readNoticeCfmCt()">确认签约</span>
            <span class="cancelct" onclick="window.parent.ct001.readNoticeCancel()">取消</span>
        </div>
    </body>
    <script type="text/javascript">
        uParse("body", {rootPath: '${pageContext.request.contextPath}/oframe/plugin/ueditor/'})
    </script>
</html>

