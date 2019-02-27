<%--待办任务列表--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="mar5">
    <div class="pageNav">
        <a class="link" href="javascript:void(0)">作业管理</a>
    </div>
    <div style="position: relative;">
        <div class="panelBar">
            <ul class="toolBar">
                <li onclick="sys015.openQSch();">
                    <a class="find" href="javascript:void(0)"><span>检索</span></a>
                </li>
                <li onclick="sys015.query();">
                    <a class="reflesh" href="javascript:void(0);"><span>刷新</span></a>
                </li>
                <li onclick="sys015.editView('')">
                    <a class="add" href="javascript:void(0);"><span>添加作业</span></a>
                </li>
            </ul>
        </div>
        <div style="padding-left: 5px; padding-right: 5px;">
            <div id="sys015create" class="hidden"
                 style="position: absolute;width:100%;top: 0px;z-index: 100;background-color: #ffffff; margin-top: 40px;">
                <div class="triangle triangle-up" style="border-bottom-color: #2c7bae;top: -10px; left: 25px;"></div>
                <table class="border">
                    <tr>
                        <th><label>作业名称：</label></th>
                        <td>
                            <input name="jobNameCh" condition="like" type="text" value=""/>
                        </td>
                        <th><label>作业类型：</label></th>
                        <td>
                            <input name="jobType" condition="=" type="text">
                        </td>
                        <th><label>是否有效：</label></th>
                        <td>
                            <oframe:select prjCd="${param.prjCd}" itemCd="COMMON_YES_NO" name="jobStatus"
                                           withEmpty="true" value=""/>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="8" align="center">
                            <button onclick="sys015.query();sys015.openQSch()" type="button" id="schBtn"
                                    class="js_faTask btn btn-primary">
                                查询
                            </button>
                            <button onclick="sys015.openQSch()" type="button" class="btn btn-info">关闭</button>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
        <form id="sys015form" method="post">
            <input type="hidden" name="entityName" value="JobTaskInfo"/>
            <input type="hidden" name="conditionName" value="">
            <input type="hidden" name="condition" value="">
            <input type="hidden" name="conditionValue" value="">
            <input type="hidden" name="sortColumn" value="">
            <input type="hidden" name="sortOrder" value="">
            <input type="hidden" name="forceResultField" value="jobTaskId">
            <input type="hidden" class="js_conditionValue" value="">
            <input type="hidden" name="divId" value="jobTask">
            <input type="hidden" name="cmptName" id="cmptName" value="QUERY_JOB_TASK_INFO_CMPT"/>
            <input type="hidden" name="resultField"
                   value="jobNameCh,jobDescription,jobStatus,jobType,jobExecTime,jobStartParam">
            <input type="hidden" name="forward" id="forward" value="/oframe/sysmg/sys015/sys015_list"/>
        </form>
        <div id="sys015_page_data" class="js_page" layoutH="55"></div>
    </div>
</div>
<oframe:script type="text/javascript" src="${pageContext.request.contextPath}/oframe/sysmg/sys015/js/sys015.js"/>

