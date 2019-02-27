<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="panelBar">
    <ul class="toolBar">
        <li><a class="save" onclick="prjFunc.savePrjFun(1)"><span>保存</span></a></li>
    </ul>
</div>
<div>
    <ul id="prjFuncSltTree" layoutH="100" class="ztree"></ul>
</div>
<oframe:script src="${pageContext.request.contextPath}/eland/pj/pj002/js/prjFunc.js" type="text/javascript"/>
