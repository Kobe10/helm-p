<%--
  Created by IntelliJ IDEA.
  User: shfb_wang
  Date: 2015/7/23 0023 14:18
  Copyright(c) 北京四海富博计算机服务有限公司
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>

<div style="margin: 5px">
    <c:set var="chNum" value="0"/>
    <table style="width: 99.99%;border-collapse: collapse; border: 2px dashed #e81313;line-height: 28px">
        <tr>
            <th width="10%" style="text-align: right"><label>请求名称：</label></th>
            <td>
                ${PROC_INST_INFO.ProcInsInfo.Variables.procInsName}
            </td>
        </tr>
        <tr>
            <th width="10%" style="text-align: right"><label>请求备注：</label></th>
            <td>
                ${PROC_INST_INFO.ProcInsInfo.ProcBidVars.taskComment}
            </td>
        </tr>
        <tr>
            <th width="10%" style="text-align: right"><label>修改记录：</label></th>
            <td>
                <dl>
                    <c:forEach items="${chEntityList}" var="changedEntity" varStatus="varStatus2">
                        <c:set var="chAttr" value="${changedEntity.chAttrList}"/>
                        <c:set var="pseudoList" value="${changedEntity.pseudoList}"/>
                        <c:forEach items="${chAttr}" var="entityAttr" varStatus="varStatus3">
                            <c:set var="chNum" value="${chNum+1}"/>
                            <dt>
                                <c:set var="attrNameEn" value="${entityAttr.EntityAttr.attrNameEn}${''}"/>

                                <c:set var="attrValueBef" value="${entityAttr.EntityAttr.attrValueBef}${''}"/>
                                <c:set var="attrValueAft" value="${entityAttr.EntityAttr.attrValueAft}${''}"/>
                                <span>${chNum}、${entityAttr.EntityAttr.attrNameCh}：
                                    <c:choose>
                                        <c:when test="${entityAttr.EntityAttr.attrValueBef_name != null && entityAttr.EntityAttr.attrValueBef_name != ''}">
                                            <c:if test="${entityAttr.EntityAttr.isPseudoFlag == '1'}">
                                                <div>
                                                    <table class="border" style="text-align: center; width: 99%">
                                                        <c:forEach items="${pseudoList}" var="pseudo">
                                                            <tr>
                                                                <td></td>
                                                                <c:forEach items="${pseudo.thMap}" var="thMap" varStatus="varStatusTh">
                                                                    <td>${thMap.value}</td>
                                                                </c:forEach>
                                                            </tr>
                                                            <c:forEach items="${pseudo.befMapList}" var="befMap" varStatus="varStatusBef">
                                                                <tr>
                                                                    <td>旧</td>
                                                                    <c:forEach items="${pseudo.thMap}" var="thMap" varStatus="varStatusTh">
                                                                        <c:set var="thMapKey" value="${thMap.key}${''}"/>
                                                                        <c:choose>
                                                                            <c:when test="${befMap[thMapKey] != null && befMap[thMapKey] != ''}">
                                                                                <td>${befMap[thMapKey]}</td>
                                                                            </c:when>
                                                                            <c:otherwise>
                                                                                <td>——</td>
                                                                            </c:otherwise>
                                                                        </c:choose>
                                                                    </c:forEach>
                                                                </tr>
                                                            </c:forEach>
                                                            <c:forEach items="${pseudo.aftMapList}" var="aftMap" varStatus="varStatusAft">
                                                                <tr>
                                                                    <td>新</td>
                                                                    <c:forEach items="${pseudo.thMap}" var="thMap" varStatus="varStatusTh">
                                                                        <c:set var="thMapKey" value="${thMap.key}${''}"/>
                                                                        <c:choose>
                                                                            <c:when test="${aftMap[thMapKey] != null && aftMap[thMapKey] != ''}">
                                                                                <td>${aftMap[thMapKey]}</td>
                                                                            </c:when>
                                                                            <c:otherwise>
                                                                                <td>——</td>
                                                                            </c:otherwise>
                                                                        </c:choose>

                                                                    </c:forEach>
                                                                </tr>
                                                            </c:forEach>
                                                        </c:forEach>
                                                    </table>
                                                </div>
                                            </c:if>
                                            <c:if test="${entityAttr.EntityAttr.isPseudoFlag != '1'}">
                                                旧：
                                                <span>
                                                    ${entityAttr.EntityAttr.attrValueBef_name}
                                                </span>，
                                            </c:if>
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
                                            <c:if test="${entityAttr.EntityAttr.isPseudoFlag != '1'}">
                                                新：${entityAttr.EntityAttr.attrValueAft_name}，
                                            </c:if>
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
                            </dt>
                        </c:forEach>
                    </c:forEach>
                </dl>
            </td>
        </tr>
    </table>
</div>
<%--引入 房产信息详情页 --%>
<jsp:include page="/eland/ph/ph003/ph00301-initS.gv?prjCd=${prjCd}&hsId=${hsId}&procReadonly=false"/>

<oframe:script src="${pageContext.request.contextPath}/eland/ph/ph003/js/ph00301_task.js" type="text/javascript"/>
<oframe:script src="${pageContext.request.contextPath}/eland/ph/ph003/js/ph00301_input.js" type="text/javascript"/>
<script>
    $(document).ready(function () {
        $(":input").attr("disabled", "disabled");
        $(":text").each(function () {
            var $this = $(this);
            var tTitle = $this.attr("title");
            if (tTitle) {
                $this.attr("title", tTitle + "；" + $this.val());
            } else {
                $this.attr("title", $this.val());
            }
        });
        $("input[name=procInsName]").removeAttr("disabled");
        $("textarea[name=taskComment]").removeAttr("disabled");
    });
</script>