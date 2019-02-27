sltroleTree = {
    isCheckEnable: function () {
        var objName = $("input[name='objName']", $.pdialog.getCurrent()).val();
        if (objName && objName != "") {
            return true;
        } else {
            return false;
        }
    },
    /**
     * 初始化化目录树
     */
    initFullTree: function () {
        // 重新加载
        var packet = new AJAXPacket();

        packet.data.add("prjCd", $("input[name=staffPrjCd]", $.pdialog.getCurrent()).val());
        packet.url = getGlobalPathRoot() + "oframe/sysmg/sys004/sys004-orgRoleTreeInfo.gv?prjCd=" + getPrjCd();
        core.ajax.sendPacketHtml(packet, function (response) {
                var jsonData = eval("(" + response + ")");
                var treeJson = jsonData.resultMap.treeJson;
                // 服务调用是否成功
                var isSuccess = jsonData.success;
                if (isSuccess) {
                    // 目录树数据
                    // 左侧树的生成和回调
                    var pjsltroleTreeSetting = {
                        data: {
                            simpleData: {
                                enable: true
                            }
                        },
                        callback: {
                            onClick: sltroleTree.clickNode
                        },
                        check: {
                            autoCheckTrigger: false,
                            enable: sltroleTree.isCheckEnable(),
                            chkStyle: "checkbox",
                            chkboxType: {"Y": "", "N": ""}
                        }
                    };
                    // 初始化目录树
                    var zTree = $.fn.zTree.init($("#sltroleTreeContainer"), pjsltroleTreeSetting, treeJson);
                }
                else {
                    alertMsg.warn(jsonData.data.errMsg);
                }
            }, true, false
        )
    },
    /**
     * 点击节点，选择单个员工
     * @param event
     * @param treeId
     * @param treeNode
     * @returns {boolean}
     */
    clickNode: function (event, treeId, treeNode) {
        var tipObj = $("#openTip", $.pdialog.getCurrent());
        if (tipObj.length == 0) {
            tipObj = $("#openTip", navTab.getCurrentPanel());
        }
        var clickObj = tipObj.data("clickObj");
        var rowObj = $(clickObj).parent();
        $(rowObj).find("input[type=hidden]").val(treeNode.id);
        $(rowObj).find("input[type=text]").val(treeNode.name);
        $(tipObj).remove();
    }

};
$(document).ready(function () {
    // 执行目录树初始化
    sltroleTree.initFullTree();
});

