<%--安置外迁房源--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="pageNav">
    <a class="current js_hs_type_name">
        <oframe:name prjCd="${param.prjCd}" itemCd="REG_USE_TYPE"
                     value="${regUseType}"/>
    </a>
    <input type="hidden" name="regUseType" value="${regUseType}">
    <%--初始化显示模式--%>
    <input type="hidden" name="showFyTree" value="${showFyTree}">
    <input type="hidden" name="showFyTreeModel" value="${showFyTreeModel}">

</div>
<oframe:power prjCd="${param.prjCd}" rhtCd="edit_${regUseType}_reg_rht">
    <input type="hidden" name="editAble" value="1"/>
</oframe:power>
<oframe:power prjCd="${param.prjCd}" rhtCd="del_${regUseType}_reg_rht">
    <input type="hidden" name="removeAble" value="1"/>
</oframe:power>
<oframe:power prjCd="${param.prjCd}" rhtCd="move_${regUseType}_reg_rht">
    <input type="hidden" name="moveAble" value="1"/>
</oframe:power>
<div class="mar5" style="vertical-align: top;">
    <%--左侧区域树--%>
    <div style="width: 240px;float:left;position: relative;" id="oh001DivTree" layoutH="15" class="panel left_menu">
        <h1>
            <span class="panel_title">楼 宇 导 航</span>
            <span class="item_switch" onclick="oh001.switchTree(this);">&nbsp;</span>
            <span onclick="oh001.extendOrClose(this);" class="panel_menu js_reload">展开</span>
        </h1>
        <ul id="oh001Tree_${regUseType}" class="ztree" layoutH="65"></ul>
        <ul id="oh001HxTree_${regUseType}" class="ztree hidden" layoutH="65"></ul>
    </div>
    <div class="split_line" layoutH="15">
        <span class="split_but" title="展开/隐藏导航"></span>
    </div>

    <%--右侧自定义画板--%>
    <div style="margin-left: 250px;margin-right: 5px;position: relative;"
         id="oh001Right"
         class="js_query_condition">
        <div class="panelBar">
            <ul class="toolBar">
                <li onclick="oh001.openQSch(this);">
                    <a class="find" href="javascript:void(0)"><span>条件检索</span></a>
                </li>
                <li onclick="oh001.refleshHs();">
                    <a class="reflesh" href="javascript:void(0)"><span>刷新检索</span></a>
                </li>
                <li style="display: none;" onclick="oh001.opHsHx(this)">
                    <a class="new-house">
                        <span>户型管理</span>
                    </a>
                </li>
                <oframe:op prjCd="${param.prjCd}" template="li" cssClass="edit"
                           name="批量修改" rhtCd="edit_${regUseType}_new_hs_rht"
                           onClick="oh001.batchChange(this);"/>
                <li>
                    <a class="import" href="javascript:oh001.importHouse();"><span>房源导入</span></a>
                </li>

                <oframe:op prjCd="${param.prjCd}" template="li" cssClass="export"
                           name="修改导出" rhtCd="exp_${regUseType}_new_hs_rht"
                           onClick="Page.exportExcelByTemplate('oh00101_list_print',
                    'webroot:eland/oh/oh001/export.xlsx',
                    'NewHsInfo.newHsId,NewHsInfo.hsAddr,NewHsInfo.hsSt,NewHsInfo.hsFl,NewHsInfo.hsNo,NewHsInfo.hsNm,NewHsInfo.preBldSize,NewHsInfo.hsBldSize,NewHsInfo.hsUseSize,NewHsInfo.hsSalePrice,NewHsInfo.hsHxName,NewHsInfo.hsTp,NewHsInfo.hsDt,NewHsInfo.aplyHsDate,NewHsInfo.handHsDate,NewHsInfo.wyUnPrice,NewHsInfo.gnUnPrice,NewHsInfo.statusCd,NewHsInfo.hsJush,NewHsInfo.hsUnPrice,NewHsInfo.hsUn')"/>
                <oframe:op prjCd="${param.prjCd}" template="li" cssClass="export"
                           name="明细导出" rhtCd="exp_${regUseType}_new_hs_rht"
                           onClick="Page.exportExcel('oh00101_list_print',false)"/>
                <oframe:op prjCd="${param.prjCd}" template="li" cssClass="export"
                           name="楼宇销控" rhtCd="exp_${regUseType}_new_hs_rht"
                           onClick="oh001.exportbuild()"/>
                <oframe:op prjCd="${param.prjCd}" template="li" cssClass="export"
                           name="户型销控" rhtCd="exp_${regUseType}_new_hs_rht"
                           onClick="oh001.exportByHx()"/>
                <oframe:op prjCd="${param.prjCd}" template="li" cssClass="export"
                           name="居室销控" rhtCd="exp_${regUseType}_new_hs_rht"
                           onClick="oh001.exportByJs()"/>

                <li><a class="print" href="javascript:$.printBox('oh00101_list_print')"><span>打印</span></a></li>
                <li style="float: right;" onclick="oh001.changeShowModel(this,'3');">
                    <a class="sum" href="javascript:void(0)"><span>统计</span></a>
                </li>
                <li style="float: right;" onclick="oh001.changeShowModel(this,'2');">
                    <a class="pic" href="javascript:void(0)"><span>图形</span></a>
                </li>
                <li style="float: right;" onclick="oh001.changeShowModel(this, '1');">
                    <a class="list" href="javascript:void(0)"><span class="active">列表</span></a>
                </li>
            </ul>
        </div>
        <div id="oh001QSchDialog" class="hidden"
             style="position: absolute;width:100%;top: 0px;z-index: 100;background-color: #ffffff; margin-top: 40px;">
            <div class="triangle triangle-up" style="border-bottom-color: #2c7bae;top: -10px; left: 26px;"></div>
            <jsp:include page="/oframe/common/page/page-favList.gv">
                <jsp:param name="entityName" value="NewHsInfo"/>
                <jsp:param name="tabOpCode" value="${param.opCode}"/>
                <jsp:param name="callBack" value="oh001.clickFav"/>
            </jsp:include>
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
                        <button onclick="oh001.qSchData();" class="btn btn-primary js_query marr20">查询</button>
                        <button onclick="oh001.closeQSch(true);" class="btn btn-info">关闭</button>
                        <button onclick="oh001.queryHs(this);" class="btn btn-warn marl20">高级</button>
                    </td>
                </tr>
            </table>
        </div>
        <input type="hidden" class="js_query_model" value="1"/>
        <input type="hidden" value="" name="sltRegId">

        <form id="oh00101frm" method="post">
            <input type="hidden" name="conditionName" value="">
            <input type="hidden" name="condition" value="">
            <input type="hidden" name="conditionValue" value="">
            <input type="hidden" name="sortColumn" value="NewHsInfo.hsAddr">
            <input type="hidden" name="sortOrder" value="asc">
            <input type="hidden" class="js_conditionValue" value="">
            <input type="hidden" name="resultField"
                   value="NewHsInfo.hsAddr,NewHsInfo.hsHxName,NewHsInfo.hsTp,NewHsInfo.hsTp,NewHsInfo.hsDt,NewHsInfo.preBldSize,NewHsInfo.statusCd">
            <input type="hidden" name="forceResultField" value="NewHsInfo.newHsId,NewHsInfo.hsClass">
            <input type="hidden" name="regUseType" value="${regUseType}">
            <input type="hidden" name="rhtType" value="${regUseType}">
            <input type="hidden" name="rhtRegId" value="">
            <input type="hidden" name="rhtHxName" value="">
            <input type="hidden" name="rhtHsTp" value="">
            <input type="hidden" name="entityName" value="NewHsInfo">
            <input type="hidden" name="divId" value="oh001Context">
            <input type="hidden" name="forward" id="forward" value="/eland/oh/oh001/oh00101_list"/>
        </form>
        <div id="oh001Context" class="js_page" layoutH="55"></div>
    </div>
</div>
<oframe:script src="${pageContext.request.contextPath}/eland/oh/oh001/js/oh001.js" type="text/javascript"/>