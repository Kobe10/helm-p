/**文件上传及文件扫描 **/
var file = {
    /**
     * 标记是否处理成功
     */
    upLoadedFiles: [],


    /**
     * 默认文件分类获取方法
     * @return {string}
     */
    defaultDocTypeRelUrl: function () {
        var docRelType = $("#uploadDocTypeName", $.pdialog.getCurrent()).attr("typeItemCd");
        return getGlobalPathRoot() + "oframe/sysmg/sys005/sys005-data.gv?prjCd=" + getPrjCd() + "&itemCd=" + docRelType;
    },

    /**
     * 扩展的获取URL路径方法
     */
    getDocTypeRelUrl: function () {
        var docRelType = $("#uploadDocTypeName", $.pdialog.getCurrent()).attr("typeItemCd");
        return getGlobalPathRoot() + "oframe/sysmg/sys005/sys005-data.gv?prjCd=" + getPrjCd() + "&itemCd=" + docRelType;
    },

    /**
     * 成功上传提交回调
     */
    successCallBack: null,

    /**
     * 打开文件上传对话框(不允许选分类，但是可以选择已有）
     * @param clickObj 点击对象
     * @param relType 关联类型
     * @param relId 关联对象编号
     * @param docTypeName 文档分类
     * @param prjCd 项目编号
     * @param docIds 已关联的文件编号
     * @param editAble 是否允许删除附件
     *  @param subDir 子目录路径
     */
    openSUpload: function (clickObj, relType, relId, docTypeName, prjCd, docIds, editAble, subDir) {
        var url = getGlobalPathRoot() + "oframe/common/file/file-init.gv?prjCd=" + prjCd + "&docTypeName=" + docTypeName +
            "&docIds=" + docIds + "&editAble=" + editAble
            + "&relType=" + relType + "&relId=" + relId + "&subDir=" + subDir;
        $.pdialog.open(url, "file", "附件上传", {
            height: 600, width: 800, mask: true,
            param: {clickSpan: clickObj}
        });
    },

    /**
     * 打开上传对话框（可选择关联分类）
     * @param clickObj 点击对象
     * @param relType 关联类型
     * @param relId 关联对象编号
     * @param docTypeName 默认文档分类
     * @param prjCd 项目编号
     * @param docIds 已关联的文件编号
     * @param editAble 是否允许删除附件
     * @param subDir 子目录路径
     * @param typeItemCd  文档分类码表
     *
     */
    openAUpload: function (clickObj, relType, relId, docTypeName, prjCd, docIds, editAble, subDir, typeItemCd) {
        var url = getGlobalPathRoot() + "oframe/common/file/file-uploadFile.gv?prjCd=" + prjCd + "&docTypeName=" + docTypeName +
            "&docIds=" + docIds + "&editAble=" + editAble + "&typeItemCd=" + typeItemCd
            + "&relType=" + relType + "&relId=" + relId + "&subDir=" + subDir;
        $.pdialog.open(url, "file", "附件上传", {
            height: 600, width: 800, mask: true,
            param: {clickSpan: clickObj}
        });
    },

    /**
     * 选择文件类型上传附件,适用于多文件上传
     * @param clickObj 点击对象
     */
    startUpload: function (clickObj) {
        var closeTable = $(clickObj).closest("table");
        var docTypeName = closeTable.find("input[name=docTypeName]").val();
        if (docTypeName != "") {
            $('#fileUpload').uploadify('upload', "*")
        } else {
            alertMsg.warn("附件类型必须选择");
        }
    },

    /**
     * 户附件类型信息
     * @param obj
     * @returns map
     */
    getDocTypeRelOpt: function (obj) {
        return {
            processData: function (result) {
                var myData = [];
                for (i = 0; i < result.length; i++) {
                    var dataRow = {};
                    dataRow.data = result[i];
                    dataRow.value = result[i].value;
                    myData.push(dataRow);
                }
                return myData;
            },
            showResult: function (value, data) {
                return data.value;
            },
            displayValue: function (value, data) {
                return data.value;
            },
            onItemSelect: function (obj) {
                $("input[name=docTypeName]", $.pdialog.getCurrent()).val(obj.data.value);
            },
            remoteDataType: "json",
            delay: 300,
            minChars: 0,
            sortResults: false,
            filterResults: false
        }
    },

    /**
     * 关闭
     */
    closeD: function () {
        return scan.closeScan();
    },


    /**
     * 获取所有在上传区域的文件列表
     * @returns {Array}
     */
    getAllUploadFiles: function () {
        var temp = [];
        $("div.js_upload span.js_pic_span", $.pdialog.getCurrent()).each(function () {
            var $this = $(this);
            var docId = $this.attr("docId");
            if (docId != "") {
                var tempFileItem = {};
                tempFileItem.docId = docId;
                tempFileItem.fileName = $this.attr("docName");
                tempFileItem.docPath = $this.attr("docPath");
                temp.push(tempFileItem);
            }
        });
        $("div.js_uploaded span.js_pic_span", $.pdialog.getCurrent()).each(function () {
            var $this = $(this);
            var checked = $this.find("input[type=checkbox]").attr("checked");
            if (checked) {
                var docId = $this.attr("docId");

                if (docId != "") {
                    var tempFileItem = {};
                    tempFileItem.docId = docId;
                    tempFileItem.fileName = $this.attr("docName");
                    tempFileItem.docPath = $this.attr("docPath");
                    temp.push(tempFileItem);
                }
            }
        });
        // 获取上传的图片列表
        var scanFiles = scan.getUploadFiles();
        for (var i = 0; i < scanFiles.length; i++) {
            var tempFileItem = {};
            tempFileItem.docId = scanFiles[i].docId;
            tempFileItem.fileName = scanFiles[i].fileName;
            tempFileItem.docPath = scanFiles[i].docPath;
            temp.push(tempFileItem);
        }
        // 返回所有上传的文件列表
        return temp;
    },

    /**
     * 标记是否处理成功
     */
    uploadSuccess: false,

    /**
     * 文件上传成功
     */
    uploadifySuccess: function (upfile, data, response) {
        var jsonData = eval("(" + data + ")");
        var docId = jsonData.data[0].docId;
        var hiddenSpan = $("#uploadContain").find("span.hidden");
        var newSpan = hiddenSpan.clone();
        $(newSpan).find("a span").html(upfile.name);
        $(newSpan).find("img").attr("docId", docId);
        $(newSpan).attr("docId", docId);
        if (upfile.name.isImg()) {
            $(newSpan).find("img").attr("src", getGlobalPathRoot() + "oframe/common/file/file-download.gv?docId=" + docId);
        }
        // 保存到上传文件数组中
        var tempFileItem = {};
        tempFileItem.docId = docId;
        tempFileItem.fileName = upfile.name;
        tempFileItem.docPath = jsonData.data[0].docPath;
        // 设置到标签保存
        $(newSpan).attr("docId", tempFileItem.docId);
        $(newSpan).attr("docName", tempFileItem.fileName);
        $(newSpan).attr("docPath", tempFileItem.docPath);
        // 增加显示
        $(newSpan).removeClass("hidden");
        hiddenSpan.parent().append(newSpan);
        // 增加到保存区
        file.upLoadedFiles.push(tempFileItem);
    },
    /**
     *  保存上传的数据
     */
    saveUpFile: function () {
        // 扫描文件
        var jsonData = {};
        var $form = $("#scan001", $.pdialog.getCurrent());
        if ($form.length > 0 && $form.valid()) {
            jsonData = $form.serializeArray();
        }

        // 上传文件
        var docIdArr = [];
        for (var i = 0; i < file.upLoadedFiles.length; i++) {
            docIdArr.push(file.upLoadedFiles[i].docId);
        }
        // 文档为空返回
        if (docIdArr.length == 0 && jsonData.length == 0) {
            // 文档关联
            file.getDocTypeRelUrl = file.defaultDocTypeRelUrl;
            // 提交回调
            file.successCallBack = null;
            // 关闭镜头
            scan.closeScan();
            // 关闭当前框
            $.pdialog.closeCurrent();
            return;
        }

        jsonData.push({"name": "docId", "value": docIdArr.join(",")});
        jsonData.push({"name": "docTypeName", "value": $("input[name=docTypeName]", $.pdialog.getCurrent()).val()});
        jsonData.push({"name": "relType", "value": $("input[name=relType]", $.pdialog.getCurrent()).val()});
        jsonData.push({"name": "relId", "value": $("input[name=relId]", $.pdialog.getCurrent()).val()});
        jsonData.push({"name": "prjCd", "value": getPrjCd()});

        // 提交页面数据
        var url = getGlobalPathRoot() + "oframe/common/file/file-updateFileRel.gv";
        if (docIdArr.length == 0) {
            url = getGlobalPathRoot() + "oframe/common/file/file-upbase64.gv";
        }
        var ajaxPacket = new AJAXPacket(url);
        ajaxPacket.data.data = jsonData;
        // 提交数据
        core.ajax.sendHPacket(ajaxPacket, function (response) {
            var jsonData = eval("(" + response + ")");
            // 服务调用是否成功
            var isSuccess = jsonData.success;
            if (isSuccess) {
                // 标记文件上传成功
                file.uploadSuccess = true;
                // 获取scan上传的文件
                var fileArray = jsonData.data;
                for (var i = 0; i < fileArray.length; i++) {
                    var tempFileItem = {};
                    tempFileItem.docId = jsonData.data[i].docId;
                    tempFileItem.fileName = jsonData.data[i].docName;
                    tempFileItem.docPath = jsonData.data[i].docPath;
                    scan.upLoadedFiles.push(tempFileItem);
                }
                // 提交成功进行回调
                var callBackResult = true;
                if ($.isFunction(file.successCallBack)) {
                    callBackResult = file.successCallBack(file.getAllUploadFiles());
                }
                if (callBackResult) {
                    // 文档关联
                    file.getDocTypeRelUrl = file.defaultDocTypeRelUrl;
                    // 提交回调
                    file.successCallBack = null;
                    // 关闭镜头
                    scan.closeScan();
                    // 关闭返回处理结果
                    $.pdialog.closeCurrent();
                } else {
                    return false;
                }

            } else {
                // 错误提示
                alertMsg.error(jsonData.errMsg);
            }
        });
    },

    /**
     * 删除上传的文件
     * @param obj
     */
    removeFile: function (obj) {
        $(obj).parent().remove();
    },

    /**
     * 查看图片
     * @param obj
     */
    viewImg: function (obj) {
        var src = $(obj).attr("src");
        if ("" != src) {
            $(obj).parents("div:eq(0)").find("img.selected").removeClass("selected");
            $(obj).addClass("selected");
            window.open(src);
        }
    },
    /**
     * 下载文件
     * @param docId 文件编号
     */
    viewPdf: function (obj) {
        var docId = $(obj).attr("docId");
        window.open(getGlobalPathRoot() + "oframe/common/file/file-viewPdf.gv?prjCd=" + getPrjCd() + "&docId=" + docId);
    },

    removeImg: function (obj) {
        var src = $(obj).parent().find("img").attr("src");
        if (src != null) {
            var showSrc = $("#file_imgViewer").attr("src");
            if (showSrc == src) {
                $("#file_imgViewer").attr("src", "").hide();
            }
        }
        var docId = $(obj).closest("span.js_pic_span").attr("docId");
        if (docId) {
            alertMsg.confirm("确定删除该附件吗？", {
                okCall: function () {
                    var packet = new AJAXPacket();
                    packet.data.add("docId", docId);
                    packet.url = getGlobalPathRoot() + "/oframe/common/file/file-delete.gv";
                    core.ajax.sendPacketHtml(packet, function (response) {
                        var jsonData = eval("(" + response + ")");
                        // 服务调用是否成功
                        var isSuccess = jsonData.success;
                        if (isSuccess) {
                            //删除展示框。
                            $(obj).closest("span.js_pic_span").remove();
                        } else {
                            // 错误提示
                            alertMsg.error(jsonData.errMsg);
                        }
                    }, true);
                }
            });
        }
    },

    getUploadFiles: function () {
        return file.upLoadedFiles;
    },

    clearUploadFiles: function () {
        file.upLoadedFiles = [];
    },

    cancelUpload: function () {
        return true;
    },
    /**
     * 切换选择模式
     * @param modelFlag
     */
    switchModel: function (modelFlag) {
        if ("1" == modelFlag) {
            // 选择已有模式
            $("div.js_upload", $.pdialog.getCurrent()).addClass("hidden");
            $("div.js_uploaded", $.pdialog.getCurrent()).removeClass("hidden");
            $("div.js_scan", $.pdialog.getCurrent()).addClass("hidden");
            // 刷新文件选择
            file.flashPic();
        } else if ("2" == modelFlag) {
            // 选择已有模式
            $("div.js_upload", $.pdialog.getCurrent()).addClass("hidden");
            $("div.js_uploaded", $.pdialog.getCurrent()).addClass("hidden");
            $("div.js_scan", $.pdialog.getCurrent()).removeClass("hidden");
            // 刷新文件选择
            setTimeout(scan.init, 500);
        } else {
            $("div.js_upload", $.pdialog.getCurrent()).removeClass("hidden");
            $("div.js_uploaded", $.pdialog.getCurrent()).addClass("hidden");
            $("div.js_scan", $.pdialog.getCurrent()).addClass("hidden");
        }
    },
    /**
     * 选择已有图片模式数据更新
     */
    flashPic: function () {
        var sltItems = file.getAllUploadFiles();
        // 转换为Map处理
        var tempMap = new Map();
        for (var i = 0; i < sltItems.length; i++) {
            tempMap.put(sltItems[i].docId, sltItems[i]);
        }
        // 选择的类型
        var sltDocTypeName = $("select[name=sltDocTypeName]", $.pdialog.getCurrent()).val();
        $("div.js_uploaded span.js_pic_span", $.pdialog.getCurrent()).each(function () {
            var $this = $(this);
            var docId = $this.attr("docId");
            var docTypeName = $this.attr("docTypeName");
            if (sltDocTypeName != "") {
                if (sltDocTypeName == docTypeName) {
                    $this.removeClass("hidden");
                } else {
                    $this.addClass("hidden");
                }
            } else {
                $this.removeClass("hidden");
            }
            if (tempMap.get(docId)) {
                $this.find("input[type=checkbox]").attr("checked", true);
            } else {
                $this.find("input[type=checkbox]").removeAttr("checked");
            }
        });
    },

    /**
     * 关闭文件上传，关闭镜头
     */
    closeUpFile: function () {
        if (scan.isOpen) {
            scan.closeScan();
        }
        scan.clearUploadFiles();
        file.clearUploadFiles();
        // 文档关联
        file.getDocTypeRelUrl = file.defaultDocTypeRelUrl;
        // 提交回调
        file.successCallBack = null;
    }
};

var scan = {

    /**
     * 上传文件列表
     */
    upLoadedFiles: [],

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
            var jsonData = $form.serializeArray();
            jsonData.push({"name": "prjCd", "value": getPrjCd()});
            ajaxPacket.data.data = jsonData;
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
            // 标记镜头已经打开
            scan.isOpen = true;
            // 设置图片质量
            Capture.SetJPGQuality(70);
            // 调整切边模式
            scan.cutType(parseInt($("input[name=cutType]", $.pdialog.getCurrent()).val()));
            // 默认选择90度
            scan.changeRotation(90);
            // 设置DPI
            scan.changeDpi(200);
        } else {
            alertMsg.error("镜头打开失败！错误编码：" + result);
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
        var result = 0;
        if (!scan.isDigit(currentIdx)) {
            // 全部执行一次关闭
            Capture.CloseDeviceEx();
        } else {
            result = Capture.CloseDeviceEx();
        }
        if (result != 0) {
            alertMsg.error("镜头关闭失败!错误编码：" + result);
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
            alertMsg.error("镜头打开失败！");
            return;
        }
        var nDeviceIndex = parseInt($("input[name=deviceIdx]", $.pdialog.getCurrent()).val());
        Capture.ShowDevicePages(nDeviceIndex);
    },

    /**
     * 重启镜头
     */
    resetScan: function () {
        scan.closeScan();
        scan.init();
    },

    getCurrentDeviceIdx: function () {
        return parseInt($("input[name=deviceIdx]", $.pdialog.getCurrent()).val());
    },

    /**
     * 切换摄像头
     */
    changeDevice: function () {
        if (!scan.isOpen) {
            alertMsg.error("镜头打开失败！");
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
            alertMsg.error("镜头关闭失败！错误编码：" + result);
        }
        // 镜头已经关闭
        scan.isOpen = false;
        // 重新打开摄像头
        $("input[name=deviceIdx]", $.pdialog.getCurrent()).val(currentIdx);
        scan.init();
    },

    changeColorMode: function () {
        if (!scan.isOpen) {
            alertMsg.error("镜头打开失败！");
            return;
        }
        var nDeviceIndex = parseInt($("input[name=deviceIdx]", $.pdialog.getCurrent()).val());
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
            alertMsg.error("镜头打开失败！");
            return;
        }
        var nDeviceIndex = parseInt($("input[name=deviceIdx]", $.pdialog.getCurrent()).val());
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
    changeDpi: function (dpiValue) {
        if (!scan.isOpen) {
            alertMsg.error("镜头打开失败！");
            return;
        }
        var nDeviceIndex = parseInt($("input[name=deviceIdx]", $.pdialog.getCurrent()).val());
        if (Capture.OpenDevice(nDeviceIndex) != 1) {
            return;
        }
        if (Capture.OpenDevice(nDeviceIndex) == 1) {
            if (dpiValue == "") {
                alertMsg.error("DPI值都不能为空！");
                return;
            }
            if (scan.isDigit(dpiValue)) {
                var dpi = parseInt(dpiValue);
                if (Capture.SetGrabbedDPIEx(dpi) != 0) {
                    alertMsg.error("DPI设置失败");
                } else {
                    scan.flashText();
                }
            } else {
                alertMsg.error("含有非法字符，请重新输入数字！");
            }
        } else {
            alertMsg.error("找不到设备或打开失败！");
        }
    },

    /**
     * 旋转摄像头
     * @param rotation 旋转角度
     */
    changeRotation: function (rotation) {
        if (!scan.isOpen) {
            alertMsg.error("镜头打开失败！");
            return;
        }
        var nDeviceIndex = parseInt($("input[name=deviceIdx]", $.pdialog.getCurrent()).val());
        var nRotation = parseInt(rotation);
        if (nRotation % 90 != 0) {
            Capture.SetDeviceRotate(nDeviceIndex, 0);
        }
        var result = Capture.SetDeviceRotate(nDeviceIndex, nRotation);
        if (result != 0) {
            alertMsg.error("旋转镜头失败");
        }
    },

    //拍照并且存档,若勾选条码则一起获取条码信息
    catchPic: function () {
        if (!scan.isOpen) {
            alertMsg.error("镜头打开失败！");
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
            alertMsg.error("镜头打开失败！");
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
            alertMsg.error("切边模式切换失败!");
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
            alertMsg.error("文件必须为图片！");
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
        var menuContain = $("ul.js_scan_menu");
        if (nDeviceIndex == 0) {
            $("span.deviceType", menuContain).html("[文档镜头]");
        } else if (nDeviceIndex == 1) {
            $("span.deviceType", menuContain).html("[人像镜头]");
        } else {
            $("span.deviceType", menuContain).html("");
        }
        // 切边模式
        var cutType = parseInt($("input[name=cutType]", $.pdialog.getCurrent()).val());
        if (cutType == 0) {
            $("span.cutType", menuContain).html("[不切边]");
        } else if (cutType == 1) {
            $("span.cutType", menuContain).html("[自动切边]");
        } else if (cutType == 2) {
            $("span.cutType", menuContain).html("[自定义切边]");
        } else {
            $("span.cutType", menuContain).html("");
        }
        // 色彩模式
        var colorMode = parseInt($("input[name=colorMode]", $.pdialog.getCurrent()).val());
        if (colorMode == 0) {
            $("span.colorMode", menuContain).html("[彩色]");
        } else if (colorMode == 1) {
            $("span.colorMode", menuContain).html("[灰度]");
        } else if (colorMode == 2) {
            $("span.colorMode", menuContain).html("[黑白]");
        } else {
            $("span.colorMode", menuContain).html("");
        }
    },

    /**
     * 删除图片
     * @param obj 点击对象
     */
    removePic: function (obj) {
        $(obj).parent().parent().remove();
    },

    /**
     * 获取上传的文件列表
     * @returns {*}
     */
    getUploadFiles: function () {
        return scan.upLoadedFiles;
    },

    /**
     * 清除上传的文件列表
     */
    clearUploadFiles: function () {
        scan.upLoadedFiles = [];
        scan.isOpen = false;
    }
};


/**
 * 图片翻页函数
 * @type {{keyJump: keyJump}}
 */
/**
 * 文件查看器
 * @type {{closeFunc: null}}
 */
var FileViewer = {
    closeFunc: null
};
var ImgPage = {

    /**
     * 自定义图片查看路径预览多个图片
     * @param imgObj
     */
    viewPicWithSlfUrl: function (imgBaseUrl, paramsIds, options) {
        var url = getGlobalPathRoot() + "oframe/common/file/file-view.gv";
        var ajaxPacket = new AJAXPacket(url);
        ajaxPacket.data.add("imgBaseUrl", imgBaseUrl);
        ajaxPacket.data.add("docIds", paramsIds);
        if (options) {
            for (var item in options) {
                ajaxPacket.data.add(item, options[item]);
            }
        }
        core.ajax.sendPacketHtml(ajaxPacket, function (response) {
            navTab.getCurrentPanel().append($(response));
            FileViewer.closeFunc = function () {
            };
        });
    },

    /**
     * 打开查看单个图片视图，不允许上传
     * @param imgObj
     */
    viewPic: function (imgObj) {
        var docId = $(imgObj).attr("docId");
        if (docId) {
            var url = getGlobalPathRoot() + "oframe/common/file/file-view.gv";
            var ajaxPacket = new AJAXPacket(url);
            ajaxPacket.data.add("docIds", docId);
            ajaxPacket.data.add("canDel", "false");
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                navTab.getCurrentPanel().append($(response));
                FileViewer.closeFunc = function () {
                };
            });
        }
    },

    /**
     * 查看多个图片，运行上传
     * @param docIds
     */
    viewPics: function (docIds, currentDocId) {
        var url = getGlobalPathRoot() + "oframe/common/file/file-view.gv";
        var ajaxPacket = new AJAXPacket(url);
        ajaxPacket.data.add("docIds", docIds);
        ajaxPacket.data.add("currentDocId", currentDocId);
        ajaxPacket.data.add("canDel", "false");
        core.ajax.sendPacketHtml(ajaxPacket, function (response) {
            navTab.getCurrentPanel().append($(response));
            FileViewer.closeFunc = function () {
            };
        });
    },


    /**
     * 查看多个图片，运行上传
     * @param docIds
     */
    viewPicsDiag: function (docIds, currentDocId) {
        if (!docIds) {
            docIds = "";
        }
        if (!currentDocId) {
            currentDocId = "";
        }
        var url = getGlobalPathRoot() + "oframe/common/file/file-view.gv?canDel=false&dialogFlag=true"
            + "&currentDocId=" + currentDocId
            + "&docIds=" + docIds;
        //
        var iWidth = $(window).width() * 0.6; //弹出窗口的宽度;
        var iHeight = $(window).height() - 100; //弹出窗口的高度;
        if (isIE()) {
            // 非模态对话框仅IE支持
            window.showModelessDialog(url, "附件浏览", "dialogWidth=" + iWidth + "px;dialogHeight=" + iHeight + "px;status=no;help=no;scrollbars=no");
        } else {

            var iTop = (window.screen.availHeight - 30 - iHeight) / 2; //获得窗口的垂直位置;
            var iLeft = (window.screen.availWidth - 10 - iWidth) / 2; //获得窗口的水平位置;
            window.open(url, "附件浏览", "height=" + iHeight + ", width=" + iWidth + ", top=" + iTop + ", left=" + iLeft);
        }
    },


    keyJump: function (event) {
        var lKeyCode = null;
        if (navigator.appname == "Netscape") {
            lKeyCode = event.which;
        } else {
            lKeyCode = event.keyCode
        }
        if (lKeyCode == 38) {
            // 上一页
            if (!$("#imgDialog #rotRight").is(":hidden")) {
                $("#imgDialog #rotRight").click();
            }
            stopEvent(event);
        } else if (lKeyCode == 39) {
            // 下一页
            if (!$("#imgDialog div.btnRight").is(":hidden")) {
                $("#imgDialog div.btnRight").click();
            }
            stopEvent(event);
        } else if (lKeyCode == 37) {
            // 上一页
            if (!$("#imgDialog div.btnLeft").is(":hidden")) {
                $("#imgDialog div.btnLeft").click();
            }
            stopEvent(event);
        } else if (lKeyCode == 40) {
            // 上一页
            if (!$("#imgDialog #rotLeft").is(":hidden")) {
                $("#imgDialog #rotLeft").click();
            }
            stopEvent(event);
        }
    }
};
var upFile = {
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
        var divContainerId = $(divContainer).closest("div.drag").attr("id");
        var url = getGlobalPathRoot() + "oframe/common/file/file-init.gv?docTypeName="
            + $("#" + divContainerId).attr("docTypeName")
            + "&relType=" + $("#" + divContainerId).attr("relType")
            + "&relId=" + $("#" + divContainerId).attr("relId");
        $.pdialog.open(url, "file", "附件上传", {
            mask: true,
            close: upFile.uploadLoadFileClosed,
            param: {divId: divContainerId}
        });
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
     * 关闭页面，关闭高拍仪
     */
    closeScan: function (param) {
        if (!isIE()) {
            return true;
        }
        if (Capture == null && param == null) {
            return true;
        }
        if (typeof(Capture) == "undefined" || typeof(scan) == "undefined") {
            return true;
        }
        var currentIdx = scan.getCurrentDeviceIdx();
        var result;
        if (Capture != null && Capture.CloseDeviceEx && $.isFunction(Capture.CloseDeviceEx)) {
            // 全部执行一次关闭
            Capture.CloseDeviceEx();
        }
        if (result != 0) {
            alertMsg.error("镜头关闭失败!");
        } else {
            Capture = null;
        }
        var uploadedFiles = scan.getUploadFiles();
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
        return true;
    },

    /**
     * 打开
     * @param divContainer
     */
    openScanPage: function (divContainer) {
        var divContainerId = $(divContainer).closest("div.drag").attr("id");
        var url = getGlobalPathRoot() + "oframe/common/file/file-scan.gv?docTypeName="
            + $("#" + divContainerId).attr("docTypeName")
            + "&relType=" + $("#" + divContainerId).attr("relType")
            + "&relId=" + $("#" + divContainerId).attr("relId");
        $.pdialog.open(url, "file", "文档扫描", {
            mask: true,
            max: true,
            close: upFile.closeScan,
            param: {divId: divContainerId}
        });
    },

    /**
     * 实现文件的拖拽上传
     * @param event 拖拽事件
     */
    dropFile: function (event, divContainer) {
        var divId = $(divContainer).closest("div.drag").attr("id");
        if (typeof FileReader == "undefined") {
            alertMsg.warn("您的浏览器不支持拖拽上传");
        }
        var dragFile = event.dataTransfer.files;
        //上传文件到服务器
        var fd = new FormData();
        for (var i = 0; i < dragFile.length; i++) {
            var tempFile = dragFile[i];
            var reader = new FileReader();
            reader.readAsDataURL(tempFile);
            reader.onload = (function (file) {
                return function (e) {
                }
            })(tempFile);
            fd.append($("#" + divId).attr("docTypeName"), tempFile);
        }
        // 增加关联类型
        fd.append("relType", $("#" + divId).attr("relType"));
        fd.append("relId", $("#" + divId).attr("relId"));
        fd.append("prjCd", getPrjCd());
        // 上传文件
        upFile.sendFileToServer(fd, divId);
        // 停止响应事件
        stopEvent();
    },

    sendFileToServer: function (formData, divId) {
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
//
                    var hiddenSpan = $("#" + divId).find("label.hidden");
                    for (var i = 0; i < jsonData.data.length; i++) {
                        var uploadedFile = jsonData.data[i];
                        var newSpan = hiddenSpan.clone();
                        var downLoadUrl = getGlobalPathRoot() + "oframe/common/file/file-download.gv?docId=" + uploadedFile.docId;
                        var imgSrc = downLoadUrl;
                        var fileName = uploadedFile.docName;
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
                } else {
                    alertMsg.warn(jsonData.errMsg);
                }

            }
        });
    },
    removeFile: function (obj) {
        var packet = new AJAXPacket();
        packet.url = $(obj).attr("href");
        core.ajax.sendPacketHtml(packet, function (response) {
            var jsonData = eval("(" + response + ")");
            if (jsonData.success) {
                $(obj).parent().remove();
            } else {
            }
        });
    }
};
