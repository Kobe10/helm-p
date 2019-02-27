<%-- 右侧面板文件显示 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<div class="album-list">
    <input type="hidden" name="docIdPath"
           value='<oframe:tree treeName="DocumentInfo" primaryValue="${upDocId}" property="treePath" pathValue="docId"/>'>
    <input type="hidden" name="docNamePath"
           value='<oframe:tree treeName="DocumentInfo" primaryValue="${upDocId}" property="treePath" pathValue="docName"/>'>
    <ul class="album-ul">
        <c:forEach items="${returnList}" var="item">
            <c:choose>
                <c:when test="${item.Row.docFlag=='1'}">
                    <li class="album-li" style="text-align: center; position: relative">
                        <div ondblclick="pj015.docInfo(${item.Row.docId})" class="album-context album-folder">
                            <input type="hidden" name="docId" value="${item.Row.docId}"/>
                            <input type="hidden" name="docFlag" value="${item.Row.docFlag}"/>
                            <input type="hidden" name="prjCd" value="${item.Row.prjCd}"/>
                        </div>
                        <span ondblclick="pj015.startChangeFileName(this)" class="album-title"
                              style="text-align: center; height: auto; width: 120px;">
                            <label class="js_docName">${item.Row.docName}</label>
                            <input onblur="pj015.changeFileName(this)"
                                   onkeydown="if(event.keyCode == 13){pj015.changeFileName(this); stopEvent(event);}"
                                   class="hidden" type="text"
                                   style="width: 120px;line-height: 19px;height: 19px;"
                                   value="${item.Row.docName}">
                        </span>
                    </li>
                </c:when>
                <c:otherwise>
                    <c:set var="docName" value="${fn:toLowerCase(item.Row.docName)}"/>
                    <c:set var="isImg" value="false"/>
                    <c:set var="isPdf" value="false"/>
                    <c:set var="isDoc" value="false"/>
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
                            <c:set var="isDoc" value="true"/>
                        </c:when>
                        <c:when test="${fn:endsWith(docName, 'doc')||fn:endsWith(docName, 'docx')}">
                            <c:set var="imgSrc"
                                   value="${pageContext.request.contextPath}/oframe/themes/images/words.png"/>
                            <c:set var="isDoc" value="true"/>
                        </c:when>
                        <c:when test="${fn:endsWith(docName, 'txt')}">
                            <c:set var="imgSrc"
                                   value="${pageContext.request.contextPath}/oframe/themes/images/txt.png"/>
                        </c:when>
                        <c:when test="${fn:endsWith(docName, 'pptx')||fn:endsWith(docName, 'ppt')}">
                            <c:set var="imgSrc"
                                   value="${pageContext.request.contextPath}/oframe/themes/images/ppt.png"/>
                            <c:set var="isDoc" value="true"/>
                        </c:when>
                        <c:when test="${fn:endsWith(docName, 'pdf')}">
                            <c:set var="imgSrc"
                                   value="${pageContext.request.contextPath}/oframe/themes/images/pdf.png"/>
                            <c:set var="isPdf" value="true"/>
                        </c:when>
                    </c:choose>

                    <li class="album-li" style="text-align: center;position: relative;">
                        <c:choose>
                            <c:when test="${isImg}">
                                <div ondblclick="pj015.viewPics(${item.Row.svcDocId})"
                                     class="album-context album-file"
                                     style="background: url(${imgSrc}) no-repeat center">
                                    <input type="hidden" name="svcDocId" class="js_pic_id"
                                           value="${item.Row.svcDocId}"/>
                                    <input type="hidden" name="prjCd" value="${item.Row.prjCd}"/>
                                    <input type="hidden" name="docId" value="${item.Row.docId}"/>
                                    <input type="hidden" name="upDocId" value="${item.Row.upDocId}"/>
                                </div>
                            </c:when>
                            <c:when test="${isDoc}">
                                <div ondblclick="pj015.openFile(${item.Row.docId},${item.Row.svcDocId},'${item.Row.docName}', 'false', '1')"
                                     class="album-context album-file js_doc"
                                     style="background: url(${imgSrc}) no-repeat center">
                                    <input type="hidden" name="svcDocId" value="${item.Row.svcDocId}"/>
                                    <input type="hidden" name="docId" value="${item.Row.docId}"/>
                                    <input type="hidden" name="prjCd" value="${item.Row.prjCd}"/>
                                    <input type="hidden" name="upDocId" value="${item.Row.upDocId}"/>
                                </div>
                            </c:when>
                            <c:when test="${isPdf}">
                                <div ondblclick="pj015.openPdf('${item.Row.docId}','${item.Row.svcDocId}','${item.Row.prjCd}')"
                                     class="album-context album-file"
                                     style="background: url(${imgSrc}) no-repeat center">
                                    <input type="hidden" name="svcDocId" value="${item.Row.svcDocId}"/>
                                    <input type="hidden" name="docId" value="${item.Row.docId}"/>
                                    <input type="hidden" name="prjCd" value="${item.Row.prjCd}"/>
                                    <input type="hidden" name="upDocId" value="${item.Row.upDocId}"/>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div ondblclick="pj015.clickDownFile('${item.Row.docId}','${item.Row.svcDocId}','${item.Row.prjCd}')"
                                     class="album-context album-file"
                                     style="background: url(${imgSrc}) no-repeat center">
                                    <input type="hidden" name="svcDocId" value="${item.Row.svcDocId}"/>
                                    <input type="hidden" name="docId" value="${item.Row.docId}"/>
                                    <input type="hidden" name="prjCd" value="${item.Row.prjCd}"/>
                                    <input type="hidden" name="upDocId" value="${item.Row.upDocId}"/>
                                </div>
                            </c:otherwise>
                        </c:choose>
                        <span ondblclick="pj015.startChangeFileName(this)" class="album-title"
                              style="text-align: center; height: auto; width: 120px;">
                             <label title="${item.Row.docName}">${item.Row.docName}</label>
                            <input onblur="pj015.changeFileName(this)"
                                   onkeydown="if(event.keyCode == 13){pj015.changeFileName(this); stopEvent(event);}"
                                   type="text" class="hidden" name="docName"
                                   style="width: 120px;line-height: 19px;height: 19px;z-index: 999;"
                                   value="${item.Row.docName}">
                        </span>
                    </li>
                </c:otherwise>
            </c:choose>
        </c:forEach>
    </ul>
</div>

