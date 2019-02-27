<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="pageHeader">
    <form id="pj012frm" method="post">
        <%--自服务名称用于定位SQL--%>
        <input type="hidden" name="subSvcName" value="pt006"/>
        <%-- 分页展示 --%>
        <input type="hidden" name="forward" id="forward" value="/eland/pj/pj012/pj012_list"/>

        <div class="searchBar">
            <table class="form">
                <tr>
                    <th>
                        问题关键字：
                    </th>
                    <td>
                        <input type="text" name="qText"/>
                    </td>
                    <th>
                        提问时间：
                    </th>
                    <td>
                        <input type="text" datefmt="yyyy-MM-dd HH:mm:ss"
                               class="date" name="qStartTime" value="${qStartTime}">
                        ~
                        <input type="text" datefmt="yyyy-MM-dd HH:mm:ss" class="date"
                               name="qEndTime" value="${qEndTime}">
                    </td>
                    <th>
                        提问状态：
                    </th>
                    <td width="18%">
                        <oframe:select prjCd="${param.prjCd}" itemCd="QUES_STATUS" name="qStatus"
                                       value="0"/>
                    </td>
                </tr>
            </table>
            <div class="subBar center">
                <button type="button" id="schBtn" class="btn btn-primary marr10"
                        onclick="Query.queryList('pj012frm', 'pj012_w_list_print');">检索
                </button>
                <button type="reset" class="btn btn-info">重置</button>
            </div>
        </div>
    </form>
</div>
<div class="pageContent">
    <div class="panelBar">
        <ul class="toolBar">
            <oframe:op prjCd="${param.prjCd}" template="li" cssClass="add"
                       name="问题添加" rhtCd="edit_prj_ques_rht"
                       onClick="pj012.openAdd()"/>
            <oframe:op prjCd="${param.prjCd}" template="li" cssClass="delete-more"
                       name="批量删除" rhtCd="del_prj_ques_rht"
                       onClick="pj012.deleteQuestion(this)"/>
            <oframe:op prjCd="${param.prjCd}" template="li" cssClass="export"
                       name="导出Excel" rhtCd="edit_prj_ques_rht"
                       onClick="Query.exportExcelByTemplate('pj012_w_list_print','webroot:eland/pj/pj012/居民问答信息导出excl模版.xls')"/>
            <li><a class="print" href="javascript:$.printBox('pj012_w_list_print')"><span>打印</span></a></li>
        </ul>
    </div>
    <div id="pj012_w_list_print">
        <table class="table" width="98%" layoutH="135">
            <thead>
            <tr>
                <th width="22"><input type="checkbox" group="ids" class="checkboxCtrl"></th>
                <th width="40">序号</th>
                <th width="40">提问人区域</th>
                <th width="200" onclick="Query.sort('pj012_w_list_print', this);"
                    sortColumn="q_text"
                    class="sortable">问题内容
                </th>
                <th width="80">提问人姓名</th>
                <th width="80">提问人电话</th>
                <th width="40">问题状态</th>
                <th width="80" onclick="Query.sort('pj012_w_list_print', this);"
                    defaultSort="asc" sortColumn="q_Time" class="sortable">
                    提问时间
                </th>
                <th width="80">操作</th>
            </tr>
            </thead>
            <tbody>
            </tbody>
        </table>
    </div>
    <jsp:include page="/oframe/common/page/pager_fotter.jsp"/>
</div>
<oframe:script src="${pageContext.request.contextPath}/eland/pj/pj012/js/pj012.js" type="text/javascript"/>
