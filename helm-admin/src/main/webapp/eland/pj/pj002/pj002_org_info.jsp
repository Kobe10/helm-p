<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="panelBar">
    <ul class="toolBar">
        <li><a class="save" onclick="prjOrg.saveOrg()"><span>保存</span></a></li>
    </ul>
</div>
<form id="pj002OrgStaffInfoFrm">
    <input type="hidden" value="${nodeInfo.Node.upNodeId}" name="upNodeId"/>
    <input type="hidden" value="${nodeInfo.Node.orgId}" name="orgId"/>
    <table class="border">
        <tr>
            <th width="15%"><label>组织名称：</label></th>
            <td width="35%"><input type="text" name="orgName" class="required" value="${nodeInfo.Node.orgName}"></td>
            <th width="15%"><label>上级组织：</label></th>
            <td>${nodeInfo.Node.upNodeName}</td>
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
                    <ul id="pj002SltStaffTree" class="ztree"></ul>
                </div>
            </td>
            <td colspan="2" style="margin: 0px;padding: 0px;">
                <div layoutH="168">
                    <ul id="pj002OrgStaff" class="list">
                        <li staffId="" class="hidden">
                            <label style="display: inline-block;"></label>
                                <span onclick="prjOrg.dltOrgStaff(this);" style="float: right;"
                                      class="link marr20">删除</span>
                        </li>
                        <c:forEach items="${staffList}" var="item">
                            <li staffId="${item.Staff.staffId}">
                                <label style="display: inline-block;">${item.Staff.staffCode}(${item.Staff.staffName})</label>
                                <span onclick="prjOrg.dltOrgStaff(this);" style="float: right;"
                                      class="link marr20">删除</span>
                            </li>
                        </c:forEach>
                    </ul>
                </div>
            </td>
        </tr>
    </table>
</form>
<oframe:script src="${pageContext.request.contextPath}/eland/pj/pj002/js/prjOrg.js" type="text/javascript"/>
