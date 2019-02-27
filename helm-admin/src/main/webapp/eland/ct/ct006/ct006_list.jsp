<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div>
    <table class="table" layoutH="165" adjust="true" width="100%">
        <thead>
        <tr>
            <th>被安置人</th>
            <th>安置房地址</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${returnList}" var="item" varStatus="varStatus">
            <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}"/>
            <tr class="${tr_class}"
                onclick="ct006.ctInfo('${item.Row.NewHsInfo.oldHsId.hsId}','${item.Row.NewHsInfo.newHsId}')"
                style="cursor: pointer;">
                <%@include file="/oframe/common/query/list_hdt.jsp" %>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <jsp:include page="/oframe/common/page/pager_fotter_simple.jsp"/>
</div>