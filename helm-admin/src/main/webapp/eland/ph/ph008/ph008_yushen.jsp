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
<form id="ph008yushenFrm">
    <table class="border">
        <input type="hidden" name="hsId" value="${hsId}"/>
        <tr>
            <th width="20%"><label>预审处理结果：</label></th>
            <td><oframe:select prjCd="${param.prjCd}" id="procResult1" name="procResult1" itemCd="DISP_RESULT" value="${hsStatus.Row.procResult1}"/></td>
        </tr>
        <tr>
            <th width="20%"><label>处理时间：</label></th>
            <td>
                <input class="date" type="text" name="recordTime1" value='<oframe:date value="${hsStatus.Row.recordTime1}" _default="now" format="yyyy-MM-dd"/>'/>
            </td>
        </tr>
        <tr>
            <th><label>备注说明：</label></th>
            <td style="text-align: left"><textarea rows="5" style="margin: 0px;" name="comment1">${hsStatus.Row.comment1}</textarea></td>
        </tr>
        <tr style="text-align: center;">
            <td colspan="2">
            <span class="btn-primary marl10"
                  style="padding: 5px 10px 5px 10px; line-height: 34px; cursor:pointer;"
                  onclick="ph008.completeYuShen(this);">完成</span>
            </td>
        </tr>
    </table>
</form>
<oframe:script type="text/javascript" src="${pageContext.request.contextPath}/eland/ph/ph008/js/ph008.js"/>
