<!DOCTYPE html>
<html>
<head lang="en">
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>选房自助查询系统</title>

    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <script src="${pageContext.request.contextPath}/oframe/plugin/jquery/eland.jquery.min.js"
            type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/oframe/plugin/system/system.min.js" type="text/javascript"></script>
    <link href="css/style.css" type="text/css" rel="stylesheet">
</head>
<body>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<input type="hidden" id="sysPrjCd" name="prjCd" value="${prjCd}"/>

<div class="banner" style="position:absolute;width:100%;height:100%;overflow: hidden;">
    <img src="image/xkcx.jpg" style="width: 100%; height:100%;">
    <div class="wrapper">
        <div class="form-group" style="display: flex; border:1px solid #f86801; color: #f86801;border-radius: 5px;">
            <label class="icon-1">居民选房序号 ：</label>
            <input style="flex: 1;" type="text" class="form-control" name="chooseHsSid" placeholder="必填项！" value=""
                   onkeydown="if(event.keyCode == 13){ch003.login();}"
                   title="选房序号必须输入！">
        </div>
        <div class="form-group" style="position:relative; z-index:1;">
            <button type="button" class="btn btn-block btn-primary" onclick="ch003.login();">查看可选房源
            </button>
        </div>
    </div>
</div>

</body>
<script>
    alertMsg = {
        warn: function (msg) {
            alert(msg);
        },
        error: function (msg) {
            alert(msg);
        }
    };

    $(document).ready(function () {
        $("input[name=hsCd]").focus();
    });

    var ch003 = {
        login: function () {
            var prjCd = $.trim($("input[name=prjCd]").val());
            var chooseHsSid = $.trim($("input[name=chooseHsSid]").val());
            if (CheckUtil.checkInteger(chooseHsSid) === false) {
                alert("选房序号必须是整数");
                return false;
            }
            var url = getGlobalPathRoot() + "eland/ch/ch003/ch003-loginChooseHs.gv";
            var packet = new AJAXPacket(url);
            packet.data.add("chooseHsSid", chooseHsSid);
            packet.data.add("prjCd", prjCd);
            core.ajax.sendPacketHtml(packet, function (response) {
                var jsonData = eval("(" + response + ")");
                if (jsonData.success) {
                    var hsId = jsonData.hsId;
                    var ctType = jsonData.ctType;
                    if (hsId != '' && hsId != '0') {
                        ch003.selectHsInfo(hsId, ctType);
                    } else {
                        alert("没有查询到该房产数据，请核对后再查！");
                    }
                } else {
                    alert(jsonData.errMsg);
                }
            })
        },

        selectHsInfo: function (hsId, ctType) {
            var packet = new AJAXPacket();
            var prjCd = $("input[name=prjCd]").val();
            packet.data.add("prjCd", prjCd);
            packet.data.add("hsId", hsId);
            packet.data.add("ctType", ctType);
            packet.url = getGlobalPathRoot() + "eland/ch/ch003/ch003-info.gv";
            core.ajax.sendPacketHtml(packet, function (response) {
                $(".banner").html(response);
//                ch003cx.countDown();
            })
        }
    }
</script>
</html>
