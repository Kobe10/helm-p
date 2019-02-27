<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<table class="border">
    <tr>
        <th class="subTitle" colspan="2" style="text-align: left;"><h1>被安置人信息
            <span class="link mar5" onclick="popupWid.detail(${clickId})">查看详情</span></h1></th>

    </tr>
    <tr>
        <th style='width:100px;height: 30px;text-align: right'>安&nbsp;置&nbsp;人：</th>
        <td>${selectData.OpResult.PageData.Row[0].hsOwnerPersons}</td>
    </tr>
    <tr>
        <th style='height: 30px;text-align: right'>房屋地址：</th>
        <td>${selectData.OpResult.PageData.Row[0].hsFullAddr}</td>
    </tr>
    <tr>
        <th style='height: 30px;text-align: right'>房产状态：</th>
        <td>${selectData.OpResult.PageData.Row[0].hsStatus_Name}</td>
    </tr>
    <tr>
        <th style='height: 30px;text-align: right'>建筑面积：</th>
        <td>
            ${selectData.OpResult.PageData.Row[0].hsBuildSize}
        </td>
    </tr>
</table>

<script type="text/javascript">
    var popupWid = {
        detail: function (hsId) {
            var fullScreenSpan = $("span.fullScreen", navTab.getCurrentPanel());
            var url = "eland/ph/ph003/ph003-init.gv?prjCd=" + getPrjCd() + "&hsId=" + hsId;
            if (fullScreenSpan.length == "1") {
                index.quickOpen(url, {opCode: "ph003-init", opName: "居民详情"});
            } else {
                index.quickOpen(url, {opCode: "ph003-init", opName: "居民详情", dialog: true});
            }
        }
    }
</script>
