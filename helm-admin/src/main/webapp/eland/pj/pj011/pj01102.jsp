<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="writeFlag" value="false"/>
<div class="panelBar">
    <ul class="toolBar">
        <c:choose>
            <c:when test="${method != 'view'}">
                <oframe:op prjCd="${param.prjCd}" template="li" cssClass="save"
                           name="保存" rhtCd="edit_prj_notice_rht"
                           onClick="pj01102.save();"/>
                <c:set var="writeFlag" value="true"/>
            </c:when>
        </c:choose>
    </ul>
</div>
<form id="pj01102Frm" method="post">
    <input type="hidden" name="noticeId" value="${prjNotice.PrjNotice.noticeId}"/>
    <input type="hidden" name="prjCd" value="${prjNotice.PrjNotice.prjCd}"/>

    <div class="table-container" layoutH="80">
        <table class="form hidden">
            <tr>
                <th width="10%">
                    <label>公告类型：</label>
                </th>
                <c:choose>
                    <c:when test="${defaultNoticeType == null || defaultNoticeType==''}">
                        <td>
                            <oframe:select prjCd="${param.prjCd}" itemCd="NOTICE_TYPE" name="noticeType"
                                           value="${prjNotice.PrjNotice.noticeType}"/>
                        </td>
                    </c:when>
                    <c:otherwise>
                        <td>
                            <oframe:select prjCd="${param.prjCd}" itemCd="NOTICE_TYPE" name="noticeType"
                                           value="${defaultNoticeType}"/>
                        </td>
                    </c:otherwise>
                </c:choose>
            </tr>
            <tr>
                <th width="10%">
                    <label>公告标题：</label>
                </th>
                <td>
                    <input type="text" class="msize" name="noticeTitle" value="${prjNotice.PrjNotice.noticeTitle}"/>
                </td>
                <th width="10%">
                    <label>发布日期：</label>
                </th>
                <td>
                    <input type="text" class="date" datefmt="yyyy-MM-dd" name="publishDate"
                           value='<fmt:formatDate value="${publishDate}" type="both" pattern="yyyy-MM-dd"/>'/>
                </td>
            </tr>
            <tr>
                <th width="10%">
                    <label>公告图片及标题：</label>
                </th>
                <td colspan="3" style="vertical-align: top;">
                    <div id="pj01101UploadedFileContain"
                         ondrop="upFile.dropFile(event, this);"
                         docTypeName="noticePic"
                         relType="8"
                         relId="${prjNotice.PrjNotice.noticeId}"
                         ondragenter="return false"
                         ondragover="return false"
                         class="img-wrap drag"
                         style="width: 90%;margin-left:0px;">
                        <h2>
                            <c:if test="${writeFlag}">
                                    <span class="link btn btn-opt btn-opt-sm marr5"
                                          onclick="pj011.openUploadFile('pj01101UploadedFileContain');stopEvent(event)">[上传图片]</span>
                            </c:if>
                        </h2>

                        <div class="img-list" style="border: 1px">
                            <label class="hidden" style="width:99%; border: 1px dashed #666;">
                                <img class="show" style="width: 25%;display: inline-block;" title="" src="">
                                <input type="hidden" name="noticePicId" value="">
                                <input type="hidden" name="docId" value="">
                                <input type="hidden" name="docTypeName" value="noticePic">
                                <c:if test="${writeFlag}">
                                    <span class="remove">X</span>
                                </c:if>
                                <textarea height="100%" style="width: 74%;display: inline-block;"
                                          class="editor simpleEditor"
                                          name="noticePicDesc">${noticePic.picDesc}</textarea>
                                <input type="radio" name="noticePicCover" value="${noticePic.docId}"/>作为封面
                            </label>
                            <c:forEach items="${noticePics}" var="noticePic">
                                <label onmousedown="" style="width:99%; border: 1px dashed #666;">
                                    <input type="hidden" name="noticePicId${noticePic.docId}"
                                           value="${noticePic.noticePicId}">
                                    <input type="hidden" name="docId" value="${noticePic.docId}">
                                    <input type="hidden" name="docTypeName" value="noticePic">
                                    <img class="show" style="max-width: 25%;display: inline-block;"
                                         src="${pageContext.request.contextPath}/oframe/common/file/file-download.gv?docId=${noticePic.docId}"/>
                                    <c:if test="${writeFlag}">
                                        <span class="remove">X</span>
                                    </c:if>
                                    <textarea height="100%" style="width: 74%;display: inline-block"
                                              class="editor simpleEditor"
                                              name="noticePicDesc${noticePic.docId}">${noticePic.picDesc}</textarea>
                                    <c:set var="checked" value=""/>
                                    <c:if test="${noticePic.docId==prjNotice.PrjNotice.coverPicId}">
                                        <c:set var="checked" value="checked='checked'"/>
                                    </c:if>
                                    <input type="radio" name="noticePicCover" ${checked}
                                           value="${noticePic.docId}"/>作为封面
                                </label>
                            </c:forEach>
                        </div>
                    </div>
                </td>
            </tr>
        </table>
        <table class="form" width="100%">
            <tr>
                <th width="20%">
                    <label>发布状态：</label>
                </th>
                <td>
                    <oframe:select prjCd="${param.prjCd}" itemCd="NOTICE_STATUS" name="noticeStatus"
                                   value="${prjNotice.PrjNotice.noticeStatus}"/>
                </td>
            </tr>
            <tr>
                <th width="20%">
                    <label>视频简介：</label>
                </th>
                <td>
                    <textarea style="width:80%;min-height: 200px;" class="editor simpleEditor"
                              name="noticeSummary">${prjNotice.PrjNotice.noticeSummary}
                    </textarea>
                </td>
            </tr>
            <tr>
                <th width="20%">
                    <label>公告详情：</label>
                </th>
                <td align="left">
                    <textarea name="noticeContext" class="editor" edtOpt="{toolbars:[['source','insertvideo']]}"
                              style="width: 80%; min-height: 345px;"
                              rows="20">${prjNotice.PrjNotice.noticeContext}</textarea>
                </td>
            </tr>
        </table>
    </div>
</form>
<oframe:script src="${pageContext.request.contextPath}/eland/pj/pj011/js/pj01102.js" type="text/javascript"/>