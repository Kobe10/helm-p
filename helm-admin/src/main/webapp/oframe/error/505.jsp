<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="java.io.StringWriter" %>
<%@ page import="com.shfb.oframe.core.util.common.StringUtil" %>
<%@ page import="com.shfb.oframe.core.util.exception.OframeException" %>
<%@ page import="org.springframework.web.util.HtmlUtils" %>
<%@ page import="com.shfb.oframe.core.util.properties.PropertiesUtil" %>
<%@ page import="com.shfb.oframe.core.web.util.RequestUtil" %>
<%@ page import="java.util.Map" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="org.apache.commons.logging.LogFactory" %>
<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="com.shfb.oframe.core.web.util.ResponseUtil" %>
<fmt:setBundle basename="webbundle.oframe" var="oframe" scope="page"/>
<%--
/**
 * 系统异常错误提示界面
 * @author linql 2012-03-10
 */
--%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page isErrorPage="true" %>

<%
    RequestUtil requestUtil = new RequestUtil(request);
    String contextPath = request.getContextPath();
    String fromUrl = request.getAttribute("javax.servlet.error.request_uri") + "?";
    fromUrl = fromUrl.substring(contextPath.length(), fromUrl.length());
    Map<String, Object> params = requestUtil.getRequestMap(request);
    for (Map.Entry<String, Object> temp : params.entrySet()) {
        fromUrl = fromUrl + temp.getKey() + "=" + temp.getValue() + "&";
    }
    // 其它错误打印访问错误页面
    Log logger = LogFactory.getLog(this.getClass());
    logger.info("进入错误页面,请求来源:" + fromUrl);
    // 输出错误日志
    String errMessage = exception.getMessage();
    if (exception instanceof javax.servlet.ServletException && "121004".equals(errMessage)) {
        if (fromUrl.startsWith("/mobile")) {
            ResponseUtil.printAjax(response, "{\"isSuccess\": false, \"needLogin\": true, \"errMsg\": \"账号未登陆或登陆超时\"}");
        }
        String referer = request.getHeader("Referer");
        // 验证引用页面是否存在，不存在提示非法进入
        if (StringUtil.isEmptyOrNull(referer) || fromUrl.startsWith("/oframe/main/main-init.gv")) {
            response.sendRedirect(contextPath + "/oframe/sysmg/sys001/login-init.gv?from="
                    + java.net.URLEncoder.encode(fromUrl, "utf-8"));
        }
        // 登录失效处理
        response.setStatus(Integer.valueOf(exception.getMessage()));
        // 填出提示框
        response.setStatus(500);
        String msg = java.net.URLEncoder.encode("用户未登录或登录失效!", "utf-8");
        msg = StringUtil.replace(msg, "+", "%20");
        response.addHeader("Error-Json", "{code:'" + errMessage + "',msg:'" + msg + "',script:''}");
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>居民住房改善系统--未登录或登录失效</title>
</head>
<body>
</body>
<script language="javascript" type="text/javascript">
    $(function () {
        if (top.alertMsg) {
            top.alertMsg.error("用户未登录或登录失效!");
        } else {
            alert("用户未登录或登录失效!");
        }
    });
</script>
</html>
<%
} else if (exception instanceof javax.servlet.ServletException && "121005".equals(errMessage)) {
    // 禁止非法输入地址
    response.setStatus(500);
    response.addHeader("Error-Json", "{code:'" + errMessage + "',msg:'禁止非法进入系统!',script:''}");
    // 弹出提示框
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <link href="<%=request.getContextPath()%>/nresources/themes/default/style.css"
          rel="stylesheet" type="text/css" media="screen"/>
    <title>居民住房改善系统--系统错误</title>
    <link href="<%=request.getContextPath()%>/nresources/css/core.css" rel="stylesheet" type="text/css" media="screen"/>
    <link href="<%=request.getContextPath()%>/nresources/themes/default/style.css" rel="stylesheet" type="text/css"
          media="screen"/>
    <script src="${pageContext.request.contextPath}/oframe/plugin/jquery/jquery-1.7.2.min.js"
            type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/oframe/plugin/dwz/dwz.ui.js"
            type="text/javascript"></script>
</head>
<body>
</body>
<script language="javascript" type="text/javascript">
    $(function () {
        if (top.alertMsg) {
            top.alertMsg.error("禁止非法进入系统!");
        } else {
            alert("禁止非法进入系统!");
        }

    });
</script>
</html>
<%
} else {
    logger.error("访问页面【" + fromUrl + "】错误", exception);
    // 其他错误显示错误信息
    response.setStatus(500);
    response.addHeader("Error-Json", "{code:'500',msg:'系统错误!',script:''}");
    String errorCd = "500";
    String errorMsg = exception.getMessage();
    if (exception instanceof OframeException) {
        OframeException throwException = (OframeException) exception;
        errorCd = throwException.getErrCode();
        errorMsg = throwException.getErrMsg();
    }
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <link href="<%=request.getContextPath()%>/nresources/themes/default/style.css" rel="stylesheet" type="text/css"
          media="screen"/>
    <title>土地征收管理系统|系统错误</title>

</head>
<body class="center">
<div class="errorWrap centerBox">
    <h1 class="title">系统错误，错误编码：<%=errorCd%> 请联系管理员！</h1>

    <div class="advice">
        <ul>
            <li><a href="javascript:closeWindowTab();">关闭页面</a></li>
        </ul>
    </div>
</div>
<div style="margin:0px 10px; padding:0px 10px;">
    <%
        if (!PropertiesUtil.readBoolean("oframeweb", "com.shfb.oframe.core.web.production.model")) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            exception.printStackTrace(pw);
            String errDetail = sw.getBuffer().toString();
            errDetail = HtmlUtils.htmlEscape(errDetail);
    %>
    <pre>
        <code>
            <%=errDetail%>
        </code>
    </pre>
    <%
        }
    %>
</div>
<script type="text/javascript">
    function closeWindowTab() {
        if (window.parent && window.parent.navTab) {
            window.parent.navTab.closeCurrentTab();
        } else {
            window.close();
        }
    }
</script>
</body>
</html>
<%
    }
%>