<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div>
    <input type="hidden" name="editAble" value="1"/>
    <input type="hidden" name="addAble" value="1"/>
    <input type="hidden" name="delAble" value="1"/>

    <div class="mar5" style="vertical-align: top;">
        <%--左侧导航树--%>
        <div style="width: 280px;float: left;position: relative;" id="sys004DivTree" layoutH="15"
             class="panel left_menu">
            <h1>
                <span class="panel_title">公司组织</span>
            </h1>

            <ul id="sys004Tree" layoutH="65" class="ztree"></ul>
        </div>
        <div class="split_line" layoutH="15">
            <span class="split_but" title="展开/隐藏导航"></span>
        </div>
        <%--右侧自定义画板--%>
        <div id="sys004001" style="margin-left: 290px;margin-right: 5px;position: relative;">
        </div>
    </div>
</div>
<oframe:script src="${pageContext.request.contextPath}/oframe/sysmg/sys004/js/sys004.js" type="text/javascript"/>