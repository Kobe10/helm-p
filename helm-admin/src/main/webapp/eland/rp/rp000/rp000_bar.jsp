<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<div class="border mar5" style="min-height: 300px;">
    <input type="hidden" name="toContainer" value="${toContainer}">

    <div class="${toContainer}" style="background-color: #ffffff;float:left;min-height: 300px;width: 60%;">
        <c:if test="${fn:length(titles) == 0}">
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
            chartDiv.css("height", dataTable.height);
        }
        var xAxisPic = [];
        <c:if test="${xAxis != '' && xAxis != null}">
            xAxisPic = ${xAxis};
        </c:if>
        <c:if test="${xAxis == '' || xAxis == null}">
            xAxisPic = null;
        </c:if>

        var rotate = 0;
        if (xAxisPic != null && xAxisPic.length > 10) {
            rotate = 90;
        }
        var charts = {
            title: {text: '${param.rptName}', x: 'center', y: 'top'},
            tooltip: {trigger: 'axis'},
            <c:if test="${fn:length(seriesCharData) > 1}">
            legend: {x: 'center', y: 'bottom', orient: 'horizontal', data: ${legendData}},
            </c:if>
            toolbox: {
                show: true,
                orient: 'vertical',
                x: 'left', y: 'top',
                feature: {
                    dataZoom: {show: false},
                    mark: {show: false},
                    dataView: {show: false},
                    saveAsImage: {show: true},
                    magicType: {show: true, type: ['line', 'bar']},
                    restore: {show: false}
                }
            },
            calculable: false,
            xAxis: [
                {type: 'category', axisLabel: {interval: 0, rotate: rotate}, data: xAxisPic}
            ],
            yAxis: [
                {type: 'value', splitArea: {show: false}}
            ],
            series: [
                <c:forEach items="${seriesCharData}" var="item">
                {
                    name: '${item.key}',
                    type: "bar",
                    itemStyle: {normal: {label: {show: true, position: 'top', textStyle: {"fontSize": "14"}}}},
                    barMaxWidth: "40",
                    data: ${item.value}
                },
                </c:forEach>

            ]
        };
        <c:if test="${fn:length(seriesCharData) != 0}">
        var myChart = echarts.init(chartDiv[0]);
        myChart.setOption(charts);
        </c:if>
    });
</script>