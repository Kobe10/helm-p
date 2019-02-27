<%--
  Created by IntelliJ IDEA.
  User: shfb_wang
  Date: 2016/5/11 0011 17:39
  Copyright(c) 北京四海富博计算机服务有限公司
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<div class="mar5">
    <%--左侧导航--%>
    <div id="wf007Left" layoutH="15" class="accordion panel left_menu" extendAll="true"
         style="border: 1px solid #e4e4e4;width: 240px;float: left;position: relative;overflow: hidden;">

        <div class="accordion" extendAll="true">
            <c:forEach items="${poolMap}" var="pool" varStatus="varStatus">
                <c:set var="poolVal" value="${pool.value}"/>
                <div class="accordionHeader">
                    <h2 class="collapsable">
                        <label>${pool.key}</label>
                        <label id="${pool.key}"
                               class="summary_data countTodoNum">(${poolVal.Area.active}/${poolVal.Area.finished})</label>
                    </h2>
                </div>
                <div class="accordionContent">
                    <ul class="accordion_menu">
                        <c:set var="poolItem" value="${poolItemMap[pool.key]}"/>
                        <c:forEach items="${poolItem}" var="poItem" varStatus="varStatus">
                            <li onclick="wf007.sltFlowLink(this,'${procKey}','${poItem.item.id}');"
                                isStart="${poItem.item.isStart}">${poItem.item.name}
                                <span style="color: red"
                                      title="（待办：${poItem.item.active} / 已完成：${poItem.item.finished}）">(${poItem.item.active}/${poItem.item.finished})</span>
                            </li>
                        </c:forEach>
                    </ul>
                </div>
            </c:forEach>
        </div>
    </div>
    <div class="split_line" layoutH="15">
        <span class="split_but" title="展开/隐藏导航"></span>
    </div>
    <%--右侧自定义画板--%>
    <div id="wf007Right" style="margin-left: 250px;margin-right: 5px;position: relative;">
        <div class="js_query_condition" style="position: relative;">
            <div class="panelBar">
                <ul class="toolBar">
                    <li onclick="wf007.openQSch(this);">
                        <a class="find" href="javascript:void(0)"><span>条件检索</span></a>
                    </li>
                    <li onclick="wf007.refresh();">
                        <a class="reflesh" href="javascript:void(0)"><span>刷新检索</span></a>
                    </li>


                    <li onclick="wf007.changeActive(this, 0);" style="float: right" class="hidden">
                        <a class="list" href="javascript:void(0)"><span>已完成</span></a>
                    </li>
                    <li onclick="wf007.changeActive(this, 1);" style="float: right" class="hidden">
                        <a class="list" href="javascript:void(0)"><span class="active">待办</span></a>
                    </li>
                </ul>
            </div>

            <%--流程条件区--%>
            <input type="hidden" name="isStart" value="1"/>
            <input type="hidden" name="isActive" value="1"/>
            <input type="hidden" name="wfTaskFlag" value=""/>
            <input type="hidden" name="procDefKey" value=""/>
            <input type="hidden" name="taskDefId" value=""/>
            <input type="hidden" name="procKey" value="${procKey}"/>

            <%--查询条件区--%>
            <div id="wf007QSchDialog" class="hidden"
                 style="position: absolute;width:100%;top: 0;z-index: 100;background-color: #ffffff; margin-top: 40px;">
                <div class="triangle triangle-up" style="border-bottom-color: #2c7bae;top: -10px; left: 25px;"></div>
                <table class="border">
                    <tr>
                        <th><label>被安置人：</label></th>
                        <td><input name="HouseInfo.hsOwnerPersons" condition="like" class="textInput"/></td>
                        <th><label>房屋地址：</label></th>
                        <td><input name="HouseInfo.hsFullAddr" condition="like" class="textInput"/></td>
                        <th><label>档案编号：</label></th>
                        <td><input name="HouseInfo.hsCd" condition="like" class="textInput"/></td>
                    </tr>
                    <tr>
                        <td colspan="6" align="center">
                            <button onclick="wf007.sltFlowLink();" class="btn btn-primary js_query marr20">查询</button>
                            <button onclick="wf007.closeQSch(true);" class="btn btn-info">关闭</button>
                        </td>
                    </tr>
                </table>
            </div>
        </div>

        <form id="wf007queryForm">
            <input type="hidden" name="entityName" value="HouseInfo"/>
            <input type="hidden" name="sortColumn" value="HouseInfo.hsCd">
            <input type="hidden" name="sortOrder" value="asc">
            <input type="hidden" name="conditionName" value="${conditions.Condition.conditionNames}">
            <input type="hidden" name="condition" value="${conditions.Condition.conditions}">
            <input type="hidden" name="conditionValue" value="${conditions.Condition.conditionValues}">
            <input type="hidden" class="js_conditionValue" value="">
            <input type="hidden" name="forceResultField"
                   value="HouseInfo.hsId,HouseInfo.HsCtInfo.hsCtId,HouseInfo.ttRegId">
            <input type="hidden" name="divId" value="wf007Context">
            <input type="hidden" name="resultField"
                   value="HouseInfo.hsCd,HouseInfo.hsFullAddr,HouseInfo.hsOwnerPersons,HouseInfo.hsOwnerType,HouseInfo.hsUseSize,HouseInfo.hsBuildSize,HouseInfo.ttOrgId">
            <input type="hidden" name="taskResultField" value="procInsId"/>
            <input type="hidden" name="forward" id="forward" value="/oframe/wf/wf007/wf007_list"/>
            <input type="hidden" name="cmptName" id="cmptName" value="TASK_PRIVI_FILTER"/>
        </form>
        <div id="wf007Context" class="js_page" layoutH="55" style="position: relative;width: 100%;"></div>
    </div>
</div>
<oframe:script src="${pageContext.request.contextPath}/oframe/wf/wf007/js/wf007.js" type="text/javascript"/>