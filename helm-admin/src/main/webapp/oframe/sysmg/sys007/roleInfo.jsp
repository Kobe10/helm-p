<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<div style="min-height:400px !important; overflow:visible !important;">
    <input type="hidden" name="rolePrjCd" value="${nodeInfo.Node.prjCd}"/>
    <input type="hidden" name="roleCd" value="${nodeInfo.Node.roleCd}"/>
    <input type="hidden" name="upNodeId" value="${nodeInfo.Node.upNodeId}">
    <input type="hidden" name="orgId" value="${nodeInfo.Node.orgId}">
    <input type="hidden" name="roleName" value="${nodeInfo.Node.roleName}">

    <div class="tabs">
        <div class="tabsHeader" style="border-bottom: 1px solid #3d91c8;">
            <div class="tabsHeaderContent">
                <ul id="sys007NavUl">
                    <li onclick="sys007.loadRoleStaff(this);">
                        <a href="javascript:void(0);"><span>角色成员</span></a>
                    </li>
                    <li onclick="sys007.loadSFunc(this, 's_func');">
                        <a href="javascript:void(0);"><span>系统功能</span></a>
                    </li>
                    <li onclick="sys007.loadSData(this, 's_data');">
                        <a href="javascript:void(0);"><span>系统数据</span></a>
                    </li>
                    <li onclick="sys007.loadPrjFunc(this, 'p_func');">
                        <a href="javascript:void(0);"><span>项目功能</span></a>
                    </li>
                    <%--根据配置参数动态生成需要进行的授权类型--%>
                    <c:forEach items="${DATA_RHT_DEF}" var="item">
                        <c:choose>
                            <c:when test="${fn:startsWith(item.key, 's_')}">
                                <li onclick="sys007.loadSData(this, '${item.key}');">
                                    <a href="javascript:void(0);"><span>${item.value}</span></a>
                                </li>
                            </c:when>
                            <c:when test="${fn:startsWith(item.key, 'p_')}">
                                <li onclick="sys007.loadDataRht(this, '${item.key}');">
                                    <a href="javascript:void(0);"><span>${item.value}</span></a>
                                </li>
                            </c:when>
                        </c:choose>
                    </c:forEach>
                </ul>
            </div>
        </div>
        <div class="tabsContent" id="sys007NavContext">
            <div layoutH="50">
                <div class="panelBar">
                    <ul class="toolBar">
                        <li><a class="save" onclick="sys007.saveRoleStaff()"><span>保存</span></a></li>
                    </ul>
                </div>
                <div>
                    <table class="border">
                        <tr>
                            <th width="40%" class="subTitle">
                                <label style="float: left;font-weight: bolder;">可选账号:</label>
                            </th>
                            <th width="60%" class="subTitle">
                                <label style="float: left;font-weight: bolder;">已选账号:</label>
                            </th>
                        </tr>
                        <tr>
                            <td>
                                <div>
                                    <ul id="sys007RoleStaffSlt" layoutH="145" class="ztree"></ul>
                                </div>
                            </td>
                            <td style="margin: 0px;padding: 0px;vertical-align: top;">
                                <div layoutH="140">
                                    <ul id="sys007RoleStaffList" class="list">
                                        <li staffId="" class="hidden">
                                            <label style="display: inline-block;"></label>
                                            <span onclick="sys007.dltRoleStaff(this);" style="float: right;"
                                                  class="link marr20">删除</span>
                                        </li>
                                        <c:forEach items="${staffList}" var="item">
                                            <li staffId="${item.Staff.staffId}">
                                                <label style="display: inline-block;">${item.Staff.staffCode}(${item.Staff.staffName})</label>
                                                <span onclick="sys007.dltRoleStaff(this);" style="float: right;"
                                                      class="link marr20">删除</span>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </div>
                            </td>
                        </tr>
                    </table>
                </div>

            </div>
            <div layoutH="50">
                <div class="panelBar">
                    <ul class="toolBar">
                        <li><a class="save"
                               onclick="sys007.saveRoleRht('sys007RoleRhtTree_1', 's_func')"><span>保存</span></a>
                        </li>
                    </ul>
                </div>
                <ul id="sys007RoleRhtTree_1" layoutH="100" class="ztree"></ul>
            </div>
            <div layoutH="50">
                <div class="panelBar">
                    <ul class="toolBar">
                        <li><a class="save"
                               onclick="sys007.saveRoleRht('sys007RoleRhtTree_2', 's_data')"><span>保存</span></a>
                        </li>
                    </ul>
                </div>
                <ul id="sys007RoleRhtTree_2" layoutH="100" class="ztree"></ul>
            </div>
            <div layoutH="50">
                <div class="panelBar">
                    <ul class="toolBar">
                        <li><a class="save"
                               onclick="sys007.saveRoleRht('sys007RoleRhtTree_PFunc', 'p_func')"><span>保存</span></a>
                        </li>
                    </ul>
                </div>
                <ul id="sys007RoleRhtTree_PFunc" layoutH="100" class="ztree"></ul>
            </div>


            <%--循环依托于系统组织树的权限--%>
            <c:forEach items="${DATA_RHT_DEF}" var="item">
                <c:choose>
                    <c:when test="${fn:startsWith(item.key, 's_')}">
                        <div layoutH="50">
                            <div class="panelBar">
                                <ul class="toolBar">
                                    <li><a class="save"
                                           onclick="sys007.saveRoleRht('sys007RoleRhtTree_${item.key}', '${item.key}')"><span>保存</span></a>
                                    </li>
                                </ul>
                            </div>
                            <ul id="sys007RoleRhtTree_${item.key}" layoutH="100" class="ztree"></ul>
                        </div>
                    </c:when>
                    <c:when test="${fn:startsWith(item.key, 'p_')}">
                        <div layoutH="50">
                            <div class="panelBar">
                                <ul class="toolBar">
                                    <li><a class="save"
                                           onclick="sys007.saveRoleRht('sys007RoleRhtTree_${item.key}', '${item.key}')"><span>保存</span></a>
                                    </li>
                                </ul>
                            </div>
                            <ul id="sys007RoleRhtTree_${item.key}" layoutH="100" class="ztree"></ul>
                        </div>
                    </c:when>
                </c:choose>
            </c:forEach>


        </div>
    </div>
</div>
<script type="text/javascript">
    $(document).ready(function () {
        $("#sys007NavUl", navTab.getCurrentPanel()).find("li:eq(0)").trigger("click");
    });
</script>