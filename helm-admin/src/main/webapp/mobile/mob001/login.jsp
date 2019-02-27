<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head lang="en">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0">
    <title>居民住房改善信息系统|登录</title>
    <link type="text/css" href="${pageContext.request.contextPath}/mobile/common/css/phone.css" rel="stylesheet">
    <script type="text/javascript" src="${pageContext.request.contextPath}/mobile/common/js/jquery.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/mobile/common/js/base64.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/oframe/plugin/system/system.min.js"></script>
</head>
<body>
<div class="wrapper">
    <div class="icon-top"><p class="icon-p">居民住房改善信息系统</p>
        <img class="logo" src="../common/image/sub-logo_03.png" alt="logo">
    </div>

    <div class="center">
        <form class="required-validate" method="post" id="frm"
              action="${pageContext.request.contextPath}/mobile/mob001/login-staffLogin.gv">
            <div class="error-box"><span class="error-prompt"></span></div>
            <input type="hidden" name="loginAccept" value=""/>
            <div class="boxOne">
                <img class="oneName" id="userimg" src="../common/image/app-login_03.png">
                <input name="systemUserCode" class="centerInput" id="username" placeholder="请输入账号" type="text">
                <img class="deleteImg" id="userimgcan" src="../common/image/app-login_08.png">
            </div>
            <div class="boxTow">
                <img class="passImg" id="passimg" src="../common/image/app-login_10.png">
                <input name="password" class="centerInput" id="password" placeholder="请输入密码" type="password">
                <img class="deleteImg" id="passimgcan" src="../common/image/app-login_08.png">
            </div>
            <div class="f-mgt10">
                <label class="s-fc3"><input type="checkbox" name="saveFlag" checked="checked"
                                            class="u-auto">下次自动登录</label>
            </div>
            <div class="loginBtn" onclick="check();" id="login">登 录</div>
        </form>
    </div>
</div>
</body>
</html>
<script type="text/javascript">
    $(document).ready(function () {
        var u = localStorage.getItem("u"),
                p = localStorage.getItem("p");
        if (u && p) {
            var b = new Base64();
            $("input[name=systemUserCode]").val(b.decode(u));
            $("input[name=password]").val(b.decode(p));

            check();
        }
    });

    $("#username").blur(function () {
        $("#userimg").attr("src", "../common/image/app-login_03.png");
        $("#userimgcan").click(function () {
            $("#username").val("");
        });

    });
    $("#username").focus(function () {
        $("#userimg").attr("src", "../common/image/app-login_05.png");
        $("#passimgcan").css("visibility", "hidden");
        $("#userimgcan").css("visibility", "visible");
    });
    $("#password").blur(function () {
        $("#passimg").attr("src", "../common/image/app-login_10.png");
        $("#passimgcan").click(function () {
            $("#password").val("");
        });
    });
    $("#password").focus(function () {
        $("#passimg").attr("src", "../common/image/app-login_10-15.png");
        $("#userimgcan").css("visibility", "hidden");
        $("#passimgcan").css("visibility", "visible");
    });
    $("#username").change(function () {
        $("#password").val("");
        $("span.error-prompt").html("");
    });
    $("#password").change(function () {
        $("span.error-prompt").html("");
    });

    /**
     * 登录验证
     * @returns {boolean}
     */
    function check() {
        // 获取页面元素
        var systemUserCode = $("input[name=systemUserCode]");
        var password = $("input[name=password]");
        // 临时对象保存错误对象信息
        var errObjArr = [];
        var errMessageArr = [];

        if (systemUserCode.val() == "") {
            errObjArr.push(systemUserCode);
            errMessageArr.push("账号");
        }
        if (password.val() == "") {
            errObjArr.push(password);
            errMessageArr.push("密码");
        }
        if (errObjArr.length == 0) {
            // 提交页面数据
            var url = getGlobalPathRoot() + "mobile/mob001/login-check.gv";
            var ajaxPacket = new AJAXPacket(url);
            ajaxPacket.data.data = $("#frm").serializeArray();
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var jsonData = eval("(" + response + ")");
                // 服务调用是否成功
                var isSuccess = jsonData.isSuccess;
                if (isSuccess) {
                    // 设置登录流水号
                    $("input[name=loginAccept]").val(jsonData.loginAccept);
                    //success set form to localStorage
                    setFormToLocal();
                    $("#frm").submit();
                } else {
                    $("span.error-prompt").css("display", "inline-block").html(jsonData.errMsg);
                }
            }, false);
        } else {
            showErrorInf(errObjArr, errMessageArr);
        }
        return false;
    }

    /**
     * 显示错误信息
     * @param objArr 错误对象列表
     * @param errMessageArr 错误消息列表
     */
    function showErrorInf(objArr, errMessageArr) {
        // 显示错误信息
        var message = "";
        for (var i = 0; i < errMessageArr.length; i++) {
            if (i == 0) {
                message = errMessageArr[i];
            } else {
                message = message + "或" + errMessageArr[i];
            }
        }
        $(".error-prompt").css("display", "inline-block").html(message + "不可以为空");
    }

    /**
     * set form to localStorage
     */
    function setFormToLocal() {
        var systemUserCode = $("input[name=systemUserCode]").val();
        var password = $("input[name=password]").val();
        var saveFlag = $("input[name=saveFlag]:checked").val();
        if (saveFlag == 'on') {
            var b = new Base64();
            localStorage.setItem("u", b.encode(systemUserCode.toString()));
            localStorage.setItem("p", b.encode(password.toString()));
        } else {
            localStorage.clear();
        }
    }

</script>