<%--安置房源基础维护--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div>
    <div class="panelBar">
        <ul class="toolBar">
            <oframe:op prjCd="${param.prjCd}" template="li" cssClass="edit"
                       name="修改" rhtCd="edit_${newHs.NewHsInfo.hsClass}_new_hs_rht"
                       onClick="oh001.saveOHs()"/>
            <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
        </ul>
    </div>
    <div>
        <form id="oh00103Ohfrm" method="post" class="required-validate">
            <input type="hidden" name="newHsId" value="${newHs.NewHsInfo.newHsId}">
            <table class="border">
                <tr>
                    <th><label>房屋地址：</label></th>
                    <td colspan="3">
                        <input type="hidden" name="hsFl" value="${newHs.NewHsInfo.hsFl}"/>
                        <input type="hidden" name="hsSt" value="${newHs.NewHsInfo.hsSt}"/>
                        <input type="hidden" name="hsNo" value="${newHs.NewHsInfo.hsNo}"/>
                        <input type="hidden" name="hsAddr" value="${newHs.NewHsInfo.hsAddr}"/>
                        ${newHs.NewHsInfo.hsAddr}
                    </td>
                    <th><label>房源状态：</label></th>
                    <td><oframe:select prjCd="${param.prjCd}" itemCd="NEW_HS_STATUS" name="statusCd"
                                       value="${newHs.NewHsInfo.statusCd}" cssClass="textInput"/></td>
                </tr>
                <tr>
                    <th><label>房屋区域：</label></th>
                    <td>
                        <div style="position:relative;">
                            <%--区域信息--%>
                            <input type="hidden" name="ttRegId" value="${newHs.NewHsInfo.ttRegId}">
                            <input type="text" class="readonly pull-left required"
                                   readonly="readonly" name="regName"
                                   value='<oframe:entity  prjCd="${param.prjCd}" entityName="RegInfo" property="regName"
                                                          value="${newHs.NewHsInfo.ttRegId}"/>'>
                        </div>
                    </td>
                    <th><label>区域门牌：</label></th>
                    <td>
                        <input type="hidden" name="hsUn"
                               value="${newHs.NewHsInfo.hsUn}"/>
                        <input type="hidden" name="hsNm"
                               value="${newHs.NewHsInfo.hsNm}"/>
                        ${newHs.NewHsInfo.hsUn}单元${newHs.NewHsInfo.hsNm}门
                    </td>
                    <th><label>管理组织：</label></th>
                    <td>
                        <div style="position:relative;">
                            <input type="hidden" name="ttOrgId" value="${newHs.NewHsInfo.ttOrgId}"/>
                            <input type="text" name="prjOrgName" class="pull-left"
                                   readonly="readonly"
                                   value='<oframe:org prjCd="${param.prjCd}" orgId="${newHs.NewHsInfo.ttOrgId}"/>'/>
                            <a title="选择" onclick="$.fn.sltOrg(this);" class="btnLook">选择</a>
                        </div>
                    </td>
                </tr>
                <tr>
                    <th><label>户型编号：</label></th>
                    <td>
                        <input type="hidden" name="hsHxId" value="${newHs.NewHsInfo.hsHxId}">
                        <input type="text" name="hsHxName" value="${newHs.NewHsInfo.hsHxName}"
                               atUrl="oh001.getHxUrl"
                               atOption="oh001.getHxOpt"
                               class="autocomplete">
                    </td>
                    <th><label>居室结构：</label></th>
                    <td><oframe:select prjCd="${param.prjCd}" itemCd="HS_ROOM_TYPE" cssClass="textInput" name="hsTp"
                                       value="${newHs.NewHsInfo.hsTp}"/></td>
                    <th><label>房屋朝向：</label></th>
                    <td><oframe:select prjCd="${param.prjCd}" itemCd="HS_ROOM_DT" cssClass="textInput" name="hsDt"
                                       value="${newHs.NewHsInfo.hsDt}"/></td>
                </tr>
                <tr>
                    <th><label>预测建面：</label></th>
                    <td><input type="text" onchange="oh001.calRealPrice();" name="preBldSize"
                               value="${newHs.NewHsInfo.preBldSize}"></td>
                    <th><label>实测建面：</label></th>
                    <td><input type="text" onchange="oh001.calRealPrice();" class="number" name="hsBldSize"
                               value="${newHs.NewHsInfo.hsBldSize}"></td>
                    <th><label>套内面积：</label></th>
                    <td><input type="text" onchange="oh001.calRealPrice();" class="number" name="hsUseSize"
                               value="${newHs.NewHsInfo.hsUseSize}"></td>
                </tr>
                <tr>
                    <th><label>建面单价：</label></th>
                    <td><input type="text" class="number" name="hsUnPrice" value="${newHs.NewHsInfo.hsUnPrice}"></td>
                    <th><label>房屋总价：</label></th>
                    <td colspan="3">
                        <input type="text" class="number readonly" readonly
                               name="hsSalePrice" value="${newHs.NewHsInfo.hsSalePrice}">
                    </td>
                </tr>
                <tr>
                    <th><label>调拨时间：</label></th>
                    <td><input type="text" class="date" name="aplyHsDate" value="${newHs.NewHsInfo.aplyHsDate}"></td>
                    <th><label>交房时间：</label></th>
                    <td><input type="text" class="date" name="handHsDate" value="${newHs.NewHsInfo.handHsDate}"></td>
                    <th><label>签约时间：</label></th>
                    <td><input type="text" class="date" name="buyHsCtDate" value="${newHs.NewHsInfo.buyHsCtDate}"></td>
                </tr>
                <tr>
                    <th><label>物业单价：</label></th>
                    <td><input type="text" class="number" name="wyUnPrice" value="${newHs.NewHsInfo.wyUnPrice}"></td>
                    <th><label>取暖单价：</label></th>
                    <td><input type="text" class="number" name="gnUnPrice" value="${newHs.NewHsInfo.gnUnPrice}"></td>
                    <th><label>入住时间：</label></th>
                    <td><input type="text" class="date" name="rzTime" value="${newHs.NewHsInfo.rzTime}"></td>
                </tr>
                <tr>
                    <th><label>承担物业费：</label></th>
                    <td><input type="text" class="number" name="cmpWyCost" value="${newHs.NewHsInfo.cmpWyCost}"></td>
                    <th><label>承担取暖费：</label></th>
                    <td><input type="text" class="number" name="cmpQnCost" value="${newHs.NewHsInfo.cmpQnCost}"></td>
                    <th></th>
                    <td></td>
                </tr>
                <tr>
                    <th><label>备注说明：</label></th>
                    <td colspan="5"><textarea name="hsNode" rows="5">${newHs.NewHsInfo.hsNode}</textarea></td>
                </tr>
                <tr>
                    <td colspan="6">
                        <div style=" padding: 5px 20px;font-size: 16px; background-color: #f7f7f7">购房信息</div>
                    </td>
                </tr>
                <tr>
                    <th><label>成交单价：</label></th>
                    <td>
                        <div style="position:relative;">${newHs.NewHsInfo.dealUnPrice}</div>
                    </td>
                    <th><label>成交金额：</label></th>
                    <td>
                        <div style="position:relative;">${newHs.NewHsInfo.dealSalePrice}</div>
                    </td>
                    <th><label>购房人编号：</label></th>
                    <td>
                        <div style="position:relative;">${newHs.NewHsInfo.buyPersonId}</div>
                    </td>
                </tr>
                <tr>
                    <th><label>购房人姓名：</label></th>
                    <td>${newHs.NewHsInfo.buyPersonName}
                        <c:if test="${newHs.NewHsInfo.oldHsId != '' && newHs.NewHsInfo.oldHsId != null}">
                            <span class="marr10 link" style="float: right;"
                                  onclick="oh001.openHouseView('${newHs.NewHsInfo.oldHsId}');setTimeout('$.pdialog.closeCurrent()', 500);">查看购房详情</span>
                        </c:if>
                    </td>
                    <th><label>购房人证件号码：</label></th>
                    <td>${newHs.NewHsInfo.buyPersonCerty}</td>
                    <th><label>购房人手机号：</label></th>
                    <td>${newHs.NewHsInfo.buyPersonTel}</td>
                </tr>
            </table>
        </form>
    </div>
</div>