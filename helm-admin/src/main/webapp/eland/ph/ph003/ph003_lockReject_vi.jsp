<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<c:set var="editAble" value="true"/>
<table width="100%" class="border" style="text-align: center">
    <tr>
        <th style="width: 10%"><label>备注说明：</label></th>
        <td style="text-align: left; line-height: 50px">${hiTaskInfo.HiTaskInfo.TskBidVars.taskComment}</td>
    </tr>
</table>

<oframe:script src="${pageContext.request.contextPath}/eland/ph/ph003/js/ph00301_task.js" type="text/javascript"/>