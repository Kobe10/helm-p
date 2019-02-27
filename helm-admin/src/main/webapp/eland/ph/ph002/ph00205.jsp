<%--院落腾退成本--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<table class="border" width="100%">
    <tr>
        <th width="30%"><label>建面单价:</label></th>
        <td>${buildBcYg.BuldCbYg.jMdJ}</td>
        <th width="25%"><label>占地单价:</label></th>
        <td>${buildBcYg.BuldCbYg.zDdJ}</td>
    </tr>
    <tr>
        <th><label>总成本合计:</label></th>
        <td colspan="3">
            <oframe:money value="${buildBcYg.BuldCbYg.totalCb}" format="number"/>
        </td>
    </tr>
    <tr>
        <th><label>总建筑面积:</label></th>
        <td>${buildBcYg.BuldCbYg.totalBuldSize}</td>
        <th><label>总占地面积:</label></th>
        <td>${buildBcYg.BuldCbYg.buldLandSize}</td>
    </tr>
    <tr>
        <th><label>奖励及补助合计:</label></th>
        <td colspan="3"><oframe:money value="${buildBcYg.BuldCbYg.totalJlBz}" format="number"/></td>
    </tr>
    <tr>
        <th><label>应安置面积:</label></th>
        <td colspan="3">${buildBcYg.BuldCbYg.totalYazMj}</td>
    </tr>
    <tr>
        <th><label>应安置面积对应购房款:</label></th>
        <td colspan="3"><oframe:money value="${buildBcYg.BuldCbYg.totalYazMjMoney}" format="number"/></td>
    </tr>
</table>
