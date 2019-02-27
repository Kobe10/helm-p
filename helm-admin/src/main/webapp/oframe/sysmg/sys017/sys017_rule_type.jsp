<%--项目资料--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="panelBar">
    <ul class="toolBar">
        <c:choose>
            <c:when test="${ruleTypeInfo.RuleType.ruleTypeId ==''}">
                <oframe:op prjCd="${param.prjCd}" template="li" cssClass="add"
                           name="新增分类" rhtCd="edit_rule_rht"
                           onClick="sys017.saveRuleType();"/>
            </c:when>
            <c:otherwise>
                <oframe:op prjCd="${param.prjCd}" template="li" cssClass="edit"
                           name="修改分类" rhtCd="edit_rule_rht"
                           onClick="sys017.saveRuleType();"/>
                <oframe:op prjCd="${param.prjCd}" template="li" cssClass="add"
                           name="下级分类" rhtCd="edit_rule_rht"
                           onClick="sys017.ruleTypeInfo('', '${ruleTypeInfo.RuleType.ruleTypeId}');"/>
                <oframe:op prjCd="${param.prjCd}" template="li" cssClass="add"
                           name="新增规则" rhtCd="edit_rule_rht"
                           onClick="sys017.ruleInfo('', '${ruleTypeInfo.RuleType.ruleTypeId}')"/>
                <oframe:power prjCd="${param.prjCd}" rhtCd="edit_rule_rht">
                    <li style="position: relative;">
                        <a class="import" href="javascript:void (0)">
                            <span onclick="sys017.importXml()">导入规则
                                <input style="width:80px; height:37px; position:absolute; left:0; top:0;
                                     opacity:0;filter:alpha(opacity=0); z-index:1100; cursor:pointer;"
                                       name="uploadRuleFile" id="uploadRuleFile" type="file">
                            </span>
                        </a>
                    </li>
                </oframe:power>
            </c:otherwise>
        </c:choose>
    </ul>
</div>
<div layoutH="55" style="border: 1px solid rgb(233, 233, 233); ">
    <form id="sys017RuleTypeForm">
        <input style='display:none'/>
        <input type="hidden" name="ruleTypeId" value="${ruleTypeInfo.RuleType.ruleTypeId}">
        <input type="hidden" name="upRuleTypeId" value="${ruleTypeInfo.RuleType.upRuleTypeId}">
        <table class="border">
            <tr>
                <th width="15%"><label>分类名称：</label></th>
                <td><input type="text" class="required" name="ruleTypeName" maxlength="50"
                           value="${ruleTypeInfo.RuleType.ruleTypeName}"></td>
            </tr>
            <tr>
                <th><label>授权角色：</label></th>
                <td style="position: relative;">
                    <span style="float: left;">
                        <a title="选择" onclick="sys017.editRole(this);" class="btnLook">选择</a>
                    </span>
                    <span id="sys017RoleSpan" style="display:block;float: left;line-height: 31px;">
                        <span class="hidden" style="float: left;">
                           <input checked="true" name="rhtId" value="" type="checkbox"/>
                        </span>
                        <c:forEach items="${rhtList}" var="rht">
                            <span>
                                <input checked="true" name="rhtId" value="${rht.RuleTypeRht.rhtId}"
                                       type="checkbox"/>${rht.RuleTypeRht.rhtName}
                             </span>
                        </c:forEach>
                    </span>
                </td>
            </tr>
        </table>
    </form>
</div>