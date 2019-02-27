<%--
  Created by IntelliJ IDEA.
  User: shfb_wang
  Date: 2016/3/22 0022 15:53
  Copyright(c) 北京四海富博计算机服务有限公司
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<oframe:script type="text/javascript" src="${pageContext.request.contextPath}/eland/fm/fm001/js/fm001.js"/>
<div class="mar5">
    <div class="tabs">
        <div class="tabsHeader">
            <div class="tabsHeaderContent">
                <ul>
                    <li class="selected" onclick="fm001.query(0);">
                        <a href="javascript:void(0);"><span>提交制折</span></a>
                    </li>
                    <%--<li onclick="fm001.query(1);">--%>
                        <%--<a href="javascript:void(0);"><span>暂缓制折</span></a>--%>
                    <%--</li>--%>
                    <li onclick="fm001.query(2);">
                        <a href="javascript:void(0);"><span>完成制折</span></a>
                    </li>
                    <li onclick="fm001.query(3);">
                        <a href="javascript:void(0);"><span>领取制折</span></a>
                    </li>
                </ul>
            </div>
        </div>
        <div class="tabsContent" style="padding: 0;border-top: 1px solid #3d91c8;">
            <%--提交制折--%>
            <div id="fm001_submitBankbook_div" style="position: relative;">
                <div class="panelBar">
                    <ul class="toolBar">
                        <li onclick="fm001.openQSch(0);">
                            <a class="find" href="javascript:void(0)"><span>条件检索</span></a>
                        </li>
                        <li onclick="fm001.query(0);">
                            <a class="reflesh" href="javascript:void(0);"><span>刷新检索</span></a>
                        </li>
                        <oframe:op prjCd="${param.prjCd}" template="li" cssClass="delete-more"
                                   name="批量提交制折" rhtCd="fm_batch_submit"
                                   onClick="fm001.batchSubmit(0);"/>
                        <li onclick="fm001.query(0);">
                            <a class="export" onclick="Page.exportExcel('fm001_submit_result', false)">
                                <span>导出明细</span>
                            </a>
                        </li>
                    </ul>
                </div>
                <form id="fm001_submitBankbook_form" method="post">
                    <%-- 分页展示 --%>
                    <input type="hidden" name="entityName" value="HouseInfo" />
                    <input type="hidden" name="conditionName" value="HouseInfo.HsFmInfo.pId,HouseInfo.HsFmInfo.fmStatus">
                    <input type="hidden" name="condition" value="=,=">
                    <input type="hidden" name="conditionValue" value="${pId},8">
                    <input type="hidden" name="sortColumn" value="">
                    <input type="hidden" name="sortOrder" value="">
                    <input type="hidden" name="divId" value="fm001_submit_page_data">
                    <input type="hidden" name="forceResultField" value="HouseInfo.HsFmInfo.hsFmId,HouseInfo.HsFmInfo.hsId,HouseInfo.HsFmInfo.hsCtId">
                    <input type="hidden" class="js_conditionValue" value="">
                    <input type="hidden" name="cmptName" id="cmptName" value="QUERY_ENTITY_PAGE_DATA" />
                    <input type="hidden" name="resultField"
                           value="HouseInfo.hsCd,HouseInfo.hsOwnerPersons,HouseInfo.hsFullAddr,HouseInfo.ttOrgId,HouseInfo.HsFmInfo.fmPersonName,HouseInfo.HsFmInfo.fmMoney,HouseInfo.HsFmInfo.fmStatus">
                    <input type="hidden" name="forward" id="forward" value="/eland/fm/fm001/fm001_list" />

                    <div id="fm001_submit" class="hidden"
                         style="position: absolute;width:100%;top: 0px;z-index: 100;background-color: #ffffff; margin-top: 40px;">
                        <div class="triangle triangle-up"
                             style="border-bottom-color: #2c7bae;top: -10px; left: 25px;"></div>
                        <table class="border">
                            <tr>
                                <th><label>档案编号：</label></th>
                                <td style="position: relative;">
                                    <input type="text" name="HouseInfo.hsCd" condition="=" />
                                </td>
                                <th><label>被安置人：</label></th>
                                <td style="position: relative;">
                                    <input type="text" name="HouseInfo.hsOwnerPersons" condition="like" />
                                </td>
                                <th><label>制折状态：</label></th>
                                <td>
                                    <input type="hidden" name="HouseInfo.HsFmInfo.fmStatus" condition="=" class="textInput" value="8" />
                                    <input type="text" itemCd="FM_STATUS" class="autocomplete textInput"
                                           atOption="CODE.getCfgDataOpt"
                                           value='<oframe:name itemCd="FM_STATUS" prjCd="${param.prjCd}" value="8"/>'>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="8" align="center">
                                    <button onclick="fm001.query(0);fm001.closeQSch(0);"
                                            type="button" class="btn btn-primary">查询
                                    </button>
                                    <button onclick="fm001.closeQSch(0)" type="button" class="btn btn-info">关闭</button>
                                </td>
                            </tr>
                        </table>
                    </div>
                </form>
                <div class="js_page" id="fm001_submit_page_data"></div>
            </div>

            <%--暂缓制折--%>
            <%--<div id="fm001_pauseBankbook_div" style="position: relative;">--%>
                <%--<div class="panelBar">--%>
                    <%--<ul class="toolBar">--%>
                        <%--<li onclick="fm001.openQSch(1);">--%>
                            <%--<a class="find" href="javascript:void(0)"><span>条件检索</span></a>--%>
                        <%--</li>--%>
                        <%--<li onclick="fm001.query(1);">--%>
                            <%--<a class="reflesh" href="javascript:void(0);"><span>刷新检索</span></a>--%>
                        <%--</li>--%>
                        <%--&lt;%&ndash;<li onclick="fm001.batchSubmit(1);">&ndash;%&gt;--%>
                            <%--&lt;%&ndash;<a class="save" href="javascript:void(0);"><span>批量提交制折</span></a>&ndash;%&gt;--%>
                        <%--&lt;%&ndash;</li>&ndash;%&gt;--%>
                        <%--<li>--%>
                            <%--<a class="export" onclick="Page.exportExcel('fm001_pause_page_data', false)">--%>
                                <%--<span>导出明细</span>--%>
                            <%--</a>--%>
                        <%--</li>--%>
                    <%--</ul>--%>
                <%--</div>--%>
                <%--<form id="fm001_pauseBankbook_form" method="post">--%>
                    <%--&lt;%&ndash; 分页展示 &ndash;%&gt;--%>
                    <%--<input type="hidden" name="entityName" value="HouseInfo" />--%>
                    <%--<input type="hidden" name="conditionName" value="">--%>
                    <%--<input type="hidden" name="condition" value="">--%>
                    <%--<input type="hidden" name="conditionValue" value="">--%>
                    <%--<input type="hidden" name="sortColumn" value="HouseInfo.handleStatusDate">--%>
                    <%--<input type="hidden" name="sortOrder" value="desc">--%>
                    <%--<input type="hidden" name="divId" value="fm001_pause_page_data">--%>
                    <%--<input type="hidden" name="forceResultField" value="HouseInfo.HsFmInfo.hsFmId">--%>
                    <%--<input type="hidden" class="js_conditionValue" value="">--%>
                    <%--<input type="hidden" name="cmptName" id="cmptName" value="QUERY_ENTITY_PAGE_DATA" />--%>
                    <%--<input type="hidden" name="resultField"--%>
                           <%--value="HouseInfo.hsCd,HouseInfo.hsOwnerPersons,HouseInfo.hsFullAddr,HouseInfo.ttOrgId,HouseInfo.HsFmInfo.fmStopStDate,HouseInfo.HsFmInfo.fmStopEnDate,HouseInfo.HsFmInfo.fmStopDesc,HouseInfo.HsFmInfo.fmStatus">--%>
                    <%--<input type="hidden" name="forward" id="forward" value="/eland/fm/fm001/fm001_pause_list" />--%>

                    <%--<div id="fm001_pause" class="hidden"--%>
                         <%--style="position: absolute;width:100%;top: 0px;z-index: 100;background-color: #ffffff; margin-top: 40px;">--%>
                        <%--<div class="triangle triangle-up"--%>
                             <%--style="border-bottom-color: #2c7bae;top: -10px; left: 25px;"></div>--%>
                        <%--<table class="border">--%>
                            <%--<tr>--%>
                                <%--<th><label>档案编号：</label></th>--%>
                                <%--<td style="position: relative;">--%>
                                    <%--<input type="text" name="HouseInfo.hsCd" condition="=" />--%>
                                <%--</td>--%>
                                <%--<th><label>被安置人：</label></th>--%>
                                <%--<td style="position: relative;">--%>
                                    <%--<input type="text" name="HouseInfo.hsOwnerPersons" condition="like" />--%>
                                <%--</td>--%>
                                <%--<th><label>制折状态：</label></th>--%>
                                <%--<td>--%>
                                    <%--<input type="hidden" name="HouseInfo.HsFmInfo.fmStatus" condition="=" class="textInput" value="11" />--%>
                                    <%--<input type="text" itemCd="FM_STATUS" class="autocomplete textInput"--%>
                                           <%--atOption="CODE.getCfgDataOpt"--%>
                                           <%--value='<oframe:name itemCd="FM_STATUS" prjCd="${param.prjCd}" value="11"/>'>--%>
                                <%--</td>--%>
                            <%--</tr>--%>
                            <%--<tr>--%>
                                <%--<th><label>交房状态：</label></th>--%>
                                <%--<td>--%>
                                    <%--<input type="hidden" name="HouseInfo.hsHandleStatus" condition="=" class="textInput" value="" />--%>
                                    <%--<input type="text" itemCd="KEEP_OLD_HS_STATUS" class="autocomplete textInput"--%>
                                           <%--atOption="CODE.getCfgDataOpt"--%>
                                           <%--value='<oframe:name itemCd="KEEP_OLD_HS_STATUS" prjCd="${param.prjCd}" value=""/>'>--%>
                                <%--</td>--%>
                                <%--<th><label>交房时间（从）：</label></th>--%>
                                <%--<td>--%>
                                    <%--<input type="text" name="HouseInfo.hsHandleDate" condition=">=" class="date"--%>
                                           <%--datefmt="yyyy-MM-dd" />--%>
                                <%--</td>--%>
                                <%--<th><label>交房时间（至）：</label></th>--%>
                                <%--<td>--%>
                                    <%--<input type="text" name="HouseInfo.hsHandleDate" condition="<="--%>
                                           <%--class="date" datefmt="yyyy-MM-dd" />--%>
                                <%--</td>--%>
                            <%--</tr>--%>
                            <%--<tr>--%>
                                <%--<td colspan="8" align="center">--%>
                                    <%--<button onclick="fm001.query(1);fm001.closeQSch(1);"--%>
                                            <%--type="button" class="btn btn-primary">查询--%>
                                    <%--</button>--%>
                                    <%--<button onclick="fm001.closeQSch(1)" type="button" class="btn btn-info">关闭</button>--%>
                                <%--</td>--%>
                            <%--</tr>--%>
                        <%--</table>--%>
                    <%--</div>--%>
                <%--</form>--%>
                <%--<div class="js_page" id="fm001_pause_page_data"></div>--%>
            <%--</div>--%>

            <%--完成制折--%>
            <div id="fm001_completeBankbook_div" style="position: relative;">
                <div class="panelBar">
                    <ul class="toolBar">
                        <li onclick="fm001.openQSch(2);">
                            <a class="find" href="javascript:void(0)"><span>条件检索</span></a>
                        </li>
                        <li onclick="fm001.query(2);">
                            <a class="reflesh" href="javascript:void(0);"><span>刷新检索</span></a>
                        </li>
                        <oframe:op prjCd="${param.prjCd}" template="li" cssClass="delete-more"
                                   name="批量完成制折" rhtCd="fm_batch_complete"
                                   onClick="fm001.batchComplete(2);"/>
                        <li>
                            <a class="export" onclick="Page.exportExcel('fm001_complete_page_data', false)">
                                <span>导出明细</span>
                            </a>
                        </li>
                    </ul>
                </div>
                <form id="fm001_completeBankbook_form" method="post">
                    <%-- 分页展示 --%>
                    <input type="hidden" name="entityName" value="HouseInfo" />
                    <input type="hidden" name="conditionName" value="HouseInfo.HsFmInfo.fmStatus,HouseInfo.HsFmInfo.pId">
                    <input type="hidden" name="condition" value="=,=">
                    <input type="hidden" name="conditionValue" value="12,${pId}">
                    <input type="hidden" name="sortColumn" value="HouseInfo.hsHandleDate">
                    <input type="hidden" name="sortOrder" value="desc">
                    <input type="hidden" name="divId" value="fm001_complete_page_data">
                    <input type="hidden" name="forceResultField" value="HouseInfo.HsFmInfo.hsFmId">
                    <input type="hidden" class="js_conditionValue" value="">
                    <input type="hidden" name="cmptName" id="cmptName" value="QUERY_ENTITY_PAGE_DATA" />
                    <input type="hidden" name="resultField"
                           value="HouseInfo.hsCd,HouseInfo.hsOwnerPersons,HouseInfo.hsFullAddr,HouseInfo.ttOrgId,HouseInfo.HsFmInfo.fmMakeNum,HouseInfo.HsFmInfo.fmPersonName,HouseInfo.HsFmInfo.fmMoney,HouseInfo.HsFmInfo.fmToMakeDate,HouseInfo.HsFmInfo.fmMakedDate,HouseInfo.HsFmInfo.fmStatus">
                    <input type="hidden" name="forward" id="forward" value="/eland/fm/fm001/fm001_list" />

                    <div id="fm001_complete" class="hidden"
                         style="position: absolute;width:100%;top: 0px;z-index: 100;background-color: #ffffff; margin-top: 40px;">
                        <div class="triangle triangle-up"
                             style="border-bottom-color: #2c7bae;top: -10px; left: 25px;"></div>
                        <table class="border">
                            <tr>
                                <th><label>档案编号：</label></th>
                                <td style="position: relative;">
                                    <input type="text" name="HouseInfo.hsCd" condition="=" />
                                </td>
                                <th><label>被安置人：</label></th>
                                <td style="position: relative;">
                                    <input type="text" name="HouseInfo.hsOwnerPersons" condition="like" />
                                </td>
                                <th><label>批次号：</label></th>
                                <td style="position: relative;">
                                    <input type="text" name="HouseInfo.HsFmInfo.fmMakeNum" condition="="/>
                                </td>
                                <th><label>制折状态：</label></th>
                                <td>
                                    <input type="hidden" name="HouseInfo.HsFmInfo.fmStatus" condition="=" class="textInput" value="12" />
                                    <input type="text" itemCd="FM_STATUS" class="autocomplete textInput"
                                           atOption="CODE.getCfgDataOpt"
                                           value='<oframe:name itemCd="FM_STATUS" prjCd="${param.prjCd}" value="12"/>'>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="8" align="center">
                                    <button onclick="fm001.query(2);fm001.closeQSch(2);"
                                            type="button" class="btn btn-primary">查询
                                    </button>
                                    <button onclick="fm001.closeQSch(2)" type="button" class="btn btn-info">关闭</button>
                                </td>
                            </tr>
                        </table>
                    </div>
                </form>
                <div class="js_page" id="fm001_complete_page_data"></div>
            </div>

            <%--领取制折--%>
            <div id="fm001_receiveBankbook_div" style="position: relative;">
                <div class="panelBar">
                    <ul class="toolBar">
                        <li onclick="fm001.openQSch(3);">
                            <a class="find" href="javascript:void(0)"><span>条件检索</span></a>
                        </li>
                        <li onclick="fm001.query(3);">
                            <a class="reflesh" href="javascript:void(0);"><span>刷新检索</span></a>
                        </li>
                        <li>
                            <a class="export" onclick="Page.exportExcel('fm001_receive_page_data', false)">
                                <span>导出明细</span>
                            </a>
                        </li>
                    </ul>
                </div>
                <form id="fm001_receiveBankbook_form" method="post">
                    <%-- 分页展示 --%>
                    <input type="hidden" name="entityName" value="HouseInfo" />
                    <input type="hidden" name="conditionName" value="HouseInfo.HsFmInfo.fmStatus,HouseInfo.HsFmInfo.pId">
                    <input type="hidden" name="condition" value="=,=">
                    <input type="hidden" name="conditionValue" value="12,${pId}">
                    <input type="hidden" name="sortColumn" value="HouseInfo.hsHandleDate">
                    <input type="hidden" name="sortOrder" value="desc">
                    <input type="hidden" name="divId" value="fm001_receive_page_data">
                    <input type="hidden" name="forceResultField" value="HouseInfo.HsFmInfo.hsFmId">
                    <input type="hidden" class="js_conditionValue" value="">
                    <input type="hidden" name="cmptName" id="cmptName" value="QUERY_ENTITY_PAGE_DATA" />
                    <input type="hidden" name="resultField"
                           value="HouseInfo.hsCd,HouseInfo.hsOwnerPersons,HouseInfo.hsFullAddr,HouseInfo.ttOrgId,HouseInfo.HsFmInfo.fmMakeNum,HouseInfo.HsFmInfo.fmPersonName,HouseInfo.HsFmInfo.fmMoney,HouseInfo.HsFmInfo.fmMakedDate,HouseInfo.HsFmInfo.fmGetDate,HouseInfo.HsFmInfo.fmStatus">
                    <input type="hidden" name="forward" id="forward" value="/eland/fm/fm001/fm001_list" />

                    <div id="fm001_receive" class="hidden"
                         style="position: absolute;width:100%;top: 0px;z-index: 100;background-color: #ffffff; margin-top: 40px;">
                        <div class="triangle triangle-up"
                             style="border-bottom-color: #2c7bae;top: -10px; left: 25px;"></div>
                        <table class="border">
                            <tr>
                                <th><label>档案编号：</label></th>
                                <td style="position: relative;">
                                    <input type="text" name="HouseInfo.hsCd" condition="=" />
                                </td>
                                <th><label>被安置人：</label></th>
                                <td style="position: relative;">
                                    <input type="text" name="HouseInfo.hsOwnerPersons" condition="like" />
                                </td>
                                <th><label>制折状态：</label></th>
                                <td>
                                    <input type="hidden" name="HouseInfo.HsFmInfo.fmStatus" condition="in" class="textInput" value="13|14" />
                                    <input type="text" itemCd="FM_STATUS" class="autocomplete textInput"
                                           atOption="CODE.getCfgDataOpt"
                                           value='<oframe:name itemCd="FM_STATUS" prjCd="${param.prjCd}" value="13"/>'>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="8" align="center">
                                    <button onclick="fm001.query(3);fm001.closeQSch(3);"
                                            type="button" class="btn btn-primary">查询
                                    </button>
                                    <button onclick="fm001.closeQSch(3)" type="button" class="btn btn-info">关闭</button>
                                </td>
                            </tr>
                        </table>
                    </div>
                </form>
                <div class="js_page" id="fm001_receive_page_data"></div>
            </div>
        </div>
    </div>
</div>
<script>
    $(document).ready(function () {
        fm001.query(0);
    });
</script>