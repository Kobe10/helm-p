<%--院落信息展示页面--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<oframe:op prjCd="${param.prjCd}" template="li" cssClass="" name="居民详细信息" rhtCd="old_hs_detail_rht"
           onClick="ph003Op.openHs('${item.Row.HouseInfo.hsId}')"/>
<oframe:op prjCd="${param.prjCd}" template="li" cssClass="" name="管理居民材料" rhtCd="mng_old_hs_att"
           onClick="ph003Op.openOash('${item.Row.HouseInfo.hsId}')"/>
<oframe:op prjCd="${param.prjCd}" template="li" cssClass="" name="居民工作日志" rhtCd="hsrd_normal_rht"
           onClick="ph003Op.openTalkRecord('${item.Row.HouseInfo.hsId}')"/>
<oframe:op prjCd="${param.prjCd}" template="li" cssClass="" name="补偿款计算器" rhtCd="ph004-init"
           onClick="ph003Op.openCalculate('${item.Row.HouseInfo.hsId}')"/>
<oframe:op prjCd="${param.prjCd}" template="li" cssClass="" name="信息修改记录" rhtCd="view_ohs_edit_his"
           onClick="ph003Op.editRecords('${item.Row.HouseInfo.hsId}')"/>
<oframe:op prjCd="${param.prjCd}" template="li" cssClass="" name="删除居民信息" rhtCd="del_old_hs_rht"
           onClick="ph003Op.deleteHouse('${item.Row.HouseInfo.hsId}')"/>
<oframe:op prjCd="${param.prjCd}" template="li" cssClass="" name="关联房产信息" rhtCd="ph012-init"
           onClick="ph003Op.openHsRelInfo('${item.Row.HouseInfo.hsId}')"/>

