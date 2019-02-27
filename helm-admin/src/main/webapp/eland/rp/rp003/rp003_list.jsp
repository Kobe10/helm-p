<%--
  Created by IntelliJ IDEA.
  User: shfb_wang
  Date: 2015/5/7
  Time: 11:41
  To change this template use File | Settings | File Templates.
--%>

<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div id="rp003list" style="padding: 3px">
    <table class="border" style="text-align: center" layoutH="60" adjust="false" width="100%">
        <thead>
        <tr>
            <td colspan="26">
                2015年腾退工作进展一览表
            </td>
        </tr>
        <tr>
            <td width="40" rowspan="2">
                院落序
            </td>
            <td width="198" rowspan="2">
                地址
            </td>
            <td width="89" rowspan="2">
                门牌
            </td>
            <td width="40" rowspan="2">
                私房户
            </td>
            <td width="40" rowspan="2">
                直管户
            </td>
            <td width="40" rowspan="2">
                其他
            </td>
            <td width="40" rowspan="2">
                总户数
            </td>
            <td colspan="4">
                融泽嘉园一期房
            </td>
            <td colspan="4">
                长阳长景新园房
            </td>
            <td width="40" rowspan="2">
                签约
            </td>
            <td width="40" rowspan="2">
                选房
            </td>
            <td width="40" rowspan="2">
                过户
            </td>
            <td width="40" rowspan="2">
                网签
            </td>
            <td width="40" rowspan="2">
                完税
            </td>
            <td width="40" rowspan="2">
                退租
            </td>
            <td width="40" rowspan="2">
                转移登记
            </td>
            <td width="40" rowspan="2">
                腾退交房
            </td>
            <td width="40" rowspan="2">
                结算
            </td>
            <td width="40" rowspan="2">
                购房合同
            </td>
            <%--<td width="40" rowspan="2">--%>
                <%--备注--%>
            <%--</td>--%>
        </tr>
        <tr>
            <td width="40">
                一居
            </td>
            <td width="40">
                二居
            </td>
            <td width="40">
                三居
            </td>
            <td width="40">
                合计
            </td>
            <td width="40">
                零居
            </td>
            <td width="40">
                一居
            </td>
            <td width="40">
                二居
            </td>
            <td width="40">
                合计
            </td>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${returnList}" var="item" varStatus="varStatus">
            <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}"/>
            <c:set var="bkcolor" value="${varStatus.index % 2 == 0 ? 'rgba(61, 114, 255, 0.32)' : 'even'}"/>
            <tr class="${tr_class}" style="background-color: ${bkcolor}">
                <td>${varStatus.index + 1}</td>
                <%@include file="/oframe/common/query/list_hdt.jsp" %>
                <%--<%@include file="rp003_list_hdt.jsp" %>--%>
                <%--<td>√</td>--%>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <%--<jsp:include page="/oframe/common/page/page_fotter.jsp"/>--%>
</div>
