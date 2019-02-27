<%--建筑信息基本信息面板--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%--院落信息--%>
<c:set var="closeTemp" value="close"/>
<c:if test="${buildInfo.OpResult == '' || buildInfo.OpResult == null}">
    <c:set var="closeTemp" value=""/>
</c:if>
<div class="panel collapse mart5 ${closeTemp}">
    <h1><span>院落信息</span></h1>
    <%--面板内容--%>
    <div class="js_panel_context">
        <table class="border" width="100%">
            <tr>
                <th width="10%"><label>建筑类型：</label></th>
                <td><oframe:select prjCd="${param.prjCd}" itemCd="BUILD_TYPE" name="buildType" value="${buildInfo.OpResult.buildType}"/></td>
                <th width="10%"><label>建筑占地：</label></th>
                <td><input type="text" name="buildLandSize" class="number ctRequired"
                           value="${buildInfo.OpResult.buildLandSize}"></td>
                <%--建筑坐标中心--%>
                <input type="hidden" id="ph00301_01_buildPosition" name="buildPosition"
                       value="${buildInfo.OpResult.buildPosition}">
                <input type="hidden" id="buildPositionX" name="buildPositionX"
                       value="${buildInfo.OpResult.buildPositionX}">
                <input type="hidden" id="buildPositionY" name="buildPositionY"
                       value="${buildInfo.OpResult.buildPositionY}">
                <th width="10%"><label>位置标记：</label></th>
                <td><span class="link marl10" onclick="ph00301_input.setHsPos();">房屋位置</span></td>
            </tr>
            <tr>
                <th><label>中介公司：</label></th>
                <td><oframe:select prjCd="${param.prjCd}" collection="${cmpMap}" name="ttCompanyId"
                                   value="${buildInfo.OpResult.ttCompanyId}" /></td>
                <th><label>主谈人员：</label></th>
                <td><input type="text" name="ttMainTalk" class="ctRequired" value="${buildInfo.OpResult.ttMainTalk}"></td>
                <th><label>副谈人员：</label></th>
                <td><input type="text" name="ttSecTalk" class="ctRequired" value="${buildInfo.OpResult.ttSecTalk}"></td>
            </tr>
            <tr style="height: 80px;">
                <th><label>备注说明：</label></th>
                <td colspan="5"><textarea name="buildNote" style="height: 100%; width: 80%;"
                                          rows="5">${buildInfo.OpResult.buildNote}</textarea>
                </td>
            </tr>
        </table>
    </div>
</div>