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
                <td>
                    <input type="checkbox"
                           name="qId" value="${item.q_id}" group="ids"/>
                    <input type="hidden"
                           name="prjCd" value="${item.prj_cd}"/>
                </td>
                <td>${varStatus.index + 1}</td>
                <td>${item.q_reg}</td>
                <td class="noBreak">${item.q_text}</td>
                <td>${item.q_name}</td>
                <td>${item.q_tele}</td>
                <td><oframe:name prjCd="${param.prjCd}" itemCd="QUES_STATUS" value="${item.q_status}"/></td>
                <td><fmt:formatDate value="${item.q_time}" type="both" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                <td>
                    <a title="查看"
                       onclick="pj012.openView('${item.q_id}')"
                       class="btnView">[查看]</a>
                    <oframe:op prjCd="${param.prjCd}" template="a" cssClass="btnEdit"
                               name="编辑" rhtCd="edit_prj_ques_rht"
                               onClick="pj012.openEdit('${item.q_id}')"/>
                </td>
            </tr>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <c:import url="/oframe/common/page/pager_fotter.jsp" charEncoding="UTF-8"></c:import>
</div>
