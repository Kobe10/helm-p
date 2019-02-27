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
<style>
    #ct002_cdvl input[type="text"] {
        width: 95%;
    }

    #ct002_cdvl td {
        text-align: center
    }

    #ct002_cdvl tr.selected {
        background-color: #e3eef4;
    }
</style>
<div class="panelBar">
    <ul class="toolBar">
        <li onclick="">
            <a class="save" href="javascript:ct002.saveSchemeTypeDef();"><span>保存</span></a>
        </li>
    </ul>
</div>
<div id="ct002SchemeDiv" class="panelontainer" layoutH="58" style="border: 1px solid #e9e9e9;position: relative">
    <input type="hidden" name="schemeId" value="${schemeInfo.PreassignedScheme.schemeId}"/>

    <form id="ct002SchemeTypeDefForm">
        <table class="border">
            <tr>
                <th width="5%"><label>类型编码：</label></th>
                <td width="10%">
                    <input type="text" name="schemeType" class="required readonly" readonly value="${schemeType}"/>
                </td>
                <th width="5%"><label>类型名称：</label></th>
                <td width="10%">
                    <input type="text" name="schemeTypeName" class="required noErrorTip"
                           value="${schemeTypeName}"/>
                </td>
            </tr>
        </table>
        <div class="tabs">
            <div class="tabsHeader" style="border-bottom: 1px solid #3d91c8;">
                <div class="tabsHeaderContent">
                    <ul>
                        <%--<li><a><span>方案选择规则</span></a></li>--%>
                        <li><a><span>签约信息定义</span></a></li>
                        <li><a><span>签约款项定义</span></a></li>
                    </ul>
                </div>
            </div>
            <div class="tabsContent" id="pj002NavContext">
                <%-- <div layoutH="136">
                     <textarea name="ruleScript" class="hidden">${schemeTypeCfg.ruleScript}</textarea>
                     <iframe name="ct002CodeIFrame" allowTransparency="false" id="sys017IFrame" width="99.5%;"
                             style="background-color:#f5f9fc;"
                             src="${pageContext.request.contextPath}/eland/ct/ct002/ct002_rule_code.jsp" layoutH="140">
                     </iframe>
                 </div>--%>
                <div layoutH="138">
                    <table class="border">
                        <tr>
                            <th><label>协议表单编码:</label></th>
                            <td><input type="hidden" name="attrStatusValue" value="2"/>
                                <input type="hidden" name="moneyDy" value="${schemeTypeDef.money_dy}"/>
                                <input type="text" name="formCd" value="${schemeTypeDef.formCd}"/></td>
                            <th><label>存储关联实体:</label></th>
                            <td><input type="text" name="entityName"
                                       value="${schemeTypeDef.entity_key[0].entity_name}"/></td>
                            <th><label>签约方案属性:</label></th>
                            <td>
                                <input type="text" name="attr" value="${schemeTypeDef.entity_key[1].attr}"/>
                                <input type="hidden" name="attrValue"
                                       value="${schemeTypeDef.entity_key[1].attr_value}"/>
                            </td>
                        </tr>
                        <tr>
                            <th><label>签约状态属性:</label></th>
                            <td><input type="text" name="attrStatus" value="${schemeTypeDef.attr_status}"/></td>
                            <th><label>状态时间属性:</label></th>
                            <td><input type="text" name="attrDate" value="${schemeTypeDef.attr_date}"/></td>
                            <th><label>签约工号属性:</label></th>
                            <td><input type="text" name="attrStatusStaff" value="${schemeTypeDef.attr_status_staff}"/>
                            </td>
                        </tr>
                        <tr>
                            <th><label>签字附件关联名称:</label></th>
                            <td>
                                <input type="text" name="signDocName" value="${schemeTypeDef.sign_doc_name}"/>
                            </td>
                            <th><label>现场附件关联名称:</label></th>
                            <td>
                                <input type="text" name="photoDocName" value="${schemeTypeDef.photo_doc_name}"/>
                            </td>
                            <th><label>条目模型验证:</label></th>
                            <td>
                                <oframe:select type="radio" itemCd="COMMON_YES_NO" name="skip"
                                               value="${schemeTypeDef.skip}"/>
                            </td>
                        </tr>
                        <tr>
                            <th><label>数据查询条件(字段):</label></th>
                            <td colspan="5"><input type="text" name="conditionNames"
                                                   value="${schemeTypeDef.conditionNames}"/></td>
                        </tr>
                        <tr>
                            <th><label>数据查询条件(条件):</label></th>
                            <td colspan="5"><input type="text" name="conditions" value="${schemeTypeDef.conditions}"/>
                            </td>
                        </tr>
                        <tr>
                            <th><label>数据查询条件(取值):</label></th>
                            <td colspan="5"><input type="text" name="conditionValues"
                                                   value="${schemeTypeDef.conditionValues}"/></td>
                        </tr>
                    </table>
                </div>
                <div layoutH="138">
                    <table id="ct002_cdvl" class="border" width="100%">
                        <thead>
                        <tr style="background-color: #2e97db;">
                            <td style="text-align: center;" width="40%">取值编码</td>
                            <td style="text-align: center;" width="40%">取值名称</td>
                            <td style="text-align: center;" width="5%">总金额</td>
                            <td style="text-align: center;">
                                <a class="btnAdd" onclick="ct002.addRow('ct002_cdvl', this);">[+]</a>
                            </td>
                        </tr>
                        </thead>
                        <tbody>
                        <tr class="hidden">
                            <td><input name="valueCd" type="text"/></td>
                            <td><input name="valueName" type="text"/></td>
                            <td style="text-align: center;"><input name="totalMoneyRadio" type="radio"/></td>
                            <td style="text-align: center;">
                                <a class="btnAdd" onclick="table.addRow('ct002_cdvl', this);">[+]</a>
                                <a class="btnDel" onclick="table.deleteRow(this);">[-]</a>
                                <a class="btnAdd" onclick="table.upRow('ct002_cdvl', this);">[↑️]</a>
                                <a class="btnAdd" onclick="table.downRow('ct002_cdvl', this);">[↓]</a>
                            </td>
                        </tr>
                        <c:forEach items="${moneyDef}" var="item">
                            <tr>
                                <td><input name="valueCd" class="required" type="text" value="${item.key}"/></td>
                                <td><input name="valueName" class="required" type="text" value="${item.value}"/></td>
                                <td style="text-align: center;">
                                    <c:choose>
                                        <c:when test="${totalMoney == item.key}">
                                            <input name="totalMoneyRadio" checked type="radio"/>
                                        </c:when>
                                        <c:otherwise>
                                            <input name="totalMoneyRadio" type="radio"/>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <a class="btnAdd" onclick="table.addRow('ct002_cdvl', this);">[+]</a>
                                    <a class="btnDel" onclick="table.deleteRow(this);">[-]</a>
                                    <a class="btnAdd" onclick="table.upRow('ct002_cdvl', this);">[↑️]</a>
                                    <a class="btnAdd" onclick="table.downRow('ct002_cdvl', this);">[↓]</a>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </form>
</div>
<script>
    $(document).ready(function () {
        $("#ct002_cdvl", navTab.getCurrentPanel()).on("click", "tr", function () {
            $(this).addClass("selected");
            $(this).siblings("tr").removeClass("selected");
        });
    });

</script>
