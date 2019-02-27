var pj012 = {
    openAdd: function () {
        var url = getGlobalPathRoot() + "eland/pj/pj012/pj012-info.gv?method=add" +
            "&prjCd=" + getPrjCd();
        $.pdialog.open(url, "pj01201", "问题信息", {mask: true, max: true});
    },
    openEdit: function (qId) {
        var url = getGlobalPathRoot() + "eland/pj/pj012/pj012-info.gv?method=edit&qId=" + qId;
        $.pdialog.open(url, "pj01201", "问题信息", {mask: true, max: true});
    },
    openView: function (qId) {
        var url = getGlobalPathRoot() + "eland/pj/pj012/pj012-info.gv?method=view&qId=" + qId
        $.pdialog.open(url, "pj01201", "问题信息", {mask: true, max: true});
    },
    save: function () {
        var $form = $("#pj01201frm", $.pdialog.getCurrent());
        if ($form.valid()) {
            // 提交页面数据
            var url = getGlobalPathRoot() + "eland/pj/pj012/pj012-save.gv";
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
    },
    deleteQuestion: function (obj) {
        var qIds = new Array();
        var prjCds = new Array();
        $(obj).closest("div.pageContent")
            .find(":checkbox[checked][name=qId]").each(
            function (i) {
                qIds.push($(this).val());
                prjCds.push(getPrjCd());
            }
        );
        if (qIds.length == 0) {
            alertMsg.warn("请选择要删除的记录");
        } else {
            alertMsg.confirm("您确定要删除记录吗?", {
                okCall: function () {
                    var url = getGlobalPathRoot() + "eland/pj/pj012/pj012-deleteQuestion.gv";
                    var ajaxPacket = new AJAXPacket(url);
                    ajaxPacket.data.add("qIds", qIds.toString());
                    ajaxPacket.data.add("prjCds", prjCds.toString());
                    core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                        var jsonData = eval("(" + response + ")");
                        // 服务调用是否成功
                        var isSuccess = jsonData.success;
                        if (isSuccess) {
                            alertMsg.correct("处理成功");
                            $("#schBtn", navTab.getCurrentPanel()).trigger("click");
                        } else {
                            alertMsg.error(jsonData.errMsg);
                        }
                    });
                }
            });
        }
    }
};

$(function () {
    $("#schBtn", navTab.getCurrentPanel()).trigger("click");
});