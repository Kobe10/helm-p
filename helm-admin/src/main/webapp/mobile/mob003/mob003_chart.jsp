<%--suppress JSDuplicatedDeclaration --%>
<!DOCTYPE html>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<html>
<header>
    <title>指标图表</title>
    <script src="${pageContext.request.contextPath}/oframe/plugin/jquery/eland.jquery.min.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/mobile/common/js/echarts.min.js" type="text/javascript"></script>
</header>
<body>

<div class="prjJobData_${privilegeId}" style="width:99%;height:220px; min-height: 200px;background-color: #ffffff;margin: 1px;">
    <span class="js_no_data"
          style="line-height: 300px;font-weight: bold;display: block;width: 100%;text-align: center;">图表加载中……</span>
</div>
<script type="text/javascript">
    $(function () {
        var showRatio = "${showRatio}";
        if (showRatio == "") {
            showRatio = "true";
        }
        var chartDiv = $("div.prjJobData_${privilegeId}");
        chartDiv.height((chartDiv.parent().height() - 10) + "px");

        if (!${returnJson}) {
            chartDiv.find("span.js_no_data").html("查询服务出错");
            return;
        }
        var data = ${returnJson};
        var reportData = data.data.Report;
        if (!reportData) {
            chartDiv.find("span.js_no_data").html("无数据");
            return;
        }
        var series = reportData.series;
        if (!series) {
            chartDiv.find("span.js_no_data").html("无数据");
            return;
        }
        var chartType = '${chartType}';
        var legendData = [];
        for (var i = 0; i < series.length; i++) {
            var tempItem = series[i];
            var seriesCartType = tempItem.chartType;
            if (seriesCartType == "") {
                seriesCartType = "bar";
            }
            tempItem.type = seriesCartType;
            tempItem.stack = tempItem.stackName;
            tempItem.barMaxWidth = "25";
            tempItem.itemStyle = {
                normal: {
                    label: {
                        show: true,
                        position: 'inside',
                        textStyle: {"fontSize": "14", "color": "black"}
                    }
                }
            };
            legendData.push(tempItem.name);
        }
        var myChart = echarts.init(chartDiv[0]);
        var groupTitle = "${title}";
        if (groupTitle == "") {
            groupTitle = legendData;
        }
        var showLegend = false;
        if (series.length > 1) {
            showLegend = true;
        }
        var charts;
        //判断是否是饼图
        if (chartType == 'pie') {
            var pieData = [];
            var seriesData = [];
            var xAxisData = [];
            seriesData = reportData.series[0].data;
            xAxisData = reportData.xAxis;
            for (var i = 0; i < seriesData.length; i++) {
                pieData.push({
                    name: xAxisData[i],
                    value: seriesData[i]
                });
            }
            // 图表数据
            charts = {
                title: {text: legendData, x: 'center', y: 'top'},
                tooltip: {trigger: 'item', formatter: "{a} <br/>{b} : {c} ({d}%)"},
                legend: {x: 'right',y: 'top', orient: 'vertical', data: reportData.xAxis},
                toolbox: {
                    show: false,
                    feature: {
                        mark: {show: false},
                        dataView: {show: true, readOnly: true},
                        saveAsImage: {show: true},
                        restore: {show: true}
                    }
                },
                calculable: true,
                series: [
                    {
                        name: groupTitle,
                        type: "pie",
                        radius: '55%',
                        center: ['50%', '60%'],
                        itemStyle: {
                            normal: {
                                label: {
                                    show: true,
                                    formatter: "{b}:{c}({d}%)"
                                }
                            }
                        },
                        data: pieData
                    }
                ]
            };
        } else {
            // 计算比率增加markPoint
            if (series.length == 2 && showRatio == "true") {
                var series01Data = series[0].data;
                var series02Data = series[1].data;
                var maxNum = -1;
                for (var idx = 0; idx < series01Data.length; idx++) {
                    var value01 = parseFloat(series01Data[idx]);
                    var value02 = 0;
                    if (idx < series02Data.length) {
                        value02 = parseFloat(series02Data[idx]);
                    }
                    var sumValue = value01 + value02;
                    if (sumValue > maxNum) {
                        maxNum = sumValue;
                    }
                }
                var markPointData = [];
                for (idx = 0; idx < series01Data.length; idx++) {
                    value01 = parseFloat(series01Data[idx]);
                    value02 = 0;
                    if (idx < series02Data.length) {
                        value02 = parseFloat(series02Data[idx]);
                    }
                    sumValue = value01 + value02;
                    var radioValue = 0;
                    if (sumValue != 0) {
                        radioValue = (value01 / sumValue) * 100;
                        radioValue = radioValue.toFixed(2);
                    }
                    var symbolSize = 5;
                    if (radioValue == 100) {
                        symbolSize = 10;
                    }
                    markPointData.push({
                        "name": series[0].name + "占比",
                        "value": radioValue,
                        "xAxis": idx,
                        "yAxis": maxNum + maxNum,
                        "symbolSize": symbolSize
                    });
                }
                series[0].markPoint = {};
                series[0].markPoint.itemStyle = {
                    normal: {
                        label: {
                            show: true,
                            position: 'inside',
                            textStyle: {"fontSize": "15", "color": "black"},
                            formatter: '{c}%'
                        }
                    }
                };
                series[0].markPoint.data = markPointData;
            }
            // 是否显示缩放标尺
            var showDataZoom = false;
            var showStart = 0;
            var showEnd = 100;
            if (reportData.xAxis.length > 20) {
                showDataZoom = true;
                showStart = parseInt((reportData.xAxis.length - 20) / reportData.xAxis.length * 100);
            }
            // 图表参数
            charts = {
                title: {text: groupTitle, x: 'center', y: 'top'},
                tooltip: {
                    trigger: 'axis'
                },
                legend: {
                    show: showLegend,
                    y: 'bottom',
                    data: legendData
                },
                dataZoom: {
                    show: showDataZoom,
                    realtime: true,
                    start: showStart,
                    end: showEnd
                },
                toolbox: {
                    show: false,
                    feature: {
                        dataZoom: {
                            show: false,
                            title: {
                                dataZoom: '区域缩放',
                                dataZoomReset: '区域缩放后退'
                            }
                        },
                        mark: {show: false},
                        dataView: {
                            show: true,
                            readOnly: true
                        },
                        magicType: {show: true, type: ['line', 'bar']},
                        restore: {show: false},
                        saveAsImage: {show: true}
                    }
                },
                calculable: true,
                xAxis: [
                    {
                        type: 'category',
                        data: reportData.xAxis
                    }
                ],
                yAxis: [
                    {
                        type: 'value',
                        splitArea: {show: true}
                    }
                ],
                series: series
            };
        }
        // 最大值最小值
        if (reportData.minValue != "") {
            charts.yAxis[0].min = reportData.minValue;
        }
        if (reportData.maxValue != "") {
            charts.yAxis[0].max = reportData.maxValue;
        }
        myChart.setOption(charts);
    });
</script>
</body>
</html>