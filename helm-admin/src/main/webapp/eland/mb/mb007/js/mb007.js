/**
 * Created by shfb_wang on 2015/6/11 0011.
 */
var mb007 = {
    project: null,
    timeObj: null,
    editReg: function (obj) {
        var url = getGlobalPathRoot() + "eland/pj/pj004/pj004-initTree.gv?treeType=1&fromOp=mb007";
        $(obj).openTip({
            href: url, height: "230",
            width: "200", offsetX: 0, offsetY: 31
        });
    },
    editOrg: function (obj) {
        var url = getGlobalPathRoot() + "eland/pj/pj003/pj003-initTree.gv?fromOp=mb007";
        $(obj).openTip({
            href: url, height: "230", width: "200",
            offsetX: 0, offsetY: 31
        });
    },
    flashChanged: function (obj) {
        if ($(obj).attr("checked")) {
            mb007.changePicData();
        } else if (mb007.timeObj) {
            clearTimeout(mb007.timeObj);
            mb007.timeObj = null;
        }
    },
    /**
     * 更新刷新频率
     * @param obj
     */
    updateFlash: function (obj) {
        var checkBox = $(obj).parent().find("input[name=autoFlash]");
        if ($(obj).attr("checked")) {
            clearTimeout(mb007.timeObj);
            mb007.timeObj = null;
            mb007.changePicData();
        }
    },
    /**
     * 点击项目区域节点
     * @param regId
     */
    entityClickFunc: function (regId) {
        var url = getGlobalPathRoot() + "eland/mb/mb007/mb007-regSummary.gv";
        var packet = new AJAXPacket(url);
        var conDiv = $('#mb007ChartDiv').parent();
        var prjJobCd = $("select[name=prjJobCd]", conDiv).val();
        var jobGroup = $("input[name=prjOrgId]", conDiv).val();
        var prjOrgId = $("input[name=prjOrgId]", conDiv).val();

        //// 查询参数
        packet.data.data = {
            "prjCd": getPrjCd(),
            "regId": regId,
            "prjJobCd": prjJobCd,
            "jobGroup": jobGroup,
            "prjOrgId": prjOrgId
        };
        // 不显示背景
        packet.noCover = true;
        // 请求获取销售品销售统计报文
        core.ajax.sendPacketHtml(packet, function (response) {
            var data = eval("(" + response + ")");
            var isSuccess = data.success;
            if (isSuccess) {
                var resultData = data.data;
                var legend = data.legend;
                var xAxis = data.xAxis;

                var series = null;
                if (!resultData) {
                    series = new Array();
                } else if (resultData.length) {
                    series = resultData;
                } else {
                    series = new Array();
                    series.push(resultData.series);
                }

                for (var i = 0; i < series.length; i++) {
                    var tempItem = series[i];
                    tempItem.type = "bar";
                    //叠加图。下面两个参数同时开启，否则柱子偏移。
                    //tempItem.stack = "统计";
                    //tempItem.barCategoryGap = "80%";
                    tempItem.barMaxWidth = "25";
                    tempItem.itemStyle = {
                        normal: {
                            label: {
                                show: true,
                                position: 'inside',
                                textStyle: {"fontSize": "12", "color": "black"}
                            }
                        }
                    };
                }

                //获取要展示的图例, 图例开关 xAxis.xAxis ==》图例数据
                var showLegend = false;
                if (xAxis.xAxis.length) {
                    showLegend = true;
                }

                var chartDiv = $('#mb007ChartDiv');
                var myChart = echarts.init(chartDiv[0]);
                var groupTitle = "统计图";

                //生成图表数据
                var charts = {
                    title: {
                        x: "center", y: "top",
                        text: groupTitle,
                        textStyle: {fontStyle: "normal", fontSize: 21}
                    },
                    legend: {
                        show: showLegend,
                        data: xAxis.xAxis,
                        y: "bottom",
                        orient: "horizontal"
                    },
                    toolbox: {
                        show: true,
                        orient: "vertical",
                        x: "right",
                        y: "top",
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
                                readOnly: true,
                                show: true
                            },
                            magicType: {
                                type: ["line", "bar"],
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
                            //data: ["一分指", "二分指", "三分指", "四分指", "五分指"],
                            data: legend.legend,
                            nameLocation: "end",
                            name: "指标"
                        }
                    ],
                    yAxis: [
                        {
                            type: "value",
                            splitArea: {
                                show: false
                            },
                            name: "数量",
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
                }
                myChart.setOption(charts);


                var divObj = $("#mb007ChartDiv").parent();
                var autoFlashInt = $(divObj).find("input[name=autoFlashInt]").val();
                if (!autoFlashInt || autoFlashInt == "" || isNaN(autoFlashInt)) {
                    $(divObj).find("input[name=autoFlashInt]").val(60);
                    autoFlashInt = 60000;
                } else {
                    autoFlashInt = autoFlashInt * 1000;
                }
                var checkBox = $(divObj).find("input[name=autoFlash]");
                if ($(checkBox).attr("checked")) {
                    clearTimeout(mb007.timeObj);
                    mb007.timeObj = setTimeout(function () {
                        mb007.changePicData();
                    }, autoFlashInt);
                }
            }
            else {
                alertMsg.error(data.errMsg);
            }
        })
    },
    changePicData: function () {
        var regId = $("input[name=regId]").val();
        mb007.entityClickFunc(regId);
    }

};

$(function () {
    // 刷新显示图片
    setTimeout(mb007.changePicData, 100);
});
