sys009 = {
    /**
     * 查询缓存信息
     */
    qryCacheInfo: function () {
        // 表单验证
        if (!$("#sys009frm", navTab.getCurrentPanel()).valid()) {
            return false;
        }
        var cacheTypeName = $("#cacheTypeName", navTab.getCurrentPanel()).val();
        var cacheEngineType = $("#cacheEngineType", navTab.getCurrentPanel()).val();
        var url = getGlobalPathRoot() + "oframe/sysmg/sys009/sys009-findCache.gv";
        var ajaxPacket = new AJAXPacket(url);
        ajaxPacket.data.add("cacheTypeName", cacheTypeName);
        ajaxPacket.data.add("cacheEngineType", cacheEngineType);
        ajaxPacket.data.add("prjCd", getPrjCd());
        var toContain = $("#sys009_list_print .gridScroller tbody", navTab.getCurrentPanel());
        core.ajax.sendPacketHtml(ajaxPacket, function (data) {
            toContain.html(data);
            Query.refresh($("#sys009_list_print"));
        }, true);
    },


    /**
     * 删除缓存信息
     * @param cacheTypeName 缓存大类名称
     * @param cacheSubKey 缓存细项主键
     */
    removeCache: function (cacheTypeName, cacheSubKey) {
        var url = getGlobalPathRoot() + "oframe/sysmg/sys009/sys009-removeCache.gv";
        var ajaxPacket = new AJAXPacket(url);
        ajaxPacket.data.add("cacheTypeName", cacheTypeName);
        ajaxPacket.data.add("cacheSubKey", cacheSubKey);
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
    },


    clickRemove: function (obj) {
        var cacheTypeName = $(obj).parent().parent().find("input[name=cacheTypeName]").val();
        var cacheSubKey = $(obj).parent().parent().find("input[name=cacheSubKey]").val();
        if (cacheSubKey == "") {
            alertMsg.confirm("不指定内容主键将删除该大类的所有信息，你确定要删除吗？", {
                okCall: function () {
                    sys009.removeCache(cacheTypeName, cacheSubKey);
                }
            });
        } else {
            sys009.removeCache(cacheTypeName, cacheSubKey);
        }
    },

    removeAllCacheExpSession: function () {
        var cacheTypeName = [];
        $("#sys009_list_print", navTab.getCurrentPanel()).find("input[name=cacheTypeName]").each(function () {
            var objThis = $(this);
            cacheTypeName.push(objThis.val());
        });
        alertMsg.confirm("你确定要删除除Session以外所有缓存信息吗？", {
            okCall: function () {
                var url = getGlobalPathRoot() + "oframe/sysmg/sys009/sys009-removeAllCacheExpSession.gv";
                var ajaxPacket = new AJAXPacket(url);
                ajaxPacket.data.add("cacheTypeName", cacheTypeName.join(','));
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
};
$(document).ready(function () {
    // 界面完成初始化后，自动执行查询
    setTimeout(function () {
        sys009.qryCacheInfo();
    }, 300);
});
