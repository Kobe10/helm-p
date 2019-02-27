<%--建筑信息基本信息面板--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%--院落信息--%>
<div class="panel collapse mart5">
    <h1><span>院落信息</span>
        <c:if test="${procReadonly}">
            <span class="link marl10" onclick="ph00301_input.setBuildPos();">院落标注</span>
        </c:if>
    </h1>
    <%--面板内容--%>
    <div class="js_panel_context">
        <table class="border" width="100%">
            <tr>
                <th width="10%">
                    <label>院落类型：</label>
                </th>
                <td width="25%">
                    <oframe:select prjCd="${param.prjCd}" name="buildType"  itemCd="BUILD_TYPE"
                                   oldValue="${buildInfo.OpResult.buildType_old}"
                                   value="${buildInfo.OpResult.buildType}"/>
                </td>
                <th width="10%"><label>院落占地：</label></th>
                <td>
                    <oframe:input type="text" name="buildLandSize" cssClass="number ctRequired"
                                  oldValue="${buildInfo.OpResult.buildLandSize_old}"
                                  value="${buildInfo.OpResult.buildLandSize}"/>
                </td>
                <th width="10%"><label>总房产数：</label></th>
                <td>
                    <oframe:input type="text" name="buildHsNum" cssClass="number"
                                  oldValue="${buildInfo.OpResult.buildHsNum_old}"
                                  value="${buildInfo.OpResult.buildHsNum}"/>
                    <%--建筑坐标中心--%>
                    <input type="hidden" id="ph00301_01_buildPosition" name="buildPosition"
                           value="${buildInfo.OpResult.buildPosition}">
                    <input type="hidden" id="buildPositionX" name="buildPositionX"
                           value="${buildInfo.OpResult.buildPositionX}">
                    <input type="hidden" id="buildPositionY" name="buildPositionY"
                           value="${buildInfo.OpResult.buildPositionY}">
                </td>
            </tr>
            <tr>
                <th>
                    <label>拆迁公司：</label>
                </th>
                <td>
                    <oframe:select prjCd="${param.prjCd}" collection="${cmpMap}" name="ttCompanyId"

                                   onChange="ph00301_input.syncBuildToHs()"
                                   oldValue="${buildInfo.OpResult.ttCompanyId}"
                                   value="${buildInfo.OpResult.ttCompanyId}"/>
                </td>
                <th>
                    <label>主谈人员：</label>
                </th>
                <td>
                    <oframe:input type="text" name="ttMainTalk"
                                  onchange="ph00301_input.syncBuildToHs()"
                                  cssClass="ctRequired"
                                  oldValue="${buildInfo.OpResult.ttMainTalk_old}"
                                  value="${buildInfo.OpResult.ttMainTalk}"/>
                </td>
                <th>
                    <label>副谈人员：</label>
                </th>
                <td>
                    <oframe:input type="text" name="ttSecTalk"
                                  onchange="ph00301_input.syncBuildToHs()"
                                  cssClass="ctRequired"
                                  oldValue="${buildInfo.OpResult.ttSecTalk_old}"
                                  value="${buildInfo.OpResult.ttSecTalk}"/>
                </td>
            </tr>
            <tr style="height: 80px;">
                <th><label>备注说明：</label></th>
                <td colspan="5">
                    <oframe:textarea name="buildNote" style="height: 100%; width: 80%;" rows="5"
                                     oldValue="${buildInfo.OpResult.buildNote_old}"
                                     value="${buildInfo.OpResult.buildNote}"/>
                </td>
            </tr>
        </table>
    </div>
</div>