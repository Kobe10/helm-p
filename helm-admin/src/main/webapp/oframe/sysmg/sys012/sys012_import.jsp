<%--安置房源详细信息展示--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%--房源统计信息--%>
<div>
    <div class="panelBar">
        <ul class="toolBar">
            <li><a class="save" onclick="sys012.importXml()"><span>导入</span></a></li>
            <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
        </ul>
    </div>
    <div>
        <form id="oh00103Rgform" method="post" class="required-validate">
            <table class="border">
                <tr>
                    <th><label>项目编码：</label></th>
                    <td><input name="toPrjCd" class="textInput" value="${param.prjCd}"></td>
                </tr>
                <tr>
                    <th><label>文件选择：</label></th>
                    <td>
                        <input type="file" name="sys012ImportXmlFile" id="sys012ImportXmlFile" accept=".xml"/>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
