sys00502 = {
    valueTypeChange: function () {
        var selectValue = $("select[name=valueType]", navTab.getCurrentPanel()).val();
        if ("1" == selectValue) {
            $("#sys00502_cdvl", navTab.getCurrentPanel()).addClass("hidden")
        } else {
            $("#sys00502_cdvl", navTab.getCurrentPanel()).removeClass("hidden")
        }
    },
    saveCfg: function () {
        var $form = $("#sys00502form", navTab.getCurrentPanel());
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
                } else {
                    alertMsg.error(jsonData.errMsg);
                }
            });
        }
    },
    addRow: function (divId, obj) {
        var obj = table.addRow(divId, obj);
        $(obj).find("input[name='valueCd']").addClass("required");
        $(obj).find("input[name='valueName']").addClass("required");
    }
};
sys00502.valueTypeChange();
