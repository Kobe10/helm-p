<%--OA资料准备--处理任务--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<form id="ph00705oaFrm">
    <table width="100%" class="border" style="text-align: center">
        <tr>
            <td width="10%"><b>序号</b></td>
            <td><b>产权人</b></td>
            <td><b>房屋性质</b></td>
            <td><b>建筑面积</b></td>
            <td><b>操作</b></td>
        </tr>
        <c:forEach items="${hsInfoList}" var="item" varStatus="varStatus">
            <tr>
                <td>${varStatus.index+1}</td>
                <td>${item.Row.hsOwnerPersons}</td>
                <td><oframe:name prjCd="${param.prjCd}" itemCd="HS_OWNER_TYPE" value="${item.Row.hsOwnerType}"/></td>
                <td>${item.Row.hsBuildSize}</td>

                    <%--hsId方便下面使用，下面很多地方用到--%>
                <c:set var="hsId" value="${item.Row.hsId}${''}"/>
                <td><span class="btn" style="color: #0000ff;" onclick="ph003Op.openOash('${hsId}')">准备OA审核资料</span>
                </td>
            </tr>
        </c:forEach>
        <tr>
            <td>导出准备资料：</td>
            <td colspan="4" style="text-align: left">
                <%--<span class="btn" style="color: #0000ff;cursor:pointer;" onclick="void(0);">导出</span>--%>
                <span class="btn" style="color: #0000ff;cursor:pointer;" onclick="ph002Op.exportYardFj('${buildId}')">导出院落资料</span>
            </td>
        </tr>
        <tr>
            <td>OA审核资料：</td>
            <td colspan="4" style="text-align: left">
                <span class="btn js_doc_info" docTypeName="OA审核资料" relType="200" onclick="ph007.showDoc(this,'${buildId}')">
                    <label style="color: #0000ff;cursor:pointer;">上传</label>
                      <input type="hidden" name="docIds" value='${hiTaskInfo.HiTaskInfo.TskBidVars.docIds}'>
                </span>
                <%--<span class="btn" style="color: #0000ff;cursor:pointer;" onclick="">上传</span>--%>
            </td>
        </tr>
        <jsp:include page="/eland/ph/ph007/ph007-nextAssignee.gv">
            <jsp:param name="tdNum" value="4"/>
        </jsp:include>
        <tr style="text-align: center;">
            <td colspan="5">
            <span class="btn-primary marl10"
                  style="padding: 5px 10px 5px 10px; line-height: 34px; cursor:pointer;"
                  onclick="ph007.submitVerifyassignee('ph00705oaFrm',this, {})">提交</span>
            </td>
        </tr>
    </table>
</form>
<oframe:script type="text/javascript" src="${pageContext.request.contextPath}/eland/ph/ph003/js/ph003_op.js"/>
