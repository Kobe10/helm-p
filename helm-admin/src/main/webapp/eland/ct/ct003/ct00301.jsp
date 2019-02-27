<%--
  Created by IntelliJ IDEA.
  User: shfb_wang
  Date: 2015/10/23 0023 16:37
  Copyright(c) 北京四海富博计算机服务有限公司
--%>
<%--完成确认签约，打印序号单--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="panelBar">
    <ul class="toolBar">
        <%--判断协议是否打印--%>
        <li onclick="ct003.printCt(this, '${hsId}', 'printChHs', ${hsInfo.HouseInfo.HsCtInfo.hsCtId});">
            <a class="print" href="javascript:void(0)"><span>打印序号单</span></a>
        </li>
        <oframe:op prjCd="${param.prjCd}" template="li" cssClass="export" rhtCd="prj_down_ct_ord_rht"
                   name="下载序号单" onClick="ct003.downloadChHsNum('${hsId}');"/>
    </ul>
</div>
<div class="panelcontainer" layoutH="60" style="border: 1px solid #e9e9e9;">
    <table class="border">
        <tr>
            <th width="10%"><label>档案编号：</label></th>
            <td width="22%">${hsInfo.HouseInfo.hsCd}</td>
            <th width="10%"><label>被安置人：</label></th>
            <td width="22%">${hsInfo.HouseInfo.hsOwnerPersons}</td>
            <th><label>安置方式：</label></th>
            <td><oframe:name prjCd="${param.prjCd}" itemCd="10001" value="${hsInfo.HouseInfo.HsCtInfo.ctType}"/></td>
        </tr>
        <tr>
            <th width="10%"><label>房屋地址：</label></th>
            <td colspan="3">${hsInfo.HouseInfo.hsFullAddr}</td>
            <th><label>签约时间：</label></th>
            <td>${ctDate}</td>
        </tr>
    </table>
    <div id="ct003_ReportData" layoutH="130">
    </div>
</div>
<oframe:script src="${pageContext.request.contextPath}/oframe/plugin/pdfobject/pdfobject.js" type="text/javascript"/>
<script type="text/javascript">
    $(document).ready(function () {
        ct003.generateChHsNum(${hsId});
    });
</script>

