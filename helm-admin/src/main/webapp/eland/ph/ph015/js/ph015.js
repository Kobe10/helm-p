/**
 * Created by zhouyx on 2016/5/11.
 */
var ph015 = {

    query: function (taskId) {
        var tempDiv = $("#" + taskId, navTab.getCurrentPanel());
        var recordType = $("input[name=recordType]", tempDiv).val();
        var form = $("#ph015form_result" + recordType, tempDiv);
        Page.query(form, "ph015.queryjs('" + taskId + "')");
    },

    /**
     * 回调js
     */
    queryjs: function (taskId) {
        var tempDiv = $("#" + taskId, navTab.getCurrentPanel());
        var method = $("input[name=method]", tempDiv).val();
        var deleteRecord = $("a[name=deleteRecord]", tempDiv);
        var addRecord = $("a[name=addRecord]", tempDiv);
        var editButton = $("span[name=editButton]", tempDiv);
        //查看页面控制 删除按钮 编辑按钮
        if (method != 'edit') {
            deleteRecord.hide();
            addRecord.hide();
            editButton.text("[查看]");
        }
    },

    /**
     * 打开添加页面
     */
    editView: function (obj, recordId) {
        var thisPanel = $(obj).closest("div.js_ph015_task", navTab.getCurrentPanel());
        var method = $("input[name=method]", thisPanel).val();
        var recordRelId = $("input[name=hsId]", thisPanel).val();
        var recordType = $("input[name=recordType]", thisPanel).val();
        var taskId = $("input[name=taskId]", thisPanel).val();
        var topName = "谈话处理";
        if (recordType == '2') {
            topName = "上诉处理";
        }
        var url = getGlobalPathRoot() + "eland/ph/ph015/ph015-editView.gv?recordId=" + recordId + "&prjCd=" + getPrjCd() + "&recordRelId=" + recordRelId + "&recordType=" + recordType + "&method=" + method + "&recordTopic=" + taskId;
        $.pdialog.open(url, "ph015", topName,
            {mask: true, max: false, maxable: true, resizable: true, width: 800, height: 600}
        );
        stopEvent(event);
    },

    doneDay: function (obj) {
        var thisVal = $(obj).val();
        var doneVal = "P" + thisVal + "D";
        $("input[name=du]", navTab.getCurrentPanel()).val(doneVal);
    },

    /**
     * 提交谈话
     */
    submitMemo: function () {
        var $form = $("#ph015frm_spat", $.pdialog.getCurrent());
        var recordTopic = $("input[name=recordTopic]", $.pdialog.getCurrent()).val();
        if ($form.valid()) {
            // 提交页面数据
            var url = getGlobalPathRoot() + "eland/ph/ph015/ph015-saveWM.gv";
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
                    $.pdialog.closeCurrent();
                    ph015.query(recordTopic);
                } else {
                    alertMsg.error(jsonData.errMsg);
                }
            });
        }
    },
    /**
     * 显示关联文档信息
     * @param clickObj 点击对象
     */
    showDoc: function (clickObj, relId) {
        var $span = $(clickObj);
        var docIds = $span.find("input[type=hidden]").val();
        if (!docIds) {
            docIds = "";
        }

        var docTypeName = $span.attr("docTypeName");
        if (!docTypeName) {
            docTypeName = "";
        }
        docTypeName = encodeURI(encodeURI(docTypeName));
        var docTypeCd = $span.attr("docTypeCd");
        if (!docTypeCd) {
            docTypeCd = "";
        }

        var relType = $span.attr("relType");
        var editAble = $span.attr("editAble");
        if (!editAble) {
            editAble = true;
        }
        var url = getGlobalPathRoot() + "oframe/common/file/file-init.gv?docTypeName=" + docTypeName
            + "&docTypeCd=" + docTypeCd
            + "&docIds=" + docIds
            + "&editAble=" + editAble
            + "&relType=" + relType
            + "&prjCd=" + getPrjCd()
            + "&relId=" + relId;
        $.pdialog.open(url, "file", "附件管理", {
            height: 600, width: 800, mask: true,
            close: ph015.docClosed,
            param: {clickSpan: $span, editAble: editAble}
        });
    },
    /**
     * 关闭上传窗口
     * @param param
     * @returns {boolean}
     */
    docClosed: function (param) {
        var uploadedFiles = file.getAllUploadFiles();
        var $span = param.clickSpan;
        var editAble = param.editAble;
        var docIdArr = [];
        for (var i = 0; i < uploadedFiles.length; i++) {
            var uploadedFile = uploadedFiles[i];
            docIdArr.push(uploadedFile.docId);
        }
        if (editAble == true) {
            $span.find("input[type=hidden]").val(docIdArr.join(","));
            if (docIdArr.length > 0) {
                var label = $span.find("label");
                if (label.html().startWith("上传")) {
                    label.html(label.html().replace("上传", "查看"));
                }
            } else {
                var label = $span.find("label");
                if (label.html().startWith("查看")) {
                    label.html(label.html().replace("查看", "上传"));
                }
            }
        }
        // 调用文件的关闭
        return file.closeD();
    },
    /**
     *删除谈话
     */
    deleteView: function (obj, recordId) {
        var thisPanel = $(obj).closest("div.js_ph015_task", navTab.getCurrentPanel());
        var taskId = $("input[name=taskId]", thisPanel).val();
        alertMsg.confirm("您确定要删除记录吗?", {
            okCall: function () {
                var url = getGlobalPathRoot() + "eland/ph/ph015/ph015-deleteView.gv";
                var ajaxPacket = new AJAXPacket(url);
                ajaxPacket.data.add("recordId", recordId);
                ajaxPacket.data.add("prjCd", getPrjCd());
                core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                    var jsonData = eval("(" + response + ")");
                    // 服务调用是否成功
                    var isSuccess = jsonData.success;
                    if (isSuccess) {
                        alertMsg.correct("处理成功");
                        ph015.query(taskId);
                    } else {
                        alertMsg.error(jsonData.errMsg);
                    }
                });
            }
        });
        stopEvent(event);
    },
    /**
     * 单独验证有没有输入 下一步处理人
     * */
    submitVerifyassignee: function (formId, obj, jsonParam) {
        var $this = $(obj);
        var num = 0;
        $this.closest("table").find("input.js_targetPerTemp").each(function () {
            if ($(this).val() == '') {
                $(this).addClass("inputWarn");
                num++;
            } else {
                $(this).removeClass("inputWarn");
            }
        });
        if (num > 0) {
            alertMsg.warn("请指定下一步任务处理人！");
        } else {
            ph015.doTask(formId, jsonParam);
        }
    },
    /**
     * 处理任务
     * @param formId 提交的表单ID
     * @param optionData 表单附属数据
     */
    doTask: function (formId, optionData) {
        var container = navTab.getCurrentPanel();
        var jsonData = {};
        var form = $("#" + formId, container);

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
        var taskContainer = $("#" + formId, navTab.getCurrentPanel()).closest("div.panel");
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
     * 判断 下发征补决定负责人是否可以联系。
     * @param obj
     */
    sltFinshTalk: function (obj) {
        var fl = $(obj).val();
        if (fl == 'true') {
            $("tr.js_isFinPerson", navTab.getCurrentPanel()).show();
        } else {
            $("tr.js_isFinPerson", navTab.getCurrentPanel()).hide();
        }
    },
    /**
     * 判断 是否显示下一步完成时间。
     * @param obj
     */
    zbwcTime: function (obj) {
        var fl = $(obj).val();
        if (fl != 'true') {
            $("tr.js_zbwcTime", navTab.getCurrentPanel()).show();
        } else {
            $("tr.js_zbwcTime", navTab.getCurrentPanel()).hide();
        }
    }
};
