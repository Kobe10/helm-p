<%--项目资料--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="mar5">
    <div class="panelBar">
        <ul class="toolBar">
            <li><a class="save" onclick="wf003.saveTemplateText(this)"><span>保存</span></a></li>
            <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>取消</span></a></li>
        </ul>
    </div>
    <div class="tabsContent">
        <form id="wf003EditTempForm">
            <input type="hidden" name="id" value="${id}"/>
            <input type="hidden" name="wfReg" value="${wfReg}"/>
            <textarea name="procDefInfo" class="hidden"><c:out value="${procDefInfo}"/></textarea>
            <iframe name="wf003TempIFrame" allowTransparency="false" id="wf003TempIFrame"
                    width="99.5%;" style="background-color:#f5f9fc;"
                    src="${pageContext.request.contextPath}/oframe/wf/wf003/wf003-textCode.gv?textarea=procDefInfo&model=xml&content=dialog"
                    layoutH="60">
            </iframe>
        </form>
    </div>
</div>