<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>

<div class="mar5">
    <div style="position: relative;">
        <div class="panelBar">
            <ul class="toolBar">
                <li onclick="sys020.queryHs(this);">
                    <a class="find" href="javascript:void(0)"><span>检索</span></a>
                </li>
                <li onclick="sys020.query();">
                    <a class="reflesh" href="javascript:void(0)"><span>刷新</span></a>
                </li>
                <li onclick="sys020.sendView();">
                    <a class="add" href="javascript:void(0)"><span>发送</span></a>
                </li>
            </ul>
        </div>
        <form id="sys020form" method="post">
            <input type="hidden" name="entityName" value="SmsSend"/>
            <input type="hidden" name="conditionName" value="">
            <input type="hidden" name="condition" value="">
            <input type="hidden" name="conditionValue" value="">
            <input type="hidden" name="sortColumn" value="sRelTime">
            <input type="hidden" name="sortOrder" value="desc">
            <input type="hidden" name="divId" value="sys020_page_data">
            <input type="hidden" name="forceResultField" value="sId,sStatus">
            <input type="hidden" class="js_conditionValue" value="">
            <input type="hidden" name="cmptName" id="cmptName" value="QUERY_ENTITY_PAGE_DATA"/>
            <input type="hidden" name="resultField"
                   value="sPsName,staffCode,rPhoneNum,sRelTime,sContent,sStatus">
            <input type="hidden" name="forward" id="forward" value="/oframe/sysmg/sys020/sys020_list"/>
        </form>
        <div id="sys020_page_data" class="js_page" layoutH="55"></div>
    </div>
</div>
<oframe:script type="text/javascript" src="${pageContext.request.contextPath}/oframe/sysmg/sys020/js/sys020.js"/>
