/**
 * 基于状态的审批处理
 * @type {{openApplyDialog: FormChecker.openApplyDialog, openCheckDialog: FormChecker.openCheckDialog, openLogDialog: FormChecker.openLogDialog, submitCheck: FormChecker.submitCheck, doSubmit: FormChecker.doSubmit}}
 */
var FormChecker = {
    /**
     * 提交申请，撤销申请界面(无法选择审批结果)
     * @param checkRelType 审批关联类型
     * @param checkRelId 审批关联业务编号, 如果需要增加二分类请使用特殊副“|"分隔, 比如: 1020|2
     * @param entityProperty 审批修改的业务属性
     * @param successStatus 审批成功后业务属性修改后的状态
     * @param checkOpName 业务操作名称
     * @param submitFunc 提交处理函数,不提供为默认值FormChecker.doSubmit(checkData)
     *        返回true：成功关闭对话框,false: 失败不关闭对话框, 函数接收参数checkJson
     */
    openApplyDialog: function (checkRelType, checkRelId, entityProperty, successStatus, checkOpName, submitFunc) {
        var url = getGlobalPathRoot() + "oframe/common/form/form-checkApply.gv?prjCd=" + getPrjCd()
            + "&checkRelType=" + checkRelType
            + "&checkRelId=" + checkRelId
            + "&entityProperty=" + entityProperty
            + "&successStatus=" + successStatus
            + "&checkOpName=" + encodeURI(encodeURI(checkOpName))
            + "&submitFunc=" + submitFunc;
        $.pdialog.open(url, "form-checkApply", checkOpName, {
            height: 600, width: 800, mask: true
        });
    },

    /**
     * 打开处理审批验证框(需要填写审批结果)
     * @param checkRelType 审批关联类型
     * @param checkRelId 审批关联业务编号,如果需要增加二分类请使用特殊副“|"分隔, 比如: 1020|2
     * @param entityProperty 审批修改的业务属性
     * @param successStatus 审批成功后业务属性修改后的状态
     * @param backStatus 审批失败后业务属性修改后的状态值
     * @param checkOpName 业务操作名称
     * @param submitFunc 提交处理函数,不提供为默认值FormChecker.doSubmit(checkData)
     *        返回true：成功关闭对话框,false: 失败不关闭对话框, 函数接收参数checkJson
     */
    openCheckDialog: function (checkRelType, checkRelId, entityProperty,
                               successStatus, backStatus, checkOpName, submitFunc) {
        var url = getGlobalPathRoot() + "oframe/common/form/form-checkDone.gv?prjCd=" + getPrjCd()
            + "&checkRelType=" + checkRelType
            + "&checkRelId=" + checkRelId
            + "&entityProperty=" + entityProperty
            + "&successStatus=" + successStatus
            + "&backStatus=" + backStatus
            + "&checkOpName=" + encodeURI(encodeURI(checkOpName))
            + "&submitFunc=" + submitFunc
        ;
        $.pdialog.open(url, "form-checkApply", checkOpName, {
            height: 600, width: 800, mask: true
        });
    },

    /**
     * 审批处理记录
     * @param checkRelType 审批关联类型
     * @param checkRelId 审批关联业务编号,如果需要增加二分类请使用特殊副“|"分隔, 比如: 1020|2
     */
    openLogDialog: function (checkRelType, checkRelId) {
        if (checkRelType == "" || checkRelId == "") {
            return;
        }
        var url = getGlobalPathRoot() + "oframe/common/form/form-checkLog.gv?prjCd=" + getPrjCd()
            + "&checkRelType=" + checkRelType
            + "&checkRelId=" + checkRelId;
        $.pdialog.open(url, "form-checkLog", "审批记录", {
            height: 600, width: 800, mask: true
        });
    },

    /**
     * 提交审批意见
     */
    submitCheck: function () {
        var $form = $("#form_check_form", $.pdialog.getCurrent());
        // 控制信息
        var submitFunc = $("input[name=submitFunc]", $.pdialog.getCurrent()).val();
        // 审核信息
        var checkRelId = $("input[name=checkRelId]", $form).val();
        var subRelId = $("input[name=subRelId]", $form).val();
        var checkRelType = $("input[name=checkRelType]", $form).val();
        var checkOpName = $("input[name=checkOpName]", $form).val();
        var checkResult = $("input[name=checkResult]:checked", $form).val();
        if (!checkResult) {
            checkResult = $("input[name=checkResult]", $form).val();
        }
        var checkNote = $("textarea[name=checkNote]", $form).val();
        var entityProperty = $("input[name=entityProperty]", $form).val();
        var successStatus = $("input[name=successStatus]", $form).val();
        var backStatus = $("input[name=backStatus]", $form).val();

        // 验证是否为审批通过
        if (checkResult == "2" && $.trim(checkNote) == "") {
            alertMsg.warn("请填写审批不通过的原因!");
            return;
        }
        // 验证表单,提交修改的信息
        if ($form.valid()) {
            // 由业务数据负责提交,这里不负责提交审核填写的数据
            var checkData = {};
            checkData.checkRelType = checkRelType;
            checkData.checkRelId = checkRelId;
            checkData.subRelId = subRelId;
            checkData.checkOpName = checkOpName;
            checkData.checkResult = checkResult ? checkResult : "";
            checkData.entityProperty = entityProperty;
            checkData.successStatus = successStatus;
            checkData.backStatus = backStatus ? backStatus : "";
            checkData.checkNote = checkNote;
            // 回调审批处理界面
            var result = true;
            if (submitFunc && submitFunc != "") {
                var fn = new Function("checkData", "return " + submitFunc + "(checkData);");
                result = fn(checkData);
            } else {
                result = FormChecker.doSubmit(checkData);
            }
            // 关闭审批对话框
            if (result) {
                $.pdialog.closeCurrent();
            }
        }
    },

    /**
     * 默认提交审核处理函数
     * @param checkData 审核数据
     */
    doSubmit: function (checkData) {
        // 执行业务数据的更新
        var url = getGlobalPathRoot() + "oframe/common/form/form-saveCheck.gv";
        var ajaxPacket = new AJAXPacket(url);
        ajaxPacket.data.add("prjCd", getPrjCd());
        ajaxPacket.data.add("checkDataJson", JSON.stringify(checkData));
        var result = true;
        core.ajax.sendPacketHtml(ajaxPacket, function (response) {
            var jsonData = eval("(" + response + ")");
            if (jsonData.success) {
                result = true;
            } else {
                alertMsg.error(jsonData.errMsg);
                result = false;
            }
        }, false);
        return result;
    }
};

/**
 * 表单数据加载保存处理组件
 * @type {{getCurrentRpt: form.getCurrentRpt, info: form.info, reloadData: form.reloadData, saveCommonForm: form.saveCommonForm, saveProperty: form.saveProperty, showDoc: form.showDoc, uploadDoc: form.uploadDoc, docClosed: form.docClosed, syncDocInfo: form.syncDocInfo, viewPic: form.viewPic, syncDateAttr: form.syncDateAttr, importMb: form.importMb, allExport: form.allExport}}
 */
var form = {

    /**
     *获取当前的最底层表单
     * @return {*|jQuery}
     */
    getCurrentRpt: function () {
        return $("div.js_page_data:last", navTab.getCurrentPanel());
    },

    /**
     * 加载报表信息
     * @param hsId 居民信息编号
     * @param jsonData {infoKey:"子实体主键"}
     */
    info: function (hsId, jsonData) {
        // 重新加载
        var packet = new AJAXPacket();
        if (!jsonData) {
            jsonData = {};
        }
        jsonData.prjCd = getPrjCd();
        jsonData.hsId = hsId;
        jsonData.formCd = $("input[name=formCd]", navTab.getCurrentPanel()).val();
        jsonData.withFrame = $("input[name=withFrame]", navTab.getCurrentPanel()).val();
        packet.data.data = jsonData;
        packet.url = getGlobalPathRoot() + "eland/ph/ph014/ph014-info.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            var container = $("div.form_rpt_data:eq(0)", navTab.getCurrentPanel());
            container.html(response);
            initUI(container);
            // 处理选中效果
            var searchContainer = $("#ph014SchDiv", navTab.getCurrentPanel());
            var hsTr = searchContainer.find("tr.js_hs_" + hsId);
            if (hsTr.length > 0) {
                hsTr.siblings("tr").removeClass("selected");
                hsTr.addClass("selected");
            } else {
                searchContainer.find("tr.js_hs_item").removeClass("selected");
            }
        }, false);
    },

    /**
     * 重新加载报表信息
     * @param hsId 居民信息编号
     * @param infoKey 子实体主键
     */
    reloadData: function (hsId, jsonData) {
        // 重新加载
        var packet = new AJAXPacket();
        if (!jsonData) {
            jsonData = {};
        }
        var formCdObj = form.getCurrentRpt().find("input[name=formCd]");
        var container = $("div.form_sub_rpt_data:eq(0)", navTab.getCurrentPanel());
        if (container.length == 0) {
            container = $("div.form_rpt_data:eq(0)", navTab.getCurrentPanel());
            jsonData.withFrame = $("input[name=withFrame]", navTab.getCurrentPanel()).val();
            formCdObj = container.find("input[name=formCd]");
        }
        if (formCdObj.length == 0) {
            formCdObj = $("input[name=formCd]", navTab.getCurrentPanel());
        }
        jsonData.prjCd = getPrjCd();
        jsonData.hsId = hsId;
        jsonData.formCd = formCdObj.val();
        packet.data.data = jsonData;
        packet.url = getGlobalPathRoot() + "eland/ph/ph014/ph014-info.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            container.html(response);
            initUI(container);
            // 处理选中效果
            var searchContainer = $("#ph014SchDiv", navTab.getCurrentPanel());
            var hsTr = searchContainer.find("tr.js_hs_" + hsId);
            if (hsTr.length > 0) {
                hsTr.siblings("tr").removeClass("selected");
                hsTr.addClass("selected");
            } else {
                searchContainer.find("tr.js_hs_item").removeClass("selected");
            }
        });
    },

    /**
     * 表单通用保存处理逻辑
     * @param successCallBack: 调用成功后的执行的函数,执行接收参数(response, $from)
     * @param checkData: JSON结构的审批信息
     */
    saveCommonForm: function (successCallBack, checkData) {
        var $form = $("form.js_dynamic_form", navTab.getCurrentPanel());
        if ($form.valid()) {
            var url = getGlobalPathRoot() + "oframe/common/form/form-saveCommonForm.gv?prjCd=" + getPrjCd();
            var ajaxPacket = new AJAXPacket(url);
            var formData = $form.serializeArray();
            if (checkData) {
                formData.push({"name": "checkDataJson", "value": JSON.stringify(checkData)});
            }
            formData.push({"name": "prjCd", "value": getPrjCd()});
            ajaxPacket.data.data = formData;
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var jsonData = eval("(" + response + ")");
                if (jsonData.success) {
                    alertMsg.correct("保存成功！");
                    if (successCallBack && $.isFunction(successCallBack)) {
                        successCallBack(response, $form);
                    }
                } else {
                    alertMsg.error(jsonData.errMsg);
                }
            }, true);
        }
    },

    /**
     * 表单通用保存指定属性
     * @param successCallBack: 调用成功后的执行的函数,执行接收参数(response, $from)
     */
    saveProperty: function (entityName, propertyName, successCallBack) {
        var $form = $("form.js_dynamic_form", navTab.getCurrentPanel());
        if ($form.valid()) {
            var url = getGlobalPathRoot() + "oframe/common/form/form-saveProperty.gv?prjCd=" + getPrjCd();
            var ajaxPacket = new AJAXPacket(url);
            var formData = $form.serializeArray();
            formData.push({"name": "prjCd", "value": getPrjCd()});
            formData.push({"name": "entityName", "value": entityName});
            formData.push({"name": "propertyName", "value": propertyName});
            ajaxPacket.data.data = formData;
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var jsonData = eval("(" + response + ")");
                if (jsonData.success) {
                    alertMsg.correct("保存成功！");
                    if (successCallBack) {
                        eval(successCallBack);
                    }
                } else {
                    alertMsg.error(jsonData.errMsg);
                }
            }, true);
        }
    },

    /** 表单中附件上传逻辑处理 **/
    /**
     * 显示关联文档信息
     * @param clickObj 点击对象
     */
    showDoc: function (clickObj) {
        var $span = $(clickObj);
        var docIds = $span.find("input[type=hidden]").val();
        var relId = $span.attr("relId");
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
        var url = getGlobalPathRoot() + "oframe/common/file/file-init.gv?";
        url = url + "prjCd=" + getPrjCd() + "&docTypeName=" + docTypeName
            + "&docIds=" + docIds + "&editAble=" + editAble
            + "&relType=" + relType + "&relId=" + relId;
        $.pdialog.open(url, "file", "附件管理", {
            height: 600, width: 800, mask: true,
            close: form.docClosed,
            param: {clickSpan: $span}
        });
    },

    /**
     * 显示关联文档信息
     * @param clickObj 点击对象
     */
    uploadDoc: function (clickObj) {
        var $span = $(clickObj);
        var docIds = $span.find("input[type=hidden]").val();
        var relId = $span.attr("relId");
        var docTypeName = $span.attr("docTypeName");
        if (!docTypeName) {
            docTypeName = "";
        }
        var typeItemCd = $span.attr("typeItemCd");
        if (!typeItemCd) {
            typeItemCd = "";
        }
        docTypeName = encodeURI(encodeURI(docTypeName));
        var relType = $span.attr("relType");
        var editAble = $span.attr("editAble");
        if (!editAble) {
            editAble = true;
        }
        var url = getGlobalPathRoot() + "oframe/common/file/file-uploadFile.gv?";
        url = url + "prjCd=" + getPrjCd() + "&docTypeName=" + docTypeName
            + "&docIds=" + docIds + "&editAble=" + editAble
            + "&relType=" + relType + "&relId=" + relId
            + "&typeItemCd=" + typeItemCd;
        $.pdialog.open(url, "file", "附件管理", {
            height: 600, width: 800, mask: true,
            close: form.docClosed,
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
        form.syncDocInfo($span);
        // 调用文件的关闭
        return file.closeD();
    },

    /**
     * 根据指定上传按钮同步上传信息
     */
    syncDocInfo: function (clickObj) {
        var container = form.getCurrentRpt();
        if (!clickObj) {
            clickObj = $("span.js_upload", container);
        }
        clickObj.each(function () {
            var uploadContainer = $(this);
            // 获取上传按钮上的附件关联信息
            var docShowDiv = uploadContainer.attr("docShowDiv");
            var relId = uploadContainer.attr("relId");
            if (!relId) {
                relId = $("input[name=hsId]", container).val();
            }
            var relType = uploadContainer.attr("relType");
            var docTypeName = uploadContainer.attr("docTypeName");
            // 验证附件关联信息,为空则不进行查询
            if (!docShowDiv || !relId || !relType
                || docShowDiv == "" || relType == "") {
                return;
            }
            // 获取上传按钮的信息同步显示上传下载的附件信息
            var url = getGlobalPathRoot() + "oframe/common/form/form-initDocs.gv";
            var ajaxPacket = new AJAXPacket(url);
            ajaxPacket.noCover = true;
            ajaxPacket.data.add("relType", relType);
            ajaxPacket.data.add("relId", relId);
            ajaxPacket.data.add("docTypeName", docTypeName);
            ajaxPacket.data.add("prjCd", getPrjCd());
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var containDiv = $("#" + docShowDiv, navTab.getCurrentPanel());
                containDiv.html(response);
                initUI(containDiv);
            });
        });
    },

    /**
     * 查看 附件
     * @param docIds 查看的附件编号
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
                form.syncDocInfo();
            };
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
     * 打开下载导入模板页面
     */
    importMb: function (relFormCd) {
        var formCd = $("input[name=formCd]", navTab.getCurrentPanel()).val();
        if (relFormCd) {
            formCd = relFormCd;
        }
        var url = getGlobalPathRoot() + "eland/ph/ph001/ph001-openImport.gv?prjCd=" + getPrjCd() + "&formCd=" + formCd;
        $.pdialog.open(url, "allImport", "信息导入", {
            mask: true,
            max: false,
            maxable: false,
            resizable: false,
            width: 600,
            height: 180
        });
    },

    /**
     * 全量导出方案
     */
    allExport: function (relFormCd) {
        var formCd = $("input[name=formCd]", navTab.getCurrentPanel()).val();
        if (relFormCd) {
            formCd = relFormCd;
        }
        var form = $('#ph014QueryForm', navTab.getCurrentPanel());
        if (form.length == 0) {
            form = $('#ph000QueryForm', navTab.getCurrentPanel());
        }
        var conditionNames = $("input[name=conditionName]", form).val();
        var conditions = $("input[name=condition]", form).val();
        var conditionValues = $("input[name=conditionValue]", form).val();
        var sortColumns = $("input[name=sortColumn]", form).val();
        var sortOrders = $("input[name=sortOrder]", form).val();
        // 其他查询条件
        var url = getGlobalPathRoot() + "eland/ph/ph001/ph001-allExport.gv?prjCd=" + getPrjCd()
            + "&conditionNames=" + conditionNames
            + "&conditions=" + conditions + "&conditionValues=" + conditionValues + "&sortColumns=" + sortColumns
            + "&sortOrders=" + sortOrders + "&formCd=" + formCd;
        // 执行导出功能
        window.open(url);
    }
};

var STRING_CONSTRUCTOR = "test".constructor;
var ARRAY_CONSTRUCTOR = [].constructor;
var OBJECT_CONSTRUCTOR = {}.constructor;
/**
 * 人地房信息表单信息转为Json和Json信息填充表单
 * @type {{fillValue: FormHelp.fillValue}}
 */
var FormHelp = {

    /**
     * 获取填充值
     * @param targetContainer
     * @param prePath
     */
    getFillValue: function (targetContainer, prePath) {
        var result = {};
        var prePathLength = 0;
        var selector = ":input";
        if (prePath != "") {
            prePath = prePath + "_";
            prePathLength = prePath.length;
            selector = selector + "[name^=" + prePath + "]";
        }
        $(selector, targetContainer).each(function () {
            var $this = $(this);
            var elementName = $this.attr("name");
            var tagName = $this.prop("tagName");
            if (elementName) {
                var name = elementName.substr(prePathLength, elementName.length);
                if (name.indexOf("_") == -1) {
                    if ("INPUT" == tagName) {
                        var elementType = $this.attr("type");
                        // 普通文本输入款
                        if (elementType == "text" || elementType == "hidden") {
                            var value = $this.val();
                            result[name] = value;
                        } else if (elementType == "radio") {
                            // 选择款只处理一次
                            if (!result[name]) {
                                var value = $("input[name=" + elementName + "]:checked", targetContainer).val();
                                // 是否存在选中的值,如果没有选中的值
                                if (value) {
                                    result[name] = value;
                                } else {
                                    result[name] = "";
                                }
                            }
                        } else if (elementType == "checkbox") {
                            // 选择框只处理一次
                            if (!result[name]) {
                                var checkedTarget = $("input[name=" + elementName + "]:checked", targetContainer);
                                // 是否存在选中的值,如果没有选中的值
                                var tempArr = [];
                                for (var i = 0; i < checkedTarget.length; i++) {
                                    tempArr.push($(checkedTarget[i]).val());
                                }
                                result[name] = tempArr.join(",");
                            }
                        }
                    } else if ("SELECT" == tagName) {
                        var value = $this.val();
                        result[name] = value;
                    } else if ("TEXTAREA" == tagName) {
                        var value = $this.val();
                        result[name] = value;
                    }
                }
            }
        });
        return result;
    }
    ,

    /**
     * 填充内容到
     * @param targetContainer
     * @param fillData
     * @param prePath
     */
    fillValue: function (targetContainer, fillData, prePath) {
        if (!fillData) {
            return;
        }
        // 人地房基本信息
        $.each(fillData, function (name, value) {
            var itemName = prePath + "_" + name;
            if (value.constructor == STRING_CONSTRUCTOR) {
                FormHelp.fillItem(targetContainer, itemName, value);
            } else if (value.constructor == ARRAY_CONSTRUCTOR) {
                //
            } else if (value.constructor == OBJECT_CONSTRUCTOR) {
                FormHelp.fillValue(targetContainer, value, itemName);
            }
        });
    }
    ,
    /**
     * 填充值信息,统一对textInput, textarea, select, radio, text
     * @param targetContainer 填充文本款查找范围
     * @param itemName 文本款名称
     * @param itemValue json值
     */
    fillItem: function (targetContainer, itemName, itemValue) {
        var labelItem = $("label[relP=" + itemName + "]", targetContainer);
        labelItem.html(itemValue);
        // 多行输入框
        var foundItem = $("textarea[name=" + itemName + "]", targetContainer);
        if (foundItem.length == 1) {
            foundItem.val(itemValue);
            return;
        }

        // 文本输入框,
        foundItem = $("input[name=" + itemName + "]", targetContainer);
        if (foundItem.length == 1) {
            var elementType = foundItem.attr("type");
            // 普通文本输入款
            if (elementType == "text" || elementType == "hidden") {
                foundItem.val(itemValue);
            } else if (elementType == "radio" && itemValue == foundItem.val()) {
                foundItem.attr("checked", "true");
            } else if (elementType == "checkbox" && itemValue == foundItem.val()) {
                foundItem.attr("checked", "true");
            }
            return;
        }

        // 选择框
        foundItem = $("select[name=" + itemName + "]", targetContainer);
        if (foundItem.length == 1) {
            foundItem.val(itemValue);
            return;
        }

        // radio输入框
        foundItem = $("input[type=radio][name=" + itemName + "]", targetContainer);
        if (foundItem.length >= 1) {
            var values = itemValue.split(",");
            for (var idx = 0; idx < values.length; idx++) {
                var tempValue = values[idx];
                for (var tempCount = 0; tempCount < foundItem.length; tempCount++) {
                    var tempItem = $(foundItem[tempCount]);
                    if (tempItem.val() == tempValue) {
                        tempItem.attr("checked", "true");
                        return;
                    }
                }
            }
            return;
        }

        // checkbox框
        foundItem = $("input[type=checkbox][name=" + itemName + "]", targetContainer);
        if (foundItem.length >= 1) {
            var values = itemValue.split(",");
            for (var idx = 0; idx < values.length; idx++) {
                var tempValue = values[idx];
                for (var tempCount = 0; tempCount < foundItem.length; tempCount++) {
                    var tempItem = $(foundItem[tempCount]);
                    if (tempItem.val() == tempValue) {
                        tempItem.attr("checked", "true");
                    }
                }
            }
            return;
        }
    },


    /**
     * 清除已有的赋值,统一对textInput, textarea, select, radio, text
     * @param targetContainer 填充文本款查找范围
     * @param itemName 文本款名称
     * @param itemValue json值
     */
    clearBindValue: function (targetContainer) {
        $(".js_clear", targetContainer).each(function () {
            var $this = $(this);
            var tagName = $this.prop("tagName");
            if ("INPUT" == tagName) {
                var elementType = $this.attr("type");
                // 普通文本输入款
                if (elementType == "text" || elementType == "hidden") {
                    $this.val("");
                } else if (elementType == "radio") {
                    $this.removeAttr("checked");
                } else if (elementType == "checkbox") {
                    $this.removeAttr("checked");
                }
            } else if ("SELECT" == tagName) {
                $this.val("");
            } else if ("TEXTAREA" == tagName) {
                $this.val("");
            } else if ("label" == tagName) {
                $this.html("");
            }
        });
    }
};
/**
 * 通用工具类
 * @type {{generateMsgByIdCard: common_utils.generateMsgByIdCard}}
 */
var common_utils = {
    /**
     * 通过身份证号自动生成信息
     * @param obj 身份证号dom
     * @param container 需要改变的信息的元素容器
     */
    generateMsgByIdCard: function (obj, container) {
        if (!obj) {
            return;
        }
        var $idCard = $(obj);
        var idCard = $idCard.val();
        if (!CheckUtil.IdCardValidate(idCard)) {
            alertMsg.warn("Please enter a valid card.");
            return;
        }
        if (!container) {
            var $row = $idCard.closest("tr");
            if ($row.length == 0) {
                return;
            }
            container = $row
        }
        var gender = CheckUtil.maleOrFemalByIdCard(idCard)
        $("js_gender", container).val(gender)
        var brith = common_utils.creatBrithByIdCard(idCard)
        $("js_brith", container).val(brith)
        var age = common_utils.creatAgeByIdCard(idCard)
        $("js_age", container).val(age)
    },
    /**
     * 根据身份证号生成出生年月日
     * @param idCard
     */
    creatBrithByIdCard: function (idCard) {
        if (!idCard) {
            return
        }
        idCard = CheckUtil.trim(idCard.replace(/ /g, ""));// 对身份证号码做处理。包括字符间有空格。
        var Brith = "";
        if (idCard.length == 15) {
            var year = idCard.substring(6, 8);
            var month = idCard.substring(8, 10);
            var day = idCard.substring(10, 12);
            Brith = "19" + year + "-" + month + "-" + day
        } else if (idCard.length == 18) {
            var year = idCard.substring(6, 10);
            var month = idCard.substring(10, 12);
            var day = idCard.substring(12, 14);
            Brith = year + "-" + month + "-" + day
        }
        return Brith;
    },

    /**
     * 根据身份证号生成年龄
     * @param idCard
     */
    creatAgeByIdCard: function (idCard) {
        if (!idCard) {
            return
        }
        idCard = CheckUtil.trim(idCard.replace(/ /g, ""));// 对身份证号码做处理。包括字符间有空格。
        var myDate = new Date();
        var month = myDate.getMonth() + 1;
        var day = myDate.getDate();
        var year_people = "";
        var month_people = "";
        var day_people = "";
        if (idCard.length == 15) {
            year_people = idCard.substring(6, 8);
            month_people = idCard.substring(8, 10);
            day_people = idCard.substring(10, 12);
        } else if (idCard.length == 18) {
            year_people = idCard.substring(6, 10);
            month_people = idCard.substring(10, 12);
            day_people = idCard.substring(12, 14);
        }
        var Age = myDate.getFullYear() - year_people - 1;
        if (month_people < month || (month_people == month && day_people <= day)) {
            Age++;
        }
        return Age;
    }
};
