var ph00302 = {
    /**
     * 初始化发起任务
     */
    initiateTask: function (procDefKey, prjCd, hsId) {
        index.startWf(procDefKey, prjCd, {busiKey: "HouseInfo_" + hsId});
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
        packet.data.add("procDefKey", procDefKey);
        packet.data.add("prjCd", getPrjCd());
        packet.data.add("busiKey", "HouseInfo_" + hsId);
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
     * 历史任务
     */
    historyTask: function (hsId) {
        var url = getGlobalPathRoot() + "eland/ph/ph003/ph003-historyTask.gv?prjCd=" + getPrjCd() + "&hsId=" + hsId;
        $.pdialog.open(url, "ph00302", "历史任务", {
            mask: true, max: false, width: 880, height: 600
        });
    },
    /**
     * 查看流程
     * @param procInsId 流程实例ID
     */
    queryViewWf: function (procInsId) {
        var url = getGlobalPathRoot() + "oframe/wf/wf001/wf001-viewWf.gv?prjCd=" + getPrjCd() + "&query=true&procInsId=" + procInsId;
        window.open(url, "查看流程");
    },
    /**
     * 取消任务
     */
    cancelTask: function (obj) {

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
        var $form = $("#_ph00302frm_spat", navTab.getCurrentPanel());
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
                    editor.setContent("");
                    $form.find("textarea").val("");
                } else {
                    alertMsg.error(jsonData.errMsg);
                }
            });
        }
    }
};
$(document).ready(function () {
    $("textarea.publicEditor",navTab.getCurrentPanel()).each(function () {
        var $this = $(this);
        var edtOpt = $this.attr("edtOpt");
        var uneditUE = $this.hasClass("uneditUE");
        var option = {};
        if (edtOpt) {
            option = eval('(' + edtOpt + ')');
        }
        var publicEditor;
        if (uneditUE) {
            publicEditor = {
                minFrameHeight: 300,
                toolbars: [
                    ['undo', 'redo', '|', 'formatmatch', 'bold', 'italic', 'underline', '|',
                        'customstyle', 'paragraph', 'fontfamily', 'fontsize', 'justify', '|', 'insertimage', 'attachment', '|', 'cleardoc', 'fullscreen']
                ],
                readonly: true
            };
        } else {
            publicEditor = {
                minFrameHeight: 300,
                toolbars: [
                    ['undo', 'redo', '|', 'formatmatch', 'bold', 'italic', 'underline', '|',
                        'customstyle', 'paragraph', 'fontfamily', 'fontsize', 'justify', '|', 'insertimage', 'attachment', '|', 'cleardoc', 'fullscreen']
                ]
            };
        }

        $.extend(option, publicEditor);
        editor = new UE.ui.Editor(option);
        editor.render(this);
    });
});
