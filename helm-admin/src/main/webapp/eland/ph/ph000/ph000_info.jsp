<%--居民签约处理--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div>
    <div class="panelBar">
        <ul class="toolBar">
            <c:forEach items="${operaList}" var="item" varStatus="varStatus">
                <c:if test="${item.OperationDef.operRht == 'true'}">
                    <oframe:op rhtCd="${item.OperationDef.operRhtCd}" prjCd="${param.prjCd}"
                               onClick="${item.OperationDef.operOnClick}"
                               cssClass="${item.OperationDef.operClass}"
                               name="${item.OperationDef.operName}" template="${item.OperationDef.operTemplate}"/>
                </c:if>
            </c:forEach>
            <li onclick="ph000.viewHouse();">
                <a class="new-area" href="javascript:void(0)"><span>详情</span></a>
            </li>
            <li onclick="ph000.singleQuery('next');" style="float: right">
                <a class="export" href="javascript:void(0)"><span>下一户</span></a>
            </li>
            <li onclick="ph000.singleQuery('last');" style="float: right">
                <a class="import" href="javascript:void(0)"><span>上一户</span></a>
            </li>
        </ul>
    </div>
    <div class="panelContent" layoutH="55">
        ${article}
    </div>
</div>
<script type="text/javascript">
    <%--调用 js 区--%>
    ${operDefCode}
</script>