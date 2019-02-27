<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<c:forEach items="${resultList}" var="item" varStatus="varStatus">
    <tr>
        <td>${varStatus.index + 1}</td>
        <td>${item.cacheTypeName} <input type="hidden" name="cacheTypeName" value="${item.cacheTypeName}"/>
        </td>
        <td>${item.cacheEngineType}</td>
        <td>${item.cacheTypeDesc}</td>
        <td><input type="text" class="textInput" name="cacheSubKey"/></td>
        <td><a title="清除缓存" onclick="sys009.clickRemove(this);" class="btnDel">清除缓存</a></td>
    </tr>
</c:forEach>