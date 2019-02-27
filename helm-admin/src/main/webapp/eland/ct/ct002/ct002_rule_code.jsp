<%--预分方案使用规则配置--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title>Editor</title>
    <style type="text/css" media="screen">
        body {
            overflow: hidden;
        }

        #editor {
            margin: 0;
            position: absolute;
            top: 0;
            bottom: 0;
            left: 0;
            right: 0;
        }
    </style>
</head>
<body onload="initCode();">
<pre id="editor"></pre>
<oframe:script src="${pageContext.request.contextPath}/oframe/plugin/ace/ace.js" type="text/javascript"/>
<script>
    var editor = ace.edit("editor");
    editor.setTheme("ace/theme/chrome");
    document.getElementById('editor').style.fontSize = '16px';
    editor.getSession().setMode("ace/mode/groovy");
    function initCode() {
        var codeValue = window.parent.ct002.getCode();
        editor.setValue(codeValue);
    }
    function getCode() {
        return editor.getValue();
    }
</script>
</body>
</html>