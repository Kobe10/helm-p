<%--项目基本信息--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<input type="hidden" name="editAble" value="1"/>
<input type="hidden" name="addAble" value="1"/>
<input type="hidden" name="delAble" value="1"/>
<%--左侧导航树--%>
<div style="width: 280px;float: left;position: relative;"layoutH="52"
     class="panel">
    <h1>项目组织</h1>
    <ul id="pj002OrgTree" layoutH="150" class="ztree"></ul>
</div>
<%--右侧自定义画板--%>
<div id="pj002OrgStaffInfoDiv" style="margin-left: 290px;position: relative;">

</div>
<script type="text/javascript">
    pj002.orgFullTree();
</script>