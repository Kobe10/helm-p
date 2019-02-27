<%--自定义任务--处理任务--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<form id="ph00704Frm">
    <table width="100%" class="border" style="text-align: center">
        <tr>
            <td width="10%"><b>序号</b></td>
            <td><b>产权人</b></td>
            <td><b>房屋性质</b></td>
            <td><b>建筑面积</b></td>
            <td><b>签约状态</b></td>
            <td><b>操作</b></td>
        </tr>
        <c:forEach items="${hsInfoList}" var="item" varStatus="varStatus">
            <tr>
                <td>${varStatus.index+1}</td>
                <td>${item.Row.hsOwnerPersons}</td>
                <td><oframe:name prjCd="${param.prjCd}" itemCd="HS_OWNER_TYPE" value="${item.Row.hsOwnerType}"/></td>
                <td>${item.Row.hsBuildSize}</td>

                    <%--hsId方便下面使用，下面很多地方用到--%>
                <c:set var="hsId" value="${item.Row.hsId}${''}"/>
                <td class="js_result"><oframe:name prjCd="${param.prjCd}" itemCd="CONTRACT_STATUS"
                                                   value="${hsCtMap[hsId].Row.ctStatus}"/></td>

                <c:set var="cfmhidden" value=""/>
                <c:set var="cancelhidden" value="hidden"/>
                <c:choose>
                    <c:when test="${item.Row.hsOwnerType == '0'}">
                        <%--私房--%>
                        <td>
                            <span class="btn" style="color: #0000ff;" onclick="ph007.generateDoc('${hsId}')">生成协议</span>
                            <c:if test="${hsCtMap[hsId].Row.ctStatus == '1'}">
                                <c:set var="cfmhidden" value=""/>
                                <c:set var="cancelhidden" value="hidden"/>
                            </c:if>
                            <c:if test="${hsCtMap[hsId].Row.ctStatus == '2'}">
                                <c:set var="cfmhidden" value="hidden"/>
                                <c:set var="cancelhidden" value=""/>
                            </c:if>
                            <span class="btn js_cfmContrant ${cfmhidden}" style="color: #0000ff;"
                                  onclick="ph007.cfmContrant('${hsCtMap[hsId].Row.hsCtId}', this);">确认签约</span>
                            <span class="btn js_cancelContrant ${cancelhidden}" style="color: #0000ff;"
                                  onclick="ph007.cancelContrant('${hsCtMap[hsId].Row.hsCtId}', this);">取消签约</span>

                            <span class="link marr5 js_doc_info" docTypeName="居民签字协议" relType="100"
                                  onclick="ph007.showDoc(this,'${hsId}')">
                                <label style="cursor:pointer;">上传居民签字协议</label>
                                <input type="hidden" name="docIds" value='${hiTaskInfo.HiTaskInfo.TskBidVars.docIds}'>
                            </span>
                        </td>
                    </c:when>
                    <c:otherwise>
                        <%--公房--%>
                        <td>
                            <span class="btn" style="color: #0000ff;" onclick="ph007.generateDoc('${hsId}')">生成协议</span>
                            <c:if test="${hsCtMap[hsId].Row.ctStatus == '1'}">
                                <c:set var="cfmhidden" value=""/>
                                <c:set var="cancelhidden" value="hidden"/>
                            </c:if>
                            <c:if test="${hsCtMap[hsId].Row.ctStatus == '2'}">
                                <c:set var="cfmhidden" value="hidden"/>
                                <c:set var="cancelhidden" value=""/>
                            </c:if>
                            <span class="btn js_cfmContrant ${cfmhidden}" style="color: #0000ff;"
                                  onclick="ph007.cfmContrant('${hsCtMap[hsId].Row.hsCtId}', this);">确认签约</span>
                            <span class="btn js_cancelContrant ${cancelhidden}" style="color: #0000ff;"
                                  onclick="ph007.cancelContrant('${hsCtMap[hsId].Row.hsCtId}', this);">取消签约</span>
                        </td>
                    </c:otherwise>
                </c:choose>

            </tr>
        </c:forEach>
        <tr>
            <th><label>备注说明：</label></th>
            <td colspan="6" style="text-align: left"><textarea rows="5" style="margin: 0px;" name="comment"></textarea>
            </td>
        </tr>
        <tr>
            <input type="hidden" name="taskId" value="${taskId}"/>
            <th><label>购房资格审核处理人：</label></th>
            <td colspan="6" style="text-align: left">
                <input type="hidden" name="assignee1" class="js_pri js_assignee" value=""/>
                <input type="text" name="targetPerTemp1" atoption="ph00704.getOption" aturl="ph00704.getUrl"
                       class="autocomplete required acInput textInput valid js_targetPerTemp" autocomplete="off">
            </td>
        </tr>
        <tr>
            <th><label>OA资料准备处理人：</label></th>
            <td colspan="6" style="text-align: left">
                <input type="hidden" name="assignee2" class="js_pri js_assignee" value=""/>
                <input type="text" name="targetPerTemp2" atoption="ph00704.getOption" aturl="ph00704.getUrl"
                       class="autocomplete required acInput textInput valid js_targetPerTemp" autocomplete="off">
            </td>
        </tr>
        <c:if test="${PROC_INST_INFO.ProcInsInfo.Variables.buildType == '2' || PROC_INST_INFO.ProcInsInfo.Variables.buildType == '3'}">
            <tr>
                <th><label>私房房屋核验处理人：</label></th>
                <td colspan="6" style="text-align: left">
                    <input type="hidden" name="assignee3" class="js_pri js_assignee" value=""/>
                    <input type="text" name="targetPerTemp3" atoption="ph00704.getOption" aturl="ph00704.getUrl"
                           class="autocomplete required acInput textInput valid js_targetPerTemp" autocomplete="off">
                </td>
            </tr>
        </c:if>
        </tr>
        <tr style="text-align: center;">
            <td colspan="7">
            <span class="btn-primary marl10"
                  style="padding: 5px 10px 5px 10px; line-height: 34px; cursor:pointer;"
                  onclick="ph007.submitVerifyassignee('ph00704Frm', this, {});">提交</span>
            </td>
        </tr>
    </table>
</form>
<script>
    var ph00704 = {
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