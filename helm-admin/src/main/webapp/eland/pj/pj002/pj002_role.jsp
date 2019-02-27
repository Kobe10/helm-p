<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div>
    <div style="width: 280px;float: left;position: relative;" layoutH="53" class="panel">
        <h1>
            <span class="panel_title">角色列表</span>
            <span onclick="prjRole.roleInfo(null);" class="panel_menu js_reload">新建角色</span>
        </h1>
        <ul layoutH="93" id="pj002RoleList" class="list">
            <li roleCd="" class="hidden" onclick="prjRole.roleInfo(this)"></li>
            <c:forEach items="${roleList}" var="item">
                <li roleCd="${item.Node.roleCd}" onclick="prjRole.roleInfo(this)">${item.Node.roleName}</li>
            </c:forEach>
        </ul>
    </div>
    <%--右侧自定义画板--%>
    <div id="pj002RoleInfoDiv" style="margin-left: 290px;position: relative;">

    </div>
</div>
<oframe:script src="${pageContext.request.contextPath}/eland/pj/pj002/js/prjRole.js" type="text/javascript"/>
