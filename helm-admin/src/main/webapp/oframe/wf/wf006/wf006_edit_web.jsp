<%--项目资料--%>
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

        #container {
            margin: 0;
            position: absolute;
            top: 0;
            bottom: 0;
            left: 0;
            right: 0;
        }
    </style>
</head>
<body>
<!-- 加载编辑器的容器 -->
<script src="${pageContext.request.contextPath}/oframe/plugin/jquery/eland.jquery.min.js"
        type="text/javascript"></script>
<script id="container" name="content" type="text/plain"></script>
<script type="text/javascript" charset="utf-8"
        src="${pageContext.request.contextPath}/oframe/plugin/ueditor/ueditor.config.js"></script>
<script type="text/javascript" charset="utf-8"
        src="${pageContext.request.contextPath}/oframe/plugin/ueditor/ueditor.all.js"></script>
<script type="text/javascript" charset="utf-8"
        src="${pageContext.request.contextPath}/oframe/plugin/ueditor/formdesign/shfb.formdesign.js"></script>
<script type="text/javascript" charset="utf-8"
        src="${pageContext.request.contextPath}/oframe/plugin/ueditor/formdesign/shfbHTML/js/oframedesign.js"></script>

<script>
    var editor = UE.getEditor('container', {
        toolshfb: true,//是否在toolbars显示，表单设计器的图标
        iframeCssUrl: "${pageContext.request.contextPath}/oframe/themes/ueditor.css",
        fullscreen: true,
        allowDivTransToP: false,
        disabledTableInTable: false,
        autoClearEmptyNode: false,
        enterTag: "br",
        sourceFirst: true,
        toolbars: [
            ['source', 'undo', 'redo', 'bold', 'italic', 'underline', '|',
                'customstyle', 'paragraph', 'fontfamily', 'fontsize', '|',
                'justifyleft', 'justifycenter', 'justifyright', 'justifyjustify', '|',
                'inserttable', 'deletetable', 'insertparagraphbeforetable', 'insertrow', 'deleterow', 'insertcol', 'deletecol', 'mergecells', 'mergeright', 'mergedown', 'splittocells', 'splittorows', 'splittocols', 'charts', '|',
                'print', 'preview', 'searchreplace']
        ]
    });
    //对编辑器的操作最好在编辑器ready之后再做
    editor.ready(function () {
        var codeValue = window.parent.wf006.getCode('${textarea}', '${content}');
        editor.setContent(codeValue);
        oframedesign.initCoverHeight($(editor.body));
    });
    function getCode() {
        return editor.getContent();
    }
</script>
</body>
</html>
