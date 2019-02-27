var fm003 = {
    /**
     * 打印五联单
     */
    viewWld: function (hsFmId, formCd) {
        if (hsFmId == '') {
            alertMsg.warn("请先保存信息，再查看五联单");
            return;
        }
        var hsId = $("input[name=hsId]", navTab.getCurrentPanel()).val();
        var hsCtId = $("input[name=hsCtId]", navTab.getCurrentPanel()).val();
        var prjCd = getPrjCd();
        var url = getGlobalPathRoot() + "eland/fm/fm003/fm003-viewWld.gv?fromOp=view"
            + "&hsId=" + hsId
            + "&hsCtId=" + hsCtId
            + "&prjCd=" + prjCd
            + "&formCd=" + formCd
            + "&hsFmId=" + hsFmId;
        $.pdialog.open(url, "viewWld", "五联单", {mask: true, max: true, resizable: false});
    },

    /**
     * 下载五联单
     * @param formId
     * @param templateName
     */
    downWld: function (hsFmId, formCd) {
        if (hsFmId == '') {
            alertMsg.warn("请先保存信息，再查看五联单");
            return;
        }
        var hsId = $("input[name=hsId]", navTab.getCurrentPanel()).val();
        var hsCtId = $("input[name=hsCtId]", navTab.getCurrentPanel()).val();
        var prjCd = getPrjCd();
        var url = getGlobalPathRoot() + "eland/fm/fm003/fm003-viewWld.gv?fromOp=download"
            + "&hsId=" + hsId
            + "&hsCtId=" + hsCtId
            + "&prjCd=" + prjCd
            + "&formCd=" + formCd
            + "&hsFmId=" + hsFmId
            +"&docName=五联单";
        window.open(url);
    },

    /**
     * 检索房产
     * @param obj
     */
    schHs: function () {
        // 查询数据
        var fm003QueryForm = $("#fm003QueryForm", navTab.getCurrentPanel());
        // 查询处理结果
        Page.query(fm003QueryForm, "");
    },
    /**
     * 快速查询按钮
     */
    qSchHs: function () {
        var container = $("#fm003SchDiv", navTab.getCurrentPanel());
        var formObj = $('#fm003QueryForm', container);
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
                    condition[nameIdx] = "like";
                    conditionValue[nameIdx] = attrValue;
                }
            } else if ($.trim(attrValue) != "") {
                conditionName.push(attrName);
                condition.push("like");
                conditionValue.push(attrValue);
            }
        });


        $("input[name=conditionName]", formObj).val(conditionName.join(","));
        $("input[name=condition]", formObj).val(condition.join(","));
        $("input[name=conditionValue]", formObj).val(conditionValue.join(","));
        // 查询数据
        fm003.schHs();
    },
    /**
     * 调用通用查询查询房源数据
     */
    queryHs: function (obj) {
        Page.queryCondition(obj, {
            changeConditions: fm003.changeConditions,
            showContainer: $("#fm003AdanceSch", navTab.getCurrentPanel()),
            formObj: $("#fm003QueryForm", navTab.getCurrentPanel()),
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
        var formObj = $('#fm003QueryForm', navTab.getCurrentPanel());
        $("input[name=conditionName]", formObj).val(newCondition.conditionNames);
        $("input[name=condition]", formObj).val(newCondition.conditions);
        $("input[name=conditionValue]", formObj).val(newCondition.conditionValues);
        $("input.js_conditionValue", formObj).val(newCondition.conditionValueTexts);
        $("input[name=resultField]", formObj).val(newCondition.resultFields);
        // 排序字段
        $("input[name=sortColumn]", formObj).val(newCondition.sortColumn);
        $("input[name=sortOrder]", formObj).val(newCondition.sortOrder);
        // 查询数据
        fm003.schHs();
    },

    /**
     * 锁定计划
     */
    lockPlan: function () {
        var lockPlan = $("select[name=pId]", navTab.getCurrentPanel()).val();
        $("input[name=lockPlan]", navTab.getCurrentPanel()).val(lockPlan);
        alertMsg.correct("锁定成功!");
    },
    /**
     * 登记信息
     * @param hsId
     */
    chInfo: function (hsId, pId) {
        var lockPlan = $("input[name=lockPlan]", navTab.getCurrentPanel()).val();
        if (lockPlan != '') {
            pId = lockPlan;
        }
        // 重新加载
        var packet = new AJAXPacket();
        var prjCd = getPrjCd();
        packet.data.add("prjCd", prjCd);
        packet.data.add("hsId", hsId);
        packet.data.add("pId", pId);
        packet.url = getGlobalPathRoot() + "eland/fm/fm003/fm003-info.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            $("#fm003Info", navTab.getCurrentPanel()).html(response);
            initUI($("#fm003Info", navTab.getCurrentPanel()));
        });
    },

    /**
     * 拷贝行
     */
    addRow: function (tableId, clickObject) {
        var copyRow = table.addRowNoInit(tableId, clickObject);
        $("input", copyRow).addClass("required");
        $("input[name=hsFmId]", copyRow).addClass("tempFlag");
        $("input[name=fmMoney]", copyRow).addClass("tempMoney");
        $(copyRow).initUI();
    },

    /**
     * 自动补齐
     * @param obj
     * @returns {string}
     */
    getUrl: function (obj) {
        var hsId = $("input[name=hsId]", navTab.getCurrentPanel()).val();
        return getGlobalPathRoot() + "eland/ch/ch001/ch001-queryPer.gv?prjCd=" + getPrjCd() + "&hsId=" + hsId;
    },

    getOption: function (obj) {
        return {
            processData: function (result) {
                var myData = [];
                for (i = 0; i < result.length; i++) {
                    var dataRow = {};
                    dataRow.data = result[i];
                    dataRow.value = result[i].personName;
                    myData.push(dataRow);
                }
                return myData;
            },
            remoteDataType: "json",
            delay: 300,
            minChars: 0,
            onError: function (var1, var2, var3) {
            },
            onItemSelect: function (obj) {
                var data = obj.data;
                var source = $(obj.source);
                var tr = source.closest("tr");
                tr.find("input.js_ps_certy").val(data.personCertyNum);
                tr.find("input.js_ps_tel").val(data.personTelphone);
            },
            onNoMatch: function (obj) {
                var source = $(obj.source);
                var tr = source.closest("tr");
                tr.find("input.js_ps_certy").val("");
                tr.find("input.js_ps_tel").val("");
            }
        }
    },
    /**
     * 保存
     */
    savePayInfo: function () {
        var totalMoney = $("input[name=totalMoney]", navTab.getCurrentPanel()).val();
        var totalTempMoney = 0;
        $("input.tempMoney", navTab.getCurrentPanel()).each(function () {
            totalTempMoney += parseFloat($(this).val());
        });
        if (totalTempMoney != totalMoney) {
            alertMsg.warn("登记的各个领款金额总和应付款总金额不相等");
            return false;
        }
        var pId = $("input[name=pId]", navTab.getCurrentPanel()).val();
        var hsId = $("input[name=hsId]", navTab.getCurrentPanel()).val();
        //from提交
        var $form = $("#fm00301", navTab.getCurrentPanel());

        if ($form.valid()) {
            var url = getGlobalPathRoot() + "eland/fm/fm003/fm003-savePayInfo.gv";
            var ajaxPacket = new AJAXPacket(url);
            ajaxPacket.data.data = $form.serializeArray();
            ajaxPacket.data.data.push({name: "prjCd", value: getPrjCd()});
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var data = eval("(" + response + ")");
                var isSuccess = data.success;
                if (isSuccess) {
                    alertMsg.correct("操作成功");
                    fm003.chInfo(hsId, pId);
                } else {
                    alertMsg.error(data.errMsg);
                }
            });
        }
    },

    /**
     * 切换计划
     */
    changePlan: function (obj) {
        var hsId = $("input[name=hsId]", navTab.getCurrentPanel()).val();
        var pId = $(obj).val();
        var packet = new AJAXPacket();
        var prjCd = getPrjCd();
        packet.data.add("prjCd", prjCd);
        packet.data.add("hsId", hsId);
        packet.data.add("pId", pId);
        packet.url = getGlobalPathRoot() + "eland/fm/fm003/fm003-info.gv";
        core.ajax.sendPacketHtml(packet, function (response) {
            $("#fm003Info", navTab.getCurrentPanel()).html(response);
            initUI($("#fm003Info", navTab.getCurrentPanel()));
        });
    },

    /**
     * 显示关联文档信息
     * @param clickObj 点击对象
     */
    showDoc: function (clickObj, relId) {
        var $span = $(clickObj);
        var docIds = $span.find("input[type=hidden]").val();
        if (!docIds) {
            docIds = "";
        }

        var docTypeName = $span.attr("docTypeName");
        if (!docTypeName) {
            docTypeName = "";
        }
        docTypeName = encodeURI(encodeURI(docTypeName));
        var docTypeCd = $span.attr("docTypeCd");
        if (!docTypeCd) {
            docTypeCd = "";
        }

        var relType = $span.attr("relType");
        var editAble = $span.attr("editAble");
        if (!editAble) {
            editAble = true;
        }
        var url = getGlobalPathRoot() + "oframe/common/file/file-init.gv?docTypeName=" + docTypeName
            + "&docTypeCd=" + docTypeCd
            + "&docIds=" + docIds
            + "&editAble=" + editAble
            + "&relType=" + relType
            + "&prjCd=" + getPrjCd()
            + "&relId=" + relId;
        $.pdialog.open(url, "file", "文件管理", {
            height: 600, width: 800, mask: true,
            close: fm003.docClosed,
            param: {clickSpan: $span, editAble: editAble}
        });
    },
    /**
     * 关闭上传窗口
     * @param param
     * @returns {boolean}
     */
    docClosed: function (param) {
        var uploadedFiles = file.getAllUploadFiles();
        var docIdArr = [];
        for (var i = 0; i < uploadedFiles.length; i++) {
            var uploadedFile = uploadedFiles[i];
            docIdArr.push(uploadedFile.docId);
        }
        // 调用文件的关闭
        return file.closeD();
    },

    /**
     * 申请制折
     */
    commitApply: function () {
        var totalMoney = $("input[name=totalMoney]", navTab.getCurrentPanel()).val();
        var totalTempMoney = 0;
        $("input.tempMoney", navTab.getCurrentPanel()).each(function () {
            totalTempMoney += parseFloat($(this).val());
        });
        if (totalTempMoney != totalMoney) {
            alertMsg.warn("登记的各个领款金额总和应付款总金额不相等");
            return false;
        }
        var hsId = $("input[name=hsId]", navTab.getCurrentPanel()).val();
        var pId = $("select[name=pId]", navTab.getCurrentPanel()).val();

        //from提交
        var $form = $("#fm00301", navTab.getCurrentPanel());

        if ($form.valid()) {
            var url = getGlobalPathRoot() + "eland/fm/fm003/fm003-commitApply.gv";
            var ajaxPacket = new AJAXPacket(url);
            ajaxPacket.data.data = $form.serializeArray();
            ajaxPacket.data.data.push({name: "prjCd", value: getPrjCd()});
            core.ajax.sendPacketHtml(ajaxPacket, function (response) {
                var data = eval("(" + response + ")");
                var isSuccess = data.success;
                if (isSuccess) {
                    alertMsg.correct("操作成功");
                    fm003.chInfo(hsId, pId);
                } else {
                    alertMsg.error(data.errMsg);
                }
            });
        }
    }
};

$(document).ready(function () {
    fm003.schHs();
    // 拖动屏幕切分效果   如果要设置 cookie 在{里编写} cookieName: "cookieName"
    LeftMenu.init("fm003LeftDiv", "fm003Context", {});
});