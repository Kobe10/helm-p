<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%--<oframe:script src="${pageContext.request.contextPath}/eland/oh/oh006/js/oh006.js" type="text/javascript"/>--%>
<style>
    #oh00601 #subButton {
        display: inline-block;
        border-radius: 50%;
        line-height: 14px;
        width: 16px;
        height: 16px;
        margin-left: 5px;
        cursor: pointer;
        text-align: center;
        border: 1px solid #f12420;
        background-color: #f12420;
        color: #fff;
    }

    #oh00601 #subButton:hover {
        background-color: #ffffff;
        color: #000;
    }
</style>
<c:set var="writeTxRht" value="readonly='readonly'"/>
<oframe:power rhtCd="edite_tx_rht" prjCd="${param.prjCd}">
    <c:set var="writeTxRht" value=""/>
</oframe:power>
<c:set var="displayPbRht" value="style='display:none'"/>
<c:set var="writePbRht" value="readonly='readonly'"/>
<oframe:power rhtCd="edite_pb_rht" prjCd="${param.prjCd}">
    <c:set var="displayPbRht" value=""/>
    <c:set var="writePbRht" value=""/>
</oframe:power>
<c:set var="deleteXfRht" value="display:none;"/>
<c:set var="writeXfRht" value="true"/>
<c:set var="displayXfRht" value="style='display:none'"/>
<oframe:power rhtCd="edite_xf_rht" prjCd="${param.prjCd}">
    <c:set var="deleteXfRht" value=""/>
    <c:set var="writeXfRht" value="false"/>
    <c:set var="displayXfRht" value=""/>
</oframe:power>
<div class="panelBar">
    <ul class="toolBar">
        <c:set var="choosePbStatus" value="${matchInfo.HouseInfo.HsCtInfo.choosePbStatus}${''}"/>
        <c:set var="chHsStatus" value="${matchInfo.HouseInfo.HsCtInfo.chHsStatus}${''}"/>
        <c:choose>
            <c:when test="${choosePbStatus != '2'}">
                <li><a class="save" onclick="oh006.saveChooseHsInfo()"><span>保存</span></a></li>
                <oframe:op prjCd="${param.prjCd}" template="li" cssClass="save"
                           name="信息锁定" rhtCd="information_lock_rht"
                           onClick="oh006.approve('2')"/>
            </c:when>
            <c:otherwise>
                <c:choose>
                    <c:when test="${chHsStatus != '2'}">
                        <oframe:op prjCd="${param.prjCd}" template="li" cssClass="save"
                                   name="取消锁定" rhtCd="information_lock_rht"
                                   onClick="oh006.approve('1')"/>
                    </c:when>
                </c:choose>
            </c:otherwise>
        </c:choose>
        <oframe:op prjCd="${param.prjCd}" template="li" cssClass="export"
                   name="导出明细" rhtCd="exp_choose_hs_rht"
                   onClick="oh006.chooseExport('${hsId}')"/>
        <oframe:op prjCd="${param.prjCd}" template="li" cssClass="import"
                   name="导入明细" rhtCd="imp_choose_hs_rht"
                   onClick="oh006.importMb()"/>
        <li onclick="oh006.viewHouse(${hsId});">
            <a class="new-area" href="javascript:void(0)"><span>居民详情</span></a>
        </li>
        <li onclick="oh006.singleQuery(${hsId},'next');" style="float: right">
            <a class="export" href="javascript:void(0)"><span>下一户</span></a>
        </li>
        <li onclick="oh006.singleQuery(${hsId},'last');" style="float: right">
            <a class="import" href="javascript:void(0)"><span>上一户</span></a>
        </li>
    </ul>
</div>
<div class="tabs mart5">
    <div class="tabsHeader" style="border-bottom: 1px solid #3d91c8;">
        <div class="tabsHeaderContent">
            <ul>
                <li><a href="javascript:void(0);"><span>配比信息</span></a></li>
                <li><a href="javascript:void(0);"><span>购房信息</span></a></li>
            </ul>
        </div>
    </div>

    <div class="tabsContent" id="oh006DsContainer">
        <div id="oh00601" style="position: relative">
            <h1 class="padl10" style="line-height: 32px; background-color: #e3eef4;">配比信息</h1>
            <span onclick="oh006.switchTip(this)"
                  style="position: absolute; top:0px; right: 0px;display: inline-block; " class="hideTip"></span>
            <table class="border" width=" 100%" id="matchTable">
                <c:set var="hsOwnerType" value="${hsOwnerBean.HouseInfo.hsOwnerType}${''}"/>
                <tr>
                    <th width="12%">档案编号：</th>
                    <td width="21%">${oldHsBean.HouseInfo.hsCd}
                        <input type="hidden" name="hsFullAddr" value="${oldHsBean.HouseInfo.hsFullAddr}"/>
                    </td>
                    <th width="12%">被安置人：</th>
                    <td width="21%">${hsOwnerBean.HouseInfo.hsOwnerPersons}</td>
                    <th width="12%">房屋地址：</th>
                    <td width="21%">${oldHsBean.HouseInfo.hsFullAddr}
                        <input type="hidden" name="hsBuildSize" value="${oldHsBean.HouseInfo.hsBuildSize}"/>
                    </td>
                </tr>
                <tr>
                    <th>产权性质：</th>
                    <td>${hsOwnerBean.HouseInfo.hsOwnerType_Name}</td>
                    <th>建筑面积：</th>
                    <td>${hsOwnerBean.HouseInfo.hsBuildSize}</td>
                    <th>使用面积：</th>
                    <td>${hsOwnerBean.HouseInfo.hsUseSize}</td>
                </tr>
                <tr>
                    <th>家庭结构：</th>
                    <td>${matchInfo.HouseInfo.familyStructure}</td>
                    <th>在册户籍：</th>
                    <td>${matchInfo.HouseInfo.allFamNum}</td>
                    <th>户籍人口数：</th>
                    <td>${matchInfo.HouseInfo.famPsNum}</td>
                </tr>
                <tr>
                    <th>房源配比：</th>
                    <td colspan="5">
                        <c:forEach items="${hsJushs}" var="item">
                            <div style="float: left;margin-left: 10px">
                                    ${item.value}数量：
                                <input type="hidden" name="regId" value="${regId}">
                                <input type="hidden" name="hsType" value="${item.key}">
                                <input type="hidden" name="hsTypeName" value="${item.value}">
                                <span id="subButton" ${displayPbRht} onclick="oh006.subHs(this);">-</span>
                                <input type="text" value="${tempArr[item.key-1]}" name="hsTypeNum${item.key}"
                                       readonly class="readonly js_num"
                                       style="width: 30px; text-align: center;">
                                <span class="addPlus" ${displayPbRht} style="text-align: center;margin-left: 0px"
                                      onclick="oh006.addHs(this);">+</span>
                            </div>
                        </c:forEach>
                    </td>
                </tr>
                <tr>
                    <th>配比说明：</th>
                    <td colspan="5">
                        <textarea ${writePbRht} name="choosePbNote" cols="80"
                                                rows="5">${matchInfo.HouseInfo.HsCtInfo.choosePbNote}</textarea>
                    </td>
                </tr>
            </table>
        </div>
        <div id="js_all_choose_hs_div" style="position: relative;" class="mart5">
            <h1 class="padl10" style="line-height: 32px; background-color: #e3eef4;">购房信息
                <span class="link marr5 js_doc_info" docTypeName="选房确认单"
                      onclick="oh006.showDoc(this)">
                                                 <input type="hidden" name="chooseHsDocIds" value="">
                                                 <label class="cursorpt">选房确认单</label>
                                            </span>
                <span onclick="oh006.initHxTree(this)" style="float: right;line-height: 32;" class="marr10 mart5">
            <button ${displayXfRht} type="button" class="btn btn-more" btnContainer="dialog">
                <span style="font-style:normal;">新增登记</span>
                <span class="caret"></span>
                <ul style="width: 200px;" class="menu hidden">

                    <span style="clear: both"></span>
                </ul>

            </button>
            </span>
            </h1>
            <div style="display:none;position: absolute; width: 90px; right: 10px;left: auto;top: 27px;z-index: 100;"
                 id="hiddenTree" class="ztree tip"></div>
            <input type="hidden" name="hidHsId" value="${hsId}"/>
            <input type="hidden" name="hidHsCtId" value="${ctInfo.HsCtInfo.hsCtId}"/>

            <%--选房列表--%>
            <div id="chooseHsList">
                <form id="oh00601ChooseHsInfo">
                    <div class="panel js_hs_area_div" layoutH="155">
                        <table class="border hidden" id="tempTable">
                            <tr>
                                <th class="subTitle" style="position: relative;background-color: #FDFDCB;">
                                    <h1 class="js_js_name" style="float: left">
                                        <label class="js_reg_name"></label>
                                        (&nbsp;<label class="js_reg_count"></label>)
                                    </h1>

                                    <input type="hidden" name="hsType" value="">
                                    <span onclick="oh006.switchHsTip(this)"
                                          style="position: absolute; top:0px; right: 0px;display: inline-block; "
                                          class="hideTip"></span>
                                </th>
                            </tr>
                            <tr>
                                <td style="padding: 0px;">
                                    <div class="js_hs_buy_info hidden" style="position: relative;">
                                        <table class="border" width="100%">
                                            <tr style="background-color: #EEF1F5">
                                                <th>购房区域：</th>
                                                <td><input type="text" name="chooseHsRegName" class="autocomplete"
                                                           atOption="oh006.getRegOpt"
                                                           atUrl="oh006.getRegUrl" value=""/></td>
                                                <th>购房地址：</th>
                                                <td colspan="3">
                                                    <input type="hidden" readonly name="hsAddr" value=""/>
                                                    <input type="hidden" name="hiddenRegId" value=""/>
                                                    <input type="hidden" name="hsHxTp" value=""/>
                                                    <input type="hidden" name="hsHxName" class="js_hsHxName" value=""/>
                                                    <input type="hidden" name="preBldSize" class="js_preBldSize"
                                                           value=""/>
                                                    <input type="hidden" name="newHsId" class="js_newHsId" value=""/>
                                                    <input type="hidden" name="oldNewHsId" class="js_oldNewHsId"
                                                           value=""/>
                                                </td>
                                                <th>购房状态：</th>
                                                <td>未选房</td>
                                            </tr>
                                            <tr>
                                                <th width="8%"><label><strong>购房人</strong>:</label></th>
                                                <td>
                                                    <%--购房人--%>
                                                    <%--选房id需要传递--%>
                                                    <input type="hidden" name="hsCtChooseId" value=""/>
                                                    <input type="hidden" name="gtJzPsIds">
                                                    <input type="hidden" name="gtJzPsOwnerRels">
                                                    <input type="hidden" class="js_ps_id js_ps" name="buyPersonId">
                                                    <input type="hidden" name="huJush">
                                                    <input type="hidden" name="chooseHsRegId">
                                                    <input type="text" name="buyPersonName" atOption="oh006.getOption"
                                                           atUrl="oh006.getUrl"
                                                           class="js_ps_name js_ps autocomplete ctRequired">
                                                </td>
                                                <th width="8%"><label>身份证号码：</label></th>
                                                <td width="20%"><input name="buyPersonCerty" type="text"
                                                                       class="js_ps_certy js_ps"></td>
                                                <th width="8%"><label class="hsTypeLabel">安置人关系：</label></th>
                                                <td width="20%">
                                                    <input type="text" name="buyPersonOwnerRel" class="autocomplete"
                                                           atOption="oh006.getPsRelOpt" atUrl="oh006.getPsRelUrl">
                                                </td>
                                                <th><label>备注说明：</label></th>
                                                <td><input type="text" name="chooseNote">
                                                </td>
                                            </tr>
                                            <tr class="js_ps_more">
                                                <th><label>现住址：</label></th>
                                                <td><input type="text" class="js_live_addr js_ps autocomplete"
                                                           name="newHsAddr" atOption="oh006.getAddrOptNow"
                                                           atUrl="oh006.getAddrUrlNow"></td>
                                                <th><label>户口所在地：</label></th>
                                                <td><input type="text" class="js_family_addr js_ps"></td>
                                                <th><label>工作单位：</label></th>
                                                <td><input type="text" class="js_job_addr js_ps"></td>
                                                <td colspan="2">
                                            <span class="link marr5 js_doc_info" docTypeName="身份证"
                                                  onclick="oh006.showDoc(this)">
                                                 <input type="hidden" class="js_ps_certy_doc js_ps" value="">
                                                 <label class="cursorpt">身份证</label>
                                            </span>
                                                    <span class="link marr5 js_doc_info" docTypeName="选房关系证明"
                                                          onclick="oh006.showDoc(this)">
                                                 <input type="hidden" name="buyPsOwnerRelDocIds" value="">
                                                 <label class="cursorpt">关系证明</label>
                                            </span>
                                                </td>

                                            </tr>
                                            <tr class="js_ps_more">
                                                <th><label>通讯地址：</label></th>
                                                <td colspan="3"><input type="text" ${writeTxRht}
                                                                       name="personNoticeAddr"/>
                                                </td>
                                                <th width="8%"><label>联系电话：</label></th>
                                                <td><input name="buyPersonTel" type="text" ${writeTxRht}
                                                           class="js_ps_tel js_ps"></td>
                                                <td colspan="2" style="text-align: center; position:relative;">
                                                    <span class="link marr5"
                                                          onclick="oh006.saveNoticeInfo(this)">
                                                            保存通讯
                                                        </span>
                                                    <%--<span class="link marr5" ${displayXfRht}--%>
                                                          <%--onclick="oh006.addLivePs(this)">--%>
                                                            <%--+居住人--%>
                                                        <%--</span>--%>
                                                    <span class="link marr5 cursorpt"
                                                          onclick="oh006.changeWt(this)">
                                                            <c:if test="${chooseHs.chooseHs.buyPsWtIds != null && chooseHs.chooseHs.buyPsWtIds != ''}">
                                                                取消委托
                                                            </c:if>
                                                            <c:if test="${chooseHs.chooseHs.buyPsWtIds == null || chooseHs.chooseHs.buyPsWtIds == ''}">
                                                                委托
                                                            </c:if>
                                                        </span>
                                                </td>
                                            </tr>
                                            <tr class="hidden js_wt_info">
                                                <th><label><strong>被委托人</strong>：</label></th>
                                                <td>
                                                    <input type="hidden" class="js_ps_id" name="buyPsWtIds">
                                                    <input type="text" atOption="oh006.getOption" atUrl="oh006.getUrl"
                                                           class="js_ps_name js_ps autocomplete">
                                                </td>
                                                <th><label>身份证号码：</label></th>
                                                <td><input type="text" class="js_ps_certy js_ps"></td>
                                                <th><label>联系电话：</label></th>
                                                <td><input type="text" class="js_ps_tel js_ps"></td>
                                                <th><label class="hsTypeLabel">购房人关系：</label></th>
                                                <td>
                                                    <input type="text" name="buyPsWtOwnerRel" style="width: 30%;"
                                                           class="autocomplete" atOption="oh006.getPsRelOpt"
                                                           atUrl="oh006.getPsRelUrl">
                                                    <span class="link marr5 js_doc_info" docTypeName="身份证"
                                                          onclick="oh006.showDoc(this)">
                                                 <input type="hidden" class="js_ps_certy_doc js_ps" value="">
                                                 <label class="cursorpt">身份证</label>
                                            </span>
                                                    <span class="marr5 link js_doc_info cursorpt"
                                                          onclick="oh006.showDoc(this)">委托证明
                                                    <input type="hidden" name="buyPsWtDocIds">
                                            </span>
                                                </td>
                                            </tr>
                                            <tr class="hidden js_gtjz_info">
                                                <th><label><strong>居住人</strong>：</label></th>
                                                <td>
                                                    <input type="hidden" class="js_ps_id" name="gtJzPsId">
                                                    <input type="text" class="js_ps_name js_ps autocomplete"
                                                           atOption="oh006.getOption" atUrl="oh006.getUrl">
                                                </td>
                                                <th><label>身份证号码：</label></th>
                                                <td><input type="text" class="js_ps_certy js_ps"></td>
                                                <th><label>联系电话：</label></th>
                                                <td><input type="text" ${writeTxRht} class="js_ps_tel js_ps"></td>
                                                <th><label class="hsTypeLabel">购房人关系：</label></th>
                                                <td>
                                                    <input style="width: 30%;" type="text" class="autocomplete"
                                                           atOption="oh006.getPsRelOpt"
                                                           atUrl="oh006.getPsRelUrl"
                                                           name="gtJzPsOwnerRel">
                                                    <span class="link marr5 js_doc_info" docTypeName="身份证"
                                                          onclick="oh006.showDoc(this)">
                                                <input type="hidden" class="js_ps_certy_doc js_ps"
                                                       value="">
                                                 <label class="cursorpt">身份证</label>
                                            </span>
                                                    <span class="marl5 link js_live_span cursorpt" ${displayXfRht}
                                                          onclick="oh006.delLivePs(this);">删除</span>
                                                </td>
                                            </tr>
                                        </table>
                                        <span class='removeX' style='right: 4px; top:4px;${deleteXfRht}'
                                              onclick='oh006.delNewHs(this);'>X</span>
                                    </div>
                                </td>
                            </tr>
                        </table>
                        <c:forEach items="${hsJushs}" var="hsJush">
                            <c:set var="item" value="${chooseHuJushMap[hsJush.key]}"/>
                            <c:if test="${item != null}">
                                <table class="border" name="${item.chooseHs.huJush_Name}">
                                    <c:set var="hxListInfo" value="${allHsMap[hsJush.key]}"/>
                                    <tr>
                                        <th class="subTitle" style="position: relative;background-color: #FDFDCB;">
                                            <h1 class="js_js_name" style="float: left;">
                                                <label class="js_reg_name">${hsJush.value}</label>
                                                (&nbsp;<label class="js_reg_count">${fn:length(hxListInfo)}</label>)
                                            </h1>
                                            <input type="hidden" name="hsType" value="${hsJush.key}">
                                            <span onclick="oh006.switchHsTip(this)"
                                                  style="position: absolute; top:0px; right: 0px;display: inline-block; "
                                                  class="hideTip"></span>
                                        </th>
                                    </tr>
                                    <tr>
                                        <td style="padding: 0px;">
                                            <div class="js_hs_buy_info hidden" style="position: relative;">
                                                <table class="border" width="100%">
                                                    <tr style="background-color: #EEF1F5">
                                                        <th>购房区域：</th>
                                                        <td><input type="text" name="chooseHsRegName"
                                                                   class="autocomplete"
                                                                   atOption="oh006.getRegOpt"
                                                                   atUrl="oh006.getRegUrl" value=""/></td>
                                                        <th>购房地址：</th>
                                                        <td colspan="3">
                                                            <input type="text"
                                                                   class="autocomplete js_live_addr"
                                                                   atOption="oh006.getAddrOpt"
                                                                   atUrl="oh006.getAddrUrl"
                                                                   name="hsAddr" value=""/>
                                                            <input type="hidden" name="hiddenRegId" value=""/>
                                                            <input type="hidden" name="hsHxTp" value=""/>
                                                            <input type="hidden" name="hsHxName" class="js_hsHxName"
                                                                   value=""/>
                                                            <input type="hidden" name="preBldSize" class="js_preBldSize"
                                                                   value=""/>
                                                            <input type="hidden" name="newHsId" class="js_newHsId"
                                                                   value=""/>
                                                            <input type="hidden" name="oldNewHsId" class="js_oldNewHsId"
                                                                   value=""/>
                                                        </td>
                                                        <th>购房状态：</th>
                                                        <td>未选房</td>
                                                    </tr>
                                                    <tr>
                                                        <th width="8%"><label><strong>购房人</strong>:</label></th>
                                                        <td>
                                                                <%--购房人--%>
                                                                <%--选房id需要传递--%>
                                                            <input type="hidden" name="hsCtChooseId" value=""/>
                                                            <input type="hidden" name="gtJzPsIds">
                                                            <input type="hidden" name="gtJzPsOwnerRels">
                                                            <input type="hidden" class="js_ps_id js_ps"
                                                                   name="buyPersonId">
                                                            <input type="hidden" name="huJush">
                                                            <input type="hidden" name="chooseHsRegId">
                                                            <input type="text" name="buyPersonName"
                                                                   atOption="oh006.getOption"
                                                                   atUrl="oh006.getUrl"
                                                                   class="js_ps_name js_ps autocomplete ctRequired">
                                                        </td>
                                                        <th width="8%"><label>身份证号码：</label></th>
                                                        <td width="20%"><input name="buyPersonCerty" type="text"
                                                                               class="js_ps_certy js_ps"></td>

                                                        <th width="8%"><label class="hsTypeLabel">安置人关系：</label></th>
                                                        <td width="20%">
                                                            <input type="text" name="buyPersonOwnerRel"
                                                                   class="autocomplete"
                                                                   atOption="oh006.getPsRelOpt"
                                                                   atUrl="oh006.getPsRelUrl"></td>
                                                        <th><label>备注说明：</label></th>
                                                        <td><input type="text" name="chooseNote">
                                                        </td>

                                                    </tr>
                                                    <tr class="js_ps_more">
                                                        <th><label>现住址：</label></th>
                                                        <td><input type="text" class="js_live_addr js_ps autocomplete"
                                                                   name="newHsAddr" atOption="oh006.getAddrOptNow"
                                                                   atUrl="oh006.getAddrUrlNow"></td>
                                                        <th><label>户口所在地：</label></th>
                                                        <td><input type="text" class="js_family_addr js_ps"></td>
                                                        <th><label>工作单位：</label></th>
                                                        <td><input type="text" class="js_job_addr js_ps"></td>
                                                        <td colspan="2">
                                            <span class="link marr5 js_doc_info" docTypeName="身份证"
                                                  onclick="oh006.showDoc(this)">
                                                 <input type="hidden" class="js_ps_certy_doc js_ps" value="">
                                                 <label class="cursorpt">身份证</label>
                                            </span>
                                                            <span class="link marr5 js_doc_info" docTypeName="选房关系证明"
                                                                  onclick="oh006.showDoc(this)">
                                                 <input type="hidden" name="buyPsOwnerRelDocIds" value="">
                                                 <label class="cursorpt">关系证明</label>
                                            </span>
                                                        </td>

                                                    </tr>
                                                    <tr class="js_ps_more">
                                                        <th><label>通讯地址：</label></th>
                                                        <td colspan="3"><input type="text" ${writeTxRht}
                                                                               name="personNoticeAddr"/>
                                                        </td>
                                                        <th width="8%"><label>联系电话：</label></th>
                                                        <td><input name="buyPersonTel" ${writeTxRht} type="text"
                                                                   class="js_ps_tel js_ps">
                                                        </td>
                                                        <td style="text-align: center; position:relative;">
                                                            <span class="link marr5"
                                                                  onclick="oh006.saveNoticeInfo(this)">
                                                            保存通讯
                                                        </span>
                                                            <%--<span class="link marr5" ${displayXfRht}--%>
                                                                  <%--onclick="oh006.addLivePs(this)">--%>
                                                            <%--+居住人--%>
                                                        <%--</span>--%>
                                                            <span class="link marr5 cursorpt"
                                                                  onclick="oh006.changeWt(this)">
                                                            <c:if test="${chooseHs.chooseHs.buyPsWtIds != null && chooseHs.chooseHs.buyPsWtIds != ''}">
                                                                取消委托
                                                            </c:if>
                                                            <c:if test="${chooseHs.chooseHs.buyPsWtIds == null || chooseHs.chooseHs.buyPsWtIds == ''}">
                                                                委托
                                                            </c:if>
                                                        </span>
                                                        </td>
                                                    </tr>

                                                    <tr class="hidden js_wt_info">
                                                        <th><label><strong>被委托人</strong>：</label></th>
                                                        <td>
                                                            <input type="hidden" class="js_ps_id" name="buyPsWtIds">
                                                            <input type="text" atOption="oh006.getOption"
                                                                   atUrl="oh006.getUrl"
                                                                   class="js_ps_name js_ps autocomplete">
                                                        </td>
                                                        <th><label>身份证号码：</label></th>
                                                        <td><input type="text" class="js_ps_certy js_ps"></td>
                                                        <th><label>联系电话：</label></th>
                                                        <td><input type="text" ${writeTxRht} class="js_ps_tel js_ps">
                                                        </td>
                                                        <th><label class="hsTypeLabel">购房人关系：</label></th>
                                                        <td>
                                                            <input type="text" name="buyPsWtOwnerRel"
                                                                   style="width: 30%;"
                                                                   class="autocomplete" atOption="oh006.getPsRelOpt"
                                                                   atUrl="oh006.getPsRelUrl">
                                                            <span class="link marr5 js_doc_info" docTypeName="身份证"
                                                                  onclick="oh006.showDoc(this)">
                                                 <input type="hidden" class="js_ps_certy_doc js_ps" value="">
                                                 <label class="cursorpt">身份证</label>
                                            </span>
                                                            <span class="marr5 link js_doc_info cursorpt"
                                                                  onclick="oh006.showDoc(this)">委托证明
                                                    <input type="hidden" name="buyPsWtDocIds">
                                            </span>
                                                        </td>
                                                    </tr>
                                                    <tr class="hidden js_gtjz_info">
                                                        <th><label><strong>居住人</strong>：</label></th>
                                                        <td>
                                                            <input type="hidden" class="js_ps_id" name="gtJzPsId">
                                                            <input type="text" class="js_ps_name js_ps autocomplete"
                                                                   atOption="oh006.getOption" atUrl="oh006.getUrl">
                                                        </td>
                                                        <th><label>身份证号码：</label></th>
                                                        <td><input type="text" class="js_ps_certy js_ps"></td>
                                                        <th><label>联系电话：</label></th>
                                                        <td><input type="text" ${writeTxRht} class="js_ps_tel js_ps">
                                                        </td>
                                                        <th><label class="hsTypeLabel">购房人关系：</label></th>
                                                        <td>
                                                            <input style="width: 30%;" type="text" class="autocomplete"
                                                                   atOption="oh006.getPsRelOpt"
                                                                   atUrl="oh006.getPsRelUrl"
                                                                   name="gtJzPsOwnerRel">
                                                            <span class="link marr5 js_doc_info" docTypeName="身份证"
                                                                  onclick="oh006.showDoc(this)">
                                                <input type="hidden" class="js_ps_certy_doc js_ps"
                                                       value="">
                                                 <label class="cursorpt">身份证</label>
                                            </span>
                                                            <span class="marl5 link js_live_span cursorpt" ${displayXfRht}
                                                                  onclick="oh006.delLivePs(this);">删除</span>
                                                        </td>
                                                    </tr>
                                                </table>
                                                <span class='removeX' style='right: 4px; top:4px;${deleteXfRht}'
                                                      onclick='oh006.delNewHs(this);'>X</span>
                                            </div>
                                            <c:forEach items="${hxListInfo}" var="chooseHs">
                                                <%--户型对应的购房信息循环开始--%>
                                                <div class="js_hs_buy_info" style="position: relative;"
                                                     flag="${chooseHs.chooseHs.huJush_Name}">
                                                    <table class="border" width="100%">
                                                        <tr style="background-color: #EEF1F5">
                                                            <th>购房区域：</th>
                                                            <td><input type="text" name="chooseHsRegName"
                                                                       class="autocomplete"
                                                                       atOption="oh006.getRegOpt"
                                                                       atUrl="oh006.getRegUrl"
                                                                       value="${chooseHs.chooseHs.chooseHsRegName}"/>
                                                            </td>
                                                            <th>购房地址：</th>
                                                            <td colspan="3">
                                                                <input type="hidden" name="hsAddr" value="${chooseHs.chooseHs.chooseHsAddr}"/>
                                                                <input type="hidden" name="hiddenRegId"
                                                                       value="${chooseHs.chooseHs.chooseHsRegId}"/>
                                                                <input type="hidden" name="hsHxTp"
                                                                       value="${chooseHs.chooseHs.huJush}"/>
                                                                <input type="hidden" name="hsHxName" class="js_hsHxName"
                                                                       value="${chooseHs.chooseHs.chooseHsHxName}"/>
                                                                <input type="hidden" name="preBldSize"
                                                                       class="js_preBldSize"
                                                                       value="${chooseHs.chooseHs.chooseHsSize}"/>
                                                                <input type="hidden" name="newHsId" class="js_newHsId"
                                                                       value="${chooseHs.chooseHs.chooseHsId}"/>
                                                                <input type="hidden" name="oldNewHsId"
                                                                       class="js_oldNewHsId"
                                                                       value="${chooseHs.chooseHs.chooseHsId}"/>
                                                                    ${chooseHs.chooseHs.chooseHsAddr}
                                                            </td>
                                                            <th>购房状态：</th>
                                                            <td><oframe:name prjCd="${param.prjCd}"
                                                                             itemCd="CHOOSE_STATUS"
                                                                             value="${chooseHs.chooseHs.chooseStatus}"/></td>
                                                        </tr>
                                                        <tr>
                                                            <th width="8%"><label><span
                                                                    style="font-weight: bold;">购房人</span>：</label>
                                                            </th>
                                                            <td>
                                                                <input type="hidden" name="hsCtChooseId"
                                                                       value="${chooseHs.chooseHs.hsCtChooseId}"/>
                                                                <input type="hidden" name="gtJzPsIds"
                                                                       value="${chooseHs.chooseHs.gtJzPsIds}">
                                                                <input type="hidden" name="gtJzPsOwnerRels"
                                                                       value="${chooseHs.chooseHs.gtJzPsOwnerRels}">
                                                                <input type="hidden" class="js_ps_id js_ps"
                                                                       name="buyPersonId"
                                                                       value="${chooseHs.chooseHs.buyPersonId}">
                                                                <c:set var="buyPersonId"
                                                                       value="${chooseHs.chooseHs.buyPersonId}${''}"/>
                                                                <c:set var="buyPerson"
                                                                       value="${personsData[buyPersonId]}"/>
                                                                <input type="hidden" name="huJush"
                                                                       value="${hsJush.key}">
                                                                <input type="hidden" name="chooseHsRegId"
                                                                       value="${chooseHs.chooseHs.chooseHsRegId}">
                                                                <input type="text" name="buyPersonName"
                                                                       class="js_ps_name js_ps autocomplete ctRequired"
                                                                       atOption="oh006.getOption" atUrl="oh006.getUrl"
                                                                       value="${buyPerson.Row.personName}">
                                                            </td>
                                                            <th width="8%"><label>身份证号码：</label>
                                                            </th>
                                                            <td width="20%"><input name="buyPersonCerty" type="text"
                                                                                   class="js_ps_certy js_ps"
                                                                                   value="${buyPerson.Row.personCertyNum}">
                                                            </td>
                                                            <th width="8%"><label class="hsTypeLabel">安置人关系：</label>
                                                            </th>
                                                            <td width="20%">
                                                                <input name="buyPersonOwnerRel"
                                                                       type="text"
                                                                       class="autocomplete"
                                                                       atOption="oh006.getPsRelOpt"
                                                                       atUrl="oh006.getPsRelUrl"
                                                                       value="${chooseHs.chooseHs.buyPersonOwnerRel}">
                                                            </td>
                                                            <th><label>备注说明：</label></th>
                                                            <td>
                                                                <input type="text" name="chooseNote"
                                                                       value="${chooseHs.chooseHs.chooseNote}">
                                                            </td>
                                                        </tr>
                                                        <tr class="js_ps_more">
                                                            <th><label>现住址：</label></th>
                                                            <td><input type="text"
                                                                       class="js_live_addr js_ps autocomplete"
                                                                       name="newHsAddr" atOption="oh006.getAddrOptNow"
                                                                       atUrl="oh006.getAddrUrlNow"
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
                                                            <td colspan="2"
                                                                style="text-align: center; position:relative;">
                                                            <span class="link marr5 js_doc_info" docTypeName="身份证"
                                                                  onclick="oh006.showDoc(this)">
                                                               <input type="hidden" class="js_ps_certy_doc js_ps"
                                                                      value="${buyPerson.Row.personCertyDocIds}">
                                                               <label class="cursorpt">身份证</label>
                                                          </span>
                                                                <span class="link marr5 js_doc_info"
                                                                      docTypeName="选房关系证明"
                                                                      onclick="oh006.showDoc(this)">
                                                              <input type="hidden" name="buyPsOwnerRelDocIds"
                                                                     value="${chooseHs.chooseHs.buyPsOwnerRelDocIds}">
                                                               <label class="cursorpt">关系证明</label>
                                                          </span>
                                                            </td>
                                                        </tr>
                                                        <tr class="js_ps_more">
                                                            <th><label>通讯地址：</label></th>
                                                            <td colspan="3"><input type="text" ${writeTxRht}
                                                                                   name="personNoticeAddr"
                                                                                   value="${chooseHs.chooseHs.personNoticeAddr}"/>
                                                            </td>
                                                            <th><label>联系电话：</label></th>
                                                            <td><input name="buyPersonTel" type="text" ${writeTxRht}
                                                                       class="js_ps_tel js_ps"
                                                                       value="${chooseHs.chooseHs.buyPersonTel}">
                                                            </td>
                                                            <td colspan="2"
                                                                style="text-align: center; position:relative;">
                                                                <c:if test="${chooseHs.chooseHs.chooseStatus=='1'}">
                                                                <span class="link marr5"
                                                                      onclick="oh006.saveNoticeInfo(this)">
                                                            保存通讯
                                                        </span>
                                                                </c:if>
                                                                <%--<span class="link marr5" ${displayXfRht}--%>
                                                                      <%--onclick="oh006.addLivePs(this)">--%>
                                                            <%--+居住人--%>
                                                        <%--</span>--%>
                                                                <span class="link marr5 cursorpt"
                                                                      onclick="oh006.changeWt(this)">
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
                                                            <th><label><span
                                                                    style="font-weight: bold;">被委托人</span>：</label>
                                                            </th>
                                                            <td>
                                                                <input type="hidden" class="js_ps_id" name="buyPsWtIds"
                                                                       value="${chooseHs.chooseHs.buyPsWtIds}">
                                                                <c:set var="buyWtPersonId"
                                                                       value="${chooseHs.chooseHs.buyPsWtIds}${''}"/>
                                                                <c:set var="buyWtPerson"
                                                                       value="${personsData[buyWtPersonId]}"/>
                                                                <input type="text" value="${buyWtPerson.Row.personName}"
                                                                       class="js_ps_name js_ps">
                                                            </td>
                                                            <th><label>身份证号码：</label></th>
                                                            <td><input type="text" class="js_ps_certy js_ps"
                                                                       value="${buyWtPerson.Row.personCertyNum}">
                                                            </td>
                                                            <th><label>联系电话：</label></th>
                                                            <td><input type="text" class="js_ps_tel js_ps" ${writeTxRht}
                                                                       value="${buyWtPerson.Row.personTelphone}">
                                                            </td>
                                                            <th><label class="hsTypeLabel">购房人关系：</label></th>
                                                            <td>
                                                                <input style="width: 30%;" type="text"
                                                                       name="buyPsWtOwnerRel"
                                                                       class="autocomplete"
                                                                       atOption="oh006.getPsRelOpt"
                                                                       atUrl="oh006.getPsRelUrl"
                                                                       value="${chooseHs.chooseHs.buyPsWtOwnerRel}">
                                                                <span class="link marr5 js_doc_info" docTypeName="身份证"
                                                                      onclick="oh006.showDoc(this)">
                                                        <input type="hidden" class="js_ps_certy_doc js_ps"
                                                               value="${buyWtPerson.Row.personCertyDocIds}">
                                                         <label class="cursorpt">身份证</label>
                                                    </span>
                                                                <span class="marl5 link js_doc_info cursorpt"
                                                                      onclick="oh006.showDoc(this)">委托证明
                                                          <input type="hidden" name="buyPsWtDocIds"
                                                                 value="${chooseHs.chooseHs.buyPsWtDocIds}">
                                                    </span>
                                                            </td>
                                                        </tr>
                                                        <c:set var="gtJzPsIds"
                                                               value="${chooseHs.chooseHs.gtJzPsIds}${''}"/>
                                                        <c:set value="${ fn:split(gtJzPsIds, ',') }" var="gtJzPsIdArr"/>
                                                        <c:set var="gtJzPsOwnerRels"
                                                               value="${chooseHs.chooseHs.gtJzPsOwnerRels}${''}"/>
                                                        <c:set value="${ fn:split(gtJzPsOwnerRels, ',') }"
                                                               var="gtJzPsOwnerRelArr"/>
                                                        <c:forEach items="${gtJzPsIdArr}" var="jzPsId"
                                                                   varStatus="varStatus">
                                                            <c:set var="gtjzClass" value="hidden"/>
                                                            <c:if test="${jzPsId != null && jzPsId != ''}">
                                                                <c:set var="gtjzClass" value=""/>
                                                            </c:if>
                                                            <tr class="js_gtjz_info ${gtjzClass}">
                                                                <th><label><span
                                                                        style="font-weight: bold;">共同居住人</span>:</label>
                                                                </th>
                                                                <td>
                                                                    <input type="hidden" class="js_ps_id"
                                                                           name="gtJzPsId"
                                                                           value="${jzPsId}">
                                                                    <c:set var="gtJzPerson"
                                                                           value="${personsData[jzPsId]}"/>
                                                                    <input type="text"
                                                                           atOption="oh006.getOption"
                                                                           atUrl="oh006.getUrl"
                                                                           class="js_ps_name js_ps autocomplete"
                                                                           value="${gtJzPerson.Row.personName}">
                                                                </td>
                                                                <th><label>身份证号码：</label></th>
                                                                <td><input type="text" class="js_ps_certy js_ps"
                                                                           value="${gtJzPerson.Row.personCertyNum}">
                                                                </td>
                                                                <th width="10%"><label>联系电话：</label></th>
                                                                <td><input type="text"
                                                                           class="js_ps_tel js_ps" ${writeTxRht}
                                                                           value="${gtJzPerson.Row.personTelphone}">
                                                                </td>
                                                                <th width="10%"><label
                                                                        class="hsTypeLabel">购房人关系：</label></th>
                                                                <td>
                                                                    <input style="width: 30%;" type="text"
                                                                           name="gtJzPsOwnerRel"
                                                                           class="autocomplete"
                                                                           atOption="oh006.getPsRelOpt"
                                                                           atUrl="oh006.getPsRelUrl"
                                                                           value="${gtJzPsOwnerRelArr[varStatus.index]}">
                                                                    <span class="link marr5 js_doc_info"
                                                                          docTypeName="身份证"
                                                                          onclick="oh006.showDoc(this)">
                                                                <input type="hidden" class="js_ps_certy_doc js_ps"
                                                                       value="${gtJzPerson.Row.personCertyDocIds}">
                                                                 <label class="cursorpt">身份证</label>
                                                            </span>
                                                                    <span class="marl5 link js_live_span cursorpt" ${displayXfRht}
                                                                          onclick="oh006.delLivePs(this);">删除</span>
                                                                </td>
                                                            </tr>
                                                        </c:forEach>
                                                    </table>
                                                    <span class='removeX' style='right: 4px; top:4px;${deleteXfRht}'
                                                          onclick='oh006.delNewHs(this);'>X</span>
                                                </div>
                                                <%--户型对应的购房信息循环结束--%>
                                            </c:forEach>
                                        </td>

                                    </tr>
                                </table>
                            </c:if>
                        </c:forEach>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
<script>
    $(function () {
        //绑定人员信息
//        $("#oh00601ChooseHsInfo", navTab.getCurrentPanel()).delegate("input.js_ps", "blur", function () {
//            oh006.syncPersons(this);
//        });
        $("#oh00601ChooseHsInfo input:not(input[name=buyPersonTel],input[name=personNoticeAddr])").attr("readonly", ${writeXfRht});
    })
</script>
