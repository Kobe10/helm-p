<%--安置外迁房源--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<html>
<head>
    <title>房源销控展示</title>
    <style type="text/css">
        * {
            margin: 0;
            padding: 0;
            font-size: 14px;
            font-family: "Microsoft YaHei", Arial, SimSun;
        }

        #settingTable th {
            line-height: 25px;
            text-align: right;
            font-weight: normal;
        }

        #settingTable th.subTitle {
            background: #e6f2fa;
        }

        #settingTable th, #settingTable td {
            border: 1px solid #99bbe8;
            padding: 4px 0 4px 2px;
        }

        #settingTable th.subTitle {
            background: #e6f2fa;
        }

        #settingTable th.subTitle label {
            color: #3c90c8;
        }

        #settingDiv {
            position: absolute;
            width: 100%;
            background-color: white;
        }

        .textInput {
            line-height: 15px;
            padding: 2px;
            height: 30px;
            border: 1px solid;
            border-color: #c3c3c3;

        }

        #disTitle, #housShowTitle {
            font-weight: bolder;
            display: block;
            text-align: center;
            font-size: 40px;
            background-color: yellow;
        }

        #disData {
            vertical-align: middle;
            overflow-y: auto;
            position: relative;
        }

        /*建筑展示形式*/
        table.build {
            border-collapse: collapse;
            display: inline-block;
        }

        table.build td {
            padding: 4px 0px 4px 2px;
            height: 55px;
            text-align: center;
        }

        table.new_build td {
            height: 40px !important;
        }

        table.build th {
            padding: 4px 0px 4px 2px;
            height: 25px;
            text-align: center;
        }

        table.build th.floor, table.build td.floor {
            width: 40px;
        }

        table.build tr.data td {
            min-width: 100px;
            line-height: 25px;
        }

        table.new_build tr.data td {
            min-width: 30px !important;
        }

        table.build tr.data td:last-child {
            line-height: 25px;
        }

        table.build tr.data td:first-child {
            line-height: 25px;
        }

        table.build tr.data td label {
            display: block;
            height: 30px;
            min-width: 100px;
            font-size: 16px;
            font-weight: bolder;
            overflow: hidden;
            margin: 1px 5px 1px 5px;
        }

        table.new_build tr.data td label {
            height: 30px !important;
            display: table-cell;
            vertical-align: middle;
        }

        table.build tr.data td label:hover {
            cursor: pointer;
        }

        table.build {
            border-bottom: 1px solid #5fa4df;
        }

        table.build td, table.build th {
            border: 1px solid #5fa4df;
        }

        table.build th.unit {
            background: url("floor.png") no-repeat;
            background-size: 100% 100%;
            border: none;
            font-size: 20px;
            height: 30px;
            color: #fffc46;
        }

        table.build th.floor, table.build td.floor {
            background-color: #edf7ff;
        }

        table.build tr.data td {
            border-bottom: 1px solid #c9c9c9;
            border-top: none;
            border-left: none;
            border-right: none;
        }

        table.build tr.data td:last-child {
            border-bottom: 1px solid #c9c9c9;
            border-top: none;
            border-left: none;
            border-right: 1px solid #5fa4df !important;
        }

        table.build tr.data td:first-child {
            border-bottom: 1px solid #c9c9c9;
            border-top: none;
            border-left: 1px solid #5fa4df;
            border-right: none;
        }

        table.build tr.data td label {
            border: 1px solid #efefef;
            color: black;
        }

        table.build tr.data td label:hover {
            background-color: #c6bb81 !important;
        }

        table.build tr.data td label.used {
            background-color: #ffff00;
        }

        #new_showBox {
            position: fixed;
            top: 0px;
            left: 0px;
            width: 100%;
            height: 100%;
            display: none;
        }

        #housInfo {
            position: fixed;
            top: 0;
            left: 0;
            height: 136px;
            width: 100%;
        }

        #housShow {
            position: fixed;
            top: 136px;
            left: 0px;
            width: 100%;
            height: calc(100% - 130px);
            box-sizing: border-box;
            text-align: center;
            overflow-x: hidden;
            overflow-y: auto;
            z-index: 0;
            display: none;
        }

        #housShow::-webkit-scrollbar {
            width: 0;
        }

        /*------------分割线------------*/
        ul, li, ol {
            list-style: none;
            text-align: center;
            font-weight: bold;
        }

        .bc_build li, .bc_build ul {
            box-sizing: border-box;
            -moz-box-sizing: border-box;
            -webkit-box-sizing: border-box;
        }

        .bc_build .buildTitle {
            text-align: center;
        }

        .bc_build li.unit, #housInfo li.unit {
            background: url("floor.png") no-repeat;
            background-size: 100% 100%;
            border: none;
            font-size: 20px;
            height: 30px;
            color: #fffc46;
        }

        .build_floor > li.floor, .build_body > li li.floor {
            border-top: 1px solid #5fa4df;
            border-left: 1px solid #5fa4df;
            background-color: #edf7ff;
        }

        .build_body > li li.data {
            border-top: 1px solid #5fa4df;
            border-left: 1px solid #5fa4df;
        }

        .build_floor > li.floor:last-child {
            border-right: 1px solid #5fa4df;
        }

        .build_body {
            border-right: 1px solid #5fa4df;
            border-bottom: 1px solid #5fa4df;
        }

        .bc_build li.data:hover {
            background-color: #c6bb81 !important;
        }

    </style>
</head>
<body style="background:transparent;">
<input type="hidden" id="sysPrjCd" name="prjCd" value="${param.prjCd}"/>
<span onclick="$('#settingDiv').show();"
      style="display: block;position: fixed;width: 50px; height: 50px; z-index: 1000; right: 5px; top:0px;cursor: hand;">
</span>

<div style="position: relative;margin: 0 5px;">
    <div id="settingDiv">
        <oframe:select itemCd="NEW_HS_JUSH" name="newHsJs" style="display:none;"/>
        <table id="settingTable" width="100%" cellpadding="0" cellspacing="0">
            <thead>
            <tr>
                <th style="text-align: left;font-weight: bolder;" class="subTitle" colspan="2">
                    <label style="display: inline-block; margin-left: 5px;">滚动设置</label>
                </th>
            </tr>
            </thead>
            <tr>
                <th width="10%;">
                    房源类型：
                </th>
                <td>
                    <select name="hsClass" style="width: 80%">
                        <option value="3">外迁</option>
                        <option value="2">回迁</option>
                    </select>
                </td>
            </tr>
            <tr>
                <th width="10%;" title="指定数据按照哪个维度进行滚动播放">
                    滚动维度：
                </th>
                <td><select name="showType" class="textInput" style="width: 80%;" onchange="changeShowType()">
                    <option condition="甲1,甲2,甲3,甲4,甲5,乙1,乙2.1,乙2,乙3,乙4,乙5,乙6,乙7,乙8,乙9,乙10,乙11,乙12,乙13,乙14,乙15,乙16,乙17,乙18,乙19,乙20,乙21,乙22,乙23,乙24,乙25,乙26,乙27,乙28,乙29,丙21,乙30,乙31,乙32,乙33,乙34,乙35,丙1,丙2,丙3,丙4,丙5,丙6,丙7,丙8,丙9,丙10,丙11,丙12,丙13,丙14,丙15,丙16,丙17,丙18,丙19,丙20,丙22,丙23,丙24,丙25,丙26,丙27,丙28,丙29,丙30,丙31,丙32,丙33,丙34,丙35,丙36,丙37,丙38.1,丙38,丙39,丙40,丙41.1,丙41,丙42,丁1,丁2,丁3,丁4,丁5,丁6,丁7,丁8,丁9,丁10,丁11,丁12,丁13.1,丁13,丁14,丁15,丁16,丁17,丁18,丁19,丁20,丁21,丁22"
                            value="NewHsInfo.hsHxName">按户型滚动
                    </option>
                    <option condition="50-60,70-80,90-100" value="NewHsInfo.preBldSize">按面积区间</option>
                    <option condition="1,2,3" selected value="NewHsInfo.hsJush">按照居室</option>
                </select>
                </td>
            </tr>
            <tr>
                <th width="10%;">
                    滚动内容：
                </th>
                <td><textarea type="text" style="width: 80%;" rows="10"
                              name="showValue">1,2,3</textarea>
                    <span style="display: inline-block;padding-left: 5px;">()</span>
                </td>
            </tr>
            <tr>
                <th width="10%;"><label>滚动速度：</label></th>
                <td><input class="textInput" type="text" name="freshTime" value="5">
                    <span style="display: inline-block;padding-left: 5px;">(秒)</span>
                </td>
            </tr>
            <tr>
                <td colspan="2" align="center">
                    <button type="button" onclick="saveSetting();" style="padding: 2px 10px 2px 10px;">确定</button>
                </td>
            </tr>
        </table>
        <form id="oh005frm" method="post">
            <input type="hidden" name="conditionName" value="NewHsInfo.statusCd">
            <input type="hidden" name="condition" value="in">
            <input type="hidden" name="conditionValue" value="2|3">
            <input type="hidden" name="sortColumn" value="NewHsInfo.hsAddr">
            <input type="hidden" name="sortOrder" value="asc">
            <input type="hidden" class="js_conditionValue" value="">
            <input type="hidden" name="resultField"
                   value="NewHsInfo.hsAddr,NewHsInfo.hsHxName,NewHsInfo.preBldSize,NewHsInfo.statusCd">
            <input type="hidden" name="forceResultField" value="NewHsInfo.newHsId">
            <input type="hidden" name="regUseType" value="${regUseType}">
            <input type="hidden" name="rhtType" value="${regUseType}">
            <input type="hidden" name="rhtRegId" value="">
            <input type="hidden" name="rhtHxName" value="">
            <input type="hidden" name="rhtHsTp" value="">
            <input type="hidden" name="entityName" value="NewHsInfo">
            <input type="hidden" name="displayBuildPage" value="/eland/oh/oh005/oh005_build"/>
        </form>
    </div>
    <div id="disDiv">
        <span id="disTitle"></span>
        <div id="disData"></div>
    </div>
    <div id="new_showBox">
        <div id="housInfo">
            <span id="housShowTitle">一居(B区-B2栋号楼)</span>
        </div>
        <div id="housShow"></div>
    </div>
</div>
<script src="${pageContext.request.contextPath}/oframe/plugin/jquery/eland.jquery.min.js"
        type="text/javascript"></script>
<oframe:script src="${pageContext.request.contextPath}/oframe/plugin/system/system.min.js" type="text/javascript"/>
<script type="text/javascript">
    var alertMsg = {
        error: function (msg) {
            alert(msg);
        }
    }
    var currentIndex = 0;
    var showData = [];
    var showType = ""
    var freshTime = 3000;
    var freshTimer = null;
    var currentBuilding = null;
    var showTitle = null;
    var disPlaySize = null;
    var currentScroTop = 0;
    var builds = null;
    var currentBuildIdx = 0;

    function changeShowType() {
        var selectOption = $("select[name=showType]").find("option:selected").attr("condition");
        $("textarea[name=showValue]").val(selectOption);
    }

    function saveSetting() {
        var hsClass = $("select[name=hsClass]").val();
        $("input[name=regUseType]").val(hsClass);
        $("input[name=rhtType]").val(hsClass);
        var settingDiv = $("#settingDiv");
        showType = $("select[name=showType]").val();
        var showValue = $("textarea[name=showValue]").val();
        if (!CheckUtil.checkInteger($("input[name=freshTime]").val())) {
            alert("滚动速度必须是整数!");
            return;
        }
        freshTime = $("input[name=freshTime]").val() * 1000;
        showData = showValue.split(",");
        currentIndex = 0;
        if (freshTimer) {
            clearTimeout(freshTimer);
        }
        setDisSize();
        nextDataFunction();
        settingDiv.hide();
    }

    function setDisSize() {
        disPlaySize = ($(window).height() - 80);
        $("#disData").css("height", disPlaySize + "px");
    }

    $(window).resize(function () {
        setDisSize();
    });

    function nextDataFunction() {
        currentBuilding = null;
        var form = $("#oh005frm");
        // 重新加载
        var packet = new AJAXPacket();
        packet.data.add("prjCd", getPrjCd());
        packet.data.add("regId", "");
        // 其他查询条件
        var conditionNames = $("input[name=conditionName]", form).val();
        var conditions = $("input[name=condition]", form).val();
        var conditionValue = $("input[name=conditionValue]", form).val();
        //获取当前显示的内容
        if (currentIndex >= showData.length) {
            currentIndex = 0;
        }
        var nowCondition = showData[currentIndex];

        var hsClass = $("select[name=hsClass]").val();
        // 增加房源类型控制
        conditionNames = conditionNames + "," + "NewHsInfo.hsClass";
        conditions = conditions + ",=";
        conditionValue = conditionValue + "," + hsClass;

        // 购房区域类型
        if (showType == "NewHsInfo.hsHxName") {
            conditionNames = conditionNames + "," + showType;
            conditions = conditions + ",=";
            conditionValue = conditionValue + "," + nowCondition;
        } else if (showType == "NewHsInfo.hsBldSize") {
            var bldArea = nowCondition.split("-");
            var minValue = 0;
            var maxValue = 0;
            if (bldArea.length > 0 && bldArea[0] != "") {
                minValue = bldArea[0];
            }
            if (bldArea.length > 1 && bldArea[1] != "") {
                maxValue = bldArea[1];
            }
            conditionNames = conditionNames + "," + showType + "," + showType;
            conditions = conditions + ",>=" + ",<=";
            conditionValue = conditionValue + "," + minValue + "," + maxValue;
        } else if (showType == "NewHsInfo.hsJush") {
            // 按照居室结构
            conditionNames = conditionNames + "," + showType;
            conditions = conditions + ",=";
            conditionValue = conditionValue + "," + nowCondition;
        }
        showTitle = nowCondition;
        if (showType == "NewHsInfo.hsJush") {
            showTitle = $("select[name=newHsJs]").find("option:[value=" + nowCondition + "]").text();
        }
        packet.data.add("conditionNames", conditionNames);
        packet.data.add("conditions", conditions);
        packet.data.add("conditionValues", conditionValue);
        packet.data.add("regUseType", $("input[name=regUseType]", form).val());
        packet.data.add("rhtType", $("input[name=rhtType]", form).val());
        packet.data.add("displayBuildPage", $("input[name=displayBuildPage]", form).val());
        packet.url = getGlobalPathRoot() + "eland/oh/oh001/oh00101-initB.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            var resp = $(response);
            var disData = $("#disData");
            disData.html(resp);
            builds = disData.find("div.bc_build")
            currentBuilding = builds.eq(0);
            currentBuildIdx = 0;
            currentScroTop = 0;
            if (currentBuilding.length > 0) {
                $("#disTitle").html(showTitle + "(" + currentBuilding.find(".buildTitle").html() + ")");
            } else {
                $("#disTitle").html(showTitle + "(无房源)");
            }
            scrollBuild()
            currentIndex++;
        });
    }

    function scrollBuild() {
        var disData = $("#disDiv").hide();
        $("#housInfo").html("<span id='housShowTitle'>" + showTitle + "(" + currentBuilding.find("span.buildTitle").html() + ")</span>");
        $("#housInfo").append(currentBuilding.find("ul.build_unit").clone());
        $("#housInfo").append(currentBuilding.find("ul.build_floor").clone());
        currentBuilding.find(".buildTitle").hide()
        currentBuilding.find(".build_unit").hide()
        currentBuilding.find(".build_floor").hide()
        $("#housShow").html(currentBuilding)
        $("#housShow").append(disData.find("div.build_legend").clone().show());
        $("#housShow").animate({scrollTop: 0}, 0)
        $("#housShow").show();
        $("#new_showBox").show();
        var scrollPos = currentBuilding.height();
        var showViewHeight = $("#housShow").height();
        if (scrollPos > showViewHeight) {
            setTimeout("move()", freshTime)
        } else {
            setTimeout("changeBuild()", freshTime)
        }
    }

    function move() {
        var scrollPos = currentBuilding.height();
        var showViewHeight = $("#housShow").height();
        var temp = scrollPos - showViewHeight - currentScroTop
        if (temp > 0) {
            if (currentScroTop + showViewHeight - 20) {
            }
            currentScroTop = currentScroTop + showViewHeight - 20
            $("#housShow").animate({scrollTop: currentScroTop}, 0)
            setTimeout("move()", freshTime)
        } else {
            currentScroTop = scrollPos
            $("#housShow").animate({scrollTop: currentScroTop}, 0)
            setTimeout("changeBuild()", freshTime)
        }
    }

    function changeBuild() {
        if (builds.length > (currentBuildIdx + 1)) {
            currentBuildIdx++
            currentBuilding = builds.eq(currentBuildIdx)
            currentScroTop = 0
            scrollBuild()
        } else {
            nextDataFunction()
        }
    }
</script>
</body>
</html>