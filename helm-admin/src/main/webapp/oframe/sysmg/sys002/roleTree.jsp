<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="tree-bar">
    <span class="mart5 marr10" onclick="$(this).closeTip();"
          style="display: block; float: right; cursor: pointer;">关闭</span>
    <span class="mart5 marr10" onclick="roleTree.onConfim(this);"
          style="display: block; float: right; cursor: pointer;">确定</span>
    <input type="hidden" name="fromOp" value="${param.fromOp}"/>
</div>
<div style="width:100%; height:100%; overflow:auto;">
    <ul id="commonTreeContent" class="ztree"></ul>
</div>
<oframe:script src="${pageContext.request.contextPath}/oframe/sysmg/sys002/js/roleTree.js" type="text/javascript"/>