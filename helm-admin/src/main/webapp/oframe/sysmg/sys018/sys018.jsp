<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>

<div class="mar5">
    <div style="position: relative;">
        <div class="panelBar">
            <ul class="toolBar">
                <li onmouseover="sys018.openQSch(this);"
                    onclick="sys018.queryHs(this);">
                    <a class="find" href="javascript:void(0)"><span>指标检索</span></a>
                </li>
                <li onclick="sys018.query();">
                    <a class="reflesh" href="javascript:void(0)"><span>刷新检索</span></a>
                </li>
                <li onclick="sys018.editView('','0',this);">
                    <a class="add" href="javascript:void(0)"><span>新增指标</span></a>
                </li>
                <li>
                    <a onclick="sys018.batchDeleteView();" class="delete-more"><span>批量删除</span></a>
                </li>
                <li>
                    <a class="export" onclick="sys018.exportEntity()"><span>导出配置</span></a>
                </li>
                <li>
                    <a class="import">
                        <span onclick="sys018.startImport(this);" style="position: relative;">导入配置
                              <input style="width:110px; height:37px; position:absolute; right:0; top:0; opacity:0;filter:alpha(opacity=0); z-index:11; cursor:pointer;"
                                     name="importJobFile" id="importJobFile" type="file">
                        </span>
                    </a>
                </li>
            </ul>
        </div>
        <div>
            <div id="sys018create" class="hidden"
                 style="position: absolute;width:100%;top: 0px;z-index: 100;background-color: #ffffff; margin-top: 40px;">
                <table class="border">
                    <tr>
                        <th>
                            <lable>指标编码：</lable>
                        </th>
                        <td>
                            <input type="text" name="jobCd" condition="like"/>
                        </td>
                        <th>
                            <lable>指标名称：</lable>
                        </th>
                        <td>
                            <input type="text" name="jobName" condition="like"/>
                        </td>
                        <th>
                            <lable>指标分组：</lable>
                        </th>
                        <td>
                            <input type="text" name="jobGroupName" condition="like"/>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="8" align="center">
                            <button onclick="sys018.query();sys018.closeQSch()" type="button" id="schBtn"
                                    class="js_faTask btn btn-primary">
                                查询
                            </button>
                            <button onclick="sys018.closeQSch(false)" type="button" class="btn btn-info">关闭</button>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
        <form id="sys018form" method="post">
            <input type="hidden" name="entityName" value="PrjJobDef"/>
            <input type="hidden" name="conditionName" value="jobCd">
            <input type="hidden" name="condition" value="=">
            <input type="hidden" name="conditionValue" value="">
            <input type="hidden" name="sortColumn" value="createDate">
            <input type="hidden" name="sortOrder" value="desc">
            <input type="hidden" name="divId" value="sys018_page_data">
            <input type="hidden" name="forceResultField" value="jobCd,prjCd">
            <input type="hidden" class="js_conditionValue" value="">
            <input type="hidden" name="cmptName" id="cmptName" value="QUERY_ENTITY_PAGE_DATA"/>
            <input type="hidden" name="resultField"
                   value="jobName,prjCd,jobCd,jobGroupName,qryService,qryParam03,createDate">
            <input type="hidden" name="forward" id="forward" value="/oframe/sysmg/sys018/sys018_list"/>
        </form>
        <div id="sys018_page_data" class="js_page" layoutH="55"></div>
    </div>
</div>
<oframe:script type="text/javascript" src="${pageContext.request.contextPath}/oframe/sysmg/sys018/js/sys018.js"/>
