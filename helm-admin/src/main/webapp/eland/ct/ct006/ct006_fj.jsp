<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="panelBar">
    <ul class="toolBar">
        <c:set var="ctStatus" value="${ctStatus}"/>
        <c:set var="hsCtId" value="${hsInfo.HouseInfo.HsCtInfo.hsCtId}"/>
        <input type="hidden" name="oldHsId" value="${oldHsId}"/>
        <input type="hidden" name="newHsId" value="${newHsId}"/>
        <li onclick="ct006.ctInfo('${oldHsId}', '${newHsId}','viewDoc');">
            <a class="print" href="javascript:void(0)"><span>协议文档</span></a>
        </li>
        <c:if test="${ctStatus == '2'}">
            <oframe:op prjCd="${param.prjCd}" template="li" cssClass="cancel"
                       name="取消签约" rhtCd="cancel_ct_${schemeType}_rht"
                       onClick="ct006.cancelCt('${hsCtId}', '${oldHsId}','${newHsId}');"/>
        </c:if>
        <li onclick="ct006.viewHouse('${oldHsId}');">
            <a class="new-area" href="javascript:void(0)"><span>居民详情</span></a>
        </li>
    </ul>
</div>
<div class="panelcontainer" layoutH="55" style="border: 1px solid #e9e9e9;">
    <table class="border">
        <tr>
            <th width="10%"><label>房屋编号：</label></th>
            <td width="15%">${hsInfo.HouseInfo.hsCd}</td>
            <th width="10%"><label>被安置人：</label></th>
            <td width="20%">${hsInfo.HouseInfo.hsOwnerPersons}</td>
            <th width="12%"><label>原房屋地址：</label></th>
            <td width="30%">${hsInfo.HouseInfo.hsFullAddr}</td>
        </tr>
        <tr>
            <th><label>签约状态：</label></th>
            <td>
                <oframe:name prjCd="${param.prjCd}" itemCd="CONTRACT_STATUS"
                             value="${ctStatus}"/>
            </td>
            <th><label>签约时间：</label></th>
            <td><oframe:date value="${ctDate}"/></td>
            <th><label>安置房地址：</label></th>
            <td>${newHsInfo.Row.hsAddr}</td>
        </tr>
    </table>
    <div id="ct006_ReportData" layoutH="127">
        <table width="100%">
            <tr>
                <td width="49%">
                    <div class="panel js_ct006_fj_file">
                        <h1>签字协议附件
                            <span class="panel_menu" docTypeName="${signDocName}" relType="100"
                                  onclick="ct006.showDoc(this,'${oldHsId}')">
                                <label style="cursor:pointer;">上传</label>
                                <input type="hidden" name="docIds" value='${signDocIds}'>
                            </span>
                            <span class="panel_menu" onclick="ct006.showFjFile(this)">
                                <label style="cursor:pointer;">预览</label>
                            </span>
                        </h1>

                        <div class="js_panel_context album-list" layoutH="170">
                            <ul class="album-ul js_hidden_file_ul">
                                <li class="album-li js_hidden_file_li" style="text-align: center;display: none"
                                    onclick="ct006.viewPic(${item.Row.docId})">
                                    <div class="album-context">
                                        <img class="show" style="max-width: 120px;" width="120px" height="130px"
                                             title="${item.Row.docName}"
                                             src="${pageContext.request.contextPath}/oframe/common/file/file-download.gv?docId=${item.Row.docId}"/>
                                        <input type="hidden" name="ct00502_docId" class="js_docIds"
                                               value="${item.Row.docId}"/>
                                    </div>
                                    <span class="js_span_docName spanText">${item.Row.docName}</span>
                                </li>
                                <c:forEach items="${hsDocs}" var="item">
                                    <c:if test="${item.Row.docTypeName == signDocName}">
                                        <li class="album-li" style="text-align: center;"
                                            onclick="ct006.viewPic(${item.Row.docId})">
                                            <div class="album-context">
                                                <img class="show" style="max-width: 120px;" width="120px" height="130px"
                                                     title="${item.Row.docName}"
                                                     src="${pageContext.request.contextPath}/oframe/common/file/file-downview.gv?docId=${item.Row.docId}"/>
                                                <input type="hidden" name="ct00502_docId" class="js_docIds"
                                                       value="${item.Row.docId}"/>
                                            </div>
                                            <span class="spanText">${item.Row.docName}</span>
                                        </li>
                                    </c:if>
                                </c:forEach>
                            </ul>
                        </div>
                    </div>
                </td>
                <td>
                    <div class="panel js_ct006_fj_file">
                        <h1>签约现场附件
                            <span class="panel_menu" docTypeName="${photoDocName}" relType="100"
                                  onclick="ct006.showDoc(this,'${oldHsId}')">
                                <label style="cursor:pointer;">上传</label>
                                <input type="hidden" name="docIds" value='${photoDocIds}'>
                            </span>
                            <span class="panel_menu" onclick="ct006.showFjFile(this)">
                                <label style="cursor:pointer;">预览</label>
                            </span>
                        </h1>

                        <div class="js_panel_context album-list" layoutH="170">
                            <ul class="album-ul js_hidden_file_ul">
                                <li class="album-li js_hidden_file_li" style="text-align: center;display: none"
                                    onclick="ct006.viewPic(${item.Row.docId})">
                                    <div class="album-context">
                                        <img class="show" style="max-width: 120px;" width="120px" height="130px"
                                             title="${item.Row.docName}"
                                             src="${pageContext.request.contextPath}/oframe/common/file/file-download.gv?docId=${item.Row.docId}"/>
                                        <input type="hidden" name="ct00502_docId" class="js_docIds"
                                               value="${item.Row.docId}"/>
                                    </div>
                                    <span class="js_span_docName spanText">${item.Row.docName}</span>
                                </li>
                                <c:forEach items="${hsDocs}" var="item">
                                    <c:if test="${item.Row.docTypeName == photoDocName}">
                                        <li class="album-li" style="text-align: center;"
                                            onclick="ct006.viewPic(${item.Row.docId})">
                                            <div class="album-context">
                                                <img class="show" style="max-width: 120px;" width="120px" height="130px"
                                                     title="${item.Row.docName}"
                                                     src="${pageContext.request.contextPath}/oframe/common/file/file-downview.gv?docId=${item.Row.docId}"/>
                                                <input type="hidden" name="ct00502_docId" class="js_docIds"
                                                       value="${item.Row.docId}"/>
                                            </div>
                                            <span class="spanText">${item.Row.docName}</span>
                                        </li>
                                    </c:if>
                                </c:forEach>
                            </ul>
                        </div>
                    </div>
                </td>
            </tr>
        </table>
    </div>
</div>
