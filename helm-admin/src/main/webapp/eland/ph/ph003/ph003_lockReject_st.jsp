<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<c:set var="editAble" value="true"/>
<div class="panelBar">
    <ul class="toolBar">
        <c:if test="${hsId != '' && hsId != null}">
            <li><a class="save"
                   onclick="ph00301_task.cmdPassTask('ph003_lockReject_st','${hsId}', true);"><span>提交审核</span></a></li>
            <li><a class="cancel"
                   onclick="ph00301_task.Revoke('${procInsId}');"><span>撤销</span></a></li>
        </c:if>
    </ul>
</div>
<form id="ph003_lockReject_st">
    <table width="100%" class="border" style="text-align: center">
        <input type="hidden" name="taskId" value="${taskId}"/>
        <tr>
            <th width="10%"><label>备注说明：</label></th>
            <td colspan="2" style="text-align: left">
                <textarea rows="5" style="margin: 0px;width: 80%" name="taskComment"></textarea>
            </td>
        </tr>
    </table>
</form>

<oframe:script src="${pageContext.request.contextPath}/eland/ph/ph003/js/ph00301_task.js" type="text/javascript"/>