var ct004 = {


    /**
     * 检索房产
     * @param obj
     */
    schHs: function () {
        var container = $("#ct004SchDiv", navTab.getCurrentPanel());
        var conditionNameArr = [];
        var conditionArr = [];
        var conditionValueArr = [];
        // 查询条件
        var schType = $("select[name=schType]", container).val();
        var schValue = $("input[name=schValue]", container).val();
        conditionNameArr.push(schType);
        conditionArr.push("like");
        conditionValueArr.push(schValue);
        // 签约状态
        var ctStatus = [];
        navTab.getCurrentPanel()
            .find(":checkbox[checked][name=ct004ContractStatus]").each(
            function (i) {
                ctStatus.push($(this).val());
            }
        );
        if (ctStatus.length > 0) {
            conditionNameArr.push("HouseInfo.HsCtInfo.printCtStatus");
            conditionArr.push("in");
            conditionValueArr.push(ctStatus.join("|"));
        }
        conditionNameArr.push("HouseInfo.hsStatus");
        conditionArr.push("=");
        conditionValueArr.push("102");
        var ct004QueryForm = $("#ct004QueryForm", navTab.getCurrentPanel());
        $("input[name=conditionName]", ct004QueryForm).val(conditionNameArr.join(","));
        $("input[name=condition]", ct004QueryForm).val(conditionArr.join(","));
        $("input[name=conditionValue]", ct004QueryForm).val(conditionValueArr.join(","));
        // 查询处理结果
        Page.query(ct004QueryForm, "");
    },

    /**
     * 签约信息
     * @param hsId
     */
    ctInfo: function (hsId) {
        // 重新加载
        var packet = new AJAXPacket();
        var prjCd = getPrjCd();
        packet.data.add("prjCd", prjCd);
        packet.data.add("hsId", hsId);
        packet.url = getGlobalPathRoot() + "eland/ct/ct004/ct004-info.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            $("#ct004Context", navTab.getCurrentPanel()).html(response);
            initUI($("#ct004Context", navTab.getCurrentPanel()));
        });
    },

    /**
     * 生成业务报表
     * @param formId
     * @param templateName
     */
    generateCt: function (hsId, hsCtId) {
        //判断是否是多产权人签约
        var $thisTr = $("#ct00401_" + hsCtId, navTab.getCurrentPanel());
        var ctType = $("select[name=ctType]", $thisTr).val();
        var url = getGlobalPathRoot() + "eland/ct/ct004/ct004-genMainCtDoc.gv?fromOp=view";
        var packet = new AJAXPacket(url);
        packet.data.add("hsId", hsId);
        packet.data.add("hsCtId", hsCtId);
        packet.data.add("prjCd", getPrjCd());
        core.ajax.sendPacketHtml(packet, function (response) {
                var container = $("#ct004_ReportData", navTab.getCurrentPanel());
                container.html(response);
                container.initUI();
            }
        );
    },

    /**
     * 未签约切换安置方式
     * @param clickObj 点击对象
     */
    sltChgCtType: function (clickObj) {
        //判断是否是多产权人签约
        var $thisTr = $(clickObj).closest("tr");
        var ctType = $("select[name=ctType]", $thisTr).val();
        var hsCtId = $("input[name=hsCtId]", $thisTr).val();
        var hsId = $("input[name=hsId]", $thisTr).val();
        var url = getGlobalPathRoot() + "eland/ct/ct001/ct001-changeCtType.gv";
        var packet = new AJAXPacket(url);
        packet.data.add("hsId", hsId);
        packet.data.add("hsCtId", hsCtId);
        packet.data.add("prjCd", getPrjCd());
        packet.data.add("ctType", ctType);
        core.ajax.sendPacketHtml(packet, function (response) {
                var data = eval("(" + response + ")");
                var isSuccess = data.success;
                if (!isSuccess) {
                    $("select[name=ctType]", $thisTr).val("");
                    alertMsg.error(data.errMsg);
                } else {
                    ct004.generateCt(hsId, hsCtId);
                }
            }
        );
    },

    /**
     * 导出安置协议
     */
    downloadDoc: function (hsId, hsCtId) {
        //判断是否是多产权人签约
        if (hsCtId == "") {
            var $thisTr = $("#ct00401_" + hsCtId, navTab.getCurrentPanel());
            var ctType = $("select[name=ctType]", $thisTr).val();
            hsCtId = $("input[name=hsCtId]", $thisTr).val();
            hsId = $("input[name=hsId]", $thisTr).val();
        } else {
            ctType = $("select[name=ctType]", navTab.getCurrentPanel()).val();
        }
        // 提交页面数据
        var url = getGlobalPathRoot() + "eland/ct/ct004/ct004-genMainCtDoc.gv?fromOp=download&hsId=" + hsId
            + "&hsCtId=" + hsCtId + "&prjCd=" + getPrjCd();
        window.open(url);
    },


    /**
     * 确认打印协议， 或者确认打印回执单。
     * @param printObj 协议 or 回执单
     */
    printCt: function (hsId, hsCtId, printObj) {
        try {
            var iframeDoc = document.getElementById("ct001DocIFrame").contentWindow.document;
            iframeDoc.getElementById("PageOfficeCtrl1").ShowDialog(4);
        } catch (e) {
            //  打印出错不进行处理
        }
        alertMsg.confirm("确认文档已打印？", {
            okCall: function () {
                // 判断是否是多产权人签约
                var $thisTr = $("#ct00401_" + hsCtId, navTab.getCurrentPanel());
                if (hsCtId == "") {
                    var ctType = $("select[name=ctType]", $thisTr).val();
                    hsCtId = $("input[name=hsCtId]", $thisTr).val();
                } else {
                    ctType = $("select[name=ctType]", navTab.getCurrentPanel()).val();
                }
                var url = getGlobalPathRoot() + "eland/ct/ct004/ct004-printCtText.gv";
                var packet = new AJAXPacket(url);
                packet.data.add("printObj", printObj);
                packet.data.add("prjCd", getPrjCd());
                packet.data.add("hsCtId", hsCtId);
                packet.data.add("ctType", ctType);
                core.ajax.sendPacketHtml(packet, function (response) {
                    var data = eval("(" + response + ")");
                    var isSuccess = data.success;
                    if (isSuccess) {
                    } else {
                        alertMsg.error(data.errMsg);
                    }
                }, true);
            }
        });
    }
};

$(document).ready(function () {
    // 拖动屏幕切分效果   如果要设置 cookie 在{里编写} cookieName: "cookieName"
    LeftMenu.init("ct004SchDiv", "ct004Context", {});
});
