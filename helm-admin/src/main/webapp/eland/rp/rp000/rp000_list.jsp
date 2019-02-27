<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div>
    <c:forEach items="${returnList}" var="item" varStatus="varStatus">
        <span style="display: inline-block;min-width: 200px;">
            <input style="float: none; margin-left: 20px;" type="checkbox" name="reportItem"
                   value="${item.job_cd}" onclick="rp000.changeReportItem(this)">
            <label class="link">
                <a href="#pageChart_${item.job_cd}">${item.job_name}</a>
            </label>
        </span>
    </c:forEach>
    <input type="hidden" name="errMsg" value='${errMsg}'/>
</div>