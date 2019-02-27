<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%--<oframe:script src="${pageContext.request.contextPath}/eland/fm/fm003/js/fm003.js" type="text/javascript"/>--%>
<style>
    #fm003RegTable tr td, #fm003RegTable tr th {
        text-align: center;
    }
</style>
<div class="panelBar">
    <ul class="toolBar">
        <c:if test="${isCanEdit == 'true'}">
            <li><a class="save" onclick="fm003.savePayInfo()"><span>保存信息</span></a></li>
            <li><a class="edit" onclick="fm003.commitApply()"><span>申请制折</span></a></li>
        </c:if>
    </ul>
</div>
<div style="position: relative">
    <form id="fm00301">
        <input type="hidden" name="hsId" value="${hsId}"/>
        <input type="hidden" name="hsCtId" value="${hsCtId}"/>
        <input type="hidden" name="pId" value="${pId}"/>
        <input type="hidden" name="totalMoney" value="${totalMoney}"/>
        <table class="border" width=" 100%" id="matchTable">
            <tr>
                <th width="12%">档案编号：</th>
                <td width="21%">${hsCd}</td>
                <th width="12%">被安置人：</th>
                <td width="21%">${hsOwnerPersons}</td>
                <th width="12%">房屋地址：</th>
                <td width="21%">${hsFullAddr}</td>
            </tr>
            <tr>
                <th>安置方式：</th>
                <td>${ctType}</td>
                <th>付款计划：</th>
                <td>
                    <oframe:select collection="${payListInfo}" name="pId" onChange="fm003.changePlan(this)"
                                   value="${pId}"/>
                    <span class="link marl5" onClick="fm003.lockPlan()">锁定</span>
                </td>
                <th>应付款总金额：</th>
                <td>
                    <span style="color: red;font-size: 20px;">${errorInfo}</span>
                    ${gshTotalMoney}
                </td>
            </tr>
            <tr>
                <th>备注说明：</th>
                <td colspan="5">
                    <textarea name="fmDesc" cols="80" rows="5">${fmDesc}</textarea>
                </td>
            </tr>
            <tr>
                <th class="subTitle" colspan="6"><h1 style="float: left;">关联协议签署情况</h1></th>
            </tr>
            <tr>
                <td colspan="6">
                    <table class="table" width="100%">
                        <thead>
                        <tr>
                            <th>协议名称</th>
                            <th>协议状态</th>
                            <th>签约时间</th>
                            <th>协议总金额</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${ctInfo}" var="item">
                            <tr>
                                <td>${item.CtInfo.ctName}</td>
                                <td><oframe:name prjCd="${param.prjCd}" itemCd="CONTRACT_STATUS"
                                                 value="${item.CtInfo.ctStatus}"/></td>
                                <td>${item.CtInfo.ctDate}</td>
                                <td>
                                        ${item.CtInfo.ctMoney}
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </td>
            </tr>
            <tr>
                <th class="subTitle" colspan="6"><h1 style="float: left;">领款人登记</h1>
                    <span style="float: left;" class="link marl5 js_doc_info mart5" docTypeName="多人领款证明材料" relType="100"
                          onclick="fm003.showDoc(this,'${hsId}')">
                                    <label style="cursor:pointer;">[多人领款证明材料管理]</label>
                                    <input type="hidden" name="docId" value='${fmPersonDocIds}'>
                    </span>
                </th>
            </tr>
            <tr>
                <td colspan="6">
                    <table class="border" width="100%" id="fm003RegTable">
                        <thead>
                        <tr style="background-color: #f0f0f0">
                            <th width="15%">领款人姓名</th>
                            <th width="15%">领款金额</th>
                            <th width="15%">身份证号</th>
                            <th width="15%">联系电话</th>
                            <th width="15%">领款状态</th>
                            <th width="15%">操作 <span class="link mar10 btnAdd"
                                                     onclick="fm003.addRow('fm003RegTable',this)">[新增]</span>
                            </th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr class="hidden">
                            <td>
                                <input type="hidden" name="hsFmId" value=""/>
                                <input type="text" atOption="fm003.getOption" atUrl="fm003.getUrl" name="fmPersonName"
                                       class="js_ps_name js_ps autocomplete ctRequired">
                            </td>
                            <td>
                                <input type="text" name="fmMoney"/>
                            </td>
                            <td>
                                <input type="text" name="fmPersonCertyNum" class="js_ps_certy js_ps"/>
                            </td>
                            <td>
                                <input type="text" name="fmPersonTel" class="js_ps_tel js_ps"/>
                            </td>
                            <td><input type="hidden" name="fmStatus" value="7"/>
                                <oframe:name itemCd="FM_STATUS" value="7" prjCd="${param.prjCd}"/>
                            </td>
                            <td>
                                <span class="link marl10" onclick="fm003.viewWld('','${pFormCd}')">五联单</span>
                                <span class="link marl10" onclick="table.deleteRow(this)">删除</span>
                            </td>
                        </tr>
                        <c:if test="${flag == 'false'}">
                            <tr>
                                <td><input type="hidden" class="tempFlag" name="hsFmId" value=""/>
                                    <input type="text" atOption="fm003.getOption" atUrl="fm003.getUrl"
                                           class="js_ps_name js_ps autocomplete ctRequired required" name="fmPersonName"
                                           value="${hsOwnerPersons}">
                                </td>
                                <td>
                                    <input type="text" name="fmMoney" class="tempMoney" value="${totalMoney}"
                                           class="required"/>
                                </td>
                                <td>
                                    <input type="text" name="fmPersonCertyNum" class="js_ps_certy js_ps required"
                                           value="${personCertyNum}"/>
                                </td>
                                <td><input type="hidden" name="fmStatus" value="7"/>
                                    <input type="text" name="fmPersonTel" class="js_ps_tel js_ps required"
                                           value="${personTelphone}"/>
                                </td>
                                <td>
                                    <oframe:name itemCd="FM_STATUS" value="7" prjCd="${param.prjCd}"/>
                                </td>
                                <td>
                                    <span class="link marl10" onclick="fm003.viewWld('','${pFormCd}')">五联单</span>
                                    <span class="link marl10" onclick="table.deleteRow(this)">删除</span>
                                </td>
                            </tr>
                        </c:if>
                        <c:forEach items="${HsFmInfo}" var="item">
                            <tr>
                                <td>
                                    <input type="hidden" class="tempFlag" name="hsFmId"
                                           value="${item.HsFmInfo.hsFmId}"/>
                                    <input type="text" atOption="fm003.getOption" atUrl="fm003.getUrl"
                                           class="js_ps_name js_ps autocomplete ctRequired required" name="fmPersonName"
                                           value="${item.HsFmInfo.fmPersonName}">
                                </td>
                                <td>
                                    <input type="text" name="fmMoney" class="tempMoney" value="${item.HsFmInfo.fmMoney}"
                                           class="required"/>
                                </td>
                                <td>
                                    <input type="text" name="fmPersonCertyNum" class="js_ps_certy js_ps required"
                                           value="${item.HsFmInfo.fmPersonCertyNum}"/>
                                </td>
                                <td>
                                    <input type="text" name="fmPersonTel" class="js_ps_tel js_ps required"
                                           value="${item.HsFmInfo.fmPersonTel}"/>
                                </td>
                                <td>
                                    <input type="hidden" name="fmStatus" value="${item.HsFmInfo.fmStatus}"/>
                                    <oframe:name itemCd="FM_STATUS" value="${item.HsFmInfo.fmStatus}"
                                                 prjCd="${param.prjCd}"/>
                                </td>
                                <td>
                                    <oframe:op prjCd="${param.prjCd}" template="span" cssClass="link marl10"
                                               name="下载五联单" rhtCd="dwn_hs_rpt_rht"
                                               onClick="fm003.downWld('${item.HsFmInfo.hsFmId}','${pFormCd}');"/>
                                    <oframe:op prjCd="${param.prjCd}" template="span" cssClass="link marl10" name="打印五联单" rhtCd="prt_hs_rpt_rht"
                                               onClick="fm003.viewWld('${item.HsFmInfo.hsFmId}','${pFormCd}');"/>
                                    <%--<span class="link marl10" onclick="fm003.downWld('${item.HsFmInfo.hsFmId}','${pFormCd}')">下载五联单</span>--%>
                                    <%--<span class="link marl10" onclick="fm003.viewWld('${item.HsFmInfo.hsFmId}','${pFormCd}')">打印五联单</span>--%>
                                    <span class="link marl10" onclick="table.deleteRow(this)">删除</span>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </td>
            </tr>
        </table>
    </form>
</div>
