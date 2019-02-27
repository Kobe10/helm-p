<%--
   院落信息展示界面
--%>
<%--院落信息检索--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<oframe:script src="${pageContext.request.contextPath}/eland/ph/ph002/js/ph002_op.js" type="text/javascript"/>
<oframe:script src="${pageContext.request.contextPath}/eland/ph/ph003/js/ph003_op.js" type="text/javascript"/>
<%--基本信息--%>
<div class="panel">
    <h1>院落信息
        <button type="button" style="float: right;" class="btn btn-more mart5 marr5">
            <i style="font-style:normal;">院落操作</i>
            <span class="caret"></span>
            <ul style="width: 200px;" class="menu hidden">
                <jsp:include page="/eland/pb/pb002/pb002_op.jsp">
                    <jsp:param name="buildId" value="${buildBean.BuildInfo.buildId}"/>
                    <jsp:param name="buildType" value="${buildBean.BuildInfo.buildType}"/>
                </jsp:include>
            </ul>
        </button>
    </h1>
    <input type="hidden" name="buildId" value="${buildBean.BuildInfo.buildId}">
    <%--面板内容--%>
    <div class="js_panel_context" style="background-color: #ffffff;">
        <table class="border">
            <tr>
                <th width="8%">院落地址：</th>
                <td width="25%">${buildBean.BuildInfo.buildFullAddr}</td>
                <th width="8%">院落性质：</th>
                <td width="15%"><oframe:name prjCd="${param.prjCd}" itemCd="BUILD_TYPE" value="${buildBean.BuildInfo.buildType}"/></td>
                <th width="8%">院落状态：</th>
                <td width="15%"><oframe:name prjCd="${param.prjCd}" itemCd="YARD_STATUS"
                                             value="${buildBean.BuildInfo.buildStatus}"/></td>
            </tr>
            <tr>
                <th>占地面积：</th>
                <td>${buildBean.BuildInfo.buildLandSize}</td>
                <th>建面单价：</th>
                <td><oframe:money value="${buildBcYg.BuildInfo.totalJmdj}" format="number"/></td>
                <th>占地单价：</th>
                <td><oframe:money value="${buildBcYg.BuildInfo.totalZddj}" format="number"/></td>
            </tr>
            <tr>
                <th><label>管理小组：</label></th>
                <td>${buildMng.BuildInfo.ttOrgId_Name}</td>
                <th><label>中介公司：</label></th>
                <td>${buildMng.BuildInfo.ttCompanyName}</td>
                <th><label>主谈人员：</label></th>
                <td>${buildMng.BuildInfo.ttMainTalk}</td>
            </tr>
            <tr>
                <th style="line-height:50px;">备注说明：</th>
                <td style="line-height:50px;" colspan="7">${buildBean.BuildInfo.buildNote}</td>
            </tr>
        </table>
    </div>
</div>
<%--房屋信息--%>
<div class="mart5">
    <div class="panelBar">
        <ul class="toolBar">
            <oframe:op prjCd="${param.prjCd}" template="li" cssClass="add"
                       name="信息登记" rhtCd="add_old_hs_rht"
                       onClick="pb001.addHouse(this);"/>
            <oframe:op prjCd="${param.prjCd}" template="li" cssClass="delete-more"
                       name="批量删除" rhtCd="del_old_hs_rht"
                       onClick="pb001.deleteSltHs(this);"/>
            <oframe:op prjCd="${param.prjCd}" template="li" cssClass="export"
                       name="导出EXCEL" rhtCd="exp_old_hs_rht"
                       onClick="pb001.exportHs(this);"/>
            <li><a class="print" href="javascript:$.printBox('pb001HsList')"><span>打印</span></a></li>
        </ul>
    </div>
    <form id="ph00104frm" method="post">
        <input type="hidden" name="conditionName" value="HouseInfo.buildId">
        <input type="hidden" name="condition" value="=">
        <input type="hidden" name="conditionValue" value="${buildBean.BuildInfo.buildId}">
        <input type="hidden" name="sortColumn" value="HouseInfo.hsCd">
        <input type="hidden" name="sortOrder" value="asc">
        <input type="hidden" name="resultField"
               value="HouseInfo.hsCd,HouseInfo.hsFullAddr,HouseInfo.hsOwnerPersons,HouseInfo.hsOwnerType,HouseInfo.hsUseSize,HouseInfo.hsBuildSize">
        <input type="hidden" name="forceResultField" value="HouseInfo.hsId">
        <input type="hidden" name="regUseType" value="1">
        <input type="hidden" name="entityName" value="HouseInfo">
        <input type="hidden" name="divId" value="pb001HsList">
        <input type="hidden" name="forward" id="forward" value="/eland/pb/pb001/pb001_hs_list"/>
    </form>
    <div class="js_page" layoutH="265">
    </div>
</div>
<script type="text/javascript">
    $(document).ready(function () {
        pb001.queryBuildHs();
    });
</script>
