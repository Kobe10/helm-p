<%--批量修改房源数据--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div>
    <div class="panelBar">
        <ul class="toolBar">
            <li><a class="edit" onclick="Oh00301_bedit.batchSaveOhs()"><span>修改</span></a></li>
            <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
        </ul>
    </div>
    <div>
        <input type="hidden" name="newHsIds" value="${newHsIds}" id="newHsIds">
        <table class="border">
            <tr>
                <th width="30%"><label>开发商结算状态：</label></th>
                <td>
                    <select name="jsStatus" class="select" id="jsStatus">
                        <option value="1">已结算</option>
                        <option value="0">未结算</option>
                    </select>
                </td>
            </tr>
            <tr>
                <th><label>开发商结算时间：</label></th>
                <td class="js_condition">
                   <input type="text" id="jsTime" class="date">
                </td>
            </tr>
        </table>
    </div>
</div>
<script type="text/javascript">
    Oh00301_bedit = {
        /**
         * 修改状态及时间
         **/
        batchSaveOhs: function () {
            var url = getGlobalPathRoot() + "eland/oh/oh003/oh003-bEdit.gv";
            var ajaxPacket = new AJAXPacket(url);
            ajaxPacket.data.add("prjCd", getPrjCd());
            ajaxPacket.data.add("newHsIds",$("#newHsIds").val());
            ajaxPacket.data.add("jsTime", $("#jsTime").val());
            ajaxPacket.data.add("jsStatus", $("#jsStatus").val());

            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var jsonData = eval("(" + response + ")");
                // 服务调用是否成功
                var isSuccess = jsonData.success;
                if (isSuccess) {
                    alertMsg.correct("修改成功");
                    $.pdialog.closeCurrent();
                    oh003.refleshData();
                } else {
                    alertMsg.error(jsonData.errMsg);
                }
            });
        }
    }
</script>
