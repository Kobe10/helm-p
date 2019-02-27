<%--
  Created by IntelliJ IDEA.
  User: Dennis
  Date: 2014/11/3 0003
  Time: 17:31
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="pageNav">
    <a href="javascript:void(0);">成本管理</a>--->
    <a class="link" href="javascript:void(0)">外聘成本管理</a>
</div>
<oframe:power prjCd="${param.prjCd}" rhtCd="edit_3_reg_rht">
    <input type="hidden" name="editAble" value="1"/>
</oframe:power>
<oframe:power prjCd="${param.prjCd}" rhtCd="del_3_reg_rht">
    <input type="hidden" name="removeAble" value="1"/>
</oframe:power>
<oframe:power prjCd="${param.prjCd}" rhtCd="move_3_reg_rht">
    <input type="hidden" name="moveAble" value="1"/>
</oframe:power>
<div style="margin: 5px">
    <div id="pj016TreePanel" style="width: 240px;float: left;" layoutH="15"
         class="panel left_menu">
        <h1>
            <span>目 录 导 航</span>
        </h1>
        <ul id="pj016Tree" class="ztree" layoutH="85"></ul>
    </div>
    <div class="split_line" onm layoutH="15">
        <span class="split_but" title="展开/隐藏导航"></span>
    </div>
    <div id="pj016Context" style="margin-left: 250px;"></div>
</div>
<oframe:script src="${pageContext.request.contextPath}/eland/pj/pj016/js/pj016.js" type="text/javascript"/>

