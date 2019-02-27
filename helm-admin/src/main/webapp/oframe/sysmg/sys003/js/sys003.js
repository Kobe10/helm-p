sys003 = {
    chgPwd: function () {
        var $form = $("#sys003frm", navTab.getCurrentPanel());
        if ($form.valid()) {
            // 提交页面数据
            var url = getGlobalPathRoot() + "oframe/sysmg/sys003/sys003-chgPwd.gv";
            var ajaxPacket = new AJAXPacket(url);
            ajaxPacket.data.data = $form.serializeArray();
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var data = eval("(" + response + ")");
                var isSuccess = data.success;
                if (isSuccess) {
                    $($form)[0].reset();
                    alertMsg.correct("处理成功");
                } else {
                    alertMsg.error(data.errMsg);
                }
            });
        }
    },
    resetPwd: function () {
        // 提交页面数据
        var url = getGlobalPathRoot() + "oframe/sysmg/sys003/sys003-resetPwd.gv";
        var ajaxPacket = new AJAXPacket(url);
        var staffCd = $("input[name=staffCd]", navTab.getCurrentPanel()).val();
        ajaxPacket.data.add("staffCd", staffCd);
        core.ajax.sendPacketHtml(ajaxPacket, function (response) {
            var resetPwdRsp = eval("(" + response + ")");
            var isSuccess = resetPwdRsp.success;
            if (isSuccess) {
                $("#sys003frm", navTab.getCurrentPanel())[0].reset();
                alertMsg.correct("处理成功");
            } else {
                alertMsg.error(resetPwdRsp.errMsg);
            }
        });
    }
}