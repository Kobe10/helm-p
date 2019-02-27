/**
 * User: shfb_wang
 * Date: 2016/3/22 0022 15:55
 * Copyright(c) 北京四海富博计算机服务有限公司
 */
var fm001 = {
    /**
     * 查看五联单
     */
    viewWld: function (hsId, hsCtId, hsFmId) {
        var prjCd = getPrjCd();
        var url = getGlobalPathRoot() + "eland/fm/fm001/fm001-viewWld.gv?fromOp=view"
            + "&hsId=" + hsId
            + "&hsCtId=" + hsCtId
            + "&prjCd=" + prjCd
            + "&hsFmId=" + hsFmId;
        $.pdialog.open(url, "viewWld", "五联单", {mask: true, max: true, resizable: false});
    },
    /**
     * 初始化右侧列表
     * @param pId
     * @param obj
     */
    info: function (pId, obj) {
        $(obj).addClass("selected").siblings("li").removeClass("selected");
        var url = getGlobalPathRoot() + "eland/fm/fm001/fm001-info.gv";
        var packet = new AJAXPacket(url);
        packet.data.add("prjCd", getPrjCd());
        packet.data.add("pId", pId);
        core.ajax.sendPacketHtml(packet, function (response) {
            $("#fm001Context", navTab.getCurrentPanel()).html(response);
            initUI($("#fm001Context", navTab.getCurrentPanel()));
        }, true);
    },

    /**
     * 撤回
     */
    revocationView: function (hsFmId) {
        var url = getGlobalPathRoot() + "eland/fm/fm001/fm001-revocationView.gv?hsFmId=" + hsFmId + "&prjCd=" + getPrjCd();
        $.pdialog.open(url, "fm001revocationView", "撤回申请",
            {mask: true, width: 800, height: 400, resizable: true}
        )
    },
    /**
     * 提交撤回
     */
    revocation: function (hsFmId) {
        var url = getGlobalPathRoot() + "eland/fm/fm001/fm001-revocation.gv";
        var packet = new AJAXPacket(url);
        packet.data.add("hsFmId", hsFmId);
        packet.data.add("fmDesc", $("textarea[name=fmDesc]", $.pdialog.getCurrent()).val());
        packet.data.add("prjCd", getPrjCd());
        core.ajax.sendPacketHtml(packet, function (response) {
            var data = eval("(" + response + ")");
            var isSuccess = data.success;
            if (isSuccess) {
                alertMsg.correct("处理成功");
                $.pdialog.closeCurrent();
            } else {
                alertMsg.warn(data.errMsg);
            }
        });
    },

    switchTab: function (tabIdx) {
        var dialogId;
        if (tabIdx == 0) {
            dialogId = "fm001_submit";
        } else if (tabIdx == 1) {
            dialogId = "fm001_pause";
        } else if (tabIdx == 2) {
            dialogId = "fm001_complete";
        } else if (tabIdx == 3) {
            dialogId = "fm001_receive";
        }
        return dialogId;
    },

    /**
     * 打开快速检索对话框
     */
    openQSch: function (tabIdx) {
        var dialogId = fm001.switchTab(tabIdx);

        var div = $("#" + dialogId, navTab.getCurrentPanel());
        var active = div.attr("active");
        if ("on" == active) {
            div.hide().removeAttr("active");
        } else {
            div.show().attr("active", "on");
        }
    },

    /**
     * 关闭检索
     */
    closeQSch: function (tabIdx) {
        var dialogId = fm001.switchTab(tabIdx);

        $("#" + dialogId, navTab.getCurrentPanel()).hide().removeAttr("active");
    },

    query: function (tabIdx) {
        var dialogId = fm001.switchTab(tabIdx);
        var formId = dialogId + "Bankbook_form";

        var formObj = $("#" + formId, navTab.getCurrentPanel());
        var dialogObj = $("#" + dialogId, navTab.getCurrentPanel());
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
        Page.query($("#" + formId), "");
    },

    /**
     * 批量提交 制折
     */
    batchSubmit: function (tabIdx) {
        var dialogId = fm001.switchTab(tabIdx);
        var resultDiv = $("#" + dialogId + "_page_data", navTab.getCurrentPanel());

        var hsFmIds = [];
        resultDiv.find(":checkbox[checked][name=hsFmId]").each(
            function (i) {
                hsFmIds.push($(this).val());
            }
        );
        if (hsFmIds.length == 0) {
            alertMsg.warn("请选择要提交制折的房产！");
        } else {
            var url = getGlobalPathRoot() + "eland/fm/fm001/fm001-initBatch.gv?prjCd=" + getPrjCd()
                + "&tab=submit"
                + "&hsFmIds=" + hsFmIds.join(",");
            $.pdialog.open(url, "fm001-sub", "批量提交制折",
                {mask: true, max: false, maxable: false, resizable: false, width: 600, height: 300}
            );
        }
    },

    /**
     * 批量  完成 制折
     */
    batchComplete: function (tabIdx) {
        var dialogId = fm001.switchTab(tabIdx);
        var resultDiv = $("#" + dialogId + "_page_data", navTab.getCurrentPanel());

        var hsFmIds = [];
        resultDiv.find(":checkbox[checked][name=hsFmId]").each(
            function (i) {
                hsFmIds.push($(this).val());
            }
        );
        if (hsFmIds.length == 0) {
            alertMsg.warn("请选择要完成制折的房产！");
        } else {
            alertMsg.confirm("确认批量完成制折吗？", {
                okCall: function () {
                    var url = getGlobalPathRoot() + "eland/fm/fm001/fm001-bSaveSubmit.gv";
                    var ajaxPacket = new AJAXPacket(url);
                    ajaxPacket.data.add("prjCd", getPrjCd());
                    ajaxPacket.data.add("tab", "complete");
                    ajaxPacket.data.add("hsFmIds", hsFmIds.join(","));
                    core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                        var jsonData = eval("(" + response + ")");
                        // 服务调用是否成功
                        var isSuccess = jsonData.success;
                        if (isSuccess) {
                            alertMsg.correct("修改成功！");
                        } else {
                            alertMsg.error(jsonData.errMsg);
                        }
                    });
                }
            });
        }
    },

    /**
     * 暂缓制折 或 取消暂缓
     */
    pauseOrCancelMake: function (hsFmId, flag) {
        var url = getGlobalPathRoot() + "eland/fm/fm001/fm001-pauseOrCancel.gv?prjCd=" + getPrjCd()
            + "&flag=" + flag
            + "&hsFmId=" + hsFmId;
        var title = "暂缓制折";
        if (flag == '1') {
            title = "取消暂缓";
        }
        $.pdialog.open(url, "fm001-pause", title,
            {mask: true, max: false, maxable: false, resizable: false, width: 600, height: 300}
        );
    },

    /**
     * 领取 存折
     */
    receiveBankbook: function (hsId, hsFmId) {
        //alert(hsFmId);
        var url = getGlobalPathRoot() + "eland/ph/ph014/ph014-init.gv?prjCd=" + getPrjCd()
            + "&hsId=" + hsId
            + "&infoKey=" + hsFmId
            + "&formCd=CZ_LQ_CL";

        index.quickOpen(url, {opCode: "ph014-info", opName: "领取存折", fresh: true});
    }
};

$(document).ready(function () {
    // 拖动屏幕切分效果   如果要设置 cookie 在{里编写} cookieName: "cookieName"
    LeftMenu.init("fm001LeftDiv", "fm001Context", {});
});