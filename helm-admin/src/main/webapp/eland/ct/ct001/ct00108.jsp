<%--完成确认签约，打印回执单--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:if test="${fn:length(resultList)>0}">
    <div>
        <table class="table" width="100%">
            <thead>
            <tr>
                <th>档案编号</th>
                <th>被安置人</th>
                <th>房屋地址</th>
                <th>安置方式</th>
                <th>签约时间</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${resultList}" var="item">
                <tr>
                    <td>${item.Row.HouseInfo.hsCd}</td>
                    <td>${item.Row.HouseInfo.hsOwnerPersons}</td>
                    <td>${item.Row.HouseInfo.hsFullAddr}</td>
                    <td><oframe:name prjCd="${param.prjCd}" itemCd="10001"
                                     value="${item.Row.HouseInfo.HsCtInfo.ctType}"/></td>
                    <td>${item.Row.HouseInfo.HsCtInfo.ctDate}</td>
                    <td>
                        <a href="javascript:void(0)"><span
                                onclick="ct001.oneCtInfo('${item.Row.HouseInfo.hsId}', 'viewOneCfm');">[签约回执单]</span></a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</c:if>
<div id="ct00108OneDiv">
    <jsp:include page="ct00102.jsp"/>
</div>

