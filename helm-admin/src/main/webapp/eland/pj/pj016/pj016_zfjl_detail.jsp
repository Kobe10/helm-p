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
            <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
        </ul>
    </div>
    <form id="pj016frm" method="post">
        <table class="border">
            <tr>
                <th>
                    <label>单位名称：</label>
                </th>
                <td><oframe:entity  prjCd="${param.prjCd}"  entityName="CmpExtCmp" property="extCmpName" value="${payResult.PayRecordInfo.extCmpId}"/></td>
                <th>
                    <label>操作人：</label>
                </th>
                <td><oframe:staff staffId="${payResult.PayRecordInfo.operateStaff}"/></td>
            </tr>
            <tr>
                <th>
                    <label>支付时间：</label>
                </th>
                <td><oframe:date value="${payResult.PayRecordInfo.payTime}" format="yyyy-MM-dd HH:mm:ss"/></td>
                <th>
                    <label>支付区间：</label>
                </th>
                <td><oframe:date value="${payResult.PayRecordInfo.fromDate}" format="yyyy-MM-dd"/> 至 <oframe:date value="${payResult.PayRecordInfo.toDate}" format="yyyy-MM-dd"/></td>
            </tr>
            <tr>
                <th>
                    <label>固定服务费：</label>
                </th>
                <td>${payResult.PayRecordInfo.fixedServiceFee}</td>
                <th>
                    <label>总走户费：</label>
                </th>
                <td><oframe:money value="${payResult.PayRecordInfo.totalZhFee}" format="number"/></td>
            </tr>
            <tr>
                <th>
                    <label>总服务费：</label>
                </th>
                <td colspan="3"><oframe:money value="${payResult.PayRecordInfo.totalFee}" format="number"/></td>
            </tr>
        </table>
    </form>
</div>
