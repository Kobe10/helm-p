<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div>
    <div class="tree-bar" style="position: inherit;line-height: inherit;">
        <span class="mart5" onclick="sys012.closeQuickRslt();"
              style="display: block; float: right; margin:0px 10px 5px 0px; cursor: pointer;">关闭</span>
    </div>
    <input type="hidden" name="pageSize" value="${param.pageSize}">
    <table class="table" layoutH="185" width="100%">
        <c:choose>
            <c:when test="${fn:length(returnList) == 0}">
                <tr>
                    <td colspan="3">未查询到数据！</td>
                </tr>
            </c:when>
            <c:otherwise>
                <thead>
                <tr>
                    <%@include file="/oframe/common/query/list_htr.jsp" %>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${returnList}" var="item" varStatus="varStatus">
                    <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}"/>
                    <tr class="${tr_class}"
                        onclick="sys012.quickRsltClick('${item.Row.entityName}','${item.Row.entityGroupId}', '${item.Row.entityAttrNameEn}', '${item.Row.prjCd}')"
                        style="cursor: pointer;"
                        id="tableTr">
                        <%@include file="/oframe/common/query/list_hdt.jsp" %>
                    </tr>
                </c:forEach>
                </tbody>
            </c:otherwise>
        </c:choose>
    </table>
    <jsp:include page="/oframe/common/page/pager_fotter_simple.jsp"/>
</div>