<%--发起任务通用处理模板--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div id="sys020List">
    <table class="table" border="0" width="100%" layoutH="132">
        <thead>
        <tr>
            <th width="5%">
                <input type="checkbox" class="checkboxCtrl" group="ids"/>
            </th>
            <c:forEach items="${resultTitle}" var="title">
                <c:set var="widthVar" value=""/>
                <c:set var="tempStr" value="${title.Result.attrNameEn}${''}"/>
                <c:if test="${tempStr == 'sContent'}">
                    <c:set var="widthVar" value="width=\"30%\""/>
                </c:if>
                <c:choose>
                    <c:when test="${title.Result.sortable != null}">
                        <th class="${title.Result.sortable}" ${widthVar}
                            onclick="Page.sort('${divId}', this);"
                            fieldName="${title.Result.attrNameEn}">${title.Result.attrNameCh}
                        </th>
                    </c:when>
                    <c:otherwise>
                        <th fieldName="${title.Result.attrNameEn}" ${widthVar}>${title.Result.attrNameCh}</th>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
            <th width="5%">操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${returnList}" var="item" varStatus="varStatus">
            <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}"/>
            <tr class="${tr_class}" jobTaskId="" style="cursor: pointer;">
                <td>
                    <input type="checkbox" group="ids" name="sId" value="${item.Row.sId}"/>
                </td>
                <%@include file="/oframe/common/query/list_hdt.jsp" %>
                <td>
                    <c:if test="${item.Row.sStatus != '2'}">
                        <a title="立即发送" onclick="sys020.sendMessage('${item.Row.sId}',this);" class="btnEdit">[立即发送]</a>
                    </c:if>
                    <c:if test="${item.Row.sStatus == '2'}">
                        <a title="重新发送" onclick="sys020.sendMessage('${item.Row.sId}',this);" class="btnEdit">[重新发送]</a>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <jsp:include page="/oframe/common/page/page_fotter.jsp"/>
</div>
