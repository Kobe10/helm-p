var pb002 = {

    openBuild: function (buildId) {
        var url = "eland/pb/pb002/pb002-init.gv?buldId=" + buildId;
        index.quickOpen(url, {opName: "院落详情", opCode: "pb002-init", fresh: true});
    },

    showHideMenu: function (clickObj) {
        var pageNavMenu = $("#pageNavMenu");
        if (pageNavMenu.hasClass("hidden")) {
            pageNavMenu.removeClass("hidden");
            $(clickObj).addClass("disabled");
        } else {
            pageNavMenu.addClass("hidden");
            $(clickObj).removeClass("disabled");
        }
    },

    /*新增建筑*/
    editHouse: function (hsId) {
        var url = "eland/ph/ph003/ph00301-initS.gv?hsId=" + hsId;
        index.openTab(null, "ph00301-initS", url, {title: "信息登记", fresh: true});
    },
    /**
     * 打开计算器
     */
    openCalculate: function (hsId) {
        // 保存返回
        var url = getGlobalPathRoot() + "eland/ph/ph004/ph004-init.gv?containType=dialog&hsId=" + hsId
            + "&prjCd=" + getPrjCd();
        $.pdialog.open(url, "ph004", "补偿计算器", {
            max: true, mask: true
        });
    },

    /**
     * 跳转到院落列表页面
     */
    jumpRegList: function (regId) {
        var url = "eland/pb/pb001/pb001-init.gv?regId=" + regId;
        index.quickOpen(url, {opCode: "pb001-init", opName: "院落信息", fresh: true});
    },

    /**
     * 加载tab页的内容
     * @param obj
     */
    loadTabContext: function (obj) {
        var _this = $(obj);
        var url = _this.attr("url");
        var urlData = getGlobalPathRoot() + encodeURI(encodeURI(url));
        var contextDiv = _this.closest("div.tabs").find("div.tabsContent").find(">div:eq(" + _this.index() + ")");
        if (contextDiv.html() == "") {
            var packet = new AJAXPacket(urlData);
            packet.noCover = true;
            packet.data.add("prjCd", getPrjCd());
            core.ajax.sendPacketHtml(packet, function (response) {
                contextDiv.html(response);
                initUI(contextDiv);
            })
        }
    }
};

$(document).ready(function () {
    //绑定刷新按钮
    $("div.panelContainer", navTab.getCurrentPanel()).delegate("span.js_reload", "click", function () {
        var _this = $(this);
        var panelContainer = _this.parents("div.panelContainer");
        index.loadInfoPanel($(panelContainer).attr("panelKey"), $(panelContainer).attr("url"))
    });
    $("li.js_load_tab.selected", navTab.getCurrentPanel()).trigger("click");
    // 触发加载
    $("span.js_reload", navTab.getCurrentPanel()).trigger("click");
})
;
