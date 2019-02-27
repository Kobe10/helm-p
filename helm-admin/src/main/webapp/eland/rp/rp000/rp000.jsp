<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div id="reportDivContainer">
    <table class="form">
        <input type="hidden" name="prjOrgId" value="10">
        <tr>
            <th>
                <label>统计区间:</label>
            </th>
            <td>
                <input type="text" onchange="rp000.refreshAll();"
                       datefmt="yyyyMMdd"
                       class="date" name="startTime" value="${startTime}">
                -
                <input type="text" onchange="rp000.refreshAll();"
                       datefmt="yyyyMMdd" class="date" name="endTime" value="${endTime}">
            </td>
            <th>
                <label>统计维度：</label>
            </th>
            <td>
                <input type="text" name="srcJobName">
                <button type="button" class="btn btn-info" onclick="rp000.searchJob()">检索</button>
            </td>
        </tr>
        <tr>
            <th>
                <label>可选指标：</label><br/>
            </th>
            <td colspan="4">
                <div class="js-job-cd-list"></div>
                <span class="link mar5" onclick="rp000.removeAll();">[取消]</span>
                <span class="link" onclick="rp000.addAll();">[全选]</span>
            </td>
        </tr>
        <tr>
            <th>
                <label>已选指标：</label>
            </th>
            <td colspan="4">
                <div class="js-slt-item"></div>
            </td>
        </tr>
    </table>
    <div id="reportDiv"
         style="border-top: 1px darkcyan dotted;vertical-align: bottom;min-height: 300px;">
    </div>
</div>
<oframe:script src="${pageContext.request.contextPath}/eland/rp/rp000/js/rp000.js" type="text/javascript"/>