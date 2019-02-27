<%--待办任务信息面板--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="panel">
    <h1>待办任务
        <span class="panel_menu js_reload">刷新</span>
        <span class="panel_menu js_more" style="position: relative;">发起流程
            <span class="caret"></span>
              <ul class="menu hidden">
                  <li onclick="pb002Task.initiateTask('REMIND_TASK_DEF_KEY', '${buildId}');">提醒流程</li>
                  <li onclick="pb002Task.initiateTask('<oframe:config itemCd="TT_WORK_FLOW"
                                                                      prjCd="${param.prjCd}"/>','${buildId}');">腾退流程
                  </li>
              </ul>
        </span>
    </h1>
    <%--面板内容--%>
    <div class="js_panel_context" style="height: 128px; padding: 0 5px 5px 0;">
        <table width="100%" class="table">
            <thead>
            <tr>
                <th><label>任务名称</label></th>
                <th><label>派 发 人</label></th>
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
</div>
<div class="panel mart5">
    <h1>进度记录
        <span class="panel_menu" onclick="pb002Task.submitMemo();">提交</span>
    </h1>
    <%--面板内容--%>
    <div class="js_panel_context" style="min-height: 100px;">
        <form id="_pb002Taskfrm_spat">
            <input type="hidden" name="relType" value="2"/>
            <input type="hidden" name="relObjectId" value="${buildId}"/>
            <textarea placeholder="输入进度说明提交，方便日后跟进处理"
                      name="talkContext" style="width: 98%;" rows="6"></textarea>
        </form>
    </div>
</div>
<oframe:script type="text/javascript" src="${pageContext.request.contextPath}/eland/pb/pb002/js/pb002_task.js"/>