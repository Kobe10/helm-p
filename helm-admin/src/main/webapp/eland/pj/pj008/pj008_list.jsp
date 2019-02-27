<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div class="pageContent">
    <div id="result">
        <ul class="list-hx clear-fix">
            <c:forEach items="${returnList}" var="item" varStatus="varStatus">
                <li>
                    <div class="lh-wrap">
                        <div class="hx-img" onclick="pj008.openEdit('${item.hx_id}');">
                            <c:choose>
                                <c:when test="${item.hx_img_path != null && item.hx_img_path != ''}">
                                    <img width="100%" height="100%"
                                         src="${pageContext.request.contextPath}/oframe/common/file/file-downview.gv?docId=${item.hx_img_path}">
                                </c:when>
                            </c:choose>
                        </div>
                        <div class="p-name">
                            <span onclick="pj008.openEdit('${item.hx_id}');">
                                <a>
                                ${item.reg_addr}:${item.hx_cd}:<oframe:name prjCd="${param.prjCd}" itemCd="HS_ROOM_TYPE" value="${item.hx_name}"/>
                                </a>
                            </span>
                        </div>
                        <div class="p-addr">
                            <span onclick="pj008.openEdit('${item.hx_id}');">
                                <a>建筑面积：${item.hx_bld_size}, <br/>使用面积：${item.hx_use_size}</a>
                            </span>
                        </div>
                    </div>
                </li>
            </c:forEach>
        </ul>
    </div>
    <c:import url="/oframe/common/page/pager_fotter.jsp" charEncoding="UTF-8"></c:import>
</div>
