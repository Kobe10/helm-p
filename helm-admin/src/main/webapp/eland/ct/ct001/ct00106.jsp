<%--完成确认签约，打印序号单--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="panelBar">
    <ul class="toolBar">
        <%--判断协议是否打印--%>
        <li onclick="ct001.printCt(this, ${hsId}, 'printChHs', ${hsInfo.HouseInfo.HsCtInfo.hsCtId});">
            <a class="print" href="javascript:void(0)"><span>打印序号单</span></a>
        </li>
        <oframe:op prjCd="${param.prjCd}" template="li" cssClass="export" rhtCd="prj_down_ct_ord_rht"
                   name="下载序号单" onClick="ct001.downloadChHsNum('${hsId}');"/>
        <li onclick="ct001.ctInfo('${hsId}', 'viewDoc');">
            <a class="save" href="javascript:void(0)"><span>签约协议</span></a>
        </li>
        <li onclick="ct001.ctInfo('${hsId}', 'viewFj');">
            <a class="photo" href="javascript:void(0)"><span>现场拍照</span></a>
        </li>
    </ul>
</div>
<div class="panelcontainer" layoutH="55" style="border: 1px solid #e9e9e9;">
    <table class="border">
        <tr>
            <th width="15%"><label>被安置人：</label></th>
            <td width="35%">${hsInfo.HouseInfo.hsOwnerPersons}</td>
            <th width="15%"><label>房屋地址：</label></th>
            <td>${hsInfo.HouseInfo.hsFullAddr}</td>
        </tr>
        <tr>
            <th><label>安置方式：</label></th>
            <td><oframe:name prjCd="${param.prjCd}" itemCd="10001" value="${hsInfo.HouseInfo.HsCtInfo.ctType}"/></td>
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
<oframe:script src="${pageContext.request.contextPath}/oframe/plugin/pdfobject/pdfobject.js" type="text/javascript"/>
<script type="text/javascript">
    $(document).ready(function () {
        ct001.generateChHsNum(${hsId});
    });
</script>

