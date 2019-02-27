<%--项目资料--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="mar5" style="vertical-align: top;">
    <oframe:power prjCd="${param.prjCd}" rhtCd="edit_rule_rht">
        <input type="hidden" name="editAble" value="1"/>
    </oframe:power>
    <oframe:power prjCd="${param.prjCd}" rhtCd="del_rule_rht">
        <input type="hidden" name="removeAble" value="1"/>
    </oframe:power>
    <oframe:power prjCd="${param.prjCd}" rhtCd="move_rule_rht">
        <input type="hidden" name="moveAble" value="1"/>
    </oframe:power>
    <%--左侧区域树--%>
    <div style="width: 340px;float:left;position: relative;" id="sys017DivTree" layoutH="15" class="panel left_menu">
        <h1>
            <span>规则管理</span>
            <span onclick="sys017.extendOrClose(this);" class="panel_menu js_reload">折叠</span>
            <span onclick="sys017.initRuleTree();" class="panel_menu js_reload">刷新</span>
            <span onclick="sys017.batchExport();" class="panel_menu js_reload">批量导出</span>
        </h1>
        <table width="100%;">
            <tr>
                <td style="width: 30%;">
                    <select name="schType" style="float: left; width: 100%;">
                        <option value="RuleInfo.ruleCd">规则编码</option>
                        <option value="RuleInfo.ruleName">规则名称</option>
                    </select>
                </td>
                <td>
                    <input type="text" name="schValue" class="required noErrorTip"
                           onkeydown="if(event.keyCode == 13){sys017.quickSch();}"
                           style="width: 100%;height:26px;min-width:inherit;">
                </td>
                <td style="width: 30px;">
                    <a title="选择" onclick="sys017.quickSch();" style="height: 30px;left:6px;" class="btnLook">选择</a>
                </td>
            </tr>
        </table>
        <form id="sys017QueryForm">
            <input type="hidden" name="entityName" value="RuleInfo"/>
            <input type="hidden" name="conditionName" value="">
            <input type="hidden" name="pageSize" value="16">
            <input type="hidden" name="condition" value="like">
            <input type="hidden" name="conditionValue" value="">
            <input type="hidden" name="sortColumn" value="RuleInfo.ruleStatusTime,RuleInfo.prjCd,RuleInfo.ruleCd,RuleInfo.ruleVersion">
            <input type="hidden" name="sortOrder" value="desc,asc,asc,desc">
            <input type="hidden" name="cmptName" value="QUERY_ENTITY_PAGE_DATA">
            <input type="hidden" class="js_conditionValue" value="">
            <input type="hidden" name="divId" value="sys017RuleList">
            <input type="hidden" name="resultField" value="RuleInfo.ruleId,RuleInfo.ruleName">
            <%--必须包含的字段--%>
            <input type="hidden" name="forceResultField" value="RuleInfo.ruleCd,RuleInfo.ruleTypeId,RuleInfo.ruleStatusTime">
            <input type="hidden" name="forward" id="forward" value="/oframe/sysmg/sys017/sys017_rule_list"/>
        </form>
        <div class="js_page" id="sys017RuleList" style="position: absolute;width: 100%;z-index: 20;"></div>
        <ul id="sys017Tree" class="ztree" layoutH="100"></ul>
    </div>
    <div class="split_line" layoutH="15">
        <span class="split_but" title="展开/隐藏导航"></span>
    </div>
    <%--右侧自定义画板--%>
    <div id="sys017ContextDiv" style="margin-left: 350px;margin-right: 5px; position: relative;">

    </div>
</div>
<oframe:script src="${pageContext.request.contextPath}/oframe/sysmg/sys017/js/sys017.js" type="text/javascript"/>