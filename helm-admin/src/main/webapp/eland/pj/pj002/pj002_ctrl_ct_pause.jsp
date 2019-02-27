<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="panelBar">
    <ul class="toolBar">
        <li><a class="save" onclick="ct001CtrlCt.savePause();"><span>提交</span></a></li>
        <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
    </ul>
</div>
<div>
    <table class="form">
        <tr>
            <th>暂停签约时长：</th>
            <td><input name="pauseTime" value="10" class="required number">(分)</td>
        </tr>
    </table>
</div>