<%--院落房屋信息--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="panel">
    <h1>腾退管理
        <span class="panel_menu js_reload">刷新</span>
        <span class="panel_menu" onclick="ph00204.editMng(this);">编辑</span>
    </h1>
    <%--面板内容--%>
    <div class="js_panel_context" style="min-height: 200px;">
        <table class="border" width="100%">
            <tr>
                <th width="20%"><label>管理区域:</label></th>
                <td>${buildMng.BuildInfo.ttUpRegName}</td>
            </tr>
            <tr>
                <th><label>管理小组:</label></th>
                <td>${buildMng.BuildInfo.ttOrgName}</td>
            </tr>
            <tr>
                <th><label>中介公司:</label></th>
                <td>${buildMng.BuildInfo.ttCompanyName}</td>
            </tr>
            <tr>
                <th><label>主谈人员:</label></th>
                <td>${buildMng.BuildInfo.ttMainTalk}</td>
            </tr>
        </table>
    </div>
</div>
<script type="text/javascript">
    var ph00204 = {
        /**
         * 编辑管理区域
         * @param jsonData 图片数据
         */
        editMng: function (clickObj) {
            index.loadInfoPanelByInnerObj(clickObj, "eland/ph/ph002/ph002-initMng.gv?method=edit&buildId=${buildId}&prjCd=${prjCd}");
        }
    };
</script>
