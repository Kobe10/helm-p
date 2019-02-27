<%--参数信息--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%--左侧 参数列表--%>
<div style="width: 280px;float: left;position: relative;" layoutH="52" class="panel">
    <h1>项目参数</h1>
    <ul id="pj002ParamList" layoutH="92" class="list">
        <c:forEach items="${prjCfgList}" var="item" varStatus="varStatus">
            <li class="cursorpt" itemCd="${item.SysCfg.itemCd}" onclick="prjParam.cfgDetail(this)">${item.SysCfg.itemName}</li>
        </c:forEach>
    </ul>
</div>
<%--右侧自定义画板--%>
<div id="pj002ParamInfoDiv" style="margin-left: 290px;position: relative;" layoutH="52">

</div>
<oframe:script src="${pageContext.request.contextPath}/eland/pj/pj002/js/prjParam.js" type="text/javascript"/>
