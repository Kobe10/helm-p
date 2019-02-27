<%--
  Created by IntelliJ IDEA.
  User: shfb_wang
  Date: 2015/5/7
  Time: 11:41
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div id="pj016yzf_list">
    <table class="table" layoutH="265" adjust="false" width="100%">
        <thead>
        <tr>
            <th width="3%">序号</th>
            <%@include file="/oframe/common/query/list_htr.jsp" %>
            <th  style="width: 120px;">操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${returnList}" var="item" varStatus="varStatus">
            <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}"/>
            <tr class="${tr_class}">
                <td>${varStatus.index + 1}</td>
                <%@include file="/oframe/common/query/list_hdt.jsp" %>
                <td>
                    <a title="查看" onclick="pj016.openBuilding('${item.Row.buildId}')" class="btnEdit">[查看详情]</a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <jsp:include page="/oframe/common/page/page_fotter.jsp"/>
</div>
