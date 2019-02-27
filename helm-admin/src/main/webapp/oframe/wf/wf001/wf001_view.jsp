<%--发起任务通用处理模板--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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
    <link href="${pageContext.request.contextPath}/oframe/plugin/jquery/css/colorPicker.css" rel="stylesheet"
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
    <script src="${pageContext.request.contextPath}/oframe/plugin/jquery/jquery.colorPicker.js"
            type="text/javascript"></script>
    <%--树形引用结构--%>
    <script src="${pageContext.request.contextPath}/oframe/plugin/ztree/js/jquery.ztree.all-3.5.js"
            type="text/javascript"></script>
    <%--富文本编辑器--%>
    <script type="text/javascript" charset="utf-8"
            src="${pageContext.request.contextPath}/oframe/plugin/ueditor/ueditor.config.js"></script>
    <script type="text/javascript" charset="utf-8"
            src="${pageContext.request.contextPath}/oframe/plugin/ueditor/ueditor.all.min.js"></script>
    <script type="text/javascript" charset="utf-8"
            src="${pageContext.request.contextPath}/oframe/plugin/ueditor/third-party/video-js/video.js"></script>
    <!--建议手动加在语言，避免在ie下有时因为加载语言失败导致编辑器加载失败-->
    <!--这里加载的语言文件会覆盖你在配置项目里添加的语言类型，比如你在配置项目里配置的是英文，这里加载的中文，那最后就是中文-->
    <script type="text/javascript" charset="utf-8"
            src="${pageContext.request.contextPath}/oframe/plugin/ueditor/lang/zh-cn/zh-cn.js"></script>

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
            <div class="page unitBox">
                <%--内容开始--%>
                <div style="text-align: left; margin-left: auto; margin-right: auto; margin: 5px;">
                    <input type="hidden" name="backUrl" value="${backUrl}">
                    <input type="hidden" name="procInsId" value="${procInsId}">
                    <input type="hidden" id="sysPrjCd" name="prjCd"
                           value="${PROC_INST_INFO.ProcInsInfo.Variables.procPrjCd}"/>
                    <c:set var="procPrjCd"
                           value="${PROC_INST_INFO.ProcInsInfo.Variables.procPrjCd}"/>

                    <div class="tabs">
                        <div class="tabsHeader" style="border-bottom: 1px solid #3d91c8;">
                            <div class="tabsHeaderContent">
                                <ul>
                                    <li>
                                        <a href="javascript:void(0);"><span>任务处理</span></a>
                                    </li>
                                    <li onclick="wf001.showProcPic();">
                                        <a href="javascript:void(0);"><span>运转图例</span></a>
                                    </li>
                                </ul>
                            </div>
                        </div>
                        <div class="tabsContent" id="procContext">
                            <div layoutH="50">
                                <%--请求信息--%>
                                <div class="panelContainer" style="display: block;margin: 0px;">
                                    <div class="panel">
                                        <h1>[<oframe:staff staffCode="${PROC_INST_INFO.ProcInsInfo.procStUser}"/>] 于
                                            [${PROC_INST_INFO.ProcInsInfo.procStTimeDisp}]
                                            发起 [${PROC_INST_INFO.ProcInsInfo.procDefName}] 流程</h1>
                                        <c:set var="startPage" value="${PROC_INST_INFO.ProcInsInfo.viewPage}${''}"/>
                                        <c:if test="${startPage != ''}">
                                            <c:choose>
                                                <c:when test="${fn:indexOf(startPage, '?') == -1}">
                                                    <c:set var="startPage" value="${startPage}?prjCd=${procPrjCd}"/>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:set var="startPage" value="${startPage}&prjCd=${procPrjCd}"/>
                                                </c:otherwise>
                                            </c:choose>
                                            <jsp:include page="${startPage}"/>
                                        </c:if>
                                        <c:if test="${procPage != ''}">
                                            ${procPage}
                                        </c:if>
                                    </div>
                                </div>
                                <jsp:include page="/oframe/wf/wf001/wf001-viewHis.gv?procInsId=${procInsId}"/>
                                <%--处理中节点--%>
                                <c:forEach items="${runTaskInfos}" var="runTask" varStatus="varStatus">
                                    <div class="panelContainer" style="display: block;margin: 0px;">
                                        <div class="panel">
                                            <h1>
                                                [<oframe:staff staffCode="${runTask.RuTaskInfo.assignee}"/>]
                                                处理任务 [${runTask.RuTaskInfo.taskName}]，
                                                任务到达时间 [${runTask.RuTaskInfo.createTimeDisp}]，
                                                任务滞留时间：${runTask.RuTaskInfo.delayDays}(天)
                                            </h1>
                                            <c:if test="${runTask.RuTaskInfo.assignee == currentStaff}">
                                                <input type="hidden" name="DO_TASK_ID"
                                                       value="${runTask.RuTaskInfo.taskId}">
                                                <c:set var="editPage" value="${runTask.RuTaskInfo.editPage}${''}"/>
                                                <c:if test="${editPage != ''}">
                                                    <c:choose>
                                                        <c:when test="${fn:indexOf(editPage, '?') == -1}">
                                                            <c:set var="editPage"
                                                                   value="${editPage}?R_I=${varStatus.index}"/>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <c:set var="editPage"
                                                                   value="${editPage}&R_I=${varStatus.index}"/>
                                                        </c:otherwise>
                                                    </c:choose>
                                                    <jsp:include page="${editPage}"/>
                                                </c:if>
                                                <c:if test="${runTask.RuTaskInfo.runPage != ''}">
                                                    <%--表单解析之后的代码片段--%>
                                                    ${runTask.RuTaskInfo.runPage}
                                                    <div class="panelBar">
                                                        <ul class="toolBar">
                                                            <c:forEach items="${operaList}" var="item"
                                                                       varStatus="varStatus">
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
                                            </c:if>
                                        </div>
                                    </div>
                                </c:forEach>
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

