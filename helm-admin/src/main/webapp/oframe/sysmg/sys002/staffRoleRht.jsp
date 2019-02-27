<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<div style="min-height:400px !important; overflow:visible !important;">
    <input type="hidden" name="staffId" value="${staffId}">

    <div class="tabs">
        <div class="tabsHeader" style="border-bottom: 1px solid #3d91c8;">
            <div class="tabsHeaderContent">
                <ul id="sys002NavUl">
                    <%--<li onclick="sys002.loadRoleStaff(this);">--%>
                    <%--<a href="javascript:void(0);"><span>拥有角色</span></a>--%>
                    <%--</li>--%>
                    <li onclick="sys002.loadSFunc(this, 's_func');">
                        <a href="javascript:void(0);"><span>系统功能</span></a>
                    </li>
                    <li onclick="sys002.loadSData(this, 's_data');">
                        <a href="javascript:void(0);"><span>系统数据</span></a>
                    </li>
                    <li onclick="sys002.loadPrjFunc(this, 'p_func');">
                        <a href="javascript:void(0);"><span>项目功能</span></a>
                    </li>
                    <%--根据配置参数动态生成需要进行的授权类型--%>
                    <c:forEach items="${DATA_RHT_DEF}" var="item">
                        <c:choose>
                            <c:when test="${fn:startsWith(item.key, 's_')}">
                                <li onclick="sys002.loadDataRht(this, '${item.key}');">
                                    <a href="javascript:void(0);"><span>${item.value}</span></a>
                                </li>
                            </c:when>
                            <c:when test="${fn:startsWith(item.key, 'p_')}">
                                <li onclick="sys002.loadDataRht(this, '${item.key}');">
                                    <a href="javascript:void(0);"><span>${item.value}</span></a>
                                </li>
                            </c:when>
                        </c:choose>
                    </c:forEach>
                </ul>
            </div>
        </div>
        <div class="tabsContent" id="sys002NavContext">
            <%--<div layoutH="50">--%>
            <%--<div>--%>
            <%--<table class="border">--%>
            <%--<tr>--%>
            <%--<td>--%>
            <%--<div layoutH="60" class="mar5">--%>
            <%--<ul>--%>
            <%--<c:forEach items="1,1,3" var="item">--%>
            <%--<li>系统管理员</li>--%>
            <%--</c:forEach>--%>
            <%--</ul>--%>
            <%--</div>--%>
            <%--</td>--%>
            <%--</tr>--%>
            <%--</table>--%>
            <%--</div>--%>
            <%--</div>--%>
            <div layoutH="50">
                <div class="panelBar">
                    <ul class="toolBar">
                        <li><a class="save"
                               onclick="sys002.saveStaffRht('sys002RoleRhtTree_1', 's_func')"><span>保存</span></a>
                        </li>
                    </ul>
                </div>
                <ul id="sys002RoleRhtTree_1" layoutH="100" class="ztree"></ul>
            </div>
            <div layoutH="50">
                <div class="panelBar">
                    <ul class="toolBar">
                        <li><a class="save"
                               onclick="sys002.saveStaffRht('sys002RoleRhtTree_2', 's_data')"><span>保存</span></a>
                        </li>
                    </ul>
                </div>
                <ul id="sys002RoleRhtTree_2" layoutH="100" class="ztree"></ul>
            </div>
            <div layoutH="50">
                <div class="panelBar">
                    <ul class="toolBar">
                        <li><a class="save"
                               onclick="sys002.saveStaffRht('sys002RoleRhtTree_PFunc', 'p_func')"><span>保存</span></a>
                        </li>
                    </ul>
                </div>
                <ul id="sys002RoleRhtTree_PFunc" layoutH="100" class="ztree"></ul>
            </div>
          <%--  <div layoutH="50">
                <div class="panelBar">
                    <ul class="toolBar">
                        <li><a class="save"
                               onclick="sys002.saveStaffRht('sys002RoleRhtTree_p_h_data', 'p_h_data')"><span>保存</span></a>
                        </li>
                    </ul>
                </div>
                <ul id="sys002RoleRhtTree_p_h_data" layoutH="100" class="ztree"></ul>
            </div>
            <div layoutH="50">
                <div class="panelBar">
                    <ul class="toolBar">
                        <li><a class="save"
                               onclick="sys002.saveStaffRht('sys002RoleRhtTree_p_n_data', 'p_n_data')"><span>保存</span></a>
                        </li>
                    </ul>
                </div>
                <ul id="sys002RoleRhtTree_p_n_data" layoutH="100" class="ztree"></ul>
            </div>--%>
                <%--循环依托于系统组织树的权限--%>
                <c:forEach items="${DATA_RHT_DEF}" var="item">
                    <c:choose>
                        <c:when test="${fn:startsWith(item.key, 's_')}">
                            <div layoutH="50">
                                <div class="panelBar">
                                    <ul class="toolBar">
                                        <li><a class="save"
                                               onclick="sys002.saveStaffRht('sys002RoleRhtTree_${item.key}', '${item.key}')"><span>保存</span></a>
                                        </li>
                                    </ul>
                                </div>
                                <ul id="sys002RoleRhtTree_${item.key}" layoutH="100" class="ztree"></ul>
                            </div>
                        </c:when>
                        <c:when test="${fn:startsWith(item.key, 'p_')}">
                            <div layoutH="50">
                                <div class="panelBar">
                                    <ul class="toolBar">
                                        <li><a class="save"
                                               onclick="sys002.saveStaffRht('sys002RoleRhtTree_${item.key}', '${item.key}')"><span>保存</span></a>
                                        </li>
                                    </ul>
                                </div>
                                <ul id="sys002RoleRhtTree_${item.key}" layoutH="100" class="ztree"></ul>
                            </div>
                        </c:when>
                    </c:choose>
                </c:forEach>
        </div>
    </div>
</div>
<script type="text/javascript">
    $(document).ready(function () {
        $("#sys002NavUl", navTab.getCurrentPanel()).find("li:eq(0)").trigger("click");
    });
</script>