<%--待签约处理--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<c:set var="docFileUrl" value="${docFileUrl}"/>
<div class="panelBar">
    <ul class="toolBar">
        <li onclick="ph013_print.printRpt('${docObjId}');">
            <a class="print" href="javascript:void(0);"><span>打印</span></a>
        </li>
        <c:if test="${param.dowRht != null && param.dowRht != ''}">
            <oframe:op prjCd="${param.prjCd}" template="li" cssClass="export"
                       name="下载" rhtCd="${param.dowRht}"
                       onClick="ph013_print.downRpt();"/>
        </c:if>
    </ul>
</div>
<div>
    <iframe id="ph013PrintIFrame_${docObjId}"
            style="border: 0;margin: 0; width: 100%;"
            layoutH="${subLayoutH}"
            src="${pageContext.request.contextPath}/eland/ph/ph013/ph013-viewDoc.gv?docFileUrl=${docFileUrl}&docObjId=${docObjId}&printFlag=${param.printFlag}">
    </iframe>
</div>
<script type="text/javascript">
    var ph013_print = {
        /**
         * 打印协议
         */
        printRpt: function (docObjId) {
            try {
                var iframeWin = document.getElementById("ph013PrintIFrame_${docObjId}").contentWindow;
                iframeWin.doPrintDoc("1");
            } catch (e) {
                alertMsg.warn("您的浏览器不支持直接打印文档,请点击[打开文档]链接后进行打印!")
            }
        },
        /**
         * 下载报表
         */
        downRpt: function (docName) {
            var url = getGlobalPathRoot() + "eland/ph/ph013/ph013-downDoc.gv?prjCd=" + getPrjCd()
                    + "&docFileUrl=${docFileUrl}"
                    + "&docName=${param.docName}";
            window.open(url);
        }
    }
</script>
