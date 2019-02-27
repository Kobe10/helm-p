<%--院落房屋信息--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="panel">
    <h1>腾退管理
        <span class="panel_menu js_reload">刷新</span>
    </h1>
    <%--面板内容--%>
    <div class="js_panel_context" style="min-height: 115px;">
        <div style="height: 100%; width: 100%; display: inline-block; float: right;">
            <table class="border" width="100%">
                <tr style="line-height: 30px">
                    <th width="38%" style="line-height: 30px"><label>管理小组</label></th>
                    <td><oframe:org orgId="${houseMng.HouseInfo.ttOrgId}" prjCd="${prjCd}" property="PrjOrg.Node.prjOrgName"/></td>
                </tr>
                <tr>
                    <th style="line-height: 30px"><label>中介公司</label></th>
                    <td><oframe:name prjCd="${param.prjCd}" collection="${cmpMap}" value="${houseMng.HouseInfo.ttCompanyId}"/></td>
                </tr>
                <tr>
                    <th style="line-height: 30px"><label>主谈人员</label></th>
                    <td>${houseMng.HouseInfo.ttMainTalk}</td>
                </tr>
            </table>
        </div>
    </div>
</div>
<script type="text/javascript">
    var ph00311 = {
    };
    $(document).ready(function () {
    });
</script>
