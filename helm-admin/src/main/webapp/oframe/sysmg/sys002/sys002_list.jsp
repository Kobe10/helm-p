<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="pageContent">
    <table>
        <tbody>
        <c:forEach items="${returnList}" var="item" varStatus="varStatus">
            <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}"/>
            <tr class="${tr_class}">
                <td>${item.staff_code}</td>
                <td>${item.staff_name}</td>
                <td>${item.staff_tel}</td>
                <td><oframe:name prjCd="${param.prjCd}" itemCd="COM_STATUS_CD" value="${item.status_cd}"/></td>
                <td>${item.status_date}</td>
                <td>
                    <a title="查看" onclick="sys002.openView('${item.staff_id}')" class="btnView">[查看]</a>
                    <oframe:op prjCd="${param.prjCd}" template="a" cssClass="btnEdit"
                               name="修改" rhtCd="EDIT_SYS_USE_RHT"
                               onClick="sys002.openEdit('${item.staff_id}')"/>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <c:import url="/oframe/common/page/pager_fotter.jsp" charEncoding="UTF-8"/>
</div>

