<%--裁决流程--谈话处理--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<style>
    .wfBtn {
        display: inline-block;
        padding: 8px;
        border-radius: 5px;
        cursor: pointer;
    }
</style>
<div>

    <div class="panel js_ph015_task" id="${taskId}">
        <h1>历史谈话记录
            <%--<span class="panel_menu js_reload">刷新</span>--%>
        </h1>
        <input type="hidden" name="recordType" value="${recordType}"/>
        <input type="hidden" name="hsId" value="${hsId}"/>
        <input type="hidden" name="method" value="${method}"/>
        <input type="hidden" name="taskId" value="${taskId}"/>

        <div>
            <form id="ph015form_result${recordType}">
                <input type="hidden" name="entityName" value="RecordInfo"/>
                <input type="hidden" name="conditionName" value="recordType,recordTopic">
                <input type="hidden" name="condition" value="=,=">
                <input type="hidden" name="conditionValue" value="${recordType},${taskId}">
                <input type="hidden" name="sortColumn" value="">
                <input type="hidden" name="sortOrder" value="">
                <input type="hidden" name="divId" value="ph015_page_data${recordType}">
                <input type="hidden" name="forceResultField" value="recordId,recordRelId,recordType">
                <input type="hidden" class="js_conditionValue" value="">
                <input type="hidden" name="cmptName" id="cmptName" value="QUERY_ENTITY_PAGE_DATA"/>
                <input type="hidden" name="resultField" value="doRecordPerson,recordToPerson,startTime,endTime">
                <c:if test="${recordType == '1'}">
                    <input type="hidden" name="forward" id="forward" value="/eland/ph/ph015/ph015_list"/>
                </c:if>
                <c:if test="${recordType == '2'}">
                    <input type="hidden" name="forward" id="forward" value="/eland/ph/ph015/ph01501_list"/>
                </c:if>
            </form>
            <div id="ph015_page_data${recordType}" class="js_page"></div>
        </div>
    </div>
    <form id="ph015_thcl_form${recordType}">
        <table class="border">
            <%--判断是 谈话处理页面 还是 上诉处理页面 recordType=1 为谈话处理--%>
            <c:choose>
                <c:when test="${method == 'edit' && recordType == 1}">
                    <tr>
                        <th width="15%">处理时间：</th>
                        <td width="20%">
                            <input class="date required" type="text" datefmt="yyyy-MM-dd" name="recordGoTime"
                                   value='<oframe:date value="${hiTaskInfo.HiTaskInfo.TskBidVars.recordGoTime}" format="yyyy-MM-dd"/>'/>
                        </td>
                        <th width="15%">是否继续谈话：</th>
                        <td width="20%">
                            <oframe:select prjCd="${param.prjCd}" itemCd="IS_FINTALK" name="isFinTalk"
                                           onChange="ph015.sltFinshTalk(this);"
                                           cssClass="js_pri" value="${hiTaskInfo.HiTaskInfo.TskBidVars.isFinTalk}"/>
                        </td>
                    </tr>
                    <tr class="js_isFinPerson">
                        <th width="15%">是否可以联系负责人：</th>
                        <td colspan="3" style="text-align: left">
                            <oframe:select prjCd="${param.prjCd}" itemCd="COMMON_T_F" name="isFinPerson"
                                           onChange="ph015.zbwcTime(this)"
                                           cssClass="js_pri" value="${hiTaskInfo.HiTaskInfo.TskBidVars.isFinPerson}"/>
                        </td>
                    </tr>
                    <tr class="js_zbwcTime" style="display: none;">
                        <th>征补决定登报自动完成时间：</th>
                        <td colspan="3">
                            <input type="hidden" name="du" class="js_pri" value="P60D"/>
                            <input type="text" name="du_val" value="60"
                                   placeholder="请设置自动完成的天数" onblur="ph015.doneDay(this);"
                                   style="width: 122px;text-align: center;"/>天
                        </td>
                    </tr>
                    <tr>
                        <th width="10%"><label>备注说明：</label></th>
                        <td colspan="3" style="text-align: left">
                            <textarea rows="8" style="margin: 0;" placeholder="请输入任务处理意见或备注说明。"
                                      name="comment">${hiTaskInfo.HiTaskInfo.TskBidVars.comment}</textarea>
                        </td>
                    </tr>
                    <tr>
                        <th width="15%">下一步处理人：</th>
                        <td style="text-align: left" colspan="3">
                            <input type="hidden" name="taskId" value="${taskId}"/>

                            <div style="position: relative;">
                                <input type="hidden" name="assignee" class="js_pri js_assignee" value="${staffCd}"/>
                                <input type="text" name="targetPerTemp" class="pull-left js_targetPerTemp" width="100px"
                                       onfocus="$(this).next('a').click()"
                                       value='<oframe:staff staffCode="${staffCd}"/>'
                                       placeholder="点击右侧按钮指定下一步处理人"/>
                                <a title="选择" onclick="$.fn.sltStaff(this,{});" class="btnLook">选择</a>
                            </div>
                        </td>
                    </tr>
                    <tr style="text-align: center;">
                        <td colspan="4">
                    <span class="btn-primary marl10 wfBtn"
                          onclick="ph015.submitVerifyassignee('ph015_thcl_form${recordType}', this, {})">提&nbsp;交&nbsp;处&nbsp;理</span>
                        </td>
                    </tr>
                </c:when>
                <c:when test="${method == 'edit' && recordType == 2}">
                    <tr>
                        <th width="15%">处理时间：</th>
                        <td width="85%">
                            <input class="date" type="text" datefmt="yyyy-MM-dd" name="recordGoTime"
                                   value='<oframe:date value="" format="yyyy-MM-dd"/>'/>
                        </td>
                    </tr>
                    <tr>
                        <th width="10%"><label>备注说明：</label></th>
                        <td style="text-align: left">
                            <textarea rows="8" style="margin: 0;" placeholder="请输入任务处理意见或备注说明。"
                                      name="comment"></textarea>
                        </td>
                    </tr>
                    <tr>
                        <th width="15%">下一步处理人：</th>
                        <td style="text-align: left" colspan="3">
                            <input type="hidden" name="taskId" value="${taskId}"/>

                            <div style="position: relative;">
                                <input type="hidden" name="assignee" class="js_pri js_assignee" value="${staffCd}"/>
                                <input type="text" name="targetPerTemp" class="pull-left js_targetPerTemp" width="100px"
                                       onfocus="$(this).next('a').click()"
                                       value='<oframe:staff staffCode="${staffCd}"/>'
                                       placeholder="点击右侧按钮指定下一步处理人"/>
                                <a title="选择" onclick="$.fn.sltStaff(this,{});" class="btnLook">选择</a>
                            </div>
                        </td>
                    </tr>
                    <tr style="text-align: center;">
                        <td colspan="4">
                    <span class="btn-primary marl10 wfBtn"
                          onclick="ph015.submitVerifyassignee('ph015_thcl_form${recordType}', this, {})">提&nbsp;交&nbsp;处&nbsp;理</span>
                        </td>
                    </tr>
                </c:when>
                <c:when test="${method != 'edit' && recordType == '1' && hiTaskInfo.HiTaskInfo.TskBidVars.isFinPerson != 'true'}">
                    <tr>
                        <th width="15%">处理时间：</th>
                        <td width="20%">${hiTaskInfo.HiTaskInfo.TskBidVars.recordGoTime}</td>
                        <th width="15%">是否继续谈话：</th>
                        <td width="20%">
                            <oframe:name prjCd="${param.prjCd}" itemCd="IS_FINTALK"
                                         value="${hiTaskInfo.HiTaskInfo.TskBidVars.isFinTalk}"/>
                        </td>
                    </tr>
                    <tr class="js_isFinPerson">
                        <th width="15%">是否可以联系负责人：</th>
                        <td colspan="3" style="text-align: left">
                            <oframe:name prjCd="${param.prjCd}" itemCd="COMMON_T_F"
                                         value="${hiTaskInfo.HiTaskInfo.TskBidVars.isFinPerson}"/>
                        </td>
                    </tr>
                    <tr class="js_zbwcTime">
                        <th>征补决定登报自动完成时间：</th>
                        <td colspan="3">${hiTaskInfo.HiTaskInfo.TskBidVars.du_val}天</td>
                    </tr>
                    <tr>
                        <th width="10%"><label>备注说明：</label></th>
                        <td colspan="3" style="text-align: left">${hiTaskInfo.HiTaskInfo.TskBidVars.comment}</td>
                    </tr>
                </c:when>
                <c:when test="${method != 'edit' && recordType == '1' && hiTaskInfo.HiTaskInfo.TskBidVars.isFinPerson == 'true'}">
                    <tr>
                        <th width="15%">处理时间：</th>
                        <td width="20%">${hiTaskInfo.HiTaskInfo.TskBidVars.recordGoTime}</td>
                        <th width="15%">是否继续谈话：</th>
                        <td width="20%">
                            <oframe:name prjCd="${param.prjCd}" itemCd="IS_FINTALK"
                                         value="${hiTaskInfo.HiTaskInfo.TskBidVars.isFinTalk}"/>
                        </td>
                    </tr>
                    <tr class="js_isFinPerson">
                        <th width="15%">是否可以联系负责人：</th>
                        <td colspan="3" style="text-align: left">
                            <oframe:name prjCd="${param.prjCd}" itemCd="COMMON_T_F"
                                         value="${hiTaskInfo.HiTaskInfo.TskBidVars.isFinPerson}"/>
                        </td>
                    </tr>
                    <tr>
                        <th width="10%"><label>备注说明：</label></th>
                        <td colspan="3" style="text-align: left">${hiTaskInfo.HiTaskInfo.TskBidVars.comment}</td>
                    </tr>
                </c:when>
                <c:otherwise>
                    <tr>
                        <th width="15%">处理时间：</th>
                        <td width="20%">${hiTaskInfo.HiTaskInfo.TskBidVars.recordGoTime}</td>
                        <th width="15%">是否继续谈话：</th>
                        <td width="20%">
                            <oframe:name prjCd="${param.prjCd}" itemCd="IS_FINTALK"
                                         value="${hiTaskInfo.HiTaskInfo.TskBidVars.isFinTalk}"/>
                        </td>
                    </tr>
                    <tr>
                        <th width="10%"><label>备注说明：</label></th>
                        <td colspan="3"
                            style="text-align: left;line-height: 50px">${hiTaskInfo.HiTaskInfo.TskBidVars.comment}</td>
                    </tr>
                </c:otherwise>
            </c:choose>
        </table>
    </form>
</div>
<oframe:script type="text/javascript" src="${pageContext.request.contextPath}/eland/ph/ph015/js/ph015.js"/>
<script>
    $(document).ready(function () {
        // 执行查询
        ph015.query('${taskId}');
    });
</script>