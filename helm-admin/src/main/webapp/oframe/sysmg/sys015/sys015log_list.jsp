<%--发起任务通用处理模板--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div style="margin: 0px 0px 0px 10px">
    <table class="table" border="0" width="100%" style="margin: 0px 0px 0px 10px">
        <thead>
        <tr>
            <td>执行人</td>
            <td>执行开始时间</td>
            <td>执行结束时间</td>
            <td>操作描述</td>
            <td>操作</td>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${resultList}" var="item" varStatus="varStatus">
            <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}"/>
            <tr class="" jobTaskId="">
                <td><oframe:staff staffId="${item.Row.jobExecBy}"/></td>
                <td>
                    <oframe:date value="${item.Row.jobStartTime}" format="yyyy-MM-dd HH:mm:ss"/>
                </td>
                <td>
                    <oframe:date value="${item.Row.jobEndTime}" format="yyyy-MM-dd HH:mm:ss"/>
                </td>
                <td>${item.Row.jobNote}</td>
                <td>
                    <c:if test="${item.Row.jobStatus=='1'}">
                        <span class="link" name="end" onclick="sys015.operationJob('${item.Row.jobTaskId}','2','${item.Row.threadId}')">结束</span>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
