<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div id="oh003_div">
    <table class="table" layoutH="130" width="100%">
        <thead>
        <tr style="text-align: center">
            <th width="50px;">
                <input type="checkbox" class="checkboxCtrl" group="ids"/>
            </th>
            <th>用房院落</th>
            <th>用房居民</th>
            <c:forEach items="${resultTitle}" var="title">
                <c:set var="attrNameEn" value="${title.Result.attrNameEn}"/>
                <c:choose>
                    <c:when test="${title.Result.sortable != null}">
                        <th class="${title.Result.sortable}"
                            onclick="Page.sort('${divId}', this);"
                            fieldName="${title.Result.attrNameEn}">${title.Result.attrNameCh}
                        </th>
                    </c:when>
                    <c:otherwise>
                        <c:if test="${'oldBuldId_buildFullAddr'!= attrNameEn && 'oldHsId_hsOwnerPersons'!= attrNameEn }">
                            <th fieldName="${title.Result.attrNameEn}">${title.Result.attrNameCh}
                            </th>
                        </c:if>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${returnList}" var="item" varStatus="varStatus">
            <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}"/>
            <tr class="${tr_class}" style="cursor: pointer">
                <td>
                    <c:set var="jsTime" value="${item.Row.newHsId}"/>
                    <c:set var="disableStr" value=""/>
                    <c:if test="${jsTime == '0'}">
                        <c:set var="disableStr" value="disabled"/>
                    </c:if>
                    <input type="checkbox" group="ids" ${disableStr} name="newHsId" value="${item.Row.newHsId}"/>
                </td>
                <%@include file="/oframe/common/query/list_hdt.jsp" %>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <jsp:include page="/oframe/common/page/page_fotter.jsp"/>
</div>