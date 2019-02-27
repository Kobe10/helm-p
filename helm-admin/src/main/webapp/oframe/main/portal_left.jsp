<%--个人首页--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div id="myWorkBench" class="mar5">

    <%--左侧导航--%>
    <div class="accordion panel" extendAll="true"
         style="border: 1px solid #e4e4e4;width: 240px;float: left;position: relative;overflow: hidden;"
         id="bf001Left" layoutH="15">
        <div class="accordion" extendAll="true">
            <div class="accordionContent">
                <c:forEach items="${staffPanel}" var="typeItem" varStatus="varStatus">
                    <div class="accordionHeader">
                        <h2 class="collapsable">
                            <label>${typeItem.key}</label>
                        </h2>
                    </div>
                    <div class="accordionContent">
                        <ul class="accordion_menu">
                            <c:forEach items="${typeItem.value}" var="item" varStatus="varStatus1">
                                <li onclick='leftPortal.loadLeftPanel(this)'
                                    rhtId="${item.portalRht.rhtId}"
                                    navUrl="${item.portalRht.navUrl}"
                                    rhtCd="${item.portalRht.rhtCd}"
                                    rhtName="${item.portalRht.rhtName}"
                                    needFullScreen="${item.portalRht.needFullScreen}"
                                    class="js_panel_${item.portalRht.rhtId} js_panel_nav">
                                        ${item.portalRht.rhtName}
                                </li>
                            </c:forEach>
                        </ul>
                    </div>
                </c:forEach>
                </ul>
            </div>
        </div>
    </div>

    <%--右侧自定义画板--%>
    <div class="panel" id="leftPortalContent" style="margin-left: 250px;margin-right: 5px;position: relative;">
        <h1><span class="js_name"></span>
            <span class="reflesh" onclick='leftPortal.reloadLeftPanel(this)'></span>
            <span name="full" class="screen js_screen" onclick="leftPortal.fullScreen(this);"></span>
        </h1>
        <div class="js_panel_context" layoutH="55"></div>
    </div>
</div>

<script>
    var portal = {
        /**
         * 缓存首页面板中存在的图标对象，key： div的唯一标识， value： echart对象
         **/
        chartMap: new Map()
    };

    /**
     * 控制全屏显示
     * @type {{supportsFullScreen: boolean, fullScreen: Function}}
     */
    var leftPortal = {
        supportsFullScreen: false,
        fullScreen: function (obj) {
            var $this = $("#myWorkBench").find("li.js_panel_nav.selected");
            var $id = $this.attr("rhtId");
            var url = getGlobalPathRoot() + "oframe/main/main-screen.gv?prjCd=" + getPrjCd() + "&id=" + $id;
            window.open(url);
        },

        reloadLeftPanel: function () {
            $("#myWorkBench").find("li.js_panel_nav.selected").trigger("click");
        },
        /**
         * 加载首页面板
         */
        loadLeftPanel: function (obj) {
            var $this = $(obj);
            var rhtUrl = $this.attr("navUrl");
            if (rhtUrl == "" || rhtUrl == "#") {
                return;
            }
            var privilegeId, rhtUrl, opCode, opName;
            var privilegeId = $this.attr("rhtId");
            var rhtCd = $this.attr("rhtCd");
            var rhtName = $this.attr("rhtName");
            var needFullScreen = $this.attr("needFullScreen");
            var urlData = getGlobalPathRoot() + encodeURI(encodeURI(rhtUrl));
            var packet = new AJAXPacket(urlData);
            packet.data.add("prjCd", getPrjCd());
            packet.data.add("privilegeId", privilegeId);
            packet.noCover = true;
            core.ajax.sendPacketHtml(packet, function (response) {
                var leftPortalContent = $("#leftPortalContent");
                var contextDiv = leftPortalContent.find("div.js_panel_context");
                contextDiv.html(response);
                initUI(contextDiv);
                $this.closest("div.accordion").find("li.js_panel_nav").removeClass("selected");
                $this.addClass("selected");
                leftPortalContent.find("span.js_name").html(rhtName);
                if (needFullScreen == "true") {
                    leftPortalContent.find("span.js_screen").show();
                } else {
                    leftPortalContent.find("span.js_screen").hide();
                }
            })
        }
    };
    $(function () {
        $("#myWorkBench").find("li.js_panel_nav:eq(0)").trigger("click");
    });
</script>
