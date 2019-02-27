<%--待办任务信息面板--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="tabs">
    <div class="tabsHeader">
        <div class="tabsHeaderContent">
            <ul>
                <li class="js_load_tab selected">
                    <a href="javascript:void(0);"><span>记录日志</span></a>
                </li>
                <li class="js_load_tab" onclick="Page.query($('#ph00302_formApplyfrm', navTab.getCurrentPanel()), '');">
                    <a href="javascript:void(0);"><span>审批记录</span></a>
                </li>
                <li class="js_load_tab">
                    <a href="javascript:void(0);"><span>待办流程</span></a>
                </li>
                <li class="js_load_tab"
                    onclick=" Query.queryList('ph00302_his', 'ph00302_his_page_data');">
                    <a href="javascript:void(0);"><span>办结流程</span></a>
                </li>

            </ul>

        </div>

    </div>
    <div class="tabsContent">
        <%--日志记录--%>
        <div style="min-height: 300px;">
            <form id="_ph00302frm_spat" method="get">
                <input type="hidden" name="relType" value="3"/>
                <input type="hidden" name="relObjectId" value="${hsId}"/>
                <input type="hidden" name="doRecordPerson" value="${talkInfo.PrjTalkRecord.doRecordPerson}"/>
                <input type="hidden" name="recordToPerson" value="${talkInfo.PrjTalkRecord.recordToPerson}"/>
                <input type="hidden" name="startTime" value="${talkInfo.PrjTalkRecord.startTime}"/>
                <input type="hidden" name="endTime" value="${talkInfo.PrjTalkRecord.endTime}"/>
                <div style="height: 260px;overflow: auto; background-color: #f0f0f0;">
                    <textarea id="talkContext" rows="17" class="required readonly publicEditor" readonly=""
                              name="talkContext" style="width: 100%;height: 204px;box-sizing: border-box;"></textarea>
                    <%--<script typescripttype="text/javascript" src="../../../oframe/plugin/ueditor/ueditor.config.js">--%>
                        <%--UEDITOR_CONFIG.UEDITOR_HOME_URL = './ueditor/'; //一定要用这句话，否则你需要去ueditor.config.js修改路径的配置信息--%>
                        <%--UE.getEditor('talkContext');--%>
                    <%--</script>--%>
                </div>
            </form>
            <div style="text-align: center">
                <span class="mart10 btn btn-info" onclick="ph00302.submitMemo();">提交日志</span>
            </div>
        </div>
        <%--审批记录--%>
        <div>
            <form id="ph00302_formApplyfrm" method="get">
                <input type="hidden" name="tableLayOut" value="75"/>
                <input type="hidden" name="simplePageFlag" value="true"/>
                <input type="hidden" name="entityName" value="CheckRecord"/>
                <input type="hidden" name="sortColumn" value="checkTime">
                <input type="hidden" name="sortOrder" value="desc">
                <input type="hidden" name="conditionName" value="checkRelType,checkRelId">
                <input type="hidden" name="condition" value="=,=">
                <input type="hidden" name="conditionValue" value="100,${hsId}">
                <input type="hidden" class="js_conditionValue" value="">
                <input type="hidden" name="divId" value="form_check_apply_list">
                <input type="hidden" name="resultField" value="checkStaff,checkTime,checkOpName,checkResult,checkNote">
                <input type="hidden" name="forward" value="/oframe/common/form/form_check_list"/>
                <input type="hidden" name="cmptName" id="cmptName" value="QUERY_ENTITY_PAGE_DATA"/>
            </form>
            <div id="ph00302_form_check_apply_list" class="js_page layoutBox" style="height: 305px;"></div>
        </div>
        <div style="min-height: 305px;">
            <table width="100%" class="table">
                <thead>
                <tr>
                    <th><label>任务名称</label></th>
                    <th><label>任务派发</label></th>
                    <th><label>待处理人</label></th>
                    <th><label>滞留时间</label></th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${toDoTask}" var="item">
                    <tr style="cursor: pointer;" onclick="index.viewWf('${item.RuTask.procInsId}');">
                        <td>${item.RuTask.taskName}</td>
                        <td><oframe:staff staffCode="${item.RuTask.procStUser}"/></td>
                        <td><oframe:staff staffCode="${item.RuTask.assignee}"/></td>
                        <td>${item.RuTask.delayDays}</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
        <div>
            <form id="ph00302_his" method="post">
                <%--自服务名称用于定位SQL--%>
                <input type="hidden" name="selfDefCmpt" value="QUERY_PROC_INSTANC_EPAGE"/>
                <%-- 分页展示 --%>
                <input type="hidden" name="forward" id="forward" value="/eland/ph/ph003/ph00302_list"/>
                <input type="hidden" name="isComplete" value="1"/>
                <input type="hidden" name="businessKey" value="HouseInfo_${param.hsId}"/>
            </form>
            <div id="ph00302_his_page_data" style="min-height: 265px;">
                <table class="table" width="100%">
                    <thead>
                    <tr>
                        <th>请求名称</th>
                        <th onclick="Query.sort('ph00302_his_page_data', this);"
                            sortColumn="procStTime" class="sortable">发起时间
                        </th>
                        <th onclick="Query.sort('ph00302_his_page_data', this);"
                            defaultSort="desc" sortColumn="procEndime" class="sortable">完成时间
                        </th>
                    </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>
            </div>
            <jsp:include page="/oframe/common/page/pager_fotter_simple.jsp"/>
        </div>
    </div>

</div>
<oframe:script type="text/javascript" src="${pageContext.request.contextPath}/eland/ph/ph003/js/ph00302.js"/>