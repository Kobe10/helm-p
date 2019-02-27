<%--人地房信息展示页面--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%--房源统计信息--%>
<div class="panel" id="sys012Summary">
    <h1>
        <span>分组定义</span>
    </h1>

    <form id="addGroupView">
        <div>
            <table class="border">
                <tr>
                    <th><label>归属实体：</label></th>
                    <td>
                        <input type="hidden" name="usePrjCd" class="required" value="${usePrjCd}">
                        <input type="text" name="entityName"
                               class="required readonly" readonly
                               value="${entityName}">
                    </td>
                    <th><label>英文名称：</label></th>
                    <td>
                        <input type="text" name="entityGroupNameEn" class="required"
                               value="">
                    </td>
                </tr>
                <tr>
                    <th><label>中文名称：</label></th>
                    <td>
                        <input type="text" name="entityGroupNameCh" class="required"
                               value="">
                    </td>
                    <th><label>分组描述：</label></th>
                    <td>
                        <input type="text" name="entityGroupDesc" class="required"
                               value="">
                    </td>
                </tr>
                <tr>
                    <td colspan="4" align="center">
                        <span onclick="sys012.addGroup('addGroupView','${pId}')" class="btn btn-primary">保存</span>
                    </td>
                </tr>
            </table>
        </div>
    </form>
</div>
