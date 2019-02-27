<%--发起任务通用处理模板--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="pageContent">
    <table class="table" border="0" width="100%">
        <tbody>
        <c:forEach items="${returnList}" var="item" varStatus="varStatus">
            <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}"/>
            <tr class="${tr_class}">
                <td class="noBreak" title="${item.Row.procDefName}">
                    <span class="link"
                          onclick="ph00302.queryViewWf('${item.Row.procInsId}');">
                        <c:choose>
                            <c:when test="${item.Row.procInsName == null||item.Row.procInsName == ''}">
                                ${item.Row.procDefName}
                            </c:when>
                            <c:otherwise>
                                ${item.Row.procInsName}
                            </c:otherwise>
                        </c:choose>
                    </span>
                </td>
                <td class="noBreak">${item.Row.procStTime}</td>
                <td class="noBreak">${item.Row.procEndTime}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <c:import url="/oframe/common/page/pager_fotter_simple.jsp" charEncoding="UTF-8"></c:import>
</div>