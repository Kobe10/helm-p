var rp000 = {
    editOrg: function (obj) {
        var url = getGlobalPathRoot() + "eland/pj/pj003/pj003-initTree.gv?fromOp=rp000";
        $(obj).openTip({href: url, height: "230", width: "200",
            offsetX: -23, offsetY: 31});
    },
    changeReportItem: function (obj) {
        var container = $("#reportDivContainer");
        var _obj = $(obj);
        if (_obj.attr("checked")) {
            var newDiv = $("<div style='width: 90%;' id='pageChart_" + _obj.val() + "' class='pageChart " + _obj.val() + "'></div>");
            $("#reportDiv", container).append(newDiv);
            $("div.js-slt-item", container).append(_obj.parent().clone());
            rp000.refreshItem(_obj.val());
        } else {
            var value = _obj.val();
            var sameCheckItem = $("div.js-slt-item", container).find("input[type=checkbox][value=" + value + "]");
            sameCheckItem.parent().remove();
            $("div.js-job-cd-list").find("input[type=checkbox][value=" + value + "]").removeAttr("checked");
            $("#reportDiv", container).find("div." + _obj.val()).remove()
        }
    },

    refreshAll: function () {
        var container = $("#reportDivContainer");
        $("div.js-slt-item", container).find("input[type=checkbox]").each(function () {
            rp000.refreshItem($(this).val());
        });
    },
    refreshItem: function (jobCd) {
        // 调用服务增加展示图标
        var container = $("#reportDivContainer");
        var prjOrgId = $("input[name=prjOrgId]", container).val();
        var startTime = $("input[name=startTime]", container).val();
        var endTime = $("input[name=endTime]", container).val();
        var chartDiv = $("#reportDiv", container).find("div." + jobCd);
        var needSub = false;
        if ($("input[type=checkbox][name=needSub]", container).attr("checked")) {
            needSub = true;
        }
        // 查询参数
        var url = getGlobalPathRoot() + "eland/rp/rp000/rp000-orgSummary.gv";
        var packet = new AJAXPacket(url);
        packet.data.add("prjCd", getPrjCd());
        packet.data.add("prjOrgId", prjOrgId);
        packet.data.add("needSub", needSub);
        packet.data.add("startTime", startTime);
        packet.data.add("endTime", endTime);
        packet.data.add("jobCd", jobCd);
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
                var series = null;
                if (!reportData.series) {
                    series = new Array();
                } else if (reportData.series.length) {
                    series = reportData.series;
                } else {
                    series = new Array();
                    series.push(reportData.series);
                }
                var legendData = new Array();
                for (var i = 0; i < series.length; i++) {
                    var tempItem = series[i];
                    tempItem.type = "line";
                    tempItem.barMaxWidth = "50";
                    legendData.push(tempItem.name);
                }
                var myChart = echarts.init(chartDiv[0]);
                var charts = {
                    title: {text: legendData, x: 'center', y: 'top'},
                    tooltip: {
                        trigger: 'axis'
                    },
                    legend: {
                        y: 'bottom',
                        data: legendData
                    },
                    toolbox: {
                        show: true,
                        feature: {
                            dataZoom: {
                                show: true,
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
                            data: data.xAxis.xAxis
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
                myChart.setOption(charts);
            }
            else {
                alertMsg.error(data.errMsg);
            }
        });
    },
    searchJob: function () {
        var container = $("#reportDivContainer");
        var srcJobName = $("input[name=srcJobName]", container).val();
        var url = getGlobalPathRoot() + "eland/rp/rp000/rp000-queryJob.gv";
        var packet = new AJAXPacket(url);
        packet.data.add("prjCd", getPrjCd());
        packet.data.add("jobName", srcJobName);
        // 请求获取销售品销售统计报文
        core.ajax.sendPacketHtml(packet, function (response) {
            var r = $(response);
            var errMsg = $("input[name=errMsg]", r).val();
            if (errMsg != '') {
                alertMsg.error(errMsg);
            } else {
                $("div.js-job-cd-list").html(r.html());
                $("div.js-slt-item", container).find("input[type=checkbox]").each(
                    function () {
                        var checkValue = $(this).val();
                        $("div.js-job-cd-list").find("input[type=checkbox][value=" + checkValue + "]")
                            .attr("checked", "checked");
                    }
                );
            }
        }, true);
    },
    removeAll: function () {
        var container = $("#reportDivContainer");
        $("#reportDiv", container).find("div").remove();
        $("div.js-slt-item", container).find("span").remove();
        $("div.js-job-cd-list").find("input[type=checkbox]").removeAttr("checked");
    },
    addAll: function () {
        var container = $("#reportDivContainer").find("div.js-job-cd-list");
        $("input[type=checkbox]", container).each(function () {
            var _this = $(this);
            if (!_this.attr("checked")) {
                _this.attr("checked", "checked");
                rp000.changeReportItem(this);
            }
        });
    }
}
;
$(function () {
    // 查询可用指标
    setTimeout(rp000.searchJob, 100);
});

