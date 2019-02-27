<%-- 右侧面板文件显示 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<div class="album-list" style="border-bottom: solid #000000 1px ;min-height: 45%;padding-bottom:2cm" id="folderListDiv">
    <input type="hidden" value="${upRegId}" id="upRegId" name="upRegId"/>
    <input type="hidden" name="relId" value="${cBuildId}" id="cBuildId">
    <input type="hidden" value="reg02" name="goUpFlag"/>
    <ul class="album-ul">
        <c:forEach items="${folderList}" var="folder">
            <li class="album-li" style="text-align: center; position: relative">
                <div ondblclick="ph011.attByHouseId(${folder.hsId})" class="album-context album-folder">
                </div>
                <span class="album-title"
                      style="text-align: center; height: auto; width: 120px;">
                    <label>${folder.hsOwnerPersons}</label>
                    <input
                            onkeydown="if(event.keyCode == 13){ stopEvent(event);}"
                            class="hidden" type="text"
                            style="width: 120px;line-height: 19px;height: 19px;"
                            value="${folder.hsOwnerPersons}">
                </span>
            </li>
        </c:forEach>
    </ul>
</div>
<div class="album-list">
    <ul class="album-ul">
        <c:forEach items="${docTN}" var="attachment">
            <li class="album-li" style="text-align: center; position: relative">
                <div class="album-context album-folder"
                     ondblclick="ph011.selAttachment('${cBuildId}','200','${attachment}')">
                </div>
                <span class="album-title"
                      style="text-align: center; height: auto; width: 120px;">
                    <label>${attachment}</label>
                    <input type="hidden" id="attachment" value="${attachment}" name="attachment"/>
                </span>
            </li>
        </c:forEach>
    </ul>
</div>

