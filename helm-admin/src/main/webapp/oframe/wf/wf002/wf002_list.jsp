<%--发起任务通用处理模板--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="mart5">
    <div class="pageNav">
        <a href="javascript:void(0)">流程列表</a>---><a class="current">已发布流程</a>
    </div>
    <div style="padding-left: 5px; padding-right: 5px;">
        <table class="list" border="0" width="100%">
            <thead>
            <tr>
                <th><label>序号</label></th>
                <th><label>流程名称</label></th>
                <th><label>流程分类</label></th>
                <th><label>流程定义Key</label></th>
                <th><label>流程部署时间</label></th>
                <th><label>操作</label></th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${procList}" var="item" varStatus="varStatus">
                <tr>
                    <td>${varStatus.index + 1}</td>
                    <td class="noBreak" style="overflow: hidden">${item.ProcDefInfo.procDefName}</td>
                    <td>${item.ProcDefInfo.procDepCategory}</td>
                    <td>${item.ProcDefInfo.procDefKey}</td>
                    <td>${item.ProcDefInfo.procDepTime}</td>
                    <td>
                        <span class="btn" style="color: #0000ff;"
                              onclick="wf002.findProcDetail('${item.ProcDefInfo.procDefKey}');">详情</span>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>
<oframe:script type="text/javascript" src="${pageContext.request.contextPath}/oframe/wf/wf002/js/wf002.js"/>


