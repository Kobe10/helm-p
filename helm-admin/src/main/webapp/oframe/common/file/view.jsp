<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div>
    <div class="imgShadow"></div>
    <div class="imgDialog" id="imgDialog" currentIdx="${currentIdx}" allRealSrc="${docIds}" allRealNames="${docNames}">
        <a href="javascript:void(0)" class="close"></a>

        <div class="btn btnLeft hidden" style="visibility: visible;">
            <a href="javascript:" hidefocus="true" title="点击浏览下一张图片，支持→翻页"></a>
        </div>
        <div id="fileView"></div>
        <image id="hImgForPrt" class="hidden"/>
        <div class="btn btnRight hidden" style="visibility: visible;">
            <a href="javascript:" hidefocus="true" title="点击浏览下一张图片，支持→翻页"></a>
        </div>
        <div class="mart5 marb5" style="text-align: center;color: white;font-size: 25px;">
            <span id="viewDocNameC">${currentDocName}</span>
        </div>
        <div style="text-align: center">
            <a class="link marl10 marr10" id="rotLeft"
               style="display: inline-block; color: #35a0e6;cursor: pointer;">向左旋转</a>
            <a class="link marl10 marr10" id="rotRight"
               style="display: inline-block; color: #35a0e6; cursor: pointer;">向右旋转</a>
            <a class="link marl10 marr10" id="printFile"
               style="display: inline-block; color: #35a0e6;cursor: pointer;">打印附件</a>
            <a class="link marl10 marr10" id="openFile"
               style="display: inline-block; color: #35a0e6;cursor: pointer;">打开附件</a>
            <a class="link marl10 marr10" id="openFileNew"
               style="display: inline-block; color: #35a0e6;cursor: pointer;">新窗口浏览</a>
            <oframe:power prjCd="${param.prjCd}" rhtCd="${delFjRht}">
                <c:if test="${canDel}">
                    <a class="link marl10 marr10" id="delFile"
                       style="display: inline-block; color: #35a0e6;cursor: pointer;">删除附件</a>
                </c:if>
            </oframe:power>
        </div>
    </div>
    <script type="text/javascript">
        $(document).ready(function () {
            var container = navTab.getCurrentPanel();
            var currentIdx = parseInt('${currentIdx}');
            var totalCount = parseInt('${totalCount}');
            // 初始化显示内容
            $("#imgDialog div.btnLeft", container).show();
            $("#imgDialog div.btnRight", container).show();
            // 第一张
            if (currentIdx == 0 || currentIdx == -1) {
                $("#imgDialog div.btnLeft", container).hide();
            }
            // 最后一张
            if (currentIdx == totalCount - 1 || currentIdx == -1) {
                $("#imgDialog div.btnRight", container).hide();
            }
            $("#imgDialog div.btnRight", container).click(function (event) {
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

            /**
             * 向前翻页，前一个附件
             **/
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
            $("#imgDialog .close", container).click(function (e) {
                $(".imgShadow").remove();
                $("#imgDialog").remove();
                if (FileViewer.closeFunc) {
                    FileViewer.closeFunc.apply();
                    FileViewer.closeFunc = null;
                }
                // 取消绑定图片翻页事件
                $(document).unbind("keydown", ImgPage.keyJump);
            });
        });
        //
        var contain = $("#fileView", navTab.getCurrentPanel());
        var imgDialog = $("#imgDialog", navTab.getCurrentPanel());
        imgDialog.css({
            height: $(window).height() - 60,
            width: $(window).width() * 0.6, background: "${background}",
            marginLeft: -($(window).width() * 0.6 ) / 2 + "px"
        });
        contain.css({width: imgDialog.width(), height: imgDialog.height() - 60});
        var viewContain = new EntityContainer(contain[0]);
        viewContain.init();
        viewContain.loadImgSrc("${imgBaseUrl}", '${currentDocId}');
        $("#rotLeft", navTab.getCurrentPanel()).click(function () {
            var openUrl = viewContain.imgBaseUrl;
            //可能会有附件的后缀名是大写的，先把大写的转换成小写的再进行旋转保存
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
                            alertMsg.warn(jsonData.errMsg);
                        }
                    }, false);
                } else {
                    viewContain.rotateBackImg(-90);
                }
            } else {
                viewContain.rotateBackImg(-90);
            }
        });
        $("#rotRight", navTab.getCurrentPanel()).click(function () {
            var openUrl = viewContain.imgBaseUrl;
            //可能会有附件的后缀名是大写的，先把大写的转换成小写的再进行旋转保存
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
                            alertMsg.warn(jsonData.errMsg);
                        }
                    }, false);
                } else {
                    viewContain.rotateBackImg(90);
                }
            } else {
                viewContain.rotateBackImg(90);
            }
        });
        $("#openFile", navTab.getCurrentPanel()).click(function () {
            var openUrl = viewContain.imgBaseUrl;
            if (openUrl === "oframe/common/file/file-downview.gv?docId=") {
                var docName = $("#viewDocNameC").html();
                if (docName.endWith("pdf") || docName.endWith("PDF")) {
                    openUrl = "oframe/common/file/file-viewPdf.gv?docId=";
                } else {
                    openUrl = "oframe/common/file/file-download.gv?docId=";
                }
            }
            window.open(getGlobalPathRoot() + openUrl + viewContain.imgSrc);
        });

        /**
         * 在独立窗口中打开附件进行浏览
         */
        $("#openFileNew", navTab.getCurrentPanel()).click(function () {
            ImgPage.viewPicsDiag('${docIds}');
            $("#imgDialog .close").trigger("click");
        });

        $("#printFile", navTab.getCurrentPanel()).click(function () {
            var imgUrl = getGlobalPathRoot() + "oframe/common/file/file-print.gv?docId=" + viewContain.imgSrc;
            var iWidth = $(window).width() * 0.6; //弹出窗口的宽度;
            var iHeight = $(window).height() - 100; //弹出窗口的高度;
            if (isIE()) {
                // 非模态对话框仅IE支持
                window.showModalDialog(imgUrl, "附件浏览", "dialogWidth=" + iWidth + "px;dialogHeight=" + iHeight + "px;status=no;help=no;scrollbars=no");
            } else {
                var iTop = (window.screen.availHeight - 30 - iHeight) / 2; //获得窗口的垂直位置;
                var iLeft = (window.screen.availWidth - 10 - iWidth) / 2; //获得窗口的水平位置;
                window.open(imgUrl, "附件浏览", "height=" + iHeight + ", width=" + iWidth + ", top=" + iTop + ", left=" + iLeft);
            }
        });

        $("#delFile", navTab.getCurrentPanel()).click(function () {
            var docId = viewContain.imgSrc;
            alertMsg.confirm("你确定要删除吗？", {
                okCall: function () {
                    //展示所有的附件。
                    var url = getGlobalPathRoot() + "oframe/common/file/file-delete.gv";
                    var ajaxPacket = new AJAXPacket(url);
                    ajaxPacket.data.add("docId", docId);
                    core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                        var data = eval("(" + response + ")");
                        var isSuccess = data.success;
                        if (isSuccess) {
                            alertMsg.correct("处理成功");
                            var container = navTab.getCurrentPanel();
                            var imgDialog = $("#imgDialog");
                            var currentIdx = parseInt(imgDialog.attr("currentIdx"));
                            var imgSrcArr = imgDialog.attr("allRealSrc").split(",");
                            var docNameArr = imgDialog.attr("allRealNames").split(",");
                            if (imgSrcArr.length == 1 && currentIdx == 0) {
                                $("#imgDialog .close", container).trigger("click");
                                return;
                            }
                            if (currentIdx == imgSrcArr.length - 1) {
                                imgSrcArr.splice(currentIdx, 1);
                                docNameArr.splice(currentIdx, 1);
                                imgDialog.attr("allRealSrc", imgSrcArr.join(","));
                                imgDialog.attr("allRealNames", docNameArr.join(","));
                                imgDialog.attr("currentIdx", -1);
                            } else {
                                imgSrcArr.splice(currentIdx, 1);
                                docNameArr.splice(currentIdx, 1);
                                imgDialog.attr("allRealSrc", imgSrcArr.join(","));
                                imgDialog.attr("allRealNames", docNameArr.join(","));
                                imgDialog.attr("currentIdx", currentIdx);
                            }
                            $("#imgDialog div.btnRight", container).trigger("click");
                        } else {
                            alertMsg.error(data.errMsg);
                        }
                    });
                }
            });
        });
    </script>
</div>