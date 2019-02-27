<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div layoutH="57" id="pb001Rpt" style="border: 1px solid #e9e9e9;">
    <div class="js_sum" id="pb001Rpt_001" style="position: relative;">
         <span class="link js_item_reload" style="position: absolute; top: 10px;right: 20px; display: inline-block;">
            刷新
        </span>

        <form>
            <input type="hidden" name="rptName" value="院落进展">
            <input type="hidden" name="xAttrs" value="BuildInfo.buildStatus">
            <input type="hidden" name="resultAttrsName" value="BuildInfo.buildId">
            <input type="hidden" name="resultAttrsType" value="2">
            <input type="hidden" name="orderAttrsName" value="BuildInfo.buildStatus">
            <input type="hidden" name="orderAttrsOrder" value="asc">
        </form>
        <div class="js_sum_rslt"></div>
    </div>

    <div class="js_sum" id="pb001Rpt_003" style="position: relative;">
         <span class="link js_item_reload" style="position: absolute; top: 10px;right: 20px; display: inline-block;">
            刷新
        </span>

        <form>
            <input type="hidden" name="rptName" value="院落类型">
            <input type="hidden" name="xAttrs" value="BuildInfo.buildType">
            <input type="hidden" name="resultAttrsName" value="BuildInfo.buildId">
            <input type="hidden" name="resultAttrsType" value="2">
            <input type="hidden" name="orderAttrsName" value="BuildInfo.buildStatus">
            <input type="hidden" name="orderAttrsOrder" value="asc">
        </form>
        <div class="js_sum_rslt"></div>
    </div>

    <div class="js_sum" id="pb001Rpt_002" style="position: relative;">
         <span class="link js_item_reload" style="position: absolute; top: 10px;right: 20px; display: inline-block;">
            刷新
        </span>

        <form>
            <input type="hidden" name="rptName" value="房源使用">
            <input type="hidden" name="xAttrs"
                   value="BuildInfo.HouseInfo.HsCtInfo.HsCtChooseInfo.chooseHsTp,BuildInfo.HouseInfo.HsCtInfo.HsCtChooseInfo.chooseStatus">
            <%--<input type="hidden" name="legendAttrs"--%>
            <%--value="BuildInfo.HouseInfo.HsCtInfo.HsCtChooseInfo.chooseHsRegId.regName">--%>
            <input type="hidden" name="resultAttrsName" value="BuildInfo.HouseInfo.HsCtInfo.HsCtChooseInfo.chooseHsId">
            <input type="hidden" name="resultAttrsType" value="2">
            <input type="hidden" name="orderAttrsName"
                   value="BuildInfo.HouseInfo.HsCtInfo.HsCtChooseInfo.chooseHsTp,BuildInfo.HouseInfo.HsCtInfo.HsCtChooseInfo.chooseStatus,BuildInfo.HouseInfo.HsCtInfo.HsCtChooseInfo.chooseHsRegId.regName">
            <input type="hidden" name="orderAttrsOrder" value="asc,asc,asc">
        </form>
        <div class="js_sum_rslt"></div>
    </div>
</div>
<script type="text/javascript">
    var pb001_rpt = {
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
            var formObj = $("#pb001frm", navTab.getCurrentPanel());
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
    }

    $(function () {
        // 点击刷新重新加载图片
        $("div.js_sum", navTab.getCurrentPanel()).delegate("span.js_item_reload", "click", function () {
            pb001_rpt.loadPic($(this).closest("div.js_sum"));
        });
        // 自动加载所有的图片
        $("div.js_sum", navTab.getCurrentPanel()).each(function () {
            pb001_rpt.loadPic(this);
        });
    });
</script>
