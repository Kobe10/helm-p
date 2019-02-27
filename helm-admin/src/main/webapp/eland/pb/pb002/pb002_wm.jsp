<%--院落信息进度跟踪面板--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="panel">
    <h1>进度记录日志
        <span class="panel_menu js_reload">刷新</span>
    </h1>
    <div>
        <form id="_pb002Wmfrm" method="get">
            <input type="hidden" name="recordRelId" value="${buildId}"/>
            <input type="hidden" name="recordType" value="2"/>
            <input type="hidden" name="forward" id="forward" value="/eland/pb/pb002/pb002_wm_list"/>
            <input type="hidden" name="subSvcName" value="hs018"/>
        </form>
        <div>
            <div id="pb002_wm_w_list_print" class="result"></div>
            <jsp:include page="/oframe/common/page/pager_fotter.jsp">
                <jsp:param name="showPages" value="false"/>
            </jsp:include>
        </div>
    </div>
</div>
<oframe:script src="${pageContext.request.contextPath}/eland/pb/pb002/js/pb002_wm.js" type="text/javascript"/>