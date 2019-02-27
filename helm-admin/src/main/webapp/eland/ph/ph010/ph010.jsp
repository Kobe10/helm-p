<%--人地房信息展示页面--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="js_ph010 mar5">
    <div class="pageNav">
        <a class="current">现场视频</a>
    </div>
    <div class="" style="vertical-align: top;">
        <div id="ph010" style="position: relative;">
            <div class="js_query_condition" style="position: relative;">
                <div class="panelBar">
                    <ul class="toolBar">
                        <li onclick="ph010.queryHs(this);">
                            <a title="点击进入高级检索" class="find" href="javascript:void(0)"><span>检索</span></a>
                        </li>
                        <li onclick="ph010.query();">
                            <a title="点击进入高级检索" class="reflesh" href="javascript:void(0)"><span>刷新</span></a>
                        </li>
                        <li onclick="ph010.editView('')">
                            <a class="add" href="javascript:void(0)"><span>新增</span>
                            </a>
                        </li>
                        <li>
                            <%--<a class="export">--%>
                            <a class="export" onclick="Page.exportExcel('ph010_list_print', false)">
                                <span>导出</span>
                            </a>
                        </li>
                        <li style="position: relative;" onclick="ph010.importView()"><a class="import" title="导入">
                            <span>导入</span>
                        </a></li>
                    </ul>
                </div>
            </div>
            <form id="ph010queryForm">
                <input type="hidden" name="entityName" value="CameraMngInfo"/>
                <input type="hidden" name="conditionName" value="">
                <input type="hidden" name="condition" value="">
                <input type="hidden" name="conditionValue" value="">
                <input type="hidden" name="sortColumn" value="">
                <input type="hidden" name="sortOrder" value="">
                <input type="hidden" class="js_conditionValue" value="">
                <input type="hidden" name="forceResultField" value="camId">
                <input type="hidden" name="divId" value="ph010Context">
                <input type="hidden" name="resultField" value="camCd,channelCd,camAddr">
                <input type="hidden" name="forward" id="forward" value="/eland/ph/ph010/ph010_list"/>
                <input type="hidden" name="cmptName" id="cmptName" value="QUERY_ENTITY_PAGE_DATA"/>
            </form>
            <div id="ph010Context" class="js_page" layoutH="55" style="position: relative;width: 100%;"></div>
        </div>
    </div>
</div>
<oframe:script src="${pageContext.request.contextPath}/eland/ph/ph010/js/ph010.js" type="text/javascript"/>
