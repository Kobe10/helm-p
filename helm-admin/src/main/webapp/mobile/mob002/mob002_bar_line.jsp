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
     style="width:99%;min-height: 300px;background-color: #ffffff;
     margin: 1px; border-bottom: 1px solid #c9c9c9;">
    <span class="js_no_data"
          style="font-weight: bold;display: block;width: 100%;text-align: center;">加载中……</span>
</div>
<script type="text/javascript">
    var mob002_2bar = {
        loadPic: function (orgIdPath, divIdentity, jsonData) {
            var showRatio = "${param.showRatio}";
            if (showRatio == "") {
                showRatio = "true";
            }
            var chartDiv = $("div." + divIdentity);
            var url = getGlobalPathRoot() + "eland/mb/mb008/mb008-orgSummary.gv";
            var packet = new AJAXPacket(url);
            for (var item in jsonData) {
                packet.data.add(item, jsonData[item]);
            }
            packet.data.add("orgIdPath", orgIdPath);
            // 不显示背景
            packet.noCover = true;
            // 获取响应报文
            core.ajax.sendPacketHtml(packet, function (response) {
                var data = eval("(" + response + ")");
                var isSuccess = data.success;
                if (isSuccess) {
                    var resultData = data.data;
                    var legend = data.legend;
                    var xAxis = data.xAxis;
                    var orgName = data.xAxis.orgName;
                    var titleText = data.titleText;
                    if (orgName != "") {
                        titleText = "【" + orgName + "】" + titleText;
                    }
                    var series = null;
                    if (!resultData) {
                        series = [];
                    } else {
                        if (resultData.series) {
                            series = resultData.series.series;
                        } else {
                            series = [];
                        }
                    }
                    if (series.length == 0) {
                        chartDiv.find("span.js_no_data").html(titleText+ "无数据");
                        return;
                    }
                    for (var i = 0; i < series.length; i++) {
                        var tempItem = series[i];
                        var seriesCartType = tempItem.chartType;
                        if (seriesCartType == "") {
                            seriesCartType = "bar";
                        }
                        tempItem.type = seriesCartType;
                        var stackName = tempItem.stackName;
                        tempItem.stack = stackName;
                        tempItem.barMaxWidth = 25;
                        tempItem.barMinHeight = 20;
                        tempItem.itemStyle = {
                            normal: {
                                label: {
                                    show: true,
                                    position: 'inside',
                                    textStyle: {"fontSize": "12", "color": "black", baseline: "middle", align: "center"}
                                }
                            }
                        };
                        tempItem.data = eval("(" + tempItem.data + ")");
                    }
                    // 计算比率增加markPoint
                    if (series.length == 2 && showRatio == "true") {
                        var series01Data = series[0].data;
                        var series02Data = series[1].data;
//                        if (series01Data.length == series02Data.length) {
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
                            if(symbolSize = 100) {
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
//                        }
                    }

                    //获取要展示的图例, 图例开关 xAxis.xAxis ==》图例数据
                    var showLegend = false;
                    if (xAxis.xAxis.length) {
                        showLegend = true;
                    }
                    // 横坐标文本标记旋转角度
                    var rotate = 0;
                    if (legend.legend.length > 10) {
                        rotate = 45;
                    }

                    // 初始化化
                    var myChart = echarts.init(chartDiv[0]);
                    var groupTitle = "${title}";
                    if (groupTitle == "") {
                        groupTitle = titleText;
                    }
                    //
                    var charts = {
                        // 扩展数据增加项目组织编号
                        orgIds: xAxis.orgIds,
                        orgIdPath: data.orgIdPath,
                        jsonData: jsonData,
                        title: {
                            x: "center", y: "top",
                            text: groupTitle
                        },
                        legend: {
                            show: showLegend,
                            data: xAxis.xAxis,
                            itemGap: 5,
                            itemWidth: 10,
                            y: "top",
                            x: "left",
                            orient: "horizontal"
                        },
                        toolbox: {
                            show: false,
                            feature: {
                                dataZoom: {
                                    show: false
                                },
                                mark: {show: false},
                                dataView: {
                                    readOnly: true,
                                    show: true
                                },
                                magicType: {
                                    type: ['tiled', "stack"],
                                    show: true
                                },
                                restore: {
                                    show: true
                                },
                                saveAsImage: {
                                    show: true
                                }
                            }
                        },
                        xAxis: [
                            {
                                type: "category",
                                data: legend.legend,
                                nameLocation: "end",
                                axisLabel: {interval: 0, rotate: rotate, margin: 8}
                            }
                        ],
                        yAxis: [
                            {
                                type: "value",
                                splitArea: {
                                    show: false
                                },
                                nameLocation: "end"
                            }
                        ],
                        series: series,
                        calculable: true,
                        tooltip: {
                            trigger: "axis",
                            axisPointer: {
                                type: "line"
                            }
                        }
                    };
                    myChart.setOption(charts);
                    $(window).bind("resize", function () {
                        myChart.resize();
                    });
                    portal.chartMap.put(divIdentity, myChart);
                    // 绑定双击事件
                    myChart.on("click", function (param) {
                        if (typeof param.dataIndex != 'undefined') {
                            var localChart = portal.chartMap.get(divIdentity);
                            var dataIndex = param.dataIndex;
                            var options = localChart.getOption();
                            var orgId = options.orgIds[dataIndex];
                            var currentPath = options.orgIdPath;
                            mob002_2bar.loadPic(currentPath + "/" + orgId, divIdentity, options.jsonData);
                        }
                    });
                } else {
                    alert(data.errMsg);
                }
            });
        }
    };

    $(function () {
        mob002_2bar.loadPic("", "prjJobData_${param.privilegeId}", ${initParams});
    });

</script>