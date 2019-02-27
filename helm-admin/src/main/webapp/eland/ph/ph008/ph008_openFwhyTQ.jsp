<%--
  Created by IntelliJ IDEA.
  User: shfb_wang
  Date: 2015/4/1
  Time: 16:05
  To change this template use File | Settings | File Templates.
--%>
<%--流程业务处理-  退租申请 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="pagecontent">
    <form id="ph008tfjfFrm">
        <div class="panel" style="">
            <div class="js_panel_context">
                <input type="hidden" name="hsId" value="${hsId}"/>
                <table class="border">
                    <tr>
                        <th width="20%"><label>核验结果：</label></th>
                        <td>
                            <input type="radio" name="procResult12" value="审核通过"/>审核通过
                            <input type="radio" name="procResult12" value="审核不通过"/>审核不通过
                        </td>
                    </tr>
                    <tr>
                        <th width="20%"><label>处理时间：</label></th>
                        <td><input class="date" type="text" datefmt="yyyy-MM-dd" name="recordTime12"
                                   value='<oframe:date value="${hsStatus.Row.recordTime12}" format="yyyy-MM-dd"/>'/></td>
                    </tr>
                    <tr>
                        <th><label>备注说明：</label></th>
                        <td style="text-align: left"><textarea rows="5" style="margin: 0px;"
                                                               name="comment12">${hsStatus.Row.comment12}</textarea></td>
                    </tr>
                    <tr style="text-align: center">
                        <td colspan="2">
                            <span class="btn-primary marl10" style="padding: 5px 10px 5px 10px; line-height: 34px; cursor:pointer;"
                                  onclick="ph008.submitFwhyTQ(this);">提交</span>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
    </form>
</div>
<oframe:script type="text/javascript" src="${pageContext.request.contextPath}/eland/ph/ph008/js/ph008.js"/>
