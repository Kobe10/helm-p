login = {

    /**
     * 登录验证
     * @returns {boolean}
     */
    check: function () {
        // 获取页面元素
        var systemUserCode = $("input[name=systemUserCode]").val();
        var password = $("input[name=password]").val();
        var validate = $("input[name=validate]").val();
        var stayLogin = $("input[name=stayLogin]")[0].checked;
        var prjCd = $("#prjCd").val();
        // 临时对象保存错误对象信息
        var errObjArr = [];
        var errMessageArr = [];
        if (systemUserCode == "") {
            errObjArr.push($("input[name=systemUserCode]"));
            errMessageArr.push("用户名不能为空");
        }
        if (password == "") {
            errObjArr.push($("input[name=password]"));
            errMessageArr.push("密码不能为空");
        }
        if (validate == "") {
            errObjArr.push($("input[name=validate]"));
            errMessageArr.push("验证码不能为空");
        }
        if (errObjArr.length == 0) {
            //调用服务
            // 提交页面数据
            var url = getGlobalPathRoot() + "oframe/sysmg/sys001/login-check.gv";
            var ajaxPacket = new AJAXPacket(url);
            ajaxPacket.data.data = $("#frm").serializeArray();
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var jsonData = eval("(" + response + ")");
                var isSuccess = jsonData.isSuccess;
                if (isSuccess) {
                    // 设置登录流水号
                    $("input[name=loginAccept]").val(jsonData.loginAccept);
                    if (jsonData.outNum > 0) {
                        alertMsg.confirm("当前工号在其他地方已经登录/上次登录没有未正常退出，登录将使原有登录失效，你确定要登录吗？", {
                            okCall: function () {
                                login.whetherExpired(jsonData.isExpire);
                            }
                        });
                    } else {
                        login.whetherExpired(jsonData.isExpire);
                    }
                } else {
                    $("#errorTips").html(jsonData.errMsg);
                    // 重新加载图片信息
                    $("input[name=validate]").val("");
                    login.reloadImg();
                }

            }, false);
        } else {
            login.showErrorInf($("#errorTips"), errObjArr, errMessageArr);
        }
        return false;
    },

    /**
     * 判断是否过期
     * @param isExpire
     */
    whetherExpired: function (isExpire) {
        if (isExpire == '1') {
            //过期。弹框改密码
            $("#modifyPwdDiv").show();
        } else {
            $("#frm").submit();
        }
    },

    /**
     * 显示错误信息
     * @param errMsgDiv jq对象展示错误信息
     * @param objArr 错误对象列表
     * @param errMessageArr 错误消息列表
     */
    showErrorInf: function (errMsgDiv, objArr, errMessageArr) {
        // 标记错误元素
        for (var i = 0; i < objArr.length; i++) {
            objArr[i].addClass("form-control-error");
        }
        // 显示错误信息
        var message = "";
        for (var i = 0; i < errMessageArr.length; i++) {
            if (i == 0) {
                message = errMessageArr[i];
            } else {
                message = message + ";" + errMessageArr[i];
            }
        }
        errMsgDiv.html(message);
    },
    /**
     * 重新加载图片
     */
    reloadImg: function () {
        $("#validateImg").attr("src", getGlobalPathRoot() + "oframe/sysmg/sys001/login-randomImage.gv?" + new Date().getTime())
    },
}

var wid = $(window).width();
if(wid>=960){
    $(function () {
        $('#slides').slidesjs({
            width: 513,
            height: 349,
            navigation: {
                active: false
            },
            play: {
                active: false,
                auto: true,
                interval: 4000,
                effect: "fade",
                swap: false
            }
        });
    });
}

$("input").bind('focus', function () {
    var _this = $(this);
    _this.removeClass("form-control-error");
    $("#errorTips").html('');
    $('#modifyErrMsg').html('');
});

$('#repassConfirm').bind('click', function () {
    $(this).submit();
    var newContainer = $("#modifyPwdDiv");
    var oldPwd = $("input[name=oldPwd]", newContainer);
    var newPwd = $("input[name=newPwd]", newContainer);
    //保持登录标志
    var stayLogin = $("input[name=stayLogin]")[0].checked;
    var stayLoginValue = "";
    if (stayLogin) {
        stayLoginValue = "on"
    }
    var reNewPwd = $("input[name=reNewPwd]", newContainer);
    //用户名
    var systemUserCode = $("input[name=systemUserCode]").val();

    var errObjArr = [];
    var errMessageArr = [];

    if (oldPwd.val() == "") {
        errObjArr.push(oldPwd);
        errMessageArr.push("原密码不能为空");
    }
    if (newPwd.val() == "") {
        errObjArr.push(newPwd);
        errMessageArr.push("新密码不能为空");
    }
    if (reNewPwd.val() == "") {
        errObjArr.push(reNewPwd);
        errMessageArr.push("确认新密码不能为空");
    }
    if (errObjArr.length == 0) {
        if (newPwd.val() == reNewPwd.val()) {
            //请求服务
            var url = getGlobalPathRoot() + "oframe/sysmg/sys001/login-loginResetPwd.gv";
            var ajaxPacket = new AJAXPacket(url);
            ajaxPacket.data.data = {
                systemUserCode: systemUserCode,
                oldPassWd: oldPwd.val(),
                passWord: newPwd.val(),
                stayLogin: stayLoginValue,
                rePassWord: reNewPwd.val()
            };
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var jsonData = eval("(" + response + ")");
                if (!jsonData.success) {
                    //错误信息
                    errMessageArr.push(jsonData.errMsg);
                    console.log($('#modifyErrMsg'), errObjArr, errMessageArr)
                    login.showErrorInf($('#modifyErrMsg'), errObjArr, errMessageArr);
                } else {
                    $("#modifyPwdForm").attr("action", getGlobalPathRoot() + "oframe/sysmg/sys001/login-staffLogin.gv?systemUserCode="
                        + jsonData.systemUserCode + "&loginAccept=" + jsonData.loginAccept + "&password=" + jsonData.passWord);
                    $("#modifyPwdForm").submit();
                }
            }, false);
        }
        else {
            $('#modifyErrMsg').html('请确认两次输入密码相同！');
        }
    } else {
        login.showErrorInf($('#modifyErrMsg'), errObjArr, errMessageArr);
    }
})
;

//取消按钮的实现,实现页面的跳转
$('#repassCancel').bind('click', function () {
    var newContainer = $("#modifyPwdDiv");
    //重置成空
    var oldPwd = $("input[name=oldPwd]", newContainer).val("");
    var newPwd = $("input[name=newPwd]", newContainer).val("");
    var reNewPwd = $("input[name=reNewPwd]", newContainer).val("");
    $('#modifyPwdDiv').hide();
});


