<%--付款计划--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<oframe:script src="${pageContext.request.contextPath}/eland/fm/fm002/js/fm002.js" type="text/javascript"/>
<div class="mar5" style="vertical-align: top;">
    <%--数据查询区域--%>
    <div id="fm002LeftDiv" style="width: 260px;position: relative;" layoutH="15" class="panel left_menu left">
        <h1>
            <span class="panel_title">计划列表</span>
            <span class="menu marr10 right cursorpt" onclick="fm002.info('')">新建</span>
        </h1>

        <div class="accordion" extendAll="true">
            <ul class="accordion_menu">
                <c:forEach items="${planList}" var="item">
                    <li id="${item.Row.pId}"
                        style="padding: 0;white-space:nowrap; text-overflow:ellipsis; overflow: hidden;"
                        onclick="fm002.info('${item.Row.pId}', this)">
                                <span style="padding: 0 5px;color: red;font-weight: 900" class="left">
                                <c:if test="${item.Row.pStatus == '1'}">√</c:if>
                                <c:if test="${item.Row.pStatus == '0'}">&nbsp;&nbsp;</c:if>
                                </span>${item.Row.pCode}
                    </li>
                </c:forEach>
            </ul>
        </div>
    </div>
    <div class="split_line" layoutH="15">
        <span class="split_but" title="展开/隐藏导航"></span>
    </div>

    <input type="hidden" class="js_query_model" value="1"/>
    <%--签约区域--%>
    <div id="fm002Context" style="margin-left: 270px;margin-right: 5px;"></div>
</div>
<script>
    $(document).ready(function () {
        <c:choose>
        <c:when test="${pId != '' && pId != null}">
        fm002.info('${pId}', $("#" +${pId}, navTab.getCurrentPanel()));
        </c:when>
        <c:otherwise>
        var li = $("ul.accordion_menu:first", navTab.getCurrentPanel()).find("li:first");
        var id = li.attr("id");
        fm002.info(id, li);
        </c:otherwise>
        </c:choose>
    });
</script>