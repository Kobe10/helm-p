<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="panelBar">
    <ul class="toolBar">
        <c:choose>
            <c:when test="${method != 'view'}">
                <li><a class="save" onclick="sys00501.saveCfg()"><span>保存</span></a></li>
            </c:when>
        </c:choose>
        <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
    </ul>
</div>
<div>
    <form id="sys00501form" method="post" class="required-validate">
        <input type="hidden" name="method" value="${method}"/>
        <table class="border">
            <tr>
                <th><label>参数编码：</label></th>
                <td>
                    <c:choose>
                        <c:when test="${method != 'add'}">
                            <input type="text" name="itemCd" class="required readonly"
                                   value="${nodeInfo.SysCfg.itemCd}"/>
                        </c:when>
                        <c:otherwise>
                            <input type="text" name="itemCd" class="required" value="${nodeInfo.SysCfg.itemCd}"/>
                        </c:otherwise>
                    </c:choose>
                </td>
                <th><label>参数名称：</label></th>
                <td>
                    <input type="text" name="itemName" class="required" value="${nodeInfo.SysCfg.itemName}"/>
                </td>

                <th><label>参数用途：</label></th>
                <td>
                    <input type="text" name="note" class="msize" value="${nodeInfo.SysCfg.note}"/>
                </td>
            </tr>
            <tr>
                <th><label>值类型：</label></th>
                <td>
                    <oframe:select prjCd="${param.prjCd}" itemCd="ITEM_VALUE_TYPE"
                                   onChange="sys00501.valueTypeChange()"
                                   name="valueType" value="${nodeInfo.SysCfg.valueType}"/>
                </td>

                <th><label>默认值：</label></th>
                <td>
                    <input type="text" name="dftValue"
                           value="${nodeInfo.SysCfg.dftValue}"
                           class="msize"/>
                </td>
                <th><label>是否为空：</label></th>
                <td>
                    <oframe:select prjCd="${param.prjCd}" itemCd="COMMON_YES_NO" name="withEmpty" cached="true"
                                   value="${nodeInfo.SysCfg.withEmpty}"/>
                </td>
            </tr>
            <tr>
                <th><label>状态：</label></th>
                <td>
                    <oframe:select prjCd="${param.prjCd}" itemCd="COM_STATUS_CD" name="statusCd" cached="true"
                                   value="${nodeInfo.SysCfg.statusCd}"/>
                </td>
                <th><label>使用项目：</label></th>
                <td>
                    <oframe:select prjCd="${param.prjCd}" collection="${prjCdMap}" name="cfgPrjCd"
                                   value="${nodeInfo.SysCfg.prjCd}"
                                   style="width:81%"/>
                </td>
                <th><label>项目使用类别：</label></th>
                <td>
                    <oframe:select prjCd="${param.prjCd}" itemCd="RHT_USE_TYPE" name="itemUseType" cached="true"
                                   value="${nodeInfo.SysCfg.itemUseType}"/>
                </td>
            </tr>

        </table>
        <table id="sys00501_cdvl" class="list" width="100%">
            <thead>
            <tr>
                <td width="20%">取值编码</td>
                <td width="20%">取值名称</td>
                <td width="40%">备注说明</td>
                <td>操作 <a class="btnAdd" onclick="sys005.addRow('sys00501_cdvl', this);">添加</a></td>
            </tr>
            </thead>
            <tbody>
            <tr class="hidden">
                <td><input name="valueCd" type="text"/></td>
                <td><input name="valueName" type="text"/></td>
                <td><input name="notes" class="msize" type="text"/></td>
                <td>
                    <a class="btnDel" onclick="table.deleteRow(this);">删除</a>
                    <a class="btnAdd" onclick="table.addRow('sys00501_cdvl', this);">添加</a>
                    <a class="link" onclick="table.upRow('sys00501_cdvl', this);">[上移]</a>
                    <a class="link" onclick="table.downRow('sys00501_cdvl', this);">[下移]</a>
                </td>
            </tr>
            <c:forEach items="${valueList}" var="item">
                <tr>
                    <td><input name="valueCd" class="required" type="text" value="${item.valueCd}"/></td>
                    <td><input name="valueName" class="required titleFormat" type="text" value="${item.valueName}"/></td>
                    <td><input name="notes" class="msize titleFormat" title="<c:out value='${item.notes}'/>"
                               <%--onmouseover="TitleFormat.titleMouseOver(this, event)"--%>
                               <%--onmouseout="TitleFormat.titleMouseOut(this);"--%>
                               type="text"
                               value="<c:out value='${item.notes}'/>"/></td>
                    <td>
                        <a class="btnDel" onclick="table.deleteRow(this);">删除</a>
                        <a class="btnAdd" onclick="table.addRow('sys00501_cdvl', this);">添加</a>
                        <a class="btnView" onclick="table.upRow('sys00501_cdvl', this);">[上移]</a>
                        <a class="btnView" onclick="table.downRow('sys00501_cdvl', this);">[下移]</a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </form>
</div>
<oframe:script src="${pageContext.request.contextPath}/oframe/sysmg/sys005/js/sys00501.js"
               type="text/javascript"></oframe:script>