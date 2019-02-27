<%--发起任务通用处理模板--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="pageNav">
    <a href="javascript:void(0)">流程部署</a>---><a class="current">已发布流程</a>
</div>

<div style="padding-left: 5px; padding-right: 5px;">
    <table class="border" border="0" width="100%">
        <tr>
            <th width="20%"><label>流程ID：</label></th>
            <td>${proc.ProcDefInfo.procDepId}</td>
        </tr>
        <tr>
            <th width="20%"><label>流程名称：</label></th>
            <td class="noBreak" style="overflow: hidden">${proc.ProcDefInfo.procDefName}</td>
        </tr>
        <tr>
            <th><label>流程定义Key：</label></th>
            <td>${proc.ProcDefInfo.procDefKey}</td>
        </tr>
        <tr>
            <th><label>流程版本：</label></th>
            <td>${proc.ProcDefInfo.revision}</td>
        </tr>
        <tr>
            <th><label>自定义Form：</label></th>
            <td>${proc.ProcDefInfo.stFormKey}</td>
        </tr>
        <tr>
            <th><label>流程部署时间：</label></th>
            <td>${proc.ProcDefInfo.procDepTime}</td>
        </tr>
    </table>
</div>
<oframe:script type="text/javascript" src="${pageContext.request.contextPath}/oframe/wf/wf002/js/wf002.js"/>


