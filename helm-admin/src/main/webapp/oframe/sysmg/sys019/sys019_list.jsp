<%--发起任务通用处理模板--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div id="sys019List">
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
                    <input type="checkbox" group="ids" name="messageDefId" value="${item.Row.messageDefId}"/>
                    <input type="hidden" name="messageDefCd" value="${item.Row.messageDefCd}">
                    <input type="hidden" name="prjCd" value="${item.Row.prjCd}">
                </td>
                <%@include file="/oframe/common/query/list_hdt.jsp" %>
                <td>
                    <a title="发送消息" onclick="sys019.sendView('${item.Row.messageDefId}',this);" class="btnEdit">[发送]</a>
                    <a title="修改" onclick="sys019.editView('${item.Row.messageDefId}',this);" class="btnEdit">[修改]</a>
                    <a title="删除"
                       onclick="sys019.deleteView('${item.Row.messageDefId}');"
                       class="btnEdit">[删除]</a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <jsp:include page="/oframe/common/page/page_fotter.jsp"/>
</div>
