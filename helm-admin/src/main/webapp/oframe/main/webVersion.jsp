<%--
  Created by IntelliJ IDEA.
  User: shfb_wang
  Date: 2015/12/29 0029 14:13
  Copyright(c) 北京四海富博计算机服务有限公司
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div style="width: 100%">
    <table class="border" style="width: 100%;">
        <tr style="text-align: center;">
            <td colspan="4"><span style="font-size: 25px !important;">系统版本</span></td>
        </tr>
        <tr>
            <th>包名：</th>
            <td>${title}</td>
            <th>版本号：</th>
            <td>${version}</td>
        </tr>
        <tr>
            <th>包路径：</th>
            <td>${vendor}</td>
            <th>构建JDK：</th>
            <td>${buildJdk}</td>
        </tr>
        <tr>
            <th>构建人：</th>
            <td>${builtBy}</td>
            <th>构建时间：</th>
            <td>${buildTime}</td>
        </tr>
        <tr>
            <th>依赖 oframe jar：</th>
            <td>
                <ul>
                    <c:forEach items="${dependOframe}" var="item">
                        <li>${item}</li>
                    </c:forEach>
                </ul>
            </td>
            <th>依赖 eland jar：</th>
            <td>
                <ul>
                    <c:forEach items="${dependEland}" var="item">
                        <li>${item}</li>
                    </c:forEach>
                </ul>
            </td>
        </tr>
    </table>
</div>
<script>

</script>