<%--
  Created by IntelliJ IDEA.
  User: shfb_wang
  Date: 2015/6/11 0011 14:53
  Copyright(c) 北京四海富博计算机服务有限公司
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div style="background-color: #ffffff;height:360px;">
    <table>
        <tr>
            <th>
                <label style="text-align: left;color: #177DAE;">项目区域:</label>
            </th>
            <td nowrap>
                <div style="position:relative;">
                    <input type="hidden" name="regId" value="">
                    <input type="text" name="regName" readonly style="float:left" value=""/>
                    <a title="选择" onclick="mb007.editReg(this);" class="btnLook">选择</a>
                </div>
            </td>
            <th>
                <label style="text-align: left;color: #177DAE;">组织架构:</label>
            </th>
            <td nowrap>
                <div style="position:relative;">
                    <input type="hidden" name="prjOrgId" value="">
                    <input type="text" name="prjOrgName" readonly style="float:left" value=""/>
                    <a title="选择" onclick="mb007.editOrg(this);" class="btnLook">选择</a>
                </div>
            </td>
            <th>
                <label style="text-align: left;color: #177DAE;">统计指标:</label>
            </th>
            <td>
                <select name="prjJobCd" style="width: auto;" onchange="mb007.changePicData();">
                    <option value="hs_deal_chart">安置方式</option>
                    <option value="hs_owner_chart">房产类型</option>
                    <option value="hs_agree_chart">安置意向</option>
                    <option value="hs_status_chart">居民进度</option>
                </select>
            </td>
            <th>
                <label style="text-align: left;color: #177DAE;"> 自动刷新(秒):</label>
            </th>
            <td nowrap>
                <input onchange="mb007.updateFlash(this)"
                       style="width: 30px;"
                       onkeydown="if(event.keyCode == 13){mb007.updateFlash(this);stopEvent(event);}"
                       type="text"
                       class="digits" name="autoFlashInt" value="5">
                <input onchange="mb007.flashChanged(this)" type="checkbox"
                       name="autoFlash" value="0">
            </td>
        </tr>
    </table>
    <div id="mb007ChartDiv" class="pageChart" style="width:90%;"></div>
</div>
<oframe:script src="${pageContext.request.contextPath}/eland/mb/mb007/js/mb007.js" type="text/javascript"/>