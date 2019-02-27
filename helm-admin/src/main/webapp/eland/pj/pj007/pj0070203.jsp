<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="pageContent" layoutH="1" style="padding: 0;margin: 0;">
    <div class="panelBar">
        <ul class="toolBar">
            <li><a class="add" onclick="pj0070203.setCol('${fromOp}')"><span>确定</span></a></li>
        </ul>
    </div>
    <form method="post" class="required-validate">
        <div>
            <table id="pj0070201Table" class="form">
                <tr>
                    <th width="20%"><label>房屋门号：</label></th>
                    <td>
                        <input type="text" name="doorNm" value="${doorNm}"
                               class="required"/>
                        <input type="hidden" name="unitIdx" value="${unitIdx}">
                        <input type="hidden" name="doorIdx" value="${doorIdx}">
                    </td>
                </tr>
                <c:choose>
                    <c:when test="${regUseType != 1}">
                        <tr>
                            <th width="20%"><label>房屋户型：</label></th>
                            <td>
                                <oframe:select prjCd="${param.prjCd}" collection="${hxMap}" name="hsHx" value="${hsHx}"></oframe:select>
                            </td>
                        </tr>
                    </c:when>
                </c:choose>
            </table>
        </div>
    </form>
</div>
<script src="${pageContext.request.contextPath}/eland/pj/pj007/js/pj0070203.js" type="text/javascript"></script>