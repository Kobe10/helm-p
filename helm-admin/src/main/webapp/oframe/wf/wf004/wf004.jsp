<%--待办任务列表--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>

<input type="hidden" name="StaffCd" value="${StaffCd}">

<div class="mar5">
    <div class="pageNav">
        <a class="current">我的流程</a>
    </div>
    <div class="tabs mart5 ">
        <div class="tabsHeader">
            <div class="tabsHeaderContent">
                <ul>
                    <li class="selected" onclick="wf004.loadTabContext(1);">
                        <a href="javascript:void(0);"><span>发起流程</span></a>
                    </li>
                    <li onclick="wf004.loadTabContext(2);">
                        <a href="javascript:void(0);"><span>办结流程</span></a>
                    </li>
                </ul>
            </div>
        </div>
        <div class="tabsContent" style="padding: 0;border-top: 1px solid #3d91c8;">
            <div class="js_star_task" style="position: relative;">
                <div class="panelBar">
                    <ul class="toolBar">
                        <li onclick="wf004.openQSch(1);">
                            <a class="find" href="javascript:void(0)"><span>检索</span></a>
                        </li>
                        <li onclick="wf004.loadTabContext(1);">
                            <a class="reflesh" href="javascript:void(0);"><span>刷新</span></a>
                        </li>
                    </ul>
                </div>
                <form id="wf004form01" method="post">
                    <%--自服务名称用于定位SQL--%>
                    <input type="hidden" name="selfDefCmpt" value="QUERY_PROC_INSTANC_EPAGE"/>
                    <%-- 分页展示 --%>
                    <input type="hidden" name="forward" id="forward" value="/oframe/wf/wf004/wf004_list"/>

                    <div id="wf004create" class="hidden"
                         style="position: absolute;width:100%;top: 0px;z-index: 100;background-color: #ffffff; margin-top: 40px;">
                        <div class="triangle triangle-up"
                             style="border-bottom-color: #2c7bae;top: -10px; left: 25px;"></div>
                        <table class="border">
                            <tr>
                                <th><label>发起人：</label></th>
                                <td style="position: relative;">
                                    <c:set var="haveRht" value="false"/>
                                    <oframe:power prjCd="${param.prjCd}" rhtCd="query_proc_name">
                                        <c:set var="haveRht" value="true"/>
                                    </oframe:power>
                                    <c:choose>
                                        <c:when test="${haveRht}">
                                            <input name="createStaffName" type="text" class="pull-left autocomplete"
                                                   value="${StaffName}"
                                                   atOption="CODE.getStaffOpt"/>
                                            <a title="选择"
                                               onclick="$.fn.sltStaff(this,{offsetX: 3, fromOp:'wf004', prjCd:getPrjCd()});"
                                               class="btnLook">选择</a>
                                        </c:when>
                                        <c:otherwise>
                                            <input type="text" readonly disabled class="readonly" value="${StaffName}"/>
                                        </c:otherwise>
                                    </c:choose>
                                    <input name="procStUser" value="${StaffCd}" type="hidden">
                                </td>
                                <th><label>流程名称：</label></th>
                                <td>
                                    <input name="procDefName" type="text">
                                </td>
                                <th><label>发起时间（从）：</label></th>
                                <td>
                                    <input name="stTime" type="text"
                                           class="date" datefmt="yyyy-MM-dd HH:mm:ss">
                                </td>
                                <th><label>发起时间（至）：</label></th>
                                <td>
                                    <input name="endTime" type="text"
                                           class="date" datefmt="yyyy-MM-dd HH:mm:ss">
                                </td>
                            </tr>
                            <tr>
                                <td colspan="8" align="center">
                                    <button onclick="Query.queryList('wf004form01', 'wf004_page_data');wf004.closeQSch(1);"
                                            type="button"
                                            id="schBtn" class="js_faTask btn btn-primary">
                                        查询
                                    </button>
                                    <button onclick="wf004.closeQSch(1)" type="button" class="btn btn-info">关闭</button>
                                </td>
                            </tr>
                        </table>
                    </div>
                </form>
                <div>
                    <div class="wf004_page_data" id="wf004_page_data">
                        <table class="table" width="100%" layoutH="165">
                            <thead>
                            <tr>
                                <th width="5%">序号</th>
                                <th>请求名称</th>
                                <th>流程名称</th>
                                <th onclick="Query.sort('wf004_page_data', this);"
                                    defaultSort="desc" sortColumn="procStTime" class="sortable">发起时间
                                </th>
                                <th>持续时间</th>
                                <th>当前状态</th>
                                <th width="10%"><label>操作</label></th>
                            </tr>
                            </thead>
                            <tbody>
                            </tbody>
                        </table>
                    </div>
                    <c:import url="/oframe/common/page/pager_fotter.jsp" charEncoding="UTF-8"></c:import>
                </div>
            </div>
            <div class="js_end_task" style="position: relative;">
                <div class="panelBar">
                    <ul class="toolBar">
                        <li onclick="wf004.openQSch(2);">
                            <a class="find" href="javascript:void(0)"><span>检索</span></a>
                        </li>
                        <li onclick="wf004.loadTabContext(2);">
                            <a class="reflesh" href="javascript:void(0);"><span>刷新</span></a>
                        </li>
                    </ul>
                </div>
                <form id="wf004form02" method="post">
                    <%--自服务名称用于定位SQL--%>
                    <input type="hidden" name="selfDefCmpt" value="QUERY_PROC_INSTANC_EPAGE"/>
                    <%-- 分页展示 --%>
                    <input type="hidden" name="forward" id="forward" value="/oframe/wf/wf004/wf004_list"/>
                    <input type="hidden" name="isComplete" id="isComplete" value="1"/>

                    <div id="wf004End" class="hidden"
                         style="position: absolute;width:100%;top: 0px;z-index: 100;background-color: #ffffff; margin-top: 40px;">
                        <div class="triangle triangle-up"
                             style="border-bottom-color: #2c7bae;top: -10px; left: 25px;"></div>
                        <table class="border">
                            <tr>
                                <th><label>发起人：</label></th>
                                <td style="position: relative;">
                                    <c:set var="haveRht" value="false"/>
                                    <oframe:power prjCd="${param.prjCd}" rhtCd="query_proc_name">
                                        <c:set var="haveRht" value="true"/>
                                    </oframe:power>
                                    <c:choose>
                                        <c:when test="${haveRht}">
                                            <input name="createStaffName" type="text" class="pull-left autocomplete"
                                                   value="${StaffName}"
                                                   atOption="CODE.getStaffOpt"/>
                                            <a title="选择"
                                               onclick="$.fn.sltStaff(this,{offsetX: 3, fromOp:'wf004', prjCd:getPrjCd()});"
                                               class="btnLook">选择</a>
                                        </c:when>
                                        <c:otherwise>
                                            <input type="text" readonly disabled class="readonly" value="${StaffName}"/>
                                        </c:otherwise>
                                    </c:choose>
                                    <input name="procStUser" value="${StaffCd}" type="hidden">
                                </td>
                                <th><label>流程名称：</label></th>
                                <td>
                                    <input name="procDefName" type="text">
                                </td>
                                <th><label>发起时间（从）：</label></th>
                                <td>
                                    <input name="stTime" type="text"
                                           class="date" datefmt="yyyy-MM-dd HH:mm:ss">
                                </td>
                                <th><label>发起时间（至）：</label></th>
                                <td>
                                    <input name="endTime" type="text"
                                           class="date" datefmt="yyyy-MM-dd HH:mm:ss">
                                </td>
                            </tr>
                            <tr>
                                <td colspan="8" align="center">
                                    <button onclick="Query.queryList('wf004form02', 'wf00402_page_data');wf004.closeQSch(2)"
                                            type="button"
                                            class="js_faTask btn btn-primary">
                                        查询
                                    </button>
                                    <button onclick="wf004.closeQSch(2)" type="button" class="btn btn-info">关闭</button>
                                </td>
                            </tr>
                        </table>
                    </div>
                </form>
                <div>
                    <div class="wf00402_page_data" id="wf00402_page_data">
                        <table class="table" width="100%" layoutH="165">
                            <thead>
                            <tr>
                                <th width="5%">序号</th>
                                <th>请求名称</th>
                                <th>流程名称</th>
                                <th onclick="Query.sort('wf00402_page_data', this);"
                                    sortColumn="procStTime" class="sortable">发起时间
                                </th>
                                <th onclick="Query.sort('wf00402_page_data', this);"
                                    defaultSort="desc" sortColumn="procEndime" class="sortable">完成时间
                                </th>
                            </tr>
                            </thead>
                            <tbody>
                            </tbody>
                        </table>
                    </div>
                    <c:import url="/oframe/common/page/pager_fotter.jsp" charEncoding="UTF-8"></c:import>
                </div>
            </div>
        </div>
    </div>
</div>
<oframe:script type="text/javascript" src="${pageContext.request.contextPath}/oframe/wf/wf004/js/wf004.js"/>
