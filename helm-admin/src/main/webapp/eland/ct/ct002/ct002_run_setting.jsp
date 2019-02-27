<%--安置房源详细信息展示--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%--房源统计信息--%>
<div>
    <div class="panelBar">
        <ul class="toolBar">
            <li><a class="save" onclick="ct002.runScheme()"><span>计算</span></a></li>
            <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
        </ul>
    </div>
    <div>
        <form id="ct002RunSFrm" method="post" class="required-validate">
            <input type="hidden" name="schemeId" value="${schemeId}">
            <table class="border">
                <tr>
                    <th><label>匹配适用条件：</label></th>
                    <td>
                        <oframe:select prjCd="${param.prjCd}" type="radio" itemCd="COMMON_YES_NO" name="useCondition" value="0"/>
                    </td>
                </tr>
                <tr>
                    <th><label>计算批次名称：</label></th>
                    <td>
                        <input type="text" name="preBatch" value="${preBatch}">
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
