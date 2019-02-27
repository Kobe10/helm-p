var regTree = {
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
        var prjCd = getPrjCd();
        var tipObj = $("#openTip");
        var objValue = $("input[name='objValue']", tipObj).val();
        var treeType = $("input[name='treeType']", tipObj).val();

        packet.data.add("prjCd", prjCd);
        packet.data.add("treeType", treeType);
        packet.data.add("objValue", objValue);
        packet.url = getGlobalPathRoot() + "eland/pj/pj004/pj004-treeInfo.gv";
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
                                onClick: regTree.clickNode,
                                onCheck: regTree.regCheck
                            },
                            check: {
                                autoCheckTrigger: false,
                                enable: regTree.isCheckEnable(),
                                chkStyle: "checkbox",
                                chkboxType: { "Y": "", "N": "" }
                            }
                        }
                        ;
                    // 初始化目录树
                    var zTree = $.fn.zTree.init($("#regTreeContent"), pjOrgTreeSetting, treeJson);
                }
                else {
                    alertMsg.warn(jsonData.data.errMsg);
                }
            }, true, false
        )
        ;
    },
    clickNode: function (event, treeId, treeNode) {
        if (regTree.isCheckEnable()) {
            return false;
        } else {
            var tipObj = $("#openTip", $.pdialog.getCurrent());
            if (tipObj.length == 0) {
                tipObj = $("#openTip", navTab.getCurrentPanel());
            }
            var rowObj = $(tipObj).parent();
            $(rowObj).find("input[type=hidden]").val(treeNode.id);
            $(rowObj).find("input[name=regName]").val(treeNode.name);
            var fromOp = $("input[name=fromOp]", tipObj).val();
            $(tipObj).remove();
            if (fromOp == "pj00502") {
                pj00502.changePicData();
            } else if (fromOp == "mb003") {
                mb003.changePicData();
            } else if (fromOp == "hs001") {
                (rowObj).find("input[name=rhtRegId]").val(treeNode.id);
            } else if (fromOp == "hs00106") {
                (rowObj).find("input[name=rhtRegId]").val(treeNode.id);
            } else if (fromOp == "hs00107") {
                (rowObj).find("input[name=rhtRegId]").val(treeNode.id);
            }
        }

    },
    regCheck: function (event, treeId, treeNode) {
        var treeObj = $.fn.zTree.getZTreeObj(treeId);
        var parentNode = treeNode.getParentNode();
        if (parentNode) {
            regTree.checkAllParent(treeObj, parentNode, false);
        }
        var childNodes = treeNode.children;
        regTree.checkAllSub(treeObj, childNodes, false);
    },
    checkAllParent: function (treeObj, treeNode, checkStatus) {
        treeObj.checkNode(treeNode, checkStatus, true);
        if (treeNode.getParentNode()) {
            regTree.checkAllParent(treeObj, treeNode.getParentNode(), checkStatus);
        }
    },
    checkAllSub: function (treeObj, subTreeNodeList, checkStatus) {
        if (subTreeNodeList) {
            for (var i = 0; i < subTreeNodeList.length; i++) {
                var child = subTreeNodeList[i];
                treeObj.checkNode(child, checkStatus, true);
                regTree.checkAllSub(treeObj, child.children, checkStatus)
            }
        }
    },
    onConfim: function (obj) {
        var treeObj = $.fn.zTree.getZTreeObj("commonTreeContent");
        var nodes = treeObj.getCheckedNodes(true);
        var checkedNodes = "";
        if (nodes) {
            var tempArr = new Array();
            for (var i = 0; i < nodes.length; i++) {
                tempArr.push(nodes[i].id);
            }
            checkedNodes = tempArr.toString();
        }
        var tipObj = $("#openTip", $.pdialog.getCurrent());
        if (!tipObj || tipObj.length == 0 || tipObj.html() == "") {
            tipObj = $("#openTip", navTab.getCurrentPanel());
        }
        var rowObj = $(tipObj).parent();
        var objName = $("input[name='objName']", tipObj).val();
        $(rowObj).find("input[name='" + objName + "']").val(checkedNodes);
        $(obj).closest("div.tip").remove();
    }
};
$(document).ready(function () {
    // 执行目录树初始化
    regTree.initFullTree();
});

