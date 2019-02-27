var pb002Wm = {
    /**
     * 点击快速提交按钮
     */
    submitMemo: function () {
        var $form = $("#_pb002Wmfrm_spat", navTab.getCurrentPanel());
        if ($form.valid()) {
            // 提交页面数据
            var url = getGlobalPathRoot() + "eland/pb/pb002/pb002-saveWM.gv";
            var ajaxPacket = new AJAXPacket(url);
            var jsonData = $form.serializeArray();
            jsonData.push({
                name: 'prjCd',
                value: getPrjCd()
            });
            ajaxPacket.data.data = jsonData;
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var jsonData = eval("(" + response + ")");
                // 服务调用是否成功
                var isSuccess = jsonData.success;
                if (isSuccess) {
                    alertMsg.correct("处理成功");
                    $form.find("#talkContext").val("");
                    Query.queryList('_pb002Wmfrm', 'pb002_wm_w_list_print');
                } else {
                    alertMsg.error(jsonData.errMsg);
                }
            });
        }
    },

    /**
     *点击删除按钮
     * @param talkRecordId
     */
    deleteRecord: function (recordId) {
        // 提交页面数据
        var url = getGlobalPathRoot() + "eland/pb/pb002/pb002-deleteWM.gv";
        var ajaxPacket = new AJAXPacket(url);
        ajaxPacket.data.add("recordId", recordId);
        core.ajax.sendPacketHtml(ajaxPacket, function (response) {
            var jsonData = eval("(" + response + ")");
            // 服务调用是否成功
            var isSuccess = jsonData.success;
            if (isSuccess) {
                alertMsg.correct("处理成功");
                Query.queryList('_pb002Wmfrm', 'pb002_wm_w_list_print');
            } else {
                alertMsg.error(jsonData.errMsg);
            }
        });
    },

    /**
     * 点击编辑按钮
     * @param talkRecordId
     */
    openEditRecord: function (talkRecordId) {
        var url = getGlobalPathRoot() + "eland/hs/hs001/hs001-openEditRecord.gv?prjCd=" + getPrjCd() + "&method=edit&talkRecordId=" + talkRecordId;
        $.pdialog.open(url, "pb002Wm01", "访谈记录", {mask: true, max: true});
    }
};

$(document).ready(function () {
    Query.queryList('_pb002Wmfrm', 'pb002_wm_w_list_print');
});
