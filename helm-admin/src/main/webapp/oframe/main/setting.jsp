<%--
    个人设置
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2014/11/17 0017
  Time: 14:54
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<style type="text/css">
    div.inUsePanel {
        border: 1px solid rgb(184, 208, 214);
        padding: 10px;
        margin: 5px;
        display: block;
        position: relative;
        cursor: pointer;
    }
</style>
<div class="panel layoutBox">
    <div class="tabs">
        <div class="tabsHeader">
            <div class="tabsHeaderContent">
                <ul>
                    <li class="padl10 padr10"><a href="javascript:;">基本信息</a></li>
                    <li class="padl10 padr10"><a onclick="" href="javascript:void(0);">密码修改</a></li>
                    <li class="padl10 padr10"><a onclick="index.initSettingTree();" href="javascript:void(0);">面板设置</a>
                    </li>
                </ul>
            </div>
        </div>
        <%--基本信息--%>
        <div class="tabsContent">
            <div layoutH="45">
                <form id="settingbaseform" method="post" class="required-validate">
                    <input type="hidden" name="staffId" value="${nodeInfo.Staff.StaffId}"/>
                    <input type="hidden" name="method" value="${method}"/>
                    <table class="border">
                        <tr>
                            <th width="20%"><label>登陆账号：</label></th>
                            <td>
                                <input type="text" name="staffCode" class="required readonly" readonly="readonly"
                                       value="${nodeInfo.Staff.StaffCd}"/>
                            </td>
                            <th width="20%"><label>账号名称：</label></th>
                            <td>
                                <input type="text" name="staffName" class="required"
                                       value="${nodeInfo.Staff.StaffName}"/>
                            </td>
                        </tr>
                        <tr>
                            <th width="20%"><label>联系电话：</label></th>
                            <td>
                                <input type="text" name="staffTel" value="${nodeInfo.Staff.StaffTel}"/>
                            </td>
                            <th width="20%"><label>移动电话：</label></th>
                            <td>
                                <input type="text" name="staffMobile" value="${nodeInfo.Staff.staffMobile}"/>
                            </td>
                        </tr>
                        <tr>
                            <th width="20%"><label>邮箱地址：</label></th>
                            <td>
                                <input type="text" name="staffEmail" value="${nodeInfo.Staff.staffEmail}"/>
                            </td>
                            <th width="20%"><label>允许重复登录：</label></th>
                            <td>
                                <oframe:name prjCd="${param.prjCd}" itemCd="MULT_LOGIN"
                                             value="${nodeInfo.Staff.multLogin}"/>
                            </td>
                        </tr>
                        <tr>
                            <th width="20%"><label>锁定状态：</label></th>
                            <td>
                                <oframe:name prjCd="${param.prjCd}" itemCd="COMMON_YES_NO"
                                             value="${nodeInfo.Staff.isLocked}"/>
                            </td>
                            <th width="20%"><label>锁定时间：</label></th>
                            <td>
                                <input type="text" name="lockedTime" class="readonly" readonly="readonly"
                                       value="${nodeInfo.Staff.lockedTime}"/>
                            </td>
                        </tr>
                        <tr>
                            <th width="20%"><label>账户状态：</label></th>
                            <td>
                                <oframe:name prjCd="${param.prjCd}" itemCd="COM_STATUS_CD"
                                             value="${nodeInfo.Staff.StatusCd}"/>
                            </td>
                            <th width="20%"><label>所属组织：</label></th>
                            <td style="position: relative;">
                                <input type="hidden" name="orgId" value="${nodeInfo.Staff.OrgId}"/>
                                <input type="text" size="10" class="readonly required pull-left" readonly="readonly"
                                       name="orgName" value="${nodeInfo.Staff.OrgName}"/>
                                <%--<a title="选择" onclick="index.editOrg(this);" class="btnLook">选择</a>--%>
                            </td>
                        </tr>
                        <tr>
                            <th width="20%"><label>备注信息：</label></th>
                            <td colspan="3">
                                <textarea class="lsize" rows="9" name="notes">${nodeInfo.Staff.Notes}</textarea>
                            </td>
                        </tr>
                    </table>
                    <div class="formBar center">
                        <button type="button" class="btn btn-primary" onclick="index.saveStaff()">保存</button>
                    </div>
                </form>
            </div>
        </div>
        <%--密码修改--%>
        <div class="tabsContent">
            <div layoutH="45">
                <form id="settingpwdform" method="post" class="required-validate">
                    <input type="hidden" name="staffCd" value="${nodeInfo.Staff.StaffCd}"/>
                    <table class="border">
                        <tr>
                            <th width="30%"><label>原密码：</label></th>
                            <td>
                                <input name="oldPassword" type="password" class="required"/>
                            </td>
                        </tr>
                        <tr>
                            <th><label>新密码：</label></th>
                            <td>
                                <input name="newPassword" id="newPassword" type="password"
                                       minlength="5" maxlength="20" class="required"/>
                            </td>
                        </tr>
                        <tr>
                            <th><label>新密码确认：</label></th>
                            <td>
                                <input name="newPasswordCfm" equalto="#newPassword" type="password" class="required"/>
                            </td>
                        </tr>
                    </table>
                    <div class="formBar center">
                        <button name="chgPwd" onclick="index.chgPwd();" class="marr5 btn btn-primary" type="button">
                            修改
                        </button>
                        <button class="marr5 btn btn-info" type="reset">
                            重置
                        </button>
                    </div>
                </form>
            </div>
        </div>
        <%--面板设置--%>
        <div class="tabsContent">
            <div layoutH="45">
                <input type="hidden" name="panelStaffCode" value="${nodeInfo.Staff.StaffCd}"/>
                <!--目录展示-->
                <div class="left" style="width:30%;">
                    <div class="panel panel-blue">
                        <h1>可选面板</h1>

                        <div layoutH="140" style="overflow:auto; line-height:21px; background:#fff">
                            <ul id="settingMbRhtTree" class="ztree"></ul>
                        </div>
                    </div>
                </div>
                <!--内容编辑区-->
                <div class="right" style="width:68%;">
                    <div class="panel panel-blue">
                        <h1>已选面板</h1>
                        <div layoutH="140" id="settingPanelDiv" class="sortDrag" style="overflow: auto;">
                            <div class="hidden">
                                <label></label>
                                <%--<span onmouseup="index.removePanel(this);" class="settingRemove">X</span>--%>
                            </div>
                            <c:forEach items="${staffPanel}" var="item" varStatus="varStatus">
                                <div id="${item.portalRht.rhtId}" rhtId="${item.portalRht.rhtId}"
                                     class="inUsePanel js_panel_choose">
                                    <label>${item.portalRht.rhtName}</label>
                                        <%--<span onmouseup="index.removePanel(this);" class="settingRemove">X</span>--%>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                </div>
                <div class="clear"></div>
                <div class="formBar center">
                    <button name="savePanel" onclick="index.savePanel();" class="marr5 btn btn-primary" type="button">
                        保存
                    </button>
                </div>
            </div>
        </div>
    </div>
</div>


