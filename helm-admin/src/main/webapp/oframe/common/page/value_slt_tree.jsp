<%--文字类型检索条件，自定义--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<%--数据过滤类型(文本过滤)--%>
<input type="hidden" class="js_condType" value="codeTree">

<div class="triangle"></div>
<div>
    <table border="0" width="100%">
        <tr>
            <td style="border: 0;text-align: right;" width="120px;">
                <span class="title">过滤条件：</span>
            </td>
            <td style="border: 0;">
                <input type="hidden" class="js_code_cd" value="${sltValues}" hAttr="sltValues">
                <input name="targetPerTemp" type="text" value="${sltNames}"
                       readonly class="pull-left autocomplete js_code_name" prjCd="${param.prjCd}"/>
                <a title="选择" onclick="valueSltTree.openSltCode(this)" class="btnLook">选择</a>
            </td>
        </tr>
    </table>
</div>
<div style="text-align: center" class="mart10">
    <button type="button" class="btn btn-pri js_cfm_con">确定</button>
    <button type="button" class="btn btn-opt marl10 js_cle_con">取消</button>
</div>
<script type="text/javascript">
    var valueSltTree = {
        openSltCode:function (obj) {
            var sltData = $(obj).parent().find("input.js_code_cd").val();
            sltData = sltData.split("|").join(",");
            $.fn.sltCode(obj,{offsetX: 0, itemCd:'${itemCd}',treeCheck:true, sltData: sltData, prjCd:getPrjCd()});
        }
    }
</script>


