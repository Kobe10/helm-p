<%--居民签约控制--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="pagecontent">
    <div class="panelBar">
        <ul class="toolBar">
            <li><a class="save" onclick="wf004.delegationWf('${taskId}');"><span>发送</span></a></li>
            <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
        </ul>
    </div>

    <div style="min-height: 150px">
        <table class="border marb5" width="100%">
            <tr>
                <th width="15%"><label>处理人选择：</label></th>
                <td colspan="6" style="text-align: left">
                    <div style="position: relative;">
                        <input type="hidden" name="wfStaff"/>
                        <input type="text" class="pull-left" width="100px"
                               onfocus="$(this).next('a').click()"
                               placeholder="点击右侧按钮指定处理人"/>
                        <a title="选择" onclick="$.fn.sltStaff(this,{offsetX: 3});" class="btnLook">选择</a>
                    </div>
                </td>
            </tr>
        </table>
    </div>
</div>
