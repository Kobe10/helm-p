<%--居民签约处理--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<oframe:script src="${pageContext.request.contextPath}/eland/ph/ph013/js/ph013.js" type="text/javascript"/>
<div class="mar5" style="vertical-align: top;">
    <%--数据查询区域--%>
    <div style="width: 340px;float: left;position: relative;" id="ph013SchDiv" layoutH="15" class="panel left_menu">
        <h1>
            <span class="panel_title">数据检索</span>
            <input type="hidden" name="lockRptType" value=""/>
            <span onclick="ph013.schHs();" class="panel_menu js_reload">刷新</span>
        </h1>

        <div>
            <table width="100%;">
                <tr>
                    <td style="width: 30%;">
                        <select name="schType" style="float: left; width: 100%;">
                            <option value="HouseInfo.hsOwnerPersons">被安置人</option>
                            <option value="HouseInfo.hsFullAddr">房屋地址</option>
                            <option value="HouseInfo.hsCd">档案编号</option>
                        </select>
                    </td>
                    <td>
                        <input type="text" name="schValue"
                               onkeydown="if(event.keyCode == 13){ph013.qSchHs();}"
                               style="width: 100%;height:26px;min-width:inherit;">
                    </td>
                    <td style="width: 45px;">
                        <a title="查询" onclick="ph013.qSchHs(this);"
                           style="height: 30px;left:6px;" class="btnLook">
                            查询
                        </a>
                        <span onclick="ph013.queryHs(this);" title="高级查询" class="js-more-right"
                              style="width: 15px;cursor: pointer; text-align: center; line-height: 32px;float: right;"
                                type="button">
                        </span>
                    </td>
                </tr>
            </table>
            <form id="ph013QueryForm">
                <input type="hidden" name="entityName" value="HouseInfo"/>
                <input type="hidden" name="conditionName" value="">
                <input type="hidden" name="condition" value="">
                <input type="hidden" name="conditionValue" value="">
                <input type="hidden" name="sortColumn" value="HouseInfo.hsId">
                <input type="hidden" name="sortOrder" value="asc">
                <input type="hidden" class="js_conditionValue" value="">
                <input type="hidden" class="js_canDefResult" value="false">
                <input type="hidden" name="divId" value="query_list">
                <input type="hidden" name="resultField" value="HouseInfo.hsOwnerPersons,HouseInfo.hsFullAddr">
                <%--必须包含的字段--%>
                <input type="hidden" name="forceResultField"
                       value="HouseInfo.hsId,HouseInfo.HsCtInfo.hsCtId,HouseInfo.hsOwnerPersons,HouseInfo.hsFullAddr">
                <input type="hidden" name="forward" id="forward" value="/eland/ph/ph013/ph013_list"/>
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
    <div id="ph013Context" style="margin-left: 350px;margin-right: 5px;position: relative;">
        <div id="ph013AdanceSch" style="position: absolute;top: -40px;width: 100%;"></div>
        <div id="ph013HsInfo">
            <c:if test="${hsId != null && hsId != ''}">
                <jsp:include page="/eland/ph/ph013/ph013-info.gv">
                    <jsp:param name="hsId" value="${hsId}"/>
                </jsp:include>
            </c:if>
        </div>
    </div>
</div>
<script>
    $(document).ready(function () {
        ph013.schHs();
    });
</script>