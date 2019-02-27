<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="tree-bar">
    <span class="mart5" onclick="$(this).closeTip();"
          style="display: block; float: right; margin:0 10px 5px 0; cursor: pointer;">关闭</span>
    <span class="mart5" onclick="sys011.clearCond(this);"
          style="display: block; float: right; margin:0 10px 5px 0; cursor: pointer;">清除</span>
</div>
<div style="width:100%; height:100%; overflow:auto;">
    <ul id="sys011RhtTree" class="ztree"></ul>
</div>
<script>
    $(document).ready(function () {
        sys011.initFullTree();
    });
</script>