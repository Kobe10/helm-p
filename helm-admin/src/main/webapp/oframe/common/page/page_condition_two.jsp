<%--自定义分页查询查询条件--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<c:set var="canDefResultClass" value="${canDefResult == 'false'? 'hidden': ''}"/>
<c:set var="canQueryPsClass" value="hidden"/>
<oframe:power prjCd="${param.prjCd}" rhtCd="CAN_QUERY_PS">
    <c:set var="canQueryPsClass" value=""/>
</oframe:power>
<c:set var="canQueryTagClass" value="hidden"/>
<oframe:power prjCd="${param.prjCd}" rhtCd="CAN_QUERY_TAG">
    <c:set var="canQueryTagClass" value=""/>
</oframe:power>
<div class="tip js_query_div tipm" style="position: absolute;width: 100%; overflow:inherit;top: 40px;display: block;">
    <input type="hidden" class="js_entityName" value="${entityName}">
    <input type="hidden" class="js_canDefResult" value="${canDefResult}">

    <div class="tree-bar tree-bars" id="fav">
        <span style="display: inline-block; margin-left: 10px;">我的收藏：</span>
        <%--隐藏模板--%>
        <span style="line-height: 20px;display: none;" onclick="Page.clickFav(this);" favId=""
              class="my_fav">
                 <label class="js_fav_name"></label>
             <oframe:power prjCd="${param.prjCd}" rhtCd="EDIT_SYS_FAV">
                 <label class="js_fav_del" onclick="Page.deletefav(this)">X</label>
             </oframe:power>
        </span>
        <span style="line-height: 20px;display: none;" onclick="Page.clickFav(this);" favId=""
              class="my_fav">
                 <label class="js_fav_name" style="cursor: pointer;"></label>
             <oframe:power prjCd="${param.prjCd}" rhtCd="EDIT_SYS_FAV">
                 <label class="js_fav_del" onclick="Page.deletefav(this)">X</label>
             </oframe:power>
        </span>
        <c:forEach items="${favinfo}" var="fav">
            <c:choose>
                <c:when test="${fav.StaffFavoriteCondition.favStaff ==''}">
                    <span style="line-height: 20px;" onclick="Page.clickFav(this);"
                          favId="${fav.StaffFavoriteCondition.favId}"
                          class="my_fav">
                          <label class="js_fav_name"
                                 style="cursor: pointer;">${fav.StaffFavoriteCondition.favName}</label>
                         <oframe:power prjCd="${param.prjCd}" rhtCd="EDIT_SYS_FAV">
                             <label class="js_fav_del"
                                    onclick="Page.deletefav(this)">X</label>
                         </oframe:power>
                    </span>
                </c:when>
                <c:otherwise>
                    <span style="line-height: 20px" onclick="Page.clickFav(this);"
                          favId="${fav.StaffFavoriteCondition.favId}"
                          class="my_fav">
                         <label class="js_fav_name"
                                style="cursor: pointer;">${fav.StaffFavoriteCondition.favName}</label>
                         <label class="fav_del js_fav_del"
                                onclick="Page.deletefav(this)">X</label>
                    </span>
                </c:otherwise>
            </c:choose>
        </c:forEach>
    </div>
    <table class="border js_def_table" style="position: relative;">
        <tr>
            <th width="80px">
                自动匹配：
            </th>
            <td>
                <div class="screeningConditions">
                </div>
                <input cssClass="pull-left pull-empty"
                       type="text" name="autoCondition" style="width: 80%;" placeholder="输入档案编号、房屋地址、产权人姓名"
                       value="${autoConditions}"/>
            </td>
        </tr>
        <tr>
            <th width="80px">
                房屋过滤：
            </th>
            <td>
                <div class="screeningConditions">
                </div>
                <oframe:input cssClass="autocomplete pull-left pull-empty"
                              type="text" name="" style="width: 8%;"
                              atOption="page_input.getScress"/>

                <a title="筛选条件" class="btnLook" style="float: left;"
                   onclick="">选择</a>

                <ul class="js_conditions_ul" selector="" style="display: inline">
                    <li class="qry_cod" condFieldName="" style="display: none"
                        conditionName="" condition="" conditionValue="">
                        <span class="js_query_cond"></span>
                        <label class="con_del js_del_cond">X</label>

                        <div class="rst_field_cond hidden"></div>
                    </li>
                    <c:forEach items="${queryCondList}" var="condition">
                        <li class="qry_cod"
                            condFieldName="${condition.CondField.entityAttrNameEn}"
                            conditionName="${condition.CondField.conditionName}"
                            condition="${condition.CondField.condition}"
                            entityAttrNameCh="${condition.CondField.entityAttrNameCh}"
                            conditionValue="${condition.CondField.conditionValue}">
                            <span class="js_query_cond">${condition.CondField.entityAttrNameCh}：${condition.CondField.conditionText}</span>
                            <label class="con_del js_del_cond">X</label>

                            <div class="rst_field_cond hidden"></div>
                        </li>
                    </c:forEach>
                </ul>
            </td>
        </tr>
        <tr class="${canQueryPsClass}">
            <th width="80px">
                人员过滤：
            </th>
            <td>
                <div class="screeningConditions">
                </div>
                <oframe:input cssClass="autocomplete pull-left pull-empty"
                              type="text" name="" style="width: 8%;"
                              atOption="page_input.getPsScress"/>

                <a title="筛选条件" class="btnLook" style="float: left;"
                   onclick="">选择</a>

                <ul class="js_ps_conditions_ul" selector="" style="display: inline">
                    <li class="qry_ps_cod qry_cod" condFieldName="" style="display: none"
                        conditionName="" condition="" conditionValue="">
                        <span class="js_query_cond js_ps_query_cond"></span>
                        <label class="con_del js_del_cond js_ps_del_cond">X</label>

                        <div class="rst_field_cond rst_ps_field_cond hidden"></div>
                    </li>
                    <c:forEach items="${queryPsCondList}" var="psCondition">
                        <li class="qry_ps_cod qry_cod"
                            condFieldName="${psCondition.CondField.entityAttrNameEn}"
                            conditionName="${psCondition.CondField.conditionName}"
                            condition="${psCondition.CondField.condition}"
                            entityAttrNameCh="${psCondition.CondField.entityAttrNameCh}"
                            conditionValue="${psCondition.CondField.conditionValue}">
                            <span class="js_query_cond js_ps_query_cond">${psCondition.CondField.entityAttrNameCh}：${psCondition.CondField.conditionText}</span>
                            <label class="con_del js_del_cond js_ps_del_cond">X</label>

                            <div class="rst_field_cond rst_ps_field_cond hidden"></div>
                        </li>
                    </c:forEach>
                </ul>
            </td>
        </tr>
        <tr class="${canQueryTagClass}">
            <th width="80px">
                标签过滤：
            </th>
            <td>
                <div class="screeningConditions">
                </div>
                <oframe:input cssClass="autocomplete pull-left pull-empty"
                              type="text" name="" style="width: 8%;"
                              atOption="page_input.getTagScress"/>

                <a title="筛选条件" class="btnLook" style="float: left;"
                   onclick="">选择</a>

                <ul class="js_tag_conditions_ul" selector="" style="display: inline">
                    <li class="qry_tag_cod qry_cod" condFieldName="" style="display: none"
                        conditionName="" condition="" conditionValue="">
                        <span class="js_tag_query_cond"></span>
                        <label class="con_del js_del_cond js_tag_del_cond">X</label>

                        <div class="rst_field_cond rst_tag_field_cond hidden"></div>
                    </li>
                    <c:forEach items="${queryTagCondList}" var="tagCondition">
                        <li class="qry_tag_cod qry_cod"
                            condFieldName="${tagCondition.CondField.entityAttrNameEn}"
                            conditionName="${tagCondition.CondField.conditionName}"
                            condition="${tagCondition.CondField.condition}"
                            entityAttrNameCh="${tagCondition.CondField.entityAttrNameCh}"
                            conditionValue="${tagCondition.CondField.conditionValue}">
                            <span class="js_tag_query_cond">${tagCondition.CondField.entityAttrNameCh}"${tagCondition.CondField.condition_Name}"${tagCondition.CondField.conditionText}</span>
                            <label class="con_del js_del_cond js_tag_del_cond">X</label>
                            <div class="rst_field_cond rst_tag_field_cond hidden"></div>
                        </li>
                    </c:forEach>
                </ul>
            </td>
        </tr>
        <tr class="${canDefResultClass}">
            <th width="80px">
                检索内容：
            </th>
            <td>
                <div class="retrievalContent">
                </div>
                <oframe:input cssClass="autocomplete pull-left pull-empty"
                              type="text" name="" style="width: 8%;"
                              atOption="page_input.getRetrieval"/>
                <a title="检索内容" class="btnLook" style="float: left;"
                   onclick="">选择</a>
                <ul class="js_show_ul" selector="" style="display: inline">
                    <li class="qry_cod" resultFieldName="" style="display: none;">
                        <label class="sortable">&nbsp;</label>
                        <span class="js_fld_name"></span>
                        <label class="con_del js_del_rslt">X</label>
                    </li>
                    <c:forEach var="item" items="${resultFieldList}">
                        <li class="qry_cod" resultFieldName="${item.Attr.entityAttrNameEn}">
                            <label class="${item.Attr.sortClass}">&nbsp;</label>
                            <span class="js_fld_name">${item.Attr.entityAttrNameCh}</span>
                            <label class="con_del js_del_rslt" style="">X</label>
                        </li>
                    </c:forEach>
                </ul>
            </td>
        </tr>
        <tr>
            <td colspan="2" align="center">
                <button onclick="Page.submitCondition(this)" class="btn btn-pri marl10 marr10 ">查询</button>
                <button onclick="Page.saveCondition(this);" class="btn btn-opt marl10 marr10 js_saveCondition">收藏
                </button>
                <button onclick="Page.closeConditionTwo(this)" class="btn btn-warn marl10 marr10 ">关闭</button>
                <a class="js-more"></a>
            </td>
        </tr>
        <tr style="display: none;" id="js-show">
            <td colspan="2">
                <div class="js_cond_list" style="max-height: 300px;overflow-y: auto; ">
                    <c:forEach items="${hsGroups}" var="hsGroups">
                        <c:if test="${fn:length(hsGroups.value)>0}">
                            <div class="rst_group">
                                <span style="font-weight: bold; ">${hsGroups.key}</span>
                            </div>
                            <div class="clearfix" style="min-height: 36px;">
                                <c:forEach items="${hsGroups.value}" var="resultHsField">
                                    <span class="rst_field ${resultHsFieldresultHsField.Attr.addClass}"
                                          resultRequired="${resultHsField.Attr.required}"
                                          canConditionFlag="${resultHsField.Attr.canConditionFlag}"
                                          canOrderFlag="${resultHsField.Attr.canOrderFlag}"
                                          entityAttrNameEn="${resultHsField.Attr.entityAttrNameEn}"
                                          title="${resultHsField.Attr.entityAttrNameCh}">
                                          <label class="rst_field_slt">&nbsp;</label>
                                          <label class="rst_field_name">${resultHsField.Attr.entityAttrNameCh}</label>
                                          <div class="rst_field_cond hidden"></div>
                                    </span>
                                </c:forEach>
                            </div>
                        </c:if>
                    </c:forEach>
                </div>
                <div class="js_ps_list hidden" style="max-height: 300px;overflow-y: auto; ">
                    <c:forEach items="${psGroups}" var="psGroups">
                        <c:if test="${fn:length(psGroups.value)>0}">
                            <div class="rst_group">
                                <span style="font-weight: bold; ">${psGroups.key}</span>
                            </div>
                            <div class="clearfix" style="min-height: 36px;">
                                <c:forEach items="${psGroups.value}" var="resultPsField">
                                    <span class="rst_field rst_ps_field ${resultPsField.Attr.addClass}"
                                          resultRequired="${resultPsField.Attr.required}"
                                          canConditionFlag="${resultPsField.Attr.canConditionFlag}"
                                          canOrderFlag="${resultPsField.Attr.canOrderFlag}"
                                          entityAttrNameEn="${resultPsField.Attr.entityAttrNameEn}"
                                          title="${resultPsField.Attr.entityAttrNameCh}">
                                          <label class="rst_field_slt rst_ps_field_slt">&nbsp;</label>
                                          <label class="rst_field_name rst_ps_field_name">${resultPsField.Attr.entityAttrNameCh}</label>
                                          <div class="rst_field_cond rst_ps_field_cond hidden"></div>
                                    </span>
                                </c:forEach>
                            </div>
                        </c:if>
                    </c:forEach>
                </div>
                <div class="js_tag_list hidden" style="max-height: 300px;overflow-y: auto; ">
                    <c:forEach items="${tagGroups}" var="tagGroups">
                        <c:if test="${fn:length(tagGroups.value)>0}">
                            <div class="rst_group">
                                <span style="font-weight: bold; ">${tagGroups.key}</span>
                            </div>
                            <div class="clearfix" style="min-height: 36px;">
                                <c:forEach items="${tagGroups.value}" var="resultTagField">
                                    <span class="rst_field rst_tag_field ${resultTagField.Attr.addClass}"
                                          resultRequired="${resultTagField.Attr.required}"
                                          canConditionFlag="${resultTagField.Attr.canConditionFlag}"
                                          canOrderFlag="${resultTagField.Attr.canOrderFlag}"
                                          entityAttrNameEn="${resultTagField.Attr.entityAttrNameEn}"
                                          title="${resultTagField.Attr.entityAttrNameCh}">
                                          <label class="rst_field_slt rst_tag_field_slt">&nbsp;</label>
                                          <label class="rst_field_name rst_tag_field_name">${resultTagField.Attr.entityAttrNameCh}</label>
                                          <div class="rst_field_cond rst_tag_field_cond hidden"></div>
                                    </span>
                                </c:forEach>
                            </div>
                        </c:if>
                    </c:forEach>
                </div>
            </td>
        </tr>
    </table>
</div>
<oframe:script src="${pageContext.request.contextPath}/oframe/common/page/js/page_condition.js"
               type="text/javascript"/>
<script src="${pageContext.request.contextPath}/oframe/common/page/js/jquery-list-dragsort.js"></script>
<script>
    $("ul.js_show_ul").dragsort({
        dragSelector: "span",
        dragBetween: true,
        placeHolderTemplate: "<span></span>",
        scrollSpeed: 0
    })
</script>