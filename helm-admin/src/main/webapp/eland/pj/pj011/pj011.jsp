<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="mar5" style="position: relative;">
    <div class="panelBar">
        <ul class="toolBar">
            <li onclick="pj011.openQSch(this);">
                <a class="find" href="javascript:void(0)"><span>检索条件</span></a>
            </li>
            <li id="schBtn" onclick="Query.queryList('pj011frm', 'pj011_w_list_print');">
                <a class="reflesh" href="javascript:void(0)"><span>刷新检索</span></a>
            </li>
            <oframe:op prjCd="${param.prjCd}" template="li" cssClass="add"
                       name="公告添加" rhtCd="edit_prj_notice_rht"
                       onClick="pj011.openAdd(${noticeType})"/>
            <oframe:op prjCd="${param.prjCd}" template="li" cssClass="delete-more"
                       name="批量删除" rhtCd="del_prj_notice_rht"
                       onClick="pj011.deleteNotice(this)"/>
            <li><a class="print" href="javascript:$.printBox('pj011_w_list_print')"><span>打印</span></a></li>
        </ul>
    </div>
    <div id="pj011QSchDialog" class="hidden"
         style="position: absolute;width:100%;top: 0px;z-index: 100;background-color: #ffffff; margin-top: 40px;">
        <div class="triangle triangle-up" style="border-bottom-color: #2c7bae;top: -10px; left: 25px;"></div>
        <form id="pj011frm" method="post">
            <%--自服务名称用于定位SQL--%>
            <input type="hidden" name="subSvcName" value="pt001"/>
            <c:set var="colCount" value="6"/>
            <%-- 分页展示 --%>
            <c:choose>
                <c:when test="${noticeType != 3}">
                    <%--普通公告展示列表类型--%>
                    <input type="hidden" name="forward" id="forward" value="/eland/pj/pj011/pj011_list"/>
                </c:when>
                <c:otherwise>
                    <%--房源公告展示封面图片--%>
                    <input type="hidden" name="forward" id="forward" value="/eland/pj/pj011/pj011_pics"/>
                </c:otherwise>
            </c:choose>
            <table class="border">
                <tr>
                    <th>公告标题：</th>
                    <td><input type="text" name="noticeTitle"/></td>
                    <c:choose>
                        <c:when test="${noticeType == null || noticeType==''}">
                            <c:set var="colCount" value="8"/>
                            <th>
                                <label>公告类型：</label>
                            </th>
                            <td>
                                <oframe:select prjCd="${param.prjCd}" itemCd="NOTICE_TYPE" name="noticeType"
                                               withEmpty="true" value=""/>
                            </td>
                        </c:when>
                        <c:otherwise>
                            <input type="hidden" name="noticeType" value="${noticeType}">
                        </c:otherwise>
                    </c:choose>
                    <th>发布状态：</th>
                    <td><oframe:select prjCd="${param.prjCd}" withEmpty="true" itemCd="NOTICE_STATUS"
                                       name="noticeStatus" value=""/></td>
                    <th>发布时间：</th>
                    <td><input type="text" name="publishDate" class="date" datefmt="yyyy-MM-dd"/></td>
                </tr>
                <tr>
                    <td align="center" colspan="${colCount}">
                        <button onclick="Query.queryList('pj011frm', 'pj011_w_list_print');pj011.openQSch(this);"
                                type="button"
                                class="btn btn-primary js_query marr20">查询
                        </button>
                        <button onclick="pj011.openQSch(this);" type="button" class="btn btn-info">关闭</button>
                    </td>
                </tr>
            </table>
        </form>
    </div>

    <c:choose>
        <c:when test="${noticeType != 3}">
            <%--普通类型公告--%>
            <div id="pj011_w_list_print" class="result">
                <table class="table" width="98%" layoutH="130" fixHeight="true">
                    <thead>
                    <tr>
                        <th width="22"><input type="checkbox" group="ids" class="checkboxCtrl"></th>
                        <th width="30">序号</th>
                        <th width="60">公告类型</th>
                        <th width="200" onclick="Query.sort('pj011_w_list_print', this);" sortColumn="notice_title"
                            class="sortable">公告标题
                        </th>
                        <th width=10%>发布状态</th>
                        <th width="80">创建时间</th>
                        <th width="80" onclick="Query.sort('pj011_w_list_print', this);" sortColumn="publish_date"
                            defaultSort="desc" class="sortable">发布时间
                        </th>
                        <th width="80">操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>
            </div>
        </c:when>
        <c:otherwise>
            <div id="pj011_w_list_print" class="result" layoutH="100" fixHeight="true">
            </div>
        </c:otherwise>
    </c:choose>
    <jsp:include page="/oframe/common/page/pager_fotter.jsp"/>

</div>
<oframe:script src="${pageContext.request.contextPath}/eland/pj/pj011/js/pj011.js" type="text/javascript"/>
