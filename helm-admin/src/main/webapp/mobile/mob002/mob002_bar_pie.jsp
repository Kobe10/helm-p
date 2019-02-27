<%--
  以管理组织为维度展示柱状图形和饼图
  User: Linux
  Date: 2015/6/13
  Time: 0:23
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="prjJobData_${param.privilegeId} echart_div"
     style="width:99%;min-height: 300px;background-color: #ffffff;margin: 1px;
     border-bottom: 1px solid #c9c9c9;">
    <span class="js_no_data"
          style="font-weight: bold;display: block;width: 100%;text-align: center;">加载中……</span>
</div>
<script type="text/javascript">
    $(function () {
        var showRatio = "${param.showRatio}";
        if (showRatio == "") {
            showRatio = "true";
        }
        var chartDiv = $("div.prjJobData_${param.privilegeId}");
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
                    chartDiv.find("span.js_no_data").html("${title}无数据");
                    return;
                }
                var series = reportData.series;
                if (!series) {
                    chartDiv.find("span.js_no_data").html("${title}无数据");
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
                var option;
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
                    option = {
                        title: {text: legendData, x: 'center', y: 'top'},
                        tooltip: {trigger: 'item', formatter: "{a} <br/>{b} : {c} ({d}%)"},
                        legend: {
                            left: 10,
                            top: '15%',
                            orient: 'vertical',
                            data: reportData.xAxis
                        },
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
                            radioValue = 0;
                            if (sumValue != 0) {
                                var radioValue = (value01 / sumValue) * 100;
                                radioValue = radioValue.toFixed(2);
                            }
                            var symbolSize = 5;
                            if (symbolSize = 100) {
                                symbolSize = 10;
                            }
                            markPointData.push({
                                "name": series[0].name + "占比",
                                "value": radioValue,
                                "xAxis": idx,
                                "yAxis": maxNum,
                                "symbolSize": symbolSize
                            })
                        }
                        series[0].markPoint = {};
                        series[0].markPoint.label = {
                            normal: {
                                show: true,
                                position: 'inside',
                                textStyle: {"fontSize": "15", "color": "black"},
                                formatter: '{c}%'
                            }
                        };
                        series[0].markPoint.data = markPointData;

                    }
                    option = {
                        title: {text: groupTitle, x: 'center', y: 'top'},
                        tooltip: {
                            trigger: 'axis'
                        },
                        legend: {
                            show: showLegend,
                            data: legendData,
                            right: 10,
                            top: '15%',
                            orient: 'vertical'
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
                myChart.setOption(option);
                $(window).bind("resize", function () {
                    myChart.resize();
                });
            } else {
                alert(data.errMsg);
            }
        })
    })
</script>