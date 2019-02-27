<%--自定义任务，查看发起的任务--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<table width="100%" class="border">
    <tr>
        <th style="width: 10%"><label>院落说明：</label></th>
        <td><span class="link marr5 js_doc_info" docTypeName="院落说明" editAble="false" relType="200"
                  onclick="ph007.showDoc(this,'${hiTaskInfo.HiTaskInfo.TskBidVars.buildId}')">
                    <label style="cursor:pointer;">查看院落说明</label>
                      <input type="hidden" name="docIds" value='${hiTaskInfo.HiTaskInfo.TskBidVars.docIds}'>
                </span>
            <%--<a style="color: #0000cc"--%>
               <%--href="${pageContext.request.contextPath}/oframe/common/file/file-download.gv?docId=${hiTaskInfo.HiTaskInfo.TskBidVars.docId}">${hiTaskInfo.HiTaskInfo.TskBidVars.docName}</a>--%>
        </td>
    </tr>
    <tr>
        <th style="width: 10%"><label>备注说明：</label></th>
        <td>${hiTaskInfo.HiTaskInfo.TskBidVars.comment}</td>
    </tr>
</table>
<oframe:script type="text/javascript" src="${pageContext.request.contextPath}/eland/ph/ph006/js/ph006.js"/>