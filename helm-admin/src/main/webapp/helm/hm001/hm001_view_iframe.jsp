<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="panelBar">
    <ul class="toolBar">
        <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
    </ul>
</div>
<div class="mar5">
    <iframe layoutH="60" width="100%"
            style="border: 1px solid #efefef; background-color: #fefefe"
            src="${pageContext.request.contextPath}/helm/hm001/hm001-view.gv?noticeId=${noticeId}&prjCd=${prjCd}"></iframe>
</div>
