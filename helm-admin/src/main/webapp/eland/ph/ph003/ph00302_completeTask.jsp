<%--待办任务信息面板--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>

<%--面板内容--%>
<div class="panel" style="padding: 0 5px 5px 0; margin-top: 5px; margin-left: 10px;margin-right: 10px;">
    <div class="js_panel_context">
        <table width="100%" class="border">
            <tr>
                <th><label>任务名称：</label></th>
                <td><input type="hidden" name="hsId" value="${hsId}"/>
                    <input type="hidden" name="taskId" value="${taskId}"/>
                    ${taskInfo.WF.TaskName}
                </td>
                <th><label>发起人：</label></th>
                <td><oframe:staff staffCode="${taskInfo.WF.TaskAssignee}"/></td>
                <th><label>处理人：</label></th>
                <td>
                    <oframe:staff staffCode="${doStaffCd}"/>
                </td>
            </tr>
            <tr>
                <th><label>任务描述：</label></th>
                <td colspan="5">${taskInfo.WF.Description}</td>
            </tr>
            <tr>
                <th><label>发起时间：</label></th>
                <td>${taskInfo.WF.CreateTime}</td>
                <th><label>截至时间：</label></th>
                <td>${taskInfo.WF.DueDate}</td>
                <th><label>滞留时间：</label></th>
                <td>${taskInfo.WF.delayDays}</td>
            </tr>
            <tr>
                <th><label>处理说明：</label></th>
                <td colspan="5">
                    <textarea rows="5" name="taskCompleteDetails"
                              class="js_taskCompleteDetails"></textarea>
                </td>
            </tr>
            <tr style="text-align: center;">
                <td colspan="6">
                    <span class="btn-primary"
                          style="padding: 5px 10px 5px 10px; line-height: 34px; cursor:pointer;"
                          onclick="ph00302.completeTask('${hsId}')">完成任务</span>
                </td>
            </tr>
        </table>
    </div>
</div>
<oframe:script type="text/javascript" src="${pageContext.request.contextPath}/eland/ph/ph003/js/ph00302.js"/>