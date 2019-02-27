<%--人地房信息展示页面--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%--房源统计信息--%>
<div class="panel" id="sys012Summary">
    <h1>
        <span>实体定义</span>
        <a onclick="sys012.extendOrClosePanel(this)" class="collapsable"></a>
    </h1>

    <div>
        <form id="sys012update">
            <input type="hidden" name="oldEntityDefId" value="${entityInfo.EntityInfo.entityDefId}">
            <input type="hidden" name="oldEntityName" value="${entityInfo.EntityInfo.entityName}">
            <input type="hidden" name="usePrjCd" value="${entityInfo.EntityInfo.prjCd}">
            <input type="hidden" name="nodeId" value="${id}">

            <table class="border">
                <tr>
                    <th><label>英文名称：</label></th>
                    <td>
                        <input type="text" name="entityName" class="required"
                               value="${entityInfo.EntityInfo.entityName}">
                    </td>
                    <th><label>中文名称：</label></th>
                    <td>
                        <input type="text" name="entityDesc" class="required"
                               value="${entityInfo.EntityInfo.entityDesc}">
                    </td>
                    <th><label>业务主键：</label></th>
                    <td>
                        <input type="text" name="primaryField" class="required"
                               value="${entityInfo.EntityInfo.primaryField}">
                    </td>
                    <th><label>业务序列：</label></th>
                    <td>
                        <input type="text" name="primarySeqName" class="required"
                               value="${entityInfo.EntityInfo.primarySeqName}">
                    </td>
                </tr>
                <tr>
                    <th><label>访问地址：</label></th>
                    <td colspan="7">
                        <input type="text" name="dataViewUrl" class=""
                               value="${entityInfo.EntityInfo.dataViewUrl}">
                    </td>
                </tr>
                <tr>
                    <td colspan="8" align="center">
                        <span onclick="sys012.updateEntity('${id}', this)" class="btn btn-primary">保存实体</span>
                            <span onclick="sys012.addView('entity', '', '','${entityInfo.EntityInfo.prjCd}')"
                                  class="btn btn-info marl5">新建实体</span>
                            <span class="btn btn-primary marl5"
                                  onclick="sys012.exportEntity('${entityInfo.EntityInfo.prjCd}',this);">
                                导出实体
                            </span>
                        <c:if test="${staffId == '0'}">
                            <span class="btn btn-warn marl5" onclick="sys012.deleteInfo('entity');">删除实体</span>
                        </c:if>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<%--实体属性--%>
<div class="mart5" style="position: relative;">
    <div class="panelBar">
        <ul class="toolBar">
            <li onclick="Query.queryList('sys012EditFrm', 'sys012EntityList');">
                <a class="reflesh" href="javascript:void(0)"><span>刷新</span></a>
            </li>
            <li><a class="print" href="javascript:$.printBox('sys012EntityList')"><span>打印</span></a></li>
        </ul>
    </div>
    <input type="hidden" value="${sltRegId}" name="sltRegId">

    <div style="display: none;">
        <form id="sys012EditFrm" method="post">
            <%--实体查询条件--%>
            <input type="hidden" name="entityName" value="${entityInfo.EntityInfo.entityName}">
            <%--自服务名称用于定位SQL--%>
            <input type="hidden" name="subSvcName" value="entity001"/>
            <input type="hidden" name="currPrjCd" value="${entityInfo.EntityInfo.prjCd}"/>
            <%-- 分页展示 --%>
            <input type="hidden" name="forward" id="forward" value="/oframe/sysmg/sys012/sys012_attr_list"/>
        </form>
    </div>
    <div id="sys012EntityList">
        <table class="table" width="98%" layoutH="290">
            <thead>
            <tr>
                <th>序号</th>
                <th onclick="Query.sort('sys012EntityList', this);"
                    sortColumn="entity_group_id" class="sortable">属性分组
                </th>
                <th onclick="Query.sort('sys012EntityList', this);"
                    sortColumn="entity_attr_name_en" class="sortable">属性英文名称
                </th>
                <th onclick="Query.sort('sys012EntityList', this);"
                    sortColumn="entity_attr_name_ch" class="sortable">属性中文名称
                </th>
                <th>属性描述</th>
            </tr>
            </thead>
            <tbody>

            </tbody>
        </table>
    </div>
    <c:import url="/oframe/common/page/pager_fotter.jsp" charEncoding="UTF-8"/>
</div>
<script type="text/javascript">
    $(document).ready(function () {
        // 检索项目属性
        Query.queryList('sys012EditFrm', 'sys012EntityList', "sys012.adjustHeight();");
    });
</script>