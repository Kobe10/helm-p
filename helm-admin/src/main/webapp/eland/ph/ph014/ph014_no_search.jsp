<%--没有检索框的居民信息界面 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<oframe:script src="${pageContext.request.contextPath}/eland/ph/ph014/js/ph014.js" type="text/javascript"/>
<oframe:script src="${pageContext.request.contextPath}/oframe/plugin/json/json-patch.min.js" type="text/javascript"/>
<oframe:script src="${pageContext.request.contextPath}/oframe/plugin/json/json-patch-duplex.js" type="text/javascript"/>
<div class="${param.mar =='true'? "mar5": ''}" style="vertical-align: top;">
    <input type="hidden" name="formCd" value="${formCd}"/>
    <input type="hidden" name="withFrame" value="${withFrame}"/>
    <input type="hidden" name="showPrevNext" value="false"/>
    <%--居民信息展示界面--%>
    <div id="ph014Right" style="position: relative;">
        <div class="form_rpt_data" id="ph014Context">
            <jsp:include page="/eland/ph/ph014/ph014-info.gv">
                <jsp:param name="hsId" value="${param.hsId}"/>
                <jsp:param name="keyId" value="${keyId}"/>
                <jsp:param name="formCd" value="${formCd}"/>
                <jsp:param name="withFrame" value="${withFrame}"/>
                <jsp:param name="showPrevNext" value="false"/>
                <jsp:param name="fromEdit" value="true"/>
            </jsp:include>
        </div>
    </div>
</div>
