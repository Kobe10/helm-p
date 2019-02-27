<%--
  Created by IntelliJ IDEA.
  User: shfb_wang
  Date: 2016/3/17 0017 16:09
  Copyright(c) 北京四海富博计算机服务有限公司
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="album-list">
    <ul class="album-ul">
        <c:forEach items="${attachTypeSummary}" var="item">
            <li class="album-li" onclick="form.viewPic('${item.ids}')" style="text-align: center;">
                <div class="album-context"
                     style="background: url(${pageContext.request.contextPath}/oframe/themes/images/fileicon.png);">
                    <input type="hidden" class="js_docIds" value="${item.ids}"/>
                </div>
                <span class="album-title"
                      style="text-align: center; height: auto; width: 120px;">${item.name}(${item.count})</span>
            </li>
        </c:forEach>
    </ul>
</div>