<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div id="ph001001query_list">
    <table class="table" layoutH="130" width="100%">
        <thead>
        <tr>
            <th width="3%"><input type="checkbox" class="checkboxCtrl" group="ids"/></th>
            <%@include file="/oframe/common/query/list_htr.jsp" %>
            <th style="width: 120px;">操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${returnList}" var="item" varStatus="varStatus">
            <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}"/>
            <tr class="${tr_class}">
                <td><input type="checkbox" group="ids" name="hsId" value="${item.Row.HouseInfo.hsId}"/></td>
                <%@include file="/oframe/common/query/list_hdt.jsp" %>
                <td>
                    <c:choose>
                        <c:when test="${param.inputRhtCd == null || param.inputRhtCd == ''}">
                            <c:set value="" var="clickFcn"/>
                            <oframe:power prjCd="${param.prjCd}" rhtCd="old_hs_detail_rht">
                                <c:set var="clickFcn" value="ph001.openHs('${item.Row.HouseInfo.hsId}')"/>
                            </oframe:power>
                            <button type="button" onclick="${clickFcn}" class="btn btn-more mart5">
                                <i style="font-style:normal;">居民详情</i>
                                <span class="caret"></span>
                                <ul style="width: 200px;" class="menu hidden">
                                    <%@include file="/eland/ph/ph003/ph003_list_op.jsp" %>
                                    <span style="clear: both"></span>
                                </ul>
                            </button>
                        </c:when>
                        <c:otherwise>
                            <button type="button" onclick="ph001.editHs('${item.Row.HouseInfo.hsId}')"
                                    class="btn btn-more mart5">
                                <i style="font-style:normal;">信息修改</i>
                                <span class="caret"></span>
                                <ul style="width: 200px;" class="menu hidden">
                                    <%@include file="/eland/ph/ph003/ph003_list_op.jsp" %>
                                    <span style="clear: both"></span>
                                </ul>
                            </button>
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </c:forEach>
        <tr class="summary">
            <td>汇总</td>
            <%@include file="/oframe/common/query/list_sum.jsp" %>
            <td></td>
        </tr>
        </tbody>
    </table>
    <jsp:include page="/oframe/common/page/page_fotter.jsp"/>
</div>