<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div class="pageContent" layoutH="0">
    <div class="panelBar">
        <ul class="toolBar">
            <li><a class="edit" onclick="scan.sendUpload();"><span>提交上传</span></a></li>
        </ul>
    </div>
    <div>
        <div class="withTitle noClose" style="width: 75%; float: left;">
            <h1>视频预览</h1>

            <div style="height: 30px;background-color: rgba(193, 196, 234, 0.08);
                    clear: both; vertical-align: middle; padding: 5px;line-height: 30px;">
                切边模式：<span class="link mar5 cutType" onclick="scan.changeCutType();">[不切边]</span>、
                当前摄像头：<span class="link mar5 deviceType" onclick="scan.changeDevice();">[文档镜头]</span>、
                色彩模式：<span class="link mar5 colorMode" onclick="scan.changeColorMode()">[彩色]</span>
                旋转镜头：<span class="link mar5" onclick="scan.changeRotation(90)">[正向旋转]</span>
                <span class="link mar5" onclick="scan.changeRotation(270)">[反向旋转]</span>、
                设置：<span class="link mar5" onclick="scan.changeScan()">[镜头设置]</span>
                <input type="hidden" name="deviceIdx" value="0"/>
                <input type="hidden" name="cutType" value="0"/>
                <input type="hidden" name="colorMode" value="0"/>
                <span class="link mar5" onclick="scan.catchPic();"
                      style="float: right; color: #ff0000;font-weight: bold;">[图片采集]</span>
            </div>
            <div layoutH="100"
                 style="border: 1px #000000 dotted; width: 100%;text-align: center;vertical-align: middle;">
                <object id="ScanCapture" classid="clsid:9A73DB73-2CA3-478D-9A3F-7E9D6A8D327C"
                        style="width: 98%; height: 98%; z-index: 10;"
                        codebase="CaptureVideo.cab#version=1,1,1,11">
                </object>
            </div>
        </div>

        <div class="withTitle noClose" style="width: 24%;float: right;">
            <h1>图像预览</h1>

            <div class="hidden" id="scanTemplate">
                <span style="display: block; margin-top: 5px; height: 30px;">
                    <input type="text" name="fileName" value="">
                    <input type="hidden" name="upFile" value="">
                    <span class="link marl5" onclick="scan.removePic(this)">[删除]</span>
                </span>
            </div>

            <form id="scan001" method="post" class="required-validate">

                <input type="hidden" name="docTypeName" value="${docTypeName}">
                <input type="hidden" name="relId" value="${relId}">
                <input type="hidden" name="relType" value="${relType}">

                <div layoutH="58" class="img-wrap" style="width:99%;border: 1px #000000 dotted;text-align: center;">
                    <ul id="picUl" class="ztree">
                        <li style="text-align: center; padding: 5px; margin: 2px; border: #0000ff 1px solid;"
                            class="hidden">
                            <img alt="" name="pic1" style="width:80%;height:300px;"/>
                        </li>
                    </ul>
                </div>
            </form>
        </div>
    </div>
</div>
<script type="text/javascript">
    $(function () {
        // 初始化扫描控件,打开预览
        // 查询可用指标
        setTimeout(scan.init, 500);
    });
</script>