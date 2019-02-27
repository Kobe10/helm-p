<%--居民签约控制--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="pagecontent">
    <div class="panelBar">
        <ul class="toolBar">
            <li><a class="save" onclick="sys019.sendViewMessage('${messageDefId}');"><span>发送</span></a></li>
            <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
        </ul>
    </div>

    <div style="min-height: 150px">
        <table class="border marb5" width="100%">
            <tr>
                <th width="15%"><label>报文定义：</label></th>
                <td colspan="5">
                    <textarea name="message" style="height: 290px"></textarea>
                </td>
            </tr>
        </table>
    </div>
</div>
