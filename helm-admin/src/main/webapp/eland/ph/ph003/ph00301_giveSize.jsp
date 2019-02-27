<%--待办任务信息面板--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="pagecontent">
    <%--面板内容--%>
    <div class="panel" style="padding: 0 5px 5px 0; margin-top: 5px; margin-left: 10px;margin-right: 10px;">
        <h1><span>赠送信息</span></h1>

        <div class="js_panel_context">
            <form id="ph00301GiveFrm">
                <table width="100%" class="border">
                    <tr>
                        <th width="15%"><label>房源区域：</label></th>
                        <td width="34%">
                            <input type="hidden" name="hsId" value="${hsId}"/>
                            <select name="gvReg" class="gvReg required" onchange="ph00301_03.changeChReg(this);">
                                <c:forEach items="${chooseRegMap}" var="item">
                                    <c:set var="chRegInfo" value="${chooseRegMap[item.key]}"/>
                                    <option cangivesize="${chRegInfo.HsRegInfo.sjmj}" value="${item.key}"><oframe:entity
                                            entityName="RegInfo" property="regName"
                                            prjCd="${prjCd}" value="${item.key}"/></option>
                                </c:forEach>
                            </select>
                        </td>
                        <th width="15%"><label>赠送面积：</label></th>
                        <td width="34%">
                            <input type="text" name="giveSize" class="required number" onfocus="ph00301_03.checkIn(this);"
                                   onchange="ph00301_03.checkSize(this);" value=""/></td>
                    </tr>
                    <tr>
                        <th><label>赠送方：</label></th>
                        <td>
                            <input type="hidden" name="hsCtAId" value="${hsCtId}"/>
                            ${ctInfo.HsCtInfo.ctPsNames}
                        </td>
                        <th><label>接收方：</label></th>
                        <td>
                            <input type="hidden" name="hsCtBId" value=""/>
                            <input type="text" name="hsCtBIdName" atoption="ph00301_03.getOption"
                                   aturl="ph00301_03.getUrl" placeholder="点击选择接收人"
                                   class="autocomplete required acInput textInput valid" autocomplete="off">
                        </td>
                    </tr>
                    <tr style="text-align: center;">
                        <td colspan="4">
                    <span class="btn-primary"
                          style="padding: 5px 10px 5px 10px; line-height: 34px; cursor:pointer;"
                          onclick="ph00301_03.saveGive('${hsId}')">保存赠送</span>
                        </td>
                    </tr>
                </table>
            </form>
        </div>
    </div>
    <div class="panel" style="padding: 0 5px 5px 0; margin-top: 5px; margin-left: 10px;margin-right: 10px;">
        <h1><span>赠送日志</span></h1>

        <div class="js_panel_context">
            <table width="100%" class="border" style="text-align: center">
                <tr>
                    <td><b>序号</b></td>
                    <td><b>房源区域</b></td>
                    <td><b>赠送方</b></td>
                    <td><b>接收方</b></td>
                    <td><b>赠送面积</b></td>
                    <td><b>赠送时间</b></td>
                    <td><b>操作</b></td>
                </tr>
                <c:forEach items="${aGiveSize}" var="item" varStatus="varStatus">
                    <tr>
                        <td>${varStatus.index+1}
                            <input type="hidden" name="hsCtGiveId" value="${item.HsCtGive.hsCtGiveId}"/>
                        </td>
                        <td><oframe:entity entityName="RegInfo" property="regName" prjCd="${prjCd}"
                                           value="${item.HsCtGive.regId}"/></td>
                        <td><oframe:entity prjCd="${param.prjCd}" entityName="HsCtInfo" property="ctPsNames"
                                           value="${item.HsCtGive.hsCtAId}"/></td>
                        <td><oframe:entity prjCd="${param.prjCd}" entityName="HsCtInfo" property="ctPsNames"
                                           value="${item.HsCtGive.hsCtBId}"/></td>
                        <td>${item.HsCtGive.ctSize}</td>
                        <td>${item.HsCtGive.giveStatusDate}</td>
                        <td><span style="cursor: pointer; color: blue;" onclick="ph00301_03.cancelGive(this);">取消</span>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </div>
    </div>
</div>
<oframe:script type="text/javascript" src="${pageContext.request.contextPath}/eland/ph/ph003/js/ph00301_03.js"/>