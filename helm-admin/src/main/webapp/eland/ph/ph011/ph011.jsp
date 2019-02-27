<%--人地房信息展示页面--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="mar5" style="vertical-align: top;">
    <%--左侧区域树--%>
    <div style="width: 240px;float: left;position: relative;" id="ph011Left" layoutH="15" class="panel left_menu">
        <h1>
            <span class="panel_title">区 域 导 航</span>
            <span onclick="ph011.extendOrClose(this);" class="panel_menu js_reload">展开</span>
        </h1>

        <ul id="ph011Tree" layoutH="65" class="ztree"></ul>
    </div>
    <div class="split_line" layoutH="15">
        <span class="split_but" title="展开/隐藏导航"></span>
    </div>
    <%--右侧自定义画板--%>
    <div id="pb011Right" style="margin-left: 250px;margin-right: 5px;position: relative;">
        <div class="js_query_condition" style="position: relative;">
            <div class="panelBar">
                <ul class="toolBar">
                    <li id="upRegionLink">
                        <a class="hight-level" onclick="ph011.showUpFolder()"><span>上级</span></a>
                    </li>
                    <li id="exprExcel" style="float:right;display: none">
                        <a class="export" onclick="ph011.exportFjCl();"><span>导出</span></a>
                    </li>
                </ul>
            </div>
        </div>
        <input type="hidden" class="js_query_model" value="2"/>
        <input type="hidden" class="figureBuildId" value=""/>
        <input type="hidden" class="figureRhtId" value=""/>


        <form id="ph011queryForm">
            <input type="hidden" name="entityName" value="HouseInfo"/>
            <input type="hidden" name="cRegId" value=""/>
            <input type="hidden" name="cBuildId" value=""/>
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
        <div id="ph011Context" layoutH="57"
             class="js_page"
             ondrop="ph011.uploadFile()"
             ondragenter="return false"
             ondragover="return false"
             style="border: 1px solid #e9e9e9;">
        </div>

    </div>
</div>
<oframe:script src="${pageContext.request.contextPath}/eland/ph/ph011/js/ph011.js" type="text/javascript"/>
<oframe:script src="${pageContext.request.contextPath}/eland/ph/ph003/js/ph003_op.js" type="text/javascript"/>
<oframe:script src="${pageContext.request.contextPath}/eland/ph/ph002/js/ph002_op.js" type="text/javascript"/>
<script>
    $(document).ready(function () {
        //初始化左侧区域树
        ph011.initFullRegTree();

        // 拖动屏幕切分效果
        LeftMenu.init("ph011Left", "pb011Right", {});
    });
</script>