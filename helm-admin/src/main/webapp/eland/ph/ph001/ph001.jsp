<%--人地房信息展示页面--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="pageNav">
    <a class="current">居民信息</a>
</div>
<oframe:power prjCd="${param.prjCd}" rhtCd="edit_1_reg_rht">
    <input type="hidden" name="editAble" value="1"/>
</oframe:power>
<oframe:power prjCd="${param.prjCd}" rhtCd="del_1_reg_rht">
    <input type="hidden" name="removeAble" value="1"/>
</oframe:power>
<oframe:power prjCd="${param.prjCd}" rhtCd="move_1_reg_rht">
    <input type="hidden" name="moveAble" value="1"/>
</oframe:power>
<input type="hidden" name="initRegId" value="${initRegId}">
<input type="hidden" name="_opCode" value="${opCode}">
<input type="hidden" name="_inputUrl" value="${inputUrl}">
<input type="hidden" name="_inputName" value="${inputName}">
<input type="hidden" name="_inputRhtCd" value="${inputRhtCd}">
<input type="hidden" name="_formCd" value="${inputFormCd}">
<%--初始化显示模式--%>
<input type="hidden" name="showJmTree" value="${showJmTree}">
<input type="hidden" name="showJmTreeModel" value="${showJmTreeModel}">
<%--内容区域--%>
<div class="mar5" style="vertical-align: top;">
    <%--左侧区域树--%>
    <div style="width: 240px;float: left;position: relative;" id="ph001DivTree" layoutH="15" class="panel left_menu">
        <h1>
            <span class="panel_title">区 域 导 航</span>
            <span class="item_switch" onclick="ph001.switchTree(this);">&nbsp;</span>
            <span onclick="ph001.extendOrClose(this);" class="panel_menu js_reload">展开</span>
        </h1>

        <ul id="ph001Tree_${opCode}" layoutH="65" class="ztree"></ul>
        <ul id="ph001OrgTree_${opCode}" layoutH="65" class="ztree hidden"></ul>
    </div>
    <div class="split_line" layoutH="15">
        <span class="split_but" title="展开/隐藏导航"></span>
    </div>
    <%--右侧自定义画板--%>
    <div id="ph001001" style="margin-left: 250px;margin-right: 5px;position: relative;">
        <div class="js_query_condition" style="position: relative;">
            <div class="panelBar">
                <ul class="toolBar">
                    <%--<li onmousemove="ph001.openQSch(this)"--%>
                        <%--onmouseout="ph001.leaveQSch()"--%>
                    <li onclick="ph001.queryHs(this);">
                        <a class="find" href="javascript:void(0)"><span>条件检索</span></a>
                    </li>
                    <li onclick="ph001.refleshBuild();">
                        <a class="reflesh" href="javascript:void(0)"><span>刷新检索</span>
                        </a>
                    </li>
                    <oframe:op prjCd="${param.prjCd}" template="li" cssClass="delete-more"
                               name="选中删除" rhtCd="del_old_hs_rht_${opCode}"
                               onClick="ph001.deleteSltHs(this)"/>
                    <oframe:op prjCd="${param.prjCd}" template="li" cssClass="edit"
                               name="批量修改" rhtCd="bedit_old_hs_rht_${opCode}"
                               onClick="ph001.batchChange(this)"/>
                    <oframe:op prjCd="${param.prjCd}" template="li" cssClass="export"
                               name="导出明细" rhtCd="exp_old_hs_rht_${opCode}"
                               onClick="Page.exportExcel('ph001001query_list', false)"/>
                    <oframe:op prjCd="${param.prjCd}" template="li" cssClass="import"
                               name="信息导入" rhtCd="imp_all_hs_rht_${opCode}"
                               onClick="ph001.importMb('${inputFormCd}');"/>
                    <oframe:op prjCd="${param.prjCd}" template="li" cssClass="export"
                               name="全量导出" rhtCd="exp_all_hs_rht_${opCode}"
                               onClick="ph001.allExport('${inputFormCd}')"/>
                    <oframe:op prjCd="${param.prjCd}" template="li" cssClass="new-area"
                               name="预分计算" rhtCd="pre_cal_hs_rht_${opCode}"
                               onClick="ph001.openRunScheme()"/>
                    <oframe:op prjCd="${param.prjCd}" template="li" cssClass="export"
                               name="购房明细" rhtCd="exp_choose_hs_rht_${opCode}"
                               onClick="ph001.chooseExport()"/>
                    <li onclick="ph001.sign('2');">
                        <a class="design" href="javascript:void(0)"><span>标签管理</span>
                        </a>
                    </li>
                    <c:set var="canDefResult" value="false"/>
                    <oframe:power prjCd="${param.prjCd}" rhtCd="adv_ch_${opCode}">
                        <c:set var="canDefResult" value="true"/>
                    </oframe:power>
                    <li style="float: right;" onclick="ph001.changeShowModel(this,'3');">
                        <a class="sum" href="javascript:void(0)"><span>统计</span></a>
                    </li>
                    <li id="changeModel" style="float: right;display: none;" onclick="ph001.changeShowModel(this,'2')">
                        <a class="pic" href="javascript:void(0)"><span>图形</span></a>
                    </li>
                    <li style="float: right" onclick="ph001.changeShowModel(this,'1');">
                        <a class="list" href="javascript:void(0)"><span class="active">列表</span></a>
                    </li>
                </ul>
            </div>
            <div id="ph001QSchDialog" class="hidden"
                 style="position: absolute;width:100%;top: 0px;z-index: 100;background-color: #ffffff; margin-top: 40px;">
                <div class="triangle triangle-up" style="border-bottom-color: #2c7bae;top: -10px; left: 25px;"></div>
                <jsp:include page="/oframe/common/page/page-favList.gv">
                    <jsp:param name="entityName" value="HouseInfo,PersonInfo"/>
                    <jsp:param name="tabOpCode" value="${param.opCode}"/>
                    <jsp:param name="callBack" value="ph001.clickFav"/>
                </jsp:include>
                <table class="border">
                    <%--<tr>--%>
                        <%--<c:forEach items="${attrNameMap}" var="item">--%>
                            <%--<th><label>${item.value}：</label></th>--%>
                            <%--<td><input name="${item.key}" condition="like" class="textInput"></td>--%>
                        <%--</c:forEach>--%>
                    <%--</tr>--%>
                    <%--<tr>--%>
                        <%--<td colspan="6" align="center">--%>
                            <%--<button onclick="ph001.qSchData();" class="btn btn-primary js_query marr20">查询</button>--%>
                            <%--<button onclick="ph001.closeQSch(true);" class="btn btn-info">关闭</button>--%>
                            <%--<button onclick="ph001.queryHs(this);" class="btn btn-warn marl20">高级</button>--%>
                        <%--</td>--%>
                    <%--</tr>--%>
                </table>
            </div>
        </div>
        <input type="hidden" class="js_query_model" value="1"/>
        <input type="hidden" class="figureBuildId" value=""/>
        <input type="hidden" class="figureRhtId" value=""/>

        <form id="ph001queryForm">
            <input type="hidden" name="entityName" value="HouseInfo"/>
            <c:choose>
                <c:when test="${buildId==null}">
                    <input type="hidden" name="conditionName" value="${conditions.Condition.conditionNames}">
                    <input type="hidden" name="condition" value="${conditions.Condition.conditions}">
                    <input type="hidden" name="conditionValue" value="${conditions.Condition.conditionValues}">
                </c:when>
                <c:otherwise>
                    <input type="hidden" name="conditionName" value="HouseInfo.buildId,${conditions.Condition.conditionNames}">
                    <input type="hidden" name="condition" value="=,${conditions.Condition.conditions}">
                    <input type="hidden" name="conditionValue" value="${buildId},${conditions.Condition.conditionValues}">
                </c:otherwise>
            </c:choose>
            <input type="hidden" name="autoConditions" value="${conditions.Condition.autoConditions}">
            <input type="hidden" name="psConditions" value="${conditions.Condition.psConditionsJson}">
            <input type="hidden" name="tagConditions" value="${conditions.Condition.tagConditionsJson}">
            <input type="hidden" name="rhtRegId" value="${id}">
            <input type="hidden" name="rhtOrgId" value="${rhtOrgId}">
            <input type="hidden" name="regUseType" value="${regUseType}">
            <input type="hidden" name="rhtType" value="${conditions.Condition.rhtType}">
            <input type="hidden" name="sortColumn" value="HouseInfo.updateDate">
            <input type="hidden" name="sortOrder" value="desc">
            <input type="hidden" name="inputRhtCd" value="${inputRhtCd}">
            <input type="hidden" class="js_conditionValue" value="">
            <input type="hidden" class="js_canDefResult" value="${canDefResult}">
            <input type="hidden" name="forceResultField"
                   value="HouseInfo.hsId,HouseInfo.HsCtInfo.hsCtId,HouseInfo.ttRegId">
            <input type="hidden" name="divId" value="ph001Context">
            <input type="hidden" name="resultField" value="${conditions.Condition.resultField}">
            <input type="hidden" name="forward" id="forward" value="/eland/ph/ph001/ph001_list"/>
            <input type="hidden" name="slfDefUrl" value="${pageContext.request.contextPath}/oframe/common/page/page-dataWithTag.gv">
        </form>
        <div id="ph001Context" class="js_page" layoutH="55" style="position: relative;width: 100%;"></div>
    </div>
</div>
<oframe:script src="${pageContext.request.contextPath}/eland/ph/ph001/js/ph001.js" type="text/javascript"/>
<oframe:script src="${pageContext.request.contextPath}/eland/ph/ph003/js/ph003_op.js" type="text/javascript"/>
