<%--待签约处理--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page language="java" import="com.zhuozhengsoft.pageoffice.*" %>
<%@ page import="com.shfb.oframe.core.util.common.StringUtil" %>
<%@ page import="com.shfb.oframe.core.util.common.DateUtil" %>
<%@ taglib uri="http://java.pageoffice.cn" prefix="po" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<c:set var="docFileUrl" value="${docFileUrl}"/>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
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
<%
    String docFileUrl = StringUtil.obj2Str(pageContext.getAttribute("docFileUrl"));
    String contextPath = StringUtil.obj2Str(pageContext.getAttribute("contextPath"));
    PageOfficeCtrl poCtrl1 = new PageOfficeCtrl(request);
    poCtrl1.setServerPage(contextPath + "/poserver.zz");//此行必须
    poCtrl1.setTitlebar(false); //隐藏标题栏
    poCtrl1.setMenubar(false); //隐藏菜单栏
    poCtrl1.setOfficeToolbars(false);//隐藏Office工具条
    poCtrl1.setCustomToolbar(true);//隐藏自定义工具栏
    poCtrl1.addCustomToolButton("打印", "printDoc", 6);
//    poCtrl1.setAllowCopy(false);
    //打开文件
    poCtrl1.webOpen(contextPath + "/" + docFileUrl, OpenModeType.docReadOnly, "张三");
    String tagId = DateUtil.toStringYmdHmsWthHS(DateUtil.getSysDate());
    poCtrl1.setTagId(tagId); //此行必须
%>
<po:PageOfficeCtrl id="<%=tagId%>"/>
<div id="notOpen" style="text-align: center;" class="mart20 hidden">
    <h3 style="font-size: 20px;">无法加载插件请确认插件已安装，安装后仍无法打开请点击以下链接</h3><br/>
    <a class="link" style="font-size: 18px;display: inline-block; margin-right: 10px;"
       href="<%=PageOfficeLink.openWindow(request, "ct004-printDoc.gv?docFileUrl=" + docFileUrl, "width=1024px;height=768px;title=文件打印")%>">
        打开文档
    </a>
    <a class="link" style="font-size: 12px;display: inline-block; margin-right: 10px;"
       href="${pageContext.request.contextPath}/posetup.exe">插件下载 </a>
</div>
<script src="${pageContext.request.contextPath}/oframe/plugin/jquery/eland.jquery.min.js"
        type="text/javascript"></script>
<script type="text/javascript">
    function printDoc() {
        document.getElementById("<%=tagId%>").ShowDialog(4);
    }
    $(document).ready(function () {
        var PageOfficeCtrl1 = $("#<%=tagId%>");
        var ct004CanNotOpen = $("#notOpen");
        var isLoad = undefined;
        try {
            isLoad = document.getElementById("<%=tagId%>").AllowCopy;
        } catch (e) {
            isLoad = undefined;
        }
        if (isLoad == undefined) {
            $("div", PageOfficeCtrl1).remove();
            PageOfficeCtrl1.append(ct004CanNotOpen);
            ct004CanNotOpen.removeClass("hidden");
        }
    });
    function closeDoc() {
        var tempObj = document.getElementById("<%=tagId%>");
        if (tempObj && (typeof tempObj.close == 'function' )) {
            tempObj.Close();
        }
    }
</script>
</body>
</html>


