var ph003 = {

    /**
     * 打开居民基本信息
     * @param hsId
     */
    openHs: function (hsId) {
        var url = "eland/ph/ph003/ph003-init.gv?hsId=" + hsId;
        index.quickOpen(url, {opCode: "ph003-init", opName: "居民详情", fresh: true});
    },

    openBuild: function (buildId) {
        var url = "eland/ph/ph001/ph001-init.gv?buildId=" + buildId;
        index.quickOpen(url, {buldId: buildId}, {opCode: "ph001-init", opName: "居民信息", fresh: true});
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
    jumpYard: function () {
        var buildId = $("input[name=buildId]", navTab.getCurrentPanel()).val();
        index.quickOpen('eland/ph/ph002/ph002-init.gv?buldId=' + buildId, {
            opCode: "ph002-init",
            opName: "院落信息",
            fresh: true
        });
    },

    /**
     * 跳转到院落简介页面
     */
    jumpRegInfo: function (regId) {
        var url = "eland/ph/ph001/ph001-init.gv?regId=" + regId;
        index.quickOpen(url, {opCode: "ph001-init", opName: "居民信息"});
    },
    /**
     * 编辑签约信息
     * @param obj 点击对象
     * @param hsId 房屋编号
     * @param buildId 签约编号
     */
    editCtInfo: function (obj, hsId, hsCtId) {
        //替换整个面板div
        var _this = $(obj);
        var urlData = "eland/ph/ph003/ph003-initTt.gv?method=edit&hsId=" + hsId
            + "&hsCtId=" + hsCtId
            + "&prjCd=" + getPrjCd();
        index.loadInfoPanelByInnerObj(obj, urlData);
    },

    /**
     * 编辑签约信息
     * @param obj 点击对象
     * @param hsId 房屋编号
     * @param buildId 签约编号
     */
    editPG: function (obj, hsId, hsCtId) {
        //替换整个面板div
        var _this = $(obj);
        var urlData = "eland/ph/ph003/ph003-initPG.gv?method=edit&hsId=" + hsId
            + "&prjCd=" + getPrjCd();
        index.loadInfoPanelByInnerObj(obj, urlData);
    },

    /*新增建筑*/
    editHouse: function (hsId) {
        var url = "eland/ph/ph003/ph00301-initHs.gv?hsId=" + hsId;
        index.quickOpen(url, {opCode: "ph00301-initHs", opName: "信息登记"});
    },

    //生成协议文档
    generateDoc: function (hsId, hsCtId, ctType) {
        // 提交页面数据
        var url = getGlobalPathRoot() + "eland/ct/ct001/ct001-generateCt.gv?fromOp=download&hsId=" + hsId + "&prjCd=" + getPrjCd() + "&hsCtId=" + hsCtId + "&ctType=" + ctType;
        window.open(url);
    }
};

$(document).ready(function () {
    //绑定刷新按钮
    $("div.panelContainer", navTab.getCurrentPanel()).delegate("span.js_reload", "click", function () {
        var _this = $(this);
        var panelContainer = _this.parents("div.panelContainer");
        index.loadInfoPanel($(panelContainer).attr("panelKey"), $(panelContainer).attr("url"))
    });
    // 触发加载
    $("span.js_reload", navTab.getCurrentPanel()).trigger("click");
})
;
