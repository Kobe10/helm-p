<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="pageContent">
    <form id="appTopPic" method="post">
        <div id="webPicUploadedFileContain"
             docTypeName="appTopPic"
             relType="9" relId="appTopPic"
             class="drag img-wrap"
             style="width: 100%;height: 90%; margin-left: 0px; margin-top: 10px;background-image: none;">
            <h2 style="line-height: 40px;">APP轮播图(750*360):
                <span class="link marr5"
                      onclick="mob004.saveAppPic();stopEvent(event);">[保存]</span>
                <span class="link marr5 marl5"
                      onclick="upFile.openUploadFile(this);stopEvent(event);">[上传]</span>
            </h2>
            <label style="width: 200px; margin-top: 10px;" class="hidden js_img">
                <img class="show" style="max-width: 100%;" title="" src="">
                <a target="_blank" title="" href=""> <span class="link" style="width: 100%;"></span></a>
                <input type="hidden" name="docId" value="">
                <input type="hidden" name="docName" value="">
                <input type="hidden" name="docTypeName" value="">
                <span class="remove">X</span>
            </label>
            <c:forEach items="${appPicList}" var="item">
                <label style="width: 200px;" class="js_img">
                    <input type="hidden" name="docId" value="${item.valueCd}">
                    <input type="hidden" name="docName" value="${item.valueName}">
                    <input type="hidden" name="docTypeName" value="homePagePic">
                    <img class="show" style="max-width: 100%;"
                         realSrc="${pageContext.request.contextPath}/oframe/common/file/file-download.gv?docId=${item.valueCd}"
                         title="${item.valueName}"
                         src="${pageContext.request.contextPath}/oframe/common/file/file-download.gv?docId=${item.valueCd}">
                    <span class="remove">X</span>
                </label>
            </c:forEach>
        </div>
    </form>
</div>

<oframe:script src="${pageContext.request.contextPath}/mobile/mob004/js/mob004.js"
               type="text/javascript"></oframe:script>