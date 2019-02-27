<%--院落信息基本信息面板--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="s" uri="http://java.sun.com/jsp/jstl/core" %>
<style type="text/css">
    .addrInput {
        width: 20%;
        border: 0;
        border-bottom: 1px solid #000000;
    }

    .addrTailInput {
        width: 8%;
        border: 0;
        border-bottom: 1px solid #000000;
    }

    .isEdit {
        color: red !important;
    }

    .js_doc_info {
        position: relative;
    }

    .js_browser {
        position: absolute;
        top: 20px;
        left: 0px;
        font-size: 14px !important;
        font-weight: bold !important;
        color: blue !important;
    }

    tr.isAddPs {
        background-color: #ff919d !important;
    }

    tr.isDelPs {
        background-color: #727272 !important;
    }
</style>
<div>
    <form id="ph00103_inputFrm" method="post" class="required-validate entermode">
        <input type="hidden" name="hsId" value="${baseBean.HouseInfo.hsId}">
        <input type="hidden" name="hsStatus" value="${baseBean.HouseInfo.hsStatus}">
        <input type="hidden" name="errMsg" value="${errMsg}">
        <input type="hidden" name="saveHsPbType" value="">
        <div>
            <table class="border js_hs_base_info">
                <tr>
                    <th><label>归属门牌：</label></th>
                    <td colspan="3">
                        <span class="js_showResult">
                            <span class="js_reg_build_3" style="position: relative;">
                                <oframe:input type="text" name="buildAddr"
                                              style="width: 50%;" readonly="readonly"
                                              placeholder="点击选择房屋对应的门牌位置"
                                              title="${baseBean.HouseInfo.hsFullAddr}"
                                              onclick="ph00301_input.initAddrRegTree(this);"
                                              cssClass="required js_required js_editAddr addrInput"
                                              oldValue="${addrPathStr_old}"
                                              value="${addrPathStr}"/>
                                &nbsp;-&nbsp;
                                <span class="js_reg_build_2 hidden" readonly>
                                    <oframe:input type="text" name="buldUnitNum"
                                                  cssClass="js_editAddr addrTailInput" placeholder="单元号"
                                                  oldValue="${baseBean.HouseInfo.buldUnitNum_old}"
                                                  value="${baseBean.HouseInfo.buldUnitNum}"/> -
                                </span>
                                    <oframe:input type="text" name="hsAddrTail"
                                                  onblur="ph00301_input.showAddr(this);"
                                                  cssClass="js_editAddr addrTailInput"
                                                  placeholder="房号" title="具体房号，没有可忽略"
                                                  oldValue="${baseBean.HouseInfo.hsAddrTail_old}"
                                                  value="${baseBean.HouseInfo.hsAddrTail}"/>
                            </span>
                            <span>
                                (用于生成档案编号)
                            </span>
                        </span>
                    </td>
                    <th><label>房屋类型：</label></th>
                    <td colspan="3">
                        <input type="hidden" class="js_db_hsType" value="${baseBean.HouseInfo.hsType}"/>
                        <oframe:select prjCd="${param.prjCd}" itemCd="HS_TYPE" name="hsType"
                                       cssClass="js_editAddr" onChange="ph00301_input.onBuildTypeChange()"
                                       oldValue="${baseBean.HouseInfo.hsType_old}"
                                       value="${baseBean.HouseInfo.hsType}"/>
                    </td>
                </tr>
                <tr>
                    <th><label>房屋地址：</label></th>
                    <td colspan="3">
                        <oframe:input cssClass="pull-left js_showFullAddr"
                                      type="text" name="hsFullAddr" style="width: 70%;"
                                      oldValue="${baseBean.HouseInfo.hsFullAddr_old}"
                                      value="${baseBean.HouseInfo.hsFullAddr}"/>
                        <c:if test="${procReadonly}">
                            <a title="按地址检索房屋" class="btnLook" style="float: left;"
                               onclick="ph00301_input.queryBuildDetail(this);">选择</a>
                        </c:if>
                        <c:if test="${procReadonly}">
                            <span class="link marl10" style="line-height: 29px;" onclick="ph00301_input.setHsPos();">房屋标注</span>（房本地址）
                        </c:if>
                        <%--隐藏的区域数据、建筑数据--%>
                        <input type="hidden" name="buildId" value="${baseBean.HouseInfo.buildId}">
                        <input type="hidden" name="ttUpRegId"
                               value='<oframe:entity  prjCd="${param.prjCd}" entityName="RegInfo" property="upRegId"
                               value="${houseInfo.HouseInfo.ttRegId}"/>'>
                        <input type="hidden" name="ttRegId" value="${houseInfo.HouseInfo.ttRegId}"/>
                        <input type="hidden" name="ttOrgId" value="${houseInfo.HouseInfo.ttOrgId}"/>
                        <input type="hidden" id="hsPosX" name="hsPosX" value="${baseBean.HouseInfo.hsPosX}"/>
                        <input type="hidden" id="hsPosY" name="hsPosY" value="${baseBean.HouseInfo.hsPosY}"/>
                        <input type="hidden" id="hsPoints" name="hsPoints" value="${baseBean.HouseInfo.hsRoomPos}"/>
                        <input type="hidden" id="hsSlfPoints" name="hsSlfPoints"
                               value="${baseBean.HouseInfo.hsSlfRoomPos}"/>
                    </td>
                    <th><label>档案编号：</label></th>
                    <td colspan="3">
                        <oframe:input type="text" onblur="ph00301_input.queryHsCd();"
                                      onclick="ph00301_input.onclickhsCd()"
                                      name="hsCd" placeholder="自动生成"
                                      oldValue="${baseBean.HouseInfo.hsCd_old}"
                                      value="${baseBean.HouseInfo.hsCd}"/>
                        <input type="hidden" name="history" value="">
                    </td>
                </tr>
                <tr>
                    <th><label>信息标记：</label></th>
                    <td colspan="5">
                        <oframe:select prjCd="${param.prjCd}" itemCd="COMMON_YES" type="checkbox" showLabel="问题户"
                                       name="isPbHs" id="isPbHs" onClick="ph00301_input.isPbHs()"
                                       oldValue="${dataStatus.OpResult.isPbHs_old}"
                                       value="${dataStatus.OpResult.isPbHs}"/>
                        <oframe:select prjCd="${param.prjCd}" type="checkbox" id="hsFlag" name="hsFlag" itemCd="HS_FLAG"
                                       oldSltValues="${hsFlag_old}" sltValues="${hsFlag}"/>
                    </td>
                </tr>
                <tr>
                    <th width="10%"><label>拆迁公司：</label></th>
                    <td width="25%">
                        <input type="hidden" class="js_db_ttCompanyId" value="${hsTTBean.HouseInfo.ttCompanyId}"/>
                        <oframe:select prjCd="${param.prjCd}" collection="${cmpMap}" name="housettCompanyId"

                                       oldValue="${hsTTBean.HouseInfo.ttCompanyId_old}"
                                       value="${hsTTBean.HouseInfo.ttCompanyId}"/>
                    </td>
                    <th width="10%"><label>主谈人员：</label></th>
                    <td width="20%">
                        <input type="hidden" class="js_db_housettMainTalk" value="${hsTTBean.HouseInfo.ttMainTalk}"/>
                        <oframe:input type="text" name="housettMainTalk"
                                      oldValue="${hsTTBean.HouseInfo.ttMainTalk_old}"
                                      value="${hsTTBean.HouseInfo.ttMainTalk}"/>
                    </td>
                    <th width="10%"><label>副谈人员：</label></th>
                    <td width="25%">
                        <input type="hidden" class="js_db_housettSecTalk" value="${hsTTBean.HouseInfo.ttSecTalk}"/>
                        <oframe:input type="text" name="housettSecTalk"
                                      oldValue="${hsTTBean.HouseInfo.ttSecTalk_old}"
                                      value="${hsTTBean.HouseInfo.ttSecTalk}"/>
                    </td>
                </tr>
            </table>
        </div>
        <c:if test="${dataStatus.OpResult.isPbHs!='1'}">
            <c:set var="isPbDis" value="none"/>
        </c:if>
        <div class="panel mar5 js_pb_hs" style="margin: 5px 0 0 0;display: ${isPbDis}">
            <h1>问题信息</h1>

            <div class="js_panel_context">
                <table class="border">
                    <tr>
                        <th width="10%"><label>问题分类：</label></th>
                        <td>
                            <oframe:select prjCd="${param.prjCd}" type="checkbox" name="hsPbType" itemCd="HS_PB_TYPE"
                                           oldSltValues="${hsPbType_old}" id="hsPbType"
                                           sltValues="${hsPbType}"/>
                        </td>
                    </tr>
                    <tr>
                        <th><label>问题程度：</label></th>
                        <td>
                            <oframe:select prjCd="${param.prjCd}" type="select" name="pbLevel"
                                           oldValue="${dataStatus.OpResult.pbLevel_old}"
                                           value="${dataStatus.OpResult.pbLevel}"
                                           itemCd="HS_PB_LEVEL"/>
                        </td>
                    </tr>
                    <tr>
                        <th><label>问题详情：</label></th>
                        <td>
                            <oframe:textarea rows="5" name="pbDetail" style="width: 80%;"
                                             oldValue="${dataStatus.OpResult.pbDetail_old}"
                                             value="${dataStatus.OpResult.pbDetail}"/>
                        </td>
                    </tr>
                    <tr>
                        <th><label>谈户方案：</label></th>
                        <td>
                            <oframe:textarea rows="5" name="pbPlan" style="width: 80%;"
                                             oldValue="${dataStatus.OpResult.pbPlan_old}"
                                             value="${dataStatus.OpResult.pbPlan}"/>
                        </td>
                    </tr>
                </table>
            </div>
        </div>

        <%--院落信息--%>
        <c:set var="hideBuildInfo" value="${prjType == '1'? '': 'hidden'}"/>
        <div class="js_build_info ${hideBuildInfo}">
            <c:if test="${baseBean.HouseInfo.buildId != null && baseBean.HouseInfo.buildId != ''}">
                <jsp:include
                        page="/eland/ph/ph003/ph00301-initBuild.gv?buildId=${baseBean.HouseInfo.buildId}&procReadonly=${procReadonly}"/>
            </c:if>
        </div>
        <%--产权信息--%>
        <div class="panel mart5">
            <h1>产权信息</h1>
            <%--面板内容--%>
            <div class="js_panel_context">
                <table class="border" id="ownerTable" width="100%">
                    <tr>
                        <th width="10%"><label>产权性质：</label></th>
                        <td><oframe:select prjCd="${param.prjCd}" itemCd="HS_OWNER_TYPE" name="hsOwnerType"
                                           cssClass="ctRequired"
                                           onChange="ph00301_input.onHsOwnerTypeChange()"
                                           oldValue="${ownerInfo.HouseInfo.hsOwnerType_old}"
                                           value="${ownerInfo.HouseInfo.hsOwnerType}"/></td>
                        <th width="10%"><label>使用面积：</label></th>
                        <td>
                            <oframe:input type="text" name="hsUseSize"
                                          oldValue="${ownerInfo.HouseInfo.hsUseSize_old}"
                                          value="${ownerInfo.HouseInfo.hsUseSize}"
                                          onchange="ph00301_input.changeUseSize()"/>
                        </td>
                        <th width="10%"><label>建筑面积：</label></th>
                        <td><oframe:input type="text" cssClass="number ctRequired"
                                          oldValue="${ownerInfo.HouseInfo.hsBuildSize_old}"
                                          value="${ownerInfo.HouseInfo.hsBuildSize}"
                                          name="hsBuildSize"/></td>
                    </tr>
                    <tr class="js_hsPubOwnerName">
                        <th><label>产权单位：</label></th>
                        <td colspan="3">
                            <oframe:input type="text" name="hsPubOwnerName"
                                          oldValue="${ownerInfo.HouseInfo.hsPubOwnerName_old}"
                                          value="${ownerInfo.HouseInfo.hsPubOwnerName}"/>
                        </td>
                        <th><label>房改售房：</label></th>
                        <td>
                            <oframe:select itemCd="FINISH_RESULT" value="${houseInfo.HouseInfo.transferStatus}"
                                           name="transferStatus"/>
                            <span class="js_doc_info link cursorpt " docTypeName="房改售房附件" editAble="${editAble}"
                                  onclick="ph00301_input.showDoc(this)">
                                <input type="hidden" name="transferDocs" value='${houseInfo.HouseInfo.transferDocs}'>
                                <label style="cursor:pointer;">附件</label>
                            </span>
                        </td>
                    </tr>
                    <tr>
                        <th><label class="hsTypeLabel ctRequired"><span style="font-weight: bold;">产权人：</span></label>
                        </th>
                        <td>
                            <input type="hidden" class="js_ps_id" value="">
                            <input type="text" class="autocomplete js_ps_name js_owner_display required"
                                   atOption="ph00301_input.getOwnerPsOpt"
                                   onblur="ph00301_input.syncOwnerInfo()"
                                   value='' name="hsOwnerPersons">
                            <span class="link" ownerModel="0" onclick="ph00301_input.switchModel('1');">高级</span>
                        </td>
                        <th><label>证件号码：</label></th>
                        <td>
                            <input type="text" class="ctRequired js_ps js_ps_certy js_owner_display needDoc"
                                   onblur="ph00301_input.syncOwnerInfo()" value=''>
                            <span class="link marr5 js_doc_info" docTypeName="身份证" editAble="${editAble}"
                                  onclick="ph00301_input.showAndSyncDoc(this)">证件
                                <input type="hidden" class="js_ps_certy_doc js_ps js_owner_display" value="">
                            </span>
                        </td>
                        <th><label>联系电话：</label></th>
                        <td><input type="text" class="js_ps_tel js_owner_display js_ps ctRequired"
                                   onblur="ph00301_input.syncOwnerInfo()" value=''>
                        </td>
                    </tr>
                    <tr>
                        <th><label class="hsOwnLabel">产权证号：</label></th>
                        <td>
                            <input type="text" class="ctRequired js_owner_certy js_owner_display needDoc" value="">
                            <span class="link marr5 js_doc_info" docTypeName="产权证" editAble="${editAble}"
                                  onclick="ph00301_input.showAndSyncDoc(this)">产权证
                                  <input type="hidden" class="js_owner_certy_doc js_owner_display" value="">
                            </span>
                        </td>
                        <th><label>安置意向：</label></th>
                        <td>
                            <oframe:select prjCd="${param.prjCd}" itemCd="10004" name="isAgree" cssClass="ctRequired"
                                           id="isAgree" oldValue="${ctInfo.HsCtInfo.isAgree_old}"
                                           value="${ctInfo.HsCtInfo.isAgree}"/>
                        </td>
                        <th><label>安置方式：</label></th>
                        <td>
                            <oframe:select prjCd="${param.prjCd}" itemCd="10001"
                                           name="ctType" cssClass="ctRequired"
                                           id="ctType" oldValue="${ctInfo.HsCtInfo.ctType_old}"
                                           value="${ctInfo.HsCtInfo.ctType}"/>
                        </td>
                        <input type="hidden" name="hsCtId" value="${ctInfo.HsCtInfo.hsCtId}"/>
                    </tr>
                    <tr>
                        <th><label class="hsTypeLabel">正式房间数：</label></th>
                        <td>
                            <oframe:input type="text" name="hsRoomNum"
                                          oldValue="${baseBean.HouseInfo.hsRoomNum_old}"
                                          value="${baseBean.HouseInfo.hsRoomNum}"/>
                        </td>
                        <th><label>正式房用途：</label></th>
                        <td>
                            <oframe:select prjCd="${param.prjCd}" itemCd="HS_USE_TYPE" name="hsUseType"
                                           id="hsUseType"
                                           oldValue="${baseBean.HouseInfo.hsUseType_old}"
                                           value="${baseBean.HouseInfo.hsUseType}"/>
                        </td>
                        <th><label>正式房朝向：</label></th>
                        <td>
                            <oframe:select prjCd="${param.prjCd}" itemCd="OLD_HS_CX" name="hsDt"
                                           id="hsDt"
                                           oldValue="${baseBean.HouseInfo.hsDt_old}"
                                           value="${baseBean.HouseInfo.hsDt}"/>
                        </td>
                    </tr>
                    <tr>
                        <th><label class="hsTypeLabel">自建房间数：</label></th>
                        <td>
                            <oframe:input type="text" name="hsSlfRoom"
                                          oldValue="${baseBean.HouseInfo.hsSlfRoom_old}"
                                          value="${baseBean.HouseInfo.hsSlfRoom}"/>
                        </td>
                        <th><label>自建房用途：</label></th>
                        <td>
                            <oframe:select prjCd="${param.prjCd}" itemCd="HS_USE_TYPE" name="hsSlfUse" id="hsSlfUse"
                                           oldValue="${baseBean.HouseInfo.hsSlfUse_old}"
                                           value="${baseBean.HouseInfo.hsSlfUse}"/>
                        </td>
                        <th><label class="hsTypeLabel">土地证号：</label></th>
                        <td>
                            <oframe:input type="text" name="landCertyNum"
                                          oldValue="${ownerInfo.HouseInfo.landCertyNum_old}"
                                          value="${ownerInfo.HouseInfo.landCertyNum}"/>
                        </td>
                    </tr>
                    <tr style="height: 60px;">
                        <th><label>备注说明：</label></th>
                        <td colspan="5">
                            <oframe:textarea name="hsOwnerNote" rows="5"
                                             style="height: 100%; width: 80%;"
                                             oldValue="${ownerInfo.HouseInfo.hsOwnerNote_old}"
                                             value="${ownerInfo.HouseInfo.hsOwnerNote}"/>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
        <%--产权信息高级录入--%>
        <div class="panel hidden mart5" id="advanceOwnerDiv">
            <h1>产权信息高级录入
                <span class="panel_menu link js_reload" onclick="ph00301_input.switchModel('0');">隐藏</span>
            </h1>
            <%--面板内容--%>
            <div class="js_panel_context">
                <table class="border" width="100%">
                    <tr>
                        <th width="15%"><label>产权分配方式：</label></th>
                        <td>
                            <oframe:select prjCd="${param.prjCd}" itemCd="HS_OWN_MODEL" name="hsOwnerModel"
                                           cssClass="ctRequired"
                                           onChange="ph00301_input.switchOwnerType();"
                                           value="${ownerInfo.HouseInfo.hsOwnerModel}"/>
                            <button type="button" id="addOwnerBtn" class="btn btn-primary marl20"
                                    onclick="ph00301_input.addOwner()">添加产权人
                            </button>
                        </td>
                    </tr>
                    <tr>
                        <th class="subTitle" colspan="2" style="text-align: left;"><label>产权信息</label></th>
                    </tr>
                    <tr>
                        <td colspan="2">
                            <c:forEach items="${hsOwnersList}" var="owner">
                                <div class="mar10 js_owner_div" style="position: relative;">
                                    <table class="border">
                                        <c:set var="wtClass" value=""/>
                                        <c:set var="notWtClass" value="hidden"/>
                                            <%--将后台传递的产权人 与受托人id组成map 取出--%>
                                        <c:set var="ctPsWtIdStr" value="${owner.HsOwner.ownerPsId}${''}"/>
                                            <%--判断每个产权人对应的委托人是否存在，用以显示被托人一栏--%>
                                        <c:if test="${ctPsWtMap[ctPsWtIdStr] != null && ctPsWtMap[ctPsWtIdStr] != ''}">
                                            <c:set var="wtClass" value="hidden"/>
                                            <c:set var="notWtClass" value=""/>
                                        </c:if>
                                            <%--产权人--%>
                                        <c:set var="ownerPsId" value="${owner.HsOwner.ownerPsId}${''}"/>
                                        <c:set var="ownerPerson" value="${personsData[ownerPsId]}"/>
                                            <%--委托人--%>
                                        <c:set var="wtPsId" value="${ctPsWtMap[ctPsWtIdStr]}"/>
                                        <c:set var="wtPerson" value="${personsData[wtPsId]}"/>

                                        <tr>
                                            <th><label class="hsTypeLabel ctRequired"><span
                                                    style="font-weight: bold;">产权人</span>姓名：</label>
                                            </th>
                                            <td>
                                                <input type="hidden" name="ownerPsId" class="js_ps_id"
                                                       value="${owner.HsOwner.ownerPsId}">
                                                <oframe:input type="text" cssClass="autocomplete js_ps_name ctRequired"
                                                              atOption="ph00301_input.getOwnerAdvPsOpt"
                                                              onblur="ph00301_input.syncOwnerDisp();"
                                                              name="ownerName"
                                                              oldValue='${ownerPerson.Row.personName_old}'
                                                              value='${ownerPerson.Row.personName}'/>
                                            </td>
                                            <th><label class="hsTypeLabel">身份证号：</label></th>
                                            <td>
                                                <oframe:input type="text" cssClass="js_ps_certy js_ps ctRequired"
                                                              onblur="ph00301_input.syncOwnerDisp();"
                                                              oldValue='${ownerPerson.Row.personCertyNum_old}'
                                                              value='${ownerPerson.Row.personCertyNum}'/>
                                                <span class="link marr5 js_doc_info" docTypeName="身份证"
                                                      editAble="${editAble}"
                                                      onclick="ph00301_input.showDoc(this)">身份证
                                                    <oframe:input type="hidden" cssClass="js_ps_certy_doc js_ps"
                                                                  oldValue="${ownerPerson.Row.personCertyDocIds_old}"
                                                                  value="${ownerPerson.Row.personCertyDocIds}"/>
                                                </span>
                                            </td>
                                            <th><label class="hsTypeLabel">联系电话：</label></th>
                                            <td>
                                                <oframe:input type="text" cssClass="js_ps_tel js_ps ctRequired"
                                                              onblur="ph00301_input.syncOwnerDisp();"
                                                              oldValue="${ownerPerson.Row.personTelphone_old}"
                                                              value="${ownerPerson.Row.personTelphone}"/>
                                                <span class="link ${wtClass} js_add_wt"
                                                      onclick="ph00301_input.addOwnerWt(this)">委托</span>
                                            </td>
                                        </tr>
                                        <tr class="${notWtClass}">
                                            <th><label><span style="font-weight: bold;">被托人</span>姓名：</label></th>
                                            <td>
                                                <input type="hidden" name="ctWtPsId" class="js_ps_id" value="${wtPsId}">
                                                <oframe:input type="text" cssClass="autocomplete js_ps_name needDoc"
                                                              atOption="ph00301_input.getOwnerWtPsOpt"
                                                              oldValue='${wtPerson.Row.personName}'
                                                              value='${wtPerson.Row.personName}'/>
                                                <span class="js_doc_info link" docTypeName="委托证明" editAble="${editAble}"
                                                      onclick="ph00301_input.showDoc(this)">
                                                    <oframe:input type="hidden" name="ctWtPsDocs"
                                                                  oldValue="${baseBean.HouseInfo.ctWtPsDocs_old}"
                                                                  value="${baseBean.HouseInfo.ctWtPsDocs}"/>
                                                    证明
                                                </span>
                                            </td>
                                            <th><label>身份证号：</label></th>
                                            <td><oframe:input type="text" cssClass="js_ps_certy js_ps needDoc"
                                                              oldValue='${wtPerson.Row.personCertyNum_old}'
                                                              value='${wtPerson.Row.personCertyNum}'/>
                                                <span class="link marr5 js_doc_info" docTypeName="身份证"
                                                      editAble="${editAble}"
                                                      onclick="ph00301_input.showDoc(this)">身份证
                                                    <oframe:input type="hidden" cssClass="js_ps_certy_doc js_ps"
                                                                  oldValue='${wtPerson.Row.personCertyDocIds_old}'
                                                                  value='${wtPerson.Row.personCertyDocIds}'/>
                                                </span>
                                            </td>
                                            <th><label>联系电话：</label></th>
                                            <td><oframe:input type="text" cssClass="js_ps_tel js_ps"
                                                              oldValue='${wtPerson.Row.personTelphone_old}'
                                                              value='${wtPerson.Row.personTelphone}'/>
                                                <span class="link" onclick="ph00301_input.delOwnerWt(this)">取消</span>
                                            </td>
                                        </tr>
                                        <tr>
                                            <th><label class="hsOwnLabel">产权证号：</label></th>
                                            <td><oframe:input type="text" cssClass="js_owner_certy ctRequired needDoc"
                                                              onblur="ph00301_input.syncOwnerDisp();"
                                                              oldValue="${owner.HsOwner.ownerCerty_old}"
                                                              value='${owner.HsOwner.ownerCerty}' name="ownerCerty"/>
                                                <span class="js_doc_info link needDoc" docTypeName="产权证"
                                                      editAble="${editAble}"
                                                      onclick="ph00301_input.showDoc(this)">
                                                    <oframe:input type="hidden" name="ownerCertyDocIds"
                                                                  cssClass="js_owner_certy_doc"
                                                                  oldValue="${owner.HsOwner.ownerCertyDocIds_old}"
                                                                  value="${owner.HsOwner.ownerCertyDocIds}"/>
                                                    <label style="cursor:pointer;">上传</label>
                                                </span>
                                            </td>
                                            <th><label>产权占比：</label></th>
                                            <td><oframe:input type="text" cssClass="js_ps_certy"
                                                              oldValue="${owner.HsOwner.ownerPrecent_old}"
                                                              value="${owner.HsOwner.ownerPrecent}"
                                                              name="ownerPrecent"/>
                                            </td>
                                            <th><label>产权备注：</label></th>
                                            <td><oframe:input type="text" cssClass="js_ps_tel" name="ownerNote"
                                                              oldValue="${owner.HsOwner.ownerNote_old}"
                                                              value="${owner.HsOwner.ownerNote}"/>
                                            </td>
                                        </tr>
                                    </table>
                                    <c:choose>
                                        <c:when test="${fn:length(hsOwnersList) > 0}">
                                            <span class="removeX"
                                                  onclick="ph00301_input.deleteOwner(this)">X</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="removeX" style="display: none"
                                                  onclick="ph00301_input.deleteOwner(this)">X</span>
                                        </c:otherwise>
                                    </c:choose>

                                </div>
                            </c:forEach>
                        </td>
                    </tr>
                </table>
            </div>
        </div>

        <%--户籍信息--%>
        <c:set var="psType1" value="0"/>
        <c:set var="psType2" value="0"/>
        <c:set var="psType3" value="0"/>
        <c:forEach items="${hsPersonBean}" var="hsPerson">
            <c:if test="${hsPerson.Person.personType == '1'}">
                <c:set var="psType1" value="${psType1+1}"/>
            </c:if>
            <c:if test="${hsPerson.Person.personType == '2'}">
                <c:set var="psType2" value="${psType2+1}"/>
            </c:if>
            <c:if test="${hsPerson.Person.personType == '3' || hsPerson.person.isLiveHere == '1'}">
                <c:set var="psType3" value="${psType3+1}"/>
            </c:if>
        </c:forEach>
        <div class="tabs mart5">
            <div class="tabsHeader">
                <div class="tabsHeaderContent">
                    <ul>
                        <li class="js_load_tab selected"
                            onclick="ph00301_input.loadTabContext(this);">
                            <a href="javascript:void(0);"><span>在册户籍（${psType1}人）</span></a>
                        </li>
                        <li class="js_load_tab"
                            onclick="ph00301_input.loadTabContext(this);">
                            <a href="javascript:void(0);"><span>非本址产承人户籍（${psType2}人）</span></a>
                        </li>
                        <li class="js_load_tab"
                            onclick="ph00301_input.loadTabContext(this);">
                            <a href="javascript:void(0);"><span>其他现场居住人（${psType3}人）</span></a>
                        </li>
                    </ul>
                </div>
            </div>
            <div class="tabsContent" style="padding-top: 0">
                <%--在册户籍--%>
                <div style="min-height: 200px">
                    <div class="js_panel_context">
                        <div style="width: 100%;height: 100%;">
                            <table class="border" width="100%">
                                <tbody>
                                <tr>
                                    <th width="10%">家庭结构：</th>
                                    <td>
                                        <oframe:input type="text" name="familyStructure"
                                                      placeholder="请输入家庭结构数"
                                                      oldValue="${houseInfo.HouseInfo.familyStructure_old}"
                                                      value="${houseInfo.HouseInfo.familyStructure}"/>
                                    </td>
                                    <th width="10%">总户籍数：</th>
                                    <td>
                                        <input type="text" readonly name="allFamNum"
                                               value="${houseInfo.HouseInfo.allFamNum}"/>
                                    </td>
                                    <th width="10%">户籍人口数：</th>
                                    <td>
                                        <input type="text" readonly name="famPsNum"
                                               value="${houseInfo.HouseInfo.famPsNum}"/>
                                    </td>
                                </tr>
                                </tbody>
                            </table>
                            <table class="list" id="familyTable_booklet" width="100%">
                                <thead>
                                <tr>
                                    <th width="10%">成员姓名</th>
                                    <th width="5%">户主关系</th>
                                    <th width="15%">证件号码</th>
                                    <th width="10%">联系电话</th>
                                    <th width="10%">工作单位</th>
                                    <th width="5%">年龄</th>
                                    <th width="5%">现场居住</th>
                                    <th width="5%">婚姻状况</th>
                                    <th width="18%"><span class="link js_reload"
                                                          onclick="ph00301_input.addFamily('familyTable_booklet',this)">新增整户</span>
                                    </th>
                                </tr>
                                </thead>
                                <tbody>
                                <tr class="hidden">
                                    <td>
                                        <input type="hidden" class="js_ps_id" value="">
                                        <input type="hidden" class="js_ps_family_ps_id js_ps" value="">
                                        <input type="hidden" class="js_ps_family_flag js_ps" value="">
                                        <input type="text" atOption="ph00301_input.getPsOpt"
                                               onblur="ph00301_input.calcFamNum();" class="autocomplete js_ps_name"
                                               value=""/>
                                    </td>
                                    <td><input type="text" class="js_ps_rel autocomplete js_ps" value=""
                                               atOption="ph00301_input.getPsRelOpt" atUrl="ph00301_input.getPsRelUrl"/>
                                    </td>
                                    <td><input type="text" class="js_ps_certy js_ps"
                                               onblur="ph00301_input.checkIdCalcAge(this)" value=""/></td>
                                    <td><input type="text" class="js_ps_tel js_ps" value=""/></td>
                                    <td><input type="text" class="js_ps_job js_ps" value=""/></td>
                                    <td><input type="text" class="js_ps_age js_ps" value=""/></td>
                                    <td><oframe:select prjCd="${param.prjCd}" itemCd="COMMON_YES_NO"
                                                       cssClass="js_ps_is_live js_ps"
                                    /></td>
                                    <td><oframe:select prjCd="${param.prjCd}" itemCd="MARRY_STATUS"
                                                       cssClass="js_ps_marry js_ps"
                                    /></td>
                                    <td>
                                        <span class="link marr5 js_family_rht"
                                              onclick="ph00301_input.deleteFamily(this);">-整户</span>
                                        <span class="link marr5 js_family_rht"
                                              onclick="ph00301_input.addPerson('familyTable_booklet',this);">+成员</span>
                                        <span class="link marr5 js_family_rht js_doc_info" docTypeName="户口本"
                                              editAble="${editAble}"
                                              onclick="ph00301_input.showDoc(this)">
                                            <input type="hidden" class="js_family_doc" value=""><label>户口本</label>
                                        </span>
                                        <span class="link marr5 js_mem_rht" onclick="ph00301_input.deletePerson(this);">-成员</span>
                                        <span class="link marr5 js_doc_info" docTypeName="身份证" editAble="${editAble}"
                                              onclick="ph00301_input.showDoc(this)">
                                            <input type="hidden" class="js_ps_certy_doc js_ps"
                                                   value=""><label>身份证</label>
                                        </span>
                                    </td>
                                </tr>
                                <c:forEach items="${hsPersonBean}" var="hsPerson">
                                    <c:set var="readRht" value=""/>
                                    <c:set var="autocomplete" value="autocomplete"/>
                                    <c:if test="${hsPerson.Person.familyOwnFlag == '1'}">
                                        <c:set var="readRht" value="readonly"/>
                                        <c:set var="autocomplete" value=""/>
                                    </c:if>
                                    <%-- 判断人员 执行操作 --%>
                                    <c:set var="psMethodClass" value=""/>
                                    <c:if test="${hsPerson.Person.method == '1'}">
                                        <c:set var="psMethodClass" value="isAddPs"/>
                                    </c:if>
                                    <c:if test="${hsPerson.Person.method == '3'}">
                                        <c:set var="psMethodClass" value="isDelPs"/>
                                    </c:if>
                                    <c:if test="${hsPerson.Person.personType == '1'}">
                                        <tr class="${psMethodClass}">
                                            <td>
                                                <input type="hidden" class="js_ps_id"
                                                       value="${hsPerson.Person.personId}">
                                                <input type="hidden" class="js_ps_family_ps_id js_ps"
                                                       value="${hsPerson.Person.familyPersonId}">
                                                <input type="hidden" class="js_ps_family_flag js_ps"
                                                       value="${hsPerson.Person.familyOwnFlag}">
                                                <oframe:input type="text" atOption="ph00301_input.getPsOpt"
                                                              onblur="ph00301_input.calcFamNum();"
                                                              cssClass="autocomplete js_ps_name"
                                                              oldValue="${hsPerson.Person.personName_old}"
                                                              value="${hsPerson.Person.personName}"/>
                                            </td>
                                            <td>
                                                <oframe:input type="text"
                                                              cssClass="js_ps_rel ${readRht} ${autocomplete} js_ps ${readRht}"
                                                              atOption="ph00301_input.getPsRelOpt"
                                                              atUrl="ph00301_input.getPsRelUrl"
                                                              readonly="${readRht}"
                                                              oldValue="${hsPerson.Person.familyPersonRel_old}"
                                                              value="${hsPerson.Person.familyPersonRel}"/>
                                            </td>
                                            <td><oframe:input type="text" cssClass="js_ps_certy js_ps"
                                                              onblur="ph00301_input.checkIdCalcAge(this)"
                                                              oldValue="${hsPerson.Person.personCertyNum_old}"
                                                              value="${hsPerson.Person.personCertyNum}"/></td>
                                            <td><oframe:input type="text" cssClass="js_ps_tel js_ps"
                                                              oldValue="${hsPerson.Person.personTelphone_old}"
                                                              value="${hsPerson.Person.personTelphone}"/></td>
                                            <td><oframe:input type="text" cssClass="js_ps_job js_ps"
                                                              oldValue="${hsPerson.Person.personJobAddr_old}"
                                                              value="${hsPerson.Person.personJobAddr}"/></td>
                                            <td><oframe:input type="text" cssClass="js_ps_age js_ps"
                                                              oldValue="${hsPerson.Person.personAge_old}"
                                                              value="${hsPerson.Person.personAge}"/></td>
                                            <td><oframe:select prjCd="${param.prjCd}" itemCd="COMMON_YES_NO"
                                                               cssClass="js_ps_is_live js_ps"

                                                               oldValue="${hsPerson.Person.isLiveHere_old}"
                                                               value="${hsPerson.Person.isLiveHere}"/></td>
                                            <td><oframe:select prjCd="${param.prjCd}" itemCd="MARRY_STATUS"
                                                               cssClass="js_ps_marry js_ps"

                                                               oldValue="${hsPerson.Person.marryStatus_old}"
                                                               value="${hsPerson.Person.marryStatus}"/></td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${hsPerson.Person.familyOwnFlag == '1'}">
                                                        <span class="link marr5 js_family_rht"
                                                              onclick="ph00301_input.deleteFamily(this);">-整户</span>
                                                        <span class="link marr5 js_family_rht"
                                                              onclick="ph00301_input.addPerson('familyTable_booklet',this);">+成员</span>
                                                        <span class="link marr5 js_family_rht js_doc_info"
                                                              docTypeName="户口本"
                                                              editAble="${editAble}"
                                                              onclick="ph00301_input.showDoc(this)">
                                                            <oframe:input type="hidden" cssClass="js_family_doc"
                                                                          oldValue="${hsPerson.Person.familyDocIds_old}"
                                                                          value="${hsPerson.Person.familyDocIds}"/>
                                                            <label>户口本</label>
                                                        </span>
                                                       <span class="link marr5 js_doc_info" docTypeName="身份证"
                                                             editAble="${editAble}"
                                                             onclick="ph00301_input.showDoc(this)">
                                                            <oframe:input type="hidden" cssClass="js_ps_certy_doc js_ps"
                                                                          oldValue="${hsPerson.Person.personCertyDocIds_old}"
                                                                          value="${hsPerson.Person.personCertyDocIds}"/>
                                                           <label>身份证</label>
                                                        </span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="link marr5 js_mem_rht"
                                                              onclick="ph00301_input.deletePerson(this);">-成员</span>
                                                        <span class="link marr5 js_doc_info" docTypeName="身份证"
                                                              editAble="${editAble}"
                                                              onclick="ph00301_input.showDoc(this)">
                                                            <oframe:input type="hidden" cssClass="js_ps_certy_doc js_ps"
                                                                          oldValue="${hsPerson.Person.personCertyDocIds}"
                                                                          value="${hsPerson.Person.personCertyDocIds}"/>
                                                            <label>身份证</label>
                                                        </span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                    </c:if>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
                <%--产权人户籍--%>
                <div style="min-height: 200px">
                    <div class="js_panel_context">
                        <div style="width: 100%;height: 100%;">
                            <table class="list" id="familyTable_owner" width="100%">
                                <thead>
                                <tr>
                                    <th width="10%">成员姓名</th>
                                    <th width="5%">户主关系</th>
                                    <th width="15%">证件号码</th>
                                    <th width="10%">联系电话</th>
                                    <th width="10%">户籍地址</th>
                                    <th width="10%">工作单位</th>
                                    <th width="5%">年龄</th>
                                    <th width="5%">现场居住</th>
                                    <th width="5%">婚姻状况</th>
                                    <th width="15%"><span class="link js_reload"
                                                          onclick="ph00301_input.addFamily('familyTable_owner', this)">新增产权人户籍</span>
                                    </th>
                                </tr>
                                </thead>
                                <tbody>
                                <tr class="hidden">
                                    <td>
                                        <input type="hidden" class="js_ps_id" value="">
                                        <input type="hidden" class="js_ps_family_ps_id js_ps" value="">
                                        <input type="hidden" class="js_ps_family_flag js_ps" value="">
                                        <input type="text" atOption="ph00301_input.getPsOpt"
                                               onblur="ph00301_input.calcFamNum();" class="autocomplete js_ps_name"
                                               value=""/>
                                    </td>
                                    <td><input type="text" class="js_ps_rel autocomplete js_ps" value=""
                                               atOption="ph00301_input.getPsRelOpt" atUrl="ph00301_input.getPsRelUrl"/>
                                    </td>
                                    <td><input type="text" class="js_ps_certy js_ps"
                                               onblur="ph00301_input.checkIdCalcAge(this)" value=""/></td>
                                    <td><input type="text" class="js_ps_tel js_ps" value=""/></td>
                                    <td><input type="text" class="js_family_addr js_ps" value=""/></td>
                                    <td><input type="text" class="js_ps_job js_ps" value=""/></td>
                                    <td><input type="text" class="js_ps_age js_ps" value=""/></td>
                                    <td><oframe:select prjCd="${param.prjCd}" itemCd="COMMON_YES_NO"
                                                       cssClass="js_ps_is_live js_ps"/></td>
                                    <td><oframe:select prjCd="${param.prjCd}" itemCd="MARRY_STATUS"
                                                       cssClass="js_ps_marry js_ps"/></td>
                                    <td>
                                            <span class="link marr5 js_family_rht"
                                                  onclick="ph00301_input.deleteFamily(this);">-整户</span>
                                            <span class="link marr5 js_family_rht"
                                                  onclick="ph00301_input.addPerson('familyTable_owner', this);">+成员</span>
                                            <span class="link marr5 js_family_rht js_doc_info" docTypeName="户口本"
                                                  editAble="${editAble}"
                                                  onclick="ph00301_input.showDoc(this)">
                                                <input type="hidden" class="js_family_doc" value="">
                                                <label>户口本</label>
                                            </span>
                                            <span class="link marr5 js_mem_rht"
                                                  onclick="ph00301_input.deletePerson(this);">-成员</span>
                                            <span class="link marr5 js_doc_info" docTypeName="身份证"
                                                  editAble="${editAble}"
                                                  onclick="ph00301_input.showDoc(this)">
                                                <input type="hidden" class="js_ps_certy_doc js_ps" value="">
                                                 <label>身份证</label>
                                            </span>
                                    </td>
                                </tr>
                                <c:forEach items="${hsPersonBean}" var="hsPerson">
                                    <c:set var="readRht" value=""/>
                                    <c:set var="autocomplete" value="autocomplete"/>
                                    <c:if test="${hsPerson.Person.familyOwnFlag == '1'}">
                                        <c:set var="readRht" value="readonly"/>
                                        <c:set var="autocomplete" value=""/>
                                    </c:if>
                                    <%-- 判断人员 执行操作 --%>
                                    <c:set var="psMethodClass" value=""/>
                                    <c:if test="${hsPerson.Person.method == '1'}">
                                        <c:set var="psMethodClass" value="isAddPs"/>
                                    </c:if>
                                    <c:if test="${hsPerson.Person.method == '3'}">
                                        <c:set var="psMethodClass" value="isDelPs"/>
                                    </c:if>
                                    <c:if test="${hsPerson.Person.personType == '2'}">
                                        <tr class="${psMethodClass}">
                                            <td>
                                                <input type="hidden" class="js_ps_id"
                                                       value="${hsPerson.Person.personId}">
                                                <input type="hidden" class="js_ps_family_ps_id js_ps"
                                                       value="${hsPerson.Person.familyPersonId}">
                                                <input type="hidden" class="js_ps_family_flag js_ps"
                                                       value="${hsPerson.Person.familyOwnFlag}">
                                                <oframe:input type="text" atOption="ph00301_input.getPsOpt"
                                                              onblur="ph00301_input.calcFamNum();"
                                                              cssClass="autocomplete js_ps_name "
                                                              value="${hsPerson.Person.personName}"/>
                                            </td>
                                            <td><oframe:input type="text"
                                                              cssClass="js_ps_rel ${readRht} ${autocomplete} js_ps"
                                                              readonly="${readRht}"
                                                              atOption="ph00301_input.getPsRelOpt"
                                                              atUrl="ph00301_input.getPsRelUrl"
                                                              oldValue="${hsPerson.Person.familyPersonRel_old}"
                                                              value="${hsPerson.Person.familyPersonRel}"/></td>
                                            <td><oframe:input type="text" cssClass="js_ps_certy js_ps "
                                                              onblur="ph00301_input.checkIdCalcAge(this)"
                                                              oldValue="${hsPerson.Person.personCertyNum}"
                                                              value="${hsPerson.Person.personCertyNum}"/></td>
                                            <td><oframe:input type="text" cssClass="js_ps_tel js_ps "
                                                              oldValue="${hsPerson.Person.personTelphone_old}"
                                                              value="${hsPerson.Person.personTelphone}"/></td>
                                            <td><oframe:input type="text" cssClass="js_family_addr js_ps "
                                                              oldValue="${hsPerson.Person.familyAddr_old}"
                                                              value="${hsPerson.Person.familyAddr}"/></td>
                                            <td><oframe:input type="text" cssClass="js_ps_job js_ps "
                                                              oldValue="${hsPerson.Person.personJobAddr_old}"
                                                              value="${hsPerson.Person.personJobAddr}"/></td>
                                            <td><oframe:input type="text" cssClass="js_ps_age js_ps"
                                                              oldValue="${hsPerson.Person.personAge_old}"
                                                              value="${hsPerson.Person.personAge}"/></td>
                                            <td><oframe:select prjCd="${param.prjCd}" itemCd="COMMON_YES_NO"
                                                               cssClass="js_ps_is_live js_ps"
                                                               oldValue="${hsPerson.Person.isLiveHere_old}"
                                                               value="${hsPerson.Person.isLiveHere}"/></td>
                                            <td><oframe:select prjCd="${param.prjCd}" itemCd="MARRY_STATUS"
                                                               cssClass="js_ps_marry js_ps "
                                                               oldValue="${hsPerson.Person.marryStatus_old}"
                                                               value="${hsPerson.Person.marryStatus}"/></td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${hsPerson.Person.familyOwnFlag == '1'}">
                                                            <span class="link marr5 js_family_rht"
                                                                  onclick="ph00301_input.deleteFamily(this);">-整户</span>
                                                            <span class="link marr5 js_family_rht"
                                                                  onclick="ph00301_input.addPerson('familyTable_owner',this);">+成员</span>
                                                            <span class="link marr5 js_family_rht js_doc_info"
                                                                  docTypeName="户口本"
                                                                  editAble="${editAble}"
                                                                  onclick="ph00301_input.showDoc(this)">
                                                                <oframe:input type="hidden" cssClass="js_family_doc"
                                                                              oldValue="${hsPerson.Person.familyDocIds_old}"
                                                                              value="${hsPerson.Person.familyDocIds}"/>
                                                                <label>户口本</label>
                                                            </span>
                                                           <span class="link marr5 js_doc_info" docTypeName="身份证"
                                                                 editAble="${editAble}"
                                                                 onclick="ph00301_input.showDoc(this)">
                                                                <oframe:input type="hidden"
                                                                              cssClass="js_ps_certy_doc js_ps"
                                                                              oldValue="${hsPerson.Person.personCertyDocIds_old}"
                                                                              value="${hsPerson.Person.personCertyDocIds}"/>
                                                               <label>身份证</label>
                                                            </span>
                                                    </c:when>
                                                    <c:otherwise>
                                                         <span class="link marr5 js_mem_rht"
                                                               onclick="ph00301_input.deletePerson(this);">-成员</span>
                                                         <span class="link marr5 js_doc_info" docTypeName="身份证"
                                                               editAble="${editAble}"
                                                               onclick="ph00301_input.showDoc(this)">
                                                             <oframe:input type="hidden"
                                                                           cssClass="js_ps_certy_doc js_ps"
                                                                           oldValue="${hsPerson.Person.personCertyDocIds_old}"
                                                                           value="${hsPerson.Person.personCertyDocIds}"/>
                                                             <label>身份证</label>
                                                         </span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                    </c:if>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
                <%--现居住人--%>
                <div style="min-height: 200px">
                    <div class="js_panel_context">
                        <div style="width: 100%;height: 100%;">
                            <table class="border" style="margin: 0" width="100%">
                                <tr>
                                    <th width="10%">实际居住人：</th>
                                    <td colspan="5">
                                        <oframe:input type="text" placeholder="请输入当前居住人姓名并以空格分隔"
                                                      name="livePsNum"
                                                      oldValue="${houseInfo.HouseInfo.livePsNum_old}"
                                                      value="${houseInfo.HouseInfo.livePsNum}"/>
                                    </td>
                                </tr>
                            </table>
                            <table class="list" id="familyTable_live" width="100%">
                                <thead>
                                <tr>
                                    <th width="10%">成员姓名</th>
                                    <th width="8%">与产承人关系</th>
                                    <th width="15%">证件号码</th>
                                    <th width="10%">联系电话</th>
                                    <th width="10%">工作单位</th>
                                    <th width="10%">户籍所在地</th>
                                    <th width="4%">年龄</th>
                                    <th width="5%">婚姻状况</th>
                                    <th width="18%">
                                        <span class="link js_reload"
                                              onclick="ph00301_input.addPerson('familyTable_live', this)">新增人员</span>
                                    </th>
                                </tr>
                                </thead>
                                <tbody>
                                <tr class="hidden">
                                    <td>
                                        <input type="hidden" class="js_ps_id" value="">
                                        <input type="hidden" class="js_ps_family_ps_id js_ps" value="">
                                        <input type="hidden" class="js_ps_family_flag js_ps" value="">
                                        <input type="text" atOption="ph00301_input.getPsOpt"
                                               onblur="ph00301_input.calcFamNum();" class="autocomplete js_ps_name"
                                               value=""/>
                                    </td>
                                    <td><input type="text" class="js_ps_rel autocomplete js_ps" value=""
                                               atOption="ph00301_input.getPsRelOpt" atUrl="ph00301_input.getPsRelUrl"/>
                                    </td>
                                    <td><input type="text" class="js_ps_certy js_ps"
                                               onblur="ph00301_input.checkIdCalcAge(this)" value=""/></td>
                                    <td><input type="text" class="js_ps_tel js_ps" value=""/>
                                    <td><input type="text" class="js_ps_job js_ps" value=""/></td>
                                    <td><input type="text" class="js_family_addr js_ps" value=""/></td>
                                    <td><input type="text" class="js_ps_age js_ps" value=""/></td>
                                    <td><input type="hidden" class="js_ps_is_live js_ps" value="1"/>
                                        <oframe:select prjCd="${param.prjCd}" itemCd="MARRY_STATUS"
                                                       cssClass="js_ps_marry js_ps"/></td>
                                    </td>
                                    <td>
                                        <span class="link marr5 js_family_rht"
                                              onclick="ph00301_input.deleteFamily(this);">-整户</span>
                                        <span class="link marr5 js_family_rht"
                                              onclick="ph00301_input.addPerson('familyTable_live', this);">+成员</span>
                                        <span class="link marr5 js_family_rht js_doc_info" docTypeName="户口本"
                                              editAble="${editAble}"
                                              onclick="ph00301_input.showDoc(this)">
                                            <input type="hidden" class="js_family_doc" value=""><label>户口本</label>
                                        </span>
                                        <span class="link marr5 js_mem_rht" onclick="ph00301_input.deletePerson(this);">-成员</span>
                                        <span class="link marr5 js_doc_info" docTypeName="身份证" editAble="${editAble}"
                                              onclick="ph00301_input.showDoc(this)">
                                            <input type="hidden" class="js_ps_certy_doc js_ps"
                                                   value=""><label>身份证</label>
                                        </span>
                                    </td>
                                </tr>
                                <c:forEach items="${hsPersonBean}" var="hsPerson">
                                    <c:set var="readRht" value=""/>
                                    <c:set var="autocomplete" value="autocomplete"/>
                                    <c:if test="${hsPerson.Person.familyOwnFlag == '1'}">
                                        <c:set var="readRht" value="readonly"/>
                                        <c:set var="autocomplete" value=""/>
                                    </c:if>
                                    <c:set var="psMethodClass" value=""/>
                                    <c:if test="${hsPerson.Person.method == '1'}">
                                        <c:set var="psMethodClass" value="isAddPs"/>
                                    </c:if>
                                    <c:if test="${hsPerson.Person.method == '3'}">
                                        <c:set var="psMethodClass" value="isDelPs"/>
                                    </c:if>
                                    <c:if test="${hsPerson.Person.personType == '3'}">
                                        <c:set var="readRht" value="readonly"/>
                                        <tr class="${psMethodClass}">
                                            <td>
                                                <input type="hidden" class="js_ps_id"
                                                       value="${hsPerson.Person.personId}">
                                                <input type="hidden" class="js_ps_family_ps_id js_ps"
                                                       value="${hsPerson.Person.familyPersonId}">
                                                <input type="hidden" class="js_ps_family_flag js_ps"
                                                       value="${hsPerson.Person.familyOwnFlag}">
                                                <oframe:input type="text" atOption="ph00301_input.getPsOpt"
                                                              onblur="ph00301_input.calcFamNum();"
                                                              cssClass="autocomplete js_ps_name ${readRht}"
                                                              readonly="${readRht}"
                                                              oldValue="${hsPerson.Person.personName_old}"
                                                              value="${hsPerson.Person.personName}"/>
                                            </td>
                                            <td>
                                                <oframe:input type="text" cssClass="js_ps_rel ${readRht} js_ps"
                                                              readonly="${readRht}"
                                                              atOption="ph00301_input.getPsRelOpt"
                                                              atUrl="ph00301_input.getPsRelUrl"
                                                              oldValue="${hsPerson.Person.familyPersonRel_old}"
                                                              value="${hsPerson.Person.familyPersonRel}"/></td>
                                            <td><oframe:input type="text" cssClass="js_ps_certy js_ps ${readRht}"
                                                              readonly="${readRht}"
                                                              onblur="ph00301_input.checkIdCalcAge(this)"
                                                              oldValue="${hsPerson.Person.personCertyNum}"
                                                              value="${hsPerson.Person.personCertyNum}"/></td>
                                            <td><oframe:input type="text" cssClass="js_ps_tel js_ps ${readRht}"
                                                              readonly="${readRht}"
                                                              oldValue="${hsPerson.Person.personTelphone_old}"
                                                              value="${hsPerson.Person.personTelphone}"/></td>
                                            <td><oframe:input type="text" cssClass="js_ps_job js_ps ${readRht}"
                                                              readonly="${readRht}"
                                                              oldValue="${hsPerson.Person.personJobAddr_old}"
                                                              value="${hsPerson.Person.personJobAddr}"/></td>
                                            <td><oframe:input type="text" cssClass="js_family_addr js_ps ${readRht}"
                                                              readonly="${readRht}"
                                                              oldValue="${hsPerson.Person.familyAddr_old}"
                                                              value="${hsPerson.Person.familyAddr}"/></td>
                                            <td><oframe:input type="text" cssClass="js_ps_age js_ps ${readRht}"
                                                              oldValue="${hsPerson.Person.personAge_old}"
                                                              value="${hsPerson.Person.personAge}"/></td>
                                            <td><input type="hidden" class="js_ps_is_live js_ps" value="1"/>
                                                <oframe:select prjCd="${param.prjCd}" itemCd="MARRY_STATUS"
                                                               cssClass="js_ps_marry js_ps ${readRht}"
                                                               value="${hsPerson.Person.marryStatus}"/>
                                            </td>
                                            <td>
                                                <span class="link marr5 js_mem_rht"
                                                      onclick="ph00301_input.deletePerson(this);">-成员</span>
                                                <span class="link marr5 js_doc_info" docTypeName="身份证"
                                                      editAble="${editAble}"
                                                      onclick="ph00301_input.showDoc(this)">
                                                    <input type="hidden" class="js_ps_certy_doc js_ps"
                                                           value="${hsPerson.Person.personCertyDocIds}"><label>身份证</label>
                                                </span>
                                            </td>
                                        </tr>
                                    </c:if>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="tabs mart5">
            <div class="tabsHeader">
                <div class="tabsHeaderContent">
                    <ul>
                        <li class="js_load_tab selected"
                            onclick="ph00301_input.loadTabContext(this);">
                            <a href="javascript:void(0);"><span>人员状况</span></a>
                        </li>
                        <li class="js_load_tab"
                            onclick="ph00301_input.loadTabContext(this);">
                            <a href="javascript:void(0);"><span>附属信息</span></a>
                        </li>
                        <li class="js_load_tab"
                            onclick="ph00301_input.loadTabContext(this);">
                            <a href="javascript:void(0);"><span>经营状况</span></a>
                        </li>
                        <li class="js_load_tab"
                            onclick="ph00301_input.loadTabContext(this);">
                            <a href="javascript:void(0);"><span>保障房申请情况</span></a>
                        </li>
                    </ul>
                </div>
            </div>
            <div class="tabsContent">
                <%--腾退成本--%>
                <div style="min-height: 200px">
                    <table class="border marb5" width="100%">
                        <tr>
                            <th width="10%"><label>残疾证数：</label></th>
                            <td width="20%">
                                <oframe:input type="text" cssClass="digits needDoc" name="cjNum"
                                              oldValue="${houseInfo.HouseInfo.cjNum_old}"
                                              value="${houseInfo.HouseInfo.cjNum}"/>
                                <span class="js_doc_info link hidden" docTypeName="残疾低保证件" editAble="${editAble}"
                                      onclick="ph00301_input.showDoc(this)">
                                    <input type="hidden" name="cjDocIds" value="${houseInfo.HouseInfo.cjDocIds}">
                                    <label style="cursor:pointer;">查看</label>
                                </span>
                            </td>
                            <th width="10%"><label>低保证数：</label></th>
                            <td width="20%">
                                <oframe:input type="text" cssClass="digits needDoc" name="dbNum"
                                              oldValue="${houseInfo.HouseInfo.dbNum_old}"
                                              value="${houseInfo.HouseInfo.dbNum}"/>
                                <span class="js_doc_info link hidden" docTypeName="残疾低保证件" editAble="${editAble}"
                                      onclick="ph00301_input.showDoc(this)">
                                    <input type="hidden" name="dbDocIds" value="${houseInfo.HouseInfo.dbDocIds}">
                                    <label style="cursor:pointer;">查看</label>
                                </span>
                            </td>
                            <th width="10%"><label>高龄人数：</label></th>
                            <td width="20%">
                                <oframe:input type="text" cssClass="digits needDoc" name="hAgeNum"
                                              oldValue="${houseInfo.HouseInfo.hAgeNum_old}"
                                              value="${houseInfo.HouseInfo.hAgeNum}"/>
                                <span class="js_doc_info link hidden" docTypeName="高龄证件" editAble="${editAble}"
                                      onclick="ph00301_input.showDoc(this)">
                                    <input type="hidden" name="hAgeDocIds" value="${houseInfo.HouseInfo.hAgeDocIds}">
                                    <label style="cursor:pointer;">查看</label>
                                </span>
                            </td>
                        </tr>
                        <tr>
                            <th><label>大病人数：</label></th>
                            <td>
                                <oframe:input type="text" cssClass="digits needDoc" name="seriousIll"
                                              oldValue="${houseInfo.HouseInfo.seriousIll_old}"
                                              value="${houseInfo.HouseInfo.seriousIll}"/>
                                <span class="js_doc_info link hidden" docTypeName="大病附件" editAble="${editAble}"
                                      onclick="ph00301_input.showDoc(this)">
                                    <input type="hidden" name="seriousIllDocIds"
                                           value="${houseInfo.HouseInfo.seriousIllDocIds}">
                                    <label style="cursor:pointer;">查看</label>
                                </span>
                            </td>
                            <th><label>两劳人数：</label></th>
                            <td>
                                <oframe:input type="text" cssClass="digits" name="jailNum"
                                              oldValue="${houseInfo.HouseInfo.jailNum_old}"
                                              value="${houseInfo.HouseInfo.jailNum}"/>
                            </td>
                            <th><label>服兵役人数：</label></th>
                            <td>
                                <oframe:input type="text" cssClass="digits" name="armyNum"
                                              oldValue="${houseInfo.HouseInfo.armyNum_old}"
                                              value="${houseInfo.HouseInfo.armyNum}"/>
                            </td>
                        </tr>
                        <tr>
                            <th><label>公职人员：</label></th>
                            <td colspan="5">
                                <oframe:input type="text" name="gzPerson"
                                              oldValue="${houseInfo.HouseInfo.gzPerson_old}"
                                              value="${houseInfo.HouseInfo.gzPerson}"/>
                            </td>
                        </tr>
                        <tr>
                            <th><label>备注说明：</label></th>
                            <td colspan="5">
                                <oframe:textarea rows="5" style="width:80%" name="hsNote"
                                                 oldValue="${baseBean.HouseInfo.hsNote_old}"
                                                 value="${baseBean.HouseInfo.hsNote}"/>
                            </td>
                        </tr>
                    </table>
                </div>
                <%-- 附属信息--%>
                <div style="min-height: 200px">
                    <table class="border marb5" width="100%">
                        <tr>
                            <th width="10%"><label>空调数量：</label></th>
                            <td width="20%">
                                <oframe:input type="text" cssClass="digits needDoc" name="ktNum"
                                              oldValue="${houseInfo.HouseInfo.ktNum_old}"
                                              value="${houseInfo.HouseInfo.ktNum}"/>
                                <span class="js_doc_info link hidden" docTypeName="移机补助发票" editAble="${editAble}"
                                      onclick="ph00301_input.showDoc(this)">
                                    <input type="hidden" name="ktDocIds" value="${houseInfo.HouseInfo.ktDocIds}">
                                    <label style="cursor:pointer;">查看</label>
                                </span>
                            </td>
                            <th width="10%"><label>有线数量：</label></th>
                            <td width="20%">
                                <oframe:input type="text" cssClass="digits needDoc" name="yxNum"
                                              oldValue="${houseInfo.HouseInfo.yxNum_old}"
                                              value="${houseInfo.HouseInfo.yxNum}"/>
                                <span class="js_doc_info link hidden" docTypeName="移机补助发票" editAble="${editAble}"
                                      onclick="ph00301_input.showDoc(this)">
                                    <input type="hidden" name="yxDocIds" value="${houseInfo.HouseInfo.yxDocIds}">
                                    <label style="cursor:pointer;">查看</label>
                                </span>
                            </td>
                            <th width="10%"><label>固话数量：</label></th>
                            <td width="20%">
                                <oframe:input type="text" cssClass="digits needDoc" name="ghNum"
                                              oldValue="${houseInfo.HouseInfo.ghNum_old}"
                                              value="${houseInfo.HouseInfo.ghNum}"/>
                                <span class="js_doc_info link hidden" docTypeName="移机补助发票" editAble="${editAble}"
                                      onclick="ph00301_input.showDoc(this)">
                                    <input type="hidden" name="ghDocIds" value="${houseInfo.HouseInfo.ghDocIds}">
                                    <label style="cursor:pointer;">查看</label>
                                </span>
                            </td>
                        </tr>
                        <tr>
                            <th><label>网络数量：</label></th>
                            <td>
                                <oframe:input type="text" cssClass="digits needDoc" name="wlNum"
                                              oldValue="${houseInfo.HouseInfo.wlNum_old}"
                                              value="${houseInfo.HouseInfo.wlNum}"/>
                                 <span class="js_doc_info link hidden" docTypeName="移机补助发票" editAble="${editAble}"
                                       onclick="ph00301_input.showDoc(this)">
                                    <input type="hidden" name="wlDocIds" value="${houseInfo.HouseInfo.wlDocIds}">
                                    <label style="cursor:pointer;">查看</label>
                                </span>
                            </td>
                            <th><label>热水器数量：</label></th>
                            <td>
                                <oframe:input type="text" cssClass="digits needDoc" name="rsqNum"
                                              oldValue="${houseInfo.HouseInfo.rsqNum_old}"
                                              value="${houseInfo.HouseInfo.rsqNum}"/>
                                 <span class="js_doc_info link hidden" docTypeName="移机补助发票" editAble="${editAble}"
                                       onclick="ph00301_input.showDoc(this)">
                                    <input type="hidden" name="rsqDocIds" value="${houseInfo.HouseInfo.rsqDocIds}">
                                    <label style="cursor:pointer;">查看</label>
                                </span>
                            </td>
                            <th><label>机动车数：</label></th>
                            <td>
                                <oframe:input type="text" cssClass="digits needDoc" name="motorNum"
                                              oldValue="${houseInfo.HouseInfo.motorNum_old}"
                                              value="${houseInfo.HouseInfo.motorNum}"/>
                            </td>
                        </tr>
                        <tr>
                            <th><label>煤改电个人承担：</label></th>
                            <td>
                                <oframe:input type="text" cssClass="number needDoc" name="mgdGrZf"
                                              onchange="ph00301_input.changeMGd();"
                                              oldValue="${houseInfo.HouseInfo.mgdGrZf_old}"
                                              value="${houseInfo.HouseInfo.mgdGrZf}"/>
                                <span class="js_doc_info link hidden" docTypeName="移机补助发票" editAble="${editAble}"
                                      onclick="ph00301_input.showDoc(this)">
                                    <input type="hidden" name="mgDDocIds" value="${houseInfo.HouseInfo.mgDDocIds}">
                                    <label style="cursor:pointer;">查看</label>
                                </span>
                            </td>
                            <th><label>煤改电补助费用：</label></th>
                            <td><oframe:input type="text" cssClass="readonly" readonly="readonly" name="mgdGrZf_real"
                                              value=""/>
                            </td>
                            <th><label></label></th>
                            <td></td>
                        </tr>
                        <tr>
                            <th><label>备注说明：</label></th>
                            <td colspan="5">
                                <oframe:textarea name="fwfsNotes" style="width:80%" rows="5"
                                                 oldValue="${houseInfo.HouseInfo.fwfsNotes_old}"
                                                 value="${houseInfo.HouseInfo.fwfsNotes}"/>
                            </td>
                        </tr>
                    </table>
                </div>
                <%--经营情况--%>
                <div style="min-height: 200px">
                    <table class="border marb5" width="100%">
                        <tr class="js_busi">
                            <th width="10%"><label>是否营业：</label></th>
                            <td width="20%">
                                <oframe:select prjCd="${param.prjCd}" type="radio" name="businessStatus"
                                               id="businessStatus"
                                               oldValue="${houseInfo.HouseInfo.businessStatus}"
                                               value="${houseInfo.HouseInfo.businessStatus}"
                                               itemCd="COMMON_YES_NO"/></td>
                            <th width="10%"><label>营业执照编号：</label></th>
                            <td width="20%">
                                <oframe:input type="text" cssClass="needDoc"
                                              oldValue="${houseInfo.HouseInfo.businessCertNum_old}"
                                              value="${houseInfo.HouseInfo.businessCertNum}"
                                              onblur="ph00301_input.changeBusinessSize();"
                                              name="businessCertNum"/>
                                <span class="js_doc_info link hidden" docTypeName="营业执照" editAble="${editAble}"
                                      onclick="ph00301_input.showDoc(this)">
                                    <input type="hidden" name="businessCertDocIds"
                                           value="${houseInfo.HouseInfo.businessCertDocIds}">
                                    <label style="cursor:pointer;">查看</label>
                                </span>
                            </td>
                            <th width="10%"><label>经营面积：</label></th>
                            <td width="20%">
                                <oframe:input type="text" name="businessHsSize"
                                              oldValue="${houseInfo.HouseInfo.businessHsSize_old}"
                                              value="${houseInfo.HouseInfo.businessHsSize}"/>
                            </td>
                        </tr>
                        <tr class="js_busi">
                            <th><label>经营者姓名：</label></th>
                            <td>
                                <oframe:input type="text" name="busiMager"
                                              oldValue="${houseInfo.HouseInfo.busiMager_old}"
                                              value="${houseInfo.HouseInfo.busiMager}"/>
                            </td>
                            <th><label>与产承人关系：</label></th>
                            <td>
                                <oframe:input type="text" name="busiRelOwner"
                                              oldValue="${houseInfo.HouseInfo.busiRelOwner_old}"
                                              value="${houseInfo.HouseInfo.busiRelOwner}"/>
                            </td>
                            <th><label>执照有效期：</label></th>
                            <td><oframe:input type="text" name="yxDate"
                                              oldValue="${houseInfo.HouseInfo.yxDate_old}"
                                              value="${houseInfo.HouseInfo.yxDate}"/>
                            </td>
                        </tr>
                        <tr>
                            <th><label>注册地点：</label></th>
                            <td colspan="5">
                                <oframe:input type="text" name="regstPlace"
                                              oldValue="${houseInfo.HouseInfo.regstPlace_old}"
                                              value="${houseInfo.HouseInfo.regstPlace}"/>
                            </td>
                        </tr>
                        <tr>
                            <th><label>备注说明：</label></th>
                            <td colspan="5">
                                <oframe:textarea name="businessNote" style="width: 80%" rows="5"
                                                 oldValue="${houseInfo.HouseInfo.businessNote_old}"
                                                 value="${houseInfo.HouseInfo.businessNote}"/>
                            </td>
                        </tr>
                    </table>
                </div>
                <%--保障房申请情况--%>
                <div style="min-height: 200px">
                    <table class="border marb5" width="100%">
                        <tr>
                            <th width="10%"><label>是否享受过：</label></th>
                            <td width="20%"><oframe:select prjCd="${param.prjCd}" type="radio" name="isEngoy"
                                                           id="isEngoy"
                                                           itemCd="COMMON_YES_NO"
                                                           oldValue="${houseInfo.HouseInfo.isEngoy_old}"
                                                           value="${houseInfo.HouseInfo.isEngoy}"/></td>
                            <th width="10%"><label>申请类别：</label></th>
                            <td width="20%"><oframe:input type="text" name="aployCatgry"
                                                          oldValue="${houseInfo.HouseInfo.aployCatgry_old}"
                                                          value="${houseInfo.HouseInfo.aployCatgry}"/></td>
                            <th width="10%"><label>是否已选房：</label></th>
                            <td width="20%">
                                <oframe:select prjCd="${param.prjCd}" type="radio" name="isChoose"
                                               itemCd="COMMON_YES_NO"
                                               id="isChoose"
                                               oldValue="${houseInfo.HouseInfo.isChoose_old}"
                                               value="${houseInfo.HouseInfo.isChoose}"/>
                            </td>
                        </tr>
                        <tr>
                            <th><label>申请人姓名：</label></th>
                            <td colspan="5" width="15%">
                                <oframe:input type="text" name="aployName"
                                              oldValue="${houseInfo.HouseInfo.aployName_old}"
                                              value="${houseInfo.HouseInfo.aployName}"/>
                            </td>
                        </tr>
                        <tr>
                            <th><label>所选房源地址：</label></th>
                            <td colspan="5" width="15%">
                                <oframe:input type="text" name="chooseHsPlac"
                                              oldValue="${houseInfo.HouseInfo.chooseHsPlac_old}"
                                              value="${houseInfo.HouseInfo.chooseHsPlac}"/>
                            </td>
                        </tr>
                        <tr>
                            <th><label>备注说明：</label></th>
                            <td colspan="5">
                                <oframe:textarea name="bzxNote" style="width: 80%;" rows="5"
                                                 oldValue="${houseInfo.HouseInfo.bzxNote_old}"
                                                 value="${houseInfo.HouseInfo.bzxNote}"/>
                            </td>
                        </tr>
                    </table>
                </div>
            </div>
        </div>
    </form>
</div>