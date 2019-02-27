<%--建筑信息展示页面--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<li onclick="pb002Op.openBuilding('${param.buildId}')">查看院落详情</li>
<li onclick="pb002Op.viewBuildFj('${param.buildId}')">管理院落附件</li>
<li onclick="pb002Op.exportYardFj('${param.buildId}')">导出整院附件</li>
<li onclick="pb002Op.exportGfRyMx('${param.buildId}')">导出购房人员</li>
<li onclick="pb002Op.exportGfrhj('${param.buildId}')">导出院落户籍</li>
<c:if test="${param.buildType != '2'}">
    <li onclick="pb002Op.exportZjGhMx('${param.buildId}')">租金及过户费</li>
</c:if>
<li onclick="pb002Op.exportDxFsP('${param.buildId}')">定向房审批表</li>
<oframe:power prjCd="${param.prjCd}" rhtCd="delete_old_yard_rht">
    <li onclick="pb002Op.deleteBuild('${param.buildId}')">删除居民建筑</li>
</oframe:power>
