<%--待签约处理--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="mar5">
    <div style="position: relative;">
        <div class="panelBar" style="position: relative; width: 100%;">
            <ul class="toolBar">
                <li onclick="ct00103.showOrHideCond(this)">
                    <a class="find" href="javascript:void(0);"><span>检索</span></a>
                </li>
                <li onclick='Page.query($("#ct00103QueryForm"),"");'>
                    <a class="reflesh" href="javascript:void(0);"><span>刷新</span></a>
                </li>
                <li onclick="Page.exportExcel('ct00103_list_print','HouseInfo.hsCd,HouseInfo.hsOwnerPersons,HouseInfo.hsFullAddr,HouseInfo.HsCtInfo.ctDate,HouseInfo.HsCtInfo.chooseHsSid')">
                    <a class="export" href="javascript:void(0);">
                        <span>导出</span>
                    </a>
                </li>
                <li onclick="$.printBox('ct00103_list_print')">
                    <a class="print" href="javascript:void(0);">
                        <span>打印</span>
                    </a>
                </li>
            </ul>
        </div>
        <div class="hidden" id="ct00103create"
             style="position: absolute;width:100%;top: 0px;z-index: 100;background-color: #ffffff; margin-top: 40px;">
            <table class="border">
                <tr>
                    <th>
                        档案编号：
                    </th>
                    <td>
                        <input type="text" name="HouseInfo.hsCd" condition="like"/>
                    </td>
                    <th>
                        产承人：
                    </th>
                    <td>
                        <input type="text" name="HouseInfo.hsOwnerPersons" condition="like"/>
                    </td>
                    <th>
                        房屋地址：
                    </th>
                    <td>
                        <input type="text" name="HouseInfo.hsFullAddr" condition="like"/>
                    </td>
                </tr>
                <tr>
                    <th>
                        签约状态：
                    </th>
                    <td>
                        <input name="HouseInfo.HsCtInfo.ctStatus" type="hidden" condition="=" value="2"
                               class="textInput">
                        <input type="text" itemCd="CONTRACT_STATUS"
                               value='<oframe:name itemCd="CONTRACT_STATUS" prjCd="${param.prjCd}" value="2"/>'
                               atOption="CODE.getCfgDataOpt" class="autocomplete textInput">
                    </td>
                    <th>
                        安置方式：
                    </th>
                    <td>
                        <input name="HouseInfo.HsCtInfo.ctType" type="hidden" condition="=" value=""
                               class="textInput">
                        <input type="text" itemCd="10001" atOption="CODE.getCfgDataOpt" class="autocomplete textInput">
                    </td>
                    <th>
                        打印序号单：
                    </th>
                    <td>
                        <input name="HouseInfo.HsCtInfo.printChHsStatus" type="hidden" condition="=" value=""
                               class="textInput">
                        <input type="text" itemCd="PRINT_STATUS" atOption="CODE.getCfgDataOpt"
                               class="autocomplete textInput">
                    </td>
                </tr>
                <tr>
                    <td colspan="6" align="center">
                        <button type="button" id="schBtn" class="btn btn-primary"
                                onclick="ct00103.query();ct00103.hideSearch()">查询
                        </button>
                        <button onclick="ct00103.closeQSch(false)" type="button" class="btn btn-info">关闭</button>
                    </td>
                </tr>
            </table>
        </div>
        <form id="ct00103QueryForm">
            <input type="hidden" name="entityName" value="HouseInfo"/>
            <input type="hidden" name="conditionName" value="">
            <input type="hidden" name="condition" value="">
            <input type="hidden" name="conditionValue" value="">
            <input type="hidden" name="sortColumn" value="HouseInfo.HsCtInfo.chooseHsSid">
            <input type="hidden" name="sortOrder" value="desc">
            <input type="hidden" class="js_conditionValue" value="">
            <input type="hidden" name="divId" value="ct00103_list_print">
            <input type="hidden" name="resultField"
                   value="HouseInfo.hsCd,HouseInfo.hsOwnerPersons,HouseInfo.hsFullAddr,HouseInfo.ttOrgId,HouseInfo.HsCtInfo.ctType,HouseInfo.HsCtInfo.ctDate,HouseInfo.HsCtInfo.chooseHsSid,HouseInfo.HsCtInfo.printCtStatus,HouseInfo.HsCtInfo.printReCtStatus,HouseInfo.HsCtInfo.printChHsStatus">
            <%--必须包含的字段--%>
            <input type="hidden" name="forceResultField"
                   value="HouseInfo.hsId,HouseInfo.hsOwnerPersons,HouseInfo.hsFullAddr">
            <input type="hidden" name="forward" id="forward" value="/eland/ct/ct001/ct00103_list"/>
        </form>
        <div class="js_page" id="ct00103_list_print" layoutH="55"></div>
    </div>
</div>
<script>
    var ct00103 = {
        /**
         * 查看居民详情
         * @param hsId
         */
        viewHouse: function (hsId) {
            var url = "eland/ph/ph003/ph003-init.gv?hsId=" + hsId;
            index.quickOpen(url, {opCode: "ph003-init", opName: "居民详情", fresh: true});
        },
        /**
         * 显示 隐藏 检索按钮
         */
        showOrHideCond: function (clickObj) {
            if (!$(clickObj).hasClass("active")) {
                $("#ct00103create", navTab.getCurrentPanel()).show().attr("active", "on");
                $("#ct00103create input:eq(0)", navTab.getCurrentPanel()).focus();
            }
        },
        hideSearch: function () {
            var ct00103create = $("#ct00103create", navTab.getCurrentPanel());
            if (ct00103create.is(":visible")) {
                ct00103create.hide();
            } else {
                alert("没打开");
            }
        },
        /**
         * 关闭检索
         * @param forceFlag 强制关闭
         */
        closeQSch: function (forceFlag) {
            var close = forceFlag;
            if (!forceFlag) {
                var active = $("#ct00103create", navTab.getCurrentPanel()).attr("active");
                if ("on" == active) {
                    close = true;
                } else {
                    close = false;
                }
            }
            if (close) {
                $("#ct00103create", navTab.getCurrentPanel()).hide();
            }
        },
        /**
         * 查询
         */
        query: function () {
            var formObj = $("#ct00103QueryForm", navTab.getCurrentPanel());
            // 获取输入框
            var dialogObj = $("#ct00103create", navTab.getCurrentPanel());
            // 覆盖的查询条件
            var coverConditionName = [];
            var coverCondition = [];
            var coverConditionValue = [];
            var coverNames = [];
            // 循环当前查询条件
            $("input", dialogObj).each(function () {
                var $this = $(this);
                var attrName = $this.attr("name");
                var tagName = $this[0].tagName;
                var condition = $this.attr("condition");
                var value = $this.val();
                if ("INPUT" == tagName && condition) {
                    coverNames.push(attrName);
                    if (value != "") {
                        coverConditionName.push(attrName);
                        coverCondition.push(condition);
                        coverConditionValue.push(value);
                    }
                }
            });
            // 获取历史查询条件
            var oldConditionName = $("input[name=conditionName]", formObj).val().split(",");
            var oldCondition = $("input[name=condition]", formObj).val().split(",");
            var oldConditionValue = $("input[name=conditionValue]", formObj).val().split(",");

            // 实际使用的查询条件
            var newConditionName = [];
            var newCondition = [];
            var newConditionValue = [];

            // 处理历史查询条件，存在覆盖则删除原有的查询条件
            for (var i = 0; i < oldConditionName.length; i++) {
                var conditionName = oldConditionName[i];
                var found = false;
                for (var j = 0; j < coverNames.length; j++) {
                    if (conditionName == coverNames[j]) {
                        found = true;
                        break;
                    }
                }
                // 未找到匹配的条件
                if (!found) {
                    newConditionName.push(oldConditionName[i]);
                    newCondition.push(oldCondition[i]);
                    newConditionValue.push(oldConditionValue[i]);
                }
            }
            // 追加新的查询条件
            newConditionName = newConditionName.concat(coverConditionName);
            newCondition = newCondition.concat(coverCondition);
            newConditionValue = newConditionValue.concat(coverConditionValue);
            // 重新设置查询条件
            $("input[name=conditionName]", formObj).val(newConditionName.join(","));
            $("input[name=condition]", formObj).val(newCondition.join(","));
            $("input[name=conditionValue]", formObj).val(newConditionValue.join(","));
            // 再次执行查询
            Page.query($("#ct00103QueryForm"), "");
        }
    };

    $(document).ready(function () {
        ct00103.query()
    });
</script>