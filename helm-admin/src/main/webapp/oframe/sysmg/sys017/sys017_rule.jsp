<%--项目资料--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<style type="text/css">
    #versionMenu {
        display: inline-block;
        position: relative;
        cursor: pointer;
    }

    #versionMenu ul {
        background-color: white;
        position: absolute;
        right: 0;
    }

    #versionMenu ul li {
        padding: 0 15px;
        list-style: none;
        vertical-align: middle;
        display: block !important;
        height: 25px;
        text-align: right;
        line-height: 25px;
        cursor: pointer;
    }

    #versionMenu ul li.active {
        color: #000;
        background-color: #3c9fd5;
    }

    #versionMenu ul li:hover {
        color: #ffffff;
        background-color: #3d91c8;
    }
</style>
<div class="panelBar">
    <ul class="toolBar">
        <c:choose>
            <c:when test="${ruleInfo.RuleInfo.ruleId == null}">
                <oframe:op prjCd="${param.prjCd}" template="li" cssClass="add"
                           name="新增规则" rhtCd="edit_rule_rht"
                           onClick="sys017.saveRuleInfo();"/>
            </c:when>
            <c:otherwise>
                <oframe:op prjCd="${param.prjCd}" template="li" cssClass="edit"
                           name="修改规则" rhtCd="edit_rule_rht"
                           onClick="sys017.saveRuleInfo();"/>
                <c:set var="rulsStatus" value="${ruleInfo.RuleInfo.ruleStatus}"/>

                <c:choose>
                    <c:when test="${rulsStatus != '1'}">
                        <oframe:op prjCd="${param.prjCd}" template="li" cssClass="hight-level"
                                   name="启用规则" rhtCd="del_rule_rht"
                                   onClick="sys017.updateRuleStatus('${ruleInfo.RuleInfo.ruleId}', '1','${ruleInfo.RuleInfo.ruleCd}','${ruleInfo.RuleInfo.prjCd}');"/>
                        <oframe:op prjCd="${param.prjCd}" template="li" cssClass="delete"
                                   name="删除规则" rhtCd="del_rule_rht"
                                   onClick="sys017.deleteRule('${ruleInfo.RuleInfo.ruleId}', '${ruleInfo.RuleInfo.ruleTypeId}');"/>
                    </c:when>
                    <c:when test="${rulsStatus == '1'}">
                        <oframe:op prjCd="${param.prjCd}" template="li" cssClass="lower-level"
                                   name="停用规则" rhtCd="del_rule_rht"
                                   onClick="sys017.updateRuleStatus('${ruleInfo.RuleInfo.ruleId}', '0','${ruleInfo.RuleInfo.ruleCd}','${ruleInfo.RuleInfo.prjCd}');"/>
                    </c:when>
                </c:choose>
                <oframe:op prjCd="${param.prjCd}" template="li" cssClass="export"
                           name="导出规则" rhtCd="del_rule_rht"
                           onClick="sys017.exportRule('${ruleInfo.RuleInfo.ruleId}')"/>

            </c:otherwise>
        </c:choose>
    </ul>
</div>
<div layoutH="55" style="border: 1px solid rgb(233, 233, 233);">
    <form id="sys017RuleForm">
        <input type="hidden" name="ruleId" value="${ruleInfo.RuleInfo.ruleId}">
        <input type="hidden" name="ruleTypeId" value="${ruleInfo.RuleInfo.ruleTypeId}">
        <input type="hidden" name="oldRuleCd" value="${ruleInfo.RuleInfo.ruleCd}">
        <input type="hidden" name="oldPrjCd" value="${ruleInfo.RuleInfo.prjCd}">
        <table class="border">
            <tr>
                <th width="10%"><label>规则编码：</label></th>
                <td>
                    <input type="text" class="required" maxlength="50" name="ruleCd" style="width: 70%;"
                           value="${ruleInfo.RuleInfo.ruleCd}">
                    <span id="versionMenu" class="marl5">#${ruleInfo.RuleInfo.ruleVersion}
                        <span class="caret" style="margin-bottom: 3px;"/>
                        <c:set var="ruleVersion" value="${ruleInfo.RuleInfo.ruleVersion}"/>
                        <ul class="hidden">
                            <c:forEach items="${versionMap}" var="item">
                                <c:if test="${item.value != ruleVersion}">
                                    <li onclick="sys017.ruleInfo('${ruleInfo.RuleInfo.ruleCd}', '', '', '${item.key}')">
                                        #${item.value}</li>
                                </c:if>
                            </c:forEach>
                        </ul>

                    </span>
                </td>
                <th width="10%"><label>规则名称：</label></th>
                <td><input type="text" class="required" maxlength="50" name="ruleName"
                           value="${ruleInfo.RuleInfo.ruleName}"></td>
                <th width="10%"><label>适用平台：</label></th>
                <td><oframe:select prjCd="${param.prjCd}" collection="${prjMap}" cssClass="required" withEmpty="true"
                                   value="${ruleInfo.RuleInfo.prjCd}"
                                   name="usePrjCd"/></td>
            </tr>
            <tr>
                <th><label>规则类型：</label></th>
                <td><oframe:select prjCd="${param.prjCd}" itemCd="RULE_SCRIPT_TYPE" name="ruleScriptType"
                                   cssClass="required"
                                   value="${ruleInfo.RuleInfo.ruleScriptType}"/></td>
                <th><label>入口方法：</label></th>
                <td>
                    <input type="text" name="entryMethod" maxlength="50" class="required"
                           value="${ruleInfo.RuleInfo.entryMethod}">
                </td>
                <th><label>规则状态：</label></th>
                <td>
                    <oframe:name prjCd="${param.prjCd}" itemCd="RULE_STATUS" value="${ruleInfo.RuleInfo.ruleStatus}"/>
                </td>
            </tr>
            <tr>
                <th><label>功能描述：</label></th>
                <td><input type="text" name="ruleDesc" maxlength="200" value="${ruleInfo.RuleInfo.ruleDesc}"></td>
                <th><label title="规则最近一次被启用的时间">启用时间：</label></th>
                <td>${ruleEnableTime}</td>
                <th><label title="规则最近一次状态变化的时间(创建,启用,停用都会影响状态)">状态时间：</label></th>
                <td>${ruleStatusTime}</td>
            </tr>
            <tr>
                <th class="subTitle" colspan="6"><h1 style="float: left;">规则定义</h1></th>
            </tr>
        </table>
        <textarea name="ruleScript" class="hidden">${ruleInfo.RuleInfo.ruleScript}</textarea>
        <iframe name="sys017IFrame" allowTransparency="false" id="sys017IFrame" width="99.5%;"
                style="background-color:#f5f9fc;"
                src="${pageContext.request.contextPath}/oframe/sysmg/sys017/sys017-ruleCode.gv" layoutH="218">
        </iframe>
    </form>
</div>
<script type="text/javascript">

    sys017Rule = {
        /**
         * 隐藏项目菜单
         */
        removeVersionMenu: function () {
            var active = $("#versionMenu").attr("active");
            if ("on" != active) {
                $("#versionMenu").find("ul").addClass("hidden");
            }
        }
    };

    $("#versionMenu").bind("mouseover", function () {
        $(this).find("ul").removeClass("hidden");
        $(this).attr("active", "on");
    });

    $("#versionMenu").mouseout(function () {
        $("#versionMenu").attr("active", "off");
        setTimeout(function () {
            sys017Rule.removeVersionMenu();
        }, 300);
    });
</script>