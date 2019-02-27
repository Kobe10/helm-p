<%--数据类型检索条件，自定义--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%--数据过滤类型(数据区间过滤)--%>
<input type="hidden" class="js_condType" value="num">
<div class="triangle"></div>
<div class="clearfix">
    <span class="title">过滤条件：</span>
    <input style="width: 80px;" value="${startValue}" class="textInput js_start_value number">
    -
    <input style="width: 80px;" value="${endValue}" class="textInput js_end_value number">
</div>
<div style="text-align: center" class="mart10">
    <button type="button" class="btn btn-pri js_cfm_con">确定</button>
    <button type="button" class="btn btn-opt marl10 js_cle_con">取消</button>
</div>