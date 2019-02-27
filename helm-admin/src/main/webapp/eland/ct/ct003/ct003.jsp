<%--
  Created by IntelliJ IDEA.
  User: shfb_wang
  Date: 2015/10/23 0023 20:48
  Copyright(c) 北京四海富博计算机服务有限公司
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="mar5" style="vertical-align: top;">
    <input type="hidden" name="schemeType" value="${schemeType}"/>
    <%--数据查询区域--%>
    <div style="width: 340px;float: left;position: relative;" id="ct003SchDiv" layoutH="15" class="panel left_menu">
        <h1>
            <span class="panel_title">数据检索</span>

            <span onclick="ct003.schHs();" class="panel_menu js_reload">刷新</span>
            <span class="marr10" style="float: right;">
                <oframe:select prjCd="${param.prjCd}" id="printChHsStatus" itemCd="PRINT_STATUS" value="0"
                               onChange="ct003.schHs();"
                               name="printChHsStatus" type="checkbox"/>
            </span>
        </h1>

        <div style="padding: 0px;">
            <table width="100%;">
                <tr>
                    <td style="width: 30%;">
                        <select name="schType" style="float: left; width: 100%;">
                            <option value="HouseInfo.hsOwnerPersons">被安置人</option>
                            <option value="HouseInfo.hsFullAddr">房屋地址</option>
                            <option value="HouseInfo.hsCd">档案编号</option>
                        </select>
                    </td>
                    <td>
                        <input type="text" name="schValue"
                               onkeydown="if(event.keyCode == 13){ct003.schHs();}"
                               style="width: 100%;height:26px;min-width:inherit;">
                    </td>
                    <td style="width: 30px;">
                        <a title="选择" onclick="ct003.schHs(this);" style="height: 30px;left:6px;" class="btnLook">选择</a>
                    </td>
                </tr>
            </table>
            <form id="ct003QueryForm">
                <input type="hidden" name="entityName" value="HouseInfo"/>
                <input type="hidden" name="conditionName" value="">
                <input type="hidden" name="condition" value="">
                <input type="hidden" name="conditionValue" value="">
                <input type="hidden" name="sortColumn" value="HouseInfo.hsFullAddr">
                <input type="hidden" name="sortOrder" value="asc">
                <input type="hidden" class="js_conditionValue" value="">
                <input type="hidden" name="forceResultField" value="HouseInfo.hsId,HouseInfo.HsCtInfo.hsCtId">
                <input type="hidden" name="divId" value="query_list">
                <input type="hidden" name="resultField" value="HouseInfo.hsOwnerPersons,HouseInfo.hsFullAddr">
                <input type="hidden" name="forward" id="forward" value="/eland/ct/ct003/ct003_list"/>
            </form>
            <div class="js_page" layoutH="90" id="query_list">
                <table class="table">
                    <thead>
                    <tr>
                        <th width="34%">安置人</th>
                        <th>房屋地址</th>
                    </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
    <div class="split_line" layoutH="15">
        <span class="split_but" title="展开/隐藏导航"></span>
    </div>
    <%--签约区域--%>
    <div id="ct003Context" style="margin-left: 350px;margin-right: 5px;position: relative;">
    </div>
</div>
<oframe:script src="${pageContext.request.contextPath}/eland/ct/ct003/js/ct003.js" type="text/javascript"/>