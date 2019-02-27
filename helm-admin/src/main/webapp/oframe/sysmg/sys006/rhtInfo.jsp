<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="panelBar">
    <ul class="toolBar">
        <li><a class="save" onclick="sys006.saveNode(1);"><span>保存资源</span></a></li>
        <c:set var="cNodeId" value="${nodeInfo.Node.rhtId}${''}"/>
        <c:if test="${cNodeId != ''}">
            <li><a class="add marl10" onclick="sys006.saveNode(2);"><span>复制新增</span></a></li>
        </c:if>
    </ul>
</div>
<div layoutH="55">

    <form method="post" id="frm" class="required-validate">
        <input type="hidden" value="${nodeInfo.Node.uRhtId}" name="pNodeId"/>
        <input type="hidden" value="${nodeInfo.Node.rhtId}" name="cNodeId"/>
        <input type="hidden" name="method" value="${method}"/>
        <input type="hidden" name="oldCNodeCd" value="${nodeInfo.Node.rhtCd}"/>
        <table class="border">
            <tr>
                <th width="15%">权限类别：</th>
                <td width="35%">
                    <oframe:select prjCd="${param.prjCd}" name="rhtSubType" itemCd="RHT_SUB_TYPE" cached="true"
                                   value="${nodeInfo.Node.rhtSubType}"/>
                </td>
                <th width="15%">业务分类：</th>
                <td width="35%">
                    <oframe:select prjCd="${param.prjCd}" name="rhtUseType" itemCd="RHT_USE_TYPE" cached="true"
                                   value="${nodeInfo.Node.rhtUseType}"/>
                </td>
            </tr>
            <tr>
                <th>资源编码：</th>
                <td><input type="text" value="${nodeInfo.Node.rhtCd}" class="required" name="cNodeCd"/></td>
                <th>资源状态：</th>
                <td>
                    <oframe:select prjCd="${param.prjCd}" name="statusCd" itemCd="RHT_STATUS_CD" cached="true"
                                   value="${nodeInfo.Node.statusCd}"/>
                </td>
            </tr>
            <tr>
                <th>资源名称：</th>
                <td colspan="3">
                    <input type="text" class="required" name="cNodeName" value="${nodeInfo.Node.rhtName}"/>
                </td>
            </tr>
            <tr>
                <th>资源属性：</th>
                <td colspan="3">
                    <input type="text" class="required msize"
                           name="cNodeAttr" value="${nodeInfo.Node.rhtAttr01}"/>
                </td>
            </tr>
            <tr>
                <th>备注说明：</th>
                <td colspan="3">
                    <textarea id="cNodeDesc" name="cNodeDesc" class="titleFormat" title="<c:out value="${nodeInfo.Node.rhtAttr02}"/>"
                              rows="10">${nodeInfo.Node.rhtAttr02}</textarea>
                </td>
            </tr>
        </table>
    </form>
</div>

