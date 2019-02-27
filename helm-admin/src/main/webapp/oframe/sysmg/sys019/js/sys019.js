/**
 * Created by Administrator on 2015/11/10.
 */
sys019 = {
    uploadObj: null,
    /**
     * 打开快速检索对话框
     */
    openQSch: function (obj) {
        if (!$(obj).hasClass("active")) {
            $("#sys019create", navTab.getCurrentPanel()).show().attr("active", "on");
            $("#sys019create input:eq(0)", navTab.getCurrentPanel()).focus();
        }
    },

    /**
     * 关闭检索
     */
    closeQSch: function () {
        $("#sys019create", navTab.getCurrentPanel()).hide();
    },

    /**
     *快速查询
     */
    queryKs: function () {
        var dialogObj = $("#sys019form", navTab.getCurrentPanel());
        var messageDefType = $("input[name=messageDefType]", navTab.getCurrentPanel()).val();
        var consumerName = $("input[name=consumerName]", navTab.getCurrentPanel()).val();
        var producerName = $("input[name=producerName]", navTab.getCurrentPanel()).val();
        var url = getGlobalPathRoot() + "oframe/sysmg/sys019/sys019-queryKs.gv";
        var packet = new AJAXPacket(url);
        packet.data.add("messageDefType", messageDefType);
        packet.data.add("consumerName", consumerName);
        packet.data.add("producerName", producerName);
        packet.data.add("prjCd", getPrjCd());
        core.ajax.sendPacketHtml(packet, function (response) {
            var resultDiv = dialogObj.next("div.js_page");
            if (resultDiv.length == 0) {
                resultDiv = $("<div class='js_page'></div>");
                dialogObj.after(resultDiv);
            }
            var divOver = resultDiv.css("overflow");
            resultDiv.css("overflow", "hidden");
            resultDiv.html(response);
            resultDiv.initUI();
            resultDiv.css("overflow", divOver);
            sys019.query();
        }, true);
        packet = null;
    },

    /**
     *通用查询
     */
    query: function () {
        var formObj = $("#sys019form", navTab.getCurrentPanel());
        // 获取输入框
        var dialogObj = $("#sys019create", navTab.getCurrentPanel());
        // 覆盖的查询条件
        var coverConditionName = [];
        var coverCondition = [];
        var coverConditionValue = [];
        var coverNames = [];
        // 循环当前查询条件
        $("input", dialogObj).each(function () {
            var $this = $(this);
            var attrName = $this.attr("name");
            var tagName = $this[0].tagName;
            var condition = $this.attr("condition");
            var value = $this.val();
            if ("INPUT" == tagName && condition) {
                coverNames.push(attrName);
                if (value != "") {
                    coverConditionName.push(attrName);
                    coverCondition.push(condition);
                    coverConditionValue.push(value);
                }
            }
        });
        // 获取历史查询条件
        var oldConditionName = $("input[name=conditionName]", formObj).val().split(",");
        var oldCondition = $("input[name=condition]", formObj).val().split(",");
        var oldConditionValue = $("input[name=conditionValue]", formObj).val().split(",");

        // 实际使用的查询条件
        var newConditionName = [];
        var newCondition = [];
        var newConditionValue = [];

        // 处理历史查询条件，存在覆盖则删除原有的查询条件
        for (var i = 0; i < oldConditionName.length; i++) {
            var conditionName = oldConditionName[i];
            var found = false;
            for (var j = 0; j < coverNames.length; j++) {
                if (conditionName == coverNames[j]) {
                    found = true;
                    break;
                }
            }
            // 未找到匹配的条件
            if (!found) {
                newConditionName.push(oldConditionName[i]);
                newCondition.push(oldCondition[i]);
                newConditionValue.push(oldConditionValue[i]);
            }
        }

        // 追加新的查询条件
        newConditionName = newConditionName.concat(coverConditionName);
        newCondition = newCondition.concat(coverCondition);
        newConditionValue = newConditionValue.concat(coverConditionValue);
        // 重新设置查询条件
        $("input[name=conditionName]", formObj).val(newConditionName.join(","));
        $("input[name=condition]", formObj).val(newCondition.join(","));
        $("input[name=conditionValue]", formObj).val(newConditionValue.join(","));
        // 再次执行查询
        Page.query($("#sys019form", navTab.getCurrentPanel()), "");
    },

    /**
     * 打开添加页面
     */
    editView: function (messageDefId, obj) {
        var temp = $(obj).closest("tr");
        var oprjCd = $("input[name=prjCd]", temp).val();
        var url = getGlobalPathRoot() + "oframe/sysmg/sys019/sys019-editView.gv?messageDefId=" + messageDefId + "&prjCd=" + getPrjCd() + "&oprjCd=" + oprjCd;
        $.pdialog.open(url, "sys019", "消息编辑",
            {mask: true, max: true, maxable: true, resizable: true, width: 800, height: 480}
        );
        stopEvent(event);
    },

    /**
     * 打开发消息界面
     */
    sendView: function (messageDefId, obj) {
        //var temp = $(obj).closest("tr");
        var url = getGlobalPathRoot() + "oframe/sysmg/sys019/sys019-sendView.gv?messageDefId=" + messageDefId + "&prjCd=" + getPrjCd();
        $.pdialog.open(url, "sys019SendView", "发送消息",
            {mask: true, width: 800, height: 400, resizable: true}
        )
    },

    /**
     * 触发消息
     */
    sendViewMessage: function (messageDefId) {
        var url = getGlobalPathRoot() + "oframe/sysmg/sys019/sys019-sendViewMessage.gv";
        var packet = new AJAXPacket(url);
        packet.data.add("messageDefId", messageDefId);
        packet.data.add("message", $("textarea[name=message]", $.pdialog.getCurrent()).val());
        packet.data.add("prjCd", getPrjCd());
        core.ajax.sendPacketHtml(packet, function (response) {
            var data = eval("(" + response + ")");
            var isSuccess = data.success;
            if (isSuccess) {
                alertMsg.correct("发送成功");
                $.pdialog.closeCurrent();
            } else {
                alertMsg.warn(data.errMsg);
            }
        });
    },

    /**
     *单条删除
     */
    deleteView: function (messageDefId) {
        alertMsg.confirm("您确定要删除记录吗?", {
            okCall: function () {
                var url = getGlobalPathRoot() + "oframe/sysmg/sys019/sys019-deleteView.gv";
                var ajaxPacket = new AJAXPacket(url);
                ajaxPacket.data.add("messageDefId", messageDefId);
                core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                    var jsonData = eval("(" + response + ")");
                    // 服务调用是否成功
                    var isSuccess = jsonData.success;
                    if (isSuccess) {
                        alertMsg.correct("处理成功");
                        sys019.query();
                    } else {
                        alertMsg.error(jsonData.errMsg);
                    }
                });
            }
        });
        stopEvent(event);
    },

    /**
     * 添加
     */
    editMsg: function () {
        var codeText = window.frames['sys019IFrame'].getCode();
        $("textarea[name=messageDefContent]", $.pdialog.getCurrent()).val(codeText);
        var prjCd = $("select[name=prjCd]", $.pdialog.getCurrent()).val();
        var messageDefType = $("select[name=messageDefType]", $.pdialog.getCurrent()).val();
        var messageDefId = $("input[name=messageDefId]", $.pdialog.getCurrent()).val();
        //from提交
        var $form = $("#sys019MsgForm", $.pdialog.getCurrent());

        if ($form.valid()) {
            var url = getGlobalPathRoot() + "oframe/sysmg/sys019/sys019-add.gv";
            var ajaxPacket = new AJAXPacket(url);
            ajaxPacket.data.data = $form.serializeArray();
            ajaxPacket.data.data.push({name: "prjCd", value: getPrjCd()});
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var data = eval("(" + response + ")");
                var isSuccess = data.success;
                if (isSuccess) {
                    if (messageDefType != "" && messageDefType != null) {
                        alertMsg.correct("修改消息成功");
                    } else {
                        alertMsg.correct("添加消息成功！");
                    }
                    sys019.query();
                    $.pdialog.closeCurrent();
                } else {
                    alertMsg.error(data.errMsg);
                }
            });
        }
    },

    /**
     * 批量删除
     */
    batchDeleteView: function () {
        var messageDefIds = [];
        $("#sys019List", navTab.getCurrentPanel()).find(":checkbox[checked][name=messageDefId]").each(
            function (i) {
                var temp = $(this).closest("tr");
                var messageDefId = $("input[name=messageDefId]", temp).val();
                messageDefIds.push(messageDefId);
            }
        );
        if (messageDefIds.length == 0) {
            alertMsg.warn("请选择要删除的记录");
        } else {
            alertMsg.confirm("您确定要删除记录吗?", {
                okCall: function () {
                    var url = getGlobalPathRoot() + "oframe/sysmg/sys019/sys019-deleteView.gv";
                    var ajaxPacket = new AJAXPacket(url);
                    ajaxPacket.data.add("messageDefId", messageDefIds.toString());
                    core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                        var jsonData = eval("(" + response + ")");
                        // 服务调用是否成功
                        var isSuccess = jsonData.success;
                        if (isSuccess) {
                            alertMsg.correct("处理成功");
                            sys019.query();
                        } else {
                            alertMsg.error(jsonData.errMsg);
                        }
                    });
                }
            });
        }
        stopEvent(event);
    },

    /**
     * 导出消息
     */
    exportEntity: function () {
        var messageDefIds = [];
        $("#sys019List", navTab.getCurrentPanel()).find(":checkbox[checked][name=messageDefId]").each(
            function (i) {
                var temp = $(this).closest("tr");
                var messageDefId = $("input[name=messageDefId]", temp).val();
                messageDefIds.push(messageDefId);
            });
        if (messageDefIds.length == 0) {
            alertMsg.warn("请选择需要导出的信息");
        } else {
            var url = getGlobalPathRoot() + "oframe/sysmg/sys019/sys019-batchExport.gv?prjCd=" + getPrjCd() + "&messageDefIds=" + messageDefIds.toString();
            window.open(url);
        }
    },

    /**
     * 导入消息
     */
    startImport: function (obj) {
        var clickFileBtn = $(obj).parent().find("input[type=file]");
        clickFileBtn.attr("id", new Date().getTime());
        clickFileBtn.val("");
        clickFileBtn.unbind("change", sys019.importXml);
        clickFileBtn.bind("change", sys019.importXml);
        sys019.uploadObj = $(obj);
    },

    /**
     * 改变导入文件
     */

    //导入配置参数
    importXml: function () {
        var uploadURL = getGlobalPathRoot() + "oframe/sysmg/sys019/sys019-importData.gv?prjCd=" + getPrjCd();
        var importMsgFile = sys019.uploadObj.parent().find("input[type=file]").attr("id");
        $.ajaxFileUpload({
                url: uploadURL,
                secureuri: false,
                fileElementId: importMsgFile,
                dataType: 'json',
                success: function (data, status, fileElementId) {
                    if (data.isSuccess) {
                        alertMsg.correct("处理成功");
                        Page.query($("#sys019form", navTab.getCurrentPanel()), "");
                    } else {
                        alertMsg.error(data.errMsg);
                    }
                }

            }
        )
    },

    /**
     *获取iframe中的信息
     * @returns {*|jQuery}
     */
    getCode: function () {
        return $("textarea[name=messageDefContent]", $.pdialog.getCurrent()).val();
    },

    /**
     * 添加行
     */
    addRow: function (tableId, clickObject, flag) {
        var $table = $("#" + tableId, $.pdialog.getCurrent());
        var hiddenRow = null;
        if (flag == "pro") {
            hiddenRow = $table.find("tr.js_pro");
        } else {
            hiddenRow = $table.find("tr.js_cus");
        }
        var copyRow = $(hiddenRow).clone().removeClass("hidden").removeClass("js_cus").removeClass("js_pro");
        if (flag == 'pro') {
            $("input[name=producerName]", copyRow).addClass("required noErrorTip");
        } else {
            $("input[name=consumerName]", copyRow).addClass("required noErrorTip");
        }
        // 获取行号
        if (clickObject) {
            var index = $table.find("tr").index($(clickObject).parent().parent());
            if (index == 0) {
                $(hiddenRow).after(copyRow);
            } else {
                $(clickObject).parent().parent().after(copyRow);
            }
        } else {
            $table.append(copyRow);
        }
        $(copyRow).initUI();
        return copyRow;
    }
};
$(document).ready(function () {
    // 执行查询
    sys019.query();
});

















