<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="mar5" style="vertical-align: top;">
    <input type="hidden" name="editAble" value="1"/>
    <input type="hidden" name="removeAble" value="1"/>
    <%--左侧导航树--%>
    <div style="width: 280px;float: left;position: relative;" id="wf005TreeDiv" layoutH="15" class="panel left_menu">
        <h1>
            <span class="panel_title">表单分类</span>
        </h1>
        <ul id="wf005Tree" layoutH="65" class="ztree"></ul>
    </div>
    <div class="split_line" layoutH="15">
        <span class="split_but" title="展开/隐藏导航"></span>
    </div>
    <%--右侧自定义画板--%>
    <div id="wf005RightDiv" style="margin-left: 290px;margin-right: 5px;position: relative;">
        <div class="tabs mart5 ">
            <div class="tabsHeader">
                <div class="tabsHeaderContent">
                    <ul>
                        <li class="selected" onclick="wf005.query('publish');">
                            <a href="javascript:void(0);"><span>表单发布区</span></a>
                        </li>
                        <li onclick="wf005.query('design');">
                            <a href="javascript:void(0);"><span>表单设计区</span></a>
                        </li>
                    </ul>
                </div>
            </div>
            <div class="tabsContent" style="padding: 0;border-top: 1px solid #3d91c8;">
                <div id="publish_div" class="publish_div" style="position: relative;">
                    <div class="panelBar">
                        <ul class="toolBar">
                            <li onclick="wf005.openQSch('publish');">
                                <a class="find" href="javascript:void(0)"><span>条件检索</span></a>
                            </li>
                            <li onclick="wf005.query('publish');">
                                <a class="reflesh" href="javascript:void(0);"><span>刷新检索</span></a>
                            </li>
                            <li onclick="wf005.exportForm('publish')">
                                <a class="export" href="javascript:void(0);"><span>批量导出</span></a>
                            </li>
                            <oframe:op prjCd="${param.prjCd}" template="li" cssClass="delete"
                                       name="批量删除" rhtCd="del_pub_fm_rht"
                                       onClick="wf005.deleteForm('wf005_page_data','publish');"/>
                        </ul>
                    </div>
                    <form id="wf005fmpublish" method="post">
                        <%-- 分页展示 --%>
                        <input type="hidden" name="entityName" value="SysFormInfoDef"/>
                        <input type="hidden" name="conditionName" value="formStatus">
                        <input type="hidden" name="condition" value="=">
                        <input type="hidden" name="conditionValue" value="1">
                        <input type="hidden" name="sortColumn" value="formStatuDate,formVersion">
                        <input type="hidden" name="sortOrder" value="desc,desc">
                        <input type="hidden" name="divId" value="wf005_page_data">
                        <input type="hidden" name="forceResultField" value="">
                        <input type="hidden" class="js_conditionValue" value="">
                        <input type="hidden" name="cmptName" id="cmptName" value="QUERY_FORM_INFO_BY_TYPE_CMPT"/>
                        <input type="hidden" name="resultField"
                               value="formCd,formVersion,formName,formType,formDesc,formStatuDate">
                        <input type="hidden" name="forward" id="forward" value="/oframe/wf/wf005/wf005_list"/>

                        <div id="wf005publish" class="hidden"
                             style="position: absolute;width:100%;top: 0px;z-index: 100;background-color: #ffffff; margin-top: 40px;">
                            <div class="triangle triangle-up"
                                 style="border-bottom-color: #2c7bae;top: -10px; left: 25px;"></div>
                            <table class="border">
                                <tr>
                                    <th><label>表单编码：</label></th>
                                    <td>
                                        <input name="formCd" type="text" condition="like"/>
                                    </td>
                                    <th><label>表单名称：</label></th>
                                    <td>
                                        <input name="formName" type="text" condition="like"/>
                                        <input name="formTypeId" type="hidden" condition="="/>
                                    </td>
                                    <th>
                                        <lable>表单描述：</lable>
                                    </th>
                                    <td>
                                        <input name="formDesc" type="text" condition="like"/>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="8" align="center">
                                        <button onclick="wf005.query('publish');wf005.closeQSch('publish');"
                                                type="button"
                                                id="schBtn" class="js_faTask btn btn-primary">
                                            查询
                                        </button>
                                        <button onclick="wf005.closeQSch('publish')" type="button"
                                                class="btn btn-info">关闭
                                        </button>
                                    </td>
                                </tr>
                            </table>
                        </div>
                    </form>
                    <div class="js_page" id="wf005_page_data"></div>
                </div>
                <div id="design_div" class="design_div" style="position: relative;">
                    <div class="panelBar">
                        <ul class="toolBar">
                            <li onclick="wf005.openQSch('design');">
                                <a class="find" href="javascript:void(0)"><span>条件检索</span></a>
                            </li>
                            <li onclick="wf005.query('design');">
                                <a class="reflesh" href="javascript:void(0);"><span>刷新检索</span></a>
                            </li>
                            <li onclick="wf005.designForm('new','');">
                                <a class="add" href="javascript:void(0)"><span>新建表单</span></a>
                            </li>
                            <li>
                                <a class="import" href="javascript:void(0)">
                                    <span onclick="wf005.importForm(this);">
                                        <input style="width:70px; position:absolute;margin-top: 5px;
                                                 opacity:0;filter:alpha(opacity=0);z-index:11; cursor: pointer"
                                               id="wf005ImportForms" name="wf005ImportForms" type="file">
                                        批量导入
                                    </span>
                                </a>
                            </li>
                            <li onclick="wf005.deleteForm('wf00502_page_data','design');">
                                <a class="delete" href="javascript:void(0);"><span>批量删除</span></a>
                            </li>
                        </ul>
                    </div>
                    <form id="wf005fmdesign" method="post">
                        <%-- 分页展示 --%>
                        <input type="hidden" name="entityName" value="SysFormInfoDef"/>
                        <input type="hidden" name="conditionName" value="formStatus">
                        <input type="hidden" name="condition" value="=">
                        <input type="hidden" name="conditionValue" value="0">
                        <input type="hidden" name="sortColumn" value="formStatuDate">
                        <input type="hidden" name="sortOrder" value="desc">
                        <input type="hidden" name="divId" value="wf00502_page_data">
                        <input type="hidden" name="forceResultField" value="">
                        <input type="hidden" class="js_conditionValue" value="">
                        <input type="hidden" name="cmptName" id="cmptName" value="QUERY_FORM_INFO_BY_TYPE_CMPT"/>
                        <input type="hidden" name="resultField"
                               value="formCd,formName,formType,formDesc,formStatuDate">
                        <input type="hidden" name="forward" id="forward" value="/oframe/wf/wf005/wf005_list"/>

                        <div id="wf005design" class="hidden"
                             style="position: absolute;width:100%;top: 0px;z-index: 100;background-color: #ffffff; margin-top: 40px;">
                            <div class="triangle triangle-up"
                                 style="border-bottom-color: #2c7bae;top: -10px; left: 25px;"></div>
                            <table class="border">
                                <tr>
                                    <th><label>表单编码：</label></th>
                                    <td>
                                        <input name="formCd" type="text" condition="like"/>
                                    </td>
                                    <th><label>表单名称：</label></th>
                                    <td>
                                        <input name="formName" type="text" condition="like"/>
                                        <input name="formTypeId" type="hidden" condition="="/>
                                    </td>
                                    <th>
                                        <lable>表单描述：</lable>
                                    </th>
                                    <td>
                                        <input name="formDesc" type="text" condition="like"/>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="8" align="center">
                                        <button onclick="wf005.query('design');wf005.closeQSch('design');"
                                                type="button"
                                                class="js_faTask btn btn-primary">查询
                                        </button>
                                        <button onclick="wf005.closeQSch('design')" type="button"
                                                class="btn btn-info">关闭
                                        </button>
                                    </td>
                                </tr>
                            </table>
                        </div>
                    </form>
                    <div class="js_page" id="wf00502_page_data"></div>
                </div>
            </div>
        </div>
    </div>
</div>
<oframe:script type="text/javascript" src="${pageContext.request.contextPath}/oframe/wf/wf005/js/wf005.js"/>