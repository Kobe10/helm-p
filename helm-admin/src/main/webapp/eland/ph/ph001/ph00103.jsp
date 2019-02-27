<%--
   新增区域
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div id="regionBar" class="panelBar">
    <ul class="toolBar">
        <oframe:op prjCd="${param.prjCd}" template="li" cssClass="save"
                   name="保存" rhtCd="edit_1_reg_rht"
                   onClick="pb00103.saveRegionInfo()"/>
        <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
    </ul>
</div>
<div layoutH="50" id="pj004RegionDetail">
    <input type="hidden" name="updateTreeId" value="${param.updateTreeId}"/>

    <form id="ph00103Rgform" method="post" class="required-validate">
        <input type="hidden" name="prjCd" value="${nodeInfo.PrjReg.prjCd}">
        <input type="hidden" name="regId" value="${nodeInfo.PrjReg.regId}">
        <input type="hidden" name="upRegId" value="${nodeInfo.PrjReg.upRegId}">
        <input type="hidden" name="oldPrjOrgId" value="${nodeInfo.PrjReg.prjOrgId}">
        <input type="hidden" name="regUseType" value="${nodeInfo.PrjReg.regUseType}">
        <input type="hidden" name="prjBuldId" value="${nodeInfo.PrjReg.prjBuldId}">
        <input type="hidden" name="lastUpdateTime" value="${nodeInfo.PrjReg.lastUpdateTime}">
        <table class="border">
            <tr>
                <th><label>区域名称：</label></th>
                <td>
                    <input type="text" name="regName" ${readonly} class="required" value="${nodeInfo.PrjReg.regName}"/>
                </td>
                <th><label>区域编码：</label></th>
                <td>
                    <div style="position: relative;">
                        <input type="text" name="regAttr15" value="${nodeInfo.PrjReg.regAttr15}"/>
                    </div>
                </td>
            </tr>
            <tr>
                <th><label>区域类型：</label></th>
                <td>
                    <oframe:select prjCd="${param.prjCd}" type="radio" name="regEntityType"
                                   value="${nodeInfo.PrjReg.regEntityType}" collection="${regTypeMap}"/>
                </td>
                <th><label>管理小组：</label></th>
                <td>
                    <div style="position: relative;">
                        <input type="hidden" name="prjOrgId" value="${nodeInfo.PrjReg.prjOrgId}"/>
                        <input type="text" name="prjOrgName" class="pull-left"
                               value="${nodeInfo.PrjReg.prjOrgName}"/>
                        <a title="选择" class="btnLook"
                           onclick="$(this).sltOrg(this, {args:{prjCd:getPrjCd(),orgTreeFlag:'1'}})">选择</a>
                    </div>
                </td>
            </tr>
            <tr>
                <th><label>区域地址：</label></th>
                <td colspan="3">
                    <input type="text" name="regAddr" value="${nodeInfo.PrjReg.regAddr}" size="60"/>
                </td>
            </tr>
            <tr>
                <th><label>区域描述：</label></th>
                <td colspan="3">
                    <textarea name="regDesc" cols="60" rows="4">${nodeInfo.PrjReg.regDesc}</textarea>
                </td>
            </tr>
        </table>
    </form>
</div>
<script type="text/javascript">
    pb00103 = {
        /**
         * 保存区域信息
         */
        saveRegionInfo: function () {
            var opCode = $("input[name=_opCode]", navTab.getCurrentPanel()).val();//_" + opCode
            var container = $.pdialog.getCurrent();
            var frm = $("#ph00103Rgform", container);
            var treeId = $("input[name=updateTreeId][type=hidden]", container).val();
            if (!treeId || treeId == "") {
                treeId = "ph001Tree_" + opCode;
            }
            if (frm.valid()) {
                var regId = $("input[name=regId]", container).val();
                var upRegId = $("input[name=upRegId]", container).val();
                var regName = $("input[name=regName]", container).val();
                var regDesc = $("textarea[name=regDesc]", container).val();
                var regAddr = $("input[name=regAddr]", container).val();
                var oldPrjOrgId = $("input[name=oldPrjOrgId]", container).val();
                var prjOrgId = $("input[name=prjOrgId]", container).val();
                var prjOrgName = $("input[name=prjOrgName]", container).val();
                var regUseType = $("input[name=regUseType]", container).val();
                var lastUpdateTime = $("input[name=lastUpdateTime]", container).val();
                var prjBuldId = $("input[name=prjBuldId]", container).val();
                var regEntityType = $("select[name=regEntityType]", container).val();
                var regAttr15 = $("input[name=regAttr15]", container).val();

                var packet = new AJAXPacket(getGlobalPathRoot() + "eland/pj/pj004/pj004-savePrjReg.gv?prjCd=" + getPrjCd());
                var cfmUpdateOrg = false;
                //判断组织是否修改，如果修改提示。
                if (oldPrjOrgId != prjOrgId) {
                    alertMsg.confirm(" 确认修改该区域下的所有子节点的归属组织?", {
                        okCall: function () {
                            cfmUpdateOrg = true;
                            // 查询参数
                            var formData = frm.serializeArray();
                            formData.push({name: "prjCd", value: getPrjCd()});
                            formData.push({name: "cfmUpdateOrg", value: cfmUpdateOrg});
                            packet.data.data = formData;
                            // 保存区域信息
                            core.ajax.sendPacketHtml(packet, function (response) {
                                var data = eval("(" + response + ")");
                                var isSuccess = data.success;
                                var regData = data.data;
                                if (isSuccess) {
                                    if (regId == "") {
                                        var zTree = $.fn.zTree.getZTreeObj(treeId);
                                        var pNode = zTree.getNodeByParam("id", regData.upRegId, null);
                                        var nodeInfo = {
                                            "id": regData.regId,
                                            "iconSkin": regData.iconSkin,
                                            "pId": regData.upRegId,
                                            iconSkin: "folder",
                                            "checked": "false",
                                            "name": regData.regName,
                                            open: "true"
                                        };
                                        zTree.addNodes(pNode, nodeInfo);
                                    } else {
                                        // 更新节点信息
                                        var zTree = $.fn.zTree.getZTreeObj(treeId);
                                        var uNode = zTree.getNodeByParam("id", regData.regId, null);
                                        var nodeInfo = {
                                            "id": regData.regId, "pId": regData.upRegId,
                                            "checked": "false", "name": regData.regName, open: "true"
                                        };
                                        // 合并修改信息
                                        uNode = $.extend(uNode, uNode, nodeInfo);
                                        zTree.updateNode(uNode);
                                    }
                                    // 关闭窗口
                                    $.pdialog.closeCurrent();
                                } else {
                                    alertMsg.error(data.errMsg);
                                }
                            }, true);
                        }
                    });
                } else {
                    // 查询参数
                    var formData = frm.serializeArray();
                    formData.push({name: "prjCd", value: getPrjCd()});
                    packet.data.data = formData;
                    // 保存区域信息
                    core.ajax.sendPacketHtml(packet, function (response) {
                        var data = eval("(" + response + ")");
                        var isSuccess = data.success;
                        var regData = data.data;
                        if (isSuccess) {
                            if (regId == "") {
                                var zTree = $.fn.zTree.getZTreeObj(treeId);
                                var pNode = zTree.getNodeByParam("id", regData.upRegId, null);
                                var nodeInfo = {
                                    "id": regData.regId, "pId": regData.upRegId, iconSkin: "folder",
                                    "checked": "false", "name": regData.regName, open: "true"
                                };
                                zTree.addNodes(pNode, nodeInfo);
                            } else {
                                // 更新节点信息
                                var zTree = $.fn.zTree.getZTreeObj(treeId);
                                var uNode = zTree.getNodeByParam("id", regData.regId, null);
                                var nodeInfo = {
                                    "id": regData.regId, "pId": regData.upRegId,
                                    "checked": "false", "name": regData.regName, open: "true"
                                };
                                // 合并修改信息
                                uNode = $.extend(uNode, uNode, nodeInfo);
                                zTree.updateNode(uNode);
                            }
                            // 关闭窗口
                            $.pdialog.closeCurrent();
                        } else {
                            alertMsg.error(data.errMsg);
                        }
                    }, true);
                }
            }
        }
    }
</script>