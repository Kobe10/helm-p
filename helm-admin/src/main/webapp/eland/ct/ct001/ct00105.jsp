<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div id="regionBar" class="panelBar">
    <ul class="toolBar">
        <li><a class="save" onclick="ct00105.confirmAdd();$.pdialog.closeCurrent()"><span>确定</span></a></li>
    </ul>
</div>

<div id="ct00105List">
    <table class="table" width="100%">
        <thead>
        <tr>
            <th width="5%">
                <input type="checkbox" class="checkboxCtrl" group="ids"/>
            </th>
            <th>被关联安置人</th>
            <th>被关联房屋地址</th>
            <th>关联安置人</th>
            <th>关联房屋地址</th>
            <th>关联关系</th>
            <th>备注说明</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${resultList}" var="item" varStatus="varStatus">
            <tr>
                <td width="5%">
                    <input type="checkbox" group="ids">
                </td>
                <td>${item.Row.ctPsNamesA}
                    <input type="hidden" name="hsCtId" value="${item.Row.hsCtIdA}"
                           psName="${item.Row.ctPsNamesA}"
                           ctType="${item.Row.ctTypeA}"
                           hsAddr="${item.Row.hsFullAddrA}"
                           hsId="${item.Row.hsIdA}"
                           printCtStatus="${item.Row.printCtStatusA}"
                           hsType="${item.Row.hsTypeA}"
                           hsType_Name="${item.Row.hsType_NameA}"
                           hsOwnerType="${item.Row.hsOwnerTypeA}"
                           hsOwnerType_Name="${item.Row.hsOwnerType_NameA}"
                           hsCd="${item.Row.hsCdA}">
                </td>
                <td>${item.Row.hsFullAddrA}</td>
                <td>${item.Row.ctPsNamesB}
                    <input type="hidden" name="hsCtId" value="${item.Row.hsCtIdB}"
                           psName="${item.Row.ctPsNamesB}"
                           ctType="${item.Row.ctTypeB}"
                           hsAddr="${item.Row.hsFullAddrB}"
                           hsId="${item.Row.hsIdB}"
                           printCtStatus="${item.Row.printCtStatusB}"
                           hsType="${item.Row.hsTypeB}"
                           hsType_Name="${item.Row.hsType_NameB}"
                           hsOwnerType="${item.Row.hsOwnerTypeB}"
                           hsOwnerType_Name="${item.Row.hsOwnerType_NameB}"
                           hsCd="${item.Row.hsCdB}">
                </td>
                <td>${item.Row.hsFullAddrB}</td>
                <td>${item.Row.relName}</td>
                <td>${item.Row.reNote}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<script type="text/javascript">
    ct00105 = {
        confirmAdd: function () {
            var hsCtIds = [];
            var ctTypes = [];
            var names = [];
            var addrs = [];
            var hsIds = [];
            var printCtStatus = [];
            var hsType_Names = [];
            var hsOwnerType_Names = [];
            var hsCds = [];
            var container = $.pdialog.getCurrent();
            var htId = $("input[name=htId]", navTab.getCurrentPanel()).val();
            $("#ct00105List", container).find(":checkbox[checked]").each(
                    function (i) {
                        var temp = $(this).closest("tr");
                        $("input[name=hsCtId]", temp).each(function () {
                            var $this = $(this);
                            if ($this.val() != htId) {
                                var hsCtId = $this.val();
                                var ctType = $this.attr("ctType");
                                var psName = $this.attr("psName");
                                var hsAddr = $this.attr("hsAddr");
                                var hsId = $this.attr("hsId");
                                var printCt = $this.attr("printCtStatus");
                                var hsType_Name = $this.attr("hsType_Name");
                                var hsOwnerType_Name = $this.attr("hsOwnerType_Name");
                                var hsCd = $this.attr("hsCd");
                                hsCtIds.push(hsCtId);
                                ctTypes.push(ctType);
                                names.push(psName);
                                addrs.push(hsAddr);
                                hsIds.push(hsId);
                                printCtStatus.push(printCt);
                                hsType_Names.push(hsType_Name);
                                hsOwnerType_Names.push(hsOwnerType_Name);
                                hsCds.push(hsCd);
                            }
                        });
                    });
            $("input[name=toHsCtId]", navTab.getCurrentPanel()).val(hsCtIds.join(","));
            $("input[name=toCtType]", navTab.getCurrentPanel()).val(ctTypes.join(","));
            var $table = $("#ct00107Table", navTab.getCurrentPanel());
            var hiddenRow = $table.find("tr.hidden");
            $table.find("tr:visible:not(:first)").remove();
            for (var i = 0; i < hsCtIds.length; i++) {
                var copyRow = hiddenRow.clone().removeClass("hidden");
                copyRow.find("td:eq(0)").text(hsCds[i]);
                copyRow.find("td:eq(1)").text(names[i]);
                copyRow.find("td:eq(2)").text(addrs[i]);
                copyRow.find("td:eq(3)").text(hsType_Names[i]);
                copyRow.find("td:eq(4)").text(hsOwnerType_Names[i]);
                copyRow.find("input[name=hsCtId]").val(hsCtIds[i]);
                copyRow.find("input[name=hsId]").val(hsIds[i]);
                copyRow.find("input[name=ctType]").val(ctTypes[i]);
                copyRow.find("input[name=printCtStatus]").val(printCtStatus[i]);
                copyRow.find("select[name=ctType]").val(ctTypes[i]);
                $table.append(copyRow);
            }
            $table.initUI();
        }
    };

    $(function () {
        var container = $.pdialog.getCurrent();
        var toHsCtIdStr = $("input[name=toHsCtId]", navTab.getCurrentPanel()).val();
        var toHsCtIdArr = [];
        if (toHsCtIdStr && toHsCtIdStr != "") {
            toHsCtIdArr = toHsCtIdStr.split(",");
            for (var j = 0; j < toHsCtIdArr.length; j++) {
                var ctId = toHsCtIdArr[j];
                var htId = $("input[name=htId]", navTab.getCurrentPanel()).val();
                if (ctId != htId) {
                    $("input[name=hsCtId][value=" + ctId + "]", container).closest("tr").find(":checkbox").attr("checked", "true");
                }
            }
        }
    })
</script>

