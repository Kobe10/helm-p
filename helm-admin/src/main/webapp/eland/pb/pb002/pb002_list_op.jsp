<%----%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<li onclick="pb002Op.openBuilding('${item.Row.BuildInfo.buildId}')">查看院落详情</li>
<li onclick="pb002Op.viewBuildFj('${item.Row.BuildInfo.buildId}')">管理院落附件</li>
<li onclick="pb002Op.browseBuildFj('${item.Row.BuildInfo.buildId}')">浏览整院附件</li>
<li onclick="pb002Op.exportGfRyMx('${item.Row.BuildInfo.buildId}')">导出购房人员</li>
<li onclick="pb002Op.exportGfrhj('${item.Row.BuildInfo.buildId}')">导出建筑户籍</li>
<c:if test="${item.Row.BuildInfo.buildType != '2'}">
    <li onclick="pb002Op.exportZjGhMx('${item.Row.BuildInfo.buildId}')">租金及过户费</li>
</c:if>
<li onclick="pb002Op.exportDxFsP('${item.Row.BuildInfo.buildId}')">定向房审批表</li>
<oframe:power prjCd="${param.prjCd}" rhtCd="delete_old_yard_rht">
    <li onclick="pb002Op.deleteBuild('${item.Row.BuildInfo.buildId}')">删除居民建筑</li>
</oframe:power>
