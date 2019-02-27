<%--自定义任务，查看发起的任务--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<table width="100%" class="border">
    <tr>
        <th width="15%"><label>任务名称：</label></th>
        <td>${PROC_INST_INFO.ProcInsInfo.Variables.taskName}</td>
    </tr>
    <tr>
        <th><label>详情描述：</label></th>
        <td>${PROC_INST_INFO.ProcInsInfo.Variables.description}</td>
    </tr>
    <tr>
        <th><label>截至时间：</label></th>
        <td>${PROC_INST_INFO.ProcInsInfo.Variables.dueTime}</td>
    </tr>
</table>
<oframe:script type="text/javascript" src="${pageContext.request.contextPath}/eland/ph/ph006/js/ph006.js"/>