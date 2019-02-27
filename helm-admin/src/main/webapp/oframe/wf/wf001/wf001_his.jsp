<%--发起任务通用处理模板--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%--历史任务节点--%>
<c:forEach items="${hisTaskInfos}" var="hisTask" varStatus="varStatus">
    <div class="panelContainer mar5" style="display: block;margin: 0;">
        <div class="panel" id="wf_${hisTask.HiTaskInfo.taskName}">
            <h1>
                [<oframe:staff staffCode="${hisTask.HiTaskInfo.assignee}"/>]
                [${hisTask.HiTaskInfo.endTimeDisp}] 处理任务
                [${hisTask.HiTaskInfo.taskName}]
                任务滞留时间：${hisTask.HiTaskInfo.delayDays}(天)}
            </h1>
            <c:set var="viewPage" value="${hisTask.HiTaskInfo.viewPage}${''}"/>
            <c:if test="${viewPage != ''}">
                <c:choose>
                    <c:when test="${fn:indexOf(viewPage, '?') == -1}">
                        <c:set var="viewPage" value="${viewPage}?H_I=${varStatus.index}"/>
                    </c:when>
                    <c:otherwise>
                        <c:set var="viewPage" value="${viewPage}&H_I=${varStatus.index}"/>
                    </c:otherwise>
                </c:choose>
                <jsp:include page="${viewPage}"/>
            </c:if>
            <c:if test="${hisTask.HiTaskInfo.hisPage != '' && hisTask.HiTaskInfo.hisPage != null}">
                ${hisTask.HiTaskInfo.hisPage}
            </c:if>
        </div>
    </div>
</c:forEach>
