rhtTree = {
    /**
     * 初始化化目录树
     */
    initFullTree: function () {
        // 重新加载
        var packet = new AJAXPacket();
        // 权限编码
        var rhtType = $("select[name=rhtType]").val();
        if (rhtType == "") {
            rhtType = "1";
        }
        // 角色编码
        var roleCd = $("input[name=roleCd]").val();
        if (roleCd == "") {
            return;
        }
        packet.url = getGlobalPathRoot() + "oframe/sysmg/sys007/sys007-rhtInfo.gv";
        packet.data.add("rhtType", rhtType);
        packet.data.add("roleCd", roleCd);
        packet.data.add("prjCd", getPrjCd());
        core.ajax.sendPacketHtml(packet, function (response) {
                var jsonData = eval("(" + response + ")");
                var treeJson = jsonData.resultMap.treeJson;
                // 服务调用是否成功
                var isSuccess = jsonData.success;
//                console.log(treeJson);
                if (isSuccess) {
                    // 目录树数据
                    // 左侧树的生成和回调
                    var rhtTreeSetting = {
                        data: {
                            simpleData: {
                                enable: true
                            }
                        },
                        callback: {
                            onClick: rhtTree.clickNode,
                            onCheck: rhtTree.roleCheck
                        },
                        check: {
                            autoCheckTrigger: false,
                            enable: true,
                            chkStyle: "checkbox",
                            chkboxType: {"Y": "", "N": ""}
                        }
                    };
                    // 初始化目录树
                    var zTree = $.fn.zTree.init($("#commonTreeContent"), rhtTreeSetting, treeJson);
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
    },
    roleCheck: function (event, treeId, treeNode) {
        var treeObj = $.fn.zTree.getZTreeObj(treeId);
        var parentNode = treeNode.getParentNode();
        if (parentNode) {
            rhtTree.checkAllParent(treeObj, parentNode, false);
        }
        var childNodes = treeNode.children;
        rhtTree.checkAllSub(treeObj, childNodes, false);
    },
    checkAllParent: function (treeObj, treeNode, checkStatus) {
        treeObj.checkNode(treeNode, checkStatus, true);
        if (treeNode.getParentNode()) {
            rhtTree.checkAllParent(treeObj, treeNode.getParentNode(), checkStatus);
        }
    },
    checkAllSub: function (treeObj, subTreeNodeList, checkStatus) {
        if (subTreeNodeList) {
            for (var i = 0; i < subTreeNodeList.length; i++) {
                var child = subTreeNodeList[i];
                treeObj.checkNode(child, checkStatus, true);
                rhtTree.checkAllSub(treeObj, child.children, checkStatus)
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
        // 重新加载
        var packet = new AJAXPacket();
        // 权限编码
        var rhtType = $("select[name=rhtType]", navTab.getCurrentPanel()).val();
        if (rhtType == "") {
            rhtType = "1";
        }
        // 角色编码
        var roleCd = $("input[name=roleCd]").val();
        packet.url = getGlobalPathRoot() + "oframe/sysmg/sys007/sys007-saveRht.gv";
        packet.data.add("prjCd", getPrjCd());
        packet.data.add("rhtType", rhtType);
        packet.data.add("roleCd", roleCd);
        packet.data.add("newRhtIds", checkedNodes);
        core.ajax.sendPacketHtml(packet, function (response) {
                var jsonData = eval("(" + response + ")");
                // 服务调用是否成功
                var isSuccess = jsonData.success;
                if (isSuccess) {
                    $(obj).closest("div.tip").remove();
                    sys007.queryRht();
                }
                else {
                    alertMsg.warn(jsonData.errMsg);
                }
            }, true, false
        );
    }
};
// 执行目录树初始化
rhtTree.initFullTree();
