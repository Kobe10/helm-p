<%--完成确认签约，打印回执单--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="panelBar">
    <ul class="toolBar">
        <c:set var="hsCtId" value="${hsInfo.HouseInfo.HsCtInfo.hsCtId}"/>
        <c:set var="ctType" value="${hsInfo.HouseInfo.HsCtInfo.ctType}"/>
        <c:set var="printReCtStatus" value="${hsInfo.HouseInfo.HsCtInfo.printReCtStatus}"/>
        <li onclick="ct001.printCt(this, ${hsId}, 'printReCt', ${hsCtId});">
            <a class="print" href="javascript:void(0)"><span>打印回执单</span></a>
        </li>
        <oframe:op prjCd="${param.prjCd}" template="li" cssClass="export" rhtCd="prj_down_ct_re_rht"
                   name="下载回执单" onClick="ct001.downloadCtOrder('${hsId}');"/>
        <oframe:op prjCd="${param.prjCd}" template="li" cssClass="print" rhtCd="print_ct_${schemeType}_rht"
                   name="签约协议" onClick="ct001.ctInfo('${hsId}', 'viewDoc');"/>
        <oframe:op prjCd="${param.prjCd}" template="li" cssClass="cancel"
                   name="取消签约" rhtCd="cancel_ct_${schemeType}_rht"
                   onClick="ct001.cancelCt('${hsId}', '${hsCtId}');"/>
        <li onclick="ct001.ctInfo('${hsId}', 'viewFj');">
            <a class="photo" href="javascript:void(0)"><span>现场拍照</span></a>
        </li>
    </ul>
</div>
<div class="panelcontainer" layoutH="57" style="border: 1px solid #e9e9e9;">
    <table class="border">
        <tr>
            <th width="15%"><label>被安置人：</label></th>
            <td width="35%">${hsInfo.HouseInfo.hsOwnerPersons}</td>
            <th width="15%"><label>房屋地址：</label></th>
            <td>${hsInfo.HouseInfo.hsFullAddr}</td>
        </tr>
        <tr>
            <th><label>安置方式：</label></th>
            <td><oframe:name prjCd="${param.prjCd}" itemCd="10001" value="${ctType}"/></td>
            <th><label>签约时间：</label></th>
            <td>
                <c:set var="ctStatus" value="${hsInfo.HouseInfo.HsCtInfo.ctStatus}"/>
                <c:choose>
                    <c:when test="${ctStatus == '2'}">
                        ${ctDate}
                    </c:when>
                    <c:otherwise>
                        <oframe:name prjCd="${param.prjCd}" itemCd="CONTRACT_STATUS"
                                     value="${hsInfo.HouseInfo.HsCtInfo.ctStatus}"/>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
    </table>
    <div id="ct001_ReportData" layoutH="133">
    </div>
</div>
<script type="text/javascript">
    $(document).ready(function () {
        ct001.generateCtOrder(${hsId});
    });
</script>

