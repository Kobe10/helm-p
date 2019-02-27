<%--待办任务信息面板--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>

<%--面板内容--%>
<div class="panel" style="padding: 0 5px 5px 0; margin-top: 5px; margin-left: 10px;margin-right: 10px;">
    <c:forEach items="${hiTaskList}" var="item" varStatus="varStatus">
        <div class="js_panel_context">
            <div style="background-color: #ffffff;min-height: 150px;margin-bottom: 20px;">
                <table class="border" style="border: 1px solid #000000" width="100%">
                    <tr style="background-color: #accdf4">
                        <td colspan="4" style="line-height: 25px">&nbsp;&nbsp;<b><oframe:staff
                                staffCode="${initPer}"/></b> 于 <b>${item.hitask.startTime}</b>
                            发起任务：<b>${item.hitask.taskName}</b>。
                        </td>
                    </tr>
                    <tr>
                        <th><label>任务内容：</label></th>
                        <td colspan="3">${item.hitask.description}</td>
                    </tr>
                    <tr>
                        <th width="15%"><label>完成时间：</label></th>
                        <td>${item.hitask.endTime}</td>
                        <th width="15%"><label>滞留时间：</label></th>
                        <td>${item.hitask.duration}</td>
                    </tr>
                    <tr>
                        <th><label>处理内容：</label></th>
                        <td colspan="3">${item.hitask.description}</td>
                    </tr>
                </table>
            </div>
        </div>
    </c:forEach>
</div>
<oframe:script type="text/javascript" src="${pageContext.request.contextPath}/eland/ph/ph003/js/ph00302.js"/>