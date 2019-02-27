<%--自定义分页查询查询条件--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<form id="favForm">
    <table class="form">
        <tr>
            <th><label>收藏名称：</label></th>
            <td>
                <input type="text" name="favName" value="${favName}" class="required"/>
                <oframe:power prjCd="${param.prjCd}" rhtCd="EDIT_SYS_FAV">
                    <input type="checkbox" value="${favStaff}" name="favFlag"/>系统级
                </oframe:power>
            </td>
        </tr>
        <tr>
            <td colspan="2" align="center">
                <span onclick="Page.saveFav('${entityName}');"
                      class="btn btn-primary">收藏</span>
            </td>
        </tr>
    </table>
</form>

