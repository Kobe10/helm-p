sys00501 = {
    valueTypeChange: function () {
        var selectValue = $("select[name=valueType]", $.pdialog.getCurrent()).val();
        if ("1" == selectValue) {
            $("#sys00501_cdvl", $.pdialog.getCurrent()).addClass("hidden")
        } else {
            $("#sys00501_cdvl", $.pdialog.getCurrent()).removeClass("hidden")
        }
    },
    saveCfg: function () {
        var $form = $("#sys00501form", $.pdialog.getCurrent());
        if ($form.valid()) {
            // 提交页面数据
            var url = getGlobalPathRoot() + "oframe/sysmg/sys005/sys005-save.gv?prjCd=" + getPrjCd();
            var ajaxPacket = new AJAXPacket(url);
            ajaxPacket.data.data = $form.serializeArray();
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var jsonData = eval("(" + response + ")");
                // 服务调用是否成功
                var isSuccess = jsonData.success;
                if (isSuccess) {
                    alertMsg.correct("处理成功");
                    $("#schBtn", navTab.getCurrentPanel()).trigger("click");
                    $.pdialog.closeCurrent();
                } else {
                    alertMsg.error(jsonData.errMsg);
                }
            });
        }
    }
};
sys00501.valueTypeChange();
