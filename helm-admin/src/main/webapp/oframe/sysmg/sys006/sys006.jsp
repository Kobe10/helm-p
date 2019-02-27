<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div>
    <oframe:power prjCd="${param.prjCd}" rhtCd="ADD_CMP_RHT_RHT">
        <input type="hidden" name="editAble" value="1"/>
    </oframe:power>
    <oframe:power prjCd="${param.prjCd}" rhtCd="EDIT_CMP_RHT_RHT">
        <input type="hidden" name="addAble" value="1"/>
    </oframe:power>
    <oframe:power prjCd="${param.prjCd}" rhtCd="DEL_CMP_RHT_RHT">
        <input type="hidden" name="delAble" value="1"/>
    </oframe:power>

    <div class="mar5" style="vertical-align: top;">
        <%--左侧导航树--%>
        <div style="width: 280px;float: left;position: relative;" id="sys006DivTree" layoutH="15"
             class="panel left_menu">
            <c:choose>
                <c:when test="${rhtType == null || rhtType == ''}">
                    <h1>系统资源</h1>
                    <ul class="blueBox">
                        <oframe:select prjCd="${param.prjCd}" itemCd="RHT_TYPE" style="width:100%;"
                                       onChange="sys006.initFullTree();" name="sys006RhtType"/>
                    </ul>
                </c:when>
                <c:otherwise>
                    <h1><oframe:name prjCd="${param.prjCd}" itemCd="RHT_TYPE" value="${rhtType}"/></h1>
                    <input type="hidden" name="sys006RhtType" value="${rhtType}">
                </c:otherwise>
            </c:choose>
            <ul id="rhtTree" layoutH="100" class="ztree"></ul>
        </div>
        <div class="split_line" layoutH="15">
            <span class="split_but" title="展开/隐藏导航"></span>
        </div>
        <%--右侧自定义画板--%>
        <div id="rhtRightContent" style="margin-left: 290px;margin-right: 5px;position: relative;">
        </div>
    </div>
</div>
<oframe:script src="${pageContext.request.contextPath}/oframe/sysmg/sys006/js/sys006.js" type="text/javascript"/>