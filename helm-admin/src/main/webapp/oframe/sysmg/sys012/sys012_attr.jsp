<%--人地房信息展示页面--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%--房源统计信息--%>
<div class="panel" id="sys012Summary">
    <h1>
        <span>属性定义</span>
    </h1>

    <form>
        <input type="hidden" name="oldEntityName" value="${entityName}"/>
        <input type="hidden" name="oldEntityGroupId" value="${entityGroupId}"/>
        <input type="hidden" name="oldEntityAttrNameEn" value="${attr.Attr.entityAttrNameEn}"/>
        <input type="hidden" name="deleteCaseadeFlag" value="${attr.Attr.deleteCaseadeFlag}"/>
        <input type="hidden" name="usePrjCd" value="${usePrjCd}"/>
        <input type="hidden" name="id" value="${id}"/>

        <div layoutH="55">
            <table class="border">
                <tr>
                    <th width="10%"><label>归属实体：</label></th>
                    <td>
                        <input type="text" name="entityName" class="required" value="${entityName}">
                    </td>
                    <th width="10%"><label>归属分组：</label></th>
                    <td>
                        <oframe:select prjCd="${param.prjCd}" collection="${groups}" name="entityGroupId"
                                       value="${entityGroupId}"/>
                    </td>
                    <th width="10%"><label>英文名称：</label></th>
                    <td>
                        <input type="text" name="entityAttrNameEn" class="required"
                               value="${attr.Attr.entityAttrNameEn}">
                    </td>
                    <th width="10%"><label>中文名称：</label></th>
                    <td>
                        <input type="text" name="entityAttrNameCh" class="required"
                               value="${attr.Attr.entityAttrNameCh}">
                    </td>

                </tr>
                <tr>
                    <th><label>一对多：</label></th>
                    <td>
                        <oframe:select prjCd="${param.prjCd}" collection="${mutiAttrFlagMap}" name="mutiAttrFlag"
                                       value="${mutiAttrFlag}"/>
                    </td>
                    <th><label>接受变量：</label></th>
                    <td>
                        <oframe:select prjCd="${param.prjCd}" collection="${muti}" name="attrCanVariable"
                                       value="${attr.Attr.attrCanVariable}"/>
                    </td>
                    <th><label>参考实体：</label></th>
                    <td>
                        <input type="text" name="attrRelEntity" class=""
                               value="${attr.Attr.attrRelEntity}">
                    </td>
                    <th><label>参考属性：</label></th>
                    <td>
                        <input type="text" name="attrRelEntiyField" class=""
                               value="${attr.Attr.attrRelEntiyField}">
                    </td>
                </tr>
                <tr>
                    <th><label>存储表：</label></th>
                    <td>
                        <input type="text" name="entitySaveTable" class="required"
                               value="${attr.Attr.entitySaveTable}">
                    </td>
                    <th><label>存储字段：</label></th>
                    <td>
                        <input type="text" name="entitySaveField" class="required"
                               value="${attr.Attr.entitySaveField}">
                    </td>
                    <th><label>数据类型：</label></th>
                    <td>
                        <input type="text" name="entitySaveType" class=""
                               value="${attr.Attr.entitySaveType}">
                    </td>
                    <th><label>默认值：</label></th>
                    <td>
                        <input type="text" name="attrDefaultValue" class=""
                               value="${attr.Attr.attrDefaultValue}">
                    </td>
                </tr>
                <tr>
                    <th><label>支持函数：</label></th>
                    <td>
                        <oframe:select prjCd="${param.prjCd}" type="radio" name="isSupportFunc"
                                       value="${attr.Attr.isSupportFunc}"
                                       itemCd="COMMON_YES_NO"/>
                    </td>
                    <th><label>查询条件：</label></th>
                    <td>
                        <oframe:select prjCd="${param.prjCd}" type="radio" name="canConditionFlag"
                                       value="${attr.Attr.canConditionFlag}"
                                       itemCd="COMMON_YES_NO"/>
                    </td>
                    <th><label>排序条件：</label></th>
                    <td>
                        <oframe:select prjCd="${param.prjCd}" type="radio" name="canOrderFlag"
                                       value="${attr.Attr.canOrderFlag}"
                                       itemCd="COMMON_YES_NO"/>
                    </td>
                    <th><label>检索结果：</label></th>
                    <td>
                        <oframe:select prjCd="${param.prjCd}" type="radio" name="canResultFlag"
                                       value="${attr.Attr.canResultFlag}"
                                       itemCd="COMMON_YES_NO"/>
                    </td>
                </tr>
                <tr>
                    <th><label>监控变化：</label></th>
                    <td>
                        <oframe:select prjCd="${param.prjCd}" type="radio" name="attrValueMonitor"
                                       value="${attr.Attr.attrValueMonitor}"
                                       itemCd="COMMON_YES_NO"/>
                    </td>
                    <th><label>强制删除：</label></th>
                    <td>
                        <oframe:select prjCd="${param.prjCd}" type="radio" name="deleteCaseadeFlag"
                                       value="${attr.Attr.deleteCaseadeFlag}"
                                       itemCd="COMMON_YES_NO"/>
                    </td>
                    <th><label>修改执行规则：</label></th>
                    <td colspan="3">
                        <input type="text" name="attrModifyRule" class=""
                               value="<c:out value='${attr.Attr.attrModifyRule}'/>">
                    </td>
                </tr>
                <tr>
                    <th><label>前端展示样式：</label></th>
                    <td colspan="3">
                        <input type="text" name="attrRuleCss" class=""
                               value="${attr.Attr.attrRuleCss}">
                    </td>
                    <th><label>前端填写描述：</label></th>
                    <td colspan="3">
                        <input type="text" name="attrRuleDesc" class=""
                               value="${attr.Attr.attrRuleDesc}">
                    </td>
                </tr>
                <tr>
                    <th><label>前端填写规则：</label></th>
                    <td colspan="3">
                        <input type="text" name="attrFrontCheckRule" class=""
                               value="${attr.Attr.attrFrontCheckRule}">
                    </td>
                    <th><label>后端校验规则：</label></th>
                    <td colspan="3">
                        <input type="text" name="attrBackCheckRule" class=""
                               value="<c:out value='${attr.Attr.attrBackCheckRule}'/>">
                    </td>
                </tr>
                <tr>
                    <th><label>后端翻译函数：</label></th>
                    <td colspan="3">
                        <input type="text" name="attrValueNameFunc" class=""
                               value="<c:out value='${attr.Attr.attrValueNameFunc}'/>">
                    </td>
                    <th><label>前端检索规则：</label></th>
                    <td colspan="3">
                        <input type="text" name="attrFrontSltRule" class=""
                               value='<c:out value="${attr.Attr.attrFrontSltRule}"/>'>
                    </td>
                </tr>
                <tr>
                    <th><label>属性描述：</label></th>
                    <td colspan="8">
                    <textarea name="entityAttrDesc" rows="4" style="width:90%"><c:out
                            value='${attr.Attr.entityAttrDesc}'/></textarea>
                    </td>
                </tr>
                <tr>
                    <td colspan="8" align="center">
                        <span onclick="sys012.updateAttr(1, this)" class="marr10 btn btn-primary">保存属性</span>
                        <span onclick="sys012.updateAttr(2, this)" class="marr10 btn btn-info">复制新建</span>
                        <span onclick="sys012.addView('attr', '${entityName}','','${usePrjCd}')"
                              class="marr10 btn btn-primary">新建属性</span>
                        <c:if test="${staffId == '0'}">
                            <span class="btn btn-warn marl10" onclick="sys012.deleteInfo('attr');">删除属性</span>
                        </c:if>
                    </td>
                </tr>
            </table>
        </div>
    </form>
</div>