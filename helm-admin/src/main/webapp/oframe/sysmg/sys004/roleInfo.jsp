<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="panelBar">
    <ul class="toolBar">
        <li><a class="save" onclick="orgRole.saveRole()"><span>保存</span></a></li>
        <c:choose>
            <c:when test="${roleNode.Node.roleCd == null}">
                <li id="sys004RmRoleLi" style="display: none !important;"><a class="delete"
                                                                             onclick="orgRole.removeRole()"><span>删除</span></a>
                </li>
            </c:when>
            <c:otherwise>
                <li><a class="delete" onclick="orgRole.removeRole()"><span>删除</span></a></li>
            </c:otherwise>
        </c:choose>
    </ul>
</div>
<form id="sys004RoleInfoFrm">
    <input type="hidden" name="upNodeId" value="${roleNode.Node.upNodeId}">
    <table class="border">
        <tr>
            <th width="15%"><label>角色名称：</label></th>
            <td><input type="text" name="roleName" class="required" value="${roleNode.Node.roleName}"></td>
            <th width="15%"><label>角色编码：</label></th>
            <td>
                <input type="text" name="roleCd" readonly class="readonly" value="${roleNode.Node.roleCd}">
            </td>
        </tr>
        <tr>
            <th colspan="2" class="subTitle">
                <label style="float: left;font-weight: bolder;">可选账号:</label>
            </th>
            <th colspan="2" class="subTitle">
                <label style="float: left;font-weight: bolder;">已选账号:</label>
            </th>
        </tr>
        <tr>
            <td colspan="2">
                <div layoutH="175">
                    <ul id="sltStaffTree" class="ztree"></ul>
                </div>
            </td>
            <td colspan="2" style="margin: 0px;padding: 0px;">
                <div layoutH="168">
                    <ul id="sys004RoleStaff" class="list">
                        <li staffId="" class="hidden">
                            <label style="display: inline-block;"></label>
                            <span onclick="orgRole.dltRoleStaff(this);" style="float: right;"
                                  class="link marr20">删除</span>
                        </li>
                        <c:forEach items="${staffList}" var="item">
                            <li staffId="${item.Staff.staffId}">
                                <label style="display: inline-block;">${item.Staff.staffCode}(${item.Staff.staffName})</label>
                                <span onclick="orgRole.dltRoleStaff(this);" style="float: right;"
                                      class="link marr20">删除</span>
                            </li>
                        </c:forEach>
                    </ul>
                </div>
            </td>
        </tr>
    </table>
</form>
</div>
<script type="text/javascript">
    $(document).ready(function () {
        // 执行目录树初始化
        orgRole.initFullTree();
    });
</script>
