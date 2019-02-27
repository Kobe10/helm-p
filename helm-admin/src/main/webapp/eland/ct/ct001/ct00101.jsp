<%--待签约处理--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="panelBar">
    <ul class="toolBar">
        <%--签约状态--%><%--协议打印状态--%>
        <c:set var="hsCtId" value="${hsInfo.HouseInfo.HsCtInfo.hsCtId}"/>
        <c:set var="ctType" value="${hsInfo.HouseInfo.HsCtInfo.ctType}"/>
        <c:set var="ctStatus" value="${hsInfo.HouseInfo.HsCtInfo.ctStatus}"/>
        <c:set var="printCtStatus" value="${hsInfo.HouseInfo.HsCtInfo.printCtStatus}"/>
        <input type="hidden" value="${printCtStatus}" id="paintOrNo">
        <oframe:op prjCd="${param.prjCd}" template="li" cssClass="print"
                   name="打印协议" rhtCd="print_ct_${schemeType}_rht"
                   onClick="ct001.printCt(this, '${hsId}', 'printCt', '${hsCtId}');"/>
        <oframe:op prjCd="${param.prjCd}" template="li" cssClass="export"
                   name="下载协议" rhtCd="down_ct_${schemeType}_rht"
                   onClick="ct001.downloadDoc('${hsId}', '${hsCtId}');"/>
        <%--签约状态--%>
        <c:if test="${ctStatus == '1'}">
            <input type="hidden" name="ctReadNotice"
                   value='<oframe:config prjCd="${param.prjCd}" itemCd="CONTRANT_READ_NOTICE"/>'/>
            <oframe:op prjCd="${param.prjCd}" template="li" cssClass="ensure"
                       name="确认签约" rhtCd="cfm_ct_${schemeType}_rht"
                       onClick="ct001.cfmCt('${hsId}', '${hsCtId}');"/>
        </c:if>
        <c:if test="${ctStatus == '2'}">
            <oframe:op prjCd="${param.prjCd}" template="li" cssClass="cancel"
                       name="取消签约" rhtCd="cancel_ct_${schemeType}_rht"
                       onClick="ct001.cancelCt('${hsId}', '${hsCtId}');"/>
            <oframe:op prjCd="${param.prjCd}" template="li" cssClass="new-area"
                       name="更换安置方式" rhtCd="change_ct_type"
                       onClick="ct001.changeCtType('${hsId}', '${hsCtId}', '${ctType}');"/>
            <c:if test="${printCtStatus == '1'}">
                <li onclick="ct001.ctInfo('${hsId}', 'viewCfm');">
                    <a class="print" href="javascript:void(0)"><span>签约回执单</span></a>
                </li>
            </c:if>
        </c:if>
        <li onclick="ct001.ctInfo('${hsId}', 'viewFj');">
            <a class="photo" href="javascript:void(0)"><span>现场拍照</span></a>
        </li>
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
        <tr>
            <th><label>安置方式：</label></th>
            <td>
                <c:if test="${ctStatus == '1'}">
                    <oframe:select prjCd="${param.prjCd}" value="${ctType}" itemCd="10001"
                                   name="ctType" onChange="ct001.generateCt(${hsId} ,this);"/>
                </c:if>
                <c:if test="${ctStatus == '2'}">
                    <oframe:name prjCd="${param.prjCd}" value="${ctType}" itemCd="10001"/>
                </c:if>
            </td>
            <th><label>签约状态：</label></th>
            <td>
                <oframe:name prjCd="${param.prjCd}" itemCd="CONTRACT_STATUS"
                             value="${hsInfo.HouseInfo.HsCtInfo.ctStatus}"/>
            </td>
        </tr>
    </table>
    <div id="ct001_ReportData" layoutH="135">
    </div>
</div>
<script type="text/javascript">
    $(document).ready(function () {
        ct001.generateCt(${hsId});
    });
</script>

