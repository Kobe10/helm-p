var prjStaff = {

    /**
     * 初始化化目录树
     */
    initFullTree: function () {
        // 重新加载
        var packet = new AJAXPacket();
        var prjCd = $("input[name=prjCd]", navTab.getCurrentPanel()).val();
        packet.data.add("prjCd", prjCd);
        packet.data.add("fullTree", "true");
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
                            onClick: prjStaff.viewNode
                        }
                    };
                    // 初始化目录树
                    var zTree = $.fn.zTree.init($("#pj002OrgTreeForStaff"), pjOrgTreeSetting, treeJson);
                    // 显示根节点数据
                    var rootNode = zTree.getNodesByFilter(function (node) {
                        return node.level == 0
                    }, true);
                    if (rootNode != null) {
                        zTree.selectNode(rootNode);
                        prjStaff.viewNode(null, "ph001Tree", rootNode);
                    }
                } else {
                    alertMsg.warn(jsonData.errMsg);
                }
            }, true, false
        )
    },
    /**
     * 点击查看目录节点
     * @param event js event对象
     * @param treeId  对应 zTree 的 treeId
     * @param treeNode  被点击的节点 JSON 数据对象
     * @param method 对节点的操作
     */
    viewNode: function (event, treeId, treeNode) {
        var formObj = $("#pj002PrjStaffFrm", navTab.getCurrentPanel());
        $("input[name=orgId]", formObj).val(treeNode.id);
        prjStaff.queryStaff();
    },

    /**
     * 查询
     */
    queryStaff: function () {
        Query.queryList("pj002PrjStaffFrm", 'pj002_staff_list_print', "");
    },

    /**
     * 打开检索
     * @param conditionFieldArr 查询字段数组
     * @param conditionArr 查询条件数组
     * @param conditionValueArr 查询值数组
     */
    openQSch: function (clickObj) {
        if (!$(clickObj).hasClass("active")) {
            $("#pj002StaffQSchDialog", navTab.getCurrentPanel()).show().attr("active", "on");
            $("#pj002StaffQSchDialog input:eq(0)", navTab.getCurrentPanel()).focus();
        }
    },

    /**
     * 关闭检索
     * @param forceFlag 强制关闭
     */
    closeQSch: function (forceFlag) {
        $("#pj002StaffQSchDialog", navTab.getCurrentPanel()).hide();
    },

    openAddStaff: function () {
        var orgId = $("input[name=orgId]", navTab.getCurrentPanel()).val();
        var url = getGlobalPathRoot() + "oframe/sysmg/sys002/sys002-info.gv?prjCd=" + getPrjCd() + "&method=add&orgId=" + orgId;
        $.pdialog.open(url, "sys00201", "员工信息", {mask: true, width: 800, height: 600});
    },

    openEditStaff: function (staffId) {
        var url = getGlobalPathRoot() + "oframe/sysmg/sys002/sys002-info.gv?prjCd=" + getPrjCd() + "&method=edit&staffId=" + staffId;
        $.pdialog.open(url, "sys00201", "员工信息", {mask: true, width: 800, height: 600});
    },


};

$(document).ready(function () {
    // 执行目录树初始化
    prjStaff.initFullTree();
});

