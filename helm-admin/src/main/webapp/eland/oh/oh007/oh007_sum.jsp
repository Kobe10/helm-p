<%--人地房房产信息检索界面--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%--房源统计信息--%>
<div style="border: 1px solid #e9e9e9;text-align: center;" layoutH="57">
    <div id="oh007Rpt" style="border: 1px solid #e9e9e9;">
        <div class="js_sum" id="oh007Rpt_001" style="position: relative;" chartType="">
            <span class="link js_item_reload" style="position: absolute; top: 10px;right: 20px; display: inline-block;">
                刷新
            </span>
            <form>
                <input type="hidden" name="rptName" value="已选房交房统计">
                <input type="hidden" name="xAttrs" value="NewHsInfo.handleStatus">
                <input type="hidden" name="resultAttrsName" value="NewHsInfo.newHsId">
                <input type="hidden" name="extConditionName" value="NewHsInfo.statusCd">
                <input type="hidden" name="extCondition" value="=">
                <input type="hidden" name="extConditionValue" value="3">
                <input type="hidden" name="resultAttrsType" value="2">
                <input type="hidden" name="orderAttrsName" value="NewHsInfo.handleStatus">
                <input type="hidden" name="orderAttrsOrder" value="asc">
            </form>
            <div class="js_sum_rslt"></div>
        </div>
        <div class="js_sum" id="oh007Rpt_002" style="position: relative;" chartType="">
            <span class="link js_item_reload" style="position: absolute; top: 10px;right: 20px; display: inline-block;">
                刷新
            </span>
            <form>
                <input type="hidden" name="rptName" value="交房时间统计">
                <input type="hidden" name="xAttrs" value="NewHsInfo.handHsDate">
                <input type="hidden" name="resultAttrsName" value="NewHsInfo.newHsId">
                <input type="hidden" name="extConditionName" value="">
                <input type="hidden" name="extCondition" value="">
                <input type="hidden" name="extConditionValue" value="">
                <input type="hidden" name="resultAttrsType" value="2">
                <input type="hidden" name="orderAttrsName" value="NewHsInfo.handHsDate">
                <input type="hidden" name="orderAttrsOrder" value="asc">
            </form>
            <div class="js_sum_rslt"></div>
        </div>
    </div>

</div>
<script type="text/javascript">
    var oh007_rpt = {
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
            var formObj = $("#oh00701frm", navTab.getCurrentPanel());
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
            oh007_rpt.loadPic($(this).closest("div.js_sum"));
        });
        // 自动加载所有的图片
        $("div.js_sum", navTab.getCurrentPanel()).each(function () {
            oh007_rpt.loadPic(this);
        });

    });
</script>


