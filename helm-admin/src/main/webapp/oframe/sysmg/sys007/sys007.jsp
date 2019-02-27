<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div>
    <oframe:power prjCd="${param.prjCd}" rhtCd="EDIT_CMP_ROLE_RHT">
        <input type="hidden" name="editAble" value="1"/>
    </oframe:power>
    <oframe:power prjCd="${param.prjCd}" rhtCd="ADD_CMP_ROLE_RHT">
        <input type="hidden" name="addAble" value="1"/>
    </oframe:power>
    <oframe:power prjCd="${param.prjCd}" rhtCd="DEL_CMP_ROLE_RHT">
        <input type="hidden" name="delAble" value="1"/>
    </oframe:power>
    <div class="mar5" style="vertical-align: top;">
        <%--左侧导航树--%>
        <div class="tabs left_menu" style="width: 280px;float: left;position: relative;" id="sys007LeftDiv" layoutH="15">
            <div class="tabsHeader">
                <div class="tabsHeaderContent">
                    <ul id="sys007leftNavUl">
                        <%--<li onclick="sys007.switchTree(1)">--%>
                        <li onclick="sys007.initFullRoleTree()">
                            <a href="javascript:void(0);"><span>系统角色</span></a>
                        </li>
                        <%--<li onclick="sys007.switchTree(2)">--%>
                        <li onclick="sys007.initFullStaffTree()">
                            <a href="javascript:void(0);"><span>系统员工</span></a>
                        </li>
                        <span onclick="sys007.extendOrClose(this);" class="panel_menu js_reload cursorpt" style="float: right;margin: 6px">展开</span>
                    </ul>
                </div>
            </div>
            <div class="tabsContent" id="sys007leftNavContext">
                <ul id="roleTreeContext" layoutH="65" class="ztree"></ul>
                <ul id="sys007StaffTree" layoutH="65" class="ztree hidden"></ul>
            </div>
        </div>

        <div class="split_line" layoutH="15">
            <span class="split_but" title="展开/隐藏导航"></span>
        </div>
        <%--右侧自定义画板--%>
        <div id="sys007RightDiv" style="margin-left: 290px;margin-right: 5px;position: relative;">
        </div>
    </div>
</div>
<oframe:script src="${pageContext.request.contextPath}/oframe/sysmg/sys007/js/sys007.js" type="text/javascript"/>
<oframe:script src="${pageContext.request.contextPath}/oframe/sysmg/sys002/js/sys002.js" type="text/javascript"/>