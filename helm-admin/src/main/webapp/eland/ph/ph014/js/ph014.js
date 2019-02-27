/**
 * 居民交房 处理
 */
var ph014 = {
    printForm: function (printAreaClass) {
        var printDiv = $("." + printAreaClass + ":eq(0)", navTab.getCurrentPanel());
        if (printDiv.length > 0) {
            printDiv.printArea({extraCss: getGlobalPathRoot() + "/oframe/themes/common/form.print.css"});
        }
    },
    /**
     * 查看居民详情
     * @param hsId
     */
    viewHouse: function (hsId) {
        var url = "eland/ph/ph003/ph003-init.gv?hsId=" + hsId;
        index.quickOpen(url, {opCode: "ph003-init", opName: "居民详情", fresh: true});
    },

    /**
     * 初始化第一个，在右侧没有内容的时候
     */
    initFirst: function () {
        var container = $("#ph014Context", navTab.getCurrentPanel());
        var fromEdit = container.find(">div:eq(0)").attr("fromEdit");
        var searchContainer = $("#ph014SchDiv", navTab.getCurrentPanel());
        var searchResult = searchContainer.find("tr.js_hs_item");
        var schType = $("select[name=schType]", searchContainer).val();
        var addBtn = $("span.js_add", searchContainer);
        // 使用二维码扫码的时候直接进入展示结果
        if (searchResult.length > 0) {
            if (container.children().length == 0 || schType.indexOf("UID.") == 0) {
                $(searchResult[0]).trigger("click");
            } else if (fromEdit != "true" && addBtn.length == 0) {
                // 非新增操作都允许直接定位第一个
                $(searchResult[0]).trigger("click");
            }
        } else {
            alertMsg.warn("没有找到满足条件的数据！")
        }
    },
    /**
     * 检索房产
     */
    schHs: function () {
        // 查询数据
        var ph014QueryForm = $("#ph014QueryForm", navTab.getCurrentPanel());
        // 查询处理结果
        Page.query(ph014QueryForm, "ph014.initFirst();");
    },
    /**
     * 快速查询按钮
     */
    qSchHs: function () {
        var container = $("#ph014SchDiv", navTab.getCurrentPanel());
        var formObj = $('#ph014QueryForm', container);
        var conditionName = $("input[name=conditionName]", formObj).val().split(",");
        var condition = $("input[name=condition]", formObj).val().split(",");
        var conditionValue = $("input[name=conditionValue]", formObj).val().split(",");
        // 查询条件
        $("select[name=schType]", container).find("option").each(function () {
            var $this = $(this);
            var attrName = $this.val();
            var attrValue = "";
            if ($this.attr("selected")) {
                attrValue = $("input[name=schValue]", container).val();
            }
            if (!attrName || attrName == "") {
                return true;
            }
            var nameIdx = -1;
            for (var i = 0; i < conditionName.length; i++) {
                var temp = conditionName[i];
                if (temp == attrName) {
                    nameIdx = i;
                    break;
                }
            }
            if (nameIdx != -1) {
                if ($.trim(attrValue) == "") {
                    conditionName.splice(nameIdx, 1);
                    condition.splice(nameIdx, 1);
                    conditionValue.splice(nameIdx, 1);
                } else {
                    if (attrName.indexOf("UID.") == 0) {
                        condition[nameIdx] = "=";
                    } else {
                        condition[nameIdx] = "like";
                    }

                    conditionValue[nameIdx] = attrValue;
                }
            } else if ($.trim(attrValue) != "") {
                conditionName.push(attrName);
                if (attrName.indexOf("UID.") == 0) {
                    condition.push("=");
                } else {
                    condition.push("like");
                }
                conditionValue.push(attrValue);
            }
        });


        $("input[name=conditionName]", formObj).val(conditionName.join(","));
        $("input[name=condition]", formObj).val(condition.join(","));
        $("input[name=conditionValue]", formObj).val(conditionValue.join(","));
        // 查询数据
        ph014.schHs();
    },

    /**
     * 调用通用查询查询房源数据
     */
    queryHs: function (obj) {
        Page.queryCondition(obj, {
            changeConditions: ph014.changeConditions,
            showContainer: $("#ph014AdanceSch", navTab.getCurrentPanel()),
            formObj: $("#ph014QueryForm", navTab.getCurrentPanel()),
            callback: function (showContainer) {
                showContainer.find("div.tip").prepend($('<div class="triangle triangle-left" style="top: 45px; left: -30px;"></div>'));
                showContainer.find("a.js-more").trigger("click");
            }
        });
    },

    /**
     * 改变查询条件
     * @param newCondition
     */
    changeConditions: function (newCondition) {
        var formObj = $('#ph014QueryForm', navTab.getCurrentPanel());
        $("input[name=conditionName]", formObj).val(newCondition.conditionNames);
        $("input[name=condition]", formObj).val(newCondition.conditions);
        $("input[name=conditionValue]", formObj).val(newCondition.conditionValues);
        $("input.js_conditionValue", formObj).val(newCondition.conditionValueTexts);
        $("input[name=resultField]", formObj).val(newCondition.resultFields);
        // 排序字段
        $("input[name=sortColumn]", formObj).val(newCondition.sortColumn);
        $("input[name=sortOrder]", formObj).val(newCondition.sortOrder);

        // 查询数据
        ph014.schHs();
    },

    /**
     * 重新加载信息内容
     */
    reloadInfo: function () {
        var pageDiv = $("div.js_page_data", navTab.getCurrentPanel());
        var hsId = $("input[name=hsId]", pageDiv).val();
        var infoKey = $("input[name=infoKey]", pageDiv).val();
        ph014.info(hsId, infoKey);
    },

    /**
     * 查看居民文档信息
     * @param hsId 居民信息编号
     * @param infoKey 子实体主键
     */
    info: function (hsId, infoKey, jsonData) {
        // 重新加载
        var packet = new AJAXPacket();
        var prjCd = getPrjCd();
        packet.data.add("prjCd", prjCd);
        packet.data.add("hsId", hsId);
        packet.data.add("infoKey", infoKey);
        packet.data.add("formCd", $("input[name=formCd]", navTab.getCurrentPanel()).val());
        packet.data.add("withFrame", $("input[name=withFrame]", navTab.getCurrentPanel()).val());
        packet.url = getGlobalPathRoot() + "eland/ph/ph014/ph014-info.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            $("#ph014Context", navTab.getCurrentPanel()).html(response);
            initUI($("#ph014Context", navTab.getCurrentPanel()));
        });
    },

    /**
     * 保存交房信息
     */
    saveCommonForm: function (obj) {
        var pageDiv = form.getCurrentRpt();
        var hsId = $("input[name=hsId]", pageDiv).val();
        var $form = $("form.js_dynamic_form", pageDiv);
        if ($form.valid()) {
            //处理时间类型 属性
            ph014.syncDateAttr();
            var url = getGlobalPathRoot() + "eland/ph/ph014/ph014-saveCommonForm.gv?prjCd=" + getPrjCd();
            var ajaxPacket = new AJAXPacket(url);
            ajaxPacket.data.data = $form.serializeArray();
            ajaxPacket.data.data.push({name: "hsId", value: hsId});
            ajaxPacket.data.data.push({name: "formCd", value: $("input[name=formCd]", pageDiv).val()});
            ajaxPacket.data.data.push({name: "infoKey", value: $("input[name=infoKey]", pageDiv).val()});
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var jsonData = eval("(" + response + ")");
                if (jsonData.success) {
                    alertMsg.correct("保存成功！");
                    ph014.info(hsId);
                } else {
                    alertMsg.error(jsonData.errMsg);
                }
            }, true);
        }
    },

    /**
     * 保存交房信息
     */
    saveHandle: function (hsId) {
        var $form = $("#ph014Form", navTab.getCurrentPanel());
        if ($form.valid()) {
            var url = getGlobalPathRoot() + "eland/ph/ph014/ph014-saveForm.gv?prjCd=" + getPrjCd();
            var ajaxPacket = new AJAXPacket(url);
            ajaxPacket.data.data = $form.serializeArray();
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var jsonData = eval("(" + response + ")");
                if (jsonData.success) {
                    alertMsg.correct("保存成功！");
                    ph014.info(hsId);
                } else {
                    alertMsg.error(jsonData.errMsg);
                }
            }, true);
        }
    },

    /**
     * 保存领款
     */
    saveLk: function (hsId, infoKey) {
        var $form = $("#ph014Form", navTab.getCurrentPanel());
        if ($form.valid()) {
            var url = getGlobalPathRoot() + "eland/ph/ph014/ph014-saveLkForm.gv?prjCd=" + getPrjCd();
            var ajaxPacket = new AJAXPacket(url);
            ajaxPacket.data.data = $form.serializeArray();
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var jsonData = eval("(" + response + ")");
                if (jsonData.success) {
                    alertMsg.correct("保存成功！");
                    ph014.info(hsId, infoKey);
                } else {
                    alertMsg.error(jsonData.errMsg);
                }
            }, true);
        }
    },

    /**
     * 查看 附件
     * @param docIds
     */
    viewPic: function (docIds) {
        var url = getGlobalPathRoot() + "oframe/common/file/file-view.gv";
        var ajaxPacket = new AJAXPacket(url);
        ajaxPacket.data.add("docIds", docIds);
        ajaxPacket.data.add("prjCd", getPrjCd());
        core.ajax.sendPacketHtml(ajaxPacket, function (response) {
            navTab.getCurrentPanel().append($(response));
            FileViewer.closeFunc = function () {
                // 触发加载
                ph014.closedDoc();
            };
        });
    },

    closedDoc: function () {
        var hsId = $("input[name=hsId]", navTab.getCurrentPanel()).val();
        ph014.flushDocs(hsId);
        // 调用文件的关闭
        return file.closeD();
    },

    /**
     * 显示关联文档信息
     * @param clickObj 点击对象
     * @param relId 传入hsId
     */
    showDoc: function (clickObj, relId) {
        if (!relId) {
            var pageDiv = $("div.js_page_data", navTab.getCurrentPanel());
            relId = $("input[name=hsId]", pageDiv).val();
        }
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
            close: ph014.docClosed,
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
        var docIdArr = [];
        for (var i = 0; i < uploadedFiles.length; i++) {
            //保存已经上传的docId
            var uploadedFile = uploadedFiles[i];
            docIdArr.push(uploadedFile.docId);
        }
        $span.find("input[type=hidden]").val(docIdArr.join(","));
        var hsId = $("input[name=hsId]", navTab.getCurrentPanel()).val();
        ph014.flushDocs(hsId);
        // 调用文件的关闭
        return file.closeD();
    },

    /**
     * 刷新该户的 文档
     */
    flushDocs: function () {
        var container = $("#commonForm", navTab.getCurrentPanel());
        $("span.js_upload", container).each(function () {
            ph014.syncDocInfo($(this));
        });
    },

    /**
     * 根据指定上传按钮同步上传信息
     */
    syncDocInfo: function (uploadContainer) {
        // 获取上传按钮上的附件关联信息
        var docShowDiv = uploadContainer.attr("docShowDiv");
        var relId = uploadContainer.attr("relId");
        var relType = uploadContainer.attr("relType");
        var docTypeName = uploadContainer.attr("docTypeName");
        // 验证附件关联信息,为空则不进行查询
        if (!docShowDiv || !relId || !relType || !docTypeName
            || docShowDiv == "" || relType == "" || docTypeName == "") {
            return;
        }

        var url = getGlobalPathRoot() + "oframe/common/form/form-initDocs.gv";
        // 获取上传按钮的信息同步显示上传下载的附件信息
        var ajaxPacket = new AJAXPacket(url);
        ajaxPacket.data.add("relType", relType);
        ajaxPacket.data.add("relId", relId);
        ajaxPacket.data.add("docTypeName", docTypeName);
        ajaxPacket.data.add("prjCd", getPrjCd());
        core.ajax.sendPacketHtml(ajaxPacket, function (response) {
            var containDiv = $("#" + docShowDiv, navTab.getCurrentPanel());
            containDiv.html(response);
            initUI(containDiv);
        });
    },

    /**
     * 生成下载 模板
     */
    downTemp: function () {
        var formCd = $("input[name=formCd]", navTab.getCurrentPanel()).val();
        // 获取居民信息容器
        var container = $("#ph014Context", navTab.getCurrentPanel());
        var hsId = $("input[name=hsId]", container).val();
        var url = getGlobalPathRoot() + "eland/ph/ph014/ph014-downTemp.gv?fromOp=download&hsId=" + hsId
            + "&formCd=" + formCd
            + "&prjCd=" + getPrjCd();
        window.open(url);
    },

    /**
     * 上一户下一户
     */
    singleQuery: function (hsId, flag) {
        var packet = new AJAXPacket();
        if (!hsId) {
            hsId = form.getCurrentRpt().find("input[name=hsId]").val();
        }
        var ph014QueryForm = $("#ph014QueryForm", navTab.getCurrentPanel());
        var jsonData = ph014QueryForm.serializeArray();
        jsonData.push({name: "hsId", value: hsId});
        jsonData.push({name: "flag", value: flag});
        jsonData.push({name: "prjCd", value: getPrjCd()});
        // 获取排序条件
        // 赋值新的排序及样式
        var dataContainer = ph014QueryForm.next("div.js_page");
        var sortOrder = dataContainer.find("input[name=sortOrder]").val();
        var sortColumn = dataContainer.find("input[name=sortColumn]").val();
        jsonData.push({name: "rSortOrder", value: sortOrder});
        jsonData.push({name: "rSortColumn", value: sortColumn});
        packet.data.data = jsonData;
        // 查询上一个、下一个 交房
        packet.url = getGlobalPathRoot() + "eland/ph/ph014/ph014-singleQuery.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            var data = eval("(" + response + ")");
            var hsId = data.hsId;
            if (hsId) {
                form.reloadData(data.hsId, {infoKey: data.infoKey});
            } else {
                if (flag == 'last') {
                    alertMsg.warn("已经是第一个人");
                } else if (flag == 'next') {
                    alertMsg.warn("已经是最后一个人");
                }
            }
        });
    },

    /**
     * 同步格式化 日期类型属性
     */
    syncDateAttr: function () {
        $(":input", navTab.getCurrentPanel()).each(function () {
            var thisObj = $(this);
            if (thisObj.hasClass("date")) {
                var fmtData = thisObj.attr("formatedata");
                if (fmtData && fmtData != '' && thisObj.val()) {
                    //TODO: 格式化日期类型的属性
                    var d = new Date(thisObj.val());
                    var parse = d.formatDate("yyyyMMdd");
                    thisObj.val(parse);
                }
            }
        });
    },

    /**
     * 设置检索类型
     * @param obj
     */
    cookieSchType: function (obj) {
        setPCookie("ph014-schType", $(obj).val(), "/");
    }
};

$(document).ready(function () {
    LeftMenu.init("ph014SchDiv", "ph014Right", {cookieName: "ph014-" + navTab.getCurrentTabId()});
});