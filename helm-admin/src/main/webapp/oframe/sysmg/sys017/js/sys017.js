var sys017 = {

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

    //初始化左侧目录树
    initRuleTree: function () {
        // 重新加载
        var packet = new AJAXPacket();
        var prjCd = getPrjCd();
        packet.data.add("prjCd", prjCd);
        packet.url = getGlobalPathRoot() + "oframe/sysmg/sys017/sys017-ruleTypeTree.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
                var jsonData = eval("(" + response + ")");
                var treeJson = jsonData.resultMap.treeJson;
                // 服务调用是否成功
                var isSuccess = jsonData.success;
                if (isSuccess) {
                    // 目录树数据
                    // 左侧树的生成和回调
                    var pjOrgTreeSetting = {
                        data: {simpleData: {enable: true}},
                        callback: {onClick: sys017.clickNode, beforeDrop: sys017.beforeDrop},
                        view: {
                            addHoverDom: sys017.addHoverDom,
                            removeHoverDom: sys017.removeHoverDom,
                            selectedMulti: false
                        },
                        edit: {
                            drag: {isCopy: false, isMove: sys017.getMoveAble()},
                            enable: sys017.getMoveAble(), showRemoveBtn: false, showRenameBtn: false
                        },
                        check: {
                            autoCheckTrigger: false,
                            chkDisabled: true,
                            enable: true,
                            chkStyle: "checkbox",
                            chkboxType: {"Y": "s", "N": "s"}
                        }
                    };
                    // 初始化目录树
                    var zTree = $.fn.zTree.init($("#sys017Tree", navTab.getCurrentPanel()), pjOrgTreeSetting, treeJson);
                    var rootNode = zTree.getNodesByFilter(function (node) {
                        return node.level == 0
                    }, true);
                    // 显示规则目录信息
                    sys017.ruleTypeInfo(rootNode.id, rootNode.pId);
                }
                else {
                    alertMsg.warn(jsonData.data.errMsg);
                }
            }, true, false
        );
    },
    /**
     * 批量导出规则
     */
    batchExport: function () {
        var zTree = $.fn.zTree.getZTreeObj("sys017Tree");
        var nodes = zTree.getCheckedNodes();
        if (nodes.length == 0) {
            alertMsg.warn("请选择想要导出的规则");
        } else {
            var ruleCd = [];
            var rulePrjCd = [];
            for (var i = 0; i < nodes.length; i++) {
                if (!nodes[i].typeId) {
                    ruleCd.push(nodes[i].id);
                    rulePrjCd.push(nodes[i].rulePrjCd);
                }
            }
            var url = getGlobalPathRoot() + "oframe/sysmg/sys017/sys017-batchExport.gv";
            var ajaxPacket = new AJAXPacket(url, "数据导出处理中，请稍候......");
            ajaxPacket.data.add("ruleCd", ruleCd.toString());
            ajaxPacket.data.add("rulePrjCd", rulePrjCd.toString());
            ajaxPacket.data.add("prjCd", getPrjCd());
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var jsonData = eval("(" + response + ")");
                // 服务调用是否成功
                var isSuccess = jsonData.success;
                if (isSuccess) {
                    var downloadUrl = getGlobalPathRoot() + "oframe/common/file/file-down.gv??prjCd=" + getPrjCd();
                    downloadUrl = downloadUrl + "&remoteFile=" + encodeURI(jsonData.data.remoteFile);
                    downloadUrl = downloadUrl + "&clientFile=" + encodeURI(jsonData.data.clientFile);
                    window.open(downloadUrl, "_self");
                } else {
                    alertMsg.error(jsonData.errMsg);
                }
            }, true);
            ajaxPacket = null;
        }
    },

    /**
     * 悬浮增加添加事件并处理添加操作
     * @param treeId
     * @param treeNode
     */
    addHoverDom: function (treeId, treeNode) {
        var divArea = navTab.getCurrentPanel();
        var sObj = $("#" + treeNode.tId + "_span", divArea);
        var isRuleType = false;
        if (treeNode.typeId && treeNode.typeId != '') {
            isRuleType = true;
        }
        if (isRuleType && sys017.getEditAble() && $("#addRuleTypeBtn_" + treeNode.tId, divArea).length == 0) {
            var addStr = "<span class='button marr5 marl5 add' id='addRuleTypeBtn_" + treeNode.tId + "' title='新增分类' onfocus='this.blur();'/>";
            sObj.after(addStr);
            var btn = $("#addRuleTypeBtn_" + treeNode.tId);
            if (btn) btn.bind("click", function () {
                sys017.ruleTypeInfo("", treeNode.id);
                return false;
            });
        }
        if (isRuleType && sys017.getRemoveAble() && $("#removeRuleTypeBtn_" + treeNode.tId, divArea).length == 0) {
            var addStr = "<span class='button marr5 marl5 remove' id='removeRuleTypeBtn_" + treeNode.tId + "' title='删除分类' onfocus='this.blur();'/>";
            sObj.after(addStr);
            var btn = $("#removeRuleTypeBtn_" + treeNode.tId);
            if (btn) btn.bind("click", function () {
                // 删除规则目录
                var packet = new AJAXPacket();
                // 目录树类型
                // 当前节点信息
                packet.data.add("ruleTypeId", treeNode.id);
                packet.data.add("prjCd", getPrjCd());
                // 提交界面展示
                packet.url = getGlobalPathRoot() + "oframe/sysmg/sys017/sys017-deleteRuleType.gv";
                var moveResult = false;
                // 处理删除
                core.ajax.sendPacketHtml(packet, function (response) {
                    var data = eval("(" + response + ")");
                    var isSuccess = data.success;
                    if (isSuccess) {
                        var zTree = $.fn.zTree.getZTreeObj("sys017Tree");
                        var pNode = zTree.getNodeByParam("id", treeNode.pId, null);
                        zTree.removeNode(treeNode, false);
                        // 显示父级节点
                        sys017.ruleTypeInfo(pNode.id, pNode.pId);
                    } else {
                        alertMsg.error(data.errMsg);
                        moveResult = false;
                    }
                }, false);
            });
        }

    },

    /**
     * 移开光标删除事件
     * @param treeId  树编号
     * @param treeNode 选择节点编号
     */
    removeHoverDom: function (treeId, treeNode) {
        $("#removeRuleTypeBtn_" + treeNode.tId, navTab.getCurrentPanel()).unbind().remove();
        $("#addRuleTypeBtn_" + treeNode.tId, navTab.getCurrentPanel()).unbind().remove();
    },

    /**
     * 移动节点-拖动放下节点时候触发
     * @param treeId  所在 zTree 的 treeId
     * @param treeNodes 被拖拽的节点 JSON 数据集合
     * @param targetNode treeNode 被拖拽放开的目标节点JSON 数据对象。
     * @param moveType  指定移动到目标节点的相对位置,"inner"：成为子节点，"prev"：成为同级前一个节点，"next"：成为同级后一个节点
     */
    beforeDrop: function (treeId, treeNodes, targetNode, moveType) {
        //
        if (!targetNode.typeId) {
            return false;
        }
        // 只考虑拖拽一个节点的情况
        var packet = new AJAXPacket();
        // 当前节点信息
        packet.data.add("moveCd", treeNodes[0].id);
        packet.data.add("rulePrjCd", treeNodes[0].rulePrjCd);
        packet.data.add("moveToId", targetNode.id);
        packet.data.add("moveType", moveType);
        // 项目编码
        packet.data.add("prjCd", getPrjCd());
        if (treeNodes[0].typeId) {
            // 移动分类
            packet.url = getGlobalPathRoot() + "oframe/sysmg/sys017/sys017-moveRuleType.gv";
        } else {
            if (moveType != "inner") {
                return false;
            }
            // 移动规则
            packet.url = getGlobalPathRoot() + "oframe/sysmg/sys017/sys017-moveRule.gv";
        }

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

    /**
     * 显示区域目录树信息
     * @param ruleTypeId 需要显示的分类说明
     */
    ruleTypeInfo: function (ruleTypeId, upRuleTypeId) {
        // 只考虑拖拽一个节点的情况
        var packet = new AJAXPacket();
        // 目录树类型
        packet.data.add("prjCd", getPrjCd);
        // 当前节点信息
        packet.data.add("ruleTypeId", ruleTypeId);
        packet.data.add("upRuleTypeId", upRuleTypeId);
        // 提交界面展示
        packet.url = getGlobalPathRoot() + "oframe/sysmg/sys017/sys017-ruleTypeInit.gv";
        // 处理删除
        core.ajax.sendPacketHtml(packet, function (response) {
            var sys017ContextDiv = $("#sys017ContextDiv", navTab.getCurrentPanel());
            $("#sys017ContextDiv", navTab.getCurrentPanel()).html(response);
            initUI(sys017ContextDiv);
        });
    },

    /**
     * 保存规则分类
     */
    saveRuleType: function () {
        var $form = $("#sys017RuleTypeForm", navTab.getCurrentPanel());
        if ($form.valid()) {
            // 提交页面数据
            var url = getGlobalPathRoot() + "oframe/sysmg/sys017/sys017-saveRuleType.gv";
            var ajaxPacket = new AJAXPacket(url);
            ajaxPacket.data.data = $form.serializeArray();
            ajaxPacket.data.data.push({name: "prjCd", value: getPrjCd()});
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var jsonData = eval("(" + response + ")");
                // 服务调用是否成功
                var isSuccess = jsonData.success;
                if (isSuccess) {
                    alertMsg.correct("保存成功");
                    if (isSuccess) {
                        var zTree = $.fn.zTree.getZTreeObj("sys017Tree");
                        var nodeInfo = jsonData.data.node[0];
                        // 新增节点
                        if (jsonData.data.addFlag) {
                            var pNode = zTree.getNodeByParam("id", nodeInfo.pId, null);
                            zTree.addNodes(pNode, nodeInfo);
                            // 刷新当前目录
                            sys017.ruleTypeInfo(nodeInfo.id, nodeInfo.pId);
                        } else {
                            // 修改节点
                            var cNode = zTree.getNodeByParam("id", nodeInfo.id, null);
                            // 合并修改信息
                            cNode = $.extend(cNode, cNode, nodeInfo);
                            zTree.updateNode(cNode);
                        }

                    }
                } else {
                    alertMsg.error(jsonData.errMsg);
                }
            });
        }
    },

    /**
     * 检索房产
     * @param obj
     */
    quickSch: function () {
        var sys017DivTree = $("#sys017DivTree", navTab.getCurrentPanel());
        var conditionNameArr = new Array();
        var conditionArr = new Array();
        var conditionValueArr = new Array();
        var schType = $("select[name=schType]", sys017DivTree).val();
        var schValue = $("input[name=schValue]", sys017DivTree).val();
        conditionNameArr.push(schType);
        conditionArr.push("like");
        conditionValueArr.push(schValue);
        var sys017QueryForm = $("#sys017QueryForm", navTab.getCurrentPanel());
        $("input[name=conditionName]", sys017QueryForm).val(conditionNameArr.join(","));
        $("input[name=condition]", sys017QueryForm).val(conditionArr.join(","));
        $("input[name=conditionValue]", sys017QueryForm).val(conditionValueArr.join(","));
        // 查询处理结果
        Page.query(sys017QueryForm, "");
    },

    /**
     * 检索结果查询
     * @param ruleId 规则编号
     * @param ruleTypeId 规则分类
     */
    quickRsltClick: function (ruleCd, ruleTypeId, rulePrjCd, ruleId) {
        // 只有一把版本删除节点，刷新分类
        var zTree = $.fn.zTree.getZTreeObj("sys017Tree");
        var rNode = zTree.getNodeByParam("id", ruleCd, null);
        if (!rNode) {
            rNode = zTree.getNodeByParam("id", ruleTypeId, null);
        }
        if (rNode) {
            zTree.selectNode(rNode);
            if (rNode.typeId) {
                // 刷新规则类型下的规则
                sys017.reloadRuleTypeRule(rNode.id);
            }
        }
        sys017.ruleInfo(ruleCd, ruleTypeId, rulePrjCd, ruleId);
    },

    /**
     * 删除快速检索结果
     */
    closeQuickRslt: function () {
        $("#sys017RuleList>div", navTab.getCurrentPanel()).hide();
    },

    /**
     * 显示规则详情
     */
    ruleInfo: function (ruleCd, ruleTypeId, rulePrjCd, ruleId) {
        // 只考虑拖拽一个节点的情况
        var packet = new AJAXPacket();
        // 目录树类型
        packet.data.add("prjCd", getPrjCd());
        // 当前节点信息
        packet.data.add("ruleCd", ruleCd);
        packet.data.add("ruleTypeId", ruleTypeId);
        packet.data.add("rulePrjCd", rulePrjCd);
        packet.data.add("ruleId", ruleId);
        // 提交界面展示
        packet.url = getGlobalPathRoot() + "oframe/sysmg/sys017/sys017-ruleInit.gv";
        // 处理删除
        core.ajax.sendPacketHtml(packet, function (response) {
            var sys017ContextDiv = $("#sys017ContextDiv", navTab.getCurrentPanel());
            $("#sys017ContextDiv", navTab.getCurrentPanel()).html(response);
            initUI(sys017ContextDiv);
        });
    },

    /**
     *
     * @returns {*|jQuery}
     */
    getCode: function () {
        return $("textarea[name=ruleScript]", navTab.getCurrentPanel()).val();
    },


    /**
     * 显示规则详情
     */
    saveRuleInfo: function () {
        // 获取编辑后的规则
        var codeText = window.frames['sys017IFrame'].getCode();
        $("textarea[name=ruleScript]", navTab.getCurrentPanel()).val(codeText);
        // 提交规则保存
        var $form = $("#sys017RuleForm", navTab.getCurrentPanel());
        if ($form.valid()) {
            // 提交页面数据
            var url = getGlobalPathRoot() + "oframe/sysmg/sys017/sys017-saveRuleInfo.gv";
            var ajaxPacket = new AJAXPacket(url);
            ajaxPacket.data.data = $form.serializeArray();
            ajaxPacket.data.data.push({name: "prjCd", value: getPrjCd()});
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var jsonData = eval("(" + response + ")");
                // 服务调用是否成功
                var isSuccess = jsonData.success;
                if (isSuccess) {
                    alertMsg.correct("保存成功");
                    if (isSuccess) {
                        var zTree = $.fn.zTree.getZTreeObj("sys017Tree");
                        var nodeInfo = jsonData.data.nodeInfo;
                        // 新增节点
                        if (jsonData.data.addFlag) {
                            var pNode = zTree.getNodeByParam("id", nodeInfo.pId, null);
                            zTree.addNodes(pNode, nodeInfo);
                            // 刷新当前目录
                            sys017.ruleInfo(nodeInfo.id, nodeInfo.pId, nodeInfo.rulePrjCd, nodeInfo.ruleId);
                        } else {
                            // 修改节点
                            var ruleId = $("input[name=ruleId]", navTab.getCurrentPanel()).val();
                            var cNode = zTree.getNodeByParam("id", ruleId, null);
                            // 合并修改信息
                            cNode = $.extend(cNode, cNode, nodeInfo);
                            // 更新修改后的信息
                            zTree.updateNode(cNode);
                            // 刷新当前规则
                            sys017.ruleInfo(nodeInfo.id, nodeInfo.pId, nodeInfo.rulePrjCd, nodeInfo.ruleId);
                        }
                    }
                } else {
                    alertMsg.error(jsonData.errMsg);
                }
            });
        }
    },

    /**
     * 更新规则的状态
     * @param ruleId 规则编号
     * @param statusCd 规则状态
     */
    updateRuleStatus: function (ruleId, statusCd, ruleCd, rulePrjCd) {
        // 提交页面数据
        var url = getGlobalPathRoot() + "oframe/sysmg/sys017/sys017-updateRuleStatus.gv";
        var ajaxPacket = new AJAXPacket(url);
        ajaxPacket.data.add("prjCd", getPrjCd());
        ajaxPacket.data.add("ruleId", ruleId);
        ajaxPacket.data.add("statusCd", statusCd);
        core.ajax.sendPacketHtml(ajaxPacket, function (response) {
            var jsonData = eval("(" + response + ")");
            // 服务调用是否成功
            var isSuccess = jsonData.success;
            if (isSuccess) {
                alertMsg.correct("保存成功");
                // 刷新当前规则
                sys017.ruleInfo(ruleCd, '', rulePrjCd);
            } else {
                alertMsg.error(jsonData.errMsg);
            }
        });
    },

    /**
     * 更新规则的状态
     * @param ruleId 规则编号
     * @param statusCd 规则状态
     */
    deleteRule: function (ruleId, ruleTypeId) {
        alertMsg.confirm("你确定要删除？", {
            okCall: function () {
                // 提交页面数据
                var url = getGlobalPathRoot() + "oframe/sysmg/sys017/sys017-deleteRule.gv";
                var ajaxPacket = new AJAXPacket(url);
                ajaxPacket.data.add("prjCd", getPrjCd());
                ajaxPacket.data.add("ruleId", ruleId);
                core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                    var jsonData = eval("(" + response + ")");
                    // 服务调用是否成功
                    var isSuccess = jsonData.success;
                    if (isSuccess) {
                        alertMsg.correct("删除成功");
                        var versionLi = $("#versionMenu").find("ul li");
                        if (versionLi.length > 0) {
                            //刷新规则
                            var ruleCd = $("input[name=oldRuleCd]", navTab.getCurrentPanel()).val();
                            var rulePrjCd = $("input[name=oldPrjCd]", navTab.getCurrentPanel()).val();
                            sys017.ruleInfo(ruleCd, '', rulePrjCd);
                        } else {
                            // 只有一把版本删除节点，刷新分类
                            var zTree = $.fn.zTree.getZTreeObj("sys017Tree");
                            var rNode = zTree.getNodeByParam("id", ruleId, null);
                            zTree.removeNode(rNode, false);
                            // 显示节点目录
                            sys017.ruleTypeInfo(ruleTypeId, '');
                        }

                    } else {
                        alertMsg.error(jsonData.errMsg);
                    }
                });
            }
        });
    },

    /**
     * 导出规则
     * @param ruleId 需要导出的规则编号
     */
    exportRule: function (ruleId) {
        var url = getGlobalPathRoot() + "oframe/sysmg/sys017/sys017-exportRule.gv";
        var ajaxPacket = new AJAXPacket(url, "数据导出处理中，请稍候......");
        ajaxPacket.data.add("ruleId", ruleId);
        ajaxPacket.data.add("prjCd", getPrjCd());
        core.ajax.sendPacketHtml(ajaxPacket, function (response) {
            var jsonData = eval("(" + response + ")");
            // 服务调用是否成功
            var isSuccess = jsonData.success;
            if (isSuccess) {
                var downloadUrl = getGlobalPathRoot() + "oframe/common/file/file-down.gv??prjCd=" + getPrjCd();
                downloadUrl = downloadUrl + "&remoteFile=" + encodeURI(jsonData.data.remoteFile);
                downloadUrl = downloadUrl + "&clientFile=" + encodeURI(jsonData.data.clientFile);
                window.open(downloadUrl, "_self");
            } else {
                alertMsg.error(jsonData.errMsg);
            }
        }, true);
        ajaxPacket = null;
    },

    /**
     * 展开或显示区域树
     * @param obj
     */
    extendOrClose: function (obj) {
        var $span = $(obj);
        var zTree = $.fn.zTree.getZTreeObj("sys017Tree");
        if ($span.html() == "展开") {
            zTree.expandAll(true);
            $span.html("折叠");
        } else {
            zTree.expandAll(false);
            $span.html("展开");
        }
    },


    /**
     * 左侧区域树点击事件
     */
    clickNode: function (event, treeId, treeNode) {
        if (treeNode.typeId) {
            // 显示规则类型
            sys017.ruleTypeInfo(treeNode.id, treeNode.pId);
            // 刷新规则类型下的规则
            sys017.reloadRuleTypeRule(treeNode.id);
        } else {
            sys017.ruleInfo(treeNode.id, treeNode.pId, treeNode.rulePrjCd);
        }
    },

    /**
     * 刷新规则类型
     * @param ruleTypeId 规则类型编号
     * @param reloadFlag 强制刷新
     */
    reloadRuleTypeRule: function (ruleTypeId, reloadFlag) {
        var zTree = $.fn.zTree.getZTreeObj("sys017Tree");
        var pNode = zTree.getNodeByParam("id", ruleTypeId, null);
        var subNodes = zTree.getNodesByParam("pId", ruleTypeId, pNode);
        var ruleNodes = [];
        if (subNodes) {
            for (var i = 0; i < subNodes.length; i++) {
                if (!subNodes[i].typeId) {
                    ruleNodes.push(subNodes[i]);
                }
            }
        }
        // 规则类型已经加载
        if (ruleNodes.length > 0 && !reloadFlag) {
            return;
        }
        // 清空并重新加载规则
        if (ruleNodes.length > 0 && reloadFlag) {
            for (var i = 0; i < ruleNodes.length; i++) {
                zTree.removeNode(ruleNodes[i], false);
            }
        }
        // 重新加载
        var packet = new AJAXPacket();
        packet.data.add("prjCd", getPrjCd());
        packet.data.add("ruleTypeId", ruleTypeId);
        packet.url = getGlobalPathRoot() + "oframe/sysmg/sys017/sys017-ruleTypeNodes.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            var treeJson = jsonData.resultMap.treeJson;
            zTree.addNodes(pNode, treeJson, false);
        });
    },

    /**
     * 发布说明
     */
    importXml: function () {
        var ruleTypeId = $("input[name=ruleTypeId]", navTab.getCurrentPanel()).val();
        var url = getGlobalPathRoot() + "oframe/sysmg/sys017/sys017-openImport.gv?prjCd=" + getPrjCd() + "&ruleTypeId=" + ruleTypeId;
        $.pdialog.open(url, "sys017-openImport", "规则导入", {
            height: 165, width: 550, mask: true
        });
    },

    /**
     * 点击开始上传文件
     */
    startImport: function () {
        $("#uploadRuleFile", navTab.getCurrentPanel()).val("");
        $("#uploadRuleFile", navTab.getCurrentPanel()).unbind("change", sys017.importFile);
        $("#uploadRuleFile", navTab.getCurrentPanel()).bind("change", sys017.importFile);
    },

    /**
     * Excel 导入 Person
     */
    importFile: function (obj) {
        var importXmlFile = $("#sys017ImportXmlFile", $.pdialog.getCurrent).val();
        var toPrjCd = $("select[name=toPrjCd]", $.pdialog.getCurrent).val();
        if (importXmlFile == "") {
            alertMsg.warn("请选择导入的文件");
            return;
        }
        var ruleTypeId = $("input[name=ruleTypeId]", $.pdialog.getCurrent).val();
        var uploadURL = getGlobalPathRoot() + "oframe/sysmg/sys017/sys017-importRule.gv?prjCd=" + getPrjCd() + "&ruleTypeId=" + ruleTypeId
            + "&toPrjCd=" + toPrjCd
        $.ajaxFileUpload({
                url: uploadURL,
                secureuri: false,
                fileElementId: "sys017ImportXmlFile",
                dataType: 'json',
                data: {"importXmlFile":importXmlFile},
                success: function (data, status, fileElementId) {
                    if (data.success) {
                        // 导入成功后，根据导入的方式不同 提示样式不同
                        if (data.batUpdateRule.toString() != "") {
                            $("#sys017ContextDiv", navTab.getCurrentPanel()).html('<table class="border" style="position: relative; left:50%; -webkit-transform:translate(-50%,10%); transform:translate(-50%,10%); width: 50%;height:300px;text-align: center;"><tr style="height: 10%;"><td style="font-weight: bold">本次更新的规则编码为</td></tr><tr><td>' + data.batUpdateRule.toString() + '</td></tr></table>');
                            alertMsg.correct("导入规则成功");
                        } else {
                            alertMsg.correct("导入规则成功，更新的规则版本为" + data.sinUpdateRule);
                        }
                        $.pdialog.closeCurrent();
                    } else {
                        alertMsg.error(data.errMsg);
                    }
                }
            }
        )
    },

    /**
     * 授权角色
     * @param obj
     */
    editRole: function (obj) {
        var url = getGlobalPathRoot() + "oframe/sysmg/sys002/sys002-roleTree.gv?prjCd=" + getPrjCd() + "&fromOp=sys017";
        $(obj).openTip({href: url, height: "280", width: "250", offsetX: 0, offsetY: 30});
    }

};


$(document).ready(function () {
    //初始化左侧区域树
    sys017.initRuleTree();

    // 拖动调整规则现实内容
    $("div.split_line", navTab.getCurrentPanel()).mousedown(function (event) {
        $("div.split_line", navTab.getCurrentPanel()).jDrag({
            scop: true, cellMinW: 20,
            move: "horizontal",
            event: event,
            stop: function (moveObj) {
                var newWidth = $(moveObj).position().left;
                $("#sys017DivTree", navTab.getCurrentPanel()).width($(moveObj).position().left);
                $("#sys017ContextDiv", navTab.getCurrentPanel()).css("margin-left", (newWidth + 10) + "px");
                $(moveObj).css("left", 0);
            }
        });
    });
    // 点击收缩和展开
    $("span.split_but", navTab.getCurrentPanel()).mousedown(function (event) {
        stopEvent(event);
    });
    //
    $("span.split_but", navTab.getCurrentPanel()).click(function (event) {
        var splitLine = $(this).parent();
        var leftMenu = splitLine.prev("div.left_menu");
        if (leftMenu.is(":visible")) {
            leftMenu.hide();
            splitLine.addClass("menu_hide");
            var rightDiv = $("#sys017ContextDiv", navTab.getCurrentPanel());
            rightDiv.css("margin-left", "10px");
            var resizeGrid = $("div.j-resizeGrid", rightDiv);
            if (resizeGrid.length > 0) {
                resizeGrid.jResize();
            }
        } else {
            leftMenu.show();
            splitLine.removeClass("menu_hide");
            var rightDiv = $("#sys017ContextDiv", navTab.getCurrentPanel());
            rightDiv.css("margin-left", leftMenu.width() + 10 + "px");
            var resizeGrid = $("div.j-resizeGrid", rightDiv);
            if (resizeGrid.length > 0) {
                resizeGrid.jResize();
            }
        }
    });
});
