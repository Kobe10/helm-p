pbTree = {
    /**
     * 初始化化目录树
     */
    initFullTree: function () {
        // 重新加载
        var packet = new AJAXPacket();
        var container = $("#pbTreeContainer");
        var treePageContainer = container.closest("div.js_pb_tree");
        var prjCd = $("input[name=prjCd]", treePageContainer).val();
        if (prjCd == "") {
            prjCd = getPrjCd();
        }
        var checkedIds = $("input[name='HouseInfo_pbTree']", navTab.getCurrentPanel()).val();
        var hsOwnerType = $("input[name='HouseInfo_hsOwnerType']:checked", navTab.getCurrentPanel()).val();
        packet.data.add("prjCd", prjCd);
        packet.data.add("checkedIds", checkedIds);
        packet.data.add("hsOwnerType", hsOwnerType);
        packet.url = getGlobalPathRoot() + "eland/ph/ph016/ph016-pbTree.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
                var jsonData = eval("(" + response + ")");
                var treeJson = jsonData.resultMap.treeJson;
                // 服务调用是否成功
                var isSuccess = jsonData.success;
                if (isSuccess) {
                    // 目录树数据
                    // 左侧树的生成和回调
                    var pjstaffTreeSetting = {
                        data: {
                            simpleData: {
                                enable: true
                            }
                        },
                        callback: {},
                        check: {
                            autoCheckTrigger: false,
                            chkDisabled: true,
                            enable: true,
                            chkStyle: "checkbox",
                            chkboxType: {"Y": "ps", "N": "ps"}
                        }
                    };
                    // 初始化目录树
                    var zTree = $.fn.zTree.init(container, pjstaffTreeSetting, treeJson);
                }
                else {
                    alertMsg.warn(jsonData.data.errMsg);
                }
            }, true, false
        )
    },

    onConfim: function (obj) {
        //1.清空数据
        var pbInput = $("#pbTreeInput", navTab.getCurrentPanel());
        var pbTree = $("input[name='HouseInfo_pbTree']", navTab.getCurrentPanel());
        pbInput.val("");
        pbTree.val("");
        var pbInputValues = [];
        var pbTreeValues = [];
        //2.获取checkedbox
        var treeObj = $.fn.zTree.getZTreeObj("pbTreeContainer");
        var nodes = treeObj.getCheckedNodes(true);
        if (nodes) {
            for (var i = 0; i < nodes.length; i++) {
                pbInputValues.push(nodes[i].name);
                pbTreeValues.push(nodes[i].id);
            }
            pbInput.val(pbInputValues.join(","));
            pbTree.val(pbTreeValues.join(","));
        }
        $(obj).closeTip();
    }
};
$(document).ready(function () {
    // 执行目录树初始化
    pbTree.initFullTree();
});

