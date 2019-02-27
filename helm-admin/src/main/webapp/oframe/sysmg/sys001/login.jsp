<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <title>百万庄安居工程管理驾驶舱|登录</title>
    <script src="${pageContext.request.contextPath}/oframe/plugin/jquery/jquery-1.7.2.js"
            type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/oframe/plugin/jquery/jquery.validate.js"
            type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/oframe/plugin/dwz/dwz.min.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/oframe/plugin/dwz/dwz.regional.zh.js"
            type="text/javascript"></script>
    <%--系统基础框架类--%>
    <script id="systemScript" contextPath="${pageContext.request.contextPath}/"
            src="${pageContext.request.contextPath}/oframe/plugin/system/system.min.js"
            type="text/javascript"></script>
    <oframe:link href="${pageContext.request.contextPath}/oframe/themes/common/core.css" rel="stylesheet"
                 type="text/css" media="screen"/>
    <link href="${pageContext.request.contextPath}/oframe/themes/blue/style.css" rel="stylesheet" type="text/css"
          media="screen"/>
    <script src="${pageContext.request.contextPath}/oframe/plugin/jquery/jquery.slides.min.js"
            type="text/javascript"></script>
    <link href="${pageContext.request.contextPath}/oframe/sysmg/sys001/css/login.css" rel="stylesheet" type="text/css"/>
    <link href="${pageContext.request.contextPath}/oframe/sysmg/sys001/css/login_min.css" rel="stylesheet"
          type="text/css"
          media="screen and (max-width:960px)"/>
    <script type="text/javascript">
        $(function () {
            DWZ.init("${pageContext.request.contextPath}/oframe/plugin/dwz/dwz.frag.xml", {});
        });
    </script>
</head>
<body class="login" style="width: 100%;">
<!--重新输入密码页面-->
<div id="modifyPwdDiv">
    <div class="containner" id="modifyPwdDivSon">
        <p style="margin: 25px;">
            密码已过期，请修改您的密码重新登录</p>

        <div class="line"></div>
        <!--表单样式-->
        <form id="modifyPwdForm" action="" method="post" style="margin-top: 40px;">
            <div>
                <span>原密码：</span>
                <input type="password" class="form-control required" name="oldPwd">
            </div>
            <div>
                <span>新密码：</span>
                <input type="password" class="form-control required" name="newPwd">
            </div>
            <div>
                <span>确认新密码：</span>
                <input type="password" class="form-control required" name="reNewPwd">
            </div>
            <p id="modifyErrMsg"></p>
            <!--确认，取消按钮-->
            <div class="btn-group">
                <div class="btn btn-primary" id="repassConfirm">确认</div>
                <div class="btn btn-success" id="repassCancel">取消</div>
            </div>
        </form>
    </div>
</div>


<!--常规登录页面-->
<div class="containner">
    <h1 class="logo"></h1>

    <div id="slides" class="img pull-left">
        <img class="hidden" src="${pageContext.request.contextPath}/oframe/sysmg/sys001/images/login-img-1.jpg">
        <img class="hidden" src="${pageContext.request.contextPath}/oframe/sysmg/sys001/images/login-img-2.jpg">
        <img class="hidden" src="${pageContext.request.contextPath}/oframe/sysmg/sys001/images/login-img-3.jpg">
    </div>
    <div class="login-box pull-right">
        <h4>欢迎登录</h4>

        <div class="wrapper">
            <form method="post" id="frm" class="required-validate"
                  action="${pageContext.request.contextPath}/oframe/sysmg/sys001/login-staffLogin.gv">
                <input type="hidden" name="from" value="${param.from}"/>
                <input type="hidden" name="loginAccept" value=""/>

                <div class="form-group">
                    <label class="icon-1"></label>
                    <input type="text" class="form-control" placeholder="请输入用户名" name="systemUserCode"
                           onkeydown="if(event.keyCode == 13){document.forms[1].password.focus()}" tabindex="1"
                           id="systemUserCode">
                </div>
                <div class="form-group">
                    <label class="icon-2"></label>
                    <!--验证错误时给input加class= form-control-error -->
                    <input type="password" class="form-control" placeholder="请输入密码" name="password"
                           onkeydown="if(event.keyCode == 13){document.forms[1].validate.focus()}" tabindex="2"
                           id="password">
                </div>
                <div class="form-group">
                    <label class="icon-3">
                        <input style="opacity:0; width:5px; height:5px;" id="item_input">
                    </label>
                    <input type="text" class="form-control validate-img" placeholder="请输验证码" name="validate"
                           onkeydown="if(event.keyCode == 13){login.check(); stopEvent(event);}" tabindex="3"
                           id="validate">
                    <img id="validateImg" onclick="login.reloadImg()"
                         src="${pageContext.request.contextPath}/oframe/sysmg/sys001/login-randomImage.gv">
                </div>
                <!--用户名和密码错误验证的提示-->
                <div>
                    <p class="error-tips" id="errorTips"></p>
                </div>

                <!--保持登录功能-->
                <div class="stayContainer">
                    <label><input type="checkbox" tabindex="4" id="stayLogin" name="stayLogin">
                        <span> 保持登录</span>
                    </label>
                </div>
                <div class="form-group" style="position:relative; z-index:0;margin: 0 0 20px 0;">
                    <button type="button" class="btn btn-block btn-primary" tabindex="5" onclick="login.check();">登&nbsp;&nbsp;&nbsp;&nbsp;录
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>
<div class="page-footer">Copyright ©2013- <a href="http://www.bjshfb.com/" target="_blank">北京四海富博计算机服务有限公司</a></div>

</body>
<oframe:script type="text/javascript" src="${pageContext.request.contextPath}/oframe/sysmg/sys001/js/login.js"/>
<oframe:script type="text/javascript" src="${pageContext.request.contextPath}/oframe/sysmg/sys001/js/respond.js"/>
</html>
