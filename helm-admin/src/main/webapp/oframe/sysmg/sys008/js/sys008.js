sys008 = {
    forceOut: function (loginAccept) {
        alertMsg.confirm("你确定要强制该用户退出系统吗?", {
            okCall: function () {
                var url = getGlobalPathRoot() + "oframe/sysmg/sys008/sys008-forceOut.gv";
                var ajaxPacket = new AJAXPacket(url);
                ajaxPacket.data.add("loginAccept", loginAccept);
                ajaxPacket.data.add("prjCd", getPrjCd());
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
    },
    /**
     * 加载内容
     */
    loadTabContext: function (flag) {
        if (flag == 1) {
            Query.queryList('sys008frm', 'sys008_list_print');
        } else {
            //Query.jumpPage(1,'sys008_list_history');
            Query.queryList('sys008his', 'sys008_list_history');
        }
    },

    /**
     * 打开快速检索对话框
     */
    openQSch: function (flag) {
        var dialogId = "#sys008create1";
        if (flag == 2) {
            dialogId = "#sys008create2";
        }
        var div = $(dialogId, navTab.getCurrentPanel());
        var active = div.attr("active");
        if ("on" == active) {
            div.hide().removeAttr("active");
        } else {
            div.show().attr("active", "on");
        }
    },
    /**
     * 关闭快速检索对话框
     */
    closeQSch: function (flag) {
        var dialogId = "#sys008create1";
        if (flag == "2") {
            dialogId = "#sys008create2";
        }
        $(dialogId, navTab.getCurrentPanel()).hide().removeAttr("active");
    },
    /**
     * 批量强制退出
     */
    forceQuit: function () {
        var ids = [];
        $("#sys008_list_print", navTab.getCurrentPanel()).find(":checkbox[checked][name=id]").each(
            function () {
                var temp = $(this).closest("tr");
                var quitId = $("input[name=id]", temp).val();
                ids.push(quitId);
            }
        );
        if (ids.length == 0) {
            alertMsg.warn("请选择需要退出的记录");
        } else {
            alertMsg.confirm("你确定要强制该用户退出系统吗?", {
                okCall: function () {
                    var url = getGlobalPathRoot() + "oframe/sysmg/sys008/sys008-forceOut.gv";
                    var ajaxPacket = new AJAXPacket(url);
                    ajaxPacket.data.add("loginAccept", ids.toString());
                    ajaxPacket.data.add("prjCd", getPrjCd());
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
$(document).ready(function () {
    // 执行查询
    Query.queryList('sys008frm', 'sys008_list_print');
});
