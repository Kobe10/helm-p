<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div>
    <div class="panelBar">
        <ul class="toolBar">
            <li><a class="save" onclick="sys00201.saveStaff()"><span>保存</span></a></li>
            <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
        </ul>
    </div>
    <div>
        <form id="sys00201form" method="post" class="required-validate">
            <input type="hidden" name="staffId" value="${nodeInfo.Staff.StaffId}"/>
            <input type="hidden" name="method" value="${method}"/>
            <table class="border">
                <tr>
                    <th width="15%"><label>员工账户：</label></th>
                    <td>
                        <input type="text" name="staffCode" class="required" value="${nodeInfo.Staff.StaffCd}"/>
                    </td>
                    <th width="15%"><label>账户名称：</label></th>
                    <td>
                        <input type="text" name="staffName" class="required" value="${nodeInfo.Staff.StaffName}"/>
                    </td>
                </tr>
                <tr>
                    <th><label>账户密码：</label></th>
                    <td>
                        <c:choose>
                            <c:when test="${method == 'add'}">
                                <input type="password" name="password" class="required readonly"
                                       readonly="readonly" value="111111"/>
                            </c:when>
                            <c:otherwise>
                                <input type="password" name="password" class="required readonly"
                                       readonly="readonly"
                                       value="${nodeInfo.Staff.Password}"/>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <th><label>联系电话：</label></th>
                    <td>
                        <input type="text" name="staffTel" value="${nodeInfo.Staff.StaffTel}"/>
                    </td>
                </tr>
                <tr>
                    <th><label>移动电话：</label></th>
                    <td>
                        <input type="text" name="staffMobile" value="${nodeInfo.Staff.staffMobile}"/>
                    </td>
                    <th><label>邮箱地址：</label></th>
                    <td>
                        <input type="text" name="staffEmail" value="${nodeInfo.Staff.staffEmail}"/>
                    </td>
                </tr>
                <tr>
                    <th><label>公司组织：</label></th>
                    <td style="position: relative;">
                        <input type="hidden" name="orgId" value="${nodeInfo.Staff.OrgId}"/>
                        <input type="text" size="10" class="readonly required pull-left"
                               readonly="readonly" name="orgName"
                               title="<oframe:org orgId='${nodeInfo.Staff.OrgId}' prjCd='0' property='Org.Node.nodePathName'/>"
                               value="<oframe:org orgId='${nodeInfo.Staff.OrgId}' prjCd='0'/> "/>
                        <a title="选择" onclick="$.fn.sltOrg(this,{args:{orgPrjCd: 0},offsetX: 3});"
                           class="btnLook">选择</a>
                    </td>
                    <th><label>重复登录：</label></th>
                    <td>
                        <oframe:select prjCd="${param.prjCd}" itemCd="MULT_LOGIN" name="multLogin"
                                       value="${nodeInfo.Staff.multLogin}"/>
                    </td>
                </tr>
                <tr>
                    <th><label>登录IP限制：</label></th>
                    <td colspan="3">
                        <input type="text" name="loginIp" style="width: 70%;"
                               placeholder="空不进行限制，多个使用分号分隔，可以用*表示任意,如：192.168.1.*;127.0.0.1"
                               value="${nodeInfo.Staff.loginIp}"/>&nbsp;&nbsp;&nbsp;&nbsp;多个IP使用分号(;)分隔
                    </td>
                </tr>
                <tr>
                    <th><label>锁定状态：</label></th>
                    <td>
                        <oframe:select prjCd="${param.prjCd}" itemCd="COMMON_YES_NO" name="isLocked"
                                       value="${nodeInfo.Staff.isLocked}"/>
                    </td>
                    <th><label>锁定时间：</label></th>
                    <td>
                        <input type="text" name="lockedTime" class="readonly" readonly="readonly"
                               value="${nodeInfo.Staff.lockedTime}"/>
                    </td>
                </tr>
                <tr>
                    <th><label>账户状态：</label></th>
                    <td>
                        <oframe:select prjCd="${param.prjCd}" itemCd="COM_STATUS_CD" name="statusCd"
                                       value="${nodeInfo.Staff.StatusCd}"/>
                    </td>
                    <th><label>状态时间：</label></th>
                    <td>
                        <input type="text" name="statusDate" class="readonly" readonly="readonly"
                               value="${nodeInfo.Staff.StatusDate}"/>
                    </td>
                </tr>
                <tr>
                    <th><label>备注信息：</label></th>
                    <td colspan="3">
                        <textarea rows="8" style="width: 90%" name="notes">${nodeInfo.Staff.Notes}</textarea>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<script type="text/javascript">
    sys00201 = {
        saveStaff: function () {
            var $form = $("#sys00201form", $.pdialog.getCurrent());
            if ($form.valid()) {
                // 提交页面数据
                var url = getGlobalPathRoot() + "oframe/sysmg/sys002/sys002-save.gv?prjCd=" + getPrjCd();
                var ajaxPacket = new AJAXPacket(url);
                ajaxPacket.data.data = $form.serializeArray();
                core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                    var jsonData = eval("(" + response + ")");
                    // 服务调用是否成功
                    var isSuccess = jsonData.success;
                    if (isSuccess) {
                        alertMsg.correct("处理成功");
                        $.pdialog.closeCurrent();
                    } else {
                        alertMsg.error(jsonData.errMsg);
                    }
                });
            }
        }
    }
</script>