<%--安置房源详细信息展示--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%--房源统计信息--%>
<div>
    <div class="panelBar">
        <ul class="toolBar">
            <li><a class="save" onclick="ch001.delayCount()"><span>确定</span></a></li>
            <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
        </ul>
    </div>
    <div>
        <form id="ch001Delayform" method="post" class="required-validate">
            <table class="border">
                <tr>
                    <th><label>延长时间：</label></th>
                    <td><input style="width: 50%;" type="text" name="delayNum" class="number" value="5">(分钟)</td>
                </tr>
            </table>
        </form>
    </div>
</div>
