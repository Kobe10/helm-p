<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.shfb.oframe.core.util.common.*" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    Map<String, BigDecimal> summaryResult = new HashMap<String, BigDecimal>();
%>
<%--初始化汇总结果--%>
<c:forEach items="${resultTitle}" var="title">
    <c:set var="fieldName" value="${title.Result.attrNameEn}"/>
    <c:set var="isNumber" value="${title.Result.isNumber}"/>
    <%
        String fieldName = StringUtil.obj2Str(pageContext.getAttribute("fieldName"));
        String isNumber = StringUtil.obj2Str(pageContext.getAttribute("isNumber"));
        if ("true".equals(isNumber)) {
            summaryResult.put(fieldName, new BigDecimal(0));
        }
    %>
</c:forEach>
<%--累计计算结果--%>
<c:forEach items="${returnList}" var="item" varStatus="varStatus">
    <%
        XmlNode temp = (XmlNode) pageContext.getAttribute("item");
        temp = (XmlNode) temp.getIterator().get("Row");
        for (Map.Entry<String, BigDecimal> summaryItem : summaryResult.entrySet()) {
            BigDecimal fieldSum = summaryItem.getValue();
            if (fieldSum == null) {
                continue;
            }
            String fieldName = summaryItem.getKey();
            String itemValue = null;
            // 判断是否是跨实体查询。
            if (fieldName.contains(".")) {
                if (temp != null) {
                    XmlBean tempBean = new XmlBean(temp);
                    itemValue = tempBean.getStrValue(fieldName);
                }
            } else {
                itemValue = StringUtil.obj2Str(temp.get(fieldName));
            }
            if (StringUtil.isNumeric(itemValue)) {
                fieldSum = fieldSum.add(new BigDecimal(itemValue));
            }
            summaryResult.put(fieldName, fieldSum);
        }
    %>
</c:forEach>
<%--输出计算结果--%>
<c:forEach items="${resultTitle}" var="title">
    <c:set var="fieldName" value="${title.Result.attrNameEn}"/>
    <c:set var="frontSltRuleView" value="${title.Result.frontSltRuleView}"/>
    <%
        String fieldName = StringUtil.obj2Str(pageContext.getAttribute("fieldName"));
        String frontSltRuleView = StringUtil.obj2Str(pageContext.getAttribute("frontSltRuleView"));
        BigDecimal itemSum = summaryResult.get(fieldName);
        pageContext.setAttribute("value", itemSum);
        if (summaryResult.get(fieldName) == null) {
    %>
    <td>-</td>
    <%} else if ("MONEY".equals(frontSltRuleView)) {%>
    <td class="noBreak"><oframe:money value="${value}" format="number"/></td>
    <% } else {%>
    <td>${value}</td>
    <%}%>
</c:forEach>

