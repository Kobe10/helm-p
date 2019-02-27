<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="panelBar">
    <ul class="toolBar">
        <c:choose>
            <c:when test="${method != 'view'}">
                <li><a class="save" onclick="prjParam.saveCfg()"><span>保存</span></a></li>
            </c:when>
        </c:choose>
    </ul>
</div>
<form id="sys00501form" method="post" class="required-validate">
    <input type="hidden" name="itemCd" value="${nodeInfo.SysCfg.itemCd}"/>
    <input type="hidden" name="itemName" value="${nodeInfo.SysCfg.itemName}"/>
    <input type="hidden" name="note" value="${nodeInfo.SysCfg.note}"/>
    <input type="hidden" name="valueType" value="${nodeInfo.SysCfg.valueType}"/>
    <input type="hidden" name="statusCd" value="${nodeInfo.SysCfg.statusCd}"/>
    <input type="hidden" name="itemPrjCd" value="${nodeInfo.SysCfg.prjCd}"/>
    <input type="hidden" name="itemUseType" value="${nodeInfo.SysCfg.itemUseType}"/>
    <table class="border">
        <tr>
            <th width="10%"><label>参数编码：</label></th>
            <td width="20%">${nodeInfo.SysCfg.itemCd}</td>
            <th width="10%"><label>参数名称：</label></th>
            <td width="20%">${nodeInfo.SysCfg.itemName}</td>
            <th width="10%"><label>参数用途：</label></th>
            <td width="20%">${nodeInfo.SysCfg.note}</td>
        </tr>
        <tr>
            <th width="10%"><label>值类型：</label></th>
            <td width="20%"><oframe:name prjCd="${param.prjCd}" itemCd="ITEM_VALUE_TYPE" value="${nodeInfo.SysCfg.valueType}"/></td>
            <th width="10%"><label>默认值：</label></th>
            <td width="20%"><input type="text" name="dftValue"
                                   value="${nodeInfo.SysCfg.dftValue}"
                                   class="msize"/></td>
            <th width="10%"><label>是否为空：</label></th>
            <td width="20%"><oframe:select prjCd="${param.prjCd}" itemCd="COMMON_YES_NO" name="withEmpty" cached="true"
                                           value="${nodeInfo.SysCfg.withEmpty}"/></td>
        </tr>
        <c:if test="${nodeInfo.SysCfg.valueType == '1'}">
            <tr>
                <th><label>参数取值：</label></th>
                <td colspan="5"><input type="text" name="dftValue" value="${nodeInfo.SysCfg.dftValue}"/></td>
            </tr>
        </c:if>
    </table>
    <c:if test="${nodeInfo.SysCfg.valueType == '2'}">
        <input type="hidden" name="dftValue" value="${nodeInfo.SysCfg.dftValue}"/>
        <table id="pj002Param" class="list" width="100%" layoutH="170">
            <thead>
                <tr>
                    <td width="15%">取值编码</td>
                    <td width="20%">取值名称</td>
                    <td width="50%">备注说明</td>
                    <td>操作 <a class="btnAdd" onclick="prjParam.addRow('pj002Param', this);">添加</a></td>
                </tr>
            </thead>
            <tbody>
                <tr class="hidden">
                    <td><input name="valueCd" type="text"/></td>
                    <td><input name="valueName" type="text"/></td>
                    <td><input name="notes" class="msize" type="text"/></td>
                    <td>
                        <a class="btnDel" onclick="table.deleteRow(this);">删除</a>
                        <a class="btnAdd" onclick="table.addRow('pj002Param', this);">添加</a>
                        <a class="link" onclick="table.upRow('pj002Param', this);">[上移]</a>
                        <a class="link" onclick="table.downRow('pj002Param', this);">[下移]</a>
                    </td>
                </tr>
                <c:forEach items="${valueList}" var="item">
                    <tr>
                        <td><input name="valueCd" class="required" type="text" value="${item.valueCd}"/></td>
                        <td><input name="valueName" class="required" type="text" value="${item.valueName}"/></td>
                        <td><input name="notes" class="msize" type="text" value="<c:out value='${item.notes}'/>"/></td>
                        <td>
                            <a class="btnDel" onclick="table.deleteRow(this);">删除</a>
                            <a class="btnAdd" onclick="table.addRow('pj002Param', this);">添加</a>
                            <a class="btnView" onclick="table.upRow('pj002Param', this);">[上移]</a>
                            <a class="btnView" onclick="table.downRow('pj002Param', this);">[下移]</a>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>
</form>