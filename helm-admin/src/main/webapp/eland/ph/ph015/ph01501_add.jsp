<%--居民签约控制--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<div class="pagecontent">
    <div class="panelBar">
        <ul class="toolBar">
            <c:if test="${method == 'edit'}">
            <li><a class="save" onclick="ph015.submitMemo();"><span>保存</span></a></li>
            </c:if>
            <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
        </ul>
    </div>
    <div style="text-align: center;min-height: 305px;">
        <form id="ph015frm_spat" method="get">
            <input type="hidden" name="recordRelId" value="${recordRelId}"/>
            <input type="hidden" name="recordId" value="${recordId}"/>
            <input type="hidden" name="recordType" value="${recordType}"/>
            <input type="hidden" name="recordTopic" value="${recordTopic}"/>
            <table class="border">
                <tr>
                    <th width="10%">原告人：</th>
                    <td width="20%">
                        <input style="float: left" type="text" name="doRecordPerson" value="${resultList.Row.doRecordPerson}" />
                    </td>
                    <th width="10%">被告人：</th>
                    <td width="20%">
                        <input style="float: left" type="text" name="recordToPerson" value="${resultList.Row.recordToPerson}"/>
                    </td>
                </tr>
                <tr>
                    <th width="10%">开庭时间：</th>
                    <td width="20%">
                        <input style="float: left" type="text"
                               value='<oframe:date value="${resultList.Row.startTime}" format="yyyy-MM-dd HH:mm:ss"/>'
                               class="date" datefmt="yyyy-MM-dd HH:mm:ss" name="startTime"/>
                    </td>
                    <th width="10%">闭庭时间：</th>
                    <td width="20%">
                        <input style="float: left" type="text"
                               value='<oframe:date value="${resultList.Row.endTime}" format="yyyy-MM-dd HH:mm:ss"/>'
                               class="date" datefmt="yyyy-MM-dd HH:mm:ss" name="endTime"/>
                    </td>
                </tr>
                <tr>
                    <th width="10%">内容概述：</th>
                    <td width="20%" colspan="3">
                        <textarea rows="17" class="required" name="recordContext"
                                  style="width: 98%;">${resultList.Row.recordContext}</textarea>
                    </td>
                </tr>
                <tr>
                    <th width="10%">关联附件：</th>
                    <td colspan="6" style="text-align: left">
                        <c:choose>
                            <c:when test="${method == 'edit'}">
                                <span class="link marl5 js_doc_info" docTypeName="${dealType}处理附件" relType="100"
                                      onclick="ph015.showDoc(this,'${hsId}')">
                                    <label style="cursor:pointer;">上传附件</label>
                                    <input type="hidden" name="docId" value='${resultList.Row.docId}'>
                                </span>
                            </c:when>
                            <c:otherwise>
                                <span class="link marl10 js_doc_info" docTypeName="${dealType}处理附件" editAble="false" relType="100"
                                      onclick="ph015.showDoc(this,'${hsId}')">
                                    <label style="cursor:pointer;">查看附件</label>
                                    <input type="hidden" name="docIds" value='${resultList.Row.docId}'>
                                </span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>