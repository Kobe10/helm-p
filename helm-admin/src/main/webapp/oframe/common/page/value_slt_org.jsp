<%--文字类型检索条件，自定义--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%--数据过滤类型(文本过滤)--%>
<input type="hidden" class="js_condType" value="org">

<div class="triangle"></div>
<div>
    <span class="title" style="float: left;">过滤条件：</span>
    <span class="text" style="position: relative;">
        <input type="hidden" class="js_org_id" value="${conditionValue}"/>
        <input type="text" class="pull-left readonly js_org_name" style="width: auto;" readonly
               value='<oframe:org orgId="${conditionValue}" property="Org.Node.orgName" prjCd="${param.prjCd}"/>'/>
        <a title="选择" class="btnLook" onclick="$(this).sltOrg(this, {})">选择</a>
    </span>
</div>
<div style="text-align: center" class="mart10">
    <button type="button" class="btn btn-pri js_cfm_con">确定</button>
    <button type="button" class="btn btn-opt marl10 js_cle_con">取消</button>
</div>


