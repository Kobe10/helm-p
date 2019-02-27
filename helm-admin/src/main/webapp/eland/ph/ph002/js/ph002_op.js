var ph002Op = {

    /**
     * 打开建筑信息页面
     * @param regId
     */
    openBuilding: function (buldId) {
        var url = "eland/ph/ph002/ph002-init.gv?buldId=" + buldId;
        index.quickOpen(url, {opCode: "pb002-init", opName: "院落详情", fresh: true});
    },

    /**
     * 删除院落信息
     * @param buildId 房屋编号
     */
    deleteBuild: function (buildId) {
        alertMsg.confirm("删除院落将同时删除院落内的房产信息，你确定要删除吗？", {
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
                    var isSuccess = data.success;
                    if (isSuccess) {
                        // 从目录树中删除节点
                        var zTree = $.fn.zTree.getZTreeObj("ph001Tree");
                        var treeNode = zTree.getNodesByParam("buldId", data.buildId, null);
                        if (treeNode.length == 1) {
                            zTree.removeNode(treeNode[0], false);
                        }
                        // 重新查询数据
                        ph001.refleshBuild();
                    } else {
                        alertMsg.error(data.errMsg);
                    }
                }, true);
            }
        });
    },

    /**
     * 导出xxx号院落购房明细表
     * @param buildId
     */
    exportTxAzF: function (buildId) {
        var url = getGlobalPathRoot() + "eland/ph/ph009/ph009-exportTxAzF.gv?buildId=" + buildId + "&prjCd=" + getPrjCd();
        window.open(url)
    },


    /**
     * 导出院落腾退说明
     * @param buildId
     */
    exportTtCb: function (buildId) {
        var url = getGlobalPathRoot() + "eland/ph/ph002/ph002-exportTtCb.gv?buildId=" + buildId + "&prjCd=" + getPrjCd();
        window.open(url)
    },

    /**
     * 查看院落附件
     * @param buildId 院落编号
     */
    viewBuildFj: function (buildId) {
        var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-openOash.gv?relType=200&relId="
            + buildId + "&prjCd=" + getPrjCd();
        $.pdialog.open(url, "ph008-openOash", "附件上传", {mask: true, height: 600, width: 800});
    },

    /**
     * 查看院落附件
     * @param buildId 院落编号
     */
    exportYardFj: function (buildId) {
        var url = getGlobalPathRoot() + "eland/ph/ph002/ph002-exportYardFj.gv?buildId=" + buildId + "&prjCd=" + getPrjCd();
        window.open(url);
    },

    /**
     * 导出导出院落下的所有附件
     * @param buildId
     */
    exportBuildFj: function (buildId) {
        var url = url = getGlobalPathRoot() + "eland/ph/ph002/ph002-exportFj.gv?buildId=" + buildId + "&prjCd=" + getPrjCd();
        window.open(url)
    },

    /**
     * 导出 购房人资格审核阶段的 购房人员明细表
     * @param buildId
     */
    exportGfRyMx: function (buildId) {
        var url = getGlobalPathRoot() + "eland/ph/ph009/ph009-exportGfRyMx.gv?prjCd=" + getPrjCd() + "&buildId=" + buildId;
        window.open(url)
    },

    /**
     * 导出 购房人资格审核阶段的 购房人户籍人口明细表
     * @param buildId
     */
    exportGfrhj: function (buildId) {
        var url = getGlobalPathRoot() + "eland/ph/ph009/ph009-exportGfrhj.gv?prjCd=" + getPrjCd() + "&buildId=" + buildId;
        window.open(url)
    },

    /**
     * 完成退租手续 租金过户明细
     * @param buildId
     */
    exportZjGhMx: function (buildId) {
        var url = getGlobalPathRoot() + "eland/ph/ph009/ph009-exportZjGhMx.gv?prjCd=" + getPrjCd() + "&buildId=" + buildId;
        window.open(url)
    },
    /**
     * 导出 定向房审批表
     * @param buildId
     */
    exportDxFsP: function (buildId) {
        var url = getGlobalPathRoot() + "eland/ph/ph009/ph009-exportDxFsP.gv?prjCd=" + getPrjCd() + "&buildId=" + buildId;
        window.open(url)
    }
};
