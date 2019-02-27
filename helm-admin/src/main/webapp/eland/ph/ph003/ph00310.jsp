<%--院落房屋信息--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="panel">
    <h1>房屋经营情况
        <span class="panel_menu js_reload">刷新</span>
    </h1>
    <%--面板内容--%>
    <div class="js_panel_context" style="min-height: 115px;">
        <div style="height: 100%; width: 100%; display: inline-block; float: right;">
            <table class="border" width="100%">
                <tr style="line-height: 30px">
                    <th width="38%" style="line-height: 30px"><label>营业执照</label></th>
                    <td>${bizBean.HouseInfo.businessCertNum}</td>
                </tr>
                <tr>
                    <th style="line-height: 30px"><label>临街院落</label></th>
                    <td><oframe:name prjCd="${param.prjCd}" value="${bizBean.HouseInfo.xlFwFlag}" itemCd="COMMON_YES_NO"/></td>
                </tr>
                <tr>
                    <th style="line-height: 30px"><label>经营面积</label></th>
                    <td>${bizBean.HouseInfo.businessHsSize}</td>
                </tr>
                <tr>
                    <th style="line-height: 30px"><label>备注说明</label></th>
                    <td>${bizBean.HouseInfo.businessNote}</td>
                </tr>
            </table>
        </div>
    </div>
</div>
<script type="text/javascript">
    var ph00310 = {
    };
    $(document).ready(function () {
    });
</script>
