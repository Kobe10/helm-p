<%--
  Created by IntelliJ IDEA.
  User: shfb_wang
  Date: 2015/8/30 0030 11:22
  Copyright(c) 北京四海富博计算机服务有限公司
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>

<table width="100%">
    <tr>
        <td>
            <oframe:select name="inputCondition" itemCd="LIST_QUERY_CON" style="width:100%"/>
        </td>
        <td>
            <input type="text" name="inputConditionValue" placeholder="输入内容进行数据定位"
                   onkeydown="if(event.keyCode==13){ph005Map.loadMapData('pos');}"
                   style="width: 100%;height:26px;min-width:initial;">
        </td>
        <td style="width: 45px;">
            <a title="搜索" onclick="ph005Map.loadMapData('pos');" style="height: 30px;left:6px;"
               class="btnLook">搜索</a>
            <span onclick="ph005Map.queryHs(this);" title="高级搜索" class="js-more-right"
                  style="width: 15px;cursor: pointer; text-align: center; line-height: 32px;float: right;"
                  type="button">
            </span>
        </td>
    </tr>
</table>
<table width="100%" layoutH="93">
    <c:set var="colorNum" value="0"/>
    <c:forEach items="${list}" var="item1" varStatus="varStatus1">
        <tr style="background-color:#e6f2fa; color: #020406;height: 30px;">
            <td colspan="2">
                <span class="marl10">${item1.dimension}</span>
                <c:set var="checked" value=""/>
                <c:if test="${varStatus1.index == 0}">
                    <c:set var="checked" value="checked"/>
                </c:if>
                <span class="right marr10">
                        <input type="radio" id="id_${item1.inputName}" name="showDimension" ${checked}
                               onchange="ph005Map.changeDisp('search');"
                               value="${item1.inputName}"/>
                        <label for="id_${item1.inputName}" class="cursorpt">展示维度</label>
                    </span>
            </td>
        </tr>
        <c:forEach items="${item1.paramName}" var="item" varStatus="varStatus">
            <c:if test="${varStatus.index % 2 == 0}">
                <tr class="js_slt_tr">
            </c:if>
            <%-- 获取当前颜色  $('input[name=allInputColor]',$(this)).val() --%>
            <td width="50%">
                <div class="colorCheck" onclick="ph005Search.checkCondition(this);">
                    <input type="hidden" name="${item1.inputName}" value="${item.key}"/>
                    <input name="allInputColor" stopClick="true" onchange="ph005Search.changeColor()"
                           type="text" value="${item1.color[varStatus.index]}"
                           class="hidden color-pick value"/>
                    <span class="note">${item.value}</span>
                </div>
            </td>
            <c:if test="${varStatus.index % 2 == 1 || varStatus.last }">
                </tr>
            </c:if>
        </c:forEach>
    </c:forEach>
</table>
<form id="ph005QueryHsForm" class="form" method="post">
    <input type="hidden" name="entityName" value="HouseInfo"/>
    <input type="hidden" name="conditionName" value="${conditions.Condition.conditionNames}">
    <input type="hidden" name="condition" value="${conditions.Condition.conditions}">
    <input type="hidden" name="conditionValue" value="${conditions.Condition.conditionValues}">
    <input type="hidden" name="sortColumn" value="">
    <input type="hidden" name="sortOrder" value="">
    <input type="hidden" class="js_conditionValue" value="">
    <input type="hidden" class="js_canDefResult" value="false">
    <input type="hidden" name="resultField" value="HouseInfo.hsId">
    <%--必须包含的字段--%>
    <input type="hidden" name="forceResultField" value="HouseInfo.hsId">
    <input type="hidden" name="forward" id="forward" value=""/>
</form>
