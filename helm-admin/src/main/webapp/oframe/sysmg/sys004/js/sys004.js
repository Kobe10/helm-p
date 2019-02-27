sys004 = {
    uploadObj: null,
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
        // 重新加载
        var packet = new AJAXPacket();
        // 获取当前选中的组织树类型，传递项目编号获取组织树
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
                var cmpOrgTreeSetting = {
                    data: {
                        simpleData: {
                            enable: true
                        }
                    },
                    view: {
                        addHoverDom: sys004.addHoverDom,
                        removeHoverDom: sys004.removeHoverDom,
                        selectedMulti: false
                    },
                    callback: {
                        onClick: sys004.viewNode,
                        beforeDrop: sys004.beforeDrop
                    },
                    edit: {
                        drag: {
                            isCopy: false,
                            isMove: sys004.getEditAble()
                        },
                        enable: sys004.getEditAble(),
                        showRemoveBtn: false,
                        showRenameBtn: false
                    }
                };
                // 初始化目录树
                var zTree = $.fn.zTree.init($("#sys004Tree", navTab.getCurrentPanel()), cmpOrgTreeSetting, treeJson);
                // 显示根节点数据
                var rootNode = zTree.getNodesByFilter(function (node) {
                    return node.level == 0
                }, true);
                if (rootNode != null) {
                    zTree.selectNode(rootNode);
                    sys004.viewNode(null, "ph001Tree", rootNode);
                }
            } else {
                alertMsg.warn(jsonData.errMsg);
            }
        }, true, false);
    },

    /**
     * 悬浮增加添加事件并处理添加操作
     * @param treeId
     * @param treeNode
     */
    addHoverDom: function (treeId, treeNode) {
        var sObj = $("#" + treeNode.tId + "_span", navTab.getCurrentPanel());
        var prjCd = "0";
        var divArea = navTab.getCurrentPanel();
        // 是否可以增加子节点
        if (sys004.getAddAble() && $("#addBtn_" + treeNode.tId, divArea).length == 0) {
            var addStr = "<span class='button add' id='addBtn_" + treeNode.tId + "' title='添加组织' onfocus='this.blur();'/>";
            sObj.after(addStr);
            var btn = $("#addBtn_" + treeNode.tId);
            if (btn) btn.bind("click", function () {
                var url = getGlobalPathRoot() + "oframe/sysmg/sys004/sys004-initSubInfo.gv";
                $.pdialog.open(url, "sys004-initSubInfo", "增加组织",
                    {
                        mask: true, max: false, data: {cNodeId: treeNode.id}, maxable: false,
                        resizable: false, width: 600, height: 400,
                        sFunc: function (nodeInfos) {
                            // 更新节点信息
                            var zTree = $.fn.zTree.getZTreeObj("sys004Tree");
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
            });
        }
        // 修改子节点信息
        if (sys004.getEditAble() && $("#editBtn_" + treeNode.tId, divArea).length == 0) {
            var addStr = "<span class='button edit' id='editBtn_" + treeNode.tId + "' title='修改组织' onfocus='this.blur();'/>";
            sObj.after(addStr);
            var btn = $("#editBtn_" + treeNode.tId);
            if (btn) btn.bind("click", function () {
                var url = getGlobalPathRoot() + "oframe/sysmg/sys004/sys004-initBaseInfo.gv";
                $.pdialog.open(url, "sys004-initBaseInfo", "组织修改",
                    {
                        mask: true, data: {cNodeId: treeNode.id}, max: false,
                        maxable: false, resizable: false, width: 600, height: 300,
                        sFunc: function (nodeInfos) {
                            // 更新节点信息
                            var zTree = $.fn.zTree.getZTreeObj("sys004Tree");
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
            });
        }
        // 是否可以删除节点
        if (sys004.getDelAble() && $("#removeBtn_" + treeNode.tId, divArea).length == 0) {
            var addStr = "<span class='button marr5 remove' id='removeBtn_" + treeNode.tId + "' title='删除组织' buonfocus='this.blur();'/>";
            sObj.after(addStr);
            var btn = $("#removeBtn_" + treeNode.tId);
            if (btn) btn.bind("click", function () {
                alertMsg.confirm("您确定要删除该节点及其下的所有子节点吗?", {
                    okCall: function () {
                        sys004.removeNode(treeId, treeNode);
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
     * 删除树中的节点
     * @param treeId 树编号
     * @param treeNode 删除的树节点
     */
    beforeRemove: function (treeId, treeNode) {

    },

    /**
     * 删除节点
     * @param treeId 删除节点
     * @param treeNode
     */
    removeNode: function (treeId, treeNode) {
        var packet = new AJAXPacket();
        // 当前节点信息
        packet.data.add("nodeId", treeNode.id);
        var prjCd = "0";
        packet.data.add("prjCd", prjCd);
        // 提交界面展示
        packet.url = getGlobalPathRoot() + "oframe/sysmg/sys004/sys004-deleteNode.gv";
        // 处理删除
        core.ajax.sendPacketHtml(packet, function (response) {
            var data = eval("(" + response + ")");
            var isSuccess = data.success;
            if (isSuccess) {
                $.fn.zTree.getZTreeObj(treeId).removeNode(treeNode, false);
                $("#nodeContent", navTab.getCurrentPanel()).html("");
                initUI($("#nodeContent", navTab.getCurrentPanel()));
            } else {
                alertMsg.error(data.errMsg);
                return false;
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
        if (targetNode.id == "9999") {
            alertMsg.warn("只能保留一个根节点！");
            return false;
        }
        // 只考虑拖拽一个节点的情况
        var packet = new AJAXPacket();
        // 目录树类型
        var catalogType = $("input[name='catalogType']:checked").val();
        packet.data.add("catalogType", catalogType);
        // 当前节点信息
        packet.data.add("mNodeId", treeNodes[0].id);
        packet.data.add("tNodeId", targetNode.id);
        packet.data.add("moveType", moveType);
        var prjCd = $("select[name=sys004TreeType]", navTab.getCurrentPanel()).val();
        packet.data.add("prjCd", prjCd);
        // 提交界面展示
        packet.url = getGlobalPathRoot() + "oframe/sysmg/sys004/sys004-moveNode.gv";
        // 处理删除
        core.ajax.sendPacketHtml(packet, function (response) {
            var data = eval("(" + response + ")");
            var isSuccess = data.success;
            if (isSuccess) {
                $("#nodeContent", navTab.getCurrentPanel()).html("");
                initUI($("#nodeContent", navTab.getCurrentPanel()));
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
        sys004.showNode(treeNode.id, "edit");
    },

    showNode: function (cNodeId, method) {
        // value为用户选中的关键词
        var packet = new AJAXPacket();
        // 目录树类型
        packet.data.add("method", method);
        // 当前节点信息
        packet.data.add("cNodeId", cNodeId);
        // 组织节点
        var prjCd = 0;
        packet.data.add("prjCd", prjCd);
        // 提交界面展示
        packet.url = getGlobalPathRoot() + "oframe/sysmg/sys004/sys004-treeNode.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            $("#sys004001", navTab.getCurrentPanel()).html(response);
            initUI($("#sys004001", navTab.getCurrentPanel()));
        }, true);
    },

    /**
     * 保存节点信息
     */
    saveNode: function () {
        // 根据表单ID验证表单
        if (!$("#frm", navTab.getCurrentPanel()).valid()) {
            return false;
        }
        // 序列化表单
        var jsonData = $("#frm", navTab.getCurrentPanel()).serializeArray();
        // 目录树类型
        var packet = new AJAXPacket();
        packet.data.data = jsonData;
        var prjCd = "0";
        packet.data.add("prjCd", prjCd);
        // 提交界面展示
        packet.url = getGlobalPathRoot() + "oframe/sysmg/sys004/sys004-saveNode.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            var data = eval("(" + response + ")");
            var isSuccess = data.success;
            if (isSuccess) {
                var method = data.method;
                var nodeInfo = data.nodeInfo;
                if ("add" == method) {
                    var zTree = $.fn.zTree.getZTreeObj("sys004Tree");
                    var pNode = zTree.getNodeByParam("id", nodeInfo.pId, null);
                    zTree.addNodes(pNode, nodeInfo);
                } else {
                    // 更新节点信息
                    var zTree = $.fn.zTree.getZTreeObj("sys004Tree");
                    var uNode = zTree.getNodeByParam("id", nodeInfo.id, null);
                    // 合并修改信息
                    uNode = $.extend(uNode, uNode, nodeInfo);
                    zTree.updateNode(uNode);
                }
                // 保存成功
                alertMsg.correct("处理成功");
            } else {
                alertMsg.error(data.errMsg);
            }
        }, true);
    },

    /**
     * 打开岗位信息
     * @param clickObj 点击对象
     */
    openPosInfo: function (clickObj) {
        var clickTr = $(clickObj).parent().parent();
        var sys004PosTable = $("#sys004PosTable", navTab.getCurrentPanel());
        var index = sys004PosTable.find("tr").index(clickTr);
        // 获取
        var posId, posName, posType, posDesc;
        if (index > 0) {
            posId = $("input[name=posId]", clickTr).val();
            posName = $("input[name=posName]", clickTr).val();
            posType = $("input[name=posType]", clickTr).val();
            posDesc = $("input[name=posDesc]", clickTr).val();
        } else {
            posId = "";
            posName = "";
            posType = "";
            posDesc = "";
        }
        var url = getGlobalPathRoot() + "oframe/sysmg/sys004/sys004-initPosInfo.gv?prjCd=" + getPrjCd() + "&posId="
            + posId
            + "&clickIdx=" + index
            + "&posName=" + encodeURI(encodeURI(posName))
            + "&posType=" + posType
            + "&posDesc=" + encodeURI(encodeURI(posDesc));
        $.pdialog.open(url, "sys00402", "岗位信息",
            {mask: true, max: false, maxable: false, resizable: false, width: 750, height: 530});
    },

    /**
     * 保存岗位信息
     */
    savePosInfo: function () {
        var form = $("#sys004PosInfoFrm", $.pdialog.getCurrent());
        if (form.valid()) {
            var clickIdx = $("input[name=clickIdx]", $.pdialog.getCurrent()).val();
            // 信息需要保存到的Tr对象
            var updateTr;
            var sys004PosTable = $("#sys004PosTable", navTab.getCurrentPanel());
            if (clickIdx == 0) {
                // 增加行
                var hiddenRow = sys004PosTable.find("tr:eq(1)");
                updateTr = $(hiddenRow).clone().removeClass("hidden");
                // 获取行号
                sys004PosTable.append(updateTr);
                $(updateTr).initUI();
            } else {
                // 修改行数据
                updateTr = sys004PosTable.find("tr:eq(" + clickIdx + ")");
            }
            // 获取值
            var posName = $("input[name=posName]", $.pdialog.getCurrent()).val();
            var posTypeName = $("select[name=posType]", $.pdialog.getCurrent()).find("option:selected").text();
            var posDesc = $("textarea[name=posDesc]", $.pdialog.getCurrent()).val();
            // 设置值
            $("input[name=posId]", updateTr).val($("input[name=posId]", $.pdialog.getCurrent()).val());
            $("input[name=posName]", updateTr).val(posName);
            $("input[name=posType]", updateTr).val($("select[name=posType]", $.pdialog.getCurrent()).val());
            $("input[name=posDesc]", updateTr).val(posDesc);
            // 设置显示值
            updateTr.find("td:eq(0)").html(posName);
            updateTr.find("td:eq(1)").html(posTypeName);
            updateTr.find("td:eq(2)").html(posDesc);

            // 关闭当前对话框
            $.pdialog.closeCurrent();
        }
    },

    queryStaff: function () {
        Query.queryList("sys004OrgFrm", 'sys004_staff_list_print', "");
    },

    /**
     * 打开检索
     * @param conditionFieldArr 查询字段数组
     * @param conditionArr 查询条件数组
     * @param conditionValueArr 查询值数组
     */
    openQSch: function (clickObj) {
        if (!$(clickObj).hasClass("active")) {
            $("#sys004QSchDialog", navTab.getCurrentPanel()).show().attr("active", "on");
            $("#sys004QSchDialog input:eq(0)", navTab.getCurrentPanel()).focus();
        }
    },

    /**
     * 关闭检索
     * @param forceFlag 强制关闭
     */
    closeQSch: function (forceFlag) {
        $("#sys004QSchDialog", navTab.getCurrentPanel()).hide();
    },

    openAddStaff: function () {
        var orgId = $("input[name=orgId]", navTab.getCurrentPanel()).val();
        var url = getGlobalPathRoot() + "oframe/sysmg/sys002/sys002-info.gv?prjCd=" + getPrjCd() + "&method=add&orgId=" + orgId;
        $.pdialog.open(url, "sys00201", "员工信息", {mask: true, width: 800, height: 600});
    },

    openEditStaff: function (staffId) {
        var url = getGlobalPathRoot() + "oframe/sysmg/sys002/sys002-info.gv?prjCd=" + getPrjCd() + "&method=edit&staffId=" + staffId;
        $.pdialog.open(url, "sys00201", "员工信息", {mask: true, width: 800, height: 600, close: sys004.closeEditStaff});
    },

    closeEditStaff: function () {
        sys004.queryStaff();
        return true;
    },

    orgRoleInfo: function (obj) {
        var $this = $(obj);
        var cIdx = $this.prevAll("li").length;
        var textContainer = $("#sys004NavContext", navTab.getCurrentPanel()).find(">div").eq(cIdx);
        if (textContainer.html() == "") {
            // 加载项目组织
            // value为用户选中的关键词
            var packet = new AJAXPacket();
            // 组织节点
            packet.data.add("prjCd", $("input[name=prjCd]", navTab.getCurrentPanel()).val());
            packet.data.add("orgId", $("input[name=orgId]", navTab.getCurrentPanel()).val());
            // 提交界面展示
            packet.url = getGlobalPathRoot() + "oframe/sysmg/sys004/sys004-orgRole.gv";
            core.ajax.sendPacketHtml(packet, function (response) {
                textContainer.html(response);
                initUI(textContainer);
            });
        }
    },

    /**
     * 导入员工配置
     */
    startImport: function (obj) {
        var clickFileBtn = $(obj).parent().find("input[type=file]");
        clickFileBtn.attr("id", new Date().getTime());
        clickFileBtn.val("");
        clickFileBtn.unbind("change", sys004.importStaff);
        clickFileBtn.bind("change", sys004.importStaff);
        sys004.uploadObj = $(obj);
    },
    /**
     * 打开导入员工面板
     */
    importMb: function () {
        var url = getGlobalPathRoot() + "oframe/sysmg/sys004/sys004-openImport.gv?prjCd=" + getPrjCd();
        $.pdialog.open(url, "orgInfo", "员工导入",
            {mask: true, max: false, maxable: false, resizable: false, width: 600, height: 180});
    },

    /**
     * 导入员工
     */
    importStaff: function () {
        var orgImportFile = $("#orgImportFile", $.pdialog.getCurrent()).val();
        if (orgImportFile == "") {
            alertMsg.warn("请选择导入的文件");
            return;
        }
        var uploadURL = getGlobalPathRoot() + "oframe/sysmg/sys002/sys002-importStaff.gv?prjCd=" + getPrjCd();
        var ajaxbg = $("#background,#progressBar");
        ajaxbg.show();
        $.ajaxFileUpload({
            url: uploadURL,
            secureuri: false,
            fileElementId: "orgImportFile",
            dataType: 'json',
            success: function (data, status, fileElementId) {
                if (data.isSuccess) {
                    alertMsg.correct("处理成功");
                    $.pdialog.closeCurrent();
                    sys004.queryStaff();
                } else {
                    alertMsg.error(data.errMsg);
                }
                var ajaxbg = $("#background,#progressBar");
                ajaxbg.hide();
            }
        })
    }
};
/**
 * 界面初始化加载
 */
$(document).ready(function () {
    // 执行目录树初始化
    sys004.initFullTree();

    // 拖动屏幕切分效果
    $("div.split_line", navTab.getCurrentPanel()).mousedown(function (event) {
        $("div.split_line", navTab.getCurrentPanel()).jDrag({
            scop: true, cellMinW: 20,
            move: "horizontal",
            event: event,
            stop: function (moveObj) {
                var leftDiv = $("#sys004DivTree", navTab.getCurrentPanel());
                var rightDiv = $("#sys004001", navTab.getCurrentPanel());
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
    /**
     * 快速查询绑定回车事件
     */
    $("#sys004QSchDialog input", navTab.getCurrentPanel()).bind("keydown", function () {
        var $this = $(this);
        if (event.keyCode == 13) {
            if ($this.val() != "") {
                $("#sys004QSchDialog button.js_query", navTab.getCurrentPanel()).trigger("click");
            } else {
                event.keyCode = 9;
            }
        }
    });

    $("span.split_but", navTab.getCurrentPanel()).click(function (event) {
        var splitLine = $(this).parent();
        var leftMenu = splitLine.prev("div.left_menu");
        if (leftMenu.is(":visible")) {
            leftMenu.hide();
            splitLine.addClass("menu_hide");
            var rightDiv = $("#sys004001", navTab.getCurrentPanel());
            rightDiv.css("margin-left", "10px");
            var resizeGrid = $("div.j-resizeGrid", rightDiv);
            if (resizeGrid.length > 0) {
                resizeGrid.jResize();
            }
        } else {
            leftMenu.show();
            splitLine.removeClass("menu_hide");
            var rightDiv = $("#sys004001", navTab.getCurrentPanel());
            rightDiv.css("margin-left", leftMenu.width() + 10 + "px");
            var resizeGrid = $("div.j-resizeGrid", rightDiv);
            if (resizeGrid.length > 0) {
                resizeGrid.jResize();
            }
        }
    });
});

