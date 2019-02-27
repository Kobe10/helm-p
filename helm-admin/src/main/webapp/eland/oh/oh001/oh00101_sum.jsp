<%--人地房房产信息检索界面--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%--房源统计信息--%>
<div style="border: 1px solid #e9e9e9;text-align: center;" layoutH="57">
    <c:forEach var="item" items="${hsTpHxNames}">
        <div style="width: ${100/hsTpCount-0.5}%;display: inline-block;" class="marb5">
            <table class="border">
                <tr>
                    <td colspan="8" align="center" style="background-color:#f7f7f7;">
                        <oframe:name prjCd="${param.prjCd}" itemCd="HS_ROOM_TYPE" value="${item.key}"/>
                    </td>
                </tr>
                <tr>
                    <td align="center" style="background-color:#ffffd7;">户型</td>
                    <c:forEach var="newHs" items="${newHsStatus}">
                        <td align="center" style="background-color:#ffffd7;">${newHs.value}</td>
                    </c:forEach>
                </tr>

                <c:set var="rowCount" value="0"/>
                <c:forEach items="${item.value}" var="itemHsTp">
                    <%--eveRowCount该户型下所有状态总数--%>
                    <c:set var="rowCount" value="${rowCount + 1}"/>
                    <tr>
                        <td align="center">${itemHsTp}</td>
                            <%--根据状态来显示不同状态下的数--%>
                        <c:forEach var="newHs" items="${newHsStatus}" varStatus="varStatus">
                            <td align="center">
                                    <%--居室-户型名称-状态--%>
                                <c:set var="hxKey" value="${item.key}-${itemHsTp}-${newHs.key}"/>
                                <c:choose>
                                    <c:when test="${hsTpValue[hxKey] != null}">
                                        <c:out value="${hsTpValue[hxKey] }"/>
                                    </c:when>
                                    <c:otherwise>0</c:otherwise>
                                </c:choose>
                            </td>
                        </c:forEach>
                    </tr>
                </c:forEach>
                <tr>
                    <td align="right">总计：</td>
                    <c:forEach var="newHs" items="${newHsStatus}" varStatus="varStatus">
                        <td align="center">
                            <c:set var="totalKey" value="${item.key}-${newHs.key}"/>
                            <c:choose>
                                <c:when test="${hsTpSummary[totalKey] != null}">
                                    <c:out value="${hsTpSummary[totalKey]}"/>
                                </c:when>
                                <c:otherwise>
                                    0
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </c:forEach>
                </tr>

                <c:forEach var="temp" begin="0" end="${maxTpCount - rowCount}" step="1" varStatus="status">
                    <tr>
                        <td align="center">&nbsp;</td>
                        <c:forEach var="newHs" items="${newHsStatus}" varStatus="varStatus">
                            <td align="center"></td>
                        </c:forEach>
                    </tr>
                </c:forEach>
            </table>
        </div>
    </c:forEach>
    <div id="oh001Rpt" style="border: 1px solid #e9e9e9;">
    <c:forEach items="${list}" var="item" varStatus="varStatus">
        <div class="js_sum" id="oh001Rpt_00${varStatus.index}" style="position: relative;" chartType="${item.chartType}">
            <span class="link js_item_reload" style="position: absolute; top: 10px;right: 20px; display: inline-block;">
                刷新
            </span>

            <form>
                <input type="hidden" name="rptName" value="${item.rptName}">
                <input type="hidden" name="xAttrs" value="${item.xAttrs}">
                <input type="hidden" name="extConditionName" value="${item.extConditionName}">
                <input type="hidden" name="extCondition" value="${item.extCondition}">
                <input type="hidden" name="extConditionValue" value="${item.extConditionValue}">
                <input type="hidden" name="resultAttrsName" value="${item.resultAttrsName}">
                <input type="hidden" name="resultAttrsType" value="${item.resultAttrsType}">
                <input type="hidden" name="orderAttrsName" value="${item.orderAttrsName}">
                <input type="hidden" name="orderAttrsOrder" value="${item.orderAttrsOrder}">
            </form>
            <div class="js_sum_rslt"></div>
        </div>
    </c:forEach>
</div>
<script type="text/javascript">
    var oh001_rpt = {
        loadPic: function (containDivObj) {
            var $this = $(containDivObj);
            // 查询参数
            var url = getGlobalPathRoot() + "eland/rp/rp000/rp000-entitySummary.gv";
            var packet = new AJAXPacket(url);
            var extForm = $("form", $this);
            var jsonData = extForm.serializeArray();
            jsonData.push({name: "toContainer", value: $this.attr("id")});
            jsonData.push({name: "chartType", value: $this.attr("chartType")});
            jsonData.push({name: "prjCd", value: getPrjCd()});
            // 获取扩展查询条件
            var extConditionName = $("input[name=extConditionName]", extForm).val();
            var extCondition = $("input[name=extCondition]", extForm).val();
            var extConditionValue = $("input[name=extConditionValue]", extForm).val();

            // 查询条件
            var formObj = $("#oh00101frm", navTab.getCurrentPanel());
            var entityName = $("input[name=entityName]", formObj).val();
            var conditionNames = $("input[name=conditionName]", formObj).val();
            var conditions = $("input[name=condition]", formObj).val();
            var conditionValues = $("input[name=conditionValue]", formObj).val();
            // 获取扩展查询条件
            if (extConditionName) {
                if (conditionNames != "") {
                    conditionNames = extConditionName + "," + conditionNames;
                    conditions = extCondition + "," + conditions;
                    conditionValues = extConditionValue + "," + conditionValues;
                } else {
                    conditionNames = extConditionName;
                    conditions = extCondition;
                    conditionValues = extConditionValue;
                }
            }
            // 排序字段
            var orderAttrsName = $("input[name=sortColumn]", formObj).val();
            var orderAttrsOrder = $("input[name=sortOrder]", formObj).val();

            var regUseType = $("input[name=regUseType]", formObj).val();
            var rhtRegId = $("input[name=rhtRegId]", formObj).val();
            jsonData.push({name: "entityName", value: entityName});
            jsonData.push({name: "conditionName", value: conditionNames});
            jsonData.push({name: "condition", value: conditions});
            jsonData.push({name: "conditionValue", value: conditionValues});
            jsonData.push({name: "orderAttrsName", value: orderAttrsName});
            jsonData.push({name: "orderAttrsOrder", value: orderAttrsOrder});
            jsonData.push({name: "regUseType", value: regUseType});
            jsonData.push({name: "rhtRegId", value: rhtRegId});
            packet.data.data = jsonData;
            // 不显示背景
            packet.noCover = true;
            // 显示统计结果
            core.ajax.sendPacketHtml(packet, function (response) {
                var responseHtml = $(response);
                var divId = $("input[name=toContainer]", responseHtml).val();
                var divContainer = $("#" + divId, navTab.getCurrentPanel());
                $("div.js_sum_rslt", divContainer).html(responseHtml);
            });
            packet = null;
        }
    }

    $(function () {
        // 点击刷新重新加载图片
        $("div.js_sum", navTab.getCurrentPanel()).delegate("span.js_item_reload", "click", function () {
            oh001_rpt.loadPic($(this).closest("div.js_sum"));
        });
        // 自动加载所有的图片
        $("div.js_sum", navTab.getCurrentPanel()).each(function () {
            oh001_rpt.loadPic(this);
        });

    });
</script>


