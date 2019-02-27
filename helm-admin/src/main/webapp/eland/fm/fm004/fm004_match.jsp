<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<style>
    #fm004match table th {
        text-align: center;
        font-weight: normal;
    }

    #fm004match table td {
        text-align: center;
    }

    #hsIdTr span.cssTr {
        border: 1px solid #b7b7b7;
        padding: 5px 20px;
        margin-left: 5px;
        /* text-align: center; */
        margin-top: 5px;
        display: inline-block;
        position: relative;
        /* width: 50px; */
    }

    #hsIdTr label.removeX {
        cursor: pointer;
        /* color: #fb7226; */
        background: url("${pageContext.request.contextPath}/oframe/themes/blue/images/duibi/del.png") no-repeat;
        width: 10px;
        height: 10px;
        display: inline-block;
        background-size: 10px;
        margin-left: 10px;
        position: absolute;
        top: 0px;
        right: 0px;
    }

    #fm004match .queryTd {
        position: relative;
        text-align: center;
    }

    #fm004match .rIcon {
        width: 40px;
        position: absolute;
        right: 5px;
        top: 0px;
    }

    #fm004match .rIcon .del {
        cursor: pointer;
        color: #fb7226;
        background: url("${pageContext.request.contextPath}/oframe/themes/blue/images/duibi/del.png") no-repeat;
        width: 15px;
        height: 15px;
        display: inline-block;
        background-size: 15px;
        margin-left: 10px;
        position: absolute;
        top: 10px;
        right: 20px;
    }

    #fm004match .rIcon .copy {
        cursor: pointer;
        color: #fb7226;
        background: url("${pageContext.request.contextPath}/oframe/themes/blue/images/duibi/copy.png") no-repeat;
        width: 15px;
        height: 15px;
        display: inline-block;
        background-size: 15px;
        margin-left: 10px;
        position: absolute;
        top: 10px;
        right: 0px;
    }

    #hsIdTr td {
        text-align: left;
        vertical-align: baseline;
    }
</style>
<div id="fm004match">
    <table class="border" border="0" width="100%" id="fm004matchtable">
        <tr>
            <th width="100px;">方案名称</th>
            <input type="hidden" name="allFund" value=""/>
            <c:forEach var="item" items="${initResult}">
                <td name="queryTd" class="queryTd">${item.value.planName}
                    <input type="hidden" name="hsId" value="${item.value.hsId}"/>
                    <input type="hidden" name="buildId" value="${item.value.buildId}"/>
                    <input type="hidden" name="ctType" value="${item.value.ctType}"/>
                    <input type="hidden" name="ctName" value="${item.value.ctName}"/>
                    <input type="hidden" name="planName" value="${item.value.planName}"/>
                    <input type="hidden" name="schemeName" value="${item.key}"/>

                    <div class="rIcon">
                        <label class="del" onclick="fm004.removeMatchScheme(this, '1')"></label>
                        <label class="copy" onclick="fm004.cloneRank(this)"></label>
                    </div>
                </td>
            </c:forEach>
        </tr>
        <tr name="queryTr">
            <th>计算模型</th>
            <td style="display: none;text-align: center;">
                <oframe:select name="ctType" prjCd="${param.prjCd}" withEmpty="true" itemCd="10001"
                               onChange="fm004.changeMatchType(this);"/>
            </td>
        </tr>
        <tr id="hsIdTr" name="queryTr">
            <th>方案户籍</th>
            <td style="display: none;">
                <div>
                <span class="cssTr" name="hsIdTemp" style="display: none">
                    <input type="hidden" name="hsId" value=""/>
                    <span></span>
                    <label class="removeX" onclick="fm004.delHsId(this)"></label>
                </span>
                </div>
            </td>
        </tr>
    </table>
</div>
<script>
    $(document).ready(function () {
        $("td[name=queryTd]", $.pdialog.getCurrent()).each(function () {
            var $this = $(this);
            var hsId = $("input[name=hsId]", $this).val();
            var ctType = $("input[name=ctType]", $this).val();
            var ctName = $("input[name=ctName]", $this).val();
            var url = getGlobalPathRoot() + "eland/fm/fm004/fm004-queryYuFenMoney.gv";
            var packet = new AJAXPacket(url);
            packet.data.add("ctType", ctType);
            packet.data.add("hsId", hsId);
            packet.data.add("prjCd", getPrjCd());
            core.ajax.sendPacketHtml(packet, function (response) {
                var data = eval("(" + response + ")");
                var isSuccess = data.success;
                if (isSuccess) {
                    //查看款项中是否有新的款项，如果有就在table中添加
                    var allFund = $("input[name=allFund]", $.pdialog.getCurrent());
                    var allFundArr = allFund.val().split(",");
                    var newAllFund = [];
                    //判断input是否为空，如果为空直接添加行
                    if (allFundArr[0] != "") {
                        //判断是否有新款项，如果有就添加行
                        for (var i = 0; i < data.schemeInfo.length; i++) {
                            var hasFlag = false;
                            for (var j = 0; j < allFundArr.length; j++) {
                                if (allFundArr[j] == data.schemeInfo[i].name) {
                                    hasFlag = true;
                                    break;
                                }
                            }
                            if (!hasFlag) {
                                newAllFund.push(data.schemeInfo[i].name);
                                $("#fm004matchtable", $.pdialog.getCurrent()).append('<tr name="queryTr"><th>' + data.schemeInfo[i].name + '</th></tr>');
                            }
                        }
                        //拼接字符串
                        allFundArr = allFundArr.concat(newAllFund);
                        //更新总的方案input
                        allFund.val(allFundArr.join(","));
                    } else {
                        for (var k = 0; k < data.schemeInfo.length; k++) {
                            newAllFund.push(data.schemeInfo[k].name);
                            $("#fm004matchtable", $.pdialog.getCurrent()).append('<tr name="queryTr"><th>' + data.schemeInfo[k].name + '</th></tr>');
                        }
                        allFund.val(newAllFund.join(","));
                    }

                    var temp = 0;
                    $("tr[name=queryTr]", $("#fm004matchtable")).each(function () {
                        var $thisTr = $(this);
                        if ($thisTr.context.rowIndex == 1) {
                            //填充计算模型
                            var cloneTd = $("td:eq(0)", $thisTr).clone(true).css("display", "");
                            $("select[name=ctType]", cloneTd).find("option[value=" + ctType + "]").attr("selected", "true");
                            $thisTr.append(cloneTd);
                        } else if ($thisTr.context.rowIndex == 2) {
                            //填充方案户籍
                            var cloneTd = $("td:eq(0)", $thisTr).clone().css({"display": ""});
                            var hsIdArr = hsId.split(",");
                            var ctNameArr = ctName.split(",");
                            for (var i = 0; i < hsIdArr.length; i++) {
                                var cloneObj = $("div >span[name=hsIdTemp]", cloneTd).clone().css("display", "").attr("name", "");
                                $("input[name=hsId]", $(cloneObj)).val(hsIdArr[i]);
                                $(">span", $(cloneObj)).text(ctNameArr[i]);
                                $("span[name=hsIdTemp]", $(cloneTd)).after(cloneObj);
                            }
                            $thisTr.append(cloneTd);
                        } else {
                            //填充款项
                            if ($this.context.cellIndex == 1) {
                                //如果是第一列直接添加即可
                                $thisTr.append("<td>" + data.schemeInfo[temp++].value + "</td>");
                            } else {
                                //其余列
                                var tdNum = $("td", $thisTr).length;
                                var flag = false;
                                var tempk = 0;
                                //查看款项里有没有这一项,如果有写入数据，如果没有写入“-”
                                for (var k = 0; k < data.schemeInfo.length; k++) {
                                    if ($("th", $thisTr).text() == data.schemeInfo[k].name) {
                                        flag = true;
                                        tempk = k;
                                        break;
                                    }
                                }
                                if (flag) {
                                    //循环这一列同行的td，少一个就用“-”补齐，最后写入数据
                                    for (var t = 0; t < $this.context.cellIndex - 1 - tdNum; t++) {
                                        $thisTr.append("<td>-</td>");
                                    }
                                    $thisTr.append("<td>" + data.schemeInfo[tempk].value + "</td>");
                                } else {
                                    $thisTr.append("<td>-</td>");
                                }
                            }
                        }
                    });
                } else {
                    alertMsg.warn(data.errMsg);
                }
            }, false);
        })
    });
</script>