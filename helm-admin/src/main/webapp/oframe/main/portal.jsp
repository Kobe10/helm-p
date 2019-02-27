<%--个人首页--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div id="myWorkBench">
    <div class="change" style="width: 50%;display: inline-block; float:left;vertical-align: top;">
        <c:forEach items="${staffPanel}" var="item" varStatus="varStatus">
            <c:if test="${varStatus.index % 2 == 0}">
                <div class="js_panel_${item.portalRht.rhtId} panel mar5 collapse js_curr_panel"
                     url="${item.portalRht.navUrl}" id="${item.portalRht.rhtId}">
                    <h1><span>${item.portalRht.rhtName}${mb}</span>
                        <c:if test="${item.portalRht.needWorkMore == 'true'}">
                            <span name="more" style="float: right;color:#a7a7a7;cursor: pointer;"
                                  onmouseover="this.style.color='#337fb1';" onmouseout="this.style.color='#a7a7a7';"
                                  onclick="wf001.queryWF(this);">···</span>
                        </c:if>
                        <c:if test="${item.portalRht.needTaskMore == 'true'}">
                            <span name="more" style="float: right;color:#a7a7a7;cursor: pointer;"
                                  onmouseover="this.style.color='#337fb1';" onmouseout="this.style.color='#a7a7a7';"
                                  onclick="wf001.queryTask(this);">···</span>
                        </c:if>
                        <span class="reflesh"
                              onclick='index.loadPanel(${item.portalRht.rhtId},"${item.portalRht.navUrl}","${item.portalRht.rhtCd}","${item.portalRht.rhtName}")'>
                        </span>
                        <c:if test="${item.portalRht.needFullScreen != null}">
                            <span href="javascript:void(0);" name="full"
                                  class="screen"
                                  onclick="fullScreenApi.fullScreen(this);"></span>
                        </c:if>
                    </h1>
                        <%--面板内容--%>
                    <div class="js_panel_context" style="background-color: #ffffff;">
                    </div>
                </div>
            </c:if>
        </c:forEach>
    </div>
    <div class="change" style="width: 50%;display: inline-block;float:right;vertical-align: top;">
        <c:forEach items="${staffPanel}" var="item" varStatus="varStatus">
            <c:if test="${varStatus.index % 2 == 1}">
                <div class="js_panel_${item.portalRht.rhtId} panel mar5 collapse js_curr_panel"
                     name="prjCd" url="${item.portalRht.navUrl}" id="${item.portalRht.rhtId}">
                    <h1><span>${item.portalRht.rhtName}</span>
                        <c:if test="${item.portalRht.needWorkMore == 'true'}">
                            <span name="more" style="float: right;color:#a7a7a7;cursor: pointer;"
                                  onmouseover="this.style.color='#337fb1';" onmouseout="this.style.color='#a7a7a7';"
                                  onclick="wf001.queryWF(this);">···</span>
                        </c:if>
                        <c:if test="${item.portalRht.needTaskMore == 'true'}">
                            <span name="more" style="float: right;color:#a7a7a7;cursor: pointer;"
                                  onmouseover="this.style.color='#337fb1';" onmouseout="this.style.color='#a7a7a7';"
                                  onclick="wf001.queryTask(this);">···</span>
                        </c:if>
                        <span class="reflesh" onclick='index.loadPanel(${item.portalRht.rhtId},
                                "${item.portalRht.navUrl}",
                                "${item.portalRht.rhtCd}",
                                "${item.portalRht.rhtName}")'>
                        </span>
                        <c:if test="${item.portalRht.needFullScreen != null}">
                            <span name="full"
                                  class="screen"
                                  onclick="fullScreenApi.fullScreen(this);"></span>
                        </c:if>
                    </h1>
                        <%--面板内容--%>
                    <div class="js_panel_context" style="background-color: #ffffff;">
                    </div>
                </div>
            </c:if>
        </c:forEach>
    </div>
</div>
<oframe:script type="text/javascript" src="${pageContext.request.contextPath}/oframe\wf\wf001\js\wf001.js"/>
<script>
    var portal = {
        /**
         * 缓存首页面板中存在的图标对象，key： div的唯一标识， value： echart对象
         **/
        chartMap: new Map()
    };

    $(function () {
        $("#myWorkBench").find("span.reflesh").trigger("click");
    });

    /**
     * 控制全屏显示
     * @type {{supportsFullScreen: boolean, fullScreen: Function}}
     */
    var fullScreenApi = {
        supportsFullScreen: false,
        fullScreen: function (obj) {
            var $this = $(obj);
            var $id = $this.closest("div.js_curr_panel").attr("id");
            var url = getGlobalPathRoot() + "oframe/main/main-screen.gv?prjCd=" + getPrjCd() + "&id=" + $id;
            window.open(url);
        }
    }
</script>
