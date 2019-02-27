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
        <td><b>签约状态</b></td>
        <td><b>操作</b></td>
    </tr>
    <c:forEach items="${hsInfoList}" var="item" varStatus="varStatus">
        <tr style="line-height: 30px;">
            <td>${varStatus.index+1}</td>
            <td>${item.Row.hsOwnerPersons}</td>
            <td><oframe:name prjCd="${param.prjCd}" itemCd="HS_OWNER_TYPE" value="${item.Row.hsOwnerType}"/></td>
            <td>${item.Row.hsBuildSize}</td>

                <%--hsId方便下面使用，下面很多地方用到--%>
            <c:set var="hsId" value="${item.Row.hsId}${''}"/>
            <td class="js_result"><oframe:name prjCd="${param.prjCd}" itemCd="CONTRACT_STATUS" value="${hsCtMap[hsId].Row.ctStatus}"/></td>
            <c:choose>
                <c:when test="${item.Row.hsOwnerType == '0'}">
                    <%--私房--%>
                    <td>
                        <span class="link marr5 js_doc_info" docTypeName="居民签字协议" editAble="false" relType="100"
                              onclick="ph007.showDoc(this,'${hsId}')">
                            <label style="cursor:pointer;">查看居民签字协议</label>
                            <input type="hidden" name="docIds" value='${hiTaskInfo.HiTaskInfo.TskBidVars.docIds}'>
                        </span>
                    </td>
                </c:when>
                <c:otherwise>
                    <%--公房--%>
                    <td>
                    </td>
                </c:otherwise>
            </c:choose>

        </tr>
    </c:forEach>
    <tr>
        <th style="width: 10%"><label>备注说明：</label></th>
        <td colspan="5" style="text-align: left; line-height: 50px">${hiTaskInfo.HiTaskInfo.TskBidVars.comment}</td>
    </tr>
</table>