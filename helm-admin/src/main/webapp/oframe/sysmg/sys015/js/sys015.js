sys015 = {
    /**
     * 打开快速检索对话框
     */
    openQSch: function () {
        var div = $("#sys015create", navTab.getCurrentPanel());
        var active = div.attr("active");
        if ("on" == active) {
            div.hide().removeAttr("active");
        } else {
            div.show().attr("active", "on");
        }
    },

    /**
     * 打开添加页面
     */
    editView: function (jobTaskId) {
        var url = getGlobalPathRoot() + "oframe/sysmg/sys015/sys015-editView.gv?jobTaskId=" + jobTaskId;
        $.pdialog.open(url, "sys015", "作业信息",
            {mask: true, max: false, maxable: false, resizable: false, width: 800, height: 400}
        );
        stopEvent(event);
    },

    /**
     * 操作作业
     * @param jobTaskId
     */
    operationJob: function (jobTaskId, type,threadId) {
        if (type == "" || type == null) {
            return
        }
        var url = getGlobalPathRoot() + "oframe/sysmg/sys015/sys015-operationJob.gv?prjCd=" + getPrjCd();
        var packet = new AJAXPacket(url);
        packet.data.add("jobTaskId", jobTaskId);
        packet.data.add("threadId", threadId);
        packet.data.add("type", type);
        core.ajax.sendPacketHtml(packet, function (response) {
            var data = eval("(" + response + ")");
            var isSuccess = data.success;
            if (isSuccess) {
                alertMsg.correct("操作成功");
                sys015.query();
            } else {
                alertMsg.error(data.errMsg);
            }
        });
        stopEvent(event);
    },
    /**
     * 操作作业
     * @param jobTaskId
     */
    deleteJob: function (jobTaskId) {
        alertMsg.confirm("是否删除该作业？", {
            okCall: function () {
                var url = getGlobalPathRoot() + "oframe/sysmg/sys015/sys015-deleteJob.gv?prjCd=" + getPrjCd();
                var packet = new AJAXPacket(url);
                packet.data.add("jobTaskId", jobTaskId);
                core.ajax.sendPacketHtml(packet, function (response) {
                    var data = eval("(" + response + ")");
                    var isSuccess = data.success;
                    if (isSuccess) {
                        alertMsg.correct("删除成功");
                        sys015.query();
                    } else {
                        alertMsg.error(data.errMsg);
                    }
                });
            }
        });
        stopEvent(event);
    },

    /**
     * 添加
     */
    editJob: function () {
        var jobNameCh = $("input[name=jobNameCh]", $.pdialog.getCurrent()).val();
        var jobExecTime = $("input[name=jobExecTime]", $.pdialog.getCurrent()).val();
        var jobDescription = $("input[name=jobDescription]", $.pdialog.getCurrent()).val();
        var jobType = $("input[name=jobType]", $.pdialog.getCurrent()).val();
        var jobStartParam = $("input[name=jobStartParam]", $.pdialog.getCurrent()).val();
        var jobTaskId = $("input[name=jobTaskId]", $.pdialog.getCurrent()).val();
        var jobStatus = $("select[name=jobStatus]", $.pdialog.getCurrent()).val();
        var jobRunNow = $("select[name=jobRunNow]", $.pdialog.getCurrent()).val();

        if (jobNameCh == "") {
            alertMsg.warn("作业名称必须输入");
            return
        }
        if (jobExecTime == "") {
            alertMsg.warn("执行时间必须输入");
            return
        }
        if (jobType == "") {
            alertMsg.warn("作业类型必须输入");
            return
        }
        if (jobStartParam == "") {
            alertMsg.warn("启动参数必须输入");
            return
        }

        var url = getGlobalPathRoot() + "oframe/sysmg/sys015/sys015-add.gv?prjCd=" + getPrjCd();
        var packet = new AJAXPacket(url);
        packet.data.add("jobNameCh", jobNameCh);
        packet.data.add("jobStatus", jobStatus);
        packet.data.add("jobRunNow", jobRunNow);
        packet.data.add("jobExecTime", jobExecTime);
        packet.data.add("jobDescription", jobDescription);
        packet.data.add("jobType", jobType);
        packet.data.add("jobStartParam", jobStartParam);
        packet.data.add("jobTaskId", jobTaskId);
        core.ajax.sendPacketHtml(packet, function (response) {
            var data = eval("(" + response + ")");
            var isSuccess = data.success;
            if (isSuccess) {
                if (jobTaskId != "" && jobTaskId != null) {
                    alertMsg.correct("修改作业成功");
                } else {
                    alertMsg.correct("添加作业成功！");
                }
                sys015.query();
            } else {
                alertMsg.error(data.errMsg);
            }
            $.pdialog.closeCurrent();
        });
    },

    /**
     * 查询
     */
    query: function () {
        var formObj = $("#sys015form", navTab.getCurrentPanel());
        // 获取输入框
        var dialogObj = $("#sys015create", navTab.getCurrentPanel());
        // 覆盖的查询条件
        var coverConditionName = [];
        var coverCondition = [];
        var coverConditionValue = [];
        var coverNames = [];
        // 循环当前查询条件
        $("input,select", dialogObj).each(function () {
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
            } else if ("SELECT" == tagName) {
                coverNames.push(attrName);
                if (value != "") {
                    coverConditionName.push(attrName);
                    coverCondition.push("=");
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
        Page.query($("#sys015form"), "");
    },

    /**
     * 查看历史
     */
    queryLog: function (obj, jobTaskId) {
        var $_this = $(obj);
        var oldhistory = $_this.next("#history");
        if (oldhistory.length > 0) {
            oldhistory.remove();
            return
        }
        var url = getGlobalPathRoot() + "oframe/sysmg/sys015/sys015-query.gv?prjCd=" + getPrjCd();
        var packet = new AJAXPacket(url);
        packet.data.add("jobTaskId", jobTaskId);
        core.ajax.sendPacketHtml(packet, function (response) {
            var tdLength = $_this.children().length;
            var history = $("#history", navTab.getCurrentPanel());
            if (history.length > 0) {
                history.remove();
            }
            var htm = "<tr id='history'><td  colspan='" + tdLength + "'>" + response + "</td></tr>";
            $_this.after(htm);
            initUI($("#history", navTab.getCurrentPanel()));
        });
    },

    /**
     * 点击移除历史
     * @param obj
     */
    removeHistory: function (obj) {
        var history = $("#history", navTab.getCurrentPanel());
        history.remove();
    }

};

$(document).ready(function () {
    // 执行查询
    sys015.query();
});