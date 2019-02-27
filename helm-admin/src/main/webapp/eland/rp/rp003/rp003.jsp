<%--
  Created by IntelliJ IDEA.
  User: shfb_wang
  Date: 2015/5/8
  Time: 15:20
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<div class="js_query_condition" style="position: relative;">
    <div class="panelBar">
        <ul class="toolBar">
            <li onclick="Page.queryCondition(this, {width: '100%', formObj: $(this).closest('div.js_query_condition').next('form')});">
                <a class="find" href="javascript:void(0)"><span>检索</span></a>
            </li>
            <li onclick="Page.query($(this).closest('div.js_query_condition').next('form'));">
                <a class="reflesh" href="javascript:void(0)"><span>刷新</span>
                </a>
            </li>
            <%--<li>--%>
                <%--<a class="export" onclick="Page.exportExcel('rp003list', false)">--%>
                    <%--<span>导出EXCEL</span>--%>
                <%--</a>--%>
            <%--</li>--%>
        </ul>
    </div>
</div>

<form id="rp003Form">
    <input type="hidden" name="entityName" value="BuildInfo"/>
    <%--<input type="hidden" name="cmptName" value="QUERY_ENTITY_GROUP"/>--%>
    <input type="hidden" name="conditionName" value="">
    <input type="hidden" name="condition" value="">
    <input type="hidden" name="conditionValue" value="">
    <input type="hidden" class="js_can_defesult" value="false">
    <input type="hidden" class="js_conditionValue" value="">
    <input type="hidden" name="divId" value="rp003list">
    <input type="hidden" name="pageSize" value="500">
    <input type="hidden" name="resultField"
           value="BuildInfo.buildFullAddr,BuildInfo.buildNo,BuildInfo.priHsNum,BuildInfo.pubHsNum,BuildInfo.othHsNum,BuildInfo.totalHsNum,BuildInfo.rzYiju,BuildInfo.rzErju,BuildInfo.rzSanju,BuildInfo.totalRz,BuildInfo.cyLingju,BuildInfo.cyYiju,BuildInfo.cyErju,BuildInfo.totalCy,BuildInfo.hsCtCd,BuildInfo.chsHsCd,BuildInfo.trsOwnCd,BuildInfo.netSignCd,BuildInfo.finishTaxCd,BuildInfo.backRentCd,BuildInfo.trsRegistCd,BuildInfo.ttHsCd,BuildInfo.jsCd,BuildInfo.gfCtCd">
    <input type="hidden" name="forward" id="forward" value="/eland/rp/rp003/rp003_list"/>
</form>
<script type="text/javascript">
    $(document).ready(function () {
        Page.query($("#rp003Form", navTab.getCurrentPanel()), '');
    });
</script>


