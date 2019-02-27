<%--居民房产信息检索界面--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div style="position: relative;width: 100%;top:10px;text-align: center;">
    <c:set var="selectedHs" value="${fn:split(param.selectedHs, ',')}"/>
    <c:if test="${fn:length(builds) == 0}">
        <span style="display: inline-block;color: red; font-size: 30px;" class="mart20">没有可用房源</span>
    </c:if>
    <div style="position: relative;width: 100%;text-align: center;">
        <c:forEach var="build" items="${builds}">
            <c:set var="ttRegId" value="${build.BuildInfo.ttRegId}${''}"/>
            <div id="b${ttRegId}" class="mart5">

                <c:set var="bUnit" value="${units[ttRegId]}"/>
                <c:forEach items="${bUnit}" var="unit" varStatus="varStatusUnit">
                    <table class="build new_build">
                        <c:set var="floorNum" value="${build.BuildInfo.floorNum}${''}"/>
                        <c:set var="minNum" value="${build.BuildInfo.minNum}${''}"/>

                        <tr>
                            <th class="unit" colspan="${fn:length(unit.value)+1}">${unit.key}单元</th>
                        </tr>
                        <tr>
                            <c:forEach items="${unit.value}" var="door" varStatus="varStatusDoor">
                                <c:set var="doorKey" value="${ttRegId}_${unit.key}_${door}"/>
                                <c:set var="roomNoHsInfoItem" value="${roomNoHsInfo[doorKey]}"/>
                                <c:if test="${varStatusUnit.index == 0 && varStatusDoor.index == 0}">
                                    <th class="floor" style="height: 45px;">层</th>
                                </c:if>

                                <td class="floor">${door}<br/>(<span>${roomNoHsInfoItem.Row.NewHsInfo.hsHxName}</span>&nbsp;${roomNoHsInfoItem.Row.NewHsInfo.preBldSize})
                                </td>
                            </c:forEach>
                        </tr>

                        <c:forEach begin="1" end="${floorNum-minNum}" var="floor" varStatus="varStatusFloor">
                            <c:choose>
                                <c:when test="${floor > floorNum}">
                                    <tr class="data">
                                        <c:forEach items="${unit.value}" var="door">
                                            <c:if test="${varStatusUnit.index == 0 && varStatusDoor1.index == 0}">
                                                <td>${floorNum - floor}</td>
                                            </c:if>
                                            <td>
                                                <c:set var="hsKey"
                                                       value="${ttRegId}_${unit.key}_${floorNum - floor}_${door}"/>
                                                <c:set var="house" value="${houses[hsKey]}"/>
                                                <c:set var="doorKey" value="${ttRegId}_${unit.key}_${door}"/>
                                                <c:choose>
                                                    <c:when test="${house.Row.statusCd != null}">
                                                        <c:set var="hsStatus"
                                                               value="${house.Row.statusCd}${''}"/>
                                                        <label style="background-color: ${newHsColorMap[hsStatus]}"
                                                               title="${house.Row.hsAddr}">
                                                            &nbsp;${house.Row.hsNm} &nbsp;<br>
                                                            <c:set var="roomNoHsInfoItem"
                                                                   value="${roomNoHsInfo[doorKey]}"/>
                                                            <c:set var="tHsHxName"
                                                                   value="${roomNoHsInfoItem.Row.NewHsInfo.hsHxName}${''}"/>
                                                            <c:set var="tHsBldSize"
                                                                   value="${roomNoHsInfoItem.Row.NewHsInfo.preBldSize}${''}"/>
                                                            <c:set var="nHsHxName"
                                                                   value="${house.Row.NewHsInfo.hsHxName}${''}"/>
                                                            <c:set var="nHsBldSize"
                                                                   value="${house.Row.NewHsInfo.preBldSize}${''}"/>
                                                            <c:if test="${tHsHxName != nHsHxName
                                                                    || tHsBldSize != nHsBldSize}">
                                                                (${house.Row.NewHsInfo.hsHxName}&nbsp;${house.Row.NewHsInfo.preBldSize}})
                                                            </c:if>
                                                        </label>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <label> &nbsp;${house.Row.hsNm}&nbsp;</label>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </c:forEach>
                                    </tr>
                                </c:when>
                                <c:otherwise>
                                    <tr class="data">
                                        <c:forEach items="${unit.value}" var="door" varStatus="varStatusDoor1">
                                            <c:if test="${varStatusUnit.index == 0 && varStatusDoor1.index == 0}">
                                                <th class="floor">${floorNum - floor + 1}</th>
                                            </c:if>
                                            <td>
                                                <c:set var="hsKey"
                                                       value="${ttRegId}_${unit.key}_${floorNum - floor + 1}_${door}"/>
                                                <c:set var="house" value="${houses[hsKey]}"/>
                                                <c:set var="doorKey" value="${ttRegId}_${unit.key}_${door}"/>
                                                <c:choose>
                                                    <c:when test="${house.Row.NewHsInfo.statusCd != null}">
                                                        <c:set var="hsStatus"
                                                               value="${house.Row.NewHsInfo.statusCd}${''}"/>
                                                        <label style="background-color: ${newHsColorMap[hsStatus]}"
                                                               title="${house.Row.NewHsInfo.hsAddr}">
                                                            &nbsp;${house.Row.NewHsInfo.hsNm}&nbsp;<br>
                                                            <c:set var="roomNoHsInfoItem"
                                                                   value="${roomNoHsInfo[doorKey]}"/>
                                                            <c:set var="tHsHxName"
                                                                   value="${roomNoHsInfoItem.Row.NewHsInfo.hsHxName}${''}"/>
                                                            <c:set var="tHsBldSize"
                                                                   value="${roomNoHsInfoItem.Row.NewHsInfo.preBldSize}${''}"/>
                                                            <c:set var="nHsHxName"
                                                                   value="${house.Row.NewHsInfo.hsHxName}${''}"/>
                                                            <c:set var="nHsBldSize"
                                                                   value="${house.Row.NewHsInfo.preBldSize}${''}"/>
                                                            <c:if test="${tHsHxName != nHsHxName
                                                                    || tHsBldSize != nHsBldSize}">
                                                                (${house.Row.NewHsInfo.hsHxName}&nbsp;${house.Row.NewHsInfo.preBldSize})
                                                            </c:if>
                                                        </label>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <label>${house.Row.NewHsInfo.hsNm}</label>
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
            <span style="display: inline-block;margin: 20px 0; font-weight: bolder;">${build.BuildInfo.buildAddr}</span>
        </c:forEach>
    </div>
    <div id="statusShow"
         style="position: fixed; min-width: 240px; right: 30px; top: 10px;background-color:rgba(228, 228, 230, 0.7);padding:10px;z-index: 100;">
        <span onclick="ch00101Build.switchTip(this)"
              style="position: absolute; top:0px; right: 0px;display: inline-block;" class="hideTip"></span>
        <table class="border">
            <tr>
                <th valign="top" width="45%">
                    <label>楼座列表：</label>
                </th>
                <td align="left">
                    <ul style="max-height: 250px;overflow-y: auto;">
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

<script type="text/javascript">
    var ch00101Build = {
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
        },

        /**
         * 左侧区域树点击事件
         */
        opHsHx: function (obj) {
            var regUseType = $("input[name=regUseType]", navTab.getCurrentPanel()).val();
            var $this = $(obj);
            var hxCd = $this.attr("hxCd");
            var regId = $this.attr("regId");
            url = getGlobalPathRoot() + "eland/pj/pj008/pj008-info.gv?regId=" + regId
                    + "&hxCd=" + hxCd
                    + "&regUseType=" + regUseType
                    + "&prjCd=" + getPrjCd();
            $.pdialog.open(url, "pj00801", "户型信息", {mask: true, max: false, height: 600, width: 850});
        }
    }
</script>


