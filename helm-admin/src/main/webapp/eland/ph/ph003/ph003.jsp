<%--院落信息展示页面--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="pageNav" style="display: block;">
    <a class="link" href="javascript:ph003.jumpRegInfo('${upRegId}')">${hsAddr}</a>--->
    <a class="link" href="javascript:ph003.jumpRegInfo('${regId}')">${hsAddrNo}</a>--->
    <a class="current js_more" offsetX="0" offsetY="-28" openDirection="down" style="position: relative;">
        ${hsOwnerPersons}
        <c:if test="${fn:length(hsList) > 1}">
            <span class="caret"></span>
            <ul class="menu hidden" style="width:200px;padding-right: 2px;padding-bottom: 1px;">
                <c:forEach items="${hsList}" var="item">
                    <c:choose>
                        <c:when test="${item.Row.hsId == hsId}">
                            <li style="width: 100%" onclick="ph003.openHs('${item.Row.hsId}')"
                                class="active">${item.Row.hsOwnerPersons}</li>
                        </c:when>
                        <c:otherwise>
                            <li style="width: 100%"
                                onclick="ph003.openHs('${item.Row.hsId}')">${item.Row.hsOwnerPersons}</li>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
            </ul>
        </c:if>
    </a>
<%--
    <span onclick="$.printBox('ph003MainContainer')" class="link marr20 right">[打印本页]</span>
--%>
    <oframe:op prjCd="${param.prjCd}" template="span" cssClass="link marr20 right"
               name="修改居民信息" rhtCd="edit_old_hs_rht"
               onClick="ph003.editHouse('${hsId}');"/>
</div>

<div id="ph003MainContainer" class="mart5 marb10">
    <%--待办任务--%>
    <div style="width: 49%;"
         id="m2"
         url="eland/ph/ph003/ph003-initTD.gv?hsId=${hsId}"
         panelKey="js_panel_ph003_2"
         class="panelContainer">
        <div class="panel">
            <h1>待办任务 <span class="panel_menu js_reload">刷新</span></h1>
            <%--面板内容--%>
            <div style="min-height: 300px;"></div>
        </div>
    </div>

    <%--房屋坐落--%>
    <div id="m3" style="width: 49%;"
         url="eland/ph/ph003/ph003-initPos.gv?hsId=${hsId}&regId=${regId}"
         panelKey="js_panel_ph003_3"
         class="panelContainer">
        <div class="panel">
            <h1>房屋坐落 <span class="panel_menu js_reload">刷新</span></h1>
            <%--面板内容--%>
            <div style="min-height: 300px;"></div>
        </div>
    </div>

    <%--入户调查表--%>
    <div id="m1" style="width: 99%;"
         url="eland/ph/ph003/ph003-initBase.gv?hsId=${hsId}&regId=${regId}"
         panelKey="js_panel_ph003_1"
         class="panelContainer">
        <div class="panel">
            <h1>入户调查表 <span class="panel_menu js_reload">刷新</span></h1>
            <%--面板内容--%>
            <div></div>
        </div>
    </div>

    <%--房产评估--%>
    <c:if test="${showPg}">
        <div id="m06" style="width: 99%"
             url="eland/ph/ph003/ph003-initPG.gv?hsId=${hsId}"
             panelKey="js_panel_ph003_06"
             class="panelContainer">
            <div class="panel">
                <h1>房产评估 <span class="panel_menu js_reload">刷新</span></h1>
                    <%--面板内容--%>
                <div></div>
            </div>
        </div>
    </c:if>

    <%--签约信息--%>
    <div id="m05" style="width: 99%"
         url="eland/ph/ph003/ph003-initTt.gv?hsId=${hsId}"
         panelKey="js_panel_ph003_05"
         class="panelContainer">
        <div class="panel">
            <h1>签约信息 <span class="panel_menu js_reload">刷新</span></h1>
            <%--面板内容--%>
            <div></div>
        </div>
    </div>

    <%--附件信息--%>
    <div style="width: 49%;"
         id="m15"
         url="eland/ph/ph003/ph003-initFj.gv?hsId=${hsId}"
         panelKey="js_panel_ph003_15"
         class="panelContainer">
        <div class="panel">
            <h1>附件<span class="panel_menu js_reload">刷新</span></h1>
            <%--面板内容--%>
            <div style="min-height: 400px;"></div>
        </div>
    </div>

    <%--居民工作日志--%>
    <div style="width: 49%;overflow: auto"
         id="m09"
         url="eland/ph/ph003/ph003-initWM.gv?hsId=${hsId}"
         panelKey="js_panel_ph003_09"
         class="panelContainer">
        <div class="panel">
            <h1>居民工作日志<span class="panel_menu js_reload">刷新</span></h1>
            <%--面板内容--%>
            <div style="min-height:400px;"></div>
        </div>
    </div>
</div>
<oframe:script src="${pageContext.request.contextPath}/eland/ph/ph003/js/ph003.js" type="text/javascript"/>
