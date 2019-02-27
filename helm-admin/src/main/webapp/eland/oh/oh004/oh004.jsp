<%--
   意向院落
--%>
<%--院落信息检索--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<oframe:script src="${pageContext.request.contextPath}/eland/oh/oh004/js/oh004.js" type="text/javascript"/>
<div style="vertical-align: top;">
    <%--定义右侧面板 内容--%>
    <div id="oh004_qsh" style="position: relative;">
        <%--查询条件 --%>
        <div class="js_query_condition" style="position: relative;">
            <div class="panelBar">
                <ul class="toolBar">
                    <%--自定义检索--%>
                    <li onclick="oh004.openQSch(this)">
                        <a class="find" href="javascript:void(0)"><span>条件检索</span></a>
                    </li>
                    <li onclick="oh004.refleshData()">
                        <a class="reflesh" href="javascript:void(0)"><span>刷新检索</span></a>
                    </li>
                    <li>
                        <a class="export" onclick="Page.exportExcelByTemplate('oh004_div',
                             'webroot:eland/oh/oh004/export.xlsx',
                             'hsFullAddr,ctDate,hsSt,hsAddr,buyPersonName,buyPersonTel,handHsDate,buyHsCtDate,cmpWyCost,cmpQnCost,jsStatus,jsTime')">
                            <span>导出明细</span>
                        </a>
                    </li>

                    <%--切换展示方式--%>
                    <%--<li style="float: right;" class="model" onclick="oh004.changeShowModel(this,'m3');">--%>
                        <%--<a class="sum" href="javascript:void(0)"><span>统计</span></a>--%>
                    <%--</li>--%>
                    <%--<li style="float: right;" class="model" onclick="oh004.changeShowModel(this,'m1');">--%>
                        <%--<a class="list" href="javascript:void(0)"><span class="active">列表</span></a>--%>
                    <%--</li>--%>

                    <%--快捷进入--%>
                    <li style="float: right;" class="filter marr20" onclick="oh004.changeShowFilter(this, 'f2')">
                        <a class="filter" href="javascript:void(0);"><span>已外迁</span></a>
                    </li>
                    <li style="float: right;" class="filter" onclick="oh004.changeShowFilter(this, 'f3')">
                        <a class="filter" href="javascript:void(0);"><span class="active">未外迁</span></a>
                    </li>
                </ul>
            </div>
            <%--快速搜索框--%>
            <div id="oh004QSch" class="hidden"
                 style="position: absolute;width:100%;top: 0px;z-index: 100;background-color: #ffffff; margin-top: 40px;">
                <div class="triangle triangle-up" style="border-bottom-color: #2c7bae;top: -10px; left: 50px;"></div>
                <table class="border">
                    <tr>
                        <th width="10%"><label>户主名称：</label></th>
                        <td width="20%"><input name="PersonInfo.personName" condition="like" class="textInput"></td>
                            <th width="10%"><label>房屋地址：</label></th>
                            <td width="20%"><input name="PersonInfo.oldHsId.hsFullAddr" condition="like" class="textInput"></td>
                        <th width="10%"><label>是否已外迁：</label></th>
                        <td width="23%">
                            <input name="PersonInfo.isHkWq" isHkWq="isHkWq" type="hidden" condition="=" value="0">
                            <input type="text" itemcd="COMMON_YES_NO" atoption="CODE.getCfgDataOpt"
                                   value="否" class="autocomplete textInput acInput" autocomplete="off">
                        </td>
                    </tr>
                    <tr>
                        <td colspan="6" align="center">
                            <button onclick="oh004.refleshData();oh004.closeQSch(true);" class="btn btn-primary js_query marr20">查询
                            </button>
                            <button onclick="oh004.closeQSch(true);" class="btn btn-info">关闭</button>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
        <%--数据展示方式： m1:列表，m2:统计--%>
        <input type="hidden" class="js_query_model" value="m1"/>
        <%--数据过滤： f1:放弃院落，f2：已外迁，   f3:未外迁--%>
        <input type="hidden" class="js_query_filter" value="f3"/>
        <%--通用检索form--%>
        <form id="oh004Form">
            <input type="hidden" name="jsQueryFilter" value="f3">
            <input type="hidden" name="conditionName" value="PersonInfo.isHkWq,PersonInfo.familyOwnFlag">
            <input type="hidden" name="condition" value="=,=">
            <input type="hidden" name="conditionValue" value="0,1">
            <input type="hidden" name="sortColumn" value=""><%-- hsSt,buyHsCtDate--%>
            <input type="hidden" name="sortOrder" value=""><%--asc,asc--%>
            <input type="hidden" class="js_conditionValue" value="">
            <input type="hidden" name="resultField" value="">
            <input type="hidden" name="forceResultField" value=""><%--newHsId,jsTime--%>
            <input type="hidden" name="regUseType" value="1">
            <input type="hidden" name="rhtRegId" value="">
            <input type="hidden" name="rhtOrgId" value="">
            <input type="hidden" name="entityName" value="PersonInfo">
            <input type="hidden" name="divId" value="oh004_div">
            <input type="hidden" name="cmptName" id="cmptName" value="QUERY_WQ_INFO_CMPT"/>
            <input type="hidden" name="forward" id="forward" value="/eland/oh/oh004/oh004_list"/>
        </form>
        <div id="oh004_div" class="js_page" layoutH="55" style="position: relative;width: 100%;"></div>
    </div>
</div>
<script>
    $(document).ready(function () {
        oh004.refleshData();
    });
</script>
