<%--院落信息展示页面--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="mart5">
    <div class="pageNav">
        <a href="javascript:index.quickOpen('eland/ph/ph001/ph001-init.gv',{opCode:'ph001-init',opName:'居民信息',fresh:true})">开发刷数据</a>--->
    </div>
    <%--基本信息--%>
    <div style="width: 100%;"
         class="panelContainer">
        <div class="panel">
            <h1><i style="color: red; font-size: 18px;">按钮刷数据使用，平时不开放（请谨慎使用）：</i></h1>

            <div style="min-height: 600px;">
                <table class="border" style="text-align: center">
                    <tr>
                        <td width="30%" style="font-size: 15px"><b>刷数据所需参数或文件</b></td>
                        <td style="font-size: 15px"><b>执行按钮</b></td>
                    </tr>
                    <tr>
                        <td></td>
                        <td><span class="btn btn-primary marr10 marl10" onclick="ph008Fuck.clickItem();">更新房产Hscd</span>
                        </td>
                    </tr>
                    <tr>
                        <td><input id="gylHsDataFile" name="gylHsDataFile" type="file" alt="请选择导入文档"
                                   placeholder="请选择导入文档"/></td>
                        <td><span class="btn btn-primary marr10 marl10"
                                  onclick="ph008Fuck.gylHsData();">光源里房产数据导入</span>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
    </div>
</div>
<script>
    ph008Fuck = {
        clickItem: function () {
            var url = getGlobalPathRoot() + "eland/ph/ph008/ph008-devTest.gv";
            var packet = new AJAXPacket(url);
            packet.data.add("prjCd", getPrjCd());
            core.ajax.sendPacketHtml(packet, function (response) {
                var jsonData = eval("(" + response + ")");
                if (jsonData.success) {
                    alertMsg.correct("刷数据成功！");
                } else {
                    alertMsg.warn(jsonData.errMsg);
                }
            }, true);
        },

        gylHsData: function () {
            var gylHsDataFile = $("#gylHsDataFile", navTab.getCurrentPanel()).val();
            if (!gylHsDataFile) {
                alertMsg.warn("请选择要导入文件！");
                return false;
            }
            var uploadURL = getGlobalPathRoot() + "eland/ph/ph008/ph008-gylHsData.gv?prjCd=" + getPrjCd();
            var ajaxbg = $("#background,#progressBar");
            ajaxbg.show();
            $.ajaxFileUpload({
                        url: uploadURL,
                        secureuri: false,
                        fileElementId: "gylHsDataFile",
                        dataType: 'json',
                        success: function (data, status, fileElementId) {
                            if (data.isSuccess) {
                                alertMsg.correct("刷数据成功！");
                            } else {
                                alertMsg.error(data.errMsg);
                            }
                            var ajaxbg = $("#background,#progressBar");
                            ajaxbg.hide();
                        }
                    }
            )
        }
    }
</script>