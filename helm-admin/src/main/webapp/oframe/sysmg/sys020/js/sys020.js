var sys020 = {
    /**
     * 高级检索
     */
    queryHs: function (obj) {
        Page.queryCondition(obj, {
            changeConditions: sys020.changeConditions,
            formObj: $("#sys020form", navTab.getCurrentPanel())
        });
    },

    /**
     * 改变查询条件
     * @param newCondition
     */
    changeConditions: function (newCondition) {
        var formObj = $('#sys020form', navTab.getCurrentPanel());
        $("input[name=conditionName]", formObj).val(newCondition.conditionNames);
        $("input[name=condition]", formObj).val(newCondition.conditions);
        $("input[name=conditionValue]", formObj).val(newCondition.conditionValues);
        $("input.js_conditionValue", formObj).val(newCondition.conditionValueTexts);
        $("input[name=resultField]", formObj).val(newCondition.resultFields);
        // 排序字段
        $("input[name=sortColumn]", formObj).val(newCondition.sortColumn);
        $("input[name=sortOrder]", formObj).val(newCondition.sortOrder);
        sys020.query();
    },

    /**
     *快速查询
     */
    queryKs: function () {
        var dialogObj = $("#sys020form", navTab.getCurrentPanel());
        var messageDefType = $("input[name=messageDefType]", navTab.getCurrentPanel()).val();
        var consumerName = $("input[name=consumerName]", navTab.getCurrentPanel()).val();
        var producerName = $("input[name=producerName]", navTab.getCurrentPanel()).val();
        var url = getGlobalPathRoot() + "oframe/sysmg/sys020/sys020-queryKs.gv";
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
            sys020.query();
        }, true);
        packet = null;
    },

    /**
     *通用查询
     */
    query: function () {
        var formObj = $("#sys020form", navTab.getCurrentPanel());
        // 获取输入框
        var dialogObj = $("#sys020create", navTab.getCurrentPanel());
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
        Page.query($("#sys020form", navTab.getCurrentPanel()), "");
    },

    /**
     * 打开发消息界面
     */
    sendView: function () {
        var url = getGlobalPathRoot() + "oframe/sysmg/sys020/sys020-sendView.gv?prjCd=" + getPrjCd();
        $.pdialog.open(url, "sys020SendView", "发送消息",
            {mask: true, width: 800, height: 400, resizable: true}
        )
    },

    /**
     * 立即发送
     */
    sendMessage: function (sId) {
        var url = getGlobalPathRoot() + "oframe/sysmg/sys020/sys020-sendMessage.gv";
        var packet = new AJAXPacket(url);
        packet.data.add("sId", sId);
        packet.data.add("prjCd", getPrjCd());
        core.ajax.sendPacketHtml(packet, function (response) {
            var data = eval("(" + response + ")");
            var isSuccess = data.success;
            if (isSuccess) {
                alertMsg.correct("发送成功");
                sys020.query();
            } else {
                alertMsg.warn(data.errMsg);
            }
        });
    },

    /**
     * 自定义发送信息
     */
    sendMessageZDY: function () {
        //from提交
        var $form = $("#sys020MsgForm", $.pdialog.getCurrent());
        if ($form.valid()) {
            var url = getGlobalPathRoot() + "oframe/sysmg/sys020/sys020-sendMessageZDY.gv";
            var ajaxPacket = new AJAXPacket(url);
            ajaxPacket.data.data = $form.serializeArray();
            ajaxPacket.data.data.push({name: "prjCd", value: getPrjCd()});
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var data = eval("(" + response + ")");
                var isSuccess = data.success;
                if (isSuccess) {
                    alertMsg.correct("发送成功");
                    $.pdialog.closeCurrent();
                    sys020.query();
                } else {
                    alertMsg.error(data.errMsg);
                }
            });
        }
    },
    changeStatus: function () {
        var sStatus = $("select[name=sStatus]", navTab.getCurrentPanel()).val();
        $("input[name=sStatus]",navTab.getCurrentPanel()).val(sStatus);
    }

};
$(document).ready(function () {
    // 执行查询
    sys020.query();
});

















