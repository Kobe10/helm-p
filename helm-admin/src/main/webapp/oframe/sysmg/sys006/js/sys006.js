sys006 = {
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
    /**
     * 初始化化目录树
     */
    initFullTree: function () {
        // 清空界面
        $("#rhtTree", navTab.getCurrentPanel()).html("");
        $("#rhtRightContent", navTab.getCurrentPanel()).html("");
        // 重新加载
        var packet = new AJAXPacket();
        var rhtType = sys006.getRhtType();
        packet.data.add("rhtType", rhtType);
        packet.url = getGlobalPathRoot() + "oframe/sysmg/sys006/sys006-treeInfo.gv?prjCd=" + getPrjCd();
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
                        addHoverDom: sys006.addHoverDom,
                        removeHoverDom: sys006.removeHoverDom,
                        selectedMulti: false
                    },
                    callback: {
                        onClick: sys006.viewNode,
                        beforeDrop: sys006.beforeDrop,
                        beforeRemove: sys006.beforeRemove
                    },
                    edit: {
                        enable: sys006.getEditAble(),
                        showRemoveBtn: sys006.getDelAble(),
                        showRenameBtn: false
                    }
                };
                // 初始化目录树
                var zTree = $.fn.zTree.init($("#rhtTree"), pjOrgTreeSetting, treeJson);
                // 显示根节点数据
                var rootNode = zTree.getNodesByFilter(function (node) {
                    return node.level == 0
                }, true);
                if (rootNode != null) {
                    zTree.selectNode(rootNode);
                    sys006.viewNode(null, "ph001Tree", rootNode);
                }
            } else {
                alertMsg.warn(jsonData.data.errMsg);
            }
        }, true, false);
    },

    /**
     * 悬浮增加添加事件并处理添加操作
     * @param treeId
     * @param treeNode
     */
    addHoverDom: function (treeId, treeNode) {
        if (!sys006.getAddAble()) {
            return false;
        }
        var sObj = $("#" + treeNode.tId + "_span");
        if (treeNode.editNameFlag || $("#addBtn_" + treeNode.tId).length > 0) return;
        var addStr = "<span class='button add' id='addBtn_" + treeNode.tId
            + "' title='添加节点' onfocus='this.blur();'/>";
        sObj.after(addStr);
        var btn = $("#addBtn_" + treeNode.tId);
        if (btn) btn.bind("click", function () {
            var packet = new AJAXPacket();
            var nodeType = sys006.getRhtType();
            packet.data.add("pNodeId", treeNode.id);
            packet.data.add("method", "add");
            packet.data.add("nodeType", nodeType);
            packet.url = getGlobalPathRoot() + "oframe/sysmg/sys006/sys006-initAdd.gv?prjCd=" + getPrjCd();
            core.ajax.sendPacketHtml(packet, function (response) {
                $("#rhtRightContent").html(response);
                initUI($("#rhtRightContent"));
            }, true);
            return false;
        });
    },
    /**
     * 移开光标删除事件
     * @param treeId  树编号
     * @param treeNode 选择节点编号
     */
    removeHoverDom: function (treeId, treeNode) {
        $("#addBtn_" + treeNode.tId, navTab.getCurrentPanel()).unbind().remove();
    },

    /**
     * 删除树中的节点
     * @param treeId 树编号
     * @param treeNode 删除的树节点
     */
    beforeRemove: function (treeId, treeNode) {
        alertMsg.confirm("您确定要删除该节点及其下的所有子节点吗?", {
            okCall: function () {
                sys006.removeNode(treeId, treeNode);
            }
        });
        return false;
    },

    /**
     * 删除节点
     * @param treeId 删除节点
     * @param treeNode
     */
    removeNode: function (treeId, treeNode) {
        var packet = new AJAXPacket();
        var nodeType = sys006.getRhtType();
        packet.data.add("nodeType", nodeType);
        // 当前节点信息
        packet.data.add("nodeId", treeNode.id);
        // 提交界面展示
        packet.url = getGlobalPathRoot() + "oframe/sysmg/sys006/sys006-deleteNode.gv?prjCd=" + getPrjCd();
        // 处理删除
        core.ajax.sendPacketHtml(packet, function (response) {
            var data = eval("(" + response + ")");
            var isSuccess = data.success;
            if (isSuccess) {
                $.fn.zTree.getZTreeObj(treeId).removeNode(treeNode, false);
                $("#rhtRightContent").html("");
                initUI($("#rhtRightContent"));
            } else {
                alertMsg.error(data.errMsg);
            }

        }, true);
    },

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
        var nodeType = sys006.getRhtType();
        packet.data.add("nodeType", nodeType);
        // 当前节点信息
        packet.data.add("mNodeId", treeNodes[0].id);
        packet.data.add("tNodeId", targetNode.id);
        packet.data.add("moveType", moveType);
        // 提交界面展示
        packet.url = getGlobalPathRoot() + "oframe/sysmg/sys006/sys006-moveNode.gv?prjCd=" + getPrjCd();
        // 处理删除
        core.ajax.sendPacketHtml(packet, function (response) {
            var data = eval("(" + response + ")");
            var isSuccess = data.success;
            if (isSuccess) {
                $("#rhtRightContent").html("");
                initUI($("#rhtRightContent"));
                return true;
            } else {
                alertMsg.error(data.errMsg);
                return false;
            }
        }, false);
    },

    /**
     * 点击查看目录节点
     * @param event js event对象
     * @param treeId  对应 zTree 的 treeId
     * @param treeNode  被点击的节点 JSON 数据对象
     * @param method 对节点的操作
     */
    viewNode: function (event, treeId, treeNode) {
        sys006.showNode(treeNode.id, "edit");
    },

    showNode: function (cNodeId, method) {
        $("#rhtRightContent", navTab.getCurrentPanel()).html("");
        // value为用户选中的关键词
        var packet = new AJAXPacket();
        // 目录树类型
        var nodeType = sys006.getRhtType();
        packet.data.add("method", method);
        // 当前节点信息
        packet.data.add("cNodeId", cNodeId);
        packet.data.add("nodeType", nodeType);
        // 提交界面展示
        packet.url = getGlobalPathRoot() + "oframe/sysmg/sys006/sys006-treeNode.gv?prjCd=" + getPrjCd();
        core.ajax.sendPacketHtml(packet, function (response) {
            $("#rhtRightContent").html(response);
            initUI($("#rhtRightContent"));
        }, true);
    },
    /**
     * 保存节点信息
     * @parma flag 1： 保存当前节点 2：复制新增节点
     */
    saveNode: function (flag) {
        // 根据表单ID验证表单
        if (!$("#frm", navTab.getCurrentPanel()).valid()) {
            return false;
        }
        // 判断保存还是复制新增
        if (2 == flag) {
            $("input[name=method]", navTab.getCurrentPanel()).val("add");
        } else if ($("input[name=cNodeId]", navTab.getCurrentPanel()).val() != "") {
            $("input[name=method]", navTab.getCurrentPanel()).val("update");
        }
        if ($("input[name=method]", navTab.getCurrentPanel()).val() != "add") {
            var oldCNodeCd = $("input[name=oldCNodeCd]", navTab.getCurrentPanel()).val();
            var cNodeCd = $("input[name=cNodeCd]", navTab.getCurrentPanel()).val();
            if (cNodeCd != oldCNodeCd) {
                alertMsg.confirm("你确定要修改当前资源编码，不是要新增资源?", {
                    okCall: function () {
                        sys006.doSaveNode();
                    }
                });
                return;
            }
        }
        // 执行真正的数据保存操作
        sys006.doSaveNode();

    },

    /**
     * 执行保存节点信息
     * @parma flag 1： 保存当前节点 2：复制新增节点
     */
    doSaveNode: function () {
        // 序列化表单
        var jsonData = $("#frm", navTab.getCurrentPanel()).serializeArray();
        // 目录树类型
        var nodeType = sys006.getRhtType();
        jsonData.push({
            name: 'nodeType',
            value: nodeType
        });
        var packet = new AJAXPacket();
        packet.data.data = jsonData;
        // 提交界面展示
        packet.url = getGlobalPathRoot() + "oframe/sysmg/sys006/sys006-saveNode.gv?prjCd=" + getPrjCd();
        core.ajax.sendPacketHtml(packet, function (response) {
            var data = eval("(" + response + ")");
            var isSuccess = data.success;
            if (isSuccess) {
                var method = data.method;
                var nodeInfo = data.nodeInfo;
                if ("add" == method) {
                    var zTree = $.fn.zTree.getZTreeObj("rhtTree");
                    var pNode = zTree.getNodeByParam("id", nodeInfo.pId, null);
                    zTree.addNodes(pNode, nodeInfo);
                } else {
                    // 更新节点信息
                    var zTree = $.fn.zTree.getZTreeObj("rhtTree");
                    var uNode = zTree.getNodeByParam("id", nodeInfo.id, null);
                    // 合并修改信息
                    uNode = $.extend(uNode, uNode, nodeInfo);
                    zTree.updateNode(uNode);
                }
                // 保存成功
                alertMsg.correct("处理成功");
                sys006.showNode(nodeInfo.id, "edit");
            } else {
                alertMsg.error(data.errMsg);
            }
        }, true);
    },
    /**
     * 获取权限类型
     * @returns {*}
     */
    getRhtType: function () {
        var obj = $("select[name='sys006RhtType']", navTab.getCurrentPanel());
        if (obj.length == 0) {
            obj = $("input[name='sys006RhtType']", navTab.getCurrentPanel());
        }
        return obj.val();
    }
};

/**
 * 界面初始化加载
 */
$(document).ready(function () {
    // 执行目录树初始化
    sys006.initFullTree();
    // 拖动屏幕切分效果
    $("div.split_line", navTab.getCurrentPanel()).mousedown(function (event) {
        $("div.split_line", navTab.getCurrentPanel()).jDrag({
            scop: true, cellMinW: 20,
            move: "horizontal",
            event: event,
            stop: function (moveObj) {
                var leftDiv = $("#sys006DivTree", navTab.getCurrentPanel());
                var rightDiv = $("#rhtRightContent", navTab.getCurrentPanel());
                var newWidth = $(moveObj).position().left;
                leftDiv.width($(moveObj).position().left);
                rightDiv.css("margin-left", (newWidth + 10) + "px");
                var resizeGrid = $("div.j-resizeGrid", rightDiv);
                if (resizeGrid.length > 0) {
                    resizeGrid.jResize();
                }
                $(moveObj).css("left", 0);
            }
        });
    });
    $("span.split_but", navTab.getCurrentPanel()).mousedown(function (event) {
        stopEvent(event);
    });

    $("span.split_but", navTab.getCurrentPanel()).click(function (event) {
        var splitLine = $(this).parent();
        var leftMenu = splitLine.prev("div.left_menu");
        if (leftMenu.is(":visible")) {
            leftMenu.hide();
            splitLine.addClass("menu_hide");
            var rightDiv = $("#rhtRightContent", navTab.getCurrentPanel());
            rightDiv.css("margin-left", "10px");
            var resizeGrid = $("div.j-resizeGrid", rightDiv);
            if (resizeGrid.length > 0) {
                resizeGrid.jResize();
            }
        } else {
            leftMenu.show();
            splitLine.removeClass("menu_hide");
            var rightDiv = $("#rhtRightContent", navTab.getCurrentPanel());
            rightDiv.css("margin-left", leftMenu.width() + 10 + "px");
            var resizeGrid = $("div.j-resizeGrid", rightDiv);
            if (resizeGrid.length > 0) {
                resizeGrid.jResize();
            }
        }
    });
});

