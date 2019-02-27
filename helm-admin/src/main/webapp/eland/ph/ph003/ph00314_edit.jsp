<%--
  保存居民访谈记录
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<div class="pagecontent" style="position: relative;height: 100%;">
    <div class="panelBar">
        <ul class="toolBar">
            <%--<c:set var="uneditUE" value="uneditUE"/>--%>
            <c:set var="readonly" value="readonly"/>
            <c:if test="${method != 'view'}">
                <li><a class="save" onclick="ph00314.submitRecord();"><span>修改</span></a></li>
                <%--<c:set var="uneditUE" value=""/>--%>
                <c:set var="readonly" value=""/>
            </c:if>
            <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
            <li style="float: right;">
                <a class="reveal edit_hidden change_img" onclick="ph00314.changeShowInfo(this);">
                    <span class="set_show">显示设置</span>
                    <span class="set_hidden" style="display: none">隐藏设置</span>
                </a>
            </li>
        </ul>
    </div>
    <form id="ph00314frm" method="post" style="height: 250px;overflow: auto">
        <input type="hidden" name="recordId" value="${talkInfo.OpResult.recordId}"/>
        <input type="hidden" name="recordRelId" value="${talkInfo.OpResult.recordRelId}"/>
        <input type="hidden" name="recordType" value="3"/>
        <table class="def_hidden form hidden" width="100%">
            <tr>
                <th width="15%">
                    <label>谈话人：</label>
                </th>
                <td>
                    <input type="text" class="msize required" ${readonly} style="width: 70%;"
                           name="doRecordPerson" value="${talkInfo.OpResult.doRecordPerson}"/>
                </td>
                <th width="15%">
                    <label>被谈话人：</label>
                </th>
                <td>
                    <input type="text" class="msize required" ${readonly}
                           style="width: 70%;"
                           name="recordToPerson" value="${talkInfo.OpResult.recordToPerson}"/>
                </td>
            </tr>
            <tr>
                <th width="15%">
                    <label>开始时间：</label>
                </th>
                <td>
                    <input type="text" class="date" ${readonly} datefmt="yyyy-MM-dd HH:mm:ss" name="startTime"
                           style="width: 70%;"
                           value='${talkInfo.OpResult.startTime}'>
                </td>
                <th width="15%">
                    <label>结束时间：</label>
                </th>
                <td>
                    <input type="text" class="date textInput" ${readonly} datefmt="yyyy-MM-dd HH:mm:ss" name="endTime"
                           style="width: 70%;"
                           value='${talkInfo.OpResult.endTime}'/>
                </td>
            </tr>
        </table>
        <div style="height: 250px" class="def_show" contenteditable="false">
                    <textarea class="required publicEditor ${uneditUE}" rows="7" style="width: 99.8%;height: 193px;"
                              name="recordContext">${talkInfo.OpResult.recordContext}</textarea>
        </div>
    </form>
</div>
<oframe:script src="${pageContext.request.contextPath}/eland/ph/ph003/js/ph00314.js" type="text/javascript"/>
