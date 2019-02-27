var prjRole = {
    /**
     * 初始化化目录树
     */
    initFullTree: function () {
        // 重新加载
        var packet = new AJAXPacket();
        var prjCd = $("input[name=prjCd]", navTab.getCurrentPanel()).val();
        packet.data.add("prjCd", prjCd);
        packet.data.add("fullTree", "true");
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
                                onCheck: prjRole.checkStaff
                            },
                            check: {
                                autoCheckTrigger: false,
                                chkStyle: "checkbox",
                                chkboxType: {"Y": "", "N": ""},
                                enable: true
                            }
                        }
                        ;
                    // 初始化目录树
                    var zTree = $.fn.zTree.init($("#prjRoleSltStaffTree"), pjOrgTreeSetting, treeJson);
                    $("#pj002RoleStaff>li", navTab.getCurrentPanel()).each(function () {
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
    },
    /**
     * 取消或选中
     * @param event
     * @param treeId
     * @param treeNode
     */
    checkStaff: function (event, treeId, treeNode) {
        if (treeNode.checked) {
            var pj002RoleStaffDiv = $("#pj002RoleStaff", navTab.getCurrentPanel());
            var newItem = pj002RoleStaffDiv.find("li:eq(0)").clone();
            newItem.attr("staffId", treeNode.staffId);
            newItem.find("label").html(treeNode.staffCd + "(" + treeNode.name + ")");
            newItem.removeClass("hidden");
            pj002RoleStaffDiv.append(newItem);
        } else {
            $("#pj002RoleStaff>li", navTab.getCurrentPanel()).each(function () {
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
        var staffTree = $.fn.zTree.getZTreeObj("prjRoleSltStaffTree");
        var staffNode = staffTree.getNodeByParam("id", "staff_" + staffId, null);
        if (staffNode) {
            staffTree.checkNode(staffNode, false, false);
        }
        liObj.remove();
    },
    roleInfo: function (obj) {
        var roleId = "";
        if (obj != null) {
            var $this = $(obj);
            $this.siblings().removeClass("active");
            $this.addClass("active");
            roleId = $this.attr("roleCd");
        } else {
            $("#pj002RoleList>li", navTab.getCurrentPanel()).removeClass("active");
        }
        var textContainer = $("#pj002RoleInfoDiv", navTab.getCurrentPanel());
        // value为用户选中的关键词
        var packet = new AJAXPacket();
        // 组织节点
        packet.data.add("prjCd", $("input[name=prjCd]", navTab.getCurrentPanel()).val());
        packet.data.add("roleCd", roleId);
        // 提交界面展示
        packet.url = getGlobalPathRoot() + "eland/pj/pj002/pj002-roleInfo.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            textContainer.html(response);
            initUI(textContainer);
        });

    },
    /**
     * 保存公司组织角色
     */
    saveRole: function () {
        // 验证表单
        if (!$("#pj002RoleInfoFrm", navTab.getCurrentPanel()).valid()) {
            return
        }
        // value为用户选中的关键词
        var packet = new AJAXPacket();
        // 组织节点
        packet.data.add("prjCd", $("input[name=prjCd]", navTab.getCurrentPanel()).val());
        packet.data.add("orgId", $("input[name=orgId]", navTab.getCurrentPanel()).val());
        packet.data.add("roleName", $("input[name=roleName]", navTab.getCurrentPanel()).val());
        var roleCd = $("input[name=roleCd]", navTab.getCurrentPanel()).val();
        var upRoleCd = $("input[name=upNodeId]", navTab.getCurrentPanel()).val();
        packet.data.add("roleCd", roleCd);
        packet.data.add("upRoleCd", upRoleCd);
        // 获取授权工号
        var roleStaffArr = [];
        $("#pj002RoleStaff>li", navTab.getCurrentPanel()).each(function () {
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
                var method = jsonData.method;
                var nodeInfo = jsonData.nodeInfo;
                if (method == "add") {
                    var liContain = $("#pj002RoleList", navTab.getCurrentPanel());
                    var newLi = liContain.find("li:eq(0)").clone();
                    newLi.html(nodeInfo.name).removeClass("hidden").attr("roleCd", nodeInfo.id);
                    liContain.append(newLi);
                    $("input[name=roleCd]", navTab.getCurrentPanel()).val(nodeInfo.id);
                    $("#pj002RmRoleLi", navTab.getCurrentPanel()).attr("style", "");
                } else {
                    $("#pj002RoleList>li", navTab.getCurrentPanel()).each(function () {
                        var $this = $(this);
                        if ($this.attr("roleCd") == nodeInfo.id) {
                            $this.html(nodeInfo.name);
                            return false;
                        }
                    });
                }
            } else {
                alertMsg.error(jsonData.errMsg)
            }
        });
    },

    removeRole: function () {
        alertMsg.confirm("确认要该角色的信息吗?", {
            okCall: function () {
                // value为用户选中的关键词
                var packet = new AJAXPacket();
                // 组织节点
                packet.data.add("prjCd", $("input[name=prjCd]", navTab.getCurrentPanel()).val());
                var roleCd = $("input[name=roleCd]", navTab.getCurrentPanel()).val();
                packet.data.add("roleCd", roleCd);

                // 保存角色信息
                packet.url = getGlobalPathRoot() + "oframe/sysmg/sys004/sys004-deleteRoleInfo.gv";
                core.ajax.sendPacketHtml(packet, function (response) {
                    var jsonData = eval("(" + response + ")");
                    if (jsonData.success) {
                        alertMsg.correct("删除成功!");
                        $("#pj002RoleList>li", navTab.getCurrentPanel()).each(function () {
                            var $this = $(this);
                            if ($this.attr("roleCd") == roleCd) {
                                $this.remove();
                                return false;
                            }
                        });
                        // 刷新角色信息
                        prjRole.initRoleInfo();
                    } else {
                        alertMsg.error(jsonData.errMsg)
                    }
                });
            }
        });
    },
    initRoleInfo: function () {
        var roleList = $("#pj002RoleList>li", navTab.getCurrentPanel());
        if (roleList.length > 1) {
            roleList.eq(1).trigger("click");
        } else {
            roleList.eq(0).trigger("click");
        }
    }

};
$(document).ready(function () {
    prjRole.initRoleInfo();
});

