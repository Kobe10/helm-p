<%--待签约处理--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page language="java" import="com.shfb.oframe.core.util.common.StringUtil" %>
<%@ page import="com.zhuozhengsoft.pageoffice.OpenModeType" %>
<%@ page import="com.zhuozhengsoft.pageoffice.PageOfficeCtrl" %>
<%@ page import="com.zhuozhengsoft.pageoffice.PageOfficeLink" %>
<%@ page import="com.zhuozhengsoft.pageoffice.wordwriter.WordDocument" %>
<%@ page import="com.shfb.oframe.core.web.session.SessionUtil" %>
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
<c:set var="docFileUrl" value="${docFileUrl}"/>
<c:set var="docObjId" value="${docObjId}"/>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<%
    String docFileUrl = StringUtil.obj2Str(pageContext.getAttribute("docFileUrl"));
    SessionUtil sessionUtil = new SessionUtil(request);
    String sessionKey = sessionUtil.getSessionKey();
    String contextPath = StringUtil.obj2Str(pageContext.getAttribute("contextPath"));
    String docObjId = StringUtil.obj2Str(pageContext.getAttribute("docObjId"));
    PageOfficeCtrl poCtrl1 = new PageOfficeCtrl(request);
    poCtrl1.setServerPage(contextPath + "/poserver.zz");//此行必须
    poCtrl1.setTitlebar(false); //隐藏标题栏
    poCtrl1.setMenubar(false); //隐藏菜单栏
    poCtrl1.setOfficeToolbars(false);//隐藏Office工具条
    poCtrl1.setCustomToolbar(false);
    // 禁止word选择功能
    WordDocument doc = new WordDocument();
    doc.setDisableWindowSelection(true);
    poCtrl1.setWriter(doc);
    String docName = docFileUrl;
    String staffName = "张三";
    // 打开文件
    if (docName.endsWith("docx") || docName.endsWith("doc")) {
        // 打开文件
        poCtrl1.webOpen(contextPath + "/" + docFileUrl, OpenModeType.docReadOnly, staffName);
    } else if (docName.endsWith("xls") || docName.endsWith("xlsx")) {
        // 打开文件
        poCtrl1.webOpen(contextPath + "/" + docFileUrl, OpenModeType.xlsReadOnly, staffName);
    } else if (docName.endsWith("ppt") || docName.endsWith("pptx")) {
        // 打开文件
        poCtrl1.webOpen(contextPath + "/" + docFileUrl, OpenModeType.pptReadOnly, staffName);
    } else {
        return;
    }
    poCtrl1.setTagId(docObjId); //此行必须
%>
<po:PageOfficeCtrl width="100%" height="99%" id="${docObjId}"></po:PageOfficeCtrl>
<div id="notOpen" style="text-align: center;" class="mart20 hidden">
    <h3 style="font-size: 20px;">无法加载插件请确认插件已安装，安装后仍无法打开请点击以下链接</h3><br/>
    <a class="link" style="font-size: 18px;display: inline-block; margin-right: 10px;"
       href="<%=PageOfficeLink.openWindow(request, "ph013-printDoc.gv?LOGIN_ACCEPT=" + sessionKey +"&docFileUrl=" + docFileUrl, "width=1024px;height=768px;title=文件打印")%>">
        打开文档
    </a>
    <a class="link" style="font-size: 12px;display: inline-block; margin-right: 10px;"
       href="${pageContext.request.contextPath}/posetup.exe">插件下载 </a>
</div>
<script src="${pageContext.request.contextPath}/oframe/plugin/jquery/eland.jquery.min.js"
        type="text/javascript"></script>
<script type="text/javascript">

    // 0:不自动打印， 1: 弹框打印, 2:自动打印
    var printFlag = "${param.printFlag}";

    /**
     * 打印排号单
     */
    function doPrintDoc(inPrintFlag) {
        try {
            var PageOfficeCtrl1 = document.getElementById("${docObjId}");
            PageOfficeCtrl1.Activate();
            if (inPrintFlag == "2") {
                PageOfficeCtrl1.PrintOut(false);
            } else if (inPrintFlag == "1") {
                PageOfficeCtrl1.ShowDialog(4);
            } else {
                PageOfficeCtrl1.ShowDialog(4);
            }
        } catch (e) {
            alertMsg.warn("您的浏览器不支持直接打印文档,请点击[打开文档]链接后进行打印!")
        }
    }

    /**
     * 打印排号单
     */
    function printDoc() {
        if (printFlag != "") {
            doPrintDoc(printFlag);
        }
    }

    $(document).ready(function () {
        var PageOfficeCtrl1 = $("#" + "${docObjId}");
        PageOfficeCtrl1[0].JsFunction_AfterDocumentOpened = "printDoc();";
        var ct005CanNotOpen = $("#notOpen");
        var isLoad = undefined;
        try {
            isLoad = document.getElementById("${docObjId}").AllowCopy;
        } catch (e) {
            isLoad = undefined;
        }
        if (isLoad == undefined) {
            $("div", PageOfficeCtrl1).remove();
            PageOfficeCtrl1.append(ct005CanNotOpen);
            ct005CanNotOpen.removeClass("hidden");
        }
    });
    function closeDoc() {
        var tempObj = document.getElementById("${docObjId}");
        if (tempObj && (typeof tempObj.close == 'function' )) {
            tempObj.Close();
        }
    }
</script>
</body>
</html>


