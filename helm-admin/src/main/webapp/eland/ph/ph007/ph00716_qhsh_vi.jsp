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
        <td><b>处理结果</b></td>
        <td><b>操作</b></td>
    </tr>
    <c:forEach items="${hsInfoList}" var="item" varStatus="varStatus">
        <tr>
            <td>${varStatus.index+1}</td>
            <td>${item.Row.hsOwnerPersons}</td>
            <td><oframe:name prjCd="${param.prjCd}" itemCd="HS_OWNER_TYPE" value="${item.Row.hsOwnerType}"/></td>
            <td>${item.Row.hsBuildSize}</td>
            <c:set var="hsId" value="${item.Row.hsId}${''}"/>

            <td class="js_result"><oframe:name prjCd="${param.prjCd}" itemCd="FINISH_RESULT" value="${item.Row.procResult9}"/></td>
            <td><span class="btn" style="color: #0000ff;" onclick="ph007.viewQhsh('${hsId}');">处理结果</span></td>
        </tr>
    </c:forEach>
    <tr>
        <th style="width: 10%"><label>备注说明：</label></th>
        <td colspan="5" style="text-align: left; line-height: 50px">${hiTaskInfo.HiTaskInfo.TskBidVars.comment}</td>
    </tr>
</table>