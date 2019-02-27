<%--居民签约控制--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="pagecontent">
    <div class="panelBar">
        <ul class="toolBar">
            <li><a class="save" onclick="ph010.saveMP()"><span>保存</span></a></li>
            <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
        </ul>
    </div>

    <div style="min-height: 150px">
        <table class="border marb5" width="100%">
            <input type="hidden" name="camId" value="${cameralist.CameraMngInfo.camId}">
            <input type="hidden" name="markPos" value="${cameralist.CameraMngInfo.markPos}">
            <tr>
                <th width="25%"><label>摄像头分组：</label></th>
                <td><input type="text" value="${cameralist.CameraMngInfo.camCd}" name="camCd"></td>
            </tr>
            <tr>
                <th><label>通道编号：</label></th>
                <td><input type="text" value="${cameralist.CameraMngInfo.channelCd}" name="channelCd"></td>
            </tr>
            <tr>
                <th><label>摄像头地址：</label></th>
                <td><input type="text" value="${cameralist.CameraMngInfo.camAddr}" name="camAddr"></td>
            </tr>
            <tr>
                <th><label>访问地址：</label></th>
                <td><input type="text" value="${cameralist.CameraMngInfo.accessAddr}" name="accessAddr"></td>
            </tr>
            <%--<tr>--%>
                <%--<th><label>标识坐标：</label></th>--%>
                <%--<td><input type="text" value="${cameralist.cameraMngInfo.markPos}" name="markPos"></td>--%>
            <%--</tr>--%>
        </table>
    </div>
</div>