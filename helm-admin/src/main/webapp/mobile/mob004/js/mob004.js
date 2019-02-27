/**
 * Created by linql on 2017/9/21.
 */
mob004 = {
    saveAppPic: function () {
        var $form = $("#appTopPic", navTab.getCurrentPanel());
        if ($form.valid()) {
            var jsonConf = new Array();
            $("label.js_img", $form).each(function () {
                var $this = $(this);
                var docId = $("input[name=docId]", $this).val();
                var docName = $("input[name=docName]", $this).val();
                if (docId != "") {
                    jsonConf.push({"valueCd": docId, "valueName": docName});
                }
            });
            // 提交页面数据
            var url = getGlobalPathRoot() + "mobile/mob004/mob004-save.gv";
            var ajaxPacket = new AJAXPacket(url);
            ajaxPacket.data.add("key", "APP_TOP_IMG");
            ajaxPacket.data.add("value", JSON.stringify(jsonConf));
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


};

$(document).ready(function () {

});