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
        <c:if test="${showFlag != 'choose'}">
            <oframe:op prjCd="${param.prjCd}" template="li" cssClass="ensure"
                       name="确认选房" rhtCd="do_chs_hs_rht"
                       onClick="ch001.cfmHsChooseAll();"/>
        </c:if>
        <c:if test="${showFlag == 'choose'}">
            <li onclick="ch001.chInfo(${hsId},'return');">
                <a class="cancel" href="javascript:void(0)"><span>返回</span></a>
            </li>
        </c:if>
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
                <th>配比信息：</th>
                <td>
                    <c:forEach items="${hsTps}" var="item" varStatus="status">
                        <div style="float: left;margin-left: 10px">
                            <c:set var="key" value="${item.key}${''}"/>
                            <c:if test="${key != '4'}">
                                ${item.value}：${tempArr[status.index]}
                            </c:if>
                        </div>
                    </c:forEach>
                </td>
            </tr>
        </table>
        <table class="list mart5" id="yaoXFang" width="100%" style="border-collapse: collapse;">
            <thead>
            <tr>
                <th width="10%">购房地址</th>
                <th width="10%">购房人</th>
                <th width="10%">通讯地址</th>
                <th width="10%">购房人电话</th>
                <th width="10%">购房居室</th>
                <th width="10%">购房状态</th>
                <th width="10%">操作
                    <span class="link marl5 js_delectBtn" onclick="ch001.chooseDetail('${hsId}','gylwqxf','view')">[选房详情]</span>
                </th>
            </tr>
            </thead>
            <c:forEach var="item" items="${chRegList}">
                <c:set var="chooseStatus" value="${item.chooseHs.chooseStatus}${''}"/>
                <c:set var="chooseDrawStatus" value="${item.chooseHs.chooseDrawStatus}${''}"/>
                <tr class="jFang js_xFang" onclick="ch001.choosePerson(this)">

                    <td name="tempAddr">${item.chooseHs.chooseHsAddr}</td>
                    <td>${item.chooseHs.buyPersonName}</td>
                    <td><input name="personNoticeAddr" readonly class="js_editable textInput readonly"
                               value="${item.chooseHs.personNoticeAddr}"/></td>
                    <td><input name="buyPersonTel" readonly class="js_editable textInput readonly"
                               value="${item.chooseHs.buyPersonTel}"/></td>
                    <td>${item.chooseHs.huJush_Name}</td>
                    <td>
                        <span name="chooseStatusText"><oframe:name prjCd="${param.prjCd}" itemCd="CHOOSE_STATUS"
                                                                   value="${chooseStatus}"/></span>
                    </td>
                    <td name="tempTd">
                        <input type="hidden" name="chooseStatus" value="${chooseStatus}"/>
                        <input type="hidden" name="hsCtChooseId" value="${item.chooseHs.hsCtChooseId}"/>
                        <input type="hidden" name="chooseHsAddr" value="${item.chooseHs.chooseHsAddr}"/>
                        <input type="hidden" name="newHsId" value="${item.chooseHs.chooseHsId}"/>
                        <input type="hidden" name="oldNewHsId" value="${item.chooseHs.chooseHsId}"/>
                        <input type="hidden" name="chooseHsRegId" value="${item.chooseHs.chooseHsRegId}"/>
                        <input type="hidden" name="huJush" value="${item.chooseHs.huJush}"/>
                        <input type="hidden" name="buyPersonName" value="${item.chooseHs.buyPersonName}"/>
                        <input type="hidden" name="buyPersonId" value="${item.chooseHs.buyPersonId}"/>
                        <input type="hidden" name="buyPersonCerty" value="${item.chooseHs.buyPersonCerty}"/>
                        <input type="hidden" name="buyPersonTel" value="${item.chooseHs.buyPersonTel}"/>
                        <input type="hidden" name="buyPersonOwnerRel" value="${item.chooseHs.buyPersonOwnerRel}"/>
                        <input type="hidden" name="hsTp" value="${item.chooseHs.huJush}"/>
                        <input type="hidden" name="preBldSize" value="${item.chooseHs.chooseHsSize}"/>
                        <input type="hidden" name="hsHxName" value="${item.chooseHs.chooseHsHxName}"/>
                        <c:if test="${chooseStatus == '2'}">
                            <c:set var="cfmHidFlag" value="hidden"/>
                            <c:set var="cfmHidFlagOther" value=""/>
                            <c:set var="editeBtn" value="hidden"/>
                            <c:set var="saveBtn" value="hidden"/>
                            <c:if test="${chooseDrawStatus =='0'}">
                                <c:set var="drawHidFlag1" value=""/>
                                <c:set var="drawHidFlag2" value="hidden"/>
                            </c:if>
                            <c:if test="${chooseDrawStatus !='0'}">
                                <c:set var="drawHidFlag1" value="hidden"/>
                                <c:set var="drawHidFlag2" value=""/>
                            </c:if>
                        </c:if>
                        <c:if test="${chooseStatus != '2'}">
                            <c:set var="cfmHidFlag" value=""/>
                            <c:set var="cfmHidFlagOther" value="hidden"/>
                            <c:set var="drawHidFlag1" value="hidden"/>
                            <c:set var="drawHidFlag2" value="hidden"/>
                            <c:set var="editeBtn" value=""/>
                            <c:set var="saveBtn" value="hidden"/>
                        </c:if>
                        <oframe:power rhtCd="edite_tx_rht_1" prjCd="${param.prjCd}">
                            <span class="link marl5 js_delectBtn ${editeBtn}" name="editeBtn"
                                  onclick="ch001.startEdite(this)">[编辑]</span>
                            <span class="link marl5 js_delectBtn ${saveBtn}" name="saveBtn"
                                  onclick="ch001.saveNoticeInfo(this)">[保存]</span>
                        </oframe:power>
                        <oframe:power rhtCd="get_confirm_choose" prjCd="${param.prjCd}">
                        <span class="link marl5 js_delectBtn cfmHidFlagOther ${drawHidFlag1}" name="drawConfirm1"
                              onclick="ch001.drawConfirm(this)">[领取确认单]</span>
                        <span class="marl5 js_delectBtn ${drawHidFlag2}" name="drawConfirm2">确认单已领取</span>
                        </oframe:power>
                        <span class="link marl5 js_delectBtn cfmHidFlagOther ${cfmHidFlagOther}"
                              onclick="ch001.createConfirm(this)">[确认单]</span>
                        <oframe:op prjCd="${param.prjCd}" template="span"
                                   cssClass="link marl5 cfmHidFlagOther js_delectBtn ${cfmHidFlagOther}"
                                   name="取消" rhtCd="cancel_chs_hs_rht"
                                   onClick="ch001.cancelHsChoose(this)"/>
                        <oframe:op prjCd="${param.prjCd}" template="span"
                                   cssClass="link marl5 cfmHidFlagOther js_delectBtn ${cfmHidFlagOther}"
                                   name="删除" rhtCd="delete_chs_hs_rht"
                                   onClick="ch001.deleteHsChoose(this)"/>
                        <span class="link marl5 cfmHidFlag js_delectBtn ${cfmHidFlag}"
                              onclick="ch001.cfmHsChoose(this)">[确认]</span>

                    </td>
                </tr>
            </c:forEach>
        </table>
    </form>
</div>

<div id="ch001_ReportData" class="mart5" style="border: 1px solid #e9e9e9;position: relative; ">
    <div class="panelBar" id="ch00101QryDiv">
        <div style="float: left;" class="marl5">
            <span>户型结构：</span>
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
            <input type="hidden" name="huJushFlag" value=""/>
            <input type="hidden" name="chooseHsRegIdFlag" value=""/>

            <button type="button" id="schBtn" class="btn-info" style="margin-left: 5px;height: 25px"
                    onclick="ch001.query(${hsId});">查询
            </button>
        </div>
        <ul class="toolBar">
            <li style="float: right;" onclick="ch001.changeShowModel(this,'3',${hsId});">
                <a class="pic" href="javascript:void(0)"><span class="active">图形</span></a>
            </li>
            <li style="float: right;" id="ch00101ListModel" onclick="ch001.changeShowModel(this, '1',${hsId});">
                <a class="list" href="javascript:void(0)"><span>列表</span></a>
            </li>
            <li style="float: right;" onclick="ch001.changeFull(this);">
                <input type="hidden" name="fullScreen" value="false"/>
                <a class="find" href="javascript:void(0)"><span class="active">全屏</span></a>
            </li>
        </ul>
    </div>
    <div id="ch001001div">
        <form id="ch001001frm" method="post" class="entermode" allowTransparency="false">
            <input type="hidden" class="js_query_model" value="3"/>
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
            <input type="hidden" name="forward" id="forward" value="/eland/ch/ch001/ch001002_list"/>
            <input type="hidden" name="ctType" id="ctType" value="${hsInfo.HouseInfo.HsCtInfo.ctType}"/>
            <input type="hidden" name="cmptName" value="QUERY_NEW_HOUSE_REGION"/>
            <input type="hidden" name="selectedHs"/>
        </form>
        <div class="js_page" style="overflow-y: auto; background: #f9f9f9"></div>
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
    $('td[name=tempTd]', navTab.getCurrentPanel()).click(function (event) {
        event.stopPropagation();
    });
    $(function () {
        $("tr.jFang:first", navTab.getCurrentPanel()).trigger("click").addClass("selected");
    });
</script>