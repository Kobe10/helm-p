<%--项目公告--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<div style="background-color: #ffffff; min-height: 300px;">
    <ul class="marl10 marr10 news-list">
        <c:forEach items="${noticeList}" var="item">
            <c:if test="${item.notice_id != 0}">
                <li onclick="mb002.viewNotice('${item.notice_id}')">
                    <p class="news-p clearfix">
                        <span><fmt:formatDate value="${item.publish_date}" pattern="yyyy/MM/dd"/></span>
                        <a title=" ${item.notice_summary}" class="link" style="cursor: pointer;">
                                ${item.notice_title}
                        </a>
                    </p>
                </li>
            </c:if>
        </c:forEach>
    </ul>
</div>
<script type="text/javascript">
    mb002 = {
        viewNotice: function (noticeId) {
            var url = getGlobalPathRoot() + "eland/pj/pj011/pj011-viewMain.gv?prjCd=" + getPrjCd() +
                    "&noticeId=" + noticeId;
            $.pdialog.open(url, "pj01101", "公告信息", {mask: true, maxable: true, height: 600, width: 800});
        }
    }
</script>