var pj002 = {
    getEditAble: function () {
        var result = false;
        var obj = $("input[name='editAble']", navTab.getCurrentPanel());
        if (obj && $(obj).val() == "1") {
            result = true;
        }
        return result;
    },
    getAddAble: function () {
        var result = false;
        var obj = $("input[name='addAble']", navTab.getCurrentPanel());
        if (obj && $(obj).val() == "1") {
            result = true;
        }
        return result;
    },
    getDelAble: function () {
        var result = false;
        var obj = $("input[name='delAble']", navTab.getCurrentPanel());
        if (obj && $(obj).val() == "1") {
            result = true;
        }
        return result;
    },

    checkPrjAdded: function () {
        var prjCd = $("input[name=prjCd]", navTab.getCurrentPanel()).val();
        if (prjCd == "") {
            alertMsg.warn("请先保存项目基本信息");
            stopEvent(event);
            return false;
        } else {
            return true;
        }
    },

    prjOrg: function (obj) {
        if (pj002.checkPrjAdded()) {
            var $this = $(obj);
            var cIdx = $this.prevAll("li").length;
            var textContainer = $("#pj002NavContext", navTab.getCurrentPanel()).find(">div").eq(cIdx);
            if (textContainer.html() == "") {
                // 加载项目组织
                // value为用户选中的关键词
                var packet = new AJAXPacket();
                // 组织节点
                packet.data.add("prjCd", $("input[name=prjCd]", navTab.getCurrentPanel()).val());
                // 提交界面展示
                packet.url = getGlobalPathRoot() + "eland/pj/pj002/pj002-initOrg.gv";
                core.ajax.sendPacketHtml(packet, function (response) {
                    textContainer.html(response);
                    initUI(textContainer);
                });
            }
        }
    },

    prjRole: function (obj) {
        if (pj002.checkPrjAdded()) {
            var $this = $(obj);
            var cIdx = $this.prevAll("li").length;
            var textContainer = $("#pj002NavContext", navTab.getCurrentPanel()).find(">div").eq(cIdx);
            if (textContainer.html() == "") {
                // 加载项目组织
                // value为用户选中的关键词
                var packet = new AJAXPacket();
                // 组织节点
                packet.data.add("prjCd", $("input[name=prjCd]", navTab.getCurrentPanel()).val());
                // 提交界面展示
                packet.url = getGlobalPathRoot() + "eland/pj/pj002/pj002-initRole.gv";
                core.ajax.sendPacketHtml(packet, function (response) {
                    textContainer.html(response);
                    initUI(textContainer);
                });
            }
        }
    },

    prjStaff: function (obj) {
        if (pj002.checkPrjAdded()) {
            var $this = $(obj);
            var cIdx = $this.prevAll("li").length;
            var textContainer = $("#pj002NavContext", navTab.getCurrentPanel()).find(">div").eq(cIdx);
            if (textContainer.html() == "") {
                // 加载项目组织
                var packet = new AJAXPacket();
                // 组织节点
                packet.data.add("prjCd", $("input[name=prjCd]", navTab.getCurrentPanel()).val());
                // 提交界面展示
                packet.url = getGlobalPathRoot() + "eland/pj/pj002/pj002-initStaff.gv";
                core.ajax.sendPacketHtml(packet, function (response) {
                    textContainer.html(response);
                    initUI(textContainer);
                });
            }
        }
    },

    prjFunc: function (obj) {
        if (pj002.checkPrjAdded()) {
            var $this = $(obj);
            var cIdx = $this.prevAll("li").length;
            var textContainer = $("#pj002NavContext", navTab.getCurrentPanel()).find(">div").eq(cIdx);
            if (textContainer.html() == "") {
                // 加载项目组织
                var packet = new AJAXPacket();
                // 组织节点
                packet.data.add("prjCd", $("input[name=prjCd]", navTab.getCurrentPanel()).val());
                // 提交界面展示
                packet.url = getGlobalPathRoot() + "eland/pj/pj002/pj002-initFunc.gv";
                core.ajax.sendPacketHtml(packet, function (response) {
                    textContainer.html(response);
                    initUI(textContainer);
                });
            }
        }
    },

    prjParam: function (obj) {
        if (pj002.checkPrjAdded()) {
            var $this = $(obj);
            var cIdx = $this.prevAll("li").length;
            var textContainer = $("#pj002NavContext", navTab.getCurrentPanel()).find(">div").eq(cIdx);
            if (textContainer.html() == "") {
                // 加载项目组织
                var packet = new AJAXPacket();
                // 组织节点
                packet.data.add("prjCd", $("input[name=prjCd]", navTab.getCurrentPanel()).val());
                // 提交界面展示
                packet.url = getGlobalPathRoot() + "eland/pj/pj002/pj002-initParam.gv";
                core.ajax.sendPacketHtml(packet, function (response) {
                    textContainer.html(response);
                    initUI(textContainer);
                });
            }
        }
    },

    prjCtrl: function (obj) {
        if (pj002.checkPrjAdded()) {
            var $this = $(obj);
            var cIdx = $this.prevAll("li").length;
            var textContainer = $("#pj002NavContext", navTab.getCurrentPanel()).find(">div").eq(cIdx);
            if (textContainer.html() == "") {
                // 加载项目组织
                var packet = new AJAXPacket();
                // 组织节点
                packet.data.add("prjCd", $("input[name=prjCd]", navTab.getCurrentPanel()).val());
                // 提交界面展示
                packet.url = getGlobalPathRoot() + "eland/pj/pj002/pj002-initCtrl.gv";
                core.ajax.sendPacketHtml(packet, function (response) {
                    textContainer.html(response);
                    initUI(textContainer);
                });
            }
        }
    },

    orgFullTree: function () {
        // 重新加载
        var packet = new AJAXPacket();
        var prjCd = $("input[name=prjCd]", navTab.getCurrentPanel()).val();
        packet.data.add("prjCd", prjCd);
        packet.data.add("fullTree", "true");
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
                        view: {
                            addHoverDom: pj002.addHoverDom,
                            removeHoverDom: pj002.removeHoverDom,
                            selectedMulti: false
                        },
                        callback: {
                            onClick: pj002.clickNode,
                            beforeDrop: pj002.beforeDrop
                        },
                        edit: {
                            drag: {
                                isCopy: false,
                                isMove: pj002.getEditAble()
                            },
                            enable: pj002.getEditAble(),
                            showRemoveBtn: false,
                            showRenameBtn: false
                        }
                    };
                    // 初始化目录树
                    var zTree = $.fn.zTree.init($("#pj002OrgTree", navTab.getCurrentPanel()), pjOrgTreeSetting, treeJson);
                    // 显示根节点数据
                    var rootNode = zTree.getNodesByFilter(function (node) {
                        return node.level == 0
                    }, true);
                    if (rootNode != null) {
                        zTree.selectNode(rootNode);
                        pj002.clickNode(null, "pj002OrgTree", rootNode);
                    }

                }
                else {
                    alertMsg.warn(jsonData.errMsg);
                }
            }, true, false
        )
    },

    /**
     * 悬浮增加添加事件并处理添加操作
     * @param treeId
     * @param treeNode
     */
    addHoverDom: function (treeId, treeNode) {
        var sObj = $("#" + treeNode.tId + "_span", navTab.getCurrentPanel());
        var prjCd = $("input[name=prjCd]", navTab.getCurrentPanel()).val();
        var divArea = navTab.getCurrentPanel();
        // 是否可以增加子节点
        if (pj002.getAddAble() && $("#addBtn_" + treeNode.tId, divArea).length == 0) {
            var addStr = "<span class='button add' id='addBtn_" + treeNode.tId + "' title='添加组织' onfocus='this.blur();'/>";
            sObj.after(addStr);
            var btn = $("#addBtn_" + treeNode.tId);
            if (btn) btn.bind("click", function () {
                    var url = getGlobalPathRoot() + "oframe/sysmg/sys004/sys004-initSubInfo.gv";
                    $.pdialog.open(url, "pj002-initSubInfo", "增加组织",
                        {
                            mask: true, max: false, data: {cNodeId: treeNode.id, prjCd: prjCd},
                            maxable: false, resizable: false, width: 600, height: 400,
                            sFunc: function (nodeInfos) {
                                // 更新节点信息
                                var zTree = $.fn.zTree.getZTreeObj("pj002OrgTree");
                                for (var i = 0; i < nodeInfos.length; i++) {
                                    var nodeInfo = nodeInfos[i];
                                    var uNode = zTree.getNodeByParam("id", nodeInfo.id, null);
                                    if (uNode) {
                                        // 合并修改信息
                                        uNode = $.extend(uNode, uNode, nodeInfo);
                                        zTree.updateNode(uNode);
                                    } else {
                                        // 新增组织节点
                                        var pNode = zTree.getNodeByParam("id", nodeInfo.pId, null);
                                        zTree.addNodes(pNode, nodeInfo);
                                    }
                                }
                                return true;
                            }
                        });
                    return false;
                }
            );
        }

        // 是否可以删除节点
        if (pj002.getDelAble() && $("#removeBtn_" + treeNode.tId, divArea).length == 0) {
            var addStr = "<span class='button marr5 remove' id='removeBtn_" + treeNode.tId + "' title='删除组织' buonfocus='this.blur();'/>";
            sObj.after(addStr);
            var btn = $("#removeBtn_" + treeNode.tId);
            if (btn) btn.bind("click", function () {
                alertMsg.confirm("您确定要删除该节点及其下的所有子节点吗?", {
                    okCall: function () {
                        pj002.removeNode(treeId, treeNode);
                    }
                });
                return false;
            });
        }


    },

    /**
     * 移开光标删除事件
     * @param treeId  树编号
     * @param treeNode 选择节点编号
     */
    removeHoverDom: function (treeId, treeNode) {
        var divArea = navTab.getCurrentPanel();
        $("#addBtn_" + treeNode.tId, divArea).unbind().remove();
        $("#editBtn_" + treeNode.tId, divArea).unbind().remove();
        $("#removeBtn_" + treeNode.tId, divArea).unbind().remove();
    },


    /**
     * 删除节点
     * @param treeId 删除节点
     * @param treeNode
     */
    removeNode: function (treeId, treeNode) {
        var packet = new AJAXPacket();
        var prjCd = $("input[name=prjCd]", navTab.getCurrentPanel()).val();
        // 当前节点信息
        packet.data.add("nodeId", treeNode.id);
        packet.data.add("prjCd", prjCd);
        // 提交界面展示
        packet.url = getGlobalPathRoot() + "oframe/sysmg/sys004/sys004-deleteNode.gv";
        // 处理删除
        core.ajax.sendPacketHtml(packet, function (response) {
            var data = eval("(" + response + ")");
            var isSuccess = data.success;
            if (isSuccess) {
                $.fn.zTree.getZTreeObj(treeId).removeNode(treeNode, false);
            } else {
                alertMsg.error(data.errMsg);
                return false;
            }

        }, true);
    }
    ,

    /**
     * 移动节点-拖动放下节点时候触发
     * @param treeId  所在 zTree 的 treeId
     * @param treeNodes 被拖拽的节点 JSON 数据集合
     * @param targetNode treeNode 被拖拽放开的目标节点JSON 数据对象。
     * @param moveType  指定移动到目标节点的相对位置,"inner"：成为子节点，"prev"：成为同级前一个节点，"next"：成为同级后一个节点
     */
    beforeDrop: function (treeId, treeNodes, targetNode, moveType) {
        // 只考虑拖拽一个节点的情况
        var packet = new AJAXPacket();
        // 目录树类型
        var catalogType = $("input[name='catalogType']:checked").val();
        packet.data.add("catalogType", catalogType);
        // 当前节点信息
        packet.data.add("mNodeId", treeNodes[0].id);
        packet.data.add("tNodeId", targetNode.id);
        packet.data.add("moveType", moveType);
        var prjCd = $("input[name=prjCd]", navTab.getCurrentPanel()).val();
        packet.data.add("prjCd", prjCd);
        // 提交界面展示
        packet.url = getGlobalPathRoot() + "oframe/sysmg/sys004/sys004-moveNode.gv";
        // 处理删除
        core.ajax.sendPacketHtml(packet, function (response) {
            var data = eval("(" + response + ")");
            var isSuccess = data.success;
            if (isSuccess) {
                return true;
            } else {
                alertMsg.error(data.errMsg);
                return false;
            }
        }, false);
    },

    /**
     * 左侧区域树点击事件
     */
    clickNode: function (event, treeId, treeNode) {
        pj002.initOrgStaffInfo(treeNode.id);
    },

    initOrgStaffInfo: function (orgId) {
        // 加载项目组织
        // value为用户选中的关键词
        var packet = new AJAXPacket();
        // 组织节点
        packet.data.add("prjCd", $("input[name=prjCd]", navTab.getCurrentPanel()).val());
        packet.data.add("orgId", orgId);
        // 提交界面展示
        packet.url = getGlobalPathRoot() + "eland/pj/pj002/pj002-orgStaffInfo.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            var textContainer = $("#pj002OrgStaffInfoDiv", navTab.getCurrentPanel());
            textContainer.html(response);
            initUI(textContainer);
        });
    },

    /*
     * 保存项目基本信息
     * */
    savePrjBase: function () {
        var $form = $("#pj002form", navTab.getCurrentPanel());
        if ($form.valid()) {
            // 提交页面数据
            var url = getGlobalPathRoot() + "eland/pj/pj002/pj002-saveBaseInfo.gv";
            var ajaxPacket = new AJAXPacket(url);
            ajaxPacket.data.data = $form.serializeArray();
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var jsonData = eval("(" + response + ")");
                // 服务调用是否成功
                var isSuccess = jsonData.success;
                if (isSuccess) {
                    var nodeInfo = {
                        name: $("input[name=prjName]", $form).val(),
                        pId: $("input[name=ownOrg]", $form).val(),
                        iconSkin: "prj"
                    };
                    var method = jsonData.method;
                    if (method == "add") {
                        nodeInfo.id = "prjCd-" + jsonData.prjCd;
                        nodeInfo.prjCd = jsonData.prjCd;
                        var zTree = $.fn.zTree.getZTreeObj("pj001Tree");
                        if (zTree) {
                            var pNode = zTree.getNodeByParam("id", nodeInfo.pId, null);
                            zTree.addNodes(pNode, nodeInfo);
                            pj001.prjInfo(nodeInfo.prjCd);
                        } else {
                            // 刷新当前页面
                        }
                    } else {
                        var zTree = $.fn.zTree.getZTreeObj("pj001Tree");
                        if (zTree) {
                            nodeInfo.id = "prjCd-" + $("input[name=prjCd]", $form).val();
                            nodeInfo.prjCd = $("input[name=prjCd]", $form).val();
                            var cNode = zTree.getNodeByParam("id", nodeInfo.id, null);
                            if (cNode) {
                                // 合并修改信息
                                $.extend(cNode, nodeInfo);
                                zTree.updateNode(cNode);
                            }
                        }
                    }
                    alertMsg.correct("保存成功");
                } else {
                    alertMsg.error(jsonData.errMsg);
                }
            });
        }
    }
}
;
