<%--
  保存居民访谈记录
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<div class="pagecontent">
    <div id="ph00314FrmDiv">
        <div class="panelBar">
            <ul class="toolBar">
                <c:if test="${method != 'view'}">
                    <li><a class="add" onclick="ph00314.submitRecord();"><span>新增</span></a></li>
                </c:if>
                <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
                <li style="float: right;">
                    <a class="reveal change_img" onclick="ph00314.changeShowInfo(this);">
                        <span class="set_show">显示设置</span>
                        <span class="set_hidden" style="display: none">隐藏设置</span>
                    </a>
                </li>
            </ul>
        </div>
        <form id="ph00314frm" method="post" style="height: 250px;overflow: auto">
            <input type="hidden" name="recordRelId" value="${talkInfo.PrjTalkRecord.relObjId}"/>
            <input type="hidden" name="prjCd" value="${talkInfo.PrjTalkRecord.prjCd}"/>
            <input type="hidden" name="recordType" value="3"/>
            <table class="def_hidden form hidden" width="100%">
                <tr>
                    <th width="15%">
                        <label>谈话人：</label>
                    </th>
                    <td>
                        <input type="text" class="msize required" style="width: 70%;"
                               name="doRecordPerson" value="${talkInfo.PrjTalkRecord.doRecordPerson}"/>
                    </td>
                    <th width="15%">
                        <label>被谈话人：</label>
                    </th>
                    <td>
                        <input type="text" class="msize required"
                               style="width: 70%;"
                               name="recordToPerson" value="${talkInfo.PrjTalkRecord.recordToPerson}"/>
                    </td>
                </tr>
                <tr>
                    <th>
                        <label>开始时间：</label>
                    </th>
                    <td>
                        <input type="text" class="date" datefmt="yyyy-MM-dd HH:mm:ss" name="startTime"
                               style="width: 70%;"
                               value='${talkInfo.PrjTalkRecord.startTime}'>
                    </td>
                    <th>
                        <label>结束时间：</label>
                    </th>
                    <td>
                        <input type="text" class="date textInput" datefmt="yyyy-MM-dd HH:mm:ss" name="endTime"
                               style="width: 70%;"
                               value='${talkInfo.PrjTalkRecord.endTime}'/>
                    </td>
                </tr>
            </table>
            <div style="height: 200px" class="def_show">
                <textarea class="required publicEditor" rows="14" style="width: 99.8%;min-height: 193px"
                          name="recordContext">${talkInfo.PrjTalkRecord.recordContext}</textarea>
            </div>
        </form>
    </div>
    <div class="panel" style="min-height: 310px">
        <h1>
            历史工作日志
            <%--<button style="position: absolute;bottom: 6px;right: 6px;z-index: 9999" onclick="ph00314.changeShowInfo(this)">隐藏设置</button>--%>
        </h1>
        <div>
            <form id="_ph00314frm" method="get">
                <input type="hidden" name="entityName" value="RecordInfo"/>
                <input type="hidden" name="haveRht" value="${haveRht}"/>
                <input type="hidden" name="staffId" value="${staffId}"/>
                <input type="hidden" name="sortColumn" value="publishDateTime">
                <input type="hidden" name="sortOrder" value="desc">
                <c:choose>
                    <c:when test="${haveRht == 'true'}">
                        <input type="hidden" name="conditionName" value="recordType,recordRelId">
                        <input type="hidden" name="condition" value="=,=">
                        <input type="hidden" name="conditionValue" value="3,${talkInfo.PrjTalkRecord.relObjId}">
                    </c:when>
                    <c:otherwise>
                        <input type="hidden" name="conditionName" value="recordType,recordRelId,publishStf">
                        <input type="hidden" name="condition" value="=,=,=">
                        <input type="hidden" name="conditionValue"
                               value="3,${talkInfo.PrjTalkRecord.relObjId},${staffId}">
                    </c:otherwise>
                </c:choose>
                <input type="hidden" class="js_conditionValue" value="">
                <input type="hidden" name="forceResultField" value="recordId">
                <input type="hidden" name="divId" value="ph00314_w_list_print">
                <input type="hidden" name="resultField"
                       value="recordRelId,publishStf,publishDateTime,recordType,recordTopic,doRecordPerson,recordToPerson,startTime,endTime,recordContext">
                <input type="hidden" name="forward" id="forward" value="/eland/ph/ph003/ph00314_list"/>
                <input type="hidden" name="cmptName" id="cmptName" value="QUERY_ENTITY_PAGE_DATA"/>
            </form>
            <div id="ph00314_w_list_print" class="js_page" layoutH="340"></div>
        </div>
    </div>
</div>
<oframe:script src="${pageContext.request.contextPath}/eland/ph/ph003/js/ph00314.js" type="text/javascript"/>
<script type="text/javascript">
    $(document).ready(function () {
        $("textarea[name=recordContext]", $.pdialog.getCurrent()).focus();
//        Query.queryList('_ph00314frm', 'ph00314_w_list_print');
        var form = $("#_ph00314frm", $.pdialog.getCurrent());
        Page.query(form, "");
    });
</script>
