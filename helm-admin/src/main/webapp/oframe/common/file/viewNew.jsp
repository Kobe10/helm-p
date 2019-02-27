<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>居民住房改善信息系统</title>

    <link href="${pageContext.request.contextPath}/oframe/themes/common/core.css" rel="stylesheet" type="text/css"
          media="screen"/>
    <link href="${pageContext.request.contextPath}/oframe/themes/blue/style.css" rel="stylesheet" type="text/css"
          media="screen"/>

    <script src="${pageContext.request.contextPath}/oframe/plugin/jquery/eland.jquery.min.js"
            type="text/javascript"></script>

    <script type="text/javascript"
            src="${pageContext.request.contextPath}/oframe/plugin/raphael/raphael-min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/oframe/plugin/raphael/raphael.entity.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/oframe/plugin/raphael/raphael.pan-zoom.js"></script>
    <!--自定义引入js-->
    <script id="systemScript" contextPath="${pageContext.request.contextPath}/"
            src="${pageContext.request.contextPath}/oframe/plugin/system/system.min.js"
            type="text/javascript"></script>
    <style type="text/css">
        a.picBtn {
            display: inline-block;
            color: #f7f7f7;
            cursor: pointer;
            background-color: #3c90c8;
            padding: 5px 10px;
            border-radius: 5px;
        }
    </style>
</head>
<body style="background-color: white;">
<div>
    <div class="imgShadow"></div>
    <div class="imgDialog" id="imgDialog" currentIdx="${currentIdx}" allRealSrc="${docIds}" allRealNames="${docNames}"
         style="top: 0;left: 0;margin-left:0;">

        <div class="btn btnLeft hidden" style="visibility: visible;left: 0; z-index: 10000;">
            <a href="javascript:" hidefocus="true" title="点击浏览下一张图片，支持→翻页"></a>
        </div>

        <div id="fileView"></div>
        <image id="hImgForPrt" class="hidden"/>

        <div class="btn btnRight hidden" style="visibility: visible;right: 0;z-index: 10000;">
            <a href="javascript:" hidefocus="true" title="点击浏览下一张图片，支持→翻页"></a>
        </div>
        <div style="text-align: center;position: fixed; bottom: 60px;width:100%;color: white;font-size: 25px;">
            <span id="viewDocNameC">${currentDocName}</span>
        </div>
        <div style="text-align: center;position: fixed; bottom: 10px; left: 40%;">
            <a class="link marl10 marr10 picBtn" id="rotLeft">向左旋转</a>
            <a class="link marl10 marr10 picBtn" id="rotRight">向右旋转</a>
            <a class="link marl10 marr10 picBtn" id="openFile">打开附件</a>
        </div>
    </div>
</div>
</body>
<script type="text/javascript">
    $(document).ready(function () {
        //
        var contain = $("#fileView");
        var imgDialog = $("#imgDialog");
        imgDialog.css({
            height: $(window).height(),
            width: $(window).width(),
            background: "${background}"
        });
        contain.css({width: imgDialog.width(), height: imgDialog.height()});

        var viewContain = new EntityContainer(contain[0]);
        viewContain.init();
        viewContain.loadImgSrc("${imgBaseUrl}", '${currentDocId}');

        $("#rotLeft").click(function () {
            var openUrl = viewContain.imgBaseUrl;
            var docName = $("#viewDocNameC").html().toLowerCase();
            if (openUrl === "oframe/common/file/file-downview.gv?docId=") {
                if (docName.endWith("jpg") || docName.endWith("png")
                        || docName.endWith("bmp") || docName.endWith("jpeg")) {
                    // 提交页面数据
                    var url = getGlobalPathRoot() + "oframe/common/file/file-rotate.gv";
                    var ajaxPacket = new AJAXPacket(url);
                    ajaxPacket.noCover = true;
                    ajaxPacket.data.add("prjCd", getPrjCd());
                    ajaxPacket.data.add("docId", viewContain.imgSrc);
                    ajaxPacket.data.add("rotate", "-90");
                    // 提交
                    core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                        var jsonData = eval("(" + response + ")");
                        // 服务调用是否成功
                        var isSuccess = jsonData.success;
                        if (isSuccess) {
                            viewContain.rotateBackImg(-90);
                        } else {
                            alert(jsonData.errMsg);
                        }
                    }, false);
                } else {
                    viewContain.rotateBackImg(-90);
                }
            } else {
                viewContain.rotateBackImg(-90);
            }
        });

        $("#rotRight").click(function () {
            var openUrl = viewContain.imgBaseUrl;
            var docName = $("#viewDocNameC").html().toLowerCase();
            if (openUrl === "oframe/common/file/file-downview.gv?docId=") {
                if (docName.endWith("jpg") || docName.endWith("png")
                        || docName.endWith("bmp") || docName.endWith("jpeg")) {
                    // 提交页面数据
                    var url = getGlobalPathRoot() + "oframe/common/file/file-rotate.gv";
                    var ajaxPacket = new AJAXPacket(url);
                    ajaxPacket.noCover = true;
                    ajaxPacket.data.add("prjCd", getPrjCd());
                    ajaxPacket.data.add("docId", viewContain.imgSrc);
                    ajaxPacket.data.add("rotate", "90");
                    // 提交
                    core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                        var jsonData = eval("(" + response + ")");
                        // 服务调用是否成功
                        var isSuccess = jsonData.success;
                        if (isSuccess) {
                            viewContain.rotateBackImg(90);
                        } else {
                            alert(jsonData.errMsg);
                        }
                    }, false);
                } else {
                    viewContain.rotateBackImg(90);
                }
            } else {
                viewContain.rotateBackImg(90);
            }
        });

        $("#openFile").click(function () {
            var openUrl = viewContain.imgBaseUrl;
            if (openUrl === "oframe/common/file/file-downview.gv?docId=") {
                var docName = $("#viewDocNameC").html();
                if (docName.endWith("pdf")) {
                    openUrl = "oframe/common/file/file-viewPdf.gv?docId=";
                } else {
                    openUrl = "oframe/common/file/file-download.gv?docId=";
                }
            }
            window.open(getGlobalPathRoot() + openUrl + viewContain.imgSrc);
        });

        /**
         * 打印图片
         */
        $("#printPic").click(function () {
            var openUrl = viewContain.imgBaseUrl;
            if (openUrl === "oframe/common/file/file-downview.gv?docId=") {
                openUrl = "oframe/common/file/file-download.gv?docId=";
            }
            openUrl = getGlobalPathRoot() + openUrl + +viewContain.imgSrc;
            var loadimg = new Image();
            loadimg.src = openUrl;
            loadimg.onload = function (e) {
                $(loadimg).jqprint();
            };
        });

        var currentIdx = parseInt('${currentIdx}');
        var totalCount = parseInt('${totalCount}');
        // 初始化显示内容
        $("#imgDialog div.btnLeft").show();
        $("#imgDialog div.btnRight").show();
        // 第一张
        if (currentIdx == 0 || currentIdx == -1) {
            $("#imgDialog div.btnLeft").hide();
        }
        // 最后一张
        if (currentIdx == totalCount - 1 || currentIdx == -1) {
            $("#imgDialog div.btnRight").hide();
        }
        $("#imgDialog div.btnRight").click(function (event) {
            var imgDialog = $("#imgDialog");
            var currentIdx = parseInt(imgDialog.attr("currentIdx"));
            var imgSrcArr = imgDialog.attr("allRealSrc").split(",");
            var docNameArr = imgDialog.attr("allRealNames").split(",");
            var totalCount = imgSrcArr.length;
            if (currentIdx == totalCount - 1) {
                currentIdx = currentIdx;
            } else {
                currentIdx++;
            }
            $("#imgDialog").attr("currentIdx", currentIdx);

            // 初始化显示内容
            $("#imgDialog div.btnLeft").show();
            $("#imgDialog div.btnRight").show();
            // 第一张
            if (currentIdx == 0) {
                $("#imgDialog div.btnLeft").hide();
            }
            // 最后一张
            if (currentIdx == totalCount - 1) {
                $("#imgDialog div.btnRight").hide();
            }
            // 更新显示的文档名称
            $("#viewDocNameC", imgDialog).html(docNameArr[currentIdx]);
            // 重置缩放
            viewContain.zoomReset();
            viewContain.dragReset();
            // 加载图片
            viewContain.loadImgSrc("${imgBaseUrl}", imgSrcArr[currentIdx]);
        });

        $("#imgDialog div.btnLeft").click(function (event) {
            var imgDialog = $("#imgDialog");
            var currentIdx = parseInt(imgDialog.attr("currentIdx"));
            var imgSrcArr = imgDialog.attr("allRealSrc").split(",");
            var docNameArr = imgDialog.attr("allRealNames").split(",");
            var totalCount = imgSrcArr.length;
            if (currentIdx <= 0) {
                currentIdx = 0;
            } else {
                currentIdx--;
            }
            $("#imgDialog").attr("currentIdx", currentIdx);
            // 初始化显示内容
            $("#imgDialog div.btnLeft").show();
            $("#imgDialog div.btnRight").show();
            // 第一张
            if (currentIdx == 0) {
                $("#imgDialog div.btnLeft").hide();
            }
            // 最后一张
            if (currentIdx == totalCount - 1) {
                $("#imgDialog div.btnRight").hide();
            }
            // 更新显示的文档名称
            $("#viewDocNameC", imgDialog).html(docNameArr[currentIdx]);
            // 重置缩放
            viewContain.zoomReset();
            viewContain.dragReset();
            // 加载图片
            viewContain.loadImgSrc("${imgBaseUrl}", imgSrcArr[currentIdx]);
        });

        // 绑定图片翻页事件
        $(document).bind("keydown", ImgPage.keyJump);
        $(window).bind("resize", function () {
            imgDialog.css({
                height: $(window).height(),
                width: $(window).width()
            });
            contain.css({width: imgDialog.width(), height: imgDialog.height()});
        });
    });
</script>
</html>