<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div id="ph010_list_print">
    <table class="table" layoutH="130"  width="100%">
        <thead>
        <tr>
            <th width="5%">序号</th>
            <%@include file="/oframe/common/query/list_htr.jsp" %>
            <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${returnList}" var="item" varStatus="varStatus">
            <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}"/>
            <tr class="${tr_class}">
                <td width="5%">${varStatus.index + 1}</td>
                <%@include file="/oframe/common/query/list_hdt.jsp" %>
                <td>
                    <a title="删除" onclick="ph010.delectView(${item.Row.camId})" class="btnView">[删除]</a>
                    <a title="修改" onclick="ph010.editView(${item.Row.camId})" class="btnView">[修改]</a>
                    <a title="地图标识" onclick="ph010.setCamPos(this, '${item.Row.camId}')" class="btnView">[地图标识]</a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <jsp:include page="/oframe/common/page/page_fotter.jsp"/>
</div>
