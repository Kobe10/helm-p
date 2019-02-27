<%--居民签约控制--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="pagecontent">
    <div class="panelBar">
        <ul class="toolBar">
            <li><a class="save" onclick="sys020.sendMessageZDY();"><span>发送</span></a></li>
            <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
        </ul>
    </div>
    <div layoutH="55" style="border: 1px solid rgb(233, 233, 233);">
        <form id="sys020MsgForm">
            <table class="border marb5" width="100%" id="sys020cuspro">
                <tr>
                    <th>
                        <lable>手机号码：</lable>
                    </th>
                    <td>
                        <input type="text" name="rPhoneNum" class="required" placeholder="多个号码用英文逗号分隔"/>
                    </td>
                </tr>
                <tr>
                    <th>
                        <lable>发送时间：</lable>
                    </th>
                    <td>
                        <input type="text" class="date" placeholder="不填写会立即发送" name="sTime" datefmt="yyyy-MM-dd HH:mm:ss" value="<oframe:date value="" format="yyyy-MM-dd HH:mm:ss"/>"/>
                    </td>
                </tr>
                <tr>
                    <th>
                        <lable>发送内容：</lable>
                    </th>
                    <td>
                        <textarea name="sContent" cols="80" rows="13"></textarea>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
