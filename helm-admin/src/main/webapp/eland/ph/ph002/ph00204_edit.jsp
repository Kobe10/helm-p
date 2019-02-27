<%--院落房屋信息--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="panel">
    <h1>腾退管理
        <span class="panel_menu js_reload">取消</span>
        <span class="panel_menu" onclick="ph00204.saveMng(this)">保存</span>
    </h1>
    <%--面板内容--%>
    <div class="js_panel_context" style="min-height: 300px;">
        <form id="ph00204Frm" method="post">
            <table class="border" width="100%">
                <input type="hidden" name="buildId" value="${buildId}"/>
                <input type="hidden" name="ttUpRegId"
                       value='<oframe:entity  prjCd="${param.prjCd}" entityName="RegInfo" property="upRegId" value="${buildMng.BuildInfo.ttRegId}"/>'/>
                <tr>
                    <input type="hidden" name="ttRegId" value="${buildMng.BuildInfo.ttRegId}">
                    <th width="20%"><label>管理区域:</label></th>
                    <td>${buildMng.BuildInfo.ttUpRegName}</td>
                </tr>
                <tr>
                    <th><label>管理小组:</label></th>
                    <td>
                        <div style="position:relative;">
                            <input type="hidden" name="prjOrgId" value="${buildMng.BuildInfo.ttOrgId}"/>
                            <input type="text" name="prjOrgName" class="pull-left"
                                   readonly="readonly" value="${buildMng.BuildInfo.ttOrgName}"/>
                            <a title="选择" onclick="ph00204.editOrg(this);" class="btnLook">选择</a>
                        </div>
                    </td>
                </tr>
                <tr>
                    <th><label>中介公司:</label></th>
                    <td>
                        <oframe:select prjCd="${param.prjCd}" collection="${cmpMap}" name="ttCompanyId"
                                       value="${buildMng.BuildInfo.ttCompanyId}"/>
                    </td>
                </tr>
                <tr>
                    <th><label>主谈人员:</label></th>
                    <td>
                        <input type="text" name="ttMainTalk" class="required" value="${buildMng.BuildInfo.ttMainTalk}"/>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<script type="text/javascript">
    var ph00204 = {
        /**
         * 编辑管理区域
         * @param jsonData 图片数据
         */
        saveMng: function (clickObj) {
            var $form = $("#ph00204Frm", navTab.getCurrentPanel());
            if ($form.valid()) {
                // 提交页面数据
                var url = getGlobalPathRoot() + "eland/ph/ph002/ph002-saveMng.gv";
                var ajaxPacket = new AJAXPacket(url);
                ajaxPacket.data.data = $form.serializeArray();
                // 增加项目编码
                ajaxPacket.data.data.push({name: "prjCd", value: getPrjCd()});
                core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                    var jsonData = eval("(" + response + ")");
                    // 服务调用是否成功
                    var isSuccess = jsonData.success;
                    if (isSuccess) {
                        alertMsg.correct("处理成功");
                        index.loadInfoPanelByInnerObj(clickObj, "eland/ph/ph002/ph002-initMng.gv?buildId=${buildId}");
                    } else {
                        alertMsg.error(jsonData.errMsg);
                    }
                });
            }
        },


        editReg: function (obj) {
            var url = getGlobalPathRoot() + "eland/pj/pj004/pj004-initTree.gv?prjCd=" + getPrjCd() + "&treeType=1"
                    + "&fromOp=hs001";
            var width = $(obj).siblings("input[name=regName]").width();
            $(obj).openTip({href: url, height: "230", width: width, offsetX: 2, offsetY: 31});
        },
        editOrg: function (obj) {
            var url = getGlobalPathRoot() + "eland/pj/pj003/pj003-initTree.gv?prjCd=" + getPrjCd();
            var width = $(obj).siblings("input[name=prjOrgName]").width();
            $(obj).openTip({
                href: url, height: "200",
                width: width, offsetX: 2, offsetY: 31
            });
        }
    };
</script>
