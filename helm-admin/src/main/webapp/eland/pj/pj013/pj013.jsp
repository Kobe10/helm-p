<%--
  Created by IntelliJ IDEA.
  User: Dennis
  Date: 2014/11/3 0003
  Time: 17:31
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="pageHeader">
    <form id="pj013frm" method="post">
        <%--自服务名称用于定位SQL--%>
        <input type="hidden" name="subSvcName" value="pj013"/>
        <%-- 分页展示 --%>
        <input type="hidden" name="forward" id="forward" value="/eland/pj/pj013/pj013_list"/>
        <%--自定义查询服务--%>
        <input type="hidden" name="selfDefSvc" value="prjDocService-queryDocumentByOrgIds"/>

        <div class="searchBar">
            <table class="form">
                <tr>
                    <th>
                        文件名称：
                    </th>
                    <td>
                        <input type="text" name="docName"/>
                    </td>
                    <th>
                        文件类型：
                    </th>
                    <td>
                        <oframe:select prjCd="${param.prjCd}" itemCd="PRJ_DOCUMENT_TYPE" name="docType" />
                    </td>
                    <th>
                        文件来源：
                    </th>
                    <td>
                        <oframe:select prjCd="${param.prjCd}" itemCd="PRJ_DOCUMENT_SOURCE" name="docSrc" />
                    </td>
                    <th>
                        发文时间：
                    </th>
                    <td>
                        <input type="text" datefmt="yyyy-MM-dd" class="date" name="pubDate"/>
                    </td>
                </tr>
                <tr>
                    <th>
                        关键字：
                    </th>
                    <td>
                        <input type="text" name="keyWord"/>
                    </td>
                    <th>
                        文件编号：
                    </th>
                    <td>
                        <input type="text" name="docCd"/>
                    </td>
                    <th>
                        发文部门：
                    </th>
                    <td>
                        <input type="text" name="pubOrg">
                    </td>
                    <oframe:power prjCd="${param.prjCd}" rhtCd="admin_old_hs_rht">
                        <th>
                            文件状态：
                        </th>
                        <td>
                            <oframe:select prjCd="${param.prjCd}" itemCd="PRJ_DOC_STATUS" name="docStatus"/>
                        </td>
                    </oframe:power>

                </tr>
            </table>
            <div class="subBar center">
                <button type="button" id="schBtn" class="btn btn-primary"
                        onclick="Query.queryList('pj013frm', 'pj013_w_list_print');">检索
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
                       name="新增项目资料" rhtCd="add_prj_doc_rht"
                       onClick="pj013.openAdd()"/>
            <%--<oframe:power prjCd="${param.prjCd}" rhtCd="edit_prj_doc_rht">--%>
            <%--<li><a onclick="pj013.deleteQuestion(this)" class="delete-more"><span>批量删除</span></a></li>--%>
            <%--</oframe:power>--%>
            <li><a class="print" href="javascript:$.printBox('pj013_w_list_print')"><span>打印</span></a></li>
        </ul>
    </div>
    <div id="pj013_w_list_print">
        <table class="table" width="98%" layoutH="135">
            <thead>
            <tr>
                <%--暂时没做删除，隐藏多选框--%>
                <%--<th width="3%"><input type="checkbox" group="ids" class="checkboxCtrl"></th>--%>
                <th width="3%">序号</th>
                <th width="20%">文件名称</th>
                <th width="10%" onclick="Query.sort('pj013_w_list_print', this);" sortColumn="pub_date"
                    class="sortable">发文时间
                </th>
                <th width="5%">文件来源</th>
                <th width="5%">文件类型</th>
                <th width="10%">发文部门</th>
                <th width="10%" onclick="Query.sort('pj013_w_list_print', this);" sortColumn="doc_cd" class="sortable">
                    发文编号
                </th>
                <th width="10%">归档人员</th>
                <th width="15%">归档部门</th>
                <th width="10%">操作</th>
            </tr>
            </thead>
            <tbody>
            </tbody>
        </table>
    </div>
    <jsp:include page="/oframe/common/page/pager_fotter.jsp"/>
</div>
<oframe:script src="${pageContext.request.contextPath}/eland/pj/pj013/js/pj013.js" type="text/javascript"/>

