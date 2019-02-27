<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="mar5">
    <div class="tabs">
        <div class="tabsHeader">
            <div class="tabsHeaderContent">
                <ul>
                    <li class="selected" onclick="sys008.loadTabContext(1)">
                        <a href="javascript:void(0);"><span>当前登录</span></a>
                    </li>
                    <li onclick="sys008.loadTabContext(2);">
                        <a href="javascript:void(0);"><span>历史登录</span></a>
                    </li>
                </ul>
            </div>
        </div>
        <div class="tabsContent">
            <%--当前登录区--%>
            <form id="sys008frm" method="post">
                <%--自服务名称用于定位SQL--%>
                <input type="hidden" name="subSvcName" value="sys008"/>
                <%-- 分页展示 --%>
                <input type="hidden" name="forward" id="forward" value="/oframe/sysmg/sys008/sys008_list"/>

                <div style="position: relative;">
                    <div class="panelBar">
                        <ul class="toolBar">
                            <li onclick="sys008.openQSch('1');">
                                <a class="find" href="javascript:void(0);"><span>条件检索</span></a>
                            </li>
                            <li onclick="Query.jumpPage(1,'sys008_list_print');">
                                <a class="reflesh" href="javascript:void(0);"><span>刷新检索</span></a>
                            </li>
                            <li onclick="sys008.forceQuit('publish');">
                                <a class="delete-more" href="javascript:void(0);"><span>强制退出</span></a>
                            </li>
                        </ul>
                    </div>
                    <div class="hidden" id="sys008create1"
                         style="position: absolute;width:100%;top: 0px;z-index: 100;background-color: #ffffff; margin-top: 40px;">
                        <table class="border">
                            <tr>
                                <th>
                                    登录工号：
                                </th>
                                <td>
                                    <input type="text" name="staffCd"/>
                                </td>
                                <th>
                                    登录时间：
                                </th>
                                <td>
                                    <input type="text" class="date" dateFmt="yyyy-MM-dd" name="loginTime"/>
                                </td>
                            </tr>
                            <tr>
                                <td align="center" colspan="4">
                                    <button type="button" id="schBtn" class="btn btn-primary"
                                            onclick="Query.queryList('sys008frm', 'sys008_list_print');sys008.closeQSch();">
                                        检索
                                    </button>
                                    <button onclick="sys008.closeQSch()" type="button" class="btn btn-info">关闭</button>
                                </td>
                            </tr>
                        </table>
                    </div>
                    <div id="sys008_list_print">
                        <table class="table" width="100%" layoutH="165">
                            <thead>
                            <tr>
                                <th fieldName="newId">
                                    <input type="checkbox" class="checkboxCtrl" group="ids"/>
                                </th>
                                <th onclick="Query.sort('sys008_list_print', this);"
                                    sortColumn="login_accept" class="sortable">登录流水号
                                </th>
                                <th onclick="Query.sort('sys008_list_print', this);"
                                    sortColumn="staff_code" class="sortable">登录工号
                                </th>
                                <th>工号名称</th>
                                <th>登录IP</th>
                                <th onclick="Query.sort('sys008_list_print', this);" defaultSort="desc"
                                    sortColumn="login_time" class="sortable">登录时间
                                </th>
                                <th>操作</th>
                            </tr>
                            </thead>
                            <tbody>
                            </tbody>
                        </table>
                    </div>
                </div>
            </form>
            <%--历史登录区--%>
            <form id="sys008his" method="post">
                <%--自服务名称用于定位SQL--%>
                <input type="hidden" name="subSvcName" value="sys009"/>
                <%-- 分页展示 --%>
                <input type="hidden" name="forward" id="forward" value="/oframe/sysmg/sys008/sys008_list"/>

                <div style="position: relative;">
                    <div class="panelBar">
                        <ul class="toolBar">
                            <li onclick="sys008.openQSch('2');">
                                <a class="find" href="javascript:void(0);"><span>条件检索</span></a>
                            </li>
                            <li onclick="Query.jumpPage(1,'sys008_list_history');">
                                <a class="reflesh" href="javascript:void(0);"><span>刷新检索</span></a>
                            </li>
                        </ul>
                    </div>
                    <div class="hidden" id="sys008create2"
                         style="position: absolute;width:100%;top: 0px;z-index: 100;background-color: #ffffff; margin-top: 40px;">
                        <table class="border">
                            <tr>
                                <th>
                                    登录月份：
                                </th>
                                <td>
                                    <input type="text" name="logYm" dateFmt="yyyyMM" class="date"/>
                                </td>
                                <th>
                                    登录工号：
                                </th>
                                <td>
                                    <input type="text" name="staffCd"/>
                                </td>
                                <th>
                                    登录时间：
                                </th>
                                <td>
                                    <input type="text" class="date" dateFmt="yyyy-MM-dd" name="loginTime"/>
                                </td>
                            </tr>
                            <tr>
                                <td align="center" colspan="6">
                                    <button type="button" class="btn btn-primary"
                                            onclick="Query.queryList('sys008his', 'sys008_list_history');sys008.closeQSch('2');">
                                        检索
                                    </button>
                                    <button onclick="sys008.closeQSch('2');" type="button" class="btn btn-info">关闭
                                    </button>
                                </td>
                            </tr>
                        </table>
                    </div>
                    <div id="sys008_list_history">
                        <table class="table" width="100%" layoutH="165">
                            <thead>
                            <tr>
                                <th onclick="Query.sort('sys008_list_history', this);"
                                    sortColumn="login_accept" class="sortable">登录流水号
                                </th>
                                <th onclick="Query.sort('sys008_list_history', this);"
                                    sortColumn="staff_code" class="sortable">登录工号
                                </th>
                                <th>工号名称</th>
                                <th>登录IP</th>
                                <th onclick="Query.sort('sys008_list_history', this);" defaultSort="desc"
                                    sortColumn="login_time" class="sortable">登录时间
                                </th>
                                <th onclick="Query.sort('sys008_list_history', this);"
                                    sortColumn="logout_time" class="sortable">退出时间
                                </th>
                                <th>退出状态</th>
                            </tr>
                            </thead>
                            <tbody>
                            </tbody>
                        </table>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>
<oframe:script src="${pageContext.request.contextPath}/oframe/sysmg/sys008/js/sys008.js"
               type="text/javascript"/>