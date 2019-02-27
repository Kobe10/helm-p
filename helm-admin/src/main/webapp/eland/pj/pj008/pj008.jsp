<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="mar5" style="position: relative;">
    <div class="panelBar">
        <ul class="toolBar">
            <li onclick="pj008.openQSch(this);">
                <a title="点击进入高级检索" class="find" href="javascript:void(0)"><span>条件检索</span></a>
            </li>
            <li onclick="pj008.queryList('pj008frm', 'pj008_w_list_print');">
                <a class="reflesh" href="javascript:void(0)"><span>刷新检索</span>
                </a>
            </li>
        </ul>
    </div>
    <div id="pj008QSchDialog" class="hidden"
         style="position: absolute;width:100%;top: 0px;z-index: 100;background-color: #ffffff; margin-top: 40px;">
        <div class="triangle triangle-up" style="border-bottom-color: #2c7bae;top: -10px; left: 25px;"></div>
        <form id="pj008frm" method="post" action="">
            <%--自服务名称用于定位SQL--%>
            <input type="hidden" name="subSvcName" value="pj00801"/>
            <%--分页展示--%>
            <input type="hidden" name="forward" id="forward" value="/eland/pj/pj008/pj008_list"/>
            <%--自定义服务--%>
            <input type="hidden" name="selfDefSvc" value="projectService-queryHsHxWithReg"/>
            <table class="border">
                <tr>
                    <c:choose>
                        <c:when test="${regId == ''}">
                            <th width="10%">
                                房产类别：
                            </th>
                            <td>
                                <oframe:select prjCd="${param.prjCd}" itemCd="REG_USE_TYPE" name="regUseType"/>
                            </td>
                        </c:when>
                        <c:otherwise>
                            <input type="hidden" name="regUseType" value="${regUseType}"/>
                        </c:otherwise>
                    </c:choose>
                    <th width="10%">
                        户型编号：
                    </th>
                    <td>
                        <input type="text" name="hxCd"/>
                    </td>
                    <th width="10%">
                        户型名称：
                    </th>
                    <td>
                        <input type="text" name="hxName"/>
                    </td>
                    <th width="10%">
                        户型区域：
                    </th>
                    <td>
                        <div style="position:relative;">
                            <input type="hidden" name="regId" value="${regId}">
                            <input type="text" class="readonly pull-left" name="regName" value="${regName}"/>
                            <a title="选择" style="float: none; display: inline-block; cursor: pointer"
                               onclick="pj008.editReg(this);" class="btnLook">选择</a>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td colspan="8" align="center">
                        <button onclick="pj008.queryList('pj008frm', 'pj008_w_list_print');" type="button"
                                class="btn btn-primary js_query marr20">查询
                        </button>
                        <button onclick="pj008.closeQSch(true);" class="btn btn-info" type="button">关闭</button>
                    </td>
                </tr>
            </table>
        </form>
    </div>
    <div id="pj008_w_list_print" style="border: 1px solid #e9e9e9;" layoutH="100"></div>
    <jsp:include page="/oframe/common/page/pager_fotter.jsp"/>
</div>
<oframe:script src="${pageContext.request.contextPath}/eland/pj/pj008/js/pj008.js"
               type="text/javascript"/>
