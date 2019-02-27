<%--待办任务列表--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="mar5 hm001Div">
    <div style="position: relative;">
        <div class="panelBar">
            <ul class="toolBar">
                <li onmousemove="hm001.openQSch(this);"
                    onclick="hm001.queryNotice(this);">
                    <a class="find" href="javascript:void(0)"><span>数据检索</span></a>
                </li>
                <li onclick="hm001.query();">
                    <a class="reflesh" href="javascript:void(0)"><span>刷新检索</span></a>
                </li>
                <li onclick="hm001.editView('');">
                    <a class="add" href="javascript:void(0);"><span>新增事记</span></a>
                </li>
                <li>
                    <a onclick="hm001.batchDelete(this)" class="delete-more"><span>批量删除</span></a>
                </li>
                <li>
                    <a onclick="hm001.batchClear(this)" class="cancel"><span>清空排序</span></a>
                </li>
            </ul>
        </div>
        <div id="hm001create" class="hidden"
             style="position: absolute;width:100%;top: 0px;z-index: 100;background-color: #ffffff; margin-top: 40px;">
            <div class="triangle triangle-up" style="border-bottom-color: #2c7bae;top: -10px; left: 25px;"></div>
            <table class="border">
                <tr>
                    <th><label>事记名称：</label></th>
                    <td>
                        <input name="noticeTitle" condition="like" type="text" value=""/>
                    </td>
                    <th><label>发布日期：</label></th>
                    <td>
                        <input name="publishDate" class="date" datefmt="yyyy-MM-dd" condition="=" type="text">
                    </td>
                    <th><label>发布状态：</label></th>
                    <td>
                        <input name="publishStatus" condition="=" type="hidden">
                        <input itemCd="NOTICE_STATUS" atOption="CODE.getCfgDataOpt" class="autocomplete" type="text">
                    </td>
                </tr>
                <tr>
                    <td colspan="8" align="center">
                        <button onclick="hm001.query();hm001.closeQSch()" type="button" id="schBtn"
                                class="js_faTask btn btn-primary">
                            查询
                        </button>
                        <button onclick="hm001.closeQSch(false)" type="button" class="btn btn-info">关闭</button>
                    </td>
                </tr>
            </table>
        </div>
        <form id="hm001form" method="post">
            <input type="hidden" name="entityName" value="HelmNotice"/>
            <input type="hidden" name="conditionName" value="">
            <input type="hidden" name="condition" value="">
            <input type="hidden" name="conditionValue" value="">
            <input type="hidden" name="sortColumn" value="publishStatus,publishOrder,publishDate">
            <input type="hidden" name="sortOrder" value="asc,desc,desc">
            <input type="hidden" name="divId" value="hm001_page_data">
            <input type="hidden" name="forceResultField" value="noticeTitle,publishStatus,publishDate">
            <input type="hidden" class="js_conditionValue" value="">
            <input type="hidden" name="cmptName" id="cmptName" value="QUERY_ENTITY_PAGE_DATA"/>
            <input type="hidden" name="resultField"
                   value="noticeType,noticeTitle,publishStatus,publishOrder,publishDate">
            <input type="hidden" name="forward" id="forward" value="/helm/hm001/hm001_list"/>
        </form>
        <div id="hm001_page_data" class="js_page" layoutH="55"></div>
    </div>
</div>
<oframe:script type="text/javascript" src="${pageContext.request.contextPath}/helm/hm001/js/hm001.js"/>

