<%--院落信息进度跟踪面板--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="panel">
    <h1>进度记录日志
        <span class="panel_menu js_reload">刷新</span>
    </h1>
    <div>
        <%--<form id="_ph00209frm_spat" method="get">
            <input type="hidden" name="relType" value="2"/>
            <input type="hidden" name="relObjectId" value="${buildId}"/>
            <div style="height: 100px; margin-bottom: 15px;">
                <div style="display: inline;width: 90%; height: 100px; float: left;margin-right: 50px;">
                    <textarea id="talkContext"
                              class="required"
                              name="talkContext" style="width: 100%; height: 100%;"></textarea>
                </div>
                <div style="display: inline-block;height: 100px; float: left;">
                    <button class="btn btn-primary marl5"
                            type="button"
                            style="padding: 40px 10px 40px 10px; margin-right: 5px;"
                            onclick="ph00209.submitMemo(); ">提交
                    </button>
                </div>
            </div>
        </form>--%>
        <form id="_ph00209frm" method="get">
            <input type="hidden" name="recordRelId" value="${buildId}"/>
            <input type="hidden" name="recordType" value="2"/>
            <input type="hidden" name="forward" id="forward" value="/eland/ph/ph002/ph00209_list"/>
            <input type="hidden" name="subSvcName" value="hs018"/>
        </form>
        <div>
            <div id="ph00209_w_list_print" class="result"></div>
            <jsp:include page="/oframe/common/page/pager_fotter.jsp">
                <jsp:param name="showPages" value="false"/>
            </jsp:include>
        </div>
    </div>
</div>
<oframe:script src="${pageContext.request.contextPath}/eland/ph/ph002/js/ph00209.js" type="text/javascript"/>