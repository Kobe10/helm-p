var ct003 = {
    schHs: function () {
        var container = $("#ct003SchDiv", navTab.getCurrentPanel());
        var conditionNameArr = [];
        var conditionArr = [];
        var conditionValueArr = [];
        // 查询条件
        var schType = $("select[name=schType]", container).val();
        var schValue = $("input[name=schValue]", container).val();
        conditionNameArr.push(schType);
        conditionArr.push("like");
        conditionValueArr.push(schValue);
        conditionNameArr.push("HouseInfo.HsCtInfo.ctStatus");
        conditionArr.push("=");
        conditionValueArr.push('2');
        // 打印状态
        var printChHsStatus = [];
        navTab.getCurrentPanel()
            .find(":checkbox[checked][name=printChHsStatus]").each(
            function (i) {
                printChHsStatus.push($(this).val());
            }
        );
        if (printChHsStatus.length > 0) {
            conditionNameArr.push("HouseInfo.HsCtInfo.printChHsStatus");
            conditionArr.push("in");
            conditionValueArr.push(printChHsStatus.join("|"));
        }
        var ct003QueryForm = $("#ct003QueryForm", navTab.getCurrentPanel());
        $("input[name=conditionName]", ct003QueryForm).val(conditionNameArr.join(","));
        $("input[name=condition]", ct003QueryForm).val(conditionArr.join(","));
        $("input[name=conditionValue]", ct003QueryForm).val(conditionValueArr.join(","));
        // 查询处理结果
        Page.query(ct003QueryForm, "");
    },
    viewChHs: function (hsId, hsCtId) {
        var url = getGlobalPathRoot() + "eland/ct/ct003/ct003-infoChHsNum.gv";
        var packet = new AJAXPacket(url);
        packet.data.add("hsId", hsId);
        packet.data.add("hsCtId", hsCtId);
        packet.data.add("prjCd", getPrjCd());
        core.ajax.sendPacketHtml(packet, function (response) {
            $("#ct003Context", navTab.getCurrentPanel()).html(response);
            initUI($("#ct003Context", navTab.getCurrentPanel()));
        }, true);
    },
    /**
     * 生成签约回执单
     * @param formId
     * @param templateName
     */
    generateChHsNum: function (hsId) {
        var url = getGlobalPathRoot() + "eland/ct/ct003/ct003-generateCfmChHsNum.gv";
        var packet = new AJAXPacket(url);
        packet.data.add("hsId", hsId);
        packet.data.add("prjCd", getPrjCd());
        core.ajax.sendPacketHtml(packet, function (response) {
            var container = $("#ct003_ReportData", navTab.getCurrentPanel());
            container.html(response);
            container.initUI();
        }, true);
    },

    /**
     * 下载序号单
     * @param formId
     * @param templateName
     */
    downloadChHsNum: function (hsId) {
        var schemeType = $("input[name=schemeType]", navTab.getCurrentPanel()).val();
        var url = getGlobalPathRoot() + "eland/ct/ct003/ct003-generateCfmChHsNum.gv?prjCd=" + getPrjCd()
            + "&hsId=" + hsId
            + "&schemeType=" + schemeType
            + "&fromOp=download";
        window.open(url);
    },

    /**
     *打印选房序号单
     * @param obj
     * @param hsId
     * @param printObj
     * @param hsCtId
     */
    printCt: function (obj, hsId, printObj, hsCtId) {
        try {
            var iframeDoc = document.getElementById("ct003DocIFrame").contentWindow.document;
            iframeDoc.getElementById("PageOfficeCtrl1").ShowDialog(4);
        } catch (e) {
            //  打印出错不进行处理
        }
        alertMsg.confirm("确认文档已打印？", {
            okCall: function () {
                // 判断是否重新加载页面
                var ctType = $("select[name=ctType]", navTab.getCurrentPanel()).val();
                var url = getGlobalPathRoot() + "eland/ct/ct001/ct001-printCtText.gv";
                var packet = new AJAXPacket(url);
                packet.data.add("printObj", printObj);
                packet.data.add("prjCd", getPrjCd());
                packet.data.add("hsCtId", hsCtId);
                packet.data.add("ctType", ctType);
                core.ajax.sendPacketHtml(packet, function (response) {
                    var data = eval("(" + response + ")");
                    var isSuccess = data.success;
                    if (isSuccess) {
                        ct003.schHs();
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
    LeftMenu.init("ct003SchDiv", "ct003Context", {});

    ct003.schHs();
});