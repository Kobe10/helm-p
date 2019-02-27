<%--建筑信息展示页面--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<li onclick="ph002Op.openBuilding('${param.buildId}')">查看建筑详情</li>
<li onclick="ph002Op.browseBuildFj('${param.buildId}')">管理1院落附件</li>
<li onclick="ph002Op.exportYardFj('${param.buildId}')">导出整院附件</li>
<li onclick="ph002Op.exportGfRyMx('${param.buildId}')">导出购房人员</li>
<li onclick="ph002Op.exportGfrhj('${param.buildId}')">导出整院户籍</li>
<c:if test="${param.buildType != '2'}">
    <li onclick="ph002Op.exportZjGhMx('${param.buildId}')">租金及过户费</li>
</c:if>
<li onclick="ph002Op.exportDxFsP('${param.buildId}')">定向房审批表</li>
<oframe:op prjCd="${param.prjCd}" template="li" cssClass=""
           name="删除居民院落" rhtCd="delete_old_yard_rht"
           onClick="ph002Op.deleteBuild('${param.buildId}')"/>
