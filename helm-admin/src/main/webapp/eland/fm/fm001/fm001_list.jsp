<%--
  Created by IntelliJ IDEA.
  User: shfb_wang
  Date: 2016/3/22 0022 17:33
  Copyright(c) 北京四海富博计算机服务有限公司
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div id="fm001_submit_result" class="pageContent" style="padding: 0">
    <table class="table" width="100%" layoutH="165">
        <thead>
        <tr>
            <th width="3%"><input type="checkbox" class="checkboxCtrl" group="ids" /></th>
            <%@include file="/oframe/common/query/list_htr.jsp" %>
            <c:if test="${param.divId == 'fm001_submit_page_data'}">
                <th>操作</th>
            </c:if>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${returnList}" var="item" varStatus="varStatus">
            <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}" />
            <%--<c:set var="disableSubmit" value="" />--%>
            <%--<c:if test="${item.Row.HouseInfo.HsFmInfo.fmStatus == '12'}">--%>
            <%--<c:set var="disableSubmit" value="disabled" />--%>
            <%--</c:if>--%>
            <tr class="${tr_class}">
                <td title='<oframe:name itemCd="FM_STATUS" value="${item.Row.HouseInfo.HsFmInfo.fmStatus}" />'>
                    <input type="checkbox" ${disableSubmit} group="ids" name="hsFmId"
                           value="${item.Row.HouseInfo.HsFmInfo.hsFmId}" /></td>
                <%@include file="/oframe/common/query/list_hdt.jsp" %>
                <c:if test="${param.divId == 'fm001_submit_page_data'}">
                    <td>
                        <%--<c:if test="${item.Row.HouseInfo.HsFmInfo.fmStatus == '13'}">--%>
                            <%--<span class="link btnView"--%>
                                  <%--onclick="fm001.receiveBankbook('${item.Row.HouseInfo.hsId}','${item.Row.HouseInfo.HsFmInfo.hsFmId}');">--%>
                                <%--[领取存折]</span>--%>
                        <%--</c:if>--%>
                            <span class="link marl10" onclick="fm001.viewWld('${item.Row.HouseInfo.HsFmInfo.hsId}','${item.Row.HouseInfo.HsFmInfo.hsCtId}','${item.Row.HouseInfo.HsFmInfo.hsFmId}')">五联单</span>
                            <c:if test="${item.Row.HouseInfo.HsFmInfo.fmStatus == '8'}">
                                <span class="link marl10"
                                      onclick="fm001.revocationView('${item.Row.HouseInfo.HsFmInfo.hsFmId}');">撤回</span>
                            </c:if>
                    </td>
                </c:if>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <jsp:include page="/oframe/common/page/page_fotter.jsp" />
</div>