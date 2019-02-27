<%--院落信息展示页面--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div style="padding: 0 8px 0 0px;">
    <div class="pageNav" style="display: block;">
        <a class="link" href="javascript:pb002.jumpRegList('${upRegId}');">
            <oframe:entity prjCd="${param.prjCd}" entityName="RegInfo" property="regName" value="${upRegId}"/>
        </a>--->
        <a class="current js_more" offsetX="0" offsetY="-28" openDirection="down" style="position: relative;">
            <span onclick="pb002.jumpRegList('${ttRegId}');">${buildNo}</span>
            <c:if test="${fn:length(regBuilList) > 1}">
                <span class="caret"></span>
                <ul class="menu hidden" style="width:auto;padding-right: 2px;padding-bottom: 1px;">
                    <c:forEach items="${regBuilList}" var="item">
                        <c:choose>
                            <c:when test="${item.Row.prjBuldId == buildId}">
                                <li style="width: 100%" onclick="pb002.openBuild('${item.Row.prjBuldId}')"
                                    class="active">${item.Row.regName}</li>
                            </c:when>
                            <c:otherwise>
                                <li style="width: 100%" onclick="pb002.openBuild('${item.Row.prjBuldId}')">${item.Row.regName}</li>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </ul>
            </c:if>
        </a>
    </div>
    <input name="buildId" type="hidden" value="${buildId}"/>
    <%--基本信息--%>
    <div style="width: 69%;"
         id="m1"
         url="eland/pb/pb002/pb002-initYardInfo.gv?buildId=${buildId}"
         panelKey="js_panel_pb002_1"
         class="panelContainer">
        <div class="panel">
            <h1>基本信息 <span class="panel_menu js_reload">刷新</span></h1>
            <%--面板内容--%>
            <div style="min-height: 280px;">
            </div>
        </div>
    </div>
    <%--待办任务--%>
    <div style="width: 30%;float: right;"
         id="m2"
         url="eland/pb/pb002/pb002-initTD.gv?buildId=${buildId}"
         panelKey="js_panel_pb002_2"
         class="panelContainer">
        <div class="panel">
            <h1>待办任务 <span class="panel_menu js_reload">刷新</span></h1>
            <%--面板内容--%>
            <div style="min-height: 280px;"></div>
        </div>
    </div>

    <%--房屋信息--%>
    <div style="width: 100%;"
         id="m3"
         url="eland/pb/pb002/pb002-initHs.gv?buildId=${buildId}&buildingRegId=${buildingRegId}"
         panelKey="js_panel_pb002_3"
         class="panelContainer">
        <div class="panel">
            <h1>房屋信息 <span class="panel_menu js_reload">刷新</span></h1>
            <%--面板内容--%>
            <div style="min-height: 300px;"></div>
        </div>
    </div>

    <%--成本信息--%>
    <div class="marl5 marb5" style="width: 100%;">
        <div class="tabs">
            <div class="tabsHeader">
                <div class="tabsHeaderContent">
                    <ul>
                        <li class="js_load_tab selected"
                            onclick="pb002.loadTabContext(this);"
                            url="eland/pb/pb002/pb002-initTtYg.gv?buildId=${buildId}">
                            <a href="javascript:"><span>腾退成本分析</span></a>
                        </li>
                        <li class="js_load_tab"
                            onclick="pb002.loadTabContext(this);"
                            url="eland/pb/pb002/pb002-initChartBck.gv?buildId=${buildId}">
                            <a href="javascript:"><span>腾退补偿款</span></a>
                        </li>
                        <li class="js_load_tab"
                            onclick="pb002.loadTabContext(this);"
                            url="eland/pb/pb002/pb002-initChartNHs.gv?buildId=${buildId}">
                            <a href="javascript:"><span>房源使用情况</span></a>
                        </li>
                    </ul>
                </div>
            </div>
            <div class="tabsContent">
                <%--腾退成本--%>
                <div></div>
                <%--腾退补偿款分析--%>
                <div></div>
                <%--房源使用情况--%>
                <div></div>
            </div>
        </div>
    </div>

    <%--附件信息--%>
    <div style="width: 100%;"
         id="m8"
         url="eland/pb/pb002/pb002-initFj.gv?buildId=${buildId}"
         panelKey="js_panel_pb002_8"
         class="panelContainer">
        <div class="panel">
            <h1>附件信息 <span class="panel_menu js_reload">刷新</span></h1>

            <div style="min-height: 300px;"></div>
        </div>
    </div>

    <%--进度跟踪--%>
    <div style="width: 100%;"
         id="m9"
         url="eland/pb/pb002/pb002-initWM.gv?buildId=${buildId}"
         panelKey="js_panel_pb002_9"
         class="panelContainer">
        <div class="panel">
            <h1>进度跟踪 <span class="panel_menu js_reload">刷新</span></h1>
            <%--面板内容--%>
            <div style="min-height: 300px;"></div>
        </div>
    </div>
</div>
<oframe:script src="${pageContext.request.contextPath}/eland/pb/pb002/js/pb002.js" type="text/javascript"/>
<oframe:script src="${pageContext.request.contextPath}/eland/pb/pb002/js/pb002_op.js" type="text/javascript"/>
<oframe:script src="${pageContext.request.contextPath}/eland/ph/ph003/js/ph003_op.js" type="text/javascript"/>

