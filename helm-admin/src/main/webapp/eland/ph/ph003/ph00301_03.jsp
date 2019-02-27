<%--信息录入 选房信息界面--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<div>
    <div class="panel mart5 js_hs_area_div" style="min-height: 300px;">
        <h1>[<oframe:entity entityName="RegInfo" property="regName" prjCd="${prjCd}" value="${regId}"/>]房源</h1>

        <div>
            <table class="border js_summary_table" width="100%">
                <tr>
                    <th width="15%">应安置面积：</th>
                    <td width="35%">
                        <c:set var="chRegMap" value="${chooseRegMap[regId]}"/>
                        <input type="hidden" name="gvHsCtRegId" value="${chRegMap.HsRegInfo.hsCtRegId}"/>
                        <input type="hidden" name="gvTtRegId" value="${regId}"/>
                        <input type="hidden" name="gvHsCtId" value="${ctInfo.HsCtInfo.hsCtId}"/>
                        <input type="text" name="fpmj" onchange="ph00301_input.calcSize(this);"
                               value="${chRegMap.HsRegInfo.fpmj}" style="width: 40px;"/> X
                        <input type="text" name="regCoeff" value="${regInfo.PrjReg.regAttr8}" class="readonly" readonly
                               style="width: 25px;"/> =
                        <input type="text" name="yazmj" value="${chRegMap.HsRegInfo.yazmj}" class="readonly" readonly
                               style="width: 55px;"/>

                        <c:forEach items="${aGiveSize}" var="item" varStatus="varStatus">
                            <c:if test="${regId == item.HsCtGive.regId}">
                                <input type="hidden" class="js_ctSizeA" value="${item.HsCtGive.ctSize}"/>
                            </c:if>
                        </c:forEach>
                        <c:forEach items="${bGiveSize}" var="item">
                            <c:if test="${regId == item.HsCtGive.regId}">
                                <input type="hidden" class="js_ctSizeB" value="${item.HsCtGive.ctSize}"/>
                            </c:if>
                        </c:forEach>
                    </td>
                    <th width="15%">实际可安置面积：</th>
                    <td width="35%" class="js_ctCanHsSize"></td>
                    <input type="hidden" name="lastSize" value=""/>
                </tr>
            </table>
            <table class="border js_summary_table" width="100%">
                <tr>
                    <c:forEach items="${hsTps}" var="item">
                        <th width="15%"><label>${item.value}数量：</label></th>
                        <td style="text-align: center;">
                            <input type="hidden" name="regId" value="${regId}">
                            <input type="hidden" name="hsType" value="${item.key}">
                            <input type="hidden" name="hsTypeName" value="${item.value}">
                            <input type="text" value="${fn:length(allHsMap[regId][item.key])}" name="hsTypeNum"
                                   readonly class="readonly js_num js_yj_num" style="width: 30px; text-align: center;">
                            <span class="addPlus" onclick="ph00301_input.addNewHs(this);">+</span>
                        </td>
                    </c:forEach>
                </tr>
            </table>

            <div class="mart5">
                <table class="js_hs_buy_info_table border">
                    <tr class="hidden">
                        <th width="0%" style="text-align: center;vertical-align: middle;min-width: 0;">
                            <label class="js_js_name"
                                   style="width:20px;display: inline-block;vertical-align: middle;"></label>
                            <input type="hidden" name="hsType" value="">
                        </th>
                        <td>
                            <div class="mar10 js_hs_buy_info hidden" style="position: relative;">
                                <table class="border" width="100%">
                                    <tr>
                                        <th width="8%"><label><span
                                                style="font-weight: bold;">购房人</span>姓名：</label></th>
                                        <td>
                                            <%--购房人--%>
                                            <%--选房id需要传递--%>
                                            <input type="hidden" name="hsCtChooseId" value=""/>
                                            <input type="hidden" name="gtJzPsIds">
                                            <input type="hidden" name="gtJzPsOwnerRels">
                                            <input type="hidden" class="js_ps_id js_ps" name="buyPersonId">
                                            <input type="hidden" name="chooseHsTp">
                                            <input type="hidden" name="chooseHsRegId">
                                            <input type="text"
                                                   class="autocomplete js_ps_name js_owner_display ctRequired"
                                                   atOption="ph00301_input.getBuyPsOpt" value=''>
                                        </td>
                                        <th width="8%"><label>身份证号码：</label></th>
                                        <td width="20%"><input type="text" class="js_ps_certy js_ps"></td>
                                        <th width="6%"><label>联系电话：</label></th>
                                        <td><input type="text" class="js_ps_tel js_ps"></td>
                                        <th width="6%"><label class="hsTypeLabel">腾退人关系：</label></th>
                                        <td width="20%">
                                            <input style="width: 30%;" name="buyPersonOwnerRel" class="autocomplete"
                                                   atOption="ph00301_input.getPsRelOpt"
                                                   atUrl="ph00301_input.getPsRelUrl"
                                                   type="text">
                                            <span class="link marr5 js_doc_info" docTypeName="身份证"
                                                  onclick="ph00301_input.showDoc(this)">
                                                <input type="hidden" class="js_ps_certy_doc js_ps" value="">
                                                 <label style="cursor:pointer;">&nbsp;身份证&nbsp;</label>
                                            </span>
                                            <span class="link marr5 js_doc_info" docTypeName="选房关系证明"
                                                  onclick="ph00301_input.showDoc(this)">
                                                <input type="hidden" name="buyPsOwnerRelDocIds" value="">
                                                 <label style="cursor:pointer;">关系证明</label>
                                            </span>
                                        </td>
                                    </tr>
                                    <tr class="js_ps_more">
                                        <th><label>现住址：</label></th>
                                        <td><input type="text" class="js_live_addr js_ps autocomplete" name="newHsAddr"
                                                   atOption="ph00301_input.getAddrOpt"
                                                   atUrl="ph00301_input.getAddrUrl"></td>
                                        <th><label>户口所在地：</label></th>
                                        <td><input type="text" class="js_family_addr js_ps"></td>
                                        <th><label>工作单位：</label></th>
                                        <td><input type="text" class="js_job_addr js_ps"></td>
                                        <th><label>备注说明：</label></th>
                                        <td><input type="text" style="width: 30%" name="chooseNote">
                                            <span class="link marr5" onclick="ph00301_input.addLivePs(this)">
                                                +居住人
                                            </span>
                                            <span class="link marr5" onclick="ph00301_input.changeWt(this)">
                                                委托
                                            </span>
                                        </td>
                                    </tr>
                                    <tr class="hidden js_wt_info">
                                        <th><label><span style="font-weight: bold;">被托人</span>姓名：</label></th>
                                        <td>
                                            <input type="hidden" class="js_ps_id js_ps" name="buyPsWtIds">
                                            <input type="text"
                                                   class="autocomplete js_ps_name js_owner_display ctRequired"
                                                   atOption="ph00301_input.getBuyPsWtOpt">
                                        </td>
                                        <th><label>身份证号码：</label></th>
                                        <td><input type="text" class="js_ps_certy js_ps"></td>
                                        <th width="10%"><label>联系电话：</label></th>
                                        <td><input type="text" class="js_ps_tel js_ps"></td>
                                        <th width="10%"><label class="hsTypeLabel">购房人关系：</label></th>
                                        <td>
                                            <input style="width: 30%;" name="buyPsWtOwnerRel"
                                                   class="autocomplete"
                                                   atOption="ph00301_input.getPsRelOpt"
                                                   atUrl="ph00301_input.getPsRelUrl"
                                                   type="text">
                                            <span class="link marr5 js_doc_info" docTypeName="身份证"
                                                  onclick="ph00301_input.showDoc(this)">
                                                <input type="hidden" class="js_ps_certy_doc" value="">
                                                 <label style="cursor:pointer;">&nbsp;身份证&nbsp;</label>
                                            </span>
                                            <span class="marr5 link js_doc_info" onclick="ph00301_input.showDoc(this)"
                                                  style="cursor:pointer;">
                                                委托证明
                                                    <input type="hidden" name="buyPsWtDocIds">
                                            </span>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th><label><span style="font-weight: bold;">居住人</span>姓名：</label></th>
                                        <td>
                                            <input type="hidden" class="js_ps_id js_ps" name="gtJzPsId">
                                            <input type="text"
                                                   class="autocomplete js_ps_name js_owner_display ctRequired"
                                                   atOption="ph00301_input.getBuyPsOpt"
                                                   value="">
                                        </td>
                                        <th><label>身份证号码：</label></th>
                                        <td><input type="text" class="js_ps_certy js_ps"></td>
                                        <th width="10%"><label>联系电话：</label></th>
                                        <td><input type="text" class="js_ps_tel js_ps"></td>
                                        <th width="10%"><label class="hsTypeLabel">购房人关系：</label></th>
                                        <td>
                                            <input style="width: 30%;" type="text" class="autocomplete"
                                                   atOption="ph00301_input.getPsRelOpt"
                                                   atUrl="ph00301_input.getPsRelUrl"
                                                   name="gtJzPsOwnerRel">
                                            <span class="link marr5 js_doc_info" docTypeName="身份证"
                                                  onclick="ph00301_input.showDoc(this)">
                                                <input type="hidden" class="js_ps_certy_doc js_ps"
                                                       value="">
                                                 <label style="cursor:pointer;">&nbsp;身份证&nbsp;</label>
                                            </span>
                                            <span class="marl5 hidden link js_live_span" style="cursor:pointer;"
                                                  onclick="ph00301_input.delLivePs(this);">删除</span>
                                        </td>
                                    </tr>
                                </table>
                                <span class="removeX" onclick="ph00301_input.delNewHs(this);">X</span>
                            </div>
                        </td>
                    </tr>
                    <c:set var="regMapInfo" value="${allHsMap[regId]}"/>
                    <c:forEach items="${regMapInfo}" var="regMapItem">
                        <tr>
                            <th width="0%" style="text-align: center;vertical-align: middle;min-width: 0;">
                                <label class="js_js_name"
                                       style="width:20px;display: inline-block;vertical-align: middle;">${hsTps[regMapItem.key]}</label>
                                <input type="hidden" name="hsType" value="${regMapItem.key}">
                            </th>
                            <td>
                                    <%--户型对应的购房信息循环开始--%>
                                <c:forEach items="${regMapItem.value}" var="chooseHs">
                                    <div class="mar10 js_hs_buy_info" style="position: relative;">
                                        <table class="border" width="100%">
                                            <tr>
                                                <th width="8%"><label><span
                                                        style="font-weight: bold;">购房人</span>姓名：</label>
                                                </th>
                                                <td>
                                                    <input type="hidden" name="hsCtChooseId"
                                                           value="${chooseHs.chooseHs.hsCtChooseId}"/>
                                                    <input type="hidden" name="gtJzPsIds"
                                                           value="${chooseHs.chooseHs.gtJzPsIds}">
                                                    <input type="hidden" name="gtJzPsOwnerRels"
                                                           value="${chooseHs.chooseHs.gtJzPsOwnerRels}">
                                                    <input type="hidden" class="js_ps_id js_ps" name="buyPersonId"
                                                           value="${chooseHs.chooseHs.buyPersonId}">
                                                    <c:set var="buyPersonId"
                                                           value="${chooseHs.chooseHs.buyPersonId}${''}"/>
                                                    <c:set var="buyPerson" value="${personsData[buyPersonId]}"/>
                                                    <input type="hidden" name="chooseHsTp" value="${regMapItem.key}">
                                                    <input type="hidden" name="chooseHsRegId"
                                                           value="${chooseHs.chooseHs.chooseHsRegId}">
                                                    <input type="text"
                                                           class="autocomplete js_ps_name js_owner_display ctRequired"
                                                           atOption="ph00301_input.getBuyPsOpt"
                                                           value="${buyPerson.Row.personName}">
                                                </td>
                                                <th width="8%"><label>身份证号码：</label>
                                                </th>
                                                <td width="20%"><input type="text" class="js_ps_certy js_ps"
                                                                       value="${buyPerson.Row.personCertyNum}">
                                                </td>
                                                <th width="8%"><label>联系电话：</label></th>
                                                <td><input type="text" class="js_ps_tel js_ps"
                                                           value="${buyPerson.Row.personTelphone}">
                                                </td>
                                                <th width="8%"><label class="hsTypeLabel">腾退人关系：</label></th>
                                                <td width="20%">
                                                    <input style="width: 30%;" name="buyPersonOwnerRel" type="text"
                                                           class="autocomplete"
                                                           atOption="ph00301_input.getPsRelOpt"
                                                           atUrl="ph00301_input.getPsRelUrl"
                                                           value="${chooseHs.chooseHs.buyPersonOwnerRel}">
                                                          <span class="link marr5 js_doc_info" docTypeName="身份证"
                                                                onclick="ph00301_input.showDoc(this)">
                                                              <input type="hidden" class="js_ps_certy_doc js_ps"
                                                                     value="${buyPerson.Row.personCertyDocIds}">
                                                               <label style="cursor:pointer;">&nbsp;身份证&nbsp;</label>
                                                          </span>
                                                          <span class="link marr5 js_doc_info" docTypeName="选房关系证明"
                                                                onclick="ph00301_input.showDoc(this)">
                                                              <input type="hidden" name="buyPsOwnerRelDocIds"
                                                                     value="${chooseHs.chooseHs.buyPsOwnerRelDocIds}">
                                                               <label style="cursor:pointer;">关系证明</label>
                                                          </span>
                                                </td>
                                            </tr>
                                            <tr class="js_ps_more">
                                                <th><label>现住址：</label></th>
                                                <td><input type="text" class="js_live_addr js_ps autocomplete"
                                                           name="newHsAddr"
                                                           atOption="ph00301_input.getAddrOpt"
                                                           atUrl="ph00301_input.getAddrUrl"
                                                           value="${buyPerson.Row.personLiveAddr}">
                                                </td>
                                                <th><label>户口所在地：</label></th>
                                                <td><input type="text" class="js_family_addr js_ps"
                                                           value="${buyPerson.Row.familyAddr}">
                                                </td>
                                                <th><label>工作单位：</label></th>
                                                <td><input type="text" class="js_job_addr js_ps"
                                                           value="${buyPerson.Row.personJobAddr}">
                                                </td>
                                                <th><label>备注说明：</label></th>
                                                <td>
                                                    <input type="text" style="width: 30%" name="chooseNote"
                                                           value="${chooseHs.chooseHs.chooseNote}">
                                                        <span class="link marr5"
                                                              onclick="ph00301_input.addLivePs(this)">
                                                            +居住人
                                                        </span>
                                                        <span class="link marr5" onclick="ph00301_input.changeWt(this)"
                                                              style="cursor:pointer;">
                                                            <c:if test="${chooseHs.chooseHs.buyPsWtIds != null && chooseHs.chooseHs.buyPsWtIds != ''}">
                                                                取消委托
                                                            </c:if>
                                                            <c:if test="${chooseHs.chooseHs.buyPsWtIds == null && chooseHs.chooseHs.buyPsWtIds != ''}">
                                                                委托
                                                            </c:if>
                                                        </span>
                                                </td>
                                            </tr>
                                            <c:set var="wtClass" value="hidden"/>
                                            <c:if test="${chooseHs.chooseHs.buyPsWtIds != null && chooseHs.chooseHs.buyPsWtIds != ''}">
                                                <c:set var="wtClass" value=""/>
                                            </c:if>
                                            <tr class="js_wt_info ${wtClass}">
                                                <th><label><span style="font-weight: bold;">被委托人</span>姓名：</label></th>
                                                <td>
                                                    <input type="hidden" class="js_ps_id" name="buyPsWtIds"
                                                           value="${chooseHs.chooseHs.buyPsWtIds}">
                                                    <c:set var="buyWtPersonId"
                                                           value="${chooseHs.chooseHs.buyPsWtIds}${''}"/>
                                                    <c:set var="buyWtPerson" value="${personsData[buyWtPersonId]}"/>
                                                    <input type="text" value="${buyWtPerson.Row.personName}"
                                                           class="autocomplete js_ps_name js_owner_display ctRequired"
                                                           atOption="ph00301_input.getBuyPsWtOpt">
                                                </td>
                                                <th><label>身份证号码：</label></th>
                                                <td><input type="text" class="js_ps_certy js_ps"
                                                           value="${buyWtPerson.Row.personCertyNum}">
                                                </td>
                                                <th width="10%"><label>联系电话：</label></th>
                                                <td><input type="text" class="js_ps_tel js_ps"
                                                           value="${buyWtPerson.Row.personTelphone}">
                                                </td>
                                                <th width="10%"><label class="hsTypeLabel">购房人关系：</label></th>
                                                <td>
                                                    <input style="width: 30%;" type="text" name="buyPsWtOwnerRel"
                                                           class="autocomplete"
                                                           atOption="ph00301_input.getPsRelOpt"
                                                           atUrl="ph00301_input.getPsRelUrl"
                                                           value="${chooseHs.chooseHs.buyPsWtOwnerRel}">
                                                    <span class="link marl5 js_doc_info" docTypeName="身份证"
                                                          onclick="ph00301_input.showDoc(this)">
                                                        <input type="hidden" class="js_ps_certy_doc"
                                                               value="${buyWtPerson.Row.personCertyDocIds}">
                                                         <label style="cursor:pointer;">&nbsp;身份证&nbsp;</label>
                                                    </span>
                                                    <span class="marl5 link js_doc_info"
                                                          onclick="ph00301_input.showDoc(this)"
                                                          style="cursor:pointer;">委托证明
                                                          <input type="hidden" name="buyPsWtDocIds"
                                                                 value="${chooseHs.chooseHs.buyPsWtDocIds}">
                                                    </span>
                                                </td>
                                            </tr>
                                            <c:set var="gtJzPsIds" value="${chooseHs.chooseHs.gtJzPsIds}${''}"/>
                                            <c:set value="${ fn:split(gtJzPsIds, ',') }" var="gtJzPsIdArr"/>
                                            <c:set var="gtJzPsOwnerRels"
                                                   value="${chooseHs.chooseHs.gtJzPsOwnerRels}${''}"/>
                                            <c:set value="${ fn:split(gtJzPsOwnerRels, ',') }" var="gtJzPsOwnerRelArr"/>
                                            <c:forEach items="${gtJzPsIdArr}" var="jzPsId" varStatus="varStatus">
                                                <tr>
                                                    <th><label><span style="font-weight: bold;">共同居住人</span>姓名</label>
                                                    </th>
                                                    <td>
                                                        <input type="hidden" class="js_ps_id" name="gtJzPsId"
                                                               value="${jzPsId}">
                                                        <c:set var="gtJzPerson" value="${personsData[jzPsId]}"/>
                                                        <input type="text"
                                                               class="autocomplete js_ps_name js_owner_display ctRequired"
                                                               atOption="ph00301_input.getBuyPsOpt"
                                                               value="${gtJzPerson.Row.personName}">
                                                    </td>
                                                    <th><label>身份证号码：</label></th>
                                                    <td><input type="text" class="js_ps_certy"
                                                               value="${gtJzPerson.Row.personCertyNum}">
                                                    </td>
                                                    <th width="10%"><label>联系电话：</label></th>
                                                    <td><input type="text" class="js_ps_tel"
                                                               value="${gtJzPerson.Row.personTelphone}">
                                                    </td>
                                                    <th width="10%"><label class="hsTypeLabel">购房人关系：</label></th>
                                                    <td>
                                                        <input style="width: 30%;" type="text" name="gtJzPsOwnerRel"
                                                               class="autocomplete"
                                                               atOption="ph00301_input.getPsRelOpt"
                                                               atUrl="ph00301_input.getPsRelUrl"
                                                               value="${gtJzPsOwnerRelArr[varStatus.index]}">
                                                            <span class="link marr5 js_doc_info" docTypeName="身份证"
                                                                  onclick="ph00301_input.showDoc(this)">
                                                                <input type="hidden" class="js_ps_certy_doc"
                                                                       value="${gtJzPerson.Row.personCertyDocIds}">
                                                                 <label style="cursor:pointer;">&nbsp;身份证&nbsp;</label>
                                                            </span>
                                                        <c:choose>
                                                            <c:when test="${varStatus.first}">
                                                                <span class="marl5 link hidden js_live_span"
                                                                      style="cursor:pointer;"
                                                                      onclick="ph00301_input.delLivePs(this);">删除</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                 <span class="marl5 link js_live_span"
                                                                       style="cursor:pointer;"
                                                                       onclick="ph00301_input.delLivePs(this);">删除</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </table>
                                        <span class="removeX" onclick="ph00301_input.delNewHs(this);">X</span>
                                    </div>
                                </c:forEach>
                                    <%--户型对应的购房信息循环结束--%>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
            </div>
        </div>
    </div>
</div>