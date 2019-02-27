<%--
  居民外迁选房操作  弹出页
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<div class="pagecontent">
    <div id="oh002FrmDiv">
        <div class="panelBar">
            <ul class="toolBar">
                <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
            </ul>
        </div>

        <c:if test="${allHsMap != null && allHsMap != ''}">
            <div layoutH="40">
                <ul class="menu" style="position: fixed; top: 90px; right: 50px;">
                    <c:set var="iterIdx" value="1"/>
                    <c:forEach items="${allHsMap}" var="item" varStatus="varIndex">
                        <c:set var="regId" value="${item.key}"/>
                        <c:set var="regMapInfo" value="${allHsMap[regId]}"/>
                        <c:forEach items="${regMapInfo}" var="regMapItem">
                            <c:forEach items="${regMapItem.value}" var="chooseHs">
                                <c:set var="buyPersonId" value="${chooseHs.chooseHs.buyPersonId}${''}"/>
                                <c:set var="buyPerson" value="${personsData[buyPersonId]}"/>
                                <li>
                                    <a href="#ct_${chooseHs.chooseHs.hsCtChooseId}">${iterIdx}：${buyPerson.Row.personName}</a>
                                </li>
                                <c:set var="iterIdx" value="${iterIdx+1}"/>
                            </c:forEach>
                        </c:forEach>
                    </c:forEach>
                </ul>
                <c:set var="iterIdx" value="0"/>
                <c:forEach items="${allHsMap}" var="item" varStatus="varIndex">
                    <c:set var="regId" value="${item.key}"/>
                    <c:set var="regMapInfo" value="${allHsMap[regId]}"/>
                    <c:forEach items="${regMapInfo}" var="regMapItem">
                        <c:forEach items="${regMapItem.value}" var="chooseHs">
                            <c:set var="buyPersonId" value="${chooseHs.chooseHs.buyPersonId}${''}"/>
                            <c:set var="buyPerson" value="${personsData[buyPersonId]}"/>

                            <div id="ct_${chooseHs.chooseHs.hsCtChooseId}" class="panel mart5 js_div"
                                 style="width: 60%; margin-left: 20%;margin-right: 20%">
                                <c:set var="iterIdx" value="${iterIdx+1}"/>
                                <h1><span>序号：${iterIdx}</span>
                                    <a href="javascript:void(0)" onclick="oh002.extendOrClosePanel(this)"
                                       class="collapsable"></a>
                                </h1>
                                    <%--面板内容--%>
                                <div class="js_panel_context">
                                        <%--隐藏项提交选房用--%>
                                    <input type="hidden" name="hsCtId" value="${ctInfo.HsCtInfo.hsCtId}"/>
                                    <input type="hidden" name="hsCtChooseId" value="${chooseHs.chooseHs.hsCtChooseId}"/>
                                    <input type="hidden" name="chooseStatus" value="${chooseHs.chooseHs.chooseStatus}"/>

                                    <table class="border everyChHsTable" style="border: 1px solid #000000" width="100%">
                                        <tr>
                                            <th width="20%"><label>购房人姓名：</label></th>
                                            <td colspan="5">${buyPerson.Row.personName}</td>
                                        </tr>
                                        <tr>
                                            <th><label>手机号：</label></th>
                                            <td colspan="5">${buyPerson.Row.personTelphone}</td>
                                        </tr>
                                        <tr>
                                            <th><label>身份证：</label></th>
                                            <td colspan="5">${buyPerson.Row.personCertyNum}</td>
                                        </tr>
                                        <tr>
                                            <th><label>现住址：</label></th>
                                            <td colspan="5">${buyPerson.Row.personLiveAddr}</td>
                                        </tr>
                                        <tr>
                                            <th><label>户口所在地：</label></th>
                                            <td colspan="5">${buyPerson.Row.familyAddr}</td>
                                        </tr>
                                        <tr>
                                            <th><label>工作单位：</label></th>
                                            <td colspan="5">${buyPerson.Row.personJobAddr}</td>
                                        </tr>

                                        <c:set var="gtJzPsIds" value="${chooseHs.chooseHs.gtJzPsIds}${''}"/>
                                        <c:set value="${ fn:split(gtJzPsIds, ',') }" var="gtJzPsIdArr"/>
                                        <c:set var="gtJzPsOwnerRels" value="${chooseHs.chooseHs.gtJzPsOwnerRels}${''}"/>
                                        <c:set value="${ fn:split(gtJzPsOwnerRels, ',') }" var="gtJzPsOwnerRelArr"/>
                                        <c:if test="${gtJzPsIds != ''}">
                                            <c:forEach items="${gtJzPsIdArr}" var="jzPsId" varStatus="varStatus">
                                                <c:set var="gtJzPerson" value="${personsData[jzPsId]}"/>
                                                <tr>
                                                    <th width="20%">共同居住人${varStatus.index+1}：</th>
                                                    <td>${gtJzPerson.Row.personName}</td>
                                                    <th width="10%">身份证号：</th>
                                                    <td>${gtJzPerson.Row.personCertyNum}</td>
                                                    <th width="10%">关系：</th>
                                                    <td>${gtJzPsOwnerRelArr[varStatus.index]}</td>
                                                </tr>
                                            </c:forEach>
                                        </c:if>
                                        <tr>
                                            <th><label>被拆迁人姓名：</label></th>
                                            <td colspan="5">
                                                <c:forEach items="${hsOwnersList}" var="owner1" varStatus="varStatus">
                                                    <c:set var="ownerPsId" value="${owner1.HsOwner.ownerPsId}${''}"/>
                                                    <c:set var="ownerPerson" value="${personsData[ownerPsId]}"/>
                                                    ${ownerPerson.Row.personName}
                                                    <c:if test="${!varStatus.last}">、</c:if>
                                                </c:forEach>
                                            </td>
                                        </tr>
                                        <tr>
                                            <th><label>被拆迁人身份证号：</label></th>
                                            <td colspan="5">
                                                <c:forEach items="${hsOwnersList}" var="owner2" varStatus="varStatus">
                                                    <c:set var="ownerPsId" value="${owner2.HsOwner.ownerPsId}${''}"/>
                                                    <c:set var="ownerPerson" value="${personsData[ownerPsId]}"/>
                                                    ${ownerPerson.Row.personCertyNum}
                                                    <c:if test="${!varStatus.last}">、</c:if>
                                                </c:forEach>
                                            </td>
                                        </tr>
                                        <tr>
                                            <th><label>拆迁协议号：</label></th>
                                            <td colspan="5">${baseBean.HouseInfo.hsCd}</td>
                                        </tr>
                                        <tr>
                                            <th><label>购房人与被拆迁人关系：</label></th>
                                            <td colspan="5">${chooseHs.chooseHs.buyPersonOwnerRel}</td>
                                        </tr>
                                        <tr>
                                            <th><label>备注说明：</label></th>
                                            <td colspan="5">${ownerInfo.HouseInfo.hsOwnerNote}</td>
                                        </tr>


                                        <c:set var="hsCtChooseId" value="${chooseHs.chooseHs.hsCtChooseId}"/>
                                        <c:set var="readonly" value=""/>
                                        <c:set var="disabled" value=""/>
                                        <c:set var="hidden" value="hidden"/>
                                        <c:if test="${chooseHs.chooseHs.chooseStatus == '2'}">
                                            <c:set var="readonly" value="readonly"/>
                                            <c:set var="disabled" value="disabled"/>
                                        </c:if>

                                            <%--根据不同请求页面 设置不同展示方式--%>
                                        <c:set var="zgDisabled" value=""/>
                                        <c:if test="${fromOp == 'gfsh'}">
                                            <%--购房人资格审核 进入--%>
                                            <c:if test="${method == 'view'}">
                                                <c:set var="zgDisabled" value="disabled"/>
                                            </c:if>
                                            <c:set var="readonly" value="readonly"/>
                                            <c:set var="disabled" value="disabled"/>
                                            <c:set var="hidden" value=""/>
                                        </c:if>

                                            <%--选房详情--%>
                                        <c:set var="chooseHsId" value="${chooseHs.chooseHs.chooseHsId}${''}"/>
                                        <c:set var="chooseHouse" value="${chooseHsData[chooseHsId]}"/>
                                        <tr>
                                            <th><label>选房详情：</label></th>
                                            <td colspan="5">
                                                <table border="0" width="100%">
                                                    <tr>
                                                        <th><label>选房地址：</label></th>
                                                        <td colspan="3">
                                                            <input type="text"
                                                                   class="autocomplete js_live_addr ${readonly}" ${readonly} ${disabled}
                                                                   atOption="oh002.getAddrOpt"
                                                                   atUrl="oh002.getAddrUrl"
                                                                   name="hsAddr" value="${chooseHouse.Row.hsAddr}"/>
                                                            <input type="hidden" name="hiddenRegId" value="${regId}"/>
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <th width="20%">房源区域：</th>
                                                        <td width="30%"><span class="js_regName"><oframe:entity
                                                                entityName="RegInfo"
                                                                property="regName"
                                                                prjCd="${prjCd}"
                                                                value="${regId}"/></span>
                                                        </td>
                                                        <th width="20%">房屋居室：</th>
                                                        <td width="30%">
                                                            <input type="hidden" name="hsHxTp" class="js_hsRoomType"
                                                                   value="${chooseHs.chooseHs.chooseHsTp}"/>
                                                            <input type="hidden" name="hsHxName"
                                                                   value="${chooseHouse.Row.hsHxName}"/>
                                                            <oframe:name prjCd="${param.prjCd}" itemCd="HS_ROOM_TYPE"
                                                                         value="${chooseHs.chooseHs.chooseHsTp}"/><span
                                                                class="js_hsHxName">
                                                            <c:if test="${chooseHouse.Row.hsHxName != '' && chooseHouse.Row.hsHxName != null}">
                                                                (${chooseHouse.Row.hsHxName})
                                                            </c:if>
                                                        </span>
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <th>购房单价：</th>
                                                        <td>
                                                            <input type="hidden" name="newHsId" class="js_newHsId"
                                                                   value="${chooseHsId}"/>
                                                            <input type="hidden" name="oldNewHsId"
                                                                   value="${chooseHsId}"/>
                                                            <input type="text" onblur="oh002.calcAllMoney(this);"
                                                                   class="js_hsUnPrice ${readonly}" ${readonly}
                                                                   value="${chooseHouse.Row.dealUnPrice}"/>
                                                        </td>
                                                        <th>实测面积：</th>
                                                        <td><input type="text"
                                                                   class="js_realSize ${readonly}" ${readonly} ${disabled}
                                                                   onblur="oh002.calcAllMoney(this);"
                                                                   value="${chooseHouse.Row.hsBldSize}"/></td>
                                                    </tr>
                                                    <tr>
                                                        <th>总购房款：</th>
                                                        <td><input type="text" class="js_allPayMoney readonly" readonly
                                                                   value="${chooseHouse.Row.dealSalePrice}"/></td>
                                                        <th>选房时间：</th>
                                                        <td><input type="text" name="chDate"
                                                                   class="date js_chooseDate ${readonly}" ${readonly} ${disabled}
                                                                   value='<oframe:date value="${chooseHs.chooseHs.cfmChooseDate}" _default="now" format="yyyy-MM-dd"/>'/>
                                                        </td>
                                                    </tr>
                                                </table>
                                            </td>
                                        </tr>

                                            <%--购房人资格审核--%>
                                        <tr class="${hidden}">
                                            <th><label>购房人资格审核：</label></th>
                                            <td colspan="5">
                                                <table border="0" width="100%">
                                                    <tr>
                                                        <th width="20%">审核结果：</th>
                                                        <td colspan="3">
                                                            <c:set var="zgResult1" value="checked"/>
                                                            <c:set var="zgResult2" value=""/>

                                                            <c:if test="${chooseHs.chooseHs.zgResult == '2'}">
                                                                <c:set var="zgResult1" value=""/>
                                                                <c:set var="zgResult2" value="checked"/>
                                                            </c:if>

                                                            <input type="radio"
                                                                   name="zgResult${hsCtChooseId}" ${zgResult1} ${zgDisabled}
                                                                   value="1"/>审核通过
                                                            <input type="radio"
                                                                   name="zgResult${hsCtChooseId}" ${zgResult2} ${zgDisabled}
                                                                   value="2"/>审核不通过
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <th>审核时间：</th>
                                                        <td colspan="3"><input type="text" class="date"
                                                                               name="zgTime" ${zgDisabled}
                                                                               value='<oframe:date value="${chooseHs.chooseHs.zgTime}" _default="now" format="yyyy-MM-dd"/>'/>
                                                        </td>
                                                    </tr>
                                                    <c:if test="${method == 'edit'}">
                                                        <tr>
                                                            <td colspan="4" style="text-align: center">
                                                <span class="btn-primary"
                                                      style="padding: 5px 10px 5px 10px; line-height: 34px; cursor:pointer;"
                                                      onclick="ph008.cfmGfSh(this);">提交审核</span>
                                                            </td>
                                                        </tr>
                                                    </c:if>
                                                </table>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td colspan="6" style="text-align: center; padding: 3px 5px 3px 5px;">
                                                <c:set var="hiddenTemp" value="hidden"/>
                                                <c:set var="hiddenTemp2" value=""/>
                                                <c:set var="chooseStatus"
                                                       value="${chooseHs.chooseHs.chooseStatus}${''}"/>
                                                <c:if test="${chooseStatus == '2'}">
                                                    <c:set var="hiddenTemp" value=""/>
                                                    <c:set var="hiddenTemp2" value="hidden"/>
                                                </c:if>
                                                <c:if test="${fromOp == 'gfsh' || method == 'view'}">
                                                    <c:set var="hiddenTemp" value="hidden"/>
                                                    <c:set var="hiddenTemp2" value="hidden"/>
                                                </c:if>
                                                <span class="btn-primary ${hiddenTemp} js_cancelChooseBtn"
                                                      style="padding: 5px 10px 5px 10px; line-height: 34px; cursor:pointer;"
                                                      onclick="oh002.cancelHsChoose(this)">取消选房</span>
                                                <span class="btn-primary ${hiddenTemp2} js_cfmChooseBtn"
                                                      style="padding: 5px 10px 5px 10px; line-height: 34px; cursor:pointer;"
                                                      onclick="oh002.cfmHsChoose(this)">确认选房</span>
                                            </td>
                                        </tr>
                                    </table>
                                </div>
                            </div>
                        </c:forEach>
                    </c:forEach>
                </c:forEach>
            </div>
        </c:if>
        <c:if test="${allHsMap == null}">
            <div class="panel mart5 js_div" style="width: 60%; margin-left: 20%;margin-right: 20%">
                <h1></h1>

                <div class="js_panel_context">
                    <table class="border" style="border: 1px solid #000000" width="100%">
                        <tr>
                            <td>选房数据为空，请先于信息录入页面添加预选房！</td>
                        </tr>
                    </table>
                </div>
            </div>
        </c:if>

    </div>
    <oframe:script src="${pageContext.request.contextPath}/eland/oh/oh002/js/oh002.js" type="text/javascript"/>
    <oframe:script src="${pageContext.request.contextPath}/eland/ph/ph008/js/ph008.js" type="text/javascript"/>
