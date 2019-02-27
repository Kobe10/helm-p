<%-- 延期交房处理 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%--房源统计信息--%>
<div>
    <div class="panelBar">
        <ul class="toolBar">
            <li><a class="save" onclick="oh007.markDelay()"><span>提交</span></a></li>
            <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
        </ul>
    </div>
    <div>
        <form id="oh007DelayForm" method="post" class="required-validate">
            <input type="hidden" name="newHsIds" value="${newHsIds}">
            <table class="border">
                <tr>
                    <th width="30%"><label>交房时间：</label></th>
                    <td><input type="text" class="textInput date required" name="handHsDate"></td>
                </tr>
            </table>
        </form>
    </div>
</div>
