<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.shfb.oframe.core.util.common.NumberUtil" %>
<%@ page import="com.shfb.oframe.core.util.common.StringUtil" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div>
    <table class="list" width="100%">
        <thead>
        <tr>
            <th>
                <div class="gridCol" title="序号">序号</div>
            </th>
            <th>
                <div class="gridCol" title="房屋地址">房屋地址</div>
            </th>
            <th>
                <div class="gridCol" title="户型名称">户型名称</div>
            </th>
            <th>
                <div class="gridCol" title="户型结构">户型结构</div>
            </th>
            <th>
                <div class="gridCol" title="户型朝向">户型朝向</div>
            </th>
            <th>
                <div class="gridCol" title="建筑面积">预测建筑</div>
            </th>
            <th>
                <div class="gridCol" title="选房状态">选房状态</div>
            </th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${returnList}" var="item" varStatus="varStatus">
            <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}"/>
            <tr class="${tr_class}" style="cursor:default;">
                <td width="5%">${varStatus.index + 1}</td>
                <td>${item.Row.NewHsInfo.hsAddr}</td>
                <td>${item.Row.NewHsInfo.hsHxName}</td>
                <td><oframe:name prjCd="${param.prjCd}" value="${item.Row.NewHsInfo.hsTp}" itemCd="HS_ROOM_TYPE"/></td>
                <td><oframe:name prjCd="${param.prjCd}" value="${item.Row.NewHsInfo.hsDt}" itemCd="HS_ROOM_DT"/></td>
                <td>${item.Row.NewHsInfo.preBldSize}</td>
                <td><oframe:name prjCd="${param.prjCd}" value="${item.Row.NewHsInfo.statusCd}" itemCd="NEW_HS_STATUS"/></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<div id="pagebox" class="panelBar bottom">
    <div class="pages">
        <span>第<u>${pagination.currentPage!=null?pagination.currentPage:"&nbsp;&nbsp;"}</u>页,
            共<u>${pagination.totalPage!=null?pagination.totalPage:"&nbsp;&nbsp;"}</u>页,
            共<u>${pagination.totalRecord!=null?pagination.totalRecord:"&nbsp;&nbsp;"}</u>条</span>
    </div>
    <div class="pagination">
        <ul id="ul00">
            <li class="j-first">
                <input class="textInput" name="pageSize"
                       type="text" size="4" maxlength="4" value="${pageSize}"
                       onkeydown="{if(event.keyCode == 13){ch003cx.jumpPage(1,'${divId }');}}"/>
            </li>
            <%-- 上一页与首页页信息，已经是第一页时候按钮不可用 --%>
            <c:choose>
                <c:when test="${pagination.currentPage == 1}">
                    <li class="j-first disabled"><a class="first"><span>首页</span></a></li>
                    <li class="j-prev disabled"><a class="previous"><span>上一页</span></a></li>
                </c:when>
                <c:otherwise>
                    <li class="j-first"><a href="javascript:ch003cx.jumpPage(1,'${divId }','${divPId }')"
                                           class="first" title="首页"><span>首页</span></a></li>
                    <li class="j-prev"><a
                            href="javascript:ch003cx.jumpPage('${pagination.currentPage - 1}','${divId }','${divPId }')"
                            class="previous" title="上一页"><span>上一页</span></a></li>
                </c:otherwise>
            </c:choose>
            <%-- 单页跳转链接，以10页为单位展示，当前页按钮不可用 --%>
            <c:set var="currentPage" value="${pagination.currentPage}"
                   scope="request"></c:set>
            <c:set var="totalPage" value="${pagination.totalPage}"
                   scope="request"></c:set>
            <%
                int currentPage = NumberUtil.getIntFromObj(request.getAttribute("currentPage"));
                int totalPage = NumberUtil.getIntFromObj(request.getAttribute("totalPage"));
                int startShowPage = (currentPage - 1) / 10 * 10 + 1;
                int endShowPage = totalPage > (startShowPage + 10) ? (startShowPage + 10) : (totalPage + 1);
                if (currentPage == (endShowPage - 1) && (startShowPage + 2) < totalPage
                        && (endShowPage + 2) < totalPage) {
                    startShowPage = startShowPage + 2;
                    endShowPage = endShowPage + 2;
                }
                String showPageStr = StringUtil.obj2Str(request.getParameter("showPages"));
                boolean showPages = true;
                if ("false".equals(showPageStr)) {
                    showPages = false;
                }
                for (int i = startShowPage; i < endShowPage && showPages; i++) {
                    if (currentPage == i) {
            %>
            <li class="selected j-num disabled"><a><%=i%>
            </a></li>
            <%
            } else {
            %>
            <li class="j-num"><a href="javascript:ch003cx.jumpPage(<%=i%>,'${divId}','${divPId }')"><%=i%>
            </a></li>
            <%
                    }
                }
            %>
            <%-- 下一页与末页信息，已经是最后一页时候按钮不可用 --%>
            <c:choose>
                <c:when test="${pagination.currentPage == pagination.totalPage || pagination.totalPage == 0 }">
                    <li class="j-next disabled"><a class="next"><span>下一页</span></a></li>
                    <li class="j-last disabled"><a class="last"><span>末页</span></a>
                    </li>
                </c:when>
                <c:otherwise>
                    <li class="j-next"><a
                            href="javascript:ch003cx.jumpPage('${pagination.currentPage + 1}','${divId }','${divPId }')"
                            class="next" title="下一页"><span>下一页</span></a></li>
                    <li class="j-last"><a
                            href="javascript:ch003cx.jumpPage('${pagination.totalPage}','${divId }','${divPId }')"
                            class="last" title="末页"><span>末页</span></a></li>
                </c:otherwise>
            </c:choose>
            <c:if test="${pagination.currentPage < pagination.totalPage}">
            </c:if>
            <c:out value=""/>
            <input type="hidden" id="remindFlag" value="${remindFlag }">
            <%--手动跳转与隐藏信息 --%>
            <li class="jumpto"><input class="textInput" name="skipToNo"
                                      type="text" size="4" maxlength="4" value="<%=currentPage%>"
                                      onkeydown="{if(event.keyCode == 13){ch003cx.skipTo('${divId }','${divPId }');}}"/>
                <input
                        class="goto" type="button" onclick="javascript:ch003cx.skipTo('${divId }','${divPId }');"
                        title="跳转"></li>
            <li><input type="hidden" name="currentPage"
                       value="${pagination.currentPage}"/>
                <input type="hidden" name="totalPage"
                       value="${pagination.totalPage}"/>
                <input type="hidden" name="totalRecord"
                       value="${pagination.totalRecord}"/>
                <input type="hidden" name="sortColumn"
                       value="${sortColumn}"/>
                <input type="hidden" name="sortOrder"
                       value="${sortOrder}"/>
                <input type="hidden" name="queryCondition"
                       value='${queryCondition}'/>
                <input type="hidden" name="errMsg"
                       value='${errMsg}'/></li>
        </ul>
    </div>
</div>