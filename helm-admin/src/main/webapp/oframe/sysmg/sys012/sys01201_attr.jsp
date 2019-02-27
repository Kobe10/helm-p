<%--人地房信息展示页面--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%--房源统计信息--%>
<div class="panel" id="sys012Summary">
    <h1>
        <span>属性定义</span>
    </h1>

    <form id="saveAttr">
        <div layoutH="55">
            <table class="border">
                <tr>
                    <th width="10%"><label>归属实体：</label></th>
                    <td>
                        <input type="hidden" name="usePrjCd" class="required" value="${usePrjCd}">
                        <input type="text" name="entityName" class="required" value="${entityName}">
                    </td>
                    <th width="10%"><label>归属分组：</label></th>
                    <td>
                        <%--<input type="text" name="entityGroupId" name="required" value="">--%>
                        <oframe:select prjCd="${param.prjCd}" collection="${groups}" name="entityGroupId" value=""/>
                    </td>
                    <th width="10%"><label>英文名称：</label></th>
                    <td>
                        <input type="text" name="entityAttrNameEn" class="required"
                               value="">
                    </td>
                    <th width="10%"><label>中文名称：</label></th>
                    <td>
                        <input type="text" name="entityAttrNameCh" class="required"
                               value="">
                    </td>

                </tr>
                <tr>
                    <th><label>一对多：</label></th>
                    <td>
                        <oframe:select prjCd="${param.prjCd}" collection="${mutiAttrFlagMap}" name="mutiAttrFlag"/>
                    </td>
                    <th><label>接受变量：</label></th>
                    <td>
                        <oframe:select prjCd="${param.prjCd}" collection="${muti}" name="attrCanVariable"/>
                    </td>
                    <th><label>参考实体：</label></th>
                    <td>
                        <input type="text" name="attrRelEntity" class=""
                               value="">
                    </td>
                    <th><label>参考属性：</label></th>
                    <td>
                        <input type="text" name="attrRelEntiyField" class=""
                               value="">
                    </td>
                </tr>
                <tr>
                    <th><label>存储表：</label></th>
                    <td>
                        <input type="text" name="entitySaveTable" class="required"
                               value="">
                    </td>
                    <th><label>存储字段：</label></th>
                    <td>
                        <input type="text" name="entitySaveField" class="required"
                               value="">
                    </td>
                    <th><label>数据类型：</label></th>
                    <td>
                        <input type="text" name="entitySaveType" class=""
                               value="">
                    </td>
                    <th><label>默认值：</label></th>
                    <td>
                        <input type="text" name="attrDefaultValue" class=""
                               value="">
                    </td>
                </tr>
                <tr>
                    <th><label>监控变化：</label></th>
                    <td>
                        <oframe:select prjCd="${param.prjCd}" type="radio" name="attrValueMonitor"
                                       value=""
                                       itemCd="COMMON_YES_NO"/>
                    </td>
                    <th><label>支持函数：</label></th>
                    <td>
                        <oframe:select prjCd="${param.prjCd}" type="radio" name="isSupportFunc"
                                       value=""
                                       itemCd="COMMON_YES_NO"/>
                    </td>
                    <th><label>排序条件：</label></th>
                    <td>
                        <oframe:select prjCd="${param.prjCd}" type="radio" name="canOrderFlag"
                                       value=""
                                       itemCd="COMMON_YES_NO"/>
                    </td>
                    <th><label>检索结果：</label></th>
                    <td>
                        <oframe:select prjCd="${param.prjCd}" type="radio" name="canResultFlag"
                                       value=""
                                       itemCd="COMMON_YES_NO"/>
                    </td>
                </tr>
                <tr>
                    <th><label>强制删除：</label></th>
                    <td>
                        <oframe:select prjCd="${param.prjCd}" type="radio" name="deleteCaseadeFlag"
                                       value=""
                                       itemCd="COMMON_YES_NO"/>
                    </td>
                    <th><label>查询条件：</label></th>
                    <td>
                        <oframe:select prjCd="${param.prjCd}" type="radio" name="canConditionFlag"
                                       value=""
                                       itemCd="COMMON_YES_NO"/>
                    </td>
                    <th><label>修改执行规则：</label></th>
                    <td colspan="3">
                        <input type="text" name="attrModifyRule" class=""
                               value="<c:out value=''/>">
                    </td>
                </tr>
                <tr>
                    <th><label>前端填写样式：</label></th>
                    <td colspan="3">
                        <input type="text" name="attrRuleCss" class=""
                               value="">
                    </td>
                    <th><label>前端填写描述：</label></th>
                    <td colspan="3">
                        <input type="text" name="attrRuleDesc" class=""
                               value="">
                    </td>
                </tr>
                <tr>
                    <th><label>前端填写规则：</label></th>
                    <td colspan="3">
                        <input type="text" name="attrFrontCheckRule" class=""
                               value=>
                    </td>
                    <th><label>后端执行规则：</label></th>
                    <td colspan="3">
                        <input type="text" name="attrBackCheckRule" class=""
                               value="<c:out value=''/>">
                    </td>
                </tr>
                <tr>
                    <th><label>后端翻译函数：</label></th>
                    <td colspan="3">
                        <input type="text" name="attrValueNameFunc" class=""
                               value="<c:out value=''/>">
                    </td>
                    <th><label>前段检索规则：</label></th>
                    <td colspan="3">
                        <input type="text" name="attrFrontSltRule" class=""
                               value=>
                    </td>
                </tr>
                <tr>
                    <th><label>属性描述：</label></th>
                    <td colspan="8">
                    <textarea name="entityAttrDesc" rows="4" style="width:80%"><c:out
                            value=''/></textarea>
                    </td>
                </tr>
                <tr>
                    <td colspan="8" align="center">
                        <span onclick="sys012.addAttr('saveAttr');" class="btn btn-primary">保存</span>
                    </td>
                </tr>
            </table>
        </div>
    </form>
</div>