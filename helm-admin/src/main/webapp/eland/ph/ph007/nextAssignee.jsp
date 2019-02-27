<%--
  Created by IntelliJ IDEA.
  User: shfb_wang
  Date: 2015/4/1
  Time: 16:05
  To change this template use File | Settings | File Templates.
--%>
<%--院落腾退流程--上报整院--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<tr>
    <th width="10%"><label>备注说明：</label></th>
    <td colspan="${tdNum}" style="text-align: left"><textarea rows="8" style="margin: 0px;" name="comment"></textarea>
    </td>
</tr>
<tr>
    <th><label>下一步处理人：</label></th>
    <td colspan="${tdNum}" style="text-align: left">
        <input type="hidden" name="taskId" value="${taskId}"/>
        <input type="hidden" name="assignee" class="js_pri js_assignee" value=""/>
        <input type="text" name="targetPerTemp" atoption="ph007.getOption" aturl="ph007.getUrl"
               class="autocomplete required acInput textInput valid js_targetPerTemp" autocomplete="off">
    </td>
</tr>
<oframe:script type="text/javascript" src="${pageContext.request.contextPath}/eland/ph/ph007/js/ph007.js"/>
