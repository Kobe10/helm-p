<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<div class="pageContent">
    <table class="table" width="100%">
        <tbody>
        <c:forEach items="${returnList}" var="item" varStatus="varStatus">
            <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}"/>
            <tr class="tr_class">
                <td>${varStatus.index + 1}</td>
                <td><oframe:staff staffCode="${item.staff_code}"/></td>
                <td>${item.from_ip}</td>
                <td>
                    <c:choose>
                        <c:when test="${item.rht_name == '' || item.rht_name == null}">
                            ${fn:substring(item.op_service, fn:indexOf(item.op_service, "-")+1, fn:length(item.op_service))}
                        </c:when>
                        <c:otherwise>
                            ${item.rht_name}
                        </c:otherwise>
                    </c:choose>
                </td>
                <td><oframe:date value="${item.op_start_time}" format="yyyy-MM-dd HH:mm:ss"/></td>
                <td>${item.op_use_time}</td>
                <td><oframe:name prjCd="${param.prjCd}" itemCd="COMMON_SUCCESS" value="${item.op_is_success}"/></td>
                <td><span class="btn" onclick="sys011.logDetail('${item.transaction_id}')">[操作详情]</span></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <c:import url="/oframe/common/page/pager_fotter.jsp" charEncoding="UTF-8"></c:import>
</div>
