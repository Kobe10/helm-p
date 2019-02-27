<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<div class="border mar5" style="min-height: 300px;">
    <input type="hidden" name="toContainer" value="${toContainer}">

    <div class="${toContainer}" style="background-color: #ffffff;float:left;min-height: 300px;width: 60%;">
        <c:if test="${fn:length(series) == 0}">
            <span style="line-height: 300px;font-weight: bold;display: block;width: 100%;text-align: center;">
                【${param.rptName}】无数据
            </span>
        </c:if>
    </div>
    <table class="list mart5 marr5" style="float:right; width: 38%;">
        <thead>
        <tr>
            <th><label>序号</label></th>
            <c:forEach items="${titles}" var="item">
                <th><label>${item.Field.name}</label></th>
            </c:forEach>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${series}" var="row" varStatus="varStataus">
            <tr>
                <td>${varStataus.index + 1}</td>
                <c:forEach items="${row.value}" var="item">
                    <td>${item}</td>
                </c:forEach>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <div style="clear: both;"></div>
</div>
<script type="text/javascript">
    $(function () {
        var chartDiv = $("div.${toContainer}", navTab.getCurrentPanel());
        var dataTable = chartDiv.next("Table");
        if (dataTable.height() > chartDiv.height()) {
            chartDiv.css("height", dataTable.height());
        }
        <%--var xAxis = ${xAxis};--%>
        var xAxisPic = [];
        <c:if test="${fn:length(series) != 0}">
        xAxisPic = ${xAxis};
        </c:if>
        <c:if test="${fn:length(series) == 0}">
        xAxisPic = null;
        </c:if>

        var pieData = [];
        <c:forEach items="${series}" var="row" varStatus="varStataus">
        pieData.push({name: "${row.value[0]}", value: ${row.value[1]}});
        </c:forEach>
        var charts = {
            title: {text: '${param.rptName}', x: 'center', y: 'top'},
            tooltip: {trigger: 'item', formatter: "{a} <br/>{b} : {c} ({d}%)"},
//            legend: {x: 'center', y: 'bottom', orient: 'horizontal', data: xAxisPic},
            toolbox: {
                show: true,
                orient: 'vertical',
                x: 'left', y: 'top',
                feature: {
                    dataZoom: {show: false}, mark: {show: false}, dataView: {show: true, readOnly: true},
                    saveAsImage: {show: true},
                    magicType: {show: true, type: ['pie', 'funnel']}, restore: {show: false}
                }
            },
            calculable: true,
            series: [
                {
                    name: '${param.rptName}',
                    type: "pie",
                    radius: '55%',
                    center: ['50%', '45%'],
                    itemStyle: {normal: {label: {show: true, position: 'top', textStyle: {"fontSize": "14"}}}},
                    data: pieData
                }
            ]
        };
        <c:if test="${fn:length(series) != 0}">
        var myChart = echarts.init(chartDiv[0]);
        myChart.setOption(charts);
        </c:if>
    });
</script>