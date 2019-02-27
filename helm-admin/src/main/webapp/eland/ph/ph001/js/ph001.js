var ph001 = {
    uploadObj: null,
    /**
     * 选择管理区域
     * @param obj
     */
    sltReg: function (obj) {
        var treeType = $("select[name=regUseType]", navTab.getCurrentPanel()).val();
        var rhtType = $("input[name=rhtType]", navTab.getCurrentPanel()).val();
        if (!treeType) {
            treeType = $("input[name=rhtType]", navTab.getCurrentPanel()).val();
        }
        var url = getGlobalPathRoot() + "eland/pj/pj004/pj004-initTree.gv?treeType=" + treeType
            + "&fromOp=hs001" + "&rhtType=" + rhtType;
        var width = 150;
        $(obj).openTip({
            href: url, height: "230",
            width: "200", offsetX: 0, offsetY: 30
        });
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
        var opCode = $("input[name=_opCode]", divArea).val();//_" + opCode
        var isBuld = false;
        if (treeNode.buldId && treeNode.buldId != '') {
            isBuld = true;
        }
        var isRoot = false;
        if (!treeNode.pId) {
            isRoot = true;
        }
        if (!isRoot && ph001.getRemoveAble() && $("#removeBtn_" + treeNode.tId, divArea).length == 0) {
            var addStr = "<span class='button marr5 remove' id='removeBtn_" + treeNode.tId + "' title='删除区域' onfocus='this.blur();'/>";
            sObj.after(addStr);
            var btn = $("#removeBtn_" + treeNode.tId);
            if (btn) btn.bind("click", function () {
                if (isBuld) {
                    ph001.deleteBuild(treeNode.buldId);
                } else {
                    alertMsg.confirm("您确定要删除该节点及其下的所有子节点吗?", {
                        okCall: function () {
                            ph001.removeReg("ph001Tree_" + opCode, treeNode);
                        }
                    });
                }
                return false;
            });
        }
        if (ph001.getEditAble() && $("#eidtRegBtn_" + treeNode.tId, divArea).length == 0) {
            var addStr = "<span class='button marr5 edit' id='eidtRegBtn_" + treeNode.tId + "' title='修改区域' onfocus='this.blur();'/>";
            sObj.after(addStr);
            var btn = $("#eidtRegBtn_" + treeNode.tId);
            if (btn) btn.bind("click", function () {
                ph001.editReg(treeNode.id);
                return false;
            });
        }
        if (!isBuld && ph001.getEditAble() && $("#addRegBtn_" + treeNode.tId, divArea).length == 0) {
            var addStr = "<span class='button marr5 add' id='addRegBtn_" + treeNode.tId + "' title='新增区域' onfocus='this.blur();'/>";
            sObj.after(addStr);
            var btn = $("#addRegBtn_" + treeNode.tId);
            if (btn) btn.bind("click", function () {
                ph001.addReg(treeNode.id);
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
        //if (moveType != 'inner') {
        //    if (treeNodes[0].pId != targetNode.pId) {
        //        alertMsg.warn("不允许跨节点移动");
        //        return false;
        //    }
        //} else {
        //    if (treeNodes[0].id != targetNode.pId) {
        //        alertMsg.warn("不允许跨节点移动");
        //        return false;
        //    }
        //}
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

    //初始化左侧区域树
    initFullRegTree: function () {
        // 重新加载
        var packet = new AJAXPacket();
        var prjCd = getPrjCd();
        packet.data.add("prjCd", prjCd);
        packet.data.add("treeType", "1");
        packet.data.add("objValue", "");
        packet.data.add("rhtType", $("input[name=rhtType]", navTab.getCurrentPanel()).val());
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
                            onClick: ph001.clickNode,
                            beforeDrop: ph001.beforeDrop
                        },
                        view: {
                            addHoverDom: ph001.addHoverDom,
                            removeHoverDom: ph001.removeHoverDom,
                            selectedMulti: false
                        },
                        edit: {
                            drag: {
                                isCopy: false,
                                isMove: ph001.getMoveAble()
                            },
                            enable: ph001.getEditAble() || ph001.getMoveAble(),
                            showRemoveBtn: false,
                            showRenameBtn: false
                        }
                    };
                    // 初始化目录树
                    var opCode = $("input[name=_opCode]", navTab.getCurrentPanel()).val();//_" + opCode
                    var zTree = $.fn.zTree.init($("#ph001Tree_" + opCode, navTab.getCurrentPanel()), pjOrgTreeSetting, treeJson);
                    var initRegId = $("input[name=initRegId]", navTab.getCurrentPanel()).val();
                    if (initRegId) {
                        var pNode = zTree.getNodeByParam("id", initRegId, null);
                        zTree.selectNode(pNode);
                        ph001.clickNode(null, "ph001Tree_" + opCode, pNode);
                    } else {
                        // 显示根节点数据
                        var rootNode = zTree.getNodesByFilter(function (node) {
                            return node.level == 0
                        }, true);
                        if (rootNode != null) {
                            zTree.selectNode(rootNode);
                            ph001.clickNode(null, "ph001Tree_" + opCode, rootNode);
                        }
                    }
                }
                else {
                    alertMsg.warn(jsonData.errMsg);
                }
            }, true, false
        );
    },

    //初始化左侧区域树
    initOrgTree: function () {
        var opCode = $("input[name=_opCode]", navTab.getCurrentPanel()).val();//_" + opCode
        // 重新加载
        var packet = new AJAXPacket();
        packet.data.add("prjCd", getPrjCd());
        packet.data.add("grantType", $("input[name=rhtType]", navTab.getCurrentPanel()).val());
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
                            onClick: ph001.clickOrgNode
                        },
                        check: {
                            autoCheckTrigger: false,
                            enable: false
                        }
                    };
                    // 初始化目录树
                    var zTree = $.fn.zTree.init($("#ph001OrgTree_" + opCode), pjOrgTreeSetting, treeJson);
                    // 显示根节点数据
                    var rootNode = zTree.getNodesByFilter(function (node) {
                        return node.level == 0
                    }, true);
                    if (rootNode != null) {
                        zTree.selectNode(rootNode);
                        ph001.clickOrgNode(null, "ph001OrgTree_" + opCode, rootNode);
                    }
                }
                else {
                    alertMsg.warn(jsonData.errMsg);
                }
            }, true, false
        )
    },

    /**
     * 左侧区域树点击事件
     */
    clickNode: function (event, treeId, treeNode) {
        var queryModel = $("input.js_query_model", navTab.getCurrentPanel()).val();
        if (treeNode.useType != '2') {
            if (queryModel == '2') {
                $("input.js_query_model", navTab.getCurrentPanel()).val("1");
            }
        }
        ph001.initHouseView(treeNode.buldId, treeNode.id, treeNode.useType);
    },

    /**
     * 左侧区域树点击事件
     */
    clickOrgNode: function (event, treeId, treeNode) {
        var queryModel = $("input.js_query_model", navTab.getCurrentPanel()).val();
        if (queryModel == '2') {
            $("input.js_query_model", navTab.getCurrentPanel()).val("1");
        }
        $("#changeModel", navTab.getCurrentPanel()).hide();
        // 修改点击节点
        $("input[name=rhtRegId]", navTab.getCurrentPanel()).val("");
        $("input[name=rhtOrgId]", navTab.getCurrentPanel()).val(treeNode.id);
        ph001.refleshBuild();
    },

    /**
     * 修改查询方式
     */
    changeType: function () {
        ph001.initQuery('');
    },

    /*查看居民信息*/
    viewHouse: function (hsId) {
        var url = "eland/ph/ph003/ph003-init.gv?hsId=" + hsId;
        index.quickOpen(url, {opCode: "ph003-init", opName: "居民信息", fresh: true});
    },

    /**
     * 初始化查询
     * @param nodeId
     */
    initQuery: function (nodeId, queryType) {
        // 重新加载
        var packet = new AJAXPacket();
        var prjCd = getPrjCd();
        packet.data.add("prjCd", prjCd);
        packet.data.add("regId", nodeId);
        packet.data.add("queryType", queryType);
        packet.url = getGlobalPathRoot() + "eland/ph/ph001/ph001-initQ.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            $("#ph001Context", navTab.getCurrentPanel()).html(response);
            initUI($("#ph001Context", navTab.getCurrentPanel()));
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
        var url = getGlobalPathRoot() + "eland/ph/ph001/ph001-addRegion.gv?"
            + "&upRegId=" + upRegId + "&prjCd=" + prjCd + "&regUseType=" + 1;
        $.pdialog.open(url, "ph001031", "新增区域",
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
        var url = getGlobalPathRoot() + "eland/ph/ph001/ph001-editRegion.gv?"
            + "&regId=" + regId + "&prjCd=" + getPrjCd() + "&regUseType=" + 1;
        $.pdialog.open(url, "ph001031", "编辑区域",
            {mask: true, max: false, maxable: false, resizable: false, width: 800, height: 550});
    },

    /**
     * 删除节点
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

    viewOldHs: function (oldHsId, regId) {
        var url = "eland/ph/ph003/ph003-init.gv?hsId=" + oldHsId + "&regId=" + regId
            + "&prjCd=" + getPrjCd();
        index.quickOpen(url, {opCode: "pb003-init", opName: "房屋信息", fresh: true});
    },

    extendOrClose: function (obj) {
        var $span = $(obj);
        var zTree = null;
        var opCode = $("input[name=_opCode]", navTab.getCurrentPanel()).val();//_" + opCode
        if ($("#ph001Tree_" + opCode, navTab.getCurrentPanel()).is(":hidden")) {
            zTree = $.fn.zTree.getZTreeObj("ph001OrgTree_" + opCode);
        } else {
            zTree = $.fn.zTree.getZTreeObj("ph001Tree_" + opCode);
        }
        if ($span.html() == "展开") {
            zTree.expandAll(true);
            $span.html("折叠");
        } else {
            zTree.expandAll(false);
            $span.html("展开");
        }
    },

    /**
     * 查询院落内房产
     */
    queryYardSummary: function () {
        if ($("#ph00102frm", navTab.getCurrentPanel()).length > 0) {
            Query.queryList('ph00102frm', 'ph00102_list_print');
        } else {
            $("#ph001Context", navTab.getCurrentPanel()).html("");
        }
    },

    adjustHeight: function () {
        var ph001DivTreeH = $("#ph001DivTree", navTab.getCurrentPanel()).height();
        var ph001ConditionH = $("#ph001Condition", navTab.getCurrentPanel()).height();
        var height = ph001DivTreeH - ph001ConditionH - 122;
        $("#ph00102_list_print", navTab.getCurrentPanel()).find("div.gridScroller").css("height", height + "px");
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
            ph001.adjustHeight();
        } else {
            $pucker.removeClass("expandable").addClass("collapsable");
            $content.show();
            ph001.adjustHeight();
        }
    },

    /**
     *新建房屋
     * */
    addHouse: function () {
        var url = "eland/ph/ph003/ph00301-initS.gv";
        index.openTab(null, "ph00301-initS", url, {title: "腾退录入", fresh: true});
    },

    /**
     * 切换展示视图
     */
    changeShowModel: function (obj, viewModel) {
        var $this = $(obj);
        var currentModelObj = $("input.js_query_model", navTab.getCurrentPanel());
        var currentModel = currentModelObj.val();
        if (currentModel == viewModel) {
            return;
        }
        // 修改模式
        currentModelObj.val(viewModel);
        $this.find("span").addClass("active");
        $this.siblings("li").find("span.active").removeClass("active");
        ph001.refleshBuild();
    },

    /**
     * 初始化房屋信息列表
     * @param buildId
     */
    initHouseView: function (buildId, rhtId, useType) {
        $("input.figureBuildId", navTab.getCurrentPanel()).val(buildId);
        $("input.figureRhtId", navTab.getCurrentPanel()).val(rhtId);
        if (useType == '2') {
            $("#changeModel", navTab.getCurrentPanel()).show();
        } else {
            //$("input.js_query_model", navTab.getCurrentPanel()).val("1");
            $("#changeModel", navTab.getCurrentPanel()).hide();
        }
        // 修改点击节点
        $("input[name=rhtRegId]", navTab.getCurrentPanel()).val(rhtId);
        $("input[name=rhtOrgId]", navTab.getCurrentPanel()).val("");
        ph001.refleshBuild();
    },

    /**
     * 调用通用查询查询房源数据
     * @param conditionFieldArr 查询字段数组
     * @param conditionArr 查询条件数组
     * @param conditionValueArr 查询值数组
     */
    queryHs: function (obj) {
        ph001.closeQSch(true);
        Page.queryConditionTwo($("div.panelBar a.find", navTab.getCurrentPanel()), {
            changeConditions: ph001.changeConditions,
            formObj: $("#ph001queryForm", navTab.getCurrentPanel())
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
            $("#ph001QSchDialog", navTab.getCurrentPanel()).show().attr("active", "on");
            $("#ph001QSchDialog input:eq(0)", navTab.getCurrentPanel()).focus();
        }
    },

    /**
     * 离开检索按钮
     */
    leaveQSch: function () {
        setTimeout(function () {
            ph001.closeQSch(false);
        }, 300);
    },

    /**
     * 关闭检索
     * @param forceFlag 强制关闭
     */
    closeQSch: function (forceFlag) {
        var close = forceFlag;
        if (!forceFlag) {
            var active = $("#ph001QSchDialog", navTab.getCurrentPanel()).attr("active");
            if ("on" == active) {
                close = false;
            } else {
                close = true;
            }
        }
        if (close) {
            $("#ph001QSchDialog", navTab.getCurrentPanel()).hide();
        }
    },

    /**
     * 快速检索
     */
    qSchData: function () {
        var formObj = $('#ph001queryForm', navTab.getCurrentPanel());
        var conditionName = $("input[name=conditionName]", formObj).val().split(",");
        var condition = $("input[name=condition]", formObj).val().split(",");
        var conditionValue = $("input[name=conditionValue]", formObj).val().split(",");
        $("#ph001QSchDialog", navTab.getCurrentPanel()).find("input").each(function () {
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
        ph001.closeQSch(true);
        ph001.refleshBuild();
    },

    /**
     * 点击收藏
     * @param newCondition
     */
    clickFav: function (newCondition) {
        ph001.closeQSch(true);
        ph001.changeConditions(newCondition);
    },
    /**
     * 改变查询条件
     * @param newCondition
     */
    changeConditions: function (newCondition) {
        var formObj = $('#ph001queryForm', navTab.getCurrentPanel());
        $("input[name=conditionName]", formObj).val(newCondition.conditionNames);
        $("input[name=condition]", formObj).val(newCondition.conditions);
        $("input[name=conditionValue]", formObj).val(newCondition.conditionValues);
        $("input.js_conditionValue", formObj).val(newCondition.conditionValueTexts);
        $("input[name=resultField]", formObj).val(newCondition.resultFields);
        // 排序字段
        $("input[name=sortColumn]", formObj).val(newCondition.sortColumn);
        $("input[name=sortOrder]", formObj).val(newCondition.sortOrder);
        //
        $("input[name=autoConditions]", formObj).val(newCondition.autoConditions);
        $("input[name=psConditions]", formObj).val(newCondition.psConditionsJson);
        $("input[name=tagConditions]", formObj).val(newCondition.tagConditionsJson);

        ph001.refleshBuild();
    },

    /**
     * 统计信息
     */
    refleshBuild: function () {
        // 查询参数
        var viewModel = $("input.js_query_model", navTab.getCurrentPanel()).val();
        var regId = $("input[name=rhtRegId]", navTab.getCurrentPanel()).val();
        var form = $('#ph001queryForm', navTab.getCurrentPanel());
        var opCode = $("input[name=_opCode]", navTab.getCurrentPanel()).val();
        var rhtType = $("input[name=rhtType]", navTab.getCurrentPanel()).val();
        if (!viewModel) {
            viewModel = "1";
        }
        // 执行通用分页查询
        if (viewModel == "1") {
            // 显示模块
            Page.queryWithTag(form, "");
        } else {
            var packet = new AJAXPacket();
            packet.data.add("prjCd", getPrjCd());
            packet.data.add("regId", regId);
            // 其他查询条件
            packet.data.add("conditionNames", $("input[name=conditionName]", form).val());
            packet.data.add("conditions", $("input[name=condition]", form).val());
            packet.data.add("conditionValues", $("input[name=conditionValue]", form).val());
            packet.data.add("rhtOrgId", $("input[name=rhtOrgId]", form).val());
            packet.data.add("uOpCode", opCode);
            packet.data.add("rhtType", rhtType);
            if (viewModel == "2") {
                // 显示模块
                packet.data.add("buildId", $("input.figureBuildId", navTab.getCurrentPanel()).val());
                packet.data.add("rhtId", $("input.figureRhtId", navTab.getCurrentPanel()).val());
                packet.data.add("useType", "2");
                packet.data.add("regId", regId);
                packet.data.add("rhtType", rhtType);
                packet.url = getGlobalPathRoot() + "eland/ph/ph001/ph001-initB.gv";
            } else if (viewModel == "3") {
                packet.url = getGlobalPathRoot() + "eland/ph/ph001/ph001-initRpt.gv";
            }
            core.ajax.sendPacketHtml(packet, function (response) {
                // 显示模块
                $("#ph001Context", navTab.getCurrentPanel()).html(response);
                initUI($("#ph001Context", navTab.getCurrentPanel()));
            });
        }
    },

    /**
     * 初始化楼房信息列表
     * @param buildId
     */
    initbuildView: function (buildId, rhtId, useType) {
        // 显示模块
        var packet = new AJAXPacket();
        var prjCd = getPrjCd();
        packet.data.add("buildId", buildId);
        packet.data.add("rhtId", rhtId);
        packet.data.add("useType", useType);
        packet.data.add("prjCd", getPrjCd());
        packet.url = getGlobalPathRoot() + "eland/ph/ph001/ph001-initB.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            $("#ph001Context", navTab.getCurrentPanel()).html(response);
            initUI($("#ph001Context", navTab.getCurrentPanel()));
        });
    },

    /**
     * 房屋信息
     * @param buildId
     */
    openHouseView: function (hsId) {
        var packet = new AJAXPacket();
        var prjCd = getPrjCd();
        packet.data.add("hsId", hsId);
        packet.data.add("prjCd", prjCd);

        packet.url = getGlobalPathRoot() + "eland/ph/ph001/ph001-initHsView.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            $("#ph001Context", navTab.getCurrentPanel()).html(response);
            initUI($("#ph001Context", navTab.getCurrentPanel()));
        });
    },

    /**
     * 改变窗口的宽度
     */
    changeWidth: function () {
        var gridScroller = $("#ph001001query_list", navTab.getCurrentPanel()).find("div.gridScroller");
        //gridScroller.css("height", height + "px");
        gridScroller.css("width", gridScroller.prev("div.gridHeader").width());
    },
    /**
     * 删除院落信息
     * @param buildId 房屋编号
     */
    deleteBuild: function (buildId) {
        alertMsg.confirm("删除建筑将同时删除建筑内的房产信息，你确定要删除吗？", {
            okCall: function () {
                var packet = new AJAXPacket();
                // 区域归属项目
                var prjCd = getPrjCd();
                packet.data.add("buildId", buildId);
                packet.data.add("prjCd", prjCd);
                // 提交界面展示
                packet.url = getGlobalPathRoot() + "eland/ph/ph001/ph001-deleteBuild.gv";
                // 处理删除
                core.ajax.sendPacketHtml(packet, function (response) {
                    var data = eval("(" + response + ")");
                    //alert(data.buildId);
                    var isSuccess = data.success;
                    if (isSuccess) {
                        // 从目录树中删除节点
                        var opCode = $("input[name=_opCode]", navTab.getCurrentPanel()).val();//_" + opCode
                        var zTree = $.fn.zTree.getZTreeObj("ph001Tree_" + opCode);
                        var treeNode = zTree.getNodesByParam("buldId", data.buildId, null);
                        if (treeNode.length == 1) {
                            zTree.removeNode(treeNode[0], false);
                        }
                        // 重新查询数据
                        ph001.queryYardSummary();
                    } else {
                        alertMsg.error(data.errMsg);
                    }
                }, true);
            }
        });
    },

    /**
     * 房屋信息
     * @param buildId
     */
    openHs: function (hsId) {
        //打开详细房产详情页面
        var url = "eland/ph/ph003/ph003-init.gv?hsId=" + hsId;
        index.quickOpen(url, {opCode: "ph003-init", opName: "居民详情", fresh: true});
        // $.pdialog.open(url, "ct001-openChangeCtType", "更换安置方式", {
        //     mask: true, maxable: false, height: 165, width: 400, close: ctInfo.changeClose,
        //     param: {hsId: hsId}
        // });
    },

    /**
     * 房屋信息
     * @param buildId
     */
    editHs: function (hsId) {
        //打开详细房产详情页面
        var inputUrl = $("input[name=_inputUrl]", navTab.getCurrentPanel()).val();
        var inputName = $("input[name=_inputName]", navTab.getCurrentPanel()).val();
        var inputRhtCd = $("input[name=_inputRhtCd]", navTab.getCurrentPanel()).val();
        var openUrl = inputUrl + "&privilegeId=" + inputRhtCd + "&hsId=" + hsId;
        if (inputUrl == "") {
            alertMsg.warn("未配置修改功能!")
        } else {
            index.quickOpen(openUrl, {opCode: inputRhtCd, opName: inputName, fresh: true});
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
                            //ph001.initBuildSummary(buildId);
                            //删除之后不打开建筑详细页面，直接刷新列表
                            ph001.refleshBuild();
                        } else {
                            alertMsg.error(data.errMsg);
                        }
                    }, true);
                }
            });
        }
    },

    /**
     * 添加 新房产 通过房产信息页面
     */
    addHs: function () {
        var url = "eland/ph/ph003/ph00301-initHs.gv";
        //index.openTab(null, "ph00301-initHs", url, {title: "居民录入", fresh: true}, "居民录入");
        index.quickOpen(url, {opCode: "ph00301-initHs", opName: "信息登记", fresh: true});
    },

    /**
     * 批量修改房产资料
     * @param obj 点击对象
     */
    batchChange: function (obj) {
        var houseIds = [];
        navTab.getCurrentPanel().find(":checkbox[checked][name=hsId]").each(
            function (i) {
                houseIds.push($(this).val());
            }
        );
        if (houseIds.length == 0) {
            alertMsg.warn("请选择要修改的房产");
        } else {
            var opCode = $("input[name=_opCode]", navTab.getCurrentPanel()).val();
            var url = getGlobalPathRoot() + "eland/ph/ph003/ph003-initBEdit.gv?prjCd=" + getPrjCd()
                + "&opCode=" + opCode
                + "&houseIds=" + houseIds.join(",");
            $.pdialog.open(url, "ph003_be", "批量修改房产",
                {mask: true, max: false, maxable: false, resizable: false, width: 800, height: 600}
            );
        }
    },

    /**
     * 调整管理组织
     */
    switchTree: function (obj) {
        var $obj = $(obj);
        var textSpan = $obj.parent().find("span.panel_title");
        var spanText = textSpan.html();
        var opCode = $("input[name=_opCode]", navTab.getCurrentPanel()).val();//_" + opCode
        var ph001TreeContain = $("#ph001Tree_" + opCode, navTab.getCurrentPanel());
        var ph001OrgTreeContain = $("#ph001OrgTree_" + opCode, navTab.getCurrentPanel());
        if ("管 理 导 航" === spanText) {
            spanText = "区 域 导 航";
            ph001TreeContain.show();
            ph001OrgTreeContain.hide();
            if (ph001TreeContain.is(":empty")) {
                ph001.initFullRegTree();
            }
            // 保存cookie
            setPCookie("showJmTreeModel", "reg", "/");
        } else {
            spanText = "管 理 导 航";
            ph001TreeContain.hide();
            ph001OrgTreeContain.show();
            if (ph001OrgTreeContain.is(":empty")) {
                ph001.initOrgTree();
            }
            // 保存cookie
            setPCookie("showJmTreeModel", "org", "/");
        }
        textSpan.html(spanText);
    },

    /**
     * 全量导出方案
     */
    allExport: function (formCd) {
        var rhtRegId = $("input[name=rhtRegId]", navTab.getCurrentPanel()).val();
        var rhtOrgId = $("input[name=rhtOrgId]", navTab.getCurrentPanel()).val();
        var rhtType = $("input[name=rhtType]", navTab.getCurrentPanel()).val();
        var form = $('#ph001queryForm', navTab.getCurrentPanel());
        var conditionNames = $("input[name=conditionName]", form).val();
        var conditions = $("input[name=condition]", form).val();
        var conditionValues = $("input[name=conditionValue]", form).val();
        var ph001Context = $("#ph001Context", navTab.getCurrentPanel());
        var sortColumns = ph001Context.find("input[name=sortColumn]").val();
        var sortOrders = ph001Context.find("input[name=sortOrder]").val();
        if (!sortColumns || !sortOrders) {
            sortColumns = $("input[name=sortColumn]", form).val();
            sortOrders = $("input[name=sortOrder]", form).val();
        }
        var packet = new AJAXPacket();
        packet.url = getGlobalPathRoot() + "eland/ph/ph001/ph001-allExport.gv";
        packet.data.add("prjCd", getPrjCd());
        // 其他查询条件
        packet.data.add("rhtRegId", rhtRegId);
        packet.data.add("rhtOrgId", rhtOrgId);
        packet.data.add("conditionNames", conditionNames);
        packet.data.add("conditions", conditions);
        packet.data.add("conditionValues", conditionValues);
        packet.data.add("sortColumns", sortColumns);
        packet.data.add("sortOrders", sortOrders);
        packet.data.add("rhtType", rhtType);
        packet.data.add("formCd", formCd);
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            if (jsonData.success) {
                // 其他查询条件
                var docFileUrl = jsonData.docFileUrl;
                if (docFileUrl && docFileUrl != "") {
                    var url = getGlobalPathRoot() + "oframe/common/file/file-downByPath.gv?prjCd=" + getPrjCd()
                        + "&docName=" + encodeURI(encodeURI("全量导出"))
                        + "&urlType=r"
                        + "&docFileUrl=" + docFileUrl;
                    // 执行导出功能
                    window.open(url);
                } else {
                    alertMsg.warn("没有下载的数据");
                }
            } else {
                alertMsg.error(jsonData.errMsg);
            }
        }, true);
    },

    /**
     * 选房明细导出
     */
    chooseExport: function () {
        var rhtRegId = $("input[name=rhtRegId]", navTab.getCurrentPanel()).val();
        var rhtOrgId = $("input[name=rhtOrgId]", navTab.getCurrentPanel()).val();
        var form = $('#ph001queryForm', navTab.getCurrentPanel());
        var conditionNames = $("input[name=conditionName]", form).val();
        var conditions = $("input[name=condition]", form).val();
        var conditionValues = $("input[name=conditionValue]", form).val();
        var sortColumns = $("input[name=sortColumn]", form).val();
        var sortOrders = $("input[name=sortOrder]", form).val();
        // 其他查询条件
        var url = getGlobalPathRoot() + "eland/ph/ph001/ph001-chooseExport.gv?prjCd=" + getPrjCd()
            + "&rhtRegId=" + rhtRegId + "&rhtOrgId=" + rhtOrgId + "&conditionNames=" + conditionNames
            + "&conditions=" + conditions + "&conditionValues=" + conditionValues + "&sortColumns=" + sortColumns
            + "&sortOrders=" + sortOrders;
        // 执行导出功能
        window.open(url);
    },

    /**
     * 打开导入面板
     */
    importMb: function (formCd) {
        var url = getGlobalPathRoot() + "eland/ph/ph001/ph001-openImport.gv?prjCd=" + getPrjCd()
            + "&formCd=" + formCd;
        $.pdialog.open(url, "allImport", "信息导入", {
            mask: true,
            max: false,
            maxable: false,
            resizable: false,
            width: 600,
            height: 180
        });
    },

    /**
     * 全量导入方案
     */
    allImport: function () {
        var importMbFile = $("#importMbFile", $.pdialog.getCurrent()).val();
        if (importMbFile == "") {
            alertMsg.warn("请选择导入文件");
            return;
        }
        var uploadURL = getGlobalPathRoot() + "eland/ph/ph001/ph001-allImport.gv?prjCd=" + getPrjCd();
        var ajaxbg = $("#background,#progressBar");
        ajaxbg.show();
        $.ajaxFileUpload({
            url: uploadURL,
            secureuri: false,
            fileElementId: "importMbFile",
            dataType: 'json',
            success: function (data) {
                if (data.isSuccess) {
                    alertMsg.correct("处理成功");
                    $.pdialog.closeCurrent();
                    Page.query($("#ph001queryForm", navTab.getCurrentPanel()), "");
                } else {
                    alertMsg.error(data.errMsg);
                }
                ajaxbg.hide();
            }
        })
    },

    /**
     * 预计算方案
     */
    openRunScheme: function (schemeId) {
        var url = getGlobalPathRoot() + "eland/ph/ph001/ph001-openRunScheme.gv?prjCd=" + getPrjCd();
        $.pdialog.open(url, "ph001-runScheme", "预分计算",
            {mask: true, max: false, maxable: false, resizable: false, width: 600, height: 210});
    },

    /**
     * 预计算方案
     */
    runScheme: function (schemeId) {
        var formObj = $('#ph001queryForm', navTab.getCurrentPanel());
        var schemeId = $("select[name=schemeId]", $.pdialog.getCurrent()).val();
        var rhtType = $("input[name=rhtType]", navTab.getCurrentPanel()).val();
        if (!schemeId) {
            alertMsg.warn("没有选择预分方案，或系统没有建立预分方案，无法计算！");
            return false;
        }
        var packet = new AJAXPacket();
        packet.url = getGlobalPathRoot() + "eland/ct/ct002/ct002-runScheme.gv";
        packet.data.add("prjCd", getPrjCd());
        // 其他查询条件
        packet.data.add("conditionName", $("input[name=conditionName]", formObj).val());
        packet.data.add("condition", $("input[name=condition]", formObj).val());
        packet.data.add("conditionValue", $("input[name=conditionValue]", formObj).val());
        packet.data.add("rhtOrgId", $("input[name=rhtOrgId]", formObj).val());
        packet.data.add("rhtRegId", $("input[name=rhtRegId]", formObj).val());
        packet.data.add("regUseType", $("input[name=regUseType]", formObj).val());
        packet.data.add("rhtType", $("input[name=rhtType]", formObj).val());
        // 计算的方案
        packet.data.add("schemeId", schemeId);
        packet.data.add("useCondition", $("input[name=useCondition]:checked", $.pdialog.getCurrent()).val());
        packet.data.add("batchName", $("input[name=batchName]", $.pdialog.getCurrent()).val());
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            if (jsonData.isSuccess) {
                $.pdialog.closeCurrent();
                alertMsg.correct("已提交后台计算");
            } else {
                alertMsg.error(jsonData.errMsg);
            }
        }, true);
    },

    /**
     * 弹出标签管理界面
     * @param obj 点击的dom元素
     * @param signType  1 单户 2 批量
     */
    sign: function () {
        var houseIds = [];
        navTab.getCurrentPanel().find(":checkbox[checked][name=hsId]").each(
            function (i) {
                houseIds.push($(this).val());
            }
        );

        if (houseIds.length == 0) {
            alertMsg.warn("请选择要标记的房产");
        } else {
            var url = getGlobalPathRoot() + "eland/ph/ph016/ph016-init.gv?prjCd=" + getPrjCd()
                + "&signType=2"
                + "&hsId=" + houseIds.join(",");
            $.pdialog.open(url, "data-label", "标签编辑", {mask: true, width: 800, height: 600});
        }
    }
};

$(document).ready(function () {
    // 拖动屏幕切分效果
    LeftMenu.init("ph001DivTree", "ph001001", {cookieName: "showJmTree"});

    /**
     * 快速查询绑定回车事件
     */
    $("#ph001QSchDialog input", navTab.getCurrentPanel()).bind("keydown", function () {
        var $this = $(this);
        if (event.keyCode == 13) {
            if ($this.val() != "") {
                $("#ph001QSchDialog button.js_query", navTab.getCurrentPanel()).trigger("click");
            } else {
                event.keyCode = 9;
            }
        }
    });
    // 区域树导航模式
    var showJmTreeModel = $("input[name=showJmTreeModel]", navTab.getCurrentPanel()).val();
    if ("reg" == showJmTreeModel) {
        ph001.initFullRegTree();
    } else {
        //初始化左侧区域树
        ph001.switchTree($("span.js_reload", navTab.getCurrentPanel()));
    }
});
