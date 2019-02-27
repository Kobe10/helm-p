<%--待签约处理--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<iframe id="ct001DocIFrame" style="border: 0;margin: 0; width: 100%;" layoutH="5"
        src="${pageContext.request.contextPath}/oframe/common/file/file-openDocEdit.gv?docId=${docId}&prjCd=${prjCd}&isEditable=${isEditable}">
</iframe>
