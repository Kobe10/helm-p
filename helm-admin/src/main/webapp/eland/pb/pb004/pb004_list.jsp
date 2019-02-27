<%--
  Created by IntelliJ IDEA.
  User: shfb_wang
  Date: 2015/5/13 15:51
  Copyright(c) 北京四海富博计算机服务有限公司
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div>
    <table class="table" layoutH="322" adjust="false" width="100%">
        <thead>
        <tr>
            <th width="3%">
                <input type="checkbox" class="checkAllInList mart10" checked="checked" group="ids"
                       onchange="pb004.checkAllBox(this)"/>
            </th>
            <%@include file="/oframe/common/query/list_htr.jsp" %>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${returnList}" var="item" varStatus="varStatus">
            <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}"/>
            <tr class="${tr_class}">
                <td>
                    <input type="checkbox" group="ids" name="buildId"
                           checked="true" onchange="pb004.sltSomeOne(this);"
                           value="${item.Row.BuildInfo.buildId}"/>
                    <input type="hidden" name="buildType" value="${item.Row.BuildInfo.buildType}"/>
                </td>
                <%@include file="/oframe/common/query/list_hdt.jsp" %>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <jsp:include page="/oframe/common/page/page_fotter.jsp"/>
</div>
<script>
    $(document).ready(function () {
        pb004.findUnCheck();
        pb004.sltSomeOne();
    });
</script>