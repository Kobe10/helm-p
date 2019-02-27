<%--
  Created by IntelliJ IDEA.
  User: shfb_wang
  Date: 2015/5/17 0017 9:52
  Copyright(c) 北京四海富博计算机服务有限公司
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<html>
<head>
    <link href="${pageContext.request.contextPath}/oframe/themes/common/simple.css" rel="stylesheet" type="text/css"
          media="screen"/>
    <script src="${pageContext.request.contextPath}/oframe/plugin/jquery/jquery-1.7.2.min.js"></script>
    <script src="${pageContext.request.contextPath}/oframe/plugin/My97DatePicker/WdatePicker.js"
            type="text/javascript"></script>
</head>
<body>
<div style="min-height: 200px;position: relative;" class="clearfix">
    <form id="query">
        <div class="tree-bar">
            <span style="display: inline-block; margin-left: 10px;">查询参数：</span>
        </div>
        <input type="hidden" name="templateName" value="${templateName}">

        <div class="tree-context">
            <ul class="conditions clearfix">
                <c:forEach items="${returnList}" var="item" varStatus="varStatus">
                    <c:set var="_type" value="${item.type}"/>
                    <c:choose>
                        <c:when test="${_type == 'hidden'}">
                            <input type="hidden" name="${item._name}" value="${item._defaultValue}"/>
                        </c:when>
                        <c:when test="${_type == 'select'}">
                            <li style="width: 32%;">
                            <label style="width: 40%;display: inline-block;text-align: right;">${item._desc}:</label>
                            <span relName="${item.relName}">
                                <oframe:select prjCd="${param.prjCd}" cssClass="textInput ${item.cssClass}"
                                               collection="${allOptions[item._name]}"
                                               style="width: 50%;"
                                               name="${item._name}"
                                               value="${item._defaultValue}"/>
                            </span>
                        </c:when>
                        <c:otherwise>
                            <li style="width: 32%;">
                                <label style="width: 40%;display: inline-block;text-align: right;">${item._desc}:</label>
                                <input type="text" name="${item._name}"
                                       class="textInput ${item.cssClass}"
                                       dateFmt="${item.datefmt}"
                                       options='<c:out value="${item.options}"/>'
                                       style="width: 50%;"
                                       value="${item._defaultValue}"/>
                                </span>
                            </li>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
                <span style="clear: both;"></span>
            </ul>
        </div>
    </form>
    <div class="mar5" style="text-align: center;">
            <span class="btn" name="submitButton"
                  onclick="generateReport();">
                确定
            </span>
    </div>
</div>
</body>
<script>
    function generateReport() {
        $("select").each(function () {
            var text = $(this).find("option:selected").text();
            var relName = $(this).closest("span").attr("relName");
            $("input[name=" + relName + "]").val(text);
        });
        window.parent.rpt001.generateReport();
    }
    $(document).ready(function () {
        $("input.date").bind("focus", function () {
            WdatePicker({dateFmt: $(this).attr("dateFmt")});
        });
        $("select").bind("change", function () {
            var text = $(this).find("option:selected").text();
            var relName = $(this).closest("span").attr("relName");
            $("input[name=" + relName + "]").val(text);
        });
        $("body").delegate(".required", "blur", function () {
            var $this = $(this);
            if ($this.val() == "") {
                $this.addClass("error");
            } else {
                $this.removeClass("error");
            }
        });
        // 首次加载自动执行报表生成
        generateReport();
    });
</script>
</html>
