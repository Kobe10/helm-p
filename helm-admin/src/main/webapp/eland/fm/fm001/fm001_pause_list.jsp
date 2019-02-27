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
            <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${returnList}" var="item" varStatus="varStatus">
            <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}" />
            <tr class="${tr_class}">
                <td title='<oframe:name itemCd="FM_STATUS" value="${item.Row.HouseInfo.HsFmInfo.fmStatus}" />'>
                    <input type="checkbox" group="ids" name="hsFmId"
                           value="${item.Row.HouseInfo.HsFmInfo.hsFmId}" /></td>
                <%@include file="/oframe/common/query/list_hdt.jsp" %>
                <td>
                    <c:if test="${item.Row.HouseInfo.HsFmInfo.fmStatus == '11'}">
                        <oframe:op prjCd="${param.prjCd}" template="span" cssClass="link btnView"
                                   name="暂缓制折" rhtCd="fm_pause_make"
                                   onClick="fm001.pauseOrCancelMake('${item.Row.HouseInfo.HsFmInfo.hsFmId}', '0');"/>
                    </c:if>
                    <c:if test="${item.Row.HouseInfo.HsFmInfo.fmStatus == '10'}">
                        <oframe:op prjCd="${param.prjCd}" template="span" cssClass="link btnView"
                                   name="取消暂缓" rhtCd="fm_cancel_make"
                                   onClick="fm001.pauseOrCancelMake('${item.Row.HouseInfo.HsFmInfo.hsFmId}', '1');"/>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <jsp:include page="/oframe/common/page/page_fotter.jsp" />
</div>