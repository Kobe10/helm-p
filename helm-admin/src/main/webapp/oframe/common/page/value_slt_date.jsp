<%--日期类型检索条件，自定义--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%--数据过滤类型(日期区间过滤)--%>
<input type="hidden" class="js_condType" value="date">

<div class="triangle"></div>
<div class="clearfix">
    <span class="title">过滤条件：</span>
    <input style="width: auto;" datefmt="${dateFormat}" value="${startValue}" class="textInput js_start_value date">
    -
    <input style="width: auto;" datefmt="${dateFormat}" value="${endValue}" class="textInput js_end_value date">
</div>
<div style="text-align: center" class="mart10">
    <button type="button" class="btn btn-pri js_cfm_con">确定</button>
    <button type="button" class="btn btn-opt marl10 js_cle_con">取消</button>
</div>


