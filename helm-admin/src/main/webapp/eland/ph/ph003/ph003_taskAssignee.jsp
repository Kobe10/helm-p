<%--
  Created by IntelliJ IDEA.
  User: shfb_wang
  Date: 2015/7/22 0022 16:05
  Copyright(c) 北京四海富博计算机服务有限公司
--%>
<%--发起修改申请前 指定下一步操作人--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div id="pla">
    <form id="ph003_taskAssignee">
        <table class="border">
            <tr>
                <th style="width:15%"><label>查找处理人：</label></th>
                <td>
                    <div style="position: relative;">
                        <input type="text" class="pull-left autocomplete" width="200px"
                               placeholder="输入简拼查询处理人"
                               atOption="ph00301_task.getOptionren"
                               atUrl="ph00301_task.getUrlren"/>
                    </div>
                </td>
            </tr>
            <tr style="height: 30px">
                <th style="width:15%"><label>下一步处理人：</label></th>
                <input type="hidden" name="toStaffId"/>
                <input type="hidden" name="toStaffCd" class="js_pri"/>
                <td class="js_toStaff_tr">
                    <span class="hidden">
                        <span class="js_toStaff_name"></span>
                        <input type="hidden" name="currentStaff" value=""/>
                        <label onclick="ph00301_task.rmToStaff(this);">X</label>
                    </span>
                </td>
            </tr>
            <%--<jsp:include page="/eland/ph/ph007/ph007-nextAssignee.gv">--%>
            <%--<jsp:param name="tdNum" value="4"/>--%>
            <%--</jsp:include>--%>
            <tr style="height: 30px">
                <th style="width:15%"><label>备注信息：</label></th>
                <td>
                <textarea rows="12" name="taskContent" placeholder="输入发送申请内容..."
                          style="width:99%; border: 0"></textarea>
                </td>
            </tr>
            <tr style="height: 30px; text-align: center">
                <td colspan="2">
                    <span class="btn btn-primary" onclick="ph00301_task.cfmSendTask(this)">确认发起申请</span>
                </td>
            </tr>
        </table>
    </form>
</div>
<oframe:script src="${pageContext.request.contextPath}/eland/ph/ph003/js/ph00301_task.js" type="text/javascript"/>

