var addrRegTree = {
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
        var objValue = $("input[name='objValue']", $.pdialog.getCurrent()).val();
        packet.data.add("prjCd", prjCd);
        packet.data.add("treeType", "1");
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
                    var addrRegTreeSetting = {
                            data: {
                                simpleData: {
                                    enable: true
                                }
                            },
                            callback: {
                                onClick: addrRegTree.clickNode,
                                onCheck: addrRegTree.regCheck
                            },
                            check: {
                                autoCheckTrigger: false,
                                enable: addrRegTree.isCheckEnable(),
                                chkStyle: "checkbox",
                                chkboxType: {"Y": "", "N": ""}
                            }
                        }
                        ;
                    // 初始化目录树
                    var zTree = $.fn.zTree.init($("#commonTreeContent"), addrRegTreeSetting, treeJson);
                }
                else {
                    alertMsg.warn(jsonData.data.errMsg);
                }
            }, true, false
        )
    },
    clickNode: function (event, treeId, treeNode) {
        var ztree = $.fn.zTree.getZTreeObj(treeId);
        if (addrRegTree.isCheckEnable()) {
            return false;
        } else {
            var tipObj = $("#openTip", $.pdialog.getCurrent());
            if (tipObj.length == 0) {
                tipObj = $("#openTip", navTab.getCurrentPanel());
            }
            //点选的时候，拼接所有地址  以/分开
            var clickObj = tipObj.data("clickObj");
            var rowObj = $(clickObj).parent();

            if (treeNode.useType == '3' || treeNode.useType == '2') {
                //点击 建筑才打开建筑信息
                //根据点击节点，拼接 reg  path
                var pNodeName = "";
                var pName = addrRegTree.getPNodeName(treeNode);
                var pNameArr = pName.split("/");
                pNameArr = pNameArr.reverse();
                pNameArr.push(treeNode.name);
                pName = pNameArr.join("/");
                $(rowObj).find("input[name=buildAddr]").val(pName.substring(1, pName.length));
                ph00301_input.showBuildInfo(treeNode.buldId);
                //根据点击区域改变 区域类型
                $("select[name=buildType]", navTab.getCurrentPanel()).val(treeNode.useType);
                ph00301_input.onBuildTypeChange();
                $(tipObj).remove();
            } else {
                $(rowObj).find("input[name=buildAddr]").val("");
                //$("input[name=buldUnitNum]", navTab.getCurrentPanel()).val("");
                //$("input[name=hsAddrTail]", navTab.getCurrentPanel()).val("");
            }
        }
    },

    getPNodeName: function (treeNode) {
        var pName = '';
        if (treeNode.getParentNode() != null && treeNode.getParentNode().level != '0') {
            pName = treeNode.getParentNode().name + "/" + addrRegTree.getPNodeName(treeNode.getParentNode());
        }

        return pName;
    },

    regCheck: function (event, treeId, treeNode) {
        var treeObj = $.fn.zTree.getZTreeObj(treeId);
        var parentNode = treeNode.getParentNode();
        if (parentNode) {
            addrRegTree.checkAllParent(treeObj, parentNode, false);
        }
        var childNodes = treeNode.children;
        addrRegTree.checkAllSub(treeObj, childNodes, false);
    },
    checkAllParent: function (treeObj, treeNode, checkStatus) {
        treeObj.checkNode(treeNode, checkStatus, true);
        if (treeNode.getParentNode()) {
            addrRegTree.checkAllParent(treeObj, treeNode.getParentNode(), checkStatus);
        }
    },
    checkAllSub: function (treeObj, subTreeNodeList, checkStatus) {
        if (subTreeNodeList) {
            for (var i = 0; i < subTreeNodeList.length; i++) {
                var child = subTreeNodeList[i];
                treeObj.checkNode(child, checkStatus, true);
                addrRegTree.checkAllSub(treeObj, child.children, checkStatus)
            }
        }
    },
    onConfim: function (obj) {
        var treeObj = $.fn.zTree.getZTreeObj("commonTreeContent");
        var nodes = treeObj.getCheckedNodes(true);
        var checkedNodes = "";
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
        $(obj).closest("div.tip").remove();
    }
}
;
// 执行目录树初始化
addrRegTree.initFullTree();
