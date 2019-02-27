<%--文字类型检索条件，自定义--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%--数据过滤类型(文本过滤)--%>
<input type="hidden" class="js_condType" value="staff">

<div class="triangle"></div>
<div>
    <table border="0" width="100%">
        <tr>
            <td style="border: 0;text-align: right;" width="120px;">
                <span class="title">过滤条件：</span>
            </td>
            <td style="border: 0;">
                <input type="hidden" class="js_staff_id" value="${conditionValue}"
                       hAttr="staffId">
                <input name="targetPerTemp" type="text"
                       class="pull-left autocomplete js_staff_name"
                       prjCd="-"
                       value="<oframe:staff staffId="${conditionValue}"/>"
                       atOption="CODE.getStaffOpt"/>
                <a title="选择"
                   onclick="$.fn.sltStaff(this,{offsetX: 0, fromOp:'sys01401', prjCd:getPrjCd()});"
                   class="btnLook">选择</a>
            </td>
        </tr>
    </table>
</div>
<div style="text-align: center" class="mart10">
    <button type="button" class="btn btn-pri js_cfm_con">确定</button>
    <button type="button" class="btn btn-opt marl10 js_cle_con">取消</button>
</div>


