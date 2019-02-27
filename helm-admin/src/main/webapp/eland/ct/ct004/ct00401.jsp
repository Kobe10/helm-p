<%--待签约处理--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="panelBar">
    <ul class="toolBar">
        <%--签约状态--%><%--协议打印状态--%>
        <c:set var="hsCtId" value="${hsInfo.HouseInfo.HsCtInfo.hsCtId}"/>
        <c:set var="ctStatus" value="${hsInfo.HouseInfo.HsCtInfo.ctStatus}"/>
        <c:set var="printCtStatus" value="${hsInfo.HouseInfo.HsCtInfo.printCtStatus}"/>
        <input type="hidden" value="${printCtStatus}" id="paintOrNo">
        <oframe:op prjCd="${param.prjCd}" template="li" cssClass="print"
                   name="打印协议" rhtCd="print_ct_${schemeType}_rht"
                   onClick="ct004.printCt('${hsId}', '${hsCtId}','printCt');"/>
        <oframe:op prjCd="${param.prjCd}" template="li" cssClass="export"
                   name="下载协议" rhtCd="down_ct_${schemeType}_rht"
                   onClick="ct004.downloadDoc('${hsId}', '${hsCtId}');"/>
    </ul>
</div>
<div class="panelcontainer" layoutH="57" style="border: 1px solid #e9e9e9;position: relative">

    <table class="border">
        <tr>
            <th width="15%"><label>被安置人：</label></th>
            <td width="35%">${hsInfo.HouseInfo.hsOwnerPersons}</td>
            <th width="15%"><label>房屋地址：</label></th>
            <td>${hsInfo.HouseInfo.hsFullAddr}</td>
        </tr>
        <tr id="ct00401_${hsCtId}">
            <th><label>安置方式：</label></th>
            <td>
                <input type="hidden" name="hsId" value="${hsId}"/>
                <input type="hidden" name="hsCtId" value="${hsCtId}"/>
                <c:if test="${ctStatus == '1'}">
                    <oframe:select prjCd="${param.prjCd}" value="${hsInfo.HouseInfo.HsCtInfo.ctType}" itemCd="10001"
                                   name="ctType" onChange="ct004.sltChgCtType(this);"/>
                </c:if>
                <c:if test="${ctStatus == '2'}">
                    <oframe:name prjCd="${param.prjCd}" value="${hsInfo.HouseInfo.HsCtInfo.ctType}" itemCd="10001"/>
                </c:if>
            </td>
            <th><label>签约状态：</label></th>
            <td>
                <oframe:name prjCd="${param.prjCd}" itemCd="CONTRACT_STATUS"
                             value="${hsInfo.HouseInfo.HsCtInfo.ctStatus}"/>
            </td>
        </tr>
    </table>
    <div id="ct004_ReportData" layoutH="135">
    </div>
</div>
<script type="text/javascript">
    $(document).ready(function () {
        ct004.generateCt('${hsId}', '${hsCtId}');
    });
</script>

