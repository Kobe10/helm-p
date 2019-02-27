var prjCtrl = {
    /**
     * 显示
     * @param obj
     * @param scType
     */
    ctrlInfo: function (obj) {
        if (obj == null) {
            return;
        }
        var $this = $(obj);
        $this.siblings().removeClass("active");
        $this.addClass("active");
        var scType = $this.attr("scType");

        var textContainer = $("#pj002CtrlInfoDiv", navTab.getCurrentPanel());
        // value为用户选中的关键词
        var packet = new AJAXPacket();
        // 组织节点
        packet.data.add("prjCd", $("input[name=prjCd]", navTab.getCurrentPanel()).val());
        packet.data.add("scType", scType);
        // 提交界面展示
        packet.url = getGlobalPathRoot() + "eland/pj/pj002/pj002-queryCtrl.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            textContainer.html(response);
            initUI(textContainer);
        });

    },

    /**
     * 初始化点击第一个环节配置
     */
    initCtrlInfo: function () {
        var roleList = $("#pj002CtrlList>li", navTab.getCurrentPanel());
        if (roleList.length > 0) {
            roleList.eq(0).trigger("click");
        }
    }
};

ct001CtrlCh = {
    saveCtCtrl: function () {
        var $form = $("#ct001_ch", navTab.getCurrentPanel());
        var prjCd = $("input[name=prjCd]", navTab.getCurrentPanel()).val();
        var startCd = $("input[name=startCd]:checked", $form).val();
        var checkOk = true;
        if ("3" != startCd) {
            checkOk = $form.valid();
        }
        if (checkOk) {
            var url = getGlobalPathRoot() + "eland/pj/pj002/pj002-saveCtrl.gv";
            var packet = new AJAXPacket(url);
            packet.data.data = $form.serializeArray();
            packet.data.data.push({name: "prjCd", value: prjCd});
            core.ajax.sendPacketHtml(packet, function (response) {
                var jsonData = eval("(" + response + ")");
                if (jsonData.success) {
                    alertMsg.correct("保存成功！");
                } else {
                    alertMsg.error(jsonData.errMsg);
                }
            });
        }
    }
};

ct001CtrlCt = {
    /**
     * 验证协议阅读时间
     **/
    checkSpaceTime: function (formObj) {
        var spaceTime = parseInt($("input[name=spaceTime]", formObj).val());
        var readTime = parseInt($("input[name=readTime]", formObj).val());
        if (readTime > spaceTime) {
            alertMsg.warn("阅读协议时长不能大于签约间隔时长");
            $("input[name=readTime]", formObj).addClass("error");
            return false;
        } else {
            $("input[name=readTime]", formObj).removeClass("error");
            return true;
        }
    },

    //验证预签约开始日期,结束日期
    checkPreDate: function () {
        var preStDateObj = $("input[name=preCtDateSt]", navTab.getCurrentPanel());
        var preStDate = preStDateObj.val();
        var preEndDateObj = $("input[name=preCtDateEnd]", navTab.getCurrentPanel());
        var preEndDate = preEndDateObj.val();
        if (!preStDate) {
            preStDateObj.addClass("error");
            return false;
        } else if (!preEndDate) {
            preEndDateObj.addClass("error");
            return false;
        } else if (preStDate > preEndDate) {
            alertMsg.warn("结束时间不能早于开始时间");
            preEndDateObj.addClass("error").val("");
            return false;
        } else {
            preStDateObj.removeClass("error");
            preEndDateObj.removeClass("error");
            return true;
        }
    },

    /**
     * 保存签约控制信息
     **/
    saveCtCtrl: function () {
        var $form = $("#ct001_ct", navTab.getCurrentPanel());
        var startCd = $("input[name=startCd]:checked", $form).val();
        var checkOk = true;
        if ("3" != startCd) {
            checkOk = $form.valid() && ct001CtrlCt.checkSpaceTime($form) && ct001CtrlCt.checkPreDate();
        }
        if (checkOk) {
            var url = getGlobalPathRoot() + "eland/pj/pj002/pj002-saveCtrl.gv";
            var packet = new AJAXPacket(url);
            packet.data.data = $form.serializeArray();
            packet.data.data.push({name: "prjCd", value: $("input[name=prjCd]", navTab.getCurrentPanel()).val()});
            core.ajax.sendPacketHtml(packet, function (response) {
                var jsonData = eval("(" + response + ")");
                if (jsonData.success) {
                    alertMsg.correct("保存成功！");
                } else {
                    alertMsg.error(jsonData.errMsg);
                }
            });
        }
    },

    /**
     * 暂停签约
     */
    pauseCtCtrl: function () {
        //弹框输入暂停时间
        var url = getGlobalPathRoot() + "eland/pj/pj002/pj002-pauseCtCtrl.gv?prjCd=" + getPrjCd();
        $.pdialog.open(url, "ct001_ct_notice", "签约暂停设置", {mask: true, maxable: false, height: 150, width: 400});
    },

    /**
     * 重置签约数据
     */
    resetCtData: function () {
        //弹框输入暂停时间
        var url = getGlobalPathRoot() + "eland/pj/pj002/pj002-resetCtData.gv?prjCd=" + getPrjCd();
        $.pdialog.open(url, "ct001_ct_reset", "重置签约数据", {mask: true, maxable: false, height: 150, width: 400});
    },

    /**
     * 保存暂停信息
     */
    savePause: function () {
        var pauseTime = $("input[name=pauseTime]", $.pdialog.getCurrent()).val();
        if (pauseTime != '' && !/^[0-9]*$/.test(pauseTime)) {
            alertMsg.warn("暂停时间必须输入，且必须是数字！");
            return false;
        }
        var url = getGlobalPathRoot() + "eland/pj/pj002/pj002-savePauseCtCtrl.gv";
        var ajaxPacket = new AJAXPacket(url);
        ajaxPacket.data.add("prjCd", getPrjCd());
        ajaxPacket.data.add("pauseTime", pauseTime);
        core.ajax.sendPacketHtml(ajaxPacket, function (response) {
            var jsonData = eval("(" + response + ")");
            if (jsonData.success) {
                alertMsg.correct("保存成功！");
                $.pdialog.closeCurrent();
                //修改之后刷新 控制信息
                var currLi = $("#pj002CtrlList>li[scType='1']", navTab.getCurrentPanel());
                prjCtrl.ctrlInfo(currLi);
            } else {
                alertMsg.error(jsonData.errMsg);
            }
        });
    },

    saveResetData: function () {
        var resetData = $("input[name=resetDataFlag][type='radio']:checked", $.pdialog.getCurrent()).val();
        if (resetData == "1") {
            alertMsg.confirm("你确定要清除居民的签约数据？", {
                okCall: function () {
                    // 清理签约数据，并重置签约数据为0
                    ct001CtrlCt.doSaveResetData();
                },
                cancelCall: function () {
                    $.pdialog.closeCurrent();
                }
            });
        } else {
            // 重新加载序列
            ct001CtrlCt.doSaveResetData();
        }
    },

    /**
     * 执行清除签约数据操作
     */
    doSaveResetData: function () {
        var resetData = $("input[name=resetDataFlag][type='radio']:checked", $.pdialog.getCurrent()).val();
        var url = getGlobalPathRoot() + "eland/pj/pj002/pj002-doResetCtData.gv";
        var packet = new AJAXPacket(url);
        packet.data.add("resetDataFlag", resetData);
        packet.data.add("prjCd", getPrjCd());
        core.ajax.sendPacketHtml(packet, function (response) {
            var data = eval("(" + response + ")");
            var isSuccess = data.success;
            if (isSuccess) {
                alertMsg.correct("处理成功！");
                //刷新
                var currLi = $("#pj002CtrlList>li[scType='1']", navTab.getCurrentPanel());
                prjCtrl.ctrlInfo(currLi);
            } else {
                alertMsg.error(data.errMsg);
            }
            $.pdialog.closeCurrent();
        }, true);
    },
    /**
     * 增加时间
     * @param obj
     */
    addDate: function (tabId, obj) {
        var addRow = ct001CtrlCt.addRowNoInit(tabId, null);
        addRow.find("span.js_mem_rht").remove();
        addRow.find("input").addClass("required");
        $(addRow).initUI();
        addRow.show();
        addRow.find("input.js_sign_begin").val();
        addRow.find("input.js_sign_end").val();
        addRow.find("input[type=text]:eq(0)").focus();
    },

    /**
     * 拷贝增加行
     */
    addRowNoInit: function (tableId, clickObject) {
        var $table = $("#" + tableId);
        var hiddenRow = $table.find("tr.js_hidden_tr");
        var copyRow = $(hiddenRow).clone().removeClass("hidden").removeClass("js_hidden_tr");
        // 获取行号
        if (clickObject) {
            var index = $table.find("tr").index($(clickObject).parent().parent());
            if (index == 0) {
                $table.append(copyRow);
            } else {
                $(clickObject).parent().parent().after(copyRow);
            }
        } else {
            $table.append(copyRow);
        }
        return copyRow;
    },

    /**
     * 删除时间
     * @param obj
     */
    deleteDate: function (obj) {
        var clickTr = $(obj).closest("tr");
        var trArr = [];
        trArr = clickTr.closest("table").find("tr.js_tr_time");
        clickTr.closest("table").find("tr.js_tr_time").each(function () {
            if (trArr.length > 2) {
                clickTr.remove();
            } else {
                alertMsg.warn("至少保留一条记录");
            }
        });
    }
};

$(document).ready(function () {
    prjCtrl.initCtrlInfo();
});
