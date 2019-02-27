<%--院落房屋信息--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="panel">
    <h1>人员统计情况
        <span class="panel_menu js_reload">刷新</span>
    </h1>
    <%--面板内容--%>
    <div class="js_panel_context" style="min-height: 115px;">
        <div style="height: 100%; width: 100%; display: inline-block; float: right;">
            <table class="border" width="100%">
                <%--<tr>--%>
                <%--<th width="18%" style="line-height: 30px; text-align: center;background-color: #fdffc3"><label>项目</label></th>--%>
                <%--<th width="18%" style="text-align: center;background-color: #fdffc3"><label>项目内容</label></th>--%>
                <%--<th width="18%" style="text-align: center;background-color: #fdffc3"><label>备注说明</label></th>--%>
                <%--</tr>--%>
                <tr>
                    <th width="38%" style="line-height: 30px;"><label>残疾人数</label></th>
                    <td>${personBean.HouseInfo.cjNum}</td>
                    <%--<td>附件${personBean.HouseInfo.cjDocIds}</td>--%>
                </tr>
                <tr>
                    <th style="line-height: 30px;"><label>低保人数</label></th>
                    <td>${personBean.HouseInfo.dbNum}</td>
                    <%--<td>附件${personBean.HouseInfo.dbDocIds}</td>--%>
                </tr>
                <tr>
                    <th style="line-height: 30px;"><label>备注说明</label></th>
                    <td>${personBean.HouseInfo.dbcjNotes}</td>
                    <%--<td>附件${personBean.HouseInfo.dbDocIds}</td>--%>
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
