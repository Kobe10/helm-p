<%--待办任务列表--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>

<div class="mar5">
    <div class="pageNav">
        <a class="current">收藏菜单</a>
    </div>
    <div class="tabs mart5 ">
        <div class="tabsContent" style="padding: 0;border-top: 1px solid #3d91c8;">
            <div class="js_star_task" style="position: relative;">
                <div class="panelBar">
                    <ul class="toolBar">
                        <li onclick="sys010.openQSch();">
                            <a class="find" href="javascript:void(0)"><span>检索</span></a>
                        </li>
                        <li onclick="sys010.check_input_box();">
                            <a class="reflesh" href="javascript:void(0);"><span>刷新</span></a>
                        </li>
                    </ul>
                </div>
                <form id="sys010frm" method="post">
                    <%--自服务名称用于定位SQL--%>
                    <input type="hidden" name="subSvcName" value="sys010"/>
                    <%-- 分页展示 --%>
                    <input type="hidden" name="forward" id="forward" value="/oframe/sysmg/sys010/sys010_list"/>

                    <div id="sys010Create" class="hidden"
                         style="position: absolute;width:100%;top: 0px;z-index: 100;background-color: #ffffff; margin-top: 40px;">
                        <div class="triangle triangle-up"
                             style="border-bottom-color: #2c7bae;top: -10px; left: 25px;"></div>
                        <table class="border">
                            <tr>
                                <th>
                                    功能名称：
                                </th>
                                <td>
                                    <input type="text" id="rht_name" name="rht_name"/>
                                </td>
                                <oframe:power prjCd="${param.prjCd}" rhtCd="collection_view">
                                    <th>
                                        员工编号：
                                    </th>
                                    <td>
                                        <input type="text" id="staff_code" name="staff_code"
                                               atOption="sys010.getOption"
                                               atUrl="sys010.getUrl"
                                               value="${baseInfo.BaseInfo.ownStaffName}"
                                               class="autocomplete"/>
                                    </td>
                                </oframe:power>
                            </tr>
                            <tr>
                                <td colspan="4" align="center">
                                    <button onclick="sys010.check_input_box();" type="button" id="schBtn"
                                            class="js_faTask btn btn-primary">
                                        查询
                                    </button>
                                    <button onclick="sys010.closeQSch()" type="button" class="btn btn-info">关闭</button>
                                </td>
                            </tr>
                        </table>
                        <!-- 隐藏域传递staff_code到后台，默认显示当前用户相关的功能收藏 -->
                        <input type="hidden" id="staffId_hidden" name="staffId" value=${staffId}/>
                        <!-- 隐藏域传递prjCd到后台，默认显示当前项目的功能收藏 -->
                        <input type="hidden" id="prjCd" name="prjCd" value="${param.prjCd}"/>
                    </div>
                </form>
                <div class="mart5">
                    <div id="sys010_w_list_print">
                        <table class="table" width="98%" layoutH="140">
                            <thead>
                            <tr>
                                <%-- <th width="22"><input type="checkbox" group="ids" class="checkboxCtrl"></th>--%>
                                <th width="40">序号</th>
                                <%--<th width="200" onclick="Query.sort('sys010_w_list_print', this);"
                                    sortColumn="q_text"
                                    class="sortable">--%>
                                <th width="80" onclick="Query.sort('sys010_w_list_print', this);"
                                    sortColumn="staff_code"
                                    class="sortable">收藏工号
                                </th>
                                <th width="80" onclick="Query.sort('sys010_w_list_print', this);"
                                    sortColumn="rht_name"
                                    class="sortable">功能名称
                                </th>
                                <th width="100" onclick="Query.sort('sys010_w_list_print', this);"
                                    sortColumn="create_date"
                                    class="sortable">收藏时间
                                </th>
                                <th width="40">操作</th>
                            </tr>
                            </thead>
                            <tbody></tbody>
                        </table>
                    </div>
                    <jsp:include page="/oframe/common/page/pager_fotter.jsp"/>
                </div>
            </div>
        </div>
    </div>
</div>
<oframe:script src="${pageContext.request.contextPath}/oframe/sysmg/sys010/js/sys010.js" type="text/javascript"/>