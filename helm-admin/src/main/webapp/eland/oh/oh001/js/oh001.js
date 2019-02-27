var oh001 = {

    /**
     * 获取区域目录树
     * @returns {string}
     */
    getBdTreeId: function () {
        var regUseType = $("input[name=regUseType]", navTab.getCurrentPanel()).val();
        return "oh001Tree_" + regUseType;
    },

    /**
     * 获取区域目录树
     * @returns {string}
     */
    getHxTreeId: function () {
        var regUseType = $("input[name=regUseType]", navTab.getCurrentPanel()).val();
        return "oh001HxTree_" + regUseType;
    },

    /**
     * 调整管理组织
     */
    switchTree: function (obj) {
        var $obj = $(obj);
        var textSpan = $obj.parent().find("span.panel_title");
        var spanText = textSpan.html();
        var oh001BdTreeContain = $("#" + oh001.getBdTreeId(), navTab.getCurrentPanel());
        var oh001HxTreeContain = $("#" + oh001.getHxTreeId(), navTab.getCurrentPanel());
        if ("楼 宇 导 航" === spanText) {
            spanText = "户 型 导 航";
            oh001HxTreeContain.show();
            oh001BdTreeContain.hide();
            if (oh001HxTreeContain.is(":empty")) {
                oh001.initHxTree();
            }
            // 保存cookie
            setPCookie("showFyTreeModel", "hx", "/");
        } else {
            spanText = "楼 宇 导 航";
            oh001BdTreeContain.show();
            oh001HxTreeContain.hide();
            if (oh001BdTreeContain.is(":empty")) {
                oh001.initFullRegTree();
            }
            // 保存cookie
            setPCookie("showFyTreeModel", "bd", "/");
        }
        textSpan.html(spanText);
    },

    /**
     * 导出建筑信息
     */
    exportbuild: function () {
        var regId = $("input[name=sltRegId]", navTab.getCurrentPanel()).val();
        var regUseType = $("input[name=regUseType]", navTab.getCurrentPanel()).val();
        var rhtType = $("input[name=rhtType]", navTab.getCurrentPanel()).val();
        var form = $('#oh00101frm', navTab.getCurrentPanel());
        var url = getGlobalPathRoot() + "eland/oh/oh001/oh00101-exportB.gv?prjCd=" + getPrjCd();
        var packet = new AJAXPacket(url, "数据导出处理中, 请稍候......");
        packet.data.add("prjCd", getPrjCd());
        packet.data.add("regId", regId);
        // 其他查询条件
        packet.data.add("conditionNames", $("input[name=conditionName]", form).val());
        packet.data.add("conditions", $("input[name=condition]", form).val());
        packet.data.add("conditionValues", $("input[name=conditionValue]", form).val());
        packet.data.add("regUseType", regUseType);
        packet.data.add("rhtType", rhtType);
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            var isSuccess = jsonData.success;
            if (isSuccess) {
                var downloadUrl = getGlobalPathRoot() + "oframe/common/file/file-down.gv?";
                downloadUrl = downloadUrl + "remoteFile=" + encodeURI(jsonData.data.remoteFile);
                downloadUrl = downloadUrl + "&clientFile=" + encodeURI(jsonData.data.clientFile);
                window.open(downloadUrl, "_self");
            } else {
                alertMsg.info(jsonData.errMsg);
            }
        }, true);
        packet = null;
    },

    /**
     * 导出建筑信息
     */
    exportByHx: function () {
        var regId = $("input[name=sltRegId]", navTab.getCurrentPanel()).val();
        var regUseType = $("input[name=regUseType]", navTab.getCurrentPanel()).val();
        var rhtType = $("input[name=rhtType]", navTab.getCurrentPanel()).val();
        var form = $('#oh00101frm', navTab.getCurrentPanel());
        var url = getGlobalPathRoot() + "eland/oh/oh001/oh00101-exportByHx.gv?prjCd=" + getPrjCd();
        var packet = new AJAXPacket(url, "数据导出处理中, 请稍候......");
        packet.data.add("prjCd", getPrjCd());
        packet.data.add("regId", regId);
        // 其他查询条件
        packet.data.add("conditionNames", $("input[name=conditionName]", form).val());
        packet.data.add("conditions", $("input[name=condition]", form).val());
        packet.data.add("conditionValues", $("input[name=conditionValue]", form).val());
        packet.data.add("regUseType", regUseType);
        packet.data.add("rhtType", rhtType);
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            var isSuccess = jsonData.success;
            if (isSuccess) {
                var downloadUrl = getGlobalPathRoot() + "oframe/common/file/file-down.gv?";
                downloadUrl = downloadUrl + "remoteFile=" + encodeURI(jsonData.data.remoteFile);
                downloadUrl = downloadUrl + "&clientFile=" + encodeURI(jsonData.data.clientFile);
                window.open(downloadUrl, "_self");
            } else {
                alertMsg.info(jsonData.errMsg);
            }
        }, true);
        packet = null;
    },

    /**
     * 导出建筑信息
     */
    exportByJs: function () {
        var regId = $("input[name=sltRegId]", navTab.getCurrentPanel()).val();
        var regUseType = $("input[name=regUseType]", navTab.getCurrentPanel()).val();
        var rhtType = $("input[name=rhtType]", navTab.getCurrentPanel()).val();
        var form = $('#oh00101frm', navTab.getCurrentPanel());
        var url = getGlobalPathRoot() + "eland/oh/oh001/oh00101-exportByJs.gv?prjCd=" + getPrjCd();
        var packet = new AJAXPacket(url, "数据导出处理中, 请稍候......");
        packet.data.add("prjCd", getPrjCd());
        packet.data.add("regId", regId);
        // 其他查询条件
        packet.data.add("conditionNames", $("input[name=conditionName]", form).val());
        packet.data.add("conditions", $("input[name=condition]", form).val());
        packet.data.add("conditionValues", $("input[name=conditionValue]", form).val());
        packet.data.add("regUseType", regUseType);
        packet.data.add("rhtType", rhtType);
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            var isSuccess = jsonData.success;
            if (isSuccess) {
                var downloadUrl = getGlobalPathRoot() + "oframe/common/file/file-down.gv?";
                downloadUrl = downloadUrl + "remoteFile=" + encodeURI(jsonData.data.remoteFile);
                downloadUrl = downloadUrl + "&clientFile=" + encodeURI(jsonData.data.clientFile);
                window.open(downloadUrl, "_self");
            } else {
                alertMsg.info(jsonData.errMsg);
            }
        }, true);
        packet = null;
    },

    /**
     * 数据修改权限
     * @returns {boolean}
     */
    getEditAble: function () {
        var result = false;
        var obj = $("input[name='editAble']", navTab.getCurrentPanel());
        if (obj && $(obj).val() == "1") {
            result = true;
        }
        return result;
    },

    /**
     * 数据删除权限
     * @returns {boolean}
     */
    getRemoveAble: function () {
        var result = false;
        var obj = $("input[name='removeAble']", navTab.getCurrentPanel());
        if (obj && $(obj).val() == "1") {
            result = true;
        }
        return result;
    },

    /**
     * 数据移动权限
     * @returns {boolean}
     */
    getMoveAble: function () {
        var result = false;
        var obj = $("input[name='moveAble']", navTab.getCurrentPanel());
        if (obj && $(obj).val() == "1") {
            result = true;
        }
        return result;
    },

    /**
     * 悬浮增加添加事件并处理添加操作
     * @param treeId
     * @param treeNode
     */
    addHoverDom: function (treeId, treeNode) {
        var divArea = navTab.getCurrentPanel();
        var sObj = $("#" + treeNode.tId + "_span", divArea);
        var isBuld = false;
        if (treeNode.buldId && treeNode.buldId != '') {
            isBuld = true;
        }
        if (treeNode.level != 0 && oh001.getRemoveAble() && $("#removeBtn_" + treeNode.tId, divArea).length == 0) {
            var addStr = "<span class='button marr5 remove' id='removeBtn_" + treeNode.tId + "' title='删除区域' onfocus='this.blur();'/>";
            sObj.after(addStr);
            var btn = $("#removeBtn_" + treeNode.tId);
            if (btn) btn.bind("click", function () {
                alertMsg.confirm("您确定要删除该节点及其下的所有子节点吗?", {
                    okCall: function () {
                        oh001.removeReg(oh001.getBdTreeId(), treeNode);
                    }
                });
                return false;
            });
        }
        if (treeNode.level != 0 && oh001.getEditAble() && $("#eidtRegBtn_" + treeNode.tId, divArea).length == 0) {
            var addStr = "<span class='button marr5 edit' id='eidtRegBtn_" + treeNode.tId + "' title='修改区域' onfocus='this.blur();'/>";
            sObj.after(addStr);
            var btn = $("#eidtRegBtn_" + treeNode.tId);
            if (btn) btn.bind("click", function () {
                oh001.editReg(treeNode.id);
                return false;
            });
        }
        if (!isBuld && oh001.getEditAble() && $("#addRegBtn_" + treeNode.tId, divArea).length == 0 && treeNode.useType == "1") {
            var addStr = "<span class='button marr5 add' id='addRegBtn_" + treeNode.tId + "' title='新增区域' onfocus='this.blur();'/>";
            sObj.after(addStr);
            var btn = $("#addRegBtn_" + treeNode.tId);
            if (btn) btn.bind("click", function () {
                oh001.addReg(treeNode.id);
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
        $("#removeBtn_" + treeNode.tId, navTab.getCurrentPanel()).unbind().remove();
        $("#addRegBtn_" + treeNode.tId, navTab.getCurrentPanel()).unbind().remove();
        $("#eidtRegBtn_" + treeNode.tId, navTab.getCurrentPanel()).unbind().remove();
    },

    /**
     * 移动节点-拖动放下节点时候触发
     * @param treeId  所在 zTree 的 treeId
     * @param treeNodes 被拖拽的节点 JSON 数据集合
     * @param targetNode treeNode 被拖拽放开的目标节点JSON 数据对象。
     * @param moveType  指定移动到目标节点的相对位置,"inner"：成为子节点，"prev"：成为同级前一个节点，"next"：成为同级后一个节点
     */
    beforeDrop: function (treeId, treeNodes, targetNode, moveType) {
        // 跨节点移动
        if (moveType != 'inner') {
            if (treeNodes[0].pId != targetNode.pId) {
                alertMsg.warn("不允许跨节点移动");
                return false;
            }
        } else {
            if (treeNodes[0].id != targetNode.pId) {
                alertMsg.warn("不允许跨节点移动");
                return false;
            }
        }

        var regUseType = $("input[name=regUseType]", navTab.getCurrentPanel()).val();
        // 只考虑拖拽一个节点的情况
        var packet = new AJAXPacket();
        // 目录树类型
        var catalogType = $("input[name='catalogType']:checked").val();
        packet.data.add("catalogType", catalogType);
        // 当前节点信息
        packet.data.add("mNodeId", treeNodes[0].id);
        packet.data.add("tNodeId", targetNode.id);
        packet.data.add("moveType", moveType);
        packet.data.add("regUseType", regUseType);
        // 项目编码
        var prjCd = getPrjCd();
        packet.data.add("prjCd", prjCd);
        // 提交界面展示
        packet.url = getGlobalPathRoot() + "eland/oh/oh001/oh001-moveRegion.gv";
        var moveResult = false;
        // 处理删除
        core.ajax.sendPacketHtml(packet, function (response) {
            var data = eval("(" + response + ")");
            var isSuccess = data.success;
            if (isSuccess) {
                moveResult = true;
            } else {
                alertMsg.error(data.errMsg);
                moveResult = false;
            }
        }, false);
        return moveResult;
    },

    //初始化左侧区域树
    initFullRegTree: function () {
        // 获取区域类型
        var regUseType = $("input[name=regUseType]", navTab.getCurrentPanel()).val();
        // 重新加载
        var packet = new AJAXPacket();
        var prjCd = getPrjCd();
        packet.data.add("prjCd", prjCd);
        packet.data.add("treeType", regUseType);
        packet.data.add("objValue", "");
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
                            onClick: oh001.clickNode,
                            beforeDrop: oh001.beforeDrop,
                            beforeRemove: oh001.beforeRemove
                        },
                        view: {
                            addHoverDom: oh001.addHoverDom,
                            removeHoverDom: oh001.removeHoverDom,
                            selectedMulti: false
                        },
                        edit: {
                            drag: {
                                isCopy: false,
                                isMove: oh001.getMoveAble()
                            },
                            enable: oh001.getEditAble() || oh001.getMoveAble(),
                            showRemoveBtn: false,
                            showRenameBtn: false
                        }
                    };
                    // 初始化目录树
                    var zTree = $.fn.zTree.init($("#" + oh001.getBdTreeId(), navTab.getCurrentPanel()), pjOrgTreeSetting, treeJson);

                    // 显示根节点数据
                    var rootNode = zTree.getNodesByFilter(function (node) {
                        return node.level == 0
                    }, true);
                    if (rootNode != null) {
                        oh001.regHs(rootNode.id, "", "");
                    }
                }
                else {
                    alertMsg.warn(jsonData.data.errMsg);
                }
            }, true, false
        );
    },

    //初始化左侧区域树
    initHxTree: function () {
        // 获取区域类型
        var regUseType = $("input[name=regUseType]", navTab.getCurrentPanel()).val();
        // 重新加载
        var packet = new AJAXPacket();
        var prjCd = getPrjCd();
        packet.data.add("prjCd", prjCd);
        packet.data.add("treeType", regUseType);
        packet.data.add("objValue", "");
        packet.url = getGlobalPathRoot() + "eland/oh/oh001/oh001-hxTree.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
                var jsonData = eval("(" + response + ")");
                var treeJson = jsonData.resultMap.treeJson;
                // 服务调用是否成功
                var isSuccess = jsonData.success;
                if (isSuccess) {
                    // 目录树数据
                    // 左侧树的生成和回调
                    var hxTreeSetting = {
                        data: {
                            simpleData: {
                                enable: true
                            }
                        },
                        callback: {
                            onClick: oh001.clickNode
                        },
                        edit: {
                            drag: {
                                isCopy: false,
                                isMove: false
                            },
                            enable: false,
                            showRemoveBtn: false,
                            showRenameBtn: false
                        }
                    };
                    // 初始化目录树
                    var zTree = $.fn.zTree.init($("#" + oh001.getHxTreeId(), navTab.getCurrentPanel()), hxTreeSetting, treeJson);

                    // 显示根节点数据
                    var rootNode = zTree.getNodesByFilter(function (node) {
                        return node.level == 0
                    }, true);
                    if (rootNode != null) {
                        oh001.regHs(rootNode.id, "", "");
                    }
                }
                else {
                    alertMsg.warn(jsonData.data.errMsg);
                }
            }, true, false
        );
    },

    /**
     * 展开或显示区域树
     * @param obj
     */
    extendOrClose: function (obj) {
        var $span = $(obj);
        var zTree = $.fn.zTree.getZTreeObj(oh001.getBdTreeId());
        var zTreeHx = $.fn.zTree.getZTreeObj(oh001.getHxTreeId());
        if ($span.html() == "展开") {
            zTree.expandAll(true);
            zTreeHx.expandAll(true);
            $span.html("折叠");
        } else {
            zTree.expandAll(false);
            zTreeHx.expandAll(false);
            $span.html("展开");
        }

    },

    /**
     * 左侧区域树点击事件
     */
    clickNode: function (event, treeId, treeNode) {
        var hsHx = $("a.new-house", navTab.getCurrentPanel()).closest("li");
        if (treeNode.useType == "hsHx") {
            // 按照户型检索
            oh001.regHs(treeNode.areaId, treeNode.hsTp, treeNode.name);
            hsHx.attr("hxCd", treeNode.name).attr("regId", treeNode.areaId).show();
        } else if (treeNode.useType == "hsTp") {
            // 按照居室检索
            oh001.regHs(treeNode.areaId, treeNode.hsTp, "");
            hsHx.hide().attr("hxCd", "").attr("regId", "");
        } else {
            oh001.regHs(treeNode.id, "", "");
            hsHx.hide().attr("hxCd", "").attr("regId", "");
        }
    },

    /**
     * 左侧区域树点击事件
     */
    opHsHx: function (obj) {
        var regUseType = $("input[name=regUseType]", navTab.getCurrentPanel()).val();
        var $this = $(obj);
        var hxCd = $this.attr("hxCd");
        var regId = $this.attr("regId");
        url = getGlobalPathRoot() + "eland/pj/pj008/pj008-info.gv?regId=" + regId
            + "&hxCd=" + hxCd
            + "&regUseType=" + regUseType
            + "&prjCd=" + getPrjCd();
        $.pdialog.open(url, "pj00801", "户型信息", {mask: true, max: false, height: 600, width: 850});
    },

    /**
     * 添加区域
     * @param upRegId 上级区域节点
     * @param regId
     */
    addReg: function (upRegId) {
        if (regId == 0) {
            return;
        }
        var prjCd = getPrjCd();
        var regUseType = $("input[name=regUseType]", navTab.getCurrentPanel()).val();
        var regId = $("input[name=regId]", navTab.getCurrentPanel()).val();
        var url = getGlobalPathRoot() + "eland/oh/oh001/oh001-addReg.gv?"
            + "&upRegId=" + upRegId + "&prjCd=" + prjCd + "&regUseType=" + regUseType;
        $.pdialog.open(url, "oh001031", "区域信息",
            {mask: true, max: false, maxable: false, resizable: false, width: 800, height: 550});
    },

    /**
     * 修改区域
     * @param upRegId 上级区域节点
     * @param regId
     */
    editReg: function (regId) {
        if (regId == 0) {
            return;
        }
        var regUseType = $("input[name=regUseType]", navTab.getCurrentPanel()).val();
        var url = getGlobalPathRoot() + "eland/oh/oh001/oh001-editReg.gv?"
            + "&regId=" + regId + "&prjCd=" + getPrjCd() + "&regUseType=" + regUseType;
        $.pdialog.open(url, "oh001031", "区域信息",
            {mask: true, max: false, maxable: false, resizable: false, width: 800, height: 550});
    },
    /**
     * 保存区域信息
     */
    saveRegionInfo: function () {
        var frm = $("#oh00103Rgform", $.pdialog.getCurrent());
        if (frm.valid()) {
            var regId = $("input[name=regId]", $.pdialog.getCurrent()).val();
            var upRegId = $("input[name=upRegId]", $.pdialog.getCurrent()).val();
            var regName = $("input[name=regName]", $.pdialog.getCurrent()).val();
            var regDesc = $("textarea[name=regDesc]", $.pdialog.getCurrent()).val();
            var regAddr = $("input[name=regAddr]", $.pdialog.getCurrent()).val();
            var oldPrjOrgId = $("input[name=oldPrjOrgId]", $.pdialog.getCurrent()).val();
            var prjOrgId = $("input[name=prjOrgId]", $.pdialog.getCurrent()).val();
            var prjOrgName = $("input[name=prjOrgName]", $.pdialog.getCurrent()).val();
            var regUseType = $("input[name=regUseType]", $.pdialog.getCurrent()).val();
            var lastUpdateTime = $("input[name=lastUpdateTime]", $.pdialog.getCurrent()).val();
            var regEntityType = $("input[name=regEntityType]", $.pdialog.getCurrent()).val();
            // 可选区域
            var regAttr7 = $("input[name=regAttr7]:checked", $.pdialog.getCurrent()).val();
            // 0到3居室申请量
            var regAttr8 = $("input[name=regAttr8]", $.pdialog.getCurrent()).val();
            var regAttr9 = $("input[name=regAttr9]", $.pdialog.getCurrent()).val();
            var regAttr10 = $("input[name=regAttr10]", $.pdialog.getCurrent()).val();
            var regAttr11 = $("input[name=regAttr11]", $.pdialog.getCurrent()).val();
            var regAttr12 = $("input[name=regAttr12]", $.pdialog.getCurrent()).val();
            var regAttr13 = $("input[name=regAttr13]", $.pdialog.getCurrent()).val();
            var cfmUpdateOrg = false;

            if (regEntityType == "1" || regEntityType == "") {
                var packet = new AJAXPacket(getGlobalPathRoot() + "eland/pj/pj004/pj004-savePrjReg.gv");
            } else {
                var packet = new AJAXPacket(getGlobalPathRoot() + "eland/oh/oh001/oh001-saveReg.gv");
            }
            if (oldPrjOrgId != prjOrgId) {
                alertMsg.confirm(" 确认修改该区域下的所有子节点的归属组织?", {
                    okCall: function () {
                        cfmUpdateOrg = true;
                        // 查询参数
                        packet.data.add("regId", regId);
                        packet.data.add("prjCd", getPrjCd());
                        packet.data.add("upRegId", upRegId);
                        packet.data.add("regName", regName);
                        packet.data.add("regAddr", regAddr);
                        packet.data.add("regDesc", regDesc);
                        packet.data.add("prjOrgId", prjOrgId);
                        packet.data.add("oldPrjOrgId", oldPrjOrgId);
                        packet.data.add("cfmUpdateOrg", cfmUpdateOrg);
                        packet.data.add("prjOrgName", prjOrgName);
                        packet.data.add("regUseType", regUseType);
                        packet.data.add("regEntityType", regEntityType);
                        packet.data.add("lastUpdateTime", lastUpdateTime);
                        packet.data.add("regAttr7", regAttr7);
                        packet.data.add("regAttr8", regAttr8);
                        packet.data.add("regAttr9", regAttr9);
                        packet.data.add("regAttr10", regAttr10);
                        packet.data.add("regAttr11", regAttr11);
                        packet.data.add("regAttr12", regAttr12);
                        packet.data.add("regAttr13", regAttr13);

                        // 保存区域信息
                        core.ajax.sendPacketHtml(packet, function (response) {
                            var data = eval("(" + response + ")");
                            var isSuccess = data.success;
                            var regData = data.data;
                            if (isSuccess) {
                                if (regId == "") {
                                    var zTree = $.fn.zTree.getZTreeObj(oh001.getBdTreeId());
                                    var pNode = zTree.getNodeByParam("id", regData.upRegId, null);
                                    var nodeInfo = {
                                        "id": regData.regId, "pId": regData.upRegId,
                                        "checked": "false", "name": regData.regName, open: "true"
                                    };
                                    zTree.addNodes(pNode, nodeInfo);
                                } else {
                                    // 更新节点信息
                                    var zTree = $.fn.zTree.getZTreeObj(oh001.getBdTreeId());
                                    var uNode = zTree.getNodeByParam("id", regData.regId, null);
                                    var nodeInfo = {
                                        "id": regData.regId, "pId": regData.upRegId,
                                        "checked": "false", "name": regData.regName, open: "true"
                                    };
                                    // 合并修改信息
                                    uNode = $.extend(uNode, uNode, nodeInfo);
                                    zTree.updateNode(uNode);
                                }
                                // 关闭窗口
                                $.pdialog.closeCurrent();
                            } else {
                                alertMsg.error(data.errMsg);
                            }
                        }, true);
                    }
                });
            } else {

                // 查询参数
                packet.data.add("regId", regId);
                packet.data.add("prjCd", getPrjCd());
                packet.data.add("upRegId", upRegId);
                packet.data.add("regName", regName);
                packet.data.add("regAddr", regAddr);
                packet.data.add("regDesc", regDesc);
                packet.data.add("prjOrgId", prjOrgId);
                packet.data.add("oldPrjOrgId", oldPrjOrgId);
                packet.data.add("cfmUpdateOrg", cfmUpdateOrg);
                packet.data.add("prjOrgName", prjOrgName);
                packet.data.add("regUseType", regUseType);
                packet.data.add("regEntityType", regEntityType);
                packet.data.add("lastUpdateTime", lastUpdateTime);
                packet.data.add("regAttr7", regAttr7);
                packet.data.add("regAttr8", regAttr8);
                packet.data.add("regAttr9", regAttr9);
                packet.data.add("regAttr10", regAttr10);
                packet.data.add("regAttr11", regAttr11);
                packet.data.add("regAttr12", regAttr12);
                packet.data.add("regAttr13", regAttr13);

                // 保存区域信息
                core.ajax.sendPacketHtml(packet, function (response) {
                    var data = eval("(" + response + ")");
                    var isSuccess = data.success;
                    var regData = data.data;
                    if (isSuccess) {
                        if (regId == "") {
                            var zTree = $.fn.zTree.getZTreeObj(oh001.getBdTreeId());
                            var pNode = zTree.getNodeByParam("id", regData.upRegId, null);
                            var nodeInfo = {
                                "id": regData.regId, "pId": regData.upRegId,
                                "checked": "false", "name": regData.regName, open: "true"
                            };
                            zTree.addNodes(pNode, nodeInfo);
                        } else {
                            // 更新节点信息
                            var zTree = $.fn.zTree.getZTreeObj(oh001.getBdTreeId());
                            var uNode = zTree.getNodeByParam("id", regData.regId, null);
                            var nodeInfo = {
                                "id": regData.regId, "pId": regData.upRegId,
                                "checked": "false", "name": regData.regName, open: "true"
                            };
                            // 合并修改信息
                            uNode = $.extend(uNode, uNode, nodeInfo);
                            zTree.updateNode(uNode);
                        }
                        // 关闭窗口
                        $.pdialog.closeCurrent();
                    } else {
                        alertMsg.error(data.errMsg);
                    }
                }, true);
            }

        }
    },
    /**
     * 删除节点
     * @param treeId 删除节点
     * @param treeNode
     */
    removeReg: function (treeId, treeNode) {
        var regUseType = $("input[name=regUseType]", navTab.getCurrentPanel()).val();
        var packet = new AJAXPacket();
        // 当前节点信息
        packet.data.add("regId", treeNode.id);
        // 区域类型
        packet.data.add("regUseType", regUseType);
        // 区域归属项目
        var prjCd = getPrjCd();
        packet.data.add("prjCd", prjCd);
        // 提交界面展示
        packet.url = getGlobalPathRoot() + "eland/oh/oh001/oh001-deleteRegion.gv";
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
    },

//    /**
//     * 打开查询房源条件设置对话框
//     */
//    queryHsDialog: function () {
//        var conditionNames = $("input[name=conditionNames]", navTab.getCurrentPanel()).val();
//        var conditions = $("input[name=conditions]", navTab.getCurrentPanel()).val();
//        var conditionValues = $("input[name=conditionValues]", navTab.getCurrentPanel()).val();
//        var conditionValueTexts = $("input.js_conditionValueTexts", navTab.getCurrentPanel()).val();
//        var url = getGlobalPathRoot() + "oframe/common/page/page-condition.gv?prjCd=" + getPrjCd()
//            + "&conditionNames=" + encodeURIComponent(conditionNames)
//            + "&conditions=" + encodeURIComponent(conditions)
//            + "&conditionValues=" + encodeURIComponent(conditionValues)
//            + "&conditionValueTexts=" + encodeURIComponent(conditionValueTexts);
//        $.pdialog.open(url, "oh00102", "自定义查询",
//            {mask: true, max: false, maxable: false, resizable: false, width: 800, height: 600,
//                close: oh001.onChangeConditon }
//        );
//    },

    /**
     * 房源详细信息
     * @param newHsId 新房源编号
     */
    info: function (newHsId) {
        var url = getGlobalPathRoot() + "eland/oh/oh001/oh001-info.gv?prjCd=" + getPrjCd()
            + "&newHsId=" + newHsId;
        $.pdialog.open(url, "oh00103", "房源详情", {mask: true, max: true, resizable: true});
    },

    /**
     * 点击收藏
     * @param newCondition
     */
    clickFav: function (newCondition) {
        oh001.closeQSch(true);
        oh001.changeConditions(newCondition);
    },

    /**
     * 确认修改查询条件
     * @returns {boolean}
     */
    onChangeConditon: function () {
        var submintCondition = PageCondition.getSubmitCondition();
        if (submintCondition) {
            $("input[name=conditionNames]", navTab.getCurrentPanel()).val(submintCondition.conditionNames);
            $("input[name=conditions]", navTab.getCurrentPanel()).val(submintCondition.conditions);
            $("input[name=conditionValues]", navTab.getCurrentPanel()).val(submintCondition.conditionValues);
            $("input.js_conditionValueTexts", navTab.getCurrentPanel()).val(submintCondition.conditionValueTexts);
            oh001.refleshHs();
        }
        return true;
    },

    /**
     * 打开导入房源面板
     */
    importHouse: function () {
        var regUseType = $("input[name=regUseType]", navTab.getCurrentPanel()).val();
        var url = getGlobalPathRoot() + "eland/oh/oh001/oh001-openImport.gv?prjCd=" + getPrjCd()
            + "&regUseType=" + regUseType;
        $.pdialog.open(url, "oh00101", "房源导入",
            {mask: true, max: false, maxable: false, resizable: false, width: 600, height: 180});
    },

    /**
     * Excel 导入 Person
     */
    doImport: function () {
        var oHouseImportFile = $("#oHouseImportFile", $.pdialog.getCurrent).val();
        if (oHouseImportFile == "") {
            alertMsg.warn("请选择导入的文件");
            return;
        }
        var uploadURL = getGlobalPathRoot() + "eland/oh/oh001/oh001-importHs.gv?prjCd=" + getPrjCd()
            + "&regUseType=" + $("input[name=regUseType]", $.pdialog.getCurrent()).val();
        var ajaxbg = $("#background,#progressBar");
        ajaxbg.show();
        $.ajaxFileUpload({
                url: uploadURL,
                secureuri: false,
                fileElementId: "oHouseImportFile",
                dataType: 'json',
                success: function (data, status, fileElementId) {
                    if (data.success) {
                        alertMsg.correct("处理成功");
                        $.pdialog.closeCurrent();
                    } else {
                        alertMsg.error(data.errMsg);
                    }
                    var ajaxbg = $("#background,#progressBar");
                    ajaxbg.hide();
                }
            }
        );
    },
    /**
     * 调用通用查询查询房源数据
     * @param conditionFieldArr 查询字段数组
     * @param conditionArr 查询条件数组
     * @param conditionValueArr 查询值数组
     */
    queryHs: function (obj) {
        Page.queryCondition($("div.panelBar a.find", navTab.getCurrentPanel()), {
            changeConditions: oh001.changeConditions,
            formObj: $("#oh00101frm", navTab.getCurrentPanel())
        });
    },

    /**
     * 房源快速检索对话框打开
     * @param conditionFieldArr 查询字段数组
     * @param conditionArr 查询条件数组
     * @param conditionValueArr 查询值数组
     */
    openQSch: function (clickObj) {
        if (!$(clickObj).hasClass("active")) {
            $("#oh001QSchDialog", navTab.getCurrentPanel()).show().attr("active", "on");
            $("#oh001QSchDialog input:eq(0)", navTab.getCurrentPanel()).focus();
        }
    },

    /**
     * 离开检索按钮
     */
    leaveQSch: function () {
        setTimeout(function () {
            oh001.closeQSch(false);
        }, 300);
    },

    /**
     * 关闭检索
     * @param forceFlag 强制关闭
     */
    closeQSch: function (forceFlag) {
        var close = forceFlag;
        if (!forceFlag) {
            var active = $("#oh001QSchDialog", navTab.getCurrentPanel()).attr("active");
            if ("on" == active) {
                close = false;
            } else {
                close = true;
            }
        }
        if (close) {
            $("#oh001QSchDialog", navTab.getCurrentPanel()).hide();
        }
    },

    /**
     * 快速检索
     */
    qSchData: function () {
        var formObj = $('#oh00101frm', navTab.getCurrentPanel());
        var conditionName = $("input[name=conditionName]", formObj).val().split(",");
        var condition = $("input[name=condition]", formObj).val().split(",");
        var conditionValue = $("input[name=conditionValue]", formObj).val().split(",");
        $("#oh001QSchDialog", navTab.getCurrentPanel()).find("input").each(function () {
            var $this = $(this);
            var attrName = $this.attr("name");
            var attrValue = $this.val();
            if (!attrName || attrName == "") {
                return true;
            }
            var nameIdx = -1;
            for (var i = 0; i < conditionName.length; i++) {
                var temp = conditionName[i];
                if (temp == attrName) {
                    nameIdx = i;
                    break;
                }
            }

            if (nameIdx != -1) {
                if ($.trim(attrValue) == "") {
                    conditionName.splice(nameIdx, 1);
                    condition.splice(nameIdx, 1);
                    conditionValue.splice(nameIdx, 1);
                } else {
                    condition[nameIdx] = $this.attr("condition");
                    conditionValue[nameIdx] = $this.val();
                }

            } else if ($.trim(attrValue) != "") {
                conditionName.push($this.attr("name"));
                condition.push($this.attr("condition"));
                conditionValue.push($this.val());
            }
        });
        $("input[name=conditionName]", formObj).val(conditionName.join(","));
        $("input[name=condition]", formObj).val(condition.join(","));
        $("input[name=conditionValue]", formObj).val(conditionValue.join(","));
        oh001.closeQSch(true);
        oh001.refleshHs();
    },

    /**
     * 改变查询条件
     * @param newCondition
     */
    changeConditions: function (newCondition) {
        var formObj = $('#oh00101frm', navTab.getCurrentPanel());
        $("input[name=conditionName]", formObj).val(newCondition.conditionNames);
        $("input[name=condition]", formObj).val(newCondition.conditions);
        $("input[name=conditionValue]", formObj).val(newCondition.conditionValues);
        $("input.js_conditionValue", formObj).val(newCondition.conditionValueTexts);
        $("input[name=resultField]", formObj).val(newCondition.resultFields);
        // 排序字段
        $("input[name=sortColumn]", formObj).val(newCondition.sortColumn);
        $("input[name=sortOrder]", formObj).val(newCondition.sortOrder);
        oh001.refleshHs();
    },

    /**
     * 显示区域的房产信息
     * @param regId 区域ID
     * @param hsTp  居室类型
     * @param hsHx 户型名称
     */
    regHs: function (regId, hsTp, hsHx) {
        $("input[name=sltRegId]", navTab.getCurrentPanel()).val(regId);
        $("input[name=rhtRegId]", navTab.getCurrentPanel()).val(regId);
        // 刷新查询条件中的居室类型和户型
        var formObj = $('#oh00101frm', navTab.getCurrentPanel());
        var conditionName = $("input[name=conditionName]", formObj).val().split(",");
        var condition = $("input[name=condition]", formObj).val().split(",");
        var conditionValue = $("input[name=conditionValue]", formObj).val().split(",");
        // 处理HsTp
        var nameIdx = -1;
        for (var i = 0; i < conditionName.length; i++) {
            var temp = conditionName[i];
            if (temp == "NewHsInfo.hsTp") {
                nameIdx = i;
                break;
            }
        }
        if (nameIdx != -1) {
            if (hsTp == "") {
                conditionName.splice(nameIdx, 1);
                condition.splice(nameIdx, 1);
                conditionValue.splice(nameIdx, 1);
            } else {
                condition[nameIdx] = "=";
                conditionValue[nameIdx] = hsTp;
            }

        } else if (hsTp != "") {
            conditionName.push("NewHsInfo.hsTp");
            condition.push("=");
            conditionValue.push(hsTp);
        }
        // 处理HsHx
        var nameIdx = -1;
        for (var i = 0; i < conditionName.length; i++) {
            var temp = conditionName[i];
            if (temp == "NewHsInfo.hsHxName") {
                nameIdx = i;
                break;
            }
        }
        if (nameIdx != -1) {
            if (hsHx == "") {
                conditionName.splice(nameIdx, 1);
                condition.splice(nameIdx, 1);
                conditionValue.splice(nameIdx, 1);
            } else {
                condition[nameIdx] = "=";
                conditionValue[nameIdx] = hsHx;
            }

        } else if (hsHx != "") {
            conditionName.push("NewHsInfo.hsHxName");
            condition.push("=");
            conditionValue.push(hsHx);
        }
        // 更新查询条件
        $("input[name=conditionName]", formObj).val(conditionName.join(","));
        $("input[name=condition]", formObj).val(condition.join(","));
        $("input[name=conditionValue]", formObj).val(conditionValue.join(","));
        oh001.refleshHs();
    },

    /**
     * 统计信息
     */
    refleshHs: function () {
        // 查询参数
        var viewModel = $("input.js_query_model", navTab.getCurrentPanel()).val();
        var regId = $("input[name=sltRegId]", navTab.getCurrentPanel()).val();
        var regUseType = $("input[name=regUseType]", navTab.getCurrentPanel()).val();
        var rhtType = $("input[name=rhtType]", navTab.getCurrentPanel()).val();
        var form = $('#oh00101frm', navTab.getCurrentPanel());
        if (!viewModel) {
            viewModel = "1";
        }
        if (viewModel == "1") {
            Page.query(form, "");
        } else {
            // 重新加载
            var packet = new AJAXPacket();
            packet.data.add("prjCd", getPrjCd());
            packet.data.add("regId", regId);
            // 其他查询条件
            packet.data.add("conditionNames", $("input[name=conditionName]", form).val());
            packet.data.add("conditions", $("input[name=condition]", form).val());
            packet.data.add("conditionValues", $("input[name=conditionValue]", form).val());
            packet.data.add("regUseType", regUseType);
            packet.data.add("rhtType", rhtType);
            // 查询条件
            if (viewModel == "3") {
                packet.url = getGlobalPathRoot() + "eland/oh/oh001/oh001-initS.gv";
                core.ajax.sendPacketHtml(packet, function (response) {
                    $("#oh001Context", navTab.getCurrentPanel()).html(response);
                    initUI($("#oh001Context", navTab.getCurrentPanel()));
                });
            } else if (viewModel == "2") {
                packet.url = getGlobalPathRoot() + "eland/oh/oh001/oh00101-initB.gv";
                core.ajax.sendPacketHtml(packet, function (response) {
                    $("#oh001Context", navTab.getCurrentPanel()).html(response);
                    initUI($("#oh001Context", navTab.getCurrentPanel()));
                });
            }
        }
    },

    /**
     * 切换展示视图
     */
    changeShowModel: function (obj, model) {
        var $this = $(obj);
        var currentModelObj = $("input.js_query_model", navTab.getCurrentPanel());
        var currentModel = currentModelObj.val();
        if (currentModel == model) {
            return;
        }
        // 修改模式
        currentModelObj.val(model);
        $this.find("span").addClass("active");
        $this.siblings("li").find("span.active").removeClass("active");
        // 执行查询
        oh001.refleshHs();
    },

    adjustHeight: function () {
        var oh001DivTreeH = $("#oh001DivTree", navTab.getCurrentPanel()).height();
        var oh001SummaryH = $("#oh001Summary", navTab.getCurrentPanel()).height();
        var height = oh001DivTreeH - oh001SummaryH - 123;
        var gridScroller = $("#oh00101_list_print", navTab.getCurrentPanel()).find("div.gridScroller");
        gridScroller.css("height", height + "px");
        gridScroller.css("width", gridScroller.prev("div.gridHeader").width());
    },

    /**
     *  显示或隐藏目录
     */
    extendOrClosePanel: function (clickObj) {
        var $pucker = $(clickObj);
        var $content = $pucker.closest("div.panelHeader").next("div.panelContent");
        if ($pucker.hasClass("collapsable")) {
            $pucker.removeClass("collapsable").addClass("expandable");
            $content.hide();
            oh001.adjustHeight();
        } else {
            $pucker.removeClass("expandable").addClass("collapsable");
            $content.show();
            oh001.adjustHeight();
        }
    },

    /**
     * 删除外迁房源
     * @param newHsId 外迁房源编号
     */
    delete: function (newHsId) {
        alertMsg.confirm("确认要删除该房源数据吗?", {
            okCall: function () {
                var url = getGlobalPathRoot() + "eland/oh/oh001/oh001-delete.gv";
                var ajaxPacket = new AJAXPacket(url);
                ajaxPacket.data.add("newHsId", newHsId);
                ajaxPacket.data.add("prjCd", getPrjCd());
                core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                    var jsonData = eval("(" + response + ")");
                    // 服务调用是否成功
                    var isSuccess = jsonData.success;
                    if (isSuccess) {
                        alertMsg.correct("处理成功");
                        oh001.refleshHs();
                    } else {
                        alertMsg.error(jsonData.errMsg);
                    }
                });
            }
        });
    },

    /**
     * 计算实测总价
     */
    calRealPrice: function () {
        var frm = $("#oh00103Ohfrm", $.pdialog.getCurrent());
        var size = $("input[name=hsBldSize]", frm).val();
        var unPrice = $("input[name=hsUnPrice]", frm).val();
        var price = (parseFloat(size) * parseFloat(unPrice)).toFixed(2);
        if (price == "NaN") {
            price = 0;
        }
        $("input[name=hsSalePrice]", frm).val(price);
    },

    /**
     * 获取区域地址列表
     * @param obj
     * @returns {string}
     */
    getHxUrl: function (obj) {
        var regId = $("input[name=ttRegId]", $.pdialog.getCurrent()).val();
        return getGlobalPathRoot() + "eland/oh/oh001/oh001-hx.gv?prjCd=" + getPrjCd() + "&ttRegId=" + regId;
    },
    /**
     * 户型自动补偿查询
     * @returns 返回自动补全参数
     */
    getHxOpt: function () {
        return {
            processData: function (result) {
                var myData = [];
                for (var i = 0; i < result.length; i++) {
                    var dataRow = {};
                    dataRow.data = result[i];
                    dataRow.value = result[i].value;
                    myData.push(dataRow);
                }
                return myData;
            },
            showResult: function (value, data) {
                return data.value;
            },
            displayValue: function (value, data) {
                return data.value;
            },
            remoteDataType: "json",
            delay: 300,
            minChars: 0,
            filterResults: false,
            onItemSelect: function (obj) {
                var data = obj.data;
                var source = $(obj.source);
                source.parent().find("input[type=hidden]").val(data.key);
            },
            onNoMatch: function (obj) {
                var source = $(obj.source);
                source.parent().find("input[type=hidden]").val("");
            }
        }
    },
    /**
     * 保存外迁房源
     */
    saveOHs: function () {
        var frm = $("#oh00103Ohfrm", $.pdialog.getCurrent());
        if (frm.valid()) {
            // 提交页面数据
            var url = getGlobalPathRoot() + "eland/oh/oh001/oh001-save.gv";
            var ajaxPacket = new AJAXPacket(url);
            var jsonData = frm.serializeArray();
            jsonData.push({"name": "prjCd", "value": getPrjCd()});
            jsonData.push({
                "name": "hsHxName",
                "value": $("select[name=hsHxId]", $.pdialog.getCurrent()).find("option:selected").text()
            });
            ajaxPacket.data.data = jsonData;
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var jsonData = eval("(" + response + ")");
                // 服务调用是否成功
                var isSuccess = jsonData.success;
                if (isSuccess) {
                    alertMsg.correct("保存成功");
                    $.pdialog.closeCurrent();
                    oh001.queryHs();
                } else {
                    alertMsg.error(jsonData.errMsg);
                }
            });
        }
    },
    batchChange: function (obj) {
        var newHsIds = [];
        navTab.getCurrentPanel()
            .find(":checkbox[checked][name=newHsId]").each(
            function (i) {
                newHsIds.push($(this).val());
            }
        );
        if (newHsIds.length == 0) {
            alertMsg.warn("请选择要修改的房源");
        } else {
            var url = getGlobalPathRoot() + "eland/oh/oh001/oh001-initBEdit.gv?prjCd=" + getPrjCd()
                + "&newHsIds=" + newHsIds.join(",");
            $.pdialog.open(url, "oh00104", "批量修改房源",
                {mask: true, max: false, maxable: false, resizable: false, width: 800, height: 600}
            );
        }
    },
    /**
     * 房屋信息
     * @param buildId
     */
    openHouseView: function (hsId) {
        //打开详细房产详情页面
        var url = "eland/ph/ph003/ph003-init.gv?hsId=" + hsId;
        index.quickOpen(url, {opCode: "ph003-init", opName: "居民信息", fresh: true});
    }
};
$(document).ready(function () {
    // 拖动屏幕切分效果   如果要设置 cookie 在{里编写} cookieName: "cookieName"
    LeftMenu.init("oh001DivTree", "oh001Right", {cookieName: "showFyTree"});

    // 区域树导航模式
    var showFyTreeModel = $("input[name=showFyTreeModel", navTab.getCurrentPanel()).val();
    if ("bd" == showFyTreeModel) {
        oh001.initFullRegTree();
    } else {
        // 初始化左侧区域树
        oh001.switchTree($("span.js_reload", navTab.getCurrentPanel()));
    }
    // 显示左侧导航
    var showFyTree = $("input[name=showFyTree", navTab.getCurrentPanel()).val();
    if ("false" == showFyTree) {
        $("span.split_but", navTab.getCurrentPanel()).trigger("click");
    }

});
