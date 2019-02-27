<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="pageContent">
    <table>
        <tbody>
        <c:forEach items="${returnList}" var="item" varStatus="varStatus">
            <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}"/>
            <tr class="${tr_class}">
                <td width="22">
                    <input type="checkbox" name="itemCd" title="${item.item_cd}" value="${item.item_cd}"
                           cfgPrjCd="${item.prj_cd}" group="ids">
                </td>
                <td>${varStatus.index + 1}</td>
                <td>${item.item_cd}</td>
                <td>${item.prj_cd}</td>
                <td>${item.item_name}</td>
                <td><oframe:name prjCd="${param.prjCd}" itemCd="RHT_USE_TYPE" value="${item.item_use_type}"/></td>
                <td><oframe:name prjCd="${param.prjCd}" itemCd="COM_STATUS_CD" value="${item.status_cd}"/></td>
                <td><oframe:date value="${item.status_date}" format="yyyy-MM-dd HH:m:ss"/> </td>
                <td>
                    <a title="查看" onclick="sys005.openView('${item.item_cd}','${item.prj_cd}')" class="btnView">[查看]</a>
                    <oframe:op prjCd="${param.prjCd}" template="a" cssClass="btnEdit" name="修改"
                               rhtCd="EDIT_SYS_CFG_RHT" onClick="sys005.openEdit('${item.item_cd}','${item.prj_cd}')"/>
                    <oframe:op prjCd="${param.prjCd}" template="a" cssClass="btnDel" name="删除"
                               rhtCd="DEL_SYS_CFG_RHT" onClick="sys005.deleteCfg('${item.item_cd}','${item.prj_cd}')"/>
                </td>
            </tr>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <c:import url="/oframe/common/page/pager_fotter.jsp" charEncoding="UTF-8"></c:import>
</div>

