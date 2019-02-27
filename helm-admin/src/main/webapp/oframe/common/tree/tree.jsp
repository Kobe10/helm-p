<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="tree-bar">
    <input type="hidden" value="${treeData}" id="treeData">
    <input type="hidden" value="${treeCheck}" id="treeCheck">
    <span class="marr10" onclick="$(this).closeTip();"
          style="display: block; float: right; cursor: pointer;">关闭</span>
    <c:if test="${treeCheck != null}">
        <span class="marr5" onclick="tree.onCfmSlt(this);"
              style="display: block; float: right; cursor: pointer;">确定</span>
    </c:if>
</div>
<div style="width:100%; height:100%; overflow:auto;">
    <ul id="commonTreeContent" class="ztree"></ul>
</div>
<script type="text/javascript">
    tree.initFullTree();
</script>