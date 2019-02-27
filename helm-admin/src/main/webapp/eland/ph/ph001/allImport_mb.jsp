<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div>
    <div class="panelBar">
        <ul class="toolBar">
            <li><a class="save" onclick="allImport.allImport()"><span>导入</span></a></li>
            <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
        </ul>
    </div>
    <div>
        <form id="importMbForm" method="post" class="required-validate">
            <c:choose>
                <c:when test="${docFileUrl != ''}">
                    <table class="border">
                        <tr>
                            <th><label>导入说明：</label></th>
                            <td>请使用系统提供的
                                <a target="_blank" onclick="allImport.downTemplate('${docFileUrl}')"
                                   style="display: inline-block; color: #03408b; padding: 0 5px 0 5px;"
                                   href="javascript:void(0)">模板</a>填写明细数据。
                            </td>
                        </tr>

                        <tr>
                            <th><label>文件选择：</label></th>
                            <td>
                                <input type="file" name="importMbFile" id="importMbFile" accept=".xls, .xlsx"/>
                            </td>
                        </tr>
                    </table>
                </c:when>
                <c:otherwise>
                    <h1 style="color: red; text-align:  center; line-height: 80px;font-size: 18px;">
                        没有配置导入模板
                    </h1>
                </c:otherwise>
            </c:choose>

        </form>
    </div>
</div>
<script>
    var allImport = {

        /**
         * 下载导入的模板
         **/
        downTemplate: function (docFileUrl) {
            if (docFileUrl && docFileUrl != "") {
                var url = getGlobalPathRoot() + "oframe/common/file/file-downByPath.gv?prjCd=" + getPrjCd()
                        + "&docName=" + encodeURI(encodeURI("信息导入模板"))
                        + "&urlType=r"
                        + "&docFileUrl=" + docFileUrl;
                // 执行导出功能
                window.open(url);
            } else {
                alertMsg.warn("没有配置模板");
            }
        },
        /**
         * 全量导入方案
         */
        allImport: function () {
            var importMbFile = $("#importMbFile", $.pdialog.getCurrent()).val();
            if (!importMbFile || importMbFile == "") {
                alertMsg.warn("请选择导入文件");
                return;
            }
            var uploadURL = getGlobalPathRoot() + "eland/ph/ph001/ph001-allImport.gv?prjCd=" + getPrjCd();
            var ajaxbg = $("#background,#progressBar");
            ajaxbg.show();
            $.ajaxFileUpload({
                url: uploadURL,
                secureuri: false,
                fileElementId: "importMbFile",
                dataType: 'json',
                success: function (data) {
                    if (data.isSuccess) {
                        alertMsg.correct("处理成功");
                        $.pdialog.closeCurrent();
                    } else {
                        alertMsg.error(data.errMsg);
                    }
                    ajaxbg.hide();
                }
            })
        }
    }
</script>
