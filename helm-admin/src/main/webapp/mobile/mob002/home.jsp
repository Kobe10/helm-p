<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head lang="en">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0">
    <title>居民住房改善信息系统</title>
    <link type="text/css" href="${pageContext.request.contextPath}/mobile/common/css/homepage.css" rel="stylesheet">
    <script type="text/javascript" src="${pageContext.request.contextPath}/mobile/common/js/jquery.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/oframe/plugin/system/system.min.js"></script>
    <%--echarts.js--%>
    <script type="text/javascript" src="${pageContext.request.contextPath}/mobile/common/js/echarts.min.js"></script>
</head>
<body>
<div class="header">
    <a href="<%=request.getContextPath()%>/mobile/mob001/login-staffLogout.gv" class="close" onclick="sysExit();"><
        注销</a>
    <span class="headerUser">我的首页</span>
</div>
<div class="container">
    <!-- 为ECharts准备一个具备大小（宽高）的Dom -->
    <c:forEach items="${staffPanel}" var="item" varStatus="varStatus">
        <c:choose>
            <c:when test="${fn:contains(item.portalRht.navUrl, 'mb005')}">
                <div class="chart_panel" style="width:100%;min-height: 300px">
                    <jsp:include
                            page="/mobile/mob002/mob002-loadPic.gv?prjCd=${prjCd}&${item.portalRht.urlParam}&privilegeId=${item.portalRht.rhtId}"/>
                </div>
            </c:when>
            <c:when test="${fn:contains(item.portalRht.navUrl, 'mb008')}">
                <div class="chart_panel" style="width:100%;min-height: 300px">
                    <jsp:include
                            page="/mobile/mob002/mob002-load2Pic.gv?prjCd=${prjCd}&${item.portalRht.urlParam}&privilegeId=${item.portalRht.rhtId}"/>
                </div>
            </c:when>
            <%--<c:otherwise>--%>
                <%--<jsp:include page="/${item.portalRht.navUrl}"/>--%>
            <%--</c:otherwise>--%>
        </c:choose>
    </c:forEach>
</div>
<script type="text/javascript">
    /**
     * 推出系统
     */
    var portal = {
        /**
         * 缓存首页面板中存在的图标对象，key： div的唯一标识， value： echart对象
         **/
        chartMap: new Map()
    };

    function sysExit() {
        localStorage.clear();
    }
</script>
</body>
</html>