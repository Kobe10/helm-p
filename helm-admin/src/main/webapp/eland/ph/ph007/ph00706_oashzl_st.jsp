<%--自定义任务--处理任务--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<form id="ph00706Frm">
    <table width="100%" class="border">
        <tr>
            <th>审核结果：</th>
            <td><input type="radio" name="examine" value="审核通过"/>审核通过
                <input type="radio" name="examine" value="审核不通过"/>审核不通过
            </td>
        </tr>
        <jsp:include page="/eland/ph/ph007/ph007-nextAssignee.gv"/>
        <tr style="text-align: center;">
            <td colspan="2">
            <span class="btn-primary marl10"
                  style="padding: 5px 10px 5px 10px; line-height: 34px; cursor:pointer;"
                  onclick="ph007.submitVerifyassignee('ph00706Frm',this, {})">提交</span>
            </td>
        </tr>
    </table>
</form>