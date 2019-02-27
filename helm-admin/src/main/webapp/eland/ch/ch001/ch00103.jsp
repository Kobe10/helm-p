<%--待签约处理--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oframe" uri="http://www.bjshfb.com/core/web/tags/oframe" %>
<div class="panelBar">
    <ul class="toolBar marl5">

        <c:set var="ctType" value="${ctType}${''}"/>
        <c:if test="${ctType != '2'}">
            <oframe:op prjCd="${param.prjCd}" template="li" cssClass="cancel"
                       name="取消选房" rhtCd="cancel_ch_cfm_rht"
                       onClick="ch001.quXiao();"/>
        </c:if>
        <c:if test="${ctType == '2'}">
            <oframe:op prjCd="${param.prjCd}" template="li" cssClass="cancel"
                       name="取消选房" rhtCd="cancel_ch_cfm_rht"
                       onClick="ch001.quXiaoChoose();"/>
            <%--<li onclick="ch001.chInfo(${hsId},'choose');">--%>
                <%--<a class="print" href="javascript:void(0)"><span>选房详情</span></a>--%>
            <%--</li>--%>
        </c:if>
        <li onclick="ch001.chInfo('${hsId}','print');">
            <a class="new-house" href="javascript:void(0)"><span>选房确认单</span></a>
        </li>
        <li onclick="ch001.viewHouse(${hsId});">
            <a class="new-area" href="javascript:void(0)"><span>居民详情</span></a>
        </li>
        <li onclick="ch001.singleQuery(${hsInfo.HouseInfo.HsCtInfo.chooseHsSid},'next');" style="float: right">
            <a class="export" href="javascript:void(0)"><span>下一户</span></a>
        </li>
        <li onclick="ch001.singleQuery(${hsInfo.HouseInfo.HsCtInfo.chooseHsSid},'last');" style="float: right">
            <a class="import" href="javascript:void(0)"><span>上一户</span></a>
        </li>
    </ul>
</div>
<div class="panelcontainer" id="ch001Head" style="border: 1px solid #e9e9e9;">
    <input type="hidden" name="hidHsId" value="${hsId}">
    <input type="hidden" name="hsCtId" value="${hsInfo.HouseInfo.HsCtInfo.hsCtId}">
    <input type="hidden" id="ctType" value="${ctType}">
    <table class="border">
        <tr>
            <th width="7%"><label>档案编号：</label></th>
            <td width="10%">${hsInfo.HouseInfo.hsCd}</td>
            <th width="7%"><label>被安置人：</label></th>
            <td width="15%">${hsInfo.HouseInfo.hsOwnerPersons}</td>
            <th width="7%"><label>房屋地址：</label></th>
            <td width="20%">${hsInfo.HouseInfo.hsFullAddr}</td>
        </tr>
        <tr>
            <th><label>安置方式：</label></th>
            <td>
                <oframe:name prjCd="${param.prjCd}" value="${hsInfo.HouseInfo.HsCtInfo.ctType}"
                             itemCd="10001"/>
            </td>
            <th><label>选房状态：</label></th>
            <td>
                <oframe:name prjCd="${param.prjCd}" value="${hsInfo.HouseInfo.HsCtInfo.chHsStatus}"
                             itemCd="CHOOSE_STATUS"/>
            </td>
            <th><label>选房序号：</label></th>
            <td>${hsInfo.HouseInfo.HsCtInfo.chooseHsSid}</td>
        </tr>
    </table>
    <table class="list mart5" width="100%" style="border-collapse: collapse">
        <thead>
        <tr>
            <th>购房地址</th>
            <th>购房人</th>
            <th>与产承人关系</th>
            <th>购房证件</th>
            <th>联系电话</th>
        </tr>
        </thead>
        <c:forEach items="${hsInfos}" var="item" varStatus="varStatus">
            <c:if test="${item.HouseInfo.HsCtInfo.HsCtChooseInfo.chooseStatus=='2'}">
                <c:set var="tr_class" value="${varStatus.index % 2 == 0 ? 'odd' : 'even'}"/>
                <tr class="js_yiXFang">
                    <input type="hidden" name="hsCtChooseId"
                           value="${item.HouseInfo.HsCtInfo.HsCtChooseInfo.hsCtChooseId}">
                    <td>${item.HouseInfo.HsCtInfo.HsCtChooseInfo.chooseHsAddr}</td>
                    <td>${item.HouseInfo.HsCtInfo.HsCtChooseInfo.buyPersonName}</td>
                    <td>${item.HouseInfo.HsCtInfo.HsCtChooseInfo.buyPersonOwnerRel}</td>
                    <td>${item.HouseInfo.HsCtInfo.HsCtChooseInfo.buyPersonCerty}</td>
                    <td>${item.HouseInfo.HsCtInfo.HsCtChooseInfo.buyPersonTel}</td>
                </tr>
            </c:if>
        </c:forEach>
    </table>
</div>
<div id="ch001_ReportData" class="mart5" style="overflow:auto;">
    <table width="100%">
        <tr>
            <td width="49%">
                <div class="panel js_ch00103_file">
                    <h1>选房附件
                        <span class="panel_menu" docTypeName="选房确认单" relType="100"
                              onclick="ch00103.showDoc(this,'${hsId}')">
                            <label style="cursor:pointer;">上传</label>
                            <input type="hidden" name="docIds" value='${signDocIds}'>
                        </span>
                        <span class="panel_menu" onclick="ch00103.showFjFile(this)">
                            <label style="cursor:pointer;">预览</label>
                        </span>
                    </h1>

                    <div class="js_panel_context album-list" style="height: 90%">
                        <ul class="album-ul js_hidden_file_ul">
                            <li class="album-li js_hidden_file_li" style="text-align: center;display: none"
                                onclick="ch00103.viewPic(${item.Row.docId})">
                                <div class="album-context">
                                    <img class="show" style="max-width: 120px;" width="120px" height="130px"
                                         title="${item.Row.docName}"
                                         src="${pageContext.request.contextPath}/oframe/common/file/file-download.gv?docId=${item.Row.docId}"/>
                                    <input type="hidden" name="ct00104_docId" class="js_docIds"
                                           value="${item.Row.docId}"/>
                                </div>
                                <span class="js_span_docName spanText">${item.Row.docName}</span>
                            </li>
                            <c:forEach items="${hsDocs}" var="item">
                                <c:if test="${item.Row.docTypeName == '选房确认单'}">
                                    <li class="album-li" style="text-align: center;"
                                        onclick="ch00103.viewPic(${item.Row.docId})">
                                        <div class="album-context">
                                            <img class="show" style="max-width: 120px;" width="120px" height="130px"
                                                 title="${item.Row.docName}"
                                                 src="${pageContext.request.contextPath}/oframe/common/file/file-downview.gv?docId=${item.Row.docId}"/>
                                            <input type="hidden" name="ct00104_docId" class="js_docIds"
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

<script type="text/javascript">
    var ch00103 = {
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
                    ch00103.closedDoc();
                };
            });
        },
        //浏览所有附件的方法
        showFjFile: function (obj) {
            var $this = $(obj);
            var docIdTemp = $this.closest("div.js_ch00103_file").find("input[name=docIds]").val();
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
                    ch00103.closedDoc();
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
            file.getDocTypeRelUrl = ch00103.getDocTypeRelUrl;
            var url = getGlobalPathRoot() + "oframe/common/file/file-uploadFile.gv?prjCd=" + getPrjCd() + "&docTypeName=" + docTypeName +
                    "&docIds=" + docIds + "&editAble=" + editAble
                    + "&relType=100&relId=${hsId}";
            $.pdialog.open(url, "file", "选房确认单上传", {
                height: 600, width: 800, mask: true,
                close: editAble ? ch00103.closedDoc : "",
                param: {clickSpan: $span}
            });
        },

        closedDoc: function (param) {
            //关闭前刷新本页
            return file.closeD();
            ch001.chInfo(${hsId});
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
                close: ch00103.docClosed,
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
            var currentDiv = $span.closest("div.js_ch00103_file");
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
            if (docIdArr.length > 0) {
                var label = $span.find("label");
                if (label.html().startWith("上传")) {
                    label.html(label.html().replace("上传", "查看"));
                }
            } else {
                var label = $span.find("label");
                if (label.html().startWith("查看")) {
                    label.html(label.html().replace("查看", "上传"));
                }
            }

            ch001.chInfo(${hsId});
            // 调用文件的关闭
            return file.closeD();
        }
    }
    // 清除历史定时器
    clearTimeout(index.ch001Countdown);
</script>