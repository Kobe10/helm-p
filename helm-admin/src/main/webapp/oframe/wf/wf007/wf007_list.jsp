<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div id="wf007query_list">
    <table class="table" layoutH="130" width="100%">
        <thead>
        <tr>
            <%--<th width="3%"><input type="checkbox" class="checkboxCtrl" group="ids"/></th>--%>
            <%@include file="/oframe/common/query/list_htr.jsp" %>
            <th style="width: 120px;">操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${returnList}" var="item" varStatus="varStatus">
            <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}"/>
            <tr class="${tr_class}">
                    <%--<td><input type="checkbox" group="ids" name="hsId" value="${item.Row.HouseInfo.hsId}"/></td>--%>
                <%@include file="/oframe/common/query/list_hdt.jsp" %>
                <td>
                    <!--第一个环节 ，未发起的-->
                    <c:if test="${param.isActive == '1' && param.isStart == '1'}">
                        <button type="button" class="btn btn-more mart5"
                                onclick='index.startWf("${param.procKey}", "${param.prjCd}", {busiKey: "HouseInfo_${item.Row.HouseInfo.hsId}"});'>
                            <i style="font-style:normal;">启动流程</i>
                            <span class="caret"></span>
                            <ul style="width: 100px;" class="menu hidden">
                                <li onclick="wf007.openHs('${item.Row.HouseInfo.hsId}');">
                                    房屋详情
                                </li>
                            </ul>
                        </button>
                    </c:if>
                    <!--第一个环节 ，发起的-->
                    <c:if test="${param.isActive == '0' && param.isStart == '1'}">
                        <button type="button" class="btn btn-more mart5"
                                onclick="index.viewWf('${item.Row.HouseInfo.procInsId}');">
                            <i style="font-style:normal;">任务进展</i>
                            <span class="caret"></span>
                            <ul style="width: 100px;" class="menu hidden">
                                <li onclick="wf007.openHs('${item.Row.HouseInfo.hsId}');">
                                    房屋详情
                                </li>
                            </ul>
                        </button>
                    </c:if>
                    <!-- 其他环节，待办 -->
                    <c:if test="${param.isActive == '1' && param.isStart == '0'}">
                        <button type="button" class="btn btn-more mart5"
                                onclick="index.viewWf('${item.Row.HouseInfo.procInsId}');">
                            <i style="font-style:normal;">处理任务</i>
                            <span class="caret"></span>
                            <ul style="width: 100px;" class="menu hidden">
                                <li onclick="wf007.openHs('${item.Row.HouseInfo.hsId}');">
                                    房屋详情
                                </li>
                            </ul>
                        </button>
                    </c:if>
                    <!--已完成-->
                    <c:if test="${param.isActive == '0' && param.isStart == '0'}">
                        <button type="button" class="btn btn-more mart5"
                                onclick="index.viewWf('${item.Row.HouseInfo.procInsId}');">
                            <i style="font-style:normal;">任务进展</i>
                            <span class="caret"></span>
                            <ul style="width: 100px;" class="menu hidden">
                                <li onclick="wf007.openHs('${item.Row.HouseInfo.hsId}');">
                                    房屋详情
                                </li>
                            </ul>
                        </button>
                    </c:if>

                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <jsp:include page="/oframe/common/page/page_fotter.jsp"/>
</div>