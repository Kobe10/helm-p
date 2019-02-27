<%--待签约处理--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="panelBar">
    <ul class="toolBar">
        <%--签约状态、协议打印状态--%>
        <c:set var="hsCtId" value="${hsInfo.HouseInfo.HsCtInfo.hsCtId}"/>
        <c:set var="ctStatus" value="${hsInfo.HouseInfo.HsCtInfo.ctStatus}"/>
        <c:set var="ctType" value="${hsInfo.HouseInfo.HsCtInfo.ctType}"/>
        <c:set var="printCtStatus" value="${hsInfo.HouseInfo.HsCtInfo.printCtStatus}"/>
        <input type="hidden" value="${printCtStatus}" id="paintOrNo">
        <input type="hidden" value="${hsCtId}" name="htId">
        <input type="hidden" value="${hsCtId}" name="toHsCtId">
        <input type="hidden" value="${ctType}" name="toCtType">
        <input type="hidden" value="" name="tokenId">
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
                    <a class="save" href="javascript:void(0)"><span>签约回执单</span></a>
                </li>
            </c:if>
        </c:if>
        <li onclick="ct001.ctInfo('${hsId}', 'viewFj');">
            <a class="photo" href="javascript:void(0)"><span>现场拍照</span></a>
        </li>
        <oframe:op prjCd="${param.prjCd}" template="li" cssClass="add"
                   name="添加签约人" rhtCd="add_person_ct_rht"
                   onClick="ct001.addCtPerson(${hsCtId});"/>
    </ul>
</div>
<div class="panelcontainer" style="border: 1px solid #e9e9e9;position: relative">
    <table class="table" layoutH="90" width="100%">
        <thead>
        <tr>
            <th width="15%"><label>档案编号</label></th>
            <th width="10%"><label>被安置人</label></th>
            <th width="15%"><label>房屋地址</label></th>
            <th width="15%"><label>房屋类型</label></th>
            <th width="15%"><label>产权类型</label></th>
            <th width="15%"><label>安置方式</label></th>
            <th width="15%">操作</th>
        </tr>
        </thead>
        <tbody id="ct00107Table">
        <tr>
            <td>${hsInfo.HouseInfo.hsCd}</td>
            <td>${hsInfo.HouseInfo.hsOwnerPersons}</td>
            <td>${hsInfo.HouseInfo.hsFullAddr}</td>
            <td>
                <oframe:name prjCd="${param.prjCd}" value="${hsInfo.HouseInfo.hsType}" itemCd="HS_TYPE"/>
            </td>
            <td>
                <oframe:name prjCd="${param.prjCd}" value="${hsInfo.HouseInfo.hsOwnerType}" itemCd="HS_OWNER_TYPE"/>
            </td>
            <td>
                <c:if test="${ctStatus == '1'}">
                    <oframe:select prjCd="${param.prjCd}" value="" withEmpty="true"
                                   itemCd="10001" name="ctType" onChange="ct001.sltChgCtType(this);"/>
                </c:if>
                <c:if test="${ctStatus == '2'}">
                    <oframe:select prjCd="${param.prjCd}" withEmpty="true" cssClass="hidden" value="${ctType}"
                                   itemCd="10001" name="ctType"/>
                    <oframe:name prjCd="${param.prjCd}" value="${ctType}" itemCd="10001"/>
                </c:if>
            </td>
            <td>
                <input type="hidden" name="hsCtId" value="${hsCtId}">
                <input type="hidden" name="hsId" value="${hsId}">
                <input type="hidden" name="printCtStatus" value="${printCtStatus}">
                <input type="hidden" name="ctType" value="${ctType}">
                <oframe:op prjCd="${param.prjCd}" template="a" cssClass="print"
                           name="打印协议" rhtCd="print_ct_${schemeType}_rht"
                           onClick="ct001.printCtByRow(this,'printCt');"/>
                <oframe:op prjCd="${param.prjCd}" template="a" cssClass="export"
                           name="下载协议" rhtCd="down_ct_${schemeType}_rht"
                           onClick="ct001.downloadDoc(this);"/>
            </td>
        </tr>
        <tr class="hidden">
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td>
                <c:if test="${ctStatus == '1'}">
                    <oframe:select prjCd="${param.prjCd}" value="" withEmpty="true"
                                   itemCd="10001" name="ctType" onChange="ct001.generateCt(this);"/>
                </c:if>
                <c:if test="${ctStatus == '2'}">
                    <oframe:select prjCd="${param.prjCd}" value="" withEmpty="true" cssClass="hidden"
                                   itemCd="10001" name="ctType" onChange="ct001.generateCt(this);"/>
                    <oframe:name prjCd="${param.prjCd}" value="${ctType}" itemCd="10001"/>
                </c:if>
            </td>
            <td>
                <input type="hidden" name="hsCtId" value="">
                <input type="hidden" name="hsId" value="">
                <input type="hidden" name="ctType" value="">
                <input type="hidden" name="printCtStatus" value="">
                    <oframe:op prjCd="${param.prjCd}" template="a" cssClass="link marl5"
                               name="打印协议" rhtCd="print_ct_${schemeType}_rht"
                               onClick="ct001.printCtByRow(this,'printCt');"/>

                    <oframe:op prjCd="${param.prjCd}" template="a" cssClass="link"
                               name="下载协议" rhtCd="down_ct_${schemeType}_rht"
                               onClick="ct001.downloadDoc(this);"/>
            </td>
        </tr>
        </tbody>
    </table>
</div>

