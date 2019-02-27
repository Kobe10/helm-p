<%--安置房源详细信息展示--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%--房源统计信息--%>
<div>
    <div class="panelBar">
        <ul class="toolBar">
            <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
        </ul>
    </div>
    <div>
        <form id="ph010Rgform" method="post" class="required-validate">
            <input type="hidden" name="regUseType" value="${regUseType}">
            <table class="border">
                <tr>
                    <th><label>导入说明：</label></th>
                    <td>请使用系统提供的<a target="_blank"
                                   style="display: inline-block; color: #03408b; padding: 0 5px 0 5px;"
                                   href="${pageContext.request.contextPath}/eland/ph/ph010/ph010-downTemplate.gv"
                            >模板</a>填写摄像头信息。
                    </td>
                </tr>
                <tr>
                    <td style="text-align:right"><label>导入文件：</label>
                    </td>
                    <th style="text-align:left">
                        <a class="btnView" title="导入" style="position: relative;" onclick="ph010.startImport()">
                                <span>导入</span>
                                <input style="width:110px; height:37px; position:absolute;
                                right:0; top:0; opacity:0;filter:alpha(opacity=0); z-index:11; cursor:pointer;"
                                       id="uploadFile" name="uploadFile" type="file">
                            </a>
                    </th>
                </tr>
            </table>
        </form>
    </div>
</div>
