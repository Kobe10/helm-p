<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<div layoutH="57" id="oh004_rpt" style="border: 1px solid #e9e9e9;">
    <%--统计工作进展--%>
    <div class="js_sum" id="oh004_rpt_000" style="position: relative;">
        <span class="link js_item_reload" style="position: absolute; top: 10px;right: 20px; display: inline-block;">
            刷新
        </span>
        <c:set var="groupName" value="ctDate"/>
        <c:choose>
            <c:when test="${param.viewFilter  == 'f2'}">
                <c:set var="groupName" value="wqDate"/>
            </c:when>
            <c:when test="${param.viewFilter  == 'f3'}">
                <c:set var="groupName" value="ctDate"/>
            </c:when>
        </c:choose>
        <form>
            <input type="hidden" name="rptName" value="工作进展">
            <input type="hidden" name="xAttrs" value="PersonInfo.${groupName},PersonInfo.oldHsId.hsFullAddr">
            <input type="hidden" name="xAttrFuncs" value="PersonInfo.${groupName}|1|2">
            <input type="hidden" name="resultAttrsName" value="PersonInfo.personId">
            <input type="hidden" name="resultAttrsType" value="2">
            <input type="hidden" name="orderAttrsName" value="PersonInfo.oldHsId.hsFullAddr,PersonInfo.${groupName}">
            <input type="hidden" name="orderAttrsOrder" value="desc,desc">
        </form>
        <div class="js_sum_rslt"></div>
    </div>
    <%--<div class="js_sum" id="oh004_rpt_001" style="position: relative;">--%>
        <%--<span class="link js_item_reload" style="position: absolute; top: 10px;right: 20px; display: inline-block;">--%>
            <%--刷新--%>
        <%--</span>--%>

        <%--<form>--%>
            <%--<c:choose>--%>
                <%--<c:when test="${param.viewFilter  == 'f2'}">--%>
                    <%--<input type="hidden" name="rptName" value="已结算房源数量">--%>
                <%--</c:when>--%>
                <%--<c:when test="${param.viewFilter  == 'f3'}">--%>
                    <%--<input type="hidden" name="rptName" value="待结算房源数量">--%>
                <%--</c:when>--%>
                <%--<c:otherwise>--%>
                    <%--<input type="hidden" name="rptName" value="房源数量">--%>
                <%--</c:otherwise>--%>
            <%--</c:choose>--%>
            <%--<input type="hidden" name="xAttrs" value="NewHsInfo.hsSt">--%>
            <%--<input type="hidden" name="resultAttrsName" value="NewHsInfo.newHsId">--%>
            <%--<input type="hidden" name="resultAttrsType" value="2">--%>
            <%--<input type="hidden" name="orderAttrsName" value="NewHsInfo.hsSt">--%>
            <%--<input type="hidden" name="orderAttrsOrder" value="desc">--%>
        <%--</form>--%>
        <%--<div class="js_sum_rslt"></div>--%>
    <%--</div>--%>
    <%--<div class="js_sum" id="oh004_rpt_002" style="position: relative;">--%>
        <%--<span class="link js_item_reload" style="position: absolute; top: 10px;right: 20px; display: inline-block;">--%>
            <%--刷新--%>
        <%--</span>--%>

        <%--<form>--%>
            <%--<c:choose>--%>
                <%--<c:when test="${param.viewFilter  == 'f2'}">--%>
                    <%--<input type="hidden" name="rptName" value="已结算房源费用">--%>
                <%--</c:when>--%>
                <%--<c:when test="${param.viewFilter  == 'f3'}">--%>
                    <%--<input type="hidden" name="rptName" value="待结算房源费用">--%>
                <%--</c:when>--%>
                <%--<c:otherwise>--%>
                    <%--<input type="hidden" name="rptName" value="总费用">--%>
                <%--</c:otherwise>--%>
            <%--</c:choose>--%>
            <%--<input type="hidden" name="xAttrs" value="NewHsInfo.hsSt">--%>
            <%--<input type="hidden" name="resultAttrsName" value="NewHsInfo.cmpQnCost,NewHsInfo.cmpWyCost">--%>
            <%--<input type="hidden" name="resultAttrsType" value="1,1">--%>
            <%--<input type="hidden" name="orderAttrsName" value="NewHsInfo.hsSt">--%>
            <%--<input type="hidden" name="orderAttrsOrder" value="desc">--%>
        <%--</form>--%>
        <%--<div class="js_sum_rslt"></div>--%>
    <%--</div>--%>
</div>
<script type="text/javascript">
    var oh004_rpt = {
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
            var formObj = $("#oh004Form", navTab.getCurrentPanel());
            var entityName = $("input[name=entityName]", formObj).val();
            var conditionNames = $("input[name=conditionName]", formObj).val();
            var conditions = $("input[name=condition]", formObj).val();
            var conditionValues = $("input[name=conditionValue]", formObj).val();
            // 获取扩展查询条件
            if (extConditionName) {
                conditionNames = extConditionName + "," + conditionNames;
                conditions = extCondition + "," + conditions;
                conditionValues = extConditionValue + "," + conditionValues;
            }
            // 排序字段
            var orderAttrsName = $("input[name=sortColumn]", formObj).val();
            var orderAttrsOrder = $("input[name=sortOrder]", formObj).val();

            var regUseType = $("input[name=regUseType]", formObj).val();
            var rhtRegId = $("input[name=rhtRegId]", formObj).val();
            var rhtOrgId = $("input[name=rhtOrgId]", formObj).val();

            jsonData.push({name: "entityName", value: entityName});
            jsonData.push({name: "conditionName", value: conditionNames});
            jsonData.push({name: "condition", value: conditions});
            jsonData.push({name: "conditionValue", value: conditionValues});
            jsonData.push({name: "orderAttrsName", value: orderAttrsName});
            jsonData.push({name: "orderAttrsOrder", value: orderAttrsOrder});
            jsonData.push({name: "regUseType", value: regUseType});
            jsonData.push({name: "rhtRegId", value: rhtRegId});
            jsonData.push({name: "rhtOrgId", value: rhtOrgId});
            packet.data.data = jsonData;
            // 不显示背景
            packet.noCover = true;
            // 请求获取销售品销售统计报文
            core.ajax.sendPacketHtml(packet, function (response) {
                var responseHtml = $(response);
                var divId = $("input[name=toContainer]", responseHtml).val();
                var divContainer = $("#" + divId, navTab.getCurrentPanel());
                $("div.js_sum_rslt", divContainer).html(responseHtml);
            });
            packet = null;
        }
    };

    $(function () {
        // 点击刷新重新加载图片
        $("div.js_sum", navTab.getCurrentPanel()).delegate("span.js_item_reload", "click", function () {
            oh004_rpt.loadPic($(this).closest("div.js_sum"));
        });
        // 自动加载所有的图片
        $("div.js_sum", navTab.getCurrentPanel()).each(function () {
            oh004_rpt.loadPic(this);
        });
    });

</script>
