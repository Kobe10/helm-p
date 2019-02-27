<%--交房处理界面--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div style="position: relative;" class="js_page_data" fromEdit="${param.fromEdit}">
    <input type="hidden" name="hsId" value="${param.hsId}">
    <input type="hidden" name="formCd" value="${param.formCd}">
    <input type="hidden" name="infoKey" value="${param.infoKey}">
    <%--操作按钮区--%>
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
            <c:if test="${showPrevNext == 'true'}">
                <li onclick="ph014.singleQuery('${hsId}','next');" style="float: right">
                    <a class="export" href="javascript:void(0)"><span>下一户</span></a>
                </li>
                <li onclick="ph014.singleQuery('${hsId}','last');" style="float: right">
                    <a class="import" href="javascript:void(0)"><span>上一户</span></a>
                </li>
            </c:if>
        </ul>
    </div>
    <%--页面展示区--%>
    <div layoutH="55" class="form_rpt_data" style="border:1px solid #e9e9e9;">
        ${article}
    </div>
</div>
<script type="text/javascript">
    <%--调用 js 区--%>
    ${operDefCode}
    <%--初始化加载 附件--%>
    $(document).ready(function () {
        form.syncDocInfo();
    });
</script>