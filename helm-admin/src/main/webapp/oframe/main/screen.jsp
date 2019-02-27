<%--个人首页--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${portalRht.portalRht.rhtName}</title>
    <link href="${pageContext.request.contextPath}/oframe/plugin/ztree/zTreeStyle/zTreeStyle.css" rel="stylesheet"
          type="text/css" media="screen"/>
    <style type="text/css">
        * {
            margin: 0;
            padding: 0;
        }

        a.settingIcon {
            background: url('${pageContext.request.contextPath}/oframe/themes/images/iconfont-shezhi.png');
            -moz-top: 40px;
            right: 20px;
            top: 9px;
            position: absolute;
            z-index: 1;
            width: 21px;
            height: 21px;
        }

        a.goUp {
            background: url('${pageContext.request.contextPath}/oframe/themes/images/iconfont-xiangshang.png');
            -moz-top: 40px;
            right: 80px;
            top: 9px;
            position: absolute;
            z-index: 1;
            width: 21px;
            height: 21px;
        }

        a.org {
            background: url('${pageContext.request.contextPath}/oframe/themes/images/iconfont-zuzhi.png');
            -moz-top: 40px;
            right: 50px;
            top: 9px;
            position: absolute;
            z-index: 1;
            width: 21px;
            height: 21px;
        }

        a.reg {
            background: url('${pageContext.request.contextPath}/oframe/themes/images/iconfont-reg.png');
            -moz-top: 40px;
            right: 80px;
            top: 9px;
            position: absolute;
            z-index: 1;
            width: 21px;
            height: 21px;
        }

        .hidden {
            display: none;
        }
    </style>
    <%--ECharts 图表展示--%>
    <script src="${pageContext.request.contextPath}/oframe/plugin/jquery/jquery-1.7.2.min.js"
            type="text/javascript"></script>
    <%--树形引用结构--%>
    <script src="${pageContext.request.contextPath}/oframe/plugin/ztree/js/jquery.ztree.all-3.5.js"
            type="text/javascript"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/oframe/plugin/echarts/echarts.js"></script>
    <!--自定义引入js-->
    <script id="systemScript" contextPath="${pageContext.request.contextPath}/"
            src="${pageContext.request.contextPath}/oframe/plugin/system/system.min.js"
            type="text/javascript"></script>
</head>
<body style="overflow: auto;">
<div id="myWorkBench">
    <c:set var="showLogo" value="display:none;"/>
    <c:if test="${param.showLogo == 'true'}">
        <c:set var="showLogo" value=""/>
    </c:if>
    <div style="position: absolute;top:5px; left: 5px;overflow: hidden;width: 15%;max-width:260px;border: 0px;${showLogo}">
        <img src="${pageContext.request.contextPath}/oframe/sysmg/sys001/images/main-logo.png" usemap="#planetmap"
             style="border: 0px;width: 100%;" alt="Planets"/>
    </div>
    <input type="hidden" id="sysPrjCd" name="prjCd" value="${param.prjCd}"/>

    <div class="setting" style="float: right;">
        <a class="settingIcon hidden" href="javascript:void(0);"></a>
        <c:set var="mbType" value="${portalRht.portalRht.opMb}${''}"/>
        <c:if test="${(mbType == 'mb008-initOrg' || mbType == 'mb005') && (canChangeOrg == null || canChangeOrg !='false')}">
            <a class="org" title="管理组织" onclick="screenPic.sltOrg()" href="javascript:void(0);"></a>
        </c:if>
        <c:if test="${(mbType == 'mb008-initReg' || mbType =='mb005') && (canChangeReg == null || canChangeReg !='false')}">
            <a class="reg" title="管理区域" onclick="screenPic.sltReg('${regUseType}')" href="javascript:void(0);"></a>
        </c:if>
        <a class="goUp hidden" title="返回上级" href="javascript:void(0);"></a>

        <div class="setFalsh"
             style="margin-top: 37px;margin-left: -185px;position: absolute;z-index: 1;white-space:nowrap;">
            <input onchange="screenPic.updateFlash(this);"
                   style="width: 30px;"
                   onkeydown="if(event.keyCode == 13){screenPic.updateFlash(this);stopEvent(event);}"
                   type="text" class="digits" name="autoFlashInt" value="5">
            <input type="checkbox" name="autoFlash" value="0"
                   onclick="screenPic.flashChanged(this);"><span>自动刷新</span>
            <button class="sure" style="border-radius: 5px;border: 0;font-family: cursive">隐藏</button>
        </div>
    </div>
    <div class="prjJobData" style="background-color: #ffffff;box-sizing: border-box;">
        <span class="js_no_data"
              style="line-height: 300px;font-weight: bold;display: block;width: 100%;text-align: center;">加载中……</span>
    </div>
    <div id="openTip" class="hidden"
         style="z-index: 1005; right: 50px; top: 30px; width: 250px; height: 230px; overflow: auto;position: absolute;border: 1px solid #93c7ea;background: #eef4f5;">
        <div style="position: absolute; width: 100%; height: 26px; line-height: 26px; top: 0; left: 0;background-color: #93c7ea;">
                <span sty onclick='$("#openTip").hide();'
                      style="display: block; float: right; margin:0px 10px 5px 0px; cursor: pointer;">关闭</span>
        </div>
        <div style="width:100%; height:100%; overflow:auto;">
            <ul id="orgTreeContainer" class="ztree hidden" style="padding-top: 30px;"></ul>
            <ul id="regTreeContainer" class="ztree hidden" style="padding-top: 30px;"></ul>
        </div>
    </div>
</div>
</body>
<script>
    // 没有引入系统框架，需要重新alertMsg弹出对话框
    var alertMsg = {
        error: function (msg, options) {
        },
        info: function (msg, options) {
        },
        warn: function (msg, options) {
        },
        correct: function (msg, options) {
        }
    };
    var PicVar = {
        barMaxWidth: 50,
        barMinHeight: 10,
        fontSize: 11,
        titleFontSize: 20,
        legendFontSize: 5,
        titlePadding: 10,
        xMar: 20,
        lX: 20,
        rX: 20,
        tY: 30,
        bY: 30,
        color: [
            '#da70d6', '#32cd32', '#ff7f50', '#87cefa', '#6495ed',
            '#ff69b4', '#ba55d3', '#cd5c5c', '#ffa500', '#40e0d0',
            '#1e90ff', '#ff6347', '#7b68ee', '#00fa9a', '#ffd700',
            '#6b8e23', '#ff00ff', '#3cb371', '#b8860b', '#30e0e0'
        ]
    };
    var screenPic = {
        project: null,
        timeObj: null,
        orgIdPath: "",
        regIdPath: "",
        showPicData: function () {
            var mbType = '${mbType}';
            if (mbType == 'mb008-initOrg') {
                screenPic.showOrgPic();
            } else if (mbType == 'mb008-initReg') {
                screenPic.showRegPic();
            } else {
                screenPic.showNormalPic();
            }
        },

        /**
         * 按照分指的维度展示数据
         **/
        showOrgPic: function () {
            /*按照管理组织显示数据*/
            var oldUrl = "${portalRht.portalRht.navUrl}";
            var oldUrlParma = oldUrl.substring(oldUrl.indexOf("?"));
            // 是否显示比率
            var showRatio = "true";
            if (oldUrlParma.indexOf("&showRatio=false") >= 0) {
                showRatio = "false";
            }
            var url = getGlobalPathRoot() + "eland/mb/mb008/mb008-orgSummary.gv" + oldUrlParma;
            var packet = new AJAXPacket(url);
            packet.data.add("prjCd", getPrjCd());
            packet.data.add("orgIdPath", screenPic.orgIdPath);
            // 不显示背景
            packet.noCover = true;
            // 网络超时等问题
            packet.errorFunction = function (data) {
                var divObj = $(".setting");
                var autoFlashInt = $(divObj).find("input[name=autoFlashInt]").val();
                if (!autoFlashInt || autoFlashInt == "" || isNaN(autoFlashInt)) {
                    $(divObj).find("input[name=autoFlashInt]").val(60);
                    autoFlashInt = 60000;
                } else {
                    autoFlashInt = autoFlashInt * 1000;
                }
                // 设置图标
                $(".setting .settingIcon").removeClass("hidden");
                if (screenPic.orgIdPath != "") {
                    $(".setting .goUp").removeClass("hidden");
                } else {
                    $(".setting .goUp").addClass("hidden");
                }
                var checkBox = $(divObj).find("input[name=autoFlash]");
                if ($(checkBox).attr("checked")) {
                    clearTimeout(screenPic.timeObj);
                    screenPic.timeObj = setTimeout(function () {
                        screenPic.changePicData();
                    }, autoFlashInt);
                }
            };

            // 获取报文返回结果
            core.ajax.sendPacketHtml(packet, function (response) {
                var data = eval("(" + response + ")");
                var isSuccess = data.success;
                if (isSuccess) {
                    // 显示区域
                    var chartDiv = $("div.prjJobData", document);
                    // 返回数据
                    var resultData = data.data;
                    var legend = data.legend;
                    var xAxis = data.xAxis;
                    var titleText = data.titleText;
                    var orgName = xAxis.orgName;
                    var titleText = data.titleText;
                    if (orgName != "") {
                        titleText = "【" + orgName + "】" + titleText;
                    }
                    var series = null;
                    if (!resultData || !resultData.series) {
                        series = [];
                    } else {
                        series = resultData.series.series;
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
                        tempItem.barMaxWidth = PicVar.barMaxWidth;
                        tempItem.barMinHeight = PicVar.barMinHeight;
                        tempItem.itemStyle = {
                            normal: {
                                label: {
                                    show: true,
                                    position: 'inside',
                                    textStyle: {"fontSize": PicVar.fontSize, "color": "${fontColor}"}
                                }
                            }
                        };
                        tempItem.data = eval("(" + tempItem.data + ")");
                    }
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
                                    textStyle: {"fontSize": PicVar.fontSize, "color": "${fontColor}"},
                                    formatter: '{c}%'
                                }
                            }
                        };
                        series[0].markPoint.data = markPointData;
                    }
                    var groupTitle = "${title}";
                    if (groupTitle == "") {
                        groupTitle = titleText;
                    }
                    // 横坐标文本标记旋转角度
                    var rotate = 0;
                    if (legend.legend.length > 12) {
                        rotate = 45;
                    }
                    // 生成图表数据
                    var charts = {
                        // 扩展数据增加项目组织编号
                        orgIds: xAxis.orgIds,
                        orgIdPath: data.orgIdPath,
                        title: {
                            x: "center", y: "top",
                            text: groupTitle,
                            padding: PicVar.titlePadding,
                            textStyle: {"fontSize": PicVar.titleFontSize,"color": "${fontColor}"}
                        },
                        color: PicVar.color,
                        animation: true,
                        animationDuration: 1000,
                        legend: {
                            x: 'left',
                            y: 'bottom',
                            orient: 'vertical',
                            data: data.xAxis.xAxis,
                            textStyle: {"fontSize": PicVar.legendFontSize,"color": "${fontColor}"}
                        },
                        toolbox: {
                            show: true,
                            feature: {
                                dataZoom: {
                                    show: false
                                },
                                mark: {show: false},
                                dataView: {
                                    readOnly: true,
                                    show: false
                                },
                                magicType: {
                                    type: ['tiled', "stack"],
                                    show: false
                                },
                                restore: {
                                    show: false
                                },
                                saveAsImage: {
                                    show: false
                                }
                            }
                        },
                        xAxis: [
                            {
                                type: "category",
                                data: legend.legend,
                                nameLocation: "end",
                                y: "70%",
                                axisLabel: {
                                    interval: 'auto', rotate: rotate, margin: PicVar.xMar,
                                    textStyle: {
                                        "fontSize": PicVar.fontSize,
                                        "color": "${fontColor}",
                                        baseline: "middle",
                                        align: "center"
                                    }
                                }
                            }
                        ],
                        yAxis: [
                            {
                                type: "value",
                                splitArea: {
                                    show: false
                                },
                                nameLocation: "end",
                                axisLabel: {
                                    textStyle: {
                                        fontSize: PicVar.fontSize,
                                        "color": "${fontColor}"
                                    }
                                }
                            }
                        ],
                        grid: {
                            x: PicVar.lX,
                            x2: PicVar.rX,
                            y: PicVar.tY,
                            y2: PicVar.bY
                        },
                        series: series,
                        calculable: true,
                        tooltip: {
                            trigger: "axis",
                            axisPointer: {
                                type: "line"
                            }
                        },
                        color: [${colorStr}]
                    };
                    if (series.length == 0) {
                        chartDiv.find("span.js_no_data").html(groupTitle + "无数据");
                    } else {
                        var myChart = echarts.init(chartDiv[0]);
                        myChart.setOption(charts);
                        // 绑定双击事件
                        myChart.on("dblclick", function (param) {
                            if (typeof param.dataIndex != 'undefined') {
                                var dataIndex = param.dataIndex;
                                var options = myChart.getOption();
                                var orgId = options.orgIds[dataIndex];
                                screenPic.orgIdPath = options.orgIdPath + "/" + orgId;
                                screenPic.showPicData();
                            }
                        });
                    }
                    $(".echarts-dataview").css("display", "none");
                }
                var divObj = $(".setting");
                var autoFlashInt = $(divObj).find("input[name=autoFlashInt]").val();
                if (!autoFlashInt || autoFlashInt == "" || isNaN(autoFlashInt)) {
                    $(divObj).find("input[name=autoFlashInt]").val(60);
                    autoFlashInt = 60000;
                } else {
                    autoFlashInt = autoFlashInt * 1000;
                }
                // 设置图标
                $(".setting .settingIcon").removeClass("hidden");
                if (screenPic.orgIdPath != "") {
                    $(".setting .goUp").removeClass("hidden");
                } else {
                    $(".setting .goUp").addClass("hidden");
                }
                var checkBox = $(divObj).find("input[name=autoFlash]");
                if ($(checkBox).attr("checked")) {
                    clearTimeout(screenPic.timeObj);
                    screenPic.timeObj = setTimeout(function () {
                        screenPic.changePicData();
                    }, autoFlashInt);
                }
            });
        },

        /**
         * 按照分指的维度展示数据
         **/
        showRegPic: function () {
            /*按照管理组织显示数据*/
            var oldUrl = "${portalRht.portalRht.navUrl}";
            var oldUrlParma = oldUrl.substring(oldUrl.indexOf("?"));
            // 是否显示比率
            var showRatio = "true";
            if (oldUrlParma.indexOf("&showRatio=false") >= 0) {
                showRatio = "false";
            }
            var url = getGlobalPathRoot() + "eland/mb/mb008/mb008-regSummary.gv" + oldUrlParma;
            var packet = new AJAXPacket(url);
            packet.data.add("prjCd", getPrjCd());
            packet.data.add("regIdPath", screenPic.regIdPath);
            // 不显示背景
            packet.noCover = true;
            // 网络超时等问题
            packet.errorFunction = function (data) {
                var divObj = $(".setting");
                var autoFlashInt = $(divObj).find("input[name=autoFlashInt]").val();
                if (!autoFlashInt || autoFlashInt == "" || isNaN(autoFlashInt)) {
                    $(divObj).find("input[name=autoFlashInt]").val(60);
                    autoFlashInt = 60000;
                } else {
                    autoFlashInt = autoFlashInt * 1000;
                }
                // 设置图标
                $(".setting .settingIcon").removeClass("hidden");
                if (screenPic.regIdPath != "") {
                    $(".setting .goUp").removeClass("hidden");
                } else {
                    $(".setting .goUp").addClass("hidden");
                }
                var checkBox = $(divObj).find("input[name=autoFlash]");
                if ($(checkBox).attr("checked")) {
                    clearTimeout(screenPic.timeObj);
                    screenPic.timeObj = setTimeout(function () {
                        screenPic.changePicData();
                    }, autoFlashInt);
                }
            };
            // 获取报文返回结果
            core.ajax.sendPacketHtml(packet, function (response) {
                var data = eval("(" + response + ")");
                var isSuccess = data.success;
                if (isSuccess) {
                    // 显示区域
                    var chartDiv = $("div.prjJobData", document);
                    // 返回数据
                    var resultData = data.data;
                    var legend = data.legend;
                    var xAxis = data.xAxis;
                    var titleText = data.titleText;
                    var regName = xAxis.regName;
                    var titleText = data.titleText;
                    if (regName != "") {
                        titleText = "【" + regName + "】" + titleText;
                    }
                    var series = null;
                    if (!resultData || !resultData.series) {
                        series = [];
                    } else {
                        series = resultData.series.series;
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
                        tempItem.barMaxWidth = 60;
                        tempItem.barMinHeight = 20;
                        tempItem.itemStyle = {
                            normal: {
                                label: {
                                    show: true,
                                    position: 'inside',
                                    textStyle: {"fontSize": PicVar.fontSize, "color": "${fontColor}"}
                                }
                            }
                        };
                        tempItem.data = eval("(" + tempItem.data + ")");
                    }
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
                                    textStyle: {"fontSize": PicVar.fontSize, "color": "${fontColor}"},
                                    formatter: '{c}%'
                                }
                            }
                        };
                        series[0].markPoint.data = markPointData;
                    }
                    var groupTitle = "${title}";
                    if (groupTitle == "") {
                        groupTitle = titleText;
                    }
                    // 横坐标文本标记旋转角度
                    var rotate = 0;
                    if (legend.legend.length > 12) {
                        rotate = 45;
                    }
                    // 是否显示缩放标尺
                    var showDataZoom = false;
                    var showStart = 0;
                    var showEnd = 100;
                    if (legend.legend.length > 20) {
                        showDataZoom = true;
                        showStart = parseInt((legend.legend.length - 20) / legend.legend.length * 100);
                    }
                    // 生成图表数据
                    var charts = {
                        // 扩展数据增加项目组织编号
                        regIds: xAxis.regIds,
                        regIdPath: data.regIdPath,
                        title: {
                            x: "center", y: "top",
                            text: groupTitle,
                            padding: PicVar.titlePadding,
                            textStyle: {"fontSize": PicVar.titleFontSize,"color": "${fontColor}"}
                        },
                        animation: true,
                        animationDuration: 1000,
                        legend: {
                            x: 'left',
                            y: 'bottom',
                            orient: 'vertical',
                            data: data.xAxis.xAxis,
                            textStyle: {"fontSize": PicVar.legendFontSize,"color": "${fontColor}"}
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
                                    show: false
                                },
                                mark: {show: false},
                                dataView: {
                                    readOnly: true,
                                    show: false
                                },
                                magicType: {
                                    type: ['tiled', "stack"],
                                    show: false
                                },
                                restore: {
                                    show: false
                                },
                                saveAsImage: {
                                    show: false
                                }
                            }
                        },
                        xAxis: [
                            {
                                type: "category",
                                data: legend.legend,
                                nameLocation: "end",
                                y: "70%",
                                axisLabel: {
                                    interval: 'auto', rotate: rotate, margin: PicVar.xMar,
                                    textStyle: {
                                        "fontSize": PicVar.fontSize,
                                        "color": "${fontColor}",
                                        baseline: "middle",
                                        align: "center"
                                    }
                                }
                            }
                        ],
                        yAxis: [
                            {
                                type: "value",
                                splitArea: {
                                    show: false
                                },
                                nameLocation: "end",
                                axisLabel: {
                                    textStyle: {
                                        fontSize: PicVar.fontSize,
                                        "color": "${fontColor}"
                                    }
                                }
                            }
                        ],
                        grid: {
                            x: PicVar.lX,
                            x2: PicVar.rX,
                            y: PicVar.tY,
                            y2: PicVar.bY
                        },
                        series: series,
                        calculable: true,
                        tooltip: {
                            trigger: "axis",
                            axisPointer: {
                                type: "line"
                            }
                        },
                        color: [${colorStr}]
                    };
                    if (series.length == 0) {
                        chartDiv.find("span.js_no_data").html(groupTitle + "无数据");
                    } else {
                        var myChart = echarts.init(chartDiv[0]);
                        myChart.setOption(charts);
                        // 绑定双击事件
                        myChart.on("dblclick", function (param) {
                            if (typeof param.dataIndex != 'undefined') {
                                var dataIndex = param.dataIndex;
                                var options = myChart.getOption();
                                var regId = options.regIds[dataIndex];
                                screenPic.regIdPath = options.regIdPath + "/" + regId;
                                screenPic.showPicData();
                            }
                        });
                    }
                    $(".echarts-dataview").css("display", "none");
                }
                var divObj = $(".setting");
                var autoFlashInt = $(divObj).find("input[name=autoFlashInt]").val();
                if (!autoFlashInt || autoFlashInt == "" || isNaN(autoFlashInt)) {
                    $(divObj).find("input[name=autoFlashInt]").val(60);
                    autoFlashInt = 60000;
                } else {
                    autoFlashInt = autoFlashInt * 1000;
                }
                // 设置图标
                $(".setting .settingIcon").removeClass("hidden");
                if (screenPic.regIdPath != "") {
                    $(".setting .goUp").removeClass("hidden");
                } else {
                    $(".setting .goUp").addClass("hidden");
                }
                var checkBox = $(divObj).find("input[name=autoFlash]");
                if ($(checkBox).attr("checked")) {
                    clearTimeout(screenPic.timeObj);
                    screenPic.timeObj = setTimeout(function () {
                        screenPic.changePicData();
                    }, autoFlashInt);
                }
            });
        },

        /**
         * 按照SQL输出正常维度展示输出数据
         **/
        showNormalPic: function () {
            /* 按照自定义结构显示数据 */
            var oldUrl = "${portalRht.portalRht.navUrl}";
            var oldUrlParma = oldUrl.substring(oldUrl.indexOf("?"));
            var showRatio = "true";
            if (oldUrlParma.indexOf("&showRatio=false") >= 0) {
                showRatio = "false";
            }
            var url = getGlobalPathRoot() + "eland/rp/rp000/rp000-dataSummary.gv" + oldUrlParma;
            var packet = new AJAXPacket(url);
            packet.data.add("prjCd", getPrjCd());
            packet.data.add("orgIdPath", screenPic.orgIdPath);
            packet.data.add("regIdPath", screenPic.regIdPath);
            // 不显示背景
            packet.noCover = true;
            // 网络超时等问题
            packet.errorFunction = function (data) {
                var divObj = $(".setting");
                var autoFlashInt = $(divObj).find("input[name=autoFlashInt]").val();
                if (!autoFlashInt || autoFlashInt == "" || isNaN(autoFlashInt)) {
                    $(divObj).find("input[name=autoFlashInt]").val(60);
                    autoFlashInt = 60000;
                } else {
                    autoFlashInt = autoFlashInt * 1000;
                }
                // 设置图标
                $(".setting .settingIcon").removeClass("hidden");
                var checkBox = $(divObj).find("input[name=autoFlash]");
                if ($(checkBox).attr("checked")) {
                    clearTimeout(screenPic.timeObj);
                    screenPic.timeObj = setTimeout(function () {
                        screenPic.changePicData();
                    }, autoFlashInt);
                }
            };
            // 请求获取统计数据
            core.ajax.sendPacketHtml(packet, function (response) {
                // 显示区域
                var chartDiv = $("div.prjJobData", document);
                // 返回数据
                var data = eval("(" + response + ")");
                var isSuccess = data.success;
                if (isSuccess) {
                    var reportData = data.data.Report;
                    if (!reportData) {
                        return;
                    }
                    var series = reportData.series;
                    var legendData = [];
                    var chartType = '${chartType}';
                    if (chartType == "") {
                        chartType = "bar";
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
                        tempItem.barMaxWidth = PicVar.barMaxWidth;
                        tempItem.barMinHeight = PicVar.barMinHeight;
                        tempItem.itemStyle = {
                            normal: {
                                label: {
                                    show: true,
                                    position: 'inside',
                                    textStyle: {"fontSize": PicVar.fontSize, "color": "${fontColor}"}
                                }
                            }
                        };
                        legendData.push(tempItem.name);
                    }
                    var groupTitle = "${title}";
                    if (groupTitle == "") {
                        groupTitle = legendData;
                    }
                    if (reportData.showName && reportData.showName != "") {
                        groupTitle = "【" + reportData.showName + "】" + groupTitle;
                    }
                    var showLegend = false;
                    if (series.length > 1) {
                        showLegend = true;
                    }

                    var charts;
                    //判断是否是饼图
                    if (chartType == 'pie') {
                        $(".setting .settingIcon").css("right", "20px");
                        $(".setting .settingIcon").removeClass("hidden");
                        $(".setting .org").css("right", "50px");
                        $("#openTip").css("right", "20px");
                        var pieData = [];
                        var seriesData = reportData.series[0].data;
                        var xAxisData = reportData.xAxis;
                        for (var i = 0; i < seriesData.length; i++) {
                            pieData.push({
                                name: xAxisData[i],
                                value: seriesData[i]
                            });
                        }
                        charts = {
                            title: {text: groupTitle, x: 'center', y: 'top', textStyle: {"fontSize": PicVar.titleFontSize,"color": "${fontColor}"}},
                            color: PicVar.color,
                            tooltip: {trigger: 'item', formatter: "{a} <br/>{b} : {c} ({d}%)"},
                            animation: true,
                            animationDuration: 1000,
                            legend: {
                                x: 'left',
                                y: 'bottom',
                                orient: 'vertical',
                                data: xAxisData,
                                textStyle: {"fontSize": PicVar.legendFontSize,"color": "${fontColor}"}
                            },
                            toolbox: {
                                show: true,
                                feature: {
                                    mark: {show: false},
                                    dataView: {show: false, readOnly: true},
                                    saveAsImage: {show: false},
                                    restore: {show: false}
                                }
                            },
                            calculable: true,
                            series: [
                                {
                                    name: groupTitle,
                                    type: chartType,
                                    radius: '55%',
                                    center: ['50%', '50%'],
                                    itemStyle: {
                                        normal: {
                                            label: {
                                                show: true,
                                                formatter: "{b}:{c}({d}%)",
                                                textStyle: {"fontSize": PicVar.fontSize}
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
                                        textStyle: {"fontSize": PicVar.fontSize, "color": "${fontColor}"},
                                        formatter: '{c}%'
                                    }
                                }
                            };
                            series[0].markPoint.data = markPointData;
                        }

                        // 横坐标文本标记旋转角度
                        var rotate = 0;
                        if (legendData.length > 12) {
                            rotate = 45;
                        }
                        // 是否显示缩放标尺
                        var showDataZoom = false;
                        var showStart = 0;
                        var showEnd = 100;
                        if (reportData.xAxis.length > 20) {
                            showDataZoom = true;
                            showStart = parseInt((reportData.xAxis.length - 20) / reportData.xAxis.length * 100);
                        }
                        charts = {
                            title: {text: groupTitle, x: 'center', y: 'top', textStyle: {"fontSize": PicVar.titleFontSize,"color": "${fontColor}"}},
                            color: PicVar.color,
                            tooltip: {
                                trigger: 'axis'
                            },
                            legend: {
                                show: showLegend,
                                y: 'bottom',
                                data: legendData,
                                textStyle: {"fontSize": PicVar.legendFontSize,"color": "${fontColor}"}
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
                                        show: false,
                                        readOnly: true
                                    },
                                    magicType: {show: false, type: ['line', 'bar']},
                                    restore: {show: false},
                                    saveAsImage: {show: false}
                                }
                            },
                            calculable: true,
                            xAxis: [
                                {
                                    type: 'category',
                                    data: reportData.xAxis,
                                    nameLocation: "end",
                                    y: "70%",
                                    axisLabel: {
                                        interval: 'auto', rotate: rotate, margin: PicVar.xMar,
                                        textStyle: {
                                            "fontSize": PicVar.fontSize,
                                            "color": "${fontColor}",
                                            baseline: "middle",
                                            align: "center"
                                        }
                                    }
                                }
                            ],
                            grid: {
                                x: PicVar.lX,
                                x2: PicVar.rX,
                                y: PicVar.tY,
                                y2: PicVar.bY
                            },
                            yAxis: [
                                {
                                    type: 'value',
                                    splitArea: {show: true},
                                    axisLabel: {
                                        textStyle: {
                                            fontSize: PicVar.fontSize,  //刻度大小
                                            "color": "${fontColor}"
                                        }
                                    }
                                }
                            ],
                            series: series,
                            color: [${colorStr}]
                        };
                    }
                    if (series.length == 0) {
                        chartDiv.find("span.js_no_data").html(groupTitle + "无数据");
                    } else {
                        // 最大值最小值
                        if (reportData.minValue != "") {
                            charts.yAxis[0].min = reportData.minValue;
                        }
                        if (reportData.maxValue != "") {
                            charts.yAxis[0].max = reportData.maxValue;
                        }
                        var myChart = echarts.init(chartDiv[0]);
                        myChart.setOption(charts);
                        // 绑定双击事件
                        myChart.on("dblclick", function (param) {
                            if (typeof param.dataIndex != 'undefined') {
                                var dataIndex = param.dataIndex;
                                var options = myChart.getOption();
                                if (options.orgIds) {
                                    var orgId = options.orgIds[dataIndex];
                                    screenPic.orgIdPath = options.orgIdPath + "/" + orgId;
                                    screenPic.showPicData();
                                }
                            }
                        });
                    }
                    $(".echarts-dataview").css("display", "none");
                }
                var divObj = $(".setting");
                var autoFlashInt = $(divObj).find("input[name=autoFlashInt]").val();
                if (!autoFlashInt || autoFlashInt == "" || isNaN(autoFlashInt)) {
                    $(divObj).find("input[name=autoFlashInt]").val(60);
                    autoFlashInt = 60000;
                } else {
                    autoFlashInt = autoFlashInt * 1000;
                }
                // 设置图标
                $(".setting .settingIcon").removeClass("hidden");
                var checkBox = $(divObj).find("input[name=autoFlash]");
                if ($(checkBox).attr("checked")) {
                    clearTimeout(screenPic.timeObj);
                    screenPic.timeObj = setTimeout(function () {
                        screenPic.changePicData();
                    }, autoFlashInt);
                }
            })
        },

        /**
         * 切换选择管理组织
         */
        sltOrg: function () {
            $("#openTip").show();
            $("#orgTreeContainer").removeClass("hidden");
            $("#regTreeContainer").addClass("hidden");
            if ($("#orgTreeContainer").html() == "") {
                // 重新加载
                var packet = new AJAXPacket();
                packet.data.add("prjCd", getPrjCd());
                packet.url = getGlobalPathRoot() + "oframe/sysmg/sys004/sys004-treeInfo.gv";
                core.ajax.sendPacketHtml(packet, function (response) {
                            var jsonData = eval("(" + response + ")");
                            var treeJson = jsonData.resultMap.treeJson;
                            // 服务调用是否成功
                            var isSuccess = jsonData.success;
                            if (isSuccess) {
                                // 左侧树的生成和回调
                                var pjOrgTreeSetting = {
                                    data: {
                                        simpleData: {
                                            enable: true
                                        }
                                    },
                                    callback: {
                                        onClick: function (event, treeId, treeNode) {
                                            var orgIdPath = treeNode.id;
                                            var parentNode = treeNode.getParentNode();
                                            while (parentNode != null) {
                                                orgIdPath = parentNode.id + "/" + orgIdPath;
                                                parentNode = parentNode.getParentNode();
                                            }
                                            screenPic.orgIdPath = orgIdPath;
                                            screenPic.changePicData();
                                        },
                                        onCheck: false
                                    }
                                };
                                // 初始化目录树
                                var zTree = $.fn.zTree.init($("#orgTreeContainer"), pjOrgTreeSetting, treeJson);
                            }
                            else {
                                alertMsg.warn(jsonData.data.errMsg);
                            }
                        }, true, false
                )
            }
        },

        /**
         * 切换选择管理组织
         */
        sltReg: function (regUseType) {
            $("#openTip").show();
            $("#orgTreeContainer").addClass("hidden");
            $("#regTreeContainer").removeClass("hidden");
            if ($("#regTreeContainer").html() == "") {
                if (regUseType == "") {
                    regUseType = "1";
                }
                // 重新加载
                var packet = new AJAXPacket();
                var prjCd = getPrjCd();
                packet.data.add("prjCd", prjCd);
                packet.data.add("treeType", regUseType);
                packet.url = getGlobalPathRoot() + "eland/pj/pj004/pj004-treeInfo.gv";
                core.ajax.sendPacketHtml(packet, function (response) {
                            var jsonData = eval("(" + response + ")");
                            var treeJson = jsonData.resultMap.treeJson;
                            // 服务调用是否成功
                            var isSuccess = jsonData.success;
                            if (isSuccess) {
                                // 目录树数据
                                // 左侧树的生成和回调
                                var pjOrgTreeSetting = {
                                    data: {
                                        simpleData: {
                                            enable: true
                                        }
                                    },
                                    callback: {
                                        onClick: function (event, treeId, treeNode) {
                                            var regIdPath = treeNode.id;
                                            var parentNode = treeNode.getParentNode();
                                            while (parentNode != null) {
                                                regIdPath = parentNode.id + "/" + regIdPath;
                                                parentNode = parentNode.getParentNode();
                                            }
                                            screenPic.regIdPath = regIdPath;
                                            screenPic.changePicData();
                                        },
                                        onCheck: false
                                    }
                                };
                                // 初始化目录树
                                var zTree = $.fn.zTree.init($("#regTreeContainer"), pjOrgTreeSetting, treeJson);
                            }
                            else {
                                alertMsg.warn(jsonData.errMsg);
                            }
                        }, true, false
                );
            }
        },

        /***
         * 修改刷新状态
         * @param obj
         */
        flashChanged: function (obj) {
            if ($(obj).attr("checked")) {
                screenPic.changePicData();
            } else if (screenPic.timeObj) {
                clearTimeout(screenPic.timeObj);
                screenPic.timeObj = null;
            }
        },

        updateFlash: function (obj) {
            var checkBox = $(obj).parent().find("input[name=autoFlash]");
            if ($(obj).attr("checked")) {
                clearTimeout(screenPic.timeObj);
                screenPic.timeObj = null;
                screenPic.changePicData();
            }
        },

        setting: function () {
            $(".setting .setFalsh").css('display', 'none');
            $(".setting .settingIcon").hover(function () {
                $(".setting .setFalsh").css('display', 'inline');
                $(".setting .sure").click(function () {
                    $(".setting .setFalsh").css('display', 'none');
                })
            })
        },

        goUp: function () {
            $(".setting .goUp").click(function () {
                if (screenPic.orgIdPath != "") {
                    var lastIdx = screenPic.orgIdPath.lastIndexOf("/");
                    var temp = screenPic.orgIdPath.substr(0, lastIdx);
                    screenPic.orgIdPath = temp;
                    screenPic.changePicData();
                } else if (screenPic.regIdPath != "") {
                    var lastIdx = screenPic.regIdPath.lastIndexOf("/");
                    var temp = screenPic.regIdPath.substr(0, lastIdx);
                    screenPic.regIdPath = temp;
                    screenPic.changePicData();
                }
            })
        },

        changePicData: function () {
            screenPic.showPicData();
        },

        settingDisplay: function () {
            if ($("a.org").length == 0) {
                $("a.reg").css("right", "50px");
            }
            var height = $(window).height();
            $("div.prjJobData").height(height);
            // 根据高度和宽度处理边距问题
            if (height > 900) {
                PicVar.lX = 80;
                PicVar.rX = 20;
                PicVar.tY = 90;
                PicVar.bY = 70;
                PicVar.xMar = 20;
                PicVar.titlePadding = 5;
                PicVar.fontSize = 25;
                PicVar.titleFontSize = 30;
                PicVar.legendFontSize = 11;
            } else if (height > 500) {
                PicVar.lX = 80;
                PicVar.rX = 20;
                PicVar.tY = 70;
                PicVar.bY = 50;
                PicVar.xMar = 20;
                PicVar.titlePadding = 5;
                PicVar.fontSize = 20;
                PicVar.titleFontSize = 30;
                PicVar.legendFontSize = 5;
            } else {
                PicVar.lX = 50;
                PicVar.rX = 10;
                PicVar.tY = 40;
                PicVar.bY = 30;
                PicVar.xMar = 10;
                PicVar.titlePadding = 5;
                PicVar.fontSize = 10;
                PicVar.titleFontSize = 15;
                PicVar.legendFontSize = 5;
            }
        }
    };
    $(function () {
        // 请求全屏
        screenPic.settingDisplay();
        // 谁知刷新
        screenPic.setting();
        // 谁知刷新
        screenPic.goUp();
        // 刷新显示图片
        setTimeout(screenPic.changePicData, 100);
        // 重新定义
        $(window).bind("resize", function () {
            // 请求全屏
            screenPic.settingDisplay();
            // 重新加载数据图
            screenPic.showPicData();
        });
    });
</script>
</html>
