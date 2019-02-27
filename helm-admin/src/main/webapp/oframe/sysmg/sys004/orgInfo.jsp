<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div>
    <div class="tabs">
        <div class="tabsHeader" style="border-bottom: 1px solid #3d91c8;">
            <div class="tabsHeaderContent">
                <ul>
                    <li><a href="javascript:void(0);"><span>员工信息</span></a></li>
                    <li onclick="sys004.orgRoleInfo(this);"><a href="javascript:void(0);"><span>角色信息</span></a></li>
                </ul>
            </div>
        </div>
        <div class="tabsContent" id="sys004NavContext">
            <div layoutH="50">
                <div class="panelBar">
                    <ul class="toolBar">
                        <li onclick="sys004.openQSch(this)">
                            <a title="点击进入高级检索" class="find" href="javascript:void(0)"><span>条件检索</span></a>
                        </li>
                        <a class="reflesh" onclick="sys004.queryStaff();"><span>数据刷新</span></a></li>
                        <oframe:op prjCd="${param.prjCd}" template="li" cssClass="add" name="新增员工"
                                   rhtCd="EDIT_CMP_ORG_RHT" onClick="sys004.openAddStaff();"/>
                        <oframe:op prjCd="${param.prjCd}" template="li" cssClass="import" name="导入员工"
                                   rhtCd="EDIT_CMP_ORG_RHT" onClick="sys004.importMb();"/>
                        <li><a class="export"
                               onclick="Query.exportExcelByTemplate('sys004_staff_list_print','webroot:oframe/sysmg/sys004/员工信息导出.xls')"><span>导出员工</span></a>
                        </li>
                    </ul>
                </div>
                <div id="sys004QSchDialog" class="hidden"
                     style="position: absolute;width:100%;top: 0px;z-index: 100;
                      background-color: #ffffff; margin-top: 75px;">
                    <div class="triangle triangle-up"
                         style="border-bottom-color: #2c7bae;top: -10px; left: 25px;"></div>
                    <form id="sys004OrgFrm" method="post">
                        <%--查询的组织编号--%>
                        <input type="hidden" value="${nodeInfo.Node.orgId}" name="orgId"/>
                        <%--自服务名称用于定位SQL--%>
                        <input type="hidden" name="subSvcName" value="sys002"/>
                        <%--自服务名称用于定位SQL--%>
                        <input type="hidden" name="prjCd" value="0"/>
                        <%-- 分页展示 --%>
                        <input type="hidden" name="forward" id="forward" value="/oframe/sysmg/sys004/sys004_list"/>
                        <%--自定义服务--%>
                        <input type="hidden" name="selfDefSvc" value="staffService-queryPageWithRht"/>
                        <table class="border">
                            <tr>
                                <th><label>员工账号：</label></th>
                                <td><input name="staffCode" class="textInput"></td>
                                <th><label>员工名称：</label></th>
                                <td><input name="staffName" class="textInput"></td>
                                <th><label>账号状态：</label></th>
                                <td><input name="statusCd" class="hidden" type="hidden">
                                    <input type="text" itemCd="COM_STATUS_CD"
                                           atOption="CODE.getCfgDataOpt" class="autocomplete textInput">
                                </td>
                            </tr>
                            <tr>
                                <td colspan="6" align="center">
                                    <button onclick="sys004.queryStaff();sys004.closeQSch();" type="button"
                                            class="btn btn-primary js_query marr20">查询
                                    </button>
                                    <span onclick="sys004.closeQSch();" class="btn btn-info">关闭</span>
                                </td>
                            </tr>
                        </table>
                    </form>
                </div>


                <div id="sys004_staff_list_print">
                    <table class="table" layoutH="165" width="100%">
                        <thead>
                        <tr>
                            <th><input type="checkbox" class="checkboxCtrl" group="ids"/></th>
                            <th onclick="Query.sort('sys004_staff_list_print', this);"
                                defaultSort="asc" sortColumn="staff_code" class="sortable">员工账号
                            </th>
                            <th onclick="Query.sort('sys004_staff_list_print', this);"
                                sortColumn="staff_name" class="sortable">员工姓名
                            </th>
                            <th onclick="Query.sort('sys004_staff_list_print', this);"
                                sortColumn="staff_tel" class="sortable">移动电话
                            </th>
                            <th onclick="Query.sort('sys004_staff_list_print', this);"
                                sortColumn="staff_email" class="sortable">邮箱地址
                            </th>
                            <th width="30%" onclick="Query.sort('sys004_staff_list_print', this);"
                                sortColumn="org_id" class="sortable">公司组织
                            </th>
                            <th>操作</th>
                        </tr>
                        </thead>
                        <tbody>
                        </tbody>
                    </table>
                </div>
                <jsp:include page="/oframe/common/page/pager_fotter.jsp"/>
            </div>
            <div layoutH="50"></div>
        </div>
    </div>
</div>
<script type="text/javascript">
    /**
     * 界面初始化加载
     */
    $(document).ready(function () {
        sys004.queryStaff();
    });
</script>
