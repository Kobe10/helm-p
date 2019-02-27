<%--院落腾退流程--上报整院--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<form id="ph00701Frm">
    <table width="100%" class="border">
        <input type="hidden" name="buildId" value="${buildId}"/>
        <tr style="text-align: center;">
            <td colspan="6"><span class="btn-primary"
                                  style="padding: 5px 10px 5px 10px; line-height: 34px; cursor:pointer;"
                                  onclick="ph007.exportTtCb('${buildId}')">生成院落说明</span></td>
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
        <jsp:include page="/eland/ph/ph007/ph007-nextAssignee.gv"/>
        <tr style="text-align: center;">
            <td colspan="6">
            <span class="btn-primary marl10"
                  style="padding: 5px 10px 5px 10px; line-height: 34px; cursor:pointer;"
                  onclick="ph007.submitVerifyassignee('ph00701Frm', this, {})">上报整院</span>
            </td>
        </tr>
    </table>
</form>
<oframe:script type="text/javascript" src="${pageContext.request.contextPath}/eland/ph/ph007/js/ph007.js"/>