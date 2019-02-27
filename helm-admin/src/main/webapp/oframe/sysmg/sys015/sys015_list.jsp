<%--发起任务通用处理模板--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div id="jobTask">
    <table class="table" border="0" width="100%" layoutH="132">
        <thead>
        <tr>
            <th width="5%">序号</th>
            <%@include file="/oframe/common/query/list_htr.jsp" %>
            <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${returnList}" var="item" varStatus="varStatus">
            <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}"/>
            <tr class="${tr_class}" jobTaskId="" style="cursor: pointer;" onclick="sys015.queryLog(this,'${item.Row.jobTaskId}')">
                <td>${varStatus.index + 1}</td>
                <td>${item.Row.jobNameCh}</td>
                <td>${item.Row.jobDescription}</td>
                <td>
                    <c:if test="${item.Row.jobStatus=='1'}">已加载</c:if>
                    <c:if test="${item.Row.jobStatus=='0'}">未加载</c:if>
                </td>
                <td>${item.Row.jobType}</td>
                <td>${item.Row.jobExecTime}</td>
                <td>${item.Row.jobStartParam}</td>
                <td>
                    <span class="link" name="run" onclick="sys015.operationJob('${item.Row.jobTaskId}','1','')">立即执行</span>
                    <c:if test="${item.Row.jobStatus=='1'}">
                        <span class="link" name="revoke" onclick="sys015.operationJob('${item.Row.jobTaskId}','3','')">撤销作业</span>
                    </c:if>
                    <c:if test="${item.Row.jobStatus=='0'}">
                        <span class="link" name="start" onclick="sys015.operationJob('${item.Row.jobTaskId}','4','')">开启作业</span>
                        <span class="link" name="edit" onclick="sys015.editView('${item.Row.jobTaskId}')">修改作业</span>
                        <span class="link" name="delete" onclick="sys015.deleteJob('${item.Row.jobTaskId}')">删除作业</span>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <jsp:include page="/oframe/common/page/page_fotter.jsp"/>
</div>
