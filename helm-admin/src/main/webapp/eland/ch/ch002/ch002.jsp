<%--
  Created by IntelliJ IDEA.
  User: shfb_wang
  Date: 2015/11/10 0010 14:52
  Copyright(c) 北京四海富博计算机服务有限公司
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<oframe:script src="${pageContext.request.contextPath}/eland/ch/ch002/js/ch002.js" type="text/javascript"/>
<div class="panelBar">
    <ul class="toolBar">
        <li><a class="save" onclick="ch002.saveChooseHsInfo()"><span>保存</span></a></li>
        <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
    </ul>
</div>
<div>
    <table class="border" width="100%">
        <c:set var="hsOwnerType" value="${hsOwnerBean.HouseInfo.hsOwnerType}${''}"/>
        <tr>
            <th width="12%">房屋地址：</th>
            <td width="21%">${oldHsBean.HouseInfo.hsFullAddr}
                <input type="hidden" name="hsFullAddr" value="${oldHsBean.HouseInfo.hsFullAddr}"/>
            </td>
            <th width="12%">房屋产别：</th>
            <td width="21%"><oframe:name prjCd="${param.prjCd}" itemCd="HS_OWNER_TYPE" value="${hsOwnerType}"/></td>
            <th width="12%">建筑面积：</th>
            <td width="21%">${hsOwnerBean.HouseInfo.hsBuildSize}
                <input type="hidden" name="hsBuildSize" value="${hsOwnerBean.HouseInfo.hsBuildSize}"/>
            </td>
        </tr>
        <tr>
            <c:choose>
                <c:when test="${hsOwnerType >= '100' && hsOwnerType < '200'}">
                    <th>产权人：</th>
                    <td>${hsOwnerBean.HouseInfo.hsOwnerPersons}</td>
                    <th>产权证号：</th>
                    <td>${ownerCerty}</td>
                </c:when>
                <c:otherwise>
                    <th>承租人：</th>
                    <td>${hsOwnerBean.HouseInfo.hsOwnerPersons}</td>
                    <th>租赁合同号：</th>
                    <td>${hsOwnerBean.HouseInfo.HsOwners.HsOwner[0].ownerCerty}</td>
                </c:otherwise>
            </c:choose>
            <th>使用面积：</th>
            <td>${hsOwnerBean.HouseInfo.hsUseSize}</td>
        </tr>
    </table>
</div>
<div id="js_all_choose_hs_div" class="mart5">
    <h1 class="padl10" style="line-height: 32px; background-color: #e3eef4;">购房信息
        <span style="float: right;line-height: 32;" class="marr10 mart5">
            <button type="button" class="btn btn-more" btnContainer="dialog">
                <i style="font-style:normal;">+购房</i>
                <span class="caret"></span>
                <ul class="menu hidden">
                    <c:forEach items="${newHsArea}" var="item">
                        <li onclick="ch002.addNewHsArea('${item.regId}')">${item.regName}</li>
                    </c:forEach>
                </ul>
            </button>
        </span>
    </h1>
    <input type="hidden" name="hidHsId" value="${hsId}"/>
    <input type="hidden" name="hidHsCtId" value="${ctInfo.HsCtInfo.hsCtId}"/>
    <c:forEach items="${chooseRegMap}" var="item">
        <jsp:include page="/eland/ch/ch002/ch002-initNewHsArea.gv?regId=${item.key}"/>
    </c:forEach>
</div>
