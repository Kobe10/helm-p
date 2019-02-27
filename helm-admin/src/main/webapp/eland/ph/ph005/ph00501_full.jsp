<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>居民住房改善信息系统</title>
    <script src="${pageContext.request.contextPath}/oframe/plugin/jquery/eland.jquery.min.js"
            type="text/javascript"></script>
    <link href="${pageContext.request.contextPath}/oframe/themes/common/core.css" rel="stylesheet"
          type="text/css" media="screen"/>
    <link href="${pageContext.request.contextPath}/oframe/themes/blue/style.css" id="theme" rel="stylesheet"
          type="text/css" media="screen"/>
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
    <oframe:script src="${pageContext.request.contextPath}/oframe/plugin/system/system.min.js" type="text/javascript"/>
    <oframe:script src="${pageContext.request.contextPath}/oframe/common/form/js/form.js" type="text/javascript"/>
    <oframe:script src="${pageContext.request.contextPath}/oframe/main/js/index.js" type="text/javascript"/>
</head>
<body style="background-color: white;">
<div style="top: 0px !important;text-align: left" id="container">
    <div id="navTab" class="tabsPage">
        <div style="display: none;" class="tabsPageHeader hidden">
            <div class="tabsPageHeaderContent">
                <ul class="navTab-tab selected">
                    <li tabid="ph005-init" class="main"><a href="javascript:"><span><span>项目地图</span></span></a></li>
                </ul>
            </div>
        </div>
        <div class="navTab-panel tabsPageContent layoutBox">
            <input type="hidden" id="sysPrjCd" name="prjCd" value="${prjCd}"/>

            <%--房产详细地图--%>
            <div>
                <jsp:include
                        page="/eland/ph/ph005/ph005-showAllMap.gv?fullFlag=new&prjCd=${prjCd}"/>
            </div>
        </div>
    </div>
</div>
</body>
<style type="text/css">
    .tabsPage .tabsPageHeader ul.bc_tabs_nav {
        width: 100%;
        height: 100%;
        background: #3c90c8;
        text-align: center;
        line-height: 37px;
    }

    .tabsPage .tabsPageHeader ul.bc_tabs_nav li {
        border: none;
        display: inline-block;
        background: #3c90c8;
        float: none;
    }

    .tabsPage .tabsPageHeader ul.bc_tabs_nav li a {
        color: white;
    }

    .tabsPage .bc_tabs_nav a.close, .tabsPage .tabsPageHeader li.hover .close, .tabsPage .tabsPageHeader li.selected .close {
        background: url(../../../oframe/themes/blue/images/tabs/tabspage_icon.png) no-repeat;
    }
</style>
<script type="text/javascript">
    function freshMap() {
        ph005Map.loadMapData('search');
        setTimeout("freshMap()", 300000);
    }

    DWZ.init("${pageContext.request.contextPath}/oframe/plugin/dwz/dwz.frag.xml", {
        // 弹出登录对话框
        statusCode: {ok: 200, error: 300, timeout: 301},
        debug: false,
        callback: function () {
            initEnv();
            //定时刷新 地图
            freshMap();
            navTab.getCurrentPanel().attr("opCode", "ph005-showAllMap");
        }
    });

    navTab.openTab = function (tabid, url, options) { //if found tabid replace tab, else create a new tab.
        tabid = "second_tab"
        var op = $.extend({title: "New Tab", data: {}, fresh: false, external: false}, options);
        // 记录当前的标签
        this._pIndex = this._currentIndex;
        var iOpenIndex = this._indexTabId(tabid);
        if (iOpenIndex >= 0) {
            var $tab = this._getTabs().eq(iOpenIndex);
            var span$ = $tab.attr("tabid") == this._op.mainTabId ? "> span > span" : "> span";
            $tab.find(">a").attr("title", op.title).find(span$).text(op.title);
            var $panel = this._getPanels().eq(iOpenIndex);
            if (op.fresh || !$tab.attr("url")) {
                $tab.attr("url", url);
                $tab.data("opt", op.data);
                if (op.external || url.isExternalUrl()) {
                    $tab.addClass("external");
                    navTab.openExternal(url, $panel);
                } else {
                    $tab.removeClass("external");
                    $panel.ajaxUrl({
                        type: "GET", url: url, data: op.data, callback: function () {
                            navTab._loadUrlCallback($panel);
                            /*if($($panel).find(".pageContent .panelBar").css("position") == "fixed"){
                             $($panel).find(".pageContent .panelBar:has('.toolBar')").width($($panel).find(".pageContent").width()-2);
                             }*/
                        }
                    });
                }
            }
            this._currentIndex = iOpenIndex;
            $panel.find(".pageContent>.panelBar").width($panel.find(".pageContent").width());
        } else {
            var tabFrag = '<li tabid="#tabid#"><a href="javascript:" title="#title#" class="#tabid#"><span>#title#</span></a><a href="javascript:;" class="close">close</a></li>';
            this._tabBox.append(tabFrag.replaceAll("#tabid#", tabid).replaceAll("#title#", op.title));
            this._panelBox.append('<div class="page unitBox"></div>');
            this._moreBox.append('<li><a href="javascript:" title="#title#">#title#</a></li>'.replaceAll("#title#", op.title));

            var $tabs = this._getTabs();
            var $tab = $tabs.filter(":last");
            $tab.data("opt", op.data)
            var $panel = this._getPanels().filter(":last");

            if (op.external || url.isExternalUrl()) {
                $tab.addClass("external");
                navTab.openExternal(url, $panel);
            } else {
                $tab.removeClass("external");
                $panel.ajaxUrl({
                    type: "GET", url: url, data: op.data, callback: function () {
                        navTab._loadUrlCallback($panel);
                        /*if($($panel).find(".pageContent .panelBar").css("position") == "fixed"){
                         $($panel).find(".pageContent .panelBar:has('.toolBar')").width($($panel).find(".pageContent").width()-2);
                         }*/

                    }
                });
            }
            // 回退历史处理
            if ($.History) {
                setTimeout(function () {
                    $.History.addHistory(tabid, function (tabid) {
                        var i = navTab._indexTabId(tabid);
                        if (i >= 0) navTab._switchTab(i);
                    }, tabid);
                }, 10);
            }
            this._currentIndex = $tabs.size() - 1;
            this._contextmenu($tabs.filter(":last").hoverClass("hover"));


        }
        // 设置模块编号
        $panel.attr("opCode", op.data.opCode);

        this._init();
        this._scrollCurrent();

        this._getTabs().eq(this._currentIndex).attr("url", url);

        var $this = this;
        var tabsPageHeaderContent = this._tabBox.closest("div.tabsPageHeaderContent");
        var bc_tabs_nav = tabsPageHeaderContent.find("ul.bc_tabs_nav");
        if (bc_tabs_nav.length == 0) {
            tabsPageHeaderContent.append("<ul class='bc_tabs_nav'><li class='bc_" + op.data.opCode + "'><a><span>关闭</span></a></li></ul>");
            tabsPageHeaderContent.find("ul.bc_tabs_nav").click(function (event) {
                var newEvent = jQuery.Event("click");
                newEvent.forClick = true;
                $this._getTab(tabid).find("a.close").trigger(newEvent);
            });
        } else {
            bc_tabs_nav.html("<li class='bc_" + op.data.opCode + "'><a><span>关闭</span></a></li>")
        }
        this._tabBox.hide();
        this._getTab(tabid).attr("bc_dialog", true)
        this._tabBox.closest("div.tabsPageHeader").show()
    }
    navTab._closeTab = function (index, openTabid) {
        if (this._getTabs().eq(index).attr("bc_dialog")) {
            this._getTabs().closest("div.tabsPageHeader").hide()
        }
        this._getTabs().eq(index).remove();
        this._getPanels().eq(index).trigger(DWZ.eventType.pageClear).remove();
        this._getMoreLi().eq(index).remove();

        if (this._currentIndex >= index) this._currentIndex--;

        if (openTabid) {
            var openIndex = this._indexTabId(openTabid);
            if (openIndex > 0) this._currentIndex = openIndex;
        } else if (this._pIndex > 0) {
            this._currentIndex = this._pIndex;
        }
        var maxIdx = this._getTabs().length - 1;
        if (this._currentIndex > maxIdx) {
            this._currentIndex = maxIdx;
        }

        this._init();
        this._scrollCurrent();
        this._reload(this._getTabs().eq(this._currentIndex));
    }
</script>
</html>