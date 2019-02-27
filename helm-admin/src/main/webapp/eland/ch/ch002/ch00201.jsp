<%--信息录入 选房信息界面--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<div>
    <form id="ch00201ChooseHsInfo">
        <div class="panel js_hs_area_div">
        <h1>[<oframe:entity entityName="RegInfo" property="regName" prjCd="${prjCd}" value="${regId}"/>]房源</h1>

        <div>
            <table class="border js_summary_table">
                <tr>
                    <th width="15%">应安置面积：</th>
                    <td width="35%">
                        <c:set var="chRegMap" value="${chooseRegMap[regId]}"/>
                        <input type="hidden" name="gvHsCtRegId" value="${chRegMap.HsRegInfo.hsCtRegId}"/>
                        <input type="hidden" name="gvTtRegId" value="${regId}"/>
                        <input type="hidden" name="gvHsCtId" value="${ctInfo.HsCtInfo.hsCtId}"/>
                        <input type="text" name="fpmj" onchange="ch002.calcSize(this);"
                               value="${chRegMap.HsRegInfo.fpmj}" style="width: 50px;"/> X
                        <input type="text" name="regCoeff" value="${regInfo.PrjReg.regAttr8}"
                               class="readonly" readonly style="width: 25px;"/> =
                        <input type="text" name="yazmj" value="${chRegMap.HsRegInfo.yazmj}"
                               class="readonly" readonly style="width: 60px;"/>

                        <%--面积赠送信息--%>
                        <c:forEach items="${aGiveSize}" var="item">
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
                    <th width="15%">实际可安置面积：<input type="hidden" name="lastSize" value=""/></th>
                    <td width="35%" class="js_ctCanHsSize"></td>
                </tr>
            </table>
            <table class="border js_summary_table">
                <tr>
                    <c:forEach items="${hsTps}" var="item">
                        <th width="15%">${item.value}数量：</th>
                        <td style="text-align: center;">
                            <input type="hidden" name="regId" value="${regId}">
                            <input type="hidden" name="hsType" value="${item.key}">
                            <input type="hidden" name="hsTypeName" value="${item.value}">
                            <input type="text" value="${fn:length(allHsMap[regId][item.key])}" name="hsTypeNum"
                                   readonly class="readonly js_num"
                                   style="width: 30px; text-align: center;">
                            <span class="addPlus" onclick="ch002.addNewHs(this);">+</span>
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
                                        <th width="8%"><label><strong>购房人</strong>姓名：</label></th>
                                        <td>
                                            <%--购房人--%>
                                            <%--选房id需要传递--%>
                                            <input type="hidden" name="hsCtChooseId" value=""/>
                                            <input type="hidden" name="gtJzPsIds">
                                            <input type="hidden" name="gtJzPsOwnerRels">
                                            <input type="hidden" class="js_ps_id js_ps" name="buyPersonId">
                                            <input type="hidden" name="chooseHsTp">
                                            <input type="hidden" name="chooseHsRegId">
                                            <input type="text" atOption="ch002.getOption" atUrl="ch002.getUrl"
                                                   class="js_ps_name js_ps autocomplete ctRequired">
                                        </td>
                                        <th width="8%"><label>身份证号码：</label></th>
                                        <td width="20%"><input type="text" class="js_ps_certy js_ps"></td>
                                        <th width="8%"><label>联系电话：</label></th>
                                        <td><input type="text" class="js_ps_tel js_ps"></td>
                                        <th width="8%"><label class="hsTypeLabel">腾退人关系：</label></th>
                                        <td width="20%">
                                            <input type="text" name="buyPersonOwnerRel" class="autocomplete"
                                                   style="width: 30%;"
                                                   atOption="ch002.getPsRelOpt" atUrl="ch002.getPsRelUrl">
                                            <span class="link marr5 js_doc_info" docTypeName="身份证"
                                                  onclick="ch002.showDoc(this)">
                                                 <input type="hidden" class="js_ps_certy_doc js_ps" value="">
                                                 <label class="cursorpt">身份证</label>
                                            </span>
                                            <span class="link marr5 js_doc_info" docTypeName="选房关系证明"
                                                  onclick="ch002.showDoc(this)">
                                                 <input type="hidden" name="buyPsOwnerRelDocIds" value="">
                                                 <label class="cursorpt">关系证明</label>
                                            </span>
                                        </td>
                                    </tr>
                                    <tr class="js_ps_more">
                                        <th><label>现住址：</label></th>
                                        <td><input type="text" class="js_live_addr js_ps autocomplete"
                                                   name="newHsAddr" atOption="ch002.getAddrOpt"
                                                   atUrl="ch002.getAddrUrl"></td>
                                        <th><label>户口所在地：</label></th>
                                        <td><input type="text" class="js_family_addr js_ps"></td>
                                        <th><label>工作单位：</label></th>
                                        <td><input type="text" class="js_job_addr js_ps"></td>
                                        <th><label>备注说明：</label></th>
                                        <td><input type="text" name="chooseNote" style="width: 30%">
                                            <span class="link marr5" onclick="ch002.addLivePs(this)">+居住人</span>
                                            <span class="link marr5" onclick="ch002.changeWt(this)">委托</span>
                                        </td>
                                    </tr>
                                    <tr class="hidden js_wt_info">
                                        <th><label><strong>被委托人</strong>姓名：</label></th>
                                        <td>
                                            <input type="hidden" class="js_ps_id" name="buyPsWtIds">
                                            <input type="text" atOption="ch002.getOption" atUrl="ch002.getUrl"
                                                   class="js_ps_name js_ps autocomplete">
                                        </td>
                                        <th><label>身份证号码：</label></th>
                                        <td><input type="text" class="js_ps_certy js_ps"></td>
                                        <th><label>联系电话：</label></th>
                                        <td><input type="text" class="js_ps_tel js_ps"></td>
                                        <th><label class="hsTypeLabel">购房人关系：</label></th>
                                        <td>
                                            <input type="text" name="buyPsWtOwnerRel" style="width: 30%;"
                                                   class="autocomplete" atOption="ch002.getPsRelOpt"
                                                   atUrl="ch002.getPsRelUrl">
                                            <span class="link marr5 js_doc_info" docTypeName="身份证"
                                                  onclick="ch002.showDoc(this)">
                                                 <input type="hidden" class="js_ps_certy_doc js_ps" value="">
                                                 <label class="cursorpt">身份证</label>
                                            </span>
                                            <span class="marr5 link js_doc_info cursorpt" onclick="ch002.showDoc(this)">委托证明
                                                    <input type="hidden" name="buyPsWtDocIds">
                                            </span>
                                        </td>
                                    </tr>
                                    <tr class="hidden js_gtjz_info">
                                        <th><label><strong>居住人</strong>姓名：</label></th>
                                        <td>
                                            <input type="hidden" class="js_ps_id" name="gtJzPsId">
                                            <input type="text" class="js_ps_name js_ps autocomplete"
                                                   atOption="ch002.getOption" atUrl="ch002.getUrl">
                                        </td>
                                        <th><label>身份证号码：</label></th>
                                        <td><input type="text" class="js_ps_certy js_ps"></td>
                                        <th><label>联系电话：</label></th>
                                        <td><input type="text" class="js_ps_tel js_ps"></td>
                                        <th><label class="hsTypeLabel">购房人关系：</label></th>
                                        <td>
                                            <input style="width: 30%;" type="text" class="autocomplete"
                                                   atOption="ch002.getPsRelOpt"
                                                   atUrl="ch002.getPsRelUrl"
                                                   name="gtJzPsOwnerRel">
                                            <span class="link marr5 js_doc_info" docTypeName="身份证"
                                                  onclick="ch002.showDoc(this)">
                                                <input type="hidden" class="js_ps_certy_doc js_ps"
                                                       value="">
                                                 <label class="cursorpt">身份证</label>
                                            </span>
                                            <span class="marl5 link js_live_span cursorpt"
                                                  onclick="ch002.delLivePs(this);">删除</span>
                                        </td>
                                    </tr>
                                </table>
                                <span class="removeX" onclick="ch002.delNewHs(this);">X</span>
                            </div>
                        </td>
                    </tr>
                    <c:set var="regMapInfo" value="${allHsMap[regId]}"/>
                    <c:forEach items="${regMapInfo}" var="regMapItem">
                        <tr>
                            <th width="0%" style="text-align: center;vertical-align: middle;min-width: 0;">
                                <label class="js_js_name"
                                       style="width:20px;display: inline-block;vertical-align: middle;">
                                        ${hsTps[regMapItem.key]}
                                </label>
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
                                                    <c:set var="buyPersonId" value="${chooseHs.chooseHs.buyPersonId}${''}"/>
                                                    <c:set var="buyPerson" value="${personsData[buyPersonId]}"/>
                                                    <input type="hidden" name="chooseHsTp" value="${regMapItem.key}">
                                                    <input type="hidden" name="chooseHsRegId" value="${chooseHs.chooseHs.chooseHsRegId}">
                                                    <input type="text" class="js_ps_name js_ps autocomplete ctRequired"
                                                           atOption="ch002.getOption" atUrl="ch002.getUrl"
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
                                                           atOption="ch002.getPsRelOpt"
                                                           atUrl="ch002.getPsRelUrl"
                                                           value="${chooseHs.chooseHs.buyPersonOwnerRel}">
                                                          <span class="link marr5 js_doc_info" docTypeName="身份证"
                                                                onclick="ch002.showDoc(this)">
                                                               <input type="hidden" class="js_ps_certy_doc js_ps"
                                                                     value="${buyPerson.Row.personCertyDocIds}">
                                                               <label class="cursorpt">身份证</label>
                                                          </span>
                                                          <span class="link marr5 js_doc_info" docTypeName="选房关系证明"
                                                                onclick="ch002.showDoc(this)">
                                                              <input type="hidden" name="buyPsOwnerRelDocIds"
                                                                     value="${chooseHs.chooseHs.buyPsOwnerRelDocIds}">
                                                               <label class="cursorpt">关系证明</label>
                                                          </span>
                                                </td>
                                            </tr>
                                            <tr class="js_ps_more">
                                                <th><label>现住址：</label></th>
                                                <td><input type="text" class="js_live_addr js_ps autocomplete"
                                                           name="newHsAddr" atOption="ch002.getAddrOpt"
                                                           atUrl="ch002.getAddrUrl"
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
                                                              onclick="ch002.addLivePs(this)">
                                                            +居住人
                                                        </span>
                                                        <span class="link marr5 cursorpt" onclick="ch002.changeWt(this)">
                                                            <c:if test="${chooseHs.chooseHs.buyPsWtIds != null && chooseHs.chooseHs.buyPsWtIds != ''}">
                                                                取消委托
                                                            </c:if>
                                                            <c:if test="${chooseHs.chooseHs.buyPsWtIds == null || chooseHs.chooseHs.buyPsWtIds == ''}">
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
                                                <th><label><span style="font-weight: bold;">被委托人</span>姓名：</label>
                                                </th>
                                                <td>
                                                    <input type="hidden" class="js_ps_id" name="buyPsWtIds" value="${chooseHs.chooseHs.buyPsWtIds}">
                                                    <c:set var="buyWtPersonId" value="${chooseHs.chooseHs.buyPsWtIds}${''}"/>
                                                    <c:set var="buyWtPerson" value="${personsData[buyWtPersonId]}"/>
                                                    <input type="text" value="${buyWtPerson.Row.personName}"
                                                           class="js_ps_name js_ps">
                                                </td>
                                                <th><label>身份证号码：</label></th>
                                                <td><input type="text" class="js_ps_certy js_ps" value="${buyWtPerson.Row.personCertyNum}">
                                                </td>
                                                <th><label>联系电话：</label></th>
                                                <td><input type="text" class="js_ps_tel js_ps"
                                                           value="${buyWtPerson.Row.personTelphone}">
                                                </td>
                                                <th><label class="hsTypeLabel">购房人关系：</label></th>
                                                <td>
                                                    <input style="width: 30%;" type="text" name="buyPsWtOwnerRel"
                                                           class="autocomplete"
                                                           atOption="ch002.getPsRelOpt"
                                                           atUrl="ch002.getPsRelUrl"
                                                           value="${chooseHs.chooseHs.buyPsWtOwnerRel}">
                                                    <span class="link marr5 js_doc_info" docTypeName="身份证"
                                                          onclick="ch002.showDoc(this)">
                                                        <input type="hidden" class="js_ps_certy_doc js_ps"
                                                               value="${buyWtPerson.Row.personCertyDocIds}">
                                                         <label class="cursorpt">身份证</label>
                                                    </span>
                                                    <span class="marl5 link js_doc_info cursorpt"
                                                          onclick="ch002.showDoc(this)">委托证明
                                                          <input type="hidden" name="buyPsWtDocIds"
                                                                 value="${chooseHs.chooseHs.buyPsWtDocIds}">
                                                    </span>
                                                </td>
                                            </tr>
                                            <c:set var="gtJzPsIds" value="${chooseHs.chooseHs.gtJzPsIds}${''}"/>
                                            <c:set value="${ fn:split(gtJzPsIds, ',') }" var="gtJzPsIdArr"/>
                                            <c:set var="gtJzPsOwnerRels" value="${chooseHs.chooseHs.gtJzPsOwnerRels}${''}"/>
                                            <c:set value="${ fn:split(gtJzPsOwnerRels, ',') }" var="gtJzPsOwnerRelArr"/>
                                            <c:forEach items="${gtJzPsIdArr}" var="jzPsId" varStatus="varStatus">
                                                <c:set var="gtjzClass" value="hidden"/>
                                                <c:if test="${jzPsId != null && jzPsId != ''}">
                                                    <c:set var="gtjzClass" value=""/>
                                                </c:if>
                                                <tr class="js_gtjz_info ${gtjzClass}">
                                                    <th><label><span
                                                            style="font-weight: bold;">共同居住人</span>姓名</label>
                                                    </th>
                                                    <td>
                                                        <input type="hidden" class="js_ps_id" name="gtJzPsId"
                                                               value="${jzPsId}">
                                                        <c:set var="gtJzPerson" value="${personsData[jzPsId]}"/>
                                                        <input type="text"
                                                               atOption="ch002.getOption" atUrl="ch002.getUrl"
                                                               class="js_ps_name js_ps autocomplete"
                                                               value="${gtJzPerson.Row.personName}">
                                                    </td>
                                                    <th><label>身份证号码：</label></th>
                                                    <td><input type="text" class="js_ps_certy js_ps"
                                                               value="${gtJzPerson.Row.personCertyNum}">
                                                    </td>
                                                    <th width="10%"><label>联系电话：</label></th>
                                                    <td><input type="text" class="js_ps_tel js_ps"
                                                               value="${gtJzPerson.Row.personTelphone}">
                                                    </td>
                                                    <th width="10%"><label class="hsTypeLabel">购房人关系：</label></th>
                                                    <td>
                                                        <input style="width: 30%;" type="text" name="gtJzPsOwnerRel"
                                                               class="autocomplete"
                                                               atOption="ch002.getPsRelOpt"
                                                               atUrl="ch002.getPsRelUrl"
                                                               value="${gtJzPsOwnerRelArr[varStatus.index]}">
                                                            <span class="link marr5 js_doc_info" docTypeName="身份证"
                                                                  onclick="ch002.showDoc(this)">
                                                                <input type="hidden" class="js_ps_certy_doc js_ps"
                                                                       value="${gtJzPerson.Row.personCertyDocIds}">
                                                                 <label class="cursorpt">身份证</label>
                                                            </span>
                                                            <span class="marl5 link js_live_span cursorpt" onclick="ch002.delLivePs(this);">删除</span>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </table>
                                        <span class="removeX" onclick="ch002.delNewHs(this);">X</span>
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
    </form>
</div>