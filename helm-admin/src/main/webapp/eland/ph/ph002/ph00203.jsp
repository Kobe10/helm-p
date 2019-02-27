<%--院落房屋信息--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="panel">
    <h1>房屋信息
        <span class="panel_menu js_reload">刷新</span>
    </h1>
    <%--面板内容--%>
    <div class="js_panel_context">
        <table class="border" width="100%">
            <c:forEach items="${resultMap}" var="item" varStatus="varStatus">
                <c:if test="${varStatus.index % 3 == 0}">
                    <tr>
                </c:if>
                <th width="20%"><label>${item.key}:</label></th>
                <td>${item.value}</td>
                <c:if test="${varStatus.index % 3 == 2}">
                    </tr>
                </c:if>
            </c:forEach>
        </table>
        <div class="mart5 border">
            <form id="ph00203QFrm">
                <%--区域信息--%>
                <input type="hidden" name="rhtRegId" value="${buildingRegId}">
                <%--区域信息--%>
                <input type="hidden" name="buldId" value="${buildId}">
                <%--自服务名称用于定位SQL--%>
                <input type="hidden" name="subSvcName" value="hs001021"/>
                <%--自定义服务--%>
                <input type="hidden" name="selfDefSvc" value="projectService-queryPageWithRht"/>
                <%-- 分页展示 --%>
                <input type="hidden" name="forward" id="forward" value="/eland/ph/ph002/ph00203_list"/>
                <%--房源类型--%>
                <input type="hidden" name="regUseType" value="1"/>
            </form>
            <div id="ph00203_list_print">
                <table class="table" width="90%">
                    <thead>
                    <tr>
                        <th>序号</th>
                        <th onclick="Query.sort('ph00203_list_print', this);"
                            defaultSort="asc" sortColumn="hs_cd" class="sortable">档案编号
                        </th>
                        <th onclick="Query.sort('ph00203_list_print', this);"
                            sortColumn="hs_owner_persons" class="sortable">产权人/承租人
                        </th>
                        <th onclick="Query.sort('ph00203_list_print', this);"
                            sortColumn="hs_owner_type" class="sortable">房屋产别
                        </th>
                        <th>建筑面积</th>
                        <th>当前状态</th>
                        <th>操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>
            </div>
            <jsp:include page="/oframe/common/page/pager_fotter.jsp">
                <jsp:param name="show" value="false"/>
            </jsp:include>
        </div>
    </div>
</div>
<script type="text/javascript">
    var ph00203 = {
        openHs: function (hsId) {
            var url = "eland/ph/ph003/ph003-init.gv?hsId=" + hsId
                    + "&prjCd=" + getPrjCd();
            index.quickOpen(url, {opCode:"ph003-init",opName:"居民详情",fresh:true});
        }
    };
    $(document).ready(function () {
        //查询房产
        Query.queryList('ph00203QFrm', 'ph00203_list_print');
    });
</script>
