<%--房屋腾退保存信息--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<div class="panel">
    <h1>安置信息
        <span class="panel_menu js_reload">取消</span>
        <span class="panel_menu" onclick="ph00305_edit.saveCt(this);">保存</span>
    </h1>
    <%--面板内容--%>
    <div class="js_panel_context" style="min-height: 300px;">
        <form id="ph00305Frm" class="required-validate">
            <input type="hidden" name="hsCtId" value="${hsCtInfo.HsCtInfo.hsCtId}">
            <input type="hidden" name="hsId" value="${hsId}">
            <table class="border">
                <tr>
                    <th width="15%;"><label>安置方式：</label></th>
                    <td width="20%"><oframe:name prjCd="${param.prjCd}" itemCd="10001" value="${hsCtInfo.HsCtInfo.ctType}"/></td>
                    <th width="15%;"><label>应安置人：</label></th>
                    <td width="20%"><c:set var="ctPsIds" value="${hsCtInfo.HsCtInfo.ctPsIds}${''}"/>
                        <c:forEach items="${fn:split(ctPsIds, ',') }" var="ctPsId"
                                   varStatus="varStatus">
                            ${personsData[ctPsId].Row.personName}&nbsp;
                        </c:forEach>
                    </td>
                    <th width="15%;"><label>安置状态：</label></th>
                    <td><oframe:name prjCd="${param.prjCd}" itemCd="CONTRACT_STATUS" value="${hsCtInfo.HsCtInfo.ctStatus}"/></td>
                </tr>
                <tr>
                    <th><label>应安置面积：</label></th>
                    <td>
                        <c:forEach items="${chooseRegMap}" var="chRegId">
                            <oframe:entity entityName="RegInfo" property="regName" prjCd="${param.prjCd}"
                                           value="${chRegId.key}"/>：
                            <c:set var="chRegMap" value="${chooseRegMap[chRegId.key]}"/>
                            <c:if test="${chRegMap.HsRegInfo.yazmj != null}">
                                (
                                ${chRegMap.HsRegInfo.yazmj}
                                <c:set var="sizeSum" value="${chRegMap.HsRegInfo.yazmj}${''}"/>

                                <c:forEach items="${aGiveSize}" var="item" varStatus="varStatus">
                                    <c:if test="${chRegId.key == item.HsCtGive.regId}">
                                        - <a onclick="ph00305_edit.openGive('${item.HsCtGive.hsCtGiveId}');"
                                             style="color: dodgerblue;cursor: pointer;">${item.HsCtGive.ctSize}</a>
                                        <c:set var="ctSize" value="${item.HsCtGive.ctSize}${''}"/>
                                        <c:set var="sizeSum" value="${sizeSum-ctSize}"/>
                                    </c:if>
                                </c:forEach>
                                <c:forEach items="${bGiveSize}" var="item">
                                    <c:if test="${chRegId.key == item.HsCtGive.regId}">
                                        + <a onclick="ph00305_edit.openGive('${item.HsCtGive.hsCtGiveId}');"
                                             style="color: dodgerblue;cursor: pointer;">${item.HsCtGive.ctSize}</a>
                                        <c:set var="ctSize" value="${item.HsCtGive.ctSize}${''}"/>
                                        <c:set var="sizeSum" value="${sizeSum+ctSize}"/>
                                    </c:if>
                                </c:forEach>
                                )
                            </c:if>
                            <fmt:formatNumber value="${sizeSum}" pattern="#########.##"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        </c:forEach>
                    </td>
                    <th>
                        <label>协议签订日期：</label>
                    </th>
                    <td>
                        <c:choose>
                            <c:when test="${hsCtInfo.HsCtInfo.ctDate != ''}">
                                <input type="text" name="ctDate" maxlength="17" class="number" minlength="8"
                                       value="${hsCtInfo.HsCtInfo.ctDate}">
                            </c:when>
                            <c:otherwise>
                                <input type="text" name="ctDate" maxlength="17" minlength="8" class="number" value=""/>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <th>
                        <label>生效截止时间：</label>
                    </th>
                    <td>
                        <input type="hidden" name="buildId" value="${houseInfo.HouseInfo.buildId}"/>
                        <input type="hidden" name="buildCtLimitDateBak"
                               value='<oframe:entity entityName="BuildInfo" property="buildCtLimitDate" prjCd="${param.prjCd}" value="${houseInfo.HouseInfo.buildId}"/>'>
                        <input type="text" name="buildCtLimitDate" class="date" dateFmt="yyyy-MM-dd"
                               value='<oframe:date value="${buildCtLimitDate}" format="yyyy-MM-dd"/>'>
                    </td>
                </tr>
                <tr>
                    <th><label>签约信息备注：</label></th>
                    <td colspan="5"><textarea style="width: 80%;" rows="5"
                                              name="ctNote">${hsCtInfo.HsCtInfo.ctNote}</textarea></td>
                </tr>

                <tr>
                    <th class="subTitle" colspan="6">
                        <h1 style="float: left;">安置款项</h1>
                    </th>
                </tr>
                <tr>
                    <td colspan="6">
                        <div style="min-height: 100px;">

                            <table class="list" id="ph00305EditTreeTable" width="100%" style="margin: 0!important;">
                                <thead>
                                <tr data-tt-id="0" style="background-color: #e2edf3;line-height: 25px">
                                    <th width="12%">序号</th>
                                    <th width="15%">补助项目</th>
                                    <th width="10%">补助内容</th>
                                    <th>计算方式</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach items="${ctItems}" var="item" varStatus="varStatus">
                                    <tr data-tt-id="${item.Item.nameEn}" data-tt-parent-id="${item.Item.upName}">
                                        <td style="text-align: left!important;"><label>${varStatus.index + 1}</label>
                                        </td>
                                        <td>${item.Item.nameCh}</td>
                                        <td style="text-align: right;">
                                            <c:choose>
                                                <c:when test="${item.Item.money == ''}">
                                                    <input type="text" style="width: 95%;" class="number js_money"
                                                           name="${item.Item.nameEn}" value="0">
                                                </c:when>
                                                <c:otherwise>
                                                    <input type="text" style="width: 95%;" class="number js_money"
                                                           name="${item.Item.nameEn}"
                                                           value='<oframe:money value="${item.Item.money}" format="number"/>'>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td style="text-align: left;">${item.Item.ruleDesc}</td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </td>
                </tr>
                <tr>
                    <th class="subTitle" colspan="6">
                        <h1 style="float: left;">安置房源</h1>
                    </th>
                </tr>
                <tr>
                    <td colspan="6" style="border: 0; padding: 0;">
                        <div style="min-height: 100px;">
                            <table class="list" width="100%" style="border-top: 0;">
                                <thead>
                                <tr>
                                    <th>序号</th>
                                    <th>购房区域</th>
                                    <th>购房人</th>
                                    <th>共同居住人</th>
                                    <th>房屋地址</th>
                                    <th>房屋居室</th>
                                    <th>房屋面积</th>
                                    <th>购房单价</th>
                                    <th>处理状态</th>
                                    <th>状态时间</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach items="${chooseHs}" var="item" varStatus="varStatus">
                                    <tr>
                                        <td>${varStatus.index + 1}</td>
                                        <td><oframe:entity prjCd="${param.prjCd}" entityName="RegInfo"
                                                           property="regName"
                                                           value="${item.chooseHs.chooseHsRegId}"/>
                                        </td>
                                        <td>
                                            <c:set var="buyPersonId" value="${item.chooseHs.buyPersonId}${''}"/>
                                                ${personsData[buyPersonId].Row.personName}
                                        </td>
                                        <td>
                                            <c:set var="gtJzPsIds" value="${item.chooseHs.gtJzPsIds}${''}"/>
                                            <c:forEach items="${fn:split(gtJzPsIds, ',') }" var="jzPs"
                                                       varStatus="varStatus">
                                                ${personsData[jzPs].Row.personName}&nbsp;
                                            </c:forEach>
                                        </td>
                                        <td>${item.chooseHs.chooseHsAddr}</td>
                                        <td>
                                            <oframe:name prjCd="${param.prjCd}" itemCd="HS_ROOM_TYPE" value="${item.chooseHs.chooseHsTp}"/>
                                        </td>
                                        <td>${item.chooseHs.chooseHsSize}</td>
                                        <td>${item.chooseHs.chooseHsPrice}</td>
                                        <td><oframe:name prjCd="${param.prjCd}" itemCd="CHOOSE_STATUS"
                                                         value="${item.chooseHs.chooseStatus}"/></td>
                                        <td>${item.chooseHs.chooseStatusDate}</td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<script type="text/javascript">
    ph00305_edit = {
        // 计算总款
        calTotal: function () {
            var $form = $("#ph00305Frm", navTab.getCurrentPanel());
            var totalMoney = 0.00;
            var totalMoneyWithoutHk = 0.00;
            $("input.js_money", $form).each(function () {
                var $this = $(this);
                var name = $this.attr("name");
                if (name == "allCtMoney01" || name == "allCtMoney02") {
                    return true;
                }
                var temp = parseFloat($this.val().replaceAll(",", ""));
                totalMoney = totalMoney + temp;
                // 非户口非累加
                if (name != "ctMoney13") {
                    totalMoneyWithoutHk = totalMoneyWithoutHk + temp;
                }
            });
            // 保留两位小数
            totalMoney = totalMoney.toFixed(2);
            totalMoneyWithoutHk = totalMoneyWithoutHk.toFixed(2);
            // 刷新显示值
//            $("#ph00305_total", $form).html(totalMoney);
//            $("input[type=hidden][name=allCtMoney01]", $form).val(totalMoney);
//
//            $("#ph00305_total2", $form).html(totalMoneyWithoutHk);
//            $("input[type=hidden][name=allCtMoney02]", $form).val(totalMoneyWithoutHk);
        },
        saveCt: function (obj) {
            // 点击对象
            var _this = $(obj);
            var $form = $("#ph00305Frm", navTab.getCurrentPanel());
            if ($form.valid()) {
                // 提交页面数据
                var url = getGlobalPathRoot() + "eland/ph/ph003/ph003-saveTt.gv";
                var ajaxPacket = new AJAXPacket(url);
                // 增加请求参数
                ajaxPacket.data.add("hsId", $("input[name=hsId]", $form).val());
                ajaxPacket.data.add("hsCtId", $("input[name=hsCtId]", $form).val());
                ajaxPacket.data.add("prjCd", getPrjCd());
                var ctDateNum = $("input[name=ctDate]", $form).val();
                ajaxPacket.data.add("ctDate", ctDateNum);
                // 整院限制时间
                ajaxPacket.data.add("buildId", $("input[name=buildId]", $form).val());
                ajaxPacket.data.add("buildCtLimitDate", $("input[name=buildCtLimitDate]", $form).val());
                ajaxPacket.data.add("buildCtLimitDateBak", $("input[name=buildCtLimitDateBak]", $form).val());
                // 签约备注信息
                ajaxPacket.data.add("ctNote", $("textarea[name=ctNote]", $form).val());
                // 签约款项
                var moneyNames = [];
                $("input.js_money", $form).each(function () {
                    var $this = $(this);
                    moneyNames.push($this.attr("name"));
                    ajaxPacket.data.add($this.attr("name"), $this.val().replaceAll(",", ""));
                });
                ajaxPacket.data.add("moneyNames", moneyNames.join(","));
                core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                    var jsonData = eval("(" + response + ")");
                    // 服务调用是否成功
                    var isSuccess = jsonData.success;
                    if (isSuccess) {
                        alertMsg.correct("保存成功");
                        var panelContainer = _this.parents("div.panelContainer");
                        index.loadInfoPanel($(panelContainer).attr("panelKey"), $(panelContainer).attr("url"));
                    } else {
                        alertMsg.error(jsonData.errMsg);
                    }
                });
            }
        },

        openGive: function (hsCtGiveId) {
            var url = getGlobalPathRoot() + "eland/ph/ph003/ph003-openGive.gv?prjCd=" + getPrjCd() + "&hsCtGiveId=" + hsCtGiveId;
            $.pdialog.open(url, "ph00305", "赠送详情", {
                mask: true, max: false, width: 750, height: 210
            });
        },

        /**
         * 处理 每个节点展示 文本
         */
        serializIdText: function (tabId) {
            var treeTable = $("#" + tabId, navTab.getCurrentPanel());
            var treeTrs = treeTable.find("tr[data-tt-id][data-tt-parent-id]:visible");
            var trLength = treeTrs.length;
            var idMap = new Map();
            var pidMap = new Map();
            //loop all tr construct two Map;
            treeTrs.each(function () {
                var _oneTr = $(this);
                var oneTrId = _oneTr.attr("data-tt-id");
                var oneTrPid = _oneTr.attr("data-tt-parent-id");
                idMap.put(oneTrId, _oneTr);
                var _oneTrChild = pidMap.get(oneTrPid);
                if (!_oneTrChild) {
                    _oneTrChild = [];
                    pidMap.put(oneTrPid, _oneTrChild);
                }
                _oneTrChild.push(_oneTr);
                _oneTrChild = null;
            });

            //loop all tr set tr text
            treeTrs.each(function (index, element) {
                var cTr = $(this);
                var cTrId = cTr.attr("data-tt-id");
                var cTrPid = cTr.attr("data-tt-parent-id");
                var childArr = pidMap.get(cTrPid);

                var childIdx = ph00305_edit.objInArray(cTrId, childArr);
                var pareText = "";
                //获取当前元素位于数组第几个，set tr text
                if (cTrPid != '') {
                    var parentTr = idMap.get(cTrPid);
                    pareText = parentTr.find("td:eq(0)").find("label").text();
                }
                var currText = "";
                if (pareText != "") {
                    pareText = pareText + "-";
                }
                currText = pareText + (childIdx + 1);
                cTr.find("td:eq(0)").find("label").text(currText);
            });
        },
        objInArray: function (id, array) {
            for (var i = 0; i < array.length; i++) {
                var obj1 = array[i];
                if (id == obj1.attr("data-tt-id")) {
                    return i;
                }
            }
            return -1;
        }
    };
    $(document).ready(function () {
        var options = {
            initialState: 'expanded'
        };
        var tabId = "ph00305EditTreeTable";
        $("#" + tabId, navTab.getCurrentPanel()).treetable(options);
        ph00305_edit.serializIdText(tabId);
    });
</script>