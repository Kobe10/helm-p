<%--信息审批结果提交处理界面--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div>
    <div class="panelBar">
        <ul class="toolBar">
            <li><a class="save" onclick="wf006.publishForm(this);"><span>发布</span></a></li>
            <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
        </ul>
    </div>
    <div>
        <form id="form_publish_dialog" method="post" class="required-validate">
            <input type="hidden" name="dialog_formId" value="${formId}">
            <table class="border">
                <tr>
                    <th width="20%"><label>发布说明：</label></th>
                    <td><textarea name="dialog_formDesc" rows="10" >${formDesc}</textarea></td>
                </tr>
            </table>
        </form>
    </div>
</div>
<oframe:script src="${pageContext.request.contextPath}/oframe/wf/wf006/js/wf006.js" type="text/javascript"/>