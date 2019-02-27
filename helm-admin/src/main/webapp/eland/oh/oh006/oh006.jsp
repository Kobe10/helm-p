<%--居民签约处理--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>

<div class="mar5" style="vertical-align: top;">
<%--数据查询区域--%>
    <div style="width: 340px;float: left;position: relative;" id="oh006SchDiv" layoutH="15" class="panel left_menu">
        <h1>
            <span class="panel_title">数据检索</span>
            <span onclick="oh006.schHs(false);" class="panel_menu js_reload">刷新</span>
        </h1>

        <div id="oh006001">
            <table width="100%;">
                <tr>
                    <input type="hidden" name="" id="" value="/eland/oh/oh006/oh006001_list"/>
                    <td style="width: 30%;">
                        <select name="schType" style="float: left; width: 100%;">
                            <option value="HouseInfo.hsOwnerPersons">被安置人</option>
                            <option value="HouseInfo.hsCd">档案编号</option>
                            <option value="HouseInfo.hsFullAddr">房屋地址</option>
                        </select>
                    </td>
                    <td>
                        <input type="text" name="schValue"
                               onkeydown="if(event.keyCode == 13){oh006.qSchHs();}"
                               style="width: 100%;height:26px;min-width:inherit;">
                    </td>
                    <td style="width: 45px;">
                        <a title="选择" onclick="oh006.qSchHs(this);" style="height: 30px;left:6px;"
                           class="btnLook">选择</a>
                        <span onclick="oh006.queryHs(this);" title="高级查询" class="js-more-right"
                              style="width: 15px;cursor: pointer; text-align: center; line-height: 32px;float: right;"
                              type="button">
                        </span>
                    </td>
                </tr>
            </table>
            <form id="oh006QueryForm">
                <input type="hidden" name="entityName" value="HouseInfo"/>
                <input type="hidden" name="conditionName" value="HouseInfo.HsCtInfo.ctType">
                <input type="hidden" name="condition" value="=">
                <input type="hidden" name="conditionValue" value="2">
                <input type="hidden" name="sortColumn" value="HouseInfo.hsId">
                <input type="hidden" name="sortOrder" value="asc">
                <input type="hidden" class="js_conditionValue" value="">
                <input type="hidden" name="divId" value="chquery_list">
                <input type="hidden" name="resultField" value="HouseInfo.hsOwnerPersons,HouseInfo.hsFullAddr">
                <input type="hidden" class="js_canDefResult" value="false">
                <%--必须包含的字段--%>
                <input type="hidden" class="js_need_field" name="forceResultField"
                       value="HouseInfo.hsId,HouseInfo.hsOwnerPersons,HouseInfo.hsFullAddr">
                <input type="hidden" name="forward" id="forward" value="/eland/oh/oh006/oh006_list"/>
            </form>
            <div class="js_page" layoutH="90" id="chquery_list"></div>
        </div>
    </div>

    <div class="split_line" layoutH="15">
        <span class="split_but" title="展开/隐藏导航"></span>
    </div>

    <%--选房区域--%>
    <div id="oh006Context" style="margin-left: 350px;margin-right: 5px;position: relative;">
        <div id="oh006AdanceSch" style="position: absolute;top: -40px;width: 100%;"></div>
        <div id="oh006Info"></div>
    </div>
</div>
<oframe:script src="${pageContext.request.contextPath}/eland/oh/oh006/js/oh006.js" type="text/javascript"/>


