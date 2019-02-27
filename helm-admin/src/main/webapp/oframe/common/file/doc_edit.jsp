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
<style type="text/css">
    .mart20 {
        margin-top: 20px;
    }

    .hidden {
        display: none;
    }
</style>
<c:set var="docId" value="${docId}"/>
<c:set var="prjCd" value="${prjCd}"/>
<c:set var="isEditable" value="${isEditable}"/>
<c:set var="docName" value="${docName}"/>
<c:set var="staffName" value="${staffName}"/>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<%
    String docId = StringUtil.obj2Str(pageContext.getAttribute("docId"));
    String prjCd = StringUtil.obj2Str(pageContext.getAttribute("prjCd"));
    String docName = StringUtil.obj2Str(pageContext.getAttribute("docName"));
    String staffName = StringUtil.obj2Str(pageContext.getAttribute("staffName"));
    String docFileUrl = "oframe/common/file/file-download.gv?docId=" + docId;
    boolean isEditable = Boolean.valueOf(StringUtil.obj2Str(pageContext.getAttribute("isEditable"))).booleanValue();
    String contextPath = StringUtil.obj2Str(pageContext.getAttribute("contextPath"));
    PageOfficeCtrl poCtrl1 = new PageOfficeCtrl(request);
    poCtrl1.setServerPage(contextPath + "/poserver.zz");//此行必须

    //隐藏标题栏
    poCtrl1.setTitlebar(false);
    //菜单栏
    //Office工具条
    if (isEditable) {
        poCtrl1.setMenubar(true);
        poCtrl1.setOfficeToolbars(true);
        // 允许word选择功能
        WordDocument doc = new WordDocument();
        doc.setDisableWindowSelection(false);
        poCtrl1.setWriter(doc);
    } else {
        poCtrl1.setMenubar(false);
        poCtrl1.setOfficeToolbars(false);
        // 禁止word选择功能
        WordDocument doc = new WordDocument();
        doc.setDisableWindowSelection(true);
        poCtrl1.setWriter(doc);
    }

    //隐藏自定义工具栏
    if (!isEditable) {
        poCtrl1.setCustomToolbar(true);
        // 打印文档
        poCtrl1.addCustomToolButton("打印文档", "printDoc", 6);
    } else {
        poCtrl1.setCustomToolbar(false);
    }
    poCtrl1.setSaveFilePage(contextPath + "/oframe/common/file/file-saveEditFile.gv?prjCd=" + prjCd + "&docId=" + docId);

    if (docName.endsWith("docx") || docName.endsWith("doc")) {
        // 打开文件
        if (isEditable) {
            poCtrl1.webOpen(contextPath + "/" + docFileUrl, OpenModeType.docNormalEdit, staffName);
        } else {
            poCtrl1.webOpen(contextPath + "/" + docFileUrl, OpenModeType.docReadOnly, staffName);
        }
    } else if (docName.endsWith("xls") || docName.endsWith("xlsx")) {
        if (isEditable) {
            // 打开文件
            poCtrl1.webOpen(contextPath + "/" + docFileUrl, OpenModeType.xlsNormalEdit, staffName);
        } else {
            // 打开文件
            poCtrl1.webOpen(contextPath + "/" + docFileUrl, OpenModeType.xlsReadOnly, staffName);
        }
    } else if (docName.endsWith("ppt") || docName.endsWith("pptx")) {
        if (isEditable) {
            // 打开文件
            poCtrl1.webOpen(contextPath + "/" + docFileUrl, OpenModeType.pptNormalEdit, staffName);
        } else {
            // 打开文件
            poCtrl1.webOpen(contextPath + "/" + docFileUrl, OpenModeType.pptReadOnly, staffName);
        }
    } else {
        return;
    }
    //此行必须
    poCtrl1.setTagId("PageOfficeCtrl1");

%>
<po:PageOfficeCtrl width="100%" height="99%" id="PageOfficeCtrl1"></po:PageOfficeCtrl>
<div id="notOpen" style="text-align: center;" class="mart20 hidden">
    <h3 style="font-size: 20px;">无法加载插件请确认插件已安装，安装后仍无法打开请点击以下链接</h3><br/>
    <a class="link" style="font-size: 18px;display: inline-block; margin-right: 10px;"
       href="<%=PageOfficeLink.openWindow(request,
                "/oframe/common/file/file-openDocEdit.gv?prjCd=" + prjCd + "&docId="+docId + "&isEditable=" + isEditable,
                "width=1024px;height=768px;title=文件打印")%>">
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
    }
    function saveDoc() {
        document.getElementById("PageOfficeCtrl1").WebSave();

    }
    function printDoc() {
        document.getElementById("PageOfficeCtrl1").ShowDialog(4);
    }
    $(document).ready(function () {
        var PageOfficeCtrl1 = $("#PageOfficeCtrl1");
        PageOfficeCtrl1[0].JsFunction_AfterDocumentOpened = "reloadInfo();";
        var ct001CanNotOpen = $("#notOpen");
        var isLoad = undefined;
        try {
            isLoad = document.getElementById("PageOfficeCtrl1").AllowCopy;
        } catch (e) {
            isLoad = undefined;
        }
        if (isLoad == undefined) {
            $("div", PageOfficeCtrl1).remove();
            PageOfficeCtrl1.append(ct001CanNotOpen);
            ct001CanNotOpen.removeClass("hidden");
        }
    });
</script>


