<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<c:forEach items="${resultTitle}" var="title">
    <c:choose>
        <c:when test="${title.Result.sortable != null}">
            <th class="${title.Result.sortable}"
                onclick="Page.sort('${divId}', this);"
                fieldName="${title.Result.attrNameEn}">${title.Result.attrNameCh}
            </th>
        </c:when>
        <c:otherwise>
            <th fieldName="${title.Result.attrNameEn}">
                ${title.Result.attrNameCh}
            </th>
        </c:otherwise>
    </c:choose>
</c:forEach>

