<%--建筑内房产信息列表--%>
<%--人地房房产信息检索--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<oframe:script src="${pageContext.request.contextPath}/eland/ph/ph003/js/ph003_op.js" type="text/javascript"/>
<div class="pageContent">
    <table class="table" width="100%">
        <tbody>
        <c:forEach items="${returnList}" var="item" varStatus="varStatus">
            <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}"/>
            <tr class="${tr_class}">
                <td>${varStatus.index + 1}</td>
                <td class="noBreak" title="${item.hs_cd}">${item.hs_cd}</td>
                <td class="noBreak" title="${item.hs_owner_persons}">${item.hs_owner_persons}</td>
                <td><oframe:name prjCd="${param.prjCd}" itemCd="HS_OWNER_TYPE" value="${item.hs_owner_type}"/></td>
                <td class="noBreak" title="${item.hs_build_size}">${item.hs_build_size}</td>
                <td class="noBreak"><oframe:name prjCd="${param.prjCd}" itemCd="OLD_HS_STATUS" value="${item.hs_status}"/></td>
                <td>
                    <button type="button" onclick="ph00203.openHs('${item.hs_id}')"
                            class="btn btn-more">
                        <i style="font-style:normal;">详情</i>
                        <span class="caret"></span>
                        <ul style="width: 600px;" class="menu hidden">
                            <jsp:include page="/eland/ph/ph003/ph003_op.jsp">
                                <jsp:param name="hsId" value="${item.hs_id}"/>
                                <jsp:param name="hsOwnerType" value="${item.hs_owner_type}"/>
                            </jsp:include>
                            <span style="clear: both"></span>
                        </ul>
                    </button>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <jsp:include page="/oframe/common/page/pager_fotter.jsp">
        <jsp:param name="show" value="false"/>
    </jsp:include>
</div>
