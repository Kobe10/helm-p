<%@ page import="com.shfb.oframe.core.util.common.XmlNode" %>
<%@ page import="com.shfb.oframe.core.util.common.StringUtil" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<c:forEach items="${resultTitle}" var="title">
  <c:set var="fieldName" value="${title.Result.attrNameEn}"/>
  <c:set var="frontSltRule" value="${title.Result.frontSltRule}"/>
  <%
    String fieldName = StringUtil.obj2Str(pageContext.getAttribute("fieldName"));
    String frontSltRule = StringUtil.obj2Str(pageContext.getAttribute("frontSltRule"));
    if (StringUtil.isNotEmptyOrNull(frontSltRule)) {
      fieldName = fieldName + "_Name";
    }
    XmlNode temp = (XmlNode) pageContext.getAttribute("item");
    temp = (XmlNode) temp.getIterator().get("Row");
    String value = StringUtil.obj2Str(temp.get(fieldName));
  %>
  <td><%= value%>
  </td>
</c:forEach>
