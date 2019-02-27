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
<div class="panelBar">
    <ul class="toolBar">
        <%--<li><a>【指挥审批】${hsId} 页面</a></li>--%>
        <input type="hidden" name="taskId" value="${taskId}"/>
        <c:if test="${hsId != '' && hsId != null}">
            <li><a class="save" onclick="ph00301_task.cmdPassTask('ph003_cmd_st','${hsId}', true);"><span>审核通过</span></a></li>
            <li><a class="cancel" onclick="ph00301_task.cmdPassTask('ph003_cmd_st','${hsId}', false);"><span>审核不通过</span></a></li>
            <%--<li><a class="cancel" onclick="index.quickOpen('eland/ph/ph001/ph001-init.gv?upRegId=${regId}&prjCd=${prjCd}');"><span>返回</span></a></li>--%>
        </c:if>
    </ul>
</div>

<form id="ph003_cmd_st">
    <table width="100%" class="border" style="text-align: center">
        <jsp:include page="/eland/ph/ph003/ph003_task-nextAssignee.gv">
            <jsp:param name="tdNum" value="2"/>
        </jsp:include>
    </table>
</form>
<%--引入 房产信息详情页 --%>
<%--<jsp:include page="/eland/ph/ph003/ph00301-initS.gv?hsId=${hsId}"/>--%>

<oframe:script src="${pageContext.request.contextPath}/eland/ph/ph003/js/ph00301_task.js" type="text/javascript"/>