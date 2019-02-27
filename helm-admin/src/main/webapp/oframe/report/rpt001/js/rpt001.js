/**
 * Created by shfb_wang on 2015/5/17 0017.
 */
var rpt001 = {
    /**
     * 点击左侧报表
     */
    showReport: function (reportName) {
        if (reportName != "#") {
            var rpt001IFrame = $("#rpt001IFrame", navTab.getCurrentPanel());
            rpt001IFrame.attr("src", (getGlobalPathRoot()
                + "oframe/report/rpt001/rpt001-condition.gv?templateName=" + reportName));
        }
    },

    /**
     * 显示和异常报表条件
     */
    showOrHideCond: function () {
        var rpt001IFrame = $("#rpt001IFrame", navTab.getCurrentPanel());
        if (rpt001IFrame.is(":visible")) {
            rpt001IFrame.hide();
        } else {
            rpt001IFrame.show();
        }
    },

    hideCond: function () {
        var rpt001IFrame = $("#rpt001IFrame", navTab.getCurrentPanel());
        rpt001IFrame.hide();
    },
    /**
     * 导出数据
     */
    doExport: function (exportType) {
        var url = getGlobalPathRoot() + "oframe/report/rpt001/rpt001-generateReport.gv";
        var packet = new AJAXPacket(url);
        var iframe = $(window.frames["rpt001IFrame"].document, navTab.getCurrentPanel());
        if (!rpt001.checkCondition(iframe)) {
            return;
        }
        var $form = $("#query", iframe).serializeArray();
        $form.push({name: "exportType", value: exportType});
        $form.push({name: "prjCd", value: getPrjCd()});
        packet.data.data = $form;
        core.ajax.sendPacketHtml(packet, function (response) {
                var data = eval("(" + response + ")");
                var isSuccess = data.success;
                if (isSuccess) {
                    var url = getGlobalPathRoot() + "oframe/common/file/file-down.gv?remoteFile="
                        + encodeURI(encodeURI(data.reportUrl));
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
        var templateNameObj = $("input[name=templateName]", iframeDoc);
        if (templateNameObj.length == 0 || templateNameObj.val() == '') {
            return false;
        }
        $("#query", iframeDoc).find(".required").each(function () {
            var $this = $(this);
            if ("" == $this.val()) {
                $this.addClass("error");
                errorCount++;
            }
        });
        if (errorCount > 0) {
            var rpt001IFrame = $("#rpt001IFrame", navTab.getCurrentPanel());
            if (!rpt001IFrame.is(":visible")) {
                rpt001IFrame.show();
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
        var url = getGlobalPathRoot() + "oframe/report/rpt001/rpt001-generateReport.gv";
        var packet = new AJAXPacket(url);
        var iframe = $(window.frames["rpt001IFrame"].document, navTab.getCurrentPanel());
        if (!rpt001.checkCondition(iframe)) {
            return;
        } else {
            rpt001.hideCond();
        }
        var $form = $("#query", iframe).serializeArray();
        $form.push({name: "exportType", value: "html"});
        $form.push({name: "prjCd", value: getPrjCd()});
        packet.data.data = $form;
        core.ajax.sendPacketHtml(packet, function (response) {
                var data = eval("(" + response + ")");
                var isSuccess = data.success;
                if (isSuccess) {
                    // 控制按钮展示
                    if (data.matchStr == '.xlsx'){
                        $(".excel_hidden", navTab.getCurrentPanel()).hide();
                    } else {
                        $(".excel_hidden", navTab.getCurrentPanel()).show();
                    }
                    $("#rpt001_ReportData", navTab.getCurrentPanel()).attr("src", getGlobalPathRoot() + data.reportUrl);
                }
                else {
                    alertMsg.error(data.errMsg);
                }
            }
        );
    }
};
$(document).ready(function () {
    // 拖动屏幕切分效果   如果要设置 cookie 在{里编写} cookieName: "cookieName"
    LeftMenu.init("rpt001DivTree", "rpt001RightDiv", {});

    /**
     * 菜单项目点击触发其他项目取消选中效果
     */
    $(".accordion_menu li", navTab.getCurrentPanel()).bind("click", function () {
        $(".accordion_menu li", navTab.getCurrentPanel()).removeClass("selected");
        $(this).addClass("selected");
    });

    /**
     * 点击第一个项目
     */
    $(".accordion_menu li:eq(0)", navTab.getCurrentPanel()).trigger("click");
});