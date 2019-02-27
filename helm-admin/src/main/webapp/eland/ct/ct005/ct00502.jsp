<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="panelBar">
    <ul class="toolBar">
        <c:set var="hsCtId" value="${hsInfo.HouseInfo.HsCtInfo.hsCtId}"/>
        <li onclick="ct005.ctInfo('${hsId}', '${hsCtId}', 'viewDoc');">
            <a class="print" href="javascript:void(0)"><span>协议信息</span></a>
        </li>
        <c:if test="${ctStatus == '2'}">
            <oframe:op prjCd="${param.prjCd}" rhtCd="cancel_ct_${schemeType}_rht" cssClass="cancel"
                       template="li" name="取消签约" onClick="ct005.cancelCt('${hsId}','${hsCtId}');"/>
        </c:if>
        <li onclick="ct005.viewHouse(${hsId});">
            <a class="new-area" href="javascript:void(0)"><span>居民详情</span></a>
        </li>
    </ul>
</div>
<div class="panelcontainer" layoutH="55" style="border: 1px solid #e9e9e9;">
    <table class="border">
        <tr>
            <th width="10%"><label>房屋编号：</label></th>
            <td width="15%">${hsInfo.HouseInfo.hsCd}</td>
            <th width="10%"><label>被安置人：</label></th>
            <td width="20%">${hsInfo.HouseInfo.hsOwnerPersons}</td>
            <th width="10%"><label>房屋地址：</label></th>
            <td width="30%">${hsInfo.HouseInfo.hsFullAddr}</td>
        </tr>
        <tr>
            <th><label>安置方式：</label></th>
            <td><oframe:name prjCd="${param.prjCd}" itemCd="10001" value="${hsInfo.HouseInfo.HsCtInfo.ctType}"/></td>
            <th><label>签约状态：</label></th>
            <td>
                <oframe:name prjCd="${param.prjCd}" itemCd="CONTRACT_STATUS"
                             value="${ctStatus}"/>
            </td>
            <th><label>签约时间：</label></th>
            <td>${ctDate}</td>
        </tr>
    </table>
    <div id="ct005_ReportData" layoutH="127">
        <table width="100%">
            <tr>
                <td width="49%">
                    <div class="panel js_ct00502_file">
                        <h1>签字协议
                            <span class="panel_menu" docTypeName="${signDocName}" relType="100"
                                  onclick="ct00502.showDoc(this,'${hsId}')">
                                <label style="cursor:pointer;">上传</label>
                                <input type="hidden" name="docIds" value='${signDocIds}'>
                            </span>
                            <span class="panel_menu" onclick="ct00502.showFjFile(this)">
                                <label style="cursor:pointer;">预览</label>
                            </span>
                        </h1>

                        <div class="js_panel_context album-list" layoutH="170">
                            <ul class="album-ul js_hidden_file_ul">
                                <li class="album-li js_hidden_file_li" style="text-align: center;display: none"
                                    onclick="ct00502.viewPic(${item.Row.docId})">
                                    <div class="album-context">
                                        <img class="show" style="max-width: 120px;" width="120px" height="130px"
                                             title="${item.Row.docName}"
                                             src="${pageContext.request.contextPath}/oframe/common/file/file-download.gv?docId=${item.Row.docId}"/>
                                        <input type="hidden" name="ct00502_docId" class="js_docIds"
                                               value="${item.Row.docId}"/>
                                    </div>
                                    <span class="js_span_docName spanText">${item.Row.docName}</span>
                                </li>
                                <c:forEach items="${hsDocs}" var="item">
                                    <c:if test="${item.Row.docTypeName == signDocName}">
                                        <li class="album-li" style="text-align: center;"
                                            onclick="ct00502.viewPic(${item.Row.docId})">
                                            <div class="album-context">
                                                <img class="show" style="max-width: 120px;" width="120px" height="130px"
                                                     title="${item.Row.docName}"
                                                     src="${pageContext.request.contextPath}/oframe/common/file/file-downview.gv?docId=${item.Row.docId}"/>
                                                <input type="hidden" name="ct00502_docId" class="js_docIds"
                                                       value="${item.Row.docId}"/>
                                            </div>
                                            <span class="spanText">${item.Row.docName}</span>
                                        </li>
                                    </c:if>
                                </c:forEach>
                            </ul>
                        </div>
                    </div>
                </td>
                <td>
                    <div class="panel js_ct00502_file">
                        <h1>现场拍照
                            <span class="panel_menu" docTypeName="${photoDocName}" relType="100"
                                  onclick="ct00502.showDoc(this,'${hsId}')">
                                <label style="cursor:pointer;">上传</label>
                                <input type="hidden" name="docIds" value='${photoDocIds}'>
                            </span>
                            <span class="panel_menu" onclick="ct00502.showFjFile(this)">
                                <label style="cursor:pointer;">预览</label>
                            </span>
                        </h1>

                        <div class="js_panel_context album-list" layoutH="170">
                            <ul class="album-ul js_hidden_file_ul">
                                <li class="album-li js_hidden_file_li" style="text-align: center;display: none"
                                    onclick="ct00502.viewPic(${item.Row.docId})">
                                    <div class="album-context">
                                        <img class="show" style="max-width: 120px;" width="120px" height="130px"
                                             title="${item.Row.docName}"
                                             src="${pageContext.request.contextPath}/oframe/common/file/file-download.gv?docId=${item.Row.docId}"/>
                                        <input type="hidden" name="ct00502_docId" class="js_docIds"
                                               value="${item.Row.docId}"/>
                                    </div>
                                    <span class="js_span_docName spanText">${item.Row.docName}</span>
                                </li>
                                <c:forEach items="${hsDocs}" var="item">
                                    <c:if test="${item.Row.docTypeName == photoDocName}">
                                        <li class="album-li" style="text-align: center;"
                                            onclick="ct00502.viewPic(${item.Row.docId})">
                                            <div class="album-context">
                                                <img class="show" style="max-width: 120px;" width="120px" height="130px"
                                                     title="${item.Row.docName}"
                                                     src="${pageContext.request.contextPath}/oframe/common/file/file-downview.gv?docId=${item.Row.docId}"/>
                                                <input type="hidden" name="ct00502_docId" class="js_docIds"
                                                       value="${item.Row.docId}"/>
                                            </div>
                                            <span class="spanText">${item.Row.docName}</span>
                                        </li>
                                    </c:if>
                                </c:forEach>
                            </ul>
                        </div>
                    </div>
                </td>
            </tr>
        </table>
    </div>
</div>
<%--<oframe:script src="${pageContext.request.contextPath}/eland/ct/ct005/js/ct005.js" type="text/javascript"/>--%>
<script type="text/javascript">
    var ct00502 = {
        //查看 附件
        viewPic: function (docIds) {
            var url = getGlobalPathRoot() + "oframe/common/file/file-view.gv";
            var ajaxPacket = new AJAXPacket(url);
            ajaxPacket.data.add("docIds", docIds);
            ajaxPacket.data.add("prjCd", getPrjCd());
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                navTab.getCurrentPanel().append($(response));
                FileViewer.closeFunc = function () {
                    // 触发加载
                    ct00502.closedDoc();
                };
            });
        },

        //浏览所有附件的方法
        showFjFile: function (obj) {
            var $this = $(obj);
            var docIdTemp = $this.closest("div.js_ct00502_file").find("input[name=docIds]").val();
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
                    ct00502.closedDoc();
                };
            });
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
            //
            file.getDocTypeRelUrl = ct00502.getDocTypeRelUrl;
            var url = getGlobalPathRoot() + "oframe/common/file/file-uploadFile.gv?prjCd=" + getPrjCd() + "&docTypeName=" + docTypeName +
                    "&docIds=" + docIds + "&editAble=" + editAble
                    + "&relType=100&relId=${hsId}";
            $.pdialog.open(url, "file", "居民结算签字协议上传", {
                height: 600, width: 800, mask: true,
                close: editAble ? ct00502.closedDoc : "",
                param: {clickSpan: $span}
            });
        },

        closedDoc: function (param) {
            //关闭前刷新本页
            ct005.photograph('${hsId}', 'hsCtId');
            return file.closeD();
        },

        /**
         * 显示关联文档信息
         * @param clickObj 点击对象
         */
        showDoc: function (clickObj, relId) {
            var $span = $(clickObj);
            var docIds = $span.find("input[type=hidden]").val();
            var docTypeName = $span.attr("docTypeName");
            if (!docTypeName) {
                docTypeName = "";
            }
            docTypeName = encodeURI(encodeURI(docTypeName));

            var relType = $span.attr("relType");

            var editAble = $span.attr("editAble");
            if (!editAble) {
                editAble = true;
            }
            var url = getGlobalPathRoot() + "oframe/common/file/file-init.gv?prjCd=" + getPrjCd() + "&docTypeName=" + docTypeName
                    + "&docIds=" + docIds + "&editAble=" + editAble
                    + "&relType=" + relType + "&relId=" + relId;
            $.pdialog.open(url, "file", "附件上传", {
                height: 600, width: 800, mask: true,
                close: ct00502.docClosed,
                param: {clickSpan: $span}
            });
        },

        /**
         * 关闭上传窗口
         * @param param
         * @returns {boolean}
         */
        docClosed: function (param) {
            var uploadedFiles = file.getAllUploadFiles();
            var $span = param.clickSpan;
            //处理上传附件，展示
            var currentDiv = $span.closest("div.js_ct00502_file");
            var oldDocIds = $span.find("input[name=docIds]").val();
            var oldDocIdArr = [];
            oldDocIdArr = oldDocIds.split(",");
            var docIdArr = [];
            for (var i = 0; i < uploadedFiles.length; i++) {
                //保存已经上传的docId
                var uploadedFile = uploadedFiles[i];
                docIdArr.push(uploadedFile.docId);
            }
            $span.find("input[name=docIds]").val(docIdArr.join(","));
            ct005.photograph('${hsId}', '${hsCtId}');
            // 调用文件的关闭
            return file.closeD();
        }
    }
</script>
