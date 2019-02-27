<%--院落腾退成本--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<table class="border" width="100%">
    <tr>
        <th width="15%">总产籍数：</th>
        <td width="10%">${buildBcYg.BuildInfo.totalCjNum}</td>
        <th width="15%">总户籍数：</th>
        <td width="10%">${buildBcYg.BuildInfo.totalHjNum}</td>
        <th width="15%">总公房数：</th>
        <td width="10%">${buildBcYg.BuildInfo.totalPubHsNum}</td>
        <th width="15%">总私房数：</th>
        <td width="10%">${buildBcYg.BuildInfo.totalPriHsNum}</td>
    </tr>
    <tr>
        <th>总建筑面积(㎡)：</th>
        <td><oframe:money value="${buildBcYg.BuildInfo.totalBuldSize}" format="number"/></td>
        <th>总占地面积(㎡)：</th>
        <td><oframe:money value="${buildBcYg.BuildInfo.totalBuldSize}" format="number"/></td>
        <th>总建面单价(元/㎡)：</th>
        <td><oframe:money value="${buildBcYg.BuildInfo.totalJmdj}" format="number"/></td>
        <th>总占地单价(元/㎡)：</th>
        <td><oframe:money value="${buildBcYg.BuildInfo.totalZddj}" format="number"/></td>
    </tr>
    <tr>
        <th>总成本合计(元)：</th>
        <td><oframe:money value="${buildBcYg.BuildInfo.totalCb}" format="number"/></td>
        <th>奖励及补助合计(元)：</th>
        <td><oframe:money value="${buildBcYg.BuildInfo.totalJlbz}" format="number"/></td>
        <th>税费合计(元)：</th>
        <td><oframe:money value="${buildBcYg.BuildInfo.totalTaxFee}" format="number"/></td>
        <th>户口外迁费合计(元)：</th>
        <td><oframe:money value="${buildBcYg.BuildInfo.totalHkWq}" format="number"/></td>
    </tr>
    <tr>
        <th>总应安置面积(㎡)：</th>
        <td>${buildBcYg.BuildInfo.totalYaz}</td>
        <th>应安置面积对应购房款合计(元)：</th>
        <td><oframe:money value="${buildBcYg.BuildInfo.totalGfk}" format="number"/></td>
        <th>应安置面积差额补助合计(元)：</th>
        <td><oframe:money value="${buildBcYg.BuildInfo.totalMjceBz}" format="number"/></td>
        <th>&nbsp;</th>
        <td>&nbsp;</td>
    </tr>
</table>
