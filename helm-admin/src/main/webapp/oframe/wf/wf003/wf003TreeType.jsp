<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div id="regionBar" class="panelBar">
    <ul class="toolBar">
        <li><a class="save" onclick="wf003.saveActType()"><span>保存</span></a></li>
        <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
    </ul>
</div>
<div layoutH="50">
    <form id="wf003TreeEditForm" method="post" class="required-validate">
        <input type="hidden" name="actTypeId" value="${nodeInfo.SysActType.actTypeId}"/>
        <input type="hidden" name="upActTypeId" value="${nodeInfo.SysActType.upActTypeId}">
        <table class="border">
            <tr>
                <th><label>分类编码：</label></th>
                <td>
                    <input type="text" name="actTypeCd" style="ime-mode:disabled " class="required"
                           value="${nodeInfo.SysActType.actTypeCd}"/>
                </td>
                <th><label>分类名称：</label></th>
                <td>
                    <input type="text" name="actTypeName" class="required"
                           value="${nodeInfo.SysActType.actTypeName}"/>
                </td>
            </tr>
            <tr>
                <th><label>授权组织：</label></th>
                <td colspan="3" style="position: relative;">
                    <span style="float: left;">
                        <a title="选择" onclick="wf003.editOrg(this);" class="btnLook">选择</a>
                    </span>
                    <span id="wf003OrgSpan" style="height: 31px;line-height: 31px;">
                        <span class="hidden" style="float: left;">
                            <input checked="true" name="orgCd" value="" type="checkbox"/>
                        </span>
                        <c:forEach items="${rhtList}" var="role">
                            <input type="hidden" name="oldRoles" value="${role.ActTypeRht.rhtId}"/>
                            <span><input name="orgCd" type="checkbox" checked="true"
                                         value="${role.ActTypeRht.rhtId}"/>${role.ActTypeRht.rhtName}</span>
                        </c:forEach>
                    </span>
                </td>
            </tr>
        </table>
    </form>
</div>