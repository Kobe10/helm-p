/**
 * User: shfb_wang
 * Date: 2016/3/14 0014 15:29
 * Copyright(c) 北京四海富博计算机服务有限公司
 */
var fromTypeTree = {
    /**
     * 初始化目录树
     */
    initFullTree: function () {
        // 重新加载
        var packet = new AJAXPacket();
        // 获取当前选中的组织树类型，传递项目编号获取组织树
        packet.data.add("prjCd", "0");
        packet.url = getGlobalPathRoot() + "oframe/wf/wf005/wf005-formTypeTree.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            var treeJson = jsonData.resultMap.treeJson;
            // 服务调用是否成功
            var isSuccess = jsonData.success;
            if (isSuccess) {
                var formTreeSetting = {
                    data: {
                        simpleData: {
                            enable: true
                        }
                    },
                    callback: {
                        onClick: fromTypeTree.clickNode
                    }
                };
                // 初始化目录树
                var zTree = $.fn.zTree.init($("#commonFormTree", navTab.getCurrentPanel()), formTreeSetting, treeJson);
            } else {
                alertMsg.warn(jsonData.errMsg);
            }
        }, true, false);
    },

    clickNode: function (event, treeId, treeNode) {
        var ztree = $.fn.zTree.getZTreeObj(treeId);
        var tipObj = $("#openTip", $.pdialog.getCurrent());
        if (tipObj.length == 0) {
            tipObj = $("#openTip", navTab.getCurrentPanel());
        }
        if (treeNode.formTypeCd) {
            $("input[name=formType]", navTab.getCurrentPanel()).val(treeNode.name);
            $("input[name=formTypeId]", navTab.getCurrentPanel()).val(treeNode.id);
            $(tipObj).remove();
        }
    }
};

/**
 * 界面初始化加载
 */
$(document).ready(function () {
    // 执行目录树初始化
    fromTypeTree.initFullTree();
});