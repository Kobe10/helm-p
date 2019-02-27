<%@ page import="com.shfb.oframe.core.util.common.StringUtil" %>
<%@ page import="com.shfb.oframe.core.util.common.XmlBean" %>
<%@ page import="com.shfb.oframe.core.util.common.XmlNode" %>
<%@ page import="com.shfb.oframe.core.util.common.DateUtil" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<c:forEach items="${resultTitle}" var="title">
    <c:set var="fieldName" value="${title.Result.attrNameEn}"/>
    <c:set var="frontSltRuleView" value="${title.Result.frontSltRuleView}"/>
    <%
        String fieldName = StringUtil.obj2Str(pageContext.getAttribute("fieldName"));
        String frontSltRuleView = StringUtil.obj2Str(pageContext.getAttribute("frontSltRuleView"));
        if ("CODE".equals(frontSltRuleView)) {
            fieldName = fieldName + "_Name";
        }
        if ("SIGN".equals(frontSltRuleView)) {
            fieldName = fieldName + "_Name";
        }
        XmlNode temp = (XmlNode) pageContext.getAttribute("item");
        temp = (XmlNode) temp.getIterator().get("Row");
//        判断是否是跨实体查询。
        if (fieldName.contains(".")) {
            if (temp != null) {
                XmlBean tempBean = new XmlBean(temp);
                String value = tempBean.getStrValue(fieldName);
                pageContext.setAttribute("frontSltRuleView", frontSltRuleView);
                pageContext.setAttribute("value", value);
            }
        } else {
            String value = StringUtil.obj2Str(temp.get(fieldName));
            pageContext.setAttribute("frontSltRuleView", frontSltRuleView);
            pageContext.setAttribute("value", value);
        }
    %>
    <c:choose>
        <c:when test="${'MONEY' == frontSltRuleView}">
            <td class="noBreak"><oframe:money value="${value}" format="number"/></td>
        </c:when>
        <c:when test="${'STAFF' == frontSltRuleView}">
            <td class="noBreak"><oframe:staff staffId="${value}"/></td>
        </c:when>
        <c:when test="${'ORG' == frontSltRuleView}">
            <td class="noBreak"><oframe:org orgId="${value}" prjCd="${param.prjCd}"/></td>
        </c:when>
        <c:when test="${'DATE' == frontSltRuleView}">
            <td class="noBreak">
                <c:set var="tempDateFormat" value="${title.Result.frontSltRuleViewParam}"/>
                <c:choose>
                    <c:when test="${tempDateFormat == 'yyyy-MM-dd HH:mm:ss.SSS'}">
                        <%
                            String tempDateFormat = StringUtil.obj2Str(pageContext.getAttribute("tempDateFormat"));
                            String value = StringUtil.obj2Str(pageContext.getAttribute("value"));
                            if (StringUtil.isNotEmptyOrNull(value)) {
                                value = DateUtil.format(DateUtil.parse(value, "yyyyMMddHHmmssSSS"), tempDateFormat);
                            }
                            out.print(value);
                        %>
                    </c:when>
                    <c:otherwise>
                        <oframe:date value="${value}" format="${title.Result.frontSltRuleViewParam}"/>
                    </c:otherwise>
                </c:choose>
            </td>
        </c:when>
        <c:otherwise>
            <td class="noBreak">${value}</td>
        </c:otherwise>
    </c:choose>
</c:forEach>
