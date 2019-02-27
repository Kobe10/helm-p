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
    <table class="table" adjust="false" width="100%">
        <thead>
        <tr>
            <th width="3%"><input type="checkbox" class="checkAllInList mart10" checked="checked" group="ids" onchange="rp002.checkAllBox(this)"/></th>
            <th width="3%">序号</th>
            <th>地址/门牌</th>
            <th>总建筑面积</th>
            <th>总占地面积</th>
            <th>总建面单价</th>
            <th>总占地单价</th>
            <th>总成本合计</th>
            <th>总应安置面积</th>
            <th>总应安置面积对应购房款</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${returnList}" var="item" varStatus="varStatus">
            <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}"/>
            <tr class="${tr_class}">
                <td><input type="checkbox" group="ids" name="buildId" checked onchange="rp002.sltSomeOne(this);" value="${item.Row.BuildInfo.buildId}" />
                                    <input type="hidden" name="buildType" value="${item.Row.BuildInfo.buildType}"/>
                </td>
                <td>${item.Row.BuildInfo.buildId}</td>
                <td>${item.Row.BuildInfo.yardFullAddr}</td>
                <td>${item.Row.BuildInfo.totalBuldSize}</td>
                <td>${item.Row.BuildInfo.totalLandSize}</td>
                <td>${item.Row.BuildInfo.totalJmdj}</td>
                <td>${item.Row.BuildInfo.totalZddj}</td>
                <td>${item.Row.BuildInfo.totalCb}</td>
                <td>${item.Row.BuildInfo.totalYaz}</td>
                <td>${item.Row.BuildInfo.totalGfk}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <jsp:include page="/oframe/common/page/page_fotter.jsp"/>
</div>
<script>
    $(document).ready(function(){
        rp002.findUnCheck();
    });
</script>