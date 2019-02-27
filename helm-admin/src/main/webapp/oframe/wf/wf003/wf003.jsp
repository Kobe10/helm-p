<%--待办任务列表--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>

<div class="mar5">
    <%--控制树可以编辑和删除--%>
    <input type="hidden" name="editAble" value="1"/>
    <input type="hidden" name="removeAble" value="1"/>
    <%--左侧树--%>
        <div style="width: 280px;float: left;position: relative;" id="wf003TreeDiv" layoutH="15"
             class="panel left_menu">
            <h1>
                <span class="panel_title">流程分类</span>
            </h1>
            <ul id="wf003Tree" layoutH="65" class="ztree"></ul>
        </div>
        <div class="split_line" layoutH="15">
            <span class="split_but" title="展开/隐藏导航"></span>
        </div>
    <%--右侧区域--%>
    <div id="wf003RightDiv" style="margin-left: 290px;margin-right: 5px;position: relative;">
        <div class="tabs">
            <div class="tabsHeader">
                <div class="tabsHeaderContent">
                    <ul>
                        <li class="selected" onclick="wf003.loadTabContext(1);">
                            <a hr]ef="javascript:void(0);"><span>流程发布区</span></a>
                        </li>
                        <li onclick="wf003.loadTabContext(2);">
                            <a href="javascript:void(0);"><span>流程设计区</span></a>
                        </li>
                        <li onclick="wf003.loadTabContext(3);">
                            <a href="javascript:void(0);"><span>新建流程</span></a>
                        </li>
                    </ul>
                </div>
            </div>
            <div class="tabsContent">
                <%--流程发布区--%>
                <form id="wf003fmPublish" method="post">
                    <%--自定义查询组件--%>
                    <input type="hidden" name="selfDefCmpt" value="QUERY_PROC_DEF_LIST_PAGE"/>
                    <%--结果推送页面--%>
                    <input type="hidden" name="forward" id="forwrad" value="/oframe/wf/wf003/wf003_list"/>

                    <div style="position: relative;">
                        <div class="panelBar">
                            <ul class="toolBar">
                                <li onclick="wf003.openQSch('1');">
                                    <a class="find" href="javascript:void(0);"><span>检索</span></a>
                                </li>
                                <li onclick="wf003.loadTabContext(1);">
                                    <a class="reflesh" href="javascript:void(0);"><span>刷新</span></a>
                                </li>
                                <oframe:op prjCd="${param.prjCd}" template="li" cssClass="delete-more"
                                           name="批量删除" rhtCd="del_all_act_rht"
                                           onClick="wf003.batchDelete('publish');"/>
                                <li onclick="wf003.exportLc('publish');">
                                    <a class="export" href="javascript:void(0);"><span>批量导出</span></a>
                                </li>
                            </ul>
                        </div>
                        <div id="wf003create1" class="hidden"
                             style="position: absolute;width:100%;top: 0px;z-index: 100;background-color: #ffffff; margin-top: 40px;">
                            <table class="border">
                                <tr>
                                    <th>
                                        <lable>流程分类：</lable>
                                    </th>
                                    <td>
                                        <input type="text" name="category"/>
                                        <input name="actTypeCd" type="hidden"/>
                                    </td>
                                    <th>
                                        <lable>流程名称：</lable>
                                    </th>
                                    <td>
                                        <input type="text" name="name"/>
                                    </td>
                                    <th>
                                        <lable>流程定义：</lable>
                                    </th>
                                    <td>
                                        <input type="text" name="key"/>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="8" align="center">
                                        <button onclick="Query.queryList('wf003fmPublish', 'wf003_page_published', '');wf003.closeQSch()"
                                                type="button"
                                                class="js_faTask btn btn-primary">
                                            查询
                                        </button>
                                        <button onclick="wf003.closeQSch()" type="button" class="btn btn-info">关闭
                                        </button>
                                    </td>
                                </tr>
                            </table>
                        </div>
                        <div id="wf003_page_published" class="result">
                            <table class="table" width="100%" layoutH="165">
                                <thead>
                                <tr>
                                    <th width="5%" fieldName="newId">
                                        <input type="checkbox" class="checkboxCtrl" group="ids"/>
                                    </th>
                                    <th>流程名称</th>
                                    <th>流程定义</th>
                                    <th>流程分类</th>
                                    <th>流程描述</th>
                                    <th width="10%"><label>操作</label></th>
                                </tr>
                                </thead>
                                <tbody>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </form>
                <%--流程设计区--%>
                <form id="wf003fmDesign" method="post">
                    <%--自定义查询组件--%>
                    <input type="hidden" name="selfDefCmpt" value="QUERY_MODEL_DEF_LIST_PAGE"/>
                    <%--结果推送页面--%>
                    <input type="hidden" name="forward" id="forwrad" value="/oframe/wf/wf003/wf003_list"/>
                    <div style="position: relative;">
                        <div class="panelBar">
                            <ul class="toolBar">
                                <li onclick="wf003.openQSch('2');">
                                    <a class="find" href="javascript:void(0);"><span>检索</span></a>
                                </li>
                                <li onclick="wf003.loadTabContext(2);">
                                    <a class="reflesh" href="javascript:void(0);"><span>刷新</span></a>
                                </li>
                                <oframe:op prjCd="${param.prjCd}" template="li" cssClass="delete-more"
                                           name="批量删除" rhtCd="del_all_act_rht"
                                           onClick="wf003.batchDelete('design');"/>
                                <li onclick="wf003.exportLc('design');">
                                    <a class="export" href="javascript:void(0);"><span>批量导出</span></a>
                                </li>
                            </ul>
                        </div>
                        <div id="wf003create2" class="hidden"
                             style="position: absolute;width:100%;top: 0px;z-index: 100;background-color: #ffffff; margin-top: 40px;">
                            <table class="border">
                                <tr>
                                    <th>
                                        <lable>流程分类：</lable>
                                    </th>
                                    <td>
                                        <input type="text" name="category"/>
                                        <input name="actTypeCd" type="hidden"/>
                                    </td>
                                    <th>
                                        <lable>流程名称：</lable>
                                    </th>
                                    <td>
                                        <input type="text" name="name"/>
                                    </td>
                                    <th>
                                        <lable>流程定义：</lable>
                                    </th>
                                    <td>
                                        <input type="text" name="key" placeholder="此处不支持模糊查询"/>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="8" align="center">
                                        <button onclick="Query.queryList('wf003fmDesign', 'wf003_page_design', '');wf003.closeQSch('2')"
                                                type="button"
                                                class="js_faTask btn btn-primary">
                                            查询
                                        </button>
                                        <button onclick="wf003.closeQSch('2')" type="button" class="btn btn-info">关闭
                                        </button>
                                    </td>
                                </tr>
                            </table>
                        </div>
                        <div id="wf003_page_design" class="result">
                            <table class="table" width="100%" layoutH="165">
                                <thead>
                                <tr>
                                    <th width="5%" fieldName="newId">
                                        <input type="checkbox" class="checkboxCtrl" group="ids"/>
                                    </th>
                                    <th>流程名称</th>
                                    <th>流程定义</th>
                                    <th>流程分类</th>
                                    <th>流程描述</th>
                                    <th width="10%"><label>操作</label></th>
                                </tr>
                                </thead>
                                <tbody>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </form>
                <%--新建流程区展示--%>
                <div style="position: relative;">
                    <div class="panelBar">
                        <ul class="toolBar">
                            <li><a class="save" onclick="wf003.doDeploy();"><span>保存流程</span></a></li>
                        </ul>
                    </div>
                    <div>
                        <input name="actTypeCd" id="actTypeCd" type="hidden" value=""/>
                        <form id="wf003fmadd" method="post" class="required-validate entermode">
                            <div layoutH="90">
                                <table class="border">
                                    <tr>
                                        <th style="width: 15%"><label>流程名称：</label></th>
                                        <td><input type="text" class="required" maxlength="30" name="deployName"/></td>
                                        <th style="width: 15%"><label>流程分类：</label></th>
                                        <td><input type="text" class="required" maxlength="30" name="procDepCategory"/>
                                        </td>
                                        <th style="width: 15%"><label>流程定义：</label></th>
                                        <td><input type="text" class="required" maxlength="30" name="key"/></td>
                                    </tr>
                                    <tr>
                                        <th><label>流程描述：</label></th>
                                        <td colspan="5"><input type="text" class="required" maxlength="30"
                                                               name="description"/></td>
                                    </tr>
                                    <tr>
                                        <th><label>定义文件：</label></th>
                                        <td colspan="5"><input type="file" name="wfFile" id="wfFile"
                                                               accept=".zip,.xml"/></td>
                                    </tr>
                                </table>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<oframe:script type="text/javascript" src="${pageContext.request.contextPath}/oframe/wf/wf003/js/wf003.js"/>
