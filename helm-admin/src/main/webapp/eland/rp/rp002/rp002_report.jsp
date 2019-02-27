<%--
  Created by IntelliJ IDEA.
  User: shfb_wang
  Date: 2015/5/13 15:51
  Copyright(c) 北京四海富博计算机服务有限公司
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div>
    <table class="border" style="text-align: center">
        <tr>
            <th width="15%">总产籍数：</th>
            <td width="10%">${buildReport.totalCjNum}</td>
            <th width="15%">总户籍数：</th>
            <td width="10%">${buildReport.totalHjNum}</td>
            <th width="15%">总公房数：</th>
            <td width="10%">${buildReport.totalPubHsNum}</td>
            <th width="15%">总私房数：</th>
            <td width="10%">${buildReport.totalPriHsNum}</td>
        </tr>
        <tr>
            <th>总建筑面积(㎡)：</th>
            <td><oframe:money value="${buildReport.totalBuldSize}" format="number"/></td>
            <th>总占地面积(㎡)：</th>
            <td><oframe:money value="${buildReport.totalLandSize}" format="number"/></td>
            <th>总建面单价(元/㎡)：</th>
            <td><oframe:money value="${totalJmdj}" format="number"/></td>
            <th>总占地单价(元/㎡)：</th>
            <td><oframe:money value="${totalZddj}" format="number"/></td>
        </tr>
        <tr>
            <th>总成本合计(元)：</th>
            <td><oframe:money value="${buildReport.totalCb}" format="number"/></td>
            <th>奖励及补助合计(元)：</th>
            <td><oframe:money value="${buildReport.totalJlbz}" format="number"/></td>
            <th>税费合计(元)：</th>
            <td><oframe:money value="${buildReport.totalTaxFee}" format="number"/></td>
            <th>户口外迁费合计(元)：</th>
            <td><oframe:money value="${buildReport.totalHkWq}" format="number"/></td>
        </tr>
        <tr>
            <th>总应安置面积(㎡)：</th>
            <td>${buildReport.totalYaz}</td>
            <th>应安置面积对应购房款合计(元)：</th>
            <td><oframe:money value="${buildReport.totalGfk}" format="number"/></td>
            <th>应安置面积差额补助合计(元)：</th>
            <td><oframe:money value="${buildReport.totalMjceBz}" format="number"/></td>
            <th>&nbsp;</th>
            <td>&nbsp;</td>
        </tr>
    </table>
</div>