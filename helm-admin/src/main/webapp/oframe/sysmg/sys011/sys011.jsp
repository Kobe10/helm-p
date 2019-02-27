<%--日志信息查询--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<div class="mar5">
    <div style="position: relative;" class="js_sys011">
        <div class="panelBar">
            <ul class="toolBar">
                <li onclick="sys011.openQSch(this);">
                    <a class="find" href="javascript:void(0)"><span>检索条件</span></a>
                </li>
                <li onclick="Query.queryList('sys011frm', 'sys011_list_print');">
                    <a class="reflesh" href="javascript:void(0)"><span>刷新检索</span></a>
                </li>
            </ul>
        </div>
        <div>
            <div id="sys011create" class="hidden"
                 style="position: absolute;width:100%;top: 0px;z-index: 100;background-color: #ffffff; margin-top: 40px;">
                <form id="sys011frm" method="post" class="required-validate">
                    <%--自服务名称用于定位SQL--%>
                    <input type="hidden" name="subSvcName" value="sys011"/>
                    <%-- 分页展示 --%>
                    <input type="hidden" name="forward" id="forward" value="/oframe/sysmg/sys011/sys011_list"/>

                    <table class="border">
                        <tr>
                            <oframe:power prjCd="${param.prjCd}" rhtCd="wrt_log_id">
                                <th>操作人：</th>
                                <td>
                                    <input name="staff_code" type="hidden" value="${staffCode}"/>
                                    <input type="text" value='<oframe:staff staffCode="${staffCode}"/>'
                                           class="pull-left autocomplete"
                                           atOption="CODE.getStaffOpt"
                                    />
                                    <a title="选择"
                                       onclick="$.fn.sltStaff(this,{offsetX: 0,offsetY: 0, fromOp:'sys011', prjCd:getPrjCd()});"
                                       class="btnLook">选择</a>
                                </td>
                            </oframe:power>
                            <th>操作时间(≥)：</th>
                            <td><input id="log_time1" type="text" name="log_time1" class="required date"
                                       dateFmt="yyyy-MM-dd HH:mm:ss"
                                       value='<fmt:formatDate value="${defultTime1}" type="both" pattern="yyyy-MM-dd HH:mm:ss"/>'/>
                                <input type="hidden" name="name_table" id="tableName" value="">
                            </td>
                            <th>操作时间(≤)：</th>
                            <td>
                                <input id="log_time2" type="text" name="log_time2" class="required date"
                                       dateFmt="yyyy-MM-dd HH:mm:ss"
                                       value='<fmt:formatDate value="${defultTime2}" type="both" pattern="yyyy-MM-dd HH:mm:ss"/>'/>
                            </td>
                        </tr>
                        <tr>
                            <th>操作名称：</th>
                            <td>
                                <input type="hidden" name="rht_id" class="pull-left"/>
                                <input type="text" name="rht_name" class="pull-left" readonly/>
                                <a title="选择" onclick="sys011.initRhtTree(this);" class="btnLook">选择</a>
                            </td>
                            <th>操作IP地址：</th>
                            <td><input type="text" name="from_ip"/></td>
                            <th>执行结果：</th>
                            <td><input type="text" name="op_is_success"/></td>
                        </tr>
                        <tr>
                            <td colspan="6" align="center">
                                <button onclick="sys011.checkTime();"
                                        type="button" id="schBtn"
                                        class="js_faTask btn btn-primary">
                                    检索
                                </button>
                                <button onclick="sys011.closeQSch(false)" type="button" class="btn btn-info">关闭</button>
                            </td>
                        </tr>
                    </table>
                </form>
            </div>


            <div id="sys011_list_print">
                <table class="table" width="100%" layoutH="125">
                    <thead>
                    <tr>
                        <th width="5%">序号</th>
                        <th width="10%"
                        <%--onclick="Query.sort('sys011_list_print', this);"--%>
                        <%--sortColumn="op_staff_id"--%>
                        <%--defaultSort="asc"--%>
                        <%--class="sortable"--%>
                        >操作人
                        </th>
                        <th width="10%">操作IP地址</th>
                        <th width="10%"
                        <%--onclick="Query.sort('sys011_list_print', this);"--%>
                        <%--sortColumn="rht_name"--%>
                        <%--defaultSort="asc"--%>
                        <%--class="sortable"--%>
                        >操作名称
                        </th>
                        <th width="20%" onclick="Query.sort('sys011_list_print', this);"
                            sortColumn="op_start_time"
                            defaultSort="desc"
                            class="sortable">
                            操作时间
                        </th>
                        <th width="15%" onclick="Query.sort('sys011_list_print', this);"
                            sortColumn="op_use_time"
                            defaultSort="asc"
                            class="sortable">
                            响应耗时(ms)
                        </th>
                        <th width="5%">是否成功</th>
                        <th>操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>
            </div>
            <c:import url="/oframe/common/page/pager_fotter.jsp" charEncoding="UTF-8"/>
        </div>
    </div>
</div>
<oframe:script src="${pageContext.request.contextPath}/oframe/sysmg/sys011/js/sys011.js" type="text/javascript"/>