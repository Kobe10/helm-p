var pj014 = {
    openAdd: function () {
        var url = getGlobalPathRoot() + "eland/pj/pj014/pj014-info.gv?method=add" +
            "&prjCd=" + getPrjCd();
        $.pdialog.open(url, "pj01401", "外聘单位", {mask: true, max: true});
    },
    openView: function (extCmpId, prjExtCmpId) {
        var url = getGlobalPathRoot() + "eland/pj/pj014/pj014-info.gv?method=view&extCmpId=" + extCmpId + "&prjExtCmpId=" + prjExtCmpId + "&prjCd=" + getPrjCd();
        $.pdialog.open(url, "pj014", "外聘单位", {mask: true, max: true});
    },

    openEdit: function (extCmpId, prjExtCmpId) {
        var url = getGlobalPathRoot() + "eland/pj/pj014/pj014-info.gv?method=edit&extCmpId=" + extCmpId + "&prjExtCmpId=" + prjExtCmpId + "&prjCd=" + getPrjCd();
        $.pdialog.open(url, "pj014", "外聘单位", {mask: true, max: true});
    },
    save: function (method) {
        var $form = $("#pj014frm", $.pdialog.getCurrent());
        if ($form.valid()) {
            // 提交页面数据
            var url = getGlobalPathRoot() + "eland/pj/pj014/pj014-save.gv?method=" + method + "&prjCd=" + getPrjCd();
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
                if (!fileName.isImg()) {
                    imgSrc = getGlobalPathRoot() + "oframe/themes/images/word.png";
                }
                $(newSpan).find("img").attr("src", imgSrc).attr("realSrc", imgSrc).attr("title", fileName);
                $(newSpan).find("input[name=docId]").val(uploadedFile.docId);
                $(newSpan).find("input[name=docName]").val(fileName);
                $(newSpan).find("input[name=docTypeName]").val($("#" + divId).attr("docTypeName"));
                $(newSpan).find("a").attr("href", downLoadUrl).attr("title", fileName).find("span").html(fileName);
                $(newSpan).removeClass("hidden");
                $("#" + divId).append(newSpan);
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
        ;
        $.pdialog.open(url, "file", "附件上传", {mask: true,
            close: pj014.uploadLoadFileClosed,
            param: {divId: divContainerId}});
    }
};

$(document).ready(function () {
    $("#schBtn", navTab.getCurrentPanel()).trigger("click");
});
