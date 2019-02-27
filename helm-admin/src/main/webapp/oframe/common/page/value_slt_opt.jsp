<%--代码表类型检索条件，自定义--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%--数据过滤类型(选项过滤)--%>
<input type="hidden" class="js_condType" value="opt">
<input type="hidden" class="js_model" value="${model}">

<div class="triangle"></div>
<div class="clearfix">
    <c:forEach items="${values}" var="item">
        <c:choose>
            <c:when test="${fn:indexOf(sltValues,item.key) >= 0}">
                <span class="val_opt val_selected js_opt_val" value="${item.key}">${item.value}</span>
            </c:when>
            <c:otherwise>
                <span class="val_opt js_opt_val" value="${item.key}">${item.value}</span>
            </c:otherwise>
        </c:choose>
    </c:forEach>
</div>
<div style="text-align: center" class="mart10">
    <button type="button" class="btn btn-pri js_cfm_con">确定</button>
    <button type="button" class="btn btn-opt marl10 js_cle_con">取消</button>
</div>



