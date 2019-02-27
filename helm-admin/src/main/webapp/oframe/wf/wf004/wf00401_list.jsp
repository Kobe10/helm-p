<%--发起任务通用处理模板--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="pageContent">
    <table class="table" border="0" width="100%">
        <tbody>
        <c:forEach items="${returnList}" var="item" varStatus="varStatus">
            <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}"/>
            <c:choose>
                <c:when test="${item.Row.endTime==null||item.Row.endTime==''}">
                    <tr class="${tr_class} js_task_info" assignee="${item.Row.assignee}">
                        <td>${varStatus.index + 1}</td>
                        <td>
                            <span class="link"
                                  onclick="wf004.queryViewWf('${item.Row.procInsId}');">
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
                        <td>${item.Row.procDefName}</td>
                        <td>${item.Row.taskName}</td>
                        <td>
                            <oframe:staff staffCode="${item.Row.procStUser}"/>
                        </td>
                        <td>
                            <oframe:staff staffCode="${item.Row.wp}"/>
                        </td>
                        <td>${item.Row.createTime}</td>
                        <td>${item.Row.duration}</td>
                        <td><span class="link" name="processingTask"
                                  onclick="index.viewWf('${item.Row.procInsId}')">[办理]</span>
                        <span class="link" onclick="wf004.delegationWfView('${item.Row.taskId}')">[委托]</span></td>
                    </tr>
                </c:when>
                <c:otherwise>
                    <tr class="${tr_class}">
                        <td>${varStatus.index + 1}</td>
                        <td>
                            <span class="link"
                                  onclick="wf004.queryViewWf('${item.Row.procInsId}');">
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
                        <td>${item.Row.procDefName}</td>
                        <td>${item.Row.taskName}</td>
                        <td>
                            <oframe:staff staffCode="${item.Row.procStUser}"/>
                        </td>
                        <td>
                            <oframe:staff staffCode="${item.Row.wp}"/>
                        </td>
                        <td>${item.Row.createTime}</td>
                        <td>${item.Row.endTime}</td>
                    </tr>
                </c:otherwise>
            </c:choose>
        </c:forEach>
        </tbody>
    </table>
    <c:import url="/oframe/common/page/pager_fotter.jsp" charEncoding="UTF-8"></c:import>
</div>
<script type="text/javascript">
    $(document).ready(function () {
        // 执行查询
        wf004.processingTaskShow();
    });
</script>