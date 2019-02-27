<%--
  Created by IntelliJ IDEA.
  User: shfb_wang
  Date: 2015/7/23 0023 14:18
  Copyright(c) 北京四海富博计算机服务有限公司
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>

<%--引入 房产信息详情页 --%>
<jsp:include page="/eland/ph/ph003/ph00301-initS.gv?prjCd=${prjCd}&hsId=${hsId}&procReadonly=false"/>
<span class="panelBar">
    <ul class="toolBar">
        <input type="hidden" name="taskId" value="${taskId}"/>
        <c:if test="${hsId != '' && hsId != null}">
            <li><a class="save" onclick="ph00301_task.cfmSendTask(this)"><span>发起申请</span></a></li>
            <li>
                <%--<a class="cancel" onclick="index.quickOpen('eland/ph/ph001/ph001-init.gv?upRegId=${regId}&prjCd=${prjCd}',{opCode: 'ph001-init', opName: '居民信息', fresh: true});">--%>
                <a class="cancel" onclick="window.close()">
                    <span>取消申请</span>
                </a>
            </li>
        </c:if>
    </ul>
</span>

<form id="ph003_cmd_st">
    <table width="100%" class="border">
        <tr>
            <th width="10%"><label>请求名称：</label></th>
            <td>
                <input type="text" name="procInsName" class="js_pri"
                       value='[<oframe:entity  prjCd="${param.prjCd}" entityName="HouseInfo" property="hsOwnerPersons" value="${hsId}"/>]资料修改申请'/>
            </td>
        </tr>
        <%--<jsp:include page="/eland/ph/ph003/ph003_task-nextAssignee.gv">--%>
            <%--<jsp:param name="tdNum" value="2"/>--%>
        <%--</jsp:include>--%>
        <tr>
            <th width="10%"><label>备注说明：</label></th>
            <td colspan="2" style="text-align: left">
                <textarea rows="5" style="margin: 0px;width: 80%" name="taskComment"></textarea>
            </td>
        </tr>
    </table>
</form>

<oframe:script src="${pageContext.request.contextPath}/eland/ph/ph003/js/ph00301_input.js" type="text/javascript"/>
<oframe:script src="${pageContext.request.contextPath}/eland/ph/ph003/js/ph00301_task.js" type="text/javascript"/>