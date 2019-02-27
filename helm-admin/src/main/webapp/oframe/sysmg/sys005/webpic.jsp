<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="panelBar">
    <ul class="toolBar">
        <li><a class="save" onclick="sys005Apply.saveWebPic();"><span>保存</span></a></li>
    </ul>
</div>
<div class="pageContent" style="height: 80%;">
    <form id="webpicfrm" method="post">
        <div id="webPicUploadedFileContain"
             ondrop="upFile.dropFile(event, this);"
             docTypeName="homePagePic"
             relType="9"
             relId="homePagePic"
             class="drag img-wrap"
             style="width: 100%;height: 90%; margin-left: 0px;"
             ondragenter="return false"
             ondragover="return false">
            <h2>首页图片 <span class="link btn btn-opt btn-opt-sm marr5"
                           onclick="upFile.openUploadFile(this);stopEvent(event);">[上传]</span></h2>

            <div class="img-list">
                <label style="width: 200px;" class="hidden">
                    <img class="show" style="max-width: 100%;" title="" src="">
                    <a target="_blank" title="" href=""> <span class="link"></span></a>
                    <input type="hidden" name="docId" value="">
                    <input type="hidden" name="docName" value="">
                    <input type="hidden" name="docTypeName" value="">
                    <span class="remove">X</span>
                </label>
                <c:forEach items="${webPicList}" var="item">
                    <label onmousedown="" style="width: 200px;">
                        <input type="hidden" name="docId" value="${item.valueCd}">
                        <input type="hidden" name="docName" value="${item.valueName}">
                        <input type="hidden" name="docTypeName" value="homePagePic">
                        <img class="show" style="max-width: 100%;"
                             title="${item.valueName}"
                             src="${pageContext.request.contextPath}/oframe/common/file/file-download.gv?docId=${item.valueCd}">
                        <span class="remove">X</span>
                    </label>
                </c:forEach>
            </div>
        </div>
    </form>
</div>

<oframe:script src="${pageContext.request.contextPath}/oframe/sysmg/sys005/js/sys005Apply.js"
               type="text/javascript"></oframe:script>