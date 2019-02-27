<%--
  Created by IntelliJ IDEA.
  User: shfb_wang
  Date: 2015/8/30 0030 11:27
  Copyright(c) 北京四海富博计算机服务有限公司
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>

<table width="100%">
    <tr>
        <td style="width: 30%;">
            <oframe:select name="inputCondition" itemCd="MAP_BLD_CON" style="width:100%"/>
        </td>
        <td>
            <input type="text" name="inputConditionValue"
                   onkeydown="if(event.keyCode==13){fm004Map.loadMapData('search');}"
                   style="width: 100%;height:26px;min-width:initial;">
        </td>
        <td style="width: 45px;">
            <a title="搜索" onclick="fm004Map.loadMapData('search');" style="height: 30px;left:6px;"
               class="btnLook">搜索</a>
                <span onclick="fm004Map.queryBuild(this);" title="高级搜索" class="js-more-right"
                      style="width: 15px;cursor: pointer; text-align: center; line-height: 32px;float: right;"
                      type="button">
                </span>
        </td>
    </tr>
</table>

<table width="100%" layoutH="88">
    <c:set var="colorNum" value="0"/>
    <c:forEach items="${list}" var="item1" varStatus="varStatus1">
        <tr style="background-color: #b7d4f4; color: #020406;height: 30px;">
            <td colspan="2">
                <span class="marl10">${item1.dimension}</span>
                <c:set var="checked" value=""/>
                <c:if test="${varStatus1.index == 0}">
                    <c:set var="checked" value="checked"/>
                </c:if>
                    <span class="right marr10">
                        <input type="radio" id="id_${item1.inputName}" name="showDimension" ${checked}
                               onchange="fm004Map.loadMapData('search');"
                               value="${item1.inputName}"/>
                        <label for="id_${item1.inputName}" class="cursorpt">展示该维度</label>
                    </span>
            </td>
        </tr>

        <c:forEach items="${item1.paramName}" var="item" varStatus="varStatus">
            <c:if test="${varStatus.index % 2 == 0}">
                <tr class="js_slt_tr">
            </c:if>
            <td width="50%">
                <div class="colorCheck" onclick="fm004Search.checkCondition(this);">
                    <input type="hidden" name="${item1.inputName}" value="${item.key}"/>
                    <input name="allInputColor" type="text" value="${item1.color[colorNum]}"
                           class="hidden color-pick value"/>
                    <span class="note">${item.value}</span>
                </div>
                <c:set var="colorNum" value="${colorNum+1}"/>
            </td>
            <c:if test="${varStatus.index % 2 == 1 || varStatus.last }">
                </tr>
            </c:if>
        </c:forEach>
    </c:forEach>
</table>
<form id="fm004QueryBuildForm" class="form" method="post">
    <input type="hidden" name="entityName" value="BuildInfo"/>
    <input type="hidden" name="conditionName" value="">
    <input type="hidden" name="condition" value="">
    <input type="hidden" name="conditionValue" value="">
    <input type="hidden" name="sortColumn" value="">
    <input type="hidden" name="sortOrder" value="">
    <input type="hidden" class="js_conditionValue" value="">
    <input type="hidden" class="js_canDefResult" value="false">
    <input type="hidden" name="resultField" value="buildId">
    <%--必须包含的字段--%>
    <input type="hidden" name="forceResultField" value="buildId">
    <input type="hidden" name="forward" id="forward" value=""/>
</form>
