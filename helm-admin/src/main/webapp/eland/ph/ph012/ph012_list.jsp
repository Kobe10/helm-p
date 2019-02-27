<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div id="ph012List">
    <table class="table" border="0" width="100%" layoutH="130">
        <thead>
            <tr>
                <th>被关联房产安置人</th>
                <th>被关联房产地址</th>
                <th>被关联房产管理组织</th>
                <th>关联被安置人</th>
                <th>关联房产地址</th>
                <th>关联房产管理组织</th>
                <th>关联关系</th>
                <th>备注说明</th>
                <th>操作</th>
            </tr>
        </thead>
        <tbody>
        <c:forEach items="${returnList}" var="item" varStatus="varStatus">
            <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}"/>
            <tr class="${tr_class}">
                <td>${item.Row.ctPsNamesA}</td>
                <td>${item.Row.hsFullAddrA}</td>
                <td><oframe:org prjCd="${param.prjCd}" orgId="${item.Row.ttOrgIdA}"/></td>
                <td>${item.Row.ctPsNamesB}</td>
                <td>${item.Row.hsFullAddrB}</td>
                <td><oframe:org prjCd="${param.prjCd}" orgId="${item.Row.ttOrgIdB}"/></td>
                <td>${item.Row.relName}</td>
                <td>${item.Row.reNote}</td>
                <td>
                    <a title="修改" onclick="ph012.changeEdit('${item.Row.hsCtReId}');" class="btnEdit">[修改]</a>
                    <a title="删除" onclick="ph012.deleteRel('${item.Row.hsCtReId}');" class="btnEdit">[删除]</a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <jsp:include page="/oframe/common/page/page_fotter.jsp"/>
</div>
