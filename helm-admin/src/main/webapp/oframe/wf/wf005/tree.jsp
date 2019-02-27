<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="tree-bar">
    <input type="hidden" name="fromOp" value="${fromOp}">
    <span class="mart5" onclick="$(this).closeTip();"
          style="display: block; float: right; margin:0px 10px 5px 0px; cursor: pointer;">关闭</span>
    <%--<span onclick="fromTypeTree.onConfim(this);"--%>
          <%--style="display: block; float: right; margin:0px 10px 5px 0px; cursor: pointer;">确定</span>--%>
</div>
<div style="width:100%; height:100%; overflow:auto;">
    <ul id="commonFormTree" class="ztree"></ul>
</div>
<oframe:script src="${pageContext.request.contextPath}/oframe/wf/wf005/js/tree.js" type="text/javascript"/>