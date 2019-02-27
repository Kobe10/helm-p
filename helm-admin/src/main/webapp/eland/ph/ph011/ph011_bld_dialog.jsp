<%--人地房信息展示页面--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div>
    <input type="hidden" name="ph011BldDialog" value="1"/>

    <form id="ph011BldDialogQueryForm">
        <input type="hidden" name="entityName" value="HouseInfo"/>
        <input type="hidden" name="cRegId" value=""/>
        <input type="hidden" name="cBuildId" value="${buildId}"/>
        <input type="hidden" name="cHsId" value=""/>
        <input type="hidden" name="regUseType" value="1">
        <input type="hidden" name="sortColumn" value="HouseInfo.hsFullAddr">
        <input type="hidden" name="sortOrder" value="asc">
        <input type="hidden" class="js_conditionValue" value="">
        <input type="hidden" name="forceResultField" value="HouseInfo.hsId,HouseInfo.HsCtInfo.hsCtId">
        <input type="hidden" name="divId" value="ph011Context">
        <input type="hidden" name="resultField"
               value="HouseInfo.hsCd,HouseInfo.hsFullAddr,HouseInfo.hsOwnerPersons,HouseInfo.hsOwnerType,HouseInfo.hsBuildSize,HouseInfo.hsStatus">
        <input type="hidden" name="forward" id="forward" value="/eland/ph/ph011/ph011_list"/>
        <input type="hidden" name="cmptName" value="QUERY_DOCUMENTINFO_PAGE">
    </form>
    <div class="panelBar">
        <ul class="toolBar">
            <li id="upRegionLink">
                <a class="hight-level" onclick="ph011.showUpFolder()"><span>上级</span></a>
            </li>
            <li id="exprExcel">
                <a class="export" onclick="ph011.exportFjCl();"><span>导出</span></a>
            </li>
        </ul>
    </div>
    <div id="ph011Context" layoutH="45" ondragenter="return false" ondragover="return false"
         style="border: 1px solid #e9e9e9;">
        <jsp:include page="/eland/ph/ph011/ph011-showBuildFile.gv">
            <jsp:param name="cBuildId" value="${buildId}"/>
        </jsp:include>
    </div>
</div>
<oframe:script src="${pageContext.request.contextPath}/eland/ph/ph011/js/ph011.js" type="text/javascript"/>