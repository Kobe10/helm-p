<%--居民签约处理--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<oframe:script src="${pageContext.request.contextPath}/eland/ch/ch001/js/ch001.js" type="text/javascript"/>
<input type="hidden" name="showXfList" value="${showXfList}">
<input type="hidden" name="ctType" value="${ctType}">
<input type="hidden" name="hsClass" value="${hsClass}">
<input type="hidden" name="chsType" value="${chsType}">
<input type="hidden" name="xyFormCd" value="${xyFormCd}">
<input type="hidden" name="chooseStatus" value="${chooseStatus}">
<input type="hidden" name="isGiveUpHs" value="${isGiveUpHs}">
<input type="hidden" name="countSwitch" id="countSwitchFlag" value="${countSwitch}">
<input type="hidden" name="chaHsId" value="${hsId}"/>
<oframe:power prjCd="${param.prjCd}" rhtCd="add_chs_time_right">
    <input type="hidden" name="chsAddTimeRight" value="1"/>
</oframe:power>

<div class="mar5" style="vertical-align: top;">
    <%--数据查询区域--%>
    <div style="width: 340px;float: left;position: relative;" id="ch001SchDiv" layoutH="10" class="panel left_menu">
        <h1>
            <span class="panel_title">数据检索</span>
            <span onclick="ch001.schHs(false);" class="panel_menu js_reload">刷新</span>
            <span onclick="ch001.toggleEdit(this);" class="panel_menu">设置</span>
        </h1>

        <div style="padding: 0;" id="ch001001">
            <div id="ch001create" class="hidden"
                 style="position: absolute;background-color: white;z-index: 100;width: 100%;">
                <table class="border">
                    <tr>
                        <th>安置方式：</th>
                        <td>
                        <span class="marr10">
                            <input type="radio" id="ctType" checked name="ctType" value="${ctType}">
                            <oframe:name prjCd="${param.prjCd}" itemCd="10001" value="${ctType}"/>
                        </span>
                        </td>
                    </tr>
                    <tr>
                        <th>选房状态：</th>
                        <td>
                        <span class="marr10">
                            <oframe:select prjCd="${param.prjCd}" id="chooseStatus" itemCd="HS_DL_STATUS"
                                           name="chooseStatus" type="checkbox"/>
                        </span>
                        </td>
                    </tr>
                    <tr>
                        <th>倒计时开关：</th>
                        <td>
                        <span class="marr10" title="是否自动启动选房倒计时">
                            <oframe:select prjCd="${param.prjCd}" itemCd="COUNT_SWITCH" id="ch001CountSwitch"
                                           name="countSwitch" type="radio"/>
                        </span>
                        </td>
                    </tr>
                </table>
            </div>
            <table width="100%;">
                <tr>
                    <input type="hidden" name="" id="" value="/eland/ch/ch001/ch001001_list"/>
                    <td style="width: 30%;">
                        <select name="schType" style="float: left; width: 100%;">
                            <option value="HouseInfo.hsCd">档案编号</option>
                            <option value="HouseInfo.hsOwnerPersons">被安置人</option>
                            <option value="HouseInfo.hsFullAddr">房屋地址</option>
                            <option value="HouseInfo.HsCtInfo.chooseHsSid">选房序号</option>
                        </select>
                    </td>
                    <td>
                        <input type="text" name="schValue"
                               onkeydown="if(event.keyCode == 13){ch001.schHs(false);}"
                               style="width: 100%;height:26px;min-width:inherit;">
                    </td>
                    <td style="width: 30px;">
                        <a title="选择" onclick="ch001.schHs(false);" style="height: 30px;left:6px;"
                           class="btnLook">选择</a>
                    </td>
                </tr>
            </table>
            <form id="ch001QueryForm">
                <input type="hidden" name="entityName" value="HouseInfo"/>
                <input type="hidden" name="conditionName" value="">
                <input type="hidden" name="condition" value="">
                <input type="hidden" name="conditionValue" value="">
                <input type="hidden" name="sortColumn" value="HouseInfo.HsCtInfo.chooseHsSid">
                <input type="hidden" name="sortOrder" value="asc">
                <input type="hidden" class="js_conditionValue" value="">
                <input type="hidden" name="divId" value="chquery_list">
                <input type="hidden" name="cmptName" value="QUERY_CANDIDATE">
                <input type="hidden" name="resultField" value="HouseInfo.hsCd,HouseInfo.hsOwnerPersons">
                <%--必须包含的字段--%>
                <input type="hidden" class="js_need_field" name="forceResultField"
                       value="HouseInfo.hsId,HouseInfo.hsOwnerPersons,HouseInfo.hsFullAddr">
                <input type="hidden" name="forward" id="forward" value="/eland/ch/ch001/ch001_list"/>
            </form>
            <div class="js_page" layoutH="85" id="chquery_list"></div>
        </div>
    </div>

    <div class="split_line" layoutH="15">
        <span class="split_but" title="展开/隐藏导航"></span>
    </div>

    <%--选房区域--%>
    <div id="ch001Context" style="margin-left: 350px;margin-right: 5px;position: relative;"></div>
</div>


