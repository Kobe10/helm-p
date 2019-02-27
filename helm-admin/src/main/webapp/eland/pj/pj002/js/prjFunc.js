var prjFunc = {
    /**
     * 初始化化目录树
     */
    initFullTree: function () {
        // 重新加载
        var packet = new AJAXPacket();
        var prjCd = $("input[name=prjCd]", navTab.getCurrentPanel()).val();
        var roleCd = $("input[name=funcRole]", navTab.getCurrentPanel()).val();
        var prjType = $("input[name=prjType]", navTab.getCurrentPanel()).val();
        packet.url = getGlobalPathRoot() + "oframe/sysmg/sys007/sys007-rhtInfo.gv";
        packet.data.add("rhtType", "1");
        packet.data.add("rhtUseType", prjType);
        packet.data.add("grantType", "p_func");
        packet.data.add("roleCd", roleCd);
        packet.data.add("prjCd", prjCd);
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
                            check: {
                                autoCheckTrigger: false,
                                chkStyle: "checkbox",
                                chkboxType: {"Y": "ps", "N": ""},
                                enable: true
                            }
                        }
                        ;
                    // 初始化目录树
                    var zTree = $.fn.zTree.init($("#prjFuncSltTree"), pjOrgTreeSetting, treeJson);
                    //$("#pj002RoleStaff>li", navTab.getCurrentPanel()).each(function () {
                    //    var $this = $(this);
                    //    var staffId = $this.attr("staffId");
                    //    if (staffId != "") {
                    //        var staffNode = zTree.getNodeByParam("id", "staff_" + staffId, null);
                    //        if (staffNode) {
                    //            zTree.checkNode(staffNode, true, true);
                    //        }
                    //    }
                    //
                    //});
                } else {
                    alertMsg.warn(jsonData.errMsg);
                }
            }, true, false
        )
    },

    /**
     * 保存公司组织角色
     */
    savePrjFun: function () {
        var treeObj = $.fn.zTree.getZTreeObj("prjFuncSltTree");
        var nodes = treeObj.getCheckedNodes(true);
        var prjCd = $("input[name=prjCd]", navTab.getCurrentPanel()).val();
        var checkedNodes = "";
        var checkedPrjCds = "";
        if (nodes) {
            var tempArr = [];
            var prjCdArr = [];
            for (var i = 0; i < nodes.length; i++) {
                tempArr.push(nodes[i].id);
                prjCdArr.push(prjCd);
            }
            checkedNodes = tempArr.toString();
            checkedPrjCds = prjCdArr.toString();
        }
        // 重新加载
        var packet = new AJAXPacket();
        // 角色编码
        var roleCd = $("input[name=funcRole]", navTab.getCurrentPanel()).val();
        packet.url = getGlobalPathRoot() + "oframe/sysmg/sys007/sys007-saveRht.gv";
        packet.data.add("grantType", "p_func");
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
    }
};
$(document).ready(function () {
    prjFunc.initFullTree();
});

