<%--居民工作日志跟踪面板--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="panel">
    <h1>居民工作日志
        <span class="panel_menu js_reload">刷新</span>
    </h1>
    <div style="min-height: 400px;">
        <form id="ph00309QueryForm">
            <input type="hidden" name="entityName" value="RecordInfo"/>
            <input type="hidden" name="haveRht" value="${haveRht}"/>
            <input type="hidden" name="staffId" value="${staffId}"/>
            <input type="hidden" name="sortColumn" value="publishDateTime">
            <input type="hidden" name="sortOrder" value="desc">
            <c:choose>
                <c:when test="${haveRht == 'true'}">
                    <input type="hidden" name="conditionName" value="recordType,recordRelId">
                    <input type="hidden" name="condition" value="=,=">
                    <input type="hidden" name="conditionValue" value="3,${hsId}">
                </c:when>
                <c:otherwise>
                    <input type="hidden" name="conditionName" value="recordType,recordRelId,publishStf">
                    <input type="hidden" name="condition" value="=,=,=">
                    <input type="hidden" name="conditionValue" value="3,${hsId},${staffId}">
                </c:otherwise>
            </c:choose>
            <input type="hidden" class="js_conditionValue" value="">
            <input type="hidden" name="forceResultField" value="recordId">
            <input type="hidden" name="divId" value="ph00309Context">
            <input type="hidden" name="resultField"
                   value="recordRelId,publishStf,publishDateTime,recordType,recordTopic,doRecordPerson,recordToPerson,startTime,endTime,recordContext">
            <input type="hidden" name="forward" id="forward" value="/eland/ph/ph003/ph00309_list"/>
            <input type="hidden" name="cmptName" id="cmptName" value="QUERY_ENTITY_PAGE_DATA"/>
        </form>
        <div id="ph00309Context" class="js_page" style="position: relative;width: 100%;"></div>
    </div>
</div>
<script type="text/javascript">
    var ph00309 = {
        /**
         * 点击快速提交按钮
         */
        submitMemo: function () {
            var $form = $("#_ph00309frm_spat", navTab.getCurrentPanel());
            if ($form.valid()) {
                // 提交页面数据
                var url = getGlobalPathRoot() + "eland/ph/ph002/ph002-saveWM.gv?prjCd=" + getPrjCd();
                var ajaxPacket = new AJAXPacket(url);
                ajaxPacket.data.data = $form.serializeArray();
                core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                    var jsonData = eval("(" + response + ")");
                    // 服务调用是否成功
                    var isSuccess = jsonData.success;
                    if (isSuccess) {
                        alertMsg.correct("处理成功");
                        $form.find("#talkContext").val("");
                        var form = $("#ph00309QueryForm", navTab.getCurrentPanel());
                        Page.query(form, "");
                    } else {
                        alertMsg.error(jsonData.errMsg);
                    }
                });
            }
        },

        /**
         *点击删除按钮
         * @param recordId
         */
        deleteRecord: function (recordId) {
            // 提交页面数据
            var url = getGlobalPathRoot() + "eland/ph/ph002/ph002-deleteWM.gv";
            var ajaxPacket = new AJAXPacket(url);
            ajaxPacket.data.add("recordId", recordId);
            ajaxPacket.data.add("prjCd", getPrjCd());
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var jsonData = eval("(" + response + ")");
                // 服务调用是否成功
                var isSuccess = jsonData.success;
                if (isSuccess) {
                    alertMsg.correct("处理成功");
                    var form = $("#ph00309QueryForm", navTab.getCurrentPanel());
                    Page.query(form, "");
                } else {
                    alertMsg.error(jsonData.errMsg);
                }
            });
        },

        /**
         * 点击编辑按钮
         * @param recordId
         */
        editRecord: function (recordId) {
            var url = getGlobalPathRoot() + "eland/ph/ph003/ph00314-initRecord.gv?prjCd=" + getPrjCd() + "&recordId=" + recordId;
            $.pdialog.open(url, "ph00314", "居民工作日志", {mask: true, height: 330, width: 800});
        },

        /**
         * 点击编辑按钮
         * @param recordId
         */
        viewRecord: function (recordId) {
            var url = getGlobalPathRoot() + "eland/ph/ph003/ph00314-initRecord.gv?prjCd=" + getPrjCd() + "&method=view&recordId=" + recordId;
            $.pdialog.open(url, "ph00314", "居民工作日志", {mask: true, height: 330, width: 800});
        }
    };

    $(document).ready(function () {
        var form = $("#ph00309QueryForm", navTab.getCurrentPanel());
        Page.query(form, "");
    });
</script>