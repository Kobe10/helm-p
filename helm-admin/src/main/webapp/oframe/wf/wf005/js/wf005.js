var wf005 = {
    uploadObj: null,
    /**
     * 关闭检索
     * @param forceFlag 强制关闭
     */
    closeQSch: function (tabIdx) {
        var dialogId = "wf005publish";
        if (tabIdx == "design") {
            dialogId = "wf005design";
        }
        $("#" + dialogId, navTab.getCurrentPanel()).hide().removeAttr("active");
    },

    /**
     * 打开快速检索对话框
     */
    openQSch: function (tabIdx) {
        var dialogId = "wf005publish";
        if (tabIdx == "design") {
            dialogId = "wf005design";
        }
        var div = $("#" + dialogId, navTab.getCurrentPanel());
        var active = div.attr("active");
        if ("on" == active) {
            div.hide().removeAttr("active");
        } else {
            div.show().attr("active", "on");
        }
    },
    /**
     *通用查询
     */
    query: function (flag) {
        var formObj = $("#wf005fm" + flag, navTab.getCurrentPanel());
        // 获取输入框
        var dialogObj = $("#wf005" + flag, navTab.getCurrentPanel());
        // 覆盖的查询条件
        var coverConditionName = [];
        var coverCondition = [];
        var coverConditionValue = [];
        var coverNames = [];
        // 循环当前查询条件
        $("input", dialogObj).each(function () {
            var $this = $(this);
            var attrName = $this.attr("name");
            var tagName = $this[0].tagName;
            var condition = $this.attr("condition");
            var value = $this.val();
            if ("INPUT" == tagName && condition) {
                coverNames.push(attrName);
                if (value != "") {
                    coverConditionName.push(attrName);
                    coverCondition.push(condition);
                    coverConditionValue.push(value);
                }
            }
        });
        // 获取历史查询条件
        var oldConditionName = $("input[name=conditionName]", formObj).val().split(",");
        var oldCondition = $("input[name=condition]", formObj).val().split(",");
        var oldConditionValue = $("input[name=conditionValue]", formObj).val().split(",");
        // 实际使用的查询条件
        var newConditionName = [];
        var newCondition = [];
        var newConditionValue = [];

        // 处理历史查询条件，存在覆盖则删除原有的查询条件
        for (var i = 0; i < oldConditionName.length; i++) {
            var conditionName = oldConditionName[i];
            var found = false;
            for (var j = 0; j < coverNames.length; j++) {
                if (conditionName == coverNames[j]) {
                    found = true;
                    break;
                }
            }
            // 未找到匹配的条件
            if (!found) {
                newConditionName.push(oldConditionName[i]);
                newCondition.push(oldCondition[i]);
                newConditionValue.push(oldConditionValue[i]);
            }
        }

        // 追加新的查询条件
        newConditionName = newConditionName.concat(coverConditionName);
        newCondition = newCondition.concat(coverCondition);
        newConditionValue = newConditionValue.concat(coverConditionValue);
        // 重新设置查询条件
        $("input[name=conditionName]", formObj).val(newConditionName.join(","));
        $("input[name=condition]", formObj).val(newCondition.join(","));
        $("input[name=conditionValue]", formObj).val(newConditionValue.join(","));
        // 再次执行查询
        Page.query($("#wf005fm" + flag, navTab.getCurrentPanel()), "");
    },
    /**
     * 批量导出
     */
    exportForm: function (flag) {
        var findDiv = $("#" + flag + "_div", navTab.getCurrentPanel());
        var formIds = [];
        $(findDiv).find(":checkbox[checked][name=formId]").each(
            function (i) {
                var temp = $(this).closest("tr");
                var formId = $("input[name=formId]", temp).val();
                formIds.push(formId);
            });
        if (formIds.length == 0) {
            alertMsg.warn("请选择需要导出的表单");
        } else {
            var url = getGlobalPathRoot() + "oframe/wf/wf005/wf005-exportForm.gv?prjCd=" + getPrjCd() + "&formIds=" + formIds.toString();
            window.open(url);
        }
    },
    /**
     * 批量导入
     */
    importForm: function (obj) {
        var thisObj = $(obj);
        $("input[name=wf005ImportForms]", thisObj).unbind("change", wf005.uploadFormFile);
        $("input[name=wf005ImportForms]", thisObj).bind("change", {"clickObj": thisObj}, wf005.uploadFormFile);
    },

    /**
     * 上传 表单文件
     */
    uploadFormFile: function (event) {
        var currLi = event.data.clickObj.closest("li");
        var uploadURL = getGlobalPathRoot() + "oframe/wf/wf005/wf005-saveImportForm.gv";
        var fileElementId = $('input[type=file]', currLi).attr("id");
        $.ajaxFileUpload({
                url: uploadURL,
                data: {prjCd: getPrjCd()},
                secureuri: false,
                fileElementId: fileElementId,
                dataType: 'json',
                success: function (data, status, fileElementId) {
                    if (data.isSuccess) {
                        alertMsg.correct("导入成功！");
                    } else {
                        alertMsg.error(data.errMsg);
                    }
                }
            }
        )
    },

    /**
     * 删除
     */
    deleteForm: function (divId, flag) {
        var formIds = [];
        $("#" + divId, navTab.getCurrentPanel()).find(":checkbox[checked][name=formId]").each(
            function (i) {
                var temp = $(this).closest("tr");
                var formId = $("input[name=formId]", temp).val();
                formIds.push(formId);
            }
        );
        alertMsg.confirm("您确定要删除记录吗？", {
            okCall: function () {
                var url = getGlobalPathRoot() + "oframe/wf/wf005/wf005-deleteForm.gv?prjCd=" + getPrjCd();
                var ajaxPacket = new AJAXPacket(url);
                ajaxPacket.data.add("formIds", formIds.toString());
                core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                    var jsonData = eval("(" + response + ")");
                    if (jsonData.success) {
                        alertMsg.correct("处理成功！");
                        if (flag == 'publish') {
                            wf005.query('publish');
                        } else {
                            wf005.query('design');
                        }
                    } else {
                        alertMsg.error(jsonData.errMsg);
                    }
                });
            }
        });
    },

    /**
     * 发布说明
     */
    publishDialog: function (formId, formDesc) {
        if (!formDesc) {
            formDesc = "";
        } else {
            formDesc = encodeURI(encodeURI(formDesc));
        }
        var url = getGlobalPathRoot() + "oframe/wf/wf005/wf005-publishDialog.gv?prjCd=" + getPrjCd()
            + "&formId=" + formId
            + "&formDesc=" + formDesc;
        $.pdialog.open(url, "wf006-publishDialog", "确认发布表单", {
            height: 255, width: 800, mask: true
        });
    },

    /**
     * 发布表单
     */
    publishForm: function () {
        var $form = $("#form_publish_dialog", $.pdialog.getCurrent());
        var formId = $("input[name=dialog_formId]", $form).val();
        var formDesc = $("textarea[name=dialog_formDesc]", $form).val();
                var url = getGlobalPathRoot() + "oframe/wf/wf005/wf005-publishForm.gv?prjCd=" + getPrjCd();
                var ajaxPacket = new AJAXPacket(url);
                ajaxPacket.data.add("formId", formId);
        ajaxPacket.data.add("formDesc", formDesc);
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
     * 导入表单
     */
    startImportForm: function (obj) {
        var clickFileBtn = $(obj).parent().find("input[type=file]");
        clickFileBtn.attr("id", new Date().getTime());
        clickFileBtn.val("");
        clickFileBtn.unbind("change", wf005.importXml);
        clickFileBtn.bind("change", wf005.importXml);
        wf005.uploadObj = $(obj);
    },
    /**
     * 导入表单
     */
    importXml: function () {
        var uploadURL = getGlobalPathRoot() + "oframe/wf/wf005/wf005-importData.gv?prjCd=" + getPrjCd();
        var importMsgFile = wf005.uploadObj.parent().find("input[type=file]").attr("id");
        $.ajaxFileUpload({
            url: uploadURL,
            secureuri: false,
            fileElementId: importMsgFile,
            dataType: 'json',
            success: function (data, status, fileElementId) {
                if (data.isSuccess) {
                    alertMsg.correct("处理成功");
                    wf005.query('design');
                } else {
                    alertMsg.error(data.errMsg);
                }
            }
        })
    },
    /**
     * 表单设计。
     */
    designForm: function (designFlag, formId) {
        var formTypeId = $("input[name=formTypeId]", navTab.getCurrentPanel()).val();
        var url = "oframe/wf/wf006/wf006-init.gv?prjCd=" + getPrjCd() + "&designFlag=" + designFlag + "&formId=" + formId + "&formTypeId=" + formTypeId;
        index.quickOpen(url, {opCode: "wf006-init", opName: "表单设计", fresh: true});
    },

    /**
     * 撤销已发布表单
     * @param formId
     */
    cancelPublish: function (formId) {
        alertMsg.confirm("确定撤销此表单吗？", {
            okCall: function () {
                var url = getGlobalPathRoot() + "oframe/wf/wf005/wf005-cancelPublish.gv?prjCd=" + getPrjCd();
                var ajaxPacket = new AJAXPacket(url);
                ajaxPacket.data.add("formId", formId);
                core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                    var jsonData = eval("(" + response + ")");
                    if (jsonData.success) {
                        alertMsg.correct("撤销成功！");
                        wf005.query('publish');
                    } else {
                        alertMsg.error(jsonData.errMsg);
                    }
                });
            }
        });
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
        packet.url = getGlobalPathRoot() + "oframe/wf/wf005/wf005-formTypeTree.gv";
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
                        addHoverDom: wf005.addHoverDom,
                        removeHoverDom: wf005.removeHoverDom,
                        selectedMulti: false
                    },
                    callback: {
                        onClick: wf005.clickNode
                    }
                };
                // 初始化目录树
                var zTree = $.fn.zTree.init($("#wf005Tree", navTab.getCurrentPanel()), formTreeSetting, treeJson);
                var rootNode = zTree.getNodes()[0];
                if (rootNode != null) {
                    zTree.selectNode(rootNode);
                    wf005.clickNode(null, "wf005Tree", rootNode);
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
        if (wf005.getEditAble() && $("#addFormTypeBtn_" + treeNode.tId, divArea).length == 0) {
            var addStr = "<span class='button marr5 add' id='addFormTypeBtn_" + treeNode.tId + "' title='新增分类' onfocus='this.blur();'/>";
            sObj.after(addStr);
            var btn = $("#addFormTypeBtn_" + treeNode.tId);
            if (btn) btn.bind("click", function () {
                wf005.editFromType("", treeNode.id);
                return false;
            });
        }
        //显示编辑按钮
        if (isRuleType && wf005.getEditAble() && $("#editFormTypeBtn_" + treeNode.tId, divArea).length == 0) {
            var addStr = "<span class='button marr5 marl5 edit' id='editFormTypeBtn_" + treeNode.tId + "' title='编辑分类' onfocus='this.blur();'/>";
            sObj.after(addStr);
            var btn = $("#editFormTypeBtn_" + treeNode.tId);
            if (btn) btn.bind("click", function () {
                // 修改分类
                wf005.editFromType(treeNode.id, "");
                return false;
            });
        }
        //显示删除按钮
        if (treeNode.level != 0 && isRuleType && wf005.getRemoveAble() && $("#removeFormTypeBtn_" + treeNode.tId, divArea).length == 0) {
            var addStr = "<span class='button marr5 marl5 remove' id='removeFormTypeBtn_" + treeNode.tId + "' title='删除分类' onfocus='this.blur();'/>";
            sObj.after(addStr);
            var btn = $("#removeFormTypeBtn_" + treeNode.tId);
            if (btn) btn.bind("click", function () {
                // 删除规则目录
                alertMsg.confirm("您确定要删除记录吗?", {
                    okCall: function () {
                        var packet = new AJAXPacket();
                        // 目录树类型
                        // 当前节点信息
                        packet.data.add("formTypeId", treeNode.id);
                        packet.data.add("prjCd", getPrjCd());
                        // 提交界面展示
                        packet.url = getGlobalPathRoot() + "oframe/wf/wf005/wf005-deleteFormType.gv";
                        // 处理删除
                        core.ajax.sendPacketHtml(packet, function (response) {
                            var data = eval("(" + response + ")");
                            var isSuccess = data.success;
                            if (isSuccess) {
                                alertMsg.correct("删除成功");
                                var zTree = $.fn.zTree.getZTreeObj("wf005Tree");
                                var pNode = zTree.getNodeByParam("id", treeNode.pId, null);
                                zTree.removeNode(treeNode, false);
                                //// 显示父级节点
                                wf005.initFullTree();
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
     * 新建/编辑分类
     */
    editFromType: function (formTypeId, upFormTypeId) {
        var url = getGlobalPathRoot() + "oframe/wf/wf005/wf005-editFromType.gv?"
            + "&formTypeId=" + formTypeId + "&upFormTypeId=" + upFormTypeId + "&prjCd=" + getPrjCd();
        $.pdialog.open(url, "wf005FormType", "分类信息",
            {mask: true, max: false, maxable: false, resizable: false, width: 800, height: 550});
    },

    /**
     * 保存分类
     */
    saveFormType: function () {
        var frm = $("#wf005TreeEditForm", $.pdialog.getCurrent());
        if (frm.valid()) {
            var jsonData = frm.serializeArray();
            var packet = new AJAXPacket(getGlobalPathRoot() + "oframe/wf/wf005/wf005-saveFormType.gv");
            packet.data.data = jsonData;
            packet.data.data.push({name: "prjCd", value: getPrjCd()});
            core.ajax.sendPacketHtml(packet, function (response) {
                var jsonData = eval("(" + response + ")");
                var isSuccess = jsonData.success;
                if (isSuccess) {
                    alertMsg.correct("保存成功");
                    $.pdialog.closeCurrent();
                    var zTree = $.fn.zTree.getZTreeObj("wf005Tree");
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
     * 移开光标删除事件
     * @param treeId  树编号
     * @param treeNode 选择节点编号
     */
    removeHoverDom: function (treeId, treeNode) {
        $("#removeFormTypeBtn_" + treeNode.tId, navTab.getCurrentPanel()).unbind().remove();
        $("#editFormTypeBtn_" + treeNode.tId, navTab.getCurrentPanel()).unbind().remove();
        $("#addFormTypeBtn_" + treeNode.tId, navTab.getCurrentPanel()).unbind().remove();
    },

    /**
     * 点击表单分类节点
     * @param event
     * @param treeId
     * @parma treeNode
     */
    clickNode: function (event, treeId, treeNode) {
        // 点击的是项目则展示项目编号
        $("input[name=formTypeId]", navTab.getCurrentPanel()).val(treeNode.id);
        // 获取当前查询类型，刷新检索结果
        $("#wf005RightDiv", navTab.getCurrentPanel()).find("div.tabsHeaderContent li.selected").trigger("click");
    },
    /**
     * 授权角色
     * @param obj
     */
    editRole: function (obj) {
        var url = getGlobalPathRoot() + "oframe/sysmg/sys002/sys002-roleTree.gv?prjCd=" + getPrjCd();
        $(obj).openTip({href: url, width: "250", offsetX: 0, offsetY: 30});
    }
};

$(document).ready(function () {
    // 执行目录树初始化
    wf005.initFullTree();

    // 拖动屏幕切分效果
    LeftMenu.init("wf005TreeDiv", "wf005RightDiv");

});