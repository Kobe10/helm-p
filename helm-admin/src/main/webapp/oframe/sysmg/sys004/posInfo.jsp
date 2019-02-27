<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="pageContent">
    <div class="panelBar marb10">
        <ul class="toolBar">
            <oframe:op prjCd="${param.prjCd}" template="li" cssClass="save" name="保存"
                       rhtCd="EDIT_CMP_ORG_RHT" onClick="sys004.savePosInfo();"/>
            <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
        </ul>
    </div>
    <div layoutH="100">
        <form method="post" id="sys004PosInfoFrm" class="required-validate">
            <input type="hidden" name="clickIdx" value="${clickIdx}">
            <table class="form">
                <tr>
                    <th><label>岗位名称：</label></tH>
                    <td><input type="text" name="posName" value="${posName}" class="msize required"></td>
                </tr>
                <tr>
                    <th><label>岗位职务：</label></tH>
                    <td><oframe:select prjCd="${param.prjCd}" itemCd="POS_TYPE" name="posType" value="${posType}"/></td>
                </tr>
                <tr>
                    <th><label>岗位描述：</label></tH>
                    <td><textarea cols="80" rows="5" name="posDesc">${posDesc}</textarea></td>
                </tr>
            </table>
        </form>
    </div>
</div>

