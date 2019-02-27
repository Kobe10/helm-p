<%--居民签约处理--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<oframe:script src="${pageContext.request.contextPath}/eland/ph/ph014/js/ph014.js" type="text/javascript"/>
<oframe:script src="${pageContext.request.contextPath}/oframe/plugin/json/json-patch.min.js" type="text/javascript"/>
<oframe:script src="${pageContext.request.contextPath}/oframe/plugin/json/json-patch-duplex.js" type="text/javascript"/>
<div class="mar5" style="vertical-align: top;">
    <input type="hidden" name="formCd" value="${formCd}"/>
    <input type="hidden" name="withFrame" value="${withFrame}"/>
    <%--数据查询区域--%>
    <div style="width: 340px;float: left;position: relative;" id="ph014SchDiv" layoutH="15" class="panel left_menu">
        <h1>
            <span class="panel_title">数据检索</span>
            <c:if test="${showAdd}">
                <oframe:power rhtCd="${formCd}_ADD_RHT" prjCd="${param.prjCd}">
                    <span onclick="form.info('',{});" class="panel_menu js_reload js_add">新增</span>
                </oframe:power>
            </c:if>
            <span onclick="ph014.schHs();" class="panel_menu js_reload">刷新</span>
        </h1>

        <div>
            <table width="100%;">
                <tr>
                    <td style="width: 30%;">
                        <oframe:select name="schType" onChange="ph014.cookieSchType(this);"
                                       value="${cookie['ph014-schType'].value}" itemCd="LIST_QUERY_CON"
                                       style="width:100%"/>
                    </td>
                    <td>
                        <input type="text" name="schValue" onfocus="$(this).select();"
                               onkeydown="if(event.keyCode == 13){ph014.qSchHs();}"
                               style="width: 100%;height:26px;min-width:inherit;">
                    </td>
                    <td style="width: 45px;">
                        <a title="查询" onclick="ph014.qSchHs(this);"
                           style="height: 30px;left:6px;" class="btnLook">
                            查询
                        </a>
                        <span onclick="ph014.queryHs(this);" title="高级查询" class="js-more-right"
                              style="width: 15px;cursor: pointer; text-align: center; line-height: 32px;float: right;"
                              type="button">
                        </span>
                    </td>
                </tr>
            </table>
            <form id="ph014QueryForm">
                <%-- 信息查询主键编号 --%>
                <input type="hidden" name="infoKeyPath" value="${conditions.Condition.infoKeyPath}">
                <%--通用分页查询信息--%>
                <input type="hidden" name="entityName" value="HouseInfo"/>
                <input type="hidden" name="conditionName" value="${conditions.Condition.conditionNames}">
                <input type="hidden" name="condition" value="${conditions.Condition.conditions}">
                <input type="hidden" name="conditionValue" value="${conditions.Condition.conditionValues}">
                <input type="hidden" name="rhtType" value="${conditions.Condition.rhtType}">
                <input type="hidden" name="sortColumn" value="HouseInfo.hsFullAddr">
                <input type="hidden" name="sortOrder" value="asc">
                <input type="hidden" class="js_conditionValue" value="">
                <input type="hidden" class="js_canDefResult" value="false">
                <input type="hidden" name="divId" value="query_list">
                <input type="hidden" name="resultField" value="HouseInfo.hsOwnerPersons,HouseInfo.hsFullAddr">
                <input type="hidden" name="slfDefUrl" value="${pageContext.request.contextPath}/oframe/common/page/page-dataWithTag.gv">
                <%--必须包含的字段--%>
                <input type="hidden" name="forceResultField"
                       value="HouseInfo.hsId,HouseInfo.HsCtInfo.hsCtId,HouseInfo.hsOwnerPersons,HouseInfo.hsFullAddr,${conditions.Condition.infoKeyPath}">
                <input type="hidden" name="forward" id="forward" value="/eland/ph/ph014/ph014_list"/>
            </form>
            <div class="js_page" layoutH="90" id="query_list">
                <table class="table">
                    <thead>
                    <tr>
                        <th width="34%">安置人</th>
                        <th>房屋地址</th>
                    </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
    <div class="split_line" layoutH="15">
        <span class="split_but" title="展开/隐藏导航"></span>
    </div>
    <%--居民信息展示界面--%>
    <div id="ph014Right" style="margin-left: 350px;margin-right: 5px;position: relative;">
        <div id="ph014AdanceSch" style="position: absolute;top: -40px;width: 100%;"></div>
        <div class="form_rpt_data" id="ph014Context">
            <c:if test="${showAdd || (param.hsId != null && param.hsId != '')}">
                <jsp:include page="/eland/ph/ph014/ph014-info.gv">
                    <jsp:param name="hsId" value="${param.hsId}"/>
                    <jsp:param name="keyId" value="${keyId}"/>
                    <jsp:param name="formCd" value="${formCd}"/>
                    <jsp:param name="withFrame" value="${withFrame}"/>
                    <jsp:param name="fromEdit" value="true"/>
                </jsp:include>
            </c:if>
        </div>
    </div>
</div>
<script>
    $(document).ready(function () {
        ph014.schHs();
    });
</script>
