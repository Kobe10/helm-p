var pj01102 = {
    save: function () {
        var $form = $("#pj01102Frm", $.pdialog.getCurrent());
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
                } else {
                    alertMsg.error(jsonData.errMsg);
                }
            });
        }
    },
    deleteNotice: function (obj) {
        var noticeIds = new Array();
        var prjCds = new Array();
        $(obj).closest("div.pageContent")
            .find(":checkbox[checked][name=noticeId]").each(
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
                    var url = getGlobalPathRoot() + "eland/pj/pj01102/pj01102-deleteNotice.gv";
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
        $.pdialog.open(url, "file", "附件上传", {mask: true,
            close: pj01102.uploadLoadFileClosed,
            param: {divId: divContainerId}});
    }
};

$(function () {
    $("#schBtn", navTab.getCurrentPanel()).trigger("click");
});