<%-- 右侧面板文件显示 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<div class="album-list">
    <input type="hidden" name="relId" value="${hsId}" id="hsId">
    <input type="hidden" value="reg03" name="goUpFlag"/>
    <ul class="album-ul">
        <c:forEach items="${docTN}" var="attachment">
            <li class="album-li" style="text-align: center; position: relative">
                <div class="album-context album-folder"
                     ondblclick="ph011.selAttachment('${relId}','100','${attachment}')">
                </div>
                <span class="album-title" style="text-align: center; height: auto; width: 120px;">
                    <label>${attachment}</label>
                </span>
            </li>
        </c:forEach>
    </ul>
</div>