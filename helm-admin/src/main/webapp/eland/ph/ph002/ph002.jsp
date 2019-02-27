<%--院落信息展示页面--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="pageNav">
    <a href="javascript:index.quickOpen('eland/ph/ph001/ph001-init.gv',{opCode:'ph001-init',opName:'居民信息',fresh:true})">居民信息</a>--->
    <a class="link" href="javascript:ph002.jumpYardS();">
        <oframe:entity  prjCd="${param.prjCd}" entityName="RegInfo" property="regName" value="${ttUpRegId}"/>
    </a>--->
    <a class="current js_more" offsetX="5" style="position: relative;">
        ${buildNo}号
        <c:if test="${fn:length(regBuilList) > 1}">
            <span class="caret"></span>
            <ul class="menu hidden">
                <c:forEach items="${regBuilList}" var="item">
                    <c:choose>
                        <c:when test="${item.Row.prjBuldId == buildId}">
                            <li onclick="ph002.openBuild('${item.Row.prjBuldId}')"
                                class="active">${item.Row.regName}</li>
                        </c:when>
                        <c:otherwise>
                            <li onclick="ph002.openBuild('${item.Row.prjBuldId}')">${item.Row.regName}</li>
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
     url="eland/ph/ph002/ph002-initYardInfo.gv?buildId=${buildId}"
     panelKey="js_panel_ph002_1"
     class="panelContainer">
    <div class="panel">
        <h1>基本信息 <span class="panel_menu js_reload">刷新</span></h1>
        <%--面板内容--%>
        <div style="min-height: 300px;">
        </div>
    </div>
</div>
<%--待办任务--%>
<div style="width: 29%;"
     id="m2"
     url="eland/ph/ph002/ph002-initTD.gv?buildId=${buildId}"
     panelKey="js_panel_ph002_2"
     class="panelContainer">
    <div class="panel">
        <h1>待办任务 <span class="panel_menu js_reload">刷新</span></h1>
        <%--面板内容--%>
        <div style="min-height: 300px;"></div>
    </div>
</div>
<%--房屋信息--%>
<div style="width: 99%;"
     id="m3"
     url="eland/ph/ph002/ph002-initHs.gv?buildId=${buildId}&buildingRegId=${buildingRegId}"
     panelKey="js_panel_ph002_3"
     class="panelContainer">
    <div class="panel">
        <h1>房屋信息 <span class="panel_menu js_reload">刷新</span></h1>
        <%--面板内容--%>
        <div style="min-height: 300px;"></div>
    </div>
</div>
<%--成本信息--%>
<div class="marl5 marb5" style="width: 99%;">
    <div class="tabs">
        <div class="tabsHeader">
            <div class="tabsHeaderContent">
                <ul>
                    <li class="js_load_tab selected"
                        onclick="ph002.loadTabContext(this);"
                        url="eland/ph/ph002/ph002-initTtYg.gv?buildId=${buildId}">
                        <a href="javascript:"><span>腾退成本分析</span></a>
                    </li>
                    <li class="js_load_tab"
                        onclick="ph002.loadTabContext(this);"
                        url="eland/ph/ph002/ph002-initChartBck.gv?buildId=${buildId}">
                        <a href="javascript:"><span>腾退补偿款</span></a>
                    </li>
                    <li class="js_load_tab"
                        onclick="ph002.loadTabContext(this);"
                        url="eland/ph/ph002/ph002-initChartNHs.gv?buildId=${buildId}">
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
<div style="width: 99%;"
     id="m8"
     url="eland/ph/ph002/ph002-initFj.gv?buildId=${buildId}"
     panelKey="js_panel_ph002_8"
     class="panelContainer">
    <div class="panel">
        <h1>附件信息 <span class="panel_menu js_reload">刷新</span></h1>

        <div style="min-height: 300px;"></div>
    </div>
</div>

<%--进度跟踪--%>
<div style="width: 99%;"
     id="m9"
     url="eland/ph/ph002/ph002-initWM.gv?buildId=${buildId}"
     panelKey="js_panel_ph002_9"
     class="panelContainer">
    <div class="panel">
        <h1>进度跟踪 <span class="panel_menu js_reload">刷新</span></h1>
        <%--面板内容--%>
        <div style="min-height: 300px;"></div>
    </div>
</div>
<oframe:script src="${pageContext.request.contextPath}/eland/ph/ph002/js/ph002.js" type="text/javascript"/>
<oframe:script src="${pageContext.request.contextPath}/eland/ph/ph002/js/ph002_op.js" type="text/javascript"/>
