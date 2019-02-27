orgTree = {
    /**
     * 初始化化目录树
     */
    initFullTree: function () {
        // 重新加载
        var packet = new AJAXPacket();
        packet.data.add("prjCd", "0");
        packet.url = getGlobalPathRoot() + "oframe/sysmg/sys004/sys004-treeInfo.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
                var jsonData = eval("(" + response + ")");
                var treeJson = jsonData.resultMap.treeJson;
                // 服务调用是否成功
                var isSuccess = jsonData.success;
                if (isSuccess) {
                    // 目录树数据
                    // 左侧树的生成和回调
                    var pjOrgTreeSetting = {
                            data: {
                                simpleData: {
                                    enable: true
                                }
                            },
                            callback: {
                                onClick: orgTree.clickNode
                            }
                        }
                        ;
                    // 初始化目录树
                    var zTree = $.fn.zTree.init($("#commonTreeContent"), pjOrgTreeSetting, treeJson);
                }
                else {
                    alertMsg.warn(jsonData.data.errMsg);
                }
            }, true, false
        )
        ;
    },
    clickNode: function (event, treeId, treeNode) {
        var tipObj = $("#openTip", $.pdialog.getCurrent());
        var rowObj = $(tipObj).parent();
        $(rowObj).find("input[name=orgId]").val(treeNode.id);
        $(rowObj).find("input[name=orgName]").val(treeNode.name);
        $(tipObj).remove();
    }
}
;
// 执行目录树初始化
orgTree.initFullTree();
