<%--项目资料--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="mar5">
    <div class="panelBar">
        <ul class="toolBar">
            <li><a class="save" onclick="wf006.saveTemplateText(this)"><span>保存</span></a></li>
            <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>取消</span></a></li>
        </ul>
    </div>
    <div class="tabsContent">
        <form id="wf006EditTempForm">
            <input type="hidden" name="formId" value="${formId}"/>
            <input type="hidden" name="oldTemplateCd" value="${oldTemplateCd}"/>
            <input type="hidden" name="sysFormTemplateType" value="${sysFormTemplateType}"/>
            <textarea name="templateText" class="hidden">${tempText}</textarea>
            <iframe name="wf006TempIFrame" allowTransparency="false" id="wf006TempIFrame"
                    width="99.5%;" style="background-color:#f5f9fc;border:0;"
                    src="${pageContext.request.contextPath}/oframe/wf/wf006/wf006-webDesign.gv?textarea=templateText&content=dialog"
                    layoutH="60">
            </iframe>
        </form>
    </div>
</div>