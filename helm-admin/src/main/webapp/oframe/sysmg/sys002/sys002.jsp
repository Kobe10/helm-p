<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="pageHeader">
    <form id="sys002frm" method="post">
        <%--自服务名称用于定位SQL--%>
        <input type="hidden" name="subSvcName" value="sys002"/>
        <%-- 分页展示 --%>
        <input type="hidden" name="forward" id="forward" value="/oframe/sysmg/sys002/sys002_list"/>

        <div class="searchBar">
            <table class="form">
                <tr>
                    <th>
                        登录帐号：
                    </th>
                    <td>
                        <input type="text" name="staffCode"/>
                    </td>
                    <th>
                        用户姓名：
                    </th>
                    <td>
                        <input type="text" name="staffName"/>
                    </td>
                    <td style="text-align:left;">
                        <button type="button" id="schBtn" class="btn btn-primary"
                                onclick="Query.queryList('sys002frm', 'sys002_list_print');">检索
                        </button>
                        <button type="reset" class="btn btn-info">重置</button>
                    </td>
                </tr>
            </table>
        </div>
    </form>
</div>
<div class="pageContent">
    <div class="panelBar">
        <ul class="toolBar">
            <oframe:op prjCd="${param.prjCd}" template="li" cssClass="add"
                       name="添加用户" rhtCd="ADD_SYS_USE_RHT"
                       onClick="sys002.openAdd()"/>
            <li class="line">line</li>
            <oframe:op prjCd="${param.prjCd}" template="li" cssClass="export"
                       name="导出EXCEL" rhtCd="EXP_SYS_USE_RHT"
                       onClick="Query.exportExcelByTemplate('sys002_list_print','webroot:oframe/sysmg/sys002/用户管理信息导出excl模版.xls')"/>
        </ul>
    </div>
    <div id="sys002_list_print">
        <table class="table" width="100%" layoutH="140">
            <thead>
            <tr>
                <th width="80" onclick="Query.sort('sys002_list_print', this);"
                    defaultSort="asc" sortColumn="STAFF_CODE" class="sortable">系统账户
                </th>
                <th width="80" onclick="Query.sort('sys002_list_print', this);"
                    sortColumn="STAFF_NAME" class="sortable">用户姓名
                </th>
                <th width="100">联系电话</th>
                <th width="80">状态</th>
                <th width="100">状态时间</th>
                <th width="80">操作</th>
            </tr>
            </thead>
            <tbody>

            </tbody>
        </table>
    </div>
    <c:import url="/oframe/common/page/pager_fotter.jsp" charEncoding="UTF-8"/>
</div>
<oframe:script src="${pageContext.request.contextPath}/oframe/sysmg/sys002/js/sys002.js"
               type="text/javascript"/>
