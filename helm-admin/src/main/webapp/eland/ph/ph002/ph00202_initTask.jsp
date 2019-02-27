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
                <td><input type="hidden" name="buildId" value="${buildId}"/>
                    <input type="text" class="js_taskName" name="taskName" value=""/>
                </td>
            </tr>
            <tr>
                <th><label>任务发起人：</label></th>
                <td><oframe:staff staffId="${initPer}"/></td>
            </tr>
            <tr>
                <th><label>任务详情描述：</label></th>
                <td><textarea rows="2" style="width: 550px; height: 45px;margin: 0px;" name="taskDetails"
                              class="js_taskDetails"></textarea></td>
            </tr>
            <tr>
                <th><label>任务指派给：</label></th>
                <td>
                    <input type="hidden" name="targetPer" value=""/>
                    <input type="text" name="targetPerTemp" atoption="ph00202.getOption" aturl="ph00202.getUrl"
                           class="autocomplete required acInput textInput valid js_targetPer" autocomplete="off">
                </td>
            </tr>
            <tr>
                <th><label>任务截至时间：</label></th>
                <td><input type="text" name="taskEndDate" class="date js_taskEndDate" datefmt="yyyy-MM-dd HH:mm:ss" value=""/></td>
            </tr>
            <tr style="text-align: center;">
                <td colspan="2">
                    <span class="btn-primary"
                          style="padding: 5px 10px 5px 10px; line-height: 34px; cursor:pointer;"
                          onclick="ph00202.cfmSendTask('${buildId}')">发送任务</span>
                    <span class="btn-primary"
                          style="padding: 5px 10px 5px 10px; line-height: 34px; cursor:pointer;"
                          onclick="ph00202.cancelTask(this)">取消任务</span>
                </td>
            </tr>
        </table>
    </div>
</div>
<oframe:script type="text/javascript" src="${pageContext.request.contextPath}/eland/ph/ph002/js/ph00202.js"/>