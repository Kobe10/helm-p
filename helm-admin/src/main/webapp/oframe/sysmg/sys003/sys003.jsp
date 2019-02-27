<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="pageContent">
    <form id="sys003frm" method="post" class="required-validate">
        <table class="form">
            <oframe:power prjCd="${param.prjCd}" rhtCd="OTH_PWD_RST">
                <tr>
                    <th><label>工号：</label></th>
                    <td>
                        <input name="staffCd" style="width: 50%;" type="text" value="${staffCd}" class="required"/>
                        <button name="resetPwd" onclick="sys003.resetPwd();" class="marr5 btn btn-opt" type="button">
                            重置
                        </button>
                    </td>
                    <td></td>
                </tr>
            </oframe:power>
            <tr>
                <th width="40%"><label>原密码：</label></th>
                <td width="40%">
                    <input name="oldPassword" style="width: 50%;" type="password" class="required"/>
                </td>
                <td width="20%"></td>
            </tr>
            <tr>
                <th><label>新密码：</label></th>
                <td>
                    <input name="newPassword" style="width: 50%;" id="newPassword" type="password"
                           minlength="5" maxlength="20" class="required"/>
                </td>
                <td></td>
            </tr>
            <tr>
                <th><label>新密码确认：</label></th>
                <td>
                    <input name="newPasswordCfm" style="width: 50%;" equalto="#newPassword" type="password"
                           class="required"/>
                </td>
                <td></td>
            </tr>
        </table>
        <div class="formBar center">
            <button name="chgPwd" onclick="sys003.chgPwd();" class="marr5 btn btn-primary" type="button">修改</button>
            <button type="button" class="close marr5 btn btn-info">取消</button>
        </div>
    </form>
</div>
<oframe:script src="${pageContext.request.contextPath}/oframe/sysmg/sys003/js/sys003.js"
               charset="utf-8" type="text/javascript"></oframe:script>