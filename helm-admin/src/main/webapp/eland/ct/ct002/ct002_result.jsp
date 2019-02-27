<%--
  Created by IntelliJ IDEA.
  User: Dennis
  Date: 15/9/15
  Time: 00:09
  To change this template use File | Settings | File Templates.
--%>
<%--待签约处理--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div id="ct002SchemeDiv" class="panelcontainer" layoutH="15" style="border: 1px solid #e9e9e9;position: relative">
    <input type="hidden" name="schemeId" value="${schemeId}"/>

    <div class="panelBar">
        <ul class="toolBar">
            <li onclick="ct002.openQSch(this);">
                <a class="find" href="javascript:void(0)"><span>检索结果</span></a>
            </li>
            <li onclick="ct002.query();">
                <a class="reflesh" href="javascript:void(0)"><span>刷新检索</span></a>
            </li>
            <li onclick="ct002.delectSlt();">
                <a class="delete" href="javascript:void(0)"><span>删除结果</span></a>
            </li>
            <li onclick="ct002.openRunScheme('${schemeId}')">
                <a class="print" href="javascript:void(0)"><span>提交计算</span></a>
            </li>
            <%--切换试图--%>
            <li class="right" onclick="">
                <a class="result" href="javascript:void(0)"><span class="active">方案计算</span></a>
            </li>
            <li class="right" onclick="ct002.showDefinition();">
                <a class="design" href="javascript:void(0)"><span>方案定义</span></a>
            </li>
        </ul>
    </div>
    <div style="padding-left: 5px; padding-right: 5px;">
        <div id="ct002SchDiv" class="hidden"
             style="position: absolute;width:99%;top: 0px;z-index: 100;background-color: #ffffff; margin-top: 40px;">
            <div class="triangle triangle-up" style="border-bottom-color: #2c7bae;top: -10px; left: 25px;"></div>
            <table class="border">
                <tr>
                    <th><label>计算开始时间：</label></th>
                    <td>
                        <input name="preStartTime" condition="like" type="text" class="date" value=""/>
                    </td>
                    <th><label>计算结束时间：</label></th>
                    <td>
                        <input name="preEndTime" condition="like" type="text" class="date">
                    </td>
                    <th><label>预分状态：</label></th>
                    <td>
                        <input name="preStatus" condition="=" type="text">
                    </td>
                </tr>
                <tr>
                    <td colspan="8" align="center">
                        <button onclick="ct002.query();ct002.closeQSch()" type="button" id="schBtn"
                                class="js_faTask btn btn-primary">
                            查询
                        </button>
                        <button onclick="ct002.closeQSch(false)" type="button" class="btn btn-info">关闭</button>
                    </td>
                </tr>
            </table>
        </div>
    </div>

    <form id="ct002ResultQueryForm">
        <input type="hidden" name="entityName" value="PreassignedResult"/>
        <input type="hidden" name="conditionName" value="schemeId">
        <input type="hidden" name="condition" value="=">
        <input type="hidden" name="conditionValue" value="${schemeId}">
        <input type="hidden" name="sortColumn" value="preStartTime">
        <input type="hidden" name="sortOrder" value="desc">
        <input type="hidden" class="js_conditionValue" value="">
        <input type="hidden" name="forceResultField" value="resultId,schemeId">
        <input type="hidden" name="divId" value="ct002Restult">
        <input type="hidden" name="resultField" value="batchName,preStartTime,preEndTime,preStatus">
        <input type="hidden" name="cmptName" id="cmptName" value="QUERY_ENTITY_PAGE_DATA"/>
        <input type="hidden" name="forward" id="forward" value="/eland/ct/ct002/ct002_result_list"/>
    </form>
    <div id="ct002Restult" class="js_page" layoutH="55" style="position: relative;width: 100%;"></div>
</div>
<script>
    $(document).ready(function () {
        var form = $('#ct002ResultQueryForm', navTab.getCurrentPanel());
        Page.query(form, "");
    });
</script>
