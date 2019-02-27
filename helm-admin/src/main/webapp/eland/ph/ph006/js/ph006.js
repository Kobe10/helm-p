var ph006 = {
    /**
     * 初始化发起任务
     */
    initiateTask: function (procDefKey, busiKey) {
        index.startWf(procDefKey, {busiKey: busiKey});
    },

    /**
     * 确认发起任务
     */
    cfmSendTask: function (hsId) {
        var url = getGlobalPathRoot() + "oframe/wf/wf001/wf001-doStartWf.gv";
        var packet = new AJAXPacket(url);
        var container = navTab.getCurrentPanel();
        var procDefKey = $("input[name=procDefKey]", container).val();
        var taskName = $("input.js_taskName", container).val();
        var taskStartDate = $("input.js_taskStartDate", container).val();
        var taskDetails = $("textarea.js_taskDetails", container).val();
        var targetPer = $("input[name=targetPer]", container).val();
        var taskEndDate = $("input.js_taskEndDate", container).val();
        var busiKey = $("input[name=busiKey]", container).val();
        packet.data.add("procDefKey", procDefKey);
        packet.data.add("prjCd", getPrjCd());
        packet.data.add("busiKey", busiKey);
        packet.data.add("WF_PRI_TaskName", taskName);
        packet.data.add("WF_PRI_Description", taskDetails);
        packet.data.add("WF_PRI_TaskAssignee", targetPer);
        packet.data.add("WF_PRI_DueTime", taskEndDate);
        // 提交成功
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            var isSuccess = jsonData.success;
            if (isSuccess) {
                index.viewWf(jsonData.procInsId);
            } else {
                alertMsg("任务发送失败！" + jsonData.errMsg);
            }
        });
    },

    /**
     * 完成任务
     */
    completeTask: function (obj) {
        var url = getGlobalPathRoot() + "eland/ph/ph003/ph003-cfmCompleteTask.gv?prjCd=" + getPrjCd();
        var packet = new AJAXPacket(url);
        var hsId = $("input[name=hsId]", $.pdialog.getCurrent()).val();
        var taskId = $("input[name=taskId]", $.pdialog.getCurrent()).val();
        var taskName = $("input[name=taskName]", $.pdialog.getCurrent()).val();
        var taskStartDate = $("input[name=taskStartDate]", $.pdialog.getCurrent()).val();
        var taskDetails = $("textarea[name=taskDetails]", $.pdialog.getCurrent()).val();
        var targetPer = $("input[name=targetPer]", $.pdialog.getCurrent()).val();
        var taskEndDate = $("input[name=taskEndDate]", $.pdialog.getCurrent()).val();
        var runningDate = $("input[name=runningDate]", $.pdialog.getCurrent()).val();
        var taskCompleteDetails = $("textarea[name=taskCompleteDetails]", $.pdialog.getCurrent()).val();

        packet.data.add("hsId", hsId);
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
                alertMsg.correct("任务处理完成，已结束！");
                $.pdialog.closeCurrent(this);
            } else {
                alertMsg("任务处理失败！" + jsonDate.errMsg);
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
                return data.STAFF_NAME;
            },
            mustMatch: false,
            remoteDataType: "json",
            autoFill: false,
            delay: 300,
            minChars: 1,
            onItemSelect: function (obj) {
                var name = obj.data.STAFF_NAME.trim();
                $("input[name=taskAssignee]", $(obj.source).closest("td")).val(obj.data.STAFF_CODE);
                $("input[name=targetPerTemp]", $(obj.source).closest("td")).val(name);
            },
            onNoMatch: function (obj) {
                $("input[name=taskAssignee]", $(obj.source).closest("td")).val("");
                $("input[name=targetPerTemp]", $(obj.source).closest("td")).val("");
            }
        }
    }
};
