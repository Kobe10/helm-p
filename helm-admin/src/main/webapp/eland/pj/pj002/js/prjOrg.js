var prjOrg = {

    /**
     * 初始化化目录树
     */
    initFullTree: function () {
        // 重新加载
        var packet = new AJAXPacket();
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
                                onCheck: prjOrg.checkStaff
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
                    var zTree = $.fn.zTree.init($("#pj002SltStaffTree"), pjOrgTreeSetting, treeJson);
                    $("#pj002OrgStaff>li", navTab.getCurrentPanel()).each(function () {
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
            var pj002OrgStaffDiv = $("#pj002OrgStaff", navTab.getCurrentPanel());
            var newItem = pj002OrgStaffDiv.find("li:eq(0)").clone();
            newItem.attr("staffId", treeNode.staffId);
            newItem.find("label").html(treeNode.staffCd + "(" + treeNode.name + ")");
            newItem.removeClass("hidden");
            pj002OrgStaffDiv.append(newItem);
        } else {
            $("#pj002OrgStaff>li", navTab.getCurrentPanel()).each(function () {
                var $this = $(this);
                var staffId = $this.attr("staffId");
                if (staffId == treeNode.staffId) {
                    $this.remove();
                    return false;
                }
            });
        }
    },
    dltOrgStaff: function (obj) {
        var liObj = $(obj).closest("li");
        var staffId = liObj.attr("staffId");
        var staffTree = $.fn.zTree.getZTreeObj("pj002SltStaffTree");
        var staffNode = staffTree.getNodeByParam("id", "staff_" + staffId, null);
        if (staffNode) {
            staffTree.checkNode(staffNode, false, false);
        }
        liObj.remove();
    },
    /**
     * 保存组织信息
     */
    saveOrg: function () {
        // 验证表单
        if (!$("#pj002OrgStaffInfoFrm", navTab.getCurrentPanel()).valid()) {
            return
        }
        // value为用户选中的关键词
        var packet = new AJAXPacket();
        // 组织节点
        packet.data.add("prjCd", $("input[name=prjCd]", navTab.getCurrentPanel()).val());
        packet.data.add("orgId", $("input[name=orgId]", navTab.getCurrentPanel()).val());
        packet.data.add("orgName", $("input[name=orgName]", navTab.getCurrentPanel()).val());
        packet.data.add("upNodeId", $("input[name=upNodeId]", navTab.getCurrentPanel()).val());
        // 获取授权工号
        var roleStaffArr = [];
        $("#pj002OrgStaff>li", navTab.getCurrentPanel()).each(function () {
            var $this = $(this);
            var staffId = $this.attr("staffId");
            if (staffId != "") {
                roleStaffArr.push(staffId);
            }
        });
        packet.data.add("staffIds", roleStaffArr.join(","));
        // 保存角色信息
        packet.url = getGlobalPathRoot() + "oframe/sysmg/sys004/sys004-saveNode.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            if (jsonData.success) {
                alertMsg.correct("保存成功!");
                var method = jsonData.method;
                var nodeInfo = jsonData.nodeInfo;
            } else {
                alertMsg.error(jsonData.errMsg)
            }
        });
    }
};

$(document).ready(function () {
    // 执行目录树初始化
    prjOrg.initFullTree();
});

