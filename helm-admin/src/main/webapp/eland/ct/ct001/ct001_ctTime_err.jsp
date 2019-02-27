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
        font-size: 20px;
        font-weight: bold;
    }

    table tr th span.reloadCt {
        cursor: pointer;
        font-size: 12px;
        float: right;
        margin-right: 5px;
        margin-top: 5px;
        color: yellow;
        font-weight: bolder;
    }

    span.currrntTime {
        display: inline-block;
        margin-left: 5px;
        font-size: 25px;
        color: red;
    }

    table.border td.errorInfo {
        color: red;
        font-size: 35px;
        font-weight: bolder;
        text-align: center;
    }
</style>
<body onload="cdTimeSt.showCurrentTime();cdTimeSt.countDown();" style="padding: 0; margin: 0">
<div>
    <input type="hidden" name="startCd" value="${startCd}">
    <input type="hidden" name="mark" value="${mark}">
    <input type="hidden" name="spaceTime" value="${spaceTime}">
    <table class="border" style="width: 100%;">
        <tr>
            <th colspan="2" class="titleTh">
                <span class="titleSpan">提示</span>
                <span class="link reloadCt" onclick="document.location.reload();">[刷新]</span>
            </th>
        </tr>
        <tr>
            <th width="55%">签约系统时间：</th>
            <td><span class="currrntTime"></span></td>
        </tr>
        <tr>
            <td width="300px" height="90px" colspan="2" class="errorInfo">
                ${info}
                <span class="ctcountTime">${spaceTime}</span></td>
        </tr>
    </table>
</div>
</body>
<script>
    var ctcountdown;
    var ctcountTime;
    var mTime = new Date().getTime() - ${sysDate};//获取时间差 sysDate服务器时间。
    var cdTimeSt = {
        countDown: function () {
            clearTimeout(ctcountdown);
            var spaceTime = $("input[name=spaceTime]").val();
            if (spaceTime != null && spaceTime != "0" && spaceTime) {
                ctcountTime = Number(spaceTime);
                cdTimeSt.count();
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
            } else {
                clearTimeout(ctcountdown);
                document.location.reload();
            }
            ctcountdown = setTimeout("cdTimeSt.count()", 1000);
        },
        //更新到真实时间。cTime真实时间long型。
        showCurrentTime: function () {
            var cTime = new Date().getTime() - mTime;
            var cDate = new Date(cTime);
            var hour = cDate.getHours();
            var minite = cDate.getMinutes();
            var second = cDate.getSeconds();
            $(".currrntTime").html(hour + ":" + minite + ":" + second);
            setTimeout("cdTimeSt.showCurrentTime()", 1000);
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
    getStatus();

    //   iframe 禁用右键菜单 防止 倒计时停止
    $().ready(function () {
        $(document).bind("contextmenu", function () {
            return false;
        });
    });
</script>