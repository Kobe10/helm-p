<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%--添加子组织--%>
<div>
    <div class="panelBar">
        <ul class="toolBar">
            <li><a class="save" onclick="sys004Sub.saveNodeNames()"><span>保存</span></a></li>
            <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
        </ul>
    </div>
    <div>
        <form id="sys004BaseFrm" method="post" class="required-validate">
            <input type="hidden" value="${nodeInfo.Node.prjCd}" name="prjCd"/>
            <table class="border" id="sys004SubTable" layoutH="40">
                <tr>
                    <td><label>当前位置：${nodeInfo.Node.nodePathName}${nodeInfo.Node.orgName}</label></td>
                </tr>
                <tr class="hidden">
                    <td style="padding-left: 10px;">
                        <input type="hidden" value="${nodeInfo.Node.orgId}" name="upNodeId"/>
                        <input type="hidden" value="" name="orgId"/>
                        <input type="hidden" value="" name="orgStyle"/>
                        <input type="hidden" value="" name="orgLogo"/>
                        -><input type="text" name="orgName" value="${orgName}"/>
                        <span class="link marl10" onclick="table.deleteRow(this)">删除</span>
                        <span class="link marl10" onclick="table.addRow('sys004SubTable', this)">添加</span>
                    </td>
                </tr>
                <c:forEach items="1,2,3,4">
                    <tr>
                        <td style="padding-left: 10px;">
                            <input type="hidden" value="${nodeInfo.Node.orgId}" name="upNodeId"/>
                            <input type="hidden" value="" name="orgId"/>
                            <input type="hidden" value="" name="orgStyle"/>
                            <input type="hidden" value="" name="orgLogo"/>
                            -><input type="text" name="orgName" class="required" value="${orgName}"/>
                            <span class="link marl10" onclick="table.deleteRow(this)">删除</span>
                            <span class="link marl10" onclick="table.addRow('sys004SubTable', this)">添加</span>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </form>
    </div>
</div>
<script type="text/javascript">
    var sys004Sub = {
        /**
         * 保存节点名称及增加子节点列表
         */
        saveNodeNames: function () {
            // 根据表单ID验证表单
            if (!$("#sys004BaseFrm", $.pdialog.getCurrent()).valid()) {
                return false;
            }
            // 序列化表单
            var jsonData = $("#sys004BaseFrm", $.pdialog.getCurrent()).serializeArray();
            // 目录树类型
            var packet = new AJAXPacket();
            packet.data.data = jsonData;
            console.log(jsonData)
//            var prjCd = "0";
//            packet.data.add("prjCd", prjCd);
            // 提交界面展示
            packet.url = getGlobalPathRoot() + "oframe/sysmg/sys004/sys004-saveNodes.gv";
            core.ajax.sendPacketHtml(packet, function (response) {
                var data = eval("(" + response + ")");
                var isSuccess = data.success;
                if (isSuccess) {
                    var nodeInfos = data.OrgNodes;
                    var sFunc = $.pdialog.getCurrent().data("sFunc");
                    var close = true;
                    if (sFunc && $.isFunction(sFunc)) {
                        close = sFunc(nodeInfos);
                    }
                    if (close) {
                        $.pdialog.closeCurrent();
                    }
                } else {
                    alertMsg.error(data.errMsg);
                }
            }, true);
        }
    }
</script>