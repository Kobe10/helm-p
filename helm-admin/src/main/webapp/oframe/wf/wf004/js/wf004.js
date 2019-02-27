var wf004 = {
    /**
     * 关闭检索
     * @param forceFlag 强制关闭
     */
    closeQSch: function (tabIdx) {
        var dialogId = "wf004create";
        if (tabIdx == "2") {
            dialogId = "wf004End";
        }
        $("#" + dialogId, navTab.getCurrentPanel()).hide().removeAttr("active");
    },

    /**
     * 流程撤销权限
     * @returns {boolean}
     */
    getrevokeTask: function (obj) {
        var result = false;
        var $this = $(obj);
        var revoke = $("input[name='revokeTask']", $this);
        if (revoke && $(revoke).val() == "1") {
            result = true;
        }
        return result;
    },


    /**
     * 流程撤销按钮显示控制
     * @returns {boolean}
     */
    revokeTaskShow: function () {
        var staffCd = $("input[name=StaffCd]").val();
        $("tr.js_proc_info", navTab.getCurrentPanel()).each(function () {
            var procStUser = $(this).attr("procStUser");
            if (staffCd == procStUser) {
                $("span[name=revoke]", $(this)).show();
            } else if (wf004.getrevokeTask($(this))) {
                $("span[name=revoke]", $(this)).show();
            } else {
                $("span[name=revoke]", $(this)).hide();
            }
        });
    },

    /**
     * 流程撤销按钮显示控制
     * @returns {boolean}
     */
    processingTaskShow: function () {
        var staffCd = $("input[name=StaffCd]").val();
        $("tr.js_task_info", navTab.getCurrentPanel()).each(function () {
            var assignee = $(this).attr("assignee");
            if (staffCd == assignee) {
                $("span[name=processingTask]", $(this)).show();
            } else {
                $("span[name=processingTask]", $(this)).hide();
            }
        });
    },
    /**
     * 撤销
     * @param forceFlag 强制关闭
     */
    Revoke: function (procInsId) {
        alertMsg.confirm("您确定要撤销该流程?", {
            okCall: function () {
                // 提交页面数据
                var url = getGlobalPathRoot() + "oframe/wf/wf004/wf004-revoke.gv?prjCd=" + getPrjCd() + "&procInsId=" + procInsId;
                var ajaxPacket = new AJAXPacket(url);
                core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                    var jsonData = eval("(" + response + ")");
                    // 服务调用是否成功
                    var isSuccess = jsonData.success;
                    if (isSuccess) {
                        alertMsg.correct("撤销成功");
                        wf004.loadTabContext(1);
                    } else {
                        alertMsg.error(jsonData.errMsg);
                    }
                });
            }
        });
    },

    /**
     * 自动信息接收人
     */
    getUrlren: function (obj) {
        return getGlobalPathRoot() + "oframe/common/auto/auto-data.gv?subSvcName=sys002Data";
    },
    getOptionren: function (obj) {
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
            mustMatch: true,
            remoteDataType: "json",
            autoFill: true,
            delay: 100,
            minChars: 1,
            onItemSelect: function (obj) {
                var name = obj.data.STAFF_NAME.trim();
                var tdObj = $(obj.source).parent();
                $("input[type=hidden]", tdObj).val(obj.data.STAFF_CODE);
                $("input[type=text]", tdObj).val(name);
            },
            onNoMatch: function (obj) {
                var tdObj = $(obj.source).parent();
                $("input[type=hidden]", tdObj).val("");
                $("input[type=text]", tdObj).val("");
            }
        }
    },

    /**
     * 加载t内容
     * @param obj
     */
    loadTabContext: function (id) {
        if (id == "1") {
            var sta = $(".js_star_task", navTab.getCurrentPanel());
            // 执行查询
            $(".js_faTask", sta).click();
        } else if (id == "2") {
            var end = $(".js_end_task", navTab.getCurrentPanel());
            // 执行查询
            $(".js_faTask", end).click();
        }
    },
    /**
     * 查看流程
     * @param procInsId 流程实例ID
     */
    queryViewWf: function (procInsId) {
        var url = getGlobalPathRoot() + "oframe/wf/wf001/wf001-viewWf.gv?prjCd=" + getPrjCd() + "&procInsId=" + procInsId;
        window.open(url, "查看流程");
    },
    /**
     * 打开快速检索对话框
     */
    openQSch: function (tabIdx) {
        var dialogId = "wf004create";
        if (tabIdx == "2") {
            dialogId = "wf004End";
        }
        var div = $("#" + dialogId, navTab.getCurrentPanel());
        var active = div.attr("active");
        if ("on" == active) {
            div.hide().removeAttr("active");
        } else {
            div.show().attr("active", "on");
        }
    },

    /**
     * 打开委托流程页面
     */
    delegationWfView: function (taskId) {
        var url = getGlobalPathRoot() + "oframe/wf/wf004/wf004-delegationWfView.gv?prjCd=" + getPrjCd() + "&taskId=" + taskId;
        $.pdialog.open(url, "wf004-delegationWfView", "任务委托办理", {mask: true, width: 800, height: 400, resizable: true})
    },
    /**
     * 委托流程
     */
    delegationWf: function (taskId) {
        var wfStaff = $("input[name=wfStaff]", $.pdialog.getCurrent()).val();
        console.log(wfStaff);
        var url = getGlobalPathRoot() + "oframe/wf/wf004/wf004-delegationWf.gv?prjCd=" + getPrjCd() + "&taskId=" + taskId + "&wfStaff=" + wfStaff;
        var ajaxPacket = new AJAXPacket(url);
        core.ajax.sendPacketHtml(ajaxPacket, function (response) {
            var jsonData = eval("(" + response + ")");
            // 服务调用是否成功
            var isSuccess = jsonData.success;
            if (isSuccess) {
                alertMsg.correct("委托成功");
                $.pdialog.closeCurrent();
                wf004.loadTabContext(1);
            } else {
                alertMsg.error(jsonData.errMsg);
            }
        });
    }
};

$(document).ready(function () {
    // 执行查询
    wf004.loadTabContext(1);
});