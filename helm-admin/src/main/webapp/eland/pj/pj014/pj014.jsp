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
    <form id="pj014frm" method="post">
        <%--自服务名称用于定位SQL--%>
        <input type="hidden" name="subSvcName" value="pj014"/>
        <%-- 分页展示 --%>
        <input type="hidden" name="forward" id="forward" value="/eland/pj/pj014/pj014_list"/>

        <div class="searchBar">
            <table class="form">
                <tr>
                    <th width="10%">
                        单位名称：
                    </th>
                    <td>
                        <input type="text" class="msize textInput" name="cmpName"/>
                    </td>
                    <td>
                        <button type="button" id="schBtn" class="btn btn-primary"
                                onclick="Query.queryList('pj014frm', 'pj014_w_list_print');">检索
                        </button>
                        <button type="reset" class="btn btn-info">重置</button>
                    </td>
                </tr>
            </table>
        </div>
    </form>
</div>
<div class="pageContent">
    <div class="panelBar">
        <ul class="toolBar">
            <%--<oframe:power prjCd="${param.prjCd}" rhtCd="add_ext_cmp_rht">--%>
                <li><a class="add" onclick="pj014.openAdd()"><span>新建</span></a></li>
            <%--</oframe:power>--%>
        </ul>
    </div>
    <div id="pj014_w_list_print" class="result" layoutH="100">
    </div>
    <jsp:include page="/oframe/common/page/pager_fotter.jsp"/>
</div>
<oframe:script src="${pageContext.request.contextPath}/eland/pj/pj014/js/pj014.js" type="text/javascript"/>

