<%--自定义任务--处理任务--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<form id="ph00709Frm">
    <table width="100%" class="border" style="text-align: center">
        <tr>
            <td width="10%"><b>序号</b></td>
            <td><b>产权人</b></td>
            <td><b>房屋性质</b></td>
            <td><b>建筑面积</b></td>
            <td><b>操作</b></td>
        </tr>
        <c:forEach items="${hsInfoList}" var="item" varStatus="varStatus">
            <tr>
                <td>${varStatus.index+1}</td>
                <td>${item.Row.hsOwnerPersons}</td>
                <td><oframe:name prjCd="${param.prjCd}" itemCd="HS_OWNER_TYPE" value="${item.Row.hsOwnerType}"/></td>
                <td>${item.Row.hsBuildSize}</td>

                <c:set var="hsId" value="${item.Row.hsId}${''}"/>
                <td><span class="btn" style="color: #0000ff" onclick="ph003Op.chooseHouse('${hsId}', '','edit');">外迁选房</span></td>
            </tr>
        </c:forEach>
        <tr>
            <th width="10%"><label>备注说明：</label></th>
            <td colspan="4" style="text-align: left"><textarea rows="8" style="margin: 0px;" name="comment"></textarea>
            </td>
        </tr>
        <input type="hidden" name="taskId" value="${taskId}"/>
        <c:if test="${PROC_INST_INFO.ProcInsInfo.Variables.buildType == '1' || PROC_INST_INFO.ProcInsInfo.Variables.buildType == '3'}">
            <tr>
                <th><label>完成腾退手续处理人：</label></th>
                <td colspan="4" style="text-align: left">
                    <input type="hidden" name="assignee4" class="js_pri js_assignee" value=""/>
                    <input type="text" name="targetPerTemp4" atoption="ph00709.getOption" aturl="ph00709.getUrl"
                           class="autocomplete required acInput textInput valid js_targetPerTemp" autocomplete="off">
                </td>
            </tr>
        </c:if>
        <c:if test="${PROC_INST_INFO.ProcInsInfo.Variables.buildType == '2' || PROC_INST_INFO.ProcInsInfo.Variables.buildType == '3'}">
            <tr>
                <th><label>指定网签、核税处理人：</label></th>
                <td colspan="4" style="text-align: left">
                    <input type="hidden" name="assignee5" class="js_pri js_assignee" value=""/>
                    <input type="text" name="targetPerTemp5" atoption="ph00709.getOption" aturl="ph00709.getUrl"
                           class="autocomplete required acInput textInput valid js_targetPerTemp" autocomplete="off">
                </td>
            </tr>
        </c:if>
        <tr style="text-align: center;">
            <td colspan="5">
            <span class="btn-primary marl10"
                  style="padding: 5px 10px 5px 10px; line-height: 34px; cursor:pointer;"
                  onclick="ph007.submitVerifyassignee('ph00709Frm', this, {})">提交</span>
            </td>
        </tr>
    </table>
</form>
<script>
    var ph00709 = {
        /**
         * 获取任务指派人
         */
        getUrl: function (obj) {
            return getGlobalPathRoot() + "oframe/common/auto/auto-data.gv?prjCd=" + getPrjCd() + "&subSvcName=sys002Data";
        },

        getOption: function (obj) {
            return {
                processData: function (result) {
                    var myData = [];
                    for (i = 0; i < result.length; i++) {
                        var dataRow = {};
                        dataRow.data = result[i];
                        dataRow.value = result[i].STAFF_NAME;
                        myData.push(dataRow);
                    }
                    return myData;
                },
                showResult: function (value, data) {
                    return data.STAFF_NAME + " ( " + data.STAFF_CODE + " ) ";
                },
                mustMatch: false,
                remoteDataType: "json",
                autoFill: false,
                delay: 300,
                minChars: 0,
                onItemSelect: function (obj) {
                    $("input.js_assignee", $(obj.source).closest("td")).val(obj.data.STAFF_CODE);
                    $("input.js_targetPerTemp", $(obj.source).closest("td")).val(obj.data.STAFF_NAME);
                },
                onNoMatch: function (obj) {
                    $("input.js_assignee", $(obj.source).closest("td")).val("");
                }
            }
        }
    }
</script>