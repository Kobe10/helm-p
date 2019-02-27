<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div>
    <div style="width: 240px;float: left;position: relative;" layoutH="53" class="panel">
        <h1>
            <span class="panel_title">控制环节</span>
        </h1>
        <ul layoutH="220" id="pj002CtrlList" class="list">
            <li scType="1" onclick="prjCtrl.ctrlInfo(this)">签约环节</li>
            <li scType="2" onclick="prjCtrl.ctrlInfo(this)">选房环节</li>
        </ul>
    </div>
    <%--右侧自定义画板--%>
    <div id="pj002CtrlInfoDiv" style="margin-left: 250px;position: relative;">
    </div>
</div>
<oframe:script src="${pageContext.request.contextPath}/eland/pj/pj002/js/prjCtrl.js" type="text/javascript"/>
