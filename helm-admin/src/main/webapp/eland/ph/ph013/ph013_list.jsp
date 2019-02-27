<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div>
    <table class="table" layoutH="167" adjust="true" width="100%">
        <thead>
        <tr>
            <th>档案编号</th>
            <th>房屋地址</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${returnList}" var="item" varStatus="varStatus">
            <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}"/>
            <tr class="${tr_class} js_hs_item" onclick="ph013.info('${item.Row.HouseInfo.hsId}','${item.Row.HouseInfo.HsCtInfo.hsCtId}')" style="cursor: pointer;"
                id="tableTr">
                <td class="noBreak" title="${item.Row.HouseInfo.hsOwnerPersons}">
                    ${item.Row.HouseInfo.hsOwnerPersons}
                </td>
                <td class="noBreak" title="${item.Row.HouseInfo.hsOwnerPersons}">
                    ${item.Row.HouseInfo.hsFullAddr}
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <jsp:include page="/oframe/common/page/pager_fotter_simple.jsp"/>
</div>