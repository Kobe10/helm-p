<%--完成退租--处理任务--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<form id="ph00710Frm">
    <table width="100%" class="border" style="text-align: center">
        <tr>
            <td width="10%"><b>序号</b></td>
            <td><b>产权人</b></td>
            <td><b>房屋性质</b></td>
            <td><b>建筑面积</b></td>
            <td><b>公房年租金</b></td>
            <td><b>公房过户费用</b></td>
            <td><b>操作</b></td>
        </tr>
        <c:forEach items="${hsInfoList}" var="item" varStatus="varStatus">
            <tr>
                <td>${varStatus.index+1}</td>
                <td>${item.Row.hsOwnerPersons}</td>
                <td><oframe:name prjCd="${param.prjCd}" itemCd="HS_OWNER_TYPE" value="${item.Row.hsOwnerType}"/></td>
                <td>${item.Row.hsBuildSize}</td>

                <c:set var="hsId" value="${item.Row.hsId}${''}"/>
                <c:choose>
                    <c:when test="${item.Row.hsOwnerType == '0'}">
                        <%--私房--%>
                        <td>无</td>
                        <td>无</td>
                        <td><span class="btn">无</span></td>
                    </c:when>
                    <c:otherwise>
                        <%--公房--%>
                        <td class="js_hsPubZj">${item.Row.hsPubZj}</td>
                        <td class="js_hsPubGhf">${item.Row.hsPubGhf}</td>
                        <td><span class="btn" style="color: #0000ff;" onclick="ph007.openWctz('${hsId}', this);">处理退租手续</span></td>
                    </c:otherwise>
                </c:choose>
            </tr>
        </c:forEach>
        <tr>
            <th>租金及过户费用明细表：</th>
            <td colspan="6" style="text-align: left"><span style="color: #0000ff; cursor: pointer;" onclick="ph002Op.exportZjGhMx('${buildId}')">导出租金及过户费明细表</span></td>
        </tr>
        <jsp:include page="/eland/ph/ph007/ph007-nextAssignee.gv">
            <jsp:param name="tdNum" value="6"/>
        </jsp:include>
        <tr style="text-align: center;">
            <td colspan="7">
            <span class="btn-primary marl10"
                  style="padding: 5px 10px 5px 10px; line-height: 34px; cursor:pointer;"
                  onclick="ph007.submitVerifyassignee('ph00710Frm', this, {})">提交</span>
            </td>
        </tr>
    </table>
</form>