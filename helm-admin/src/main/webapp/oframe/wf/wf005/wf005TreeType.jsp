<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div id="regionBar" class="panelBar">
    <ul class="toolBar">
        <li><a class="save" onclick="wf005.saveFormType()"><span>保存</span></a></li>
        <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
    </ul>
</div>
<div layoutH="50">
    <form id="wf005TreeEditForm" method="post" class="required-validate">
        <input type="hidden" name="upFormTypeId" value="${nodeInfo.SysFormType.upFormTypeId}">
        <table class="border">
            <tr>
                <th><label>分类编码：</label></th>
                <td>
                    <input type="hidden" name="formTypeId" value="${nodeInfo.SysFormType.formTypeId}"/>
                    <input type="text" name="formTypeCd" style="ime-mode:disabled " class="required"
                           value="${nodeInfo.SysFormType.formTypeCd}"/>
                </td>
                <th><label>分类名称：</label></th>
                <td>
                    <input type="text" name="formTypeName" class="required"
                           value="${nodeInfo.SysFormType.formTypeName}"/>
                </td>
            </tr>
            <tr>
                <th><label>授权角色：</label></th>
                <td colspan="3" style="position: relative;">
                    <span style="float: left;">
                        <a title="选择" onclick="wf005.editRole(this);" class="btnLook">选择</a>
                    </span>
                    <span id="sys00201RoleSpan" style="height: 31px;line-height: 31px;">
                        <span class="hidden" style="float: left;">
                            <input checked="true" name="roleCd" value="" type="checkbox"/>
                        </span>
                        <c:forEach items="${rhtList}" var="role">
                            <input type="hidden" name="oldRoles" value="${role.FormTypeRht.rhtId}"/>
                            <span><input name="roleCd" type="checkbox" checked="true"
                                         value="${role.FormTypeRht.rhtId}"/>${role.FormTypeRht.rhtName}</span>
                        </c:forEach>
                    </span>
                </td>
            </tr>
        </table>
    </form>
</div>