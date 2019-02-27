<%--付款计划--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<oframe:script src="${pageContext.request.contextPath}/eland/fm/fm001/js/fm001.js" type="text/javascript"/>
<style>
    ul.accordion_menu li.active {
        color: #ffffff;
        background-color: #0088cc;
    }
    ul.accordion_menu li.active:hover {
        color: #ffffff;
        background-color: #0088cc;
    }
</style>
<div class="mar5" style="vertical-align: top;">
    <%--数据查询区域--%>
    <div id="fm001LeftDiv" style="width: 260px;position: relative;" layoutH="15" class="panel left_menu left">
        <h1>
            <span class="panel_title">计划列表</span>
        </h1>
        <div class="accordion" extendAll="true">
            <c:forEach items="${schList}" var="item">
                <c:if test="${item.Row.schemeType == typeItem.key}">

                </c:if>
            </c:forEach>
            <ul class="accordion_menu">
                <c:forEach items="${planList}" var="item">
                    <li id="${item.Row.pId}"
                        style="padding: 0;white-space:nowrap; text-overflow:ellipsis; overflow: hidden;padding-left: 20px;"
                        onclick="fm001.info('${item.Row.pId}', this)">
                            ${item.Row.pCode}
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
    <div id="fm001Context" style="margin-left: 270px;margin-right: 5px;"></div>
</div>
<script>
    $(document).ready(function () {
        var li = $("ul.accordion_menu:first", navTab.getCurrentPanel()).find("li:first");
        var id = li.attr("id");
        fm001.info(id, li);
    });
</script>