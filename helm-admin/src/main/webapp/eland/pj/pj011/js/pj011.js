var pj011 = {
    /**
     * 打开快速检索对话框
     */
    openQSch: function (obj) {
        var div = $("#pj011QSchDialog", navTab.getCurrentPanel());
        var active = div.attr("active");
        if ("on" == active) {
            div.hide().removeAttr("active");
        } else {
            div.show().attr("active", "on");
        }
    },
    openAdd: function (noticeType) {
        var url = getGlobalPathRoot() + "eland/pj/pj011/pj011-info.gv?method=add" +
            "&prjCd=" + getPrjCd() +
            "&noticeType=" + noticeType;
        $.pdialog.open(url, "pj01101", "公告信息", {mask: true, max: true});
    },
    openEdit: function (noticeId) {
        var url = getGlobalPathRoot() + "eland/pj/pj011/pj011-info.gv?method=edit&noticeId=" + noticeId;
        $.pdialog.open(url, "pj01101", "公告信息", {mask: true, max: true});
    },
    openView: function (noticeId) {
        var url = getGlobalPathRoot() + "eland/pj/pj011/pj011-info.gv?method=view&noticeId=" + noticeId;
        $.pdialog.open(url, "pj01101", "公告信息", {mask: true, max: true});
    },
    save: function () {
        var $form = $("#pj01101frm", $.pdialog.getCurrent());
        if ($form.valid()) {
            // 提交页面数据
            var url = getGlobalPathRoot() + "eland/pj/pj011/pj011-save.gv";
            var ajaxPacket = new AJAXPacket(url);
            ajaxPacket.data.data = $form.serializeArray();
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var jsonData = eval("(" + response + ")");
                // 服务调用是否成功
                var isSuccess = jsonData.success;
                if (isSuccess) {
                    alertMsg.correct("处理成功");
                    $("#schBtn", navTab.getCurrentPanel()).trigger("click");
                    $.pdialog.closeCurrent();
                } else {
                    alertMsg.error(jsonData.errMsg);
                }
            });
        }
    },
    deleteNotice: function (obj) {
        var noticeIds = new Array();
        var prjCds = new Array();
        $("input[name=noticeId][type=checkbox]:checked",navTab.getCurrentPanel()).each(
            function (i) {
                noticeIds.push($(this).val());
                prjCds.push(getPrjCd());
            }
        );
        if (noticeIds.length == 0) {
            alertMsg.warn("请选择要删除的记录");
        } else {
            alertMsg.confirm("您确定要删除记录吗?", {
                okCall: function () {
                    var url = getGlobalPathRoot() + "eland/pj/pj011/pj011-deleteNotice.gv";
                    var ajaxPacket = new AJAXPacket(url);
                    ajaxPacket.data.add("noticeIds", noticeIds.toString());
                    ajaxPacket.data.add("prjCds", prjCds.toString());
                    core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                        var jsonData = eval("(" + response + ")");
                        // 服务调用是否成功
                        var isSuccess = jsonData.success;
                        if (isSuccess) {
                            alertMsg.correct("处理成功");
                            $("#schBtn", navTab.getCurrentPanel()).trigger("click");
                        } else {
                            alertMsg.error(jsonData.errMsg);
                        }
                    });
                }
            });
        }
    },

    /**
     * 关闭上传窗口
     * @param param
     * @returns {boolean}
     */
    uploadLoadFileClosed: function (param) {
        var uploadedFiles = file.getUploadFiles();
        if (file.uploadSuccess) {
            var divId = param.divId;
            var hiddenSpan = $("#" + divId).find("label.hidden");
            for (var i = 0; i < uploadedFiles.length; i++) {
                var uploadedFile = uploadedFiles[i];
                var newSpan = hiddenSpan.clone();
                var downLoadUrl = getGlobalPathRoot() + "oframe/common/file/file-download.gv?docId=" + uploadedFile.docId;
                var imgSrc = downLoadUrl;
                var fileName = uploadedFile.fileName;

                $(newSpan).find("img").attr("src", imgSrc).attr("realSrc", imgSrc).attr("title", fileName);
                $(newSpan).find("input[name=docId]").val(uploadedFile.docId);
                $(newSpan).find("input[name=docName]").val(fileName);
                $(newSpan).find("input[name=docTypeName]").val($("#" + divId).attr("docTypeName"));
                $(newSpan).find("input[name=noticePicCover]").val(uploadedFile.docId);
                $(newSpan).find("input[name=noticePicId]").attr("name", "noticePicId" + uploadedFile.docId);
                $(newSpan).find("textarea[name=noticePicDesc]").attr("name", "noticePicDesc" + uploadedFile.docId)
                    .addClass("editor").addClass("simpleEditor");
                $(newSpan).find("a").attr("href", downLoadUrl).attr("title", fileName).find("span").html(fileName);
                $(newSpan).removeClass("hidden");
                $("#" + divId).append(newSpan);
                initUI(newSpan);
            }
        }
        return true;
    },

    openUploadFile: function (divContainer) {
        var divContainerId = divContainer;
        var url = getGlobalPathRoot() + "oframe/common/file/file-init.gv?docTypeName="
            + $("#" + divContainerId).attr("docTypeName")
            + "&relType=" + $("#" + divContainerId).attr("relType")
            + "&relId=" + $("#" + divContainerId).attr("relId");
        $.pdialog.open(url, "file", "附件上传", {
            mask: true,
            close: pj011.uploadLoadFileClosed,
            param: {divId: divContainerId}
        });
    }
};

$(function () {
    Query.queryList('pj011frm', 'pj011_w_list_print');
});