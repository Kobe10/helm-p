<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div id="${param.divId}">
    <table class="table" layoutH="165" width="100%">
        <thead>
        <tr>
            <%@include file="/oframe/common/query/list_htr.jsp" %>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${returnList}" var="item" varStatus="varStatus">
            <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}"/>
            <tr class="${tr_class}">
                <%@include file="/oframe/common/query/list_hdt.jsp" %>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <jsp:include page="/oframe/common/page/page_fotter.jsp"/>
</div>
