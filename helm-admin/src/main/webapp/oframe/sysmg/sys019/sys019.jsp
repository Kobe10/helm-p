<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>

<div class="mar5">
    <div style="position: relative;">
        <div class="panelBar">
            <ul class="toolBar">
                <li onclick="sys019.openQSch(this);">
                    <a class="find" href="javascript:void(0)"><span>消息检索</span></a>
                </li>
                <li onclick="sys019.query();">
                    <a class="reflesh" href="javascript:void(0)"><span>刷新检索</span></a>
                </li>
                <li onclick="sys019.editView('',this);">
                    <a class="add" href="javascript:void(0)"><span>新增消息</span></a>
                </li>
                <li>
                    <a onclick="sys019.batchDeleteView();" class="delete-more"><span>批量删除</span></a>
                </li>
                <li>
                    <a class="export" onclick="sys019.exportEntity()"><span>导出配置</span></a>
                </li>
                <li>
                    <a class="import">
                        <span onclick="sys019.startImport(this);" style="position: relative;">导入配置
                              <input style="width:110px; height:37px; position:absolute; right:0; top:0; opacity:0;filter:alpha(opacity=0); z-index:11; cursor:pointer;"
                                     name="importMsgFile" id="importMsgFile" type="file">
                        </span>
                    </a>
                </li>
            </ul>
        </div>
        <div>
            <div id="sys019create" class="hidden"
                 style="position: absolute;width:100%;top: 0px;z-index: 100;background-color: #ffffff; margin-top: 40px;">
                <table class="border">
                    <tr>
                        <th>
                            <lable>消息类型：</lable>
                        </th>
                        <td>
                            <input type="text" name="messageDefType" condition="like"/>
                        </td>
                        <th>
                            <lable>生产者名称：</lable>
                        </th>
                        <td>
                            <input type="text" name="producerName" condition="like"/>
                        </td>
                        <th>
                            <lable>消费者名称：</lable>
                        </th>
                        <td>
                            <input type="text" name="consumerName" condition="like"/>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="8" align="center">
                            <button onclick="sys019.queryKs();sys019.closeQSch()" type="button" id="schBtn"
                                    class="js_faTask btn btn-primary">
                                查询
                            </button>
                            <button onclick="sys019.closeQSch()" type="button" class="btn btn-info">关闭</button>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
        <form id="sys019form" method="post">
            <input type="hidden" name="entityName" value="MessageDef"/>
            <input type="hidden" name="conditionName" value="">
            <input type="hidden" name="condition" value="=">
            <input type="hidden" name="conditionValue" value="">
            <input type="hidden" name="sortColumn" value="">
            <input type="hidden" name="sortOrder" value="">
            <input type="hidden" name="divId" value="sys019_page_data">
            <input type="hidden" name="forceResultField" value="messageDefId">
            <input type="hidden" class="js_conditionValue" value="">
            <input type="hidden" name="cmptName" id="cmptName" value="QUERY_ENTITY_PAGE_DATA"/>
            <input type="hidden" name="resultField"
                   value="messageDefType,messageCreateTime,dealTemplate,messageDefDesc,messageDefTarget">
            <input type="hidden" name="forward" id="forward" value="/oframe/sysmg/sys019/sys019_list"/>
        </form>
        <div id="sys019_page_data" class="js_page" layoutH="55"></div>
    </div>
</div>
<oframe:script type="text/javascript" src="${pageContext.request.contextPath}/oframe/sysmg/sys019/js/sys019.js"/>
