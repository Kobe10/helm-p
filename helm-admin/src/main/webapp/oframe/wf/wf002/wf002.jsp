<%--发起任务通用处理模板--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="mart5">
    <div class="pageNav">
        <a href="javascript:void(0)">流程管理</a>---><a class="current">流程发布</a>
    </div>

    <div class="panelBar">
        <ul class="toolBar">
            <li><a class="save" onclick="wf002.doDeploy();"><span>发布</span></a></li>
        </ul>
    </div>
    <div style="padding-left: 5px; padding-right: 5px;">
        <form id="wf002Frm" method="post" class="required-validate entermode">
            <input type="hidden" name="hsId" value="${baseBean.HouseInfo.hsId}">
            <input type="hidden" name="errMsg" value="${errMsg}">

            <div class="mar5">
                <table class="border">
                    <tr>
                        <th style="width: 15%"><label>流程名称：</label></th>
                        <td><input type="text" class="required" maxlength="30" name="deployName"/></td>
                    </tr>
                    <tr>
                        <th><label>流程分类：</label></th>
                        <td><input type="text" class="required" maxlength="30" name="procDepCategory"/></td>
                    </tr>
                    <tr>
                        <th><label>定义文件：</label></th>
                        <td><input type="file" name="wfFile" class="required" id="wfFile" accept=".zip,.xml"/></td>
                    </tr>
                </table>
            </div>
        </form>
    </div>
</div>
<oframe:script type="text/javascript" src="${pageContext.request.contextPath}/oframe/wf/wf002/js/wf002.js"/>


