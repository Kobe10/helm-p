<%@ page import="sun.swing.StringUIClientPropertyKey" %>
<%@ page import="com.shfb.oframe.core.util.common.StringUtil" %>
<%@ page import="com.shfb.oframe.core.util.common.XmlNode" %>
<%@ page import="com.shfb.oframe.core.util.common.XmlBean" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div>
    <table class="table" layoutH="167" adjust="true" width="100%">
        <thead>
        <tr>
            <th>被安置人</th>
            <th>房屋地址</th>
        </tr>
        </thead>
        <tbody>
        <%
            String infoKeyPath = request.getParameter("infoKeyPath");
        %>
        <c:forEach items="${returnList}" var="item" varStatus="varStatus">
            <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}"/>
            <%
                String infoKey = "";
                if (StringUtil.isNotEmptyOrNull(infoKeyPath)) {
                    XmlNode temp = (XmlNode) pageContext.getAttribute("item");
                    temp = (XmlNode) temp.getIterator().get("Row");
                    XmlBean tempBean = new XmlBean(temp);
                    infoKey = tempBean.getStrValue(infoKeyPath);
                }
            %>
            <tr class="${tr_class} js_hs_item"
                onclick="form.info('${item.Row.HouseInfo.hsId}',{})"
                style="cursor: pointer;"
                id="tableTr">
                <td class="noBreak" title="${item.Row.HouseInfo.hsOwnerPersons}">
                        ${item.Row.HouseInfo.hsOwnerPersons}
                </td>
                <td class="noBreak" title="${item.Row.HouseInfo.hsOwnerPersons}">
                        ${item.Row.HouseInfo.hsFullAddr}
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <jsp:include page="/oframe/common/page/pager_fotter_simple.jsp"/>
</div>