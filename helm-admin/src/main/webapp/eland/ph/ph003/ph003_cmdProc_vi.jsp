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
<%--<div class="panelBar">--%>
    <%--<ul class="toolBar">--%>
        <%--<li><a>【指挥审批结果】 ${hsId} 页面</a></li>--%>
        <%--<c:if test="${hsId != '' && hsId != null}">--%>
            <%--<li><a class="save" onclick="ph00301_task.initTask('${busiKey}');"><span>指挥审核通过</span></a></li>--%>
            <%--<li><a class="save" onclick="index.quickOpen('eland/ph/ph003/ph00301-initHs.gv?hsId=${hsId}&buildId=${buildId}&regId=${regId}&prjCd=${prjCd}');"><span>返回</span></a></li>--%>
            <%--<li><a class="save" onclick="index.quickOpen('eland/ph/ph001/ph001-init.gv?upRegId=${regId}&prjCd=${prjCd}');"><span>返回</span></a></li>--%>
        <%--</c:if>--%>
    <%--</ul>--%>
<%--</div>--%>
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
<%--引入 房产信息详情页 --%>
<%--<jsp:include page="/eland/ph/ph003/ph00301-initS.gv?hsId=${hsId}"/>--%>

<oframe:script src="${pageContext.request.contextPath}/eland/ph/ph003/js/ph00301_task.js" type="text/javascript"/>