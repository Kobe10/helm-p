var pj008 = {

    /**
     * 房源快速检索对话框打开
     * @param conditionFieldArr 查询字段数组
     * @param conditionArr 查询条件数组
     * @param conditionValueArr 查询值数组
     */
    openQSch: function (clickObj) {
        if (!$(clickObj).hasClass("active")) {
            $("#pj008QSchDialog", navTab.getCurrentPanel()).show().attr("active", "on");
            $("#pj008QSchDialog input:eq(0)", navTab.getCurrentPanel()).focus();
        } else {
            pj008.closeQSch(true);
        }
    },

    /**
     * 执行查询
     * @param frmId
     * @param divId
     */
    queryList: function (frmId, divId) {
        Query.queryList(frmId, divId);
        pj008.closeQSch(true);
    },

    /**
     * 关闭检索
     * @param forceFlag 强制关闭
     */
    closeQSch: function (forceFlag) {
        var close = forceFlag;
        if (!forceFlag) {
            var active = $("#pj008QSchDialog", navTab.getCurrentPanel()).attr("active");
            if ("on" == active) {
                close = false;
            } else {
                close = true;
            }
        }
        if (close) {
            $("#pj008QSchDialog", navTab.getCurrentPanel()).hide();
        }
    },

    /**
     * 修改区域
     * @param obj
     */
    editReg: function (obj) {
        var treeType = $("select[name=regUseType]", navTab.getCurrentPanel()).val();
        if (!treeType) {
            treeType = $("input[name=regUseType]", navTab.getCurrentPanel()).val();
        }
        var url = getGlobalPathRoot() + "eland/pj/pj004/pj004-initTree.gv?treeType=" + treeType
            + "&fromOp=pj008";
        var width = $(obj).width() - 220;
        $(obj).openTip({
            href: url, height: "230",
            width: "200", offsetX: 0, offsetY: 30
        });
    }
};
$(document).ready(function () {
    // 进入自动查询
    $("#schBtn", navTab.getCurrentPanel()).trigger("click");
});
