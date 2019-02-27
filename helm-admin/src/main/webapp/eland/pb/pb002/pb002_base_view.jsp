<%--建筑信息基本信息面板--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="panel">
    <h1>基本信息
        <span class="panel_menu js_reload">刷新</span>
        <%--<span class="panel_menu" onclick="ph00201.edit(this,'${buildingRegId}');">编辑</span>--%>
        <%--<span class="panel_menu" onclick="ph00201.editBuild(this,'${buildId}');">编辑</span>--%>
    </h1>
    <%--面板内容--%>
    <div class="js_panel_context" style="background-color: #ffffff; min-height: 280px;">
        <input type="hidden" name="buildPosition" value="${buildBean.BuildInfo.buildPosition}"/>
        <input type="hidden" name="baselayerUrl" value="${baselayerUrl}"/>
        <input type="hidden" name="layerName1" value="${layerName1}"/>
        <input type="hidden" name="layerName2" value="${layerName2}"/>

        <div class="border" style="position: relative; width: 49%; display: inline-block; overflow: hidden;">
            <div id="yardView" style="width: 100%;height: 270px;">
                 <%--style="clear: both; width: 54%; height: 295px; border: 1px #000000 solid;position: relative; display: inline-block; overflow: hidden;">--%>
            </div>
            <div class="mapPosition" title="全屏" onclick="pb002Base.openHsInMap();"></div>
        </div>

        <div style="position: relative; display: inline-block; width: 49%; overflow: hidden; float: right;">
            <input type="hidden" name="" value=""/>

            <table class="border">
                <tr>
                    <th width="25%">建筑类型：</th>
                    <td><oframe:name prjCd="${param.prjCd}" itemCd="BUILD_TYPE" value="${buildBean.BuildInfo.buildType}"/></td>
                </tr>
                <tr>
                    <th>建筑状态：</th>
                    <td><oframe:name prjCd="${param.prjCd}" itemCd="YARD_STATUS" value="${buildBean.BuildInfo.buildStatus}"/></td>
                </tr>
                <tr>
                    <th>占地面积：</th>
                    <td>${buildBean.BuildInfo.buildLandSize}</td>
                </tr>
                <tr>
                    <th>管理区域：</th>
                    <td>${buildMng.BuildInfo.ttUpRegName}</td>
                </tr>
                <tr>
                    <th>管理小组：</th>
                    <td>${buildMng.BuildInfo.ttOrgName}</td>
                </tr>
                <tr>
                    <th>中介公司：</th>
                    <td>${buildMng.BuildInfo.ttCompanyName}</td>
                </tr>
                <tr>
                    <th>主谈人员：</th>
                    <td>${buildMng.BuildInfo.ttMainTalk}</td>
                </tr>
                <tr>
                    <th>备注说明：</th>
                    <td>${buildBean.BuildInfo.buildNote}</td>
                </tr>
            </table>
        </div>
    </div>
</div>
<oframe:script src="${pageContext.request.contextPath}/eland/pb/pb002/js/pb002_base.js" type="text/javascript"/>
<script>
    $(document).ready(function () {
        //初始化地图渲染
        pb002Base.viewMap();
        //不全全地址
        $("div.pageNav a.fullAddr", navTab.getCurrentPanel()).html('${buildBean.BuildInfo.buildFullAddr}');
    });
</script>
