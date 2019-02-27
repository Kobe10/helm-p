<%--
  Created by IntelliJ IDEA.
  User: shfb_wang
  Date: 2015/5/17 0017 9:52
  Copyright(c) 北京四海富博计算机服务有限公司
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>

<html>
<head>
    <link href="${pageContext.request.contextPath}/oframe/themes/common/core.css" rel="stylesheet" type="text/css"
          media="screen"/>
    <link href="${pageContext.request.contextPath}/oframe/themes/blue/style.css" rel="stylesheet"
          type="text/css" media="screen"/>
    <link href="${pageContext.request.contextPath}/oframe/themes/common/print.css" rel="stylesheet" type="text/css"
          media="print"/>
    <script src="${pageContext.request.contextPath}/oframe/plugin/jquery/eland.jquery.min.js"
            type="text/javascript"></script>
    <script id="systemScript" contextPath="${pageContext.request.contextPath}/"
            src="${pageContext.request.contextPath}/oframe/plugin/system/system.min.js"
            type="text/javascript"></script>
</head>
<body style="background-color: white;">
<div class="mar5" style="position: relative;">
    <div id="rpt001RightDiv" style="position: relative;">
        <div class="panelBar">
            <ul class="toolBar">
                <li onclick="rpt001.showOrHideCond();">
                    <a class="find" href="javascript:void(0)"><span>条件定义</span></a>
                </li>
                <li onclick="rpt001.generateReport('pdf');">
                    <a class="reflesh" href="javascript:void(0)"><span>刷新报表</span></a>
                </li>
                <li>
                    <a class="export" href="javascript:void(0)">
                        <%--<span onclick="rpt001.doExport('excel');">导出EXCEL</span>--%>
                            <span onclick="rpt001.doExport('xlsx');">导出EXCEL</span>
                    </a>
                </li>
                <li>
                    <a class="export" href="javascript:void(0)">
                        <span onclick="rpt001.doExport('docx');">导出WORD</span>
                    </a>
                </li>
                <li>
                    <a class="export" href="javascript:void(0)">
                        <span onclick="rpt001.doExport('pdf');">导出PDF</span>
                    </a>
                </li>
            </ul>
        </div>
        <%--检索条件处理控制--%>
        <iframe name="rpt001IFrame" allowTransparency="false" id="rpt001IFrame" class="hidden"
                style="position: absolute;top: 35px;border:1px;width:100%;background-color:#f5f9fc;"
                src="${pageContext.request.contextPath}/oframe/report/rpt001/rpt001-condition.gv?templateName=${templateName}">
        </iframe>
        <%--内容--%>
        <iframe id="rpt001_ReportData" style="border: 0px solid #B3E4EB; width: 100%;"></iframe>
    </div>
</div>
</body>
<script type="text/javascript">
    var navTab = {
        getCurrentPanel: function () {
            return $(document);
        }
    }
    /**
     * Created by shfb_wang on 2015/5/17 0017.
     */
    var rpt001 = {
        /**
         * 显示和异常报表条件
         */
        showOrHideCond: function () {
            var rpt001IFrame = $("#rpt001IFrame", navTab.getCurrentPanel());
            if (rpt001IFrame.is(":visible")) {
                rpt001IFrame.hide();
            } else {
                rpt001IFrame.show();
            }
        },

        hideCond: function () {
            var rpt001IFrame = $("#rpt001IFrame", navTab.getCurrentPanel());
            rpt001IFrame.hide();
        },
        /**
         * 导出数据
         */
        doExport: function (exportType) {
            var url = getGlobalPathRoot() + "oframe/report/rpt001/rpt001-generateReport.gv?" + "${queryStr}";
            var packet = new AJAXPacket(url);
            var iframe = $(window.frames["rpt001IFrame"].document, navTab.getCurrentPanel());
            if (!rpt001.checkCondition(iframe)) {
                return;
            }
            var $form = $("#query", iframe).serializeArray();
            $form.push({name: "exportType", value: exportType});
            packet.data.data = $form;
            core.ajax.sendPacketHtml(packet, function (response) {
                        var data = eval("(" + response + ")");
                        var isSuccess = data.success;
                        if (isSuccess) {
                            var url = getGlobalPathRoot() + "oframe/common/file/file-down.gv?remoteFile=" + encodeURI(encodeURI(data.reportUrl));
                            window.open(url, "_blank");
                        } else {
                            alert(data.errMsg);
                        }
                    }
            );
        },
        /**
         * 验证
         * @param iframeDoc
         * @returns {boolean}
         */
        checkCondition: function (iframeDoc) {
            var errorCount = 0;
            $("#query", iframeDoc).find(".required").each(function () {
                var $this = $(this);
                if ("" == $this.val()) {
                    $this.addClass("error");
                    errorCount++;
                }
            });
            if (errorCount > 0) {
                var rpt001IFrame = $("#rpt001IFrame", navTab.getCurrentPanel());
                if (!rpt001IFrame.is(":visible")) {
                    rpt001IFrame.show();
                }
                alert("报表参数中红色标记条件必须填写");
                return false;
            }
            return true;
        },

        /**
         * 生成业务报表
         * @param formId
         * @param templateName
         */
        generateReport: function () {

            var url = getGlobalPathRoot() + "oframe/report/rpt001/rpt001-generateReport.gv?" + "${queryStr}";
            var packet = new AJAXPacket(url);
            var iframe = $(window.frames["rpt001IFrame"].document, navTab.getCurrentPanel());
            if (!rpt001.checkCondition(iframe)) {
                return;
            } else {
                rpt001.hideCond();
            }
            var $form = $("#query", iframe).serializeArray();
            $form.push({name: "exportType", value: "html"});
            packet.data.data = $form;
            core.ajax.sendPacketHtml(packet, function (response) {
                        var data = eval("(" + response + ")");
                        var isSuccess = data.success;
                        if (isSuccess) {
                            $("#rpt001_ReportData", navTab.getCurrentPanel()).attr("src", getGlobalPathRoot() + data.reportUrl);
                        }
                        else {
                            alertMsg.error(data.errMsg);
                        }
                    }
            );
        }
    };
    $("#rpt001_ReportData").height($(window).height() - 100);
    $("#rpt001IFrame").height($(window).height() / 2);
</script>
</html>
