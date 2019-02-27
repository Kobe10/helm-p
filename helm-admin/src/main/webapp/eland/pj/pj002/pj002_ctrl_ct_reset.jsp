<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="panelBar">
    <ul class="toolBar">
        <li><a class="save" onclick="ct001CtrlCt.saveResetData();"><span>提交</span></a></li>
        <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
    </ul>
</div>
<div>
    <table class="border">
        <tr>
            <th width="40%">清空签约数据：</th>
            <td><oframe:select itemCd="COMMON_YES_NO" type="radio" name="resetDataFlag" value="0"/></td>
        </tr>
    </table>
</div>