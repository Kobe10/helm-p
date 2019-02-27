$(function () {
    $.fn.smartFloat = function () {
        var position = function (element) {
            var top = element.position().top, pos = element.css("position");
            $("#fileUpload").parent().scroll(function () {
                var scrolls = $(this).scrollTop();
                if (scrolls > top) {
                    if (window.XMLHttpRequest) {
                        element.css({
                            position: "fixed",
                            top: 0
                        });
                    } else {
                        element.css({
                            top: scrolls
                        });
                    }
                } else {
                    element.css({
                        position: pos,
                        top: top
                    });
                }
            });
        };
        return $(this).each(function () {
            position($(this));
        });
    };
    if ($("#fileUpload", $.pdialog.getCurrent()).length > 0) {
        // 初始化上传
        var subDir = $("input[name=subDir]", $.pdialog.getCurrent()).val();
        var docTypeName = $("input[name=docTypeName]", $.pdialog.getCurrent()).val();
        var docType = $("input[name=docType]", $.pdialog.getCurrent()).val();
        var options = {
            fileObjName: "uploadFile",
            multi: true,
            onUploadError: uploadifyError,
            swf: getGlobalPathRoot() + 'oframe/plugin/uploadify/scripts/uploadify.swf',
            uploader: getGlobalPathRoot() + 'oframe/common/file/file-upload.gv',
            queueID: 'fileQueue',
            buttonText: '选择文件',
            buttonImage: getGlobalPathRoot() + 'oframe/plugin/uploadify/img/add.png',
            flashImage: getGlobalPathRoot() + 'oframe/plugin/uploadify/img/trans.png',
            debug: false,
            onUploadSuccess: file.uploadifySuccess,
            onCancel: file.cancelUpload,
            fileTypeExts: docType,
            width: 102,
            auto: false,
            /*onFallback: function () {
                alertMsg.warn("需要flash支持，请确认已安装并启用!");
            },*/
            onUploadStart: function () {
                var formData = {
                    PHPSESSID: 'xxx', ajax: 1, prjCd: getPrjCd(),
                    subDir: $("input[name=subDir]", $.pdialog.getCurrent()).val(),
                    docTypeName: $("input[name=docTypeName]", $.pdialog.getCurrent()).val()
                };
                $("#fileUpload", $.pdialog.getCurrent()).uploadify("settings", "formData", formData);
            }
        };
        $("#fileUpload", $.pdialog.getCurrent()).uploadify(options);
    }
    // 清空上次上传的文件内容
    file.clearUploadFiles();
});