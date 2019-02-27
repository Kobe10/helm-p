<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div>
    <input type="hidden" name="docIdPath"
           value='<oframe:tree treeName="DocumentInfo" primaryValue="${param.upDocId}" property="treePath" pathValue="docId"/>'>
    <input type="hidden" name="docNamePath"
           value='<oframe:tree treeName="DocumentInfo" primaryValue="${param.upDocId}" property="treePath" pathValue="docName"/>'>
    <table class="table" layoutH="132" adjust="true" width="100%">
        <thead>
        <tr>
            <%@include file="/oframe/common/query/list_htr.jsp" %>
            <th>资料路径</th>
            <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${returnList}" var="item" varStatus="varStatus">
            <c:if test="${item.Row.docFlag == '0'}">
                <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}"/>
                <tr class="${tr_class}">
                    <%@include file="/oframe/common/query/list_hdt.jsp" %>
                    <td title="${item.Row.path}">${item.Row.path}</td>
                    <td>
                    <span onclick="pj015.editFileById('${item.Row.docId}', '${item.Row.docFlag}')"
                          class="link marr5">修改</span>
                        <%--<oframe:power prjCd="${param.prjCd}" rhtCd="edit_prj_doc_rht">--%>
                        <%--<span onclick="pj015.deleteFileById('${item.Row.docId}', '${item.Row.docFlag}')"--%>
                              <%--class="link marr5">删除</span>--%>
                        <%--</oframe:power>--%>
                        <oframe:op prjCd="${param.prjCd}" template="span" cssClass="link marr5" name="删除"
                                   rhtCd="edit_prj_doc_rht" onClick="pj015.deleteFileById('${item.Row.docId}', '${item.Row.docFlag}')"/>
                    </td>
                </tr>
            </c:if>
        </c:forEach>
        </tbody>
    </table>
    <jsp:include page="/oframe/common/page/page_fotter.jsp"/>
</div>

