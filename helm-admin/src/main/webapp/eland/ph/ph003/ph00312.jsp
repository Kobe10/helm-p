<%--院落房屋信息--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="panel">
    <h1>房屋附属信息
        <span class="panel_menu js_reload">刷新</span>
    </h1>
    <%--面板内容--%>
    <div class="js_panel_context" style="min-height: 115px;">
        <div style="height: 100%; width: 100%; display: inline-block; float: right;">
            <table class="border" width="100%">
                <tr>
                    <th width="38%" style="line-height: 30px;"><label>空调数量</label></th>
                    <td>${affiliatedBean.HouseInfo.ktNum}</td>
                    <th width="38%" style="line-height: 30px;"><label>有线数量</label></th>
                    <td>${affiliatedBean.HouseInfo.yxNum}</td>
                </tr>
                <tr>
                    <th style="line-height: 30px;"><label>固话数量</label></th>
                    <td>${affiliatedBean.HouseInfo.ghNum}</td>
                    <th style="line-height: 30px;"><label>热水器数量</label></th>
                    <td>${affiliatedBean.HouseInfo.rsqNum}</td>
                </tr>
                <tr>
                    <th style="line-height: 30px;"><label>煤改电个人承担</label></th>
                    <td colspan="3">${affiliatedBean.HouseInfo.mgdGrZf}</td>
                </tr>
                <tr>
                    <th style="line-height: 30px;"><label>备注说明</label></th>
                    <td colspan="3">${affiliatedBean.HouseInfo.fwfsNotes}</td>
                </tr>
            </table>
        </div>
    </div>
</div>
<script type="text/javascript">
    var ph00311 = {
    };
    $(document).ready(function () {
    });
</script>
