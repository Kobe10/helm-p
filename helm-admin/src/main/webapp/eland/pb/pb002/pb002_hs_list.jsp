<%--建筑内房产信息列表--%>
<%--人地房房产信息检索--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<oframe:script src="${pageContext.request.contextPath}/eland/ph/ph003/js/ph003_op.js" type="text/javascript"/>
<div id="pb002Hs_list_print">
    <table class="table" width="100%">
        <thead>
        <tr>
            <th width="10%">序号</th>
            <%@include file="/oframe/common/query/list_htr.jsp" %>
            <th width="15%">操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${returnList}" var="item" varStatus="varStatus">
            <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}"/>
            <tr class="${tr_class}">
                <td>${varStatus.index + 1}</td>
                <%@include file="/oframe/common/query/list_hdt.jsp" %>
                <td>
                    <button type="button" onclick="ph003Op.viewHouse('${item.Row.HouseInfo.hsId}')"
                            class="btn btn-more">
                        <i style="font-style:normal;">房产详情</i>
                    </button>
                </td>
            </tr>
        </c:forEach>
        <tr class="summary">
            <td>汇总</td>
            <%@include file="/oframe/common/query/list_sum.jsp" %>
            <td></td>
        </tr>
        </tbody>
    </table>
    <jsp:include page="/oframe/common/page/page_fotter.jsp"/>
</div>
