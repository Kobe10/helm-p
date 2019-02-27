<%--
  Created by IntelliJ IDEA.
  User: Dennis
  Date: 15/9/21
  Time: 22:33
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div id="ct002ResultQueryList">
    <table class="table" layoutH="130" width="100%">
        <thead>
        <tr>
            <th width="3%"><input type="checkbox" class="checkboxCtrl" group="ids"/></th>
            <%@include file="/oframe/common/query/list_htr.jsp" %>
            <th style="width: 120px;">操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${returnList}" var="item" varStatus="varStatus">
            <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}"/>
            <tr class="${tr_class}">
                <td><input type="checkbox" group="ids" name="resultId" value="${item.Row.resultId}"/></td>
                <%@include file="/oframe/common/query/list_hdt.jsp" %>
                <c:if test="${item.Row.preStatus == '1'}">
                    <td>计算中...</td>
                </c:if>
                <c:if test="${item.Row.preStatus == '0'}">
                    <td><span class="link" onclick="ct002.exportResult('${item.Row.schemeId}','${item.Row.resultId}','${item.Row.preBatch}')">[导出结果]</span>
                    </td>
                </c:if>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <jsp:include page="/oframe/common/page/page_fotter.jsp"/>
</div>
