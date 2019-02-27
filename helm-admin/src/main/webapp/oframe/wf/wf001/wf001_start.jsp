<%--发起任务通用处理模板--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>居民住房改善信息系统</title>

    <link href="${pageContext.request.contextPath}/oframe/themes/common/core.css" rel="stylesheet"
          type="text/css" media="screen"/>
    <link href="${pageContext.request.contextPath}/oframe/themes/blue/style.css" rel="stylesheet"
          type="text/css" media="screen"/>
    <!--[if IE]>
    <link href="${pageContext.request.contextPath}/oframe/themes/css/ieHack.css" rel="stylesheet" type="text/css"
          media="screen"/>
    <![endif]-->
    <link href="${pageContext.request.contextPath}/oframe/plugin/ztree/zTreeStyle/zTreeStyle.css" rel="stylesheet"
          type="text/css" media="screen"/>
    <link href="${pageContext.request.contextPath}/oframe/plugin/jquery/css/jquery.autocomplete.css" rel="stylesheet"
          type="text/css" media="screen"/>
    <link href="${pageContext.request.contextPath}/oframe/plugin/uploadify/css/uploadify.css" rel="stylesheet"
          type="text/css" media="screen"/>
    <!--[if lte IE 9]>
    <script src="${pageContext.request.contextPath}/oframe/js/speedup.js" type="text/javascript"></script>
    <![endif]-->
    <script src="${pageContext.request.contextPath}/oframe/plugin/jquery/eland.jquery.min.js"
            type="text/javascript"></script>
    <!--这里加载的语言文件会覆盖你在配置项目里添加的语言类型，比如你在配置项目里配置的是英文，这里加载的中文，那最后就是中文-->
    <script src="${pageContext.request.contextPath}/oframe/plugin/jquery/jquery.autocomplete.js"
            type="text/javascript"></script>
    <%--树形引用结构--%>
    <script src="${pageContext.request.contextPath}/oframe/plugin/ztree/js/jquery.ztree.all-3.5.js"
            type="text/javascript"></script>
    <!-- 可以用dwz.min.js替换前面全部dwz.*.js (注意：替换是下面dwz.regional.zh.js还需要引入)-->
    <script src="${pageContext.request.contextPath}/oframe/plugin/dwz/dwz.min.js" type="text/javascript"></script>
    <!--项目图形-->
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/oframe/plugin/raphael/raphael-min.js"></script>
    <oframe:script type="text/javascript"
                   src="${pageContext.request.contextPath}/oframe/plugin/raphael/raphael.entity.js"/>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/oframe/plugin/raphael/raphael.pan-zoom.js"></script>
    <%--异步文件上传--%>
    <oframe:script type="text/javascript" charset="UTF-8"
                   src="${pageContext.request.contextPath}/oframe/plugin/ajaxfileupload/ajaxfileupload.js"/>
    <script src="${pageContext.request.contextPath}/oframe/plugin/uploadify/scripts/jquery.uploadify.js"
            type="text/javascript"></script>
    <!--自定义引入js-->
    <script id="systemScript" contextPath="${pageContext.request.contextPath}/"
            src="${pageContext.request.contextPath}/oframe/plugin/system/system.min.js"
            type="text/javascript"></script>
    <oframe:script src="${pageContext.request.contextPath}/oframe/common/form/js/form.js" type="text/javascript"/>
    <oframe:script src="${pageContext.request.contextPath}/oframe/plugin/json/json-patch.min.js"
                   type="text/javascript"/>
    <oframe:script src="${pageContext.request.contextPath}/oframe/plugin/json/json-patch-duplex.js"
                   type="text/javascript"/>
    <oframe:script type="text/javascript" src="${pageContext.request.contextPath}/oframe/wf/wf001/js/wf001.js"/>
    <style type="text/css">
        .panel {
            border: 0;
        }
        .panel .panelHeader {
            border: 1px solid #e4e4e4;
        }
    </style>
</head>
<body style="background-color: white;">
<div style="top: 0px !important;" id="container">
    <div id="navTab" class="tabsPage">
        <div style="display: none;" class="tabsPageHeader hidden">
            <div class="tabsPageHeaderContent">
                <ul class="navTab-tab selected">
                    <li tabid="wdsy" class="main"><a href="javascript:"><span><span>发起流程</span></span></a></li>
                </ul>
            </div>
        </div>
        <div class="navTab-panel tabsPageContent layoutBox">
            <input type="hidden" id="sysPrjCd" name="prjCd" value="${prjCd}"/>
            <div class="page unitBox">
                <div style="text-align: left; margin-left: auto; margin-right: auto; margin: 5px;">
                    <div class="tabs">
                        <div class="tabsHeader" style="border-bottom: 1px solid #3d91c8;">
                            <div class="tabsHeaderContent">
                                <ul>
                                    <li>
                                        <a href="javascript:void(0);"><span>发起流程</span></a>
                                    </li>
                                    <li onclick="wf001.showProcPic();">
                                        <a href="javascript:void(0);"><span>流程图例</span></a>
                                    </li>
                                </ul>
                            </div>
                        </div>
                        <div class="tabsContent" id="procContext">
                            <div layoutH="50">
                                <input type="hidden" name="procInsId" value="">
                                <input type="hidden" name="procDefId" value="${procDefInfo.ProcDefInfo.procDefId}">
                                <input type="hidden" name="procDefKey" value="${procDefInfo.ProcDefInfo.procDefKey}">
                                <input type="hidden" name="procPrjCd" value="${prjCd}">
                                <input type="hidden" name="busiKey" value="${busiKey}">
                                <input type="hidden" name="backUrl" value="${backUrl}">
                                <div id="_startFormRequestData">
                                    <c:forEach items="${requestData}" var="item">
                                        <input type="hidden" name="rq_${item.key}"
                                               value="<c:out value='${item.value}'/>">
                                    </c:forEach>
                                </div>
                                <%--腾退发起基本信息--%>
                                <div class="panelContainer" style="display: block;margin: 0px;">
                                    <div class="panel">
                                        <h1>[<oframe:staff staffId="${startStaff}"/>] 于 [${startDate}] 发起 [${procDefInfo.ProcDefInfo.procDefName}] 流程</h1>
                                        <c:if test="${url != null && url !=''}">
                                            <jsp:include page="${url}">
                                                <jsp:param name="prjCd" value="${prjCd}"/>
                                            </jsp:include>
                                        </c:if>
                                        <c:if test="${article != null && article !=''}">
                                            ${article}
                                            <div class="panelBar">
                                                <ul class="toolBar">
                                                    <c:forEach items="${operaList}" var="item" varStatus="varStatus">
                                                        <c:if test="${item.OperationDef.operRht == 'true'}">
                                                            <oframe:op rhtCd="${item.OperationDef.operRhtCd}"
                                                                       prjCd="${param.prjCd}"
                                                                       onClick="${item.OperationDef.operOnClick}"
                                                                       cssClass="${item.OperationDef.operClass}"
                                                                       name="${item.OperationDef.operName}"
                                                                       template="${item.OperationDef.operTemplate}"/>
                                                        </c:if>
                                                    </c:forEach>
                                                </ul>
                                            </div>
                                        </c:if>
                                    </div>
                                </div>
                            </div>
                            <div layoutH="50">
                                <div layoutH="60" id="procImgContain"></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
<script type="text/javascript">
    DWZ.init("${pageContext.request.contextPath}/oframe/plugin/dwz/dwz.frag.xml", {
        // 弹出登录对话框
        statusCode: {ok: 200, error: 300, timeout: 301},
        debug: false,
        callback: function () {
            initEnv();
        }
    });
    $(document).ready(function () {
        $(":input", navTab.getCurrentPanel()).each(function () {
            var thisObj = $(this);
            if (thisObj.attr("relName") && thisObj.attr("name")) {
                thisObj.attr("name", thisObj.attr("relName"));
            }
        });
    });
    ${operDefCode}
</script>
</html>
