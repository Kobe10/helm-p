<%-- 补偿计算 腾退项目 展示 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<c:choose>
    <c:when test="${errMsg != ''}">
        <div class="subBar center mart5 marb5">
            <span style="margin-right: 30px; color: #ff0000; font-size: 18px; font-weight: bold">${errMsg}</span>
        </div>
    </c:when>
    <c:otherwise>
        <table class="list" id="ph004TreeBody" width="100%" style="margin: 0!important;">
            <thead>
            <tr data-tt-id="0" style="background-color: #e2edf3;line-height: 25px">
                <th width="12%">序号</th>
                <th width="15%">补助项目</th>
                <th width="10%">补助金额(元)</th>
                <th>计算方式</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${items}" var="item" varStatus="varStatus">
                <tr data-tt-id="${item.Item.nameEn}" data-tt-parent-id="${item.Item.upName}">
                    <td style="text-align: left!important;"><label>${varStatus.index + 1}</label></td>
                    <td>${item.Item.nameCh}</td>
                    <td style="text-align: right;">
                        <c:if test="${item.Item.money == ''}">
                            0.00
                        </c:if>
                        <c:if test="${item.Item.money != ''}">
                            <oframe:money value="${item.Item.money}" format="number"/>
                        </c:if>
                    </td>
                    <td style="text-align: left;">${item.Item.ruleDesc}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:otherwise>
</c:choose>

