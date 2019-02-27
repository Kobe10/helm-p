<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style type="text/css">
body{
    font-family: "微软雅黑", Verdana;
}
</style>

<div class="pageContent" layoutH="50" id="">

    <table class="border">
        <tr>
            <th width="25%"><label>发件人：</label></th>
            <td>
                ${noticeInfo.NoticeInfo.createStaffName}
            </td>
        </tr>
        <tr>
            <th><label>发件时间：</label></th>
            <td>
                <oframe:date value="${noticeInfo.NoticeInfo.createTime}" format="yyyy-MM-dd HH:mm:ss"/>
            </td>
        </tr>
        <tr>
            <th><label>消息内容：</label></th>
            <td>
                <textarea rows="10" style="width:99%; border: 0;font-family: '微软雅黑', Verdana">${noticeInfo.NoticeInfo.noticeContent}</textarea>
            </td>
        </tr>
    </table>
</div>
