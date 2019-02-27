<%--
  Created by IntelliJ IDEA.
  User: Dennis
  Date: 15/9/15
  Time: 00:09
  To change this template use File | Settings | File Templates.
--%>
<%--待签约处理--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<style type="text/css">
    #treeBody th, #treeBody td {
        border: 1px solid #8DB9DB;
        padding: 5px;
        border-collapse: collapse;
        font-size: 16px;
    }

    #treeBody tr:hover {
        background-color: #dff1fb;
    }

    #treeBody input {
        width: 98%;
    }

    #treeBody > tr:hover, span.cursorpt:hover {
        color: #0088cc;
        font-weight: bolder;
    }

    #treeBody td.tdCen {
        text-align: center
    }
</style>
<div class="panelBar">
    <ul class="toolBar">
        <c:if test="${schemeInfo.PreassignedScheme.schemeStatus == '0'}">
            <li onclick="ct002.saveScheme('${schemeInfo.PreassignedScheme.schemeId}')">
                <a class="save" href="javascript:void(0)"><span>保存</span></a>
            </li>
        </c:if>
        <c:if test="${method != 'new'}">
            <c:if test="${schemeInfo.PreassignedScheme.schemeStatus == '0'}">
                <li onclick="ct002.enableScheme('${schemeInfo.PreassignedScheme.schemeId}', '1')">
                    <a class="hight-level" href="javascript:void(0)"><span>启用</span></a>
                </li>
            </c:if>
            <c:if test="${schemeInfo.PreassignedScheme.schemeStatus == '1'}">
                <li onclick="ct002.enableScheme('${schemeInfo.PreassignedScheme.schemeId}', '0')">
                    <a class="lower-level" href="javascript:void(0)"><span>停用</span></a>
                </li>
            </c:if>
            <li onclick="ct002.delScheme('${schemeInfo.PreassignedScheme.schemeId}')">
                <a class="delete" href="javascript:void(0)"><span>删除</span></a>
            </li>
            <li onclick="ct002.saveScheme('','copy')">
                <a class="save" href="javascript:void(0)"><span>复制</span></a>
            </li>
            <li onclick="ct002.exportScheme('${schemeInfo.PreassignedScheme.schemeId}')">
                <a class="export" href="javascript:void(0)"><span>导出</span></a>
            </li>
            <%--切换试图--%>
            <li class="right" onclick="ct002.showCalcResult();">
                <a class="result" href="javascript:void(0)"><span>方案计算</span></a>
            </li>
        </c:if>
        <li class="right" onclick="">
            <a class="design" href="javascript:void(0)"><span class="active">方案定义</span></a>
        </li>
    </ul>
</div>
<div id="ct002SchemeDiv" class="panelcontainer" layoutH="55" style="border: 1px solid #e9e9e9;position: relative">
    <input type="hidden" name="schemeId" value="${schemeInfo.PreassignedScheme.schemeId}"/>

    <form id="ct002Form">
        <table class="border">
            <tr>
                <th width="5%"><label>方案名称：</label></th>
                <td width="10%"><input type="text" name="schemeName" class="required noErrorTip"
                                       value="${schemeInfo.PreassignedScheme.schemeName}"/></td>
                <th width="5%"><label>预分分类：</label></th>
                <td width="10%"><oframe:select prjCd="${param.prjCd}" itemCd="SCHEME_TYPE" name="schemeType"
                                               value="${schemeInfo.PreassignedScheme.schemeType}"/></td>
                <th width="5%"><label>控制规则：</label></th>
                <td width="10%">
                    <input type="text" name="conditionRule" class=""
                           value="<c:out value='${schemeEnableRule.SchemeEnableRule.conditionRule}'/>">
                    <input type="hidden" name="schemeStatus" value="${schemeInfo.PreassignedScheme.schemeStatus}"/>
                </td>
                <th width="5%"><label>规则参数：</label></th>
                <td width="10%">
                    <input type="text" name="ctType" class="noErrorTip"
                           value="<c:out value='${schemeEnableRule.SchemeEnableRule.conditionRuleParams.ctType}'/>">
                </td>
            </tr>
        </table>
        <div id="ct002_subJect" layoutH="100">
            <table id="treeBody" width="100%">
                <tbody>
                <c:if test="${method == 'new'}">
                    <tr data-tt-id="0" style="background-color: #e2edf3;line-height: 25px">
                        <td width="15%"><label>预分方案</label>
                            <input type="hidden" name="subId" value="0"/>
                            <input type="hidden" name="upId" value=""/>
                            <input type="hidden" name="name" value=""/>
                            <input type="hidden" name="code" value=""/>
                            <input type="hidden" name="node" value=""/>
                            <input type="hidden" name="conditionRule" value=""/>
                            <input type="hidden" name="executeRule" value=""/>
                            <input type="hidden" name="calcType" value=""/>
                            <input type="hidden" name="calcParam" value=""/>
                        </td>
                        <td width="15%" class="tdCen">科目名称</td>
                        <td width="20%" class="tdCen">补偿说明</td>
                        <td width="15%" class="tdCen">计算方式</td>
                        <td width="15%" class="tdCen">计算参数</td>
                        <td width="20%" class="tdCen" style="color: #7fbb2a;">
                            <span class="cursorpt" onclick="ct002.addBroRowNode(this, 'root');">插入</span>
                        </td>
                    </tr>
                </c:if>
                <c:forEach items="${subjectList}" var="item" varStatus="varStatus">
                    <%--handling root node --%>
                    <c:if test="${item.Subject.id == '0'}">
                        <tr data-tt-id="0" style="background-color: #e2edf3;line-height: 25px">
                            <td width="20%"><label>预分方案</label>
                                <input type="hidden" name="subId" value="0"/>
                                <input type="hidden" name="upId" value=""/>
                                <input type="hidden" name="name" value=""/>
                                <input type="hidden" name="code" value=""/>
                                <input type="hidden" name="node" value=""/>
                                <input type="hidden" name="conditionRule" value=""/>
                                <input type="hidden" name="executeRule" value=""/>
                                <input type="hidden" name="calcType" value=""/>
                                <input type="hidden" name="calcParam" value=""/>
                            </td>
                            <td width="15%" class="tdCen">科目名称</td>
                            <td width="15%" class="tdCen">补偿说明</td>
                            <td width="15%" class="tdCen">计算方式</td>
                            <td width="15%" class="tdCen">计算参数</td>
                            <td width="100px" class="tdCen" style="color: #7fbb2a;">
                                <span class="cursorpt" onclick="ct002.addBroRowNode(this, 'root');">插入</span>
                            </td>
                        </tr>
                    </c:if>
                    <c:if test="${item.Subject.id != '0'}">
                        <tr data-tt-id="${item.Subject.id}" data-tt-parent-id="${item.Subject.upId}">
                            <td>
                                <label>${item.Subject.id}</label>
                            </td>
                            <td>
                                <input type="hidden" name="subId" value="${item.Subject.id}"/>
                                <input type="hidden" name="upId" value="${item.Subject.upId}"/>
                                <input name="code" type="hidden" value="${item.Subject.code}"/>
                                <input name="name" type="text" class="autocomplete required noErrorTip" atOption="ct002.getOpt"
                                       value="${item.Subject.name}"/>
                            </td>
                            <td>
                                <input type="text" name="node" class="required noErrorTip"
                                       value="${item.Subject.node}"/>
                                <input type="hidden" name="conditionRule" value="${item.Subject.conditionRule}"/>
                                <input type="hidden" name="executeRule" value="${item.Subject.executeRule}"/>
                            </td>
                            <td>
                                <input type="hidden" name="calcType"
                                       value="${item.Subject.executeRuleParams.calcType}"/>
                                <input type="text" class="required noErrorTip autocomplete" name="calcTypeName"
                                       atOption="ct002.getCfgDataOpt" itemCd="SCHE_CALC_TYPE"
                                       value=' <oframe:name prjCd="${param.prjCd}" itemCd="SCHE_CALC_TYPE" value="${item.Subject.executeRuleParams.calcType}"/>'/>
                            </td>
                            <td><input type="text" name="calcParam" class="required noErrorTip"
                                       value="${item.Subject.executeRuleParams.calcParam}"/>
                            </td>
                            <td style="color: #7fbb2a;" class="tdCen">
                                <span class="cursorpt" onclick="ct002.addBroRowNode(this, 'child');">[+]</span>
                                <c:if test="${item.Subject.id != '0'}">
                                    <span class="cursorpt" onclick="ct002.mvUpRowNode(this);">[↑]</span>
                                    <span class="cursorpt" onclick="ct002.mvDownRowNode(this);">[↓]</span>
                                    <span class="cursorpt" onclick="ct002.upLevel(this);">[←]</span>
                                    <span class="cursorpt" onclick="ct002.downLevel(this);">[→]</span>
                                    <span class="cursorpt" onclick="ct002.deleteRowNode(this);">[-]</span>
                                </c:if>
                            </td>
                        </tr>
                    </c:if>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </form>
</div>
<%--templete Table--%>
<table id="hiddenTable" hidden="hidden" style="display: none">
    <tr data-tt-id="" data-tt-parent-id="">
        <td class="indexNum"><label></label></td>
        <td>
            <input type="hidden" name="subId" value=""/>
            <input type="hidden" name="upId" value=""/>
            <input name="code" type="hidden" value="${item.Subject.code}"/>
            <input name="name" type="text" class="autocomplete required noErrorTip"
                   atOption="ct002.getOpt" value=''/>
        </td>
        <td>
            <input type="text" name="node" class="required noErrorTip" value=""/>
            <input type="hidden" name="conditionRule" value=""/>
            <input type="hidden" name="executeRule" value=""/>
        </td>
        <td>
            <input type="hidden" name="calcType" value=""/>
            <input type="text" class="required noErrorTip autocomplete" name="calcTypeName"
                   atoption="CODE.getCfgDataOpt" itemCd="SCHE_CALC_TYPE" value=''/>
        </td>
        <td><input type="text" name="calcParam" class="required noErrorTip" value=""/></td>
        <td style="color: #7fbb2a;" class="tdCen">
            <span class="cursorpt" onclick="ct002.addBroRowNode(this, 'child');">[+]</span>
            <span class="cursorpt" onclick="ct002.mvUpRowNode(this);">[↑]</span>
            <span class="cursorpt" onclick="ct002.mvDownRowNode(this);">[↓]</span>
            <span class="cursorpt" onclick="ct002.upLevel(this);">[←]</span>
            <span class="cursorpt" onclick="ct002.downLevel(this);">[→]</span>
            <span class="cursorpt" onclick="ct002.deleteRowNode(this);">[-]</span>
        </td>
    </tr>
</table>
<script>
    $(document).ready(function () {
        var options = {
            initialState: 'expanded'
        };
        $("#treeBody", navTab.getCurrentPanel()).treetable(options);
        ct002.serializIdText();
    });

</script>
