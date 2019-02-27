<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<span>${selectData.OpResult.PageData.Row[0].camAddr}</span>
<span class="link"
      onclick="fm004.openCam('${selectData.OpResult.PageData.Row[0].camId}', '${selectData.OpResult.PageData.Row[0].camAddr}');">(视频)</span>


