<%--
  Created by IntelliJ IDEA.
  User: Dennis
  Date: 2014/11/3 0003
  Time: 17:31
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="pageContent">
    <table class="table" width="100%">
        <tbody>
        <c:forEach items="${returnList}" var="item" varStatus="varStatus">
            <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}"/>
            <tr class="${tr_class}">
                    <%--暂时没做删除，隐藏多选框--%>
            <%--<td>--%>
                    <%--<input type="checkbox"--%>
                           <%--name="doc_id" value="${item.doc_id}" group="ids"/>--%>
                    <%--<input type="hidden"--%>
                           <%--name="prjCd" value="${item.prj_cd}"/>--%>
                <%--</td>--%>
                <td>${varStatus.index + 1}</td>
                <td class="noBreak">${item.doc_name}</td>
                <td><fmt:formatDate value="${item.pub_date}" type="both" pattern="yyyy-MM-dd"/></td>
                <td><oframe:name prjCd="${param.prjCd}" itemCd="PRJ_DOCUMENT_SOURCE" value="${item.doc_src}"/></td>
                <td><oframe:name prjCd="${param.prjCd}" itemCd="PRJ_DOCUMENT_TYPE" value="${item.doc_type}"/></td>
                <td class="noBreak">${item.pub_org}</td>
                <td>${item.doc_cd}</td>
                <td>${item.staff_name}</td>
                <td class="noBreak">${item.prj_org_name}</td>
                <td>
                    <a title="查看资料"
                       onclick="pj013.openView('${item.doc_id}')"
                       class="btnView">[查看]</a>
                    <oframe:op prjCd="${param.prjCd}" template="a" cssClass="btnEdit"
                               name="编辑" rhtCd="edit_prj_doc_rht"
                               onClick="pj013.openEdit('${item.doc_id}')"/>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <c:import url="/oframe/common/page/pager_fotter.jsp" charEncoding="UTF-8"></c:import>
</div>

