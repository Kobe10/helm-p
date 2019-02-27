<%--居民签约控制--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="pagecontent">
    <div class="panelBar">
        <ul class="toolBar">
            <li><a class="save" onclick="sys015.editJob();"><span>保存</span></a></li>
            <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
        </ul>
    </div>

    <div style="min-height: 150px">
        <table class="border marb5" width="100%">
            <input type="hidden" name="jobTaskId" value="${jobInfo.JobTaskInfo.jobTaskId}"/>
            <tr>
                <th width="20%"><label>作业名称：</label></th>
                <td colspan="3"><input type="text" value="${jobInfo.JobTaskInfo.jobNameCh}" name="jobNameCh"/></td>
            </tr>
            <tr>
                <th><label>作业类型：</label></th>
                <td colspan="3"><input type="text" value="${jobInfo.JobTaskInfo.jobType}" name="jobType"/></td>
            </tr>
            <tr>
                <th><label>执行时间：</label></th>
                <td colspan="3"><input type="text" value="${jobInfo.JobTaskInfo.jobExecTime}" name="jobExecTime"/></td>
            </tr>
            <tr>
                <th><label>启动参数：</label></th>
                <td colspan="3"><input type="text" value='${jobInfo.JobTaskInfo.jobStartParam}' name="jobStartParam"/>
                </td>
            </tr>
            <c:if test="${jobInfo.JobTaskInfo.jobTaskId==null||jobInfo.JobTaskInfo.jobTaskId==''}">
                <tr>
                    <th width="20%"><label>是否有效：</label></th>
                    <td>
                        <oframe:select prjCd="${param.prjCd}" itemCd="COMMON_YES_NO" name="jobStatus"
                                       value="${jobInfo.JobTaskInfo.jobStatus}"/>
                    </td>
                </tr>
                <tr>
                    <th width="20%"><label>是否立即执行：</label></th>
                    <td>
                        <oframe:select prjCd="${param.prjCd}" itemCd="COMMON_YES_NO" name="jobRunNow"
                                       value=""/>
                    </td>
                </tr>
            </c:if>
            <tr>
                <th><label>作业描述：</label></th>
                <td colspan="3"><input type="text" value="${jobInfo.JobTaskInfo.jobDescription}" name="jobDescription"/>
                </td>
            </tr>
        </table>
    </div>
</div>
