<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div id="ch001001_list_print">
    <table class="table" width="100%">
        <thead>
        <tr>
            <th>序号</th>
            <%@include file="/oframe/common/query/list_htr.jsp" %>
            <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${returnList}" var="item" varStatus="varStatus">
            <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}"/>
            <tr class="${tr_class}" style="cursor:default;">
                <td width="5%">${varStatus.index + 1}</td>
                <td>${item.Row.NewHsInfo.hsAddr}</td>
                <td>${item.Row.NewHsInfo.hsHxName}</td>
                <td><oframe:name prjCd="${param.prjCd}" value="${item.Row.NewHsInfo.hsTp}" itemCd="HS_ROOM_TYPE"/></td>
                <td><oframe:name prjCd="${param.prjCd}" value="${item.Row.NewHsInfo.hsDt}" itemCd="HS_ROOM_DT"/></td>
                <td>${item.Row.NewHsInfo.preBldSize}</td>
                <td>
                    <a onclick="ch001.changeHs('${item.Row.NewHsInfo.newHsId}','${item.Row.NewHsInfo.hsAddr}','${item.Row.NewHsInfo.hsTp}','${item.Row.NewHsInfo.preBldSize}',this)">
                        <c:set var="selectedHs" value="${fn:split(param.selectedHs, ',')}"/>
                        <c:set var="newHsId" value="${item.Row.NewHsInfo.newHsId}${''}"/>
                        <c:set var="found" value="false"/>
                        <c:forEach var="tempItem" items="${selectedHs}">
                            <c:if test="${tempItem == newHsId}">
                                <c:set var="found" value="true"/>
                                <c:set var="exitId" value="0"/>
                            </c:if>
                        </c:forEach>
                        <c:choose>
                            <c:when test="${found}">
                                <span class="btn_opt_selected js_select">&nbsp;</span>
                            </c:when>
                            <c:otherwise>
                                <span class="btn_opt_un_select js_select">&nbsp;</span>
                            </c:otherwise>
                        </c:choose>
                        <input type="hidden" name="newHsId" value="${item.Row.NewHsInfo.newHsId}"/>
                    </a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <jsp:include page="/oframe/common/page/page_fotter.jsp"/>
</div>