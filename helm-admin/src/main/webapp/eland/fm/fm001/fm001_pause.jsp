<%--
  Created by IntelliJ IDEA.
  User: shfb_wang
  Date: 2016/3/22 0022 17:12
  Copyright(c) 北京四海富博计算机服务有限公司
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div>
    <div class="panelBar">
        <ul class="toolBar">
            <li><a class="edit" onclick="fm001_pause.savePause()"><span>修改</span></a></li>
            <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
        </ul>
    </div>
    <div>
        <input type="hidden" name="hsFmId" value="${hsFmId}">
        <input type="hidden" name="flag" value="${flag}">
        <table class="border">
            <tr>
                <th width="20%"><label>暂缓说明：</label></th>
                <td>
                    <textarea name="fmStopDesc" class="required" rows="10" cols="5">${fmStopDesc}</textarea>
                </td>
            </tr>
        </table>
    </div>
</div>
<script>
    var fm001_pause = {
        savePause: function () {
            var container = $.pdialog.getCurrent();
            var fmStopDesc = $("textarea[name=fmStopDesc]", container).val();
            if (fmStopDesc) {
                // 提交修改内容
                var url = getGlobalPathRoot() + "eland/fm/fm001/fm001-savePause.gv";
                var ajaxPacket = new AJAXPacket(url);
                ajaxPacket.data.add("prjCd", getPrjCd());
                ajaxPacket.data.add("fmStopDesc", fmStopDesc);
                ajaxPacket.data.add("hsFmId", $("input[name=hsFmId]", container).val());
                ajaxPacket.data.add("flag", $("input[name=flag]", container).val());
                core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                    var jsonData = eval("(" + response + ")");
                    // 服务调用是否成功
                    var isSuccess = jsonData.success;
                    if (isSuccess) {
                        alertMsg.correct("修改成功！");
                        $.pdialog.closeCurrent();
                    } else {
                        alertMsg.error(jsonData.errMsg);
                    }
                });
            } else {
                alertMsg.warn("暂缓备注说明不可为空！");
                return false;
            }
        }
    }
</script>