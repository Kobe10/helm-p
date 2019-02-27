mb003 = {
    project: null,
    timeObj: null,
    editReg: function (obj) {
        var url = getGlobalPathRoot() + "eland/pj/pj004/pj004-initTree.gv?treeType=1&fromOp=mb003";
        $(obj).openTip({href: url, height: "230",
            width: "200", offsetX: -23, offsetY: 31});
    },
    editOrg: function (obj) {
        var url = getGlobalPathRoot() + "eland/pj/pj003/pj003-initTree.gv?fromOp=mb003";
        $(obj).openTip({href: url, height: "230", width: "200",
            offsetX: -23, offsetY: 31});
    },
    flashChanged: function (obj) {
        if ($(obj).attr("checked")) {
            mb003.changePicData();
        } else if (mb003.timeObj) {
            clearTimeout(mb003.timeObj);
            mb003.timeObj = null;
        }
    },
    /**
     * 更新刷新频率
     * @param obj
     */
    updateFlash: function (obj) {
        var checkBox = $(obj).parent().find("input[name=autoFlash]");
        if ($(obj).attr("checked")) {
            clearTimeout(mb003.timeObj);
            mb003.timeObj = null;
            mb003.changePicData();
        }
    },
    /**
     * 点击项目区域节点
     * @param regId
     */
    entityClickFunc: function (regId) {
        var url = getGlobalPathRoot() + "eland/mb/mb003/mb003-regSummary.gv";
        var packet = new AJAXPacket(url);
        var conDiv = $('#mb003ChartDiv').parent();
        var picType = $("select[name=mb003Tjwd]", conDiv).val();
        var prjOrgId = $("input[name=prjOrgId]", conDiv).val();

        // 查询参数
        packet.data.data = {"prjCd": getPrjCd(), "regId": regId, "picType": picType, "prjOrgId": prjOrgId};
        // 不显示背景
        packet.noCover = true;
        // 请求获取销售品销售统计报文
        core.ajax.sendPacketHtml(packet, function (response) {
            var data = eval("(" + response + ")");
            var isSuccess = data.success;
            if (isSuccess) {
                var myChart = echarts.init($('#mb003ChartDiv')[0]);
                var charts = {
                    tooltip: {
                        trigger: 'axis'
                    },
                    legend: {
                        data: data.group
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
                            data: data.categories
                        }
                    ],
                    yAxis: [
                        {
                            type: 'value',
                            splitArea: {show: true}
                        }
                    ],
                    series: eval("(" + data.series + ")")
                };
                myChart.setOption(charts);
                var divObj = $("#mb003ChartDiv").parent();
                var autoFlashInt = $(divObj).find("input[name=autoFlashInt]").val();
                if (!autoFlashInt || autoFlashInt == "" || isNaN(autoFlashInt)) {
                    $(divObj).find("input[name=autoFlashInt]").val(60);
                    autoFlashInt = 60000;
                } else {
                    autoFlashInt = autoFlashInt * 1000;
                }
                var checkBox = $(divObj).find("input[name=autoFlash]");
                if ($(checkBox).attr("checked")) {
                    clearTimeout(mb003.timeObj);
                    mb003.timeObj = setTimeout(function () {
                        mb003.changePicData();
                    }, autoFlashInt);
                }
            } else {
                alertMsg.error(data.errMsg);
            }
        }, true);
    },
    changePicData: function () {
        var regId = $("input[name=regId]").val();
        mb003.entityClickFunc(regId);
    }

}
;
$(function () {
    // 刷新显示图片
    setTimeout(mb003.changePicData, 100);
});

