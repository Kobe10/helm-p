<%--
  Created by IntelliJ IDEA.
  User: shfb_wang
  Date: 2015/7/26 0026 17:05
  Copyright(c) 北京四海富博计算机服务有限公司
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<tr>
    <th width="10%"><label>指定处理人：</label></th>
    <td colspan="${tdNum}" style="text-align: left">
        <input type="hidden" name="taskId" value="${taskId}"/>

        <div style="position: relative;">
            <input type="hidden" name="toAssigneeId" class="js_pri js_assignee" value=""/>
            <input type="text" name="targetPerTemp" class="pull-left" width="100px" onfocus="$(this).next('a').click()"
                   placeholder="点击右侧按钮指定下一步处理人"/>
            <a title="选择" onclick="$.fn.sltStaff(this,{});" class="btnLook">选择</a>
        </div>
    </td>
</tr>
<tr>
    <th width="10%"><label>备注说明：</label></th>
    <td colspan="${tdNum}" style="text-align: left">
        <textarea rows="5" style="margin: 0px;width: 80%" name="taskComment"></textarea>
    </td>
</tr>
<oframe:script src="${pageContext.request.contextPath}/eland/ph/ph003/js/ph00301_task.js" type="text/javascript"/>