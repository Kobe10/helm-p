<%--代码表类型检索条件，自定义--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%--数据过滤类型(选项过滤)--%>
<style>
    .condition-box {
        margin-top: -10px;
        margin-left: -10px;
        margin-right: -10px;
        border-bottom: 1px solid #999999;
    }

    .condition-box label {
        vertical-align: middle;
        display: inline-block;
        padding-left: 10px;
        padding-top: 5px;
        padding-bottom: 5px;
    }
</style>
<div class="triangle"></div>
<div class="clearfix">
    <div class="condition-box">
        <c:choose>
            <c:when test="${condition == 'NOT IN'}">
                <label><input type="radio" name="condition" value="IN">包含</label>
                <label><input type="radio" name="condition" value="NOT IN" checked>不包含</label>
            </c:when>
            <c:otherwise>
                <label><input type="radio" name="condition" value="IN" checked>包含</label>
                <label><input type="radio" name="condition" value="NOT IN">不包含</label>
            </c:otherwise>
        </c:choose>
    </div>
    <c:forEach items="${values}" var="item">
        <c:choose>
            <c:when test="${fn:indexOf(sltValues,item.key) >= 0}">
                <span class="val_opt val_selected js_tag_opt_val" value="${item.key}">${item.value}</span>
            </c:when>
            <c:otherwise>
                <span class="val_opt js_tag_opt_val" value="${item.key}">${item.value}</span>
            </c:otherwise>
        </c:choose>
    </c:forEach>
</div>
<div style="text-align: center" class="mart10">
    <button type="button" class="btn btn-pri js_tag_cfm_con">确定</button>
    <button type="button" class="btn btn-opt marl10 js_cle_con">取消</button>
</div>



