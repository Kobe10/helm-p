sys00202 = {
    /**
     * 初始化化目录树
     */
    initFullTree: function () {
        // 重新加载
        var packet = new AJAXPacket();
//
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
                            onClick: sys00202.clickNode
                        }
                    };
                    // 初始化目录树
                    var zTree = $.fn.zTree.init($("#commonTreeContent_org", $.pdialog.getCurrent), pjOrgTreeSetting, treeJson);
                }
                else {
                    alertMsg.warn(jsonData.errMsg);
                }
            }, true, false
        )
        ;
    },
    clickNode: function (event, treeId, treeNode) {
        var packet = new AJAXPacket();
        packet.data.add("prjCd", "0");
        packet.url = getGlobalPathRoot() + "oframe/sysmg/sys002/sys002-queryPosition.gv?orgId=" + treeNode.id;
        core.ajax.sendPacketHtml(packet, function (response) {
            $("#positionInfo", $.pdialog.getCurrent).html(response);
            initUI($("#positionInfo", navTab.getCurrentTabId()));
        }, true);
    },

    //勾选岗位并展示于 右侧已选岗位栏
    clickCheckItem: function (obj) {
        var $this = $(obj);
        var clickLi = $this.parent().clone(true);
        if ($this.attr("checked")) {
            var result = $("#selectedPos input", $.pdialog.getCurrent);
            $(clickLi).appendTo($("#selectedPos", $.pdialog.getCurrent));
        } else {
//            console.log($("#selectedPos", $.pdialog.getCurrent).html());
            var inputs = $("input[name=posId]", $("#selectedPos", $.pdialog.getCurrent));
            for (var i = 0; i < inputs.length; i++) {
                var obj1 = $(inputs[i]);
                if ($this.val() == obj1.val()) {
                    obj1.parent().remove();
                }
            }
        }
    },

    savePos: function () {
        $("tr.js_selectedPosition", $.pdialog.getCurrent).remove();
        //岗位信息保存 添加到员工页
        var inputPosIds = $("input[name=posId]", $("#selectedPos", $.pdialog.getCurrent));
        var inputPosNames = $("input[name=posName]", $("#selectedPos", $.pdialog.getCurrent));
        for (var i = 0; i < inputPosIds.length; i++) {
            var inputPosId = $(inputPosIds[i]);
            var inputPosName = $(inputPosNames[i]);
            var hiddenTr = $("tr.js_staffpos");
            var showTr = hiddenTr.clone(true);
            $("input[name=posId]", showTr).val(inputPosId.val());
            $("input[name=posName]", showTr).val(inputPosName.val());
            $("input[name=mainPos]", showTr).attr("name", "mainPos" + inputPosId.val());
            $("input[name=posOwner]", showTr).attr("name", "posOwner" + inputPosId.val());
            $(showTr).appendTo(hiddenTr.parent().last()).removeClass().addClass("js_selectedPosition");
        }
    }

}
;
// 执行目录树初始化
sys00202.initFullTree();
