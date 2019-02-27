<%-- 领取存折信息 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<c:set var="statusEditAble" value=""/>
<c:set var="uploadDisabled" value="true"/>
<div style="position: relative;">
    <div class="panelBar">
        <ul class="toolBar">
            <c:set var="fmStatus" value="${hsInfo.HouseInfo.HsFmInfo.fmStatus}"/>
            <c:if test="${fmStatus == '13' || fmStatus == '14'}">
                <li onclick="ph014.saveLk('${hsId}','${infoKey}');">
                    <a class="save" href="javascript:void(0)"><span>保存</span></a>
                </li>
            </c:if>
            <%--<li onclick="ph014.viewHouse(${hsId});">--%>
                <%--<a class="new-area" href="javascript:void(0)"><span>居民详情</span></a>--%>
            <%--</li>--%>
            <%--<li onclick="ph014.singleQuery(${hsId},'next');" style="float: right">--%>
                <%--<a class="export" href="javascript:void(0)"><span>下一户</span></a>--%>
            <%--</li>--%>
            <%--<li onclick="ph014.singleQuery(${hsId},'last');" style="float: right">--%>
                <%--<a class="import" href="javascript:void(0)"><span>上一户</span></a>--%>
            <%--</li>--%>
        </ul>
    </div>
    <div class="panlContainer" layoutH="57" style="border: 1px solid #e9e9e9;position: relative">
        <form id="ph014Form">
            <input name="hsId" type="hidden" value="${hsId}"/>
            <input name="hsFmId" type="hidden" value="${infoKey}"/>
            <table class="border">
                <tr>
                    <th width="12%">档案编号：</th>
                    <td width="20%">
                        <label name="hsCd">${hsInfo.HouseInfo.hsCd}</label>
                    </td>
                    <th width="12%">被安置人：</th>
                    <td width="20%">
                        <label name="hsOwnerPersons">${hsInfo.HouseInfo.hsOwnerPersons}</label>
                    </td>
                    <th width="12%">房屋地址：</th>
                    <td>
                        <label name="hsFullAddr">${hsInfo.HouseInfo.hsFullAddr}</label>
                    </td>
                </tr>

                <tr>
                    <th>领款人：</th>
                    <td>
                        ${hsInfo.HouseInfo.HsFmInfo.fmPersonName}
                    </td>
                    <th>领款金额：</th>
                    <td>
                        ${hsInfo.HouseInfo.HsFmInfo.fmMoney}
                    </td>
                    <th>制折完成时间：</th>
                    <td>
                        <oframe:date value="${hsInfo.HouseInfo.HsFmInfo.fmMakedDate}" format="yyyy-MM-dd"/>
                    </td>

                </tr>
                <tr>
                    <th>领折时间：</th>
                    <td>
                        <input name="fmGetDate" type="text" class="date required"
                               value='<oframe:date value="${hsInfo.HouseInfo.HsFmInfo.fmGetDate}" format="yyyy-MM-dd"/>'/>
                    </td>
                    <th>状态时间：</th>
                    <td>
                        <oframe:date value="${hsInfo.HouseInfo.HsFmInfo.fmGetOpDate}" format="yyyy-MM-dd HH:mm:ss"/>
                    </td>
                    <th>存折状态：</th>
                    <td>
                        <input type="hidden" name="fmStatus" value="14"/>
                        <oframe:name itemCd="FM_STATUS" value="${hsInfo.HouseInfo.HsFmInfo.fmStatus}"/>
                    </td>
                </tr>
                <tr>
                    <th>付款计划：</th>
                    <td><oframe:name collection="${payListInfo}" value="${hsInfo.HouseInfo.HsFmInfo.pId}"/></td>
                </tr>
                <tr>
                    <th>备注说明：</th>
                    <td colspan="5">
                    <textarea rows="8" name="fmGetOpDesc"
                              style="width: 80%;">${hsInfo.HouseInfo.HsFmInfo.fmGetOpDesc}</textarea>
                    </td>
                </tr>
                <tr>
                    <th class="subTitle" colspan="6">
                        <h1 style="float: left;">
                            <span>领折材料</span>
                            <span class="link marl10 js_upload" docTypeName="领折材料" relType="100"
                                  editAble="${uploadDisabled}"
                                  onclick="ph014.showDoc(this,'${hsId}')">
                                <label style="cursor:pointer;">附件上传</label>
                                <input type="hidden" name="docIds" value='${hsInfo.HouseInfo.HsFmInfo.fmGetOpDocIds}'>
                            </span>
                        </h1>
                    </th>
                </tr>
            </table>
        </form>
        <div layoutH="340" class="album-list" id="ph014docsDiv" style="border:1px solid #99bbe8">
            <jsp:include page="/eland/ph/ph014/ph014-initDocs.gv">
                <jsp:param name="prjCd" value="${param.prjCd}"/>
                <jsp:param name="hsId" value="${hsId}"/>
                <jsp:param name="docTypeName" value="领折材料"/>
            </jsp:include>
        </div>
    </div>
</div>