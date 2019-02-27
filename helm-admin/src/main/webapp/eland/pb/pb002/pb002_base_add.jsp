<%--新增建筑信息--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="pageNav">
    <a href="javascript:index.quickOpen('eland/pb/pb001/pb001-init.gv'),{opCode:'pb001-init',opName:'居民信息',fresh:true}">居民信息</a>---><a class="current">院落信息</a>---><a
        class="current">新增建筑</a>
</div>
<%--基本信息--%>
<div class="panel" style="margin: 2px 2px">
    <h1>基本信息
        <span class="panel_menu" onclick="navTab.closeCurrentTab();">取消</span>
        <span class="panel_menu" onclick="pb002Base.saveYardInfo(this, 'add');">保存</span>
    </h1>
    <%--面板内容--%>
    <div id="pb002Base_add" class="js_panel_context" style="background-color: #ffffff; min-height: 300px;">

        <div id="yardMap"
             style="clear: both; width: 54%; height: 300px; border: 1px #000000 solid;position: relative; display: inline-block; overflow: hidden;"></div>

        <div style="position: relative; display: inline-block; width: 45%; overflow: hidden; float: right;">
            <form id="pb002Base_add_frm" method="post">
                <%--基本信息--%>
                <input type="hidden" name="buildId" value="${buildId}"/>
                <%--<input type="hidden" name="buildFullAddr" value="${buildBean.BuildInfo.buildFullAddr}"/>--%>
                <%--<input type="hidden" name="buildFloorNum" value="${buildBean.BuildInfo.buildFloorNum}"/>--%>
                <%--<input type="hidden" name="buildUnitNum" value="${buildBean.BuildInfo.buildUnitNum}"/>--%>
                <%--<input type="hidden" name="buildHsNum" value="${buildBean.BuildInfo.buildHsNum}"/>--%>
                <%--管理信息--%>
                <input type="hidden" name="ttUpRegId" value="${ttRegId}"/>

                <table class="border">
                    <tr>
                        <th>建筑性质：</th>
                        <td colspan="3"><oframe:select prjCd="${param.prjCd}" itemCd="YARD_TYPE" name="buildType" value=""/></td>
                    </tr>
                    <tr>
                        <th>建筑状态：</th>
                        <td colspan="3"><oframe:select prjCd="${param.prjCd}" itemCd="YARD_STATUS" name="buildStatus" value=""/>
                        </td>
                    </tr>
                    <tr>
                        <th>占地面积：</th>
                        <td colspan="3"><input type="text" class="required" name="buildLandSize" value=""/></td>
                    </tr>
                    <tr>
                        <th style="line-height:90px;width:25%;">备注说明：</th>
                        <td style="line-height:90px;" colspan="3"><textarea cols="60" rows="6"
                                                                            name="buildNote"></textarea></td>
                    </tr>

                </table>
            </form>
        </div>
        <div>
            <ul>
                <li style="float: left; margin-left: 40px;">
                    <input type="radio" name="type" value="none" id="noneToggle"
                           onclick="pb002Base.toggleControl(this);" checked="checked"/>
                    <label for="noneToggle">导航模式</label>
                </li>
                <li style="float: left; margin-left: 40px;">
                    <input type="radio" name="type" value="polygon" id="polygonToggle"
                           onclick="pb002Base.toggleControl(this);"/>
                    <label for="polygonToggle">绘图模式</label>
                </li>
                <li style="float: left; margin-left: 40px;">
                    <input type="radio" name="type" value="select" id="selectToggle"
                           onclick="pb002Base.toggleControl(this);"/>
                    <label for="selectToggle">删除模式</label>
                </li>
                <li style="float: left; margin-left: 40px;">
                    <input type="button" onclick="pb002Base.alertDrawPoint();" value="查看图形坐标"/>
                </li>
            </ul>
        </div>
    </div>
</div>
<oframe:script src="${pageContext.request.contextPath}/eland/pb/pb002/js/pb002_base.js" type="text/javascript"/>
<script>
    $(document).ready(function () {
        //初始化地图渲染
        pb002Base.init('add');
    });
</script>
