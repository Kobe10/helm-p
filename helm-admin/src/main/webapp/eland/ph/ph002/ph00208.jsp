<%--附件管理--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="panel">
    <h1>附件信息
        <span class="panel_menu js_reload js_uploadFjEnd">刷新</span>
        <span class="panel_menu" onclick="ph002Op.exportYardFj('${buildId}');">导出居民附件</span>
        <span class="panel_menu" onclick="ph002Op.exportBuildFj('${buildId}');">导出院落附件</span>
        <span class="panel_menu" onclick="ph00208.showFjFile();">浏览</span>
        <span class="panel_menu js_docTypeName" editAble="true" docTypeName=""
              onclick="ph00208.uploadFjFile(this);">上传</span>
    </h1>
    <%--面板内容--%>
    <div class="js_panel_context js_ph00206ChartDiv_08 album-list"
         style="min-height: 300px;">
        <ul class="album-ul">
            <c:forEach items="${attachTypeSummary}" var="item">
                <li class="album-li" onclick="ph00208.viewPic('${item.ids}')" style="text-align: center;">
                    <div class="album-context"
                         style="background: url(${pageContext.request.contextPath}/oframe/themes/images/fileicon.png);">
                        <input type="hidden" name="ph00208_docId" class="js_docIds" value="${item.ids}"/>
                    </div>
                    <span class="album-title"
                          style="text-align: center; height: auto; width: 120px;">
                          ${item.name}(${item.count})
                    </span>
                </li>
            </c:forEach>
        </ul>
    </div>
</div>
<script type="text/javascript">
    ph00208 = {
        //浏览附件的方法
        showFjFile: function () {
            var docIdTemp;
            $("input[name=ph00208_docId]").each(function () {
                if (docIdTemp) {
                    docIdTemp = $(this).val() + "," + docIdTemp;
                } else {
                    docIdTemp = $(this).val();
                }
            });
            //展示所有的附件。
            var url = getGlobalPathRoot() + "oframe/common/file/file-view.gv";
            var ajaxPacket = new AJAXPacket(url);
            ajaxPacket.data.add("docIds", docIdTemp);
            ajaxPacket.data.add("prjCd", getPrjCd());
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var obj = $(response);
                navTab.getCurrentPanel().append($(response));
                FileViewer.closeFunc = function () {
                    // 触发加载
                    ph00208.closedDoc();
                };
            });
        },
        viewPic: function (docIds) {
            var url = getGlobalPathRoot() + "oframe/common/file/file-view.gv";
            var ajaxPacket = new AJAXPacket(url);
            ajaxPacket.data.add("docIds", docIds);
            ajaxPacket.data.add("prjCd", getPrjCd());
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                navTab.getCurrentPanel().append($(response));
                FileViewer.closeFunc = function () {
                    // 触发加载
                    ph00208.closedDoc();
                };
            });
        },
        /**
         * 获取附件类型
         * @param obj
         * @returns {string}
         */
        getDocTypeRelUrl: function (obj) {
            return getGlobalPathRoot() + "oframe/sysmg/sys005/sys005-data.gv?prjCd=" + getPrjCd() + "&itemCd=YARD_DOC_REL_TYPE";
        },

        //上传附件
        uploadFjFile: function (clickObj) {
            var $span = $(clickObj);
            var docIds = $span.find("input[type=hidden]").val();
            var hsId = $("input[name=hsId]", navTab.getCurrentPanel()).val();
            var docTypeName = $span.attr("docTypeName");
            if (!docTypeName) {
                docTypeName = "";
            }
            docTypeName = encodeURI(encodeURI(docTypeName));
            var editAble = $span.attr("editAble");
            if (!editAble) {
                editAble = true;
            }
            // 获取下拉URL的方法
            file.getDocTypeRelUrl = ph00208.getDocTypeRelUrl;

            var url = getGlobalPathRoot() + "oframe/common/file/file-uploadFile.gv?prjCd=" + getPrjCd() + "&docTypeName=" + docTypeName +
                    "&docIds=" + docIds + "&editAble=" + editAble
                    + "&relType=200&relId=${buildId}";
            $.pdialog.open(url, "file", "附件上传", {
                height: 600, width: 800, mask: true,
                close: editAble ? ph00208.closedDoc : "",
                param: {clickSpan: $span}
            });
        },

        /**
         * 关闭上传并同步数据保存
         * @param param
         * @returns {boolean}
         */
        closedDoc: function (param) {
            file.getDocTypeRelUrl = null;
            $("span.js_uploadFjEnd", navTab.getCurrentPanel()).click();
            return file.closeD();
        }
    };

</script>
