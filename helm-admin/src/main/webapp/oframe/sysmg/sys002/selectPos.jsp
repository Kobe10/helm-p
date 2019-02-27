<%@ page contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<ul class="js_positionInfo">
    <c:forEach items="${posMapList}" var="pos">
        <li><input type="checkbox" name="posId" onclick="sys00202.clickCheckItem(this)" value="${pos.Position.posId}"/>
            <input type="hidden" name="posName" value="${pos.Position.posName}"/>
                ${pos.Position.posName}</li>
    </c:forEach>
</ul>
<script>
    $(document).ready(function () {
        var rightPosIds = $("input[name=posId]", $("#selectedPos", $.pdialog.getCurrent));
        var allPosIds = $("input[name=posId]", $("ul.js_positionInfo", $.pdialog.getCurrent));
        for (var i = 0; i < allPosIds.length; i++) {
            var allPosId = $(allPosIds[i]).val();
            for (var j = 0; j < rightPosIds.length; j++) {
                var rightPosId = $(rightPosIds[j]).val();
                if (allPosId == rightPosId) {
                    $(allPosIds[i]).attr("checked","checked");
                }
            }
        }
    });
</script>
<oframe:script src="${pageContext.request.contextPath}/oframe/sysmg/sys002/js/sys00202.js" type="text/javascript"/>