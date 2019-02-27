<%--
  Created by IntelliJ IDEA.
  User: Dennis
  Date: 2014/11/3 0003
  Time: 17:33
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<div class="pagecontent">
<c:set var="writeAbleClass" value="readonly"/>
<c:set var="writeFlag" value="false"/>
<div class="panelBar">
    <ul class="toolBar">
        <c:choose>
            <c:when test="${method != 'view'}">
                <oframe:op prjCd="${param.prjCd}" template="li" cssClass="save"
                           name="保存" rhtCd="edit_prj_doc_rht"
                           onClick="pj013.save();"/>
                <c:set var="writeAbleClass" value=""/>
                <c:set var="writeFlag" value="true"/>
            </c:when>
        </c:choose>
        <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
    </ul>
</div>
<form id="pj01301frm" method="post">
<input type="hidden" name="documentId" value="${document.Document.docId}"/>
<input type="hidden" name="prjCd" value="${document.Document.prjCd}"/>

<div class="withTitle ${writeAbleClass}">
<table class="form">
<tr>
    <th>
        <label>文件名称：</label>
    </th>
    <td>
        <input type="text" name="docName" class="required textInput" value="${document.Document.docName}"/>
    </td>
    <th>
        <label>文件类型：</label>
    </th>
    <td>
        <oframe:select prjCd="${param.prjCd}" itemCd="PRJ_DOCUMENT_TYPE" name="docType"
                       value="${document.Document.docType}"/>
    </td>
    <th>
        <label>文件来源：</label>
    </th>
    <td>
        <oframe:select prjCd="${param.prjCd}" itemCd="PRJ_DOCUMENT_SOURCE" name="docSrc"
                       value="${document.Document.docSrc}"/>
    </td>
</tr>
<tr>
    <th>
        <label>发文时间：</label>
    </th>
    <td>
        <input type="text" class="date required ${writeAbleClass}" datefmt="yyyy-MM-dd" name="pubDate"
               value='<fmt:formatDate value="${pubDate}" type="both" pattern="yyyy-MM-dd"/>'/>
    </td>
    <th>
        <label>发文部门：</label>
    </th>
    <td>
        <input type="text" name="pubOrg" class="required textInput" value="${document.Document.pubOrg}"/>
    </td>
    <th>
        <label>文件编号：</label>
    </th>
    <td>
        <input type="text" name="docCd" class="required textInput" value="${document.Document.docCd}"/>
    </td>
</tr>
<tr>
    <th>
        <label>文件状态：</label>
    </th>
    <td>
        <oframe:select prjCd="${param.prjCd}" itemCd="PRJ_DOC_STATUS" name="docStatus"
                       value="${document.Document.docStatus}"/>
    </td>
    <th>
        <label>发布组织：</label>
    </th>
    <td>
        <div style="position:relative;">
            <input type="hidden" name="prjOrgId" value="${document.Document.orgId}"/>
            <input type="text" name="prjOrgName" class="pull-left required"
                   readonly="readonly" value="${document.Document.orgName}"/>
            <a title="选择" onclick="pj013.editOrg(this);" class="btnLook">选择</a>
        </div>
    </td>
</tr>
<tr>
    <th>
        <label>文件关键字：</label>
    </th>
    <td colspan="5">
        <input type="text" name="keyWordTemp" onchange="pj013.changeKeyWord(this);"/>
        <label class="marl5 marr5 pad10 js_keyword"
               style="position: relative;display: none;color:#ffffff;background-color:#6ab625;border-color:#6ab625;">
            <input type="hidden" name="keyWord" value=""/>
            <span class="in-block">${keyWord}</span>
            <c:if test="${writeFlag}">
                <span class="removeX" onclick="$(this).parent().remove();">X</span>
            </c:if>
        </label>
        <c:forEach items="${keyWords}" var="keyWord">
            <label class="marl5 marr5 pad10 js_keyword"
                   style="position: relative;display: inline-block;color:#ffffff;background-color:#6ab625;border-color:#6ab625;">
                <input type="hidden" name="keyWord" value="${keyWord}"/>
                <span class="in-block">${keyWord}</span>
                <c:if test="${writeFlag}">
                    <span class="removeX" onclick="$(this).parent().remove();">X</span>
                </c:if>
            </label>
        </c:forEach>
    </td>
</tr>
<tr>
    <th>
        <label>文件描述：</label>
    </th>
    <td colspan="5">
        <textarea rows="5" cols="100" name="docDesc" maxlength="1000">${document.Document.docDesc}</textarea>
    </td>
</tr>
<tr>
    <th>
        <label>附件资料：</label>
    </th>
    <td colspan="5" style="vertical-align: top;">
        <div id="pj01301UploadedFileContain_${document.Document.docType}"
             ondrop="upFile.dropFile(event, this);"
             docTypeName="${document.Document.docType}"
             relType="5"
             relId="${document.Document.docId}"
             ondragenter="return false"
             ondragover="return false"
             class="drag img-wrap"
             style="width: 50%;margin-left:0px;">
            <h2>
                项目资料：
                <c:if test="${writeFlag}">
                <span class="link btn btn-opt btn-opt-sm marr5"
                      onclick="upFile.openUploadFile(this);stopEvent(event);">[上传]</span>
                </c:if>
            </h2>

            <div class="mar10 img-list">
                <label style="" class="hidden">
                    <img class="show" style="max-width: 60px;" title="" src="">
                    <a target="_blank" title="" href=""> <span class="link"></span></a>
                    <input type="hidden" name="docId" value="">
                    <input type="hidden" name="docName" value="">
                    <input type="hidden" name="docTypeName" value="">
                    <c:if test="${writeFlag}">
                        <span class="remove">X</span>
                    </c:if>
                </label>
                <c:forEach items="${svcDocs}" var="svcDoc">
                    <label onmousedown="">
                        <input type="hidden" name="docId" value="${svcDoc.svcDoc.docId}">
                        <input type="hidden" name="docName" value="${svcDoc.svcDoc.docName}">
                        <input type="hidden" name="docTypeName" value="${svcDoc.svcDoc.docTypeName}">
                        <c:set var="imgSrc"
                               value="${pageContext.request.contextPath}/oframe/themes/images/img.png"/>
                        <c:set var="docName" value="${fn:toLowerCase(svcDoc.svcDoc.docName)}"/>
                        <c:if test="${!fn:endsWith(docName, 'jpeg') && !fn:endsWith(docName, 'gif')
                                             && !fn:endsWith(docName, 'jpg') && !fn:endsWith(docName, 'png')
                                             && !fn:endsWith(docName, 'bmp') && !fn:endsWith(docName, 'pic')}">
                            <c:set var="imgSrc"
                                   value="${pageContext.request.contextPath}/oframe/themes/images/word.png"/>
                        </c:if>
                        <img class="show" style="max-width: 60px;"
                             title="${svcDoc.svcDoc.docName}"
                             realSrc="${pageContext.request.contextPath}/oframe/common/file/file-download.gv?docId=${svcDoc.svcDoc.docId}"
                             src="${imgSrc}"/>
                        <a target="_blank" title="${svcDoc.svcDoc.docName}"
                           href="${pageContext.request.contextPath}/oframe/common/file/file-download.gv?docId=${svcDoc.svcDoc.docId}">
                            <span class="link">${svcDoc.svcDoc.docName}</span>
                        </a>
                        <c:if test="${writeFlag}">
                            <span class="remove">X</span>
                        </c:if>
                    </label>
                </c:forEach>
            </div>
        </div>
    </td>
</tr>
<tr>
    <th>
        <label>关联资料：</label>
    </th>
    <td align="left" colspan="5" style="border: 2px;">
        <table id="pj01301_doc" class="list" width="50%">
            <thead>
            <tr>
                <th>关联类型</th>
                <th>关联文件</th>
                <th>
                    <c:if test="${writeFlag}">
                        <span class="link btn btn-opt btn-opt-sm marr5"
                              onclick="table.addRow('pj01301_doc',this);stopEvent(event);">[增加]</span>
                    </c:if>
                </th>
            </tr>
            </thead>
            <tbody>
            <tr class="hidden">
                <td><oframe:select prjCd="${param.prjCd}" itemCd="REL_DOC_TYPE" name="relName"  value=""/></td>
                <td>
                    <input type="hidden" name="relBDocId" value=""/>
                    <input type="text" name="relDocName"
                           atOption="pj013.getOption"
                           atUrl="pj013.getUrl"
                           value=""
                           class="autocomplete required"/>
                </td>
                <td><c:if test="${writeFlag}">
                    <span class="link btn btn-opt btn-opt-sm marr5" onclick="table.deleteRow(this);stopEvent(event);">[移除]</span></c:if>
                </td>
            </tr>
            <c:forEach items="${relDocs}" var="relDoc">
                <tr>
                    <td><oframe:select prjCd="${param.prjCd}" itemCd="REL_DOC_TYPE" name="relName"
                                       value="${relDoc.relDocument.relName}"/></td>
                    <td><input type="hidden" name="relBDocId" value="${relDoc.relDocument.docId}"/>
                        <input type="text" name="relDocName" id="doc_name"
                               atOption="pj013.getOption"
                               atUrl="pj013.getUrl"
                               value="${relDoc.relDocument.docName}"
                               class="autocomplete required"/></td>
                    <td><c:if test="${writeFlag}"><span class="link btn btn-opt btn-opt-sm marr5"
                                                        onclick="table.deleteRow(this);stopEvent(event);">[移除]</span></c:if>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </td>
</tr>
</table>
</div>
</form>
</div>
<oframe:script src="${pageContext.request.contextPath}/eland/pj/pj013/js/pj013.js" type="text/javascript"/>
