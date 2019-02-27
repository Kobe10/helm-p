<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="pageContent">
    <table>
        <tbody>
        <c:forEach items="${returnList}" var="item" varStatus="varStatus">
            <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}"/>
            <tr class="${tr_class}">
                <td><input type="checkbox" group="ids" name="staffId" value="${item.staff_id}"/></td>
                <td>${item.staff_code}</td>
                <td>${item.staff_name}</td>
                <td>${item.staff_tel}</td>
                <td>${item.staff_email}</td>
                <td style="text-align: left;"
                    title="<oframe:org prjCd="${param.prjCd}" orgId="${item.org_id}" property="Org.Node.nodeFullPathName"/>"
                    class="noBreak"><oframe:org prjCd="${param.prjCd}" orgId="${item.org_id}"
                                                property="Org.Node.nodeFullPathName"/>
                </td>
            </tr>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <c:import url="/oframe/common/page/pager_fotter.jsp" charEncoding="UTF-8"></c:import>
</div>

