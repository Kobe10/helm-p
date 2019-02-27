<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="pageContent">
    <%--房源公告--%>
    <div id="result">
        <ul class="list-chx clear-fix">
            <c:forEach items="${returnList}" var="item" varStatus="varStatus">
                <li>
                    <div class="lh-wrap">
                        <div class="hx-img" onclick="pj011.openEdit('${item.notice_id}')" title="公告封面">
                            <c:choose>
                                <c:when test="${item.cover_pic_id != null && item.cover_pic_id != '1111'}">
                                    <img width="100%" height="100%" title="${item.notice_title}"
                                         src="${pageContext.request.contextPath}/oframe/common/file/file-download.gv?docId=${item.cover_pic_id}">
                                </c:when>
                            </c:choose>
                        </div>
                        <div class="p-name">
                            <input type="checkbox"
                                   name="noticeId" value="${item.notice_id}" group="ids"/>
                            <input type="hidden"
                                   name="prjCd" value="${item.prj_cd}"/>
                            <span onclick="pj011.openEdit('${item.notice_id}')">
                                <a>${item.notice_title}</a>
                            </span>
                        </div>
                    </div>
                </li>
            </c:forEach>
        </ul>
    </div>
    <c:import url="/oframe/common/page/pager_fotter.jsp" charEncoding="UTF-8"></c:import>
</div>
