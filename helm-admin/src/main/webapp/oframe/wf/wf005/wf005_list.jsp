<%--发起任务通用处理模板--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div id="wf005List">
    <table class="table" border="0" width="100%" layoutH="165">
        <thead>
        <tr>
            <c:choose>
                <c:when test="${divId == 'wf005_page_data'}">
                    <th width="5%" fieldName="newHsId">
                        <input type="checkbox" class="checkboxCtrl" group="ids"/>
                    </th>
                    <%@include file="/oframe/common/query/list_htr.jsp" %>
                    <th width="12%">操作</th>
                </c:when>
                <c:otherwise>
                    <th width="5%" fieldName="newHsId">
                        <input type="checkbox" class="checkboxCtrl" group="ids"/>
                    </th>
                    <%@include file="/oframe/common/query/list_htr.jsp" %>
                    <th width="12%">操作</th>
                </c:otherwise>
            </c:choose>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${returnList}" var="item" varStatus="varStatus">
            <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}"/>
            <tr class="${tr_class}" jobTaskId="" style="cursor: pointer;">
                <c:choose>
                    <c:when test="${divId == 'wf005_page_data'}">
                        <td>
                            <input type="checkbox" group="ids" name="formId" value="${item.Row.formId}"/>
                            <input type="hidden" name="formCd" value="${item.Row.formCd}">
                            <input type="hidden" name="prjCd" value="${item.Row.prjCd}">
                        </td>
                        <%@include file="/oframe/common/query/list_hdt.jsp" %>
                        <td>
                            <a title="详情" onclick="wf005.designForm('detail','${item.Row.formId}');" class="btnEdit">[详情]</a>
                            <a title="卸载" onclick="wf005.cancelPublish('${item.Row.formId}');" class="btnEdit">[卸载]</a>
                        </td>
                    </c:when>
                    <c:otherwise>
                        <td><input type="checkbox" group="ids" name="formId" value="${item.Row.formId}"/></td>
                        <%@include file="/oframe/common/query/list_hdt.jsp" %>
                        <td>
                            <a title="发布" onclick="wf005.publishDialog('${item.Row.formId}', '');" class="btnEdit">[发布]</a>
                            <a title="设计" onclick="wf005.designForm('design','${item.Row.formId}');" class="btnEdit">[设计]</a>
                        </td>
                    </c:otherwise>
                </c:choose>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <jsp:include page="/oframe/common/page/page_fotter.jsp"/>
</div>
