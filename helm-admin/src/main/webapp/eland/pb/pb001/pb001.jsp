<%--院落信息查询视图--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="pageNav">
    <a class="current">腾退信息</a>
</div>
<oframe:power prjCd="${param.prjCd}" rhtCd="edit_1_reg_rht">
    <input type="hidden" name="editAble" value="1"/>
</oframe:power>
<oframe:power prjCd="${param.prjCd}" rhtCd="del_1_reg_rht">
    <input type="hidden" name="removeAble" value="1"/>
</oframe:power>
<oframe:power prjCd="${param.prjCd}" rhtCd="move_1_reg_rht">
    <input type="hidden" name="moveAble" value="1"/>
</oframe:power>

<%--初始化显示的建筑ID--%>
<input type="hidden" name="initRegId" value="${regId}">

<%--初始化显示模式--%>
<input type="hidden" name="showYlTree" value="${showYlTree}">
<input type="hidden" name="showYlTreeModel" value="${showYlTreeModel}">

<%--内容显示区域--%>
<div class="mar5" style="vertical-align: top;">
    <%--左侧区域树--%>
    <div style="width: 240px;float: left;position: relative;" id="pb001DivTree" layoutH="15" class="panel left_menu">
        <h1>
            <span class="panel_title">区 域 导 航</span>
            <span class="item_switch" onclick="pb001.switchTree(this);">&nbsp;</span>
            <span onclick="pb001.extendOrClose(this);" class="panel_menu js_reload">展开</span>
        </h1>
        <ul id="pb001Tree" class="ztree" layoutH="65"></ul>
        <ul id="pb001OrgTree" layoutH="65" class="ztree hidden"></ul>
    </div>
    <div class="split_line" layoutH="15">
        <span class="split_but" title="展开/隐藏导航"></span>
    </div>
    <%--右侧自定义画板--%>
    <div id="pb001001" style="margin-left: 250px;margin-right: 5px;position: relative;">
        <div id="pb00102_qry">
            <div class="panelBar">
                <ul class="toolBar">
                    <li onmousemove="pb001.openQSch(this)"
                        onmouseout="pb001.leaveQSch()"
                        onclick="pb001.queryBuild(this);">
                        <a class="find" href="javascript:void(0)"><span>条件检索</span></a>
                    </li>
                    <li onclick="pb001.refleshBuild();">
                        <a class="reflesh" href="javascript:void(0)"><span>刷新检索</span></a>
                    </li>
                    <li>
                        <a class="export" onclick="Page.exportExcel('pb00102Context', false);">
                            <span>导出EXCEL</span>
                        </a>
                    </li>
                    <li>
                        <a class="add" href="javascript:pb001.addHouse();"><span>信息登记</span></a>
                    </li>
                    <li><a class="print" href="javascript:$.printBox('pb00102Context')"><span>打印</span></a></li>
                    <li style="float: right;" onclick="pb001.changeShowModel(this,'3');">
                        <a class="sum" href="javascript:void(0)"><span>统计</span></a>
                    </li>
                    <li style="float: right;" onclick="pb001.changeShowModel(this, '1');">
                        <a class="list" href="javascript:void(0)"><span class="active">列表</span></a>
                    </li>
                </ul>
            </div>
            <div id="pb001QSchDialog" class="hidden"
                 style="position: absolute;width:100%;top: 0px;z-index: 100;background-color: #ffffff; margin-top: 40px;">
                <div class="triangle triangle-up" style="border-bottom-color: #2c7bae;top: -10px; left: 25px;"></div>
                <table class="border">
                    <tr>
                        <th><label>院落地址：</label></th>
                        <td><input name="BuildInfo.buildFullAddr" condition="like" class="textInput"></td>
                        <th><label>院落性质：</label></th>
                        <td><input name="BuildInfo.buildType" type="hidden" condition="=" class="textInput">
                            <input type="text" style="width: auto;" itemCd="YARD_TYPE"
                                   atOption="CODE.getCfgDataOpt" class="autocomplete textInput">
                        </td>
                        <th><label>安置人名称：</label></th>
                        <td><input name="BuildInfo.HouseInfo.hsOwnerPersons" condition="like" class="textInput"></td>
                    </tr>
                    <tr>
                        <td colspan="6" align="center">
                            <button onclick="pb001.qSchData();" class="btn btn-primary js_query marr20">查询</button>
                            <button onclick="pb001.closeQSch(true);" class="btn btn-info">关闭</button>
                        </td>
                    </tr>
                </table>
            </div>
            <input type="hidden" class="js_query_model" value="1"/>

            <form id="pb001frm" method="post">
                <input type="hidden" name="conditionName" value="">
                <input type="hidden" name="condition" value="">
                <input type="hidden" name="conditionValue" value="">
                <input type="hidden" name="sortColumn" value="BuildInfo.buildFullAddr">
                <input type="hidden" name="sortOrder" value="asc">
                <input type="hidden" class="js_conditionValue" value="">
                <input type="hidden" name="resultField"
                       value="BuildInfo.buildFullAddr,BuildInfo.buildType,BuildInfo.buildStatus,BuildInfo.buildLandSize">
                <input type="hidden" name="forceResultField" value="BuildInfo.buildId,BuildInfo.buildType">
                <input type="hidden" name="regUseType" value="1">
                <input type="hidden" name="rhtRegId" value="">
                <input type="hidden" name="rhtOrgId" value="">
                <input type="hidden" name="entityName" value="BuildInfo">
                <input type="hidden" name="divId" value="pb00102Context">
                <input type="hidden" name="forward" id="forward" value="/eland/pb/pb001/pb001_list"/>
            </form>
            <div id="pb00102Context" class="js_page" layoutH="50"></div>
        </div>
        <div id="pb001Context"></div>
    </div>
</div>
<oframe:script src="${pageContext.request.contextPath}/eland/pb/pb001/js/pb001.js" type="text/javascript"/>
<oframe:script src="${pageContext.request.contextPath}/eland/pb/pb002/js/pb002_op.js" type="text/javascript"/>