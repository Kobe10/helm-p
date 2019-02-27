<%--人地房房产信息检索--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div id="oh007_list_print">
    <table class="table" layoutH="130" width="100%">
        <thead>
        <tr>
            <th width="5%" fieldName="newHsId">
                <input type="checkbox" class="checkboxCtrl" group="ids"/>
            </th>
            <%@include file="/oframe/common/query/list_htr.jsp" %>
            <th width="10%">操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${returnList}" var="item" varStatus="varStatus">
            <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}"/>
            <c:set var="tempVar" value="${item.Row.NewHsInfo.handleStatus}${''}"/>
            <tr class="${tr_class}">
                <td>
                    <c:if test="${tempVar != '4'}">
                        <input type="checkbox" group="ids" name="newHsId"
                               statusCd="${item.Row.NewHsInfo.statusCd}"
                               handleStatus="${item.Row.NewHsInfo.handleStatus}"
                               value="${item.Row.NewHsInfo.newHsId}"/>
                    </c:if>
                </td>
                <%@include file="/oframe/common/query/list_hdt.jsp" %>
                <td>

                    <c:if test="${tempVar == '2' || tempVar == '3'}">
                            <span class="link"
                                  onclick="oh007.ctDelay('${item.Row.NewHsInfo.oldHsId}','${item.Row.NewHsInfo.newHsId}')">延期签约</span>
                    </c:if>

                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <jsp:include page="/oframe/common/page/page_fotter.jsp"/>
</div>
