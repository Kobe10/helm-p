<%--完成退租--查看任务--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>

<table width="100%" class="border" style="text-align: center">
    <tr>
        <td width="10%"><b>序号</b></td>
        <td><b>产权人</b></td>
        <td><b>房屋性质</b></td>
        <td><b>建筑面积</b></td>
        <td><b>公房年租金</b></td>
        <td><b>公房过户费用</b></td>
        <td><b>操作</b></td>
    </tr>
    <c:forEach items="${hsInfoList}" var="item" varStatus="varStatus">
        <tr>
            <td>${varStatus.index+1}</td>
            <td>${item.Row.hsOwnerPersons}</td>
            <td><oframe:name prjCd="${param.prjCd}" itemCd="HS_OWNER_TYPE" value="${item.Row.hsOwnerType}"/></td>
            <td>${item.Row.hsBuildSize}</td>

            <c:set var="hsId" value="${item.Row.hsId}${''}"/>
            <c:choose>
                <c:when test="${item.Row.hsOwnerType == '0'}">
                    <%--私房--%>
                    <td>无</td>
                    <td>无</td>
                    <td><span class="btn">无</span></td>
                </c:when>
                <c:otherwise>
                    <%--公房--%>
                    <td class="js_hsPubZj">${item.Row.hsPubZj}</td>
                    <td class="js_hsPubGhf">${item.Row.hsPubGhf}</td>
                    <td><span class="btn" style="color: #0000ff;" onclick="ph007.viewWctz('${hsId}');">退租手续处理结果</span></td>
                </c:otherwise>
            </c:choose>
        </tr>
    </c:forEach>
    <tr>
        <th>租金及过户费用明细表：</th>
        <td colspan="6" style="text-align: left"><span style="color: #0000ff; cursor: pointer;" onclick="ph002Op.exportZjGhMx('${buildId}')">导出租金及过户费明细表</span></td>
    </tr>
    <tr>
        <th style="width: 10%"><label>备注说明：</label></th>
        <td colspan="6" style="text-align: left; line-height: 50px">${hiTaskInfo.HiTaskInfo.TskBidVars.comment}</td>
    </tr>
</table>