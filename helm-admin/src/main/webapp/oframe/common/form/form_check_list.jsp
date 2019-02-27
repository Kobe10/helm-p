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
<div>
    <table class="table" layoutH="${param.tableLayOut}" width="100%">
        <thead>
        <tr>
            <%--checkStaff,checkTime,checkResult,checkNote,modifyTransactionId--%>
            <th class="sortable asc" width="15%"
                onclick="Page.sort('${divId}', this);"
                fieldName="checkStaff">操作工号
            </th>
            <th class="sortable" width="15%"
                onclick="Page.sort('${divId}', this);"
                fieldName="checkTime">操作时间
            </th>
            <th width="15%" fieldName="checkResult">操作事项
            </th>
            <th fieldName="checkNote">操作备注</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${returnList}" var="item" varStatus="varStatus">
            <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}"/>
            <c:set var="modifyTransactionId" value="${item.Row.modifyTransactionId}${''}"/>
            <tr class="${tr_class}">
                <td class="noBreak"><oframe:staff staffId="${item.Row.checkStaff}"/></td>
                <td class="noBreak"><oframe:date value="${item.Row.checkTime}" format="yy-MM-dd HH:mm"/></td>
                <td class="noBreak">
                    <c:set var="temp" value="${item.Row.checkResult_Name}${''}"/>
                        ${item.Row.checkOpName}
                    <c:if test="${temp != ''}">(${temp})</c:if>
                </td>
                <td style="text-align: left;">${item.Row.checkNote}
                    <c:if test="${modifyTransactionId != ''}">
                        <span class="link" onclick="form_check_list.logDetail('${modifyTransactionId}');">[修改详情]</span>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <c:choose>
        <c:when test="${param.simplePageFlag}">
            <jsp:include page="/oframe/common/page/pager_fotter_simple.jsp"/>
        </c:when>
        <c:otherwise>
            <jsp:include page="/oframe/common/page/page_fotter.jsp"/>
        </c:otherwise>
    </c:choose>
</div>
<script type="text/javascript">
    var form_check_list = {
        /**
         * 查询日志详情
         * @param transactionId
         */
        logDetail: function (transactionId) {
            var url = getGlobalPathRoot() + "oframe/sysmg/sys011/sys011-logDetail.gv?prjCd=" + getPrjCd() + "&transactionId=" + transactionId;
            $.pdialog.open(url, "sys011-logDetail", "操作详情", {height: 600, width: 800, mask: true});
        }
    }

</script>