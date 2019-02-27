<%--
   腾退区域，要求配置腾退安置系数
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
                <th><label>可选区域：</label></th>
                <td>
                    <oframe:select prjCd="${param.prjCd}" type="radio" id="regAttr7" itemCd="COMMON_YES_NO"
                                   name="regAttr7" value="${nodeInfo.PrjReg.regAttr7}"/>
                </td>
            </tr>
            <tr>
                <th><label>安置系数：</label></th>
                <td>
                    <input type="text" name="regAttr8" class="required" value="${nodeInfo.PrjReg.regAttr8}"/>
                </td>
                <th><label>区域均价：</label></th>
                <td>
                    <input type="text" name="regAttr9" class="required number" value="${nodeInfo.PrjReg.regAttr9}"/>
                </td>
            </tr>
            <tr>
                <th><label><oframe:name prjCd="${param.prjCd}" itemCd="HS_ROOM_TYPE" value="4"/>申请数量：</label></th>
                <td>
                    <input type="text" name="regAttr10" class="required" value="${nodeInfo.PrjReg.regAttr10}"/>
                </td>
                <th><label><oframe:name prjCd="${param.prjCd}" itemCd="HS_ROOM_TYPE" value="4"/>已用数量：</label></th>
                <td>
                    <input type="text" readonly class="readonly number" value="${ht_4!=null? ht_4: 0}"/>
                </td>
            </tr>
            <tr>
                <th><label><oframe:name prjCd="${param.prjCd}" itemCd="HS_ROOM_TYPE" value="1"/>申请数量：</label></th>
                <td>
                    <input type="text" name="regAttr11" class="required" value="${nodeInfo.PrjReg.regAttr11}"/>
                </td>
                <th><label><oframe:name prjCd="${param.prjCd}" itemCd="HS_ROOM_TYPE" value="1"/>已用数量：</label></th>
                <td>
                    <input type="text" readonly class="readonly number" value="${ht_1!=null? ht_1: 0}"/>
                </td>
            </tr>
            <tr>
                <th><label><oframe:name prjCd="${param.prjCd}" itemCd="HS_ROOM_TYPE" value="2"/>申请数量：</label></th>
                <td>
                    <input type="text" name="regAttr12" class="required" value="${nodeInfo.PrjReg.regAttr12}"/>
                </td>
                <th><label><oframe:name prjCd="${param.prjCd}" itemCd="HS_ROOM_TYPE" value="2"/>已用数量：</label></th>
                <td>
                    <input type="text" readonly class="readonly number" value="${ht_2!=null? ht_2: 0}"/>
                </td>
            </tr>
            <tr>
                <th><label><oframe:name prjCd="${param.prjCd}" itemCd="HS_ROOM_TYPE" value="3"/>申请数量：</label></th>
                <td>
                    <input type="text" name="regAttr13" class="required" value="${nodeInfo.PrjReg.regAttr13}"/>
                </td>
                <th><label><oframe:name prjCd="${param.prjCd}" itemCd="HS_ROOM_TYPE" value="3"/>已用数量：</label></th>
                <td>
                    <input type="text" readonly class="readonly number" value="${ht_3!=null? ht_3: 0}"/>
                </td>
            </tr>

            <tr>
                <th><label>区域描述：</label></th>
                <td colspan="3">
                    <textarea name="regDesc" style="width: 84%;" rows="4">${nodeInfo.PrjReg.regDesc}</textarea>
                </td>
            </tr>
        </table>
    </form>
</div>