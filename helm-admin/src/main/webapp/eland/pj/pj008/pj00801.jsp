<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="panelBar">
    <ul class="toolBar">
        <oframe:op prjCd="${param.prjCd}" template="li" cssClass="save"
                   name="保存" rhtCd="add_reg_hx_rht"
                   onClick="pj00801.saveHx();"/>
        <li onclick="$.pdialog.closeCurrent();"><a class="close"><span>关闭</span></a></li>
    </ul>
</div>
<div>
    <form id="pj00801frm" method="post">
        <input type="hidden" name="hxId" value="${nodeInfo.PrjHsHx.hxId}"/>
        <input type="hidden" name="prjCd" value="${prjCd}"/>
        <input type="hidden" name="regId" value="${regId}"/>
        <input type="hidden" name="regUseType" value="${regUseType}"/>
        <table class="border">
            <tr>
                <th>
                    <label>户型编号：</label>
                </th>
                <td>
                    <input type="text" name="hxCd" readonly="true" class="required readonly"
                           value="${nodeInfo.HsHx.hxCd}"/>
                </td>
                <th>
                    <label>户型结构：</label>
                </th>
                <td>
                    <oframe:select prjCd="${param.prjCd}" itemCd="HS_ROOM_TYPE" value="${nodeInfo.HsHx.hxName}"

                                   name="hxName"/>
                </td>
                <th>
                    <label>户型朝向：</label>
                </th>
                <td>
                    <oframe:select prjCd="${param.prjCd}" itemCd="HS_ROOM_DT" name="hxDt"
                                   value="${nodeInfo.HsHx.hxDt}"/>
                </td>
            </tr>
            <tr>
                <th>
                    <label>建筑面积：</label>
                </th>
                <td>
                    <input type="text" name="hxBldSize" class="required number"
                           onkeyup="pj00801.calculateTotalPrice();"
                           value="${nodeInfo.HsHx.hxBldSize}"/>
                </td>
                <th>
                    <label>使用面积：</label>
                </th>
                <td>
                    <input type="text" name="hxUseSize" class="number"
                           value="${nodeInfo.HsHx.hxUseSize}"/>
                </td>
                <th>
                    <label>销售单价：</label>
                </th>
                <td>
                    <input type="text" name="hxSalePrice" class="number"
                           onkeyup="pj00801.calculateTotalPrice();"
                           value="${nodeInfo.HsHx.hxSalePrice}"/>
                </td>
            </tr>
            <tr>
                <th><label>总房款：</label></th>
                <td colspan="7">
                    <span id="pj008TotalPrice">${totalPrice}</span>
                </td>
            </tr>

            <tr>
                <th><label>户型描述：</label></th>
                <td colspan="7" valign="top">
                    <textarea rows="8" cols="25" name="hxDesc">${nodeInfo.HsHx.hxDesc}</textarea>
                </td>
            </tr>
            <tr>
                <th>
                    <label>户型图：</label>
                    <input type="hidden" name="hxImgPath" value="${nodeInfo.HsHx.hxImgPath}"/>
                </th>
                <td colspan="7">
                    <div style="width: 200px; height: 210px; display: inline-block;" id="pj00801drag"
                         class="drag" ondrop="pj00801.dropFile(event)"
                         ondragenter="return false"
                         ondragover="return false">
                        <span class="close"></span>
                        <c:choose>
                            <c:when test="${nodeInfo.HsHx.hxImgPath != null && nodeInfo.HsHx.hxImgPath != ''}">
                                <img height='100%' width='100%' onclick="pj00801.viewImg()"
                                     src="${pageContext.request.contextPath}/oframe/common/file/file-downview.gv?docId=${nodeInfo.HsHx.hxImgPath}"/>
                            </c:when>
                        </c:choose>
                    </div>
                    <span style="display: inline-block;position: relative;" onclick="pj00801.startImport()">
                        <button type="button" class="btn btn-info btn-opt-sm">上传
                        </button>
                        <input style="width:80px; height:37px; position:absolute; left:0; top:0;
                                     opacity:0;filter:alpha(opacity=0); z-index:1100; cursor:pointer;"
                               accept="*.jpg;*.bmp;*.png;*.gif;"
                               name="uploadFile" id="uploadFile" type="file">
                    </span>
                </td>
            </tr>
        </table>
    </form>
</div>
<oframe:script src="${pageContext.request.contextPath}/eland/pj/pj008/js/pj00801.js" type="text/javascript"/>

