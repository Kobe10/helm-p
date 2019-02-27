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
                           name="noticeId" value="${item.notice_id}" group="ids"/>
                    <input type="hidden"
                           name="prjCd" value="${item.prj_cd}"/>
                </td>
                <td>${varStatus.index + 1}</td>
                <td><oframe:name prjCd="${param.prjCd}" itemCd="NOTICE_TYPE" value="${item.notice_type}"/></td>
                <td class="noBreak">${item.notice_title}</td>
                <td><oframe:name prjCd="${param.prjCd}" itemCd="NOTICE_STATUS" value="${item.public_status}"/></td>
                <td><fmt:formatDate value="${item.notice_date}" type="both" pattern="yyyy-MM-dd"/></td>
                <td><fmt:formatDate value="${item.publish_date}" type="both" pattern="yyyy-MM-dd"/></td>
                <td>
                    <a title="查看"
                       onclick="pj011.openView('${item.notice_id}')"
                       class="btnView">[查看]</a>
                    <oframe:op prjCd="${param.prjCd}" template="a" cssClass="btnEdit"
                               name="编辑" rhtCd="edit_prj_notice_rht"
                               onClick="pj011.openEdit('${item.notice_id}')"/>
                </td>
            </tr>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <c:import url="/oframe/common/page/pager_fotter.jsp" charEncoding="UTF-8"></c:import>
</div>
