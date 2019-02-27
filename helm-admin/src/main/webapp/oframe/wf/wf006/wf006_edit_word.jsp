<%--待签约处理--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page language="java" import="com.shfb.oframe.core.util.common.StringUtil" %>
<%@ page import="com.zhuozhengsoft.pageoffice.OpenModeType" %>
<%@ page import="com.zhuozhengsoft.pageoffice.PageOfficeCtrl" %>
<%@ page import="com.zhuozhengsoft.pageoffice.PageOfficeLink" %>
<%@ page import="com.zhuozhengsoft.pageoffice.wordwriter.WordDocument" %>
<%@ taglib uri="http://java.pageoffice.cn" prefix="po" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<html>

<body onunload="closeDoc();">
<style type="text/css">
    .mart20 {
        margin-top: 20px;
    }
    .hidden {
        display: none;
    }
</style>
<c:set var="docPath" value="${docPath}"/>
<c:set var="formId" value="${formId}"/>
<c:set var="oldTemplateCd" value="${oldTemplateCd}"/>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<%
    String docPath = StringUtil.obj2Str(pageContext.getAttribute("docPath"));
    String formId = StringUtil.obj2Str(pageContext.getAttribute("formId"));
    String oldTemplateCd = StringUtil.obj2Str(pageContext.getAttribute("oldTemplateCd"));
    String url = "oframe/wf/wf006/wf006-openFile.gv?formId=" + formId + "&oldTemplateCd=" + oldTemplateCd;
    String staffName = StringUtil.obj2Str(pageContext.getAttribute("staffName"));
    String contextPath = StringUtil.obj2Str(pageContext.getAttribute("contextPath"));
    PageOfficeCtrl poCtrl1 = new PageOfficeCtrl(request);
    poCtrl1.setServerPage(contextPath + "/poserver.zz");//此行必须
    poCtrl1.setTitlebar(false); //隐藏标题栏
    poCtrl1.setMenubar(true); //显示菜单栏
    poCtrl1.setOfficeToolbars(true);//显示Office工具条
    poCtrl1.setCustomToolbar(false);
    // 禁止word选择功能
    WordDocument doc = new WordDocument();
    doc.setDisableWindowSelection(false);
    poCtrl1.setWriter(doc);
    poCtrl1.setSaveFilePage(contextPath + "/oframe/wf/wf006/wf006-saveEditFile.gv?&formId=" + formId + "&oldTemplateCd=" + oldTemplateCd);
    // 打开文件
    if (docPath.endsWith("docx") || docPath.endsWith("doc")) {
        // 打开文件
        poCtrl1.webOpen(contextPath + "/" + url, OpenModeType.docNormalEdit, staffName);
    } else if (docPath.endsWith("xls") || docPath.endsWith("xlsx")) {
        // 打开文件
        poCtrl1.webOpen(contextPath + "/" + url, OpenModeType.xlsNormalEdit, staffName);
    } else {
        return;
    }
    poCtrl1.setTagId("PageOfficeCtrl1"); //此行必须
%>
<po:PageOfficeCtrl width="100%" height="99%" id="PageOfficeCtrl1"></po:PageOfficeCtrl>
<div id="notOpen" style="text-align: center;" class="mart20 hidden">
    <h3 style="font-size: 20px;">无法加载插件请确认插件已安装，安装后仍无法打开请点击以下链接</h3><br/>
    <a class="link" style="font-size: 18px;display: inline-block; margin-right: 10px;"
       href="<%=PageOfficeLink.openWindow(request, "wf006-viewWordDoc.gv?docPath=" + docPath+ "&formId=" + formId + "&oldTemplateCd=" + oldTemplateCd, "width=1024px;height=768px;title=文件编辑")%>">
        打开文档
    </a>
    <a class="link" style="font-size: 12px;display: inline-block; margin-right: 10px;"
       href="${pageContext.request.contextPath}/posetup.exe">插件下载 </a>
</div>
<script src="${pageContext.request.contextPath}/oframe/plugin/jquery/eland.jquery.min.js"
        type="text/javascript"></script>
<script type="text/javascript">
    function reloadInfo() {
        var ctCountDownIFrame = window.parent.frames["ctCountDownIFrame"];
        if (ctCountDownIFrame && typeof  ctCountDownIFrame != undefined) {
            ctCountDownIFrame.document.location.reload();
        }
    }
    $(document).ready(function () {
        var PageOfficeCtrl1 = $("#PageOfficeCtrl1");
        PageOfficeCtrl1[0].JsFunction_AfterDocumentOpened = "reloadInfo();";
        var wf006CanNotOpen = $("#notOpen");
        var isLoad = undefined;
        try {
            isLoad = document.getElementById("PageOfficeCtrl1").AllowCopy;
        } catch (e) {
            isLoad = undefined;
        }
        if (isLoad == undefined) {
            $("div", PageOfficeCtrl1).remove();
            PageOfficeCtrl1.append(wf006CanNotOpen);
            wf006CanNotOpen.removeClass("hidden");
        }
    });
    function closeDoc() {
        var tempObj = document.getElementById("PageOfficeCtrl1");
        if (tempObj && (typeof tempObj.close == 'function' )) {
            tempObj.Close();
        }
    }
</script>
</body>

</html>


