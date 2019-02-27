<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<oframe:script src="${pageContext.request.contextPath}/oframe/common/file/js/file.js" type="text/javascript"/>
<span class="dCloseFunc hidden" dCloseFunc="file.closeUpFile()"/>

<div class="panelBar">
    <ul class="toolBar">
        <c:if test="${editAble}">
            <li><a class="edit" onclick="file.saveUpFile();"><span>提交</span></a></li>
        </c:if>
        <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>取消</span></a></li>
    </ul>
</div>
<div class="pageContent js_upload" layoutH="55">
    <input type="hidden" name="subDir" value="${subDir}">
    <input type="hidden" name="docTypeName" value="${docTypeName}">
    <input type="hidden" name="docType" value="${docType}">
    <c:if test="${editAble}">
        <input id="fileUpload" style="display: inline-block;" type="file" name="uploadFile"/>
        <button type="button" class="btn btn-primary mar10" onclick="$('#fileUpload').uploadify('upload', '*')">开始上传
        </button>
        <button type="button" class="btn btn-info mar10" onclick="$('#fileUpload').uploadify('cancel', '*')">取消上传
        </button>
        <c:if test="${hasRels}">
            <button type="button" class="btn btn-primary mar10" onclick="file.switchModel('1')">
                选择已有
            </button>
        </c:if>
        <oframe:power prjCd="${param.prjCd}" rhtCd="SCAN_FUNCTION">
            <button type="button" class="btn btn-primary mar10" onclick="file.switchModel('2')">
                文档扫描
            </button>
        </oframe:power>
        <input type="hidden" name="relId" value="${relId}">
        <input type="hidden" name="relType" value="${relType}">
    </c:if>
    <div id="uploadContain" layoutH="150" class="js-img-wrap"
         style="border: 3px #000000 dotted;margin-top: 10px;overflow-y: auto; overflow-x: auto;">
        <div id="fileQueue" class="fileQueue"></div>
        <span class="mar5 hidden js_pic_span" docId="" docName="" docPath="">
            <label style="display: inline-block; text-align: center;
                   position: relative; width: 150px; margin: 5px;"
                   onmousedown="">
                <img class="show" style="max-width: 150px;" onclick="ImgPage.viewPic(this);" title="" src="">
                <a target="_blank" title="" style="display: block;" href="">
                    <span class="link"
                          style="display:inline-block;width:150px;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;">
                    </span>
                </a>
                <c:if test="${editAble}">
                    <span class="removeX" onclick="file.removeImg(this);">X</span>
                </c:if>
            </label>
        </span>
        <c:forEach items="${upLoadDocs}" var="item">
            <span class="mar5 js_pic_span" docId="${item.docId}" docName="${item.docName}" docPath="${item.docPath}">
                <label style="display: inline-block; text-align: center; position: relative; width: 150px; margin: 5px;"
                       onmousedown="">
                    <c:choose>
                        <c:when test="${fn:endsWith(item.docName,'pdf')}">
                            <img class="show" style="width: 100%;" docId="${item.docId}" title=""
                                 onclick="file.viewPdf(this);"
                                 src="${pageContext.request.contextPath}/oframe/common/file/file-downview.gv?docId=${item.docId}"/>
                        </c:when>
                        <c:otherwise>
                            <img class="show" style="width: 100%;" docId="${item.docId}" title=""
                                 onclick="ImgPage.viewPic(this);"
                                 src="${pageContext.request.contextPath}/oframe/common/file/file-downview.gv?docId=${item.docId}"/>
                        </c:otherwise>
                    </c:choose>
                    <a target="_blank" title="" style="display: block;"
                       href="${pageContext.request.contextPath}/oframe/common/file/file-download.gv?docId=${item.docId}">
                        <span class="link"
                              style="display:inline-block;width:150px;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;">
                                ${item.docName}
                        </span>
                    </a>
                    <c:if test="${editAble}">
                        <span class="removeX" onclick="file.removeImg(this);">X</span>
                    </c:if>
                </label>
            </span>
        </c:forEach>
    </div>
    <div style="text-align: center;">
        <img id="file_imgViewer" class="hidden" src=""/>
    </div>
</div>
<div class="pageContent js_uploaded hidden" layoutH="55">
    <table>
        <tr>
            <th><label>附件类型：</label></th>
            <td>
                <oframe:select prjCd="${param.prjCd}" collection="${typeMap}" onChange="file.flashPic();"
                               name="sltDocTypeName"
                               style="width:120px;" value="${docTypeName}"/>
            </td>
            <td>
                <button type="button" class="btn btn-info marl10" onclick="file.switchModel('0')">返回上传</button>
            </td>
        </tr>
    </table>
    <div id="uploadedContain" layoutH="150" style="bborder: 3px #000000 dotted;margin-top: 10px;
          border-style: dotted; overflow-y: auto; overflow-x: auto;">
        <c:forEach items="${allDocResult}" var="item">
            <span class="mar5 js_pic_span" docId="${item.docId}" docTypeName="${item.docTypeName}"
                  docName="${item.docName}" docPath="${item.docPath}">
                <label style="display: inline-block; text-align: center; position: relative; width: 150px; margin: 5px;"
                       onmousedown="">
                    <span class="rhtCorner" onclick="file.selectImg(this);"><input type="checkbox"></span>
                    <c:choose>
                        <c:when test="${fn:endsWith(item.docName,'pdf')}">
                            <img class="show" style="width: 100%;" docId="${item.docId}" title=""
                                 onclick="file.viewPdf(this);"
                                 src="${pageContext.request.contextPath}/oframe/common/file/file-downview.gv?docId=${item.docId}"/>
                        </c:when>
                        <c:otherwise>
                            <img class="show" style="width: 100%;" docId="${item.docId}" title=""
                                 onclick="ImgPage.viewPic(this);"
                                 src="${pageContext.request.contextPath}/oframe/common/file/file-downview.gv?docId=${item.docId}"/>
                        </c:otherwise>
                    </c:choose>
                    <a target="_blank" title="" style="display: block;"
                       href="${pageContext.request.contextPath}/oframe/common/file/file-download.gv?docId=${item.docId}">
                        <span class="link"
                              style="display:inline-block;width:150px;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;">
                                ${item.docName}
                        </span>
                    </a>
                    <c:if test="${editAble}">
                        <span class="removeX" onclick="file.removeImg(this);">X</span>
                    </c:if>
                </label>
            </span>
        </c:forEach>
    </div>
</div>
<div class="pageContent js_scan hidden">

    <div class="withTitle noClose" style="width: 60%; float: left;">
        <h1>
            <span>视频预览</span>
            <button type="button" class="btn btn-more mar5" style="float: right;" onclick="file.switchModel('0');">
                <i style="font-style:normal;">返回上传</i>
            </button>
            <input type="hidden" name="deviceIdx" value="0"/>
            <input type="hidden" name="cutType" value="1"/>
            <input type="hidden" name="colorMode" value="0"/>
        </h1>
        <div layoutH="120"
             style="border: 1px #000000 dotted; width: 100%;text-align: center;vertical-align: middle;">
            <oframe:power prjCd="${param.prjCd}" rhtCd="SCAN_FUNCTION">
                <object id="ScanCapture" classid="clsid:9A73DB73-2CA3-478D-9A3F-7E9D6A8D327C"
                        style="width: 98%; height: 98%; z-index: 10;"
                        codebase="<%=request.getContextPath()%>/oframe/common/file/CaptureVideo.cab">
                    <param name="wmode" value="transparent"/>
                </object>
            </oframe:power>
        </div>
    </div>
    <div class="withTitle noClose" style="width: 37%;float: right;">
        <h1><span>图像预览</span>
            <button type="button" class="btn btn-more mar5" style="float: right;" onclick="scan.catchPic();">
                <i style="font-style:normal;">图像采集</i>
            </button>
            <button type="button" offsetX="77" offsetY="-22" class="btn btn-more mar5" style="float: right;">
                <i style="font-style:normal;">采集设置</i>
                <span class="caret"></span>
                <ul class="menu down_menu hidden js_scan_menu" style="width: 167px;">
                    <li style="width: 165px;">切边模式：<span class="link mar5 cutType"
                                                         onclick="scan.changeCutType();">[自动切边]</span></li>
                    <li style="width: 165px;">镜头切换：<span class="link mar5 deviceType" onclick="scan.changeDevice();">[文档镜头]</span>
                    </li>
                    <li style="width: 165px;">色彩模式：<span class="link mar5 colorMode" onclick="scan.changeColorMode()">[彩色]</span>
                    </li>
                    <li style="width: 165px;">旋转镜头：<span class="link mar5"
                                                         onclick="scan.changeRotation(90)">[正向旋转]</span></li>
                    <li style="width: 165px;">旋转镜头:<span class="link mar5"
                                                         onclick="scan.changeRotation(270)">[反向旋转]</span></li>
                    <li style="width: 165px;">旋转设置：<span class="link mar5" onclick="scan.changeScan()">[镜头设置]</span>
                    </li>
                    <li style="width: 165px;">重启镜头：<span class="link mar5" onclick="scan.resetScan()">[重启镜头]</span>
                    </li>
                </ul>
            </button>
        </h1>
        <div class="hidden" id="scanTemplate">
            <span style="display: block; margin-top: 5px; height: 30px;">
                <input type="text" name="fileName" value="">
                <input type="hidden" name="upFile" value="">
                <span class="link marl5" onclick="scan.removePic(this)">[删除]</span>
            </span>
        </div>
        <form id="scan001" method="post" class="required-validate">
            <div layoutH="120" style="width:99%;border: 1px #000000 dotted;text-align: center;">
                <ul id="picUl" class="ztree">
                    <li style="text-align: center; padding: 5px; margin: 2px; border: #0000ff 1px solid;"
                        class="hidden">
                        <img alt="" name="pic1" style="width:80%;height:150px;"/>
                    </li>
                </ul>
            </div>
        </form>
    </div>
</div>