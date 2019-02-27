<%--文字类型检索条件，自定义--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%--数据过滤类型(文本过滤)--%>
<input type="hidden" class="js_condType" value="text">
<div class="triangle"></div>
<div>
    <span class="title">过滤条件：</span>
    <oframe:select prjCd="${param.prjCd}" itemCd="QUERY_CONDITION" cssClass="js_text_cond" value="${condition}" style="width: auto;"/>
    <input style="width: auto;" value="${conditionValue}" class="textInput js_text_cond_value">
</div>
<div style="text-align: center" class="mart10">
    <button type="button" class="btn btn-pri js_cfm_con">确定</button>
    <button type="button" class="btn btn-opt marl10 js_cle_con">取消</button>
</div>


