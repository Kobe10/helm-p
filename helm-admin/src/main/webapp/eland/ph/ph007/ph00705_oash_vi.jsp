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
            <td><span class="btn" style="color: #0000ff;"
                      onclick="ph003Op.openOash('${hsId}');">查看OA审核资料</span>
            </td>
        </tr>
    </c:forEach>
    <tr>
        <th>OA审核资料：</th>
        <td colspan="4" style="text-align: left">
            <span class="btn js_doc_info" docTypeName="OA审核资料" editAble="false" relType="200"
                  onclick="ph007.showDoc(this,'${buildId}')">
                    <label style="color: #0000ff;cursor:pointer;">查看</label>
                      <input type="hidden" name="docIds" value='${hiTaskInfo.HiTaskInfo.TskBidVars.docIds}'>
                </span>
        </td>
    </tr>
    <tr>
        <th style="width: 10%"><label>备注说明：</label></th>
        <td colspan="5" style="text-align: left; line-height: 50px">${hiTaskInfo.HiTaskInfo.TskBidVars.comment}</td>
    </tr>
</table>
<oframe:script type="text/javascript" src="${pageContext.request.contextPath}/eland/ph/ph003/js/ph003_op.js"/>