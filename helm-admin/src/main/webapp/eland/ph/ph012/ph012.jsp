<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>

<div class="mar5">
    <div style="position: relative;">
        <div class="panelBar">
            <ul class="toolBar">
                <li onclick="ph012.openQSch(this);">
                    <a class="find" href="javascript:void(0);"><span>检索</span></a>
                </li>
                <li onclick="ph012.query();">
                    <a class="reflesh" href="javascript:void(0);"><span>刷新</span></a>
                </li>
                <li onclick="ph012.changeEdit('');">
                    <a class="add" href="javascript:void(0);"><span>添加</span></a>
                </li>
                <%--<li onclick="ph012.export()">--%>
                    <%--<a class="export" href="javascript:void(0);"><span>导出</span></a>--%>
                <%--</li>--%>
            </ul>
        </div>
        <div>
            <div id="ph012create" class="hidden"
                 style="position: absolute;width:100%;top: 0;z-index: 100;background-color: #ffffff; margin-top: 40px;">
                <table class="border">
                    <tr>
                        <th>
                            <lable>被安置人：</lable>
                        </th>
                        <td>
                            <input type="text" name="ctPsNames" condition="like" value="${hsOwnerPersons}"/>
                        </td>
                        <th>
                            <lable>房屋地址：</lable>
                        </th>
                        <td>
                            <input type="text" name="hsFullAddr" condition="like"/>
                        </td>
                        <th>
                            <lable>关联关系：</lable>
                        </th>
                        <td>
                            <input type="text" name="relName" condition="like"/>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="8" align="center">
                            <button onclick="ph012.query();ph012.closeQSch()" type="button" id="schBtn"
                                    class="js_faTask btn btn-primary">
                                查询
                            </button>
                            <button onclick="ph012.closeQSch()" type="button" class="btn btn-info">关闭</button>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
        <form id="ph012form" method="post">
            <input type="hidden" name="entityName" value="HsCtRelationInfo"/>
            <input type="hidden" name="conditionName" value="">
            <input type="hidden" name="condition" value="">
            <input type="hidden" name="conditionValue" value="">
            <input type="hidden" name="sortColumn" value="">
            <input type="hidden" name="sortOrder" value="">
            <input type="hidden" name="divId" value="ph012_page_data">
            <input type="hidden" name="forceResultField" value="hsCtReId">
            <input type="hidden" class="js_conditionValue" value="">
            <input type="hidden" name="cmptName" id="cmptName" value="QUERY_HSCTRE_INFO_CMPT"/>
            <input type="hidden" name="resultField" value="">
            <input type="hidden" name="forward" id="forward" value="/eland/ph/ph012/ph012_list"/>
        </form>
        <div id="ph012_page_data" class="js_page" layoutH="55"></div>
    </div>
</div>
<oframe:script type="text/javascript" src="${pageContext.request.contextPath}/eland/ph/ph012/js/ph012.js"/>
