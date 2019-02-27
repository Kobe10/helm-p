<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<table>
    <tr>
        <th style='width: 80px;height: 30px;text-align: right'>院落地址：</th>
        <td>${selectData.OpResult.PageData.Row[0].buildFullAddr}</td>
    </tr>
    <tr>
        <th style='height: 30px;text-align: right'>院落类型：</th>
        <td>${selectData.OpResult.PageData.Row[0].buildType_Name}</td>
    </tr>
    <tr>
        <th style='height: 30px;text-align: right'>院落状态：</th>
        <td>${selectData.OpResult.PageData.Row[0].buildStatus_Name}</td>
    </tr>
    <tr>
        <th style='height: 30px;text-align: right'>占地面积：</th>
        <td> ${selectData.OpResult.PageData.Row[0].buildLandSize}
            <span class="link"
                  onclick="ph005.viewBuilding('${selectData.OpResult.PageData.Row[0].buildId}')"
                  style="float: right;">详情</span>
        </td>
    </tr>
</table>


