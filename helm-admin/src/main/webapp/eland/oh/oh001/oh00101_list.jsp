<%--人地房房产信息检索--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div id="oh00101_list_print">
    <table class="table" layoutH="130" width="100%">
        <thead>
        <tr>
            <th width="5%" fieldName="newHsId">
                <input type="checkbox" class="checkboxCtrl" group="ids"/>
            </th>
            <%@include file="/oframe/common/query/list_htr.jsp" %>
            <th width="10%">操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${returnList}" var="item" varStatus="varStatus">
            <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}"/>
            <tr class="${tr_class}">
                <td><input type="checkbox" group="ids" name="newHsId" value="${item.Row.NewHsInfo.newHsId}"/></td>
                <%@include file="/oframe/common/query/list_hdt.jsp" %>
                <td>
                    <button type="button" onclick="oh001.info('${item.Row.NewHsInfo.newHsId}')"
                            class="btn btn-more mart5">
                        <i style="font-style:normal;">房源详情</i>
                        <span class="caret"></span>
                        <ul class="menu hidden">
                            <oframe:op prjCd="${param.prjCd}" template="li" cssClass="menu"
                                       name="删除房源" rhtCd="edit_${item.Row.NewHsInfo.hsClass}_new_hs_rht"
                                       onClick="oh001.delete('${item.Row.NewHsInfo.newHsId}')"/>
                        </ul>

                    </button>
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
