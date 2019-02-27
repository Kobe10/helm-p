<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div>
    <%--左侧导航树--%>
    <div style="width: 280px;float: left;position: relative;" id="pj002DivTree" layoutH="53"
         class="panel left_menu">
        <h1>
            <span class="panel_title">项目组织</span>
        </h1>
        <ul id="pj002OrgTreeForStaff" layoutH="200" class="ztree"></ul>
    </div>
    <%--右侧自定义画板--%>
    <div layoutH="50" id="pj002StaffRightDiv" style="margin-left: 290px;margin-right: 5px;position: relative;">
        <div class="panelBar">
            <ul class="toolBar">
                <li onclick="prjStaff.openQSch(this)">
                    <a title="点击进入高级检索" class="find" href="javascript:void(0)"><span>条件检索</span></a>
                </li>
                <a class="reflesh" onclick="prjStaff.queryStaff();"><span>数据刷新</span></a></li>
                <%--<li><a class="add" onclick="prjStaff.openAddStaff();"><span>新增成员</span></a></li>--%>
                <%--<li><a class="delete" onclick="prjStaff.deleteStaff();"><span>删除成员</span></a></li>--%>
                <%--<li><a class="import" onclick="prjStaff.deleteStaff();"><span>导入成员</span></a></li>--%>
                <%--<li><a class="export" onclick="prjStaff.deleteStaff();"><span>导出成员</span></a></li>--%>
            </ul>
        </div>
        <div id="pj002StaffQSchDialog" class="hidden"
             style="position: absolute;width:100%;top: 0px;z-index: 100;
                      background-color: #ffffff; margin-top: 35px;">
            <div class="triangle triangle-up"
                 style="border-bottom-color: #2c7bae;top: -10px; left: 25px;"></div>
            <form id="pj002PrjStaffFrm" method="post">
                <%--查询的组织编号--%>
                <input type="hidden" value="${nodeInfo.Node.orgId}" name="orgId"/>
                <%--自服务名称用于定位SQL--%>
                <input type="hidden" name="subSvcName" value="sys00201"/>
                <%--自服务名称用于定位SQL--%>
                <input type="hidden" name="prjCd" value="${param.prjCd}"/>
                <%-- 分页展示 --%>
                <input type="hidden" name="forward" id="forward" value="/eland/pj/pj002/pj002_staff_list"/>
                <%--自定义服务--%>
                <input type="hidden" name="selfDefSvc" value="staffService-queryPageWithRht"/>
                <table class="border">
                    <tr>
                        <th><label>员工账号：</label></th>
                        <td><input name="staffCode" class="textInput"></td>
                        <th><label>员工名称：</label></th>
                        <td><input name="staffName" class="textInput"></td>
                        <th><label>账号状态：</label></th>
                        <td><input name="statusCd" class="hidden">
                            <input type="text" itemCd="COM_STATUS_CD"
                                   atOption="CODE.getCfgDataOpt" class="autocomplete textInput">
                        </td>
                    </tr>
                    <tr>
                        <td colspan="6" align="center">
                            <button onclick="prjStaff.queryStaff();prjStaff.closeQSch();" type="button"
                                    class="btn btn-primary js_query marr20">查询
                            </button>
                            <span onclick="prjStaff.closeQSch();" class="btn btn-info">关闭</span>
                        </td>
                    </tr>
                </table>
            </form>
        </div>


        <div id="pj002_staff_list_print">
            <table class="table" layoutH="165" width="100%">
                <thead>
                <tr>
                    <th><input type="checkbox" class="checkboxCtrl" group="ids"/></th>
                    <th onclick="Query.sort('pj002_staff_list_print', this);"
                        defaultSort="asc" sortColumn="staff_code" class="sortable">员工账号
                    </th>
                    <th onclick="Query.sort('pj002_staff_list_print', this);"
                        sortColumn="staff_name" class="sortable">员工姓名
                    </th>
                    <th onclick="Query.sort('pj002_staff_list_print', this);"
                        sortColumn="staff_tel" class="sortable">移动电话
                    </th>
                    <th onclick="Query.sort('pj002_staff_list_print', this);"
                        sortColumn="staff_email" class="sortable">邮箱地址
                    </th>
                    <th width="30%" onclick="Query.sort('pj002_staff_list_print', this);">项目组织
                    </th>
                </tr>
                </thead>
                <tbody>
                </tbody>
            </table>
        </div>
        <jsp:include page="/oframe/common/page/pager_fotter.jsp"/>
    </div>
</div>
<oframe:script src="${pageContext.request.contextPath}/eland/pj/pj002/js/prjStaff.js" type="text/javascript"/>
