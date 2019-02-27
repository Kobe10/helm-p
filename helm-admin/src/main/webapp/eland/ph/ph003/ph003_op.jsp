<%--院落信息展示页面--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<oframe:op prjCd="${param.prjCd}" template="li" cssClass=""
           name="修改居民信息" rhtCd="edit_old_hs_rht"
           onClick="ph003Op.editHouse('${param.hsId}')"/>
<li onclick="ph003Op.openTalkRecord('${param.hsId}')">居民工作日志</li>
<li onclick="ph003Op.openCalculate('${param.hsId}')">补偿款计算器</li>
<li onclick="ph003Op.hsSizeCtGive('${param.hsId}')">赠送安置面积</li>
<oframe:op prjCd="${param.prjCd}" template="li" cssClass=""
           name="删除居民信息" rhtCd="del_old_hs_rht"
           onClick="ph003Op.deleteHouse('${param.hsId}')"/>

