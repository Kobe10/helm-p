<%--延期交房处理进度--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<input type="hidden" name="regUseType" value="${regUseType}">
<%--初始化显示模式--%>
<input type="hidden" name="showFyTree" value="${showFyTree}">
<input type="hidden" name="showFyTreeModel" value="${showFyTreeModel}">
<div class="mar5" style="vertical-align: top;">
    <%--左侧区域树--%>
    <div style="width: 240px;float:left;position: relative;" id="oh001DivTree" layoutH="15" class="panel left_menu">
        <h1>
            <span class="panel_title">楼 宇 导 航</span>
            <span class="item_switch" onclick="oh007.switchTree(this);">&nbsp;</span>
            <span onclick="oh007.extendOrClose(this);" class="panel_menu js_reload">展开</span>
        </h1>
        <ul id="oh007Tree_${regUseType}" class="ztree" layoutH="65"></ul>
        <ul id="oh007HxTree_${regUseType}" class="ztree hidden" layoutH="65"></ul>
    </div>
    <div class="split_line" layoutH="15">
        <span class="split_but" title="展开/隐藏导航"></span>
    </div>

    <%--右侧自定义画板--%>
    <div style="margin-left: 250px;margin-right: 5px;position: relative;"
         id="oh007Right"
         class="js_query_condition">
        <div class="panelBar">
            <ul class="toolBar">
                <li onmousemove="oh007.openQSch(this)"
                    onmouseout="oh007.leaveQSch()"
                    onclick="oh007.queryHs(this);">
                    <a class="find" href="javascript:void(0)"><span>条件检索</span></a>
                </li>
                <li onclick="oh007.refleshHs();">
                    <a class="reflesh" href="javascript:void(0)"><span>刷新检索</span></a>
                </li>
                <oframe:op template="li" prjCd="${param.prjCd}" rhtCd="do_dl_${regUseType}_new_hs_rht"
                           name="延期交房" onClick="oh007.openMarkDelay(this);" cssClass="edit"/>
                <oframe:op template="li" prjCd="${param.prjCd}" rhtCd="do_dl_${regUseType}_new_hs_rht"
                           name="延期通知" onClick="oh007.telDelay(this);" cssClass="new-area"/>
                <oframe:op template="li" prjCd="${param.prjCd}" rhtCd="exp_dl_${regUseType}_new_hs_rht"
                           name="明细导出" onClick="Page.exportExcel('oh007_list_print',false)" cssClass="export"/>
                <oframe:op template="li" prjCd="${param.prjCd}" rhtCd="exp_dl_${regUseType}_new_hs_rht"
                           name="楼宇导出" onClick="oh007.exportbuild()" cssClass="export"/>
                <li style="float: right;" onclick="oh007.changeShowModel(this,'3');">
                    <a class="sum" href="javascript:void(0)"><span>统计</span></a>
                </li>
                <li style="float: right;" onclick="oh007.changeShowModel(this,'2');">
                    <a class="pic" href="javascript:void(0)"><span>图形</span></a>
                </li>
                <li style="float: right;" onclick="oh007.changeShowModel(this, '1');">
                    <a class="list" href="javascript:void(0)"><span class="active">列表</span></a>
                </li>
            </ul>
        </div>
        <div id="oh007QSchDialog" class="hidden"
             style="position: absolute;width:100%;top: 0px;z-index: 100;background-color: #ffffff; margin-top: 40px;">
            <div class="triangle triangle-up" style="border-bottom-color: #2c7bae;top: -10px; left: 26px;"></div>
            <table class="border">
                <tr>
                    <th><label>房屋地址：</label></th>
                    <td><input name="NewHsInfo.hsAddr" condition="like" class="textInput"></td>
                    <th><label>房屋状态：</label></th>
                    <td><input name="NewHsInfo.statusCd" type="hidden" condition="=" class="textInput">
                        <input type="text" style="width: auto;" itemCd="NEW_HS_STATUS"
                               atOption="CODE.getCfgDataOpt" class="autocomplete textInput">
                    </td>
                    <th><label>购房人：</label></th>
                    <td><input name="NewHsInfo.buyPersonName" condition="like" class="textInput"></td>
                </tr>
                <tr>
                    <td colspan="6" align="center">
                        <button onclick="oh007.qSchData();" class="btn btn-primary js_query marr20">查询</button>
                        <button onclick="oh007.closeQSch(true);" class="btn btn-info">关闭</button>
                    </td>
                </tr>
            </table>
        </div>
        <input type="hidden" class="js_query_model" value="1"/>
        <input type="hidden" value="" name="sltRegId">

        <form id="oh00701frm" method="post">
            <input type="hidden" name="conditionName" value="NewHsInfo.statusCd">
            <input type="hidden" name="condition" value="=">
            <input type="hidden" name="conditionValue" value="3">
            <input type="hidden" name="sortColumn" value="NewHsInfo.hsAddr">
            <input type="hidden" name="sortOrder" value="asc">
            <input type="hidden" class="js_conditionValue" value="">
            <input type="hidden" name="resultField"
                   value="NewHsInfo.hsAddr,NewHsInfo.hsHxName,NewHsInfo.preBldSize,NewHsInfo.statusCd,NewHsInfo.buyPersonName,NewHsInfo.handleStatus,NewHsInfo.handHsDate">
            <input type="hidden" name="forceResultField"
                   value="NewHsInfo.newHsId,NewHsInfo.oldHsId,NewHsInfo.statusCd,NewHsInfo.handleStatus">
            <input type="hidden" name="regUseType" value="${regUseType}">
            <input type="hidden" name="rhtType" value="${regUseType}">
            <input type="hidden" name="rhtRegId" value="">
            <input type="hidden" name="rhtHxName" value="">
            <input type="hidden" name="rhtHsTp" value="">
            <input type="hidden" name="entityName" value="NewHsInfo">
            <input type="hidden" name="divId" value="oh007Context">
            <input type="hidden" name="forward" id="forward" value="/eland/oh/oh007/oh007_list"/>
            <input type="hidden" class="js_displayBuildPage" value="/eland/oh/oh007/oh007_build"/>
            <input type="hidden" class="js_moreResultField" value="NewHsInfo.handleStatus,NewHsInfo.oldHsId"/>
            <input type="hidden" class="js_colorItemCd" value="HS_HD_COLOR"/>
            <input type="hidden" class="js_colorRelCode" value="NEW_HS_HD_ST"/>
            <input type="hidden" class="js_showCdFieldName" value="handleStatus"/>


        </form>
        <div id="oh007Context" class="js_page" layoutH="55">
        </div>
    </div>
</div>
<oframe:script src="${pageContext.request.contextPath}/eland/oh/oh007/js/oh007.js" type="text/javascript"/>