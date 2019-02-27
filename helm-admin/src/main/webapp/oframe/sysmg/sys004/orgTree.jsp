<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="tree-bar">
    <input type="hidden" name="orgPrjCd" value="${orgPrjCd}">
    <input type="hidden" name="objName" value="${objName}">
    <input type="hidden" name="objValue" value="${objValue}">
    <input type="hidden" name="orgTreeFlag" value="${orgTreeFlag}">
    <input type="hidden" name="fromOp" value="${fromOp}">

    <c:choose>
        <c:when test="${objName != '' && objName != null && fromOp != 'wf003'}">
            <span class="" onclick="$(this).closeTip();"
                  style="display: block; float: right; margin:0px 10px 0px 0px; cursor: pointer;">关闭</span>
              <span onclick="orgTree.onConfim(this);"
                    style="display: block; float: right; margin:0px 10px 0px 0px; cursor: pointer;">确定</span>
        </c:when>
        <c:otherwise>
            <span class="marr10" onclick="$(this).closeTip();"
                  style="display: block; float: right; cursor: pointer;">关闭</span>
            <span class="marr10" onclick="orgTree.onConfim(this);"
                  style="display: block; float: right; cursor: pointer;">确定</span>
        </c:otherwise>
    </c:choose>
</div>
<div style="width:100%; height:100%; overflow:auto;">
    <ul id="orgTreeContainer" class="ztree"></ul>
</div>
<oframe:script src="${pageContext.request.contextPath}/oframe/sysmg/sys004/js/orgTree.js"
               type="text/javascript"/>