<%--
  Created by IntelliJ IDEA.
  User: shfb_wang
  Date: 2015/7/23 0023 14:18
  Copyright(c) 北京四海富博计算机服务有限公司
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>

<div style="margin: 5px">
    <c:set var="chNum" value="0"/>
    <table style="width: 99.99%;border-collapse: collapse; border: 2px dashed #e81313;line-height: 28px">
        <tr>
            <th width="10%" style="text-align: right"><label>请求名称：</label></th>
            <td>
                ${PROC_INST_INFO.ProcInsInfo.Variables.procInsName}
            </td>
        </tr>
        <tr>
            <th width="10%" style="text-align: right"><label>请求备注：</label></th>
            <td>
                ${PROC_INST_INFO.ProcInsInfo.ProcBidVars.taskComment}
            </td>
        </tr>
    </table>
</div>
<%--引入 房产信息详情页 --%>
<jsp:include page="/eland/ph/ph003/ph00301-initS.gv?prjCd=${prjCd}&hsId=${hsId}&procReadonly=false"/>

<oframe:script src="${pageContext.request.contextPath}/eland/ph/ph003/js/ph00301_task.js" type="text/javascript"/>
<oframe:script src="${pageContext.request.contextPath}/eland/ph/ph003/js/ph00301_input.js" type="text/javascript"/>
<script>
    $(document).ready(function () {
        $(":input").attr("disabled", "disabled");
        $(":text").each(function () {
            var $this = $(this);
            var tTitle = $this.attr("title");
            if (tTitle) {
                $this.attr("title", tTitle + "；" + $this.val());
            } else {
                $this.attr("title", $this.val());
            }
        });
        $("textarea[name=taskComment]").removeAttr("disabled");
    });
</script>