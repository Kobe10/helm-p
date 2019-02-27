<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="mar5" style="vertical-align: top;">
    <%--左侧导航树--%>
    <div style="width: 280px;float: left;position: relative;" id="pj001TreeDiv" layoutH="15"
         class="panel left_menu">
        <h1>
            <span class="panel_title">公司项目</span>
            <oframe:op prjCd="${param.prjCd}" template="span" cssClass="panel_menu js_reload"
                       name="新建项目" rhtCd="add_prj_rht"
                       onClick="pj001.prjInfo('');"/>
        </h1>
        <ul id="pj001Tree" layoutH="65" class="ztree"></ul>
    </div>
    <div class="split_line" layoutH="15">
        <span class="split_but" title="展开/隐藏导航"></span>
    </div>
    <%--右侧自定义画板--%>
    <div id="pj001RightDiv" style="margin-left: 290px;margin-right: 5px;position: relative;">

    </div>
</div>
<oframe:script src="${pageContext.request.contextPath}/eland/pj/pj001/js/pj001.js" type="text/javascript"/>