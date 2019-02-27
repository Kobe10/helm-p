<%--
  Created by IntelliJ IDEA.
  User: Dennis
  Date: 2014/11/3 0003
  Time: 17:31
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div>
    <div id="result" style="min-height: 360px;">
        <c:forEach items="${returnList}" var="item" varStatus="varStatus">
            <ul class="mart5" style="height: 80px; overflow: hidden">
                <li>
                    <div style="background-color: #EDF6FB; line-height: 25px;">
                        <span class="marl10 marr10" style="display: inline-block; line-height: 25px;">
                            <oframe:staff staffId="${item.Row.publishStf}"/>
                        </span>
                        记录于：<oframe:date value="${item.Row.publishDateTime}" format="yyyy-MM-dd HH:mm:ss"/>
                        <span style="float:right">
                            <c:choose>
                                <c:when test="${loginStaffId == item.Row.publishStf}">
                                    <c:choose>
                                        <c:when test="${tempDate < item.Row.publishDateTime}">
                                            <oframe:op prjCd="${param.prjCd}" template="span" cssClass="link marr5"
                                                       name="删除" rhtCd="hsrd_normal_rht"
                                                       onClick="ph00309.deleteRecord('${item.Row.recordId}'); stopEvent(event);"/>
                                            <oframe:op prjCd="${param.prjCd}" template="span" cssClass="link marr5"
                                                       name="编辑" rhtCd="hsrd_normal_rht"
                                                       onClick="ph00309.editRecord('${item.Row.recordId}'); stopEvent(event);"/>
                                        </c:when>
                                        <c:otherwise>
                                            <oframe:op prjCd="${param.prjCd}" template="span" cssClass="link marr5"
                                                       name="删除" rhtCd="hsrd_super_rht"
                                                       onClick="ph00309.deleteRecord('${item.Row.recordId}'); stopEvent(event);"/>
                                            <oframe:op prjCd="${param.prjCd}" template="span" cssClass="link marr5"
                                                       name="编辑" rhtCd="hsrd_super_rht"
                                                       onClick="ph00309.editRecord('${item.Row.recordId}'); stopEvent(event);"/>
                                        </c:otherwise>
                                    </c:choose>
                                </c:when>
                                <c:otherwise>
                                    <oframe:op prjCd="${param.prjCd}" template="span" cssClass="link marr5"
                                               name="删除" rhtCd="hsrd_super_rht"
                                               onClick="ph00309.deleteRecord('${item.Row.recordId}'); stopEvent(event);"/>
                                    <oframe:op prjCd="${param.prjCd}" template="span" cssClass="link marr5"
                                               name="编辑" rhtCd="hsrd_super_rht"
                                               onClick="ph00309.editRecord('${item.Row.recordId}'); stopEvent(event);"/>
                                </c:otherwise>
                            </c:choose>
                            <span class="link marr5"
                                  onclick="ph00309.viewRecord('${item.Row.recordId}'); stopEvent(event);">[详情]</span>
                        </span>
                    </div>
                    <div class="padl10 padr10" style="line-height: 25px;">
                            ${item.Row.recordContext}
                    </div>

                </li>
            </ul>
        </c:forEach>
    </div>
    <jsp:include page="/oframe/common/page/page_fotter.jsp"/>
</div>