<%--居民签约控制--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="pagecontent">
    <div class="panelBar">
        <ul class="toolBar">
            <li><a class="save" onclick="sys016.editJob();"><span>保存</span></a></li>
            <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
        </ul>
    </div>

    <div style="min-height: 150px">
        <table class="border marb5" width="100%">
            <input type="hidden" name="componetId" value="${prjComponent.PrjComponents.PrjComponent.componetId}"/>
            <tr>
                <th width="10%"><label>组件调用名称：</label></th>
                <td colspan="5"><input type="text" value="${prjComponent.PrjComponents.PrjComponent.componentNameEn}"
                                       name="componentNameEn"/>
                    <input type="hidden" name="oldComponentNameEn"
                           value="${prjComponent.PrjComponents.PrjComponent.componentNameEn}"/>
                </td>
            </tr>
            <tr>
                <th width="20%"><label>组件实现定义：</label></th>
                <td colspan="5"><input type="text" value="${prjComponent.PrjComponents.PrjComponent.componentName}"
                                       name="componentName"/></td>
            </tr>
            <tr>
                <th width="10%"><label>适用项目：</label></th>
                <td colspan="5">
                    <oframe:select prjCd="${param.prjCd}" collection="${prjCdMap}" name="prjCd"
                                   value="${prjComponent.PrjComponents.PrjComponent.prjCd}"
                                   style="width:81%"/>
                    <input type="hidden" name="oldPrjCd" value="${prjComponent.PrjComponents.PrjComponent.prjCd}"/>
                </td>
            </tr>
            <tr>
                <th><label>中文名称：</label></th>
                <td colspan="5"><input type="text" value="${prjComponent.PrjComponents.PrjComponent.componentNameCh}"
                                       name="componentNameCh"/></td>
            </tr>
            <tr>
                <th><label>实体名称：</label></th>
                <td colspan="5"><input type="text" value="${prjComponent.PrjComponents.PrjComponent.entityName}"
                                       name="entityName"/>
                </td>
            </tr>
            <tr>
                <th><label>执行前规则：</label></th>
                <td colspan="5"><input type="text" value="${prjComponent.PrjComponents.PrjComponent.executeBeforeRule}"
                                       name="executeBeforeRule"/></td>
            </tr>
            <tr>
                <th><label>执行后规则：</label></th>
                <td colspan="5"><input type="text" value="${prjComponent.PrjComponents.PrjComponent.executeAfterRule}"
                                       name="executeAfterRule"/></td>
            </tr>
            <tr>
                <th><label>组件描述：</label></th>
                <td colspan="5">
                    <textarea name="componentNote"
                              style="height: 80px;">${prjComponent.PrjComponents.PrjComponent.componentNote}</textarea>
                    <input type="hidden" name="para" value="${para}"/>
                </td>
            </tr>
        </table>
    </div>
</div>
