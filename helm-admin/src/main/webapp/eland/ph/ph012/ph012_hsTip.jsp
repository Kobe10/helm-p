<%--院落信息基本信息面板--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<div class="tree-bar">
    <span class="marr10" onclick="$(this).closeTip();"
          style="display: block; float: right; cursor: pointer;">关闭</span>
</div>
<%--院落内房屋信息列表--%>
<table class="list" width="100%">
    <thead>
    <tr>
        <th>产承人</th>
        <th>房屋地址</th>
        <th>管理组织</th>
    </tr>
    </thead>
    <tbody>
    <c:choose>
        <c:when test="${fn:length(result) > 0}">
            <c:forEach items="${result}" var="item">
                <tr style="cursor: pointer;"
                    onclick="ph012.chooseHsPs('${item.hsCtId}','${item.hsFullAddr}','${item.hsOwnerPersons}', '${flag}', this)">
                    <td>${item.hsOwnerPersons}</td>
                    <td>${item.hsFullAddr}</td>
                    <td><oframe:org prjCd="${param.prjCd}" orgId="${item.ttOrgId}"/></td>
                </tr>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <tr>
                <td colspan="3">查询数据不存在，请确认地址后重新查询！</td>
            </tr>
        </c:otherwise>
    </c:choose>
    </tbody>
</table>