<%--
  Created by IntelliJ IDEA.
  User: shfb_wang
  Date: 2015/7/23 0023 14:18
  Copyright(c) 北京四海富博计算机服务有限公司
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<c:set var="editAble" value="true"/>
<table width="100%" class="border" style="text-align: center">
    <tr>
        <th style="width: 10%"><label>审核结果：</label></th>
        <td colspan="4" style="text-align: left; line-height: 50px">
            <c:if test="${PROC_INST_INFO.ProcInsInfo.Variables.isAgree == 'true'}">
                审核通过
            </c:if>
            <c:if test="${PROC_INST_INFO.ProcInsInfo.Variables.isAgree != 'true'}">
                审核不通过
            </c:if>
        </td>
    </tr>
    <tr>
        <th style="width: 10%"><label>备注说明：</label></th>
        <td colspan="4" style="text-align: left; line-height: 50px">${hiTaskInfo.HiTaskInfo.TskBidVars.taskComment}</td>
    </tr>
</table>

<oframe:script src="${pageContext.request.contextPath}/eland/ph/ph003/js/ph00301_task.js" type="text/javascript"/>