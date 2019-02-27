<%--
  以管理组织为维度展示柱状图形和饼图
  Created with IntelliJ IDEA
  User: shfb_wang
  Date: 2016/8/31 0011 21:16
  Copyright(c) 北京四海富博计算机服务有限公司
--%>
<!DOCTYPE html>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<html>
<header>
    <title>指标图表</title>
    <script src="${pageContext.request.contextPath}/oframe/plugin/jquery/eland.jquery.min.js"
            type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/mobile/common/js/echarts.min.js" type="text/javascript"></script>
</header>
<body>

<div class="prjJobData_${privilegeId}" style="width:99%;height:290px; min-height: 200px;background-color: #ffffff;margin: 1px;">
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

        var data = ${returnJson};

        var resultData = data.data;
        var legend = data.legend;
        var xAxis = data.xAxis;
        var orgName = xAxis.orgName;
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
            chartDiv.find("span.js_no_data").html("无数据");
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
                x: "right",
                orient: "vertical"
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
//        var portal = {};
//        portal.chartMap.put(divIdentity, myChart);
//        // 绑定双击事件
//        myChart.on("dblclick", function (param) {
//            if (typeof param.dataIndex != 'undefined') {
//                var localChart = portal.chartMap.get(divIdentity);
//                var dataIndex = param.dataIndex;
//                var options = localChart.getOption();
//                var orgId = options.orgIds[dataIndex];
//                var currentPath = options.orgIdPath;
//                alert(1);
////                    mb008.loadPic(currentPath + "/" + orgId, divIdentity, options.jsonData);
//            }
//        });
    });
</script>
</body>
</html>