<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="js_pb_tree">
    <div class="tree-bar">
        <input type="hidden" name="objName" value="${objName}">
        <input type="hidden" name="objValue" value="${objValue}">
        <input type="hidden" name="fromOp" value="${fromOp}">
        <input type="hidden" name="prjCd" value="${prjCd}">
        <span class="mart5" onclick="$(this).closeTip();" style="display: block; float: right; margin:0px 10px 5px 0px; cursor: pointer;">关闭</span>
        <span onclick="pbTree.onConfim(this);" style="display: block; float: right; margin:0px 10px 5px 0px; cursor: pointer;">确定</span>

    </div>
    <div style="width:100%; height:100%; overflow:auto;">
        <ul id="pbTreeContainer" class="ztree"></ul>
    </div>
</div>
<oframe:script src="${pageContext.request.contextPath}/eland/ph/ph016/js/pbTree.js" type="text/javascript"/>