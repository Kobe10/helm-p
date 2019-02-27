<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="po" uri="http://java.pageoffice.cn" %>
<%@ page import="com.shfb.oframe.core.util.common.StringUtil" %>
<%@ page import="com.zhuozhengsoft.pageoffice.PageOfficeCtrl" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<%
    String contextPath = StringUtil.obj2Str(pageContext.getAttribute("contextPath"));
    PageOfficeCtrl poCtrl1 = new PageOfficeCtrl(request);
    poCtrl1.setServerPage(contextPath + "/poserver.zz");//此行必须
    poCtrl1.setTitlebar(false); //隐藏标题栏
    poCtrl1.setMenubar(false); //隐藏菜单栏
    poCtrl1.setOfficeToolbars(false);//隐藏Office工具条
    poCtrl1.setCustomToolbar(false);
    poCtrl1.setJsFunction_AfterDocumentOpened("evalFun()");
    String tagId = request.getParameter("tagId");
    if (StringUtil.isEmptyOrNull(tagId)) {
        tagId = "pageOfficeCtrl";
    }
    poCtrl1.setTagId(tagId); //此行必须
%>
<po:PageOfficeCtrl width="100%" height="99%" id="<%=tagId%>"></po:PageOfficeCtrl>
<script type="text/javascript">
    /**
     * 打印文档
     */
    function printDoc() {
        try {
            var printFlag = '${param.printFlag}';
            var PageOfficeCtrl1 = document.getElementById("<%=tagId%>");
            if (printFlag == "2") {
                PageOfficeCtrl1.PrintOut(false);
            } else if (printFlag == "1") {
                PageOfficeCtrl1.ShowDialog(4);
            }
        } catch (e) {
            alertMsg.warn("您的浏览器不支持直接打印文档,请点击[打开文档]链接后进行打印!")
        }
    }
    function evalFun() {
        eval('${param.exec}')
    }
</script>