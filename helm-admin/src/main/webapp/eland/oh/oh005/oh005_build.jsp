<%--居民房产信息检索界面--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="build_legend" style="position: fixed; right: 30px; top: 11px;z-index: 100; display: none;">
    <ul>
        <c:forEach items="${newHsColorList}" var="item" varStatus="varStatus">
            <li style="margin-right:10px;display: inline-block;">
                <div class="colorCheck">
                    <c:set var="valueCd" value="${item.Value.valueCd}${''}"/>
                    <c:if test="${'2' == valueCd || '3'== valueCd}">
                        <span style="background-color: ${item.Value.valueName}">&nbsp;&nbsp;&nbsp;&nbsp;</span>
                        <span style="font-size: 20px; font-weight: bolder;">${item.Value.notes}</span>
                    </c:if>
                </div>
            </li>
        </c:forEach>
    </ul>
</div>
<div class="new_build_box" style="width: 100%;height: 100%">
    <c:forEach var="build" items="${builds}">
        <div class="bc_build">
            <c:set var="ttRegId" value="${build.BuildInfo.ttRegId}${''}"/>
            <span class="buildTitle"
                  style="display: block;height:56px;line-height:56px;font-weight: bolder;">${build.BuildInfo.buildAddr}</span>
            <ul class="build_unit"
                style="margin:0 auto;margin-top: 11px;width: 99%;display: flex;flex-direction: row;">
                <li style="width: 99px;height:36px;line-height: 36px;"></li>
                <c:set var="bUnit" value="${units[ttRegId]}"/>
                <c:forEach items="${bUnit}" var="unit">
                    <li class="unit"
                        style="flex: ${fn:length(unit.value)};height:36px;line-height: 36px;text-align: center;">${unit.key}单元
                    </li>
                </c:forEach>
            </ul>
            <ul class="build_floor" style="margin:0 auto;width: 99%;display: flex;flex-direction: row;">
                <li class="floor" style="width: 99px;height:36px;line-height: 36px;"></li>
                <c:set var="bUnit" value="${units[ttRegId]}"/>
                <c:forEach items="${bUnit}" var="unit">
                    <c:forEach items="${unit.value}" var="door">
                        <c:set var="doorKey" value="${ttRegId}_${unit.key}_${door}"/>
                        <c:set var="roomNoHsInfoItem" value="${roomNoHsInfo[doorKey]}"/>
                        <li class="floor" style="flex: 1;height:36px;line-height: 36px;">
                                ${door}/(<span class="link"
                                               hxCd="${roomNoHsInfoItem.Row.NewHsInfo.hsHxName}"
                                               regId="${roomNoHsInfoItem.Row.NewHsInfo.hsAreaId}"
                                               onclick="oh001.opHsHx(this)">
                                        ${roomNoHsInfoItem.Row.NewHsInfo.preBldSize}㎡</span>)
                        </li>
                    </c:forEach>
                </c:forEach>
            </ul>
            <ul class="build_body" style="margin:0 auto;width: 99%;display: flex;flex-direction: row;">
                <li style="width: 100px;text-align: center;">
                    <ul style="width: 100%;flex-direction: column;display: flex;">
                        <c:set var="floorNum" value="${build.BuildInfo.floorNum}${''}"/>
                        <c:set var="minNum" value="${build.BuildInfo.minNum}${''}"/>

                        <c:forEach begin="1" end="${floorNum - minNum}" var="item">
                            <c:choose>
                                <c:when test="${item>floorNum}">
                                    <li class="floor"
                                        style="width: 100%;height:36px;line-height: 36px;">${floorNum-item}层
                                    </li>
                                </c:when>
                                <c:otherwise>
                                    <li class="floor"
                                        style="width: 100%;height:36px;line-height: 36px;">${floorNum - item + 1}层
                                    </li>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </ul>
                </li>
                <c:forEach items="${bUnit}" var="unit">
                    <c:forEach items="${unit.value}" var="door">
                        <c:set var="roomNoHsInfoItem" value="${roomNoHsInfo[doorKey]}"/>
                        <li style="flex: 1;">
                            <ul style="width: 100%;flex-direction: column;display: flex;">
                                <c:set var="floorNum" value="${build.BuildInfo.floorNum}${''}"/>
                                <c:set var="minNum" value="${build.BuildInfo.minNum}${''}"/>

                                <c:forEach begin="1" end="${floorNum-minNum}" var="floor">
                                    <c:set var="hsKey"
                                           value="${ttRegId}_${unit.key}_${floorNum - floor}_${door}"/>
                                    <c:set var="doorKey" value="${ttRegId}_${unit.key}_${door}"/>
                                    <c:set var="house" value="${houses[hsKey]}"/>
                                    <c:choose>
                                        <c:when test="${house.Row.NewHsInfo.statusCd != null}">
                                            <c:set var="hsStatus"
                                                   value="${house.Row.NewHsInfo.statusCd}${''}"/>
                                            <li class="data"
                                                style="width: 100%;height:36px;line-height: 36px;background-color: ${newHsColorMap[hsStatus]}">${house.Row.NewHsInfo.hsNm}</li>
                                        </c:when>
                                        <c:otherwise>
                                            <li class="data"
                                                style="width: 100%;height:36px;line-height: 36px;">${house.Row.NewHsInfo.hsNm}</li>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>
                            </ul>
                        </li>
                    </c:forEach>
                </c:forEach>
            </ul>
        </div>
    </c:forEach>
</div>



