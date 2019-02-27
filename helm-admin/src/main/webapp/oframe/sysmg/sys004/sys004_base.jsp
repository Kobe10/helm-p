<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%--公司组织基本信息--%>
<div>
    <div class="panelBar">
        <ul class="toolBar">
            <li><a class="save" onclick="sys004Base.saveNodeNames()"><span>保存</span></a></li>
            <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
        </ul>
    </div>
    <div>
        <form id="sys004BaseFrm" method="post" class="required-validate">
            <input type="hidden" value="${nodeInfo.Node.prjCd}" name="prjCd"/>
            <input type="hidden" value="${nodeInfo.Node.upNodeId}" name="upNodeId"/>
            <input type="hidden" value="${nodeInfo.Node.orgId}" name="orgId"/>
            <table class="border">
                <tr>
                    <th width="20%"><label>组织名称：</label></th>
                    <td><input type="text" name="orgName" value="${nodeInfo.Node.orgName}"/></td>
                </tr>
                <tr>
                    <th><label>默认样式：</label></th>
                    <td>
                        <oframe:select prjCd="${param.prjCd}" itemCd="DEFAULT_STYLE" name="orgStyle"
                                       value="${nodeInfo.Node.orgStyle}"/>
                    </td>
                </tr>
                <tr>
                    <th>自定义LOGO：</th>
                    <td>
                        <div style="width: 80%;text-align: center;">
                            <img width="239px" height="39px" style="position: relative;"
                                 onclick="sys004Base.changeImg(this);" name="orgLogo" title="238px*39px"
                                 src="${nodeInfo.Node.orgLogo}"
                                 class="mar5">
                            <span class="removeX" name="removeX" style="right: 202px;top: 165px;"
                                  onclick="sys004Base.deletePic(this);">X</span>
                            <input type="hidden" name="orgLogo" value="${nodeInfo.Node.orgLogo}"/>
                            <span onclick="sys004Base.changeImg(this);" style="position: relative;top:-14px;padding: 3px 5px;
                                height:17px;font-size: 12px!important;" class="btn btn-pri marl10 marr10 ">上传图片
                                <input style="width:60px; height:20px; position:absolute; right:0; top:0;
                                    opacity:0;filter:alpha(opacity=0); z-index:11; cursor:pointer;"
                                    accept=".jpg,.png,.gif" name="importPic" id="importPic" type="file">
                            </span>
                        </div>

                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<script type="text/javascript">
    var sys004Base = {
        uploadObj: null,
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
            var prjCd = "0";
            packet.data.add("prjCd", prjCd);
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
        },

        /**
         * 删除logo
         */
        deletePic: function (obj) {
            $(obj).closest("div").find("img").attr("src", "");
            $("input[name=orgLogo]", $.pdialog.getCurrent()).val("");
            $("span[name=removeX]", $.pdialog.getCurrent()).hide();
        },

        /**
         * 打开上传图片
         */
        importFile: function (obj) {
            var uploadURL = getGlobalPathRoot() + "oframe/sysmg/sys004/sys004-base64Image.gv?prjCd=" + getPrjCd();
            var fileElementId = sys004Base.uploadObj.parent().find("input[type=file]").attr("id");
            $.ajaxFileUpload({
                        url: uploadURL,
                        secureuri: false,
                        fileElementId: fileElementId,
                        dataType: 'json',
                        success: function (data, status, fileElementId) {
                            if (data.errMsg) {
                                alertMsg.warn(data.errMsg);
                            } else {
                                var container = $(sys004Base.uploadObj).parent();
                                $("img", container).attr("src", data.base64Image);
                                $("input[type=hidden]", container).val(data.base64Image);
                                $("span[name=removeX]", $.pdialog.getCurrent()).show();
                            }
                        }
                    }
            )
        },

        changeImg: function (obj) {
            var clickFileBtn = $(obj).parent().find("input[type=file]");
            clickFileBtn.attr("id", new Date().getTime());
            clickFileBtn.val("");
            clickFileBtn.unbind("change", sys004Base.importFile);
            clickFileBtn.bind("change", sys004Base.importFile);
            sys004Base.uploadObj = $(obj);
        }
    };
    $(function () {
        var removeX = $("span[name=removeX]", $.pdialog.getCurrent());
        var orgLogo = $("img[name=orgLogo]", $.pdialog.getCurrent()).attr("src");
        if (!orgLogo) {
            removeX.hide();
        }
    });
</script>