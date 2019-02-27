<%--院落腾退流程--OA资料准备--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="panelBar">
    <ul class="toolBar">
        <li><a class="import" onclick="ph008OpenOash.uploadFjFile(this);"><span>上传</span></a></li>
        <li><a class="export" onclick="ph008OpenOash.exportFjCl('${relId}');"><span>导出</span></a></li>
        <li><a class="reflesh" onclick="$.pdialog.reloadCurrent();"><span>刷新</span></a></li>
        <li><a class="close" onclick="$.pdialog.closeCurrent();"><span>关闭</span></a></li>
    </ul>
</div>
<div class="album-list">
    <%--关联编号--%>
    <input type="hidden" name="relId" value="${relId}">
    <%--关联类型--%>
    <input type="hidden" name="relType" value="${relType}">
    <ul class="album-ul">
        <c:forEach items="${attachTypeSummary}" var="item">
            <li class="album-li" onclick="ph008OpenOash.viewPic('${item.ids}')" style="text-align: center;">
                <div class="album-context"
                     style="background: url(${pageContext.request.contextPath}/oframe/themes/images/fileicon.png);">
                    <input type="hidden" name="ph00315_docId" class="js_docIds" value="${item.ids}"/>
                </div>
                    <span class="album-title"
                          style="text-align: center; height: auto; width: 120px;">${item.name}(${item.count})</span>
            </li>
        </c:forEach>
    </ul>
</div>
<script type="text/javascript">
    ph008OpenOash = {
        dialogId: null,

        viewPic: function (docIds) {
            // 当前的弹出框ID
            ph008OpenOash.dialogId = $.pdialog.getCurrent().data("id");
            var url = getGlobalPathRoot() + "oframe/common/file/file-view.gv";
            var ajaxPacket = new AJAXPacket(url);
            ajaxPacket.data.add("prjCd", getPrjCd());
            ajaxPacket.data.add("docIds", docIds);
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                navTab.getCurrentPanel().append($(response));
                FileViewer.closeFunc = function () {
                    // 触发加载
                    ph008OpenOash.closedDoc();
                };
            });
        },

        /**
         * 获取附件类型
         * @param obj
         * @returns {string}
         */
        getDocTypeRelUrl: function (obj) {
            var relType = "${relType}";
            var itemCd = "";
            if (relType == "100") {
                itemCd = "HS_DOC_REL_TYPE";
            } else if (relType == "200") {
                itemCd = "YARD_DOC_REL_TYPE";
            }
            return getGlobalPathRoot() + "oframe/sysmg/sys005/sys005-data.gv?prjCd=" + getPrjCd() + "&itemCd=" + itemCd;
        },

        //上传附件
        uploadFjFile: function (clickObj) {
            // 当前的弹出框ID
            ph008OpenOash.dialogId = $.pdialog.getCurrent().data("id");
            // 打开上传对话框
            var $span = $(clickObj);
            var docIds = $span.find("input[type=hidden][name=ph00315_docId]").val();
            var relId = $("input[name=relId]", $.pdialog.getCurrent()).val();
            var relType = $("input[name=relType]", $.pdialog.getCurrent()).val();
            var docTypeName = $span.attr("docTypeName");
            if (!docTypeName) {
                docTypeName = "";
            }
            docTypeName = encodeURI(encodeURI(docTypeName));
            var editAble = $span.attr("editAble");
            if (!editAble) {
                editAble = true;
            }
            //
            file.getDocTypeRelUrl = ph008OpenOash.getDocTypeRelUrl;
            var url = getGlobalPathRoot() + "oframe/common/file/file-uploadFile.gv?prjCd=" + getPrjCd() + "&docTypeName=" + docTypeName +
                    "&docIds=" + docIds + "&editAble=" + editAble
                    + "&relType=" + relType
                    + "&relId=" + relId;
            $.pdialog.open(url, "upFile", "附件上传", {
                height: 600, width: 800, mask: true,
                close: editAble ? ph008OpenOash.closedDoc : "",
                param: {clickSpan: $span}
            });
        },
        /**
         * 关闭上传并同步数据保存
         * @param param
         * @returns {boolean}
         */
        closedDoc: function (param) {
            $.pdialog.reloadDialog(ph008OpenOash.dialogId);
            return file.closeD();
        },

        /**
         * 导出 生成公房结算明细表（房屋）
         * @param buildId
         */
        exportFjCl: function (relId) {
            var relType = "${relType}";
            var url = getGlobalPathRoot() + "eland/ph/ph003/ph003-exportFj.gv?hsId=" + relId + "&prjCd=" + getPrjCd();
            if (relType == "100") {
                url = getGlobalPathRoot() + "eland/ph/ph003/ph003-exportFj.gv?hsId=" + relId + "&prjCd=" + getPrjCd();
            } else if (relType == "200") {
                url = getGlobalPathRoot() + "eland/ph/ph002/ph002-exportFj.gv?buildId=" + relId + "&prjCd=" + getPrjCd();
            }
            window.open(url)
        }
    }
</script>