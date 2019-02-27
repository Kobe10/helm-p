<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<form id="pay" class="js_pay">
    <div class="js_allContent">
        <div>
            <table class="form">
                <tr>
                    <th width="13%">
                        固定服务费：
                    </th>
                    <td>
                        <input type="text" name="fixedServiceFee" onchange="pj016.counts(this)"/>
                    </td>
                    <th width="15%">
                        总走户服务费：
                    </th>
                    <td>
                        <input type="text" name="totalZhFee"/>
                    </td>
                    <th width="15%">
                        总服务费：
                    </th>
                    <td>
                        <input type="text" name="feeCount"/>
                    </td>
                    <th width="10%">
                        总院落数：
                    </th>
                    <td>
                        <input type="text" name="buildCount" readonly/>
                    </td>
                </tr>
                <tr>
                    <th>
                        支付周期：
                    </th>
                    <td colspan="3">
                        <input type="text" name="fromDate" onchange="pj016.fixedServiceFee();" style="width: 135px;"
                               class="date" value="<oframe:date value="${cmpBean.Row.recentPayDate}" format="yyyy-MM-dd"/>"/> -
                        <input type="text" name="toDate" onchange="pj016.fixedServiceFee();" style="width: 135px;"
                               class="date" value='<oframe:date value="" _default="now" format="yyyy-MM-dd"/>'/>
                    </td>
                    <th>
                        总户数：
                    </th>
                    <td>
                        <input type="text" name="hsCount" readonly/>
                    </td>
                </tr>
            </table>
        </div>
        <div class="panel">
            <div>
                <table class="table" width="98%">
                    <thead>
                    <tr>
                        <th><input type="checkbox" class="checkAllInList mart10" group="checkBd" checked onchange="pj016.checkAllBox(this)">
                            <input type="hidden" name="extCmpId" value="${extCmpId}"/>
                        </th>
                        <th>序号</th>
                        <th>院落地址</th>
                        <th>走户费</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${buildList}" var="item" varStatus="varStatus">
                        <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}"/>
                        <tr class="${tr_class} js_everyBuildTr">
                            <td><input type="checkbox" name="buildId" value="${item.Row.buildId}" group="checkBd" checked
                                       onchange="pj016.counts(this)"/>
                            </td>
                            <td>${varStatus.index + 1}</td>
                            <td>${item.Row.buildFullAddr}</td>
                            <td>
                                <input name="everyZhFee" type="text" value="${item.Row.totalZhFee}"
                                       onchange="pj016.counts(this)"/>
                                <input name="everyBdHsCount" type="hidden" value="${item.Row.hsCount}">
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                    <tr style="background-color: #ffffff">
                        <td colspan="4"><span class="btn btn-pri" align="center" onclick="pj016.payCfm(this);">支付勾选院落</span></td>
                    </tr>
                </table>
            </div>
        </div>
    </div>
</form>
<%--<oframe:script src="${pageContext.request.contextPath}/eland/pj/pj016/js/pj016.js" type="text/javascript"/>--%>
<script>
    $(document).ready(function () {
        pj016.fixedServiceFee();
        $("input.checkAllInList").click(function(event){
        });
    });
</script>