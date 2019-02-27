<%--自定义任务--发起任务页面--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<form id="ph006Frm">
    <table width="100%" class="border">
        <tr>
            <th width="15%"><label>任务名称：</label></th>
            <td>
                <input type="text" name="taskName" class="js_pri" value=""/>
            </td>
        </tr>
        <tr>
            <th><label>详情描述：</label></th>
            <td><textarea rows="2" style="height: 45px;margin: 0px;" class="js_pri" name="description"></textarea>
            </td>
        </tr>
        <tr>
            <th><label>指派给：</label></th>
            <td>
                <div style="position: relative;">
                    <input type="hidden" name="taskAssignee" class="js_pri" value=""/>
                    <input type="text" name="targetPerTemp" class="pull-left" width="100px" placeholder="点击右侧按钮指定下一步处理人"/>
                    <a title="选择" onclick="$.fn.sltStaff(this,{offsetX: 3});" class="btnLook">选择</a>
                    <%--<input type="text" name="targetPerTemp" atoption="ph006.getOption" aturl="ph006.getUrl" class="autocomplete required acInput textInput valid" autocomplete="off">--%>
                </div>
            </td>
        </tr>
        <tr>
            <th><label>截至时间：</label></th>
            <td><input type="text" name="dueTime" class="date js_pri" datefmt="yyyy-MM-dd HH:mm:ss"
                       value=""/>
            </td>
        </tr>
        <tr style="text-align: center;">
            <td colspan="2">
            <span class="btn-primary marl10"
                  style="padding: 5px 10px 5px 10px; line-height: 34px; cursor:pointer;border-radius: 5px;"
                  onclick="wf001.doStart('ph006Frm', {})">提交</span>
            <span class="btn-primary marl10"
                  style="padding: 5px 10px 5px 10px; line-height: 34px; cursor:pointer;border-radius: 5px;"
                  onclick="window.close()">取消</span>
            </td>
        </tr>
    </table>
</form>
<oframe:script type="text/javascript" src="${pageContext.request.contextPath}/eland/ph/ph006/js/ph006.js"/>