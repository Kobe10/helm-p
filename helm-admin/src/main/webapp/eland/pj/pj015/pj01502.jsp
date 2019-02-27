<%--
    新建文件窗口
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<style>
    span.js_toStaff_span {
        position: relative;
        display: inline-block;
        color: #000000;
        border: 1px solid #000000;
        padding: 5px;
        margin: 0 2px 2px 0;
    }

    span.js_toStaff_span label.rmStaff {
        cursor: pointer;
        color: #fb7226;
    }
</style>
<div id="regionBar" class="panelBar">
    <ul class="toolBar">
        <c:set var="writeFlag" value="false"/>
        <oframe:power prjCd="${param.prjCd}" rhtCd="edit_prj_doc_rht">
            <c:set var="writeFlag" value="true"/>
        </oframe:power>
        <oframe:op prjCd="${param.prjCd}" template="li" cssClass="save" name="保存"
                   rhtCd="edit_prj_doc_rht" onClick="pj015.saveDocInfo()"/>
        <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
    </ul>
</div>
<form id="pj015Docform" method="post" class="required-validate">
    <div class="mart5" layoutH="50" style="min-height:400px !important; overflow:visible !important;">
        <div class="tabs">
            <div class="tabsHeader">
                <div class="tabsHeaderContent">
                    <ul>
                        <li><a href="javascript:"><span>基本信息</span></a></li>
                        <li><a href="javascript:"><span>关联附件</span></a></li>
                        <li><a href="javascript:"><span>文档记录</span></a></li>
                    </ul>
                </div>
            </div>
            <div class="tabsContent">
                <div>
                    <input type="hidden" name="docFlag" value="${docInfo.DocumentInfo.docFlag}">
                    <input type="hidden" name="docId" value="${docInfo.DocumentInfo.docId}">
                    <input type="hidden" name="upDocId" value="${docInfo.DocumentInfo.upDocId}">
                    <input type="hidden" name="fileFlag" value="${fileFlag}">
                    <table class="border" id="ownerTable" width="100%">
                        <tbody>
                        <tr>
                            <th width="15%"><label>文件名称：</label></th>
                            <td><input type="text" name="docName" value="${docInfo.DocumentInfo.docName}"></td>
                            <th width="15%" ${docFlag}><label>文件类型：</label></th>
                            <td><oframe:select prjCd="${param.prjCd}" itemCd="PRJ_DOCUMENT_TYPE" cached="true"
                                               name="docType" withEmpty="true"
                                               value="${docInfo.DocumentInfo.docType}"
                                               onChange="pj015.onHsOwnerTypeChange()" />
                            </td>
                        </tr>
                        <tr>
                            <th><label class="hsTypeLabel">文件编号：</label></th>
                            <td><input type="text" name="docCd" value="${docInfo.DocumentInfo.docCd}"></td>
                            <th ${docFlag}><label>文件来源：</label></th>
                            <td><oframe:select prjCd="${param.prjCd}" itemCd="PRJ_DOCUMENT_SOURCE" cached="true"
                                               name="docSrc" withEmpty="true"
                                               value="${docInfo.DocumentInfo.docSrc}"
                                               onChange="pj015.onHsOwnerTypeChange()" />
                            </td>
                        </tr>
                        <tr>
                            <th><label class="hsTypeLabel">发文部门：</label></th>
                            <td><input type="text" name="pubOrg" value="${docInfo.DocumentInfo.pubOrg}"></td>
                            <th><label class="hsTypeLabel">发文时间：</label></th>
                            <td>
                                <input type="text" name="pubDate"
                                       value='<oframe:date value="${docInfo.DocumentInfo.pubDate}" format="yyyy-MM-dd"/>'
                                       class="date">
                            </td>
                        </tr>
                        <tr>
                            <th><label>文件状态：</label></th>
                            <td colspan="3"><oframe:select prjCd="${param.prjCd}" itemCd="PRJ_DOC_STATUS" cached="true"
                                                           name="docStatus" withEmpty="true"
                                                           value="${docInfo.DocumentInfo.docStatus}"
                                                           onChange="pj015.onHsOwnerTypeChange()" />
                            </td>
                        </tr>
                        <tr>
                            <th><label>授权角色：</label></th>
                            <td colspan="3" style="position: relative;">
                                <span style="float: left;">
                                    <a title="选择" onclick="pj015.editRole(this);" class="btnLook">选择</a>
                                </span>
                                <span id="sys00201RoleSpan" style="height: 31px;line-height: 31px;">
                                    <span class="hidden" style="float: left;">
                                       <input checked="true" name="roleCd" value="" type="checkbox"/>
                                    </span>
                                    <c:forEach items="${roleList}" var="role">
                                        <input type="hidden" name="oldRoles" value="${role}"/>
                                        <span><input name="roleCd" type="checkbox" checked="true"
                                                     value="${role}"/><oframe:role roleCd="${role}"/></span>
                                    </c:forEach>
                                </span>
                            </td>
                        </tr>
                        <tr>
                            <th><label>授权用户：</label></th>
                            <input type="hidden" name="oldToStaffId" value="${userStr}"/>
                            <input type="hidden" name="toStaffId" value="${userStr}"/>
                            <td colspan="3" style="position: relative;">
                                <div style="position: absolute;">
                                   <span style="float: left;">
                                        <a title="选择"
                                           onclick="$.fn.sltStaff(this,{width: 250, fromOp:'pj01502', prjCd:0});"
                                           class="btnLook">选择</a>
                                   </span>
                                </div>
                                <div class="js_toStaff_div" style="margin-left:30px;display: inline-block">
                                    <span class="hidden marb5">
                                        <span class="js_toStaff_name"></span>
                                        <input type="hidden" name="currentStaff" value=""/>
                                        <label class="rmStaff" onclick="pj015.rmToStaff(this);">X</label>
                                    </span>
                                    <c:forEach items="${userList}" var="user">
                                        <span class="js_toStaff_span">
                                            <span class="js_toStaff_name"><oframe:staff staffId="${user}"/></span>
                                            <input type="hidden" name="currentStaff" value="${user}"/>
                                            <label class="rmStaff" onclick="pj015.rmToStaff(this);">X</label>
                                        </span>
                                    </c:forEach>
                                </div>
                            </td>
                        </tr>
                        <tr style="height: 45px;">
                            <th><label>关键字：</label></th>
                            <td colspan="3">
                                <input type="text" name="docKey"
                                       onchange="pj015.changeKeyWord(this);"
                                       style="width: 100px;" value="">
                                <label class="marl5 marr5 pad10 key_label js_keyword" style="display: none;">
                                    <input type="hidden" name="keyWord" value=""/>
                                    <span class="key_text in-block">${keyWord}</span>
                                    <c:if test="${writeFlag}">
                                        <span class="key_remove" onclick="$(this).parent().remove();">X</span>
                                    </c:if>
                                </label>
                                <c:set var="keyWords" value="${docInfo.DocumentInfo.docKey}"/>
                                <c:set var="keyWordArr" value="${fn:split(keyWords, ',')}"/>
                                <c:forEach items="${keyWordArr}" var="keyWord">
                                    <c:if test="${keyWord!=''}">
                                        <label class="marl5 marr5 pad10 key_label js_keyword">
                                            <input type="hidden" name="keyWord" value="${keyWord}"/>
                                            <span class="key_text in-block">${keyWord}</span>
                                            <c:if test="${writeFlag}">
                                                <span class="key_remove" onclick="$(this).parent().remove();">X</span>
                                            </c:if>
                                        </label>
                                    </c:if>
                                </c:forEach>
                            </td>
                        </tr>
                        <tr style="height: 65px;">
                            <th><label>描述信息：</label></th>
                            <td colspan="3">
                                <textarea name="docDesc" rows="3"
                                          style="height: 100%; width: 80%;">${docInfo.DocumentInfo.docDesc}</textarea>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </div>
                <div id="pj01502RelDiv">
                    <table class="border" style="text-align: center" id="pj01502">
                        <tbody>
                        <tr style="text-align: center">
                            <td width="25%"><label>关联类型</label></td>
                            <td width="50%"><label>关联文件</label></td>
                            <td>
                                <c:if test="${writeFlag}">
                                    <span class="link" style="padding: 3px" onclick="pj015.sltBDocTree(this)">增加</span>
                                </c:if>
                            </td>
                        </tr>

                        <tr style="text-align: center" class="hidden">
                            <td><oframe:select prjCd="${param.prjCd}" itemCd="REL_DOC_TYPE" name="relDocType"
                                               /></td>
                            <td>
                                <div style="position:relative;">
                                    <input type="hidden" name="relDocId" value=""/>
                                    <input type="text" name="relDocName"
                                           style="float: left;"
                                           class="pull-left"
                                           readonly="readonly"
                                           value=''/>
                                    <a title="选择" style="float: left;"
                                       onclick="pj015.sltDocTree(this);" class="btnLook">选择</a>
                                </div>
                            </td>
                            <td>
                                <c:if test="${writeFlag}">
                                    <span class="link" style="padding: 3px" onclick="table.deleteRow(this)">[删除]</span>
                                </c:if>
                            </td>
                        </tr>
                        <c:forEach items="${relDocList}" var="relDoc">
                            <tr style="text-align: center">
                                <td><oframe:select prjCd="${param.prjCd}" itemCd="REL_DOC_TYPE"
                                                   value="${relDoc.DocumentRel.relName}"
                                                   name="relDocType" /></td>
                                <td>
                                    <div style="position:relative;">
                                        <input type="hidden" name="relDocId"
                                               value="${relDoc.DocumentRel.relDocId}"/>
                                        <input type="text" name="relDocName"
                                               style="float: left;"
                                               class="pull-left"
                                               readonly="readonly"
                                               value=' <oframe:tree treeName="DocumentInfo"
                                                     primaryValue="${relDoc.DocumentRel.relDocId}"
                                                     property="docName"/>'
                                                />
                                        <a title="选择" style="float: left;"
                                           onclick="pj015.sltDocTree(this);" class="btnLook">选择</a>
                                    </div>
                                </td>
                                <td>
                                    <c:if test="${writeFlag}">
                                        <span class="link" style="padding: 3px"
                                              onclick="table.deleteRow(this)">[删除]</span>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>

                        </tbody>
                    </table>
                </div>

                <div id="pj01503RelDiv">
                    <table class="border" layoutH="100" adjust="true" width="100%"
                           style="text-align: center;font-family: '微软雅黑', Verdana;">
                        <thead>
                        <tr>
                            <td style="height: 25px">IP地址</td>
                            <td style="height: 25px">时间</td>
                            <td style="height: 25px">操作人</td>
                            <td style="height: 25px">操作</td>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${resultList}" var="item">
                            <tr>
                                <td style="height: 25px">${item.fromIp}</td>
                                <td style="height: 25px">${item.opStartTime}</td>
                                <td style="height: 25px"><oframe:staff staffCode="${item.opStaffCode}"/></td>
                                <td style="height: 25px">
                                        ${item.opNote}
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                    <div>
                        <div id="hs00101_w_list_print" class="result">
                        </div>
                        <%--<jsp:include page="/oframe/common/page/pager_fotter.jsp">--%>
                        <%--<jsp:param name="showPages" value="false"/>--%>
                        <%--</jsp:include>--%>
                    </div>
                </div>
            </div>
        </div>
    </div>
</form>
</div>