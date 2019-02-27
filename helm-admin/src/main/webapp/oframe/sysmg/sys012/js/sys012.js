var sys012 = {
    uploadObj: null,

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
        if (sys012.getRemoveAble() && $("#removeBtn_" + treeNode.tId, divArea).length == 0) {
            var addStr = "<span class='button marr5 remove' id='removeBtn_" + treeNode.tId + "' title='删除区域' onfocus='this.blur();'/>";
            sObj.after(addStr);
            var btn = $("#removeBtn_" + treeNode.tId);
            if (btn) btn.bind("click", function () {
                if (isBuld) {
                    sys012.deleteBuild(treeNode.buldId);
                } else {
                    alertMsg.confirm("您确定要删除该节点及其下的所有子节点吗?", {
                        okCall: function () {
                            sys012.removeReg("sys012Tree", treeNode);
                        }
                    });
                }
                return false;
            });
        }
        if (sys012.getEditAble() && $("#eidtRegBtn_" + treeNode.tId, divArea).length == 0) {
            var addStr = "<span class='button marr5 edit' id='eidtRegBtn_" + treeNode.tId + "' title='修改区域' onfocus='this.blur();'/>";
            sObj.after(addStr);
            var btn = $("#eidtRegBtn_" + treeNode.tId);
            if (btn) btn.bind("click", function () {
                sys012.editReg(treeNode.id);
                return false;
            });
        }
        if (!isBuld && sys012.getEditAble() && $("#addRegBtn_" + treeNode.tId, divArea).length == 0) {
            var addStr = "<span class='button marr5 add' id='addRegBtn_" + treeNode.tId + "' title='新增区域' onfocus='this.blur();'/>";
            sObj.after(addStr);
            var btn = $("#addRegBtn_" + treeNode.tId);
            if (btn) btn.bind("click", function () {
                sys012.addReg(treeNode.id);
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
        packet.url = getGlobalPathRoot() + "eland/ph/sys012/sys012-moveRegion.gv";
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

    //  ########################## 初始化左侧实体树 ############################
    initFullRegTree: function () {
        // 重新加载
        var packet = new AJAXPacket();
        var prjCd = getPrjCd();
        packet.data.add("prjCd", prjCd);
        packet.data.add("treeType", "1");
        packet.data.add("objValue", "");
        packet.url = getGlobalPathRoot() + "oframe/sysmg/sys012/sys012-treeInfo.gv";
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
                            onClick: sys012.clickNode,
                            beforeDrop: sys012.beforeDrop,
                            beforeRemove: sys012.beforeRemove
                        },
                        view: {
                            addHoverDom: false,
                            removeHoverDom: false,
                            selectedMulti: false
                        },
                        edit: {
                            drag: {
                                isCopy: false,
                                isMove: sys012.getMoveAble()
                            },
                            enable: false,
                            showRemoveBtn: false,
                            showRenameBtn: false
                        }
                    };
                    // 初始化目录树
                    var zTree = $.fn.zTree.init($("#sys012Tree", navTab.getCurrentPanel()), pjOrgTreeSetting, treeJson);
                }
                else {
                    alertMsg.warn(jsonData.errMsg);
                }
            }, true, false
        );
    },

    /**
     * 左侧区域树点击事件
     */
    clickNode: function (event, treeId, treeNode) {
        var isBuld = false;
        // 点击的是实体
        if (treeNode.nType == '1') {
            // 显示实体
            sys012.initEntity(treeNode.enName, treeNode.id, treeNode.currPrjCd);
            // 展开目录树
            var childrens = treeNode.children;
            if (!childrens || childrens.length == 0) {
                // 加载业务模型的实体属性
                var packet = new AJAXPacket();
                packet.data.add("prjCd", treeNode.currPrjCd);
                packet.data.add("enName", treeNode.enName);
                packet.data.add("nodeId", treeNode.id);
                packet.url = getGlobalPathRoot() + "oframe/sysmg/sys012/sys012-entityTree.gv";
                core.ajax.sendPacketHtml(packet, function (response) {
                    var jsonData = eval("(" + response + ")");
                    var treeJson = jsonData.resultMap.treeJson;
                    var zTree = $.fn.zTree.getZTreeObj(treeId);
                    zTree.addNodes(treeNode, treeJson, false);
                }, false);
            }
        } else if (treeNode.nType == '2') {
            // 点击分组
            sys012.initGroup(treeNode.entityName, treeNode.groupId, treeNode.id, treeNode.pId, treeNode.currPrjCd);
        } else if (treeNode.nType == '3') {
            // 点击属性
            sys012.initAttr(treeNode.entityName, treeNode.groupId, treeNode.attrName, treeNode.id, treeNode.currPrjCd);
        }
    },

    /**
     * 初始化实体信息展示界面
     * @param enName 实体名称
     */
    initEntity: function (enName, id, currPrjCd) {
        // 重新加载
        var packet = new AJAXPacket();
        packet.data.add("currPrjCd", currPrjCd);
        packet.data.add("enName", enName);
        packet.data.add("id", id);
        packet.url = getGlobalPathRoot() + "oframe/sysmg/sys012/sys012-initE.gv?prjCd=" + getPrjCd();
        core.ajax.sendPacketHtml(packet, function (response) {
            $("#sys012Context", navTab.getCurrentPanel()).html(response);
            initUI($("#sys012Context", navTab.getCurrentPanel()));
        });
    },
    /**
     * 初始化实体信息展示界面
     * @param enName 实体名称
     */
    initGroup: function (entityName, groupId, id, pId, currPrjCd) {
        // 重新加载
        var packet = new AJAXPacket();
        packet.data.add("prjCd", currPrjCd);
        packet.data.add("entityName", entityName);
        packet.data.add("groupId", groupId);
        packet.data.add("id", id);
        packet.data.add("pId", pId);
        packet.url = getGlobalPathRoot() + "oframe/sysmg/sys012/sys012-initG.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            $("#sys012Context", navTab.getCurrentPanel()).html(response);
            initUI($("#sys012Context", navTab.getCurrentPanel()));
        });
    },
    /**
     * 初始化实体信息展示界面
     * @param entityName 实体名称
     * @param groupId 组编号
     * @param attrName 属相名称
     */
    initAttr: function (entityName, groupId, attrName, id, currPrjCd) {
        // 重新加载
        var packet = new AJAXPacket();
        packet.data.add("prjCd", currPrjCd);
        packet.data.add("entityName", entityName);
        packet.data.add("groupId", groupId);
        packet.data.add("attrName", attrName);
        packet.data.add("id", id);
        packet.url = getGlobalPathRoot() + "oframe/sysmg/sys012/sys012-initA.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            $("#sys012Context", navTab.getCurrentPanel()).html(response);
            initUI($("#sys012Context", navTab.getCurrentPanel()));
        });
    },

    /**
     * 导出实体
     */
    exportEntity: function (usePrjCd, obj) {
        var container = $(obj, navTab.getCurrentPanel()).closest("form");
        var entityName = $("input[name=entityName]", container).val();
        var url = getGlobalPathRoot() + "oframe/sysmg/sys012/sys012-export.gv?prjCd=" + getPrjCd()
            + "&entityName=" + entityName + "&usePrjCd=" + usePrjCd;
        window.open(url)
    },

    /**
     * 添加窗口
     */
    addView: function (view, entityName, pId, usePrjCd) {
        var packet = new AJAXPacket();
        packet.data.add("view", view);
        packet.data.add("entityName", entityName);
        packet.data.add("pId", pId);
        packet.data.add("prjCd", getPrjCd());
        packet.data.add("usePrjCd", usePrjCd);
        packet.data.add("prjCd", getPrjCd());
        packet.url = getGlobalPathRoot() + "oframe/sysmg/sys012/sys012-initAdd.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            $("#sys012Context", navTab.getCurrentPanel()).html(response);
            initUI($("#sys012Context", navTab.getCurrentPanel()));
        });
    },
    /**
     * 添加实体
     */
    addEntity: function (formId) {
        var $form = $("#" + formId);
        if ($form.valid()) {
            var packet = new AJAXPacket();
            packet.data.data = $form.serializeArray();
            packet.url = getGlobalPathRoot() + "oframe/sysmg/sys012/sys012-saveEntity.gv?prjCd=" + getPrjCd();
            core.ajax.sendPacketHtml(packet, function (response) {
                var jsonData = eval("(" + response + ")");
                var nodeInfo = jsonData.treeJson;
                var zTree = $.fn.zTree.getZTreeObj("sys012Tree");
                var pNode = zTree.getNodeByParam("id", nodeInfo.pId, null);
                zTree.addNodes(pNode, nodeInfo);
                alertMsg.correct("处理成功");
            });
        }
    },
    /**
     * 添加分组
     * @param formId
     */
    addGroup: function (formId, pId) {
        var $form = $("#" + formId);
        if ($form.valid()) {
            var packet = new AJAXPacket();
            packet.data.data = $form.serializeArray();
            packet.url = getGlobalPathRoot() + "oframe/sysmg/sys012/sys012-saveGroup.gv?prjCd=" + getPrjCd() + "&pId=" + pId;
            core.ajax.sendPacketHtml(packet, function (response) {
                var jsonData = eval("(" + response + ")");
                var nodeInfo = jsonData.treeJson;
                var zTree = $.fn.zTree.getZTreeObj("sys012Tree");
                var pNode = zTree.getNodeByParam("id", nodeInfo.pId, null);
                zTree.addNodes(pNode, nodeInfo);
                alertMsg.correct("处理成功");
            });
        }
    },
    /**
     * 添加属性
     * @param formId
     */
    addAttr: function (formId) {
        var $form = $("#" + formId);
        if ($form.valid()) {
            var packet = new AJAXPacket();
            packet.data.data = $form.serializeArray();
            packet.url = getGlobalPathRoot() + "oframe/sysmg/sys012/sys012-saveAttr.gv?prjCd=" + getPrjCd();
            core.ajax.sendPacketHtml(packet, function (response) {
                var jsonData = eval("(" + response + ")");
                var nodeInfo = jsonData.treeJson;
                var zTree = $.fn.zTree.getZTreeObj("sys012Tree");
                var pNode = zTree.getNodeByParam("id", nodeInfo.pId, null);
                zTree.addNodes(pNode, nodeInfo);
                alertMsg.correct("处理成功");

            });
        }
    },

    /**
     * 修改实体
     * @param id
     */
    updateEntity: function (id, obj) {
        var container = $(obj, navTab.getCurrentPanel()).closest("form");
        var entityName = $("input[name=entityName]", container).val();
        var entityDesc = $("input[name=entityDesc]", container).val();
        var primaryField = $("input[name=primaryField]", container).val();
        var primarySqlName = $("input[name=primarySqlName]", container).val();
        var dataViewUrl = $("input[name=dataViewUrl]", container).val();
        var oldEntityName = $("input[name=oldEntityName]", container).val();
        var usePrjCd = $("input[name=usePrjCd]", container).val();
        var url = getGlobalPathRoot() + "oframe/sysmg/sys012/sys012-saveEntity.gv?prjCd=" + getPrjCd();
        var packet = new AJAXPacket(url);
        packet.data.add("entityName", entityName);
        packet.data.add("entityDesc", entityDesc);
        packet.data.add("primaryField", primaryField);
        packet.data.add("primarySqlName", primarySqlName);
        packet.data.add("dataViewUrl", dataViewUrl);
        packet.data.add("oldEntityName", oldEntityName);
        packet.data.add("usePrjCd", usePrjCd);
        packet.data.add("id", id);
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            // 更新节点信息
            var nodeInfo = jsonData.treeJson;
            var zTree = $.fn.zTree.getZTreeObj("sys012Tree");
            var uNode = zTree.getNodeByParam("id", nodeInfo.id, null);
            // 合并修改信息
            uNode = $.extend(uNode, uNode, nodeInfo);
            zTree.updateNode(uNode);
            alertMsg.correct("处理成功");
        });
    },
    /**
     * 修改分组
     * @param formId
     */
    updateGroup: function (id, pId, obj) {
        var container = $(obj, navTab.getCurrentPanel()).closest("form");
        var url = getGlobalPathRoot() + "oframe/sysmg/sys012/sys012-saveGroup.gv?prjCd=" + getPrjCd();
        var packet = new AJAXPacket(url);
        var entityGroupId = $("input[name=entityGroupId]", container).val();
        var entityName = $("input[name=entityName]", container).val();
        var entityGroupNameCh = $("input[name=entityGroupNameCh]", container).val();
        var entityGroupNameEn = $("input[name=entityGroupNameEn]", container).val();
        var entityGroupDesc = $("input[name=entityGroupDesc]", container).val();
        var usePrjCd = $("input[name=usePrjCd]", container).val();

        packet.data.add("entityGroupId", entityGroupId);
        packet.data.add("entityName", entityName);
        packet.data.add("entityGroupNameCh", entityGroupNameCh);
        packet.data.add("entityGroupNameEn", entityGroupNameEn);
        packet.data.add("entityGroupDesc", entityGroupDesc);
        packet.data.add("usePrjCd", usePrjCd);
        packet.data.add("id", id);
        packet.data.add("pId", pId);
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            if (jsonData.success) {
                // 更新节点信息
                var nodeInfo = jsonData.treeJson;
                var zTree = $.fn.zTree.getZTreeObj("sys012Tree");
                var uNode = zTree.getNodeByParam("id", nodeInfo.id, null);
                // 合并修改信息
                uNode = $.extend(uNode, uNode, nodeInfo);
                zTree.updateNode(uNode);
                alertMsg.correct("处理成功");
            } else {
                alertMsg.warn(jsonData.errMsg);
            }
        });
    },
    /**
     * 修改属性
     * @param formId
     */
    updateAttr: function (updateFlag, obj) {
        var container = $(obj, navTab.getCurrentPanel()).closest("form");
        var url = getGlobalPathRoot() + "oframe/sysmg/sys012/sys012-saveAttr.gv?prjCd=" + getPrjCd();
        var packet = new AJAXPacket(url);
        var id = $("input[name=id]", container).val();
        var entityAttrDesc = $("textarea[name=entityAttrDesc]", container).val();
        var oldEntityAttrNameEn = $("input[name=oldEntityAttrNameEn]", container).val();
        var entityAttrNameEn = $("input[name=entityAttrNameEn]", container).val();
        if (1 == updateFlag) {
            // 修改属性
        } else {
            // 复制属性
            if (oldEntityAttrNameEn == entityAttrNameEn) {
                alertMsg.error("复制属性必须修改属性的[英文名称]");
                return;
            } else {
                oldEntityAttrNameEn = "";
            }
        }
        var oldEntityGroupId = $("input[name=oldEntityGroupId]", container).val();
        var entityGroupId = $("select[name=entityGroupId]", container).val();

        var entityAttrNameCh = $("input[name=entityAttrNameCh]", container).val();
        var entitySaveType = $("input[name=entitySaveType]", container).val();
        var entitySaveTable = $("input[name=entitySaveTable]", container).val();
        var entitySaveField = $("input[name=entitySaveField]", container).val();
        var attrCanVariable = $("select[name=attrCanVariable]", container).val();
        var mutiAttrFlag = $("select[name=mutiAttrFlag]", container).val();
        var attrRuleDesc = $("input[name=attrRuleDesc]", container).val();
        var attrRuleCss = $("input[name=attrRuleCss]", container).val();
        var attrDefaultValue = $("input[name=attrDefaultValue]", container).val();
        var attrFrontCheckRule = $("input[name=attrFrontCheckRule]", container).val();
        var attrBackCheckRule = $("input[name=attrBackCheckRule]", container).val();
        var attrValueMonitor = $("input[name=attrValueMonitor]:checked", container).val();
        var isSupportFunc = $("input[name=isSupportFunc]:checked", container).val();
        var attrModifyRule = $("input[name=attrModifyRule]", container).val();
        var attrRelEntiyField = $("input[name=attrRelEntiyField]", container).val();
        var attrRelEntity = $("input[name=attrRelEntity]", container).val();
        var attrValueNameFunc = $("input[name=attrValueNameFunc]", container).val();
        var deleteCaseadeFlag = $("input[name=deleteCaseadeFlag]", container).val();
        var canConditionFlag = $("input[name=canConditionFlag]:checked", container).val();
        var canOrderFlag = $("input[name=canOrderFlag]:checked", container).val();
        var canResultFlag = $("input[name=canResultFlag]:checked", container).val();
        var attrFrontSltRule = $("input[name=attrFrontSltRule]", container).val();
        var entityName = $("input[name=entityName]", container).val();
        var usePrjCd = $("input[name=usePrjCd]", container).val();

        packet.data.add("id", id);
        packet.data.add("entityName", entityName);
        packet.data.add("entityAttrDesc", entityAttrDesc);
        packet.data.add("oldEntityAttrNameEn", oldEntityAttrNameEn);
        packet.data.add("oldEntityGroupId", oldEntityGroupId);
        packet.data.add("entityGroupId", entityGroupId);
        packet.data.add("entityAttrNameEn", entityAttrNameEn);
        packet.data.add("entityAttrNameCh", entityAttrNameCh);
        packet.data.add("entitySaveType", entitySaveType);
        packet.data.add("entitySaveTable", entitySaveTable);
        packet.data.add("entitySaveField", entitySaveField);
        packet.data.add("mutiAttrFlag", mutiAttrFlag);
        packet.data.add("attrCanVariable", attrCanVariable);
        packet.data.add("attrRuleCss", attrRuleCss);
        packet.data.add("attrRuleDesc", attrRuleDesc);
        packet.data.add("attrDefaultValue", attrDefaultValue);
        packet.data.add("attrFrontCheckRule", attrFrontCheckRule);
        packet.data.add("attrBackCheckRule", attrBackCheckRule);
        packet.data.add("attrValueMonitor", attrValueMonitor);
        packet.data.add("isSupportFunc", isSupportFunc);
        packet.data.add("attrModifyRule", attrModifyRule);
        packet.data.add("attrRelEntiyField", attrRelEntiyField);
        packet.data.add("attrRelEntity", attrRelEntity);
        packet.data.add("attrValueNameFunc", attrValueNameFunc);
        packet.data.add("deleteCaseadeFlag", deleteCaseadeFlag);
        packet.data.add("canConditionFlag", canConditionFlag);
        packet.data.add("canOrderFlag", canOrderFlag);
        packet.data.add("canResultFlag", canResultFlag);
        packet.data.add("attrFrontSltRule", attrFrontSltRule);
        packet.data.add("usePrjCd", usePrjCd);

        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            if (jsonData.success) {
                // 更新节点信息
                var nodeInfo = jsonData.treeJson;
                var zTree = $.fn.zTree.getZTreeObj("sys012Tree");
                var uNode = zTree.getNodeByParam("id", nodeInfo.id, null);
                // 合并修改信息
                uNode = $.extend(uNode, uNode, nodeInfo);
                zTree.updateNode(uNode);
                //zTree.removeNode(uNode);
                alertMsg.correct("处理成功");
            } else {
                alertMsg.warn(jsonData.errMsg);
            }
        });
    },

    /**
     * 快速查询
     */
    quickSch: function () {
        var sys012DivTree = $("#sys012DivTree", navTab.getCurrentPanel());
        var conditionNameArr = [];
        var conditionArr = [];
        var conditionValueArr = [];
        var schType = $("select[name=schType]", sys012DivTree).val();
        var schValue = $("input[name=schValue]", sys012DivTree).val();
        conditionNameArr.push(schType);
        conditionArr.push("like");
        conditionValueArr.push(schValue);
        var sys012QueryForm = $("#sys012QueryForm", navTab.getCurrentPanel());
        $("input[name=conditionName]", sys012QueryForm).val(conditionNameArr.join(","));
        $("input[name=condition]", sys012QueryForm).val(conditionArr.join(","));
        $("input[name=conditionValue]", sys012QueryForm).val(conditionValueArr.join(","));

        // 查询搜索
        var url = getGlobalPathRoot() + "oframe/common/page/page-data.gv";
        var packet = new AJAXPacket(url);
        packet.data.data = sys012QueryForm.serializeArray();
        packet.data.data.push({"name": "currentPage", "value": 1});
        packet.data.data.push({"name": "pageSize", "value": 20});
        packet.data.data.push({"name": "prjCd", "value": "0"});
        // 判断是点击查询还是翻页
        core.ajax.sendPacketHtml(packet, function (response) {
            var resultDiv = sys012QueryForm.next("div.js_page");
            if (resultDiv.length == 0) {
                resultDiv = $("<div class='js_page'></div>");
                sys012QueryForm.after(resultDiv);
            }
            var divOver = resultDiv.css("overflow");
            resultDiv.css("overflow", "hidden");
            resultDiv.html(response);
            resultDiv.initUI();
            resultDiv.css("overflow", divOver);
        }, true);
        packet = null;
    },

    /**
     * 关闭快速检索结果
     */
    closeQuickRslt: function () {
        $("#sys012AttrList>div", navTab.getCurrentPanel()).hide();
    },

    /**
     * 选择结果查询
     */
    quickRsltClick: function (entityName, entityGroupId, entityAttrNameEn, prjCd) {
        sys012.closeQuickRslt();

        var zTree = $.fn.zTree.getZTreeObj("sys012Tree");
        var pNode = zTree.getNodeByParam("id", "p_" + prjCd, null);
        var eNode = zTree.getNodeByParam("enName", entityName, pNode);
        if (eNode) {
            zTree.selectNode(eNode);
            sys012.clickNode(null, 'sys012Tree', eNode);
            var gNode = zTree.getNodeByParam("id", "g" + entityGroupId, eNode);
            if (gNode) {
                zTree.selectNode(gNode);
                sys012.clickNode(null, 'sys012Tree', eNode);
                var attrNode = zTree.getNodeByParam("attrName", entityAttrNameEn, gNode);
                if (attrNode) {
                    zTree.selectNode(attrNode);
                    sys012.clickNode(null, 'sys012Tree', attrNode);
                } else {
                    zTree.selectNode(gNode);
                }

            }
        }
    },

    /**
     * 删除实体信息
     * @param deleteObj 删除对象
     */
    deleteInfo: function (deleteObj) {
        var usePrjCd = $("input[name=usePrjCd]", navTab.getCurrentPanel()).val();
        var entityName = $("input[name=oldEntityName]", navTab.getCurrentPanel()).val();
        var entityDefId = $("input[name=oldEntityDefId]", navTab.getCurrentPanel()).val();
        var entityGroupId = $("input[name=oldEntityGroupId]", navTab.getCurrentPanel()).val();
        var entityAttrNameEn = $("input[name=oldEntityAttrNameEn]", navTab.getCurrentPanel()).val();

        alertMsg.confirm("确定要删除吗?", {
                okCall: function () {
                    var url = getGlobalPathRoot() + "oframe/sysmg/sys012/sys012-deleteInfo.gv";
                    var ajaxPacket = new AJAXPacket(url);
                    ajaxPacket.data.add("prjCd", getPrjCd());
                    ajaxPacket.data.add("deleteObj", deleteObj);
                    ajaxPacket.data.add("usePrjCd", usePrjCd);
                    ajaxPacket.data.add("entityName", entityName);
                    ajaxPacket.data.add("entityDefId", entityDefId);
                    ajaxPacket.data.add("entityGroupId", entityGroupId);
                    ajaxPacket.data.add("entityAttrNameEn", entityAttrNameEn);
                    core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                        var jsonData = eval("(" + response + ")");
                        if (jsonData.success) {
                            alertMsg.correct("删除成功!");
                            var zTree = $.fn.zTree.getZTreeObj("sys012Tree");
                            var currPrjCd = $("input[name=usePrjCd]", navTab.getCurrentPanel()).val();
                            if (deleteObj == 'entity') {
                                var entityNodeId = $("input[name=nodeId]", navTab.getCurrentPanel()).val();
                                var node = zTree.getNodeByParam("id", entityNodeId, null);
                                zTree.selectNode(node.getParentNode());
                                zTree.removeNode(node);
                            } else if (deleteObj == 'group') {
                                var nodeId = $("input[name=nodeId]", navTab.getCurrentPanel()).val();
                                sys012.initEntity(entityName, nodeId, currPrjCd);
                                var eNode = zTree.getNodeByParam("id", nodeId, null);
                                zTree.selectNode(eNode.getParentNode());
                                zTree.removeNode(eNode);
                            } else if (deleteObj == 'attr') {
                                var id = $("input[name=id]", navTab.getCurrentPanel()).val();
                                sys012.initGroup(entityName, entityGroupId, id, '', currPrjCd);
                                var gNode = zTree.getNodeByParam("id", id, null);
                                zTree.selectNode(gNode.getParentNode());
                                zTree.removeNode(gNode);
                            }
                        } else {
                            alertMsg.error(jsonData.errMsg);
                        }
                    }, true);
                }
            }
        );
    },

    /**
     * 打开导入对话框
     */
    openImport: function () {
        var regUseType = $("input[name=regUseType]", navTab.getCurrentPanel()).val();
        var url = getGlobalPathRoot() + "oframe/sysmg/sys012/sys012-openImport.gv?prjCd=" + getPrjCd();
        $.pdialog.open(url, "sys01201", "导入模型",
            {mask: true, max: false, maxable: false, resizable: false, width: 600, height: 180});
    },
    /**
     * 导入配置参数
     */
    importXml: function () {
        var importXmlFile = $("#sys012ImportXmlFile", $.pdialog.getCurrent).val();
        var toPrjCd = $("input[name=toPrjCd]", $.pdialog.getCurrent).val();
        if (importXmlFile == "") {
            alertMsg.warn("请选择导入的文件");
            return;
        }
        var uploadURL = getGlobalPathRoot()
            + "oframe/sysmg/sys012/sys012-saveImportData.gv?"
            + "prjCd=" + getPrjCd()
            + "&toPrjCd=" + toPrjCd;
        // 提示加载中
        var ajaxbg = $("#background,#progressBar");
        ajaxbg.show();
        $.ajaxFileUpload({
                url: uploadURL,
                secureuri: false,
                fileElementId: "sys012ImportXmlFile",
                dataType: 'json',
                success: function (data, status, fileElementId) {
                    if (data.isSuccess) {
                        //alertMsg.correct("处理成功");
                        var res = data.resultXml;
                        if (res != "" && res != null) {
                            var packet = new AJAXPacket();
                            packet.data.add("res", res);
                            packet.data.add("prjCd", getPrjCd());
                            packet.url = getGlobalPathRoot() + "oframe/sysmg/sys012/sys012-initImportResult.gv";
                            core.ajax.sendPacketHtml(packet, function (response) {
                                $("#sys012Context", navTab.getCurrentPanel()).html(response);
                                initUI($("#sys012Context", navTab.getCurrentPanel()));
                            });
                        }
                        $.pdialog.closeCurrent();
                    } else {
                        alertMsg.error(data.errMsg);
                    }
                    var ajaxbg = $("#background,#progressBar");
                    ajaxbg.hide();
                }

            }
        )
    },

    /**
     * 显示或隐藏目录
     **/
    extendOrClosePanel: function (clickObj) {
        var $pucker = $(clickObj);
        var $content = $pucker.closest("div.panelHeader").next("div.panelContent");
        if ($pucker.hasClass("collapsable")) {
            $pucker.removeClass("collapsable").addClass("expandable");
            $content.hide();
            sys012.adjustHeight();
        } else {
            $pucker.removeClass("expandable").addClass("collapsable");
            $content.show();
            sys012.adjustHeight();
        }
    },
    /**
     * 重新计算高度
     */
    adjustHeight: function () {
        var sys012DivTreeH = $("#sys012DivTree", navTab.getCurrentPanel()).height();
        var sys012ConditionH = $("#sys012Summary", navTab.getCurrentPanel()).height();
        var height = sys012DivTreeH - sys012ConditionH - 122;
        $("#sys012EntityList", navTab.getCurrentPanel()).find("div.gridScroller").css("height", height + "px");
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
                var leftDiv = $("#sys012DivTree", navTab.getCurrentPanel());
                var rightDiv = $("#sys012Context", navTab.getCurrentPanel());
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
            var rightDiv = $("#sys012Context", navTab.getCurrentPanel());
            rightDiv.css("margin-left", "10px");
            var resizeGrid = $("div.j-resizeGrid", rightDiv);
            if (resizeGrid.length > 0) {
                resizeGrid.jResize();
            }
        } else {
            leftMenu.show();
            splitLine.removeClass("menu_hide");
            var rightDiv = $("#sys012Context", navTab.getCurrentPanel());
            rightDiv.css("margin-left", leftMenu.width() + 10 + "px");
            var resizeGrid = $("div.j-resizeGrid", rightDiv);
            if (resizeGrid.length > 0) {
                resizeGrid.jResize();
            }
        }
    });

    //初始化左侧区域树
    sys012.initFullRegTree();
});
