sys005Apply = {
    //关键字自动显示
    changeKeyWord: function (obj) {
        var changeTd = $(obj).parent();
        var keyWord = $(obj).val();
        if (!/^\s*$/.test(keyWord)) {
            var newKey = $("label.js_keyword", changeTd).first().clone();
            $("span.in-block", newKey).val("");
            newKey.appendTo(changeTd.last("label"));
            $("span.in-block", newKey).html(keyWord);
            $("label.js_keyword", changeTd).last().show();
            $("input[type=hidden]", newKey).val(keyWord);
            $(obj).val("");
        } else {
            alertMsg.warn("关键字不可以为空！");
        }
    },
    addRow: function (divId, obj) {
        var obj = table.addRow(divId, obj);
        $(obj).find("input[name='valueCd']").addClass("required");
        $(obj).find("input[name='valueName']").addClass("required");
    },
    saveCfg: function () {
        var $form = $("#sys005Applyform", navTab.getCurrentPanel());
        if ($form.valid()) {
            // 提交页面数据
            var url = getGlobalPathRoot() + "oframe/sysmg/sys005/web-saveApply.gv";
            var ajaxPacket = new AJAXPacket(url);
            ajaxPacket.data.data = $form.serializeArray();
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var jsonData = eval("(" + response + ")");
                // 服务调用是否成功
                var isSuccess = jsonData.success;
                if (isSuccess) {
                    alertMsg.correct("处理成功");
                } else {
                    alertMsg.error(jsonData.errMsg);
                }
            });
        }
    },

    saveWebPic: function () {
        var $form = $("#webpicfrm", navTab.getCurrentPanel());
        if ($form.valid()) {
            // 提交页面数据
            var url = getGlobalPathRoot() + "oframe/sysmg/sys005/web-saveWebPic.gv";
            var ajaxPacket = new AJAXPacket(url);
            ajaxPacket.data.data = $form.serializeArray();
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var jsonData = eval("(" + response + ")");
                // 服务调用是否成功
                var isSuccess = jsonData.success;
                if (isSuccess) {
                    alertMsg.correct("处理成功");
                } else {
                    alertMsg.error(jsonData.errMsg);
                }
            });
        }
    }
};