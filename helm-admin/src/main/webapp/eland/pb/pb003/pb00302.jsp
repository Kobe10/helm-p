<%--
  Created by IntelliJ IDEA.
  User: Dennis
  Date: 2014/11/3 0003
  Time: 17:33
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="pagecontent">
    <div class="panelBar">
        <ul class="toolBar">
            <oframe:op prjCd="${param.prjCd}" template="li" cssClass="save"
                       name="保存" rhtCd="edit_ext_cmp_rht"
                       onClick="pb003.save();"/>
            <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
        </ul>
    </div>
    <form id="pb003frm" method="post">
        <input type="hidden" name="extCmpId" value="${info.CmpExtCmp.extCmpId}"/>
        <input type="hidden" name="prjExtCmpId" value="${subInfo.Row.prjExtCmpId}"/>
        <input type="hidden" name="prjCd" value="${prjCd}"/>
        <table class="form">
            <tr>
                <th width="20%">
                    <label>单位名称：</label>
                </th>
                <td>
                    <input type="text" name="extCmpName" value="${info.CmpExtCmp.extCmpName}"/>
                </td>
            </tr>

            <tr>
                <th>
                    <label>单位描述：</label>
                </th>
                <td colspan="3">
                    <textarea rows="5" cols="120" name="extCmpDesc">${info.CmpExtCmp.extCmpDesc}</textarea>
                </td>
            </tr>
            <tr>
                <th>
                    <label>聘用说明：</label>
                </th>
                <td colspan="3">
                    <textarea rows="5" cols="120" name="extNote">${subInfo.Row.extNote}</textarea>
                </td>
            </tr>
            <tr>
                <th>
                    <label>聘用开始时间：</label>
                </th>
                <td colspan="3">
                    <input type="text" class="date" datefmt="yyyy-MM-dd" name="startDate"
                           value='<oframe:date value="${subInfo.Row.startDate}" format="yyyy-MM-dd"/>'/>
                </td>
            </tr>
            <tr>
                <th>
                    <label>聘用结束时间：</label>
                </th>
                <td colspan="3">
                    <input type="text" class="date textInput" datefmt="yyyy-MM-dd" name="endDate"
                           value='<oframe:date value="${subInfo.Row.endDate}" format="yyyy-MM-dd"/>'/>
                </td>
            </tr>
        </table>
    </form>
</div>
<oframe:script src="${pageContext.request.contextPath}/eland/pj/pj014/js/pj014.js" type="text/javascript"/>
