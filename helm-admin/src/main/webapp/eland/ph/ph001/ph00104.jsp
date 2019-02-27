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
                   onClick="pb00104.saveRegionInfo()"/>
        <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
    </ul>
</div>
<div layoutH="50" id="pj004RegionDetail">
    <input type="hidden" name="updateTreeId" value="${param.updateTreeId}"/>

    <form id="ph00104Rgform" method="post" class="required-validate">
        <input type="hidden" name="prjCd" value="${nodeInfo.PrjReg.prjCd}">
        <input type="hidden" name="regId" value="${nodeInfo.PrjReg.regId}">
        <input type="hidden" name="upRegId" value="${nodeInfo.PrjReg.upRegId}">
        <input type="hidden" name="regUseType" value="${nodeInfo.PrjReg.regUseType}">
        <input type="hidden" name="oldPrjOrgId" value="${nodeInfo.PrjReg.prjOrgId}">
        <input type="hidden" name="upRegName" value="${nodeInfo.PrjReg.regName}">
        <input type="hidden" name="upRegAddr" value="${nodeInfo.PrjReg.regAddr}">
        <input type="hidden" name="prjOrgId" value="${nodeInfo.PrjReg.prjOrgId}"/>
        <table class="border" id="ph00103Table">
            <tr>
            </tr>
            <tr class="hidden">
                <th width="7%"><label>类型：</label></th>
                <td width="10%"><oframe:select prjCd="${param.prjCd}" name="regEntityType" value=""
                                               itemCd="REG_TYPE"/></td>
                <th width="7%"><label>名称：</label></th>
                <td><input type="text" name="regName" onchange="pb00104.changeRegAddr(this);"
                           class="noErrorTip" size="15"/></td>
                <th width="7%"><label>编码：</label></th>
                <td><input type="text" name="regAttr15" class="noErrorTip" size="15"/></td>
                <th width="7%"><label>地址：</label></th>
                <td width="15%">
                    <input type="text" name="regAddr" class="noErrorTip" size="15"/>
                </td>
                <td>
                    <span class="link marl10" onclick="table.deleteRow(this)">删除</span>
                    <span class="link marl10" onclick="pb00104.addRow('ph00103Table', this)">添加</span>
                </td>
            </tr>
            <c:forEach items="1,2,3,4">
                <tr>
                    <th width="7%"><label>类型：</label></th>
                    <td width="10%">
                        <oframe:select prjCd="${param.prjCd}" name="regEntityType"
                                       value="${nodeInfo.PrjReg.regEntityType}" itemCd="REG_TYPE"/>
                    </td>
                    <th width="7%"><label>名称：</label></th>
                    <td>
                        <input type="text" name="regName" onchange="pb00104.changeRegAddr(this);"
                               class="required noErrorTip"
                               size="15"/>
                    </td>
                    <th width="7%"><label>编码：</label></th>
                    <td><input type="text" name="regAttr15" class="noErrorTip" size="15"/></td>
                    <th width="7%"><label>地址：</label></th>
                    <td>
                        <input type="hidden" name="prjOrgId" value="${nodeInfo.PrjReg.prjOrgId}"/>
                        <input type="text" name="regAddr" class="noErrorTip"
                               value=""
                               size="15"/>
                    </td>
                    <td>
                        <span class="link marl10" onclick="table.deleteRow(this)">删除</span>
                        <span class="link marl10" onclick="pb00104.addRow('ph00103Table', this)">添加</span>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </form>
</div>
<script type="text/javascript">
    pb00104 = {
        addRow: function (tableId, clickObject) {
            var copyRow = table.addRowNoInit(tableId, clickObject);
            $("input[name=regName]", copyRow).addClass("required");
            $(copyRow).initUI();
        },

        /**
         * 修改区域地址
         **/
        changeRegAddr: function (obj) {
            var $this = $(obj);
            var regName = $this.val();
            var upRegAddr = $("input[name=upRegAddr", $.pdialog.getCurrent()).val();
            $this.closest("tr").find("input[name=regAddr]").val(upRegAddr + regName);
        },

        /**
         * 保存区域信息
         */
        saveRegionInfo: function () {
            var opCode = $("input[name=opCode]", navTab.getCurrentPanel()).val();//_" + opCode
            var container = $.pdialog.getCurrent();
            var frm = $("#ph00104Rgform", container);
            var treeId = $("input[name=updateTreeId][type=hidden]", container).val();
            if (!treeId || treeId == "") {
                treeId = "ph001Tree_" + opCode;
            }
            if (frm.valid()) {
                var packet = new AJAXPacket(getGlobalPathRoot() + "eland/pj/pj004/pj004-addPrjReg.gv?prjCd=" + getPrjCd());
                // 查询参数
                var formData = frm.serializeArray();
                formData.push({name: "prjCd", value: getPrjCd()});
                packet.data.data = formData;
                // 保存区域信息
                core.ajax.sendPacketHtml(packet, function (response) {
                    var data = eval("(" + response + ")");
                    var isSuccess = data.success;
                    var regDataArr = data.data;
                    if (isSuccess) {
                        var zTree = $.fn.zTree.getZTreeObj(treeId);
                        for (var i = 0; i < regDataArr.length; i++) {
                            var regData = regDataArr[i];
                            var pNode = zTree.getNodeByParam("id", regData.pId, null);
                            zTree.addNodes(pNode, regData);
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
</script>