<%--延期交房签约处理--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<oframe:script src="${pageContext.request.contextPath}/eland/ct/ct006/js/ct006.js" type="text/javascript"/>
<div class="mar5" style="vertical-align: top;">
    <input type="hidden" name="schemeType" value="${schemeType}"/>
    <input type="hidden" name="ctStatusField" value="${ctStatusField}"/>
    <%--数据查询区域--%>
    <div style="width: 340px;float: left;position: relative;" id="ct006SchDiv" layoutH="15" class="panel left_menu">
        <h1>
            <span class="panel_title">数据检索</span>

            <span onclick="ct006.schHs();" class="panel_menu js_reload">刷新</span>
            <span class="marr10" style="float: right;">
                <oframe:select prjCd="${param.prjCd}" id="ct006AccountStatus" 
                   itemCd="CONTRACT_STATUS" value="1" onChange="ct006.schHs();" name="ct006AccountStatus" type="checkbox"/>
            </span>
        </h1>

        <div style="padding: 0px;">
            <table width="100%;">
                <tr>
                    <td style="width: 30%;">
                        <select name="schType" style="float: left; width: 100%;">
                            <option value="NewHsInfo.oldHsId.hsOwnerPersons">被安置人</option>
                            <option value=NewHsInfo.oldHsId.hsCd">档案编号</option>
                            <option value="NewHsInfo.oldHsId.hsFullAddr">原房屋地址</option>
                            <option value="NewHsInfo.hsAddr">安置房地址</option>
                        </select>
                    </td>
                    <td>
                        <input type="text" name="schValue"
                               onkeydown="if(event.keyCode == 13){ct006.schHs();}"
                               style="width: 100%;height:26px;min-width:inherit;">
                    </td>
                    <td style="width: 30px;">
                        <a title="选择" onclick="ct006.schHs(this);" style="height: 30px;left:6px;" class="btnLook">选择</a>
                    </td>
                </tr>
            </table>
            <form id="ct006QueryForm_${schemeType}">
                <input type="hidden" name="entityName" value="NewHsInfo"/>
                <input type="hidden" name="conditionName" value="NewHsInfo.handleStatus">
                <input type="hidden" name="condition" value=">">
                <input type="hidden" name="conditionValue" value="1">
                <input type="hidden" name="sortColumn" value="NewHsInfo.oldHsId">
                <input type="hidden" name="sortOrder" value="asc">
                <input type="hidden" class="js_conditionValue" value="">
                <input type="hidden" name="divId" value="query_list">
                <%--权限控制--%>
                <input type="hidden" name="regUseType" value="${regUseType}">
                <input type="hidden" name="rhtType" value="${regUseType}">
                <input type="hidden" name="resultField" value="NewHsInfo.oldHsId.hsOwnerPersons,NewHsInfo.hsAddr">
                <%--必须包含的字段--%>
                <input type="hidden" name="forceResultField"
                       value="NewHsInfo.newHsId,NewHsInfo.oldHsId.hsId,NewHsInfo.oldHsId.hsOwnerPersons">
                <input type="hidden" name="forward" id="forward" value="/eland/ct/ct006/ct006_list"/>
                <%--必须包含的条件--%>
                <input type="hidden" class="js_conditionNames" value="${conditionNames}"/>
                <input type="hidden" class="js_conditions" value="${conditions}"/>
                <input type="hidden" class="js_conditionValues" value="${conditionValues}"/>
            </form>
            <div class="js_page" layoutH="90" id="query_list">
                <table class="table">
                    <thead>
                    <tr>
                        <th width="34%">安置人</th>
                        <th>选房地址</th>
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
    <%--签约区域--%>
    <div id="ct006Context" style="margin-left: 350px;margin-right: 5px;position: relative;">
        <c:if test="${param.oldHsId != null && param.oldHsId != '' && param.newHsId != null && param.newHsId != ''}">
            <jsp:include page="/eland/ct/ct006/ct006-info.gv">
                <jsp:param name="oldHsId" value="${param.oldHsId}"/>
                <jsp:param name="newHsId" value="${param.newHsId}"/>
                <jsp:param name="schemeType" value="${schemeType}"/>
            </jsp:include>
        </c:if>
    </div>
</div>
<script>
    $(document).ready(function () {
        ct006.schHs();
    });
</script>
