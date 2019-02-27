/**
 * Created by shfb_wang on 2015/3/6.
 */
var oh002 = {
    /**
     *  显示或隐藏目录
     */
    extendOrClosePanel: function (clickObj) {
        var $pucker = $(clickObj);
        var $content = $pucker.closest("div.panelHeader").next("div.panelContent");
        if ($pucker.hasClass("collapsable")) {
            $pucker.removeClass("collapsable").addClass("expandable");
            $content.hide();
        } else {
            $pucker.removeClass("expandable").addClass("collapsable");
            $content.show();
        }
    },

    /**
     * 获取地址列表
     * @param obj
     * @returns {string}
     */
    getAddrUrl: function (obj) {
        return getGlobalPathRoot() + "eland/oh/oh002/oh002-queryNewHsAddr.gv?prjCd=" + getPrjCd();
    },

    /**
     * 自动填充地址信息
     * @param obj
     * @returns {String} hsFullAddr
     */
    getAddrOpt: function (obj) {
        return {
            processData: function (result) {
                var myData = [];
                if (result) {
                    for (i = 0; i < result.length; i++) {
                        var dataRow = {};
                        dataRow.data = result[i];
                        dataRow.value = result[i].hsAddr;
                        myData.push(dataRow);
                    }
                }
                return myData;
            },
            showResult: function (value, data) {
                return data.hsAddr;
            },
            displayValue: function (value, data) {
                return data.hsAddr;
            },
            dyExtraParams: function (obj) {
                var $table = $(obj).closest("table");
                return {
                    hsAddr: $table.find("input[name=hsAddr]").val(),
                    hsHxTp: $table.find("input[name=hsHxTp]").val(),
                    regId: $table.find("input[name=hiddenRegId]").val(),
                    prjCd: getPrjCd()
                }
            },
            onItemSelect: function (obj) {
                var source = $(obj.source);
                var table = source.closest("table.everyChHsTable");
                table.find("input.js_newHsId").val(obj.data.newHsId);
                table.find("input.js_live_addr").val(obj.data.hsAddr);
                table.find("span.js_hsHxName").text("(" + obj.data.hsHxName + ")");
                table.find("input.js_hsUnPrice").val(obj.data.hsUnPrice);
                table.find("input.js_allPayMoney").val(obj.data.hsSalePrice);
                if (obj.data.hsBldSize) {
                    table.find("input.js_realSize").val(obj.data.hsBldSize);
                } else {
                    table.find("input.js_realSize").val(obj.data.preBldSize);
                }
            },
            onNoMatch: function (obj) {
                var source = $(obj.source);
                var table = source.closest("table.everyChHsTable");
                table.find("input.js_live_addr").val("");
                table.find("span.js_hsHxName").text("");
                table.find("input.js_newHsId").val("");
                table.find("input.js_hsUnPrice").val("");
                table.find("input.js_realSize").val("");
                table.find("input.js_chooseDate").val("");
                table.find("input.js_allPayMoney").val("");
            },
            remoteDataType: "json",
            delay: 300,
            minChars: 0,
            queryParamName: "hsAddr",
            filterResults: true
        }
    },

    //确认选房 保存各项数据
    cfmHsChoose: function (obj) {
        var $this = $(obj);
        var thisDiv = $this.closest("div.js_div");
        var url = getGlobalPathRoot() + "eland/oh/oh002/oh002-cfmHsChoose.gv";

        var chooseHsAddr = thisDiv.find("input[name=hsAddr]").val();
        var newHsId = thisDiv.find("input[name=newHsId]").val();
        var oldNewHsId = thisDiv.find("input[name=oldNewHsId]").val();
        var hsCtId = thisDiv.find("input[name=hsCtId]").val();
        var hsCtChooseId = thisDiv.find("input[name=hsCtChooseId]").val();
        var hsUnPrice = thisDiv.find("input.js_hsUnPrice").val();
        var realSize = thisDiv.find("input.js_realSize").val();
        var allPayMoney = (parseFloat(hsUnPrice) * parseFloat(realSize)).toFixed(2);  //特殊处理，
        var chooseDate = thisDiv.find("input.js_chooseDate").val();
        var hsHxName = thisDiv.find("input.js_hsHxName").val();

        var packet = new AJAXPacket(url);
        packet.data.add("newHsId", newHsId);
        packet.data.add("oldNewHsId", oldNewHsId);
        packet.data.add("hsCtId", hsCtId);
        packet.data.add("hsCtChooseId", hsCtChooseId);
        packet.data.add("hsUnPrice", hsUnPrice);
        packet.data.add("realSize", realSize);
        packet.data.add("allPayMoney", allPayMoney);
        packet.data.add("chooseDate", chooseDate);
        packet.data.add("hsHxName", hsHxName);
        packet.data.add("chooseHsAddr", chooseHsAddr);
        packet.data.add("prjCd", getPrjCd());
        if (chooseHsAddr) {
            core.ajax.sendPacketHtml(packet, function (response) {
                var jsonData = eval("(" + response + ")");
                // 服务调用是否成功
                var isSuccess = jsonData.success;
                if (isSuccess) {
                    alertMsg.correct("执行成功！");
                    //选房成功改变页面样子
                    thisDiv.find("input.js_hsUnPrice").addClass("readonly").attr("readonly", "readonly").attr("disabled", "disabled");
                    thisDiv.find("input.js_realSize").addClass("readonly").attr("readonly", "readonly").attr("disabled", "disabled");
                    thisDiv.find("input.js_allPayMoney").addClass("readonly").attr("readonly", "readonly").attr("disabled", "disabled");
                    thisDiv.find("input.js_chooseDate").addClass("readonly").attr("readonly", "readonly").attr("disabled", "disabled");
                    thisDiv.find("input.js_live_addr").addClass("readonly").attr("readonly", "readonly").attr("disabled", "disabled");
                    //隐藏按钮
                    thisDiv.find("span.js_cfmChooseBtn").hide();
                    thisDiv.find("span.js_cancelChooseBtn").show();
                } else {
                    alertMsg.warn(jsonData.errMsg);
                }
            });
        } else {
            alertMsg.warn("请选房再提交！");
        }
    },

    //取消选房
    cancelHsChoose: function (obj) {
        var $this = $(obj);
        var thisDiv = $this.closest("div.js_panel_context");
        var hsCtChooseId = thisDiv.find("input[name=hsCtChooseId]").val();
        var hsCtId = thisDiv.find("input[name=hsCtId]").val();
        if (hsCtChooseId) {
            alertMsg.confirm("确认取消选房吗？", {
                okCall: function () {
                    var url = getGlobalPathRoot() + "eland/oh/oh002/oh002-cancelHsChoose.gv";
                    var packet = new AJAXPacket(url);
                    packet.data.add("hsCtChooseId", hsCtChooseId);
                    packet.data.add("hsCtId", hsCtId);
                    packet.data.add("prjCd", getPrjCd());
                    core.ajax.sendPacketHtml(packet, function (response) {
                        var jsonData = eval("(" + response + ")");
                        // 服务调用是否成功
                        var isSuccess = jsonData.success;
                        if (isSuccess) {
                            alertMsg.correct("执行成功！");

                            //选房成功改变页面样子
                            thisDiv.find("input.js_hsUnPrice").removeClass("readonly").removeAttr("readonly").val("").removeAttr("disabled");
                            thisDiv.find("input.js_realSize").removeClass("readonly").removeAttr("readonly").val("").removeAttr("disabled");
                            thisDiv.find("input.js_chooseDate").removeClass("readonly").removeAttr("readonly").val("").removeAttr("disabled");
                            thisDiv.find("input.js_live_addr").removeClass("readonly").removeAttr("readonly").val("").removeAttr("disabled");
                            thisDiv.find("span.js_hsHxName").text("");
                            thisDiv.find("input.js_allPayMoney").val("");
                            //隐藏按钮
                            thisDiv.find("span.js_cfmChooseBtn").show();
                            thisDiv.find("span.js_cancelChooseBtn").hide();
                        } else {
                            alertMsg.warn(jsonData.errMsg);
                        }
                    });
                }
            });
        } else {

        }
    },

    //自动计算总购房款
    calcAllMoney: function (obj) {
        var $table = $(obj).closest("table");
        //判断有没有填写总购房款，
        var allMoney = $table.find("input.js_allPayMoney").val();
        var hsUnPrice = $table.find("input.js_hsUnPrice").val();
        var realSize = $table.find("input.js_realSize").val();
        $table.find("input.js_allPayMoney").val((parseFloat(hsUnPrice) * parseFloat(realSize)).toFixed(2));
    }
};

$(document).ready(function () {

});