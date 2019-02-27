<%--待签约处理--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="panelBar">
    <ul class="toolBar">
        <c:set var="hsJsId" value="${hsInfo.HouseInfo.HsCtInfo.hsJsId}"/>
        <c:set var="hsCtId" value="${hsInfo.HouseInfo.HsCtInfo.hsCtId}"/>
        <c:set var="ctStatus" value="${ctStatus}"/>
        <%--打印协议--%>
        <input type="hidden" value="${printJsStatus}" id="paintOrNo">
        <oframe:op prjCd="${param.prjCd}" rhtCd="print_ct_${schemeType}_rht" cssClass="print"
                   template="li" name="打印协议" onClick="ct005.printCt(this, ${hsId}, 'printCt', ${hsCtId});"/>
        <%--下载协议--%>
        <oframe:op prjCd="${param.prjCd}" rhtCd="down_ct_${schemeType}_rht" cssClass="export"
                   template="li" name="下载协议" onClick="ct005.downloadCt(${hsId},${hsCtId},'3');"/>
        <%--确认签约,未签约可用--%>
        <c:if test="${ctStatus == '1'}">
            <oframe:op prjCd="${param.prjCd}" rhtCd="cfm_ct_${schemeType}_rht" cssClass="ensure"
                       template="li" name="确认签约" onClick="ct005.cfmCt('${hsId}','${hsCtId}');"/>
        </c:if>
        <%--取消签约,已签约可用--%>
        <c:if test="${ctStatus == '2'}">
            <oframe:op prjCd="${param.prjCd}" rhtCd="cancel_ct_${schemeType}_rht" cssClass="cancel"
                       template="li" name="取消签约" onClick="ct005.cancelCt('${hsId}','${hsCtId}');"/>
        </c:if>
        <li onclick="ct005.photograph('${hsId}', '${hsCtId}');">
            <a class="photo" href="javascript:void(0)"><span>现场拍照</span></a>
        </li>
        <li onclick="ct005.viewHouse('${hsId}');">
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
            <td width="20%">${hsInfo.HouseInfo.hsOwnerPersons}</td>
            <th width="10%"><label>房屋地址：</label></th>
            <td width="30%">${hsInfo.HouseInfo.hsFullAddr}</td>
        </tr>
        <tr>
            <th><label>安置方式：</label></th>
            <td>
                <oframe:name prjCd="${param.prjCd}" value="${hsInfo.HouseInfo.HsCtInfo.ctType}" itemCd="10001"/>
            </td>
            <th><label>签约状态：</label></th>
            <td>
                <oframe:name prjCd="${param.prjCd}" itemCd="CONTRACT_STATUS" value="${ctStatus}"/>
            </td>
            <th><label>签约时间：</label></th>
            <td>${ctDate}</td>
        </tr>
    </table>
    <div id="ct005_ReportData" class="layoutBox" layoutH="135">
    </div>
</div>
<script type="text/javascript">
    $(document).ready(function () {
        ct005.generateCt('${hsId}', '${hsCtId}');
    });
</script>

