<%--选房控制控制--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<style>
    .input {
        width: 200px;
    }
</style>
<div class="panelBar">
    <ul class="toolBar">
        <li><a class="save" onclick="ct001CtrlCh.saveCtCtrl()"><span>保存</span></a></li>
    </ul>
</div>
<div>
    <form id="ct001_ch" method="post">
        <input name="scId" value="${ctrlInfo.SysControlInfo.scId}" type="hidden"/>
        <input name="scType" value="2" type="hidden"/>
        <table class="border">
            <tr>
                <th width="20%">选房启动开关：</th>
                <td>
                    <c:choose>
                        <c:when test="${ctrlInfo.SysControlInfo.startCd == '1'}">
                            <input type="radio" name="startCd" value="1" checked>启动倒计时
                            <input type="radio" name="startCd" value="2">暂停选房
                            <input type="radio" name="startCd" value="3">关闭倒计时
                        </c:when>
                        <c:when test="${ctrlInfo.SysControlInfo.startCd == '2'}">
                            <input type="radio" name="startCd" value="1">启动倒计时
                            <input type="radio" name="startCd" value="2" checked>暂停选房
                            <input type="radio" name="startCd" value="3">关闭倒计时
                        </c:when>
                        <c:otherwise>
                            <input type="radio" name="startCd" value="1">启动倒计时
                            <input type="radio" name="startCd" value="2">暂停选房
                            <input type="radio" name="startCd" value="3" checked>关闭倒计时
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
            <tr>
                <th>最大选房间隔：</th>
                <td><input type="text" class="input digits required" max="59" min="1"
                           name="spaceTime" id="spaceTime"
                           value="${ctrlInfo.SysControlInfo.spaceTime}">(分钟)
                </td>
            </tr>
        </table>
    </form>
</div>
