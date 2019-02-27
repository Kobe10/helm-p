<%--访问量统计指标--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<div class="prjJobData_${param.privilegeId}" style="background-color: #ffffff;min-height: 300px; margin: 1px;">
    <span class="js_no_data"
          style="line-height: 300px;font-weight: bold;display: block;width: 100%;text-align: center;">加载中……</span>
</div>
<script type="text/javascript">
    $(function () {
        var showRatio = "${param.showRatio}";
        if (showRatio == "") {
            showRatio = "true";
        }
        var chartDiv = $("div.prjJobData_${param.privilegeId}", navTab.getCurrentPanel());
        chartDiv.height((chartDiv.parent().height() - 10) + "px");
        // 查询参数
        var url = getGlobalPathRoot() + "eland/rp/rp000/rp000-dataSummary.gv";
        var packet = new AJAXPacket(url);
        var jsonData = ${initParams};
        for (var item in jsonData) {
            packet.data.add(item, jsonData[item]);
        }
        // 不显示背景
        packet.noCover = true;
        // 获取图表显示数据
        core.ajax.sendPacketHtml(packet, function (response) {
            var data = eval("(" + response + ")");
            var isSuccess = data.success;
            if (isSuccess) {
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
                var legendData = new Array();
                for (var i = 0; i < series.length; i++) {
                    var tempItem = series[i];
                    var seriesCartType = tempItem.chartType;
                    if (seriesCartType == "") {
                        seriesCartType = "bar";
                    }
                    tempItem.type = seriesCartType;
                    var stackName = tempItem.stackName;
                    tempItem.stack = stackName;
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
                    var seriesData = new Array();
                    var xAxisData = new Array();
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
                        title: {text: groupTitle, x: 'center', y: 'top'},
                        tooltip: {trigger: 'item', formatter: "{a} <br/>{b} : {c} ({d}%)"},
                        legend: {x: 'left', orient: 'vertical', data: reportData.xAxis},
                        toolbox: {
                            show: true,
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
                        ],
                        color: [${colorStr}]
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
                        for (var idx = 0; idx < series01Data.length; idx++) {
                            var value01 = parseFloat(series01Data[idx]);
                            var value02 = 0;
                            if (idx < series02Data.length) {
                                value02 = parseFloat(series02Data[idx]);
                            }
                            var sumValue = value01 + value02;
                            var radioValue = 0;
                            if (sumValue != 0) {
                                var radioValue = (value01 / sumValue) * 100;
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
                            show: true,
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
                        series: series,
                        color: [${colorStr}]
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
            } else {
                alertMsg.error(data.errMsg);
            }
        })
    });
</script>
