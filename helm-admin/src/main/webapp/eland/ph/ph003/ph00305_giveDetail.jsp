<%--赠送详情--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="pagecontent">
    <div class="panel">
        <div class="js_panel_context">
            <table width="100%" class="border">
                <tr>
                    <th>房源区域：</th>
                    <td colspan="3"><oframe:entity entityName="RegInfo" property="regName" prjCd="${prjCd}"
                                                   value="${giveInfo.HsCtGiveInfo.regId}"/></td>
                </tr>
                <tr>
                    <th>赠送方：</th>
                    <td><oframe:entity prjCd="${param.prjCd}" entityName="HsCtInfo" property="ctPsNames"
                                       value="${giveInfo.HsCtGiveInfo.hsCtAId}"/></td>
                    <th>接收方：</th>
                    <td><oframe:entity  prjCd="${param.prjCd}" entityName="HsCtInfo" property="ctPsNames"
                                       value="${giveInfo.HsCtGiveInfo.hsCtBId}"/></td>
                </tr>
                <tr>
                    <th>赠送面积：</th>
                    <td>${giveInfo.HsCtGiveInfo.ctSize} (平方米)</td>
                    <th>赠送时间：</th>
                    <td>${giveInfo.HsCtGiveInfo.giveStatusDate}</td>
                </tr>
                <tr>
                    <td colspan="4" style="text-align: center;">
                        <span class="btn-primary" style="padding: 5px 10px 5px 10px; line-height: 34px; cursor:pointer;"
                              onclick="$.pdialog.closeCurrent(this);">关闭</span>
                    </td>
                </tr>
            </table>
        </div>
    </div>
</div>