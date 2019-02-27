/**
 * 房屋签约处理
 * @type {{viewHouse: ct005.viewHouse, schHs: ct005.schHs, ctInfo: ct005.ctInfo, generateCt: ct005.generateCt, cfmCt: ct005.cfmCt, cancelCt: ct005.cancelCt, rePrintDoc: ct005.rePrintDoc, printCt: ct005.printCt, photograph: ct005.photograph, openHs: ct005.openHs}}
 */
var ct005 = {

    viewHouse: function (hsId) {
        var url = "eland/ph/ph003/ph003-init.gv?hsId=" + hsId;
        index.quickOpen(url, {opCode: "ph003-init", opName: "居民详情", fresh: true});
    },

    /**
     * 检索房产
     * @param obj
     */
    schHs: function () {
        var schemeType = $("input[name=schemeType]", navTab.getCurrentPanel()).val();
        var ct005QueryForm = $("#ct005QueryForm_" + schemeType, navTab.getCurrentPanel());
        var ctStatusField = $("input[name=ctStatusField]", navTab.getCurrentPanel()).val();
        var container = $("#ct005SchDiv", navTab.getCurrentPanel());
        // 查询条件拼接
        var conditionNameArr = new Array();
        var conditionArr = new Array();
        var conditionValueArr = new Array();

        // 增加必须增加的数据条件
        var conditionNameTemp = $("input.js_conditionNames", ct005QueryForm).val().split(",");
        var conditionTemp = $("input.js_conditions", ct005QueryForm).val().split(",");
        var conditionValueTemp = $("input.js_conditionValues", ct005QueryForm).val().split(",");
        for (var i = 0; i < conditionNameTemp.length; i++) {
            if (i < conditionTemp.length && i < conditionValueTemp.length) {
                var tempName = conditionNameTemp[i];
                var tempCond = conditionTemp[i];
                var tempValue = conditionValueTemp[i];
                if (tempName != "" && tempCond != "" && tempValue != "") {
                    conditionNameArr.push(tempName);
                    conditionArr.push(tempCond);
                    conditionValueArr.push(tempValue);
                }
            }
        }

        // 查询条件
        var schType = $("select[name=schType]", container).val();
        var schValue = $("input[name=schValue]", container).val();
        conditionNameArr.push(schType);
        conditionArr.push("like");
        conditionValueArr.push(schValue);
        // 签约状态
        var ctStatus = [];
        navTab.getCurrentPanel()
            .find(":checkbox[checked][name=" + ctStatusField + "]").each(
            function (i) {
                ctStatus.push($(this).val());
            }
        );
        // 增加签约状态
        if (ctStatus.length > 0) {
            conditionNameArr.push("HouseInfo.HsCtInfo." + ctStatusField);
            conditionArr.push("in");
            conditionValueArr.push(ctStatus.join("|"));
        }
        // 执行数据查询
        $("input[name=conditionName]", ct005QueryForm).val(conditionNameArr.join(","));
        $("input[name=condition]", ct005QueryForm).val(conditionArr.join(","));
        $("input[name=conditionValue]", ct005QueryForm).val(conditionValueArr.join(","));
        // 查询处理结果
        Page.query(ct005QueryForm, "");
    },

    /**
     * 调用通用查询查询房源数据
     * @param conditionFieldArr 查询字段数组
     * @param conditionArr 查询条件数组
     * @param conditionValueArr 查询值数组
     */
    queryHs: function (obj) {
        var schemeType = $("input[name=schemeType]",navTab.getCurrentPanel()).val();
        Page.queryCondition(obj, {
            changeConditions: ct005.changeConditions,
            showContainer: $("#ct005AdanceSch", navTab.getCurrentPanel()),
            formObj: $("#ct005QueryForm_" + schemeType, navTab.getCurrentPanel()),
            callback: function (showContainer) {
                showContainer.find("div.tip").prepend($('<div class="triangle triangle-left" style="top: 45px; left: -30px;"></div>'));
                showContainer.find("a.js-more").trigger("click");
            }
        });
    },

    /**
     * 改变查询条件
     * @param newCondition
     */
    changeConditions: function (newCondition) {
        var schemeType = $("input[name=schemeType]", navTab.getCurrentPanel()).val();
        var formObj = $("#ct005QueryForm_" + schemeType, navTab.getCurrentPanel());
        $("input[name=conditionName]", formObj).val(newCondition.conditionNames);
        $("input[name=condition]", formObj).val(newCondition.conditions);
        $("input[name=conditionValue]", formObj).val(newCondition.conditionValues);
        $("input.js_conditionValue", formObj).val(newCondition.conditionValueTexts);
        $("input[name=resultField]", formObj).val(newCondition.resultFields);
        // 排序字段
        $("input[name=sortColumn]", formObj).val(newCondition.sortColumn);
        $("input[name=sortOrder]", formObj).val(newCondition.sortOrder);

        // 查询数据
        ct005.queryGjHs();
    },

    /**
     * 检索房产
     * @param obj
     */
    queryGjHs: function () {
        // 查询数据
        var schemeType = $("input[name=schemeType]", navTab.getCurrentPanel()).val();
        var formObj = $("#ct005QueryForm_" + schemeType, navTab.getCurrentPanel());
        // 查询处理结果
        Page.query(formObj, "");
    },


    /**
     * 打开签约界面
     * @param hsId 房屋编号
     * @param hsCtId 协议编号
     * @param schemeType 协议类型
     */
    //ctInfo: function (hsId, hsCtId) {
    //    ct005.ctInfo(hsId, hsCtId, '');
    //},

    /**
     * 显示居民详情信息
     * @param hsId 房屋编号
     * @param hsCtId 协议编号
     * @param opFlag 展示标志
     */
    ctInfo: function (hsId, hsCtId, opFlag) {
        // 预分方案类型
        var schemeType = $("input[name=schemeType]", navTab.getCurrentPanel()).val();
        // 重新加载
        var packet = new AJAXPacket();
        var prjCd = getPrjCd();
        packet.data.add("prjCd", prjCd);
        packet.data.add("hsId", hsId);
        packet.data.add("hsCtId", hsCtId);
        packet.data.add("schemeType", schemeType);
        packet.data.add("opFlag", opFlag);
        packet.url = getGlobalPathRoot() + "eland/ct/ct005/ct005-info.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            $("#ct005HsInfo", navTab.getCurrentPanel()).html(response);
            initUI($("#ct005HsInfo", navTab.getCurrentPanel()));
        });
    },


    /**
     * 生成业务报表
     */
    generateCt: function (hsId, hsCtId) {
        // 预分方案类型
        var schemeType = $("input[name=schemeType]", navTab.getCurrentPanel()).val();
        var url = getGlobalPathRoot() + "eland/ct/ct005/ct005-generateCt.gv?fromOp=view";
        var packet = new AJAXPacket(url);
        packet.data.add("hsId", hsId);
        packet.data.add("hsCtId", hsCtId);
        packet.data.add("schemeType", schemeType);
        packet.data.add("prjCd", getPrjCd());
        core.ajax.sendPacketHtml(packet, function (response) {
                var container = $("#ct005_ReportData", navTab.getCurrentPanel());
                container.html(response);
                container.initUI();
            }
        );
    },

    /**
     * 下载协议
     */
    downloadCt: function (hsId, hsCtId, schemeType) {
        var schemeType = $("input[name=schemeType]", navTab.getCurrentPanel()).val();
        var url = getGlobalPathRoot() + "eland/ct/ct005/ct005-generateCt.gv?fromOp=download"
            + "&hsId=" + hsId
            + "&hsCtId=" + hsCtId
            + "&schemeType=" + schemeType
            + "&prjCd=" + getPrjCd();
        window.open(url);
    },

    /**
     * 确认签约
     * @param hsId
     * @param hsCtId
     */
    cfmCt: function (hsId, hsCtId) {
        // 预分方案类型
        var schemeType = $("input[name=schemeType]", navTab.getCurrentPanel()).val();
        // 提交签约
        var url = getGlobalPathRoot() + "eland/ct/ct005/ct005-cfmCt.gv";
        var packet = new AJAXPacket(url);
        packet.data.add("hsId", hsId);
        packet.data.add("hsCtId", hsCtId);
        packet.data.add("schemeType", schemeType);
        packet.data.add("prjCd", getPrjCd());
        core.ajax.sendPacketHtml(packet, function (response) {
            var data = eval("(" + response + ")");
            var isSuccess = data.success;
            if (isSuccess) {
                alertMsg.correct("确认结算成功！");
                ct005.ctInfo(hsId);
            } else {
                alertMsg.warn(data.errMsg);
            }
        }, true);
        $.pdialog.close($.pdialog._current);
    },

    /**
     * 取消结算
     * @param hsId 房产id
     * @param hsCtId 签约id
     */
    cancelCt: function (hsId, hsCtId) {
        // 预分方案类型
        var schemeType = $("input[name=schemeType]", navTab.getCurrentPanel()).val();
        // 提交取消签约
        alertMsg.confirm("你确定要取消签约？", {
            okCall: function () {
                var url = getGlobalPathRoot() + "eland/ct/ct005/ct005-cancelCt.gv";
                var packet = new AJAXPacket(url);
                packet.data.add("hsId", hsId);
                packet.data.add("hsCtId", hsCtId);
                packet.data.add("schemeType", schemeType);
                packet.data.add("prjCd", getPrjCd());
                core.ajax.sendPacketHtml(packet, function (response) {
                    var data = eval("(" + response + ")");
                    var isSuccess = data.success;
                    if (isSuccess) {
                        alertMsg.correct("取消签约成功！");
                        ct005.ctInfo(hsId, hsCtId);
                    } else {
                        alertMsg.error(data.errMsg);
                    }
                }, true);
            }
        });
    },

    /**
     * 确认打印协议， 或者确认打印回执单。
     * @param printObj 协议 or 回执单
     */
    printCt: function (obj, hsId, printObj, hsCtId) {
        try {
            var iframeDoc = document.getElementById("ct005DocIFrame").contentWindow.document;
            iframeDoc.getElementById("PageOfficeCtrl1").ShowDialog(4);
        } catch (e) {
            alertMsg.error("控件加载失败无法打印,请点击连接查看文档进行打印!");
        }
    },

    /**
     * 拍照
     */
    photograph: function (hsId, hsCtId) {
        ct005.ctInfo(hsId, hsCtId, 'viewFj');
    }

};
$(document).ready(function () {
    // 拖动屏幕切分效果   如果要设置 cookie 在{里编写} cookieName: "cookieName"
    LeftMenu.init("ct005SchDiv", "ct005Context", {});
});
