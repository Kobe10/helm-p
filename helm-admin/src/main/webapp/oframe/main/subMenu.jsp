<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<c:forEach items="${subMenu}" var="item" varStatus="varStatus">
    <li class="last-li">
        <a href="javascript:void(0)" id="top_${item.id}"
           onclick='index.openTab(this, ${item.id}, "${item.url}", "${item.cd}", "${item.name}")'
           onmouseover='index.queryMenuTree("${item.id}", "${item.url}")'>${item.name}</a>
        <ul></ul>
    </li>
</c:forEach>
