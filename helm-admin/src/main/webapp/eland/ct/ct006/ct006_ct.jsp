<%--待签约处理--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="panelBar">
    <ul class="toolBar">
        <c:set var="hsCtId" value="${hsInfo.HouseInfo.HsCtInfo.hsCtId}"/>
        <c:set var="ctStatus" value="${ctStatus}"/>
        <oframe:op cssClass="print" rhtCd="print_ct_${schemeType}_rht" template="li" name="打印协议"
                   prjCd="${param.prjCd}" onClick="ct006.printCt(this, 'printCt', '${hsCtId}');"/>

        <oframe:op cssClass="export" rhtCd="down_ct_${schemeType}_rht" template="li" name="下载协议"
                   prjCd="${param.prjCd}" onClick="ct006.downloadCt('${hsCtId}','${oldHsId}', '${newHsId}');"/>
        <c:if test="${ctStatus == '1'}">
            <oframe:op cssClass="ensure" rhtCd="cfm_ct_${schemeType}_rht" template="li" name="确认签约"
                       prjCd="${param.prjCd}" onClick="ct006.cfmCt('${hsCtId}','${oldHsId}', '${newHsId}');"/>

        </c:if>
        <c:if test="${ctStatus == '2'}">
            <oframe:op cssClass="cancel" rhtCd="cancel_ct_${schemeType}_rht" template="li" name="取消签约"
                       prjCd="${param.prjCd}" onClick="ct006.cancelCt('${hsCtId}','${oldHsId}', '${newHsId}');"/>
        </c:if>
        <li onclick="ct006.ctInfo('${hsId}', '${newHsId}', 'viewFj');">
            <a class="photo" href="javascript:void(0)"><span>现场拍照</span></a>
        </li>
        <li onclick="ct006.viewHouse(${hsId});">
            <a class="new-area" href="javascript:void(0)"><span>居民详情</span></a>
        </li>
    </ul>
</div>
<div class="panelcontainer" layoutH="57" style="border: 1px solid #e9e9e9;position: relative">
    <table class="border">
        <tr>
            <th width="10%"><label>房屋编号：</label></th>
            <td width="15%">${hsInfo.HouseInfo.hsCd}</td>
            <th width="10%"><label>被安置人：</label></th>
            <td width="20%">${hsInfo.HouseInfo.hsOwnerPersons}</td>
            <th width="12%"><label>原房屋地址：</label></th>
            <td width="30%">${hsInfo.HouseInfo.hsFullAddr}</td>
        </tr>
        <tr>
            <th><label>选房序号：</label></th>
            <td>${hsInfo.HouseInfo.HsCtInfo.chooseHsSid}</td>
            <th><label>选房时间：</label></th>
            <td><oframe:date value="${newHsInfo.Row.statusDate}"/></td>
            <th><label>安置房地址：</label></th>
            <td>${newHsInfo.Row.hsAddr}</td>
        </tr>
    </table>
    <div id="ct006_ReportData" layoutH="135">
    </div>
</div>
<script type="text/javascript">
    $(document).ready(function () {
        ct006.generateCt('${oldHsId}', '${hsCtId}', '${newHsId}');
    });
</script>

