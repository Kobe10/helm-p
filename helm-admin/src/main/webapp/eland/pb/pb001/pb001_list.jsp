<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div>
    <table class="table" width="100%" layoutH="128" width="100%">
        <thead>
        <tr>
            <th width="50px"><label>序号</label></th>
            <%@include file="/oframe/common/query/list_htr.jsp" %>
            <th width="150px"><label>操作</label></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${returnList}" var="item" varStatus="varStatus">
            <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}"/>
            <tr class="${tr_class}">
                <td>${varStatus.index + 1}</td>
                <%@include file="/oframe/common/query/list_hdt.jsp" %>
                <td>
                    <button type="button" onclick="pb001.initBuildSummary(${item.Row.BuildInfo.buildId})"
                            class="btn btn-more mart5">
                        <i style="font-style:normal;">院落信息</i>
                        <span class="caret"></span>
                        <ul style="width: 200px;" class="menu hidden">
                            <%@include file="/eland/pb/pb002/pb002_list_op.jsp" %>
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
    <c:import url="/oframe/common/page/page_fotter.jsp" charEncoding="UTF-8"></c:import>
</div>
