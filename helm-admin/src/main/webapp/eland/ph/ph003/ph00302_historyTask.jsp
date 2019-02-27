<%--待办任务信息面板--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>

<%--面板内容--%>
<div class="js_end_task" style="position: relative;">
    <form id="ph00302_his" method="post">
        <%--自服务名称用于定位SQL--%>
        <input type="hidden" name="selfDefCmpt" value="QUERY_PROC_INSTANC_EPAGE"/>
        <%-- 分页展示 --%>
        <input type="hidden" name="forward" id="forward" value="/oframe/wf/wf004/wf004_list"/>
        <input type="hidden" name="isComplete" value="1"/>
        <input type="hidden" name="businessKey" value="HouseInfo_${param.hsId}"/>
    </form>
    <div>
        <div class="ph00302_his_page_data" id="ph00302_his_page_data">
            <table class="table" width="100%" layoutH="75">
                <thead>
                <tr>
                    <th width="5%">序号</th>
                    <th>请求名称</th>
                    <th>流程名称</th>
                    <th onclick="Query.sort('ph00302_his_page_data', this);"
                        sortColumn="procStTime" class="sortable">发起时间
                    </th>
                    <th onclick="Query.sort('ph00302_his_page_data', this);"
                        defaultSort="desc" sortColumn="procEndime" class="sortable">完成时间
                    </th>
                </tr>
                </thead>
                <tbody>
                </tbody>
            </table>
        </div>
        <c:import url="/oframe/common/page/pager_fotter.jsp" charEncoding="UTF-8"></c:import>
    </div>
</div>
<script type="text/javascript">
    Query.queryList('ph00302_his', 'ph00302_his_page_data');
</script>