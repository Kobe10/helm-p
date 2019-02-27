roleTree = {
    /**
     * 初始化化目录树
     */
    initFullTree: function () {
        // 重新加载
        var packet = new AJAXPacket();
        packet.url = getGlobalPathRoot() + "oframe/sysmg/sys004/sys004-orgRoleTreeInfo.gv?prjCd=" + getPrjCd();
        core.ajax.sendPacketHtml(packet, function (response) {
                var jsonData = eval("(" + response + ")");
                var treeJson = jsonData.resultMap.treeJson;
                // 服务调用是否成功
                var isSuccess = jsonData.success;
                if (isSuccess) {
                    // 目录树数据
                    // 左侧树的生成和回调
                    var pjroleTreeSetting = {
                        data: {
                            simpleData: {
                                enable: true
                            }
                        },
                        callback: {
                            onClick: roleTree.clickNode
                        },
                        check: {
                            autoCheckTrigger: false,
                            enable: true,
                            chkStyle: "checkbox",
                            chkboxType: {"Y": "", "N": ""}
                        }
                    };
                    // 初始化目录树
                    var zTree = $.fn.zTree.init($("#commonTreeContent"), pjroleTreeSetting, treeJson);
                    var fromOp = $("#openTip").find("input[name=fromOp]").val();
                    var $roleContainer = null;
                    if (fromOp == "sys017") {
                        // 业务规则选择
                        $roleContainer = $("#sys017RoleSpan", navTab.getCurrentPanel());
                        $($roleContainer).find("input[name=rhtId]").each(
                            function (i) {
                                if (i == 0) {
                                    return true;
                                } else {
                                    var node = zTree.getNodeByParam("id", $(this).val(), null);
                                    if (node) {
                                        zTree.checkNode(node, true, true);
                                    }
                                }
                            }
                        );
                    } else {
                        $roleContainer = $("#sys00201RoleSpan", $.pdialog.getCurrent());
                        $($roleContainer).find("input[name=roleCd]").each(
                            function (i) {
                                if (i == 0) {
                                    return true;
                                } else {
                                    var node = zTree.getNodeByParam("id", $(this).val(), null);
                                    if (node) {
                                        zTree.checkNode(node, true, true);
                                    }
                                }
                            }
                        );
                    }
                }
                else {
                    alertMsg.warn(jsonData.data.errMsg);
                }
            }, true, false
        );
    },

    roleCheck: function (event, treeId, treeNode) {
        var treeObj = $.fn.zTree.getZTreeObj(treeId);
        if (treeNode.getParentNode()) {
            roleTree.checkAllParent(treeObj, treeNode.getParentNode(), false);
        }
        var childNodes = treeNode.children;
        roleTree.checkAllSub(treeObj, childNodes, false);
    },

    checkAllParent: function (treeObj, treeNode, checkStatus) {
        treeObj.checkNode(treeNode, checkStatus, true);
        if (treeNode.getParentNode()) {
            roleTree.checkAllParent(treeObj, treeNode.getParentNode(), checkStatus);
        }
    },
    checkAllSub: function (treeObj, subTreeNodeList, checkStatus) {
        if (subTreeNodeList) {
            for (var i = 0; i < subTreeNodeList.length; i++) {
                var child = subTreeNodeList[i];
                treeObj.checkNode(child, checkStatus, true);
                roleTree.checkAllSub(treeObj, child.children, checkStatus)
            }
        }
    },
    onConfim: function (obj) {
        var treeObj = $.fn.zTree.getZTreeObj("commonTreeContent");
        var nodes = treeObj.getCheckedNodes(true);
        var fromOp = $("#openTip").find("input[name=fromOp]").val();
        var $roleContainer = null;
        if (fromOp == "sys017") {
            $roleContainer = $("#sys017RoleSpan", navTab.getCurrentPanel());
            var hiddenSpan;
            $roleContainer.find("span").each(function (i) {
                if (i == 0) {
                    hiddenSpan = $(this);
                    return true;
                } else {
                    $(this).remove();
                }
            });
            if (nodes) {
                for (var i = 0; i < nodes.length; i++) {
                    var node = nodes[i];
                    var copyRow = $(hiddenSpan).clone().removeClass("hidden");
                    copyRow.find("input[name=rhtId]").val(node.id);
                    copyRow.append(node.name);
                    $roleContainer.append(copyRow);
                }
            }
        } else {
            $roleContainer = $("#sys00201RoleSpan", $.pdialog.getCurrent());
            var hiddenSpan;
            $roleContainer.find("span").each(function (i) {
                if (i == 0) {
                    hiddenSpan = $(this);
                    return true;
                } else {
                    $(this).remove();
                }
            });
            if (nodes) {
                for (var i = 0; i < nodes.length; i++) {
                    var node = nodes[i];
                    var copyRow = $(hiddenSpan).clone().removeClass("hidden");
                    copyRow.find("input[name=roleCd]").val(node.id);
                    copyRow.append(node.name);
                    $roleContainer.append(copyRow);
                }
            }
        }
        $(obj).closest("div.tip").remove();
    }

}
;
// 执行目录树初始化
roleTree.initFullTree();
