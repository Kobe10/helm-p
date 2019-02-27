<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div id="pb001HsList">
    <table class="table" layoutH="340" width="100%">
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
                <td><input type="checkbox" group="ids" name="hsId" value="${item.Row.HouseInfo.hsId}"/></td>
                <%@include file="/oframe/common/query/list_hdt.jsp" %>
                <td>
                    <button type="button" onclick="pb001.openHs('${item.Row.HouseInfo.hsId}')"
                            class="btn btn-more">
                        <i style="font-style:normal;">居民详情</i>
                        <span class="caret"></span>
                        <ul style="width: 200px;" class="menu hidden">
                            <%@include file="/eland/ph/ph003/ph003_list_op.jsp" %>
                            <span style="clear: both"></span>
                        </ul>
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