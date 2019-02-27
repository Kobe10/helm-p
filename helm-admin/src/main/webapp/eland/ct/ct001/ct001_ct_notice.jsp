<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="mar5">
    <iframe layoutH="15" width="100%"
            style="border: 1px solid #efefef; background-color: #fefefe"
            src="${pageContext.request.contextPath}/eland/ct/ct001/ct001-viewCfmNotice.gv?noticeId=${noticeId}&prjCd=${prjCd}&hsId=${hsId}&hsCtId=${hsCtId}"></iframe>
</div>
