<%--发起任务通用处理模板--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="pageContent" style="padding: 0">
    <table class="table" border="0" width="100%" layoutH="190">
        <tbody>
        <c:forEach items="${returnList}" var="item" varStatus="varStatus">
            <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}"/>
            <tr class="${tr_class} js_proc_info" procStUser="${item.Row.procStUser}">
                <td>
                    <c:choose>
                        <c:when test="${divId == 'wf003_page_published'}">
                            <input type="checkbox" group="ids" name="id" value="${item.Row.id}"/>
                        </c:when>
                        <c:otherwise>
                            <input type="checkbox" group="ids" name="modelId" value="${item.Row.modelId}"/>
                        </c:otherwise>
                    </c:choose>
                </td>
                <td>${item.Row.name}</td>
                <c:choose>
                    <c:when test="${item.Row.version != null}">
                        <td>${item.Row.key}:${item.Row.version}</td>
                    </c:when>
                    <c:otherwise>
                        <td>${item.Row.key}</td>
                    </c:otherwise>
                </c:choose>
                <td>${item.Row.category}</td>
                <td>${item.Row.description}</td>
                <c:choose>
                    <c:when test="${divId == 'wf003_page_published'}">
                        <td>
                            <%--<span class="link" name="start" onclick="wf003.startWf('${item.Row.key}');">[启动]</span>--%>
                            <%--<span class="link" name="detail" onclick="wf003.design('${item.Row.id}','1');">[设计]</span>--%>
                            <span class="link" name="exportLd" onclick="wf003.exportLd('${item.Row.id}','publish');">[导出]</span>
                                <%--<input type="hidden" name="procDefId" value="${item.Row.id}"/>--%>
                        </td>
                    </c:when>
                    <c:otherwise>
                        <td>
                            <span class="link" name="design" onclick="wf003.design('${item.Row.modelId}','0');">[设计]</span>
                            <%--<span class="link" name="exportLd" onclick="wf003.exportLd(${item.Row.modelId},'design');">[导出]</span>--%>
                            <span class="link" name="deploy" onclick="wf003.deploy(${item.Row.modelId});">[部署]</span>
                        </td>
                    </c:otherwise>
                </c:choose>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <c:import url="/oframe/common/page/pager_fotter.jsp" charEncoding="UTF-8"/>
</div>