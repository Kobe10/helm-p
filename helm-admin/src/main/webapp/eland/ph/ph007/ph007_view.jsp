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
            <th style="width: 10%"><label>院落说明：</label></th>
            <td colspan="5"><span class="link marr5 js_doc_info" docTypeName="院落说明" editAble="false" relType="200"
                      onclick="ph007.showDoc(this,'${hiTaskInfo.HiTaskInfo.TskBidVars.buildId}')">
                    <label style="cursor:pointer;">查看院落说明</label>
                      <input type="hidden" name="docIds" value='${hiTaskInfo.HiTaskInfo.TskBidVars.docIds}'>
                </span>
            </td>
        </tr>
        <tr>
            <th style="width: 10%"><label>备注说明：</label></th>
            <td colspan="5">${hiTaskInfo.HiTaskInfo.TskBidVars.comment}</td>
        </tr>
    </table>
</form>
<oframe:script type="text/javascript" src="${pageContext.request.contextPath}/eland/ph/ph007/js/ph007.js"/>
<oframe:script type="text/javascript" src="${pageContext.request.contextPath}/eland/ph/ph008/js/ph008.js"/>
<oframe:script type="text/javascript" src="${pageContext.request.contextPath}/eland/ph/ph002/js/ph002_op.js"/>
<oframe:script type="text/javascript" src="${pageContext.request.contextPath}/eland/ph/ph003/js/ph003_op.js"/>