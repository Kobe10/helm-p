<%--房屋腾退信息--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<div class="panel">
    <h1>签约信息
        <span class="panel_menu js_reload">刷新</span>
    </h1>
    <%--面板内容--%>
    <div class="js_panel_context" style="min-height: 300px;">
        <table class="border">
            <tr>
                <th width="15%"><label>安置方式：</label></th>
                <td><oframe:name prjCd="${param.prjCd}" itemCd="10001" value="${ctType}"/></td>
            </tr>
        </table>
        <div class="tabs">
            <div class="tabsHeader">
                <div class="tabsHeaderContent">
                    <ul>
                        <c:forEach items="${schemeTypeMap}" var="item" varStatus="varStatus">
                            <c:if test="${hsBcJsMap[item.key].HsBcJe.schemeId != '' && hsBcJsMap[item.key].HsBcJe.schemeId != null}">
                                <li class="js_load_tab selected"
                                    onclick="ph00305.loadTabContext(this);">
                                    <a href="javascript:void(0);"><span>${item.value}</span></a>
                                </li>
                            </c:if>
                        </c:forEach>
                    </ul>
                </div>
            </div>
            <div class="tabsContent">
                <%--签约信息--%>
                <c:forEach items="${schemeTypeMap}" var="sType" varStatus="varStatus">
                    <c:if test="${hsBcJsMap[sType.key].HsBcJe.schemeId != '' && hsBcJsMap[sType.key].HsBcJe.schemeId != null}">
                        <div style="min-height: 200px">
                            <div style="min-height: 100px;">
                                <table class="border">
                                    <tr>
                                        <th width="15%">签约状态：</th>
                                        <td width="35%">${hsBcJsMap[sType.key].HsBcJe.ctStatus}</td>
                                        <th width="15%">签约时间：</th>
                                        <td width="35%">
                                            <c:set var="ctDate"
                                                   value="${hsBcJsMap[sType.key].HsBcJe.ctDate}"/>
                                            <c:if test="${ctDate != null && ctDate != ''}">
                                                ${ctDate}
                                            </c:if>
                                        </td>
                                    </tr>
                                </table>
                                <table class="list border js_tree_table" id="ph00305TreeTable_${varStatus.index}"
                                       width="100%"
                                       style="margin: 0!important;">
                                    <thead>
                                    <tr data-tt-id="0" style="background-color: #e2edf3;line-height: 25px">
                                        <th width="15%">序号</th>
                                        <th width="15%">补助项目</th>
                                        <th width="10%">补助内容</th>
                                        <th>计算方式</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach items="${hsBcJsItemsMap[sType.key]}" var="item"
                                               varStatus="varStatus">
                                        <tr data-tt-id="${item.Item.nameEn}"
                                            data-tt-parent-id="${item.Item.upName}">
                                            <td style="text-align: left!important;">
                                                <label>${varStatus.index + 1}</label>
                                            </td>
                                            <td>${item.Item.nameCh}</td>
                                            <td style="text-align: right;">
                                                <c:choose>
                                                    <c:when test="${item.Item.money == ''}">
                                                        0.00
                                                    </c:when>
                                                    <c:otherwise>
                                                        <oframe:money value="${item.Item.money}"
                                                                      format="number"/>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td style="text-align: left;">${item.Item.ruleDesc}
                                                <c:set var="errSubjectId"
                                                       value="${item.Item.subjectId}${'_'}${sType.key}"/>
                                                <span style="float:right;color: #ff0000;">${fn:substring(errMap[errSubjectId], fn:indexOf(errMap[errSubjectId], ':')+1, fn:length(errMap[errSubjectId]))}</span>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </c:if>
                </c:forEach>
            </div>
        </div>
        <div class="mart5" style="min-height: 100px;">
            <table class="border">
                <tr>
                    <th class="subTitle"><h1 style="float: left;">安置房源</h1></th>
                </tr>
                <tr>
                    <td style="border: 0; padding: 0;">
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
                                    <td><oframe:entity prjCd="${param.prjCd}" entityName="NewHsInfo" property="hsSt"
                                                       value="${item.chooseHs.chooseHsId}"/>
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
                                        <oframe:name prjCd="${param.prjCd}" itemCd="HS_ROOM_TYPE"
                                                     value="${item.chooseHs.chooseHsTp}"/>
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
                    </td>
                </tr>
            </table>
        </div>
    </div>
</div>
<script type="text/javascript">
    var ph00305 = {
        /**
         * 加载tab页的内容
         * @param obj
         */
        loadTabContext: function (obj) {
            var _this = $(obj);
            var url = _this.attr("url");
            var urlData = getGlobalPathRoot() + encodeURI(encodeURI(url));
            var contextDiv = _this.closest("div.tabs").find("div.tabsContent").find(">div:eq(" + _this.index() + ")");
            if (contextDiv.html() == "") {
                initUI(contextDiv);
            }
        },

        /**
         * 自动计算新增选房的安置面积
         */
        calcViewSize: function (obj) {
            var hsBuildSize = $("input[name=hsBuildSize]", navTab.getCurrentPanel()).val();
            var $thisTd = $(obj).closest("td");
            var fpmj = $("input[name=fpmj]", $thisTd).val();
            if (fpmj == null || fpmj == 0) {
                $("input[name=fpmj]", $thisTd).val(hsBuildSize);
                fpmj = hsBuildSize;
                //$(obj).addClass("inputWarn").val("分配选房面积不能大于建筑面积");
            }
            var regCoeff = $("input[name=regCoeff]", $thisTd).val();
            var ctHsCanSize = parseFloat(fpmj * regCoeff).toFixed(2);
            $("input[name=sjmj]", $thisTd).val(ctHsCanSize);
            //计算赠送过程
            var js_ctSizeA = 0;
            $("input.js_ctSizeA", $thisTd).each(function () {
                var $this = $(this);
                js_ctSizeA = parseFloat(js_ctSizeA) + parseFloat($this.val());
            });
            var js_ctSizeB = 0;
            $("input.js_ctSizeB", $thisTd).each(function () {
                var $this = $(this);
                js_ctSizeB = parseFloat(js_ctSizeB) + parseFloat($this.val());
            });
            var calcResult = (ctHsCanSize - js_ctSizeA + js_ctSizeB).toFixed(2);
            var calcStr = "";
            if (js_ctSizeA == 0 && js_ctSizeB == 0) {
                calcStr = calcResult;
            } else if (js_ctSizeA == 0 && js_ctSizeB != 0) {
                calcStr = calcResult + " ( " + ctHsCanSize + " + " + js_ctSizeB + " ) ";
            } else if (js_ctSizeA != 0 && js_ctSizeB == 0) {
                calcStr = calcResult + " ( " + ctHsCanSize + " - " + js_ctSizeA + " ) ";
            } else {
                calcStr = calcResult + " ( " + ctHsCanSize + " - " + js_ctSizeA + " + " + js_ctSizeB + " ) ";
            }
            //有赠送，显示详细赠送公式
            $thisTd.parent("tr").find("td.js_ctCanHsSize").text(calcStr);
            $thisTd.parent("tr").find("input[name=lastSize]", $.pdialog.getCurrent()).val(calcResult);

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

                var childIdx = ph00305.objInArray(cTrId, childArr);
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
        $("table.js_tree_table").each(function () {
            var tabId = $(this).attr("id");
            $("#" + tabId, navTab.getCurrentPanel()).treetable(options);
            ph00305.serializIdText(tabId);
        });
    });
</script>