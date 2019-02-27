<%-- 右侧面板文件显示 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<div class="album-list">
    <input type="hidden" value="${upRegId}" id="upRegId" name="upRegId"/>
    <input type="hidden" value="reg01" name="goUpFlag"/>
    <ul class="album-ul">
        <c:forEach items="${folderList}" var="folder">
            <c:choose>
                <c:when test="${folder.Node.prjBuldId == '' || null==folder.Node.prjBuldId}">
                    <li class="album-li" style="text-align: center; position: relative">
                        <div ondblclick="ph011.docInfo(${folder.Node.regId})" class="album-context album-folder">
                        </div>
                        <span class="album-title"
                              style="text-align: center; height: auto; width: 120px;">
                            <label>${folder.Node.regName}</label>
                            <input onkeydown="if(event.keyCode == 13){ stopEvent(event);}"
                                   class="hidden" type="text"
                                   style="width: 120px;line-height: 19px;height: 19px;"
                                   value="${folder.Node.regId}">
                        </span>
                    </li>
                </c:when>
                <c:otherwise>
                    <li class="album-li" style="text-align: center; position: relative">
                        <div ondblclick="ph011.hsByBuildId(${folder.Node.regId},${folder.Node.prjBuldId})"
                             class="album-context album-folder">
                        </div>
                        <span class="album-title"
                              style="text-align: center; height: auto; width: 120px;">
                            <label>${folder.Node.regName}</label>
                            <input onkeydown="if(event.keyCode == 13){ stopEvent(event);}"
                                   class="hidden" type="text"
                                   style="width: 120px;line-height: 19px;height: 19px;"
                                   value="${folder.Node.regName}">
                        </span>
                    </li>
                </c:otherwise>
            </c:choose>
        </c:forEach>
    </ul>
</div>
