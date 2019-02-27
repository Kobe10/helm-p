<%--
  Created by IntelliJ IDEA.
  User: Dennis
  Date: 16/4/11 09:13
  Copyright(c) 北京四海富博计算机服务有限公司
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<div class="mar10">
    <c:forEach items="${operationList}" var="operation" varStatus="varStatus1">
        <c:set var="chNum" value="0"/>
        <div class="mar10">
            <ul>
                <c:set var="chEntity" value="${operation.chEntityList}"/>
                <li>
                    <ul>
                        <li style="list-style: none;float: left; margin-left: 20px;"><oframe:staff
                                staffId="${operation.opStaffId}"/></li>
                        <li style="list-style: none;float: left; margin-left: 20px;"><oframe:date
                                value="${operation.opEndTime}" format="yyyy/MM/dd HH:mm:ss"/></li>
                        <%--<li style="list-style: none;float: left; margin-left: 20px;">${operation.opCode}</li>--%>
                        <li style="list-style: none;float: left; margin-left: 20px;">${operation.fromIp}</li>
                    </ul>
                    <br/>
                    <ul>
                        <c:forEach items="${chEntity}" var="changedEntity" varStatus="varStatus2">
                            <c:set var="chAttr" value="${changedEntity.chAttrList}"/>
                            <c:forEach items="${chAttr}" var="entityAttr" varStatus="varStatus3">
                                <c:if test="${entityAttr.EntityAttr.isPseudoFlag != 'true'}">
                                    <c:set var="chNum" value="${chNum+1}"/>
                                    <li class="marl20" style="color: #5fb0ff;">
                                        <c:set var="attrValueBef" value="${entityAttr.EntityAttr.attrValueBef}${''}"/>
                                        <c:set var="attrValueAft" value="${entityAttr.EntityAttr.attrValueAft}${''}"/>
                                            <span>${chNum}、${entityAttr.EntityAttr.attrNameCh}：
                                            <c:choose>
                                                <c:when test="${entityAttr.EntityAttr.attrValueBef_name != null && entityAttr.EntityAttr.attrValueBef_name != ''}">
                                                    旧：<span>${entityAttr.EntityAttr.attrValueBef_name}</span>，
                                                </c:when>
                                                <c:otherwise>
                                                    <c:choose>
                                                        <c:when test="${attrValueBef != '' && attrValueBef != null}">
                                                            旧：<span>${attrValueBef}</span>，
                                                        </c:when>
                                                        <c:otherwise>
                                                            旧：<span>空值</span>，
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:otherwise>
                                            </c:choose>
                                            <c:choose>
                                                <c:when test="${entityAttr.EntityAttr.attrValueAft_name != null && entityAttr.EntityAttr.attrValueAft_name != ''}">
                                                    新：${entityAttr.EntityAttr.attrValueAft_name}。
                                                </c:when>
                                                <c:otherwise>
                                                    <c:choose>
                                                        <c:when test="${attrValueAft != '' && attrValueAft != null}">
                                                            新：${attrValueAft}。
                                                        </c:when>
                                                        <c:otherwise>
                                                            新：空值。
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:otherwise>
                                            </c:choose>
                                        </span>
                                    </li>
                                </c:if>
                            </c:forEach>
                        </c:forEach>
                    </ul>
                </li>
            </ul>
        </div>
    </c:forEach>
    <c:if test="${fn:length(operationList) == 0}">
        <div style="text-align: center; font-size: large">该操作未修改任何实体的具体内容!</div>
    </c:if>
</div>
<script>

</script>