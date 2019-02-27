<%--日统计报表--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="rptDiv ${jobCdType}">
    <table class="form rptTable">

        <tr>
            <td colspan="2" align="center"><b>${jobCdName}</b></td>
        </tr>
        <tr>
            <td align="center">私房</td>
            <td align="center">公房</td>
        </tr>
        <tr>
            <td align="center"></td>
            <td align="center"></td>
        </tr>
        <c:forEach items="${resultList}" var="result" varStatus="index">
            <tr style="background-color: ${colorList[index.index]}">
                <c:forEach items="${result}" var="item">
                    <td align="center">${item}</td>
                </c:forEach>
            </tr>
        </c:forEach>
        <tr style="background-color: #cccccc">
            <td align="center">37</td>
            <td align="center">35</td>
        </tr>
    </table>
</div>
