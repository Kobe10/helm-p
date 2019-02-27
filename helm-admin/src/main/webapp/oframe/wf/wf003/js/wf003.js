var wf003 = {
    /**
     * 发布流程
     */
    publishForm: function () {
        var $form = $("#flow_publish_dialog", $.pdialog.getCurrent());
        var modelId = $("input[name=dialog_modelId]", $form).val();
        var flowDesc = $("textarea[name=dialog_flowDesc]", $form).val();
        var url = getGlobalPathRoot() + "oframe/wf/wf003/wf003-deploy.gv?prjCd=" + getPrjCd();
        var ajaxPacket = new AJAXPacket(url);
        ajaxPacket.data.add("modelId", modelId);
        ajaxPacket.data.add("flowDesc", flowDesc);
        core.ajax.sendPacketHtml(ajaxPacket, function (response) {
            var jsonData = eval("(" + response + ")");
            if (jsonData.success) {
                alertMsg.correct("发布成功！");
                $.pdialog.closeCurrent();
            } else {
                alertMsg.error(jsonData.errMsg);
            }
        });
    },

    /**
     * 打开快速检索对话框
     */
    openQSch: function (tabIdx) {
        var dialogId = "#wf003create1";
        if (tabIdx == "2") {
            dialogId = "#wf003create2";
        }
        var div = $(dialogId, navTab.getCurrentPanel());
        var active = div.attr("active");
        if ("on" == active) {
            div.hide().removeAttr("active");
        } else {
            div.show().attr("active", "on");
        }
    },

    /**
     * 启动流程
     * @param procDefKey 流程定义编号
     */
    startWf: function (procDefKey) {
        var iWidth = $(window).width() * 0.6; //弹出窗口的宽度;
        var iHeight = $(window).height() - 100; //弹出窗口的高度;
        var iTop = (window.screen.availHeight - 30 - iHeight) / 2; //获得窗口的垂直位置;
        var iLeft = (window.screen.availWidth - 10 - iWidth) / 2; //获得窗口的水平位置;
        var url = getGlobalPathRoot() + "oframe/wf/wf001/wf001-startWf.gv?procDefKey=" + procDefKey + "&busiKey=HouseInfo_1&prjCd=161008";
        window.open(url, "启动流程", "height=" + iHeight + ", width=" + iWidth + ", top=" + iTop + ", left=" + iLeft);
    },

    /**
     * 关闭检索
     */
    closeQSch: function (tabIdx) {
        var dialogId = "#wf003create1";
        if (tabIdx == "2") {
            dialogId = "#wf003create2";
        }
        $(dialogId, navTab.getCurrentPanel()).hide().removeAttr("active");
    },

    /**
     * 加载内容
     */
    loadTabContext: function (flag) {
        if (flag == 1) {
            Query.queryList('wf003fmPublish', 'wf003_page_published', "");
        } else if (flag == 2) {
            Query.queryList('wf003fmDesign', 'wf003_page_design', "");
        } else {
            var add = $(".js_add_task", navTab.getCurrentPanel());
        }
    },
    /**
     * 导出
     */
    exportLc: function (flag) {
        var divId = "#wf003_page_published";
        var id = "id";
        if (flag == "design") {
            divId = "#wf003_page_design";
            id = "modelId";
        }
        var ids = [];
        $(divId, navTab.getCurrentPanel()).find(":checkbox[checked][name=" + id + "]").each(
            function (i) {
                var temp = $(this).closest("tr");
                var exportId = $("input[name=" + id + "]", temp).val();
                ids.push(exportId);
            });
        if (ids.length == 0) {
            alertMsg.warn("请选择需要导出的信息");
        } else {
            var url = getGlobalPathRoot() + "oframe/wf/wf003/wf003-exportLc.gv?prjCd=" + getPrjCd() + "&ids=" + ids.toString() + "&flag=" + flag;
            window.open(url);
        }
    },
    /**
     * 单条导出
     */
    exportLd: function (ids, flag) {
        var url = getGlobalPathRoot() + "oframe/wf/wf003/wf003-exportLc.gv?ids=" + ids + "&flag=" + flag + "&prjCd=" + getPrjCd();
        window.open(url);
    },
    /**
     * 批量删除
     */
    batchDelete: function (flag) {
        var divId = "#wf003_page_published";
        var id = "id";
        if (flag == "design") {
            divId = "#wf003_page_design";
            id = "modelId";
        }
        var ids = [];
        $(divId, navTab.getCurrentPanel()).find(":checkbox[checked][name=" + id + "]").each(
            function (i) {
                var temp = $(this).closest("tr");
                var deleteId = $("input[name=" + id + "]", temp).val();
                ids.push(deleteId);
            });
        if (ids.length == 0) {
            alertMsg.warn("请选择要删除的记录");
        } else {
            alertMsg.confirm("您确定要删除记录吗?", {
                okCall: function () {
                    var url = getGlobalPathRoot() + "oframe/wf/wf003/wf003-batchDelete.gv";
                    var ajaxPacket = new AJAXPacket(url);
                    ajaxPacket.data.add("ids", ids.toString());
                    ajaxPacket.data.add("flag", flag);
                    core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                        var jsonData = eval("(" + response + ")");
                        // 服务调用是否成功
                        var isSuccess = jsonData.success;
                        if (isSuccess) {
                            alertMsg.correct("处理成功");
                            if (flag == 'publish') {
                                Query.queryList('wf003fmPublish', 'wf003_page_published', "");
                            } else {
                                Query.queryList('wf003fmDesign', 'wf003_page_design', "");
                            }
                        } else {
                            alertMsg.error(jsonData.errMsg);
                        }
                    });
                }
            });
        }
        stopEvent(event);
    },
    ///**
    // * 启动
    // */
    //start: function () {
    //
    //},

    /**
     * 设计
     */
    design: function (id, wfReg) {
        var url = getGlobalPathRoot() + "oframe/wf/wf003/wf003-editTemplate.gv?prjCd=" + getPrjCd() + "&id=" + id + "&wfReg=" + wfReg;
        $.pdialog.open(url, "wf003-editTemp", "在线编辑模板", {max: true, mask: true});
    },

    /**
     * 获取 编辑框内代码
     */
    getCode: function (textareaName, content) {
        if (content && content == 'navTab') {
            return $("textarea[name=" + textareaName + "]", navTab.getCurrentPanel()).val();
        } else if (content && content == 'dialog') {
            return $("textarea[name=" + textareaName + "]", $.pdialog.getCurrent()).val();
        } else {
            return "";
        }
    },

    saveTemplateText: function (obj) {
        var $form = $("#wf003EditTempForm", $.pdialog.getCurrent());

        //处理 模板 代码
        var procDefInfo = window.frames['wf003TempIFrame'].getCode("procDefInfo");
        $('textarea[name=procDefInfo]', navTab.getCurrentPanel()).val(procDefInfo);

        var id = $("input[name=id]", $form).val();
        var wfReg = $("input[name=wfReg]", $form).val();
        var url = getGlobalPathRoot() + "oframe/wf/wf003/wf003-saveTemplateText.gv?prjCd=" + getPrjCd();
        var packet = new AJAXPacket(url);
        packet.data.add("id", id);
        packet.data.add("wfReg", wfReg);
        packet.data.add("procDefInfo", procDefInfo);
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            if (jsonData.success) {
                alertMsg.correct("保存成功！");
                $.pdialog.closeCurrent();
            } else {
                alertMsg.error(jsonData.errMsg);
            }
        }, true);
    },
    /**
     * 弹出发布框
     */
    deploy: function (modelId) {
        var url = getGlobalPathRoot() + "oframe/wf/wf003/wf003-publishDialog.gv?prjCd=" + getPrjCd()
            + "&modelId=" + modelId;
        $.pdialog.open(url, "wf003-publishDialog", "确认发布流程", {
            height: 255, width: 800, mask: true
        });
    },
    /**
     * Excel 导入 Person
     */
    doDeploy: function () {
        var wf003Frm = $("#wf003fmadd", navTab.getCurrentPanel());
        if (!wf003Frm.valid()) {
            return;
        }
        var wfFile = $("#wfFile", navTab.getCurrentPanel()).val();
        var actTypeCd = $("#actTypeCd", navTab.getCurrentPanel()).val();
        if (actTypeCd == '') {
            alertMsg.warn("请点击左侧树节点，新建相应类型的流程");
            return false;
        }
        var uploadURL = getGlobalPathRoot() + "oframe/wf/wf003/wf003-doDeploy.gv?prjCd=" + getPrjCd();
        $.ajaxFileUpload({
                url: uploadURL,
                data: {
                    deployName: $("input[name=deployName]", wf003Frm).val(),
                    key: $("input[name=key]", wf003Frm).val(),
                    description: $("input[name=description]", wf003Frm).val(),
                    procDepCategory: $("input[name=procDepCategory]", wf003Frm).val(),
                    actTypeCd: $("#actTypeCd", navTab.getCurrentPanel()).val()
                },
                secureuri: false,
                fileElementId: "wfFile",
                dataType: 'json',
                success: function (data, status, fileElementId) {
                    if (data.success) {
                        alertMsg.correct("处理成功");
                        $("input,textarea,select", wf003Frm).val("");
                    } else {
                        alertMsg.error(data.errMsg);
                    }
                }
            }
        );
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

    /**
     * 初始化目录树
     */
    initFullTree: function () {
        // 重新加载
        var packet = new AJAXPacket();
        // 获取当前选中的组织树类型，传递项目编号获取组织树
        packet.data.add("prjCd", "0");
        packet.url = getGlobalPathRoot() + "oframe/wf/wf003/wf003-actTypeTree.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            var treeJson = jsonData.resultMap.treeJson;
            // 服务调用是否成功
            var isSuccess = jsonData.success;
            if (isSuccess) {
                var formTreeSetting = {
                    data: {
                        simpleData: {
                            enable: true
                        }
                    },
                    view: {
                        addHoverDom: wf003.addHoverDom,
                        removeHoverDom: wf003.removeHoverDom,
                        selectedMulti: false
                    },
                    callback: {
                        onClick: wf003.clickNode
                    }
                };
                // 初始化目录树
                var zTree = $.fn.zTree.init($("#wf003Tree", navTab.getCurrentPanel()), formTreeSetting, treeJson);
                var rootNode = zTree.getNodes()[0];
                if (rootNode != null) {
                    zTree.selectNode(rootNode);
                    wf003.clickNode(null, "wf003Tree", rootNode);
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
        var divArea = navTab.getCurrentPanel();
        var sObj = $("#" + treeNode.tId + "_span", divArea);
        var isRuleType = false;
        if (treeNode.typeId && treeNode.typeId != '') {
            isRuleType = true;
        }

        //新增加分类
        if (wf003.getEditAble() && $("#addActTypeBtn_" + treeNode.tId, divArea).length == 0) {
            var addStr = "<span class='button marr5 add' id='addActTypeBtn_" + treeNode.tId + "' title='新增分类' onfocus='this.blur();'/>";
            sObj.after(addStr);
            var btn = $("#addActTypeBtn_" + treeNode.tId);
            if (btn) btn.bind("click", function () {
                wf003.editActType("", treeNode.id);
                return false;
            });
        }
        //显示编辑按钮
        if (isRuleType && wf003.getEditAble() && $("#editActTypeBtn_" + treeNode.tId, divArea).length == 0) {
            var addStr = "<span class='button marr5 marl5 edit' id='editActTypeBtn_" + treeNode.tId + "' title='编辑分类' onfocus='this.blur();'/>";
            sObj.after(addStr);
            var btn = $("#editActTypeBtn_" + treeNode.tId);
            if (btn) btn.bind("click", function () {
                // 修改分类
                wf003.editActType(treeNode.id, "");
                return false;
            });
        }
        //显示删除按钮
        if (treeNode.level != 0 && isRuleType && wf003.getRemoveAble() && $("#removeActTypeBtn_" + treeNode.tId, divArea).length == 0) {
            var addStr = "<span class='button marr5 marl5 remove' id='removeActTypeBtn_" + treeNode.tId + "' title='删除分类' onfocus='this.blur();'/>";
            sObj.after(addStr);
            var btn = $("#removeActTypeBtn_" + treeNode.tId);
            if (btn) btn.bind("click", function () {
                // 删除规则目录
                alertMsg.confirm("您确定要删除记录吗?", {
                    okCall: function () {
                        var packet = new AJAXPacket();
                        // 目录树类型
                        // 当前节点信息
                        packet.data.add("actTypeCd", treeNode.actTypeCd);
                        packet.data.add("prjCd", getPrjCd());
                        // 提交界面展示
                        packet.url = getGlobalPathRoot() + "oframe/wf/wf003/wf003-deleteActType.gv";
                        // 处理删除
                        core.ajax.sendPacketHtml(packet, function (response) {
                            var data = eval("(" + response + ")");
                            var isSuccess = data.success;
                            if (isSuccess) {
                                alertMsg.correct("删除成功");
                                var zTree = $.fn.zTree.getZTreeObj("wf003Tree");
                                zTree.removeNode(treeNode, false);
                                //// 显示父级节点
                                wf003.initFullTree();
                            } else {
                                alertMsg.error(data.errMsg);
                            }
                        }, false);
                    }
                });
            });
        }
    },

    /**
     * 移开光标删除事件
     * @param treeId  树编号
     * @param treeNode 选择节点编号
     */
    removeHoverDom: function (treeId, treeNode) {
        $("#removeActTypeBtn_" + treeNode.tId, navTab.getCurrentPanel()).unbind().remove();
        $("#editActTypeBtn_" + treeNode.tId, navTab.getCurrentPanel()).unbind().remove();
        $("#addActTypeBtn_" + treeNode.tId, navTab.getCurrentPanel()).unbind().remove();
    },

    /**
     * 点击流程分类节点
     * @param event
     * @param treeId
     * @parma treeNode
     */
    clickNode: function (event, treeId, treeNode) {
        // 点击的是项目则展示项目编号
        $("input[name=actTypeCd]", navTab.getCurrentPanel()).val(treeNode.actTypeCd);
        // 获取当前查询类型，刷新检索结果
        $("#wf003RightDiv", navTab.getCurrentPanel()).find("div.tabsHeaderContent li.selected").trigger("click");
    },

    /**
     * 新建/编辑分类
     */
    editActType: function (actTypeId, upActTypeId) {
        var url = getGlobalPathRoot() + "oframe/wf/wf003/wf003-editActType.gv?"
            + "&actTypeId=" + actTypeId + "&upActTypeId=" + upActTypeId + "&prjCd=" + getPrjCd();
        $.pdialog.open(url, "wf003ActType", "分类信息",
            {mask: true, max: false, maxable: false, resizable: false, width: 800, height: 550});
    },
    /**
     * 保存分类
     */
    saveActType: function () {
        var frm = $("#wf003TreeEditForm", $.pdialog.getCurrent());
        if (frm.valid()) {
            var jsonData = frm.serializeArray();
            var packet = new AJAXPacket(getGlobalPathRoot() + "oframe/wf/wf003/wf003-saveActType.gv");
            packet.data.data = jsonData;
            packet.data.data.push({name: "prjCd", value: getPrjCd()});
            core.ajax.sendPacketHtml(packet, function (response) {
                var jsonData = eval("(" + response + ")");
                var isSuccess = jsonData.success;
                if (isSuccess) {
                    alertMsg.correct("保存成功");
                    $.pdialog.closeCurrent();
                    var zTree = $.fn.zTree.getZTreeObj("wf003Tree");
                    var nodeInfo = jsonData.data.node[0];
                    //判断是否是新增
                    if (jsonData.data.addFlag) {
                        //更新树节点
                        var pNode = zTree.getNodeByParam("id", nodeInfo.pId, null);
                        zTree.addNodes(pNode, nodeInfo);
                    } else {
                        // 修改节点
                        var cNode = zTree.getNodeByParam("id", nodeInfo.id, null);
                        // 合并修改信息
                        cNode = $.extend(cNode, cNode, nodeInfo);
                        zTree.updateNode(cNode);
                    }
                } else {
                    alertMsg.error(jsonData.errMsg);
                }
            });
        }
    },
    /**
     * 授权组织
     * @param obj
     */
    editOrg: function (obj) {
        var url = getGlobalPathRoot() + "oframe/wf/wf003/wf003-orgTree.gv?prjCd=" + getPrjCd();
        $(obj).openTip({href: url, width: "250", offsetX: 0, offsetY: 30});
    }
};

$(document).ready(function () {
    // 执行查询
    //wf003.loadTabContext(1);
    //初始化左侧树
    wf003.initFullTree();
    // 拖动屏幕切分效果
    LeftMenu.init("wf003TreeDiv", "wf003RightDiv");
});