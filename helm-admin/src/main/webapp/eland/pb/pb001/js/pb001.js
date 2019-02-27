var pb001 = {

    /**
     * 调整管理组织
     */
    switchTree: function (obj) {
        var $obj = $(obj);
        var textSpan = $obj.parent().find("span.panel_title");
        var spanText = textSpan.html();
        var pb001TreeContain = $("#pb001Tree", navTab.getCurrentPanel());
        var pb001OrgTreeContain = $("#pb001OrgTree", navTab.getCurrentPanel());
        if ("管 理 导 航" === spanText) {
            spanText = "区 域 导 航";
            pb001TreeContain.show();
            pb001OrgTreeContain.hide();
            var zTree = $.fn.zTree.getZTreeObj("pb001Tree");
            if (pb001TreeContain.is(":empty")) {
                pb001.initFullRegTree();
            }
            // 保存cookie
            setPCookie("showYlTreeModel", "reg", "/");
        } else {
            spanText = "管 理 导 航";
            pb001TreeContain.hide();
            pb001OrgTreeContain.show();
            if (pb001OrgTreeContain.is(":empty")) {
                pb001.initOrgTree();
            }
            // 保存cookie
            setPCookie("showYlTreeModel", "org", "/");
        }
        textSpan.html(spanText);
    },

    //初始化左侧区域树
    initOrgTree: function () {
        // 重新加载
        var packet = new AJAXPacket();
        packet.data.add("prjCd", getPrjCd());
        packet.data.add("grantType", "p_h_data");
        packet.data.add("objValue", "");
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
                        callback: {
                            onClick: pb001.clickOrgNode
                        },
                        check: {
                            autoCheckTrigger: false,
                            enable: false
                        }
                    };
                    // 初始化目录树
                    var zTree = $.fn.zTree.init($("#pb001OrgTree"), pjOrgTreeSetting, treeJson);
                    // 显示根节点数据
                    var rootNode = zTree.getNodesByFilter(function (node) {
                        return node.level == 0
                    }, true);
                    if (rootNode != null) {
                        zTree.selectNode(rootNode);
                        zTree.expandNode(rootNode);
                        pb001.clickOrgNode(null, "pb001OrgTree", rootNode);
                    }
                }
                else {
                    alertMsg.warn(jsonData.data.errMsg);
                }
            }, true, false
        )
    },

    /**
     * 左侧区域树点击事件
     */
    clickOrgNode: function (event, treeId, treeNode) {
        // 修改点击节点
        $("input[name=rhtRegId]", navTab.getCurrentPanel()).val("");
        $("input[name=rhtOrgId]", navTab.getCurrentPanel()).val(treeNode.id);
        // 显示模块
        $("#pb001Context", navTab.getCurrentPanel()).hide();
        $("#pb00102_qry", navTab.getCurrentPanel()).show();
        pb001.refleshBuild();
    },

    /**
     * 初始化显示区域树
     */
    initFullRegTree: function () {
        // 重新加载
        var packet = new AJAXPacket();
        var prjCd = getPrjCd();
        packet.data.add("prjCd", prjCd);
        packet.data.add("treeType", "1");
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
                            onClick: pb001.clickRegNode,
                            beforeDrop: pb001.beforeDrop,
                            beforeRemove: pb001.beforeRemove
                        },
                        view: {
                            addHoverDom: pb001.addHoverDom,
                            removeHoverDom: pb001.removeHoverDom,
                            selectedMulti: false
                        },
                        edit: {
                            drag: {
                                isCopy: false,
                                isMove: pb001.getMoveAble()
                            },
                            enable: pb001.getEditAble() || pb001.getMoveAble(),
                            showRemoveBtn: false,
                            showRenameBtn: false
                        }
                    };
                    // 初始化目录树
                    var zTree = $.fn.zTree.init($("#pb001Tree", navTab.getCurrentPanel()), pjOrgTreeSetting, treeJson);

                    // 初始化右侧
                    var initRegId = $("input[name=initRegId]", navTab.getCurrentPanel()).val();
                    // 初始化指定院落
                    if (initRegId != "") {
                        var pNode = zTree.getNodeByParam("id", initRegId, null);
                        zTree.selectNode(pNode);
                        pb001.clickRegNode(null, "ph001Tree", pNode);
                    } else {
                        // 显示根节点数据
                        var rootNode = zTree.getNodesByFilter(function (node) {
                            return node.level == 0
                        }, true);
                        pb001.initQuery(rootNode.id, "2");
                    }

                }
                else {
                    alertMsg.warn(jsonData.data.errMsg);
                }
            }, true, false
        );
    },

    /**
     * 左侧区域树点击事件
     */
    clickRegNode: function (event, treeId, treeNode) {
        var isBuld = false;
        if (treeNode.buldId && treeNode.buldId != '') {
            isBuld = true;
        }
        if (isBuld) {
            // 点击院落，展示院落基本信息
            pb001.initBuildSummary(treeNode.buldId);
        } else {
            pb001.initQuery(treeNode.id, "2");
        }
    },

    /**
     * 修改区域权限
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
     * 删除区域权限
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
     * 移动区域权限
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
     * 区域树鼠标悬浮显示的效果
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
        if (pb001.getRemoveAble() && $("#removeBtn_" + treeNode.tId, divArea).length == 0) {
            var addStr = "<span class='button marr5 remove' id='removeBtn_" + treeNode.tId + "' title='删除区域' onfocus='this.blur();'/>";
            sObj.after(addStr);
            var btn = $("#removeBtn_" + treeNode.tId);
            if (btn) btn.bind("click", function () {
                if (isBuld) {
                    pb002Op.deleteBuild(treeNode.buldId, "pb001Tree", pb001.aftDeleteBuild);
                } else {
                    alertMsg.confirm("您确定要删除该节点及其下的所有子节点吗?", {
                        okCall: function () {
                            pb001.removeReg("pb001Tree", treeNode);
                        }
                    });
                }
                return false;
            });
        }
        if (pb001.getEditAble() && $("#eidtRegBtn_" + treeNode.tId, divArea).length == 0) {
            var addStr = "<span class='button marr5 edit' id='eidtRegBtn_" + treeNode.tId + "' title='修改区域' onfocus='this.blur();'/>";
            sObj.after(addStr);
            var btn = $("#eidtRegBtn_" + treeNode.tId);
            if (btn) btn.bind("click", function () {
                pb001.editReg(treeNode.id);
                return false;
            });
        }
        if (!isBuld && pb001.getEditAble() && $("#addRegBtn_" + treeNode.tId, divArea).length == 0) {
            var addStr = "<span class='button marr5 add' id='addRegBtn_" + treeNode.tId + "' title='新增区域' onfocus='this.blur();'/>";
            sObj.after(addStr);
            var btn = $("#addRegBtn_" + treeNode.tId);
            if (btn) btn.bind("click", function () {
                pb001.addReg(treeNode.id);
                return false;
            });
        }
    },

    /**
     * 区域树移开光标删除事件
     * @param treeId  树编号
     * @param treeNode 选择节点编号
     */
    removeHoverDom: function (treeId, treeNode) {
        $("#removeBtn_" + treeNode.tId, navTab.getCurrentPanel()).unbind().remove();
        $("#addRegBtn_" + treeNode.tId, navTab.getCurrentPanel()).unbind().remove();
        $("#eidtRegBtn_" + treeNode.tId, navTab.getCurrentPanel()).unbind().remove();
    },

    /**
     * 区域树移动节点-拖动放下节点时候触发
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
        // 项目编码
        var prjCd = getPrjCd();
        packet.data.add("prjCd", prjCd);
        // 提交界面展示
        packet.url = getGlobalPathRoot() + "eland/ph/ph001/ph001-moveRegion.gv";
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
     * 修改查询方式
     */
    changeType: function () {
        pb001.initQuery('');
    },

    /**
     * 删除建筑后的触发查询动作
     */
    aftDeleteBuild: function () {
        pb001.initQuery('');
    },
    /*查看居民信息*/
    viewHouse: function (hsId) {
        var url = "eland/pb/ph003/ph003-init.gv?hsId=" + hsId;
        index.openTab(null, "ph003-init", url, {title: "居民信息", fresh: true});
    },

    /**
     * 初始化查询
     * @param nodeId
     */
    initQuery: function (nodeId, queryType) {
        // 显示模块
        $("#pb001Context", navTab.getCurrentPanel()).hide();
        $("#pb00102_qry", navTab.getCurrentPanel()).show();
        // 修改点击节点
        $("input[name=rhtRegId]", navTab.getCurrentPanel()).val(nodeId);
        pb001.refleshBuild();
    },

    /**
     * 调用通用查询查询房源数据
     * @param conditionFieldArr 查询字段数组
     * @param conditionArr 查询条件数组
     * @param conditionValueArr 查询值数组
     */
    queryBuild: function (obj) {
        Page.queryCondition(obj, {
            changeConditions: pb001.changeConditions,
            formObj: $("#pb001frm", navTab.getCurrentPanel())
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
            $("#pb001QSchDialog", navTab.getCurrentPanel()).show().attr("active", "on");
            $("#pb001QSchDialog input:eq(0)", navTab.getCurrentPanel()).focus();
        }
    },

    /**
     * 离开检索按钮
     */
    leaveQSch: function () {
        setTimeout(function () {
            pb001.closeQSch(false);
        }, 300);
    },
    /**
     * 关闭检索
     * @param forceFlag 强制关闭
     */
    closeQSch: function (forceFlag) {
        var close = forceFlag;
        if (!forceFlag) {
            var active = $("#pb001QSchDialog", navTab.getCurrentPanel()).attr("active");
            if ("on" == active) {
                close = false;
            } else {
                close = true;
            }
        }
        if (close) {
            $("#pb001QSchDialog", navTab.getCurrentPanel()).hide();
        }
    },

    /**
     * 快速检索
     */
    qSchData: function () {
        var formObj = $('#pb001frm', navTab.getCurrentPanel());
        var conditionName = $("input[name=conditionName]", formObj).val().split(",");
        var condition = $("input[name=condition]", formObj).val().split(",");
        var conditionValue = $("input[name=conditionValue]", formObj).val().split(",");
        $("#pb001QSchDialog", navTab.getCurrentPanel()).find("input").each(function () {
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
        pb001.closeQSch(true);
        pb001.refleshBuild();
    },

    /**
     * 改变查询条件
     * @param newCondition
     */
    changeConditions: function (newCondition) {
        var formObj = $('#pb001frm', navTab.getCurrentPanel());
        $("input[name=conditionName]", formObj).val(newCondition.conditionNames);
        $("input[name=condition]", formObj).val(newCondition.conditions);
        $("input[name=conditionValue]", formObj).val(newCondition.conditionValues);
        $("input.js_conditionValue", formObj).val(newCondition.conditionValueTexts);
        $("input[name=resultField]", formObj).val(newCondition.resultFields);
        // 排序字段
        $("input[name=sortColumn]", formObj).val(newCondition.sortColumn);
        $("input[name=sortOrder]", formObj).val(newCondition.sortOrder);
        // 刷新建筑
        pb001.refleshBuild();
    },

    /**
     * 统计信息
     */
    refleshBuild: function () {
        // 查询参数
        var viewModel = $("input.js_query_model", navTab.getCurrentPanel()).val();
        var regId = $("input[name=sltRegId]", navTab.getCurrentPanel()).val();
        var form = $('#pb001frm', navTab.getCurrentPanel());
        if (!viewModel) {
            viewModel = "1";
        }
        // 执行通用分页查询
        if (viewModel == "1") {
            Page.query(form, "");
        } else {
            // 显示院落统计视图
            var packet = new AJAXPacket();
            packet.data.add("prjCd", getPrjCd());
            packet.data.add("regId", regId);
            // 其他查询条件
            packet.data.add("conditionNames", $("input[name=conditionName]", form).val());
            packet.data.add("conditions", $("input[name=condition]", form).val());
            packet.data.add("conditionValues", $("input[name=conditionValue]", form).val());
            packet.url = getGlobalPathRoot() + "eland/pb/pb001/pb001-initBRpt.gv";
            core.ajax.sendPacketHtml(packet, function (response) {
                // 显示模块
                $("#pb00102Context", navTab.getCurrentPanel()).html(response);
                initUI($("#pb00102Context", navTab.getCurrentPanel()));
            });
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
        pb001.refleshBuild();
    },

    /**
     * 初始化查询
     * @param nodeId
     */
    initBuildSummary: function (buildId) {
        // 显示模块
        $("#pb001Context", navTab.getCurrentPanel()).show();
        $("#pb00102_qry", navTab.getCurrentPanel()).hide();
        // 重新加载
        var packet = new AJAXPacket();
        var prjCd = getPrjCd();
        packet.data.add("prjCd", prjCd);
        packet.data.add("buildId", buildId);
        packet.url = getGlobalPathRoot() + "eland/pb/pb001/pb001-initBuildSummary.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            $("#pb001Context", navTab.getCurrentPanel()).html(response);
            initUI($("#pb001Context", navTab.getCurrentPanel()));
        });
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
        var url = getGlobalPathRoot() + "eland/ph/ph001/ph001-addRegion.gv?updateTreeId=pb001Tree"
            + "&upRegId=" + upRegId + "&prjCd=" + prjCd + "&regUseType=" + 1;
        $.pdialog.open(url, "ph001031", "区域信息",
            {mask: true, max: false, maxable: false, resizable: false, width: 800, height: 550});
    },
    /**
     * 修改区域节点信息
     * @param upRegId 上级区域节点
     * @param regId
     */
    editReg: function (regId) {
        if (regId == 0) {
            return;
        }
        var url = getGlobalPathRoot() + "eland/ph/ph001/ph001-editRegion.gv?updateTreeId=pb001Tree"
            + "&regId=" + regId + "&prjCd=" + getPrjCd() + "&regUseType=" + 1;
        $.pdialog.open(url, "pb001031", "区域信息",
            {mask: true, max: false, maxable: false, resizable: false, width: 800, height: 550});
    },

    /**
     * 删除区域节点,调用ph001的控制层
     * @param treeId 删除节点
     * @param treeNode
     */
    removeReg: function (treeId, treeNode) {
        var packet = new AJAXPacket();
        // 当前节点信息
        packet.data.add("regId", treeNode.id);
        // 区域类型
        packet.data.add("regUseType", "1");
        // 区域归属项目
        var prjCd = getPrjCd();
        packet.data.add("prjCd", prjCd);
        // 提交界面展示
        packet.url = getGlobalPathRoot() + "eland/ph/ph001/ph001-deleteRegion.gv";
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

    /**
     * 展开导航树
     * @param obj 点击对象
     */
    extendOrClose: function (obj) {
        var $span = $(obj);
        var zTree = null;
        if ($("#pb001Tree", navTab.getCurrentPanel()).is(":hidden")) {
            zTree = $.fn.zTree.getZTreeObj("pb001OrgTree");
        } else {
            zTree = $.fn.zTree.getZTreeObj("pb001Tree");
        }
        if ($span.html() == "展开") {
            zTree.expandAll(true);
            $span.html("折叠");
        } else {
            zTree.expandAll(false);
            $span.html("展开");
        }
    },

    adjustHeight: function () {
        var pb001DivTreeH = $("#pb001DivTree", navTab.getCurrentPanel()).height();
        var pb001ConditionH = $("#pb001Condition", navTab.getCurrentPanel()).height();
        var height = pb001DivTreeH - pb001ConditionH - 122;
        $("#pb00102_list_print", navTab.getCurrentPanel()).find("div.gridScroller").css("height", height + "px");
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
            pb001.adjustHeight();
        } else {
            $pucker.removeClass("expandable").addClass("collapsable");
            $content.show();
            pb001.adjustHeight();
        }
    },
    /**
     *新建房屋
     * */
    addHouse: function () {
        var frm = $("#ph00104frm", navTab.getCurrentPanel());
        var buldId = $("input[name=buldId]", frm).val();
        var url = "eland/ph/ph003/ph00301-initHs.gv?prjCd=" + getPrjCd() + "&buildId" + buldId;
        index.quickOpen(url, {opCode: "ph00301-initHs", opName: "信息登记", fresh: true});
    },

    /**
     * 查询展示建筑下的房产信息
     */
    queryBuildHs: function () {
        var frm = $("#ph00104frm", navTab.getCurrentPanel());
        var buldId = $("input[name=buldId]", frm).val();
        if (buldId != "") {
            //查询房产
            Page.query(frm, "");
        }
    },
    /**
     * 删除选中的房产
     */
    deleteSltHs: function () {
        var houseIds = [];
        navTab.getCurrentPanel()
            .find(":checkbox[checked][name=hsId]").each(
            function (i) {
                houseIds.push($(this).val());
            }
        );
        if (houseIds.length == 0) {
            alertMsg.warn("请选择要删除的房产");
        } else {
            alertMsg.confirm("选择的房产数据将被删除，你确定要删除吗？", {
                okCall: function () {
                    var packet = new AJAXPacket();
                    // 区域归属项目
                    var prjCd = getPrjCd();
                    packet.data.add("houseId", houseIds.join(","));
                    packet.data.add("prjCd", prjCd);
                    // 提交界面展示
                    packet.url = getGlobalPathRoot() + "eland/ph/ph001/ph001-deleteHouse.gv";
                    // 处理删除
                    core.ajax.sendPacketHtml(packet, function (response) {
                        var data = eval("(" + response + ")");
                        var isSuccess = data.success;
                        if (isSuccess) {
                            var buildId = $("input[name=buildId]", navTab.getCurrentPanel()).val();
                            // 重新显示院落信息
                            pb001.initBuildSummary(buildId);
                        } else {
                            alertMsg.error(data.errMsg);
                        }
                    }, true);
                }
            });
        }
    },
    /**
     * 导出建筑内的房产信息
     */
    exportHs: function () {
        Page.exportExcel('pb001HsList', false)
    },
    /**
     * 房屋信息
     * @param buildId
     */
    openHs: function (hsId) {
        //打开详细房产详情页面
        var url = "eland/ph/ph003/ph003-init.gv?hsId=" + hsId;
        index.quickOpen(url, {opCode: "ph003-init", opName: "居民详情", fresh: true});
    }

};

$(document).ready(function () {
    // 拖动屏幕切分效果
    $("div.split_line", navTab.getCurrentPanel()).mousedown(function (event) {
        $("div.split_line", navTab.getCurrentPanel()).jDrag({
            scop: true, cellMinW: 20,
            move: "horizontal",
            event: event,
            stop: function (moveObj) {
                var leftDiv = $("#pb001DivTree", navTab.getCurrentPanel());
                var rightDiv = $("#pb001001", navTab.getCurrentPanel());
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
    /**
     * 停止键盘响应事件
     */
    $("span.split_but", navTab.getCurrentPanel()).mousedown(function (event) {
        stopEvent(event);
    });

    /**
     * 响应鼠标点击事件
     */
    $("span.split_but", navTab.getCurrentPanel()).click(function (event) {
        var splitLine = $(this).parent();
        var leftMenu = splitLine.prev("div.left_menu");
        if (leftMenu.is(":visible")) {
            leftMenu.hide();
            splitLine.addClass("menu_hide");
            var rightDiv = $("#pb001001", navTab.getCurrentPanel());
            rightDiv.css("margin-left", "10px");

            var resizeGrid = $("div.j-resizeGrid", rightDiv);
            if (resizeGrid.length > 0) {
                resizeGrid.each(function () {
                    var $this = $(this);
                    $this.jResize();
                });
            }
            // 保存cookie
            setPCookie("showYlTree", "false", "/");
        } else {
            leftMenu.show();
            splitLine.removeClass("menu_hide");
            var rightDiv = $("#pb001001", navTab.getCurrentPanel());
            rightDiv.css("margin-left", leftMenu.width() + 10 + "px");
            var resizeGrid = $("div.j-resizeGrid", rightDiv);
            if (resizeGrid.length > 0) {
                resizeGrid.each(function () {
                    var $this = $(this);
                    $this.jResize();
                });
            }
            // 保存cookie
            setPCookie("showYlTree", "true", "/");
        }
    });

    // 区域树导航模式
    var showYlTreeModel = $("input[name=showYlTreeModel", navTab.getCurrentPanel()).val();
    if ("reg" == showYlTreeModel) {
        pb001.initFullRegTree();
    } else {
        //初始化左侧区域树
        pb001.switchTree($("span.js_reload", navTab.getCurrentPanel()));
    }
    // 显示左侧导航
    var showYlTree = $("input[name=showYlTree", navTab.getCurrentPanel()).val();
    if ("false" == showYlTree) {
        $("span.split_but", navTab.getCurrentPanel()).trigger("click");
    }
});
