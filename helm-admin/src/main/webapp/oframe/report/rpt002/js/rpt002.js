/**
 * Created by shfb_wang on 2015/5/17 0017.
 */
var rpt002 = {
    showOrHideCond: function () {
        var rpt002IFrame = $("#rpt002IFrame", navTab.getCurrentPanel());
        if (rpt002IFrame.is(":visible")) {
            rpt002IFrame.hide();
        } else {
            rpt002IFrame.show();
        }
    },

    hideCond: function () {
        var rpt002IFrame = $("#rpt002IFrame", navTab.getCurrentPanel());
        rpt002IFrame.hide();
    },
    /**
     * 导出数据
     */
    doExport: function () {
        var exportType = $("input[name=exportType]:checked", navTab.getCurrentPanel()).val();
        var url = getGlobalPathRoot() + "oframe/report/rpt002/rpt002-generateReport.gv";
        var packet = new AJAXPacket(url);
        var iframe = $(window.frames["rpt002IFrame"].document, navTab.getCurrentPanel());
        if (!rpt002.checkCondition(iframe)) {
            return;
        }
        var $form = $("#query", iframe).serializeArray();
        $form.push({name: "exportType", value: exportType})
        packet.data.data = $form;
        core.ajax.sendPacketHtml(packet, function (response) {
                var data = eval("(" + response + ")");
                var isSuccess = data.success;
                if (isSuccess) {
                    var url = getGlobalPathRoot() + data.reportUrl;
                    window.open(url, "_blank");
                } else {
                    alert(data.errMsg);
                }
            }
        );
    },

    /**
     * 验证
     * @param iframeDoc
     * @returns {boolean}
     */
    checkCondition: function (iframeDoc) {
        var errorCount = 0;
        $("#query", iframeDoc).find(".required").each(function () {
            var $this = $(this);
            if ("" == $this.val()) {
                $this.addClass("error");
                errorCount++;
            }
        });
        if (errorCount > 0) {
            var rpt002IFrame = $("#rpt002IFrame", navTab.getCurrentPanel());
            if (!rpt002IFrame.is(":visible")) {
                rpt002IFrame.show();
            }
            alert("报表参数中红色标记条件必须填写");
            return false;
        }
        return true;
    },
    /**
     * 生成业务报表
     * @param formId
     * @param templateName
     */
    generateReport: function () {
        var url = getGlobalPathRoot() + "oframe/report/rpt002/rpt002-generateReport.gv";
        var packet = new AJAXPacket(url);
        var iframe = $(window.frames["rpt002IFrame"].document, navTab.getCurrentPanel());
        if (!rpt002.checkCondition(iframe)) {
            return;
        } else {
            rpt002.hideCond();
        }
        var $form = $("#query", iframe).serializeArray();
        packet.data.data = $form;
        core.ajax.sendPacketHtml(packet, function (response) {
                var data = eval("(" + response + ")");
                var isSuccess = data.success;
                if (isSuccess) {
                    var param = {
                        url: getGlobalPathRoot() + data.reportUrl,
                        pdfOpenParams: {
                            view: "FitH,top"
                        }
                    };
                    var myPDF = new PDFObject(param).embed("rpt002_ReportData");
                }
                else {
                    alertMsg.error(data.errMsg);
                }
            }
        );
    }
};
$(document).ready(function () {
    rpt002.hideCond();
});