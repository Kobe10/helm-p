<%--安置房源详细信息展示--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%--房源统计信息--%>
<div>
    <div class="panelBar">
        <ul class="toolBar">
            <li><a class="save" onclick="oh001.doImport()"><span>导入</span></a></li>
            <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
        </ul>
    </div>
    <div>
        <form id="oh00103Rgform" method="post" class="required-validate">
            <input type="hidden" name="regUseType" value="${regUseType}">
            <table class="border">
                <tr>
                    <th><label>导入说明：</label></th>
                    <td>请使用系统提供的<a target="_blank"
                                   style="display: inline-block; color: #03408b; padding: 0 5px 0 5px;"
                                   href="${pageContext.request.contextPath}/eland/oh/oh001/oh001-downTemplate.gv"
                            >模板</a>填写房源明细数据。
                    </td>
                </tr>
                <tr>
                    <th><label>文件选择：</label></th>
                    <td>
                        <input type="file" name="oHouseImportFiled" id="oHouseImportFile" accept=".xls,.xlsx"/>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
