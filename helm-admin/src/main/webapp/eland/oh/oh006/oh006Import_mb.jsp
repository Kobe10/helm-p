<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div>
    <div class="panelBar">
        <ul class="toolBar">
            <li><a class="save" onclick="oh006.chooseImport()"><span>导入</span></a></li>
            <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
        </ul>
    </div>
    <div>
        <form id="importMbForm" method="post" class="required-validate">
            <table class="border">
                <tr>
                    <th><label>导入说明：</label></th>
                    <td>请使用系统提供的<a target="_blank"
                                   style="display: inline-block; color: #03408b; padding: 0 5px 0 5px;"
                                   href="${pageContext.request.contextPath}/eland/oh/oh006/oh006-downTemplate.gv">模板</a>填写明细数据。
                    </td>
                </tr>
                <tr>
                    <th><label>文件选择：</label></th>
                    <td>
                        <input type="file" name="importMbFile" id="importMbFile" accept=".xlsx"/>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
