var ph00202 = {
    /**
     * 初始化发起任务
     */
    initiateTask: function (procDefKey, buildId) {
        index.startWf(procDefKey, {busiKey: buildId, backUrl: "eland/ph/ph002/ph002-init.gv?buldId=" + buildId});
    },

    /**
     * 确认发起任务
     */
    cfmSendTask: function (buildId) {
        var url = getGlobalPathRoot() + "eland/ph/ph002/ph002-cfmSendTask.gv?prjCd=" + getPrjCd() + "&buildId=" + buildId;
        var packet = new AJAXPacket(url);
        var container = navTab.getCurrentPanel();
        var procDefKey = $("input[name=procDefKey]", container).val();
        var taskName = $("input.js_taskName", container).val();
        var taskStartDate = $("input.js_taskStartDate", container).val();
        var taskDetails = $("textarea.js_taskDetails", container).val();
        var targetPer = $("input[name=targetPer]", container).val();
        var taskEndDate = $("input.js_taskEndDate", container).val();
        packet.data.add("procDefKey", procDefKey);
        packet.data.add("taskName", taskName);
        packet.data.add("taskStartDate", taskStartDate);
        packet.data.add("taskDetails", taskDetails);
        packet.data.add("targetPer", targetPer);
        packet.data.add("taskEndDate", taskEndDate);
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            var isSuccess = jsonData.success;
            if (isSuccess) {
                alert(jsonData.procInsId);
                index.viewWf(jsonData.procInsId);
            } else {
                alertMsg("任务发送失败！" + jsonData.errMsg);
            }
        });
    },

    /**
     * 历史任务
     */
    historyTask: function (buildId) {
        var url = getGlobalPathRoot() + "eland/ph/ph002/ph002-historyTask.gv?prjCd=" + getPrjCd() + "&buildId=" + buildId;
        $.pdialog.open(url, "ph00202", "历史任务", {
            mask: true, max: false, width: 880, height: 650
            //close: ph00202.faqirenwu
        });
    },

    /**
     * 取消任务
     */
    cancelTask: function (obj) {
        //var url = getGlobalPathRoot() + "eland/ph/ph002/ph002-historyTask.gv?prjCd=" + getPrjCd() + "&buildId=" + buildId;
        $.pdialog.closeCurrent(obj);
    },

    /**
     * 完成任务
     */
    completeTask: function (obj) {
        var url = getGlobalPathRoot() + "eland/ph/ph002/ph002-cfmCompleteTask.gv?prjCd=" + getPrjCd();
        var packet = new AJAXPacket(url);
        var buildId = $("input[name=buildId]", $.pdialog.getCurrent()).val();
        var taskId = $("input[name=taskId]", $.pdialog.getCurrent()).val();
        var taskName = $("input[name=taskName]", $.pdialog.getCurrent()).val();
        var taskStartDate = $("input[name=taskStartDate]", $.pdialog.getCurrent()).val();
        var taskDetails = $("textarea[name=taskDetails]", $.pdialog.getCurrent()).val();
        var targetPer = $("input[name=targetPer]", $.pdialog.getCurrent()).val();
        var taskEndDate = $("input[name=taskEndDate]", $.pdialog.getCurrent()).val();
        var runningDate = $("input[name=runningDate]", $.pdialog.getCurrent()).val();
        var taskCompleteDetails = $("textarea[name=taskCompleteDetails]", $.pdialog.getCurrent()).val();

        packet.data.add("buildId", buildId);
        packet.data.add("taskId", taskId);
        packet.data.add("taskName", taskName);
        packet.data.add("taskStartDate", taskStartDate);
        packet.data.add("taskDetails", taskDetails);
        packet.data.add("targetPer", targetPer);
        packet.data.add("taskEndDate", taskEndDate);
        packet.data.add("runningDate", runningDate);
        packet.data.add("taskCompleteDetails", taskCompleteDetails);

        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonDate = eval("(" + response + ")");
            var isSuccess = jsonDate.success;
            if (isSuccess) {
                alertMsg.correct("任务已发出");
                $.pdialog.closeCurrent(this);
            } else {
                alertMsg("任务发送失败！" + jsonDate.errMsg);
            }
        });
    },

    /**
     * 获取任务指派人
     * @param obj
     * @returns {string}
     */
    getUrl: function (obj) {
        return getGlobalPathRoot() + "oframe/common/auto/auto-data.gv?subSvcName=sys002Data";
    },
    getOption: function (obj) {
        return {
            processData: function (result) {
                var myData = [];
                for (i = 0; i < result.length; i++) {
                    var dataRow = {};
                    dataRow.data = result[i];
                    dataRow.value = result[i].STAFF_NAME;
                    myData.push(dataRow);
                }
                return myData;
            },
            showResult: function (value, data) {
                return data.STAFF_NAME + " ( " + data.STAFF_CODE + " ) ";
            },
            mustMatch: false,
            remoteDataType: "json",
            autoFill: false,
            delay: 300,
            minChars: 0,
            onItemSelect: function (obj) {
                $("input[name=targetPer]", $.pdialog.getCurrent()).val(obj.data.STAFF_CODE);
                $("input[name=targetPerTemp]", $.pdialog.getCurrent()).val(obj.data.STAFF_NAME);
            },
            onNoMatch: function (obj) {
                $("input[name=targetPer]", $.pdialog.getCurrent()).val("");
            }
        }
    },
    /**
     * 点击快速提交按钮
     */
    submitMemo: function () {
        var $form = $("#_ph00202frm_spat", navTab.getCurrentPanel());
        if ($form.valid()) {
            // 提交页面数据
            var url = getGlobalPathRoot() + "eland/ph/ph002/ph002-saveWM.gv";
            var ajaxPacket = new AJAXPacket(url);
            var jsonData = $form.serializeArray();
            jsonData.push({
                name: 'prjCd',
                value: getPrjCd()
            });
            ajaxPacket.data.data = jsonData;
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var jsonData = eval("(" + response + ")");
                // 服务调用是否成功
                var isSuccess = jsonData.success;
                if (isSuccess) {
                    alertMsg.correct("处理成功");
                    $("textarea", $form).val("");
                } else {
                    alertMsg.error(jsonData.errMsg);
                }
            });
        }
    }
};
