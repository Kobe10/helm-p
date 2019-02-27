<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="pageContent">
    <table class="table" width="100%">
        <tbody>
        <c:forEach items="${returnList}" var="item" varStatus="varStatus">
            <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}"/>
            <tr class="${tr_class}">
                <td>${varStatus.index + 1}</td>
                <td>${item.staff_code}</td>
                <td>${item.rht_name}</td>
                <td>${item.create_date}</td>
                <td><a title="删除" onclick="sys010.delete('${item.staff_id}','${item.rht_id}')" class="btnDel">[删除]</a></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <c:import url="/oframe/common/page/pager_fotter.jsp" charEncoding="UTF-8"></c:import>
</div>
