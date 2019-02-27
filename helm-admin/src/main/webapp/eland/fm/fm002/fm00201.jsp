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
<div class="panelBar">
    <ul class="toolBar">
        <li onclick="fm002.savePlan('${PaymentPlan.PaymentPlan.pId}')">
            <a class="save" href="javascript:void(0)"><span>保存</span></a>
        </li>
        <li onclick="fm002.delPlan('${PaymentPlan.PaymentPlan.pId}')">
            <a class="delete" href="javascript:void(0)"><span>删除</span></a>
        </li>
    </ul>
</div>
<div id="fm002SchemeDiv" class="panelcontainer" layoutH="55" style="border: 1px solid #e9e9e9;position: relative">
    <input type="hidden" name="pId" value="${PaymentPlan.PaymentPlan.pId}"/>

    <form id="fm002Form">
        <table class="border">
            <tr>
                <th width="5%"><label>计划名称：</label></th>
                <td width="10%"><input type="text" name="pCode" class="required noErrorTip"
                                       value="${PaymentPlan.PaymentPlan.pCode}"/></td>
                <th width="5%"><label>计划状态：</label></th>
                <td width="10%"><oframe:select prjCd="${param.prjCd}" itemCd="PAY_PLAN_STATUS" name="pStatus"
                                               value="${PaymentPlan.PaymentPlan.pStatus}"/></td>
                <th width="5%"><label>五联单表单：</label></th>
                <td width="10%">
                    <input type="text" name="pFormCd" value="${PaymentPlan.PaymentPlan.pFormCd}"/>
                </td>
            </tr>
            <tr>
                <th width="5%"><label>付款关联协议：</label></th>
                <td colspan="5">
                    <oframe:select itemCd="SCHEME_TYPE" sltValues="${pCtSchemeIds}" name="pCtSchemeId"
                                   prjCd="${param.prjCd}"
                                   type="checkbox"/>
                </td>
            </tr>
            <tr>
                <td colspan="6">
                    <div class="tabs mart5">
                        <div class="tabsHeader" style="border-bottom: 1px solid #3d91c8;">
                            <div class="tabsHeaderContent">
                                <ul>
                                    <li><a href="javascript:void(0);"><span>付款条件要求</span></a></li>
                                    <li><a href="javascript:void(0);"><span>付款总金额计算方式</span></a></li>
                                </ul>
                            </div>
                        </div>

                        <div class="tabsContent" id="wf006DsContainer">
                            <div>
                                <textarea name="pPreRule" class="hidden">${PaymentPlan.PaymentPlan.pPreRule}</textarea>
                                <iframe name="fm002IFrameRule" allowTransparency="false" id="fm002IFrameRule"
                                        width="99.5%;"
                                        style="background-color:#e2edf3;"
                                        src="${pageContext.request.contextPath}/eland/fm/fm002/fm002-payCode.gv?flag=rule"
                                        layoutH="300">
                                </iframe>
                            </div>
                            <div>
                                <textarea name="pCalcMoney"
                                          class="hidden">${PaymentPlan.PaymentPlan.pCalcMoney}</textarea>
                                <iframe name="fm002IFrameMoney" allowTransparency="false" id="fm002IFrameMoney"
                                        width="99.5%;"
                                        style="background-color:#e2edf3;"
                                        src="${pageContext.request.contextPath}/eland/fm/fm002/fm002-payCode.gv?flag=money"
                                        layoutH="300">
                                </iframe>
                            </div>
                        </div>
                    </div>
                </td>
            </tr>
        </table>
    </form>
</div>
