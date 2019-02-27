<%--发起任务通用处理模板--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div id="ph015List">
    <table class="table" border="0" width="100%">
        <thead>
        <tr>
            <th width="5%">
                序号
            </th>
            <th>原告人</th>
            <th>被告人</th>
            <th>开庭时间</th>
            <th>闭庭时间</th>
            <th>操作<a title="新增" name="addRecord"
                     onclick="ph015.editView(this,'');"
                     class="pad10 btnEdit">[新增]</a></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${returnList}" var="item" varStatus="varStatus">
            <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}"/>
            <tr class="${tr_class}" jobTaskId="" style="cursor: pointer;">
                <td>${varStatus.index + 1}
                    <input type="hidden" name="recordId" value="${item.Row.recordId}"/>
                </td>
                <%@include file="/oframe/common/query/list_hdt.jsp" %>
                <td>
                    <a title="编辑"
                       onclick="ph015.editView(this,'${item.Row.recordId}');"
                       class="btnEdit"><span name="editButton">[编辑]</span></a>
                    <a title="删除" name="deleteRecord"
                       onclick="ph015.deleteView(this,'${item.Row.recordId}');"
                       class="btnEdit">[删除]</a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <jsp:include page="/oframe/common/page/page_fotter.jsp"/>
</div>
