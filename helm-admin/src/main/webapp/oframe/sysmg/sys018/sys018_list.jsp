<%--发起任务通用处理模板--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div id="sys018List">
    <table class="table" border="0" width="100%" layoutH="132">
        <thead>
        <tr>
            <th width="5%" fieldName="newHsId">
                <input type="checkbox" class="checkboxCtrl" group="ids"/>
            </th>
            <%@include file="/oframe/common/query/list_htr.jsp" %>
            <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${returnList}" var="item" varStatus="varStatus">
            <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}"/>
            <tr class="${tr_class}" jobTaskId="" style="cursor: pointer;">
                <td>
                    <input type="checkbox" group="ids" name="jobId" value="${item.Row.jobId}"/>
                    <input type="hidden" name="jobCd" value="${item.Row.jobCd}">
                    <input type="hidden" name="prjCd" value="${item.Row.prjCd}">
                </td>
                <%@include file="/oframe/common/query/list_hdt.jsp" %>
                <td>
                    <a title="修改" onclick="sys018.editView('${item.Row.jobId}','0',this);" class="btnEdit">[修改]</a>
                    <a title="复制" onclick="sys018.editView('${item.Row.jobId}','1',this);" class="btnEdit">[复制]</a>
                    <a title="删除"
                       onclick="sys018.deleteView('${item.Row.prjCd}','${item.Row.jobCd}','${item.Row.jobId}');"
                       class="btnEdit">[删除]</a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <jsp:include page="/oframe/common/page/page_fotter.jsp"/>
</div>
