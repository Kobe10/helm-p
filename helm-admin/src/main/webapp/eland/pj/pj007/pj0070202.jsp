<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="pageContent" layoutH="1" style="padding: 0;margin: 0;">
    <div class="panelBar">
        <ul class="toolBar">
            <li><a class="add" onclick="pj0070202.setRow('${fromOp}')"><span>确定</span></a></li>
        </ul>
    </div>
    <form method="post" class="required-validate">
        <div>
            <table id="pj0070201Table" class="form">
                <tr>
                    <th width="20%"><label>楼层编号：</label></th>
                    <td>
                        <input type="text" name="floorNm" value="${floorNm}"
                               class="required"/>
                        <input type="hidden" name="floorIdx" value="${floorIdx}">
                    </td>
                </tr>
            </table>
        </div>
    </form>
</div>
<script src="${pageContext.request.contextPath}/eland/pj/pj007/js/pj0070202.js" type="text/javascript"></script>