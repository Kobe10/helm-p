<%--建筑信息基本信息面板--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="panel">
    <h1>基本信息
        <span class="panel_menu js_reload">取消</span>
        <span class="panel_menu" onclick="pb00201.saveYardInfo(this,'edit');">保存</span>
    </h1>
    <%--面板内容--%>
    <div id="pb00201_edit" class="js_panel_context" style="background-color: #ffffff; min-height: 300px;">

        <div id="yardMap"
        style="clear: both; width: 54%; height: 300px; border: 1px #000000 solid;position: relative; display: inline-block; overflow: hidden;"></div>

        <div style="position: relative; display: inline-block; width: 45%; overflow: hidden; float: right;">
            <form id="pb00201_edit_frm" method="post">
            <%--基本信息--%>
                <input type="hidden" name="buildId" value="${buildId}"/>
                <input type="hidden" name="buildFullAddr" value="${buildBean.BuildInfo.buildFullAddr}"/>
                <input type="hidden" name="buildFloorNum" value="${buildBean.BuildInfo.buildFloorNum}"/>
                <input type="hidden" name="buildUnitNum" value="${buildBean.BuildInfo.buildUnitNum}"/>
                <input type="hidden" name="buildHsNum" value="${buildBean.BuildInfo.buildHsNum}"/>
                <input type="hidden" name="buildPosition" value="${buildBean.BuildInfo.buildPosition}"/>

                <input type="hidden" name="baselayerUrl" value="${baselayerUrl}"/>
                <input type="hidden" name="layerName1" value="${layerName1}"/>
                <input type="hidden" name="layerName2" value="${layerName2}"/>

                <table class="border">
                    <tr>
                        <th style="width:25%;">胡同/街道：</th>
                        <td style="width:50%;"><input type="text" name="buildAddr" style="width:97%;" class="required" value="${buildBean.BuildInfo.buildAddr}"/></td>
                        <th style="width:10%;">门牌：</th>
                        <td><input type="text" name="buildAddrNo" style="width:60%;"  class="required" value="${buildBean.BuildInfo.buildNo}"/>号</td>
                    </tr>
                    <tr>
                        <th>建筑类型：</th>
                        <td colspan="3"><oframe:select prjCd="${param.prjCd}" itemCd="BUILD_TYPE" name="buildType" style="width:81%"
                                                       value="${buildBean.BuildInfo.buildType}"/></td>
                    </tr>
                    <tr>
                        <th>建筑状态：</th>
                        <td colspan="3">
                            <oframe:select prjCd="${param.prjCd}" itemCd="YARD_STATUS" name="buildStatus" style="width:81%"
                                           value="${buildBean.BuildInfo.buildStatus}"/>
                        </td>
                    </tr>
                    <tr>
                        <th>占地面积：</th>
                        <td colspan="3"><input type="text" name="buildLandSize"
                                               value="${buildBean.BuildInfo.buildLandSize}"/></td>
                    </tr>
                    <tr>
                        <th style="line-height:90px;width:25%;">备注说明：</th>
                        <td style="line-height:90px;" colspan="3"><textarea cols="60" rows="6" name="buildNote">${buildBean.BuildInfo.buildNote}</textarea></td>
                    </tr>
                </table>
            </form>
        </div>
        <div>
            <ul>
                <li style="float: left; margin-left: 40px;">
                    <input type="radio" name="checkModeltype" value="none" id="noneToggle"
                           onclick="pb00201.toggleControl(this);" checked="checked"/>
                    <label for="noneToggle">导航模式</label>
                </li>
                <li style="float: left; margin-left: 40px;">
                    <input type="radio" name="checkModeltype" value="polygon" id="polygonToggle"
                           onclick="pb00201.toggleControl(this);"/>
                    <label for="polygonToggle">修改模式</label>
                </li>
                <li style="float: left; margin-left: 40px;">
                    <input type="radio" name="checkModeltype" value="select" id="selectToggle"
                           onclick="pb00201.toggleControl(this);"/>
                    <label for="selectToggle">删除模式</label>
                </li>
                <li style="float: left; margin-left: 40px;">
                    <input type="button" onclick="pb00201.alertDrawPoint();" value="查看图形坐标"/>
                </li>
            </ul>
        </div>
    </div>
</div>
<oframe:script src="${pageContext.request.contextPath}/eland/pb/pb002/js/pb002_base.js" type="text/javascript"/>
<script>
    $(document).ready(function () {
        //初始化地图渲染
        pb002Base.init('edit');
    });
</script>
