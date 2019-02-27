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
    <div id="result">
        <c:forEach items="${returnList}" var="item" varStatus="varStatus">
            <ul class="mart5">
                <li>
                    <div style="background-color: #91c4e4; line-height: 25px; border-width: 5px 0 5px 0;">
                        <span class="marl10 marr10" style="display: inline-block; line-height: 25px;">
                            <oframe:staff staffId="${item.publish_stf}"/></span>
                        记录于：<fmt:formatDate value="${item.publish_date_time}" type="both"
                                            pattern="yyyy-MM-dd HH:mm:ss"/>
                    </div>
                    <div class="padl10 padr10" style="line-height: 25px;">
                            ${item.record_context}
                    </div>
                    <div style="display: block;text-align: right; line-height: 25px;height: 15px;">
                        <c:choose>
                            <c:when test="${loginStaffId != item.public_stf}">
                                <oframe:op prjCd="${param.prjCd}" template="span" cssClass="link marr5"
                                           name="删除" rhtCd="yjd_super_rht"
                                           onClick="pb002Wm.deleteRecord('${item.record_id}'); stopEvent(event);"/>
                            </c:when>
                            <c:otherwise>
                                <oframe:op prjCd="${param.prjCd}" template="span" cssClass="link marr5"
                                           name="删除" rhtCd="yard_record_rht"
                                           onClick="pb002Wm.deleteRecord('${item.record_id}'); stopEvent(event);"/>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </li>
            </ul>
        </c:forEach>

    </div>
    <jsp:include page="/oframe/common/page/pager_fotter.jsp">
        <jsp:param name="showPages" value="false"/>
    </jsp:include>
</div>