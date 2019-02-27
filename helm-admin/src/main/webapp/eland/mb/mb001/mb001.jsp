<%--个人待办--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<div style="background-color: #ffffff;min-height: 300px;">
    <table class="list" border="0" width="100%">
        <thead>
        <tr>
            <th width="50px;"><label>序号</label></th>
            <th><label>请求名称</label></th>
            <th><label>待办事项</label></th>
            <th><label>提交人员</label></th>
            <th><label>委托人员</label></th>
            <th><label>提交时间</label></th>
            <th><label>滞留时间</label></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${returnList}" var="item" varStatus="varStatus">
            <tr>
                <td>${varStatus.index + 1}</td>
                <td class="noBreak">
                    <span class="link"
                          onclick="index.viewWf('${item.Row.procInsId}');">
                        <c:choose>
                            <c:when test="${item.Row.procInsName == null||item.Row.procInsName == ''}">
                                ${item.Row.procDefName}
                            </c:when>
                            <c:otherwise>
                                ${item.Row.procInsName}
                            </c:otherwise>
                        </c:choose>
                    </span>
                </td>
                <td>${item.Row.taskName}</td>
                <td>
                    <oframe:staff staffCode="${item.Row.procStUser}"/>
                </td>
                <td>
                    <oframe:staff staffCode="${item.Row.wp}"/>
                </td>
                <td title="${item.Row.createTime}">${fn:substring(item.Row.createTime, 0, 10)}</td>
                <td>${item.Row.duration}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>