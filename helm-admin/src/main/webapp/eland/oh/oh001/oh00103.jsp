<%--
   普通房源区域修改
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div id="regionBar" class="panelBar">
    <ul class="toolBar">
        <oframe:op prjCd="${param.prjCd}" template="li" cssClass="save"
                   name="保存" rhtCd="edit_3_reg_rht"
                   onClick="oh001.saveRegionInfo()"/>
        <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
    </ul>
</div>
<div class="pageContent" layoutH="50" id="pj004RegionDetail">
    <form id="oh00103Rgform" method="post" class="required-validate">
        <input type="hidden" name="prjCd" value="${nodeInfo.PrjReg.prjCd}">
        <input type="hidden" name="regId" value="${nodeInfo.PrjReg.regId}">
        <input type="hidden" name="upRegId" value="${nodeInfo.PrjReg.upRegId}">
        <input type="hidden" name="oldPrjOrgId" value="${nodeInfo.PrjReg.prjOrgId}">
        <input type="hidden" name="regUseType" value="${nodeInfo.PrjReg.regUseType}">
        <input type="hidden" name="prjBuldId" value="${nodeInfo.PrjReg.prjBuldId}">
        <input type="hidden" name="lastUpdateTime" value="${nodeInfo.PrjReg.lastUpdateTime}">
        <input type="hidden" name="regEntityType" value="${nodeInfo.PrjReg.regEntityType}">
        <table class="form">
            <tr>
                <th><label>区域名称：</label></th>
                <td>
                    <input type="text" name="regName" ${readonly} class="required" value="${nodeInfo.PrjReg.regName}"/>
                </td>
                <th><label>管理小组：</label></th>
                <td>
                    <div style="position: relative;">
                        <input type="hidden" name="prjOrgId" value="${nodeInfo.PrjReg.prjOrgId}"/>
                        <input type="text" name="prjOrgName" readonly class="pull-left"
                               value="${nodeInfo.PrjReg.prjOrgName}"/>
                        <a title="选择" class="btnLook" onclick="$(this).sltOrg(this, {});">选择</a>
                    </div>
                </td>
            </tr>
            <tr>
                <th><label>区域描述：</label></th>
                <td colspan="3">
                    <textarea name="regDesc" cols="60" rows="4">${nodeInfo.PrjReg.regDesc}</textarea>
                </td>
            </tr>
        </table>
    </form>
</div>