<%--
  Created by IntelliJ IDEA.
  User: Dennis
  Date: 2014/11/3 0003
  Time: 17:31
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="panel" style="display: block;">
    <h1><span>外聘成本统计</span></h1>

    <table class="border" style="text-align: center">
        <tr>
            <td rowspan="2">单位名称</td>
            <td colspan="4">已支付</td>
            <td colspan="4">待支付</td>
        </tr>
        <tr>
            <td>总户数</td>
            <td>固定服务费</td>
            <td>走户服务费</td>
            <td>总服务费用</td>
            <td>总户数</td>
            <td>固定服务费</td>
            <td>走户服务费</td>
            <td>总服务费用</td>
        </tr>
        <tr>
            <td><oframe:entity prjCd="${param.prjCd}"  entityName="CmpExtCmp" property="extCmpName" value="${extCmpId}"/></td>
            <td>${jobDefBean.JobDefResults.payHsNum}</td>
            <td>${jobDefBean.JobDefResults.unPayHsNum}</td>
            <c:choose>
                <c:when test="${jobDefBean.JobDefResults.payFixSerFee != '' && jobDefBean.JobDefResults.payFixSerFee != null}">
                    <td><oframe:money value="${jobDefBean.JobDefResults.payFixSerFee}" format="number"/></td>
                    <td><oframe:money value="${jobDefBean.JobDefResults.payZhFee}" format="number"/></td>
                    <td><oframe:money value="${jobDefBean.JobDefResults.payTotalFee}" format="number"/></td>
                    <td><oframe:money value="${jobDefBean.JobDefResults.unPayFixSerFee}" format="number"/></td>
                    <td><oframe:money value="${jobDefBean.JobDefResults.unPayZhFee}" format="number"/></td>
                    <td><oframe:money value="${jobDefBean.JobDefResults.unPayTotalFee}" format="number"/></td>
                </c:when>
                <c:otherwise>
                    <td>${jobDefBean.JobDefResults.payFixSerFee}</td>
                    <td>${jobDefBean.JobDefResults.payZhFee}</td>
                    <td>${jobDefBean.JobDefResults.payTotalFee}</td>
                    <td>${jobDefBean.JobDefResults.unPayFixSerFee}</td>
                    <td>${jobDefBean.JobDefResults.unPayZhFee}</td>
                    <td>${jobDefBean.JobDefResults.unPayTotalFee}</td>
                </c:otherwise>
            </c:choose>
        </tr>
    </table>
</div>
<div class="tabs mart5" style="padding: 0">
    <div class="tabsHeader">
        <div class="tabsHeaderContent" style="padding: 0">
            <ul>
                <li class="js_load_tab selected" onclick="pj016.loadTabContext(this);"
                    url="eland/pj/pj016/pj016-initWzf.gv?extCmpId=${extCmpId}">
                    <a href="javascript:"><span>待支付居民</span></a>
                </li>
                <li class="js_load_tab" onclick="pj016.loadTabContext(this);"
                    url="eland/pj/pj016/pj016-initYzf.gv?extCmpId=${extCmpId}">
                    <a href="javascript:"><span>已支付居民</span></a>
                </li>
                <li class="js_load_tab" onclick="pj016.loadTabContext(this);"
                    url="eland/pj/pj016/pj016-initFuze.gv?extCmpId=${extCmpId}">
                    <a href="javascript:"><span>负责居民</span></a>
                </li>
                <li class="js_load_tab" onclick="pj016.loadTabContext(this);"
                    url="eland/pj/pj016/pj016-initZfjl.gv?extCmpId=${extCmpId}">
                    <a href="javascript:"><span>支付记录</span></a>
                </li>
                <span class="js_PayButton"
                      style="color: #ffffff;margin: 5px 15px;padding: 3px 10px; font-size:16px; float: right; cursor:pointer;background-color: rgb(61, 145, 200)"
                      onclick="pj016.paySelectBuild(this,'${extCmpId}');">支付勾选居民</span>
            </ul>
        </div>
    </div>
    <div class="tabsContent" layoutH="185">
        <%--待支付院落--%>
        <div></div>
        <%--已支付院落--%>
        <div></div>
        <%--负责院落--%>
        <div></div>
        <%--支付记录--%>
        <div></div>
    </div>
</div>
<script>
    $(document).ready(function () {
        $("li.js_load_tab.selected", navTab.getCurrentPanel()).trigger("click");
    });
</script>
