/**
 * User: shfb_wang
 * Date: 2016/3/12 0012 11:01
 * Copyright(c) 北京四海富博计算机服务有限公司
 */
var wf006 = {
    /**
     * 保存整个表单数据
     */
    saveForm: function () {
        //处理 前提条件 代码
        var exeFormRule = window.frames['wf006PreCondIFrame'].getCode("exeFormRule");
        $('textarea[name=exeFormRule]', navTab.getCurrentPanel()).val(exeFormRule);
        //处理 数据准备 代码
        var exeDataParam = window.frames['wf006dataPrepIFrame'].getCode("exeDataParam");
        $('textarea[name=exeDataParam]', navTab.getCurrentPanel()).val(exeDataParam);
        //处理 操作定义 代码
        var operDefCode = window.frames['wf006operDefIFrame'].getCode("operDefCode");
        $('textarea[name=operDefCode]', navTab.getCurrentPanel()).val(operDefCode);
        var formObj = $("#wf006FormDesign", navTab.getCurrentPanel());
        if (formObj.valid()) {
            var url = getGlobalPathRoot() + "oframe/wf/wf006/wf006-saveForm.gv?prjCd=" + getPrjCd();
            var ajaxPacket = new AJAXPacket(url);
            ajaxPacket.data.data = formObj.serializeArray();
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var jsonData = eval("(" + response + ")");
                if (jsonData.success) {
                    alertMsg.correct("保存成功！");
                    var resultFormId = jsonData.resultFormId;
                    //由于不关闭tab页 所以需要刷新 tab页
                    $("input[name=formId]", navTab.getCurrentPanel()).val(resultFormId);
                } else {
                    alertMsg.error(jsonData.errMsg);
                }
            }, true);
        }
    },

    /**
     * 发布说明
     */
    publishDialog: function (formId, formDesc) {
        if (!formDesc) {
            formDesc = "";
        } else {
            formDesc = encodeURI(encodeURI(formDesc));
        }
        var url = getGlobalPathRoot() + "oframe/wf/wf005/wf005-publishDialog.gv?prjCd=" + getPrjCd()
            + "&formId=" + formId
            + "&formDesc=" + formDesc;
        $.pdialog.open(url, "wf006-publishDialog", "确认发布表单", {
            height: 255, width: 800, mask: true
        });
    },

    /**
     * 发布表单
     */
    publishForm: function () {
        var $form = $("#form_publish_dialog", $.pdialog.getCurrent());
        var formId = $("input[name=dialog_formId]", $form).val();
        var formDesc = $("textarea[name=dialog_formDesc]", $form).val();
        var url = getGlobalPathRoot() + "oframe/wf/wf005/wf005-publishForm.gv?prjCd=" + getPrjCd();
        var ajaxPacket = new AJAXPacket(url);
        ajaxPacket.data.add("formId", formId);
        ajaxPacket.data.add("formDesc", formDesc);
        core.ajax.sendPacketHtml(ajaxPacket, function (response) {
            var jsonData = eval("(" + response + ")");
            if (jsonData.success) {
                alertMsg.correct("发布成功！");
                $.pdialog.closeCurrent();
            } else {
                alertMsg.error(jsonData.errMsg);
            }
        });
    },
    /**
     * 获取 编辑框内代码
     */
    getCode: function (textareaName, content) {
        if (content && content == 'navTab') {
            return $("textarea[name=" + textareaName + "]", navTab.getCurrentPanel()).val();
        } else if (content && content == 'dialog') {
            return $("textarea[name=" + textareaName + "]", $.pdialog.getCurrent()).val();
        } else {
            return "";
        }
    },
    saveTemplateText: function (obj) {
        var $form = $("#wf006EditTempForm", $.pdialog.getCurrent());
        //处理 模板 代码
        var templateText = window.frames['wf006TempIFrame'].getCode("templateText");
        $('textarea[name=templateText]', navTab.getCurrentPanel()).val(templateText);
        var formId = $("input[name=formId]", $form).val();
        var sysFormTemplateType = $("input[name=sysFormTemplateTypeOld]",  $.pdialog.getCurrent()).val();
        var oldTemplateCd = $("input[name=oldTemplateCd]", $form).val();
        var url = getGlobalPathRoot() + "oframe/wf/wf006/wf006-saveTemplateText.gv?prjCd=" + getPrjCd();
        var packet = new AJAXPacket(url);
        packet.data.add("formId", formId);
        packet.data.add("oldTemplateCd", oldTemplateCd);
        packet.data.add("templateText", templateText);
        packet.data.add("sysFormTemplateType", sysFormTemplateType);
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            if (jsonData.success) {
                alertMsg.correct("保存成功！");
                $.pdialog.closeCurrent();
            } else {
                alertMsg.error(jsonData.errMsg);
            }
        }, true);
    },

    /**
     * 模板定义增加行
     */
    addRow: function (tableId, obj) {
        var addRow = table.addRowNoInit(tableId, obj);
        // 为增加的行动态增加样式
        $("input[name=formTemplateCd]", addRow).addClass("required");
        $("input[name=formTemplateName]", addRow).addClass("required");
        $("select[name=sysFormTemplateType]", addRow).addClass("required");
        //将新增的tr动态赋值 id  和 name
        $('input[type=file]', addRow).attr("id", "uploadTempFile_" + new Date().getTime());
        $(addRow).initUI();
    },

    /**
     * 打开上传窗口 模板
     */
    uploadTemp: function (obj) {
        var thisObj = $(obj);
        $("input[name=uploadTempFile]", thisObj).unbind("change", wf006.uploadTempFile);
        $("input[name=uploadTempFile]", thisObj).val("");
        $("input[name=uploadTempFile]", thisObj).bind("change", {"clickObj": thisObj}, wf006.uploadTempFile);
    },

    /**
     * 上传 模板文件
     */
    uploadTempFile: function (event) {
        var currTr = event.data.clickObj.closest("tr");
        var sysFormTemplateType = $("select[name=sysFormTemplateType]", currTr).val();
        var uploadURL = getGlobalPathRoot() + "oframe/wf/wf006/wf006-saveUploadFile.gv";
        var fileElementId = $('input[type=file]', currTr).attr("id");
        $.ajaxFileUpload({
                url: uploadURL,
                data: {prjCd: getPrjCd(), sysFormTemplateType: sysFormTemplateType},
                secureuri: false,
                fileElementId: fileElementId,
                dataType: 'json',
                success: function (data, status, fileElementId) {
                    if (data.isSuccess) {
                        alertMsg.correct("上传成功！");
                        $("label.js_upload_time", currTr).html(data.upLoadTime);
                        $("input[name=tempFilePath]", currTr).val(data.tempFilePath);
                    } else {
                        alertMsg.error(data.errMsg);
                    }
                }
            }
        )
    },
    /**
     * 下载 模板
     */
    downTemp: function (obj) {
        var currTr = $(obj).closest("tr");
        var formId = $("input[name=formId]", navTab.getCurrentPanel()).val();
        var oldTemplateCd = $("input[name=oldTemplateCd]", currTr).val();
        var url = getGlobalPathRoot() + "oframe/wf/wf006/wf006-downTemp.gv?prjCd=" + getPrjCd() + "&formId=" + formId + "&tid=" + oldTemplateCd;
        window.open(url);
    },
    /**
     * 编辑 模板
     */
    editTemp: function (obj) {
        var container = $(obj).closest("tr");
        var oldTemplateCd = $("input[name=oldTemplateCd]", container).val();
        var formId = $("input[name=formId]", navTab.getCurrentPanel()).val();
        var sysFormTemplateType = $("input[name=sysFormTemplateTypeOld]", container).val();
        var url = getGlobalPathRoot() + "oframe/wf/wf006/wf006-editTemplate.gv?prjCd=" + getPrjCd()
            + "&formId=" + formId + "&oldTemplateCd="
            + oldTemplateCd + "&sysFormTemplateType=" + sysFormTemplateType;
        $.pdialog.open(url, "wf006-editTemp", "在线编辑模板", {max: true, mask: true});
    },

    /**
     * 打开表单类型树
     * @param obj
     */
    sltFormType: function (obj) {
        var $this = $(obj);
        var url = getGlobalPathRoot() + "oframe/wf/wf005/wf005-initTypeTree.gv?fromOp=wf006&prjCd=0";
        $this.openTip({href: url, height: "300", appendBody: true});
    },
    /**
     * 切换模板类型
     */
    chengeTemplateType: function (obj) {
        var container = $(obj).closest("tr");
        var $this = $(obj);
        $("input[name=sysFormTemplateTypeOld]",container).val($this.val());
    },

    /**
     * 隐藏操作区定义
     * @param obj
     */
    showHideOp: function (obj) {
        var $this = $(obj);
        if ($this.hasClass("close")) {
            $this.closest("table").find("tbody>tr:gt(0)").show();
            $this.removeClass("close");
        } else {
            $this.closest("table").find("tbody>tr:gt(0)").hide();
            $this.addClass("close");
        }

    }
};