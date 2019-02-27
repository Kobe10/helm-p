staffTree = {
    isCheckEnable: function () {
        var objName = $("input[name='objName']", $.pdialog.getCurrent()).val();
        if (objName && objName != "") {
            return true;
        } else {
            return false;
        }
    },
    /**
     * 初始化化目录树
     */
    initFullTree: function () {
        // 重新加载
        var packet = new AJAXPacket();
        var container = $("#staffTreeContainer");
        var treePageContainer = container.closest("div.js_staff_tree");
        var staffPrjCd = $("input[name=staffPrjCd]", treePageContainer).val();
        if (staffPrjCd == "") {
            staffPrjCd = getPrjCd();
        }
        packet.data.add("prjCd", staffPrjCd);
        packet.url = getGlobalPathRoot() + "oframe/sysmg/sys004/sys004-staffTree.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
                var jsonData = eval("(" + response + ")");
                var treeJson = jsonData.resultMap.treeJson;
                // 服务调用是否成功
                var isSuccess = jsonData.success;
                if (isSuccess) {
                    // 目录树数据
                    // 左侧树的生成和回调
                    var pjstaffTreeSetting = {
                        data: {
                            simpleData: {
                                enable: true
                            }
                        },
                        callback: {
                            onClick: staffTree.clickNode
                        },
                        check: {
                            autoCheckTrigger: false,
                            enable: staffTree.isCheckEnable(),
                            chkStyle: "checkbox",
                            chkboxType: {"Y": "", "N": ""}
                        }
                    };
                    // 初始化目录树
                    var zTree = $.fn.zTree.init(container, pjstaffTreeSetting, treeJson);
                }
                else {
                    alertMsg.warn(jsonData.data.errMsg);
                }
            }, true, false
        )
    },
    /**
     * 点击节点，选择单个员工
     * @param event
     * @param treeId
     * @param treeNode
     * @returns {boolean}
     */
    clickNode: function (event, treeId, treeNode) {
        var tipObj = $("#openTip", $.pdialog.getCurrent());
        if (tipObj.length == 0) {
            tipObj = $("#openTip", navTab.getCurrentPanel());
        }
        var fromOp = $("input[name=fromOp]", tipObj).val();
        if (fromOp == 'sys014') {
            if (staffTree.isCheckEnable()) {
                return false;
            } else {
                var clickObj = tipObj.data("clickObj");
                var rowObj = $(clickObj).parent();
                // 有staffId是人 拿 staffId ，没有就是组织拿id。
                if (treeNode.staffId) {
                    //选中 收件人
                    var staffIdArr = [];
                    var staffIdStr = $("input[name=toStaffId]", $.pdialog.getCurrent()).val();
                    if (staffIdStr && staffIdStr != undefined && staffIdStr != '') {
                        staffIdArr = staffIdStr.split(",");
                        var idInArr = false;
                        for (var i = 0; i < staffIdArr.length; i++) {
                            var staffId = staffIdArr[i];
                            if (staffId == treeNode.staffId) {
                                idInArr = true;
                                break;
                            }
                        }
                        if (!idInArr) {
                            staffIdArr.push(treeNode.staffId);
                            var currTd = $("td.js_toStaff_tr", $.pdialog.getCurrent());
                            var hideSpan = $("span.hidden", currTd).clone(true);
                            $("input[name=currentStaff]", hideSpan).val(treeNode.staffId);
                            hideSpan.removeClass("hidden");
                            $("span.js_toStaff_name", hideSpan).text(treeNode.name);
                            currTd.append(hideSpan);
                        }
                        $("input[name=toStaffId]", $.pdialog.getCurrent()).val(staffIdArr.join(","));
                    } else {
                        var currTd = $("td.js_toStaff_tr", $.pdialog.getCurrent());
                        var hideSpan = $("span.hidden", currTd).clone(true);
                        $("input[name=currentStaff]", hideSpan).val(treeNode.staffId);
                        hideSpan.removeClass("hidden");
                        $("span.js_toStaff_name", hideSpan).text(treeNode.name);
                        currTd.append(hideSpan);
                        $("input[name=toStaffId]", $.pdialog.getCurrent()).val(treeNode.staffId);
                    }
                } else {
                    //点击 组织
                    /**
                     * 添加发送组织
                     */
                        //var $this = $("#sys014OrgId", $.pdialog.getCurrent());
                    var currOrgId = treeNode.id;
                    var currOrgName = treeNode.name;
                    //选中 收件人
                    var orgIdArr = [];
                    var orgIdStr = $("input[name=toOrgId]", $.pdialog.getCurrent()).val();
                    if (orgIdStr && orgIdStr != undefined && orgIdStr != '') {
                        orgIdArr = orgIdStr.split(",");
                        var idInArr = false;
                        for (var i = 0; i < orgIdArr.length; i++) {
                            var orgId = orgIdArr[i];
                            if (orgId == currOrgId) {
                                idInArr = true;
                                break;
                            }
                        }
                        if (!idInArr) {
                            orgIdArr.push(currOrgId);
                            var currTd = $("td.js_toOrg_tr", $.pdialog.getCurrent());
                            var hideSpan = $("span.hidden", currTd).clone(true);
                            $("input[name=currentOrg]", hideSpan).val(currOrgId);
                            hideSpan.removeClass("hidden");
                            $("span.js_toOrg_name", hideSpan).text(currOrgName);
                            currTd.append(hideSpan);
                        }
                        $("input[name=toOrgId]", $.pdialog.getCurrent()).val(orgIdArr.join(","));
                    } else {
                        var currTd = $("td.js_toOrg_tr", $.pdialog.getCurrent());
                        var hideSpan = $("span.hidden", currTd).clone(true);
                        $("input[name=currentOrg]", hideSpan).val(currOrgId);
                        hideSpan.removeClass("hidden");
                        $("span.js_toOrg_name", hideSpan).text(currOrgName);
                        currTd.append(hideSpan);
                        $("input[name=toOrgId]", $.pdialog.getCurrent()).val(currOrgId);
                    }
                }
                $(tipObj).remove();
            }
        } else if ("pj01502" == fromOp) {
            if (treeNode.staffId) {
                pj015.addRhtStaff(treeNode.staffId, treeNode.name);
                $(tipObj).remove();
            }
        } else {
            if (staffTree.isCheckEnable() || !treeNode.staffId) {
                return false;
            } else {
                var tipObj = $("#openTip", $.pdialog.getCurrent());
                if (tipObj.length == 0) {
                    tipObj = $("#openTip", navTab.getCurrentPanel());
                }
                var clickObj = tipObj.data("clickObj");
                var rowObj = $(clickObj).parent();
                var hiddenObj = $(rowObj).find("input[type=hidden]");
                var hAttr = hiddenObj.attr("hAttr");
                if (!hAttr || hAttr == "") {
                    hAttr = "staffCd";
                }
                $(rowObj).find("input[type=hidden]").val(treeNode[hAttr]);
                $(rowObj).find("input[type=text]").val(treeNode.name);
                $(tipObj).remove();
            }
        }
    },

    onConfim: function (obj) {
        var treeObj = $.fn.zTree.getZTreeObj("staffTreeContainer");
        var nodes = treeObj.getCheckedNodes(true);
        var checkedNodes = "";
        if (nodes) {
            var tempArr = [];
            for (var i = 0; i < nodes.length; i++) {
                tempArr.push(nodes[i].id);
            }
            checkedNodes = tempArr.toString();
        }
        var tipObj = $("#openTip", $.pdialog.getCurrent());
        var clickObj = tipObj.data("clickObj");
        var rowObj = $(clickObj).parent();
        var objName = $("input[name='objName']", $.pdialog.getCurrent()).val();
        $(rowObj).find("input[name='" + objName + "']").val(checkedNodes);
        $(obj).closest("div.tip").remove();
    }
};
$(document).ready(function () {
    // 执行目录树初始化
    staffTree.initFullTree();
});

