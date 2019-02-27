<%--院落腾退流程--上报整院--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<form id="ph007Frm">
    <input type="hidden" name="buildId" value="${buildId}"/>
    <table width="100%" class="border">
        <tr>
            <th width="10%">院落地址：</th>
            <td width="18%">${buildBean.BuildInfo.buildFullAddr}</td>
            <th width="10%">院落性质：</th>
            <td width="18%"><oframe:name prjCd="${param.prjCd}" itemCd="YARD_TYPE" value="${buildBean.BuildInfo.buildType}"/></td>
            <th width="10%">院落状态：</th>
            <td width="18%"><oframe:name prjCd="${param.prjCd}" itemCd="YARD_STATUS"
                                         value="${buildBean.BuildInfo.buildStatus}"/></td>
        </tr>
        <tr>
            <th>占地面积：</th>
            <td>${buildBean.BuildInfo.buildLandSize}</td>
            <th>建面单价：</th>
            <td>${buildBcYg.BuldCbYg.jMdJ}</td>
            <th>占地单价：</th>
            <td>${buildBcYg.BuldCbYg.zDdJ}</td>
        </tr>
        <tr>
            <th><label>管理小组：</label></th>
            <td>${buildMng.BuildInfo.ttOrgName}</td>
            <th><label>中介公司：</label></th>
            <td>${buildMng.BuildInfo.ttCompanyName}</td>
            <th><label>主谈人员：</label></th>
            <td>${buildMng.BuildInfo.ttMainTalk}</td>
        </tr>
        <tr>
            <th width="10%"><label>院落说明：</label></th>
            <td colspan="5">
                <span class="link marr5 js_doc_info" docTypeName="院落说明" relType="200" onclick="ph007.showDoc(this,'${buildId}')">
                    <label style="cursor:pointer;">上传院落说明</label>
                      <input type="hidden" name="docIds" value='${hiTaskInfo.HiTaskInfo.TskBidVars.docIds}'>
                </span>
            </td>
        </tr>
        <input type="hidden" name="buildType" class="js_pri" value="${buildBean.BuildInfo.buildType}"/>
        <jsp:include page="/eland/ph/ph007/ph007-nextAssignee.gv">
            <jsp:param name="tdNum" value="5"/>
        </jsp:include>
        <tr style="text-align: center;">
            <td colspan="6">
            <input type="hidden" name="procInsName" class="js_pri" value='[${buildBean.BuildInfo.buildFullAddr}] 院落腾退'/>
            <span class="btn-primary marl10"
                  style="padding: 5px 10px 5px 10px; line-height: 34px; cursor:pointer;"
                  onclick="ph007.submitVerifyassignee('ph007Frm', this, {})">上报整院</span>
                <span class="btn-primary marl20"
                      style="padding: 5px 10px 5px 10px; line-height: 34px; cursor:pointer;"
                      onclick="ph007.exportTtCb('${buildId}')">生成院落说明</span>
            </td>
        </tr>
    </table>
</form>
<oframe:script type="text/javascript" src="${pageContext.request.contextPath}/eland/ph/ph007/js/ph007.js"/>
<oframe:script type="text/javascript" src="${pageContext.request.contextPath}/eland/ph/ph008/js/ph008.js"/>
<oframe:script type="text/javascript" src="${pageContext.request.contextPath}/eland/ph/ph002/js/ph002_op.js"/>
<oframe:script type="text/javascript" src="${pageContext.request.contextPath}/eland/ph/ph003/js/ph003_op.js"/>