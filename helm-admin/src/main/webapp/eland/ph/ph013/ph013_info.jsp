<%--居民信息报表--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="panelBar">
    <ul class="toolBar">
        <input name="hsId" type="hidden" value="${hsId}"/>
        <input name="hsCtId" type="hidden" value="${hsCtId}"/>
        <oframe:op prjCd="${param.prjCd}" template="li" cssClass="print"
                   name="打印报表" rhtCd="prt_hs_rpt_rht"
                   onClick="ph013.printRpt();"/>
        <oframe:op prjCd="${param.prjCd}" template="li" cssClass="export"
                   name="下载报表" rhtCd="dwn_hs_rpt_rht"
                   onClick="ph013.downRpt();"/>
        <li onclick="ph013.viewHouse('${hsId}');">
            <a class="new-area" href="javascript:void(0)"><span>居民详情</span></a>
        </li>
    </ul>
</div>
<div class="panelcontainer" layoutH="57" style="border: 1px solid #e9e9e9;position: relative">
    <table class="border">
        <tr>
            <th width="10%"><label>房屋编号：</label></th>
            <td width="20%">${hsInfo.HouseInfo.hsCd}</td>
            <th width="10%"><label>被安置人：</label></th>
            <td width="20%"><input type="hidden" name="hsOwnerPersons"
                                   value="${hsInfo.HouseInfo.hsOwnerPersons}"/> ${hsInfo.HouseInfo.hsOwnerPersons}</td>
            <th width="10%"><label>房屋地址：</label></th>
            <td width="30%">${hsInfo.HouseInfo.hsFullAddr}</td>
        </tr>
        <tr>
            <th><label>报表选择：</label></th>
            <td colspan="5">
                <oframe:select prjCd="${param.prjCd}" name="hsRptType" onChange="ph013.getRpt();"
                               value="${lockRptType}" itemCd="HS_RPT_DEF"/>
                <span class="link marl5" onClick="ph013.lockRptType()">锁定</span>
            </td>
        </tr>
    </table>
    <div id="ph013_ReportData" layoutH="135">
    </div>
</div>
<script type="text/javascript">
    $(document).ready(function () {
        ph013.getRpt();
    });
</script>