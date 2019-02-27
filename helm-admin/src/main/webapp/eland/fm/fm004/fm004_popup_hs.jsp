<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<table>
    <tr>
        <th style='width:80px;height: 30px;text-align: right'>安&nbsp;置&nbsp;人：</th>
        <td>${selectData.OpResult.PageData.Row[0].hsOwnerPersons}</td>
    </tr>
    <tr>
        <th style='height: 30px;text-align: right'>房屋地址：</th>
        <td>${selectData.OpResult.PageData.Row[0].hsFullAddr}</td>
    </tr>
    <tr>
        <th style='height: 30px;text-align: right'>产权性质：</th>
        <td>${selectData.OpResult.PageData.Row[0].hsOwnerType_Name}</td>
    </tr>
    <tr>
        <th style='height: 30px;text-align: right'>房产状态：</th>
        <td>${selectData.OpResult.PageData.Row[0].hsStatus_Name}</td>
    </tr>
    <tr>
        <th style='height: 30px;text-align: right'>建筑面积：</th>
        <td>
            ${selectData.OpResult.PageData.Row[0].hsBuildSize}
             <span class="link"
                   onclick="fm004.viewHouse('${selectData.OpResult.PageData.Row[0].hsId}')"
                   style="float: right;">详情</span>
        </td>
    </tr>
</table>


