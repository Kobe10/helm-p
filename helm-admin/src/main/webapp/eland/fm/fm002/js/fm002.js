/**
 * 付款计划管理
 * Created by shfb_linql on 16/4/20.
 */
var fm002 = {
    init: function (pId) {
        var url = "eland/fm/fm002/fm002-init.gv";
        if (pId) {
            url = "eland/fm/fm002/fm002-init.gv?pId=" + pId;
        }
        index.quickOpen(url, {opCode: "fm002-init", opName: "付款计划"});
    },
    /**
     * 查看付款计划
     * @param schemeId
     * @param obj
     */
    info: function (pId, obj) {
        if (!pId) {
            pId = "";
        } else {
            if (obj) {
                obj = $("#" + pId, navTab.getCurrentPanel());
            }
        }
        $(obj).addClass("selected").siblings("li").removeClass("selected");
        var url = getGlobalPathRoot() + "eland/fm/fm002/fm002-info.gv";
        var packet = new AJAXPacket(url);
        packet.data.add("prjCd", getPrjCd());
        packet.data.add("pId", pId);
        core.ajax.sendPacketHtml(packet, function (response) {
            $("#fm002Context", navTab.getCurrentPanel()).html(response);
            initUI($("#fm002Context", navTab.getCurrentPanel()));
        }, true);
    },

    /**
     * 保存付款计划
     */
    savePlan: function (pId) {
        var codeText = window.frames['fm002IFrameRule'].getCode();
        $("textarea[name=pPreRule]", navTab.getCurrentPanel()).val(codeText);
        var codeText = window.frames['fm002IFrameMoney'].getCode();
        $("textarea[name=pCalcMoney]", navTab.getCurrentPanel()).val(codeText);
        var pCode = $("input[name=pCode]", navTab.getCurrentPanel()).val();
        var pFormCd = $("input[name=pFormCd]", navTab.getCurrentPanel()).val();
        var pStatus = $("select[name=pStatus]", navTab.getCurrentPanel()).val();
        var pCtSchemeIds = [];
        $("#fm002Form", navTab.getCurrentPanel()).find(":checkbox[checked][name=pCtSchemeId]").each(
            function (i) {
                var temp = $(this).val();
                pCtSchemeIds.push(temp);
            }
        );
        var fm002Form = $("#fm002Form", navTab.getCurrentPanel());
        if (fm002Form.valid()) {
            var url = getGlobalPathRoot() + "eland/fm/fm002/fm002-savePlan.gv";
            var packet = new AJAXPacket(url);
            packet.data.data = fm002Form.serializeArray();
            packet.data.data.push({name: "prjCd", value: getPrjCd()});
            packet.data.data.push({name: "pCode", value: pCode});
            packet.data.data.push({name: "pFormCd", value: pFormCd});
            packet.data.data.push({name: "pStatus", value: pStatus});
            packet.data.data.push({name: "pCtSchemeIds", value: pCtSchemeIds.toString()});
            packet.data.data.push({name: "pId", value: pId});

            core.ajax.sendPacketHtml(packet, function (response) {
                var jsonData = eval("(" + response + ")");
                if (jsonData.success) {
                    alertMsg.correct("保存成功！");
                    fm002.init(jsonData.pId);
                } else {
                    alertMsg.error(jsonData.errMsg);
                }
            }, true);
        }
    },

    /**
     *获取iframe中的信息
     * @returns {*|jQuery}
     */
    getCode: function (flag) {
        if(flag == 'rule'){
            return $("textarea[name=pPreRule]", navTab.getCurrentPanel()).val();
        }else {
            return $("textarea[name=pCalcMoney]", navTab.getCurrentPanel()).val();
        }
    },

    /**
     * 删除方案
     */
    delPlan: function (pId) {
        alertMsg.confirm("确认删除该方案？", {
            okCall: function () {
                var url = getGlobalPathRoot() + "eland/fm/fm002/fm002-delPlan.gv";
                var packet = new AJAXPacket(url);
                packet.data.add("prjCd", getPrjCd());
                packet.data.add("pId", pId);
                core.ajax.sendPacketHtml(packet, function (response) {
                    var jsonData = eval("(" + response + ")");
                    if (jsonData.success) {
                        alertMsg.correct("删除成功！");
                        fm002.init(jsonData.pId);
                    } else {
                        alertMsg.error(jsonData.errMsg);
                    }
                }, true);
            }
        });
    }
};

$(document).ready(function () {
    // 拖动屏幕切分效果   如果要设置 cookie 在{里编写} cookieName: "cookieName"
    LeftMenu.init("fm002LeftDiv", "fm002Context", {});
});