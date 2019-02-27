var ph003Op = {
    /*新增建筑*/
    addHouse: function () {
        var url = "eland/ph/ph003/ph00301-initS.gv";
        index.quickOpen(url, {opCode: "ph00301-initS", opName: "信息登记", fresh: true});
    },

    /**
     * 房屋信息
     * @param buildId
     */
    openHs: function (hsId) {
        //打开详细房产详情页面
        var url = "eland/ph/ph003/ph003-init.gv?hsId=" + hsId;
        index.quickOpen(url, {opCode: "ph003-init", opName: "居民详情", fresh: true});
    },

    /*发起修改流程*/
    initUpDateTask: function (procDefKey, hsId) {
        index.startWf(procDefKey, getPrjCd(), {busiKey: "HouseInfo_" + hsId});
    },

    /*发起房产锁定流程*/
    initLockHsTask: function (procDefKey, hsId) {
        index.startWf(procDefKey, getPrjCd(), {busiKey: "HouseInfo_" + hsId});
    },

    /* 新增建筑 */
    editHouse: function (hsId) {
        var url = "eland/ph/ph003/ph00301-initHs.gv?privilegeId=ph00301-initHs&hsId=" + hsId;
        index.quickOpen(url, {opCode: "ph00301-initHs", opName: "信息登记", fresh: true});
    },

    /* 修改居民选房 */
    editChooseHs: function (hsId) {
        var url = getGlobalPathRoot() + "eland/ch/ch002/ch002-init.gv?hsId=" + hsId + "&prjCd=" + getPrjCd();
        $.pdialog.open(url, "ch002-init", "修改居民选房", {max: true, mask: true});
    },


    /*建筑修改记录*/
    editRecords: function (hsId) {
        var url = getGlobalPathRoot() + "eland/ph/ph003/ph00301-editRecords.gv?hsId=" + hsId + "&prjCd=" + getPrjCd();
        $.pdialog.open(url, "ph00301-editRecords", "修改记录", {max: true, mask: true});
    },

    /*查看居民信息*/
    viewHouse: function (hsId) {
        var url = "eland/ph/ph003/ph003-init.gv?hsId=" + hsId;
        index.quickOpen(url, {opCode: "ph003-init", opName: "居民详情", fresh: true});
    },

    /**
     * 打开计算器
     */
    openCalculate: function (hsId) {
        // 保存返回
        var url = "eland/ph/ph004/ph004-init.gv?hsId=" + hsId + "&prjCd=" + getPrjCd();
        index.quickOpen(url, {opCode: "ph004-init", opName: "补偿计算", fresh: true});
    },

    /**
     * 打开赠送面积页面
     */
    hsSizeCtGive: function (hsId) {
        // 保存返回
        var url = getGlobalPathRoot() + "eland/ph/ph003/ph00301-hsSizeCtGive.gv?hsId=" + hsId + "&prjCd=" + getPrjCd();
        $.pdialog.open(url, "ph00301-hsSizeCtGive", "面积赠送", {mask: true, height: 600, width: 800});
    },

    /**
     * 打开访谈记录
     * @param hsId
     */
    openTalkRecord: function (hsId) {
        var url = getGlobalPathRoot() + "eland/ph/ph003/ph00314-initTalkRecord.gv?hsId=" + hsId + "&prjCd=" + getPrjCd();
        $.pdialog.open(url, "ph00314-initTalkRecord", "居民工作日志", {mask: true, height: 690, width: 800});
    },

    /**
     * 打开关联房产
     */
    openHsRelInfo: function (hsId) {
        var url = "eland/ph/ph012/ph012-init.gv?prjCd=" + getPrjCd() + "&hsId=" + hsId;
        index.quickOpen(url, {opCode: "ph012-init", opName: "关联房产", fresh: true});
    },

    /**
     * 删除房屋信息
     * @param hsId 房屋编号
     */
    deleteHouse: function (hsId) {
        alertMsg.confirm("你确定要删除吗？", {
            okCall: function () {
                var packet = new AJAXPacket();
                // 区域归属项目
                var prjCd = getPrjCd();
                packet.data.add("houseId", hsId);
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
    },

    /**
     * 生成签约安置协议
     */
    hsCt: function (hsId) {
        // 提交页面数据
        var url = "eland/ct/ct001/ct001-init.gv?hsId=" + hsId;
        index.quickOpen(url, {opCode: "ct001-init", opName: "居民签约", fresh: true});
    },

    /**
     * 生成签约安置协议
     */
    hsCh: function (hsId) {
        // 提交页面数据
        var url = "eland/ch/ch001/ch001-init.gv?hsId=" + hsId;
        index.quickOpen(url, {opCode: "ch001-init", opName: "居民选房", fresh: true});
    },
    /**
     * 外迁选房操作
     */
    chooseHouse: function (hsId, fromOp, method) {
        var url = getGlobalPathRoot() + "eland/oh/oh002/oh002-outChooseHs.gv?hsId=" + hsId + "&prjCd=" + getPrjCd() + "&fromOp=" + fromOp + "&method=" + method;
        $.pdialog.open(url, "oh002", "选房详情", {max: true, mask: true});
    },

    /**
     * 导出房屋交接清单 信息
     * @param buildId
     */
    exportFwJjQd: function (hsId) {
        var url = getGlobalPathRoot() + "eland/ph/ph009/ph009-exportFwJjQd.gv?hsId=" + hsId + "&prjCd=" + getPrjCd();
        window.open(url)
    },

    /**
     * 导出腾房交房（房屋）房屋交接验收确认单
     * @param buildId
     */
    exportTfJf: function (hsId) {
        var url = getGlobalPathRoot() + "eland/ph/ph009/ph009-exportTfJf.gv?hsId=" + hsId + "&prjCd=" + getPrjCd();
        window.open(url)
    },
    /**
     * 导出承租人变更申请
     * @param buildId
     */
    exportGfTz: function (hsId, buildId) {
        var url = getGlobalPathRoot() + "eland/ph/ph009/ph009-exportGfTz.gv?hsId=" + hsId + "&prjCd=" + getPrjCd() + "&buildId=" + buildId;
        window.open(url)
    },

    /**
     * 导出居民换房申请书
     * @param buildId
     */
    exportZmHf: function (hsId, buildId) {
        var url = getGlobalPathRoot() + "eland/ph/ph009/ph009-exportZmHf.gv?hsId=" + hsId + "&prjCd=" + getPrjCd() + "&buildId=" + buildId;
        window.open(url)
    },
    /**
     * 导出 过户介绍信信息
     * @param buildId
     */
    exportGhJsX: function (hsId, buildId) {
        var url = getGlobalPathRoot() + "eland/ph/ph009/ph009-exportGhJsX.gv?hsId=" + hsId + "&prjCd=" + getPrjCd() + "&buildId=" + buildId;
        window.open(url)
    },
    /**
     * 导出过户介绍信 信息
     * @param buildId
     */
    exportZjGh: function (buildId) {
        var url = getGlobalPathRoot() + "eland/ph/ph009/ph009-exportZjGh.gv?buildId=" + buildId + "&prjCd=" + getPrjCd();
        window.open(url)
    },
    /**
     * 导出 办理网签、核税、缴税等手续合同明细表
     * @param buildId
     */
    exportWqHsJs: function (hsId) {
        var url = getGlobalPathRoot() + "eland/ph/ph009/ph009-exportWqHsJsMx.gv?hsId=" + hsId + "&prjCd=" + getPrjCd() + "&buildId=";
        window.open(url)
    },
    /**
     * 导出 生成公房结算明细表（房屋）
     * @param buildId
     */
    exportGfSs: function (hsId) {
        var url = getGlobalPathRoot() + "eland/ph/ph009/ph009-exportGfSs.gv?hsId=" + hsId + "&prjCd=" + getPrjCd();
        window.open(url)
    },
    exportWyGn: function (hsId) {
        var url = getGlobalPathRoot() + "eland/ph/ph009/ph009-exportWyGn.gv?hsId=" + hsId + "&prjCd=" + getPrjCd();
        window.open(url)
    },

    /**
     * 上传附件
     * @param hsId
     * @param buildId
     */
    openOash: function (hsId) {
        var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-openOash.gv?relType=100&relId=" + hsId + "&prjCd=" + getPrjCd();
        $.pdialog.open(url, "ph008-openOash", "附件管理", {mask: true, height: 600, width: 800});
    }

};
