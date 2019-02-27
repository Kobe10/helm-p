sys007 = {

    /**
     * 切换角色 与 员工树
     * @param obj
     */
    switchTree: function (flag) {
        var roleTreeContain = $("#roleTreeContext", navTab.getCurrentPanel());
        var sys007StaffTreeContain = $("#sys007StaffTree", navTab.getCurrentPanel());
        if (1 === flag) {
            roleTreeContain.show();
            sys007StaffTreeContain.hide();
            if (roleTreeContain.is(":empty")) {
                sys007.initFullRoleTree();
            }
        } else {
            roleTreeContain.hide();
            sys007StaffTreeContain.show();
            if (sys007StaffTreeContain.is(":empty")) {
                sys007.initFullStaffTree();
            }
        }
    },

    /**
     * 折叠, 展开树
     * @param obj
     */
    extendOrClose: function (obj) {
        var $span = $(obj);
        var zTree = null;
        if ($("#roleTreeContext", navTab.getCurrentPanel()).is(":hidden")) {
            zTree = $.fn.zTree.getZTreeObj("sys007StaffTree");
        } else {
            zTree = $.fn.zTree.getZTreeObj("roleTreeContext");
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
     * 初始化 系统员工树
     */
    initFullStaffTree: function () {
        //// 清空界面
        $("#sys007RightDiv", navTab.getCurrentPanel()).html("");
        // 重新加载
        var packet = new AJAXPacket();
        var prjCd = $("input[name=prjCd]", navTab.getCurrentPanel()).val();
        packet.data.add("prjCd", "0");
        packet.url = getGlobalPathRoot() + "oframe/sysmg/sys004/sys004-staffTree.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            var treeJson = jsonData.resultMap.treeJson;
            // 服务调用是否成功
            var isSuccess = jsonData.success;
            if (isSuccess) {
                // 左侧树的生成和回调
                var prjStaffTreeSetting = {
                    data: {
                        simpleData: {
                            enable: true
                        }
                    },
                    callback: {
                        onClick: sys007.clickStaff
                    },
                    check: {
                        autoCheckTrigger: false,
                        enable: false
                    }
                };
                // 初始化目录树
                var zTree = $.fn.zTree.init($("#sys007StaffTree", navTab.getCurrentPanel()), prjStaffTreeSetting, treeJson);
                // 显示根节点数据
                var rootNode = zTree.getNodesByFilter(function (node) {
                    return node.iconSkin == "user";
                }, true);
                if (rootNode != null) {
                    zTree.selectNode(rootNode);
                    sys007.clickStaff(null, "sys007StaffTree", rootNode);
                }
            } else {
                alertMsg.warn(jsonData.errMsg);
            }
        }, true, false)
    },

    //点击某个员工查询员工所有的 角色
    clickStaff: function (event, treeId, treeNode) {
        if (treeNode.staffId) {
            $("#sys007RightDiv", navTab.getCurrentPanel()).html("");
            // value为用户选中的关键词
            var packet = new AJAXPacket();
            // 当前节点信息
            packet.data.add("staffId", treeNode.staffId);
            // 提交界面展示
            packet.url = getGlobalPathRoot() + "oframe/sysmg/sys002/sys002-staffTreeInfo.gv?prjCd=" + getPrjCd();
            core.ajax.sendPacketHtml(packet, function (response) {
                $("#sys007RightDiv", navTab.getCurrentPanel()).html(response);
                initUI($("#sys007RightDiv", navTab.getCurrentPanel()));
            }, true);
        }
    },

    /**
     * 初始化系统角色树
     */
    initFullRoleTree: function () {
        // 清空界面
        $("#sys007RightDiv", navTab.getCurrentPanel()).html("");
        // 重新加载
        var packet = new AJAXPacket();
        packet.url = getGlobalPathRoot() + "oframe/sysmg/sys004/sys004-orgRoleTreeInfo.gv?prjCd=" + getPrjCd();
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
                    callback: {
                        onClick: sys007.viewNode
                    }
                };
                // 初始化目录树
                var zTree = $.fn.zTree.init($("#roleTreeContext", navTab.getCurrentPanel()), cmpOrgTreeSetting, treeJson);
                // 显示根节点数据
                var rootNode = zTree.getNodesByFilter(function (node) {
                    return node.type == "role";
                }, true);
                if (rootNode != null) {
                    zTree.selectNode(rootNode);
                    sys007.viewNode(null, "ph001Tree", rootNode);
                }
            } else {
                alertMsg.warn(jsonData.errMsg);
            }
        }, true, false);
    },

    /**
     * 点击查看目录节点
     * @param event js event对象
     * @param treeId  对应 zTree 的 treeId
     * @param treeNode  被点击的节点 JSON 数据对象
     * @param method 对节点的操作
     */
    viewNode: function (event, treeId, treeNode) {
        if (treeNode.type == "role") {
            sys007.showNode(treeNode.id, "edit");
        }
    },
    showNode: function (cNodeId, method) {
        $("#sys007RightDiv", navTab.getCurrentPanel()).html("");
        // value为用户选中的关键词
        var packet = new AJAXPacket();
        // 目录树类型
        packet.data.add("method", method);
        // 当前节点信息
        packet.data.add("cNodeId", cNodeId);
        // 提交界面展示
        packet.url = getGlobalPathRoot() + "oframe/sysmg/sys007/sys007-treeNode.gv?prjCd=" + getPrjCd();
        core.ajax.sendPacketHtml(packet, function (response) {
            $("#sys007RightDiv", navTab.getCurrentPanel()).html(response);
            initUI($("#sys007RightDiv", navTab.getCurrentPanel()));
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
        // 提交界面展示
        packet.url = getGlobalPathRoot() + "oframe/sysmg/sys007/sys007-saveNode.gv?prjCd=" + getPrjCd();
        core.ajax.sendPacketHtml(packet, function (response) {
            var data = eval("(" + response + ")");
            var isSuccess = data.success;
            if (isSuccess) {
                var method = data.method;
                var nodeInfo = data.nodeInfo;
                if ("add" == method) {
                    var zTree = $.fn.zTree.getZTreeObj("treeContext");
                    var pNode = zTree.getNodeByParam("id", nodeInfo.pId, null);
                    zTree.addNodes(pNode, nodeInfo);
                } else {
                    // 更新节点信息
                    var zTree = $.fn.zTree.getZTreeObj("treeContext");
                    var uNode = zTree.getNodeByParam("id", nodeInfo.id, null);
                    // 合并修改信息
                    uNode = $.extend(uNode, uNode, nodeInfo);
                    zTree.updateNode(uNode);
                }
                // 保存成功
                alertMsg.correct("处理成功");
                //
                sys007.showNode(nodeInfo.id, "edit");
            } else {
                alertMsg.error(data.errMsg);
            }
        }, true);
    },
    queryRht: function () {
        var roleCd = $("input[name=roleCd]", navTab.getCurrentPanel()).val();
        if (roleCd == "") {
            return;
        }
        var jsonData = $("#sys00701Rhtfrm", navTab.getCurrentPanel()).serializeArray();
        jsonData.push({
            name: 'currentPage',
            value: '1'
        });
        jsonData.push({
            name: 'roleCd',
            value: roleCd
        });
        Query.pubQuery(jsonData, 'sys00701_rht_list_print');
    },
    editRht: function (obj) {
        var url = getGlobalPathRoot() + "oframe/sysmg/sys007/sys007-initRht.gv?prjCd=" + getPrjCd();
        $(obj).openTip({href: url, height: "300", appendBody: true, width: "250", offsetX: 0, offsetY: 0});
    },

    loadRoleStaff: function (obj) {
        var $this = $(obj);
        var cIdx = $this.prevAll("li").length;
        var textContainer = $("#sys007NavContext", navTab.getCurrentPanel()).find(">div").eq(cIdx);
        if (textContainer.find("ul.ztree").html() == "") {
            // 重新加载
            var packet = new AJAXPacket();
            var prjCd = $("input[name=prjCd]", navTab.getCurrentPanel()).val();
            packet.data.add("prjCd", "0");
            packet.url = getGlobalPathRoot() + "oframe/sysmg/sys004/sys004-staffTree.gv";
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
                                onCheck: sys007.checkStaff
                            },
                            check: {
                                autoCheckTrigger: false,
                                chkStyle: "checkbox",
                                chkboxType: {"Y": "", "N": ""},
                                enable: true
                            }
                        };
                        // 初始化目录树
                        var zTree = $.fn.zTree.init($("#sys007RoleStaffSlt"), pjOrgTreeSetting, treeJson);
                        $("#sys007RoleStaffList>li", navTab.getCurrentPanel()).each(function () {
                            var $this = $(this);
                            var staffId = $this.attr("staffId");
                            if (staffId != "") {
                                var staffNode = zTree.getNodeByParam("id", "staff_" + staffId, null);
                                if (staffNode) {
                                    zTree.checkNode(staffNode, true, true);
                                }
                            }
                        });
                    } else {
                        alertMsg.warn(jsonData.errMsg);
                    }
                }, true, false
            )
        }
    },

    /**
     * 取消或选中
     * @param event
     * @param treeId
     * @param treeNode
     */
    checkStaff: function (event, treeId, treeNode) {
        if (treeNode.checked) {
            var pj002RoleStaffDiv = $("#sys007RoleStaffList", navTab.getCurrentPanel());
            var newItem = pj002RoleStaffDiv.find("li:eq(0)").clone();
            newItem.attr("staffId", treeNode.staffId);
            newItem.find("label").html(treeNode.staffCd + "(" + treeNode.name + ")");
            newItem.removeClass("hidden");
            pj002RoleStaffDiv.append(newItem);
        } else {
            $("#sys007RoleStaffList>li", navTab.getCurrentPanel()).each(function () {
                var $this = $(this);
                var staffId = $this.attr("staffId");
                if (staffId == treeNode.staffId) {
                    $this.remove();
                    return false;
                }
            });
        }
    },

    dltRoleStaff: function (obj) {
        var liObj = $(obj).closest("li");
        var staffId = liObj.attr("staffId");
        var staffTree = $.fn.zTree.getZTreeObj("sys007RoleStaffSlt");
        var staffNode = staffTree.getNodeByParam("id", "staff_" + staffId, null);
        if (staffNode) {
            staffTree.checkNode(staffNode, false, false);
        }
        liObj.remove();
    },

    /**
     * 加载普通功能权限
     * @param obj
     * @param rhtType
     * @param rhtUseType
     */
    loadSFunc: function (obj, grantType) {
        var $this = $(obj);
        var cIdx = $this.prevAll("li").length;
        var textContainer = $("#sys007NavContext", navTab.getCurrentPanel()).find(">div").eq(cIdx);
        if (textContainer.find("ul.ztree").html() == "") {
            // 重新加载
            var packet = new AJAXPacket();
            // 角色编码
            var roleCd = $("input[name=roleCd]", navTab.getCurrentPanel()).val();
            if (roleCd == "") {
                return;
            }
            packet.url = getGlobalPathRoot() + "oframe/sysmg/sys007/sys007-rhtInfo.gv?prjCd=" + getPrjCd();
            packet.data.add("rhtType", "1");
            packet.data.add("rhtUseType", "0");
            packet.data.add("grantType", grantType);
            packet.data.add("roleCd", roleCd);
            core.ajax.sendPacketHtml(packet, function (response) {
                    var jsonData = eval("(" + response + ")");
                    var treeJson = jsonData.resultMap.treeJson;
                    // 服务调用是否成功
                    var isSuccess = jsonData.success;
                    if (isSuccess) {
                        // 目录树数据
                        // 左侧树的生成和回调
                        var rhtTreeSetting = {
                            data: {
                                simpleData: {
                                    enable: true
                                }
                            },
                            check: {
                                autoCheckTrigger: false,
                                enable: true,
                                chkStyle: "checkbox",
                                chkboxType: {"Y": "ps", "N": "s"}
                            }
                        };
                        // 初始化目录树
                        var zTree = $.fn.zTree.init($("#sys007RoleRhtTree_1"), rhtTreeSetting, treeJson);
                    }
                    else {
                        alertMsg.warn(jsonData.errMsg);
                    }
                }, true, false
            );
        }
    },

    /**
     * 加载普通功能权限
     * @param obj
     * @param rhtType
     * @param rhtUseType
     */
    loadSData: function (obj, grantType) {
        var $this = $(obj);
        var cIdx = $this.prevAll("li").length;
        var textContainer = $("#sys007NavContext", navTab.getCurrentPanel()).find(">div").eq(cIdx);
        if (textContainer.find("ul.ztree").html() == "") {
            // 重新加载
            var packet = new AJAXPacket();
            // 角色编码
            var roleCd = $("input[name=roleCd]", navTab.getCurrentPanel()).val();
            if (roleCd == "") {
                return;
            }
            packet.url = getGlobalPathRoot() + "oframe/sysmg/sys007/sys007-roleSData.gv?prjCd=" + getPrjCd();
            packet.data.add("rhtType", "1");
            packet.data.add("rhtUseType", "0");
            packet.data.add("grantType", grantType);
            packet.data.add("roleCd", roleCd);
            core.ajax.sendPacketHtml(packet, function (response) {
                    var jsonData = eval("(" + response + ")");
                    var treeJson = jsonData.resultMap.treeJson;
                    // 服务调用是否成功
                    var isSuccess = jsonData.success;
                    if (isSuccess) {
                        // 目录树数据
                        // 左侧树的生成和回调
                        var rhtTreeSetting = {
                            data: {
                                simpleData: {
                                    enable: true
                                }
                            },
                            callback: {
                                onCheck: sys007.checkNode
                            },
                            check: {
                                autoCheckTrigger: false,
                                enable: true,
                                chkStyle: "checkbox",
                                chkboxType: {"Y": "", "N": ""}
                            }
                        };
                        // 初始化目录树
                        var zTree = $.fn.zTree.init($("#sys007RoleRhtTree_2"), rhtTreeSetting, treeJson);
                    }
                    else {
                        alertMsg.warn(jsonData.errMsg);
                    }
                }, true, false
            );
        }
    },

    /**
     * 加载普通功能权限
     * @param obj
     * @param rhtType
     * @param rhtUseType
     */
    loadPrjFunc: function (obj, grantType) {
        var $this = $(obj);
        var cIdx = $this.prevAll("li").length;
        var textContainer = $("#sys007NavContext", navTab.getCurrentPanel()).find(">div").eq(cIdx);
        if (textContainer.find("ul.ztree").html() == "") {
            // 重新加载
            var packet = new AJAXPacket();
            // 角色编码
            var roleCd = $("input[name=roleCd]", navTab.getCurrentPanel()).val();
            if (roleCd == "") {
                return;
            }
            packet.url = getGlobalPathRoot() + "oframe/sysmg/sys007/sys007-prjFunc.gv?prjCd=" + getPrjCd();
            packet.data.add("grantType", grantType);
            packet.data.add("roleCd", roleCd);
            core.ajax.sendPacketHtml(packet, function (response) {
                    var jsonData = eval("(" + response + ")");
                    var treeJson = jsonData.resultMap.treeJson;
                    // 服务调用是否成功
                    var isSuccess = jsonData.success;
                    if (isSuccess) {
                        // 目录树数据
                        // 左侧树的生成和回调
                        var rhtTreeSetting = {
                            data: {
                                simpleData: {
                                    enable: true
                                }
                            },
                            check: {
                                autoCheckTrigger: false,
                                enable: true,
                                chkStyle: "checkbox",
                                chkboxType: {"Y": "ps", "N": "s"}
                            }
                        };
                        // 初始化目录树
                        var zTree = $.fn.zTree.init($("#sys007RoleRhtTree_PFunc"), rhtTreeSetting, treeJson);
                    }
                    else {
                        alertMsg.warn(jsonData.errMsg);
                    }
                }, true, false
            );
        }
    },

    /**
     * 加载项目数据权限
     * @param obj 点击对象
     */
    loadDataRht: function (obj, grantType) {
        var $this = $(obj);
        var cIdx = $this.prevAll("li").length;
        var textContainer = $("#sys007NavContext", navTab.getCurrentPanel()).find(">div").eq(cIdx);
        if (textContainer.find("ul.ztree").html() == "") {
            // 重新加载
            var packet = new AJAXPacket();
            // 角色编码
            var roleCd = $("input[name=roleCd]", navTab.getCurrentPanel()).val();
            if (roleCd == "") {
                return;
            }
            packet.url = getGlobalPathRoot() + "oframe/sysmg/sys007/sys007-dataRht.gv?prjCd=" + getPrjCd();
            packet.data.add("grantType", grantType);
            packet.data.add("roleCd", roleCd);
            core.ajax.sendPacketHtml(packet, function (response) {
                    var jsonData = eval("(" + response + ")");
                    var treeJson = jsonData.resultMap.treeJson;
                    // 服务调用是否成功
                    var isSuccess = jsonData.success;
                    if (isSuccess) {
                        // 目录树数据
                        // 左侧树的生成和回调
                        var rhtTreeSetting = {
                            data: {
                                simpleData: {
                                    enable: true
                                }
                            },
                            callback: {
                                onCheck: sys007.checkNode
                            },
                            check: {
                                autoCheckTrigger: false,
                                enable: true,
                                chkStyle: "checkbox",
                                chkboxType: {"Y": "", "N": ""}
                            }
                        };
                        // 初始化目录树
                        var zTree = $.fn.zTree.init($("#sys007RoleRhtTree_" + grantType), rhtTreeSetting, treeJson);
                    }
                    else {
                        alertMsg.warn(jsonData.errMsg);
                    }
                }, true, false
            );
        }
    },

    /**
     * 保存角色权限
     * @param rhtType 角色类型
     * @param rhtUseType 调整的分类
     */
    saveRoleRht: function (treeId, grantType) {
        var treeObj = $.fn.zTree.getZTreeObj(treeId);
        var nodes = treeObj.getCheckedNodes(true);
        var checkedNodes = "";
        var checkedPrjCds = "";
        if (nodes) {
            var tempArr = [];
            var prjCdArr = [];
            for (var i = 0; i < nodes.length; i++) {
                tempArr.push(nodes[i].rhtId);
                if (nodes[i].prjCd) {
                    prjCdArr.push(nodes[i].prjCd);
                } else {
                    prjCdArr.push(0);
                }

            }
            checkedNodes = tempArr.toString();
            checkedPrjCds = prjCdArr.toString();
        }
        // 重新加载
        var packet = new AJAXPacket();
        // 角色编码
        var roleCd = $("input[name=roleCd]", navTab.getCurrentPanel()).val();
        packet.url = getGlobalPathRoot() + "oframe/sysmg/sys007/sys007-saveRht.gv";
        packet.data.add("grantType", grantType);
        packet.data.add("roleCd", roleCd);
        packet.data.add("newRhtIds", checkedNodes);
        packet.data.add("prjCds", checkedPrjCds);
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            // 服务调用是否成功
            var isSuccess = jsonData.success;
            if (isSuccess) {
                alertMsg.correct("保存成功")
            } else {
                alertMsg.warn(jsonData.errMsg);
            }
        });
    },

    saveRoleStaff: function () {
        // value为用户选中的关键词
        var packet = new AJAXPacket();
        // 组织节点
        packet.data.add("prjCd", $("input[name=rolePrjCd]", navTab.getCurrentPanel()).val());
        packet.data.add("orgId", $("input[name=orgId]", navTab.getCurrentPanel()).val());
        packet.data.add("roleName", $("input[name=roleName]", navTab.getCurrentPanel()).val());
        packet.data.add("roleCd", $("input[name=roleCd]", navTab.getCurrentPanel()).val());
        packet.data.add("upRoleCd", $("input[name=upNodeId]", navTab.getCurrentPanel()).val());
        // 获取授权工号
        var roleStaffArr = [];
        $("#sys007RoleStaffList>li", navTab.getCurrentPanel()).each(function () {
            var $this = $(this);
            var staffId = $this.attr("staffId");
            if (staffId != "") {
                roleStaffArr.push(staffId);
            }
        });
        packet.data.add("staffIds", roleStaffArr.join(","));
        // 保存角色信息
        packet.url = getGlobalPathRoot() + "oframe/sysmg/sys004/sys004-saveRoleInfo.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            if (jsonData.success) {
                alertMsg.correct("保存成功!");
            } else {
                alertMsg.error(jsonData.errMsg);
            }
        });
    },

    /**
     * 选中父节点取消所有子节点，选中子节点取消所有父节点
     * @param event
     * @param treeId
     * @param treeNode
     */
    checkNode: function (event, treeId, treeNode) {
        var treeObj = $.fn.zTree.getZTreeObj(treeId);
        var parentNode = treeNode.getParentNode();
        if (parentNode) {
            sys007.checkAllParent(treeObj, parentNode, false);
        }
        var childNodes = treeNode.children;
        sys007.checkAllSub(treeObj, childNodes, false);
    },
    checkAllParent: function (treeObj, treeNode, checkStatus) {
        treeObj.checkNode(treeNode, checkStatus, true);
        if (treeNode.getParentNode()) {
            sys007.checkAllParent(treeObj, treeNode.getParentNode(), checkStatus);
        }
    },
    checkAllSub: function (treeObj, subTreeNodeList, checkStatus) {
        if (subTreeNodeList) {
            for (var i = 0; i < subTreeNodeList.length; i++) {
                var child = subTreeNodeList[i];
                treeObj.checkNode(child, checkStatus, true);
                sys007.checkAllSub(treeObj, child.children, checkStatus)
            }
        }
    }
}
;
/**
 * 界面初始化加载
 */
$(document).ready(function () {
    // 执行目录树初始化
    sys007.initFullRoleTree();

    // 拖动屏幕切分效果
    LeftMenu.init("sys007LeftDiv", "sys007RightDiv");
});


