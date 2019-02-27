<%--自定义任务--查看任务--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<table width="100%" class="border">
    <tr>
        <th style="width: 10%"><label>处理结果：</label></th>
        <td>${hiTaskInfo.HiTaskInfo.TskBidVars.examine}</td>
    </tr>
    <tr>
        <th style="width: 10%"><label>备注说明：</label></th>
        <td style="line-height: 50px;">${hiTaskInfo.HiTaskInfo.TskBidVars.comment}</td>
    </tr>
</table>