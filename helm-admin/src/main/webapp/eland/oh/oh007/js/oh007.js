var oh007 = {

    /**
     * 获取区域目录树
     * @returns {string}
     */
    getBdTreeId: function () {
        var regUseType = $("input[name=regUseType]", navTab.getCurrentPanel()).val();
        return "oh007Tree_" + regUseType;
    },

    /**
     * 获取区域目录树
     * @returns {string}
     */
    getHxTreeId: function () {
        var regUseType = $("input[name=regUseType]", navTab.getCurrentPanel()).val();
        return "oh007HxTree_" + regUseType;
    },

    /**
     * 调整管理组织
     */
    switchTree: function (obj) {
        var $obj = $(obj);
        var textSpan = $obj.parent().find("span.panel_title");
        var spanText = textSpan.html();
        var oh007BdTreeContain = $("#" + oh007.getBdTreeId(), navTab.getCurrentPanel());
        var oh007HxTreeContain = $("#" + oh007.getHxTreeId(), navTab.getCurrentPanel());
        if ("楼 宇 导 航" === spanText) {
            spanText = "户 型 导 航";
            oh007HxTreeContain.show();
            oh007BdTreeContain.hide();
            if (oh007HxTreeContain.is(":empty")) {
                oh007.initHxTree();
            }
            // 保存cookie
            setPCookie("showFyTreeModel", "hx", "/");
        } else {
            spanText = "楼 宇 导 航";
            oh007BdTreeContain.show();
            oh007HxTreeContain.hide();
            if (oh007BdTreeContain.is(":empty")) {
                oh007.initFullRegTree();
            }
            // 保存cookie
            setPCookie("showFyTreeModel", "bd", "/");
        }
        textSpan.html(spanText);
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
                            onClick: oh007.clickNode
                        }
                    };
                    // 初始化目录树
                    var zTree = $.fn.zTree.init($("#" + oh007.getBdTreeId(), navTab.getCurrentPanel()), pjOrgTreeSetting, treeJson);

                    // 显示根节点数据
                    var rootNode = zTree.getNodesByFilter(function (node) {
                        return node.level == 0
                    }, true);
                    if (rootNode != null) {
                        oh007.regHs(rootNode.id, "", "");
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
                            onClick: oh007.clickNode
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
                    var zTree = $.fn.zTree.init($("#" + oh007.getHxTreeId(), navTab.getCurrentPanel()), hxTreeSetting, treeJson);

                    // 显示根节点数据
                    var rootNode = zTree.getNodesByFilter(function (node) {
                        return node.level == 0
                    }, true);
                    if (rootNode != null) {
                        oh007.regHs(rootNode.id, "", "");
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
        var zTree = $.fn.zTree.getZTreeObj(oh007.getBdTreeId());
        var zTreeHx = $.fn.zTree.getZTreeObj(oh007.getHxTreeId());
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
            oh007.regHs(treeNode.areaId, treeNode.hsTp, treeNode.name);
            hsHx.attr("hxCd", treeNode.name).attr("regId", treeNode.areaId).show();
        } else if (treeNode.useType == "hsTp") {
            // 按照居室检索
            oh007.regHs(treeNode.areaId, treeNode.hsTp, "");
            hsHx.hide().attr("hxCd", "").attr("regId", "");
        } else {
            oh007.regHs(treeNode.id, "", "");
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
            oh007.refleshHs();
        }
        return true;
    },


    /**
     * 调用通用查询查询房源数据
     * @param conditionFieldArr 查询字段数组
     * @param conditionArr 查询条件数组
     * @param conditionValueArr 查询值数组
     */
    queryHs: function (obj) {
        Page.queryCondition(obj, {
            changeConditions: oh007.changeConditions,
            formObj: $("#oh00701frm", navTab.getCurrentPanel())
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
            $("#oh007QSchDialog", navTab.getCurrentPanel()).show().attr("active", "on");
            $("#oh007QSchDialog input:eq(0)", navTab.getCurrentPanel()).focus();
        }
    },

    /**
     * 离开检索按钮
     */
    leaveQSch: function () {
        setTimeout(function () {
            oh007.closeQSch(false);
        }, 300);
    },

    /**
     * 关闭检索
     * @param forceFlag 强制关闭
     */
    closeQSch: function (forceFlag) {
        var close = forceFlag;
        if (!forceFlag) {
            var active = $("#oh007QSchDialog", navTab.getCurrentPanel()).attr("active");
            if ("on" == active) {
                close = false;
            } else {
                close = true;
            }
        }
        if (close) {
            $("#oh007QSchDialog", navTab.getCurrentPanel()).hide();
        }
    },

    /**
     * 快速检索
     */
    qSchData: function () {
        var formObj = $('#oh00701frm', navTab.getCurrentPanel());
        var conditionName = $("input[name=conditionName]", formObj).val().split(",");
        var condition = $("input[name=condition]", formObj).val().split(",");
        var conditionValue = $("input[name=conditionValue]", formObj).val().split(",");
        $("#oh007QSchDialog", navTab.getCurrentPanel()).find("input").each(function () {
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
        oh007.closeQSch(true);
        oh007.refleshHs();
    },

    /**
     * 改变查询条件
     * @param newCondition
     */
    changeConditions: function (newCondition) {
        var formObj = $('#oh00701frm', navTab.getCurrentPanel());
        $("input[name=conditionName]", formObj).val(newCondition.conditionNames);
        $("input[name=condition]", formObj).val(newCondition.conditions);
        $("input[name=conditionValue]", formObj).val(newCondition.conditionValues);
        $("input.js_conditionValue", formObj).val(newCondition.conditionValueTexts);
        $("input[name=resultField]", formObj).val(newCondition.resultFields);
        // 排序字段
        $("input[name=sortColumn]", formObj).val(newCondition.sortColumn);
        $("input[name=sortOrder]", formObj).val(newCondition.sortOrder);
        oh007.refleshHs();
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
        var formObj = $('#oh00701frm', navTab.getCurrentPanel());
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
        oh007.refleshHs();
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
        var form = $('#oh00701frm', navTab.getCurrentPanel());
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
                packet.url = getGlobalPathRoot() + "eland/oh/oh007/oh007-initS.gv";
                core.ajax.sendPacketHtml(packet, function (response) {
                    $("#oh007Context", navTab.getCurrentPanel()).html(response);
                    initUI($("#oh007Context", navTab.getCurrentPanel()));
                });
            } else if (viewModel == "2") {
                packet.url = getGlobalPathRoot() + "eland/oh/oh001/oh00101-initB.gv";
                packet.data.add("displayBuildPage", $("input.js_displayBuildPage", form).val());
                packet.data.add("moreResultField", $("input.js_moreResultField", form).val());
                packet.data.add("colorItemCd", $("input.js_colorItemCd", form).val());
                core.ajax.sendPacketHtml(packet, function (response) {
                    $("#oh007Context", navTab.getCurrentPanel()).html(response);
                    initUI($("#oh007Context", navTab.getCurrentPanel()));
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
        oh007.refleshHs();
    },

    adjustHeight: function () {
        var oh007DivTreeH = $("#oh007DivTree", navTab.getCurrentPanel()).height();
        var oh007SummaryH = $("#oh007Summary", navTab.getCurrentPanel()).height();
        var height = oh007DivTreeH - oh007SummaryH - 123;
        var gridScroller = $("#oh00701_list_print", navTab.getCurrentPanel()).find("div.gridScroller");
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
            oh007.adjustHeight();
        } else {
            $pucker.removeClass("expandable").addClass("collapsable");
            $content.show();
            oh007.adjustHeight();
        }
    },

    /**
     * 打开交房延期处理界面
     * @param 点击对象
     */
    openMarkDelay: function (obj) {
        var newHsIds = [];
        var canNotDelay = [];
        navTab.getCurrentPanel()
            .find(":checkbox[checked][name=newHsId]").each(
            function (i) {
                var statusCd = $(this).attr("statusCd");
                if (statusCd != 3) {
                    canNotDelay.push($(this).val());
                } else {
                    newHsIds.push($(this).val());
                }
            }
        );
        if (canNotDelay.length > 0) {
            alertMsg.warn("只有\"完成选房\"的房源才允许进行该操作!");
        } else if (newHsIds.length == 0) {
            alertMsg.warn("请选择要标记延期的房源");
        } else {
            var url = getGlobalPathRoot() + "eland/oh/oh007/oh007-initDelay.gv?prjCd=" + getPrjCd()
                + "&newHsIds=" + newHsIds.join(",");
            $.pdialog.open(url, "oh007Delay", "延期交房",
                {mask: true, max: false, maxable: false, resizable: false, width: 600, height: 180}
            );
        }
    },

    /**
     * 标记延期交房状态及交房时间
     * @param 点击对象
     */
    markDelay: function (obj) {
        var container = $.pdialog.getCurrent();
        var newHsIds = $("input[name=newHsIds]", container).val();
        var handHsDate = $("input[name=handHsDate]", container).val();
        var oh007DelayForm = $("#oh007DelayForm", container)
        if (oh007DelayForm.valid()) {
            alertMsg.confirm("您确定要处理选中的房源吗?", {
                okCall: function () {
                    // 提交修改内容
                    var url = getGlobalPathRoot() + "eland/oh/oh001/oh001-bEdit.gv";
                    var ajaxPacket = new AJAXPacket(url);
                    ajaxPacket.data.add("prjCd", getPrjCd());
                    ajaxPacket.data.add("fieldNames", "handleStatus,handHsDate");
                    ajaxPacket.data.add("fieldValues", "2," + handHsDate);
                    ajaxPacket.data.add("newHsIds", newHsIds);
                    core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                        var jsonData = eval("(" + response + ")");
                        // 服务调用是否成功
                        var isSuccess = jsonData.success;
                        if (isSuccess) {
                            alertMsg.correct("处理成功");
                            $.pdialog.closeCurrent();
                            oh007.refleshHs();

                        } else {
                            alertMsg.error(jsonData.errMsg);
                        }
                    });
                }
            });
        }
    },

    /**
     * 标记已通知延期
     * @param 点击对象
     */
    telDelay: function (obj) {
        var newHsIds = [];
        var canNotDelay = [];
        navTab.getCurrentPanel()
            .find(":checkbox[checked][name=newHsId]").each(
            function (i) {
                var statusCd = $(this).attr("handleStatus");
                if (statusCd != "2") {
                    canNotDelay.push($(this).val());
                } else {
                    newHsIds.push($(this).val());
                }

            }
        );
        if (canNotDelay.length > 0) {
            alertMsg.warn("只有延期的房源才允许进行该操作!");
        } else if (newHsIds.length == 0) {
            alertMsg.warn("请选择要标记延期的房源");
        } else {
            alertMsg.confirm("您确定要处理选中的房源吗?", {
                okCall: function () {
                    // 提交修改内容
                    var url = getGlobalPathRoot() + "eland/oh/oh001/oh001-bEdit.gv";
                    var ajaxPacket = new AJAXPacket(url);
                    ajaxPacket.data.add("prjCd", getPrjCd());
                    ajaxPacket.data.add("fieldNames", "handleStatus");
                    ajaxPacket.data.add("fieldValues", "3");
                    ajaxPacket.data.add("newHsIds", newHsIds.join(","));
                    core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                        var jsonData = eval("(" + response + ")");
                        // 服务调用是否成功
                        var isSuccess = jsonData.success;
                        if (isSuccess) {
                            alertMsg.correct("处理成功");
                            oh007.refleshHs();
                        } else {
                            alertMsg.error(jsonData.errMsg);
                        }
                    });
                }
            });
        }
    },
    /**
     * 处理延期结算
     * @param hsId 被征收房屋编号
     * @param newHsId 选房编号
     */
    ctDelay: function (oldHsId, newHsId) {
        if (oldHsId == '' || newHsId == '') {
            alertMsg.warn("只有已经被选房的房源可以进行延期签约!");
        } else {
            var url = "eland/ct/ct006/ct006-init.gv?oldHsId=" + oldHsId + "&newHsId=" + newHsId;
            index.quickOpen(url, {opCode: "ct006-init", opName: "延期签约", fresh: true});
        }
    },
    /**
     * 导出建筑信息
     */
    exportbuild: function () {
        var regId = $("input[name=sltRegId]", navTab.getCurrentPanel()).val();
        var regUseType = $("input[name=regUseType]", navTab.getCurrentPanel()).val();
        var rhtType = $("input[name=rhtType]", navTab.getCurrentPanel()).val();
        var form = $('#oh00701frm', navTab.getCurrentPanel());
        var url = getGlobalPathRoot() + "eland/oh/oh001/oh00101-exportB.gv?prjCd=" + getPrjCd();
        var packet = new AJAXPacket(url, "数据导出处理中, 请稍候......");
        packet.data.add("prjCd", getPrjCd());
        packet.data.add("regId", regId);
        // 其他查询条件
        packet.data.add("conditionNames", $("input[name=conditionName]", form).val());
        packet.data.add("conditions", $("input[name=condition]", form).val());
        packet.data.add("conditionValues", $("input[name=conditionValue]", form).val());
        packet.data.add("moreResultField", $("input.js_moreResultField", form).val());
        packet.data.add("colorItemCd", $("input.js_colorItemCd", form).val());
        packet.data.add("colorRelCode", $("input.js_colorRelCode", form).val());
        packet.data.add("showCdFieldName", $("input.js_showCdFieldName", form).val());
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
};
$(document).ready(function () {
    // 拖动屏幕切分效果   如果要设置 cookie 在{里编写} cookieName: "cookieName"
    LeftMenu.init("oh007DivTree", "oh007Right", {cookieName: "showFyTree"});
    // 区域树导航模式
    var showFyTreeModel = $("input[name=showFyTreeModel", navTab.getCurrentPanel()).val();
    if ("bd" == showFyTreeModel) {
        oh007.initFullRegTree();
    } else {
        // 初始化左侧区域树
        oh007.switchTree($("span.js_reload", navTab.getCurrentPanel()));
    }
    // 显示左侧导航
    var showFyTree = $("input[name=showFyTree", navTab.getCurrentPanel()).val();
    if ("false" == showFyTree) {
        $("span.split_but", navTab.getCurrentPanel()).trigger("click");
    }

});
