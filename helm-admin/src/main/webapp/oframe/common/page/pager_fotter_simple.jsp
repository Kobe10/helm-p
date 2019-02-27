<%@ page import="com.shfb.oframe.core.util.common.NumberUtil" %>
<%@ page import="com.shfb.oframe.core.util.common.StringUtil" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div id="pagebox" class="panelBar bottom">
    <div class="pages">
        <span><u>${pagination.currentPage!=null?pagination.currentPage:"&nbsp;&nbsp;"}页/${pagination.totalPage!=null?pagination.totalPage:"&nbsp;&nbsp;"}</u>页,
            共<u>${pagination.totalRecord!=null?pagination.totalRecord:"&nbsp;&nbsp;"}</u>条</span>
    </div>
    <div class="pagination">
        <ul id="ul00">
            <c:choose>
                <c:when test="${pagination.currentPage == 1}">
                    <li class="j-prev disabled"><a class="previous"><span>上一页</span></a></li>
                </c:when>
                <c:otherwise>
                    <li class="j-prev"><a
                            href="javascript:Page.jumpPage('${pagination.currentPage - 1}','${divId }','${divPId }')"
                            class="previous" title="上一页"><span>上一页</span></a></li>
                </c:otherwise>
            </c:choose>
            <c:choose>
                <c:when test="${pagination.currentPage == pagination.totalPage || pagination.totalPage == 0 }">
                    <li class="j-next disabled"><a class="next"><span>下一页</span></a></li>
                    </li>
                </c:when>
                <c:otherwise>
                    <li class="j-next"><a
                            href="javascript:Page.jumpPage('${pagination.currentPage + 1}','${divId }','${divPId }')"
                            class="next" title="下一页"><span>下一页</span></a></li>
                </c:otherwise>
            </c:choose>
            <li>
                <input type="hidden" name="currentPage" value="${pagination.currentPage}"/>
                <input type="hidden" name="totalPage" value="${pagination.totalPage}"/>
                <input type="hidden" name="totalRecord" value="${pagination.totalRecord}"/>
                <input type="hidden" name="sortColumn" value="${sortColumn}"/>
                <input type="hidden" name="sortOrder" value="${sortOrder}"/>
                <input type="hidden" name="queryCondition" value='${queryCondition}'/>
                <input type="hidden" name="errMsg" value='${errMsg}'/>
            </li>
        </ul>
    </div>
</div>
<c:choose>
    <c:when test="${pagination.totalRecord eq 0 && remindFlag eq 1 }">
        <script type="text/javascript">
            alertMsg.info("没有查询到任何数据！");
        </script>
    </c:when>
</c:choose>
