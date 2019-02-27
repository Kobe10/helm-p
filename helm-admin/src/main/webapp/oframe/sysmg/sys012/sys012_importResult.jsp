<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>

<div id="sys012EntityList">
    <table class="table" width="98%" layoutH="100">
        <thead>
        <tr>
            <th>序号</th>
            <th>现英文名</th>
            <th>现存储表</th>
            <th>现存储字段</th>
            <th>现存储类型</th>
            <th>原英文名</th>
            <th>原存储字段</th>
            <th>原存储类型</th>
            <th>原存储表</th>
            <th>执行的操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${returnList}" var="item" varStatus="varStatus">
            <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}"/>
            <tr class="${tr_class}">
                <td>${varStatus.index + 1}</td>
                <td>${item.info.attrname}</td>
                <td>${item.info.savetable}</td>
                <td>${item.info.savefiled}</td>
                <td>${item.info.savefiledtype}</td>
                <td>${item.info.oldattrname}</td>
                <td>${item.info.oldfiled}</td>
                <td>${item.info.oldfiledtype}</td>
                <td>${item.info.oldtable}</td>
                <td>
                    <c:set var="dotype" value="${item.info.dotype}''"/>
                    <c:choose>
                        <c:when test="${dotype == '1'}">增加表</c:when>
                        <c:when test="${dotype == '2'}">增加字段</c:when>
                        <c:when test="${dotype == '3'}">修改字段</c:when>
                        <c:when test="${dotype == '4'}">修改存储类型</c:when>
                    </c:choose>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>