<%--房产信息检索--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<style>
    div.rptDiv {
        width: 210px;
        display: inline;
        float: left;
    }

    table.rptTable tr td {
        height: 18px;
        white-space: nowrap;
    }

    div.jobGroup {
        display: inline-block;
    }

    div.jobGroup h1 {
        text-align: left;
        background: rgba(87, 171, 155, 0.27);
        font-weight: bold;
        font-size: 12px;
        color: #177DAE;
        height: 28px;
        line-height: 28px;
        vertical-align: middle;
        padding-left: 5px;
        cursor: pointer;
        clear: both;
    }

    div.jobGroup div {
        border: 1px solid #3d51ff;
        min-height: 50px;
        max-width: 350px;
    }

    div.jobGroup > div > span {
        float: left;
        display: inline-block;
        margin-left: 5px;
    }

</style>
<div class="withTitle">
<h1>统计指标选择</h1>

<form id="rp001frm" method="post" class="entermode">
<table class="form" id="rp001SchContainer">
<tr>
    <th width="15%">
        统计日期：
    </th>
    <td>
        <input type="text" name="reportDate" datefmt="yyyyMMdd" class="date"
               onchange="rp001.refreshAll();"/>
    </td>
</tr>
<tr>
    <th>
        可选指标：
    </th>
    <td>
        <div class="js-job-cd-list">
            <div class="jobGroup">
                <h1>
                    分指数据指标
                    <span class="link mar10" onclick="rp001.addAll(this);">[全选]</span>
                    <span class="link marr5" onclick="rp001.removeAll(this);">[取消]</span>
                </h1>

                <div>
                            <span>
                                <input type="checkbox" name="reportItem"
                                       value="survey_ratio" onclick="rp001.changeReportItem(this)">
                                <span>
                                    <a href="javascript:void(0);">入户调查率</a>
                                </span>
                                <span class="hidden">
                                    提交申请表院落数/院落总数
                                </span>
                            </span>
                             <span>
                                <input type="checkbox" name="reportItem"
                                       value="apply" onclick="rp001.changeReportItem(this)">
                                <span>
                                    <a href="javascript:void(0);">提交申请数</a>
                                </span>
                                 <span class="hidden">
                                    房产信息中【是否提交】申请表标记为【是】的数据，户数指产籍户数
                                </span>
                            </span>
                            <span>
                                <input type="checkbox" name="reportItem"
                                       value="apply_checked" onclick="rp001.changeReportItem(this)">
                                <span>
                                    <a href="javascript:void(0);">审核通过数</a>
                                </span>
                                <span class="hidden">
                                    房产信息提交资料组审批并审核通过的数量,资料组审核通过的条件为：
                                    已经提交申请表，包含申请表附件，房产附件，家庭人员户口页附件及身份证附件
                                </span>
                            </span>
                            <span>
                                <input type="checkbox" name="reportItem"
                                       value="eight_apply" onclick="rp001.changeReportItem(this)">
                                <span>
                                    <a href="javascript:void(0);">80%申请数</a>
                                </span>
                                <span class="hidden">
                                    提交申请产权人户数/院落预计房产数 大于等于 80%、小于100%，
                                    并且资料组对院落信息审核通过并【提交督导审核】的数据(不包含整院的数据)
                                </span>
                            </span>
                             <span>
                                <input type="checkbox" name="reportItem"
                                       value="all_apply" onclick="rp001.changeReportItem(this)">
                                <span>
                                    <a href="javascript:void(0);">整院申请数</a>
                                </span>
                                <span class="hidden">
                                    提交申请产权人户数/院落预计房产数 等于100% ，
                                    并且资料组对院落信息审核通过并【提交督导审核】的数据;
                                </span>
                            </span>
                </div>
            </div>

            <div class="jobGroup">
                <h1>督导数据指标
                    <span class="link mar10" onclick="rp001.addAll(this);">[全选]</span>
                    <span class="link marr5" onclick="rp001.removeAll(this);">[取消]</span>
                </h1>

                <div>
                            <span>
                                <input type="checkbox" name="reportItem"
                                       value="eight_dd_check_apply" onclick="rp001.changeReportItem(this)">
                                <span>
                                    <a href="javascript:void(0);">80%院落督导审核通过数</a>
                                </span>
                                <span class="hidden">
                                    提交申请产权人户数/院落预计房产数  大于等于 80%、小于100%，
                                    资料组【提交督导审核】并且整院资料督导审批通过的数据;
                                </span>
                            </span>
                            <span>
                                <input type="checkbox" name="reportItem"
                                       value="all_dd_check_apply" onclick="rp001.changeReportItem(this)">
                                <span>
                                    <a href="javascript:void(0);">整院督导审核通过数</a>
                                </span>
                                <span class="hidden">
                                    提交申请产权人户数/院落预计房产数等于100%，
                                    资料组【提交督导审核】并且整院资料督导审批通过的数据;
                                </span>
                            </span>

                            <span>
                                <input type="checkbox" name="reportItem"
                                       value="dd_check_apply" onclick="rp001.changeReportItem(this)">
                                <span>
                                    <a href="javascript:void(0);">督导审核通过数</a>
                                </span>
                                <span class="hidden">
                                    提交申请产权人户数/院落预计房产数 大于80%，
                                    资料组【提交督导审核】并且整院资料督导审批通过的数据;
                                </span>
                            </span>
                             <span>
                                <input type="checkbox" name="reportItem"
                                       value="dd_cfm_apply" onclick="rp001.changeReportItem(this)">
                                <span>
                                    <a href="javascript:void(0);">督导方案确定数</a>
                                </span>
                                 <span class="hidden">
                                    督导审核通过后，院落由督导与居民谈定预分方案后通过系统点击【督导方案确定】
                                </span>
                            </span>
                            <span>
                                <input type="checkbox" name="reportItem"
                                       value="ct_create_apply" onclick="rp001.changeReportItem(this)">
                                <span>
                                    <a href="javascript:void(0);">协议生成数</a>
                                </span>
                                <span class="hidden">督导对院内购买人所需材料进行审核完成后，明确协议的各项条款细项，点击【生成协议文档】</span>
                            </span>
                </div>
            </div>

            <div class="jobGroup">
                <h1>签约数据
                    <span class="link mar10" onclick="rp001.addAll(this);">[全选]</span>
                    <span class="link marr5" onclick="rp001.removeAll(this);">[取消]</span>
                </h1>

                <div>
                        <span>
                            <input type="checkbox" name="reportItem"
                                   value="ct_cfm_apply" onclick="rp001.changeReportItem(this)">
                            <span>
                                <a href="javascript:void(0);">协议签订数</a>
                            </span>
                            <span class="hidden">督导生成协议后，组内下载协议并与居民签字；签字完成后上传附件并点击【确认协议已签订】</span>
                        </span>
                </div>
            </div>

            <div class="jobGroup">
                <h1>房源数据
                    <span class="link mar10" onclick="rp001.addAll(this);">[全选]</span>
                    <span class="link marr5" onclick="rp001.removeAll(this);">[取消]</span>
                </h1>

                <div>
                        <span>
                            <input type="checkbox" name="reportItem"
                                   value="ch_cfm_apply" onclick="rp001.changeReportItem(this)">
                            <span>
                                <a href="javascript:void(0);">已选房源数</a>
                            </span>
                            <span class="hidden">安置方式为【房屋安置】的整院签约居民，完成选房动作</span>
                        </span>
                </div>
            </div>
            <div class="jobGroup">
                <h1>其他指标
                    <span class="link mar10" onclick="rp001.addAll(this);">[全选]</span>
                    <span class="link marr5" onclick="rp001.removeAll(this);">[取消]</span>
                </h1>

                <div>
                        <span>
                            <input type="checkbox" name="reportItem"
                                   value="gv_cfm_apply" onclick="rp001.changeReportItem(this)">
                            <span>
                                <a href="javascript:void(0);">交房数</a>
                                <span class="hidden">已经交房的房屋数量</span>
                            </span>
                        </span>
                        <span>
                            <input type="checkbox" name="reportItem"
                                   value="hs_tp" onclick="rp001.changeReportItem(this)">
                            <span>
                                <a href="javascript:void(0);">房产类型</a>
                            </span>
                            <span class="hidden">以产级户为单位统计系统中录入的房产类型的数据</span>
                        </span>
                </div>
            </div>

        </div>
    </td>
</tr>
<tr class="hidden">
    <th>
        已选指标：
    </th>
    <td colspan="3">
        <div class="js-slt-item"></div>
    </td>
</tr>
</table>
</form>
</div>
<div class="pageContent">
    <div nowrap layoutH="210" style="overflow: auto;width:3000px;padding-bottom: 20px;"
         id="rp001DivContainer">
        <div class="rptDiv">
            <table class="form rptTable">
                <tr>
                    <td width="5%"></td>
                    <td align="center" colspan="2"><b>总数</b></td>
                </tr>
                <tr>
                    <td></td>
                    <td align="center">总院落</td>
                    <td align="center">总户数</td>
                </tr>
                <tr>
                    <td>&nbsp;</td>
                    <td align="center">&nbsp;</td>
                    <td align="center">&nbsp;</td>
                </tr>
                <tr style="background-color: #ffa309">
                    <td align="center">
                        <b>总计</b>
                    </td>
                    <td align="center">4603</td>
                    <td align="center">45833</td>
                </tr>
                <tr style="background-color: #ffcc02">
                    <td align="right">
                        <b>一组</b>
                    </td>
                    <td align="center">278</td>
                    <td align="center">2379</td>
                </tr>
                <tr style="background-color: #ffcc02">
                    <td align="right">
                        <b>二组</b>
                    </td>
                    <td align="center">181</td>
                    <td align="center">1655</td>
                </tr>
                <tr style="background-color: #ffcc02">
                    <td align="right">
                        <b>三组</b>
                    </td>
                    <td align="center">248</td>
                    <td align="center">2118</td>
                </tr>
                <tr style="background-color: #ffcc02">
                    <td align="right">
                        <b>四组</b>
                    </td>
                    <td align="center">169</td>
                    <td align="center">1392</td>
                </tr>
                <tr style="background-color: #ffcc02">
                    <td align="right">
                        <b>五组</b>
                    </td>
                    <td align="center">236</td>
                    <td align="center">1918</td>
                </tr>
                <tr style="background-color: #ffa309">
                    <td align="left">
                        <b>一分指</b>
                    </td>
                    <td align="center">1112</td>
                    <td align="center">9462</td>
                </tr>
                <tr style="background-color: #9acc02">
                    <td align="right">
                        <b>一组</b>
                    </td>
                    <td align="center">287</td>
                    <td align="center">2610</td>
                </tr>
                <tr style="background-color: #9acc02">
                    <td align="right">
                        <b>二组</b>
                    </td>
                    <td align="center">171</td>
                    <td align="center">1635</td>
                </tr>
                <tr style="background-color: #9acc02">
                    <td align="right">
                        <b>三组</b>
                    </td>
                    <td align="center">285</td>
                    <td align="center">3026</td>
                </tr>
                <tr style="background-color: #9acc02">
                    <td align="right">
                        <b>四组</b>
                    </td>
                    <td align="center">278</td>
                    <td align="center">2070</td>
                </tr>
                <tr style="background-color: #9acc02">
                    <td align="right">
                        <b>五组</b>
                    </td>
                    <td align="center">129</td>
                    <td align="center">1145</td>
                </tr>
                <tr style="background-color: #51cc1a">
                    <td align="left">
                        <b>二分指</b>
                    </td>
                    <td align="center">1150</td>
                    <td align="center">10486</td>
                </tr>
                <tr style="background-color: #ffcc02">
                    <td align="right">
                        <b>三分指</b>
                    </td>
                    <td align="center">-</td>
                    <td align="center">-</td>
                </tr>
                <tr style="background-color: #ffcc02">
                    <td align="right">
                        <b>四分指</b>
                    </td>
                    <td align="center">-</td>
                    <td align="center">-</td>
                </tr>
                <tr style="background-color: #ffcc02">
                    <td align="right">
                        <b>五分指</b>
                    </td>
                    <td align="center">-</td>
                    <td align="center">-</td>
                </tr>
                <tr style="background-color: #ffa309">
                    <td align="left">
                        <b>三、四、五</b>
                    </td>
                    <td align="center">2349</td>
                    <td align="center">25885</td>
                </tr>
                <tr style="background-color: #cccccc">
                    <td align="left">
                        <b>历史数据</b>
                    </td>
                    <td align="center">-</td>
                    <td align="center">-</td>
                </tr>
            </table>
        </div>
    </div>
</div>
<oframe:script src="${pageContext.request.contextPath}/eland/rp/rp001/js/rp001.js" type="text/javascript"/>
