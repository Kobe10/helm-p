<%--院落房屋信息--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="panel">
    <h1>房屋信息
        <span class="panel_menu js_reload">刷新</span>
    </h1>
    <%--面板内容--%>
    <div class="js_panel_context">
        <div class="border">
            <form id="pb002HsQFrm">
                <input type="hidden" name="entityName" value="HouseInfo"/>
                <input type="hidden" name="conditionName" value="HouseInfo.buildId">
                <input type="hidden" name="condition" value="=">
                <input type="hidden" name="conditionValue" value="${buildId}">
                <input type="hidden" name="rhtRegId" value="${buildingRegId}">
                <%--房源类型--%>
                <input type="hidden" name="regUseType" value="1"/>
                <input type="hidden" name="sortColumn" value="HouseInfo.hsCd">
                <input type="hidden" name="sortOrder" value="asc">
                <input type="hidden" name="divId" value="pb002Hs_list_print">
                <input type="hidden" name="resultField"
                       value="HouseInfo.hsCd,HouseInfo.hsOwnerPersons,HouseInfo.hsOwnerType,HouseInfo.hsBuildSize,HouseInfo.hsUseSize,HouseInfo.allFamNum,HouseInfo.famPsNum">
                <%--必须包含的字段--%>
                <input type="hidden" name="forceResultField" value="HouseInfo.hsId">
                <input type="hidden" name="forward" id="forward" value="/eland/pb/pb002/pb002_hs_list"/>
            </form>
            <div class="js_page" style="width: 99.9%;"></div>
        </div>
    </div>
</div>
<script type="text/javascript">
    var pb002Hs = {
        openHs: function (hsId) {
            var url = "eland/pb/pb003/pb003-init.gv?hsId=" + hsId
                    + "&prjCd=" + getPrjCd();
            index.quickOpen(url, {opCode: "pb003-init", opName: "房屋信息", fresh: true});
        }
    };
    $(document).ready(function () {
        // 查询数据
        Page.query($("#pb002HsQFrm", navTab.getCurrentPanel()), "");
    });
</script>
