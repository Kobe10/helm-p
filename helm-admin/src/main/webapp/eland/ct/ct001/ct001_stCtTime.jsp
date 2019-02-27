<%--
  Created by IntelliJ IDEA.
  User: shfb_wang
  Date: 2015/9/14 0014 14:03
  Copyright(c) 北京四海富博计算机服务有限公司
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <script src="${pageContext.request.contextPath}/oframe/plugin/jquery/eland.jquery.min.js"
            type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/oframe/plugin/system/system.min.js" type="text/javascript"></script>
</head>
<style>
    table.border {
        font-weight: normal;
        border-collapse: collapse;
        border: 1px solid #99bbe8;
    }

    table.border th {
        font-weight: normal;
        border: 1px solid #99bbe8;
        padding: 3px 1px;
        min-width: 50px;
        text-align: right;
        font-size: 20px;
    }

    table.border td {
        font-weight: normal;
        border: 1px solid #99bbe8;
        padding: 3px 1px;
        min-width: 50px;
        text-align: left;
        font-size: 20px;
    }

    table tr th.titleTh {
        background-color: #3c8fc7;
        text-align: center;
    }

    table tr th span.titleSpan {
        color: #ffffff;
        font-size: 25px;
        font-weight: bold;
    }

    table tr th span.reloadCt {
        cursor: pointer;
        font-size: 20px;
        float: right;
        margin-right: 5px;
        margin-top: 5px;
        color: yellow;
        font-weight: bolder;
    }

    table tr td span.styleCtTime {
        font-size: 40px;
        font-weight: bold;
        color: #ff0000;
    }

    span.currrntTime {
        display: inline-block;
        margin-left: 5px;
        font-size: 25px;
        color: red;
    }

    span.ctTimeInfo {
        display: inline-block;
        margin-left: 5px;
        font-size: 20px;
        color: blueviolet;
    }
</style>
<body onload="ctTimeSt.countDown()" style="padding: 0; margin: 0">
<div>
    <input type="hidden" name="startCd" value="${startCd}">
    <input type="hidden" name="mark" value="${mark}">
    <input type="hidden" name="spaceTime" value="${spaceTime}">
    <input type="hidden" name="hidHsId" value="${hsId}">
    <table class="border" style="width: 100%;">
        <c:if test="${stEndCt == 'start'}">
            <tr>
                <th colspan="2" class="titleTh">
                    <span class="titleSpan">本轮签约开始倒计时</span>
                    <span class="link reloadCt" onclick="document.location.reload();">刷新</span>
                </th>
            </tr>
            <tr>
                <th width="55%">签约系统时间：</th>
                <td><span class="currrntTime"></span></td>
            </tr>
            <tr>
                <th width="55%">本轮开始时间：</th>
                <td><span class="ctTimeInfo">${stTime}</span></td>
            </tr>
            <tr>
                <th>本轮结束时间：</th>
                <td><span class="ctTimeInfo">${endTime}</span></td>
            </tr>
            <tr>
                <th rowspan="2"><label>阅读协议倒计时：</label></th>
                <td rowspan="2">
                    <span class="ctcountTime styleCtTime">${spaceTime}</span>
                </td>
            </tr>
        </c:if>
        <c:if test="${stEndCt == 'end'}">
            <tr>
                <th colspan="2" class="titleTh">
                    <span class="titleSpan">本轮签约结束倒计时</span>
                    <span class="link reloadCt" onclick="document.location.reload();">刷新</span>
                </th>
            </tr>
            <tr>
                <th width="55%">签约系统时间：</th>
                <td><span class="currrntTime"></span></td>
            </tr>
            <tr>
                <th width="55%">本轮开始时间：</th>
                <td><span class="ctTimeInfo">${stTime}</span></td>
            </tr>
            <tr>
                <th>本轮结束时间：</th>
                <td><span class="ctTimeInfo">${endTime}</span></td>
            </tr>
            <tr>
                <th rowspan="2"><label>确认签约倒计时：</label></th>
                <td rowspan="2">
                    <span class="ctcountTime styleCtTime">${spaceTime}</span>
                </td>
            </tr>
        </c:if>
    </table>
</div>
</body>
<script>
    var ctcountdown;
    var ctcountTime;
    var mTime = new Date().getTime() - ${sysDate};//获取时间差 sysDate服务器时间。
    var ctTimeSt = {
        countDown: function () {
            clearTimeout(ctcountdown);
            var spaceTime = $("input[name=spaceTime]").val();
            if (spaceTime != null && spaceTime != "0") {
                ctcountTime = Number(spaceTime);
                ctTimeSt.count();
            }
        },
        count: function () {
            if (ctcountTime > 0) {
                var minite = Math.floor(ctcountTime / 60);
                var second = Math.floor(ctcountTime % 60);
                if (minite == 0) {
                    $(".ctcountTime").html(second + "秒");
                } else {
                    $(".ctcountTime").html(minite + "分" + second + "秒");
                }
                ctcountTime--;
                ctTimeSt.showCurrentTime();
            } else {
                clearTimeout(ctcountdown);
                document.location.reload();
            }
            ctcountdown = setTimeout("ctTimeSt.count()", 1000);
        },

        //更新到真实时间。cTime真实时间long型。
        showCurrentTime: function () {
            var cTime = new Date().getTime() - mTime;
            var cDate = new Date(cTime);
            var hour = cDate.getHours();
            var minite = cDate.getMinutes();
            var second = cDate.getSeconds();
            $(".currrntTime").html(hour + ":" + minite + ":" + second);
        }
    };
    //根据状态变化重新刷新页面
    function getStatus() {
        var url = getGlobalPathRoot() + "eland/ct/ct001/ct001-getStatus.gv";
        var ajaxPacket = new AJAXPacket(url);
        ajaxPacket.data.add("prjCd", "${param.prjCd}");
        core.ajax.sendPacketHtml(ajaxPacket, function (response) {
            var jsonData = eval("(" + response + ")");
            if ($("input[name=mark]").val() != jsonData.mark) {
                document.location.reload();
            } else {
                setTimeout("getStatus()", 1000);
            }
        });
    }
    setTimeout("getStatus()", 1000);


    //   iframe 禁用右键菜单 防止 倒计时停止
    $().ready(function () {
        $(document).bind("contextmenu", function () {
            return false;
        });
    });
</script>