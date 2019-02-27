orgTree = {
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
        var prjCdObj = $("input[name=orgPrjCd]", $.pdialog.getCurrent());
        if (prjCdObj.length == 0) {
            prjCdObj = $("input[name=orgPrjCd]", navTab.getCurrentPanel());
        }
        var prjCd = prjCdObj.val();
        packet.data.add("prjCd", prjCd);
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
                                onClick: orgTree.clickNode,
                                onCheck: orgTree.regCheck
                            },
                            check: {
                                autoCheckTrigger: false,
                                enable: orgTree.isCheckEnable(),
                                chkStyle: "checkbox",
                                chkboxType: {"Y": "", "N": ""}
                            }
                        }
                        ;
                    // 初始化目录树
                    var zTree = $.fn.zTree.init($("#orgTreeContainer"), pjOrgTreeSetting, treeJson);
                    var fromOp = $("input[name=fromOp]", $.pdialog.getCurrent()).val();
                    if (fromOp == 'wf003') {
                        var $orgContainer = $("#wf003OrgSpan", $.pdialog.getCurrent());
                        $($orgContainer).find("input[name=orgCd]").each(
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
        )
    },
    clickNode: function (event, treeId, treeNode) {
        if (orgTree.isCheckEnable()) {
            return false;
        } else {
            var tipObj = $("#openTip", $.pdialog.getCurrent());
            if (tipObj.length == 0) {
                tipObj = $("#openTip", navTab.getCurrentPanel());
            }
            var clickObj = tipObj.data("clickObj");
            var rowObj = $(clickObj).parent();
            $(rowObj).find("input[type=hidden]").val(treeNode.id);
            $(rowObj).find("input[type=text]").val(treeNode.name);
            var fromOp = $("input[name=fromOp]", tipObj).val();
            $(tipObj).remove();
            if (fromOp == "mb003") {
                mb003.changePicData();
            } else if (fromOp == "mb007") {
                mb007.changePicData();
            } else if (fromOp == "pj00502") {
                pj00502.changePicData();
            } else if (fromOp == "rp000") {
                rp000.refreshAll();
            }
        }
    },
    regCheck: function (event, treeId, treeNode) {
        var treeObj = $.fn.zTree.getZTreeObj(treeId);
        var parentNode = treeNode.getParentNode();
        if (parentNode) {
            orgTree.checkAllParent(treeObj, parentNode, false);
        }
        var childNodes = treeNode.children;
        orgTree.checkAllSub(treeObj, childNodes, false);
    },
    checkAllParent: function (treeObj, treeNode, checkStatus) {
        treeObj.checkNode(treeNode, checkStatus, true);
        if (treeNode.getParentNode()) {
            orgTree.checkAllParent(treeObj, treeNode.getParentNode(), checkStatus);
        }
    },
    checkAllSub: function (treeObj, subTreeNodeList, checkStatus) {
        if (subTreeNodeList) {
            for (var i = 0; i < subTreeNodeList.length; i++) {
                var child = subTreeNodeList[i];
                treeObj.checkNode(child, checkStatus, true);
                orgTree.checkAllSub(treeObj, child.children, checkStatus)
            }
        }
    },
    onConfim: function (obj) {
        var treeObj = $.fn.zTree.getZTreeObj("orgTreeContainer");
        var nodes = treeObj.getCheckedNodes(true);
        var checkedNodes = "";
        var fromOp = $("#openTip").find("input[name=fromOp]").val();
        var $orgContainer = null;
        if (fromOp == 'wf003') {
            $orgContainer = $("#wf003OrgSpan", $.pdialog.getCurrent());
            var hiddenSpan;
            $orgContainer.find("span").each(function (i) {
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
                    copyRow.find("input[name=orgCd]").val(node.id);
                    copyRow.append(node.name);
                    $orgContainer.append(copyRow);
                }
            }
        } else {
            if (nodes) {
                var tempArr = [];
                for (var i = 0; i < nodes.length; i++) {
                    tempArr.push(nodes[i].id);
                }
                checkedNodes = tempArr.toString();
            }
            var tipObj = $("#openTip", $.pdialog.getCurrent());
            var clickObj = tipObj.data("clickObj");
            var rowObj = $(clickObj).parent();
            var objName = $("input[name='objName']", $.pdialog.getCurrent()).val();
            $(rowObj).find("input[name='" + objName + "']").val(checkedNodes);
        }
        $(obj).closest("div.tip").remove();
    }
};
$(document).ready(function () {
    // 执行目录树初始化
    orgTree.initFullTree();
});

