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
    <div id="result" layoutH="380">
        <c:forEach items="${returnList}" var="item" varStatus="varStatus">
            <ul class="mart5" style="height: 80px; overflow: hidden">
                <li>
                    <div style="background-color: rgba(145, 196, 228, 0.12); line-height: 25px; border: 5px 0 5px 0;"
                         title="谈话人：${item.Row.doRecordPerson},被谈话人：${item.Row.recordToPerson}, 谈话开始时间：${item.Row.startTime},谈话结束时间：${item.Row.endTime}">
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
                                                       onClick="ph00314.deleteRecord('${item.Row.recordId}'); stopEvent(event);"/>
                                            <oframe:op prjCd="${param.prjCd}" template="span" cssClass="link marr5"
                                                       name="编辑" rhtCd="hsrd_normal_rht"
                                                       onClick="ph00314.editRecord('${item.Row.recordId}'); stopEvent(event);"/>
                                        </c:when>
                                        <c:otherwise>
                                            <oframe:op prjCd="${param.prjCd}" template="span" cssClass="link marr5"
                                                       name="删除" rhtCd="hsrd_super_rht"
                                                       onClick="ph00314.deleteRecord('${item.Row.recordId}'); stopEvent(event);"/>
                                            <oframe:op prjCd="${param.prjCd}" template="span" cssClass="link marr5"
                                                       name="编辑" rhtCd="hsrd_super_rht"
                                                       onClick="ph00314.editRecord('${item.Row.recordId}'); stopEvent(event);"/>
                                        </c:otherwise>
                                    </c:choose>
                                </c:when>
                                <c:otherwise>
                                    <oframe:op prjCd="${param.prjCd}" template="span" cssClass="link marr5"
                                               name="删除" rhtCd="hsrd_super_rht"
                                               onClick="ph00314.deleteRecord('${item.Row.recordId}'); stopEvent(event);"/>
                                    <oframe:op prjCd="${param.prjCd}" template="span" cssClass="link marr5"
                                               name="编辑" rhtCd="hsrd_super_rht"
                                               onClick="ph00314.editRecord('${item.Row.recordId}'); stopEvent(event);"/>
                                </c:otherwise>
                            </c:choose>
                            <span class="link marr5"
                                  onclick="ph00314.viewRecord('${item.Row.recordId}'); stopEvent(event);">[详情]</span>
                        </span>
                    </div>
                    <div class="padl10 padr10" style="line-height: 25px;">
                            ${item.Row.recordContext}
                    </div>
                </li>
            </ul>
        </c:forEach>
    </div>
    <jsp:include page="/oframe/common/page/page_fotter.jsp">
        <jsp:param name="showPages" value="true"/>
    </jsp:include>
</div>