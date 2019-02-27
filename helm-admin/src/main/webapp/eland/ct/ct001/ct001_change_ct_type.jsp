<%--
  Created by IntelliJ IDEA.
  User: shfb_wang
  Date: 2016/1/29 0029 16:32
  Copyright(c) 北京四海富博计算机服务有限公司
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div>
    <div class="panelBar">
        <ul class="toolBar">
            <oframe:op rhtCd="change_ct_type" prjCd="${param.prjCd}" template="li" cssClass="save" name="保存"
                       onClick="ChangeCtTypeModel.saveChangeCtType('${hsId}','${hsCtId}');"/>
            <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
        </ul>
    </div>
    <div>
        <form id="ct001_change_ct_type" method="post" class="required-validate">
            <table class="border">
                <tr>
                    <th width="25%"><label>安置方式：</label></th>
                    <td>
                        <input type="hidden" name="oldCtType" value="${ctType}">
                        <oframe:select prjCd="${param.prjCd}" itemCd="10001" name="changeCtType" value="${ctType}"/>
                    </td>
                </tr>
                <tr>
                    <th><label>重新排号：</label></th>
                    <td>
                        <oframe:select prjCd="${param.prjCd}" type="radio" id="changeTypeYsNo" itemCd="COMMON_YES_NO"
                                       name="changeCtOrder" value="0"/>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<script type="text/javascript">
    var ChangeCtTypeModel = {
        /**
         * 保存修改后的安置方式
         * @param hsId
         * @param hsCtId
         */
        saveChangeCtType: function (hsId, hsCtId) {
            var ctType = $("select[name=changeCtType]", $.pdialog.getCurrent()).val();
            var oldCtType = $("input[name=oldCtType]",  $.pdialog.getCurrent()).val();
            if (ctType == oldCtType) {
                alertMsg.warn("安置方式未改变");
                return;
            }
            var changeCtOrder = $("input[name=changeCtOrder]:checked", $.pdialog.getCurrent()).val();
            var schemeType = $("input[name=schemeType]", navTab.getCurrentPanel()).val();
            alertMsg.confirm("确认更换安置方式吗？", {
                okCall: function () {
                    var url = getGlobalPathRoot() + "eland/ct/ct001/ct001-changeCtType.gv";
                    var packet = new AJAXPacket(url);
                    packet.data.add("hsId", hsId);
                    packet.data.add("hsCtId", hsCtId);
                    packet.data.add("ctType", ctType);
                    packet.data.add("changeCtOrder", changeCtOrder);
                    packet.data.add("prjCd", getPrjCd());
                    core.ajax.sendPacketHtml(packet, function (response) {
                        var data = eval("(" + response + ")");
                        var isSuccess = data.success;
                        if (isSuccess) {
                            alertMsg.correct("安置方式更换成功！");
                            $.pdialog.closeCurrent();
                        } else {
                            alertMsg.error(data.errMsg);
                        }
                    }, true);
                }
            });
        }
    }
</script>