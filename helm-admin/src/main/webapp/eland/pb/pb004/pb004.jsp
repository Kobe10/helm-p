<%--
  Created by IntelliJ IDEA.
  User: shfb_wang
  Date: 2015/5/13 9:31
  Copyright(c) 北京四海富博计算机服务有限公司
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="mar5" style="vertical-align: top;">
    <%--左侧区域树--%>
    <div style="width: 240px;float: left;position: relative;" id="pb004DivTree" layoutH="15"
         class="panel left_menu">
        <h1>
            <span>目 录 导 航</span>
            <span onclick="pb004.extendOrClose(this);" class="panel_menu js_reload">展开</span>
        </h1>
        <ul id="pb004Tree" class="ztree" layoutH="75"></ul>
    </div>
    <div class="split_line" layoutH="15">
        <span class="split_but" title="展开/隐藏导航"></span>
    </div>
    <%--右侧自定义画板--%>
    <div id="pb004RightDiv" style="margin-left: 250px;margin-right: 5px;position: relative;">
        <div id="pb00402_qry">
            <div class="panelBar">
                <ul class="toolBar">
                    <li onclick="pb004.queryBuild(this);">
                        <a class="find" href="javascript:void(0)"><span>条件检索</span></a>
                    </li>
                </ul>
            </div>

            <div id="pb00402Context" class="js_page">
                <table class="border" style="text-align: center">
                    <tr>
                        <th width="15%">总产籍数：</th>
                        <td width="10%">&nbsp;</td>
                        <th width="15%">总户籍数：</th>
                        <td width="10%">&nbsp;</td>
                        <th width="15%">总公房数：</th>
                        <td width="10%">&nbsp;</td>
                        <th width="15%">总私房数：</th>
                        <td width="10%">&nbsp;</td>
                    </tr>
                    <tr>
                        <th>总院落数：</th>
                        <td>&nbsp;</td>
                        <th>总公房院落数：</th>
                        <td>&nbsp;</td>
                        <th>总私房院落数：</th>
                        <td>&nbsp;</td>
                        <th>总公私混合院数：</th>
                        <td>&nbsp;</td>
                    </tr>
                    <tr>
                        <th>总建筑面积(㎡)：</th>
                        <td>&nbsp;</td>
                        <th>总占地面积(㎡)：</th>
                        <td>&nbsp;</td>
                        <th>总建面单价(元/㎡)：</th>
                        <td>&nbsp;</td>
                        <th>总占地单价(元/㎡)：</th>
                        <td>&nbsp;</td>
                    </tr>
                    <tr>
                        <th>总成本合计(元)：</th>
                        <td>&nbsp;</td>
                        <th>奖励及补助合计(元)：</th>
                        <td>&nbsp;</td>
                        <th>税费合计(元)：</th>
                        <td>&nbsp;</td>
                        <th>户口外迁费合计(元)：</th>
                        <td>&nbsp;</td>
                    </tr>
                    <tr>
                        <th>总应安置面积(㎡)：</th>
                        <td>&nbsp;</td>
                        <th>应安置面积对应购房款合计(元)：</th>
                        <td>&nbsp;</td>
                        <th>应安置面积差额补助合计(元)：</th>
                        <td>&nbsp;</td>
                        <th>&nbsp;</th>
                        <td>&nbsp;</td>
                    </tr>
                </table>
            </div>
        </div>
        <div id="pb004Context">
            <form id="pb004frm" method="post">
                <input type="hidden" name="conditionName" value="BuildInfo.buildStatus,BuildInfo.buildFullAddr">
                <input type="hidden" name="condition" value="in,like">
                <input type="hidden" name="conditionValue" value=",">
                <input type="hidden" name="sortColumn" value="BuildInfo.buildFullAddr">
                <input type="hidden" name="sortOrder" value="asc">
                <input type="hidden" class="js_conditionValue" value="">
                <input type="hidden" class="js_can_defesult" value="false">
                <input type="hidden" name="resultField"
                       value="BuildInfo.buildFullAddr,BuildInfo.buildType,BuildInfo.buildStatus,BuildInfo.totalBuldSize,BuildInfo.totalLandSize,BuildInfo.totalJmdj,BuildInfo.totalZddj,BuildInfo.totalCb,BuildInfo.totalYaz,BuildInfo.totalGfk">
                <input type="hidden" name="forceResultField" value="BuildInfo.buildId">
                <input type="hidden" name="regUseType" value="1">
                <input type="hidden" name="rhtRegId" value="">
                <input type="hidden" name="entityName" value="BuildInfo">
                <input type="hidden" name="divId" value="pb004List">
                <input type="hidden" name="forward" id="forward" value="/eland/pb/pb004/pb004_list"/>
            </form>
            <div id="pb004List" class="js_page">

            </div>
            <div id="cancelCheckIds">
                <input type="hidden" name="cancelCheckIds" value=""/>
            </div>
        </div>
    </div>
</div>
<oframe:script src="${pageContext.request.contextPath}/eland/pb/pb004/js/pb004.js" type="text/javascript"/>
