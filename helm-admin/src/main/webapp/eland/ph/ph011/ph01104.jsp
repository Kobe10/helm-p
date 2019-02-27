<%-- 右侧面板文件显示 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<div class="album-list">
    <input type="hidden" value="reg04" name="goUpFlag"/>
    <input type="hidden" value="${docTypeName}" name="docTypeName"/>

    <ul class="album-ul">
        <c:forEach items="${attachmentList}" var="folder">
            <c:set var="docName" value="${fn:toLowerCase(folder.docName)}"/>
            <c:set var="isImg" value="false"/>
            <c:set var="isPdf" value="false"/>
            <c:set var="imgSrc"
                   value="${pageContext.request.contextPath}/oframe/themes/images/not.png"/>
            <c:choose>
                <c:when test="${fn:endsWith(docName, 'jpeg') || fn:endsWith(docName, 'gif')
                                                         || fn:endsWith(docName, 'jpg') || fn:endsWith(docName, 'png')
                                                         || fn:endsWith(docName, 'bmp') || fn:endsWith(docName, 'pic')}">
                    <c:set var="imgSrc"
                           value="${pageContext.request.contextPath}/oframe/themes/images/imgs.png"/>
                    <c:set var="isImg" value="true"/>
                </c:when>
                <c:when test="${fn:endsWith(docName, 'xls')||fn:endsWith(docName, 'xlsx')}">
                    <c:set var="imgSrc"
                           value="${pageContext.request.contextPath}/oframe/themes/images/excel.png"/>
                </c:when>
                <c:when test="${fn:endsWith(docName, 'doc')||fn:endsWith(docName, 'docx')}">
                    <c:set var="imgSrc"
                           value="${pageContext.request.contextPath}/oframe/themes/images/words.png"/>
                </c:when>
                <c:when test="${fn:endsWith(docName, 'txt')}">
                    <c:set var="imgSrc"
                           value="${pageContext.request.contextPath}/oframe/themes/images/txt.png"/>
                </c:when>
                <c:when test="${fn:endsWith(docName, 'pptx')||fn:endsWith(docName, 'ppt')}">
                    <c:set var="imgSrc"
                           value="${pageContext.request.contextPath}/oframe/themes/images/ppt.png"/>
                </c:when>
                <c:when test="${fn:endsWith(docName, 'pdf')}">
                    <c:set var="isPdf" value="true"/>
                    <c:set var="imgSrc"
                           value="${pageContext.request.contextPath}/oframe/themes/images/pdf.png"/>
                </c:when>
            </c:choose>
            <li class="album-li" style="text-align: center;position: relative;">
                <c:choose>
                    <c:when test="${isImg}">
                        <div ondblclick="ImgPage.viewPics(${folder.docId})"
                             class="album-context album-file"
                             style="background: url(${imgSrc}) no-repeat center">
                            <input type="hidden" name="docId" value="${folder.docId}"/>
                        </div>
                    </c:when>
                    <c:when test="${isPdf}">
                        <div ondblclick="ph011.viewPdf(${folder.docId})"
                             class="album-context album-file"
                             style="background: url(${imgSrc}) no-repeat center">
                            <input type="hidden" name="docId" value="${folder.docId}"/>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div ondblclick="ph011.downFile(${folder.docId})"
                             class="album-context album-file"
                             style="background: url(${imgSrc}) no-repeat center">
                            <input type="hidden" name="docId" value="${folder.docId}"/>
                        </div>
                    </c:otherwise>
                </c:choose>
                        <span ondblclick="ph011.startChangeFileName(this)" class="album-title"
                              style="text-align: center; height: auto; width: 120px;">
                             <label title="${folder.docName}">${folder.docName}</label>
                            <input onblur="ph011.changeFileName(this)"
                                   onkeydown="if(event.keyCode == 13){ph011.changeFileName(this); stopEvent(event);}"
                                   type="text" class="hidden"
                                   style="width: 120px;line-height: 19px;height: 19px;z-index: 999;"
                                   value="${folder.docName}">
                        </span>
            </li>
        </c:forEach>
    </ul>
</div>

