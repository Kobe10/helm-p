<%--待办任务列表--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="mar5">
    <div style="position: relative;">
        <div class="panelBar">
            <ul class="toolBar">
                <!-- <li onclick="sys016.openQSch();">-->
                <li onmousemove="sys016.openQSch(this);"
                    onclick="sys016.queryHs(this);">
                    <a class="find" href="javascript:void(0)"><span>组件检索</span></a>
                </li>
                <li onclick="sys016.query();">
                    <a class="reflesh" href="javascript:void(0)"><span>刷新检索</span></a>
                </li>
                <li onclick="sys016.editView('','0',this);">
                    <a class="add" href="javascript:void(0);"><span>新增组件</span></a>
                </li>
                <li>
                    <a onclick="sys016.batchDeleteView()" class="delete-more"><span>批量删除</span></a>
                </li>
                <li>
                    <a class="export" onclick="sys016.exportEntity()"><span>导出配置</span></a>
                </li>
                <li>
                    <a class="import">
                        <span onclick="sys016.startImport(this);" style="position: relative;">导入配置
                              <input style="width:110px; height:37px; position:absolute; right:0; top:0; opacity:0;filter:alpha(opacity=0); z-index:11; cursor:pointer;"
                                     name="importComponentFile" id="importComponentFile" type="file">
                        </span>
                    </a>
                </li>
            </ul>
        </div>
        <div id="sys016create" class="hidden"
             style="position: absolute;width:100%;top: 0px;z-index: 100;background-color: #ffffff; margin-top: 40px;">
            <div class="triangle triangle-up" style="border-bottom-color: #2c7bae;top: -10px; left: 25px;"></div>
            <table class="border">
                <tr>
                    <th><label>组件名称：</label></th>
                    <td>
                        <input name="componentNameEn" condition="like" type="text" value=""/>
                    </td>
                    <th><label>组件实现：</label></th>
                    <td>
                        <input name="componentName" condition="like" type="text">
                    </td>
                    <th><label>组件描述：</label></th>
                    <td>
                        <input name="componentNote" condition="like" type="text">
                    </td>
                </tr>
                <tr>
                    <td colspan="8" align="center">
                        <button onclick="sys016.query();sys016.closeQSch()" type="button" id="schBtn"
                                class="js_faTask btn btn-primary">
                            查询
                        </button>
                        <button onclick="sys016.closeQSch(false)" type="button" class="btn btn-info">关闭</button>
                    </td>
                </tr>
            </table>
        </div>
        <form id="sys016form" method="post">
            <input type="hidden" name="entityName" value="PrjComponent"/>
            <input type="hidden" name="conditionName" value="">
            <input type="hidden" name="condition" value="">
            <input type="hidden" name="conditionValue" value="">
            <input type="hidden" name="sortColumn" value="createTime">
            <input type="hidden" name="sortOrder" value="desc">
            <input type="hidden" name="divId" value="sys016_page_data">
            <input type="hidden" name="forceResultField" value="prjCd,componentNameEn">
            <input type="hidden" class="js_conditionValue" value="">
            <input type="hidden" name="cmptName" id="cmptName" value="QUERY_ENTITY_PAGE_DATA"/>
            <input type="hidden" name="resultField"
                   value="prjCd,componentName,componentNameEn,componentNote,createTime">
            <input type="hidden" name="forward" id="forward" value="/oframe/sysmg/sys016/sys016_list"/>
        </form>
        <div id="sys016_page_data" class="js_page" layoutH="55"></div>
    </div>
</div>
<oframe:script type="text/javascript" src="${pageContext.request.contextPath}/oframe/sysmg/sys016/js/sys016.js"/>

