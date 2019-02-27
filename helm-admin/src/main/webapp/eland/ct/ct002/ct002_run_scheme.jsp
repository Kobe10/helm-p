<%--安置房源详细信息展示--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%--房源统计信息--%>
<div>
    <div class="panelBar">
        <ul class="toolBar">
            <li><a class="save" onclick="ct002.runScheme(${schemeId});$.pdialog.closeCurrent();"><span>提交计算</span></a></li>
            <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭窗口</span></a></li>
        </ul>
    </div>
    <div>
        <form id="ct002RunSFrm" method="post" class="required-validate">
            <table class="border">
                <tr>
                    <th width="25%"><label>预分计算名称：</label></th>
                    <td>
                        <input type="text" name="batchName"/>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
