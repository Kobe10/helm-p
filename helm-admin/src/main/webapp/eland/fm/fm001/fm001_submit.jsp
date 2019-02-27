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
            <li><a class="edit" onclick="fm001_bsubmit.batchSaveSubmit()"><span>修改</span></a></li>
            <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
        </ul>
    </div>
    <div>
        <input type="hidden" name="hsFmIds" value="${hsFmIds}">
        <table class="border">
            <tr>
                <th width="20%"><label>批量申请批次：</label></th>
                <td>
                    <label>
                        <input type="text" name="fmMakeNum" class="textInput required" />
                    </label>
                </td>
            </tr>
        </table>
    </div>
</div>
<script>
    var fm001_bsubmit = {
        /**
         * 批量保存 制折批次
         */
        batchSaveSubmit: function () {
            var container = $.pdialog.getCurrent();
            var fmMakeNum = $("input[name=fmMakeNum]", container).val();
            if (fmMakeNum) {
                // 提交修改内容
                var url = getGlobalPathRoot() + "eland/fm/fm001/fm001-bSaveSubmit.gv";
                var ajaxPacket = new AJAXPacket(url);
                ajaxPacket.data.add("prjCd", getPrjCd());
                ajaxPacket.data.add("tab", "submit");
                ajaxPacket.data.add("fmMakeNum", fmMakeNum);
                ajaxPacket.data.add("hsFmIds", $("input[name=hsFmIds]", container).val());
                core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                    var jsonData = eval("(" + response + ")");
                    // 服务调用是否成功
                    var isSuccess = jsonData.success;
                    if (isSuccess) {
                        alertMsg.correct("修改成功");
                        $.pdialog.closeCurrent();
                    } else {
                        alertMsg.error(jsonData.errMsg);
                    }
                });
            } else {
                alertMsg.warn("制折批次与制折申请时间不可为空！");
                return false;
            }
        }
    }
</script>