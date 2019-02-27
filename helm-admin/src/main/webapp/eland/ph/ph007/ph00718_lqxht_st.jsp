<%--自定义任务--处理任务--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<form id="ph00718Frm">
    <table width="100%" class="border">
        <tr>
            <th width="10%"><label>备注说明：</label></th>
            <td style="text-align: left"><textarea rows="8" style="margin: 0px;" name="comment"></textarea>
            </td>
        </tr>
        <input type="hidden" name="taskId" value="${taskId}"/>
        <input type="hidden" name="assignee" value=""/>
        <tr style="text-align: center;">
            <td colspan="2">
            <span class="btn-primary marl10"
                  style="padding: 5px 10px 5px 10px; line-height: 34px; cursor:pointer;"
                  onclick="ph007.submitVerifyassignee('ph00718Frm',this, {})">提交</span>
            </td>
        </tr>
    </table>
</form>