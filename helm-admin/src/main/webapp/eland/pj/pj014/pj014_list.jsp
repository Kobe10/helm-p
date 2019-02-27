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
<div class="pageContent">
    <div id="result">
        <ul class="list-chx clear-fix">
            <c:forEach items="${returnList}" var="item" varStatus="varStatus">
                <li>
                    <div class="lh-wrap">
                        <div class="hx-img" onclick="pj014.openEdit('${item.ext_cmp_id}','${item.prj_ext_cmp_id}');"
                             style="background-image:url('/oframe/themes/images/160x175.gif')" title="公司Logo">
                            <c:choose>
                                <c:when test="${item.ext_cmp_pic != null && item.ext_cmp_pic != '1111'}">
                                    <img width="100%" height="100%" title="${item.ext_cmp_name}"
                                         src="${pageContext.request.contextPath}/oframe/common/file/file-download.gv?docId=${item.ext_cmp_pic}">
                                </c:when>
                            </c:choose>
                        </div>
                        <div class="p-name">
                            <span onclick="pj014.openEdit('${item.ext_cmp_id}','${item.prj_ext_cmp_id}');">
                                <a>${item.ext_cmp_name}</a>
                            </span>
                        </div>
                    </div>
                </li>
            </c:forEach>
        </ul>
    </div>
    <c:import url="/oframe/common/page/pager_fotter.jsp" charEncoding="UTF-8"></c:import>
</div>

