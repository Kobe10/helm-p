<%--人地房信息展示页面--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%--房源统计信息--%>
<div class="panel" id="sys012Summary">
    <h1>
        <span>实体定义</span>
    </h1>

    <form id="addEntityView">
        <div>
            <table class="border">
                <tr>
                    <th><label>表名：</label></th>
                    <td>
                        <input type="hidden" name="usePrjCd" class="required" value="${usePrjCd}">
                        <input type="text" name="primarySaveTable" class="required" value="">
                    </td>
                    <th><label>表主键：</label></th>
                    <td>
                        <input type="text" name="primarySaveField" class="required" value="">
                    </td>
                </tr>
                <tr>
                    <th><label>英文名称：</label></th>
                    <td>
                        <input type="text" name="entityName" class="required" value="">
                    </td>
                    <th><label>中文名称：</label></th>
                    <td>
                        <input type="text" name="entityDesc" class="required" value="">
                    </td>
                </tr>
                <tr>
                    <th><label>业务主键：</label></th>
                    <td>
                        <input type="text" name="primaryField" class="required"
                               value="">
                    </td>
                    <th><label>业务序列：</label></th>
                    <td>
                        <input type="text" name="primarySeqName" class="required"
                               value="">
                    </td>
                </tr>
                <tr>
                    <th><label>访问地址：</label></th>
                    <td colspan="3">
                        <input type="text" name="dataViewUrl" class=""
                               value="">
                    </td>
                </tr>
                <tr>
                    <td colspan="4" align="center">
                        <span onclick="sys012.addEntity('addEntityView')" class="btn btn-primary">保存</span>
                    </td>
                </tr>
            </table>
        </div>
    </form>
</div>