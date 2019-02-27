<%--居民签约控制--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div id="pla">
    <table class="border">
        <tr>
            <th style="width:15%"><label>查找收件人：</label></th>
            <td>
                <div style="position: relative;">
                    <input type="hidden" id="sys014OrgId" name="prjOrgId"/>
                    <input type="text" name="prjOrgName" class="pull-left" width="200px"
                           onfocus="$(this).next('a').click()"
                           placeholder="点击右侧按钮查询信息接收人"/>
                    <a title="选择" onclick="$.fn.sltStaff(this,{offsetX: 0, fromOp:'sys014', prjCd:getPrjCd()});"
                       class="btnLook">选择</a>
                </div>
            </td>
        </tr>
        <tr style="height: 30px">
            <th style="width:15%"><label>收件人列表：</label></th>
            <input type="hidden" name="toStaffId"/>
            <td class="js_toStaff_tr">
                    <span class="hidden">
                        <span class="js_toStaff_name"></span>
                        <input type="hidden" name="currentStaff" value=""/>
                        <label onclick="sys014.rmToStaff(this);">X</label>
                    </span>
            </td>
        </tr>
        <tr style="height: 30px">
            <th style="width:15%"><label>群发组织：</label></th>
            <input type="hidden" name="toOrgId"/>
            <td class="js_toOrg_tr">
                    <span class="hidden">
                        <span class="js_toOrg_name"></span>
                        <input type="hidden" name="currentOrg" value=""/>
                        <label onclick="sys014.rmToOrg(this);">X</label>
                    </span>
            </td>
        </tr>
        <tr style="height: 30px">
            <th style="width:15%"><label>发送内容：</label></th>
            <td>
                <textarea rows="12" name="noticeContent" placeholder="输入发送消息内容..."
                          style="width:99%; border: 0"></textarea>
            </td>
        </tr>
        <tr style="height: 30px; text-align: center">
            <td colspan="2">
                <span class="btn btn-primary" onclick="sys014.sendMessage(this)">发送</span>
            </td>
        </tr>
    </table>
</div>
<oframe:script src="${pageContext.request.contextPath}/oframe/sysmg/sys014/js/sys014.js" type="text/javascript"/>
