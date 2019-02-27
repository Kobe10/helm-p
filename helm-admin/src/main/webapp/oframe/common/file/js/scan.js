var scan = {

    /**
     * 上传文件列表
     */
    upLoadedFiles: new Array(),

    /**
     * 设备是否打开
     */
    isOpen: false,

    /**
     * 提交上传
     */
    sendUpload: function () {
        var $form = $("#scan001", $.pdialog.getCurrent());
        if ($("input[name=upFile][value!='']", $.pdialog.getCurrent()).length == 0) {
            // 没有上传附件直接返回
            $.pdialog.closeCurrent();
        } else if ($form.valid()) {
            // 提交页面数据
            var url = getGlobalPathRoot() + "oframe/common/file/file-upbase64.gv";
            var ajaxPacket = new AJAXPacket(url);
            ajaxPacket.data.data = $form.serializeArray();
            // 提交
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var jsonData = eval("(" + response + ")");
                // 服务调用是否成功
                var isSuccess = jsonData.success;
                if (isSuccess) {
                    var fileArray = jsonData.data;
                    for (var i = 0; i < fileArray.length; i++) {
                        var tempFileItem = {};
                        tempFileItem.docId = jsonData.data[i].docId;
                        tempFileItem.fileName = jsonData.data[i].docName;
                        tempFileItem.docPath = jsonData.data[i].docPath;
                        scan.upLoadedFiles.push(tempFileItem);
                    }
                    $.pdialog.closeCurrent();
                } else {
                    alert(jsonData.errMsg);
                }
            });
        }
    },

    /**
     * 初始化页面，打开高拍仪
     */
    init: function () {
        if (scan.isOpen) {
            return;
        }
        if (!isIE()) {
            alert("文档扫描只支持IE浏览器，请使用IE浏览器！");
//            $.pdialog.closeCurrent();
            return;
        }
        // 根据js的脚本内容，必须先获取object对象
        Capture = $("#ScanCapture", $.pdialog.getCurrent())[0];
        if (!Capture) {
            return;
        }
        // 设备号
        var nDeviceIndex = parseInt($("input[name=deviceIdx]", $.pdialog.getCurrent()).val());
        // 打开文档摄像头
        var result = Capture.OpenDevice(nDeviceIndex);
        if (result == 0) {
            scan.isOpen = true;
            scan.flashText();
            scan.changeDpi(200, 200);
        } else {
            alert("镜头打开失败！");
        }
    },

    /**
     * 关闭页面，关闭高拍仪
     */
    closeScan: function () {
        if (!scan.isOpen) {
            return true;
        }
        var currentIdx = scan.getCurrentDeviceIdx();
        var result = Capture.CloseDeviceEx();

        if (result != 0) {
            alert("镜头关闭失败!");
        } else {
            Capture = null;
            scan.isOpen = false;
        }
        return true;
    },

    /**
     * 正则判断文本框中的内容是否为数字
     */
    isDigit: function (iVal) {
        var patrn = /^-?\d+(\.\d+)?$/;
        if (!patrn.exec(iVal)) {
            return false
        }
        return true
    },

    /**
     * 打开摄像头的设置界面
     */
    changeScan: function () {
        if (!scan.isOpen) {
            alert("镜头打开失败！");
            return;
        }
        var nDeviceIndex = parseInt($("input[name=deviceIdx]", $.pdialog.getCurrent()).val());
        Capture.ShowDevicePages(nDeviceIndex);
    },

    getCurrentDeviceIdx: function () {
        return parseInt($("input[name=deviceIdx]", $.pdialog.getCurrent()).val());
    },

    /**
     * 切换摄像头
     */
    changeDevice: function () {
        if (!scan.isOpen) {
            alert("镜头打开失败！");
            return;
        }
        var currentIdx = parseInt($("input[name=deviceIdx]", $.pdialog.getCurrent()).val());
        if (currentIdx == 0) {
            currentIdx = 1;
        } else {
            currentIdx = 0;
        }
        //关闭摄像头
        var result = Capture.CloseDeviceEx();
        if (result != 0) {
            alert("镜头关闭失败");
        }
        // 重新打开摄像头
        $("input[name=deviceIdx]", $.pdialog.getCurrent()).val(currentIdx);
        scan.init();
    },

    changeColorMode: function () {
        if (!scan.isOpen) {
            alert("镜头打开失败！");
            return;
        }
        var colorMode = parseInt($("input[name=colorMode]", $.pdialog.getCurrent()).val());
        colorMode++;
        if (colorMode > 2) {
            colorMode = 0;
        }
        //关闭摄像头
        if (Capture.SetColorMode(colorMode) == 0) {
            $("input[name=colorMode]", $.pdialog.getCurrent()).val(colorMode);
            scan.flashText();
        }
    },

    /**
     *
     */
    changeCutType: function () {
        if (!scan.isOpen) {
            alert("镜头打开失败！");
            return;
        }
        var cutType = parseInt($("input[name=cutType]", $.pdialog.getCurrent()).val());
        cutType++;
        if (cutType > 2) {
            cutType = 0;
        }
        scan.cutType(cutType);
    },

    /**
     * 设置拍照存档的xDPI,yDPI
     */
    changeDpi: function (XdpiValue, YdpiValue) {
        if (!scan.isOpen) {
            alert("镜头打开失败！");
            return;
        }
        var nDeviceIndex = parseInt($("input[name=deviceIdx]", $.pdialog.getCurrent()).val());
        if (XdpiValue == "" || YdpiValue == "") {
            alert("X,Y方向的DPI值都不能为空！");
            return;
        }
        if (scan.isDigit(XdpiValue) && scan.isDigit(YdpiValue)) {
            xDpi = parseInt(XdpiValue);
            yDpi = parseInt(YdpiValue);
            if (Capture.SetGrabbedDPI(xDpi, yDpi) != 0) {
                alert("DPI设置失败");
            } else {
                scan.flashText();
            }
        } else {
            alert("含有非法字符，请重新输入数字！");
        }
    },

    /**
     * 旋转摄像头
     * @param rotation 旋转角度
     */
    changeRotation: function (rotation) {
        if (!scan.isOpen) {
            alert("镜头打开失败！");
            return;
        }
        var nRotation = parseInt(rotation);
        if (nRotation % 90 != 0) {
            Capture.SetDeviceRotate(nDeviceIndex, 0);
        }
        var result = Capture.SetDeviceRotate(nDeviceIndex, nRotation);
        if (result != 0) {
            alert("旋转镜头失败");
        }
    },

    //拍照并且存档,若勾选条码则一起获取条码信息
    catchPic: function () {
        if (!scan.isOpen) {
            alert("镜头打开失败！");
            return;
        }
        var saveFilePath = $("input[name=fileDir]", $.pdialog.getCurrent()).val();
        var fileName = (new Date()).getTime() + ".jpg";
        var szFileName = saveFilePath + fileName;
        var base64Str = Capture.GetBase64String();
        scan.addPreview(base64Str, fileName);
    },

    /**
     * 切边类型
     * @param szMode 设置切边类型
     */
    cutType: function (szMode) {
        if (!scan.isOpen) {
            alert("镜头打开失败！");
            return;
        }
        var nDeviceIndex = parseInt($("input[name=deviceIdx]", $.pdialog.getCurrent()).val());
        var result01;
        if (szMode == 0) {
            result01 = Capture.SetCutPageType(nDeviceIndex, 0);
        } else if (szMode == 1) {
            result01 = Capture.SetCutPageType(nDeviceIndex, 1);
        } else if (szMode == 2) {
            result01 = Capture.SetCutPageType(nDeviceIndex, 2);
        } else {
            result01 = Capture.SetCutPageType(nDeviceIndex, 0);
        }
        if (result01 == 0) {
            $("input[name=cutType]", $.pdialog.getCurrent()).val(szMode);
            scan.flashText();
        } else {
            alert("切边模式切换失败!");
        }
    },

    /**
     * 预览图片
     * @param fullFile 图片文件名称
     * @param fileName 文件名称
     * @param fileBase64 文件的Base64编码
     */
    addPreview: function (fileBase64, fileName) {
        var ulContainer = $("#picUl", $.pdialog.getCurrent());
        var liTemplate = $("li.hidden", ulContainer);
        // 拷贝信息的
        var newSpanText = $("#scanTemplate", $.pdialog.getCurrent());
        var newObj = liTemplate.clone().append(newSpanText.html());
        var pic = $("img", newObj);
        //var file = document.getElementById("f");
        var ext = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        // gif在IE浏览器暂时无法显示
        if (ext != 'png' && ext != 'jpg' && ext != 'bmp' && ext != 'tif') {
            alert("文件必须为图片！");
            return;
        }
        // IE浏览器
        if (document.all) {
            // 设置img的src为base64编码的透明图片 取消显示浏览器默认图片
            pic.attr("src", 'data:image/jpeg;base64,' + fileBase64);
            pic.attr("realSrc", 'data:image/jpeg;base64,' + fileBase64);
        }
        // 删除文件
        $("input[name=fileName]", newObj).val(fileName).addClass("required");
        $("input[name=upFile]", newObj).val(fileBase64);
        ulContainer.append(newObj);
        newObj.removeClass("hidden");
    },

    /**
     * 刷新状态栏
     */
    flashText: function () {
        // 稳当类型
        var nDeviceIndex = parseInt($("input[name=deviceIdx]", $.pdialog.getCurrent()).val());
        if (nDeviceIndex == 0) {
            $("span.deviceType", $.pdialog.getCurrent()).html("[文档镜头]");
        } else if (nDeviceIndex == 1) {
            $("span.deviceType", $.pdialog.getCurrent()).html("[人像镜头]");
        } else {
            $("span.deviceType", $.pdialog.getCurrent()).html("");
        }
        // 切边模式
        var cutType = parseInt($("input[name=cutType]", $.pdialog.getCurrent()).val());
        if (cutType == 0) {
            $("span.cutType", $.pdialog.getCurrent()).html("[不切边]");
        } else if (cutType == 1) {
            $("span.cutType", $.pdialog.getCurrent()).html("[自动切边]");
        } else if (cutType == 2) {
            $("span.cutType", $.pdialog.getCurrent()).html("[自定义切边]");
        } else {
            $("span.cutType", $.pdialog.getCurrent()).html("");
        }
        // 色彩模式
        var colorMode = parseInt($("input[name=colorMode]", $.pdialog.getCurrent()).val());
        if (colorMode == 0) {
            $("span.colorMode", $.pdialog.getCurrent()).html("[彩色]");
        } else if (colorMode == 1) {
            $("span.colorMode", $.pdialog.getCurrent()).html("[灰度]");
        } else if (colorMode == 2) {
            $("span.colorMode", $.pdialog.getCurrent()).html("[黑白]");
        } else {
            $("span.colorMode", $.pdialog.getCurrent()).html("");
        }
    },

    /**
     * 删除图片
     * @param obj 点击对象
     */
    removePic: function (obj) {
        $(obj).parent().parent().remove();
    },
    getUploadFiles: function () {
        return scan.upLoadedFiles;
    }
}