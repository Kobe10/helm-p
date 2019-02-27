<%--人地房信息展示页面--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="mar5" style="vertical-align: top;">
    <oframe:power prjCd="${param.prjCd}" rhtCd="edit_model_rht">
        <input type="hidden" name="editAble" value="1"/>
    </oframe:power>
    <oframe:power prjCd="${param.prjCd}" rhtCd="del_model_rht">
        <input type="hidden" name="removeAble" value="1"/>
    </oframe:power>
    <oframe:power prjCd="${param.prjCd}" rhtCd="move_model_rht">
        <input type="hidden" name="moveAble" value="1"/>
    </oframe:power>

    <%--左侧导航树--%>
    <div style="width: 330px;float: left;position: relative;" id="sys012DivTree" layoutH="15" class="panel left_menu">
        <h1>
            <span>业务模型</span>
            <span onclick="sys012.openImport(this);" class="panel_menu js_reload">导入</span>
            <span onclick="sys012.addView('entity');" class="panel_menu js_reload">新建</span>
        </h1>

        <table width="100%;">
            <tr>
                <td style="width: 30%;">
                    <select name="schType" style="float: left; width: 100%;">
                        <option value="entityAttrNameCh">属性中文名</option>
                        <option value="entityAttrNameEn">属性英文名</option>
                    </select>
                </td>
                <td>
                    <input type="text" name="schValue" class="required noErrorTip"
                           onkeydown="if(event.keyCode == 13){sys012.quickSch();}"
                           style="width: 100%;height:26px;min-width:inherit;">
                </td>
                <td style="width: 30px;">
                    <a title="选择" onclick="sys012.quickSch();" style="height: 30px;left:6px;" class="btnLook">选择</a>
                </td>
            </tr>
        </table>
        <form id="sys012QueryForm">
            <input type="hidden" name="entityName" value="SysEntityGroupAttrDef"/>
            <input type="hidden" name="pageSize" value="16">
            <input type="hidden" name="conditionName" value="">
            <input type="hidden" name="condition" value="">
            <input type="hidden" name="conditionValue" value="">
            <input type="hidden" name="sortColumn" value="">
            <input type="hidden" name="sortOrder" value="">
            <input type="hidden" name="cmptName" value="QUERY_ENTITY_PAGE_DATA">
            <input type="hidden" class="js_conditionValue" value="">
            <input type="hidden" name="divId" value="sys012AttrList">
            <input type="hidden" name="resultField" value="entityAttrNameEn,entityAttrNameCh,prjCd">
            <%--必须包含的字段--%>
            <input type="hidden" name="forceResultField" value="entityGroupId,entityName">
            <input type="hidden" name="forward" id="forward" value="/oframe/sysmg/sys012/sys012_quick_list"/>
        </form>
        <div class="js_page" id="sys012AttrList" style="position: absolute;width: 100%;z-index: 20;"></div>

        <ul id="sys012Tree" class="ztree" layoutH="100"></ul>
    </div>

    <%--中间自定义分割线--%>
    <div class="split_line" layoutH="15">
        <span class="split_but" title="展开/隐藏导航"></span>
    </div>

    <%--右侧自定义面板--%>
    <div id="sys012Context" style="margin-left: 340px;margin-right: 5px;position: relative;"></div>
</div>
<oframe:script src="${pageContext.request.contextPath}/oframe/sysmg/sys012/js/sys012.js" type="text/javascript"/>
