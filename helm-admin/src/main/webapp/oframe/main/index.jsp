<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>百万庄管理驾驶舱</title>
    <script src="${pageContext.request.contextPath}/oframe/plugin/jquery/eland.jquery.min.js"
            type="text/javascript"></script>
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/favicon.ico"/>
    <link href="${pageContext.request.contextPath}/oframe/themes/common/core.css" rel="stylesheet"
          type="text/css"/>
    <link href="${pageContext.request.contextPath}/oframe/themes/${theme}/style.css" id="theme" rel="stylesheet"
          type="text/css"/>
    <link href="${pageContext.request.contextPath}/oframe/plugin/uploadify/css/uploadify.css" rel="stylesheet"
          type="text/css" media="screen"/>
    <link href="${pageContext.request.contextPath}/oframe/plugin/jquery/css/jquery.autocomplete.css" rel="stylesheet"
          type="text/css" media="screen"/>
    <link href="${pageContext.request.contextPath}/oframe/plugin/ztree/zTreeStyle/zTreeStyle.css" rel="stylesheet"
          type="text/css" media="screen"/>
    <link href="${pageContext.request.contextPath}/oframe/plugin/jquery/css/colorPicker.css" rel="stylesheet"
          type="text/css" media="screen"/>
    <link href="${pageContext.request.contextPath}/oframe/themes/common/print.css" rel="stylesheet" type="text/css"
          media="print"/>
    <link href="${pageContext.request.contextPath}/oframe/themes/common/ieHack.css" rel="stylesheet" type="text/css"
          media="print"/>
    <%--treeTable 树形表格--%>
    <link href="${pageContext.request.contextPath}/oframe/plugin/jquery/css/jquery.treetable.css"
          rel="stylesheet" type="text/css" media="screen"/>
    <link href="${pageContext.request.contextPath}/oframe/plugin/jquery/css/jquery.treetable.theme.default.css"
          rel="stylesheet" type="text/css" media="screen"/>
    <link href="${pageContext.request.contextPath}/oframe/plugin/jquery/css/screen.css"
          rel="stylesheet" type="text/css" media="screen"/>

    <%--字体大小控制开始--%>
    <c:choose>
        <c:when test="${fontSize != null}">
            <link href="${pageContext.request.contextPath}/oframe/themes/common/font-${fontSize}.css"
                  id="fontStyle" rel="stylesheet" type="text/css" media="screen"/>
        </c:when>
        <c:otherwise>
            <link href="${pageContext.request.contextPath}/oframe/themes/common/font-default.css"
                  id="fontStyle" rel="stylesheet" type="text/css" media="screen"/>
        </c:otherwise>
    </c:choose>
    <%--字体大小控制结束--%>

    <%--cookie--%>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/oframe/plugin/jquery/jquery.cookie.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/oframe/plugin/jquery/jquery.treetable.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/oframe/plugin/jquery/jquery.PrintArea.js"></script>
    <%--Openlayers 地图展示--%>
    <link href="${pageContext.request.contextPath}/oframe/plugin/openlayers/theme/default/style.css" rel="stylesheet"
          type="text/css" media="screen"/>
    <script src="${pageContext.request.contextPath}/oframe/plugin/openlayers/OpenLayers.js"
            type="text/javascript"></script>
    <!--[if lte IE 9]>
    <script src="${pageContext.request.contextPath}/oframe/js/speedup.js" type="text/javascript"></script>
    <![endif]-->
    <script src="${pageContext.request.contextPath}/oframe/plugin/superfish.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/oframe/plugin/jcarousellite_1.0.1.js"
            type="text/javascript"></script>

    <script type="text/javascript" charset="utf-8"
            src="${pageContext.request.contextPath}/oframe/plugin/ueditor/ueditor.config.js"></script>
    <script type="text/javascript" charset="utf-8"
            src="${pageContext.request.contextPath}/oframe/plugin/ueditor/ueditor.all.min.js"></script>
    <script type="text/javascript" charset="utf-8"
            src="${pageContext.request.contextPath}/oframe/plugin/ueditor/third-party/video-js/video.js"></script>
    <!--建议手动加在语言，避免在ie下有时因为加载语言失败导致编辑器加载失败-->
    <!--这里加载的语言文件会覆盖你在配置项目里添加的语言类型，比如你在配置项目里配置的是英文，这里加载的中文，那最后就是中文-->
    <script type="text/javascript" charset="utf-8"
            src="${pageContext.request.contextPath}/oframe/plugin/ueditor/lang/zh-cn/zh-cn.js"></script>

    <script src="${pageContext.request.contextPath}/oframe/plugin/jquery/jquery.autocomplete.js"
            type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/oframe/plugin/jquery/jquery.colorPicker.js"
            type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/oframe/plugin/uploadify/scripts/jquery.uploadify.js"
            type="text/javascript"></script>

    <%--树形引用结构--%>
    <script src="${pageContext.request.contextPath}/oframe/plugin/ztree/js/jquery.ztree.all-3.5.js"
            type="text/javascript"></script>

    <!-- 可以用dwz.min.js替换前面全部dwz.*.js (注意：替换是下面dwz.regional.zh.js还需要引入)-->
    <script src="${pageContext.request.contextPath}/oframe/plugin/dwz/dwz.min.js" type="text/javascript"></script>

    <!--项目图形-->
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/oframe/plugin/raphael/raphael-min.js"></script>
    <oframe:script type="text/javascript"
                   src="${pageContext.request.contextPath}/oframe/plugin/raphael/raphael.entity.js"/>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/oframe/plugin/raphael/raphael.pan-zoom.js"></script>
    <%--ECharts 图表展示--%>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/oframe/plugin/echarts/echarts.js"></script>
    <%--异步文件上传--%>
    <oframe:script type="text/javascript"
                   src="${pageContext.request.contextPath}/oframe/plugin/ajaxfileupload/ajaxfileupload.js"
                   charset="UTF-8"/>
    <!--自定义引入js-->
    <script id="systemScript" contextPath="${pageContext.request.contextPath}/"
            src="${pageContext.request.contextPath}/oframe/plugin/system/system.min.js"
            type="text/javascript"></script>
    <oframe:script src="${pageContext.request.contextPath}/oframe/common/form/js/form.js" type="text/javascript"/>
    <script type="text/javascript">
        $(function () {
            DWZ.init("${pageContext.request.contextPath}/oframe/plugin/dwz/dwz.frag.xml", {
                loginUrl: "login_dialog.html", loginTitle: "登录",
                // 弹出登录对话框
                statusCode: {ok: 200, error: 300, timeout: 301},
                pageInfo: {
                    pageNum: "pageNum",
                    numPerPage: "numPerPage",
                    orderField: "orderField",
                    orderDirection: "orderDirection"
                }, //【可选】
                debug: false,
                callback: function () {
                    initEnv();
                    $("#themeList").theme({themeBase: "${pageContext.request.contextPath}/oframe/themes"});
                    // 加载首页面板
                    $("li.top_li:first a").trigger("click");
                }
            });
        });
        (function ($) {
            $(document).ready(function () {
                $('#sf-menu').superfish({
                    cssArrows: false,
                    pathClass: 'current'
                });
            });
        })(jQuery);
    </script>
</head>

<body scroll="no" onunload="upFile.closeScan();">
<div id="layout">
    <div id="top">欢迎您，
        <span>${loginInfo.LoginInfo.staffName}</span>
        <span class="marr5 marl5">|</span>
        <span id="prjMenu">
            <i style="font-style:normal;">
                <c:choose>
                    <c:when test="${prjCd == 0 && orgId != null}">
                        <oframe:org prjCd="0" orgId="${orgId}"/>
                    </c:when>
                    <c:otherwise>
                        ${prjName}
                    </c:otherwise>
                </c:choose>
            </i>
            <span class="caret" style="margin-bottom: 3px;"></span>
            <div id="prjListForMenu" class="ztree hidden"></div>
        </span>
        <ul id="themeList" class="themeList" title="换肤" onclick="index.switchSkin()">
            <li theme="blue" class="blue">
                <div class="">蓝色</div>
            </li>
            <li theme="yellow" class="yellow">
                <div class="">黄色</div>
            </li>
        </ul>
        <input type="hidden" class="js_main_login_staff" value="${loginInfo.LoginInfo.staffCode}"/>
        <input type="hidden" id="sysPrjCd" name="prjCd" value="${prjCd}"/>
        <a href="<%=request.getContextPath()%>/oframe/sysmg/sys001/login-staffLogout.gv" class="logout">退出</a>
        <a onclick="index.bigger(this)" title="字体缩小" class="smaller">缩小</a>
        <a onclick="index.bigger(this)" title="字体放大" class="bigger">放大</a>
        <a onclick="index.openSetting();" class="setter">设置</a>
        <a onmousemove="index.getMessage(false);" onmouseout="index.closeMessage()"
           style="position: relative;" class="message">
            消息<label class="hidden js_messageCount"></label>

            <div class="hidden js_message_context">
            </div>
        </a>
    </div>

    <div id="header">
        <div class="headerNav"><a class="logo" href="javascript:void(0)">
            <c:choose>
                <c:when test="${fn:length(orgLogo) != 0}">
                    <img width="239px" height="39px" usemap="#planetmap" alt="Planets" src="${orgLogo}">
                </c:when>
                <c:otherwise>
                    <img src="../themes/blue/images/sub-logo_03.png" usemap="#planetmap" alt="Planets"/>
                </c:otherwise>
            </c:choose>
            <map name="planetmap" id="planetmap">
                <area shape="rect" coords="192,0,230,20" href="javascript:void(0);" alt="About"/>
            </map>
        </a>
        </div>
        <a onclick="index.navMove(-1)" title="向左" class="goleft">向左</a>

        <ul class="sf-menu" rightAttr="0" id="sf-menu">
            <%--系统菜单 循环展示--%>
            <c:forEach items="${menuList}" var="item" varStatus="varStatus">
                <c:choose>
                    <c:when test="${varStatus.last}">
                        <li class="last-li-menu top_li apply_array">
                            <a href="javascript:void(0)" id="top_${item.id}"
                               onclick='index.openTab(this, ${item.id}, "${item.url}", "${item.cd}", "${item.name}")'
                               onmouseover='index.queryMenuTree("${item.id}", "${item.url}")'>${item.name}</a>
                            <ul></ul>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <li class="last-li top_li apply_array">
                            <a href="javascript:void(0)" id="top_${item.id}"
                               onclick='index.openTab(this, ${item.id}, "${item.url}", "${item.cd}", "${item.name}")'
                               onmouseover='index.queryMenuTree("${item.id}", "${item.url}")'>${item.name}</a>
                            <ul></ul>
                        </li>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
        </ul>
        <a onclick="index.navMove(1)" title="向右" class="goright">向右</a>
    </div>

    <div id="container">
        <div id="navTab" class="tabsPage">
            <div class="tabsPageHeader">
                <div class="tabsPageHeaderContent">
                    <ul class="navTab-tab selected">
                        <li tabid="wdsy" class="main"><a href="javascript:"><span><span
                                class="home_icon">我的主页</span></span></a></li>
                    </ul>
                </div>
                <div class="tabsLeft">left</div>
                <!-- 禁用只需要添加一个样式 class="tabsLeft tabsLeftDisabled" -->
                <div class="tabsRight">right</div>
                <!-- 禁用只需要添加一个样式 class="tabsRight tabsRightDisabled" -->
                <div class="tabsMore">more</div>
            </div>
            <ul class="tabsMoreList">
                <li><a href="javascript:">我的主页</a></li>
            </ul>
            <div class="navTab-panel tabsPageContent layoutBox">
                <div class="page unitBox">
                </div>
            </div>
        </div>

        <div class="short-link" id="short-link">
            <a href="javascript:void(0)" class="short-link-a"></a>
            <ul>
                <c:forEach items="${collectionItems.Collection.Node}" var="item" varStatus="varStatus">
                    <li class="slink-bg-${(varStatus.index) % 5 + 1}">
                        <a href="javascript:void(0)"
                           onclick='index.openTab(this, ${item.rhtId}, "${item.navUrl}", "${item.rhtCd}", "${item.rhtName}")'>
                                ${item.rhtName}</a>
                    </li>
                </c:forEach>
            </ul>
        </div>
    </div>

    <oframe:power prjCd="${prjCd}" rhtCd="SCAN_FUNCTION">
        <object id="Capture" class="hidden" classid="clsid:9A73DB73-2CA3-478D-9A3F-7E9D6A8D327C"
                codebase="<%=request.getContextPath()%>/oframe/common/file/CaptureVideo.cab?_v=N2.0.6.0">
        </object>
    </oframe:power>
</div>
<div id="messageTipArea">
    <div class="messageTipItem hidden">
        <h1><span class="messageTitle"></span><span class="closeMessage">X</span></h1>
        <div class="messageTipContext"></div>
    </div>
</div>
</body>
<oframe:script src="${pageContext.request.contextPath}/oframe/main/js/index.js" type="text/javascript"/>
<oframe:script src="${pageContext.request.contextPath}/oframe/main/js/msgTip.js" type="text/javascript"/>
</html>