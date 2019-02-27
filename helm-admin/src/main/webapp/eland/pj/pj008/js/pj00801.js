var pj00801 = {

    /**
     * 点击开始上传文件
     */
    startImport: function () {
        $("#uploadFile", $.pdialog.getCurrent()).val("");
        $("#uploadFile", $.pdialog.getCurrent()).unbind("change", pj00801.importFile);
        $("#uploadFile", $.pdialog.getCurrent()).bind("change", pj00801.importFile);
    },

    /**
     * Excel 导入 Person
     */
    importFile: function (obj) {
        var uploadURL = getGlobalPathRoot() + "oframe/common/file/file-upload.gv?prjCd=" + getPrjCd() + "&ruleTypeId=400";
        $.ajaxFileUpload({
                url: uploadURL,
                secureuri: false,
                fileElementId: "uploadFile",
                dataType: 'json',
                data: {},
                success: function (data, status, fileElementId) {
                    if (data.success) {
                        // 调用服务增加服务节点
                        var uploadedFile = data.data[0];
                        var text = "<img height='100%' width='100%' onclick='pj00801.viewImg();' src=\"" +
                            getGlobalPathRoot() + "oframe/common/file/file-downview.gv?docId=" + uploadedFile.docId + "\" title=\"" + uploadedFile.fileName + "\">";
                        $("#pj00801drag", $.pdialog.getCurrent()).html(text);
                        $("input[name=hxImgPath]", $.pdialog.getCurrent()).val(uploadedFile.docId);
                    } else {
                        alertMsg.error(data.errMsg);
                    }
                }
            }
        )
    },


    openUploadFile: function () {
        var url = getGlobalPathRoot() + "oframe/common/file/file-init.gv?type=*.jpg;*.bmp;*.png;*.gif;";
        $.pdialog.open(url, "file", "附件上传", {mask: true, close: pj008.uploadLoadFileClosed});
    },

    /**
     * 实现文件的拖拽上传
     * @param event 拖拽事件
     */
    dropFile: function (event) {
        if (typeof FileReader == "undefined") {
            alertMsg.error("您的浏览器不支持拖拽上传");
        }
        var dragFile = event.dataTransfer.files;
        if (dragFile.length != 1) {
            alertMsg.error("只支持当个文件的上传");

        } else if (dragFile[0].type.indexOf('image') === -1) {
            alertMsg.error("请上传图片文件");
        } else {
            var tempFile = dragFile[0];
            var reader = new FileReader();
            reader.readAsDataURL(tempFile);
            reader.onload = (function (file) {
                return function (e) {
                    $(".drag", $.pdialog.getCurrent()).html("<img height='100%' onclick='pj00801.viewImg();' width='100%' src=\""
                        + e.target.result + "\" title=\"" + file.name + "\">");
                }
            })(tempFile);
            //上传文件到服务器
            var fd = new FormData();
            fd.append('uploadFile', tempFile);
            pj008.sendFileToServer(fd);
        }
        // 停止响应事件
        stopEvent(event);
    },

    sendFileToServer: function (formData) {
        var uploadURL = getGlobalPathRoot() + "oframe/common/file/file-upload.gv";
        //Extra Data.
        var extraData = {};
        var jqXHR = $.ajax({
            xhr: function () {
                var xhrObj = $.ajaxSettings.xhr();
                return xhrObj;
            },
            url: uploadURL,
            type: "POST",
            contentType: false,
            processData: false,
            cache: false,
            data: formData,
            success: function (data) {
                var jsonData = eval("(" + data + ")");
                if (jsonData.success) {
                    $("input[name=hxImgPath]", $.pdialog.getCurrent()).val(jsonData.data[0].docId);
                    $(".drag img", $.pdialog.getCurrent()).attr("docId", jsonData.data[0].docId);
                } else {
                    alertMsg.error(jsonData.errMsg);
                }
            }
        });
    },

    /**
     * 更新户型信息
     */
    saveHx: function () {
        var $form = $("#pj00801frm", $.pdialog.getCurrent());
        if ($form.valid()) {
            var method = $("input[name=method]", $.pdialog.getCurrent()).val();
            var url = getGlobalPathRoot() + "eland/pj/pj008/pj008-save.gv?prjCd=" + getPrjCd();
            var ajaxPacket = new AJAXPacket(url);
            ajaxPacket.data.data = $form.serializeArray();
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var jsonData = eval("(" + response + ")");
                // 服务调用是否成功
                var isSuccess = jsonData.success;
                if (isSuccess) {
                    $.pdialog.closeCurrent();
                } else {
                    alertMsg.error(jsonData.errMsg);
                }
            }, false);
        }
    },
    /**
     * 重新计算户型总房款
     */
    calculateTotalPrice: function () {
        var hxBldSize = $("input[name=hxBldSize]", $.pdialog.getCurrent()).val();
        var hxSalePrice = $("input[name=hxSalePrice]", $.pdialog.getCurrent()).val();
        $("#pj008TotalPrice", $.pdialog.getCurrent()).html((hxBldSize * hxSalePrice).toFixed(2));
    },

    /**
     * 点击查看图片
     * @param obj 点击对象
     */
    viewImg: function (obj) {
        var docId = $("input[name=hxImgPath]", $.pdialog.getCurrent()).val();
        if (docId != "") {
            ImgPage.viewPicsDiag(docId, docId);
        }
    },
};
