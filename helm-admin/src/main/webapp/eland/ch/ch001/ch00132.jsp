<%--待签约处理--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="panelBar">
    <ul class="toolBar">
        <c:set var="ctType" value="${hsInfo.HouseInfo.HsCtInfo.ctType}${''}"/>
        <c:if test="${ctType != '2'}">
            <oframe:op prjCd="${param.prjCd}" template="li" cssClass="cancel"
                       name="取消选房" rhtCd="cancel_ch_cfm_rht"
                       onClick="ch001.quXiao();"/>
        </c:if>
        <c:if test="${ctType == '2'}">
            <oframe:op prjCd="${param.prjCd}" template="li" cssClass="cancel"
                       name="取消选房" rhtCd="cancel_ch_cfm_rht"
                       onClick="ch001.quXiaoChoose();"/>
            <li onclick="ch001.chInfo(${hsId},'choose');">
                <a class="print" href="javascript:void(0)"><span>选房详情</span></a>
            </li>
        </c:if>
        <oframe:op prjCd="${param.prjCd}" template="li" cssClass="js_qren print"
                   name="打印确认单" rhtCd="go_ch_rht"
                   onClick="ch001.printCh('${hsId}');"/>
        <oframe:op prjCd="${param.prjCd}" template="li" cssClass="export"
                   name="下载确认单" rhtCd="prj_down_ct_rht"
                   onClick="ch001.downloadCh('${hsId}');"/>
        <li onclick="ch001.viewHouse(${hsId});">
            <a class="new-area" href="javascript:void(0)"><span>居民详情</span></a>
        </li>

        <li onclick="ch001.singleQuery(${hsInfo.HouseInfo.HsCtInfo.chooseHsSid},'next');" style="float: right">
            <a class="export" href="javascript:void(0)"><span>下一户</span></a>
        </li>
        <li onclick="ch001.singleQuery(${hsInfo.HouseInfo.HsCtInfo.chooseHsSid},'last');" style="float: right">
            <a class="import" href="javascript:void(0)"><span>上一户</span></a>
        </li>
    </ul>
</div>
<div class="panelcontainer" id="ch001Head" style="border: 1px solid #e9e9e9;">
    <input type="hidden" name="hidHsId" value="${hsId}">
    <input type="hidden" name="hsCtId" value="${hsInfo.HouseInfo.HsCtInfo.hsCtId}">
    <input type="hidden" id="ctType" value="${ctType}">
    <table class="border">
        <tr>
            <th width="7%"><label>档案编号：</label></th>
            <td width="10%">${hsInfo.HouseInfo.hsCd}</td>
            <th width="7%"><label>被安置人：</label></th>
            <td width="15%">${hsInfo.HouseInfo.hsOwnerPersons}</td>
            <th width="7%"><label>房屋地址：</label></th>
            <td width="20%">${hsInfo.HouseInfo.hsFullAddr}</td>
        </tr>
        <tr>
            <th><label>安置方式：</label></th>
            <td>
                <input type="hidden" name="ctType" value="${ctType}"/>
                <oframe:name prjCd="${param.prjCd}" value="${ctType}"
                             itemCd="10001"/>
            </td>
            <th><label>选房状态：</label></th>
            <td>
                <oframe:name prjCd="${param.prjCd}" value="${hsInfo.HouseInfo.HsCtInfo.chHsStatus}"
                             itemCd="CHOOSE_STATUS"/>
            </td>
            <th>选定套餐：</th>
            <td>
                <c:forEach items="${chpMap}" var="item" varStatus="status">
                    <div style="float: left;margin-left: 10px">
                            ${item.key}：${item.value}
                    </div>
                </c:forEach>
            </td>
        </tr>
    </table>
    <table class="list mart5" width="100%" style="border-collapse: collapse">
        <thead>
        <tr>
            <th>购房地址</th>
            <th>预测建面</th>
            <th>购房人</th>
            <th>与产承人关系</th>
            <th>购房证件</th>
            <th>联系电话</th>
        </tr>
        </thead>
        <c:forEach items="${hsInfos}" var="item" varStatus="varStatus">
            <c:if test="${item.HouseInfo.HsCtInfo.HsCtChooseInfo.chooseStatus=='2'}">
                <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}"/>
                <tr class="js_yiXFang">
                    <input type="hidden" name="hsCtChooseId"
                           value="${item.HouseInfo.HsCtInfo.HsCtChooseInfo.hsCtChooseId}">
                    <td>${item.HouseInfo.HsCtInfo.HsCtChooseInfo.chooseHsAddr}</td>
                    <td>${item.HouseInfo.HsCtInfo.HsCtChooseInfo.chooseHsSize}㎡</td>
                    <td>${item.HouseInfo.HsCtInfo.HsCtChooseInfo.buyPersonName}</td>
                    <td>${item.HouseInfo.HsCtInfo.HsCtChooseInfo.buyPersonOwnerRel}</td>
                    <td>${item.HouseInfo.HsCtInfo.HsCtChooseInfo.buyPersonCerty}</td>
                    <td>${item.HouseInfo.HsCtInfo.HsCtChooseInfo.buyPersonTel}</td>
                </tr>
            </c:if>
        </c:forEach>
    </table>
</div>
<div id="ch001_ReportData" style="overflow:auto;"></div>
<script type="text/javascript">
    $(document).ready(function () {
        ch001.adjustHeight();
        ch001.generateCh(${hsId});
    });
    // 清除历史定时器
    clearTimeout(index.ch001Countdown);
</script>
