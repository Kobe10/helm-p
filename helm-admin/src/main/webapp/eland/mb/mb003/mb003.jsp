<%--区域统计面板--%>
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
                    <a title="选择" onclick="mb003.editReg(this);" class="btnLook">选择</a>
                </div>
            </td>
            <th>
                <label style="text-align: left;color: #177DAE;">组织结构:</label>
            </th>
            <td nowrap>
                <div style="position:relative;">
                    <input type="hidden" name="prjOrgId" value="">
                    <input type="text" name="prjOrgName" readonly style="float:left" value=""/>
                    <a title="选择" onclick="mb003.editOrg(this);" class="btnLook">选择</a>
                </div>
            </td>
            <th>
                <label style="text-align: left;color: #177DAE;">维度:</label>
            </th>
            <td>
                <select name="mb003Tjwd" onchange="mb003.changePicData();">
                    <option value="0">工作进展</option>
                    <option value="1">人口数量</option>
                    <option value="2">户籍数量</option>
                    <option value="3">产籍数量</option>
                </select>
            </td>
            <th>
                <label style="text-align: left;color: #177DAE;">自动刷新(秒):</label>
            </th>
            <td nowrap>
                <input onchange="mb003.updateFlash(this)"
                       style="width: 30px;"
                       onkeydown="if(event.keyCode == 13){mb003.updateFlash(this);stopEvent(event);}"
                       type="text"
                       class="digits" name="autoFlashInt" value="5">
                <input onchange="mb003.flashChanged(this)" type="checkbox"
                       name="autoFlash" value="0">
            </td>
        </tr>
    </table>
    <div id="mb003ChartDiv" class="pageChart" style="width:90%;"></div>
</div>
<oframe:script src="${pageContext.request.contextPath}/eland/mb/mb003/js/mb003.js" type="text/javascript"/>