wf001 = {

    /**
     * 待办任务管理页面
     */
    queryWF: function () {
        // 提交页面数据
        var url = "oframe/wf/wf004/wf004-initTask.gv?";
        index.quickOpen(url, {opCode: "wf004-initTask", opName: "我的任务"});
    },
    /**
     * 我的流程管理页面
     */
    queryTask: function () {
        // 提交页面数据
        var url = "oframe/wf/wf004/wf004-initProcess.gv?";
        index.quickOpen(url, {opCode: "wf004-initProcess", opName: "我的流程"});
    },

    /**
     * 判断
     * @constructor
     * @return {boolean}
     */
    Comparison: function () {
        var taskName = $("input[name=taskName]").val();
        var taskAssignee = $("input.js_assignee", navTab.getCurrentPanel()).val();
        var strSD = $("input[name=dueTime]").val();
        if (taskName == "") {
            alertMsg.warn("请输入任务名称！");
            return false;
        }
        if (taskAssignee == "") {
            alertMsg.warn("请输入下一步处理人！");
            return false;
        }
        if (strSD == "") {
            alertMsg.warn("请输入截至时间！");
            return false;
        }
        var strED = new Date();
        if (strSD && new Date(strSD.replace(/\-/g, '\/')) < strED) { //开始时间大于了结束时间
            alertMsg.warn("时间选择有误！截至时间必须大于或者等于当前时间！");
            return false;
        }
        return true;
    },

    /**
     * 发起任务：带有业务数据并保存业务数据
     * @param formId
     * @param optionData
     */
    doStartWithBusiness: function (formId, optionData) {
        var option = $.extend(optionData, {busiStartFlag: true});
        wf001.doStart(formId, option);
    },


    /**
     * 发起任务
     * 提供自动读取框架界面中的procDefId、procDefKey、procInsName、busiKey、procPrjCd参数
     * 和获取提交的Form中流程数据，处理规则如下：
     *    所有表单控件自动作为任务变量提交（自动在名称前追加WF_TSK_）
     *    所有具有"js_pri"样式的表单控件追加作为流程变量提交(自动在名称前追加WF_PRI_)
     *    具有样式【js_busi】的控件废弃，不在使用.
     * @param formId 提交的表单对象或则表单ID
     * @param optionData 表单附属数据
     */
    doStart: function (formId, optionData) {
        if (wf001.Comparison()) {
            var container = navTab.getCurrentPanel();
            var procDefId = $("input[name=procDefId]", container).val();
            var procDefKey = $("input[name=procDefKey]", container).val();
            var procInsName = $("input[name=procInsName]", container).val();
            var busiKey = $("input[name=busiKey]", container).val();
            var procPrjCd = $("input[name=procPrjCd]", container).val();

            var jsonData = {};
            var form = null;
            if (formId.constructor == "test".constructor) {
                form = $("#" + formId, container)
            } else {
                form = $(formId);
            }
            if (form.length > 0) {
                $("input,textarea,select", form).each(function () {
                    var name = $(this).attr("name");
                    var value = $(this).val();
                    var type = $(this).attr("type");
                    if (type == 'radio') {
                        var checked = $(this).attr("checked");
                        if (checked) {
                            if (name != "") {
                                jsonData["WF_TSK_" + name] = value;
                            }
                            if ($(this).hasClass("js_pri")) {
                                jsonData["WF_PRI_" + name] = value;
                            }
                            if ($(this).hasClass("js_busi")) {
                                jsonData["WF_BUSI_" + name] = value;
                            }
                        }
                    } else {
                        if (name != "") {
                            jsonData["WF_TSK_" + name] = value;
                        }
                        if ($(this).hasClass("js_pri")) {
                            jsonData["WF_PRI_" + name] = value;
                        }
                        if ($(this).hasClass("js_busi")) {
                            jsonData["WF_BUSI_" + name] = value;
                        }
                    }
                });
            }

            // 传递的流程级参数
            jsonData["procDefId"] = procDefId;
            jsonData["procInsName"] = procInsName;
            jsonData["procDefKey"] = procDefKey;
            jsonData["busiKey"] = busiKey;
            jsonData["procPrjCd"] = procPrjCd;
            jsonData["prjCd"] = getPrjCd();

            // 增加的参数
            if (optionData) {
                jsonData = $.extend(jsonData, optionData);
            }
            var url = getGlobalPathRoot() + "oframe/wf/wf001/wf001-doStartWf.gv";
            var packet = new AJAXPacket(url);
            packet.data.data = jsonData;
            // 提交成功
            core.ajax.sendPacketHtml(packet, function (response) {
                var jsonData = eval("(" + response + ")");
                var isSuccess = jsonData.success;
                if (isSuccess) {
                    var url = getGlobalPathRoot() + "oframe/wf/wf001/wf001-viewWf.gv?prjCd=" + getPrjCd() + "&procInsId=" + jsonData.procInsId;
                    window.location.href = url;
                } else {
                    alertMsg.error("任务发送失败！" + jsonData.errMsg);
                }
            });
        }
    },


    /**
     * 完成任务：带有业务数据的完成任务
     * @param formId
     * @param optionData
     */
    doTaskWithBusiness: function (formId, optionData) {
        var option = $.extend(optionData, {busiDoTaskFlag: true});
        wf001.doTask(formId, option);
    },

    /**
     * 处理任务公共函数， 提交成功跳转到任务查看界面
     *
     * 提供自动读取框架界面中的taskId、procPrjCd参数
     * 和获取提交的Form中流程数据，处理规则如下：
     *    所有表单控件自动作为任务变量提交（自动在名称前追加WF_TSK_）
     *    所有具有"js_pri"样式的表单控件追加作为流程变量提交(自动在名称前追加WF_PRI_)
     *    具有样式【js_busi】的控件废弃，不在使用.
     * @param formId 提交的表单ID
     * @param optionData 表单附属数据
     */
    doTask: function (formId, optionData) {
        var container = navTab.getCurrentPanel();
        var jsonData = {};
        var form = null;
        if (formId.constructor == "test".constructor) {
            form = $("#" + formId, container)
        } else {
            form = $(formId);
        }
        if (form.length > 0) {
            $("input,textarea,select", form).each(function () {
                var name = $(this).attr("name");
                var value = $(this).val();
                var type = $(this).attr("type");
                if (type == 'radio') {
                    var checked = $(this).attr("checked");
                    if (checked) {
                        if (name != "") {
                            jsonData["WF_TSK_" + name] = value;
                        }
                        if ($(this).hasClass("js_pri")) {
                            jsonData["WF_PRI_" + name] = value;
                        }
                        if ($(this).hasClass("js_busi")) {
                            jsonData["WF_BUSI_" + name] = value;
                        }
                    }
                } else {
                    if (name != "") {
                        jsonData["WF_TSK_" + name] = value;
                    }
                    if ($(this).hasClass("js_pri")) {
                        jsonData["WF_PRI_" + name] = value;
                    }
                    if ($(this).hasClass("js_busi")) {
                        jsonData["WF_BUSI_" + name] = value;
                    }
                }
            });
        }
        // 传递的流程级参数
        var taskContainer = form.closest("div.panel");
        jsonData["taskId"] = $("input[name=DO_TASK_ID", taskContainer).val();
        var prjCd = $("input[name=prjCd]", container).val();
        // 增加的参数
        if (optionData) {
            jsonData = $.extend(jsonData, optionData);
        }
        var url = getGlobalPathRoot() + "oframe/wf/wf001/wf001-doTask.gv?prjCd=" + prjCd;
        var packet = new AJAXPacket(url);
        packet.data.data = jsonData;
        // 提交成功
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            var isSuccess = jsonData.success;
            if (isSuccess) {
                alertMsg.correct("任务处理成功！");
                var procInsId = $("input[name=procInsId", container).val();
                url = getGlobalPathRoot() + "oframe/wf/wf001/wf001-viewWf.gv?prjCd=" + getPrjCd() + "&procInsId=" + procInsId;
                window.location.href = url;
            } else {
                alertMsg.error("任务处理失败！" + jsonData.errMsg);
            }
        });
    },

    /**
     * 显示流程流转历史
     */
    showProcHis: function () {
        var contain = $("#procHisContain", navTab.getCurrentPanel());
        if (contain.html() == "") {
            var procInsId = $("input[name=procInsId]", navTab.getCurrentPanel()).val();
            var packet = new AJAXPacket();
            // 组织节点
            packet.data.add("procInsId", procInsId);
            packet.data.add("prjCd", getPrjCd());
            // 提交界面展示
            packet.url = getGlobalPathRoot() + "oframe/wf/wf001/wf001-viewHis.gv";
            core.ajax.sendPacketHtml(packet, function (response) {
                contain.html(response);
                initUI(contain);
            });
        }
    },

    /**
     * 显示运行流程图
     */
    showProcPic: function () {
        var contain = $("#procImgContain", navTab.getCurrentPanel());
        if (contain.html() == "") {
            var procInsId = $("input[name=procInsId]", navTab.getCurrentPanel()).val();
            var procDefId = $("input[name=procDefId]", navTab.getCurrentPanel()).val();
            var imgUrl = "oframe/wf/wf001/wf001-viewRunWfPic.gv?procInsId=" + procInsId
                + "&procDefId=" + procDefId + "&date=";
            var contain = $("#procImgContain", navTab.getCurrentPanel());
            var procContext = $("#procContext", navTab.getCurrentPanel());
            var viewContain = new EntityContainer(contain[0]);
            contain.width(procContext.width());
            viewContain.height = contain.height() - 30;
            viewContain.init();
            viewContain.loadImgSrc(imgUrl, new Date().getTime);
        }
    }
};