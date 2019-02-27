<%--居民签约控制--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="panel">
    <h1>房产评估及房改售房情况
        <span class="panel_menu js_reload js_qux">取消</span>
        <span class="panel_menu" onclick="ph00301_edit.savePG()">保存</span>
    </h1>

    <div style="min-height: 100px">
        <form id="ph00301_pgedit">
            <h1 class="marl20">第二次房产评估情况</h1>
            <table class="border mart5" width="100%">
                <tr>
                    <th width="30%"><label>变更基准价后房屋评估价值：</label></th>
                    <td width="20%">
                        <input name="afterChangeNewWithHold" class="number" type="text" value="${afterChangeNewWithHold}"/>
                    </td>
                    <th width="30%"><label>变更基准价后扣除成新价房屋价值：</label></th>
                    <td width="20%">
                        <input name="afterChangePgMoney2" class="number" type="text" value="${afterChangePgMoney2}"/>
                    </td>
                </tr>
            </table>
            <h1 class="marl20">第一次房产评估及房改售房情况</h1>
            <table class="border marb5" width="100%">
                <input type="hidden" name="hsId" value="${hsId}">
                <tr>
                    <th width="10%"><label>房屋评估进度：</label></th>
                    <td width="15%"><oframe:select itemCd="FINISH_RESULT" value="${pgStatus}" name="pgStatus"/>
                        <span class="link cursorpt marl10" docTypeName="评估附件" relType="100"
                              onclick="ph00301_edit.showDoc(this,'${hsId}')">
                            <label>附件</label>
                            <input type="hidden" name="pgDocs" value='${pgDocs}'>
                        </span>
                    </td>
                    <th width="10%"><label>评估公司：</label></th>
                    <td width="15%"><input type="text" value="${pgCompany}" name="pgCompany"></td>
                    <th width="10%"><label>房屋评估价值：</label></th>
                    <td width="15%"><input type="text" value="${newWithHold}" class="number" name="newWithHold"></td>
                </tr>
                <tr>
                    <th><label>扣除成新价房屋价值：</label></th>
                    <td><input type="text" value="${basePrice}" class="number" name="basePrice"></td>
                    <th><label>装修、设备及附属物：</label></th>
                    <td><input type="text" value="${pgMoney1}" class="number" name="pgMoney1"></td>
                    <th><label>房屋重置成新价：</label></th>
                    <td><input type="text" value="${pgMoney2}" class="number" name="pgMoney2"></td>
                </tr>
                <tr>
                    <th><label>房改售房进展：</label></th>
                    <td><oframe:select itemCd="FINISH_RESULT" value="${transferStatus}" name="transferStatus"/>
                        <span class="link cursorpt marl10" docTypeName="房改售房附件" relType="100"
                              onclick="ph00301_edit.showDoc(this,'${hsId}')">
                            <label>附件</label>
                            <input type="hidden" name="transferDocs" value='${transferDocs}'>
                        </span>
                    </td>
                    <th><label>代扣房改售房款：</label></th>
                    <td><input type="text" value="${withHold}" class="number" name="withHold"></td>
                    <th><label></label></th>
                    <td></td>
                </tr>
            </table>
        </form>
    </div>
</div>
<script type="text/javascript">
    ph00301_edit = {
        savePG: function () {
            var currForm = $("#ph00301_pgedit", navTab.getCurrentPanel());
            if (currForm.valid()) {
                var url = getGlobalPathRoot() + "eland/ph/ph003/ph003-savePG.gv";
                var packet = new AJAXPacket(url);
                packet.data.data = currForm.serializeArray();
                packet.data.data.push({name: "prjCd", value: getPrjCd()});
                core.ajax.sendPacketHtml(packet, function (response) {
                    var jsonData = eval("(" + response + ")");
                    if (jsonData.success) {
                        alertMsg.correct("保存成功");
                        $(".js_qux").click();
                    } else {
                        alertMsg.error(jsonData.errMsg);
                    }
                });
            }
        },

        /**
         * 显示关联文档信息
         * @param clickObj 点击对象
         */
        showDoc: function (clickObj, relId) {
            var $span = $(clickObj);
            var docIds = $span.find("input[type=hidden]").val();
            var docTypeName = $span.attr("docTypeName");
            if (!docTypeName) {
                docTypeName = "";
            }
            docTypeName = encodeURI(encodeURI(docTypeName));
            var relType = $span.attr("relType");
            var editAble = $span.attr("editAble");
            if (!editAble) {
                editAble = true;
            }
            var url = getGlobalPathRoot() + "oframe/common/file/file-init.gv?prjCd=" + getPrjCd() + "&docTypeName=" + docTypeName
                    + "&docIds=" + docIds + "&editAble=" + editAble
                    + "&relType=" + relType + "&relId=" + relId;
            $.pdialog.open(url, "file", "附件上传", {
                height: 600, width: 800, mask: true,
                close: ph00301_edit.docClosed,
                param: {clickSpan: $span}
            });
        },

        /**
         * 关闭上传窗口
         * @param param
         * @returns {boolean}
         */
        docClosed: function (param) {
            var uploadedFiles = file.getAllUploadFiles();
            var $span = param.clickSpan;
            //处理上传附件，展示
            var oldDocIds = $span.find("input[type=hidden]").val();
            var oldDocIdArr = [];
            oldDocIdArr = oldDocIds.split(",");
            var docIdArr = [];
            for (var i = 0; i < uploadedFiles.length; i++) {
                //保存已经上传的docId
                var uploadedFile = uploadedFiles[i];
                docIdArr.push(uploadedFile.docId);
            }
            $span.find("input[type=hidden]").val(docIdArr.join(","));
            if (docIdArr.length > 0) {
                var label = $span.find("label");
                if (label.html().startWith("上传")) {
                    label.html(label.html().replace("上传", "查看"));
                }
            } else {
                var label = $span.find("label");
                if (label.html().startWith("查看")) {
                    label.html(label.html().replace("查看", "上传"));
                }
            }
            // 调用文件的关闭
            return file.closeD();
        }
    }

</script>
