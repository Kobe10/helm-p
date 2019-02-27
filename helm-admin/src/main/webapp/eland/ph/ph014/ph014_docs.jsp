<%--
  Created by IntelliJ IDEA.
  User: shfb_wang
  Date: 2016/3/17 0017 16:09
  Copyright(c) 北京四海富博计算机服务有限公司
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div>
    <c:forEach items="${hsDocs}" var="item">
        <li class="album-li" style="text-align: center;" onclick="ph014.viewPic(${item.Row.docId})">
            <div class="album-context">
                <img class="show" style="max-width: 120px;" width="120px" height="130px"
                     title="${item.Row.docName}"
                     src="${pageContext.request.contextPath}/oframe/common/file/file-downview.gv?docId=${item.Row.docId}"/>
                <input type="hidden" name="ph014DocId" value="${item.Row.docId}"/>
            </div>
            <span class="spanText" title="${item.Row.docName}">${item.Row.docName}</span>
        </li>
    </c:forEach>
</div>