<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div id="oh004_div">
    <table class="table" layoutH="130" width="100%">
        <thead>
        <tr style="text-align: center">
            <th width="5%">序号</th>
            <th width="15%">房屋地址</th>
            <th width="15%">产权性质</th>
            <th width="15%">建筑面积</th>
            <th width="15%">户主名称</th>
            <th width="15%">签约时间</th>
            <th width="15%">操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${returnList}" var="item" varStatus="varStatus">
            <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}"/>
            <tr class="${tr_class}" style="cursor: pointer">
                <td width="5%">${varStatus.index +1}
                    <input type="hidden" name="hsCtId" value="${hsCtId}"/>
                    <input type="hidden" name="wqPersonId" value="${personId}"/>
                </td>
                <td width="15%">${item.Row.hsFullAddr}</td>
                <td width="15%">
                        <oframe:name itemCd="HS_OWNER_TYPE" prjCd="${param.prjCd}" value="${item.Row.hsOwnerType}"/>
                </td>
                <td width="15%">${item.Row.hsBuildSize}</td>
                <td width="15%">${item.Row.personName}</td>
                <td width="15%">${item.Row.ctDate}</td>
                <td>
                    <a title="外迁处理"
                       onclick="oh004.extDeal('${item.Row.personId}',${item.Row.isHkWq});"
                       class="btnEdit">[外迁处理]</a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <jsp:include page="/oframe/common/page/page_fotter.jsp"/>
</div>