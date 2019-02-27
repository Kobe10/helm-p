<%--自定义任务--处理任务--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<form id="ph00703Frm">
    <table width="100%" class="border" style="text-align: center">
        <tr>
            <td width="10%"><b>序号</b></td>
            <td width="10%"><b>产权人</b></td>
            <td width="10%"><b>房屋性质</b></td>
            <td width="10%"><b>建筑面积</b></td>
            <td width="10%"><b>预审结果</b></td>
            <td width="25%"><b>操作</b></td>
        </tr>
        <c:forEach items="${hsInfoList}" var="item" varStatus="varStatus">
            <tr>
                <td>${varStatus.index+1}</td>
                <td>${item.Row.hsOwnerPersons}</td>
                <td><oframe:name prjCd="${param.prjCd}" itemCd="HS_OWNER_TYPE" value="${item.Row.hsOwnerType}"/></td>
                <td>${item.Row.hsBuildSize}</td>
                <c:set var="hsId" value="${item.Row.hsId}${''}"/>
                <td class="js_result"><oframe:name prjCd="${param.prjCd}" itemCd="DISP_RESULT"
                                                   value="${hsShMap[hsId].Row.procResult1}"/></td>
                <td><span class="btn" style="color: #0000ff;"
                          onclick="ph007.openYuShen('${item.Row.hsId}', this);">预审处理</span>
                </td>
            </tr>
        </c:forEach>
        <jsp:include page="/eland/ph/ph007/ph007-nextAssignee.gv">
            <jsp:param name="tdNum" value="6"/>
        </jsp:include>
        <tr style="text-align: center;">
            <td colspan="7">
            <span class="btn-primary marl10 js_submitTask"
                  style="padding: 5px 10px 5px 10px; line-height: 34px; cursor:pointer;"
                  onclick="ph007.submitTask('ph00703Frm',this, {})">提交</span>
            </td>
        </tr>
    </table>
</form>
<oframe:script type="text/javascript" src="${pageContext.request.contextPath}/eland/ph/ph007/js/ph007.js"/>