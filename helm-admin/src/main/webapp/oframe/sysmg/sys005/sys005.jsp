<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>

<div class="mar5">
    <div style="position: relative;" class="js_sys005">
        <div class="panelBar">
            <ul class="toolBar">
                <li onclick="sys005.openQSch(this);">
                    <a class="find" href="javascript:void(0)"><span>检索参数</span></a>
                </li>
                <li onclick="Query.queryList('sys005frm', 'sys005_list_print');">
                    <a class="reflesh" href="javascript:void(0)"><span>刷新检索</span></a>
                </li>
                <oframe:op prjCd="${param.prjCd}" template="li" cssClass="add" name="添加参数"
                           rhtCd="ADD_SYS_CFG_RHT" onClick="sys005.openAdd()"/>
                <oframe:op prjCd="${param.prjCd}" template="li" cssClass="export" name="导出参数"
                           rhtCd="EXP_SYS_CFG_RHT" onClick="sys005.exportXml(this);"/>
                <oframe:op prjCd="${param.prjCd}" template="li" cssClass="delete" name="批量删除"
                           rhtCd="EDIT_SYS_CFG_RHT" onClick="sys005.batchDel(this);"/>
                <oframe:power prjCd="${param.prjCd}" rhtCd="EXP_SYS_CFG_RHT">
                    <li style="position: relative;">
                        <a class="import" title="导入配置参数">
                            <form id="sys005ImportForm">
                                <span onclick="sys005.startImport(this);">导入参数
                                    <input style="width:80px; height:30px; position:absolute; left:8px; top:0;
                                             opacity:0;filter:alpha(opacity=0);z-index:11; cursor: pointer"
                                           id="importSysCfg" name="importSysCfgFile" type="file">
                                </span>
                            </form>
                        </a>
                    </li>
                </oframe:power>
            </ul>
        </div>

        <div>
            <div id="sys005create" class="hidden"
                 style="position: absolute;width:100%;top: 0px;z-index: 100;background-color: #ffffff; margin-top: 40px;">
                <form id="sys005frm" method="post" class="entermode">
                    <%--自服务名称用于定位SQL--%>
                    <input type="hidden" name="subSvcName" value="sys005"/>
                    <%-- 分页展示 --%>
                    <input type="hidden" name="forward" id="forward" value="/oframe/sysmg/sys005/sys005_list"/>

                    <table class="border">
                        <tr>
                            <th width="10%">
                                <lable>参数名称：</lable>
                            </th>
                            <td width="15%">
                                <input type="text" name="itemName" condition="like"/>
                            </td>
                            <th width="10%">
                                <lable>参数编码：</lable>
                            </th>
                            <td width="15%">
                                <input type="text" name="itemCd" condition="like"/>
                            </td>
                            <th width="10%">
                                <lable>适用项目：</lable>
                            </th>
                            <td width="15%">
                                <oframe:select collection="${prjCdMap}" cssClass="textInput" name="cfgPrjCd"
                                               withEmpty="true"/>
                            </td>
                            <th width="10%">
                                <lable>参数状态：</lable>
                            </th>
                            <td width="15%">
                                <oframe:select prjCd="${param.prjCd}" cssClass="textInput" itemCd="COM_STATUS_CD"
                                               name="statusCd"/>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="8" align="center">
                                <button onclick="Query.queryList('sys005frm', 'sys005_list_print');sys005.closeQSch()"
                                        type="button" id="schBtn"
                                        class="js_faTask btn btn-primary">
                                    检索
                                </button>
                                <button onclick="sys005.closeQSch(false)" type="button" class="btn btn-info">关闭</button>
                            </td>
                        </tr>
                    </table>
                </form>
            </div>
        </div>

        <div id="sys005_list_print">
            <table class="table" width="100%" layoutH="130">
                <thead>
                <tr>
                    <th width="22"><input type="checkbox" group="ids" class="checkboxCtrl"></th>
                    <th width="30">序号</th>
                    <th width="120" onclick="Query.sort('sys005_list_print', this);" sortColumn="item_cd"
                        class="sortable">参数编码
                    </th>
                    <th width="50" onclick="Query.sort('sys005_list_print', this);" sortColumn="prj_cd"
                        class="sortable">适用项目
                    </th>
                    <th width="120" onclick="Query.sort('sys005_list_print', this);" sortColumn="item_name"
                        class="sortable">参数名称
                    </th>
                    <th width="60">适用项目类型</th>
                    <th width="50">状态</th>
                    <th width="120" onclick="Query.sort('sys005_list_print', this);"
                        sortColumn="status_date" defaultSort="desc" class="sortable">状态时间
                    </th>
                    <th width="80">操作</th>
                </tr>
                </thead>
                <tbody>
                </tbody>
            </table>
        </div>
        <c:import url="/oframe/common/page/pager_fotter.jsp" charEncoding="UTF-8"></c:import>
    </div>
</div>
<oframe:script src="${pageContext.request.contextPath}/oframe/sysmg/sys005/js/sys005.js" type="text/javascript"/>