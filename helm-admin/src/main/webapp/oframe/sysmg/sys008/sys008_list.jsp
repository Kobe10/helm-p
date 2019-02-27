<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="pageContent">
    <table>
        <tbody>
        <c:forEach items="${returnList}" var="item" varStatus="varStatus">
            <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}"/>
            <tr class="${tr_class}">
                <c:choose>
                    <c:when test="${divId =='sys008_list_print'}">
                        <%--当前登录区--%>
                        <td>
                            <input type="checkbox" group="ids" name="id" value="${item.login_accept}"/>
                        </td>
                        <td>${item.login_accept}</td>
                        <td>${item.staff_code}</td>
                        <td>${item.staff_name}</td>
                        <td>${item.login_ip_addr}</td>
                        <td>${item.login_time}</td>
                        <td>
                            <a title="强制退出" onclick="sys008.forceOut('${item.login_accept}')" class="btnEdit">[强制退出]</a>
                        </td>
                    </c:when>
                    <c:otherwise>
                        <%--历史登录区--%>
                        <td>${item.login_accept}</td>
                        <td>${item.staff_code}</td>
                        <td>${item.staff_name}</td>
                        <td>${item.login_ip_addr}</td>
                        <td>${item.login_time}</td>
                        <td>${item.logout_time}</td>
                        <td><oframe:name prjCd="${param.prjCd}" value="${item.logout_type}"
                                         itemCd="LOGOUT_TYPE"/></td>
                    </c:otherwise>
                </c:choose>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <c:import url="/oframe/common/page/pager_fotter.jsp" charEncoding="UTF-8"/>
</div>

