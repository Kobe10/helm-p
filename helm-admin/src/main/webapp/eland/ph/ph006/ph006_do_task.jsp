<%--自定义任务--处理任务--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<form id="ph006TFrm">
    <table width="100%" class="border">
        <tr>
            <th width="15%"><label>处理意见：</label></th>
            <td><textarea rows="10" style="margin: 0px;" placeholder="请填写任务处理意见或提醒事项。"
                          name="comment"></textarea></td>
        </tr>
        <tr>
            <th><label>下一步处理人：</label></th>
            <td>
                <input type="hidden" name="taskId" value="${taskId}"/>
                <div style="position: relative;">
                    <input type="hidden" name="taskAssignee" class="js_pri" value=""/>
                    <input type="text" name="targetPerTemp" class="pull-left" width="100px"
                           placeholder="点击右侧按钮指定下一步处理人，不选则结束任务。"/>
                    <a title="选择" onclick="$.fn.sltStaff(this,{offsetX: 3});" class="btnLook">选择</a>
                    <%--<input type="text" name="targetPerTemp" atoption="ph006.getOption" aturl="ph006.getUrl" class="autocomplete required acInput textInput valid" autocomplete="off">--%>
                </div>
            </td>
        </tr>
        <tr style="text-align: center;">
            <td colspan="2">
            <span class="btn-primary marl10"
                  style="padding: 5px 10px 5px 10px; line-height: 34px; cursor:pointer;border-radius: 5px;"
                  onclick="wf001.doTask('ph006TFrm', {})">提交</span>
            <span class="btn-primary marl10"
                  style="padding: 5px 10px 5px 10px; line-height: 34px; cursor:pointer;border-radius: 5px;"
                  onclick="window.close()">取消</span>
            </td>
        </tr>
    </table>
</form>