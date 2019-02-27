<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div>
    <div class="tree-bar" style="position: inherit;line-height: inherit;">
        <span class="mart5" onclick="sys017.closeQuickRslt();"
              style="display: block; float: right; margin:0px 10px 5px 0px; cursor: pointer;">关闭</span>
    </div>
    <input type="hidden" name="pageSize" value="${param.pageSize}">
    <table class="table" layoutH="185" width="100%">
        <thead>
        <tr>
            <%@include file="/oframe/common/query/list_htr.jsp" %>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${returnList}" var="item" varStatus="varStatus">
            <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}"/>
            <tr class="${tr_class}"
                onclick="sys017.quickRsltClick('${item.Row.RuleInfo.ruleCd}', '${item.Row.RuleInfo.ruleTypeId}','${item.Row.RuleInfo.prjCd}','${item.Row.RuleInfo.ruleId}')"
                style="cursor: pointer;"
                title="调整时间：${item.Row.RuleInfo.ruleStatusTime}"
                id="tableTr">
                <%@include file="/oframe/common/query/list_hdt.jsp" %>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <jsp:include page="/oframe/common/page/pager_fotter_simple.jsp"/>
</div>