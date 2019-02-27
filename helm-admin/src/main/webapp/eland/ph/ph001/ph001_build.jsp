<%@ page import="java.util.Map" %>
<%@ page import="com.shfb.oframe.core.util.common.XmlNode" %>
<%--人地房房产信息检索界面--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div layoutH="57" style="border: 1px solid #e9e9e9;">
    <div style="position: relative;width: 100%;display: table;">
        <div style="display:table-cell;vertical-align: middle;text-align: center;padding-top: 80px">
            <table class="build">
                <tr>
                    <th style="border: none;min-width: auto;">&nbsp;</th>
                </tr>
                <tr>
                    <th class="floor">&nbsp;</th>
                </tr>
                <c:forEach begin="1" end="${floorNum-minNum}" var="item">
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
            <c:forEach items="${units}" var="unit">
                <table class="build">
                    <tr>
                        <th class="unit" colspan="${fn:length(unit.value)}">${unit.key}</th>
                    </tr>
                    <tr>
                        <c:forEach items="${unit.value}" var="door">
                            <th class="floor">${door}</th>
                        </c:forEach>
                    </tr>

                    <c:forEach begin="1" end="${floorNum-minNum}" var="floor">
                        <c:choose>
                            <c:when test="${floor>floorNum}">
                                <tr class="data">
                                    <c:forEach items="${unit.value}" var="door">
                                        <td>
                                            <c:set var="hsKey" value="${unit.key}_${floorNum - floor}_${door}"/>
                                            <c:set var="house" value="${houses[hsKey]}"/>
                                            <c:choose>
                                                <c:when test="${house.Row.hsOwnerPersons != null}">
                                                    <c:set var="hsStatus" value="${house.Row.hsStatus}${''}"/>
                                                    <label style="background-color: ${oldHsColorMap[hsStatus]}"
                                                           onclick="ph001.viewHouse('${house.Row.hsId}')"
                                                           title="${house.Row.hsOwnerPersons}:${house.Row.hsFullAddr}">
                                                            ${house.Row.hsAddrTail}<br/>${house.Row.hsOwnerPersons}
                                                    </label>
                                                </c:when>
                                                <c:otherwise>
                                                    <label>${house.Row.hsAddrTail}</label>
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
                                            <c:set var="hsKey" value="${unit.key}_${floorNum - floor + 1}_${door}"/>
                                            <c:set var="house" value="${houses[hsKey]}"/>
                                            <c:choose>
                                                <c:when test="${house.Row.hsOwnerPersons != null}">
                                                    <c:set var="hsStatus" value="${house.Row.hsStatus}${''}"/>
                                                    <label style="background-color: ${oldHsColorMap[hsStatus]}"
                                                           onclick="ph001.viewHouse('${house.Row.hsId}')"
                                                           title="${house.Row.hsOwnerPersons}:${house.Row.hsFullAddr}">
                                                            ${house.Row.hsAddrTail}<br/>${house.Row.hsOwnerPersons}
                                                    </label>
                                                </c:when>
                                                <c:otherwise>
                                                    <label>${house.Row.hsAddrTail}</label>
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
        <div style="position: fixed; width: 300px; right: 30px; top: 160px;background-color: #efefef;z-index: 100;">
            <span onclick="ph001Build.switchTip(this)"
                  style="position: absolute; top:0px; right: 0px;display: inline-block; " class="showTip"></span>
            <table class="border hidden" >
                <tr>
                    <th width="30%"><label>建筑地址：</label></th>
                    <td>${buildInfo.Row.buildFullAddr}</td>
                </tr>
                <%--<tr>--%>
                    <%--<th><label>总户数：</label></th>--%>
                    <%--<td>${buildInfo.Row.buildHsNum}</td>--%>
                <%--</tr>--%>
                <%--<tr>--%>
                    <%--<th><label>录入户数：</label></th>--%>
                    <%--<td>${buildInfo.Row.hsCount}</td>--%>
                <%--</tr>--%>
                <%--<tr>--%>
                    <%--<th><label>占地面积：</label></th>--%>
                    <%--<td>${buildInfo.Row.buildLandSize}</td>--%>
                <%--</tr>--%>
                <tr>
                    <th valign="top"><label>状态图例：</label></th>
                    <td>
                        <ul>
                            <c:forEach items="${oldHsStatus}" var="item" varStatus="varStatus">
                                <li style="min-width: 80px;" class="mart5">
                                    <div class="colorCheck">
                                        <span style="background-color: ${oldHsColorMap[item.key]}">&nbsp;&nbsp;&nbsp;&nbsp;</span>
                                        <span>${item.value}</span>
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
    ph001Build = {
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
        showHsHideMenu: function (clickObj) {
            var pageNavMenu = $("#ph001_hsPageNav");
            if (pageNavMenu.hasClass("hidden")) {
                pageNavMenu.removeClass("hidden");
                $(clickObj).addClass("disabled");
            } else {
                pageNavMenu.addClass("hidden");
                $(clickObj).removeClass("disabled");
            }
            $("#ph001_pageNav").addClass("hidden");
        }
    }
</script>


