<%--院落腾退成本--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%--面板内容--%>
<div class="js_panel_context js_ph00207ChartDiv_07" style="min-height: 300px;"></div>
<script type="text/javascript">
    $(function () {
        // 查询参数
        var url = getGlobalPathRoot() + "eland/rp/rp000/rp000-orgSummary.gv";
        var packet = new AJAXPacket(url);
        packet.data.add("prjCd", getPrjCd());
        packet.data.add("jobGroup", "yard_hs_num");
        packet.data.add("buldId", "${buildId}");
        // 不显示背景
        packet.noCover = true;
        // 请求获取销售品销售统计报文
        core.ajax.sendPacketHtml(packet, function (response) {
            var data = eval("(" + response + ")");
            var isSuccess = data.success;
            if (isSuccess) {
                var reportData = data.data.ReportData;
                if (!reportData) {
                    return;
                }
                var dataSeries = null;
                if (!reportData.series) {
                    dataSeries = [];
                } else if (reportData.series.length) {
                    dataSeries = reportData.series;
                } else {
                    dataSeries = [];
                    dataSeries.push(reportData.series);
                }
                // 循环梳理
                var legendData = [];
                var seriesData = [];
                for (var i = 0; i < dataSeries.length; i++) {
                    var tempItem = dataSeries[i];
                    tempItem.barWidth = "50";
                    tempItem.itemStyle = { normal: {label: {show: true, position: 'inside', textStyle: {"fontSize": "14"}}}};
                    legendData.push(tempItem.name);
                    //数据
                    var sItem = {"name": tempItem.name + "(" + tempItem.data[0] + ")", "value": tempItem.data[0]};
                    seriesData.push(sItem);
                }
                var option = {
                    title: {
                        text: '房源使用情况',
                        x: 'center'
                    },
                    tooltip: {
                        trigger: 'item',
                        formatter: "{a} <br/>{b} : {c} ({d}%)"
                    },
                    legend: {
                        show: false,
                        orient: 'vertical',
                        x: 'left',
                        data: legendData
                    },
                    toolbox: {
                        show: true,
                        feature: {
                            mark: {show: false},
                            dataView: {show: true, readOnly: true},
                            restore: {show: false},
                            saveAsImage: {show: true}
                        }
                    },
                    calculable: true,
                    series: [
                        {
                            name: '房源使用情况',
                            type: 'pie',
                            radius: '60%',
                            center: ['50%', '60%'],
                            data: seriesData
                        }
                    ]
                };
                var chartDiv = $("div.js_ph00207ChartDiv_07", navTab.getCurrentPanel());
                var myChart = echarts.init(chartDiv[0]);
                myChart.setOption(option);
            }
        });
    });
</script>
