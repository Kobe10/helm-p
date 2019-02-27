<%--待签约处理--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<style>
    #ch00101_st {
        font-size: 30px !important;
        color: #ff0000;
    }
</style>
<div class="panelBar">
    <input type="hidden" name="spaceTime" value="${spaceTime}">
    <ul class="toolBar marl5 marr5">
            <oframe:op prjCd="${param.prjCd}" template="li" cssClass="ensure"
                       name="确认选房" rhtCd="do_chs_hs_rht"
                       onClick="ch001.chQren();"/>
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

<div id="ch001Head" style="border: 1px solid #e9e9e9;">
    <form id="ch00101ChsForm">
        <input type="hidden" name="hidHsId" value="${hsId}">
        <input type="hidden" name="chooseHsSid" value="${hsInfo.HouseInfo.HsCtInfo.chooseHsSid}">
        <input type="hidden" name="hsCtId" value="${hsInfo.HouseInfo.HsCtInfo.hsCtId}">
        <input type="hidden" name="canChooseNum" value="${houseNum}">
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
                    <oframe:name prjCd="${param.prjCd}" value="${hsInfo.HouseInfo.HsCtInfo.ctType}"
                                 itemCd="10001"/>
                </td>
                <th><label>选房序号：</label></th>
                <td>${hsInfo.HouseInfo.HsCtInfo.chooseHsSid}</td>
                <th><label>控制信息：</label></th>
                <td>套数:【${houseNum}】 面积:【${areaSize}】 户型: 【${houseType}】</td>
            </tr>
        </table>
        <table class="list mart5" id="yaoXFang" width="100%" style="border-collapse: collapse;">
            <thead>
            <tr>
                <th width="20%">购房地址</th>
                <th width="10%">购房人</th>
                <th width="10%">与产承人关系</th>
                <th width="20%">购房人证件</th>
                <th width="20%">联系电话</th>
                <th width="10%">操作</th>
            </tr>
            </thead>
            <tr class="jFang js_xFang">
                <td id="hsAddrId"></td>
                <td>
                    <input type="hidden" class="js_hs_info" name="chooseHsAddr" value="">
                    <input type="hidden" class="js_hs_info" name="newHsId" value="">
                    <input type="hidden" class="js_hs_info" name="hsCtChooseId" value="">
                    <input type="hidden" name="buyPersonId" value="${ownerInfo.Row.personId}">
                    <input type="hidden" class="js_hs_info" name="hsHxName" value="">
                    <input type="hidden" class="js_hs_info" name="hsTp" value="">
                    <input type="hidden" class="js_hs_info" name="preBldSize" value="">
                    <input name="buyPersonName" type="text" atOption="ch001.getOption" readonly
                           class="readonly js_editable"
                           atUrl="ch001.getUrl" class="autocomplete required" value="${ownerInfo.Row.personName}"/>
                </td>
                <td>
                    <input name="buyPersonOwnerRel" readonly class="readonly js_editable" type="text"
                           value="${ownerInfo.Row.buyPersonOwnerRel}"/>
                </td>
                <td>
                    <input name="buyPersonCertyNum" type="text" readonly class="readonly js_editable"
                           value="${ownerInfo.Row.personCertyNum}"/>
                </td>
                <td>
                    <input name="buyPersonTelphone" type="text" readonly class="readonly js_editable"
                           value="${ownerInfo.Row.personTelphone}"/>
                </td>
                <td>
                    <oframe:op prjCd="${param.prjCd}" template="span" cssClass="link mar js_delectBtn"
                               name="修改" rhtCd="edit_buy_person"
                               onClick="ch001.editRow(this)"/>
                    <span class="link marl5 js_delectBtn" onclick="ch001.deleteRow(this)">[删除]</span>
                </td>
            </tr>
        </table>
    </form>
</div>

<div id="ch001_ReportData" class="mart5" style="border: 1px solid #e9e9e9;position: relative; ">
    <div class="panelBar" id="ch00101QryDiv">
        <div style="float: left;" class="marl5">
            <span>居室：</span>
            <span>
                <input type="hidden" name="hsTp"/>
                <input type="text" style="width: 50px;height: 17px;" itemcd="HS_ROOM_TYPE"
                       atoption="ch001.getCfgDataOpt" class="autocomplete textInput acInput">
            </span>
            <span class="marl5">户型：</span>
            <input type="text" name="hsHxName" style="width: 50px;height: 17px;"/>

            <span class="marl5">面积：</span>
            <input type="text" name="minSize" style="width: 50px;height: 17px;"/>
            <span class="marr5 marl5">-</span>
            <input type="text" name="maxSize" style="width: 50px;height: 17px;"/>

            <span class="marl5">地址：</span>
            <input type="text" name="chooseHsAddr" style="width: 150px; height: 17px;"/>

            <button type="button" id="schBtn" class="btn-info" style="margin-left: 5px;height: 25px"
                    onclick="ch001.query(${hsId});">查询
            </button>
        </div>
        <ul class="toolBar">
            <li style="float: right;" onclick="ch001.changeShowModel(this,'2',${hsId});">
                <a class="pic" href="javascript:void(0)"><span>图形</span></a>
            </li>
            <li style="float: right;" id="ch00101ListModel" onclick="ch001.changeShowModel(this, '1',${hsId});">
                <a class="list" href="javascript:void(0)"><span class="active">列表</span></a>
            </li>
        </ul>
    </div>
    <div id="ch001001div">
        <form id="ch001001frm" method="post" class="entermode" allowTransparency="false">
            <input type="hidden" class="js_query_model" value="1"/>
            <%--调用非框架组件同时按照框架组件增加权限控制部分逻辑--%>
            <input type="hidden" name="addRightFilter" value="true"/>
            <input type="hidden" name="regUseType" value="">
            <input type="hidden" name="rhtType" value="">
            <input type="hidden" name="entityName" value="NewHsInfo"/>
            <input type="hidden" name="conditionName" value="">
            <input type="hidden" name="condition" value="">
            <input type="hidden" name="conditionValue" value="">
            <input type="hidden" name="sortColumn" value="NewHsInfo.hsAddr">
            <input type="hidden" name="sortOrder" value="asc">
            <input type="hidden" class="js_conditionValue" value="">
            <input type="hidden" name="forceResultField" value="NewHsInfo.newHsId">
            <input type="hidden" name="divId" value="ch001001_list_print">
            <input type="hidden" name="resultField"
                   value="NewHsInfo.hsAddr,NewHsInfo.hsHxName,NewHsInfo.hsTp,NewHsInfo.hsDt,NewHsInfo.preBldSize">
            <input type="hidden" name="forward" id="forward" value="/eland/ch/ch001/ch001001_list"/>
            <input type="hidden" name="ctType" id="ctType" value="${hsInfo.HouseInfo.HsCtInfo.ctType}"/>
            <input type="hidden" name="cmptName" value="QUERY_NEW_HOUSE_REGION"/>
            <input type="hidden" name="selectedHs"/>
        </form>
        <div class="js_page" style="overflow-y: auto;"></div>
        <c:if test="${startCd != '3' && startCd != ''}">
            <div style="position: absolute;bottom: 30px; right:10px;width: 250px;background-color: white;">
                <table class="border" width="100%">
                    <tr>
                        <td style="text-align: center;background-color: #3d91c8;font-weight: bolder;color: white;">
                            <c:choose>
                                <c:when test="${startCd == '1'}">
                                    本次选房倒计时
                                    <span class="link marr5 js_count_control" onclick="ch001.countDown('start');"
                                          style="color: yellow;float: right;">[启动计时]</span>
                                </c:when>
                                <c:otherwise>
                                    提示
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                    <tr>
                        <td style="text-align: center;min-height: 80px;">
                            <c:choose>
                                <c:when test="${startCd == '1'}">
                                    <span class="countTime" id="ch00101_st">${spaceTime}分0秒</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="countTime" id="ch00101_st">选房未启动</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </table>
            </div>
        </c:if>
    </div>
</div>
<script type="text/javascript">
    // 清除历史定时器
    clearTimeout(index.ch001Countdown);
    $(document).ready(function(){
        ch001.query(${hsId});
    });
</script>