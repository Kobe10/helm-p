<%--自定义任务--查看任务--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>

<table width="100%" class="border" style="text-align: center">
    <tr>
        <td width="10%"><b>序号</b></td>
        <td><b>产权人</b></td>
        <td><b>房屋性质</b></td>
        <td><b>建筑面积</b></td>
        <td><b>操作</b></td>
    </tr>
    <c:forEach items="${hsInfoList}" var="item" varStatus="varStatus">
        <tr>
            <td>${varStatus.index+1}</td>
            <td>${item.Row.hsOwnerPersons}</td>
            <td><oframe:name prjCd="${param.prjCd}" itemCd="HS_OWNER_TYPE" value="${item.Row.hsOwnerType}"/></td>
            <td>${item.Row.hsBuildSize}</td>

                <%--hsId方便下面使用，下面很多地方用到--%>
            <c:set var="hsId" value="${item.Row.hsId}${''}"/>
            <td><span class="btn" style="color: #0000ff;" onclick="ph003Op.chooseHouse('${hsId}', 'gfsh','view');">购房人资格审核结果</span>
            </td>
        </tr>
    </c:forEach>
    <tr>
        <td>材料导出：</td>
        <td colspan="5" style="text-align: left">
            <span class="btn" style="color: #0000ff;cursor:pointer;" onclick="ph002Op.exportDxFsP('${buildId}');">导出定向房审批表</span>
            <span class="btn" style="color: #0000ff;cursor:pointer;" onclick="ph002Op.exportGfRyMx('${buildId}');">导出购房人明细表</span>
        </td>
    </tr>
    <tr>
        <th style="width: 10%"><label>备注说明：</label></th>
        <td colspan="5" style="text-align: left; line-height: 50px">${hiTaskInfo.HiTaskInfo.TskBidVars.comment}</td>
    </tr>
</table>