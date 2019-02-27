<%--交房图形展示--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div layoutH="57" style="border: 1px solid #e9e9e9;">
    <div style="position: relative;width: 100%;text-align: center;">
        <c:forEach var="build" items="${builds}">
            <c:set var="ttRegId" value="${build.BuildInfo.ttRegId}${''}"/>
            <div id="b${ttRegId}" class="mart5">
                <table class="build new_build">
                    <tr>
                        <th style="border: none;min-width: inherit;">&nbsp;</th>
                    </tr>
                    <tr>
                        <th class="floor">&nbsp;</th>
                    </tr>
                    <c:set var="floorNum" value="${build.BuildInfo.floorNum}${''}"/>
                    <c:set var="minNum" value="${build.BuildInfo.minNum}${''}"/>

                    <c:forEach begin="1" end="${floorNum - minNum}" var="item">
                        <c:choose>
                            <c:when test="${item>floorNum}">
                                <tr>
                                    <td class="floor">${floorNum-item}层</td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td class="floor">${floorNum - item + 1}层</td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </table>
                <c:set var="bUnit" value="${units[ttRegId]}"/>
                <c:forEach items="${bUnit}" var="unit">
                    <table class="build new_build">
                        <tr>
                            <th class="unit" colspan="${fn:length(unit.value)}">${unit.key}单元</th>
                        </tr>
                        <tr>
                            <c:forEach items="${unit.value}" var="door">
                                <c:set var="doorKey" value="${ttRegId}_${unit.key}_${door}"/>
                                <c:set var="roomNoHsInfoItem" value="${roomNoHsInfo[doorKey]}"/>
                                <th class="floor">${door}</th>
                            </c:forEach>
                        </tr>
                        <c:forEach begin="1" end="${floorNum-minNum}" var="floor">
                            <c:choose>
                                <c:when test="${floor>floorNum}">
                                    <tr class="data">
                                        <c:forEach items="${unit.value}" var="door">
                                            <td>
                                                <c:set var="hsKey"
                                                       value="${ttRegId}_${unit.key}_${floorNum - floor}_${door}"/>
                                                <c:set var="doorKey" value="${ttRegId}_${unit.key}_${door}"/>
                                                <c:set var="house" value="${houses[hsKey]}"/>
                                                <c:set var="handleStatus"
                                                       value="${house.Row.NewHsInfo.handleStatus}${''}"/>
                                                <c:choose>
                                                    <c:when test="${house.Row.NewHsInfo.statusCd != null}">
                                                        <label style="background-color: ${newHsColorMap[handleStatus]}"
                                                               onclick="oh001.info('${house.Row.NewHsInfo.newHsId}')"
                                                               title="${house.Row.NewHsInfo.hsAddr}">
                                                                ${house.Row.NewHsInfo.hsNm}(${house.Row.NewHsInfo.buyPersonName})<br>
                                                                ${house.Row.NewHsInfo.handleStatus_Name}
                                                        </label>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <label style="background-color: ${newHsColorMap[handleStatus]}">
                                                                ${house.Row.NewHsInfo.hsNm}<br/>11
                                                        </label>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </c:forEach>
                                    </tr>
                                </c:when>
                                <c:otherwise>
                                    <tr class="data">
                                        <c:forEach items="${unit.value}" var="door">
                                            <td>
                                                <c:set var="hsKey"
                                                       value="${ttRegId}_${unit.key}_${floorNum - floor + 1}_${door}"/>
                                                <c:set var="doorKey" value="${ttRegId}_${unit.key}_${door}"/>
                                                <c:set var="house" value="${houses[hsKey]}"/>
                                                <c:set var="handleStatus"
                                                       value="${house.Row.NewHsInfo.handleStatus}${''}"/>
                                                <c:set var="chooseStatusCd"
                                                       value="${house.Row.NewHsInfo.statusCd}${''}"/>
                                                <c:choose>
                                                    <c:when test="${chooseStatusCd == '3' && (handleStatus == '2' || handleStatus == '3')}">
                                                        <label style="background-color: ${newHsColorMap[handleStatus]}"
                                                               onclick="oh007.ctDelay('${house.Row.NewHsInfo.oldHsId}','${house.Row.NewHsInfo.newHsId}')"
                                                               title="${house.Row.NewHsInfo.hsAddr}">
                                                                ${house.Row.NewHsInfo.hsNm}(${house.Row.NewHsInfo.buyPersonName})<br>
                                                                ${house.Row.NewHsInfo.handleStatus_Name}
                                                        </label>
                                                    </c:when>
                                                    <c:when test="${chooseStatusCd == '3'}">
                                                        <label style="background-color: ${newHsColorMap[handleStatus]}"
                                                               title="${house.Row.NewHsInfo.hsAddr}">
                                                                ${house.Row.NewHsInfo.hsNm}(${house.Row.NewHsInfo.buyPersonName})<br>
                                                                ${house.Row.NewHsInfo.handleStatus_Name}
                                                        </label>
                                                    </c:when>
                                                    <c:when test="${chooseStatusCd != ''}">
                                                        <label style="background-color: ${newHsColorMap[handleStatus]}"
                                                               onclick="oh001.info('${house.Row.NewHsInfo.newHsId}')"
                                                               title="${house.Row.NewHsInfo.hsAddr}">
                                                                ${house.Row.NewHsInfo.hsNm}<br>
                                                                ${house.Row.NewHsInfo.handleStatus_Name}
                                                        </label>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <label style="background-color: ${newHsColorMap[handleStatus]}">
                                                                ${house.Row.NewHsInfo.hsNm}
                                                        </label>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </c:forEach>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </table>
                </c:forEach>
            </div>
            <span style="display: inline-block;margin-bottom: 50px;">${build.BuildInfo.buildAddr}</span>
        </c:forEach>
        <div style="position: fixed; min-width: 300px; right: 30px; top: 165px;background-color: #efefef;z-index: 100;">
            <span onclick="oh00101Build.switchTip(this)"
                  style="position: absolute; top:0px; right: 0px;display: inline-block; " class="showTip"></span>
            <table class="border hidden">
                <tr>
                    <th valign="top" width="30%">
                        <label>房屋列表：</label>
                    </th>
                    <td align="left">
                        <ul>
                            <c:forEach items="${builds}" var="build" varStatus="varStatus">
                                <li style="min-width: 80px;" class="mart5">
                                    <a href="#b${build.BuildInfo.ttRegId}">${build.BuildInfo.buildAddr}</a>
                                </li>
                            </c:forEach>
                        </ul>
                    </td>
                </tr>
                <tr>
                    <th valign="top"><label>状态图例：</label></th>
                    <td align="left">
                        <ul>
                            <c:forEach items="${newHsColorList}" var="item" varStatus="varStatus">
                                <li style="min-width: 80px;" class="mart5">
                                    <div class="colorCheck">
                                        <span style="background-color: ${item.Value.valueName}">&nbsp;&nbsp;&nbsp;&nbsp;</span>
                                        <span>${item.Value.notes}</span>
                                    </div>
                                </li>
                            </c:forEach>
                        </ul>
                    </td>
                </tr>
            </table>
        </div>
    </div>
</div>
<script type="text/javascript">
    oh00101Build = {
        switchTip: function (obj) {
            var $this = $(obj);
            if ($this.hasClass("hideTip")) {
                $this.removeClass("hideTip");
                $this.addClass("showTip");
                $this.next("table").hide();
            } else {
                $this.removeClass("showTip");
                $this.addClass("hideTip");
                $this.next("table").show();
            }
        }
    }
</script>


