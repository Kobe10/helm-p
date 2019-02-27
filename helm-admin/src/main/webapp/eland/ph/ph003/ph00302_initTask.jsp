<%--待办任务信息面板--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>

<table width="100%" class="border">
    <tr>
        <th><label>任务名称：</label></th>
        <td>
            <input type="hidden" name="hsId" value="${hsId}"/>
            <input type="text" class="js_taskName" name="taskName" value=""/>
        </td>
    </tr>
    <tr>
        <th><label>详情描述：</label></th>
        <td><textarea rows="2" style="width: 550px; height: 45px;margin: 0px;" name="taskDetails"
                      class="js_taskDetails"></textarea></td>
    </tr>
    <tr>
        <th><label>指派给：</label></th>
        <td>
            <input type="hidden" name="targetPer" value=""/>
            <input type="text" name="targetPerTemp" atoption="ph00302.getOption" aturl="ph00302.getUrl"
                   class="autocomplete required acInput textInput valid js_targetPer" autocomplete="off">
        </td>
    </tr>
    <tr>
        <th><label>截至时间：</label></th>
        <td><input type="text" name="taskEndDate" class="date js_taskEndDate" datefmt="yyyy-MM-dd HH:mm:ss" value=""/>
        </td>
    </tr>
    <tr style="text-align: center;">
        <td colspan="2">
            <span class="btn-primary marl10"
                  style="padding: 5px 10px 5px 10px; line-height: 34px; cursor:pointer;"
                  onclick="ph00302.cfmSendTask('${hsId}')">提交</span>
            <span class="btn-primary marl10"
                  style="padding: 5px 10px 5px 10px; line-height: 34px; cursor:pointer;"
                  onclick="ph00302.cancelTask(this)">取消</span>
        </td>
    </tr>
</table>
<oframe:script type="text/javascript" src="${pageContext.request.contextPath}/eland/ph/ph003/js/ph00302.js"/>