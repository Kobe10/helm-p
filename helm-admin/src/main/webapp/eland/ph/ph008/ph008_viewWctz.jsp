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
    <div class="panel" style="">
        <div class="js_panel_context">
            <input type="hidden" name="hsId" value="${hsId}"/>
            <table class="border">
                <tr>
                    <th width="20%"><label>公房年租金：</label></th>
                    <td>${hsStatus.Row.hsPubZj}</td>
                </tr>
                <tr>
                    <th width="20%"><label>公房过户费用：</label></th>
                    <td>${hsStatus.Row.hsPubGhf}</td>
                </tr>
                <tr>
                    <th width="20%"><label>处理时间：</label></th>
                    <td><oframe:date value="${hsStatus.Row.recordTime11}" format="yyyy-MM-dd"/></td>
                </tr>
                <tr>
                    <th><label>备注说明：</label></th>
                    <td style="text-align: left">${hsStatus.Row.comment11}</td>
                </tr>
            </table>
        </div>
    </div>
</div>