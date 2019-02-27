<%--居民签约控制--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="pagecontent">
    <div class="panelBar">
        <ul class="toolBar">
            <li><a class="save" onclick="sys018.editJob();"><span>保存</span></a></li>
            <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
        </ul>
    </div>
    <div layoutH="55" style="border: 1px solid rgb(233, 233, 233);">
        <form id="sys018RuleForm">
            <input type="hidden" name="oldJobCd" value="${prjJobDef.PrjJobDef.jobCd}"/>
            <input type="hidden" name="oldPrjCd" value="${prjJobDef.PrjJobDef.prjCd}"/>
            <input type="hidden" name="jobId" value="${prjJobDef.PrjJobDef.jobId}"/>

            <table class="border marb5" width="100%">
                <tr>
                    <th>
                        <label>指标编码：</label>
                    </th>
                    <td>
                        <input type="text" value="${prjJobDef.PrjJobDef.jobCd}"
                               name="jobCd" class="required"/>
                    </td>
                    <th>
                        <label>指标名称：</label>
                    </th>
                    <td>
                        <input type="text" value="${prjJobDef.PrjJobDef.jobName}"
                               name="jobName"/>
                    </td>
                    <th>
                        <label>适用项目：</label>
                    </th>
                    <td>
                        <oframe:select prjCd="${param.prjCd}" collection="${prjCdMap}" name="prjCd"
                                       value="${prjJobDef.PrjJobDef.prjCd}"
                                       style="width:81%"/>
                    </td>
                    <th>
                        <label>执行引擎：</label>
                    </th>
                    <td>
                        <oframe:select itemCd="JOB_QUERY_SERVICE" value="${prjJobDef.PrjJobDef.qryService}"
                                       withEmpty="false" name="qryService"/>
                    </td>
                </tr>
                <tr>
                    <th>
                        <label>指标分组：</label>
                    </th>
                    <td>
                        <input type="text" value="${prjJobDef.PrjJobDef.jobGroupName}"
                               name="jobGroupName"/>
                    </td>
                    <th>
                        <label>结果单位：</label>
                    </th>
                    <td>
                        <input type="text" value="${prjJobDef.PrjJobDef.jobValueSuffix}"
                               name="jobValueSuffix"/>
                    </td>
                    <th>
                        <label>叠加分组：</label>
                    </th>
                    <td>
                        <input type="text" value="${prjJobDef.PrjJobDef.exeParam09}"
                               name="exeParam09"/>
                    </td>
                    <th>
                        <label>图形类型：</label>
                    </th>
                    <td>
                        <input type="text" value="${prjJobDef.PrjJobDef.exeParam10}"
                               name="exeParam10"/>
                    </td>
                </tr>
                <tr>
                    <th>
                        <label>x参考：</label>
                    </th>
                    <td>
                        <input type="text" value="<c:out value='${prjJobDef.PrjJobDef.exeParam07}'/>"
                               name="exeParam07"/>
                    </td>
                    <th>
                        <label>y参考：</label>
                    </th>
                    <td>
                        <input type="text" value="<c:out value='${prjJobDef.PrjJobDef.exeParam08}'/>"
                               name="exeParam08"/>
                    </td>
                    <th>
                        <label>权限类型：</label>
                    </th>
                    <td>
                        <oframe:select itemCd="DATA_RHT_DEF" withEmpty="true"
                                       value="${prjJobDef.PrjJobDef.qryParam03}" name="qryParam03"/>
                    </td>
                    <th>
                        <label>区域类型：</label>
                    </th>
                    <td colspan="5">
                        <oframe:select itemCd="REG_USE_TYPE" value="${prjJobDef.PrjJobDef.qryParam02}"
                                       name="qryParam02"/>
                        <input type="hidden" name="para" value="${para}"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="8" style="text-align: left" class="subTitle">&nbsp;&nbsp;&nbsp;&nbsp;指标定义：</td>
                </tr>
            </table>
            <textarea name="qryParam01" class="hidden">${prjJobDef.PrjJobDef.qryParam01}</textarea>
            <iframe name="sys018IFrame" allowTransparency="false" id="sys018IFrame" width="99.5%;"
                    style="background-color:#f5f9fc;"
                    src="${pageContext.request.contextPath}/oframe/sysmg/sys018/sys018-ruleCode.gv" layoutH="290">
            </iframe>
        </form>
    </div>
</div>