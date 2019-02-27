<%--安置房源详细信息展示--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%--房源统计信息--%>
<div>
    <div class="panelBar">
        <ul class="toolBar">
            <li><a class="save" onclick="sys017.importFile()"><span>导入</span></a></li>
            <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
        </ul>
    </div>
    <div>
        <form id="oh00103Rgform" method="post" class="required-validate">
            <input name="ruleTypeId" class="textInput" value="${ruleTypeId}" type="hidden">
            <table class="border">
                <tr>
                    <th><label>项目编码：</label></th>
                    <%--<td><input name="toPrjCd" class="textInput" value="${param.prjCd}"></td>--%>
                    <td>
                        <select name="toPrjCd">
                            <c:forEach items="${prjMap}" var="item">
                                <option value="${item.key}">${item.value}</option>
                            </c:forEach>
                        </select>
                    </td>
                </tr>
                <tr>
                    <th><label>文件选择：</label></th>
                    <td>
                        <input type="file" name="sys017ImportXmlFile" id="sys017ImportXmlFile" accept=".xml"/>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
